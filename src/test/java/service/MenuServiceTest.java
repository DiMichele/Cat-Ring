package service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import domain.menu.Menu;
import domain.menu.SezioneMenu;
import domain.ricette.Ricetta;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MenuServiceTest {

    private MenuService menuService;
    private int initialMenusCount;
    
    @BeforeEach
    public void setup() {
        menuService = new MenuService();
        initialMenusCount = menuService.getMenus().size();
    }
    
    @Test
    @DisplayName("Test creazione nuovo menu")
    public void testCreaNuovoMenu() {
        // Act
        Menu risultato = menuService.creaNuovoMenu();
        
        // Assert
        assertNotNull(risultato);
        assertTrue(risultato.getId() > 0);
        assertEquals(initialMenusCount + 1, menuService.getMenus().size());
        assertEquals(risultato, menuService.getMenus().get(menuService.getMenus().size() - 1));
    }
    
    @Test
    @DisplayName("Test getMenuById con menu esistente")
    public void testGetMenuByIdEsistente() {
        // Arrange
        Menu menu = menuService.creaNuovoMenu();
        int menuId = menu.getId();
        
        // Act
        Menu risultato = menuService.getMenuById(menuId);
        
        // Assert
        assertNotNull(risultato);
        assertEquals(menuId, risultato.getId());
    }
    
    @Test
    @DisplayName("Test getMenuById con menu non esistente")
    public void testGetMenuByIdNonEsistente() {
        // Act
        Menu risultato = menuService.getMenuById(99999);
        
        // Assert
        assertNull(risultato);
    }
    
    @Test
    @DisplayName("Test creaSezioneMenu")
    public void testCreaSezioneMenu() {
        // Act
        SezioneMenu sezione = menuService.creaSezioneMenu("Antipasti");
        
        // Assert
        assertNotNull(sezione);
        assertTrue(sezione.getId() > 0);
        assertEquals("Antipasti", sezione.getNome());
    }
    
    @Test
    @DisplayName("Test creaSezioneMenu incrementa ID")
    public void testCreaSezioneMenuIncrementaId() {
        // Act
        SezioneMenu sezione1 = menuService.creaSezioneMenu("Antipasti");
        SezioneMenu sezione2 = menuService.creaSezioneMenu("Primi");
        
        // Assert
        assertTrue(sezione1.getId() > 0);
        assertTrue(sezione2.getId() > sezione1.getId(), "L'ID della seconda sezione dovrebbe essere maggiore della prima");
    }
    
    @Test
    @DisplayName("Test aggiornaTitoloMenu - titolo valido")
    public void testAggiornaTitoloMenuValido() {
        // Arrange
        Menu menu = menuService.creaNuovoMenu();
        menu.setTitolo("Titolo Originale");
        
        // Act - Uso un titolo unico con timestamp per evitare conflitti
        String titoloUnico = "Titolo Unico Test " + System.currentTimeMillis();
        boolean risultato = menuService.aggiornaTitoloMenu(menu, titoloUnico);
        
        // Assert
        assertTrue(risultato, "L'aggiornamento dovrebbe riuscire con titolo valido");
        assertEquals(titoloUnico, menu.getTitolo());
    }
    
    @Test
    @DisplayName("Test aggiornaTitoloMenu - titolo duplicato")
    public void testAggiornaTitoloMenuDuplicato() {
        // Arrange
        Menu menu1 = menuService.creaNuovoMenu();
        menu1.setTitolo("Titolo Esistente");
        
        Menu menu2 = menuService.creaNuovoMenu();
        menu2.setTitolo("Altro Titolo");
        
        // Act
        boolean risultato = menuService.aggiornaTitoloMenu(menu2, "Titolo Esistente");
        
        // Assert
        assertFalse(risultato, "L'aggiornamento dovrebbe fallire con titolo duplicato");
        assertEquals("Altro Titolo", menu2.getTitolo()); // Titolo deve rimanere invariato
    }
    
    @Test
    @DisplayName("Test creaNuovoMenuConTitolo - titolo unico")
    public void testCreaNuovoMenuConTitoloUnico() {
        // Act
        Menu menu = menuService.creaNuovoMenuConTitolo("Menu Unico");
        
        // Assert
        assertNotNull(menu);
        assertEquals("Menu Unico", menu.getTitolo());
        assertEquals("Bozza", menu.getStato());
    }
    
    @Test
    @DisplayName("Test creaNuovoMenuConTitolo - titolo duplicato")
    public void testCreaNuovoMenuConTitoloDuplicato() {
        // Arrange
        menuService.creaNuovoMenuConTitolo("Titolo Duplicato");
        
        // Act
        Menu menuDuplicato = menuService.creaNuovoMenuConTitolo("Titolo Duplicato");
        
        // Assert
        assertNull(menuDuplicato, "Non dovrebbe creare menu con titolo duplicato");
    }
    
    @Test
    @DisplayName("Test pubblicaMenu")
    public void testPubblicaMenu() {
        // Arrange
        Menu menu = menuService.creaNuovoMenu();
        List<String> destinatari = Arrays.asList("client@example.com");
        
        // Act
        menuService.pubblicaMenu(menu, "PDF", destinatari);
        
        // Assert
        assertEquals("Pubblicato", menu.getStato());
    }
    
    @Test
    @DisplayName("Test eliminaMenu")
    public void testEliminaMenu() {
        // Arrange
        Menu menu = menuService.creaNuovoMenu();
        int menuCount = menuService.getMenus().size();
        
        // Act
        menuService.eliminaMenu(menu);
        
        // Assert
        assertEquals(menuCount - 1, menuService.getMenus().size());
        assertNull(menuService.getMenuById(menu.getId()));
    }
    
    @Test
    @DisplayName("Test flusso completo creazione menu")
    public void testFlussoCompletoMenu() {
        // Step 1: Creazione nuovo menu
        Menu menu = menuService.creaNuovoMenu();
        assertNotNull(menu);
        assertEquals("Bozza", menu.getStato());
        
        // Step 2: Aggiunta titolo
        menu.setTitolo("Menu Primavera");
        assertEquals("Menu Primavera", menu.getTitolo());
        
        // Step 3: Definizione delle sezioni
        List<String> nomiSezioni = Arrays.asList("Antipasti", "Primi", "Secondi", "Dolci");
        menu.definisciSezioni(nomiSezioni);
        assertEquals(4, menu.getSezioni().size());
        
        // Step 4: Inserimento ricette nelle sezioni
        Ricetta ricettaAntipasto = new Ricetta(1, "Bruschetta al pomodoro");
        Ricetta ricettaPrimo = new Ricetta(2, "Pasta al pesto");
        
        menu.inserisciRicetta(ricettaAntipasto, menu.getSezioni().get(0));
        menu.inserisciRicetta(ricettaPrimo, menu.getSezioni().get(1));
        
        assertEquals(1, menu.getSezioni().get(0).getRicette().size());
        assertEquals(1, menu.getSezioni().get(1).getRicette().size());
        
        // Step 6: Aggiunta di note e informazioni
        menu.annotaInformazioni("Menu ideale per occasioni speciali. Prevede piatti freschi e leggeri.");
        assertEquals("Menu ideale per occasioni speciali. Prevede piatti freschi e leggeri.", menu.getNote());
        
        // Step 7: Pubblicazione del menu
        List<String> destinatari = Arrays.asList("cliente@example.com", "organizzatore@example.com");
        menuService.pubblicaMenu(menu, "PDF", destinatari);
        
        assertEquals("Pubblicato", menu.getStato());
    }

    @Test
    @DisplayName("Test modificaRicetta")
    public void testModificaRicetta() {
        // Arrange
        Menu menu = menuService.creaNuovoMenu();
        menu.setTitolo("Menu Test");
        List<String> sezioni = Arrays.asList("Antipasti", "Primi");
        menu.definisciSezioni(sezioni);
        
        Ricetta ricettaOriginale = new Ricetta(1, "Bruschetta");
        menu.inserisciRicetta(ricettaOriginale, menu.getSezioni().get(0));
        
        Ricetta nuovaRicetta = new Ricetta(2, "Bruschetta Gourmet");
        
        // Act
        boolean risultato = menuService.modificaRicetta(menu, ricettaOriginale, nuovaRicetta);
        
        // Assert
        assertTrue(risultato, "La modifica della ricetta dovrebbe riuscire");
        assertEquals(1, menu.getSezioni().get(0).getRicette().size());
        // Ora la ricetta è wrapped in RicettaInMenu
        assertEquals(nuovaRicetta, menu.getSezioni().get(0).getRicette().get(0).getRicettaOriginale());
    }

    @Test
    @DisplayName("Test modificaRicetta con ricetta non presente")
    public void testModificaRicettaNonPresente() {
        // Arrange
        Menu menu = menuService.creaNuovoMenu();
        menu.setTitolo("Menu Test");
        List<String> sezioni = Arrays.asList("Antipasti", "Primi");
        menu.definisciSezioni(sezioni);
        
        Ricetta ricettaNelMenu = new Ricetta(1, "Bruschetta");
        menu.inserisciRicetta(ricettaNelMenu, menu.getSezioni().get(0));
        
        Ricetta ricettaNonPresente = new Ricetta(3, "Pasta alla Carbonara");
        Ricetta nuovaRicetta = new Ricetta(2, "Bruschetta Gourmet");
        
        // Act
        boolean risultato = menuService.modificaRicetta(menu, ricettaNonPresente, nuovaRicetta);
        
        // Assert
        assertFalse(risultato, "La modifica dovrebbe fallire per ricetta non presente");
        assertEquals(1, menu.getSezioni().get(0).getRicette().size());
        // Ora la ricetta è wrapped in RicettaInMenu
        assertEquals(ricettaNelMenu, menu.getSezioni().get(0).getRicette().get(0).getRicettaOriginale());
    }
} 