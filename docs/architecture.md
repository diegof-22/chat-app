# Architettura del Progetto Java Chat

L'applicazione **Java Chat** è basata su un'architettura **Client-Server** classica, implementata utilizzando le API di Rete standard di Java (Socket TCP) e la programmazione multithreading per gestire molteplici connessioni in tempo reale.

## 🧩 Componenti Principali

### 1. Il Server (`ChatServer.java`)
Il Server funge da nodo centrale per la comunicazione. 
- **Gestione Connessioni**: Ascolta in attesa di nuove connessioni sulla porta TCP `12345` tramite un oggetto `ServerSocket`.
- **Multithreading Scalabile**: Utilizza un `ExecutorService` (nello specifico un `CachedThreadPool`) per assegnare un thread separato a ciascun client connesso. Questo permette al server di gestire molteplici utenti simultaneamente senza bloccarsi.
- **Stato Condiviso e Sicurezza Thread**: Mantiene un "Set" thread-safe (ottenuto tramite `Collections.newSetFromMap(new ConcurrentHashMap<>())`) in cui salva tutti i client correntemente attivi (`ClientHandler`).
- **Funzionalità di Broadcasting**: Espone metodi (es. `broadcast()`) per permettere l'invio di un messaggio da un client a tutti gli altri partecipanti alla chat.

### 2. Gestore del Singolo Client (`ClientHandler.java`)
Istanza creata dal Server per ogni connessione in ingresso. Implementa l'interfaccia `Runnable` così da essere eseguita dal Pool di Thread.
- **Gestione Flussi I/O**: Avvolge l'`InputStream` e l'`OutputStream` del singolo socket in `BufferedReader` e `PrintWriter` per leggere e scrivere intere stringhe agevolmente.
- **Handshake Iniziale**: Subito dopo la connessione, richiede all'utente di inserire il proprio username e annuncia a tutti l'ingresso di un nuovo partecipante.
- **Loop di Elaborazione e Routing**: Il suo compito principale è un ciclo `while` in continuo ascolto di messaggi. Se un messaggio inizia con lo slash (`/`), esso viene interpretato da `handleCommand()` (gestione comandi come `/help`, `/users`, `/quit`). Altrimenti, viene distribuito sulla rete invocando `ChatServer.broadcast()`.

### 3. Il Client (`ChatClient.java`)
L'applicazione eseguibile che l'utente finale avvia per connettersi alla chat.
- **Connessione**: Crea un `Socket` per connettersi all'indirizzo del server (es. `localhost:12345`).
- **Separazione dei Compiti (2 Thread)**:
  - **Thread di Ricezione (Background)**: Un thread `Runnable` indipendente è dedicato unicamente a leggere asincronamente i messaggi provenienti dal server e stamparli a schermo.
  - **Thread di Trasmissione (Main)**: Il thread principale del programma si focalizza sul catturare iterativamente l'input utente dalla testiera (`System.in`) inviandolo al Server.
  Questa divisione è un pattern essenziale: permette a un utente di ricevere la posta in arrivo esattamente nello stesso millisecondo in cui sta digitando un messaggio in uscita, senza alcun blocco dell'interfaccia CLI.

## 🔄 Schema del Flusso Dati

1. **Avvio Server**: La porta `12345` viene aperta. Si avvia il ciclo infinito di `serverSocket.accept()`.
2. **Connessione Utente**: Un client esegue `ChatClient` puntando al server e stabilendo una connessione.
3. **Assegnazione Risorse**: Il Server accetta, istanzia un `ClientHandler` associato alla socket, e lo fa eseguire al Thread Pool.
4. **Scambio di Messaggi**:
   - L'utente *Bob* digita un testo e preme Invio.
   - Il Main Thread del Client di Bob spara la stringa in uscita.
   - Il `ClientHandler` specifico di Bob la riceve.
   - Il `ClientHandler` di Bob invoca `ChatServer.broadcast("Bob: Ciao!")`.
   - Il `ChatServer` scorre tutti i `ClientHandler` in lista (es. quello di *Alice*) usando `sendMessage()`.
   - Il Background Thread del Client di Alice riceve una notifica sul socket, legge la stringa e la stampa sul terminale di Alice in tempo reale.
