package domain.ricette;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta una ricetta nel sistema di catering.
 * Classe del dominio che encapsula i dati e le regole di business di una ricetta.
 */
public class Ricetta implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String nome;
    private String descrizione;
    private String stato = "Bozza";
    private int tempoPreparazione;
    private boolean inUso = false;
    
    private List<Tag> tags = new ArrayList<>();

    /**
     * Costruttore predefinito per la deserializzazione JSON.
     */
    public Ricetta() {
    }

    /**
     * Costruttore per creare una ricetta con ID e nome.
     *
     * @param id   identificativo univoco della ricetta
     * @param nome nome della ricetta
     */
    public Ricetta(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    /**
     * Aggiunge un tag alla ricetta.
     *
     * @param tag il tag da aggiungere
     */
    public void aggiungiTag(Tag tag) {
        tags.add(tag);
    }
    
    // Getters
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getDescrizione() { return descrizione; }
    public String getStato() { return stato; }
    public int getTempoPreparazione() { return tempoPreparazione; }
    public boolean isInUso() { return inUso; }
    public List<Tag> getTags() { return tags; }
    
    // Setters
    public void setNome(String nome) { this.nome = nome; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
    public void setStato(String stato) { this.stato = stato; }
    public void setTempoPreparazione(int minuti) { this.tempoPreparazione = minuti; }
    public void setInUso(boolean inUso) { this.inUso = inUso; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ricetta ricetta = (Ricetta) o;
        return id == ricetta.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
    
    @Override
    public String toString() {
        return "Ricetta{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", stato='" + stato + '\'' +
                '}';
    }
}