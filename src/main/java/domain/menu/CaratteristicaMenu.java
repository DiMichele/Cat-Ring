package domain.menu;

import java.io.Serializable;

/**
 * Rappresenta una caratteristica applicabile ai menu nel sistema di catering.
 * Le caratteristiche descrivono proprietà speciali come "Vegano", "Finger Food", "Senza Glutine", ecc.
 */
public class CaratteristicaMenu implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String nome;
    private String descrizione;
    
    /**
     * Costruttore predefinito per la deserializzazione JSON.
     */
    public CaratteristicaMenu() {
    }
    
    /**
     * Costruttore per creare una caratteristica con solo il nome.
     *
     * @param nome nome della caratteristica
     */
    public CaratteristicaMenu(String nome) {
        this.nome = nome;
    }
    
    /**
     * Costruttore completo per creare una caratteristica con nome e descrizione.
     *
     * @param nome        nome della caratteristica
     * @param descrizione descrizione dettagliata della caratteristica
     */
    public CaratteristicaMenu(String nome, String descrizione) {
        this.nome = nome;
        this.descrizione = descrizione;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getDescrizione() {
        return descrizione;
    }
    
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CaratteristicaMenu that = (CaratteristicaMenu) o;
        return nome != null && nome.equalsIgnoreCase(that.nome);
    }
    
    @Override
    public int hashCode() {
        return nome != null ? nome.toLowerCase().hashCode() : 0;
    }
    
    @Override
    public String toString() {
        return nome;
    }
} 