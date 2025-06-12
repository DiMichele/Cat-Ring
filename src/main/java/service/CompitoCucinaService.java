package service;

import com.fasterxml.jackson.core.type.TypeReference;
import domain.compiti.Compito;
import domain.compiti.Turno;
import domain.eventi.Evento;
import domain.ricette.Ricetta;
import domain.utenti.Cuoco;
import service.persistence.JsonLoader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

/**
 * Rappresenta il servizio per la gestione dei compiti di cucina nel sistema di catering.
 * Implementa le operazioni del caso d'uso "Gestione dei Compiti della cucina".
 * Carica e salva i dati da/su file JSON per coerenza con la gestione dati dell'applicazione.
 */
public class CompitoCucinaService {
    private final ObservableList<Compito> compiti = FXCollections.observableArrayList();
    private final ObservableList<Turno> turni = FXCollections.observableArrayList();
    private int nextCompitoId = 1;
    private int nextTurnoId = 1;
    private UtenteService utenteService;
    
    // Percorso del file JSON dei compiti
    private String jsonPath = "src/main/resources/data/compiti.json";
    
    /**
     * Costruttore predefinito che carica i compiti dal file JSON.
     */
    public CompitoCucinaService() {
        caricaCompitiDaJson();
        caricaTurniDaJson();
    }
    
    /**
     * Costruttore per i test che permette di specificare un percorso personalizzato.
     */
    public CompitoCucinaService(String jsonPath) {
        this.jsonPath = jsonPath;
        caricaCompitiDaJson();
        caricaTurniDaJson();
    }
    
    public CompitoCucinaService(UtenteService utenteService) {
        this.utenteService = utenteService;
        caricaCompitiDaJson();
        caricaTurniDaJson();
    }
    
    public void setUtenteService(UtenteService utenteService) {
        this.utenteService = utenteService;
    }
    
