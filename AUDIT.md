# AUDIT TECNICO - BAR MAP STUDIO / NEROXIS MAP GENERATOR

**Data:** 30 Settembre 2026  
**Progetto:** BAR MAP STUDIO (Fork Neroxis Map Generator per Beyond All Reason / Recoil Engine)  
**Stato Analisi:** Risolto e Collaudato con Successo  

---

## 1. Obiettivo dell'Audit
Identificare con precisione i motivi per cui l'applicazione **non genera le mappe** (blocco del processo, generazione infinita o assenza di file generati sia da CLI che da interfaccia grafica BAR Map Studio).

---

## 2. Architettura del Sistema
Il progetto è una suite Java (OpenJDK 25) progettata per generare mappe procedurali e personalizzate per **Beyond All Reason (BAR)** basato su Recoil Engine:
- **Core Generatore:** Fork del generatore procedurale Neroxis (Supreme Commander) adattato per BAR (`com.faforever.neroxis`).
- **Pipeline BAR (`com.faforever.neroxis.exporter.bar`):**
  1. *Generazione Altezza & Bioma:* Calcolo dell'heightmap, pendenze, texture di base e posizionamento spawn/risorse/reclaim.
  2. *Esportazione SMF/SMT (`SmfWriter`, `BarMapExporter`):*
     - Creazione dell'archivio `.smt` contenente i tile grafici compressi in DXT1 con mipmaps (32x32, 16x16, 8x8, 4x4) e header di compressione (680 byte per tile).
     - Creazione file `.smf` (heightmap 16-bit, metal map, grass map, minimap, posizioni feature e scritte).
     - Creazione mappe ausiliarie (`_normals.tga`, `_specular.dds`, `_edge.dds`, `_splats.dds`, `_preview.png`, `mapinfo.lua`).
     - Confezionamento finale in archivio `.sdz` dentro la cartella `maps` di BAR.
- **Interfaccia Utente (Map Studio / BarLauncher):**
  - Editor 2D interattivo (`MapStudioCanvas`, `MapStudioPanel`, `BarMapStudio`).
  - Modalità offline con mock server locale (`127.0.0.1:8200` e `127.0.0.1:8090`).
  - Launcher standalone configurato in `extracted/{app}/bar-map-launcher.bat` e runtime Java bundled.

---

## 3. Risultati dell'Audit: Cause del Blocco Generazione Mappe

### 🔴 CAUSA 1 (CRITICA): Ciclo Infinito in `BarMapExporter.writeTiles`
* **File:** `src/sources/com/faforever/neroxis/exporter/bar/BarMapExporter.java` (Linee 1755-1765)
* **Sintomo:** Il generatore si avvia regolarmente, compila la pipeline del terreno in meno di 1 secondo, stampa a console:
  ```text
  Pipeline completed!
  Map generation done: 718 ms
  Colour grading: brightness x0.85, saturation x1.60
  Reclaim: 1218 of 1882 metal in rocks and wrecks kept (budget 1200)
  ```
  e successivamente **si blocca per sempre** con la CPU al 100% su tutti i core logici, senza mai terminare né creare il file `.sdz`.
* **Analisi Tecnica del Thread Dump (`jcmd Thread.print`):**
  Tutti i 16 worker thread del `ForkJoinPool.commonPool()` risultavano bloccati in esecuzione continua:
  ```text
  "ForkJoinPool.commonPool-worker-13" runnable
     at com.faforever.neroxis.exporter.bar.BarMapExporter.lambda$writeTiles$0(BarMapExporter.java:1765)
  ```
* **Causa nel Codice Decompilato / Ricompilato:**
  Nel metodo `writeTiles(OutputStream out)`, il ciclo di generazione dei mipmap DXT1 per ciascun tile di 32x32 pixel era implementato come:
  ```java
  int offset = tileX * 680;
  int[] mip = tile;
  int i2 = tileSize;
  while (true) {
      int size = i2;
      if (size >= 4) {
          offset += Dxt1Encoder.compress(mip, size, size, rowBytes, offset);
          if (size > 4) {
              mip = Dxt1Encoder.downsample(mip, size, size);
          }
          i2 = size / 2;
      }
  }
  ```
  **Il difetto:** Quando `size` scende a `2` (dopo aver compresso il livello `4x4`), la condizione `if (size >= 4)` diventa falsa. Non essendoci alcun `else { break; }`, il ciclo `while (true)` continuava a girare all'infinito senza mai uscire e senza mai compiere alcuna operazione.
  Poiché `writeTiles` parallelizza l'elaborazione dei tile via `IntStream.range(...).parallel()`, ogni singolo thread entrava immediatamente in loop infinito al primissimo tile, esaurendo tutti i core della macchina e congelando l'esportazione.

---

