package ui.controllers;

import domain.compiti.Compito;
import domain.compiti.Turno;
import domain.eventi.Evento;
import domain.ricette.Ricetta;
import domain.utenti.Cuoco;
import service.CompitoCucinaService;
import service.EventoService;
import service.RicettaService;
import service.UtenteService;
import service.FeedbackService;
import ui.utils.AlertUtils;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Rappresenta il controller per la gestione dei compiti di cucina nel sistema di catering.
 * Implementa il pattern GRASP Controller per il caso d'uso "Gestione dei Compiti della cucina".
 */
public class CompitiCucinaController {

    // COSTANTI PER MESSAGGI STANDARDIZZATI
    private static final String MSG_SELEZIONA_COMPITO = "Selezione richiesta";
    
    @FXML private ComboBox<Evento> cmbEventi;
    @FXML private TableView<Compito> tblCompiti;
    @FXML private TableColumn<Compito, String> colRicetta;
    @FXML private TableColumn<Compito, String> colCuoco;
    @FXML private TableColumn<Compito, String> colTurno;
    @FXML private TableColumn<Compito, String> colStato;
    @FXML private TableColumn<Compito, String> colImportanza;
    @FXML private TableColumn<Compito, Integer> colTempo;
    @FXML private TableColumn<Compito, Double> colQuantita;
    @FXML private TableColumn<Compito, String> colFeedback;
    @FXML private ComboBox<Cuoco> cmbCuochi;
    @FXML private ComboBox<Turno> cmbTurni;
    @FXML private ComboBox<Ricetta> cmbRicette;
    @FXML private Spinner<Integer> spnTempoStimato;
    @FXML private Spinner<Double> spnQuantita;
    @FXML private ToggleGroup grpImportanza;

    
    // Nuovi controlli per viste e filtri
    @FXML private ToggleGroup grpVista;
    @FXML private RadioButton rbVistaCompleta;
    @FXML private RadioButton rbVistaPerTurno;
    @FXML private ComboBox<String> cmbFiltroStato;
    @FXML private ScrollPane scrollVistaRaggruppata;
    @FXML private VBox vboxVistaRaggruppata;
    
    // Nuovi controlli per gestione avanzata
    @FXML private TableView<CaricoCuoco> tblCaricoCuochi;
    @FXML private TableColumn<CaricoCuoco, String> colCuocoCarico;
    @FXML private TableColumn<CaricoCuoco, Integer> colNumCompiti;
    @FXML private TableColumn<CaricoCuoco, Integer> colTempoTotale;
    @FXML private TableColumn<CaricoCuoco, String> colCompitiDettaglio;
    @FXML private TableColumn<CaricoCuoco, String> colDisponibilita;


    
    // Nuovi elementi per la gestione dei turni
    @FXML private TableView<Turno> tblTurni;
    @FXML private TableColumn<Turno, LocalDate> colData;
    @FXML private TableColumn<Turno, LocalTime> colOraInizio;
    @FXML private TableColumn<Turno, LocalTime> colOraFine;
    @FXML private TableColumn<Turno, String> colLuogo;
    @FXML private TableColumn<Turno, String> colTipo;
    
    private final CompitoCucinaService compitoCucinaService;
    private final EventoService eventoService;
    private final UtenteService utenteService;
    private final RicettaService ricettaService;
    private final FeedbackService feedbackService;
    private Evento eventoCorrente;
    private ObservableList<Compito> compitiCorrente;
    private FilteredList<Compito> compitiFiltrati;
    
    public CompitiCucinaController(CompitoCucinaService compitoCucinaService, 
                                EventoService eventoService,
                                UtenteService utenteService,
                                RicettaService ricettaService,
                                FeedbackService feedbackService) {
        this.compitoCucinaService = compitoCucinaService;
        this.eventoService = eventoService;
        this.utenteService = utenteService;
        this.ricettaService = ricettaService;
        this.feedbackService = feedbackService;
    }
    
