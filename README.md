# 🥗 EssMichKI – Smarter KI-Mahlzeiten- & Nährstofftracker

[![Android](https://img.shields.io/badge/Platform-Android%2014%2B%20%28API%2034%2B%20%7C%20Target%2037%29-3DDC84?style=flat-square&logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20%26%20Material%203-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM%20%2B%20Navigation%203-FF6F00?style=flat-square)](https://developer.android.com/topic/architecture)
[![License](https://img.shields.io/badge/License-MIT-blue.svg?style=flat-square)](LICENSE)

**EssMichKI** ist eine moderne, datenschutzfreundliche Android-App zur einfachen Nachverfolgung von Kalorien, Makronährstoffen und Körpergewicht. Mithilfe künstlicher Intelligenz via **OpenRouter** verwandelt die App Freitextbeschreibungen, Sprachbefehle oder Fotos in präzise Nährwertangaben – ohne manuelles Suchen in riesigen Lebensmittel-Datenbanken.

---

## 🌟 Highlights & Hauptfunktionen

### 🤖 KI-Gestützte Mahlzeitenerfassung
- **Freitext & Foto-Analyse:** Beschreibe dein Essen in eigenen Worten (z. B. *"2 Rühreier mit Vollkornbrot und einer Prise Salz"*) oder nutze die Kamera.
- **Freie Modellwahl:** Nutze extrem schnelle und günstige LLM-Modelle über [OpenRouter](https://openrouter.ai/), z. B.:
  - `google/gemini-2.0-flash-001` *(Empfohlen – extrem schnell & preiswert)*
  - `openai/gpt-4o-mini`
  - `deepseek/deepseek-r1`
  - `anthropic/claude-3.5-haiku`
- **Regionale API-Endpunkte:** Wähle zwischen Standard-, EU- oder US-Servern für optimierte Latenzen und Datenschutz.

### 📊 Smarte Nährstoff- & Zielberechnung
- **Wissenschaftliche BMR-Formeln:** Berechne deinen Kalorien-Grundumsatz präzise nach:
  - *Mifflin-St. Jeor* (Standard)
  - *Harris-Benedict*
  - *Katch-McArdle* (Körperfettanteil)
  - *WHO-Formel*
- **Makro-Verteilung:** Individuell anpassbare Verhältnisse für Eiweiß, Kohlenhydrate und Fette (z. B. High Protein, Low Carb, Balanced).
- **Mahlzeiten-Kategorien:** Strukturierte Übersicht für Frühstück, Mittagessen, Abendessen und Snacks.

### 📈 Fortschritt & Gewichtsverlauf
- **Gewichtstagebuch:** Erfasse dein tägliches Gewicht und verfolge Trends visuell.
- **Konsistenz-Streaks:** Bleibe motiviert durch automatische Tagessträhnen für regelmäßiges Loggen.

### 🔒 Privacy First & Data Ownership
- **100 % Lokale Daten:** Alle Mahlzeiten, Gewichtseinträge und Einstellungen werden lokal in einer Android **Room SQLite-Datenbank** gespeichert.
- **Keine Tracking-Server:** Kein eigener Backend-Zwang und keine Registrierung erforderlich.
- **CSV Import & Export:** Exportiere deine Daten jederzeit als CSV-Datei oder sichere lokale Backups.

---

## 📱 Screenshots & Eindrücke

| Dashboard & Gauge | KI-Mahlzeitenerfassung | Fortschritt & Gewicht |
| :---: | :---: | :---: |
| *(Dashboard Übersicht)* | *(OpenRouter KI-Dialog)* | *(Gewicht & Streaks)* |

---

## 🏗️ Architektur & Technologie-Stack

Die App basiert auf modernen Android-Entwicklungsstandards und Best Practices:

- **Programmiersprache:** [Kotlin](https://kotlinlang.org/)
- **UI-Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) mit Material 3 Design System & Dynamic Color
- **Navigation:** [androidx.navigation3](https://developer.android.com/guide/navigation)
- **Architektur:** MVVM (Model-View-ViewModel) mit Uni-directional Data Flow (`StateFlow` & `Coroutines`)
- **Lokale Datenbank:** [Room DB](https://developer.android.com/training/data-storage/room) mit KSP Codegen
- **Einstellungen:** Preferences [DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
- **Netzwerk & KI:** [OkHttp](https://square.github.io/okhttp/) & [Moshi JSON](https://github.com/square/moshi) zur direkten Anbindung der OpenRouter REST-API
- **Kamera:** [CameraX](https://developer.android.com/training/camerax) für Foto-Nährwertanalyse

---

## 🚀 Schnellstart & Einrichtung

### 1. Voraussetzungen
- Smartphone oder Emulator mit **Android 14 (API Level 34)** oder neuer.
- Ein kostenloser Account bei [OpenRouter.ai](https://openrouter.ai/).

### 2. OpenRouter API-Key einrichten

> [!TIP]
> Die Nutzung von Gemini 2.0 Flash über OpenRouter ist extrem günstig. Bereits $1 Guthaben reicht oft für viele Tausend Mahlzeit-Analysen!

1. Melde dich bei [OpenRouter.ai](https://openrouter.ai/) an.
2. Erstelle unter **Keys** einen neuen API-Schlüssel.
3. Öffne **EssMichKI** auf deinem Smartphone.
4. Tippe oben rechts auf das **Profil- / Einstellungen-Icon**.
5. Trage deinen **API-Key** ein und wähle dein bevorzugtes KI-Modell (z. B. `google/gemini-2.0-flash-001`).
6. Tippe auf **Speichern**.

---

## 📖 Bedienungsanleitung

### 🍽️ Mahlzeit loggen
1. Tippe auf dem Dashboard auf das **`+` Icon** oder auf den Button **"Mahlzeit hinzufügen"**.
2. Gib deine Mahlzeit in Alltagssprache ein (z. B. *"1 Schüssel Haferflocken mit 200ml Milch und 100g Erdbeeren"*).
3. Tippe auf **Analysieren**. Die KI ermittelt automatisch Kalorien, Proteine, Kohlenhydrate und Fett.
4. Prüfe die Werte und wähle die gewünschte Kategorie (*Frühstück*, *Mittagessen*, *Abendessen* oder *Snack*).
5. Bestätige mit **Speichern**.

### ⚖️ Gewicht & Ziele verwalten
1. Wechsle im unteren Navigationsmenü auf den Reiter **Fortschritt**.
2. Erfasse dein aktuelles Gewicht über **"Gewicht eintragen"**.
3. Passe deine Nährstoffziele oder BMR-Formel unter **"Ziele bearbeiten"** an.

### 💾 Daten exportieren / sichern
1. Öffne die **Einstellungen** über das Profil-Icon.
2. Wähle **CSV-Export**, um deine Daten als Datei lokal abzuspeichern.

---

## 🛠️ Für Entwickler / Projekt bauen

### Systemvoraussetzungen
- **Android Studio:** Ladybug (2024.2) oder neuer
- **JDK:** Java 11 / 17
- **Android SDK:** Compile SDK 37, Min SDK 34

### Schritte zum Bauen

1. **Repository klonen:**
   ```bash
   git clone https://github.com/your-username/ess-mich-ki.git
   cd ess-mich-ki
   ```

2. **Projekt in Android Studio öffnen** und Gradle-Sync durchführen.

3. **Debug-APK bauen & ausführen:**
   ```bash
   ./gradlew assembleDebug
   ```

4. **Unit-Tests ausführen:**
   ```bash
   ./gradlew test
   ```

---

## 📄 Lizenz

Dieses Projekt steht unter der [MIT Lizenz](LICENSE) zur Verfügung.
