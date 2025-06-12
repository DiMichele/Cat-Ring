package service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import domain.menu.Menu;
import domain.menu.SezioneMenu;
import domain.ricette.Ricetta;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import service.persistence.JsonLoader;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Rappresenta il servizio per la gestione dei menu nel sistema di catering.
 * Gestisce la creazione, modifica, pubblicazione e persistenza dei menu.
 * Implementa le operazioni del caso d'uso "Gestione dei Menu".
 */
public class MenuService {
    @JsonIgnore
    private final ObservableList<Menu> menus = FXCollections.observableArrayList();
    private int nextMenuId = 1;
    private final AtomicInteger nextSezioneId = new AtomicInteger(1);
    
    // Percorso del file JSON dei menu
    private String jsonPath = "src/main/resources/data/menu.json";
    
    /**
     * Costruttore predefinito che inizializza il servizio caricando i menu dal file JSON.
     */
    public MenuService() {
        caricaMenuDaJson();
    }
    
    /**
     * Costruttore per i test che permette di specificare un percorso personalizzato.
     */
    public MenuService(String jsonPath) {
        this.jsonPath = jsonPath;
        caricaMenuDaJson();
    }
    
    /**
     * Carica i menu dal file JSON.
     */
    private void caricaMenuDaJson() {
        try {
            // Carica i menu dal file JSON
            List<Menu> menuList;
            if (jsonPath.startsWith("src/test/")) {
                // Per i test, usa loadFromFile
                menuList = JsonLoader.loadFromFile(jsonPath, new TypeReference<List<Menu>>() {});
            } else {
                // Per l'uso normale, usa loadFromResources
                menuList = JsonLoader.loadFromResources("data/menu.json", new TypeReference<List<Menu>>() {});
            }
            
            // Aggiunge i menu alla lista osservabile
            if (menuList != null && !menuList.isEmpty()) {
                // Pulisce e valida i menu prima di aggiungerli
                for (Menu menu : menuList) {
                    // Corregge menu con titoli null o vuoti
                    if (menu.getTitolo() == null || menu.getTitolo().trim().isEmpty()) {
                        menu.setTitolo("Menu senza titolo #" + menu.getId());
                    }
                    
                    menus.add(menu);
                    
                    // Aggiorna nextMenuId e nextSezioneId
                    if (menu.getId() >= nextMenuId) {
                        nextMenuId = menu.getId() + 1;
                    }
                    
                    for (SezioneMenu sezione : menu.getSezioni()) {
                        if (sezione.getId() >= nextSezioneId.get()) {
                            nextSezioneId.set(sezione.getId() + 1);
                        }
                    }
                }
                
                // Salva i menu corretti
                salvaMenuInJson();
            }
        } catch (Exception e) {
            System.err.println("Errore nel caricamento dei menu: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Salva i menu nel file JSON.
     */
    private void salvaMenuInJson() {
        try {
            JsonLoader.saveToFile(jsonPath, new ArrayList<>(menus));
        } catch (Exception e) {
            System.err.println("Errore nel salvataggio dei menu: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Salva esplicitamente i dati nel file JSON.
     * Metodo pubblico per permettere ai controller di forzare il salvataggio.
     */
    public void salvaMenu() {
        salvaMenuInJson();
    }

    /**
     * Metodo per i test per cambiare il percorso del file JSON.
     */
    public void setJsonPath(String path) {
        this.jsonPath = path;
    }
    
    /**
     * Crea un nuovo menu.
     * Riferimento: UC "Gestione dei Menù" - Passo 1
     */
    public Menu creaNuovoMenu() {
        Menu nuovoMenu = new Menu(nextMenuId++);
        menus.add(nuovoMenu);
        salvaMenuInJson();
        return nuovoMenu;
    }
    
    /**
     * Crea un nuovo menu con titolo iniziale (con validazione unicità).
     * @param titolo Il titolo per il nuovo menu
     * @return Il menu creato, oppure null se il titolo è duplicato
     */
    public Menu creaNuovoMenuConTitolo(String titolo) {
        if (titolo == null || titolo.trim().isEmpty()) {
            return null;
        }
        
        String titoloNormalizzato = titolo.trim();
        
        // Verifica se il titolo è già utilizzato
        if (isTitoloGiaEsistente(titoloNormalizzato, -1)) {
            return null; // Titolo duplicato
        }
        
        Menu nuovoMenu = new Menu(nextMenuId++);
        nuovoMenu.setTitolo(titoloNormalizzato);
        menus.add(nuovoMenu);
        salvaMenuInJson();
        return nuovoMenu;
    }
    
    /**
     * Crea una nuova sezione di menu con il nome specificato.
     */
    public SezioneMenu creaSezioneMenu(String nome) {
        return new SezioneMenu(nextSezioneId.getAndIncrement(), nome);
    }
    
    /**
     * Verifica se un titolo è già utilizzato da un altro menu.
     * @param titolo Il titolo da verificare
     * @param menuCorrenteId L'ID del menu corrente (per escluderlo dal controllo)
     * @return true se il titolo è già utilizzato da un altro menu
     */
    public boolean isTitoloGiaEsistente(String titolo, int menuCorrenteId) {
        if (titolo == null || titolo.trim().isEmpty()) {
            return false;
        }
        
        return menus.stream()
            .anyMatch(menu -> menu.getId() != menuCorrenteId && 
                     menu.getTitolo() != null && 
                     titolo.trim().equalsIgnoreCase(menu.getTitolo()));
    }
    
    /**
     * Aggiorna il titolo di un menu esistente con validazione di unicità.
     * @param menu Il menu da aggiornare
     * @param nuovoTitolo Il nuovo titolo
     * @return true se il titolo è stato aggiornato, false se era duplicato
     */
    public boolean aggiornaTitoloMenu(Menu menu, String nuovoTitolo) {
        if (nuovoTitolo == null || nuovoTitolo.trim().isEmpty()) {
            return false;
        }
        
        String titoloNormalizzato = nuovoTitolo.trim();
        
        // Verifica se il titolo è già utilizzato da un altro menu
        if (isTitoloGiaEsistente(titoloNormalizzato, menu.getId())) {
            return false; // Titolo duplicato
        }
        
        menu.setTitolo(titoloNormalizzato);
        salvaMenuInJson();
        return true;
    }
    
    /**
     * Pubblica un menu.
     * Riferimento: UC "Gestione dei Menù" - Passo 7
     */
    public void pubblicaMenu(Menu menu, String formato, List<String> destinatari) {
        menu.pubblica();
        salvaMenuInJson();
        // Qui ci sarebbe la logica per l'esportazione in diversi formati
    }
    
    /**
     * Elimina un menu.
     * Riferimento: UC "Gestione dei Menù" - Estensione 7a
     */
    public void eliminaMenu(Menu menu) {
        menus.remove(menu);
        salvaMenuInJson();
    }
    
    /**
     * Modifica una ricetta all'interno di un menu.
     * Riferimento: UC "Gestione dei Menù" - Estensione 4a
     * @param menu Il menu contenente la ricetta
     * @param ricettaDaModificare La ricetta da modificare
     * @param nuovaRicetta La nuova ricetta che sostituirà quella precedente
     * @return true se la ricetta è stata modificata con successo, false altrimenti
     */
    public boolean modificaRicetta(Menu menu, Ricetta ricettaDaModificare, Ricetta nuovaRicetta) {
        boolean ricettaTrovata = false;
        
        for (SezioneMenu sezione : menu.getSezioni()) {
            for (int i = 0; i < sezione.getRicette().size(); i++) {
                domain.menu.RicettaInMenu ricettaInMenu = sezione.getRicette().get(i);
                if (ricettaInMenu.getRicettaOriginale().equals(ricettaDaModificare)) {
                    // Mantiene il nome personalizzato ma cambia la ricetta originale
                    String nomePersonalizzato = ricettaInMenu.getNomePersonalizzato();
                    sezione.getRicette().set(i, new domain.menu.RicettaInMenu(nuovaRicetta, nomePersonalizzato));
                    ricettaTrovata = true;
                    break;
                }
            }
            if (ricettaTrovata) break;
        }
        
        if (ricettaTrovata) {
            salvaMenuInJson();
        }
        
        return ricettaTrovata;
    }
    
    public ObservableList<Menu> getMenus() {
        return menus;
    }
    
    public Menu getMenuById(int id) {
        for (Menu menu : menus) {
            if (menu.getId() == id) {
                return menu;
            }
        }
        return null;
    }
    
    /**
     * Sposta una ricetta da una sezione all'altra.
     * Supporta il requirement delle storie utente di riorganizzare i menu.
     */
    public void spostaRicetta(Menu menu, domain.menu.RicettaInMenu ricetta, 
                             SezioneMenu sezioneSorgente, SezioneMenu sezioneDestinazione) {
        sezioneSorgente.spostaRicetta(ricetta, sezioneDestinazione);
        salvaMenuInJson();
    }
    
    /**
     * Rimuove una ricetta da una sezione del menu.
     * Supporta il requirement delle storie utente di eliminare ricette dalle sezioni.
     */
    public void rimuoviRicettaDaSezione(Menu menu, domain.menu.RicettaInMenu ricetta, SezioneMenu sezione) {
        sezione.rimuoviRicettaInMenu(ricetta);
        salvaMenuInJson();
    }
    
    /**
     * Rinomina una ricetta nel menu mantenendo il riferimento alla ricetta originale.
     * Supporta il requirement di Chef Remy di dare nomi fantasiosi ai piatti.
     */
    public void rinominaRicettaNelMenu(Menu menu, domain.menu.RicettaInMenu ricetta, String nuovoNome) {
        ricetta.setNomePersonalizzato(nuovoNome);
        salvaMenuInJson();
    }
}