    @FXML
    public void initialize() {
        // Inizializza le liste
        cmbEventi.setItems(eventoService.getEventi());
        cmbTurni.setItems(compitoCucinaService.getTurni());
        
        // Inizializza cuochi vuoti con messaggio di aiuto (si popolerà automaticamente)
        cmbCuochi.setItems(FXCollections.observableArrayList());
        cmbCuochi.setPromptText("Seleziona prima ricetta, turno e tempo");
        
        // Converti List<Ricetta> a ObservableList<Ricetta>
        ObservableList<Ricetta> ricetteObs = FXCollections.observableArrayList();
        ricettaService.getRicetteDisponibiliViewModel().forEach(vm -> ricetteObs.add(vm.getRicetta()));
        cmbRicette.setItems(ricetteObs);
        
        // Inizializza filtro stati
        cmbFiltroStato.setItems(FXCollections.observableArrayList(
            "Tutti", "Da iniziare", "In corso", "Completato", "Bloccato"
        ));
        cmbFiltroStato.setValue("Tutti");
        
        // Configurazione TableView per compiti
        colRicetta.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getRicetta().getNome()));
        colCuoco.setCellValueFactory(cellData -> {
            Cuoco cuoco = cellData.getValue().getCuocoAssegnato();
            if (cuoco == null) {
                return new SimpleStringProperty("Non assegnato");
            }
            return new SimpleStringProperty(cuoco.getNome() + " " + cuoco.getCognome());
        });
        colTurno.setCellValueFactory(new PropertyValueFactory<>("turno"));
        colStato.setCellValueFactory(cellData -> cellData.getValue().statoProperty());
        colImportanza.setCellValueFactory(cellData -> {
            int importanza = cellData.getValue().getImportanza();
            String testoImportanza;
            switch (importanza) {
                case 5:
                    testoImportanza = "Alta";
                    break;
                case 3:
                    testoImportanza = "Media";
                    break;
                case 1:
                    testoImportanza = "Bassa";
                    break;
                default:
                    testoImportanza = "Media";
            }
            return new SimpleStringProperty(testoImportanza);
        });
        colTempo.setCellValueFactory(new PropertyValueFactory<>("tempoStimato"));
        colQuantita.setCellValueFactory(new PropertyValueFactory<>("quantita"));
        colFeedback.setCellValueFactory(cellData -> {
            String feedback = feedbackService.getFeedbackSemplicePerCompito(cellData.getValue().getId());
            if (feedback == null || feedback.trim().isEmpty()) {
                return new SimpleStringProperty("Nessun feedback");
            } else {
                // Mostra il feedback completo
                return new SimpleStringProperty(feedback);
            }
        });
        
        // Configura il cell factory per il text wrapping della colonna feedback
        colFeedback.setCellFactory(tc -> {
            TableCell<Compito, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(colFeedback.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell;
        });
        
        // Configurazione dimensioni colonne - solo preferite e minime
        colRicetta.setPrefWidth(200);
        colRicetta.setMinWidth(150);
        // RIMUOVI questa riga: colRicetta.setMaxWidth(200);
        
        colCuoco.setPrefWidth(150);
        colCuoco.setMinWidth(100);
        // RIMUOVI: colCuoco.setMaxWidth(150);
        
        colTurno.setPrefWidth(180);
        colTurno.setMinWidth(120);
        // RIMUOVI: colTurno.setMaxWidth(180);
        
        colStato.setPrefWidth(100);
        colStato.setMinWidth(80);
        // RIMUOVI: colStato.setMaxWidth(100);
        
        colImportanza.setPrefWidth(100);
        colImportanza.setMinWidth(80);
        // RIMUOVI: colImportanza.setMaxWidth(100);
        
        colTempo.setPrefWidth(100);
        colTempo.setMinWidth(60);
        // RIMUOVI: colTempo.setMaxWidth(100);
        
        colQuantita.setPrefWidth(90);
        colQuantita.setMinWidth(60);
        // RIMUOVI: colQuantita.setMaxWidth(90);
        
        colFeedback.setPrefWidth(300);
        colFeedback.setMinWidth(200);
        // RIMUOVI: colFeedback.setMaxWidth(220);
        
        // Mantieni la policy
        tblCompiti.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_NEXT_COLUMN);

        
        // Inizializza la tabella vuota per ogni evento
        tblCompiti.setItems(FXCollections.observableArrayList());
        
        // Configurazione TableView per turni (se presente nel FXML)
        if (tblTurni != null) {
            initTurniTableView();
        }
        
        // Configura ComboBox rendering
        configureCellFactories();
        
        // Configura Spinners
        spnTempoStimato.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 480, 30, 5));
        
        // Configura Spinner quantità con formato italiano (virgola come separatore decimale)
        SpinnerValueFactory<Double> quantitaFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.1, 100.0, 1.0, 0.1);
        quantitaFactory.setConverter(createItalianDecimalConverter());
        spnQuantita.setValueFactory(quantitaFactory);
        

        
        // Configura tabella carico cuochi
        initCaricoCuochiTable();
        
        // Aggiungi listener per il cambio di evento automatico
        cmbEventi.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.equals(oldVal)) {
                eventoCorrente = newVal;
                inizializzaTabellaPerEvento(newVal);
                // Aggiorna immediatamente i cuochi disponibili dopo aver selezionato l'evento
                aggiornaListaCuochiDisponibiliPerCompito();
                // Aggiorna automaticamente il carico di lavoro dei cuochi
                aggiornaCaricoCuochiAutomaticamente();
            }
        });
        
        // Aggiungi listener per aggiornare i cuochi disponibili quando cambiano ricetta, turno o tempo
        cmbRicette.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            aggiornaListaCuochiDisponibiliPerCompito();
        });
        
        cmbTurni.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            aggiornaListaCuochiDisponibiliPerCompito();
        });
        
        spnTempoStimato.valueProperty().addListener((obs, oldVal, newVal) -> {
            aggiornaListaCuochiDisponibiliPerCompito();
        });
        
        // Carica il carico universale dei cuochi all'avvio
        aggiornaCaricoCuochiAutomaticamente();
    }
    
    private void configureCellFactories() {
        // Configura la visualizzazione degli eventi nella ComboBox
        cmbEventi.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Evento item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getNome() + " (" + item.getDataInizio() + ")");
            }
        });
        
        cmbEventi.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Evento item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getNome() + " (" + item.getDataInizio() + ")");
            }
        });
        
        // Configura la visualizzazione dei cuochi nella ComboBox
        cmbCuochi.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Cuoco item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getNome() + " " + item.getCognome());
            }
        });
        
        cmbCuochi.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Cuoco item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getNome() + " " + item.getCognome());
            }
        });
        
        // Configura la visualizzazione dei turni nella ComboBox
        cmbTurni.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Turno item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getData() + " " + item.getOraInizio() + "-" + item.getOraFine());
            }
        });
        
        cmbTurni.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Turno item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getData() + " " + item.getOraInizio() + "-" + item.getOraFine());
            }
        });
        
        // Configura la visualizzazione delle ricette nella ComboBox
        cmbRicette.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Ricetta item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getNome());
            }
        });
        
        cmbRicette.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Ricetta item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getNome());
            }
        });
    }
    

    
    /**
     * Aggiorna la lista dei cuochi disponibili in base a ricetta, turno e tempo stimato selezionati.
     * Questo metodo implementa il nuovo flusso: ricetta → turno → tempo → cuochi disponibili
     */
    private void aggiornaListaCuochiDisponibiliPerCompito() {
        Ricetta ricettaSelezionata = cmbRicette.getSelectionModel().getSelectedItem();
        Turno turnoSelezionato = cmbTurni.getSelectionModel().getSelectedItem();
        Integer tempoStimato = spnTempoStimato.getValue();
        

        
        // Verifica che tutti i prerequisiti siano selezionati
        if (eventoCorrente == null) {
            cmbCuochi.setItems(FXCollections.observableArrayList());
            cmbCuochi.setPromptText("Seleziona prima un evento");
            return;
        }
        
        if (ricettaSelezionata == null || turnoSelezionato == null || tempoStimato == null || tempoStimato <= 0) {
            // Se mancano elementi, svuota la lista cuochi e mostra messaggio informativo
            cmbCuochi.setItems(FXCollections.observableArrayList());
            cmbCuochi.setPromptText("Seleziona prima ricetta, turno e tempo");
            return;
        }
        
        // Calcola i cuochi disponibili per questo compito specifico nell'evento corrente
        List<Cuoco> cuochiDisponibili = compitoCucinaService.getCuochiDisponibiliPerCompito(turnoSelezionato, tempoStimato, eventoCorrente);
        
        // Aggiorna la lista dei cuochi
        ObservableList<Cuoco> cuochiObs = FXCollections.observableArrayList(cuochiDisponibili);
        cmbCuochi.setItems(cuochiObs);
        
        if (!cuochiObs.isEmpty()) {
            cmbCuochi.getSelectionModel().select(0);
            cmbCuochi.setPromptText("Seleziona cuoco");
        } else {
            cmbCuochi.getSelectionModel().clearSelection();
            
            // Calcola durata turno per messaggio informativo
            long durataTurno = java.time.Duration.between(
                turnoSelezionato.getOraInizio(), turnoSelezionato.getOraFine()).toMinutes();
            
            if (tempoStimato > durataTurno) {
                cmbCuochi.setPromptText("Tempo richiesto troppo lungo per il turno");
            } else {
                cmbCuochi.setPromptText("Nessun cuoco disponibile per questo tempo");
            }
        }
    }
    
    /**
     * Gestisce il cambio di vista (Completa/Per Turno/Per Servizio)
     */
    @FXML
    public void onCambiaVista() {
        if (compitiCorrente == null || compitiCorrente.isEmpty()) {
            AlertUtils.showWarning("Nessun compito", "Seleziona un evento e crea prima un riepilogo compiti.");
            rbVistaCompleta.setSelected(true);
            return;
        }
        
        if (rbVistaCompleta.isSelected()) {
            mostraVistaCompleta();
        } else if (rbVistaPerTurno.isSelected()) {
            mostraVistaPerTurno();
        }
    }
    
    /**
     * Gestisce il filtro per stato dei compiti
     */
    @FXML
    public void onFiltraPerStato() {
        if (compitiFiltrati == null) return;
        
        String statoSelezionato = cmbFiltroStato.getValue();
        if ("Tutti".equals(statoSelezionato)) {
            compitiFiltrati.setPredicate(null);
            } else {
            compitiFiltrati.setPredicate(compito -> 
                compito.getStato().equalsIgnoreCase(statoSelezionato));
        }
    }
    
    /**
     * Mostra la vista completa (tabella normale)
     */
    private void mostraVistaCompleta() {
        tblCompiti.setVisible(true);
        tblCompiti.setManaged(true);
        scrollVistaRaggruppata.setVisible(false);
        scrollVistaRaggruppata.setManaged(false);
    }
    
    /**
     * Mostra la vista raggruppata per turno
     */
    private void mostraVistaPerTurno() {
        tblCompiti.setVisible(false);
        tblCompiti.setManaged(false);
        scrollVistaRaggruppata.setVisible(true);
        scrollVistaRaggruppata.setManaged(true);
        
        vboxVistaRaggruppata.getChildren().clear();
        
        // Raggruppa compiti per turno
        Map<String, List<Compito>> compitiPerTurno = compitiCorrente.stream()
            .collect(Collectors.groupingBy(Compito::getTurno));
        
        for (Map.Entry<String, List<Compito>> entry : compitiPerTurno.entrySet()) {
            String turno = entry.getKey();
            List<Compito> compiti = entry.getValue();
            
            VBox gruppoTurno = creaGruppoCompiti("Turno: " + turno, compiti);
            
            // Configura il gruppo per espandersi orizzontalmente nel VBox contenitore
            VBox.setVgrow(gruppoTurno, Priority.NEVER);
            
            vboxVistaRaggruppata.getChildren().add(gruppoTurno);
        }
    }
    

    
    /**
     * Crea un gruppo di compiti con titolo e tabella
     */
    private VBox creaGruppoCompiti(String titolo, List<Compito> compiti) {
        VBox gruppo = new VBox(5);
        gruppo.setStyle("-fx-border-color: lightgray; -fx-border-width: 1; -fx-padding: 10;");
        
        // Configura il VBox per occupare tutta la larghezza disponibile
        gruppo.setMaxWidth(Double.MAX_VALUE);
        gruppo.setPrefWidth(Region.USE_COMPUTED_SIZE);
        
        // Titolo del gruppo
        Label lblTitolo = new Label(titolo);
        lblTitolo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Statistiche
        int completati = (int) compiti.stream().filter(c -> "Completato".equals(c.getStato())).count();
        Label lblStats = new Label(String.format("Compiti: %d | Completati: %d | Rimanenti: %d", 
            compiti.size(), completati, compiti.size() - completati));
        lblStats.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");
        
        // Tabella dei compiti
        TableView<Compito> tabellaGruppo = new TableView<>();
        tabellaGruppo.setPrefHeight(Math.max(150, compiti.size() * 25 + 50));
        
        // Configura la tabella per espandersi orizzontalmente
        tabellaGruppo.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(tabellaGruppo, Priority.ALWAYS);
        
        // Clona le colonne della tabella principale
        TableColumn<Compito, String> colRicettaGruppo = new TableColumn<>("Ricetta");
        colRicettaGruppo.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getRicetta().getNome()));
        
        TableColumn<Compito, String> colCuocoGruppo = new TableColumn<>("Cuoco");
        colCuocoGruppo.setCellValueFactory(cellData -> {
            Cuoco cuoco = cellData.getValue().getCuocoAssegnato();
            return new SimpleStringProperty(cuoco != null ? 
                cuoco.getNome() + " " + cuoco.getCognome() : "Non assegnato");
        });
        
        TableColumn<Compito, String> colStatoGruppo = new TableColumn<>("Stato");
        colStatoGruppo.setCellValueFactory(cellData -> cellData.getValue().statoProperty());
        
        TableColumn<Compito, String> colImportanzaGruppo = new TableColumn<>("Importanza");
        colImportanzaGruppo.setCellValueFactory(cellData -> {
            int importanza = cellData.getValue().getImportanza();
            String testoImportanza = importanza == 5 ? "Alta" : importanza == 3 ? "Media" : "Bassa";
            return new SimpleStringProperty(testoImportanza);
        });
        
        TableColumn<Compito, Integer> colTempoGruppo = new TableColumn<>("Tempo");
        colTempoGruppo.setCellValueFactory(new PropertyValueFactory<>("tempoStimato"));
        
        TableColumn<Compito, String> colFeedbackGruppo = new TableColumn<>("Feedback");
        colFeedbackGruppo.setCellValueFactory(cellData -> {
            String feedback = feedbackService.getFeedbackSemplicePerCompito(cellData.getValue().getId());
            if (feedback == null || feedback.trim().isEmpty()) {
                return new SimpleStringProperty("Nessun feedback");
        } else {
                String preview = feedback.length() > 20 ? feedback.substring(0, 20) + "..." : feedback;
                return new SimpleStringProperty(preview);
            }
        });
        
        tabellaGruppo.getColumns().addAll(List.of(colRicettaGruppo, colCuocoGruppo, 
            colStatoGruppo, colImportanzaGruppo, colTempoGruppo, colFeedbackGruppo));
        tabellaGruppo.setItems(FXCollections.observableArrayList(compiti));
        
        // Configura la tabella per occupare tutta la larghezza disponibile
        tabellaGruppo.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        
        // Abilita modifica compiti nella vista raggruppata (senza modifica stato)
        tabellaGruppo.setRowFactory(tv -> {
            TableRow<Compito> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    // Modifica il compito (senza possibilità di cambiare lo stato)
                    mostraDialogModificaCompito(row.getItem());
                }
            });
            return row;
        });
        
        gruppo.getChildren().addAll(lblTitolo, lblStats, tabellaGruppo);
        return gruppo;
    }


    
    /**
     * Verifica i cuochi disponibili.
     * Riferimento: UC "Gestione dei Compiti della cucina" - Passo 2
     */
    @FXML
    public void onVerificaCuochiDisponibili() {
        Turno turnoSelezionato = cmbTurni.getSelectionModel().getSelectedItem();
        if (turnoSelezionato != null) {
            List<Cuoco> cuochiDisponibili = compitoCucinaService.getCuochiDisponibili(turnoSelezionato);
            
            // Aggiorna la lista dei cuochi
            ObservableList<Cuoco> cuochiObs = FXCollections.observableArrayList(cuochiDisponibili);
            cmbCuochi.setItems(cuochiObs);
            
            // Seleziona il primo cuoco nella lista se disponibile
            if (!cuochiObs.isEmpty()) {
                cmbCuochi.getSelectionModel().select(0);
                AlertUtils.showInfo("Cuochi disponibili", "Sono disponibili " + cuochiObs.size() + " cuochi per questo turno");
            } else {
                cmbCuochi.getSelectionModel().clearSelection();
                AlertUtils.showWarning("Nessun cuoco disponibile", "Tutti i cuochi sono occupati in questo turno");
            }
        } else {
            AlertUtils.showWarning("Seleziona turno", "Devi selezionare un turno");
        }
    }
    
    /**
     * Assegna un compito a un cuoco.
     * Riferimento: UC "Gestione dei Compiti della cucina" - Passo 3
     */
    /**
     * Verifica se esistono già compiti per la stessa ricetta nell'evento corrente
     */
    private List<Compito> trovaCompitiEsistentiPerRicetta(Ricetta ricetta) {
        if (eventoCorrente == null || ricetta == null) return new ArrayList<>();
        
        List<Compito> compitiTrovati = compitoCucinaService.getCompiti().stream()
            .filter(c -> c.getEvento() != null && c.getEvento().getNome().equals(eventoCorrente.getNome()))
            .filter(c -> c.getRicetta() != null && c.getRicetta().getNome().equals(ricetta.getNome()))
            .filter(c -> !c.getStato().equals("Completato")) // Escludi compiti già completati
            .collect(Collectors.toList());
        
        return compitiTrovati;
    }
    
    /**
     * Calcola la quantità totale già pianificata per una ricetta nell'evento corrente
     */
    private double calcolaQuantitaTotalePianificata(Ricetta ricetta) {
        return trovaCompitiEsistentiPerRicetta(ricetta).stream()
            .mapToDouble(Compito::getQuantita)
            .sum();
    }
    
    /**
     * Calcola la quantità già completata per una ricetta nell'evento corrente
     */
    private double calcolaQuantitaGiaCompletata(Ricetta ricetta) {
        if (eventoCorrente == null || ricetta == null) return 0.0;
        
        // FIX: Confronta per NOME invece che per oggetto
        return compitoCucinaService.getCompiti().stream()
            .filter(c -> c.getEvento() != null && c.getEvento().getNome().equals(eventoCorrente.getNome()))
            .filter(c -> c.getRicetta() != null && c.getRicetta().getNome().equals(ricetta.getNome()))
            .filter(c -> c.getStato().equals("Completato"))
            .mapToDouble(Compito::getQuantita)
            .sum();
    }
    
    /**
     * Gestisce il caso in cui esistono già preparazioni pianificate per la stessa ricetta
     */
    private void gestisciPreparazioniEsistenti(Cuoco cuocoSelezionato, Turno turnoSelezionato, 
                                             Ricetta ricettaSelezionata, int tempoStimato, 
                                             double quantitaRichiesta, double quantitaDisponibile,
                                             List<Compito> compitiEsistenti, double quantitaGiaPianificata) {
        
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Preparazione già pianificata");
        dialog.setHeaderText("Attenzione: Esiste già una pianificazione per questa ricetta");
        
        // Crea il contenuto del dialog
        VBox content = new VBox(10);
        content.setPadding(new Insets(15));
        
        // Sezione informazioni esistenti
        VBox infoBox = new VBox(5);
        
        Label lblInfo = new Label("COMPITI GIÀ PIANIFICATI PER: " + ricettaSelezionata.getNome());
        infoBox.getChildren().add(lblInfo);
        
        // Lista compiti esistenti
        for (int i = 0; i < compitiEsistenti.size(); i++) {
            Compito compito = compitiEsistenti.get(i);
            String cuocoNome = compito.getCuocoAssegnato() != null ? 
                compito.getCuocoAssegnato().getNome() + " " + compito.getCuocoAssegnato().getCognome() : "Non assegnato";
            
            Label lblCompito = new Label((i + 1) + ". Quantità: " + compito.getQuantita() + 
                                       " | Cuoco: " + cuocoNome + 
                                       " | Turno: " + compito.getTurno() + 
                                       " | Stato: " + compito.getStato());
            infoBox.getChildren().add(lblCompito);
        }
        
        Label lblTotale = new Label("TOTALE GIÀ PIANIFICATO: " + quantitaGiaPianificata + " porzioni");
        infoBox.getChildren().add(lblTotale);
        
        content.getChildren().add(infoBox);
        
        // Sezione analisi
        VBox analisiBox = new VBox(5);
        
        Label lblAnalisi = new Label("ANALISI SITUAZIONE:");
        analisiBox.getChildren().add(lblAnalisi);
        
        Label lblRichiesta = new Label("• Quantità richiesta ora: " + quantitaRichiesta + " porzioni");
        Label lblCompletata = new Label("• Quantità già completata: " + quantitaDisponibile + " porzioni");
        Label lblFabbisogno = new Label("• Fabbisogno netto: " + (quantitaRichiesta - quantitaDisponibile) + " porzioni");
        
        analisiBox.getChildren().addAll(lblRichiesta, lblCompletata, lblFabbisogno);
        
        // Determina situazione
        double fabbisognoNetto = quantitaRichiesta - quantitaDisponibile;
        String messaggioSituazione;
        
        if (quantitaGiaPianificata >= fabbisognoNetto) {
            messaggioSituazione = "La quantità già pianificata (" + quantitaGiaPianificata + 
                                 ") è SUFFICIENTE per il fabbisogno (" + fabbisognoNetto + ")";
        } else {
            double quantitaMancante = fabbisognoNetto - quantitaGiaPianificata;
            messaggioSituazione = "Serve ancora " + quantitaMancante + 
                                " porzioni oltre quelle già pianificate";
        }
        
        Label lblSituazione = new Label(messaggioSituazione);
        lblSituazione.setWrapText(true);
        analisiBox.getChildren().add(lblSituazione);
        
        content.getChildren().add(analisiBox);
        
        // Pulsanti di azione
        ButtonType btnCreaRidotto = new ButtonType("Crea compito ridotto", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCreaCompleto = new ButtonType("Crea compito completo", ButtonBar.ButtonData.YES);
        ButtonType btnGestisciEsistenti = new ButtonType("Gestisci esistenti", ButtonBar.ButtonData.OTHER);
        ButtonType btnAnnulla = new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        dialog.getDialogPane().getButtonTypes().addAll(btnCreaRidotto, btnCreaCompleto, btnGestisciEsistenti, btnAnnulla);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setPrefWidth(700);
        dialog.getDialogPane().setPrefHeight(500);
        
        Optional<ButtonType> result = dialog.showAndWait();
        
        if (result.isPresent()) {
            ButtonType scelta = result.get();
            
            if (scelta == btnCreaRidotto) {
                // Crea un compito solo per la quantità mancante
                double quantitaMancante = Math.max(0, fabbisognoNetto - quantitaGiaPianificata);
                if (quantitaMancante > 0) {
                    // PROBLEMA 4 RISOLTO: Usa metodo silenzioso per compiti ridotti
                    creaCompitoSilenzioso(cuocoSelezionato, turnoSelezionato, ricettaSelezionata, 
                              tempoStimato, quantitaMancante, quantitaDisponibile);
                    AlertUtils.showInfo("Compito ridotto creato", 
                        "Creato compito per " + quantitaMancante + " porzioni mancanti");
                } else {
                    // PROBLEMA 4 RISOLTO: Solo avviso, nessun popup aggiuntivo
                    AlertUtils.showInfo("Quantità sufficiente", 
                        "La quantità pianificata è già sufficiente per il fabbisogno");
                }
                dialog.close();
            } else if (scelta == btnCreaCompleto) {
                // Crea il compito completo comunque
                creaCompito(cuocoSelezionato, turnoSelezionato, ricettaSelezionata, 
                          tempoStimato, quantitaRichiesta, quantitaDisponibile);
                AlertUtils.showInfo("Compito assegnato", 
                    "Compito assegnato con successo!\n" +
                    "Ricetta: " + ricettaSelezionata.getNome() + "\n" +
                    "Cuoco: " + cuocoSelezionato.getNome() + " " + cuocoSelezionato.getCognome() + "\n" +
                    "Durata: " + tempoStimato + " minuti");
            } else if (scelta == btnGestisciEsistenti) {
                // Mostra dialog per gestire compiti esistenti
                mostraDialogGestioneCompitiEsistenti(compitiEsistenti, cuocoSelezionato, turnoSelezionato, 
                                                   ricettaSelezionata, tempoStimato, quantitaRichiesta, quantitaDisponibile);
            }
            // Se btnAnnulla, non fa nulla
        }
    }
    
    /**
     * Mostra un dialog per gestire i compiti esistenti (modifica/eliminazione)
     */
    private void mostraDialogGestioneCompitiEsistenti(List<Compito> compitiEsistenti, Cuoco cuocoSelezionato, 
                                                    Turno turnoSelezionato, Ricetta ricettaSelezionata, 
                                                    int tempoStimato, double quantitaRichiesta, double quantitaDisponibile) {
        
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Gestione Compiti Esistenti");
        dialog.setHeaderText("Modifica o elimina i compiti già pianificati per: " + ricettaSelezionata.getNome());
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(15));
        
        // Tabella dei compiti esistenti
        TableView<Compito> tabellaCompiti = new TableView<>();
        tabellaCompiti.setPrefHeight(200);
        
        // Colonne della tabella
        TableColumn<Compito, String> colCuoco = new TableColumn<>("Cuoco");
        colCuoco.setCellValueFactory(cellData -> {
            Cuoco cuoco = cellData.getValue().getCuocoAssegnato();
            return new SimpleStringProperty(cuoco != null ? cuoco.getNome() + " " + cuoco.getCognome() : "Non assegnato");
        });
        colCuoco.setPrefWidth(150);
        
        TableColumn<Compito, String> colTurno = new TableColumn<>("Turno");
        colTurno.setCellValueFactory(new PropertyValueFactory<>("turno"));
        colTurno.setPrefWidth(180);
        
        TableColumn<Compito, Double> colQuantitaExist = new TableColumn<>("Quantità");
        colQuantitaExist.setCellValueFactory(new PropertyValueFactory<>("quantita"));
        colQuantitaExist.setPrefWidth(80);
        
        TableColumn<Compito, String> colStatoExist = new TableColumn<>("Stato");
        colStatoExist.setCellValueFactory(cellData -> cellData.getValue().statoProperty());
        colStatoExist.setPrefWidth(100);
        
        TableColumn<Compito, Integer> colTempoExist = new TableColumn<>("Tempo (min)");
        colTempoExist.setCellValueFactory(new PropertyValueFactory<>("tempoStimato"));
        colTempoExist.setPrefWidth(90);
        
        tabellaCompiti.getColumns().addAll(List.of(colCuoco, colTurno, colQuantitaExist, colStatoExist, colTempoExist));
        tabellaCompiti.setItems(FXCollections.observableArrayList(compitiEsistenti));
        
        // Pulsanti di azione
        HBox pulsanti = new HBox(10);
        pulsanti.setAlignment(javafx.geometry.Pos.CENTER);
        
        Button btnModificaQuantita = new Button("Modifica Quantità");
        Button btnEliminaCompito = new Button("Elimina Compito");
        Button btnCreaRidotto = new Button("Crea Compito Ridotto");
        
        pulsanti.getChildren().addAll(btnModificaQuantita, btnEliminaCompito, btnCreaRidotto);
        
        // Azioni dei pulsanti
        btnModificaQuantita.setOnAction(e -> {
            Compito compitoSelezionato = tabellaCompiti.getSelectionModel().getSelectedItem();
            if (compitoSelezionato != null) {
                modificaQuantitaCompito(compitoSelezionato, tabellaCompiti);
            } else {
                AlertUtils.showWarning(MSG_SELEZIONA_COMPITO, "Seleziona un compito da modificare");
            }
        });
        
        btnEliminaCompito.setOnAction(e -> {
            Compito compitoSelezionato = tabellaCompiti.getSelectionModel().getSelectedItem();
            if (compitoSelezionato != null) {
                eliminaCompitoEsistente(compitoSelezionato, tabellaCompiti);
            } else {
                AlertUtils.showWarning(MSG_SELEZIONA_COMPITO, "Seleziona un compito da eliminare");
            }
        });
        
        btnCreaRidotto.setOnAction(e -> {
            double quantitaPianificata = tabellaCompiti.getItems().stream().mapToDouble(Compito::getQuantita).sum();
            double fabbisognoNetto = quantitaRichiesta - quantitaDisponibile;
            double quantitaMancante = Math.max(0, fabbisognoNetto - quantitaPianificata);
            
            if (quantitaMancante > 0) {
                creaCompitoSilenzioso(cuocoSelezionato, turnoSelezionato, ricettaSelezionata, 
                          tempoStimato, quantitaMancante, quantitaDisponibile);
                AlertUtils.showInfo("Compito ridotto creato", "Creato compito per " + quantitaMancante + " porzioni mancanti");
                dialog.close();
            } else {
                AlertUtils.showInfo("Nessun compito necessario", "La quantità attuale è già sufficiente");
            }
        });
        
        content.getChildren().addAll(
            new Label("Compiti esistenti per questa ricetta:"),
            tabellaCompiti,
            new Label("Azioni disponibili:"),
            pulsanti
        );
        
        // Pulsanti dialog
        ButtonType btnChiudi = new ButtonType("Chiudi", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(btnChiudi);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setPrefWidth(700);
        dialog.getDialogPane().setPrefHeight(500);
        
        dialog.showAndWait();
    }
    
    /**
     * Modifica la quantità di un compito esistente
     */
    private void modificaQuantitaCompito(Compito compito, TableView<Compito> tabella) {
        TextInputDialog dialog = new TextInputDialog(String.valueOf(compito.getQuantita()));
        dialog.setTitle("Modifica Quantità");
        dialog.setHeaderText("Modifica la quantità per: " + compito.getRicetta().getNome());
        dialog.setContentText("Nuova quantità:");
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(quantitaStr -> {
            try {
                // Supporta sia virgola che punto come separatore decimale
                String normalized = quantitaStr.trim().replace(',', '.');
                double nuovaQuantita = Double.parseDouble(normalized);
                
                if (nuovaQuantita >= 0) {
                    compito.setQuantita(nuovaQuantita);
                    tabella.refresh();
                    tblCompiti.refresh(); // Aggiorna anche la tabella principale
                    aggiornaCaricoCuochiAutomaticamente();
                    AlertUtils.showInfo("Quantità modificata", 
                        "Quantità aggiornata a " + nuovaQuantita + " porzioni");
                } else {
                    AlertUtils.showError("Valore non valido", "La quantità deve essere maggiore o uguale a zero");
                }
            } catch (NumberFormatException e) {
                AlertUtils.showError("Formato non valido", "Inserisci un numero valido (usa virgola o punto per i decimali)");
            }
        });
    }
    
    /**
     * Elimina un compito esistente dalla pianificazione
     */
    private void eliminaCompitoEsistente(Compito compito, TableView<Compito> tabella) {
        Alert conferma = new Alert(Alert.AlertType.CONFIRMATION);
        conferma.setTitle("Conferma eliminazione");
        conferma.setHeaderText("Eliminare questo compito?");
        conferma.setContentText("Ricetta: " + compito.getRicetta().getNome() + 
                               "\nQuantità: " + compito.getQuantita() + 
                               "\nCuoco: " + (compito.getCuocoAssegnato() != null ? 
                                           compito.getCuocoAssegnato().getNome() + " " + compito.getCuocoAssegnato().getCognome() : 
                                           "Non assegnato") +
                               "\n\nIl tempo del cuoco verrà liberato automaticamente.");
        
        Optional<ButtonType> result = conferma.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Rimuovi dalle liste
            compitoCucinaService.getCompiti().remove(compito);
            tabella.getItems().remove(compito);
            if (compitiCorrente != null) {
                compitiCorrente.remove(compito);
            }
            
            // Aggiorna le visualizzazioni
            tblCompiti.refresh();
            aggiornaListaCuochiDisponibiliPerCompito();
            aggiornaCaricoCuochiAutomaticamente();
            
            AlertUtils.showInfo("Compito eliminato", 
                "Compito eliminato con successo. Il tempo del cuoco è stato liberato.");
        }
    }
    
    @FXML
    public void onAssegnaCompito() {
        Cuoco cuocoSelezionato = cmbCuochi.getSelectionModel().getSelectedItem();
        Turno turnoSelezionato = cmbTurni.getSelectionModel().getSelectedItem();
        Ricetta ricettaSelezionata = cmbRicette.getSelectionModel().getSelectedItem();
        
        if (cuocoSelezionato == null) {
            AlertUtils.showWarning("Cuoco mancante", "Seleziona un cuoco per il compito");
            return;
        }
        
        if (turnoSelezionato == null) {
            AlertUtils.showWarning("Turno mancante", "Seleziona un turno per il compito");
            return;
        }
        
        if (ricettaSelezionata == null) {
            AlertUtils.showWarning("Ricetta mancante", "Seleziona una ricetta per il compito");
            return;
        }
        
        int tempoStimato = spnTempoStimato.getValue();
        double quantita = spnQuantita.getValue();
        
        // CONTROLLO INTELLIGENTE: Verifica preparazioni già pianificate
        List<Compito> compitiEsistenti = trovaCompitiEsistentiPerRicetta(ricettaSelezionata);
        double quantitaGiaPianificata = calcolaQuantitaTotalePianificata(ricettaSelezionata);
        double quantitaGiaCompletata = calcolaQuantitaGiaCompletata(ricettaSelezionata);
        
        if (!compitiEsistenti.isEmpty()) {
            // Mostra dialog di gestione intelligente preparazioni
            gestisciPreparazioniEsistenti(cuocoSelezionato, turnoSelezionato, ricettaSelezionata, 
                                       tempoStimato, quantita, quantitaGiaCompletata, 
                                       compitiEsistenti, quantitaGiaPianificata);
        } else {
            // Nessun compito esistente, procedi normalmente
            creaCompito(cuocoSelezionato, turnoSelezionato, ricettaSelezionata, tempoStimato, quantita, quantitaGiaCompletata);
            // Alert di successo solo per compiti normali
            AlertUtils.showInfo("Compito assegnato", 
                "Compito assegnato con successo!\n" +
                "Ricetta: " + ricettaSelezionata.getNome() + "\n" +
                "Cuoco: " + cuocoSelezionato.getNome() + " " + cuocoSelezionato.getCognome() + "\n" +
                "Durata: " + tempoStimato + " minuti");
        }
        
        // Aggiorna la lista dei cuochi disponibili per riflettere il nuovo stato
        aggiornaListaCuochiDisponibiliPerCompito();
        
        // Aggiorna automaticamente il carico di lavoro dei cuochi
        aggiornaCaricoCuochiAutomaticamente();
    }
    
    /**
     * Crea un compito senza mostrare messaggi aggiuntivi (per compiti ridotti)
     */
    private void creaCompitoSilenzioso(Cuoco cuoco, Turno turno, Ricetta ricetta, int tempoStimato, double quantita, double quantitaDisponibile) {
        creaCompito(cuoco, turno, ricetta, tempoStimato, quantita, quantitaDisponibile);
    }
    
    private void creaCompito(Cuoco cuoco, Turno turno, Ricetta ricetta, int tempoStimato, double quantita, double quantitaDisponibile) {
        // Crea il compito usando il servizio CON l'evento corrente
        String turnoStr = turno.getData() + " " + turno.getOraInizio() + "-" + turno.getOraFine();
        
        // Calcola quantità effettiva da preparare
        double quantitaEffettiva = Math.max(0, quantita - quantitaDisponibile);
        int tempoEffettivo = quantitaEffettiva <= 0 ? 0 : tempoStimato;
        
        Compito nuovoCompito = compitoCucinaService.assegnaCompito(cuoco, turnoStr, ricetta, tempoEffettivo, quantitaEffettiva, eventoCorrente);
        
        // Aggiungi note per avanzi tramite FeedbackService
        if (quantitaDisponibile > 0) {
            String note = "Quantità già disponibile: " + quantitaDisponibile + ". ";
            feedbackService.aggiungiFeedback(nuovoCompito.getId(), "NOTE CHEF: " + note, "Sistema");
        }
        
        // Imposta l'importanza in base ai radio button selezionati
        if (grpImportanza != null && grpImportanza.getSelectedToggle() != null) {
            RadioButton selectedRadio = (RadioButton) grpImportanza.getSelectedToggle();
            int importanza;
            switch (selectedRadio.getText()) {
                case "Alta":
                    importanza = 5;
                    break;
                case "Media":
                    importanza = 3;
                    break;
                case "Bassa":
                    importanza = 1;
                    break;
                default:
                    importanza = 3;
            }
            nuovoCompito.setImportanza(importanza);
        } else {
            // Valore predefinito se il ToggleGroup non è configurato
            nuovoCompito.setImportanza(3); // Media
        }
        
        // Aggiorna la lista dei compiti nell'evento corrente prima di aggiornare l'UI
        // Prima aggiungiamo il compito alla lista originale
        if (compitiCorrente == null) {
            compitiCorrente = FXCollections.observableArrayList();
            compitiFiltrati = new FilteredList<>(compitiCorrente);
            tblCompiti.setItems(compitiFiltrati);
        }
        
        // Aggiungi alla lista originale (non alla FilteredList)
        compitiCorrente.add(nuovoCompito);
        
        // La FilteredList si aggiorna automaticamente
        
        // Resetta i campi del form nell'ordine del nuovo flusso
        cmbRicette.getSelectionModel().clearSelection();
        cmbTurni.getSelectionModel().clearSelection();
        spnTempoStimato.getValueFactory().setValue(30);
        cmbCuochi.getSelectionModel().clearSelection();
        spnQuantita.getValueFactory().setValue(1.0);
        // Resetta anche l'importanza al valore predefinito (Alta)
        if (grpImportanza != null && grpImportanza.getToggles().size() > 0) {
            grpImportanza.getToggles().get(0).setSelected(true);
        }
    }
    
    /**
     * Ordina i compiti per importanza.
     * Riferimento: UC "Gestione dei Compiti della cucina" - Passo 4
     */
    @FXML
    public void onOrdinaCompiti() {
        // Usa direttamente la lista della tabella per evitare problemi di sincronizzazione
        ObservableList<Compito> compitiTabella = tblCompiti.getItems();
        if (compitiTabella != null && !compitiTabella.isEmpty()) {
            ObservableList<Compito> compitiOrdinati = compitoCucinaService.ordinaCompitiPerImportanza(compitiTabella);
            tblCompiti.setItems(compitiOrdinati);
            
            // Aggiorna anche compitiCorrente per mantenere la sincronizzazione
            compitiCorrente = compitiOrdinati;
        }
    }
    
    /**
     * Monitora l'avanzamento dei compiti.
     * Riferimento: UC "Gestione dei Compiti della cucina" - Passo 6
     * 
     * Questo pulsante aggiorna la visualizzazione dei compiti e mostra un riepilogo
     * dello stato di avanzamento (quanti compiti sono stati completati, quanti sono in corso, ecc.)
     */
    @FXML
    public void onMonitoraAvanzamento() {
        if (compitiCorrente != null && !compitiCorrente.isEmpty()) {
            // Aggiorna la tabella per mostrare lo stato corrente
            tblCompiti.refresh();
            
            // Calcola statistiche sullo stato dei compiti
            int totale = compitiCorrente.size();
            int completati = 0;
            int inCorso = 0;
            int daFare = 0;
            int bloccati = 0;
            
            for (Compito compito : compitiCorrente) {
                String stato = compito.getStato().toLowerCase();
                if (stato.contains("complet")) {
                    completati++;
                } else if (stato.contains("corso")) {
                    inCorso++;
                } else if (stato.contains("blocc")) {
                    bloccati++;
                } else {
                    daFare++;
                }
            }
            
            // Calcola percentuali per tutti gli stati
            int percentualeCompletati = (completati * 100) / totale;
            int percentualeInCorso = (inCorso * 100) / totale;
            int percentualeDaFare = (daFare * 100) / totale;
            int percentualeBloccati = (bloccati * 100) / totale;
            
            // Mostra finestra con statistiche
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Avanzamento Compiti");
            alert.setHeaderText("Stato di avanzamento dei compiti");
            alert.setContentText(
                "Completati: " + completati + " (" + percentualeCompletati + "%)\n" +
                "In corso: " + inCorso + " (" + percentualeInCorso + "%)\n" +
                "Da fare: " + daFare + " (" + percentualeDaFare + "%)\n" +
                "Bloccati: " + bloccati + " (" + percentualeBloccati + "%)"
            );
            alert.showAndWait();
        } else {
            AlertUtils.showWarning("Nessun compito", "Non ci sono compiti da monitorare. Seleziona un evento e premi 'Crea riepilogo compiti'.");
        }
    }
    
    /**
     * Mostra i dettagli di un compito (SOLO VISUALIZZAZIONE).
     */
    @FXML
    public void onVisualizzaDettagliCompito() {
        Compito compitoSelezionato = tblCompiti.getSelectionModel().getSelectedItem();
        
        if (compitoSelezionato != null) {
            // Crea la finestra di dettaglio
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Dettagli Compito");
            alert.setHeaderText("Dettagli per: " + compitoSelezionato.getRicetta().getNome());
            
            // Informazioni complete
            StringBuilder sb = new StringBuilder();
            sb.append("Ricetta: ").append(compitoSelezionato.getRicetta().getNome()).append("\n");
            sb.append("Cuoco: ").append(
                compitoSelezionato.getCuocoAssegnato() != null ? 
                compitoSelezionato.getCuocoAssegnato().getNome() + " " + compitoSelezionato.getCuocoAssegnato().getCognome() : 
                "Non assegnato").append("\n");
            sb.append("Turno: ").append(compitoSelezionato.getTurno()).append("\n");
            sb.append("Tempo stimato: ").append(compitoSelezionato.getTempoStimato()).append(" minuti\n");
            sb.append("Quantità: ").append(compitoSelezionato.getQuantita()).append(" porzioni\n");
            sb.append("Importanza: ");
            
            switch (compitoSelezionato.getImportanza()) {
                case 5: sb.append("Alta (5)"); break;
                case 3: sb.append("Media (3)"); break;
                case 1: sb.append("Bassa (1)"); break;
                default: sb.append(compitoSelezionato.getImportanza());
            }
            sb.append("\n");
            
            sb.append("Stato: ").append(compitoSelezionato.getStato()).append("\n\n");
            
            // Feedback se presente (tramite FeedbackService)
            String feedback = feedbackService.getFeedbackFormattatiPerCompito(compitoSelezionato.getId());
            if (feedback != null && !feedback.trim().isEmpty() && !feedback.equals("Nessun feedback disponibile")) {
                sb.append("Feedback:\n").append(feedback);
            } else {
                sb.append("Nessun feedback disponibile");
            }
            
            alert.setContentText(sb.toString());
            
            // Espande la finestra per mostrare tutti i dettagli
            alert.getDialogPane().setPrefWidth(450);
            alert.getDialogPane().setPrefHeight(350);
            alert.setResizable(true);
            
            alert.showAndWait();
        } else {
            // STANDARDIZZATO: Messaggio uniforme per selezione compito  
            AlertUtils.showWarning("Selezione richiesta", "Seleziona un compito per visualizzarne i dettagli");
        }
    }
    
    /**
     * Aggiorna automaticamente la vista del carico di lavoro dei cuochi
     * Considera tutti i compiti di tutti gli eventi (carico universale)
     */
    private void aggiornaCaricoCuochiAutomaticamente() {
        // Ottieni tutti i cuochi
        ObservableList<Cuoco> tuttiCuochi = utenteService.getCuochi();
        ObservableList<CaricoCuoco> caricoCuochi = FXCollections.observableArrayList();
        
        for (Cuoco cuoco : tuttiCuochi) {
            // Calcola compiti per questo cuoco attraverso TUTTI gli eventi (carico universale)
            List<Compito> compitiCuoco = compitoCucinaService.getCompiti().stream()
                .filter(c -> c.getCuocoAssegnato() != null && c.getCuocoAssegnato().equals(cuoco))
                .collect(Collectors.toList());
            
            int numeroCompiti = compitiCuoco.size();
            int tempoTotale = compitiCuoco.stream()
                .mapToInt(Compito::getTempoStimato)
                .sum();
            
            // Dettaglio compiti con ricetta e tempo 
            String dettaglio = compitiCuoco.stream()
                .map(c -> c.getRicetta().getNome() + " (" + c.getTempoStimato() + "min)")
                .collect(Collectors.joining(", "));
            
            if (dettaglio.isEmpty()) {
                dettaglio = "Nessun compito assegnato";
            }
            
            // Calcola disponibilità
            String disponibilita;
            if (tempoTotale == 0) {
                disponibilita = "Completamente libero";
            } else if (tempoTotale <= 240) { // 4 ore
                disponibilita = "Può fare altro";
            } else if (tempoTotale <= 360) { // 6 ore
                disponibilita = "Abbastanza impegnato";
        } else {
                disponibilita = "Molto impegnato";
            }
            
            CaricoCuoco carico = new CaricoCuoco(cuoco, numeroCompiti, tempoTotale, dettaglio, disponibilita);
            caricoCuochi.add(carico);
        }
        
        // Ordina per tempo totale decrescente
        caricoCuochi.sort((a, b) -> Integer.compare(b.getTempoTotale(), a.getTempoTotale()));
        
        tblCaricoCuochi.setItems(caricoCuochi);
    }
    
    /**
     * Visualizza i feedback dei cuochi per un compito
     */
    @FXML
    public void onVisualizzaFeedback() {
        Compito compitoSelezionato = tblCompiti.getSelectionModel().getSelectedItem();
        
        if (compitoSelezionato != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Feedback del Compito");
            alert.setHeaderText("Feedback per: " + compitoSelezionato.getRicetta().getNome());
            
            String feedback = feedbackService.getFeedbackFormattatiPerCompito(compitoSelezionato.getId());
            if (feedback == null || feedback.trim().isEmpty() || feedback.equals("Nessun feedback disponibile")) {
                alert.setContentText("Nessun feedback disponibile per questo compito.");
            } else {
                alert.setContentText(feedback);
                // Espande la finestra per mostrare meglio i feedback lunghi
                alert.getDialogPane().setPrefWidth(500);
                alert.getDialogPane().setPrefHeight(300);
                alert.setResizable(true);
            }
            
            alert.showAndWait();
        } else {
            // STANDARDIZZATO: Messaggio uniforme per selezione compito
            AlertUtils.showWarning("Selezione richiesta", "Seleziona un compito dalla tabella");
        }
    }
    
    /**
     * Gestisce la modifica di un compito esistente (SENZA possibilità di cambiare lo stato)
     */
    @FXML
    public void onModificaCompito() {
        Compito compitoSelezionato = tblCompiti.getSelectionModel().getSelectedItem();
        if (compitoSelezionato != null) {
            mostraDialogModificaCompito(compitoSelezionato);
        } else {
            // STANDARDIZZATO: Messaggio uniforme per selezione compito
            AlertUtils.showWarning("Selezione richiesta", "Seleziona un compito da modificare");
        }
    }
    
    /**
     * Mostra un dialog per modificare un compito esistente (SENZA modifica dello stato)
     */
    private void mostraDialogModificaCompito(Compito compito) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modifica Compito");
        dialog.setHeaderText("Modifica i dettagli del compito: " + compito.getRicetta().getNome());
        
        // Crea i controlli per la modifica
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        // Ricetta (solo visualizzazione)
        Label lblRicetta = new Label("Ricetta:");
        Label lblRicettaValue = new Label(compito.getRicetta().getNome());
        lblRicettaValue.setStyle("-fx-font-weight: bold;");
        
        // Stato (solo visualizzazione - NON modificabile)
        Label lblStatoView = new Label("Stato attuale:");
        Label lblStatoValue = new Label(compito.getStato());
        lblStatoValue.setStyle("-fx-font-weight: bold; -fx-text-fill: #666666;");
        
        // Cuoco (modificabile) - Filtraggio per evento e turno
        Label lblCuoco = new Label("Cuoco:");
        ComboBox<Cuoco> cmbCuocoModifica = new ComboBox<>();
        
        // Cerca il turno corrispondente al compito
        Turno turnoCompito = null;
        String turnoString = compito.getTurno();
        for (Turno t : compitoCucinaService.getTurni()) {
            String tStr = t.getData() + " " + t.getOraInizio() + "-" + t.getOraFine();
            if (tStr.equals(turnoString)) {
                turnoCompito = t;
                break;
            }
        }
        
        // Carica cuochi disponibili per questo turno (disponibilità globale)
        if (turnoCompito != null) {
            List<Cuoco> cuochiDisponibili = compitoCucinaService.getCuochiDisponibiliPerCompito(
                turnoCompito, compito.getTempoStimato(), eventoCorrente);
            
            // Aggiungi il cuoco attualmente assegnato se non è nella lista (per permettere di mantenerlo)
            if (compito.getCuocoAssegnato() != null && !cuochiDisponibili.contains(compito.getCuocoAssegnato())) {
                cuochiDisponibili.add(compito.getCuocoAssegnato());
            }
            
            cmbCuocoModifica.setItems(FXCollections.observableArrayList(cuochiDisponibili));
        } else {
            // Fallback: tutti i cuochi
            cmbCuocoModifica.setItems(utenteService.getCuochi());
        }
        
        cmbCuocoModifica.getSelectionModel().select(compito.getCuocoAssegnato());
        
        // Tempo stimato (modificabile)
        Label lblTempo = new Label("Tempo stimato (min):");
        Spinner<Integer> spnTempoModifica = new Spinner<>(5, 480, compito.getTempoStimato(), 5);
        spnTempoModifica.setEditable(true);
        
        // Quantità (modificabile)
        Label lblQuantita = new Label("Quantità:");
        Spinner<Double> spnQuantitaModifica = new Spinner<>(0.1, 100.0, compito.getQuantita(), 0.1);
        spnQuantitaModifica.setEditable(true);
        // Configura il formato italiano per la quantità di modifica
        spnQuantitaModifica.getValueFactory().setConverter(createItalianDecimalConverter());
        
        // Importanza (modificabile)
        Label lblImportanza = new Label("Importanza:");
        ToggleGroup grpImportanzaModifica = new ToggleGroup();
        HBox hboxImportanza = new HBox(10);
        
        RadioButton rbAlta = new RadioButton("Alta");
        RadioButton rbMedia = new RadioButton("Media");
        RadioButton rbBassa = new RadioButton("Bassa");
        
        rbAlta.setToggleGroup(grpImportanzaModifica);
        rbMedia.setToggleGroup(grpImportanzaModifica);
        rbBassa.setToggleGroup(grpImportanzaModifica);
        
        // Seleziona l'importanza corrente
        switch (compito.getImportanza()) {
            case 5:
                rbAlta.setSelected(true);
                break;
            case 3:
                rbMedia.setSelected(true);
                break;
            case 1:
                rbBassa.setSelected(true);
                break;
            default:
                rbMedia.setSelected(true);
        }
        
        hboxImportanza.getChildren().addAll(rbAlta, rbMedia, rbBassa);
        
        // Aggiungi controlli al grid (senza il campo stato modificabile)
        grid.add(lblRicetta, 0, 0);
        grid.add(lblRicettaValue, 1, 0);
        grid.add(lblStatoView, 0, 1);
        grid.add(lblStatoValue, 1, 1);
        grid.add(lblCuoco, 0, 2);
        grid.add(cmbCuocoModifica, 1, 2);
        grid.add(lblTempo, 0, 3);
        grid.add(spnTempoModifica, 1, 3);
        grid.add(lblQuantita, 0, 4);
        grid.add(spnQuantitaModifica, 1, 4);
        grid.add(lblImportanza, 0, 5);
        grid.add(hboxImportanza, 1, 5);
        
        dialog.getDialogPane().setContent(grid);
        
        // Configura i bottoni
        ButtonType saveButtonType = new ButtonType("Salva", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        // Gestisci il risultato
        dialog.showAndWait().ifPresent(response -> {
            if (response == saveButtonType) {
                // Aggiorna il compito con i nuovi valori (SENZA cambiare lo stato)
                compito.setCuocoAssegnato(cmbCuocoModifica.getSelectionModel().getSelectedItem());
                compito.setDurata(spnTempoModifica.getValue());
                compito.setQuantita(spnQuantitaModifica.getValue());
                // NON aggiorniamo lo stato - quello rimane invariato
                
                // Aggiorna importanza
                if (grpImportanzaModifica.getSelectedToggle() != null) {
                    RadioButton selectedRadio = (RadioButton) grpImportanzaModifica.getSelectedToggle();
                    int importanza;
                    switch (selectedRadio.getText()) {
                        case "Alta":
                            importanza = 5;
                            break;
                        case "Media":
                            importanza = 3;
                            break;
                        case "Bassa":
                            importanza = 1;
                            break;
                        default:
                            importanza = 3;
                    }
                    compito.setImportanza(importanza);
                }
                
                // Aggiorna la tabella
                tblCompiti.refresh();
                AlertUtils.showInfo("Compito modificato", "Il compito è stato modificato con successo\n(Lo stato rimane invariato)");
                
                // Aggiorna la lista cuochi se necessario
                aggiornaListaCuochiDisponibiliPerCompito();
                
                // Aggiorna automaticamente il carico di lavoro dei cuochi
                aggiornaCaricoCuochiAutomaticamente();
            }
        });
    }
    
    /**
     * Inizializza la tabella del carico cuochi
     */
    private void initCaricoCuochiTable() {
        if (colCuocoCarico != null) {
            colCuocoCarico.setCellValueFactory(cellData -> cellData.getValue().nomeCognomeProperty());
            colNumCompiti.setCellValueFactory(cellData -> cellData.getValue().numeroCompitiProperty().asObject());
            colTempoTotale.setCellValueFactory(cellData -> cellData.getValue().tempoTotaleProperty().asObject());
            colCompitiDettaglio.setCellValueFactory(cellData -> cellData.getValue().compitiDettaglioProperty());
            colDisponibilita.setCellValueFactory(cellData -> cellData.getValue().disponibilitaProperty());
            
                    // Configura le colonne per occupare tutta la larghezza disponibile
        tblCaricoCuochi.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        }
    }
    
    /**
     * Inizializza la tabella dei turni
     */
    private void initTurniTableView() {
        // Configurazione delle colonne
        colData.setCellValueFactory(new PropertyValueFactory<>("data"));
        colOraInizio.setCellValueFactory(new PropertyValueFactory<>("oraInizio"));
        colOraFine.setCellValueFactory(new PropertyValueFactory<>("oraFine"));
        colLuogo.setCellValueFactory(new PropertyValueFactory<>("luogo"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        
        // Configura la tabella per occupare tutta la larghezza disponibile
        tblTurni.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        
        // Imposta i dati nella tabella
        tblTurni.setItems(compitoCucinaService.getTurni());
        
        // Aggiungi un menu contestuale per modificare/eliminare
        tblTurni.setRowFactory(tv -> {
            TableRow<Turno> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();
            
            MenuItem modifyItem = new MenuItem("Modifica");
            modifyItem.setOnAction(event -> modificaTurno(row.getItem()));
            
            MenuItem deleteItem = new MenuItem("Elimina");
            deleteItem.setOnAction(event -> eliminaTurno(row.getItem()));
            
            contextMenu.getItems().addAll(modifyItem, deleteItem);
            
            // Imposta il menu contestuale solo per le righe non vuote
            row.contextMenuProperty().bind(
                Bindings.when(row.emptyProperty())
                .then((ContextMenu)null)
                .otherwise(contextMenu)
            );
            
            return row;
        });
    }
    
    /**
     * Visualizza tutti i turni in una finestra separata
     */
    @FXML
    public void onVisualizzaTurni() {
        // Crea una nuova finestra di dialogo
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Gestione Turni");
        dialog.setHeaderText("Visualizza, modifica ed elimina i turni");
        
        // Aggiungi pulsante Chiudi
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        
        // Crea una TableView per i turni
        TableView<Turno> tableTurni = new TableView<>();
        tableTurni.setPrefHeight(400);
        tableTurni.setPrefWidth(600);
        
        // Crea le colonne
        TableColumn<Turno, LocalDate> colData = new TableColumn<>("Data");
        colData.setCellValueFactory(new PropertyValueFactory<>("data"));
        colData.setPrefWidth(100);
        
        TableColumn<Turno, LocalTime> colOraInizio = new TableColumn<>("Ora Inizio");
        colOraInizio.setCellValueFactory(new PropertyValueFactory<>("oraInizio"));
        colOraInizio.setPrefWidth(100);
        
        TableColumn<Turno, LocalTime> colOraFine = new TableColumn<>("Ora Fine");
        colOraFine.setCellValueFactory(new PropertyValueFactory<>("oraFine"));
        colOraFine.setPrefWidth(100);
        
        TableColumn<Turno, String> colLuogo = new TableColumn<>("Luogo");
        colLuogo.setCellValueFactory(new PropertyValueFactory<>("luogo"));
        colLuogo.setPrefWidth(150);
        
        TableColumn<Turno, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colTipo.setPrefWidth(100);
        
        // Aggiungi le colonne alla tabella
        tableTurni.getColumns().addAll(List.of(colData, colOraInizio, colOraFine, colLuogo, colTipo));
        
        // Configura la tabella per occupare tutta la larghezza disponibile
        tableTurni.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        
        // Aggiungi i dati
        tableTurni.setItems(compitoCucinaService.getTurni());
        
        // Aggiungi menu contestuale
        tableTurni.setRowFactory(tv -> {
            TableRow<Turno> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();
            
            MenuItem modifyItem = new MenuItem("Modifica");
            modifyItem.setOnAction(event -> {
                modificaTurno(row.getItem());
                tableTurni.refresh(); // Aggiorna la tabella dopo la modifica
            });
            
            MenuItem deleteItem = new MenuItem("Elimina");
            deleteItem.setOnAction(event -> {
                if (eliminaTurno(row.getItem())) {
                    tableTurni.setItems(compitoCucinaService.getTurni()); // Aggiorna la lista dopo l'eliminazione
                }
            });
            
            contextMenu.getItems().addAll(modifyItem, deleteItem);
            
            // Imposta il menu contestuale solo per le righe non vuote
            row.contextMenuProperty().bind(
                Bindings.when(row.emptyProperty())
                .then((ContextMenu)null)
                .otherwise(contextMenu)
            );
            
            return row;
        });
        
        // Aggiungi etichetta informativa
        Label lblInfo = new Label("Clicca con il tasto destro su un turno per modificarlo o eliminarlo.");
        
        // Organizza in un layout
        javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(10);
        vbox.getChildren().addAll(lblInfo, tableTurni);
        vbox.setPadding(new javafx.geometry.Insets(10));
        
        // Imposta il contenuto
        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().setPrefSize(650, 500);
        
        // Mostra la finestra
        dialog.showAndWait();
    }
    
    /**
     * Modifica un turno selezionato
     * @return true se la modifica è stata completata con successo
     */
    private boolean modificaTurno(Turno turno) {
        if (turno == null) return false;
        
        // Crea una nuova finestra di dialogo
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modifica Turno");
        dialog.setHeaderText("Modifica i dettagli del turno");
        
        // Aggiungi pulsanti
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        // Crea controlli per l'input
        DatePicker datePicker = new DatePicker(turno.getData());
        TextField txtStartTime = new TextField(turno.getOraInizio().toString());
        TextField txtEndTime = new TextField(turno.getOraFine().toString());
        TextField txtLocation = new TextField(turno.getLuogo());
        TextField txtType = new TextField(turno.getTipo());
        
        // Crea layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        
        grid.add(new Label("Data:"), 0, 0);
        grid.add(datePicker, 1, 0);
        grid.add(new Label("Ora inizio:"), 0, 1);
        grid.add(txtStartTime, 1, 1);
        grid.add(new Label("Ora fine:"), 0, 2);
        grid.add(txtEndTime, 1, 2);
        grid.add(new Label("Luogo:"), 0, 3);
        grid.add(txtLocation, 1, 3);
        grid.add(new Label("Tipo:"), 0, 4);
        grid.add(txtType, 1, 4);
        
        dialog.getDialogPane().setContent(grid);
        
        // Converti risultato
        Optional<ButtonType> result = dialog.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                LocalTime oraInizio = LocalTime.parse(txtStartTime.getText());
                LocalTime oraFine = LocalTime.parse(txtEndTime.getText());
                // Per la modifica completa, avremmo bisogno di più metodi setter nella classe Turno
                
                // Per ora, aggiorniamo solo l'orario
                turno.aggiornaOrario(oraInizio, oraFine);
                
                // Aggiorna anche la lista nel ComboBox dei turni
                cmbTurni.setItems(compitoCucinaService.getTurni());
                
                AlertUtils.showInfo("Turno aggiornato", "Il turno è stato aggiornato con successo");
                return true;
            } catch (Exception e) {
                AlertUtils.showError("Errore", "Formato non valido: " + e.getMessage());
            }
        }
        return false;
    }
    
    /**
     * Elimina un turno
     * @return true se l'eliminazione è stata completata con successo
     */
    private boolean eliminaTurno(Turno turno) {
        if (turno == null) return false;
        
        // Verifica se ci sono compiti associati al turno
        String turnoStr = turno.getData() + " " + turno.getOraInizio() + "-" + turno.getOraFine();
        boolean hasAssociatedTasks = compitoCucinaService.getCompiti().stream()
            .anyMatch(compito -> turnoStr.equals(compito.getTurno()));
        
        if (hasAssociatedTasks) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Impossibile eliminare");
            alert.setHeaderText("Ci sono compiti associati a questo turno");
            alert.setContentText("Prima di eliminare il turno, è necessario riassegnare o eliminare i compiti associati.");
            alert.showAndWait();
            return false;
        }
        
        // Chiedi conferma prima di eliminare
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma eliminazione");
        alert.setHeaderText("Stai per eliminare questo turno");
        alert.setContentText("Sei sicuro di voler eliminare il turno del " + turno.getData() + " " + 
                            turno.getOraInizio() + "-" + turno.getOraFine() + "?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Rimuovi il turno dalla lista
            compitoCucinaService.getTurni().remove(turno);
            
            // Aggiorna anche la lista nel ComboBox
            cmbTurni.setItems(compitoCucinaService.getTurni());
            
            AlertUtils.showInfo("Turno eliminato", "Il turno è stato eliminato con successo");
            return true;
        }
        return false;
    }
    
    @FXML
    public void onVisualizzaDettagliEvento() {
        Evento evento = cmbEventi.getValue();
        if (evento == null) {
            AlertUtils.showWarning("Attenzione", "Selezionare prima un evento");
            return;
        }
        
        // Creo una dialog per visualizzare i dettagli dell'evento
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Dettagli Evento");
        dialog.setHeaderText("Dettagli completi dell'evento: " + evento.getNome());
        
        // Preparo una griglia per i dettagli
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        
        // Aggiungo i dettagli dell'evento
        int row = 0;
        
        grid.add(new Label("Nome:"), 0, row);
        grid.add(new Label(evento.getNome()), 1, row++);
        
        grid.add(new Label("Data inizio:"), 0, row);
        grid.add(new Label(evento.getDataInizio().toString()), 1, row++);
        
        grid.add(new Label("Data fine:"), 0, row);
        grid.add(new Label(evento.getDataFine() != null ? evento.getDataFine().toString() : "Non specificata"), 1, row++);
        
        grid.add(new Label("Luogo:"), 0, row);
        grid.add(new Label(evento.getLuogo()), 1, row++);
        
        grid.add(new Label("Numero di persone:"), 0, row);
        grid.add(new Label(String.valueOf(evento.getNumeroDiPersone())), 1, row++);
        
        grid.add(new Label("Ricorrente:"), 0, row);
        grid.add(new Label(evento.isRicorrente() ? "Sì" : "No"), 1, row++);
        
        grid.add(new Label("Stato:"), 0, row);
        grid.add(new Label(evento.getStato()), 1, row++);
        
        if (evento.getNote() != null && !evento.getNote().isEmpty()) {
            grid.add(new Label("Note:"), 0, row);
            
            TextArea txtNote = new TextArea(evento.getNote());
            txtNote.setEditable(false);
            txtNote.setPrefWidth(300);
            txtNote.setPrefHeight(100);
            txtNote.setWrapText(true);
            
            GridPane.setVgrow(txtNote, Priority.ALWAYS);
            grid.add(txtNote, 1, row++);
        }
        
        // Aggiungo informazioni sul menu dell'evento se presente
        if (evento.getMenu() != null) {
            grid.add(new Label("Menu:"), 0, row);
            grid.add(new Label(evento.getMenu().getTitolo()), 1, row++);
        }
        
        // Aggiungo un pulsante di chiusura alla dialog
        ButtonType btnChiudi = new ButtonType("Chiudi", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(btnChiudi);
        
        dialog.getDialogPane().setContent(grid);
        
        // Adatta la dimensione della dialog al contenuto
        dialog.getDialogPane().setPrefWidth(550);
        dialog.getDialogPane().setPrefHeight(500);
        
        // Mostra la dialog
        dialog.showAndWait();
    }

    /**
     * Inizializza la tabella per un evento specifico
     */
    private void inizializzaTabellaPerEvento(Evento evento) {
        if (evento != null) {
            
            // Ottieni compiti filtrati per l'evento corrente
            List<Compito> compitiEventoFiltrati = compitoCucinaService.getCompitiPerEvento(evento);
            
            // Crea una nuova lista osservabile per evitare problemi di riferimento
            compitiCorrente = FXCollections.observableArrayList(compitiEventoFiltrati);
            
            // Inizializza la FilteredList per i filtri
            compitiFiltrati = new FilteredList<>(compitiCorrente);
            tblCompiti.setItems(compitiFiltrati);
            
            // Resetta il filtro stato
            cmbFiltroStato.setValue("Tutti");
            
            // Torna alla vista completa
            rbVistaCompleta.setSelected(true);
            mostraVistaCompleta();
            
            // Pulisci la selezione nella tabella
            tblCompiti.getSelectionModel().clearSelection();
        }
    }

    @FXML
    public void onEliminaCompito() {
        Compito compitoSelezionato = tblCompiti.getSelectionModel().getSelectedItem();
        
        if (compitoSelezionato != null) {
            // Chiedi conferma prima di eliminare
            Alert conferma = new Alert(Alert.AlertType.CONFIRMATION);
            conferma.setTitle("Conferma eliminazione");
            conferma.setHeaderText("Eliminare il compito selezionato?");
            conferma.setContentText("Ricetta: " + compitoSelezionato.getRicetta().getNome() + "\n" +
                                   "Cuoco: " + (compitoSelezionato.getCuocoAssegnato() != null ? 
                                   compitoSelezionato.getCuocoAssegnato().getNome() + " " + compitoSelezionato.getCuocoAssegnato().getCognome() : 
                                   "Non assegnato") + "\n" +
                                   "Quantità: " + compitoSelezionato.getQuantita() + " porzioni");
            
            ButtonType btnConferma = new ButtonType("Elimina", ButtonBar.ButtonData.OK_DONE);
            ButtonType btnAnnulla = new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE);
            conferma.getButtonTypes().setAll(btnConferma, btnAnnulla);
            
            if (conferma.showAndWait().orElse(btnAnnulla) == btnConferma) {
                // Rimuovi il compito dalla lista
                compitoCucinaService.getCompiti().remove(compitoSelezionato);
                
                // Aggiorna la tabella  
                if (eventoCorrente != null) {
                    inizializzaTabellaPerEvento(eventoCorrente);
                }
                
                                    // MESSAGGIO SPECIFICO: Più informativo
                    AlertUtils.showInfo("Operazione sul compito", 
                        "Compito eliminato: " + compitoSelezionato.getRicetta().getNome());
                
                // Aggiorna automaticamente il carico di lavoro dei cuochi
                aggiornaCaricoCuochiAutomaticamente();
            }
        } else {
            // STANDARDIZZATO: Messaggio uniforme per selezione compito
            AlertUtils.showWarning("Selezione richiesta", "Seleziona un compito da eliminare");
        }
    }
    
    /**
     * Crea un convertitore per i decimali in formato italiano (virgola come separatore)
     */
    private StringConverter<Double> createItalianDecimalConverter() {
        return new StringConverter<Double>() {
            private final DecimalFormat format;
            
            {
                DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ITALIAN);
                symbols.setDecimalSeparator(',');
                symbols.setGroupingSeparator('.');
                format = new DecimalFormat("#0.0#", symbols);
            }
            
            @Override
            public String toString(Double value) {
                if (value == null) return "";
                return format.format(value);
            }
            
            @Override
            public Double fromString(String string) {
                if (string == null || string.trim().isEmpty()) return 0.0;
                try {
                    // Sostituisce virgola con punto per il parsing standard
                    String normalized = string.trim().replace(',', '.');
                    return Double.parseDouble(normalized);
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            }
        };
    }
}