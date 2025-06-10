package domain.ricette;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe Ricetta del dominio.
 */
public class RicettaTest {

    private Ricetta ricetta;

    @BeforeEach
    public void setup() {
        ricetta = new Ricetta(1, "Pasta al Pomodoro");
    }

    @Test
    @DisplayName("Test costruttore con ID e nome")
    public void testCostruttore() {
        assertEquals(1, ricetta.getId());
        assertEquals("Pasta al Pomodoro", ricetta.getNome());
        assertEquals("Bozza", ricetta.getStato()); // valore di default
        assertEquals(0, ricetta.getTempoPreparazione()); // valore di default
        assertFalse(ricetta.isInUso()); // valore di default
        assertTrue(ricetta.getTags().isEmpty());
    }

    @Test
    @DisplayName("Test costruttore vuoto")
    public void testCostruttoreVuoto() {
        Ricetta ricettaVuota = new Ricetta();
        assertEquals(0, ricettaVuota.getId());
        assertNull(ricettaVuota.getNome());
        assertEquals("Bozza", ricettaVuota.getStato());
    }

    @Test
    @DisplayName("Test setters e getters")
    public void testSettersGetters() {
        ricetta.setNome("Risotto ai Funghi");
        ricetta.setDescrizione("Un cremoso risotto con funghi porcini");
        ricetta.setStato("Pubblicata");
        ricetta.setTempoPreparazione(45);
        ricetta.setInUso(true);

        assertEquals("Risotto ai Funghi", ricetta.getNome());
        assertEquals("Un cremoso risotto con funghi porcini", ricetta.getDescrizione());
        assertEquals("Pubblicata", ricetta.getStato());
        assertEquals(45, ricetta.getTempoPreparazione());
        assertTrue(ricetta.isInUso());
    }

    @Test
    @DisplayName("Test gestione tags")
    public void testGestioneTags() {
        Tag tag1 = new Tag("Vegetariano");
        Tag tag2 = new Tag("Italiano");

        ricetta.aggiungiTag(tag1);
        ricetta.aggiungiTag(tag2);

        assertEquals(2, ricetta.getTags().size());
        assertTrue(ricetta.getTags().contains(tag1));
        assertTrue(ricetta.getTags().contains(tag2));
    }

    @Test
    @DisplayName("Test equals e hashCode")
    public void testEqualsHashCode() {
        Ricetta ricetta1 = new Ricetta(1, "Test");
        Ricetta ricetta2 = new Ricetta(1, "Test Diverso");
        Ricetta ricetta3 = new Ricetta(2, "Test");

        // Due ricette con stesso ID sono uguali
        assertEquals(ricetta1, ricetta2);
        assertEquals(ricetta1.hashCode(), ricetta2.hashCode());

        // Due ricette con ID diverso non sono uguali
        assertNotEquals(ricetta1, ricetta3);
        assertNotEquals(ricetta1.hashCode(), ricetta3.hashCode());
    }

    @Test
    @DisplayName("Test toString")
    public void testToString() {
        ricetta.setStato("Pubblicata");
        String toString = ricetta.toString();
        
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("nome='Pasta al Pomodoro'"));
        assertTrue(toString.contains("stato='Pubblicata'"));
    }

    @Test
    @DisplayName("Test stati validi")
    public void testStatiValidi() {
        String[] statiValidi = {"Bozza", "Pubblicata", "Archiviata"};
        
        for (String stato : statiValidi) {
            ricetta.setStato(stato);
            assertEquals(stato, ricetta.getStato());
        }
    }

    @Test
    @DisplayName("Test tempo preparazione valido")
    public void testTempoPreparazione() {
        ricetta.setTempoPreparazione(30);
        assertEquals(30, ricetta.getTempoPreparazione());
        
        ricetta.setTempoPreparazione(0);
        assertEquals(0, ricetta.getTempoPreparazione());
    }
} 