package ui.viewmodels;

import domain.ricette.Ricetta;
import domain.ricette.Tag;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Rappresenta un ViewModel per la gestione delle ricette nell'interfaccia utente del sistema di catering.
 * Facilita il binding dei dati tra il modello di dominio e i controlli JavaFX.
 */
public class RicettaViewModel {
    
    private final Ricetta ricetta;
    private final StringProperty nome = new SimpleStringProperty();
    private final StringProperty descrizione = new SimpleStringProperty();
    private final StringProperty stato = new SimpleStringProperty();
    private final IntegerProperty tempoPreparazione = new SimpleIntegerProperty();
    private final BooleanProperty inUso = new SimpleBooleanProperty();
    private final ObservableList<Tag> tags = FXCollections.observableArrayList();

    /**
     * Costruttore che crea un ViewModel a partire da una ricetta.
     *
     * @param ricetta ricetta di dominio da cui creare il ViewModel
     */
    public RicettaViewModel(Ricetta ricetta) {
        this.ricetta = ricetta;
        loadFromRicetta();
    }

    /**
     * Carica i dati dalla ricetta nelle proprietà del ViewModel.
     */
    private void loadFromRicetta() {
        nome.set(ricetta.getNome());
        descrizione.set(ricetta.getDescrizione());
        stato.set(ricetta.getStato());
        tempoPreparazione.set(ricetta.getTempoPreparazione());
        inUso.set(ricetta.isInUso());
        tags.addAll(ricetta.getTags());
    }

    /**
     * Salva i dati dal ViewModel nella ricetta di dominio.
     */
    public void saveToRicetta() {
        ricetta.setNome(nome.get());
        ricetta.setDescrizione(descrizione.get());
        ricetta.setStato(stato.get());
        ricetta.setTempoPreparazione(tempoPreparazione.get());
        ricetta.setInUso(inUso.get());
    }

    // Proprietà per il binding JavaFX
    public StringProperty nomeProperty() { return nome; }
    public StringProperty descrizioneProperty() { return descrizione; }
    public StringProperty statoProperty() { return stato; }
    public IntegerProperty tempoPreparazioneProperty() { return tempoPreparazione; }
    public BooleanProperty inUsoProperty() { return inUso; }
    public ObservableList<Tag> getTags() { return tags; }

    /**
     * Restituisce la ricetta aggiornata con i dati del ViewModel.
     *
     * @return ricetta con i dati aggiornati
     */
    public Ricetta getRicetta() {
        saveToRicetta();
        return ricetta;
    }
} 