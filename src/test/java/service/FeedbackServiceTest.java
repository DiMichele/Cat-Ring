package service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import domain.compiti.Feedback;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FeedbackServiceTest {

    private FeedbackService feedbackService;
    private static final String TEST_FEEDBACK_FILE = "src/main/resources/data/feedback_test.json";
    
    @BeforeEach
    public void setup() {
        // Rimuovi il file di test se esiste per iniziare pulito
        File testFile = new File(TEST_FEEDBACK_FILE);
        if (testFile.exists()) {
            testFile.delete();
        }
        
        // Crea un servizio feedback con file di test isolato
        feedbackService = new FeedbackService("data/feedback_test.json");
    }
    
    @AfterEach
    public void cleanup() {
        // Rimuovi il file di test dopo ogni test
        File testFile = new File(TEST_FEEDBACK_FILE);
        if (testFile.exists()) {
            testFile.delete();
        }
    }
    
    @Test
    @DisplayName("Test aggiunta feedback")
    public void testAggiungiFeedback() {
        // Act
        Feedback feedback = feedbackService.aggiungiFeedback(1, "Test feedback", "Mario Rossi");
        
        // Assert
        assertNotNull(feedback);
        assertTrue(feedback.getId() > 0);
        assertEquals(1, feedback.getCompitoId());
        assertEquals("Test feedback", feedback.getTesto());
        assertEquals("Mario Rossi", feedback.getAutore());
        assertNotNull(feedback.getDataCreazione());
    }
    
    @Test
    @DisplayName("Test ottenimento feedback per compito esistente")
    public void testGetFeedbackPerCompitoEsistente() {
        // Arrange
        feedbackService.aggiungiFeedback(1, "Primo feedback", "Mario Rossi");
        feedbackService.aggiungiFeedback(1, "Secondo feedback", "Anna Bianchi");
        feedbackService.aggiungiFeedback(2, "Feedback altro compito", "Giuseppe Verdi");
        
        // Act
        List<Feedback> feedbackCompito1 = feedbackService.getFeedbackPerCompito(1);
        List<Feedback> feedbackCompito2 = feedbackService.getFeedbackPerCompito(2);
        
        // Assert
        assertEquals(2, feedbackCompito1.size());
        assertEquals(1, feedbackCompito2.size());
        assertTrue(feedbackCompito1.stream().allMatch(f -> f.getCompitoId() == 1));
        assertTrue(feedbackCompito2.stream().allMatch(f -> f.getCompitoId() == 2));
    }
    
    @Test
    @DisplayName("Test ottenimento feedback per compito inesistente")
    public void testGetFeedbackPerCompitoInesistente() {
        // Act
        List<Feedback> feedbacks = feedbackService.getFeedbackPerCompito(999);
        
        // Assert
        assertNotNull(feedbacks);
        assertTrue(feedbacks.isEmpty());
    }
    
    @Test
    @DisplayName("Test feedback formattati per compito")
    public void testGetFeedbackFormattatiPerCompito() {
        // Arrange
        feedbackService.aggiungiFeedback(1, "Tutto ok", "Mario Rossi");
        feedbackService.aggiungiFeedback(1, "Piccolo problema", "Anna Bianchi");
        
        // Act
        String feedbackFormattati = feedbackService.getFeedbackFormattatiPerCompito(1);
        
        // Assert
        assertNotNull(feedbackFormattati);
        assertFalse(feedbackFormattati.isEmpty());
        assertTrue(feedbackFormattati.contains("Mario Rossi"));
        assertTrue(feedbackFormattati.contains("Anna Bianchi"));
        assertTrue(feedbackFormattati.contains("Tutto ok"));
        assertTrue(feedbackFormattati.contains("Piccolo problema"));
    }
    
    @Test
    @DisplayName("Test feedback semplice per compito")
    public void testGetFeedbackSemplicePerCompito() {
        // Arrange
        feedbackService.aggiungiFeedback(1, "Primo feedback", "Mario Rossi");
        feedbackService.aggiungiFeedback(1, "Secondo feedback", "Anna Bianchi");
        
        // Act
        String feedbackSemplice = feedbackService.getFeedbackSemplicePerCompito(1);
        
        // Assert
        assertNotNull(feedbackSemplice);
        assertTrue(feedbackSemplice.contains("Primo feedback"));
        assertTrue(feedbackSemplice.contains("Secondo feedback"));
        assertTrue(feedbackSemplice.contains("---")); // Separatore
    }
    
    @Test
    @DisplayName("Test feedback semplice per compito senza feedback")
    public void testGetFeedbackSemplicePerCompitoVuoto() {
        // Act
        String feedbackSemplice = feedbackService.getFeedbackSemplicePerCompito(999);
        
        // Assert
        assertNotNull(feedbackSemplice);
        assertTrue(feedbackSemplice.isEmpty());
    }
    
    @Test
    @DisplayName("Test eliminazione feedback")
    public void testEliminaFeedback() {
        // Arrange
        Feedback feedback = feedbackService.aggiungiFeedback(1, "Test feedback", "Mario Rossi");
        int feedbackId = feedback.getId();
        
        // Verifica che esista
        List<Feedback> feedbacksPrima = feedbackService.getFeedbackPerCompito(1);
        assertEquals(1, feedbacksPrima.size());
        
        // Act
        boolean risultato = feedbackService.eliminaFeedback(feedbackId);
        
        // Assert
        assertTrue(risultato);
        List<Feedback> feedbacksDopo = feedbackService.getFeedbackPerCompito(1);
        assertTrue(feedbacksDopo.isEmpty());
    }
    
    @Test
    @DisplayName("Test eliminazione feedback inesistente")
    public void testEliminaFeedbackInesistente() {
        // Act
        boolean risultato = feedbackService.eliminaFeedback(999);
        
        // Assert
        assertFalse(risultato);
    }
    
    @Test
    @DisplayName("Test eliminazione tutti i feedback per compito")
    public void testEliminaFeedbackPerCompito() {
        // Arrange
        feedbackService.aggiungiFeedback(1, "Primo feedback", "Mario Rossi");
        feedbackService.aggiungiFeedback(1, "Secondo feedback", "Anna Bianchi");
        feedbackService.aggiungiFeedback(2, "Feedback altro compito", "Giuseppe Verdi");
        
        // Verifica stato iniziale
        assertEquals(2, feedbackService.getFeedbackPerCompito(1).size());
        assertEquals(1, feedbackService.getFeedbackPerCompito(2).size());
        
        // Act
        feedbackService.eliminaFeedbackPerCompito(1);
        
        // Assert
        assertTrue(feedbackService.getFeedbackPerCompito(1).isEmpty());
        assertEquals(1, feedbackService.getFeedbackPerCompito(2).size()); // Altri compiti non toccati
    }
    
    @Test
    @DisplayName("Test modifica feedback esistente")
    public void testModificaFeedback() {
        // Arrange
        Feedback feedback = feedbackService.aggiungiFeedback(1, "Testo originale", "Mario Rossi");
        int feedbackId = feedback.getId();
        
        // Act
        boolean risultato = feedbackService.modificaFeedback(feedbackId, "Testo modificato");
        
        // Assert
        assertTrue(risultato);
        List<Feedback> feedbacks = feedbackService.getFeedbackPerCompito(1);
        assertEquals(1, feedbacks.size());
        assertEquals("Testo modificato", feedbacks.get(0).getTesto());
        assertEquals("Mario Rossi", feedbacks.get(0).getAutore()); // Autore non cambia
    }
    
    @Test
    @DisplayName("Test modifica feedback inesistente")
    public void testModificaFeedbackInesistente() {
        // Act
        boolean risultato = feedbackService.modificaFeedback(999, "Nuovo testo");
        
        // Assert
        assertFalse(risultato);
    }
    
    @Test
    @DisplayName("Test conteggio feedback per compito")
    public void testContaFeedbackPerCompito() {
        // Arrange
        feedbackService.aggiungiFeedback(1, "Primo", "Mario");
        feedbackService.aggiungiFeedback(1, "Secondo", "Anna");
        feedbackService.aggiungiFeedback(2, "Terzo", "Giuseppe");
        
        // Act & Assert
        assertEquals(2, feedbackService.contaFeedbackPerCompito(1));
        assertEquals(1, feedbackService.contaFeedbackPerCompito(2));
        assertEquals(0, feedbackService.contaFeedbackPerCompito(999));
    }
    
    @Test
    @DisplayName("Test feedback formattato contiene data e autore")
    public void testFeedbackFormattato() {
        // Arrange
        Feedback feedback = feedbackService.aggiungiFeedback(1, "Test messaggio", "Mario Rossi");
        
        // Act
        String formattatato = feedback.getTestoFormattato();
        
        // Assert
        assertNotNull(formattatato);
        assertTrue(formattatato.contains("Mario Rossi"));
        assertTrue(formattatato.contains("Test messaggio"));
        // Verifica che contenga una data nel formato corretto
        assertTrue(formattatato.matches(".*\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}.*"));
    }
} 