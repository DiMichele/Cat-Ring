package domain.ricette;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe Tag del dominio.
 */
public class TagTest {

    private Tag tag;

    @BeforeEach
    public void setup() {
        tag = new Tag("Vegetariano");
    }

    @Test
    @DisplayName("Test costruttore con nome")
    public void testCostruttore() {
        assertEquals("Vegetariano", tag.getNome());
    }

    @Test
    @DisplayName("Test costruttore vuoto")
    public void testCostruttoreVuoto() {
        Tag tagVuoto = new Tag();
        assertNull(tagVuoto.getNome());
    }

    @Test
    @DisplayName("Test setters e getters")
    public void testSettersGetters() {
        tag.setNome("Vegano");
        assertEquals("Vegano", tag.getNome());
    }

    @Test
    @DisplayName("Test equals e hashCode")
    public void testEqualsHashCode() {
        Tag tag1 = new Tag("Italiano");
        Tag tag2 = new Tag("Italiano");
        Tag tag3 = new Tag("Francese");

        // Due tag con stesso nome sono uguali
        assertEquals(tag1, tag2);
        assertEquals(tag1.hashCode(), tag2.hashCode());

        // Due tag con nome diverso non sono uguali
        assertNotEquals(tag1, tag3);
        assertNotEquals(tag1.hashCode(), tag3.hashCode());

        // Test con null
        assertNotEquals(tag1, null);

        // Test con oggetto di tipo diverso
        assertNotEquals(tag1, "Italiano");
    }

    @Test
    @DisplayName("Test toString")
    public void testToString() {
        String toString = tag.toString();
        assertTrue(toString.contains("Vegetariano"));
    }

    @Test
    @DisplayName("Test tag con caratteri speciali")
    public void testTagCaratteriSpeciali() {
        Tag tagSpeciale = new Tag("Bio & Naturale");
        assertEquals("Bio & Naturale", tagSpeciale.getNome());
    }

    @Test
    @DisplayName("Test tag con spazi")
    public void testTagConSpazi() {
        Tag tagConSpazi = new Tag("Senza Glutine");
        assertEquals("Senza Glutine", tagConSpazi.getNome());
    }
} 