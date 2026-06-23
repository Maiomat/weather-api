# Weather API

Applicazione REST sviluppata con Java e Spring Boot per raccogliere, memorizzare e analizzare dati meteorologici relativi a diverse città italiane.

L'applicazione integra il servizio esterno Open-Meteo, salva periodicamente le rilevazioni in un database H2 e mette a disposizione API REST per:

* registrare nuove città;
* avviare manualmente la raccolta meteorologica;
* consultare le medie precalcolate tramite CAP;
* consultare le medie di tutte le città monitorate.

La documentazione delle API è disponibile tramite OpenAPI e Swagger UI.

## Funzionalità principali

* Raccolta dei dati meteorologici da Open-Meteo.
* Inizializzazione di cinque città italiane:

  * Firenze
  * Roma
  * Milano
  * Napoli
  * Torino
* Inserimento dinamico di nuove città.
* Identificazione univoca delle località tramite CAP.
* Possibilità di gestire località con lo stesso nome ma CAP differenti.
* Validazione dei dati ricevuti dalle API.
* Raccolta automatica tramite scheduler ogni 15 minuti.
* Raccolta manuale eseguita in background.
* Risposta immediata `202 Accepted` all'avvio della raccolta manuale.
* Protezione contro raccolte simultanee.
* Persistenza delle rilevazioni meteorologiche.
* Prevenzione delle rilevazioni duplicate per città e timestamp.
* Aggiornamento incrementale della temperatura media e della velocità media del vento.
* Restituzione delle unità di misura associate ai valori.
* Gestione centralizzata degli errori HTTP.
* Logging degli errori del provider esterno.
* Documentazione interattiva tramite OpenAPI e Swagger UI.
* Test unitari dei principali componenti applicativi.
* Esecuzione tramite Docker.

## Tecnologie utilizzate

* Java 21
* Spring Boot 4.1.0
* Spring Web MVC
* Spring Data JPA
* Spring Scheduling
* Spring Framework RestClient
* Bean Validation
* Springdoc OpenAPI
* Swagger UI
* H2 Database
* Maven
* JUnit 5
* Mockito
* Docker

## Architettura

Il progetto segue una struttura a livelli:

```text
Controller / Scheduler
          ↓
WeatherCollectionCoordinator
          ↓
Service
          ↓
Repository / Client esterno
          ↓
Database H2 / Open-Meteo API
```

### Controller

I controller espongono gli endpoint REST per:

* registrare nuove città;
* avviare la raccolta meteorologica;
* consultare le medie tramite CAP;
* consultare le medie di tutte le città.

### Service

I service contengono la logica applicativa.

In particolare:

* `CityService` gestisce l'inserimento delle città e verifica l'unicità del CAP;
* `WeatherCollectionService` coordina le chiamate a Open-Meteo per tutte le città;
* `WeatherMeasurementService` salva le nuove rilevazioni e aggiorna le medie;
* `WeatherStatisticsService` restituisce le statistiche precalcolate;
* `WeatherCollectionCoordinator` gestisce l'esecuzione asincrona e impedisce raccolte sovrapposte.

### Repository

I repository gestiscono l'accesso al database tramite Spring Data JPA.

### Client esterno

`OpenMeteoClient` gestisce la comunicazione HTTP con Open-Meteo e converte la risposta JSON in oggetti Java.

### Scheduler

`WeatherCollectionScheduler` avvia automaticamente la raccolta ogni 15 minuti.

Lo scheduler utilizza lo stesso coordinatore della raccolta manuale, evitando che le due modalità vengano eseguite contemporaneamente.

### Raccolta asincrona

La raccolta manuale viene affidata a un `TaskExecutor` dedicato.

Il controller restituisce immediatamente `202 Accepted`, mentre il processo prosegue in background.

Un `AtomicBoolean` mantiene lo stato della raccolta e impedisce l'avvio di una seconda esecuzione mentre la precedente è ancora attiva.

## Modello dei dati

### City

L'entità `City` contiene:

* nome;
* CAP;
* latitudine;
* longitudine;
* temperatura media;
* velocità media del vento;
* unità di misura;
* numero di rilevazioni utilizzate.

Nel modello applicativo, il CAP è l'identificatore funzionale univoco di ciascuna località monitorata.

Il nome è un attributo descrittivo e può essere condiviso da più località con CAP differenti.

L'entità mantiene comunque un identificativo numerico interno, utilizzato da JPA e dalle relazioni con le rilevazioni meteorologiche.

Il CAP viene rappresentato come stringa per preservare eventuali zeri iniziali.

Nel modello semplificato del progetto viene associato un CAP rappresentativo a ogni località. Nella realtà, il rapporto tra comuni, località e CAP può essere più complesso.

### WeatherMeasurement

L'entità `WeatherMeasurement` rappresenta una singola rilevazione ricevuta da Open-Meteo.

