package ui.controllers;

import domain.utenti.Cuoco;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.IntegerProperty;

/**
 * Rappresenta il carico di lavoro di un cuoco nel sistema di catering.
 * Classe di supporto per visualizzare statistiche sui compiti assegnati ai cuochi.
 */
public class CaricoCuoco {
    private final Cuoco cuoco;
    private final SimpleStringProperty nomeCognome;
    private final SimpleIntegerProperty numeroCompiti;
    private final SimpleIntegerProperty tempoTotale;
    private final SimpleStringProperty compitiDettaglio;
    private final SimpleStringProperty disponibilita;
    
    /**
     * Costruttore per creare un oggetto rappresentante il carico di lavoro di un cuoco.
     *
     * @param cuoco            cuoco di riferimento
     * @param numeroCompiti    numero totale di compiti assegnati
     * @param tempoTotale      tempo totale in minuti dei compiti
     * @param compitiDettaglio descrizione dettagliata dei compiti
     * @param disponibilita    informazioni sulla disponibilità
     */
    public CaricoCuoco(Cuoco cuoco, int numeroCompiti, int tempoTotale, String compitiDettaglio, String disponibilita) {
        this.cuoco = cuoco;
        this.nomeCognome = new SimpleStringProperty(cuoco.getNome() + " " + cuoco.getCognome());
        this.numeroCompiti = new SimpleIntegerProperty(numeroCompiti);
        this.tempoTotale = new SimpleIntegerProperty(tempoTotale);
        this.compitiDettaglio = new SimpleStringProperty(compitiDettaglio);
        this.disponibilita = new SimpleStringProperty(disponibilita);
    }
    
    // Getters per le proprietà JavaFX (binding)
    public StringProperty nomeCognomeProperty() { return nomeCognome; }
    public IntegerProperty numeroCompitiProperty() { return numeroCompiti; }
    public IntegerProperty tempoTotaleProperty() { return tempoTotale; }
    public StringProperty compitiDettaglioProperty() { return compitiDettaglio; }
    public StringProperty disponibilitaProperty() { return disponibilita; }
    
    // Getters standard
    public Cuoco getCuoco() { return cuoco; }
    public String getNomeCognome() { return nomeCognome.get(); }
    public int getNumeroCompiti() { return numeroCompiti.get(); }
    public int getTempoTotale() { return tempoTotale.get(); }
    public String getCompitiDettaglio() { return compitiDettaglio.get(); }
    public String getDisponibilita() { return disponibilita.get(); }
    
    // Setters
    public void setNumeroCompiti(int numeroCompiti) { this.numeroCompiti.set(numeroCompiti); }
    public void setTempoTotale(int tempoTotale) { this.tempoTotale.set(tempoTotale); }
    public void setCompitiDettaglio(String compitiDettaglio) { this.compitiDettaglio.set(compitiDettaglio); }
    public void setDisponibilita(String disponibilita) { this.disponibilita.set(disponibilita); }
} 