### 🟡 CAUSA 2 (GRAVE): Bug di Scambio Valori in `Dxt1Encoder.compressBlock`
* **File:** `src/sources/com/faforever/neroxis/exporter/bar/Dxt1Encoder.java` (Linee 53-56)
* **Sintomo:** Corruzione della quantizzazione del colore nei blocchi DXT1 (circa il 50% dei blocchi subiva perdita di contrasto ed errata interpolazione).
* **Causa nel Codice:**
  ```java
  if (c0 < c1) {
      c0 = c1;
      c1 = c0;
  }
  ```
  Lo swap decompilato senza variabile temporanea rendeva `c0 == c1`, corrompendo la palette DXT1.

---

### 🟡 CAUSA 3 (CONFIGURAZIONE): Discrepanza Percorso BAR tra Program Files e LocalAppData
* **File:** `src/sources/com/faforever/neroxis/generator/bar/Platform.java` e `BarMapStudio.java`
* **Sintomo:** Se `BarMapStudio` non riceveva il percorso, `Platform.defaultData()` cercava solo in `%LOCALAPPDATA%\Programs\Beyond-All-Reason\data` (non presente sul sistema, essendo BAR installato in `C:\Program Files\Beyond-All-Reason\data`).
* **Effetto:** `MapStudioPanel.doGenerateFinalMap()` mostrava:
  `"Cartella Beyond All Reason maps non trovata in: ..."`
  bloccando la generazione.

---

### 🟡 CAUSA 4: Thread Pool Non-Daemon in `GroundGenerator`
* **File:** `src/sources/com/faforever/neroxis/generator/bar/GroundGenerator.java`
* **Sintomo:** Il pool `EXECUTOR` usava thread standard non-daemon, tenendo aperto il processo JVM anche a pipeline completata se eseguito in modalità standalone o batch.

---