La combinazione:

```text
city_id + measured_at
```

è univoca.

Questo impedisce che più chiamate effettuate nello stesso intervallo temporale salvino la stessa rilevazione più volte.

## Calcolo incrementale delle medie

Le medie non vengono calcolate quando viene eseguita una richiesta `GET`.

Quando viene salvata una nuova rilevazione, l'applicazione aggiorna immediatamente:

* temperatura media;
* velocità media del vento;
* numero dei campioni.

La formula utilizzata è:

```text
nuova media =
media precedente
+ (nuovo valore - media precedente)
  / nuovo numero di campioni
```

In questo modo la consultazione delle statistiche non richiede di recuperare ed elaborare l'intero storico delle rilevazioni.

Una rilevazione duplicata non viene salvata e non modifica né le medie né il numero dei campioni.

## Prerequisiti

Per eseguire il progetto in locale sono necessari:

* Java 21
* Docker o Docker Desktop, solo per l'esecuzione tramite container

Non è necessario installare Maven separatamente, poiché il progetto include il Maven Wrapper.

## Configurazione

La configurazione principale si trova nel file:

```text
src/main/resources/application.yml
```

L'applicazione utilizza:

* un database H2 in memoria;
* Open-Meteo come provider esterno;
* uno scheduler configurato per raccogliere i dati ogni 15 minuti.

Estratto della configurazione principale:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:weatherdb
    driver-class-name: org.h2.Driver
    username: sa
    password: ""

  jpa:
    hibernate:
      ddl-auto: update

  h2:
    console:
      enabled: true
      path: /h2-console

weather:
  api:
    base-url: "https://api.open-meteo.com"

  collection:
    interval-ms: 900000
```

Il database H2 è in memoria, quindi i dati vengono eliminati quando l'applicazione viene arrestata.

## Avvio in locale

Dalla cartella principale del progetto eseguire:

### Windows

```powershell
.\mvnw.cmd spring-boot:run
```

### Linux o macOS

```
./mvnw spring-boot:run
```

L'applicazione sarà disponibile all'indirizzo:

```text
http://localhost:8080
```

## Avvio tramite Docker

Creare l'immagine Docker:

```
docker build -t weather-api .
```

Avviare il container:

```
docker run --rm --name weather-api-container -p 8080:8080 weather-api
```

L'applicazione sarà disponibile all'indirizzo:

```text
http://localhost:8080
```

Per arrestare il container utilizzare `Ctrl + C`.

L'applicazione non deve essere avviata contemporaneamente in locale e tramite Docker sulla stessa porta `8080`.

## Console H2

Con l'applicazione avviata, la console H2 è raggiungibile all'indirizzo:

```text
http://localhost:8080/h2-console
```

Parametri di connessione:

```text
JDBC URL: jdbc:h2:mem:weatherdb
User Name: sa
Password: lasciare vuoto
```

## OpenAPI e Swagger UI

La documentazione interattiva delle API è disponibile su:

```text
http://localhost:8080/swagger-ui.html
```

La specifica OpenAPI in formato JSON è disponibile su:

```text
http://localhost:8080/v3/api-docs
```

Swagger UI permette di:

* consultare gli endpoint disponibili;
* visualizzare parametri e DTO;
* controllare i codici HTTP documentati;
* modificare i corpi JSON di esempio;
* eseguire richieste reali tramite il pulsante `Try it out`.

Le operazioni eseguite da Swagger modificano realmente lo stato dell'applicazione.

## API REST

### Inserire una nuova città

Registra una nuova città da monitorare.

```http
POST /api/v1/cities
```

Corpo della richiesta:

```json
{
  "name": "Salerno",
  "postalCode": "84121",
  "latitude": 40.6824,
  "longitude": 14.7681
}
```

Esempio con `curl`:

```
curl -X POST http://localhost:8080/api/v1/cities \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Salerno",
    "postalCode": "84121",
    "latitude": 40.6824,
    "longitude": 14.7681
  }'
```

Risposta:

```text
HTTP 201 Created
```

Esempio di risposta:

```json
{
  "id": 6,
  "name": "Salerno",
  "postalCode": "84121",
  "latitude": 40.6824,
  "longitude": 14.7681,
  "averageTemperature": 0.0,
  "averageWindSpeed": 0.0,
  "measurementsCount": 0
}
```

Possibili errori:

* `400 Bad Request` se i dati non sono validi;
* `409 Conflict` se il CAP è già presente.

È possibile registrare più località con lo stesso nome, purché abbiano CAP differenti.

---

### Avviare manualmente la raccolta

Avvia in background la raccolta dei dati meteorologici per tutte le città configurate.

```http
POST /api/v1/weather/measurements/collect
```

Esempio con `curl`:

```
curl -X POST http://localhost:8080/api/v1/weather/measurements/collect
```

Esempio con PowerShell:

```powershell
Invoke-RestMethod `
  -Method Post `
  -Uri "http://localhost:8080/api/v1/weather/measurements/collect"
