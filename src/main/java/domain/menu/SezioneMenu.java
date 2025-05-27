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
 * Rappresenta una sezione all'interno di un menu.
 * Ogni sezione raggruppa ricette correlate (es. antipasti, primi, secondi).
 */
public class SezioneMenu {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty nome = new SimpleStringProperty();
    
    @JsonIgnore
    private final ObservableList<RicettaInMenu> ricette = FXCollections.observableArrayList();

    /**
     * Costruttore predefinito per la deserializzazione JSON.
     */
    public SezioneMenu() {
    }

    /**
     * Costruttore per creare una sezione con ID e nome.
     *
     * @param id   identificativo della sezione
     * @param nome nome della sezione
     */
    public SezioneMenu(int id, String nome) {
        this.id.set(id);
        this.nome.set(nome);
    }

    /**
     * Aggiunge una ricetta alla sezione con il nome originale.
     *
     * @param ricetta ricetta da aggiungere
     */
    public void aggiungiRicetta(Ricetta ricetta) {
        ricette.add(new RicettaInMenu(ricetta));
    }
    
    /**
     * Aggiunge una ricetta alla sezione con un nome personalizzato.
     *
     * @param ricetta             ricetta da aggiungere
     * @param nomePersonalizzato  nome personalizzato per il menu
     */
    public void aggiungiRicetta(Ricetta ricetta, String nomePersonalizzato) {
        ricette.add(new RicettaInMenu(ricetta, nomePersonalizzato));
    }
    
    /**
     * Rimuove una ricetta dalla sezione.
     *
     * @param ricetta ricetta da rimuovere
     */
    public void rimuoviRicetta(Ricetta ricetta) {
        ricette.removeIf(r -> r.getRicettaOriginale().equals(ricetta));
    }
    
    /**
     * Rimuove una specifica istanza di ricetta dal menu.
     *
     * @param ricettaInMenu istanza specifica da rimuovere
     */
    public void rimuoviRicettaInMenu(RicettaInMenu ricettaInMenu) {
        ricette.remove(ricettaInMenu);
    }
    
    /**
     * Sposta una ricetta da questa sezione a un'altra sezione.
     *
     * @param ricetta              ricetta da spostare
     * @param sezioneDestinazione  sezione di destinazione
     */
    public void spostaRicetta(RicettaInMenu ricetta, SezioneMenu sezioneDestinazione) {
        if (ricette.remove(ricetta)) {
            sezioneDestinazione.getRicette().add(ricetta);
        }
    }

    // Getters per proprietà JavaFX (binding)
    @JsonIgnore
    public IntegerProperty idProperty() { return id; }
    
    @JsonIgnore
    public StringProperty nomeProperty() { return nome; }
    
    @JsonIgnore
    public ObservableList<RicettaInMenu> getRicette() { return ricette; }
    
    /**
     * Restituisce solo le ricette originali per retrocompatibilità.
     *
     * @return lista delle ricette originali
     */
    @JsonIgnore
    public List<Ricetta> getRicetteOriginali() {
        return ricette.stream()
                .map(RicettaInMenu::getRicettaOriginale)
                .collect(java.util.stream.Collectors.toList());
    }
    
    // Metodi per la serializzazione JSON
    @JsonProperty("ricette")
    public List<RicettaInMenu> getRicetteList() {
        return new ArrayList<>(ricette);
    }
    
    @JsonProperty("ricette")
    public void setRicetteList(List<RicettaInMenu> ricetteList) {
        ricette.clear();
        if (ricetteList != null) {
            ricette.addAll(ricetteList);
        }
    }
    
    // Getters e setters standard
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    
    public String getNome() { return nome.get(); }
    public void setNome(String nome) { this.nome.set(nome); }
}