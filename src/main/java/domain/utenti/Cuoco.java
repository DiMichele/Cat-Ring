package domain.utenti;

import domain.compiti.Disponibilita;
import domain.ricette.Ricetta;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta un cuoco nel sistema di catering.
 * Estende la classe Utente con funzionalità specifiche per i cuochi.
 */
public class Cuoco extends Utente {
    private final List<Disponibilita> disponibilita = new ArrayList<>();
    
    /**
     * Costruttore predefinito per la deserializzazione JSON.
     */
    public Cuoco() {
        super();
    }
    
    /**
     * Costruttore per creare un cuoco con i dati principali.
     *
     * @param id      identificativo univoco del cuoco
     * @param nome    nome del cuoco
     * @param cognome cognome del cuoco
     */
    public Cuoco(int id, String nome, String cognome) {
        super(id, nome, cognome);
    }
    
    /**
     * Fornisce disponibilità per una data e orario specifici.
     *
     * @param idDisponibilita identificativo della disponibilità
     * @param data            data della disponibilità
     * @param oraInizio       ora di inizio della disponibilità
     * @param oraFine         ora di fine della disponibilità
     * @return la nuova disponibilità creata
     */
    public Disponibilita fornisceDisponibilita(int idDisponibilita, LocalDate data, 
                                             LocalTime oraInizio, 
                                             LocalTime oraFine) {
        Disponibilita nuovaDisponibilita = new Disponibilita(idDisponibilita, data, oraInizio, oraFine);
        disponibilita.add(nuovaDisponibilita);
        return nuovaDisponibilita;
    }
    
    /**
     * Crea una nuova ricetta.
     *
     * @param idRicetta identificativo della ricetta
     * @param nome      nome della ricetta
     * @return la nuova ricetta creata
     */
    public Ricetta creaRicetta(int idRicetta, String nome) {
        return new Ricetta(idRicetta, nome);
    }
    
    /**
     * Restituisce la lista delle disponibilità del cuoco.
     *
     * @return copia della lista delle disponibilità
     */
    public List<Disponibilita> getDisponibilita() {
        return new ArrayList<>(disponibilita);
    }
}