```

Risposta:

```text
HTTP 202 Accepted
```

Il codice `202` indica che la richiesta è stata accettata e che la raccolta continuerà in background.

Se una raccolta è già in corso:

```text
HTTP 409 Conflict
```

---

### Consultare le medie tramite CAP

Restituisce le medie meteorologiche precalcolate della città identificata dal CAP.

```http
GET /api/v1/weather/averages/{postalCode}
```

Esempio:

```
curl http://localhost:8080/api/v1/weather/averages/50121
```

Risposta di esempio:

```json
{
  "city": "Firenze",
  "postalCode": "50121",
  "sampleCount": 3,
  "averageTemperature": 24.5,
  "temperatureUnit": "°C",
  "averageWindSpeed": 11.7,
  "windSpeedUnit": "km/h"
}
```

Il campo `sampleCount` indica il numero di rilevazioni utilizzate per aggiornare le medie.

---

### Consultare le medie di tutte le città

Restituisce le medie delle città che dispongono di almeno una rilevazione meteorologica.

```http
GET /api/v1/weather/averages
```

Esempio:

```
curl http://localhost:8080/api/v1/weather/averages
```

Risposta di esempio:

```json
[
  {
    "city": "Firenze",
    "postalCode": "50121",
    "sampleCount": 3,
    "averageTemperature": 24.5,
    "temperatureUnit": "°C",
    "averageWindSpeed": 11.7,
    "windSpeedUnit": "km/h"
  },
  {
    "city": "Roma",
    "postalCode": "00184",
    "sampleCount": 3,
    "averageTemperature": 26.2,
    "temperatureUnit": "°C",
    "averageWindSpeed": 9.4,
    "windSpeedUnit": "km/h"
  }
]
```

## Gestione degli errori

L'applicazione utilizza `ProblemDetail` per produrre risposte HTTP strutturate.

### CAP inesistente

Richiesta:

```http
GET /api/v1/weather/averages/40121
```

Esempio di risposta:

```json
{
  "detail": "Città non trovata: 40121",
  "instance": "/api/v1/weather/averages/40121",
  "status": 404,
  "title": "City not found"
}
```

### Città senza rilevazioni

Se la città esiste ma non dispone ancora di rilevazioni, viene restituito:

```text
HTTP 404 Not Found
```

Esempio di risposta:

```json
{
  "detail": "Nessuna rilevazione meteo disponibile per la città: Firenze",
  "instance": "/api/v1/weather/averages/50121",
  "status": 404,
  "title": "Weather measurements not found"
}
```

### CAP duplicato

```text
HTTP 409 Conflict
```

```json
{
  "detail": "Esiste già una città con CAP: 84121",
  "status": 409,
  "title": "Postal code already exists"
}
```

### Validazione della richiesta

Esempio di errore:

```json
{
  "title": "Validation failed",
  "status": 400,
  "detail": "Uno o più campi della richiesta non sono validi",
  "errors": {
    "postalCode": "Il CAP deve contenere esattamente 5 cifre",
    "latitude": "La latitudine deve essere minore o uguale a 90"
  }
}
```

## Test

Il progetto contiene test unitari realizzati con JUnit 5 e Mockito.

Per eseguire tutti i test:

### Windows

```powershell
.\mvnw.cmd clean test
```

### Linux o macOS

```
./mvnw clean test
```

La suite contiene 13 test.

I test verificano principalmente:

* la creazione di una città valida;
* la normalizzazione di nome e CAP;
* l'accettazione di città con lo stesso nome e CAP differenti;
* il rifiuto di CAP duplicati;
* la lettura delle medie precalcolate tramite CAP;
* la gestione di un CAP inesistente;
* la gestione di una città senza rilevazioni;
* la conversione della risposta Open-Meteo in un'entità JPA;
* il salvataggio di una nuova rilevazione;
* l'aggiornamento incrementale delle medie;
* il rifiuto delle rilevazioni duplicate;
* la prosecuzione della raccolta quando il provider fallisce per una singola città;
* l'avvio asincrono della raccolta;
* il rifiuto di una seconda raccolta concorrente;
* il corretto caricamento del contesto Spring.

La maggior parte dei test è costituita da test unitari, nei quali repository, client Open-Meteo ed executor vengono simulati tramite Mockito. Questi test non dipendono dal database o dalla rete.

È inoltre presente un test dedicato al corretto caricamento del contesto Spring.

## Scelte progettuali

### Separazione a livelli

La logica è suddivisa tra controller, service, repository, coordinatore e client esterno.

Questo mantiene separate le responsabilità e rende il codice più facilmente modificabile e testabile.

### DTO dedicati

Le richieste e le risposte delle API utilizzano DTO dedicati.

Le entità JPA non vengono esposte direttamente dai controller.

### Validazione dei dati

L'inserimento di una città utilizza Bean Validation per controllare:

* nome obbligatorio;
* CAP composto da cinque cifre;
* latitudine compresa tra `-90` e `90`;
* longitudine compresa tra `-180` e `180`.

### Doppia protezione contro i duplicati

I duplicati vengono prevenuti sia a livello applicativo sia tramite vincoli del database.

Per le città viene controllata l'unicità del CAP.

Il controllo viene eseguito sia a livello applicativo sia tramite un vincolo univoco nel database. Il nome della città non è univoco e può essere ripetuto per località con CAP differenti.

Per le rilevazioni viene controllata la combinazione:

```text
città + timestamp Open-Meteo
```

Il controllo applicativo permette di gestire il caso normalmente, mentre il vincolo database protegge anche da eventuali condizioni di concorrenza.

### Timestamp del provider

Il timestamp utilizzato per identificare una rilevazione è quello restituito da Open-Meteo, non l'istante preciso in cui il client esegue la richiesta.

Durante i test è stato osservato che richieste ravvicinate possono restituire lo stesso timestamp. Per questo motivo la combinazione tra città e timestamp viene utilizzata per impedire il salvataggio ripetuto della stessa rilevazione.

### Medie precalcolate

Le medie vengono aggiornate quando viene acquisita una nuova rilevazione.

La richiesta di consultazione deve quindi soltanto leggere i valori presenti nella città, senza recuperare l'intero storico.

### Transazioni

Il salvataggio di una rilevazione e l'aggiornamento delle medie vengono eseguiti nella stessa transazione.

Se una delle operazioni fallisce, le modifiche vengono annullate insieme.

### Raccolta manuale e automatica

La raccolta può essere avviata:

* manualmente tramite endpoint REST;
* automaticamente tramite scheduler.

Entrambi i flussi condividono `WeatherCollectionCoordinator`, che impedisce esecuzioni sovrapposte.

### Gestione degli errori per singola città

Se la chiamata a Open-Meteo fallisce per una città, la raccolta continua per quelle successive.

Un errore temporaneo non interrompe quindi l'intero processo.

### Database H2

H2 in memoria è stato utilizzato per semplificare l'esecuzione e la valutazione del progetto.

I dati vengono eliminati quando l'applicazione viene arrestata.

### Docker multi-stage

Il Dockerfile utilizza una build multi-stage:

1. Maven e Java 21 compilano il progetto e generano il file JAR.
2. L'immagine finale contiene soltanto il runtime Java e il JAR dell'applicazione.

Questo evita di includere Maven e il codice sorgente nell'immagine utilizzata per l'esecuzione.

## Evoluzione cloud

L'applicazione è containerizzata tramite Docker e può costituire una base per un successivo deployment su una piattaforma cloud.

Per un utilizzo in produzione potrebbero essere introdotti:

* sostituzione di H2 con un database persistente gestito, come PostgreSQL;
* configurazione di URL, credenziali e intervalli tramite variabili d'ambiente;
* Spring Boot Actuator per health check, readiness e liveness probe;
* pubblicazione dell'immagine Docker in un container registry;
* pipeline CI/CD con GitHub Actions;
* esecuzione automatica di test, build e pubblicazione dell'immagine;
* deployment su Kubernetes o su un servizio gestito per container;
* logging, metriche e monitoraggio centralizzati;
* gestione sicura dei segreti tramite un secret manager;
* automazione degli smoke test Docker.

In un ambiente distribuito, l'`AtomicBoolean` utilizzato dal coordinatore protegge soltanto la singola istanza applicativa.

Con più repliche sarebbe necessario utilizzare un meccanismo condiviso, come:

* un lock distribuito basato su database o Redis;
* uno stato persistente dei job di raccolta;
* un sistema di messaggistica;
* uno scheduler distribuito.

Questo impedirebbe a più istanze di avviare contemporaneamente la stessa raccolta.

## Possibili miglioramenti

Tra gli ulteriori sviluppi futuri:

* utilizzo di PostgreSQL;
* test di integrazione con Testcontainers;
* filtri delle medie per intervallo temporale;
* paginazione e consultazione dello storico;
* endpoint per modificare o eliminare una città;
* geocoding automatico a partire dal CAP;
* modellazione più completa del rapporto tra comuni, località e CAP;
* autenticazione e autorizzazione delle API;
* metriche e monitoraggio;
* pipeline CI/CD;
* deploy su infrastruttura cloud.

## Note

Open-Meteo è utilizzato come provider esterno gratuito e non richiede una API key.

Poiché il database H2 è in memoria, i dati raccolti non vengono mantenuti dopo lo spegnimento dell'applicazione o del container.