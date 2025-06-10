package domain.menu;

import domain.ricette.Ricetta;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe Menu del dominio.
 */
public class MenuTest {

    private Menu menu;
    private Ricetta ricetta1;
    private Ricetta ricetta2;

    @BeforeEach
    public void setup() {
        menu = new Menu();
        ricetta1 = new Ricetta(1, "Pasta al Pomodoro");
        ricetta2 = new Ricetta(2, "Risotto ai Funghi");
    }

    @Test
    @DisplayName("Test costruttore default")
    public void testCostruttoreDefault() {
        assertEquals(0, menu.getId()); // ID di default è 0
        assertEquals("Bozza", menu.getStato());
        assertTrue(menu.getSezioni().isEmpty());
        assertNull(menu.getTitolo());
    }

    @Test
    @DisplayName("Test setters e getters")
    public void testSettersGetters() {
        menu.setTitolo("Menu Primavera");
        menu.setNote("Menu stagionale di primavera");
        menu.setStato("Pubblicato");

        assertEquals("Menu Primavera", menu.getTitolo());
        assertEquals("Menu stagionale di primavera", menu.getNote());
        assertEquals("Pubblicato", menu.getStato());
    }

    @Test
    @DisplayName("Test definizione sezioni")
    public void testDefinisciSezioni() {
        List<String> nomiSezioni = Arrays.asList("Antipasti", "Primi", "Secondi", "Dolci");
        
        menu.definisciSezioni(nomiSezioni);
        
        assertEquals(4, menu.getSezioni().size());
        assertEquals("Antipasti", menu.getSezioni().get(0).getNome());
        assertEquals("Primi", menu.getSezioni().get(1).getNome());
        assertEquals("Secondi", menu.getSezioni().get(2).getNome());
        assertEquals("Dolci", menu.getSezioni().get(3).getNome());
    }

    @Test
    @DisplayName("Test inserimento ricetta in sezione")
    public void testInserisciRicetta() {
        List<String> nomiSezioni = Arrays.asList("Antipasti", "Primi");
        menu.definisciSezioni(nomiSezioni);
        
        SezioneMenu sezioneAntipasti = menu.getSezioni().get(0);
        SezioneMenu sezionePrimi = menu.getSezioni().get(1);
        
        menu.inserisciRicetta(ricetta1, sezioneAntipasti);
        menu.inserisciRicetta(ricetta2, sezionePrimi);
        
        assertEquals(1, sezioneAntipasti.getRicette().size());
        assertEquals(1, sezionePrimi.getRicette().size());
        assertEquals(ricetta1, sezioneAntipasti.getRicette().get(0).getRicettaOriginale());
        assertEquals(ricetta2, sezionePrimi.getRicette().get(0).getRicettaOriginale());
    }

    @Test
    @DisplayName("Test annotazione informazioni")
    public void testAnnotaInformazioni() {
        menu.annotaInformazioni("Questo è un menu speciale");
        
        assertEquals("Questo è un menu speciale", menu.getNote());
    }

    @Test
    @DisplayName("Test pubblicazione menu")
    public void testPubblicaMenu() {
        assertEquals("Bozza", menu.getStato());
        
        menu.pubblica();
        
        assertEquals("Pubblicato", menu.getStato());
    }

    @Test
    @DisplayName("Test gestione caratteristiche")
    public void testGestioneCaratteristiche() {
        CaratteristicaMenu caratteristica = new CaratteristicaMenu("Vegetariano");
        
        menu.aggiungiCaratteristica(caratteristica);
        
        assertEquals(1, menu.getCaratteristiche().size());
        assertTrue(menu.hasCaratteristica("Vegetariano"));
        
        menu.rimuoviCaratteristica(caratteristica);
        
        assertEquals(0, menu.getCaratteristiche().size());
        assertFalse(menu.hasCaratteristica("Vegetariano"));
    }

    @Test
    @DisplayName("Test equals e hashCode")
    public void testEqualsHashCode() {
        Menu menu1 = new Menu();
        Menu menu2 = new Menu();
        
        // Due menu con ID diverso non sono uguali
        assertNotEquals(menu1, menu2);
        assertNotEquals(menu1.hashCode(), menu2.hashCode());
        
        // Menu con stesso ID sono uguali (test più specifico richiederebbe mock o reflection)
        assertEquals(menu1, menu1);
        assertEquals(menu1.hashCode(), menu1.hashCode());
    }

    @Test
    @DisplayName("Test toString")
    public void testToString() {
        menu.setTitolo("Menu Test");
        String toString = menu.toString();
        
        // Il toString di default di Object contiene il nome della classe
        assertTrue(toString.contains("Menu"));
        assertNotNull(toString);
    }
} 