### 🔴 CAUSA 5 (CRITICA PER MAPPE RETTANGOLARI): Assunzione di Dimensioni Quadrate in `BarMapExporter.readLayerWeights`
* **File:** [`src/sources/com/faforever/neroxis/exporter/bar/BarMapExporter.java`](file:///c:/Users/Francesco_pc/Desktop/BAR%20MAP%20STUDIO/src/sources/com/faforever/neroxis/exporter/bar/BarMapExporter.java) (Linee 1028-1051)
* **Sintomo:** Le mappe quadrate (es. 256x256, 384x384, 512x512) venivano esportate senza problemi, mentre per **qualsiasi mappa rettangolare** (es. 256x384, 384x256, 256x512, ecc.) l'esportazione falliva immediatamente con:
  ```text
  java.lang.IllegalArgumentException: Texture masks have different sizes
  ```
  oppure causava errore di generazione delle texture map nell'editor di Map Studio.
* **Causa nel Codice:**
  Mentre `GroundGenerator` ritagliava correttamente le texture mask con dimensioni rettangolari distinte (`width + 1` e `height + 1`), il metodo di lettura delle texture in `BarMapExporter` assumeva che le maschere dovessero essere obbligatoriamente quadrate:
  ```java
  private static float[][] readLayerWeights(BufferedImage low, BufferedImage high) {
      int size = low.getWidth();
      if (low.getHeight() != size) {
          throw new IllegalArgumentException("Texture masks have different sizes");
      }
      float[][] weights = new float[8][size * size];
      // ...
  }
  ```
  Per una mappa 256x384, `low.getWidth()` valeva `257` e `low.getHeight()` valeva `385`. Il controllo lanciava eccezione immediata. Inoltre, la dimensione dell'array allocata con `size * size` avrebbe causato un successivo `ArrayIndexOutOfBoundsException` appena `y >= width`.
  Inoltre, in `resolveTeams` e `describeTeams`, le coordinate degli spawn venivano normalizzate dividendo per una sola dimensione (`faSize`) anziché separatamente per `faWidth` e `faHeight`.

---

## 4. Interventi Applicati

1. **`BarMapExporter.java` - Risoluzione Ciclo Infinito in `writeTiles`:**
   Sostituito il ciclo `while (true)` con un loop deterministico:
   ```java
   int offset = tileX * 680;
   int[] mip = tile;
   for (int size = tileSize; size >= 4; size /= 2) {
       offset += Dxt1Encoder.compress(mip, size, size, rowBytes, offset);
       if (size > 4) {
           mip = Dxt1Encoder.downsample(mip, size, size);
       }
   }
   ```
2. **`Dxt1Encoder.java` - Correzione Swap Colore:**
   ```java
   if (c0 < c1) {
       int tmp = c0;
       c0 = c1;
       c1 = tmp;
   }
   ```
3. **`Platform.java` & `BarMapStudio.java` - Auto-Rilevamento Cartella Robusto:**
   Aggiunto fallback automatico che rileva sia `%ProgramFiles%\Beyond-All-Reason\data` sia `C:\Program Files\Beyond-All-Reason\data`.
4. **`GroundGenerator.java` - Thread Pool Daemon:**
   Configurato `ThreadFactory` con `setDaemon(true)` per evitare blocchi dello shutdown JVM.
5. **`BarMapExporter.java` - Supporto Completo Mappe Rettangolari:**
   - In `readLayerWeights`: gestite dimensioni indipendenti `width = low.getWidth()` e `height = low.getHeight()`, allocazione `weights = new float[8][width * height]` e verifica corretta che `high.getWidth() == width && high.getHeight() == height`.
   - In `resolveTeams` e `describeTeams`: calcolati `centerX = faWidth / 2f` e `centerZ = faHeight / 2f`, e normalizzazione separata con `faWidth` e `faHeight`.
6. **Ricompilazione e Patch `launcher.jar`:**
   Tutte le classi ricompilate sono state archiviate in `patch/` e iniettate con successo dentro `extracted/{app}/app/launcher.jar`.

---

### 🔴 CAUSA 6: Assenza dell'Anteprima / Thumbnail Mappa nella Lobby di Beyond All Reason (Chobby)
* **File:** [`src/sources/com/faforever/neroxis/exporter/bar/BarMapExporter.java`](file:///c:/Users/Francesco_pc/Desktop/BAR%20MAP%20STUDIO/src/sources/com/faforever/neroxis/exporter/bar/BarMapExporter.java)
* **Sintomo:** Le mappe generate compaiono nell'elenco mappe di BAR ma con un riquadro nero/vuoto ("missing preview") sia nella griglia delle miniature sia nel pannello di dettaglio della lobby Chobby. Nel log di BAR (`data/infolog.txt`) compariva:
  ```text
  [Chobby] Warning: GetMinimapImage not found for, Map_382331124_1.0
  [Chobby] Warning: GetMinimapImage not found for, Map_aaaaaaaaaaahw_aicae
  ```
* **Causa Tecnica nel Motore e nella Lobby BAR:**
  1. **Disaccoppiamento Lobby / Engine:** La lobby di BAR (**Chobby / LuaMenu**) non apre e non decomprime gli archivi `.sdz` (da 30-50+ MB ciascuno) mentre l'utente scorre l'elenco mappe, per evitare freeze della UI.
  2. **Lookup delle Thumbnail in Chobby (`luamenu/widgets/chobby/components/configuration.lua`):**
     Le funzioni `Configuration:GetMinimapSmallImage(mapName)` e `Configuration:GetMinimapImage(mapName)` sanificano il nome sostituendo gli spazi con underscore (`string.gsub(mapName, " ", "_")`) e cercano l'immagine unicamente nei seguenti percorsi locali:
     - `LuaMenu/configs/gameconfig/byar/minimapthumbnail/<mapName>.png` (anteprima ridotta nella lista)
     - `LuaMenu/configs/gameconfig/byar/minimapoverride/<mapName>.jpg` (anteprima grande nella stanza)
     - `LuaMenu/Images/Minimaps/<mapName>.jpg` (fallback)
     Se non presenti in locale, Chobby prova a scaricarle dalla CDN ufficiale di BAR (`api.beyondallreason.info/minimap/...`). Trattandosi di mappe locali personalizzate, la richiesta CDN restituisce 404 e Chobby ricade su `minimapNotFound.png` (riquadro nero).
  3. **Omissione in `mapinfo.lua`:** Mancavano i parametri espliciti del blocco `smf` per il puntamento della texture e del tileset:
     `smtFileName0 = "maps/@BASE@.smt"` e `minimapTex = "maps/@BASE@_preview.png"`.
  4. **Assenza Thumbnail Root nell'Archivio:** L'archivio zip conteneva solo `maps/<name>_preview.png`, mancando i file `preview.png` e `minimap.png` nella radice richiesti da parser terzi e tool Spring.

---

## 4. Interventi Applicati

1. **`BarMapExporter.java` - Risoluzione Ciclo Infinito in `writeTiles`:**
   Sostituito il ciclo `while (true)` con un loop deterministico:
   ```java
   int offset = tileX * 680;
   int[] mip = tile;
   for (int size = tileSize; size >= 4; size /= 2) {
       offset += Dxt1Encoder.compress(mip, size, size, rowBytes, offset);
       if (size > 4) {
           mip = Dxt1Encoder.downsample(mip, size, size);
       }
   }
   ```
2. **`Dxt1Encoder.java` - Correzione Swap Colore:**
   ```java
   if (c0 < c1) {
       int tmp = c0;
       c0 = c1;
       c1 = tmp;
   }
   ```
3. **`Platform.java` & `BarMapStudio.java` - Auto-Rilevamento Cartella Robusto:**
   Aggiunto fallback automatico che rileva sia `%ProgramFiles%\Beyond-All-Reason\data` sia `C:\Program Files\Beyond-All-Reason\data`.
4. **`GroundGenerator.java` - Thread Pool Daemon:**
   Configurato `ThreadFactory` con `setDaemon(true)` per evitare blocchi dello shutdown JVM.
5. **`BarMapExporter.java` - Supporto Completo Mappe Rettangolari:**
   - In `readLayerWeights`: gestite dimensioni indipendenti `width = low.getWidth()` e `height = low.getHeight()`, allocazione `weights = new float[8][width * height]` e verifica corretta che `high.getWidth() == width && high.getHeight() == height`.
   - In `resolveTeams` e `describeTeams`: calcolati `centerX = faWidth / 2f` e `centerZ = faHeight / 2f`, e normalizzazione separata con `faWidth` e `faHeight`.
6. **`BarMapExporter.java` - Integrazione Automatica Anteprima / Minimap BAR:**
   - Aggiunti `preview.png` e `minimap.png` alla radice del file `.sdz` insieme a `maps/<archiveName>_preview.png`.
   - Aggiornato `buildMapInfo()` con `smtFileName0 = "maps/@BASE@.smt"` e `minimapTex = "maps/@BASE@_preview.png"`.
   - Implementato `saveLobbyMinimaps`: salvataggio istantaneo e automatico ad ogni esportazione delle anteprime in formato `.png` e `.jpg` dentro:
     - `Beyond-All-Reason/data/LuaMenu/configs/gameconfig/byar/minimapthumbnail/`
     - `Beyond-All-Reason/data/LuaMenu/configs/gameconfig/byar/minimapoverride/`
     - `Beyond-All-Reason/data/LuaMenu/Images/Minimaps/`
     per tutte le varianti di lookup di Chobby (`Map_Seed_1.0`, `map_seed_1.0`, `Map_Seed 1.0`).
   - Sincronizzate anche tutte le mappe generate in precedenza.
7. **Ricompilazione e Patch `launcher.jar`:**
   Tutte le classi ricompilate sono state archiviate in `patch/` e iniettate con successo dentro `extracted/{app}/app/launcher.jar`.

---

## 5. Esito del Collaudo e Verifica

### Test 1: Generatore da Linea di Comando (`generate-bar-map.bat`)
Comando eseguito:
```cmd
generate-bar-map.bat --spawn-count 2 --map-size 256 --seed 123
```
**Risultato:**
- Pipeline del terreno: **746 ms**
- Esportazione BAR completa: **4486 ms (~4.5 secondi)**
- Archivio generato: `C:\Program Files\Beyond-All-Reason\data\maps\map_aaaaaaaaaaahw_aicae.sdz` (27 MB, 88 file compressi).
- **Esito: SUCCESSO 100%.**

### Test 2: Pipeline Studio Editor Quadrata (384x384)
- Generazione Terreno (384x384, stile VALLEY, bioma FRITHEN): eseguita in tempo reale.
- Esportazione mappa e script schermaglia (`_neroxis_skirmish.txt`): completata in 4 secondi.
- **Esito: SUCCESSO 100%.**

### Test 3: Mappa Rettangolare Verticale (256x384, BAR Map 8x12)
- Generazione terreno + texture masks (257x385): completata con successo.
- Esportazione BAR `.sdz` (8x12, 25.2 MB): completata in 5.8 secondi senza errori.
- **Esito: SUCCESSO 100%.**

### Test 4: Mappa Rettangolare Orizzontale (384x256, BAR Map 12x8)
- Generazione terreno + texture masks (385x257): completata con successo.
- Esportazione BAR `.sdz` (12x8, 26.2 MB): completata in 5.6 secondi senza errori.
- **Esito: SUCCESSO 100%.**

### Test 5: Verifica Anteprima Lobby Chobby (Seed 777, `map_aaaaaaaaaabqs_aicae.sdz`)
- File `.sdz` generato contiene: `maps/map_aaaaaaaaaabqs_aicae_preview.png`, `preview.png`, `minimap.png`.
- File `mapinfo.lua` contiene: `smtFileName0` e `minimapTex`.
- Cartelle Chobby popolate automaticamente:
  - `LuaMenu/configs/gameconfig/byar/minimapoverride/Map_aaaaaaaaaabqs_aicae.jpg` (73.8 KB)
  - `LuaMenu/configs/gameconfig/byar/minimapthumbnail/Map_aaaaaaaaaabqs_aicae.png` (1.75 MB)
  - `LuaMenu/Images/Minimaps/Map_aaaaaaaaaabqs_aicae.jpg` & `.png`
- **Esito: SUCCESSO 100%. L'anteprima appare immediatamente nella lista mappe e nella lobby di Beyond All Reason senza riquadri neri.**
