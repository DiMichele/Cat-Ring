import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import service.*;
import ui.controllers.CompitiCucinaController;
import ui.controllers.MenuController;

/**
 * Rappresenta la classe principale dell'applicazione di gestione catering.
 * Implementa il pattern MVC e inizializza i servizi e l'interfaccia utente.
 */
public class Main extends Application {
    
    private MenuService menuService;
    private CompitoCucinaService compitoCucinaService;
    private EventoService eventoService;
    private RicettaService ricettaService;
    private UtenteService utenteService;
    private FeedbackService feedbackService;
    
    @Override
    public void start(Stage primaryStage) {
        inizializzaServizi();
        
        try {
            TabPane tabPane = new TabPane();
            
            // Carica la vista per la gestione dei menu
            FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("/views/menu-view.fxml"));
            MenuController menuController = new MenuController(menuService, ricettaService, eventoService);
            menuLoader.setController(menuController);
            Tab menuTab = new Tab("Gestione menu");
            menuTab.setContent(menuLoader.load());
            menuTab.setClosable(false);
            
            // Carica la vista per la gestione dei compiti di cucina
            FXMLLoader compitiLoader = new FXMLLoader(getClass().getResource("/views/compiti-cucina-view.fxml"));
            CompitiCucinaController compitiController = new CompitiCucinaController(
                compitoCucinaService, eventoService, utenteService, ricettaService, feedbackService);
            compitiLoader.setController(compitiController);
            Tab compitiTab = new Tab("Gestione Compiti Cucina");
            compitiTab.setContent(compitiLoader.load());
            compitiTab.setClosable(false);
            
            // Aggiungi le tab al TabPane
            tabPane.getTabs().addAll(menuTab, compitiTab);
            
            // Configura e mostra la finestra principale
            Scene scene = new Scene(tabPane, 1200, 800);
            primaryStage.setTitle("Cat & Ring");
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Inizializza tutti i servizi dell'applicazione e risolve i riferimenti tra entità.
     */
    private void inizializzaServizi() {
        menuService = new MenuService();
        eventoService = new EventoService();
        ricettaService = new RicettaService();
        utenteService = new UtenteService();
        feedbackService = new FeedbackService();
        compitoCucinaService = new CompitoCucinaService(utenteService);
        
        // Risolvi i riferimenti nei compiti dopo l'inizializzazione di tutti i servizi
        compitoCucinaService.risolviRiferimentiCompiti(ricettaService, eventoService, utenteService);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}