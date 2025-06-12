# Cat-Ring - Sistema di Gestione Catering

Sistema completo per la gestione di eventi di catering sviluppato in Java con JavaFX.

## Caratteristiche Principali

- **Gestione Menu**: Creazione e modifica di menu personalizzati per eventi
- **Gestione Ricette**: Database completo di ricette con ingredienti e procedure
- **Gestione Eventi**: Organizzazione di eventi con dettagli completi
- **Gestione Compiti**: Assegnazione e monitoraggio compiti di cucina
- **Sistema Feedback**: Raccolta e gestione feedback degli eventi
- **Gestione Turni**: Organizzazione turni di lavoro del personale
- **Export Excel**: Esportazione dati in formato Excel

## Requisiti di Sistema

- **Java**: JDK 11 o superiore
- **Maven**: 3.6 o superiore
- **Sistema Operativo**: Windows, macOS, Linux

## Installazione

### 1. Clona il Repository
```bash
git clone https://github.com/DiMichele/Cat-Ring.git
cd Cat-Ring
```

### 2. Compila il Progetto
```bash
mvn clean compile
```

### 3. Scarica le Dipendenze JavaFX
```bash
mvn dependency:copy-dependencies
```

## Avvio dell'Applicazione

### Metodo Rapido (Consigliato)

#### Windows
```bash
.\avvia-semplice.bat
```

#### macOS/Linux
```bash
./avvia-semplice.sh
```

### Metodo Manuale
```bash
mvn clean javafx:run
```

## Sviluppo con VS Code

Il progetto include la configurazione per il debug in VS Code:

1. Apri il progetto in VS Code
2. Assicurati di aver eseguito `mvn compile dependency:copy-dependencies`
3. Premi F5 per avviare il debug

## Struttura del Progetto

```
src/
├── main/
│   ├── java/
│   │   ├── domain/          # Modelli di dominio
│   │   ├── service/         # Logica di business
│   │   └── ui/              # Interfaccia utente
│   └── resources/
│       ├── data/            # File JSON con dati
│       └── views/           # File FXML per UI
└── test/
    └── java/                # Test unitari
```

## Dati di Test

Il sistema include dati realistici per il testing:

- **4 Eventi**: Matrimonio, Conferenza, Gala, Workshop
- **15 Compiti**: Con tutti gli stati (Da iniziare, In corso, Completato, Bloccato)
- **12 Feedback**: Varie tipologie di valutazioni
- **8 Utenti**: Chef e cuochi con competenze diverse
- **6 Turni**: Organizzazione completa del personale

## Test

Esegui tutti i test:
```bash
mvn test
```

Il progetto include 51 test che verificano:
- Isolamento completo dei dati di test
- Funzionalità di tutti i servizi
- Integrità dei dati JSON

## Tecnologie Utilizzate

- **Java 11+**: Linguaggio principale
- **JavaFX 21**: Framework UI
- **Jackson**: Serializzazione JSON
- **Apache POI**: Export Excel
- **JUnit 5**: Framework di testing
- **Mockito**: Mocking per test
- **Maven**: Build e gestione dipendenze

## Funzionalità Dettagliate

### Gestione Menu
- Creazione menu personalizzati
- Associazione ricette a sezioni
- Calcolo automatico porzioni
- Gestione caratteristiche speciali

### Gestione Compiti
- Assegnazione compiti per evento
- Monitoraggio stati (Da iniziare, In corso, Completato, Bloccato)
- Gestione priorità (1-5)
- Timeline di esecuzione

### Sistema Feedback
- Raccolta valutazioni eventi
- Analisi performance cuochi
- Storico feedback per miglioramenti

### Export Dati
- Esportazione compiti in Excel
- Report dettagliati per evento
- Formattazione professionale

## Risoluzione Problemi

### Problemi di Avvio
1. Verifica versione Java: `java -version`
2. Verifica Maven: `mvn -version`
3. Ricompila: `mvn clean compile`

## Contribuire

1. Fork del repository
2. Crea branch per feature: `git checkout -b feature/nuova-funzionalita`
3. Commit modifiche: `git commit -m "Aggiunta nuova funzionalità"`
4. Push branch: `git push origin feature/nuova-funzionalita`
5. Crea Pull Request

## Licenza

Progetto sviluppato per scopi educativi.

## Contatti

- **Sviluppatore**: DiMichele
- **Email**: digennaromichele99@gmail.com
- **Repository**: https://github.com/DiMichele/Cat-Ring

---

*Sistema di gestione catering completo e professionale per organizzazione eventi.* 