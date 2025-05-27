package domain.compiti;

import javafx.beans.property.*;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Rappresenta la disponibilità di un cuoco per un determinato periodo.
 * Gestisce i tempi di disponibilità con stato di conferma e ritiro.
 */
public class Disponibilita {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final ObjectProperty<LocalDate> data = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalTime> oraInizio = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalTime> oraFine = new SimpleObjectProperty<>();
    private final BooleanProperty confermata = new SimpleBooleanProperty(false);
    private final BooleanProperty ritirata = new SimpleBooleanProperty(false);

    /**
     * Costruttore per creare una disponibilità con periodo specificato.
     *
     * @param id        identificativo della disponibilità
     * @param data      data della disponibilità
     * @param oraInizio ora di inizio della disponibilità
     * @param oraFine   ora di fine della disponibilità
     */
    public Disponibilita(int id, LocalDate data, LocalTime oraInizio, LocalTime oraFine) {
        this.id.set(id);
        this.data.set(data);
        this.oraInizio.set(oraInizio);
        this.oraFine.set(oraFine);
    }

    // Getters per proprietà JavaFX (binding)
    public IntegerProperty idProperty() { return id; }
    public ObjectProperty<LocalDate> dataProperty() { return data; }
    public ObjectProperty<LocalTime> oraInizioProperty() { return oraInizio; }
    public ObjectProperty<LocalTime> oraFineProperty() { return oraFine; }
    public BooleanProperty confermataProperty() { return confermata; }
    public BooleanProperty ritirataProperty() { return ritirata; }
    
    // Getters standard
    public int getId() { return id.get(); }
    public LocalDate getData() { return data.get(); }
    public LocalTime getOraInizio() { return oraInizio.get(); }
    public LocalTime getOraFine() { return oraFine.get(); }
    public boolean isConfermata() { return confermata.get(); }
    public boolean isRitirata() { return ritirata.get(); }
    
    // Setters
    public void setConfermata(boolean confermata) { this.confermata.set(confermata); }
    public void setRitirata(boolean ritirata) { this.ritirata.set(ritirata); }
}