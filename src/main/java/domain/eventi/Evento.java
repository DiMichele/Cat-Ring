package domain.eventi;

import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.*;
import domain.menu.Menu;
import java.time.LocalDateTime;

/**
 * Rappresenta un evento nel sistema di catering.
 * Gestisce tutte le informazioni relative ad eventi e cerimonie da servire.
 */
public class Evento {
    private final IntegerProperty id = new SimpleIntegerProperty();
    @JsonProperty("titolo")
    private final StringProperty nome = new SimpleStringProperty();
    @JsonProperty("data")
    private final ObjectProperty<LocalDateTime> dataInizio = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> dataFine = new SimpleObjectProperty<>();
    private final StringProperty luogo = new SimpleStringProperty();
    @JsonProperty("numeroPartecipanti")
    private final IntegerProperty numeroDiPersone = new SimpleIntegerProperty();
    private final BooleanProperty ricorrente = new SimpleBooleanProperty(false);
    private final StringProperty stato = new SimpleStringProperty("Pianificato");
    private final StringProperty note = new SimpleStringProperty();
    private final ObjectProperty<Menu> menu = new SimpleObjectProperty<>();

    /**
     * Costruttore predefinito per la deserializzazione JSON.
     */
    public Evento() {
    }

    public Evento(int id, String nome, LocalDateTime dataInizio, LocalDateTime dataFine, 
                 String luogo, int numeroDiPersone) {
        this.id.set(id);
        this.nome.set(nome);
        this.dataInizio.set(dataInizio);
        this.dataFine.set(dataFine);
        this.luogo.set(luogo);
        this.numeroDiPersone.set(numeroDiPersone);
    }

    // Getters per proprietà JavaFX
    public IntegerProperty idProperty() { return id; }
    public StringProperty nomeProperty() { return nome; }
    public ObjectProperty<LocalDateTime> dataInizioProperty() { return dataInizio; }
    public ObjectProperty<LocalDateTime> dataFineProperty() { return dataFine; }
    public StringProperty luogoProperty() { return luogo; }
    public IntegerProperty numeroDiPersoneProperty() { return numeroDiPersone; }
    public BooleanProperty ricorrenteProperty() { return ricorrente; }
    public StringProperty statoProperty() { return stato; }
    public StringProperty noteProperty() { return note; }
    public ObjectProperty<Menu> menuProperty() { return menu; }
    
    // Getters standard
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    
    public String getNome() { return nome.get(); }
    public LocalDateTime getDataInizio() { return dataInizio.get(); }
    public LocalDateTime getDataFine() { return dataFine.get(); }
    public String getLuogo() { return luogo.get(); }
    public int getNumeroDiPersone() { return numeroDiPersone.get(); }
    public boolean isRicorrente() { return ricorrente.get(); }
    public String getStato() { return stato.get(); }
    public String getNote() { return note.get(); }
    public Menu getMenu() { return menu.get(); }
    
    // Setters
    public void setNome(String nome) { this.nome.set(nome); }
    public void setDataInizio(LocalDateTime dataInizio) { this.dataInizio.set(dataInizio); }
    public void setDataFine(LocalDateTime dataFine) { this.dataFine.set(dataFine); }
    public void setLuogo(String luogo) { this.luogo.set(luogo); }
    public void setNumeroDiPersone(int numeroDiPersone) { this.numeroDiPersone.set(numeroDiPersone); }
    public void setRicorrente(boolean ricorrente) { this.ricorrente.set(ricorrente); }
    public void setStato(String stato) { this.stato.set(stato); }
    public void setNote(String note) { this.note.set(note); }
    public void setMenu(Menu menu) { this.menu.set(menu); }
}