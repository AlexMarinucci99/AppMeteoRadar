# MeteoRadar 🌤️

MeteoRadar è un'applicazione Android progettata per monitorare le condizioni meteorologiche in tempo reale. Grazie all'integrazione con mappe interattive, permette di visualizzare il meteo della propria posizione attuale o di qualsiasi punto salvato sulla mappa.

## ✨ Caratteristiche Principali

- **Mappa Interattiva**: Esplora il mondo con il supporto di Google Maps (modalità Standard e Satellite).
- **Meteo in Tempo Reale**: Visualizza temperatura, velocità del vento e condizioni atmosferiche dettagliate tramite l'integrazione con **Open-Meteo API**.
- **Gestione Pin Personalizzati**: Salva le tue località preferite aggiungendo titoli e note. I dati vengono salvati localmente.
- **Ricerca Intelligente**: Trova rapidamente città o indirizzi specifici tramite la barra di ricerca integrata (Geocoding).
- **Design Moderno**: Interfaccia pulita e reattiva realizzata interamente con **Jetpack Compose** e **Material Design 3**.
- **Supporto Tema Dinamico**: Pieno supporto per i temi Chiaro e Scuro, con colori dinamici su Android 12+.

## 🛠️ Stack Tecnologico

- **Linguaggio**: Kotlin
- **Interfaccia Utente**: Jetpack Compose + Material Design 3
- **Mappe**: Google Maps SDK
- **Architettura**: MVVM & Clean Architecture

## 🚀 Configurazione e Installazione

Per proteggere i dati sensibili, la chiave API di Google Maps non è inclusa nel repository. Segui questi passaggi per configurare l'ambiente locale:

### Google Maps API Key
Il progetto utilizza il **Secrets Gradle Plugin** per gestire la chiave in modo sicuro tramite il file `local.properties`.

1. Clona il repository.
2. Apri il progetto in Android Studio (il file `local.properties` verrà generato automaticamente).
3. Apri il file `local.properties` nella root del progetto.
4. Aggiungi la tua chiave API nella seguente riga:
```properties
maps_api_key = INCOLLA_QUI_LA_TUA_CHIAVE_API
``` 
 *Nota: Il file `AndroidManifest.xml` è già configurato per leggere automaticamente questa variabile durante la compilazione.*  
  
## 🏗️ Build e Run  
  
1. Assicurati di avere installata l'ultima versione stabile di **Android Studio**.  
2. Esegui un **Gradle Sync** per scaricare le dipendenze.  
3. Avvia l'app su un emulatore o un dispositivo fisico con i Google Play Services aggiornati.  
  
---  
_Applicazione sviluppata per l'esame di Laboratorio di Programmazione Mobile **[DT0309]**_