    /**
     * Carica i compiti dal file JSON.
     */
    private void caricaCompitiDaJson() {
        try {
            List<Compito> compitiList;
            if (jsonPath.startsWith("src/test/")) {
                // Per i test, usa loadFromFile
                compitiList = JsonLoader.loadFromFile(jsonPath, new TypeReference<List<Compito>>() {});
            } else {
                // Per l'uso normale, usa loadFromResources
                compitiList = JsonLoader.loadFromResources("data/compiti.json", new TypeReference<List<Compito>>() {});
            }
            
            if (compitiList != null && !compitiList.isEmpty()) {
                compiti.addAll(compitiList);
                
                // Aggiorna nextCompitoId
                for (Compito compito : compitiList) {
                    if (compito.getId() >= nextCompitoId) {
                        nextCompitoId = compito.getId() + 1;
                    }
                }
                

                
                // I riferimenti verranno risolti successivamente quando i servizi sono disponibili
            }
        } catch (Exception e) {
            System.err.println("Errore nel caricamento dei compiti: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Carica i turni dal file JSON.
     */
    private void caricaTurniDaJson() {
        try {
            List<Turno> turniList = JsonLoader.loadFromResources("data/turni.json", new TypeReference<List<Turno>>() {});
                
            if (turniList != null && !turniList.isEmpty()) {
                turni.addAll(turniList);
                
                // Aggiorna nextTurnoId
                for (Turno turno : turniList) {
                    if (turno.getId() >= nextTurnoId) {
                        nextTurnoId = turno.getId() + 1;
                    }
                }
                

            }
        } catch (Exception e) {
            System.err.println("Errore nel caricamento dei turni: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Restituisce true se non ci sono compiti caricati (file vuoto o inesistente)
     */
    public boolean isCompitiVuoti() {
        return compiti.isEmpty();
    }
    
    /**
     * Pulisce tutti i compiti e i turni (utilizzato per test puliti)
     */
    public void pulisciDati() {
        compiti.clear();
        turni.clear();
        nextCompitoId = 1;
        nextTurnoId = 1;
    }
    
    /**
     * Salva i compiti nel file JSON.
     */
    private void salvaCompitiInJson() {
        try {
            JsonLoader.saveToFile(jsonPath, new ArrayList<>(compiti));
        } catch (Exception e) {
            System.err.println("Errore nel salvataggio dei compiti: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Salva i turni nel file JSON.
     */
    private void salvaTurniInJson() {
        try {
            JsonLoader.saveToFile("src/main/resources/data/turni.json", turni);
        } catch (Exception e) {
            System.err.println("Errore nel salvataggio dei turni: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Crea un riepilogo dei compiti per un evento.
     * Riferimento: UC "Gestione dei Compiti della cucina" - Passo 1
     */
    public ObservableList<Compito> creaRiepilogoCompiti(Evento evento) {
        if (evento == null) {
            return FXCollections.observableArrayList(); // Lista vuota se l'evento è nullo
        }
        
        // Ottieni le ricette associate all'evento tramite il menu diretto
        List<Ricetta> ricetteEvento = new ArrayList<>();
        if (evento.getMenu() != null) {
            evento.getMenu().getSezioni().stream()
                .flatMap(sezione -> sezione.getRicette().stream())
                .map(ricettaInMenu -> ricettaInMenu.getRicettaOriginale())
                .forEach(ricetteEvento::add);
        }
        
        if (ricetteEvento.isEmpty()) {
            // Se non ci sono ricette, per scopi dimostrativi, includiamo tutti i compiti
            // Questo permette all'interfaccia di funzionare anche con dati incompleti
            return FXCollections.observableArrayList(compiti);
        }
        

        
        // Filtra i compiti per ricette dell'evento
        List<Compito> compitiEvento = compiti.stream()
            .filter(compito -> 
                compito.getRicetta() != null && 
                ricetteEvento.contains(compito.getRicetta()))
            .collect(Collectors.toList());

        
        return FXCollections.observableArrayList(compitiEvento);
    }
    
    /**
     * Restituisce i cuochi disponibili per un turno.
     * Un cuoco è disponibile se ha ancora tempo libero nel turno (non completamente occupato).
     * Riferimento: UC "Gestione dei Compiti della cucina" - Passo 2
     */
    public List<Cuoco> getCuochiDisponibili(Turno turno) {
        if (turno == null) {
            return new ArrayList<>();
        }
        
        // Calcola la durata del turno in minuti
        long durataTurnoMinuti = java.time.Duration.between(
            turno.getOraInizio(), turno.getOraFine()).toMinutes();
        
        // Ottieni tutti i cuochi dal sistema
        List<Cuoco> tuttiCuochi = new ArrayList<>();
        if (utenteService != null) {
            tuttiCuochi.addAll(utenteService.getCuochi());
        } else {
            // Fallback: usa i cuochi che abbiamo visto nei compiti esistenti
            tuttiCuochi = compiti.stream()
                .map(Compito::getCuocoAssegnato)
                .filter(cuoco -> cuoco != null)
                .distinct()
                .collect(Collectors.toList());
        }
        
        // Filtra i cuochi che hanno ancora tempo disponibile nel turno
        return tuttiCuochi.stream()
            .filter(cuoco -> {
                int tempoAssegnato = getTempoAssegnatoCuoco(cuoco, turno);
                return tempoAssegnato < durataTurnoMinuti; // Ha ancora tempo libero
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Assegna un compito a un cuoco.
     * Riferimento: UC "Gestione dei Compiti della cucina" - Passo 3
     */
    public Compito assegnaCompito(Cuoco cuoco, String turno, 
                                Ricetta ricetta, int tempoStimato, double quantita, Evento evento) {
        Compito nuovoCompito = new Compito(nextCompitoId++, ricetta, cuoco, turno, tempoStimato, quantita, evento);
        compiti.add(nuovoCompito);
        salvaCompitiInJson();
        return nuovoCompito;
    }
    
    // Metodo di compatibilità senza evento (deprecated)
    @Deprecated
    public Compito assegnaCompito(Cuoco cuoco, String turno, 
                                Ricetta ricetta, int tempoStimato, double quantita) {
        Compito nuovoCompito = new Compito(nextCompitoId++, ricetta, cuoco, turno, tempoStimato, quantita);
        compiti.add(nuovoCompito);
        salvaCompitiInJson();
        return nuovoCompito;
    }
    
    /**
     * Ordina i compiti per importanza.
     * Riferimento: UC "Gestione dei Compiti della cucina" - Passo 4
     */
    public ObservableList<Compito> ordinaCompitiPerImportanza(List<Compito> compitiDaOrdinare) {
        // Ordina per importanza (valori più alti = maggiore importanza)
        ObservableList<Compito> compitiOrdinati = FXCollections.observableArrayList(compitiDaOrdinare);
        compitiOrdinati.sort((c1, c2) -> {
            // Prima per importanza (decrescente: 5, 3, 1)
            int compareImportanza = Integer.compare(c2.getImportanza(), c1.getImportanza());
            if (compareImportanza != 0) {
                return compareImportanza;
            }
            // Se l'importanza è uguale, ordina per tempo stimato (decrescente: compiti lunghi prima)
            return Integer.compare(c2.getTempoStimato(), c1.getTempoStimato());
        });
        return compitiOrdinati;
    }
    
    /**
     * Verifica se un turno è pieno.
     * Riferimento: UC "Gestione dei Compiti della cucina" - Estensione 5a
     */
    public boolean isTurnoPieno(Turno turno) {
        if (turno == null) {
            return false;
        }
        
        String turnoStr = turno.getData() + " " + turno.getOraInizio() + "-" + turno.getOraFine();
        int tempoTotale = compiti.stream()
            .filter(compito -> turnoStr.equals(compito.getTurno()))
            .mapToInt(Compito::getTempoStimato)
            .sum();
        
        // Calcola tempo disponibile nel turno in minuti
        long minutiDisponibili = java.time.Duration.between(
            turno.getOraInizio(), turno.getOraFine()).toMinutes();
            
        // Se il tempo totale supera l'80% del tempo disponibile, consideriamo il turno come pieno
        return tempoTotale >= (minutiDisponibili * 0.8);
    }
    
    /**
     * Monitora l'avanzamento dei compiti.
     * Riferimento: UC "Gestione dei Compiti della cucina" - Passo 6
     */
    public ObservableList<Compito> monitoraAvanzamento() {
        return compiti;
    }
    
    /**
     * Controlla lo stato dei compiti a fine turno.
     * Riferimento: UC "Gestione dei Compiti della cucina" - Passo 7
     */
    public List<Compito> controllaStatoTurno(Turno turno) {
        String turnoStr = turno.getData() + " " + turno.getOraInizio() + "-" + turno.getOraFine();
        return compiti.stream()
            .filter(compito -> turnoStr.equals(compito.getTurno()))
            .collect(Collectors.toList());
    }
    
    /**
     * Crea un turno e lo salva nel file JSON.
     */
    public Turno creaTurno(java.time.LocalDate data, java.time.LocalTime oraInizio, 
                        java.time.LocalTime oraFine, String luogo, String tipo) {
        Turno nuovoTurno = new Turno(nextTurnoId++, data, oraInizio, oraFine, luogo, tipo);
        turni.add(nuovoTurno);
        salvaTurniInJson();
        return nuovoTurno;
    }
    
    public ObservableList<Compito> getCompiti() {
        return compiti;
    }
    
    public ObservableList<Turno> getTurni() {
        return turni;
    }
    
    /**
     * Calcola quanto tempo è già stato assegnato a un cuoco per un turno specifico in un evento specifico
     */
    public int getTempoAssegnatoCuoco(Cuoco cuoco, Turno turno, Evento evento) {
        String turnoStr = turno.getData() + " " + turno.getOraInizio() + "-" + turno.getOraFine();
        
        return compiti.stream()
            .filter(compito -> 
                compito.getCuocoAssegnato() != null && 
                compito.getCuocoAssegnato().equals(cuoco) && 
                turnoStr.equals(compito.getTurno()) &&
                (evento == null || eventoContienCompito(evento, compito))) // Se evento è null, considera tutti i compiti
            .mapToInt(Compito::getTempoStimato)
            .sum();
    }
    
    /**
     * Verifica se un compito appartiene a un evento specifico
     */
    private boolean eventoContienCompito(Evento evento, Compito compito) {
        if (evento == null || compito == null) {
            return false;
        }
        
        // Verifica diretta usando il campo evento del compito
        return evento.equals(compito.getEvento());
    }
    
    /**
     * Restituisce i compiti filtrati per un evento specifico
     */
    public List<Compito> getCompitiPerEvento(Evento evento) {
        if (evento == null) {
            return new ArrayList<>(compiti); // Se nessun evento, restituisce tutti i compiti
        }
        
        return compiti.stream()
            .filter(compito -> eventoContienCompito(evento, compito))
            .collect(Collectors.toList());
    }
    
    /**
     * Restituisce i cuochi disponibili per un compito specifico (turno + tempo necessario).
     * Un cuoco è disponibile se ha abbastanza tempo libero nel turno per il compito richiesto.
     * La disponibilità è calcolata GLOBALMENTE attraverso tutti gli eventi.
     */
    public List<Cuoco> getCuochiDisponibiliPerCompito(Turno turno, int tempoNecessario, Evento evento) {
        if (turno == null || tempoNecessario <= 0) {
            return new ArrayList<>();
        }
        
        // Calcola la durata del turno in minuti
        long durataTurnoMinuti = java.time.Duration.between(
            turno.getOraInizio(), turno.getOraFine()).toMinutes();
        
        // Verifica che il tempo necessario non superi la durata del turno
        if (tempoNecessario > durataTurnoMinuti) {
            return new ArrayList<>(); // Nessun cuoco può fare un compito più lungo del turno
        }
        
        // Ottieni tutti i cuochi dal sistema
        List<Cuoco> tuttiCuochi = new ArrayList<>();
        if (utenteService != null) {
            tuttiCuochi.addAll(utenteService.getCuochi());
        } else {
            // Fallback: usa i cuochi che abbiamo visto nei compiti esistenti
            tuttiCuochi = compiti.stream()
                .map(Compito::getCuocoAssegnato)
                .filter(cuoco -> cuoco != null)
                .distinct()
                .collect(Collectors.toList());
        }
        
        // Filtra i cuochi che hanno abbastanza tempo disponibile nel turno GLOBALMENTE (tutti gli eventi)
        return tuttiCuochi.stream()
            .filter(cuoco -> {
                // Usa il metodo deprecated che calcola su tutti gli eventi
                int tempoGiaAssegnato = getTempoAssegnatoCuoco(cuoco, turno);
                int tempoRimanente = (int) (durataTurnoMinuti - tempoGiaAssegnato);
                return tempoRimanente >= tempoNecessario; // Ha abbastanza tempo per questo compito
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Versione di compatibilità che usa tutti gli eventi
     * @deprecated Usa getCuochiDisponibiliPerCompito(Turno, int, Evento) per gestione specifica per evento
     */
    @Deprecated
    public List<Cuoco> getCuochiDisponibiliPerCompito(Turno turno, int tempoNecessario) {
        return getCuochiDisponibiliPerCompito(turno, tempoNecessario, null);
    }
    
    /**
     * Calcola quanto tempo è già stato assegnato a un cuoco per un turno specifico (tutti gli eventi)
     * @deprecated Usa getTempoAssegnatoCuoco(Cuoco, Turno, Evento) per gestione specifica per evento
     */
    @Deprecated
    public int getTempoAssegnatoCuoco(Cuoco cuoco, Turno turno) {
        String turnoStr = turno.getData() + " " + turno.getOraInizio() + "-" + turno.getOraFine();
        
        return compiti.stream()
            .filter(compito -> 
                compito.getCuocoAssegnato() != null && 
                compito.getCuocoAssegnato().equals(cuoco) && 
                turnoStr.equals(compito.getTurno()))
            .mapToInt(Compito::getTempoStimato)
            .sum();
    }
    
    /**
     * Risolve i riferimenti nei compiti utilizzando i servizi disponibili.
     * Deve essere chiamato dopo aver inizializzato tutti i servizi.
     */
    public void risolviRiferimentiCompiti(RicettaService ricettaService, EventoService eventoService, UtenteService utenteService) {
        for (Compito compito : compiti) {
            // Risolvi ricetta
            if (compito.getRicettaId() != null && ricettaService != null) {
                Ricetta ricetta = ricettaService.findById(compito.getRicettaId());
                if (ricetta != null) {
                    compito.risolviRicetta(ricetta);
                }
            }
            
            // Risolvi cuoco
            if (compito.getCuocoId() != null && utenteService != null) {
                Cuoco cuoco = utenteService.getCuochi().stream()
                    .filter(c -> c.getId() == compito.getCuocoId())
                    .findFirst()
                    .orElse(null);
                if (cuoco != null) {
                    compito.risolviCuoco(cuoco);
                }
            }
            
            // Risolvi evento
            if (compito.getEventoId() != null && eventoService != null) {
                Evento evento = eventoService.getEventi().stream()
                    .filter(e -> e.getId() == compito.getEventoId())
                    .findFirst()
                    .orElse(null);
                if (evento != null) {
                    compito.risolviEvento(evento);
                }
            }
            
            // Risolvi turno se disponibile
            if (compito.getTurnoId() != null) {
                Turno turno = turni.stream()
                    .filter(t -> t.getId() == compito.getTurnoId())
                    .findFirst()
                    .orElse(null);
                if (turno != null) {
                    String turnoStr = turno.getData() + " " + turno.getOraInizio() + "-" + turno.getOraFine();
                    compito.setTurno(turnoStr);
                }
            }
        }
        

    }
}