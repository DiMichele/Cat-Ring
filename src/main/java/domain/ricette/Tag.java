package domain.ricette;

import java.io.Serializable;

/**
 * Rappresenta un tag di categorizzazione per le ricette.
 * Classe del dominio per la classificazione e organizzazione delle ricette.
 */
public class Tag implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String nome;
    private String descrizione;
    private String colore;
    
    /**
     * Costruttore predefinito per la deserializzazione JSON.
     */
    public Tag() {
    }
    
    /**
     * Costruttore per creare un tag con solo il nome.
     *
     * @param nome nome del tag
     */
    public Tag(String nome) {
        this.nome = nome;
    }
    
    /**
     * Costruttore completo per creare un tag con tutti i parametri.
     *
     * @param nome        nome del tag
     * @param descrizione descrizione del tag
     * @param colore      colore associato al tag
     */
    public Tag(String nome, String descrizione, String colore) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.colore = colore;
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
    
    public String getColore() {
        return colore;
    }
    
    public void setColore(String colore) {
        this.colore = colore;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return nome.equals(tag.nome);
    }
    
    @Override
    public int hashCode() {
        return nome.hashCode();
    }
    
    @Override
    public String toString() {
        return nome;
    }
}