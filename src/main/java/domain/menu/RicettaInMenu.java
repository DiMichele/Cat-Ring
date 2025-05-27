package domain.menu;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import domain.ricette.Ricetta;
import javafx.beans.property.*;

/**
 * Rappresenta una ricetta all'interno di un menu con possibilità di personalizzazione.
 * Permette di avere un nome diverso da quello nel ricettario originale,
 * supportando la personalizzazione creativa dei piatti nel menu.
 */
public class RicettaInMenu {
    private final ObjectProperty<Ricetta> ricettaOriginale = new SimpleObjectProperty<>();
    private final StringProperty nomePersonalizzato = new SimpleStringProperty();

    /**
     * Costruttore predefinito per la deserializzazione JSON.
     */
    public RicettaInMenu() {
    }

    public RicettaInMenu(Ricetta ricettaOriginale) {
        this.ricettaOriginale.set(ricettaOriginale);
        this.nomePersonalizzato.set(ricettaOriginale.getNome()); // Di default usa il nome originale
    }

    public RicettaInMenu(Ricetta ricettaOriginale, String nomePersonalizzato) {
        this.ricettaOriginale.set(ricettaOriginale);
        this.nomePersonalizzato.set(nomePersonalizzato);
    }

    /**
     * Restituisce il nome da mostrare nel menu (personalizzato o originale).
     */
    public String getNomeNelMenu() {
        String nome = nomePersonalizzato.get();
        return (nome != null && !nome.trim().isEmpty()) ? nome : 
               (ricettaOriginale.get() != null ? ricettaOriginale.get().getNome() : "");
    }

    /**
     * Imposta un nome personalizzato per la ricetta nel menu.
     */
    public void setNomePersonalizzato(String nome) {
        this.nomePersonalizzato.set(nome);
    }

    /**
     * Ripristina il nome originale della ricetta.
     */
    public void ripristinaNomeOriginale() {
        if (ricettaOriginale.get() != null) {
            this.nomePersonalizzato.set(ricettaOriginale.get().getNome());
        }
    }

    // Getters per proprietà JavaFX
    @JsonIgnore
    public ObjectProperty<Ricetta> ricettaOriginaleProperty() { return ricettaOriginale; }
    
    @JsonIgnore
    public StringProperty nomePersonalizzatoProperty() { return nomePersonalizzato; }

    // Getters standard
    public Ricetta getRicettaOriginale() { return ricettaOriginale.get(); }
    public void setRicettaOriginale(Ricetta ricetta) { this.ricettaOriginale.set(ricetta); }
    
    public String getNomePersonalizzato() { return nomePersonalizzato.get(); }

    // Per serializzazione JSON
    @JsonProperty("ricettaOriginale")
    public Ricetta getRicettaOriginaleForJson() {
        return ricettaOriginale.get();
    }

    @JsonProperty("ricettaOriginale")
    public void setRicettaOriginaleForJson(Ricetta ricetta) {
        this.ricettaOriginale.set(ricetta);
    }

    @JsonProperty("nomePersonalizzato")
    public String getNomePersonalizzatoForJson() {
        return nomePersonalizzato.get();
    }

    @JsonProperty("nomePersonalizzato")
    public void setNomePersonalizzatoForJson(String nome) {
        this.nomePersonalizzato.set(nome);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        RicettaInMenu that = (RicettaInMenu) obj;
        
        // Due RicettaInMenu sono uguali se hanno la stessa ricetta originale
        Ricetta thisRicetta = this.ricettaOriginale.get();
        Ricetta thatRicetta = that.ricettaOriginale.get();
        
        return thisRicetta != null ? thisRicetta.equals(thatRicetta) : thatRicetta == null;
    }

    @Override
    public int hashCode() {
        Ricetta ricetta = ricettaOriginale.get();
        return ricetta != null ? ricetta.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "RicettaInMenu{" +
                "nomeNelMenu='" + getNomeNelMenu() + '\'' +
                ", ricettaOriginale=" + (ricettaOriginale.get() != null ? ricettaOriginale.get().getNome() : "null") +
                '}';
    }
} 