package domain.compiti;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.*;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Rappresenta un turno di lavoro nel sistema di catering.
 * Definisce i tempi, luoghi e modalità di lavoro per i cuochi.
 */
public class Turno {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final ObjectProperty<LocalDate> data = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalTime> oraInizio = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalTime> oraFine = new SimpleObjectProperty<>();
    private final StringProperty luogo = new SimpleStringProperty();
    private final StringProperty tipo = new SimpleStringProperty();
    private final BooleanProperty modificabile = new SimpleBooleanProperty(true);

    /**
     * Costruttore predefinito per la deserializzazione JSON.
     */
    public Turno() {
    }

    /**
     * Costruttore completo per creare un turno con tutti i parametri.
     *
     * @param id        identificativo univoco del turno
     * @param data      data del turno
     * @param oraInizio ora di inizio del turno
     * @param oraFine   ora di fine del turno
     * @param luogo     luogo dove si svolge il turno
     * @param tipo      tipologia del turno
     */
    @JsonCreator
    public Turno(@JsonProperty("id") int id, 
                 @JsonProperty("data") LocalDate data, 
                 @JsonProperty("oraInizio") LocalTime oraInizio, 
                 @JsonProperty("oraFine") LocalTime oraFine, 
                 @JsonProperty("luogo") String luogo, 
                 @JsonProperty("tipo") String tipo) {
        this.id.set(id);
        this.data.set(data);
        this.oraInizio.set(oraInizio);
        this.oraFine.set(oraFine);
        this.luogo.set(luogo);
        this.tipo.set(tipo);
    }

    /**
     * Aggiorna l'orario del turno se modificabile.
     *
     * @param nuovoInizio nuovo orario di inizio
     * @param nuovoFine   nuovo orario di fine
     */
    public void aggiornaOrario(LocalTime nuovoInizio, LocalTime nuovoFine) {
        if (modificabile.get()) {
            this.oraInizio.set(nuovoInizio);
            this.oraFine.set(nuovoFine);
        }
    }

    // Getters per proprietà JavaFX (binding)
    public IntegerProperty idProperty() { return id; }
    public ObjectProperty<LocalDate> dataProperty() { return data; }
    public ObjectProperty<LocalTime> oraInizioProperty() { return oraInizio; }
    public ObjectProperty<LocalTime> oraFineProperty() { return oraFine; }
    public StringProperty luogoProperty() { return luogo; }
    public StringProperty tipoProperty() { return tipo; }
    public BooleanProperty modificabileProperty() { return modificabile; }
    
    // Getters standard
    public int getId() { return id.get(); }
    public LocalDate getData() { return data.get(); }
    public LocalTime getOraInizio() { return oraInizio.get(); }
    public LocalTime getOraFine() { return oraFine.get(); }
    public String getLuogo() { return luogo.get(); }
    public String getTipo() { return tipo.get(); }
    public boolean isModificabile() { return modificabile.get(); }
    
    // Setters per deserializzazione JSON
    public void setId(int id) { this.id.set(id); }
    public void setData(LocalDate data) { this.data.set(data); }
    public void setOraInizio(LocalTime oraInizio) { this.oraInizio.set(oraInizio); }
    public void setOraFine(LocalTime oraFine) { this.oraFine.set(oraFine); }
    public void setLuogo(String luogo) { this.luogo.set(luogo); }
    public void setTipo(String tipo) { this.tipo.set(tipo); }
    public void setModificabile(boolean modificabile) { this.modificabile.set(modificabile); }
}