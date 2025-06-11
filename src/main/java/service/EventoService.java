package service;

import com.fasterxml.jackson.core.type.TypeReference;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import domain.eventi.Evento;
import service.persistence.JsonLoader;
import java.util.List;

/**
 * Rappresenta il servizio per la gestione degli eventi nel sistema di catering.
 * Carica tutti i dati SOLO dal file JSON eventi.json.
 */
public class EventoService {
    private final ObservableList<Evento> eventi = FXCollections.observableArrayList();
    private int nextEventoId = 1;
    
    // Percorso del file JSON degli eventi nel classpath
    private static final String JSON_PATH = "src/main/resources/data/eventi.json";
    
    public EventoService() {
        caricaEventiDaJson();
    }
    
    /**
     * Carica gli eventi dal file JSON.
     */
    private void caricaEventiDaJson() {
        try {
            List<Evento> eventiList = JsonLoader.loadFromResources("data/eventi.json", new TypeReference<List<Evento>>() {});
            
            if (eventiList != null && !eventiList.isEmpty()) {
                eventi.addAll(eventiList);
                
                // Aggiorna nextEventoId
                for (Evento evento : eventiList) {
                    if (evento.getId() >= nextEventoId) {
                        nextEventoId = evento.getId() + 1;
                    }
                }
                
    
            }
        } catch (Exception e) {
            System.err.println("Errore nel caricamento degli eventi: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Salva gli eventi nel file JSON.
     */
    private void salvaEventiInJson() {
        try {
            JsonLoader.saveToFile(JSON_PATH, eventi);
        } catch (Exception e) {
            System.err.println("Errore nel salvataggio degli eventi: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public ObservableList<Evento> getEventi() {
        return eventi;
    }
    
    /**
     * Crea un nuovo evento e lo salva nel file JSON.
     */
    public Evento creaEvento(String nome, java.time.LocalDateTime dataInizio, java.time.LocalDateTime dataFine, 
                           String luogo, int numeroDiPersone) {
        Evento nuovoEvento = new Evento(nextEventoId++, nome, dataInizio, dataFine, luogo, numeroDiPersone);
        eventi.add(nuovoEvento);
        salvaEventiInJson();
        return nuovoEvento;
    }
}