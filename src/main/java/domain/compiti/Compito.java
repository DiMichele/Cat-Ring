package domain.compiti;

import domain.ricette.Ricetta;
import domain.utenti.Cuoco;
import domain.eventi.Evento;
import javafx.beans.property.SimpleStringProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Rappresenta un compito di cucina nel sistema di catering.
 * Gestisce l'assegnazione di compiti ai cuochi con relative informazioni
 * su ricette, tempi, quantità e stato di avanzamento.
 * I feedback sono ora gestiti separatamente tramite FeedbackService.
 */
public class Compito {
    private int id;
    
    // Campi per riferimenti tramite ID (per JSON)
    private Integer ricettaId;
    private Integer cuocoId;
    private Integer turnoId;
    private Integer eventoId;
    
    // Oggetti risolti (transient, non salvati in JSON)
    @JsonIgnore
    private Ricetta ricetta;
    @JsonIgnore
    private Cuoco cuocoAssegnato;
    @JsonIgnore
    private Evento evento;
    
    private String turno; // Stringa rappresentazione del turno (compatibilità)
    private int durata; // durata in minuti
    private double quantita;
    private String stato = "Da iniziare";
    private int importanza = 1; // 1-5, dove 5 è la massima importanza
    
    /**
     * Costruttore predefinito per la deserializzazione JSON.
     */
    public Compito() {
    }
    
    // Costruttore principale per creazione programmatica
    public Compito(int id, Ricetta ricetta, Cuoco cuocoAssegnato, String turno, int durata, double quantita) {
        this.id = id;
        this.ricetta = ricetta;
        this.ricettaId = ricetta != null ? ricetta.getId() : null;
        this.cuocoAssegnato = cuocoAssegnato;
        this.cuocoId = cuocoAssegnato != null ? cuocoAssegnato.getId() : null;
        this.turno = turno;
        this.durata = durata;
        this.quantita = quantita;
    }
    
    // Costruttore con evento
    public Compito(int id, Ricetta ricetta, Cuoco cuocoAssegnato, String turno, int durata, double quantita, Evento evento) {
        this(id, ricetta, cuocoAssegnato, turno, durata, quantita);
        this.evento = evento;
        this.eventoId = evento != null ? evento.getId() : null;
    }
    
    // Metodi per risoluzione riferimenti
    public void risolviRicetta(Ricetta ricetta) {
        this.ricetta = ricetta;
        this.ricettaId = ricetta != null ? ricetta.getId() : null;
    }
    
    public void risolviCuoco(Cuoco cuoco) {
        this.cuocoAssegnato = cuoco;
        this.cuocoId = cuoco != null ? cuoco.getId() : null;
    }
    
    public void risolviEvento(Evento evento) {
        this.evento = evento;
        this.eventoId = evento != null ? evento.getId() : null;
    }
    
    // Getters per ID (per JSON)
    public Integer getRicettaId() { return ricettaId; }
    public Integer getCuocoId() { return cuocoId; }
    public Integer getTurnoId() { return turnoId; }
    public Integer getEventoId() { return eventoId; }
    
    // Setters per ID (per JSON)
    public void setRicettaId(Integer ricettaId) { this.ricettaId = ricettaId; }
    public void setCuocoId(Integer cuocoId) { this.cuocoId = cuocoId; }
    public void setTurnoId(Integer turnoId) { this.turnoId = turnoId; }
    public void setEventoId(Integer eventoId) { this.eventoId = eventoId; }
    
    // Getters standard
    public int getId() { return id; }
    public Ricetta getRicetta() { return ricetta; }
    public Cuoco getCuocoAssegnato() { return cuocoAssegnato; }
    public String getTurno() { return turno; }
    public int getDurata() { return durata; }
    public double getQuantita() { return quantita; }
    public String getStato() { return stato; }
    public int getImportanza() { return importanza; }
    public Evento getEvento() { return evento; }
    public int getTempoStimato() { return durata; } // Alias per compatibilità
    
    // Setters standard
    public void setId(int id) { this.id = id; }
    public void setRicetta(Ricetta ricetta) { risolviRicetta(ricetta); }
    public void setCuocoAssegnato(Cuoco cuocoAssegnato) { risolviCuoco(cuocoAssegnato); }
    public void setEvento(Evento evento) { risolviEvento(evento); }
    public void setTurno(String turno) { this.turno = turno; }
    public void setDurata(int durata) { this.durata = durata; }
    public void setQuantita(double quantita) { this.quantita = quantita; }
    public void setStato(String stato) { this.stato = stato; }
    public void setTempoStimato(int tempoStimato) { this.durata = tempoStimato; }
    
    public void setImportanza(int importanza) {
        if (importanza < 1 || importanza > 5) {
            throw new IllegalArgumentException("L'importanza deve essere tra 1 e 5");
        }
        this.importanza = importanza;
    }
    
    // Metodo di supporto per JavaFX binding
    public SimpleStringProperty statoProperty() {
        return new SimpleStringProperty(stato);
    }
    
    // Metodi per la compatibilità con il vecchio Controller
    public void aggiornaStato(String nuovoStato) {
        setStato(nuovoStato);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Compito compito = (Compito) o;
        return id == compito.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
    
    @Override
    public String toString() {
        return "Compito{" +
                "id=" + id +
                ", ricettaId=" + ricettaId +
                ", cuocoId=" + cuocoId +
                ", stato='" + stato + '\'' +
                '}';
    }
}