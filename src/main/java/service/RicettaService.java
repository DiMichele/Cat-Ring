package service;

import com.fasterxml.jackson.core.type.TypeReference;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import domain.ricette.Ricetta;
import service.persistence.JsonLoader;
import ui.viewmodels.RicettaViewModel;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Rappresenta il servizio per la gestione delle ricette nel sistema di catering.
 * Carica tutte le ricette SOLO dal file JSON ricette.json.
 * Fornisce ricette disponibili come supporto ai due UC principali:
 * - Gestione Menu
 * - Gestione Compiti Cucina
 */
public class RicettaService {
    private final ObservableList<Ricetta> ricette = FXCollections.observableArrayList();
    private final ObservableList<RicettaViewModel> ricetteViewModel = FXCollections.observableArrayList();
    private int nextRicettaId = 1;
    
    // Cache per i ViewModel
    private final Map<Integer, RicettaViewModel> viewModelCache = new ConcurrentHashMap<>();
    
    // Percorso del file JSON delle ricette
    private static final String JSON_PATH = "src/main/resources/data/ricette.json";
    
    /**
     * Costruttore che carica le ricette dal file JSON.
     */
    public RicettaService() {
        caricaRicetteDaJson();
    }
    
    /**
     * Carica le ricette dal file JSON.
     */
    private void caricaRicetteDaJson() {
        try {
            List<Ricetta> ricetteList = JsonLoader.loadFromResources("data/ricette.json", new TypeReference<List<Ricetta>>() {});
            
            if (ricetteList != null && !ricetteList.isEmpty()) {
                ricette.addAll(ricetteList);
                
                // Aggiorna nextRicettaId
                for (Ricetta ricetta : ricetteList) {
                    if (ricetta.getId() >= nextRicettaId) {
                        nextRicettaId = ricetta.getId() + 1;
                    }
                    
                    // Crea ViewModel per ogni ricetta
                    RicettaViewModel viewModel = new RicettaViewModel(ricetta);
                    ricetteViewModel.add(viewModel);
                    viewModelCache.put(ricetta.getId(), viewModel);
                }
                
    
            }
        } catch (Exception e) {
            System.err.println("Errore nel caricamento delle ricette: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Salva le ricette nel file JSON.
     */
    private void salvaRicetteInJson() {
        try {
            JsonLoader.saveToFile(JSON_PATH, ricette);
        } catch (Exception e) {
            System.err.println("Errore nel salvataggio delle ricette: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Ottiene la lista osservabile delle ricette disponibili come ViewModel.
     * UTILIZZO: Per selezione ricette nei menu e compiti
     */
    public ObservableList<RicettaViewModel> getRicetteDisponibiliViewModel() {
        return ricetteViewModel.filtered(vm -> "Pubblicata".equals(vm.getRicetta().getStato()));
    }
    
    /**
     * Trova una ricetta per ID.
     * UTILIZZO: Per recuperare ricette selezionate nei UC
     */
    public Ricetta findById(int id) {
        return ricette.stream()
                .filter(r -> r.getId() == id)
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Ottiene tutte le ricette disponibili.
     * UTILIZZO: Per supporto ai servizi Menu e Compiti
     */
    public List<Ricetta> getRicetteDisponibili() {
        return ricette.stream()
                .filter(r -> "Pubblicata".equals(r.getStato()))
                .collect(Collectors.toList());
    }
    
    /**
     * Ottiene tutte le ricette caricate.
     */
    public ObservableList<Ricetta> getRicette() {
        return ricette;
    }
    
    /**
     * Crea una nuova ricetta e la salva nel file JSON.
     */
    public Ricetta creaRicetta(String nome, String descrizione, int tempoPreparazione) {
        Ricetta nuovaRicetta = new Ricetta(nextRicettaId++, nome);
        nuovaRicetta.setDescrizione(descrizione);
        nuovaRicetta.setTempoPreparazione(tempoPreparazione);
        nuovaRicetta.setStato("Pubblicata");
        
        ricette.add(nuovaRicetta);
        
        // Crea e aggiungi ViewModel
        RicettaViewModel viewModel = new RicettaViewModel(nuovaRicetta);
        ricetteViewModel.add(viewModel);
        viewModelCache.put(nuovaRicetta.getId(), viewModel);
        
        salvaRicetteInJson();
        return nuovaRicetta;
    }
}