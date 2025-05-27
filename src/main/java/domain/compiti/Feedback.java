package domain.compiti;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Rappresenta un feedback associato a un compito di cucina.
 * Questo permette di gestire i feedback separatamente dai compiti tramite file JSON.
 */
public class Feedback {
    private int id;
    private int compitoId; // ID del compito di riferimento
    private String testo;
    private String autore; // Nome del cuoco o "Sistema" per note automatiche
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataCreazione;
    
    /**
     * Costruttore predefinito per la deserializzazione JSON.
     */
    public Feedback() {
        this.dataCreazione = LocalDateTime.now();
    }
    
    /**
     * Costruttore principale per creare un nuovo feedback.
     */
    public Feedback(int id, int compitoId, String testo, String autore) {
        this.id = id;
        this.compitoId = compitoId;
        this.testo = testo;
        this.autore = autore;
        this.dataCreazione = LocalDateTime.now();
    }
    
    // Getters
    public int getId() { return id; }
    public int getCompitoId() { return compitoId; }
    public String getTesto() { return testo; }
    public String getAutore() { return autore; }
    public LocalDateTime getDataCreazione() { return dataCreazione; }
    
    // Setters
    public void setId(int id) { this.id = id; }
    public void setCompitoId(int compitoId) { this.compitoId = compitoId; }
    public void setTesto(String testo) { this.testo = testo; }
    public void setAutore(String autore) { this.autore = autore; }
    public void setDataCreazione(LocalDateTime dataCreazione) { this.dataCreazione = dataCreazione; }
    
    /**
     * Restituisce una rappresentazione formattata del feedback per la visualizzazione.
     */
    public String getTestoFormattato() {
        String dataStr = dataCreazione.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        return String.format("[%s] %s: %s", dataStr, autore, testo);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Feedback feedback = (Feedback) o;
        return id == feedback.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
    
    @Override
    public String toString() {
        return "Feedback{" +
                "id=" + id +
                ", compitoId=" + compitoId +
                ", autore='" + autore + '\'' +
                '}';
    }
} 