# Weather API

Applicazione REST sviluppata con Java e Spring Boot per raccogliere, memorizzare e analizzare dati meteorologici relativi a diverse città italiane.

L'applicazione integra il servizio esterno Open-Meteo, salva periodicamente le rilevazioni in un database H2 e mette a disposizione endpoint REST per consultare le medie meteorologiche calcolate per ciascuna città.

## Funzionalità principali

* Raccolta dei dati meteorologici da Open-Meteo.
* Gestione iniziale di cinque città italiane:

  * Firenze
  * Roma
  * Milano
  * Napoli
  * Torino
* Raccolta manuale tramite endpoint REST.
* Raccolta automatica tramite scheduler.
* Persistenza delle rilevazioni meteorologiche.
* Calcolo della temperatura media e della velocità media del vento.
* Restituzione delle unità di misura associate ai valori.
* Gestione centralizzata degli errori HTTP.
* Logging degli errori del provider esterno.
* Test unitari dei principali service.
* Esecuzione dell'applicazione tramite Docker.

## Tecnologie utilizzate

* Java 21
* Spring Boot 4.1.0
* Spring Web MVC
* Spring Data JPA
* Spring Scheduling
* Spring Framework RestClient
* H2 Database
* Maven
* JUnit 5
* Mockito
* Docker

## Architettura

Il progetto segue una struttura a livelli:

```text
Controller
    ↓
Service
    ↓
Repository / Client esterno
    ↓
Database H2 / Open-Meteo API
```

### Controller

Espongono gli endpoint REST per avviare la raccolta dei dati e consultare le medie meteorologiche.

### Service

Contengono la logica applicativa, tra cui:

* raccolta delle informazioni meteorologiche;
* conversione della risposta Open-Meteo in un'entità JPA;
* salvataggio delle rilevazioni;
* calcolo delle medie;
* gestione dei casi di errore.

### Repository

Gestiscono l'accesso al database tramite Spring Data JPA.

### Client esterno

`OpenMeteoClient` si occupa della comunicazione HTTP con Open-Meteo e della conversione della risposta JSON in oggetti Java.

### Scheduler

`WeatherCollectionScheduler` richiama automaticamente il servizio di raccolta ogni 15 minuti.

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

## Avvio tramite Docker

Creare l'immagine Docker:

```
docker build -t weather-api .
```

Avviare il container:

```
docker run --rm --name weather-api-container -p 8080:8080 weather-api
```

L'applicazione sarà raggiungibile su:

```text
http://localhost:8080
```

Per arrestare il container utilizzare `Ctrl + C`.

## API REST

### Avviare manualmente la raccolta

Raccoglie i dati meteorologici correnti per tutte le città configurate e li salva nel database.

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
HTTP 204 No Content
```

---

### Consultare le medie di una città

Restituisce le medie meteorologiche calcolate utilizzando tutte le rilevazioni disponibili per la città richiesta.

```http
GET /api/v1/weather/averages/{cityName}
```

Esempio:

```
curl http://localhost:8080/api/v1/weather/averages/Firenze
```

Risposta di esempio:

```json
{
  "city": "Firenze",
  "sampleCount": 3,
  "averageTemperature": 24.5,
  "temperatureUnit": "°C",
  "averageWindSpeed": 11.7,
  "windSpeedUnit": "km/h"
}
```

Il campo `sampleCount` indica il numero di rilevazioni utilizzate per il calcolo delle medie.

---

### Consultare le medie di tutte le città

Restituisce le medie delle città per le quali sono presenti rilevazioni meteorologiche.

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
    "sampleCount": 3,
    "averageTemperature": 24.5,
    "temperatureUnit": "°C",
    "averageWindSpeed": 11.7,
    "windSpeedUnit": "km/h"
  },
  {
    "city": "Roma",
    "sampleCount": 3,
    "averageTemperature": 26.2,
    "temperatureUnit": "°C",
    "averageWindSpeed": 9.4,
    "windSpeedUnit": "km/h"
  }
]
```

## Gestione degli errori

L'applicazione utilizza risposte HTTP strutturate per rappresentare gli errori.

### Città inesistente

Richiesta:

```http
GET /api/v1/weather/averages/Bologna
```

Risposta:

```json
{
  "detail": "Città non trovata: Bologna",
  "instance": "/api/v1/weather/averages/Bologna",
  "status": 404,
  "title": "City not found"
}
```

### Città senza rilevazioni

Se la città esiste ma non dispone ancora di rilevazioni, viene restituita una risposta `404`:

```json
{
  "detail": "Nessuna rilevazione meteo disponibile per la città: Firenze",
  "instance": "/api/v1/weather/averages/Firenze",
  "status": 404,
  "title": "Weather measurements not found"
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

I test verificano principalmente:

* il calcolo corretto delle medie meteorologiche;
* la gestione di una città inesistente;
* la gestione di una città senza rilevazioni;
* la conversione della risposta esterna in un'entità JPA;
* il salvataggio delle rilevazioni nel repository;
* la prosecuzione della raccolta quando il provider fallisce per una singola città;
* il corretto caricamento del contesto Spring.

I repository e il client Open-Meteo vengono simulati tramite Mockito. In questo modo i test dei service non dipendono dal database, dalla rete o dall'avvio completo dell'applicazione.

## Scelte progettuali

### Separazione a livelli

La logica è stata suddivisa tra controller, service, repository e client esterno, in modo da mantenere separate le diverse responsabilità e rendere il codice più facilmente testabile.

### Client dedicato per Open-Meteo

La comunicazione con il provider esterno è gestita da `OpenMeteoClient`, evitando di inserire chiamate HTTP direttamente nei service o nei controller.

### Persistenza delle rilevazioni

Le risposte ricevute da Open-Meteo vengono salvate come rilevazioni meteorologiche. Le medie vengono calcolate utilizzando i dati raccolti nel tempo, invece di restituire soltanto il valore corrente del provider.

### Raccolta manuale e automatica

La stessa logica di raccolta può essere avviata:

* manualmente tramite endpoint REST;
* automaticamente tramite scheduler.

Entrambi i flussi utilizzano `WeatherCollectionService`, evitando la duplicazione della logica applicativa.

### Gestione degli errori per singola città

Se la chiamata a Open-Meteo fallisce per una città, la raccolta continua per le città successive. In questo modo un errore temporaneo non interrompe l'intero processo.

### Database H2

È stato utilizzato H2 in memoria per semplificare l'esecuzione e la valutazione del progetto. I dati vengono eliminati quando l'applicazione viene arrestata.

### Docker multi-stage

Il Dockerfile utilizza una build multi-stage:

1. Maven e Java 21 compilano il progetto e generano il file JAR.
2. L'immagine finale contiene soltanto il runtime Java e il JAR dell'applicazione.

Questo evita di includere Maven e il codice sorgente nell'immagine utilizzata per l'esecuzione.

## Possibili miglioramenti

Tra i possibili sviluppi futuri:

* prevenzione del salvataggio di rilevazioni duplicate per la stessa città e lo stesso timestamp;
* utilizzo di un database persistente, come PostgreSQL;
* configurazione dinamica delle città;
* test di integrazione con Testcontainers;
* documentazione interattiva delle API con OpenAPI e Swagger;
* filtri delle medie per intervallo temporale;
* paginazione e consultazione dello storico delle rilevazioni;
* monitoraggio tramite metriche e health check.

## Note

Open-Meteo è utilizzato come provider esterno gratuito e non richiede una API key.

Poiché il database è in memoria, i dati raccolti non vengono mantenuti dopo lo spegnimento dell'applicazione o del container.