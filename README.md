# Java Game Chat

Una semplice, pulita ma robusta applicazione di chat **Client-Server** scritta in Java, operante su terminale tramite **Socket TCP** e **Multithreading**. Ottima come modello base per comprendere le comunicazioni di rete in tempo reale, per i giochi multiplayer online o come base per un servizio di messaggistica CLI.

## Caratteristiche

* **Architettura Client-Server scalabile** tramite l'uso di Thread Pool.
* **I/O Asincrono**: Puoi leggere costantemente gli aggiornamenti in chat mentre continui a digitare in locale. 
* **Broadcast in tempo reale** senza interruzioni.
* **Comandi Chat Inoltrati**: Usa `/help`, `/users` e `/quit`.
* **Interfaccia CLI Pulita** con output server colorato.

## Struttura del Progetto

```text
java-chat/
├── src/
│   ├── server/
│   │   ├── ChatServer.java      # Entry point del server; gestisce le conessioni
│   │   └── ClientHandler.java   # Thread isolato che gestisce un singolo instradamento client
│   └── client/
│       └── ChatClient.java      # Entry point del client; si connette alla chat e ascolta
├── docs/
│   └── architecture.md          # Documentazione dell'architettura del sistema
└── README.md
```

## Come Eseguire il Progetto

### 1. Compilare il Codice

Assicurati di avere il **JDK** installato (Java 8 o superiore). 
Apri il terminale alla radice della cartella di progetto (dove si trova questo README) ed esegui la build nel branch `bin`:

```bash
mkdir -p bin
javac -d bin src/server/*.java src/client/*.java
```

*(Su Windows nativo, se `mkdir -p` non è supportato, crea semplicemente la cartella `bin` manualmente o con `mkdir bin` prima di lanciare il `javac`)*

### 2. Avviare il Server

Il Server deve essere avviato prima di qualsiasi altra operazione. Resterà in ascolto, fungendo da hub per gli utenti, sulla porta configurata (`12345`).

```bash
java -cp bin server.ChatServer
```

*Dovrebbe comparire un banner colorato, ad indicare l'avavvenuto boot e l'attesa di connessioni in ingresso.*

### 3. Avviare i Client

Apri uno o più terminali separati (uno per ogni utente/client che desideri avere nella chat), e avvia questo processo:

```bash
java -cp bin client.ChatClient
```

1. Quando richiesto nel terminale, **inserisci un username**.
2. Riceverai un saluto di avviso che la connessione è aperta.
3. **Chatta!** Ogni messaggio, non introdotto da `/`, verrà inviato in tempo reale a tutta la stanza.

## Comandi Disponibili (Lato Client)

All'interno dell'istanza client in rete puoi utilizzare specifici slash-commands digitandoli e premendo invio:

* `/help` : Mostra la lista completa dei comandi utilizzabili per via integrata.
* `/users` : Mostra in output il numero globale dei client correntemente attaccati al medesimo server.
* `/quit` : Effettua una disconnessione controllata dal server e un'uscita pulita dal programma.

## Documentazione Tecnica

Scopri in modo dettagliato come funzionano i flussi logici leggendo l'architettura aggiornata qui: [docs/architecture.md](docs/architecture.md)
