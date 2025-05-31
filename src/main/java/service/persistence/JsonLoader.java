package service.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Rappresenta un loader per caricare dati da file JSON nel sistema di catering.
 * Ideale per prototipi e testing, permette di avere dati di esempio preconfigurati.
 */
public class JsonLoader {
    
    private static final Logger LOGGER = Logger.getLogger(JsonLoader.class.getName());
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .enable(SerializationFeature.INDENT_OUTPUT)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    
    /**
     * Carica dati da un file JSON nella cartella resources.
     *
     * @param jsonPath Percorso relativo al file JSON in resources (es. "data/ricette.json")
     * @param typeReference Riferimento al tipo per la deserializzazione
     * @return Lista degli oggetti caricati, o lista vuota in caso di errore
     */
    public static <T> List<T> loadFromResources(String jsonPath, TypeReference<List<T>> typeReference) {
        try (InputStream is = JsonLoader.class.getClassLoader().getResourceAsStream(jsonPath)) {
            if (is == null) {
                LOGGER.warning("File JSON non trovato: " + jsonPath);
                return Collections.emptyList();
            }
            
            return MAPPER.readValue(is, typeReference);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Errore nel caricamento del file JSON: " + jsonPath, e);
            return Collections.emptyList();
        }
    }
    
    /**
     * Carica dati generici da un file JSON nella cartella resources.
     *
     * @param jsonPath Percorso relativo al file JSON in resources (es. "data/utenti.json")
     * @param typeReference Riferimento al tipo per la deserializzazione
     * @return Oggetto caricato, o null in caso di errore
     */
    public static <T> T loadFromResourcesGeneric(String jsonPath, TypeReference<T> typeReference) {
        try (InputStream is = JsonLoader.class.getClassLoader().getResourceAsStream(jsonPath)) {
            if (is == null) {
                LOGGER.warning("File JSON non trovato: " + jsonPath);
                return null;
            }
            
            return MAPPER.readValue(is, typeReference);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Errore nel caricamento del file JSON: " + jsonPath, e);
            return null;
        }
    }

    /**
     * Carica dati da un file JSON nel filesystem.
     *
     * @param filePath Percorso assoluto o relativo al file JSON
     * @param typeReference Riferimento al tipo per la deserializzazione
     * @return Lista degli oggetti caricati, o lista vuota in caso di errore
     */
    public static <T> List<T> loadFromFile(String filePath, TypeReference<List<T>> typeReference) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                LOGGER.warning("File JSON non trovato: " + filePath);
                return Collections.emptyList();
            }
            
            return MAPPER.readValue(file, typeReference);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Errore nel caricamento del file JSON: " + filePath, e);
            return Collections.emptyList();
        }
    }
    
    /**
     * Carica dati generici da un file JSON nel filesystem.
     *
     * @param filePath Percorso assoluto o relativo al file JSON
     * @param typeReference Riferimento al tipo per la deserializzazione
     * @return Oggetto caricato, o null in caso di errore
     */
    public static <T> T loadFromFileGeneric(String filePath, TypeReference<T> typeReference) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                LOGGER.warning("File JSON non trovato: " + filePath);
                return null;
            }
            
            return MAPPER.readValue(file, typeReference);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Errore nel caricamento del file JSON: " + filePath, e);
            return null;
        }
    }

    /**
     * Salva dati in un file JSON.
     *
     * @param filePath Percorso assoluto o relativo dove salvare il file JSON
     * @param data Dati da salvare
     */
    public static <T> boolean saveToFile(String filePath, List<T> data) {
        try {
            File file = new File(filePath);
            // Crea le cartelle se non esistono
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            
            MAPPER.writeValue(file, data);
            return true;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Errore nel salvataggio del file JSON: " + filePath, e);
            return false;
        }
    }

    /**
     * Salva dati generici in un file JSON.
     *
     * @param filePath Percorso assoluto o relativo dove salvare il file JSON
     * @param data Dati da salvare (qualsiasi tipo)
     */
    public static boolean saveToFileGeneric(String filePath, Object data) {
        try {
            File file = new File(filePath);
            // Crea le cartelle se non esistono
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            
            MAPPER.writeValue(file, data);
            return true;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Errore nel salvataggio del file JSON: " + filePath, e);
            return false;
        }
    }
    
    /**
     * Inizializza un file JSON con dati di esempio se non esiste già.
     *
     * @param filePath Percorso al file JSON
     * @param defaultData Dati di default da scrivere se il file non esiste
     */
    public static <T> void initializeFileIfNotExists(String filePath, List<T> defaultData) {
        File file = new File(filePath);
        if (!file.exists()) {
            saveToFile(filePath, defaultData);
            LOGGER.info("Creato file JSON con dati di esempio: " + filePath);
        }
    }
} 