package domain.utenti;

import domain.compiti.Compito;
import domain.menu.Menu;
import domain.ricette.Ricetta;

import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta uno chef nel sistema di catering.
 * Estende la classe Utente con funzionalità specializzate per la gestione dei menu e delle ricette.
 */
public class Chef extends Utente {
    private static final long serialVersionUID = 1L;
    
    private final List<Menu> menuCreati = new ArrayList<>();
    private final List<Ricetta> ricetteCreate = new ArrayList<>();
    
    /**
     * Costruttore predefinito per la deserializzazione JSON.
     */
    public Chef() {
        super();
    }
    
    /**
     * Costruttore per creare uno chef con i dati principali.
     *
     * @param id      identificativo univoco dello chef
     * @param nome    nome dello chef
     * @param cognome cognome dello chef
     */
    public Chef(int id, String nome, String cognome) {
        super(id, nome, cognome);
    }
    
    /**
     * Progetta un nuovo menu.
     * Riferimento: UC "Gestione del menu" - Passo 1
     */
    public Menu progettaMenu(int idMenu, String titolo) {
        Menu nuovoMenu = new Menu(idMenu);
        nuovoMenu.setTitolo(titolo);
        menuCreati.add(nuovoMenu);
        return nuovoMenu;
    }
    
    /**
     * Crea una nuova ricetta.
     */
    public Ricetta creaRicetta(int idRicetta, String nome) {
        Ricetta nuovaRicetta = new Ricetta(idRicetta, nome);
        ricetteCreate.add(nuovaRicetta);
        return nuovaRicetta;
    }
    
    public List<Menu> getMenuCreati() {
        return new ArrayList<>(menuCreati);
    }
    
    public List<Ricetta> getRicetteCreate() {
        return new ArrayList<>(ricetteCreate);
    }
    
    /**
     * Crea un nuovo menu.
     * Riferimento: UC "Gestione dei Menù" - Passo 1
     */
    public Menu creaNuovoMenu(int idMenu) {
        return new Menu(idMenu);
    }
    
    /**
     * Assegna un compito a un cuoco.
     * Riferimento: UC "Gestione dei Compiti della cucina" - Passo 3
     */
    public Compito assegnaCompito(int idCompito, Cuoco cuoco, 
                                String turno, 
                                Ricetta ricetta, 
                                int durata, 
                                double quantita) {
        return new Compito(idCompito, ricetta, cuoco, turno, durata, quantita);
    }
    
    @Override
    public String toString() {
        return "Chef " + getNome() + " " + getCognome();
    }
}