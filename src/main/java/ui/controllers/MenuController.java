package ui.controllers;

import domain.menu.Menu;
import domain.menu.SezioneMenu;
import domain.menu.CaratteristicaMenu;
import domain.menu.RicettaInMenu;
import domain.ricette.Ricetta;
import domain.eventi.Evento;
import service.MenuService;
import service.RicettaService;
import service.EventoService;
import ui.utils.AlertUtils;
import ui.utils.ExcelExportUtils;
import ui.viewmodels.RicettaViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList;

/**
 * Rappresenta il controller per la gestione dei menu nel sistema di catering.
 * Implementa il pattern GRASP Controller per il caso d'uso "Gestione dei Menù".
 */
public class MenuController {
    
    @FXML private TextField txtTitolo;
    @FXML private TextArea txtNote;
    @FXML private ComboBox<Menu> cmbMenuEsistenti;
    @FXML private ComboBox<Evento> cmbEventoRiferimento;
    @FXML private TextField txtSezioni;
    @FXML private ListView<RicettaViewModel> lstRicetteDisponibili;
    @FXML private TabPane tabSezioni;
    @FXML private VBox contenitoreMenu;
    @FXML private CheckBox chkPiattiCaldi;
    @FXML private CheckBox chkPiattiFreddi;
    @FXML private CheckBox chkAdattoBuffet;
    @FXML private CheckBox chkFingerFood;
    @FXML private Button btnCambiaStato;
    
    private final MenuService menuService;
    private final RicettaService ricettaService;
    private final EventoService eventoService;
    private Menu menuCorrente;
    private Tab tabSelezionato;
    
    // OTTIMIZZAZIONE: Cache dei pulsanti per evitare lookup ripetuti
    private List<Button> btnPulsantiRicette;
    
    // COSTANTI PER MESSAGGI STANDARDIZZATI
    private static final String MSG_TITOLO_DUPLICATO = "Titolo già esistente";
    private static final String MSG_SELEZIONA_MENU = "Nessun menu selezionato";
    private static final String MSG_OPERAZIONE_NON_VALIDA = "Operazione non valida";
    private static final String MSG_SELEZIONE_RICHIESTA = "Selezione richiesta";
    
    public MenuController(MenuService menuService, RicettaService ricettaService, EventoService eventoService) {
        this.menuService = menuService;
        this.ricettaService = ricettaService;
        this.eventoService = eventoService;
    }
    
