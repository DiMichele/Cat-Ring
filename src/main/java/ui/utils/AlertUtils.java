package ui.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * Rappresenta una classe di utilità per la gestione di dialoghi e alert nel sistema di catering.
 * Fornisce metodi statici per mostrare messaggi informativi, di avviso, errore e conferma.
 */
public class AlertUtils {
    
    /**
     * Mostra un dialogo informativo all'utente.
     *
     * @param title   titolo del dialogo
     * @param message messaggio informativo da visualizzare
     */
    public static void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Mostra un dialogo di avviso all'utente.
     *
     * @param title   titolo del dialogo
     * @param message messaggio di avviso da visualizzare
     */
    public static void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Mostra un dialogo di errore all'utente.
     *
     * @param title   titolo del dialogo
     * @param message messaggio di errore da visualizzare
     */
    public static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Mostra un dialogo di conferma e attende la risposta dell'utente.
     *
     * @param title   titolo del dialogo
     * @param message messaggio di conferma da visualizzare
     * @return true se l'utente ha confermato (OK), false altrimenti
     */
    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}