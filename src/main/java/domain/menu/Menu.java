package domain.menu;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import domain.ricette.Ricetta;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta un menu nel sistema di catering.
 * Gestisce la composizione dei menu con sezioni, ricette e caratteristiche.
 * Implementa le operazioni del caso d'uso "Gestione dei Menu".
 */
public class Menu {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty titolo = new SimpleStringProperty();
    private final StringProperty note = new SimpleStringProperty();
    private final StringProperty stato = new SimpleStringProperty("Bozza");
    
    @JsonIgnore
    private final ObservableList<SezioneMenu> sezioni = FXCollections.observableArrayList();
    
    @JsonIgnore
    private final ObservableList<CaratteristicaMenu> caratteristiche = FXCollections.observableArrayList();

    /**
     * Costruttore predefinito per la deserializzazione JSON.
     */
    public Menu() {
    }

    /**
     * Costruttore per creare un menu con un identificativo specifico.
     *
     * @param id identificativo univoco del menu
     */
    public Menu(int id) {
        this.id.set(id);
    }

    /**
     * Imposta il titolo del menu.
     *
     * @param titolo titolo del menu
     */
    public void setTitolo(String titolo) {
        this.titolo.set(titolo);
    }

    /**
     * Definisce le sezioni del menu creando le strutture organizzative.
     *
     * @param nomiSezioni lista dei nomi delle sezioni da creare
     */
    public void definisciSezioni(List<String> nomiSezioni) {
        sezioni.clear();
        for (String nome : nomiSezioni) {
            sezioni.add(new SezioneMenu(sezioni.size() + 1, nome));
        }
    }

    /**
     * Inserisce una ricetta in una sezione specifica del menu.
     *
     * @param ricetta ricetta da inserire
     * @param sezione sezione di destinazione
     */
    public void inserisciRicetta(Ricetta ricetta, SezioneMenu sezione) {
        sezione.aggiungiRicetta(ricetta);
    }
    
    /**
     * Inserisce una ricetta in una sezione con un nome personalizzato.
     *
     * @param ricetta             ricetta da inserire
     * @param sezione             sezione di destinazione
     * @param nomePersonalizzato  nome personalizzato per la ricetta nel menu
     */
    public void inserisciRicetta(Ricetta ricetta, SezioneMenu sezione, String nomePersonalizzato) {
        sezione.aggiungiRicetta(ricetta, nomePersonalizzato);
    }

    /**
     * Aggiunge note e informazioni utili al menu.
     *
     * @param note note informative sul menu
     */
    public void annotaInformazioni(String note) {
        this.note.set(note);
    }

    /**
     * Pubblica il menu cambiando lo stato e rendendolo disponibile.
     */
    public void pubblica() {
        this.stato.set("Pubblicato");
    }
    
    /**
     * Aggiunge una caratteristica al menu.
     *
     * @param caratteristica caratteristica da aggiungere
     */
    public void aggiungiCaratteristica(CaratteristicaMenu caratteristica) {
        if (!caratteristiche.contains(caratteristica)) {
            caratteristiche.add(caratteristica);
        }
    }
    
    /**
     * Rimuove una caratteristica dal menu.
     *
     * @param caratteristica caratteristica da rimuovere
     */
    public void rimuoviCaratteristica(CaratteristicaMenu caratteristica) {
        caratteristiche.remove(caratteristica);
    }
    
    /**
     * Verifica se il menu possiede una specifica caratteristica.
     *
     * @param nome nome della caratteristica da verificare
     * @return true se il menu ha la caratteristica, false altrimenti
     */
    public boolean hasCaratteristica(String nome) {
        for (CaratteristicaMenu caratteristica : caratteristiche) {
            if (caratteristica.getNome().equalsIgnoreCase(nome)) {
                return true;
            }
        }
        return false;
    }

    // Getters per proprietà JavaFX (binding)
    @JsonIgnore
    public IntegerProperty idProperty() { return id; }
    
    @JsonIgnore
    public StringProperty titoloProperty() { return titolo; }
    
    @JsonIgnore
    public StringProperty noteProperty() { return note; }
    
    @JsonIgnore
    public StringProperty statoProperty() { return stato; }
    
    @JsonIgnore
    public ObservableList<SezioneMenu> getSezioni() { return sezioni; }
    
    @JsonIgnore
    public ObservableList<CaratteristicaMenu> getCaratteristiche() { return caratteristiche; }
    
    // Metodi per la serializzazione JSON
    @JsonProperty("sezioni")
    public List<SezioneMenu> getSezioniList() {
        return new ArrayList<>(sezioni);
    }
    
    @JsonProperty("sezioni")
    public void setSezioniList(List<SezioneMenu> sezioniList) {
        sezioni.clear();
        if (sezioniList != null) {
            sezioni.addAll(sezioniList);
        }
    }
    
    @JsonProperty("caratteristiche")
    public List<CaratteristicaMenu> getCaratteristicheList() {
        return new ArrayList<>(caratteristiche);
    }
    
    @JsonProperty("caratteristiche")
    public void setCaratteristicheList(List<CaratteristicaMenu> caratteristicheList) {
        caratteristiche.clear();
        if (caratteristicheList != null) {
            caratteristiche.addAll(caratteristicheList);
        }
    }
    
    // Getters e setters standard
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    
    public String getTitolo() { return titolo.get(); }
    
    public String getNote() { return note.get(); }
    public void setNote(String note) { this.note.set(note); }
    
    public String getStato() { return stato.get(); }
    public void setStato(String stato) { this.stato.set(stato); }
}