    @FXML
    public void initialize() {
        // Inizializza le liste
        cmbMenuEsistenti.setItems(menuService.getMenus());
        cmbEventoRiferimento.setItems(eventoService.getEventi());
        lstRicetteDisponibili.setItems(ricettaService.getRicetteDisponibiliViewModel());
        
        // Configurazione rendering elementi ComboBox
        cmbMenuEsistenti.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Menu item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    // Mostra titolo e stato senza indicatori visivi
                    String stato = item.getStato();
                    setText(item.getTitolo() + " (" + stato + ")");
                    
                    // Nessuno stile applicato per mantenere aspetto basico
                }
            }
        });
        
        cmbMenuEsistenti.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Menu item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    // Mostra titolo e stato senza indicatori visivi
                    String stato = item.getStato();
                    setText(item.getTitolo() + " (" + stato + ")");
                }
            }
        });
        
        // Configurazione rendering elementi ComboBox eventi
        cmbEventoRiferimento.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Evento item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getNome());
            }
        });
        cmbEventoRiferimento.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Evento item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getNome());
            }
        });
        
        // Configurazione rendering elementi ListView
        lstRicetteDisponibili.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(RicettaViewModel item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty || item == null) {
                    setText(null);
                } else {
                    Ricetta ricetta = item.getRicetta();
                    setText(ricetta.getNome() + " (" + ricetta.getTempoPreparazione() + " min)");
                }
            }
        });
        
        // Aggiorna UI quando si seleziona un menu esistente - MODIFICA DIRETTA
        cmbMenuEsistenti.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                menuCorrente = newVal;
                // Abilita automaticamente la modifica DIRETTA del menu originale
                contenitoreMenu.setDisable(false);
                aggiornaUIConMenu(menuCorrente);
                
                // GESTIONE STATO: Controlla se il menu è modificabile
                boolean isModificabile = !newVal.getStato().equals("Pubblicato");
                abilitaModificaMenu(isModificabile);
                
                // AGGIORNA PULSANTE: Cambia il testo del pulsante in base allo stato
                aggiornaPulsanteCambiaStato();
            } else {
                // Se non c'è selezione, disabilita la sezione
                contenitoreMenu.setDisable(true);
                menuCorrente = null;
                aggiornaPulsanteCambiaStato();
            }
        });
        
        // Aggiorna qual è la tab selezionata
        tabSezioni.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> tabSelezionato = newVal
        );
        
        // Configura i checkbox per le caratteristiche
        chkPiattiCaldi.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (menuCorrente != null) {
                if (newVal) {
                    menuCorrente.aggiungiCaratteristica(new CaratteristicaMenu("Piatti Caldi"));
                } else {
                    menuCorrente.rimuoviCaratteristica(new CaratteristicaMenu("Piatti Caldi"));
                }
            }
        });
        
        chkPiattiFreddi.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (menuCorrente != null) {
                if (newVal) {
                    menuCorrente.aggiungiCaratteristica(new CaratteristicaMenu("Piatti Freddi"));
                } else {
                    menuCorrente.rimuoviCaratteristica(new CaratteristicaMenu("Piatti Freddi"));
                }
            }
        });
        
        chkAdattoBuffet.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (menuCorrente != null) {
                if (newVal) {
                    menuCorrente.aggiungiCaratteristica(new CaratteristicaMenu("Adatto a Buffet"));
                } else {
                    menuCorrente.rimuoviCaratteristica(new CaratteristicaMenu("Adatto a Buffet"));
                }
            }
        });
        
        chkFingerFood.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (menuCorrente != null) {
                if (newVal) {
                    menuCorrente.aggiungiCaratteristica(new CaratteristicaMenu("Finger Food"));
                } else {
                    menuCorrente.rimuoviCaratteristica(new CaratteristicaMenu("Finger Food"));
                }
            }
        });
        
        // Disabilita la sezione di modifica del menu inizialmente
        contenitoreMenu.setDisable(true);
        
        // Imposta stato iniziale del pulsante cambia stato
        aggiornaPulsanteCambiaStato();
        
        // Listener per validazione automatica del titolo
        txtTitolo.focusedProperty().addListener((obs, oldVal, newVal) -> {
            // Quando il campo perde il focus, valida automaticamente il titolo
            if (!newVal && !txtTitolo.getText().trim().isEmpty()) {
                onSalvaTitolo();
            }
        });
    }
    
    /**
     * Gestisce la creazione di un nuovo menu.
     * Riferimento: UC "Gestione dei Menù" - Passo 1
     */
    @FXML
    public void onNuovoMenu() {
        // Chiedi il titolo per il nuovo menu
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nuovo Menu");
        dialog.setHeaderText("Crea un nuovo menu");
        dialog.setContentText("Inserisci il titolo del menu:");
        
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String titolo = result.get().trim();
            
            // Crea il menu con titolo e validazione unicità
            Menu nuovoMenu = menuService.creaNuovoMenuConTitolo(titolo);
            
            if (nuovoMenu != null) {
                // Aggiorna PRIMA la lista, poi seleziona
                aggiornaListaMenu();
                
                // Imposta il nuovo menu come corrente
                menuCorrente = nuovoMenu;
                
                // Seleziona automaticamente il nuovo menu nella ComboBox
                cmbMenuEsistenti.getSelectionModel().select(menuCorrente);
                
                // Abilita l'interfaccia e aggiorna i campi
                contenitoreMenu.setDisable(false);
                txtTitolo.setText(titolo);
                txtSezioni.clear();
                txtNote.clear();
                tabSezioni.getTabs().clear();
                
                // I nuovi menu sono sempre in stato "Bozza" quindi modificabili
                abilitaModificaMenu(true);
                
                // Aggiorna il pulsante per nuovi menu mostra "Pubblica"  
                aggiornaPulsanteCambiaStato();
                
                // Focus sul campo sezioni
                txtSezioni.requestFocus();
                
                AlertUtils.showInfo("Menu creato", "Nuovo menu creato: \"" + titolo + "\"");
            } else {
                // STANDARDIZZATO: Messaggio uniforme per titolo duplicato
                AlertUtils.showError(MSG_TITOLO_DUPLICATO, 
                    "Esiste già un menu con il titolo: \"" + titolo + "\"\n" +
                    "Scegli un titolo diverso.");
            }
        }
    }
    
    /**
     * Abilita o disabilita la modifica del menu in base al suo stato.
     * I menu pubblicati non possono essere modificati direttamente.
     */
    private void abilitaModificaMenu(boolean modificabile) {
        // Campi di base
        txtTitolo.setDisable(!modificabile);
        txtSezioni.setDisable(!modificabile);
        txtNote.setDisable(!modificabile);
        
        // ComboBox e liste
        // NOTA: cmbEventoRiferimento rimane sempre attivo per consultare l'evento
        lstRicetteDisponibili.setDisable(!modificabile);
        
        // Checkbox caratteristiche
        chkPiattiCaldi.setDisable(!modificabile);
        chkPiattiFreddi.setDisable(!modificabile);
        chkAdattoBuffet.setDisable(!modificabile);
        chkFingerFood.setDisable(!modificabile);
        
        // IMPORTANTE: Le tab delle sezioni rimangono VISUALIZZABILI anche per i menu pubblicati
        // ma i pulsanti di modifica vengono disabilitati
        
        // Memorizza riferimenti ai pulsanti invece di cercarli ogni volta
        if (btnPulsantiRicette == null) {
            btnPulsantiRicette = new ArrayList<>();
            // Trova i pulsanti solo la prima volta
            if (contenitoreMenu.getScene() != null) {
                contenitoreMenu.getScene().getRoot().lookupAll(".button").forEach(node -> {
                    if (node instanceof Button) {
                        Button btn = (Button) node;
                        String btnText = btn.getText();
                        if (btnText != null && (btnText.contains("Inserisci") || btnText.contains("Rimuovi") || 
                            btnText.contains("Sposta") || btnText.contains("Rinomina") || 
                            btnText.equals("Definisci sezioni") || btnText.equals("Salva titolo") || 
                            btnText.equals("Salva note") || btnText.equals("Elimina Menu"))) {
                            btnPulsantiRicette.add(btn);
                        }
                    }
                });
            }
        }
        
        // Abilita/disabilita i pulsanti memorizzati
        btnPulsantiRicette.forEach(btn -> btn.setDisable(!modificabile));
    }
    
    /**
     * Aggiorna il testo del pulsante "Cambia Stato" in base allo stato corrente del menu.
     */
    private void aggiornaPulsanteCambiaStato() {
        if (menuCorrente == null) {
            btnCambiaStato.setText("Cambia Stato");
            btnCambiaStato.setDisable(true);
        } else {
            btnCambiaStato.setDisable(false);
            String statoAttuale = menuCorrente.getStato();
            
            if (statoAttuale.equals("Pubblicato")) {
                btnCambiaStato.setText("Bozza");
            } else {
                btnCambiaStato.setText("Pubblica");
            }
        }
    }
    
    /**
     * Cambia lo stato del menu corrente tra Bozza e Pubblicato.
     */
    @FXML
    public void onCambiaStatoMenu() {
        if (menuCorrente == null) {
            AlertUtils.showWarning("Nessun menu selezionato", "Seleziona un menu per cambiare lo stato");
            return;
        }
        
        String statoAttuale = menuCorrente.getStato();
        String nuovoStato;
        String azione;
        
        if (statoAttuale.equals("Pubblicato")) {
            nuovoStato = "Bozza";
            azione = "riportare in bozza";
        } else {
            nuovoStato = "Pubblicato";
            azione = "pubblicare";
        }
        
        // Chiedi conferma
        Alert conferma = new Alert(Alert.AlertType.CONFIRMATION);
        conferma.setTitle("Conferma cambio stato");
        conferma.setHeaderText("Vuoi " + azione + " il menu?");
        conferma.setContentText("Menu: " + menuCorrente.getTitolo() + "\n" +
                                "Stato attuale: " + statoAttuale + "\n" +
                                "Nuovo stato: " + nuovoStato);
        
        ButtonType btnConferma = new ButtonType("Conferma", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnAnnulla = new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE);
        conferma.getButtonTypes().setAll(btnConferma, btnAnnulla);
        
        if (conferma.showAndWait().orElse(btnAnnulla) == btnConferma) {
            menuCorrente.setStato(nuovoStato);
            
            // SALVATAGGIO ESPLICITO: Forza il salvataggio dei dati dopo il cambio di stato
            menuService.salvaMenu();
            
            // Aggiorna UI
            aggiornaListaMenu();
            cmbMenuEsistenti.getSelectionModel().select(menuCorrente);
            boolean isModificabile = !nuovoStato.equals("Pubblicato");
            abilitaModificaMenu(isModificabile);
            
            // AGGIORNA PULSANTE: Cambia il testo dopo aver cambiato stato
            aggiornaPulsanteCambiaStato();
            
            AlertUtils.showInfo("Stato cambiato", 
                "Il menu è stato " + (nuovoStato.equals("Pubblicato") ? "pubblicato" : "riportato in bozza") + 
                " con successo");
        }
    }
    
    /**
     * Gestisce la definizione delle sezioni.
     * Riferimento: UC "Gestione dei Menù" - Passo 3
     */
    @FXML
    public void onDefinisciSezioni() {
        if (menuCorrente == null) return;
        
        String sezioniTesto = txtSezioni.getText().trim();
        if (sezioniTesto.isEmpty()) {
            AlertUtils.showWarning("Input richiesto", "Inserisci le sezioni del menu");
            return;
        }
        
        // Parsa le sezioni dal testo (separate da virgola)
        List<String> nomiSezioni = Arrays.stream(sezioniTesto.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toList());
            
        if (nomiSezioni.isEmpty()) {
            AlertUtils.showWarning("Input non valido", "Inserisci almeno una sezione valida");
            return;
        }
        
        // Cancella le sezioni esistenti e crea quelle nuove
        menuCorrente.getSezioni().clear();
        tabSezioni.getTabs().clear();
        
        for (String nome : nomiSezioni) {
            // Crea una nuova sezione del menu
            SezioneMenu sezione = menuService.creaSezioneMenu(nome);
            menuCorrente.getSezioni().add(sezione);
            
            // Crea la tab corrispondente
            Tab tab = creaTabPerSezione(sezione);
            tabSezioni.getTabs().add(tab);
        }
        
        // Salva immediatamente
        menuService.salvaMenu();
        
        // MESSAGGIO SPECIFICO: Più preciso di "Operazione completata"
        AlertUtils.showInfo("Sezioni definite", "Sezioni del menu create con successo");
    }
    
    /**
     * Gestisce l'inserimento di una ricetta nel menu.
     * Riferimento: UC "Gestione dei Menù" - Passo 4
     */
    @FXML
    public void onInserisciRicetta() {
        if (menuCorrente == null) return;
        
        RicettaViewModel ricettaViewModel = lstRicetteDisponibili.getSelectionModel().getSelectedItem();
        
        String sezioneNome = tabSelezionato != null ? tabSelezionato.getText() : null;
        
        if (ricettaViewModel != null && tabSelezionato != null) {
            // Trova la sezione corrispondente
            Optional<SezioneMenu> sezioneMenu = menuCorrente.getSezioni().stream()
                .filter(s -> s.getNome().equals(sezioneNome))
                .findFirst();
                
            if (sezioneMenu.isPresent()) {
                menuCorrente.inserisciRicetta(ricettaViewModel.getRicetta(), sezioneMenu.get());
                aggiornaContenutoSezione(sezioneMenu.get());
                
                // SALVATAGGIO ESPLICITO: Forza il salvataggio dopo aver inserito la ricetta
                menuService.salvaMenu();
            }
        } else {
            AlertUtils.showWarning("Selezione incompleta", "Seleziona una ricetta e una sezione");
        }
    }
    
    /**
     * Gestisce l'annotazione di informazioni.
     * Riferimento: UC "Gestione dei Menù" - Passo 6
     */
    @FXML
    public void onSalvaNote() {
        if (menuCorrente != null) {
            String nuoveNote = txtNote.getText();
            menuCorrente.setNote(nuoveNote);
            
            // SALVATAGGIO ESPLICITO: Forza il salvataggio dopo il cambio delle note
            menuService.salvaMenu();
            
            AlertUtils.showInfo("Salvato", "Note aggiornate con successo");
        }
    }
    
    /**
     * Gestisce l'eliminazione del menu.
     * Riferimento: UC "Gestione dei Menù" - Estensione 7a
     */
    @FXML
    public void onEliminaMenu() {
        if (menuCorrente != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Conferma eliminazione");
            alert.setHeaderText("Sei sicuro di voler eliminare questo menu?");
            alert.setContentText("Questa operazione non può essere annullata.");
            
            ButtonType buttonTypeOk = new ButtonType("Elimina", ButtonBar.ButtonData.OK_DONE);
            ButtonType buttonTypeCancel = new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE);
            
            alert.getButtonTypes().setAll(buttonTypeOk, buttonTypeCancel);
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == buttonTypeOk) {
                menuService.eliminaMenu(menuCorrente);
                menuCorrente = null;
                contenitoreMenu.setDisable(true);
                aggiornaListaMenu();
            }
        }
    }
    
    /**
     * Crea una nuova tab per una sezione del menu.
     */
    private Tab creaTabPerSezione(SezioneMenu sezione) {
        Tab tab = new Tab(sezione.getNome());
        tab.setClosable(false);
        
        // Lista delle ricette nella sezione
        ListView<RicettaInMenu> listView = new ListView<>();
        
        // Popola la lista con le ricette nella sezione
        listView.setItems(sezione.getRicette());
        
        // Personalizza la visualizzazione delle ricette
        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(RicettaInMenu item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty || item == null) {
                    setText(null);
                } else {
                    // Mostra il nome nel menu (personalizzato o originale)
                    setText(item.getNomeNelMenu());
                }
            }
        });
        
        tab.setContent(listView);
        return tab;
    }
    
    /**
     * Aggiorna il contenuto di una sezione del menu.
     */
    private void aggiornaContenutoSezione(SezioneMenu sezione) {
        // Trova la tab corrispondente
        Optional<Tab> tab = tabSezioni.getTabs().stream()
            .filter(t -> t.getText().equals(sezione.getNome()))
            .findFirst();
            
        if (tab.isPresent() && tab.get().getContent() instanceof ListView) {
            @SuppressWarnings("unchecked")
            ListView<RicettaInMenu> listView = (ListView<RicettaInMenu>) tab.get().getContent();
            
            // Aggiorna la lista delle ricette
            listView.setItems(sezione.getRicette());
        }
    }
    
    /**
     * Aggiorna l'UI con i dati di un menu esistente.
     */
    private void aggiornaUIConMenu(Menu menu) {
        txtTitolo.setText(menu.getTitolo());
        txtNote.setText(menu.getNote());
        
        // Aggiorna le sezioni
        tabSezioni.getTabs().clear();
        for (SezioneMenu sezione : menu.getSezioni()) {
            Tab tab = creaTabPerSezione(sezione);
            tabSezioni.getTabs().add(tab);
        }
        
        // Costruisci la stringa delle sezioni per il campo di testo
        String sezioniStr = menu.getSezioni().stream()
                .map(SezioneMenu::getNome)
                .collect(Collectors.joining(", "));
        txtSezioni.setText(sezioniStr);
        
        // Aggiorna gli stati dei checkbox in base alle caratteristiche del menu
        chkPiattiCaldi.setSelected(menu.hasCaratteristica("Piatti Caldi"));
        chkPiattiFreddi.setSelected(menu.hasCaratteristica("Piatti Freddi"));
        chkAdattoBuffet.setSelected(menu.hasCaratteristica("Adatto a Buffet"));
        chkFingerFood.setSelected(menu.hasCaratteristica("Finger Food"));
    }
    
    /**
     * Aggiorna la lista dei menu esistenti.
     */
    private void aggiornaListaMenu() {
        // Salva il menu attualmente selezionato
        Menu menuSelezionato = cmbMenuEsistenti.getSelectionModel().getSelectedItem();
        
        // Forza il refresh deselezionando temporaneamente
        cmbMenuEsistenti.getSelectionModel().clearSelection();
        
        // Aggiorna la lista
        cmbMenuEsistenti.setItems(menuService.getMenus());
        
        // Riseleziona il menu per aggiornare la visualizzazione
        if (menuSelezionato != null) {
            cmbMenuEsistenti.getSelectionModel().select(menuSelezionato);
        }
    }
    
    /**
     * Gestisce la consultazione dei dettagli di un evento.
     * Supporta il requirement di Chef Remy di consultare info sull'evento.
     */
    @FXML
    public void onConsultaDettagliEvento() {
        Evento evento = cmbEventoRiferimento.getValue();
        if (evento == null) {
            AlertUtils.showWarning("Selezione richiesta", "Seleziona un evento dalla lista");
            return;
        }
        
        // Crea dialog per mostrare dettagli evento
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("Dettagli Evento");
        dialog.setHeaderText("Informazioni sull'evento: " + evento.getNome());
        
        StringBuilder dettagli = new StringBuilder();
        dettagli.append("Nome: ").append(evento.getNome()).append("\n");
        dettagli.append("Data inizio: ").append(evento.getDataInizio()).append("\n");
        dettagli.append("Data fine: ").append(evento.getDataFine()).append("\n");
        dettagli.append("Luogo: ").append(evento.getLuogo()).append("\n");
        dettagli.append("Numero persone: ").append(evento.getNumeroDiPersone()).append("\n");
        dettagli.append("Stato: ").append(evento.getStato()).append("\n");
        
        if (evento.getNote() != null && !evento.getNote().trim().isEmpty()) {
            dettagli.append("Note: ").append(evento.getNote()).append("\n");
        }
        
        dialog.setContentText(dettagli.toString());
        dialog.showAndWait();
    }
    
    /**
     * Gestisce la rimozione di una ricetta dalla sezione corrente.
     * Supporta il requirement delle storie utente di eliminare ricette dalle sezioni.
     */
    @FXML
    public void onRimuoviRicetta() {
        if (menuCorrente == null || tabSelezionato == null) {
            AlertUtils.showWarning(MSG_OPERAZIONE_NON_VALIDA, "Seleziona prima un menu e una sezione");
            return;
        }
        
        // Trova la sezione corrispondente alla tab selezionata
        Optional<SezioneMenu> sezioneOpt = menuCorrente.getSezioni().stream()
            .filter(s -> s.getNome().equals(tabSelezionato.getText()))
            .findFirst();
            
        if (!sezioneOpt.isPresent()) {
            AlertUtils.showWarning("Errore", "Sezione non trovata");
            return;
        }
        
        SezioneMenu sezione = sezioneOpt.get();
        @SuppressWarnings("unchecked")
        ListView<RicettaInMenu> listView = (ListView<RicettaInMenu>) tabSelezionato.getContent();
        RicettaInMenu ricettaSelezionata = listView.getSelectionModel().getSelectedItem();
        
        if (ricettaSelezionata == null) {
            AlertUtils.showWarning(MSG_SELEZIONE_RICHIESTA, "Seleziona una ricetta da rimuovere");
            return;
        }
        
        // Rimuovi la ricetta dalla sezione
        sezione.getRicette().remove(ricettaSelezionata);
        menuService.rimuoviRicettaDaSezione(menuCorrente, ricettaSelezionata, sezione);
        
        // Aggiorna la visualizzazione
        aggiornaContenutoSezione(sezione);
        
        // MESSAGGIO SPECIFICO: Più informativo
        AlertUtils.showInfo("Ricetta rimossa", 
            "\"" + ricettaSelezionata.getNomeNelMenu() + "\" rimossa dalla sezione");
    }
    
    /**
     * Gestisce lo spostamento di una ricetta tra sezioni.
     * Supporta il requirement delle storie utente di riorganizzare i menu.
     */
    @FXML
    public void onSpostaRicetta() {
        if (menuCorrente == null || tabSelezionato == null) {
            AlertUtils.showWarning(MSG_OPERAZIONE_NON_VALIDA, "Seleziona prima un menu e una sezione");
            return;
        }
        
        // Trova la sezione sorgente
        Optional<SezioneMenu> sezioneSorgenteOpt = menuCorrente.getSezioni().stream()
            .filter(s -> s.getNome().equals(tabSelezionato.getText()))
            .findFirst();
            
        if (!sezioneSorgenteOpt.isPresent()) {
            AlertUtils.showWarning("Errore", "Sezione sorgente non trovata");
            return;
        }
        
        SezioneMenu sezioneSorgente = sezioneSorgenteOpt.get();
        @SuppressWarnings("unchecked")
        ListView<RicettaInMenu> listView = (ListView<RicettaInMenu>) tabSelezionato.getContent();
        RicettaInMenu ricettaSelezionata = listView.getSelectionModel().getSelectedItem();
        
        if (ricettaSelezionata == null) {
            AlertUtils.showWarning(MSG_SELEZIONE_RICHIESTA, "Seleziona una ricetta da spostare");
            return;
        }
        
        // Mostra dialog per scegliere sezione destinazione
        ChoiceDialog<String> dialog = new ChoiceDialog<>();
        dialog.setTitle("Sposta ricetta");
        dialog.setHeaderText("Scegli la sezione di destinazione");
        dialog.setContentText("Ricetta: " + ricettaSelezionata.getNomeNelMenu());
        
        List<String> nomiSezioni = menuCorrente.getSezioni().stream()
            .map(SezioneMenu::getNome)
            .filter(nome -> !nome.equals(sezioneSorgente.getNome()))
            .collect(java.util.stream.Collectors.toList());
            
        if (nomiSezioni.isEmpty()) {
            AlertUtils.showWarning("Operazione non possibile", "Non ci sono altre sezioni disponibili");
            return;
        }
        
        dialog.getItems().addAll(nomiSezioni);
        dialog.setSelectedItem(nomiSezioni.get(0));
        
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            // Trova sezione destinazione
            Optional<SezioneMenu> sezioneDestinazioneOpt = menuCorrente.getSezioni().stream()
                .filter(s -> s.getNome().equals(result.get()))
                .findFirst();
                
            if (sezioneDestinazioneOpt.isPresent()) {
                SezioneMenu sezioneDestinazione = sezioneDestinazioneOpt.get();
                menuService.spostaRicetta(menuCorrente, ricettaSelezionata, sezioneSorgente, sezioneDestinazione);
                
                // Aggiorna entrambe le sezioni
                aggiornaContenutoSezione(sezioneSorgente);
                aggiornaContenutoSezione(sezioneDestinazione);
                
                // MESSAGGIO SPECIFICO: Più informativo dello generico "Operazione completata"
                AlertUtils.showInfo("Ricetta spostata", 
                    "\"" + ricettaSelezionata.getNomeNelMenu() + "\" spostata in: " + sezioneDestinazione.getNome());
            }
        }
    }
    
    /**
     * Gestisce la ridenominazione di una ricetta nel menu.
     * Supporta il requirement di Chef Remy di dare nomi fantasiosi ai piatti.
     */
    @FXML
    public void onRinominaRicetta() {
        if (menuCorrente == null || tabSelezionato == null) {
            AlertUtils.showWarning(MSG_OPERAZIONE_NON_VALIDA, "Seleziona prima un menu e una sezione");
            return;
        }
        
        @SuppressWarnings("unchecked")
        ListView<RicettaInMenu> listView = (ListView<RicettaInMenu>) tabSelezionato.getContent();
        RicettaInMenu ricettaSelezionata = listView.getSelectionModel().getSelectedItem();
        
        if (ricettaSelezionata == null) {
            AlertUtils.showWarning(MSG_SELEZIONE_RICHIESTA, "Seleziona una ricetta da rinominare");
            return;
        }
        
        // Mostra dialog per inserire nuovo nome
        TextInputDialog dialog = new TextInputDialog(ricettaSelezionata.getNomeNelMenu());
        dialog.setTitle("Rinomina ricetta nel menu");
        dialog.setHeaderText("Inserisci il nuovo nome per la ricetta nel menu");
        dialog.setContentText("Nome ricetta originale: " + ricettaSelezionata.getRicettaOriginale().getNome() + "\n\nNuovo nome:");
        
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String nuovoNome = result.get().trim();
            menuService.rinominaRicettaNelMenu(menuCorrente, ricettaSelezionata, nuovoNome);
            
            // Aggiorna IMMEDIATAMENTE la visualizzazione
            Optional<SezioneMenu> sezioneOpt = menuCorrente.getSezioni().stream()
                .filter(s -> s.getNome().equals(tabSelezionato.getText()))
                .findFirst();
                
            if (sezioneOpt.isPresent()) {
                aggiornaContenutoSezione(sezioneOpt.get());
                // Forza il refresh della ListView
                listView.refresh();
            }
            
            // SALVATAGGIO ESPLICITO: Forza il salvataggio dopo aver cambiato stato
            menuService.salvaMenu();
            
            // MESSAGGIO SPECIFICO: Più informativo
            AlertUtils.showInfo("Ricetta rinominata", 
                "Rinominata in: \"" + nuovoNome + "\"");
        }
    }
    
    /**
     * Esporta il menu corrente in formato Excel.
     */
    @FXML
    public void onEsportaMenuExcel() {
        if (menuCorrente == null) {
            AlertUtils.showWarning("Nessun menu selezionato", "Seleziona un menu da esportare");
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salva Menu in Excel");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("File Excel (*.xlsx)", "*.xlsx")
        );
        
        // Nome file suggerito
        String nomeFileSuggerito = "Menu_" + menuCorrente.getTitolo().replaceAll("[^a-zA-Z0-9]", "_") + ".xlsx";
        fileChooser.setInitialFileName(nomeFileSuggerito);
        
        // Mostra dialog per salvare
        java.io.File file = fileChooser.showSaveDialog(cmbMenuEsistenti.getScene().getWindow());
        
        if (file != null) {
            try {
                ExcelExportUtils.esportaMenu(menuCorrente, file.getAbsolutePath());
                AlertUtils.showInfo("Esportazione completata", 
                    "Menu esportato con successo in:\n" + file.getAbsolutePath());
            } catch (Exception e) {
                AlertUtils.showError("Errore durante l'esportazione", 
                    "Impossibile esportare il menu: " + e.getMessage());
            }
        }
    }
    
    /**
     * Gestisce l'inserimento del titolo con validazione di unicità.
     * Riferimento: UC "Gestione dei Menù" - Passo 2
     */
    @FXML
    public void onSalvaTitolo() {
        if (menuCorrente == null) {
            AlertUtils.showWarning(MSG_SELEZIONA_MENU, "Crea o seleziona prima un menu");
            return;
        }
        
        String nuovoTitolo = txtTitolo.getText().trim();
        if (nuovoTitolo.isEmpty()) {
            AlertUtils.showWarning("Titolo richiesto", "Inserisci un titolo per il menu");
            txtTitolo.requestFocus();
            return;
        }
        
        // Verifica se il titolo è cambiato
        if (nuovoTitolo.equals(menuCorrente.getTitolo())) {
            AlertUtils.showInfo("Nessuna modifica", "Il titolo è già quello attuale");
            return;
        }
        
        // Verifica unicità del titolo
        if (menuService.isTitoloGiaEsistente(nuovoTitolo, menuCorrente.getId())) {
            // STANDARDIZZATO: Stesso messaggio di onNuovoMenu
            AlertUtils.showError(MSG_TITOLO_DUPLICATO, 
                "Esiste già un menu con il titolo: \"" + nuovoTitolo + "\"\n" +
                "Scegli un titolo diverso.");
            txtTitolo.requestFocus();
            txtTitolo.selectAll();
            return;
        }
        
        // OTTIMIZZATO: Singola operazione atomica invece di multiple chiamate
        boolean aggiornato = menuService.aggiornaTitoloMenu(menuCorrente, nuovoTitolo);
        if (aggiornato) {
            // Aggiorna immediatamente la ComboBox dei menu esistenti
            aggiornaListaMenu();
            
            // Ri-seleziona il menu corrente nella ComboBox per mostrare il nuovo titolo
            cmbMenuEsistenti.getSelectionModel().select(menuCorrente);
            
            AlertUtils.showInfo("Titolo aggiornato", "Nuovo titolo salvato con successo");
        } else {
            AlertUtils.showError("Errore", "Impossibile aggiornare il titolo");
        }
    }
}