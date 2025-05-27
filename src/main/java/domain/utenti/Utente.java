package domain.utenti;

import java.io.Serializable;

/**
 * Rappresenta un utente nel sistema di catering.
 * Classe base astratta per tutti i tipi di utente del sistema.
 */
public abstract class Utente implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String nome;
    private String cognome;
    private boolean attivo = true;

    /**
     * Costruttore predefinito per la deserializzazione JSON.
     */
    protected Utente() {
    }

    /**
     * Costruttore per creare un utente con i dati principali.
     *
     * @param id      identificativo univoco dell'utente
     * @param nome    nome dell'utente
     * @param cognome cognome dell'utente
     */
    public Utente(int id, String nome, String cognome) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
    }
    
    // Getters
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getCognome() { return cognome; }
    public boolean isAttivo() { return attivo; }
    
    // Setters
    public void setId(int id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setCognome(String cognome) { this.cognome = cognome; }
    public void setAttivo(boolean attivo) { this.attivo = attivo; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Utente utente = (Utente) o;
        return id == utente.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
    
    @Override
    public String toString() {
        return nome + " " + cognome;
    }
}