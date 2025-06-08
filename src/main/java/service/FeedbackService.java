package service;

import domain.compiti.Feedback;
import service.persistence.JsonLoader;
import com.fasterxml.jackson.core.type.TypeReference;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servizio per la gestione dei feedback dei compiti.
 * Gestisce il caricamento e salvataggio dei feedback tramite file JSON separato.
 */
public class FeedbackService {
    
    private static final String DEFAULT_FEEDBACK_FILE_PATH = "data/feedback.json";
    private String feedbackFilePath;
    private ObservableList<Feedback> feedbacks;
    private int nextId = 1;
    
    public FeedbackService() {
        this(DEFAULT_FEEDBACK_FILE_PATH);
    }
    
    public FeedbackService(String feedbackFilePath) {
        this.feedbackFilePath = feedbackFilePath;
        loadFeedbacks();
    }
    
    /**
     * Carica i feedback dal file JSON
     */
    private void loadFeedbacks() {
        List<Feedback> loadedFeedbacks = JsonLoader.loadFromResources(
            feedbackFilePath, 
            new TypeReference<List<Feedback>>() {}
        );
        
        this.feedbacks = FXCollections.observableArrayList(loadedFeedbacks);
        
        // Calcola il prossimo ID disponibile
        this.nextId = feedbacks.stream()
            .mapToInt(Feedback::getId)
            .max()
            .orElse(0) + 1;
    }
    
    /**
     * Salva i feedback nel file JSON.
     */
    private void salvaFeedbacks() {
        JsonLoader.saveToFile("src/main/resources/" + feedbackFilePath, new ArrayList<>(feedbacks));
    }
    
    /**
     * Aggiunge un nuovo feedback per un compito.
     */
    public Feedback aggiungiFeedback(int compitoId, String testo, String autore) {
        Feedback nuovoFeedback = new Feedback(nextId++, compitoId, testo, autore);
        feedbacks.add(nuovoFeedback);
        salvaFeedbacks();
        return nuovoFeedback;
    }
    
    /**
     * Restituisce tutti i feedback per un compito specifico.
     */
    public List<Feedback> getFeedbackPerCompito(int compitoId) {
        return feedbacks.stream()
            .filter(f -> f.getCompitoId() == compitoId)
            .collect(Collectors.toList());
    }
    
    /**
     * Restituisce tutti i feedback per un compito come stringa formattata.
     */
    public String getFeedbackFormattatiPerCompito(int compitoId) {
        List<Feedback> feedbackCompito = getFeedbackPerCompito(compitoId);
        
        if (feedbackCompito.isEmpty()) {
            return "Nessun feedback disponibile";
        }
        
        return feedbackCompito.stream()
            .map(Feedback::getTestoFormattato)
            .collect(Collectors.joining("\n\n"));
    }
    
    /**
     * Restituisce un feedback concatenato semplice per compatibilità (senza date/autori).
     */
    public String getFeedbackSemplicePerCompito(int compitoId) {
        List<Feedback> feedbackCompito = getFeedbackPerCompito(compitoId);
        
        if (feedbackCompito.isEmpty()) {
            return "";
        }
        
        return feedbackCompito.stream()
            .map(Feedback::getTesto)
            .collect(Collectors.joining("\n---\n"));
    }
    
    /**
     * Elimina un feedback specifico.
     */
    public boolean eliminaFeedback(int feedbackId) {
        boolean removed = feedbacks.removeIf(f -> f.getId() == feedbackId);
        if (removed) {
            salvaFeedbacks();
        }
        return removed;
    }
    
    /**
     * Elimina tutti i feedback associati a un compito.
     */
    public void eliminaFeedbackPerCompito(int compitoId) {
        feedbacks.removeIf(f -> f.getCompitoId() == compitoId);
        salvaFeedbacks();
    }
    
    /**
     * Restituisce tutti i feedback del sistema.
     */
    public ObservableList<Feedback> getFeedbacks() {
        return feedbacks;
    }
    
    /**
     * Modifica il testo di un feedback esistente.
     */
    public boolean modificaFeedback(int feedbackId, String nuovoTesto) {
        for (Feedback feedback : feedbacks) {
            if (feedback.getId() == feedbackId) {
                feedback.setTesto(nuovoTesto);
                salvaFeedbacks();
                return true;
            }
        }
        return false;
    }
    
    /**
     * Conta il numero di feedback per un compito.
     */
    public int contaFeedbackPerCompito(int compitoId) {
        return (int) feedbacks.stream()
            .filter(f -> f.getCompitoId() == compitoId)
            .count();
    }
} 