package service;

import com.fasterxml.jackson.core.type.TypeReference;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import domain.utenti.Chef;
import domain.utenti.Cuoco;
import service.persistence.JsonLoader;
import java.util.List;
import java.util.Map;

/**
 * Rappresenta il servizio per la gestione degli utenti (Chef e Cuochi) nel sistema di catering.
 * Carica e salva i dati da/su file JSON per coerenza con la gestione dati dell'applicazione.
 */
public class UtenteService {
    private final ObservableList<Chef> chefs = FXCollections.observableArrayList();
    private final ObservableList<Cuoco> cuochi = FXCollections.observableArrayList();
    private int nextUtenteId = 1;
    
    // Percorso del file JSON degli utenti
    private static final String JSON_PATH = "src/main/resources/data/utenti.json";
    
    /**
     * Costruttore che inizializza il servizio caricando gli utenti dal file JSON.
     */
    public UtenteService() {
        caricaUtentiDaJson();
    }
    
    /**
     * Carica gli utenti dal file JSON.
     */
    private void caricaUtentiDaJson() {
        try {
            // Carica la struttura dal file JSON
            Map<String, Object> utentiData = JsonLoader.loadFromResourcesGeneric("data/utenti.json", new TypeReference<Map<String, Object>>() {});
            
            if (utentiData != null) {
                // Carica i chef
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> chefsData = (List<Map<String, Object>>) utentiData.get("chefs");
                if (chefsData != null) {
                    for (Map<String, Object> chefData : chefsData) {
                        Chef chef = new Chef(
                            (Integer) chefData.get("id"),
                            (String) chefData.get("nome"),
                            (String) chefData.get("cognome")
                        );
                        chefs.add(chef);
                        updateNextUtenteId(chef.getId());
                    }
                }
                
                // Carica i cuochi
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> cuochiData = (List<Map<String, Object>>) utentiData.get("cuochi");
                if (cuochiData != null) {
                    for (Map<String, Object> cuocoData : cuochiData) {
                        Cuoco cuoco = new Cuoco(
                            (Integer) cuocoData.get("id"),
                            (String) cuocoData.get("nome"),
                            (String) cuocoData.get("cognome")
                        );
                        cuochi.add(cuoco);
                        updateNextUtenteId(cuoco.getId());
                    }
                }
                
    
            }
        } catch (Exception e) {
            System.err.println("Errore nel caricamento degli utenti: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Salva gli utenti nel file JSON.
     */
    private void salvaUtentiInJson() {
        try {
            Map<String, Object> utentiData = Map.of(
                "chefs", chefs,
                "cuochi", cuochi
            );
            JsonLoader.saveToFileGeneric(JSON_PATH, utentiData);
        } catch (Exception e) {
            System.err.println("Errore nel salvataggio degli utenti: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Aggiorna il prossimo ID utente.
     */
    private void updateNextUtenteId(int id) {
        if (id >= nextUtenteId) {
            nextUtenteId = id + 1;
        }
    }
    
    public Chef creaChef(String nome, String cognome) {
        Chef nuovoChef = new Chef(nextUtenteId++, nome, cognome);
        chefs.add(nuovoChef);
        salvaUtentiInJson();
        return nuovoChef;
    }
    
    public Cuoco creaCuoco(String nome, String cognome) {
        Cuoco nuovoCuoco = new Cuoco(nextUtenteId++, nome, cognome);
        cuochi.add(nuovoCuoco);
        salvaUtentiInJson();
        return nuovoCuoco;
    }
    
    public ObservableList<Chef> getChefs() {
        return chefs;
    }
    
    public ObservableList<Cuoco> getCuochi() {
        return cuochi;
    }
    
    /**
     * Crea utenti di esempio solo se richiesto esplicitamente.
     * NON viene più chiamato automaticamente.
     */
    public void creaUtentiDiEsempio() {
        // Crea chef e cuochi di esempio solo se non esistono già
        creaChef("Mario", "Rossi");
        creaChef("Giuseppe", "Bianchi");
        creaCuoco("Luigi", "Verdi");
        creaCuoco("Anna", "Bianchi");
        creaCuoco("Marco", "Neri");
        creaCuoco("Francesca", "Gialli");
    }
}