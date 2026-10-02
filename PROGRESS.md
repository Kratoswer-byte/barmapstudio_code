# STATO AVANZAMENTO: BAR MAP STUDIO

**Ultimo Aggiornamento:** 01 Ottobre 2026  
**Stato Generale:** In Corso (Step 0 completato)

---

## Registro dei Punti di Ripristino (Git Commits)

| Commit ID | Tipo | Data / Ora | Descrizione |
| :--- | :---: | :---: | :--- |
| `21df09b` | `chore` | 01/10/2026 09:47 | Baseline savepoint prima delle feature heightmap, pendenze e slope map |
| `3eba201` | `feat` | 01/10/2026 09:51 | Motore di calcolo pendenze, rigenerazione ground e smooth slopes |
| `12d9cba` | `feat` | 01/10/2026 09:55 | Visualizzatore Slope Map in Canvas, HUD e ispezione quote/pendenza al cursore |
| `efdbe8e` | `feat` | 01/10/2026 10:01 | Controlli GUI per MaxHeight, WaterLevel, Smooth Slopes e importazione da file |
| `7f822c2` | `build` | 01/10/2026 10:04 | Ricompilazione patch, iniezione launcher.jar e collaudo finale |
| `50ee05f` | `feat/fix` | 01/10/2026 10:38 | Pannelli resizable con dual JSplitPane, fix glifi unrendered e layout spinner flessibile |
| `e48b38b` | `feat` | 01/10/2026 11:12 | Migrazione tipografia su Roboto & Roboto Condensed per tutti i pannelli UI e HUD Canvas |
| `e8a561e` | `feat` | 01/10/2026 11:18 | Font geometrico rotondo e corposo Poppins Bold applicato a tutta la UI e canvas |
| `d223c55` | `fix` | 01/10/2026 11:28 | Sostituzione completa emoji e simboli font con icone vettoriali native Java2D |
| `2ad01b6` | `fix` | 01/10/2026 11:34 | Indicatore stato BAR con LED vettoriale antialiased luminescente (risolto tofu U+25CF) |
| `096d209` | `feat` | 01/10/2026 11:58 | Footer moderno slim 24px con overlay popups per stato BAR e guida hotkeys |
| `d296cec` | `fix` | 01/10/2026 12:15 | Risolto taglio pannello sinistro all'avvio e abilitato resize altezza finestra frameless |
| `28398b9` | `feat` | 01/10/2026 12:35 | Controlli Slope Map, zoom e scala spostati in dock HUD centrale sotto le coordinate |
| `ab50212` | `fix` | 01/10/2026 14:27 | Ripristino massimizzazione all'avvio, bounds spaziosi e fix troncamento testi nei pannelli |
| `22ccb6b` | `docs` | 01/10/2026 14:57 | Analisi e documentazione architetturale origine biomi, texture e stili rilievo (/plan) |
| `0da6da3` | `feat` | 01/10/2026 15:16 | Finestra AssetSpecsDialog (guida requisiti asset) e supporto texture custom |
| `3146ec6` | `fix/feat` | 01/10/2026 15:58 | Icone vettoriali specs (no tofu), apertura allo start, specs button su slope map e sidebar responsive con margini |
| `0a5ebc4` | `feat` | 01/10/2026 16:55 | Modulo Texture Studio (PBR Seamless 4D, Splatting Multi-Materiale RGBA e nuova tab TEXTURES) |
| `eea4450` | `feat/fix` | 01/10/2026 17:40 | Riprogettazione Texture Studio: layout compatto 240px, default Metodo B (file maschera) e fallback Metodo A |
| `8975273` | `feat/fix` | 01/10/2026 18:00 | Sblocco pulsante Pialla Pendenze [Ramps] e integrazione completa Sliders Metodo A & PBR in sidebar compatta 240px |
| `13aa881` | `feat/fix` | 02/10/2026 09:20 | Sincronizzazione live Mappa Attiva in Texture Studio, eliminazione anelli sinusoidali con Perlin periodico e aggiornamento canvas |
| `69e19c9` | `feat/fix` | 02/10/2026 11:00 | Avvio pulito senza generazioni fake in Texture Studio, supporto Heightmap esterne con Empty State e sync bidirezionale a MAPS |
| `810d1d6` | `feat/fix` | 02/10/2026 14:49 | Risoluzione vincolo 256px in SCMap.setPreview per BAR e icone native Java2D (stella, cartella, dadi) |
| `4d149cb` | `feat/fix` | 02/10/2026 15:30 | Supporto mappe rettangolari in Texture Studio ed esportazione texture personalizzate in BAR |
| `a040f9e` | `fix` | 02/10/2026 16:10 | Allineamento a sinistra titoli e descrizioni sezioni in Texture Studio (BoxLayout alignmentX) |
| `9e800db` | `feat` | 02/10/2026 16:40 | Integrati comandi HUD Slope Map, opacita' e zoom 1:1 in Texture Studio |

---

## Roadmap e Stato delle Funzionalità

### 1. Engine Rilievo e Pendenze (`GroundGenerator.java` & `SCMap.java`)
- [x] Analisi quote minime e massime da heightmap (`minHeight`, `maxHeight`)
- [x] Calcolo pendenze discrete con gradiente normalizzato Recoil (`getSlopeAtWorld`, `calculateSlope`)
- [x] Generazione maschera Slope Map colorata (standard Recoil: verde, giallo, arancio, rosso, blu)
- [x] Rigenerazione ground (texture, bioma, normali, acqua) da heightmap (`generateGroundFromHeightmap`)
- [x] Algoritmo di levigatura pendenze (*Smooth Slopes / Ramps*) (`smoothSlopes`)

### 2. Rendering e Interfaccia Canvas (`MapStudioCanvas.java`)
- [x] Layer sovrapposto Slope Map con trasparenza regolabile (`slopeOpacity`)
- [x] Scorciatoia da tastiera e cheatsheet `[L] Slope Map`
- [x] Legenda grafica HUD a schermo (Veicoli, Bot, Invalicabile, Acqua)
- [x] Ispezione dinamica al mouse (quota in metri e pendenza in gradi/valore numerico live nel canvas HUD)

### 3. Controlli Pannello Studio (`MapStudioPanel.java`)
- [x] Spinner Quota Massima (`maxHeight`, 20m - 1500m)
- [x] Spinner Livello Acqua (`waterLevel`, -100m - +800m)
- [x] Tasto "Rigenera Ground da Heightmap"
- [x] Tasto "Carica Heightmap da File..." (supporto PNG, RAW 16-bit, BMP)
- [x] Tasto "Leviga Pendenze Eccessive (Anti-Cliff)"
- [x] Tasto rapido e slider trasparenza per la Slope Map nella Top Toolbar e hotkey [L]

### 4. Compilazione e Verifica Binari
- [x] Compilazione classi in `patch/`
- [x] Iniezione classi aggiornate in `extracted/{app}/app/launcher.jar`
- [x] Test di generazione da CLI e verifica esportazione BAR (generazione .sdz convalidata)

### 5. Layout Resizable, Fix Glifi e Spinner Flessibili
- [x] Architettura dual `JSplitPane` su MapStudioPanel (Left Sidebar <-> Center Canvas <-> Right Sidebar) per rendere ogni pannello ridimensionabile
- [x] Rimozione caratteri emoji che causavano glifi quadrati unrendered (`▯`)
- [x] Layout righe dedicate `createParamRow` con etichette chiare e spinner con unità di misura ("m")
- [x] Styling ad alto contrasto per tutti i componenti `JSpinner` nel tema Dark
- [x] `JScrollPane` non compresso con wrapping su `BorderLayout.NORTH`
- [x] `JSplitPane` integrato anche per la vista Libreria Mappe in `BarMapStudio.java`

### 6. Sistema Tipografico Moderno
- [x] Rilevamento automatico font con fallback trasparente su Segoe UI
- [x] Metodi helper centralizzati `font(...)` e `fontCond(...)` in `BarMapStudio.java`
- [x] Configurazione UIManager globale con font tipografici
- [x] Aggiornamento pulsanti di navigazione principale (`TopNavButton`), sub-nav (`SubTabButton`) e header
- [x] Aggiornamento controlli laterali: titoli schede in BOLD, label e spinner coordinati
- [x] Aggiornamento indicatori Canvas: pill badges del metallo, HUD quote/pendenze live, cheatsheet tasti e legenda Slope Map

### 7. Tipografia Rotonda, Corposa e Geometrica (Poppins Bold)
- [x] Integrazione font geometrico circolare `Poppins Bold` come scelta prioritaria
- [x] Fallback graceful automatico a cascata: `Poppins` -> `Roboto` -> `Segoe UI`
- [x] Adattamento larghezze sub-tab buttons a 205px per ospitare la spaziatura generosa di Poppins
- [x] Ricompilazione e iniezione in `launcher.jar` con test di esecuzione completato con successo

### 8. Icone Vettoriali Native Java2D (Risoluzione Glifi Mancanti ☒)
- [x] Eliminazione completa dipendenza da emoji Unicode e caratteri speciali non supportati dal font Poppins
- [x] Implementazione icone vettoriali per `TopNavButton` ("MAPS" con mappa 3D piegata a 3 ante, "UNITS" con spade incrociate)
- [x] Implementazione icone vettoriali per `SubTabButton` ("EDITOR & GENERATORE" con chiavi/utensili, "LIBRERIA MAPPE" con cartella)
- [x] Nuovo componente dedicato `WindowControlButton` che disegna a vettore minimizza (barra), massimizza (quadrato) e chiudi (X con highlight rosso all'hover)
- [x] Icona montagna a doppio picco a vettore per la legenda `Slope Map` nel Canvas HUD
- [x] Sostituzione frecce non visualizzabili in `shadeHint` con caratteri standard ASCII `<` e `>`
- [x] Risoluzione definitiva di tutti i box quadrati vuoti con crocetta (tofu `☒`)

### 9. LED Vettoriale di Stato BAR (Risoluzione Glifo Tofu [ ▯ ] su barStatus)
- [x] Identificazione carattere Unicode `●` (`\u25CF` Black Circle) impostato dinamicamente da `BarLauncher.updateBarStatus()`
- [x] Creazione di un'icona vettoriale `statusDot` con doppio anello (alone luminoso esterno a 60% alpha e nucleo LED pieno)
- [x] Aggiunto listener reattivo su `barStatus.text` per intercettare e rimuovere il prefisso `●` / `•`, rimpiazzandolo con l'icona LED vettoriale
- [x] Risoluzione del glifo `▯` mostrato accanto alla versione di Beyond All Reason ed engine Recoil

### 10. Footer Moderno Slim con Overlay Popups Informativi (Stato BAR & Guida Hotkeys) [Commit: 096d209]
- [x] Pulizia header superiore: rimosse le scritte lunghe di stato dal top-right per mantenere l'header pulito, simmetrico e dedicato unicamente alla navigazione e ai comandi finestra
- [x] Creazione footer compatto a basso profilo (altezza 24px) ancorato in basso con branding discreto a sinistra (`BAR MAP STUDIO`)
- [x] Pulsante compatto a pillola con LED di stato vettoriale e testo `BAR` in basso a destra
- [x] Overlay popup flyout per lo Stato BAR: cliccando sul pulsante si apre un riquadro flottante scuro con bordo ciano che mostra versione BAR, versione engine Recoil, stato di sincronizzazione e tasto rapido 'Apri Cartella BAR'
- [x] Pulsante guida a vettore `[ ? ]` in basso a destra
- [x] Overlay popup flyout per Scorciatoie e Hotkeys: riquadro a 2 colonne con tutte le scorciatoie di Map Studio ([S], [M], [G], [P], [R], [T], [W], [E], [DEL], [Spazio], [X], [Ctrl+Z/Y], [L], rotellina zoom, drag destro)
- [x] Posizionamento dinamico verso l'alto degli overlay popup (offset Y negativo rispetto al footer) per evitare clipping oltre il bordo dello schermo

### 11. Risoluzione Taglio Pannello Sinistro e Ridimensionamento Finestra in Altezza [Commit: d296cec]
- [x] Risolto taglio laterale all'avvio su `MapStudioPanel`: allocata larghezza iniziale di 380px per la sidebar sinistra (`leftSplit.setDividerLocation(380)`)
- [x] Aggiunto listener reattivo `ComponentListener` su `leftSplit` e `mainSplit` che garantisce e blocca l'apertura a 380px ed evita che la finestra all'avvio comprima la sidebar a 240px
- [x] Aumentata la dimensione minima consentita per la sidebar sinistra (`setMinimumSize(340, 100)`) così che né il layout automatico né l'utente possano schiacciare i controlli oltre il limite di leggibilità
- [x] Risolto schiacciamento del pulsante `rollSeedBtn` ("Roll Seed"): assegnata larghezza fissa preferita di 84px e layout flessibile `Integer.MAX_VALUE` per `seedRow` e `genGroundBtn`
- [x] Ottimizzata la riga parametri `createParamRow` (Altezza Massima Rilievo e Livello Acqua): spinner compatto a 72px e gap a 6px per garantire piena visibilità a label, spinner e unità di misura 'm'
- [x] Rimosso blocco limite altezza finestra: ridotto `frame.setMinimumSize` da 1280x720 a 850x480 pixel
- [x] Implementato `WindowResizer` globale basato su `AWTEventListener` registrato su `Toolkit.getDefaultToolkit`: cattura e gestisce il ridimensionamento della finestra frameless su TUTTI i bordi (Nord, Sud, Est, Ovest e angoli), inclusa l'altezza dal bordo superiore (header) e inferiore (footer)

### 12. Spostamento Controlli Slope Map, Zoom e Scala nel Riquadro Centrale sotto le Coordinate [Commit: 28398b9]
- [x] Rimossi controlli di visualizzazione dalla toolbar superiore (`STRUMENTI`): rimossi pulsanti Zoom `+`, Zoom `-`, Scala `1:1`, pulsante `Slope Map [L]` e slider `Opacità`
- [x] Snellita la toolbar superiore che ora accoglie unicamente gli strumenti di disegno/editing, metallo mex, controlli simmetria e undo/redo, evitando sovraffollamento e wrapping
- [x] Creato dock HUD interattivo flottante in `MapStudioPanel` (`buildCanvasHudDock`) ancorato nel riquadro centrale della mappa
- [x] Posizionato il dock comandi direttamente SOTTO al testo delle coordinate (`Pos: [x, y] | Mappa: ... | Zoom: ...`) in basso a sinistra della canvas
- [x] Il dock include: pulsante interattivo `Slope Map [L]` con stato visivo ON/OFF ambra e sincronizzazione con il tasto rapido [L], slider opacità pendenze, separatore verticale scuro, pulsante `Zoom -`, pulsante `Zoom +` e pulsante `1:1 Scala`
- [x] Spostata in alto la pillola delle coordinate (`hy = getHeight() - 52`) per creare un layout a due righe perfettamente coordinato e pulito: riga 1 = info coordinate e terreno, riga 2 = pulsanti e controlli di visualizzazione
- [x] Aggiunto assorbimento eventi mouse sul dock HUD per evitare click-through accidentali sulla mappa

### 13. Risoluzione Risoluzione di Apertura e Troncamento Testi nei Vari Pannelli
- [x] Rilevamento e ripristino automatico dello stato massimizzato: `BarMapStudio.initCustomApp` ora legge `lobby.setting("display", "maximized")` e, dopo aver rimosso le decorazioni native con `frame.dispose()` e `setUndecorated(true)`, riapplica `setMaximizedBounds(maxBounds)` e `setExtendedState(Frame.MAXIMIZED_BOTH)` evitando l'apertura forzata nella finestra ridotta a 1280x760
- [x] Geometria windowed di fallback ampia e centrata: per sessioni non massimizzate viene calcolata una dimensione ottimale (~90% schermo: fino a 1640x940 o `maxBounds.width - 60` x `maxBounds.height - 50`) perfettamente centrata per garantire che tutti i pannelli e i componenti abbiano respiro senza tagli
- [x] Persistenza bidirezionale stato finestra: salvataggio dello stato finestra (`display=maximized` o `display=window`) e dei bounds correnti in `launcher.properties` ad ogni azione di resize, drag o doppio click sull'header
- [x] Icona dinamica per il pulsante Ingrandisci/Ripristina: `WindowControlButton` visualizza ora nativamente due rettangoli sovrapposti (Ripristina) quando la finestra è massimizzata e un quadrato singolo quando è in modalità finestra
- [x] Sub-nav `MapsMasterPanel` responsive: etichetta hint snellita con tooltip ("Editor procedurale e gestione libreria mappe") e listener di auto-hide per larghezze inferiori a 880px, prevenendo collisioni coi pulsanti sub-tab
- [x] Sidebar Sinistra `MapStudioPanel`: testi snelliti e non troncabili ("Alterna Simmetria: Diag / X / Z [X]", "Quota Massima Terreno:", "Livello Mare / Acqua:", "Leviga Pendenze Ripide [Ramps]"), rimosso limite rigido di altezza sull'info box
- [x] Sidebar Destra `MapStudioPanel`: pulsanti compatti ("+ Auto Mex Basi (Spawn)", "+ Auto Reclaim (Rocce & Alberi)") con tooltip descrittivi, selettore Mex compatto e nuovo helper `setStatus(...)` con text-wrapping HTML a 2 righe per visualizzare i messaggi di stato completi senza ellissi
- [x] Top Toolbar `MapStudioPanel`: padding interno dei pulsanti ottimizzato (3, 6, 3, 6), menu a tendina metallo mex ridotto a 105px e testi di simmetria compatti per ospitare tutti i 14 strumenti su un'unica riga senza wrapping invisibile
- [x] Sub-tab Libreria Mappe: divider location proporzionale automatico impostato a `width - 360px` per mantenere l'elenco mappe sempre chiaro e spazioso
### 14. Architettura Dati: Origine di Biomi, Texture e Stili Rilievo (/plan)
- [x] Indagine approfondita sorgenti `com.faforever.neroxis`: tracciata intera catena di caricamento ed elaborazione
- [x] Origine Biomi: identificati i 13 biomi (`BiomeName.java`), percorso interno JAR `/custom_biome/<BiomeName>/` e i 5 file di configurazione (`materials.json`, `Light.scmlighting`, `WaterSettings.scmwtr`, `props.json`, `decals.json`)
- [x] Origine Texture: mappati i 3 livelli (anteprima UI con `previewColors` + illuminazione normale in `PreviewGenerator.java`, detail textures procedurali matematiche fBm/Voronoi in `DetailTextures.java`, e asset 3D BAR in `BiomeFlora.java`)
- [x] Origine Stili Rilievo: identificati i 18+ stili (`TerrainStyle.java`), pennelli alfa PNG in `/images/brushes/` (`Brushes.java`) e pipeline di calcolo procedurale (`BooleanMask`/`FloatMask`, `randomWalk`, erosione e simmetria)
- [x] Creazione artifact architetturale dedicato: `biome_texture_terrain_pipeline.md`

### 15. Finestra Specifiche Asset BAR e Supporto Asset Custom (Heightmaps, Texture, Modelli 3D)
- [x] Creazione componente dedicato `AssetSpecsDialog.java`: finestra dark moderna, non-modale, navigabile con 5 schede di approfondimento per Heightmap, Texture, Modelli 3D / Flora, Metallo & Mapinfo e Cartella Custom
- [x] Pulsante footer in basso a destra `[ ⓘ SPECS ]` con hover highlight, apribile e riapribile in qualsiasi momento
- [x] Requisiti tecnici e consigli dettagliati (formati 16-bit vs 8-bit, pendenze veicoli/bot, potenze di 2 DDS/TGA, canali splatting RGBA, budget poligonale flora 150-500 tris per evitare drop FPS, definizioni .lua)
- [x] Creazione directory `custom_assets/` (`heightmaps/`, `textures/`, `features/`) con guide `README` e pulsante di apertura diretta in Windows Explorer
- [x] Nuovo pulsante "Carica Texture Terreno..." in `MapStudioPanel` per applicare texture diffuse personali in tempo reale
- [x] Compilazione ed iniezione in `launcher.jar`, test di esecuzione completato con successo

### 16. Icone Vettoriali Specs (Risoluzione Tofu ☒), Apertura allo Start, Layout Sidebar Responsive [Commit: 3146ec6]
- [x] Risoluzione totale dei glifi tofu `[☒]` in `AssetSpecsDialog`: rimossi caratteri Unicode ed emoji (`ⓘ`, `✕`, `✓`, `•`, `🏔`, `🎨`, `🌲`, `⚙`, `📁`) e sostituiti con rendering vettoriale nativo Java2D (icona header "i", croce di chiusura animata in rosso all'hover, icone tab montagna, tavolozza a livelli, pino/flora, ingranaggio mapinfo e cartella explorer, spunte di successo verdi e triangoli di avviso ambra)
- [x] Tipografia unificata su font Poppins Bold e Roboto/Segoe UI, eliminando fallback a font serif o stili tipografici disomogenei
- [x] Apertura automatica all'avvio: implementato `AssetSpecsDialog.showOnStartup(frame, lobby)` in `BarMapStudio.initCustomApp` dopo l'avvio della finestra principale
- [x] Checkbox "Non mostrare più all'avvio" nella barra inferiore del dialog delle specifiche, con persistenza delle preferenze utente in `launcher.properties` (`hide_specs_on_start=true`)
- [x] Pulsante Specifiche posizionato in basso a sinistra in `MapStudioPanel`: nuovo pulsante in stile ciano fluorescente (`btnLeftSpecs` - "SPECIFICHE & REQUISITI ASSET") inserito direttamente sopra il box informativo della Slope Map
- [x] Pulsante vettoriale footer aggiornato: rimosso glifo unicode dal pulsante `SPECS` nel footer, sostituito con disegno vettoriale del cerchio informativo antialiased
- [x] Sidebar laterali fluide e responsive: introdotto `ScrollableTrackWidthPanel` (JPanel che implementa `Scrollable` vincolando la larghezza al viewport del `JScrollPane`), garantendo che gli elementi dei pannelli si restringano proporzionalmente quando si sposta il divider di `JSplitPane`
- [x] Margini laterali preservati: impostato padding rigido `EmptyBorder(12, 12, 12, 12)` sia sulla sidebar sinistra che su quella destra, garantendo 12px di margine sinistro e destro costanti senza overflow orizzontale
- [x] Ridotta dimensione minima delle sidebar a 180px per permettere all'utente di restringere i pannelli senza blocchi rigidi

### 17. Modulo Texture Studio, Generatore PBR Seamless e Splatting Multi-Materiale [Commit: 0a5ebc4]
- [x] Nuovo componente dedicato `TextureStudioPanel.java`: modulo completo dark mode per la generazione di texture procedurali e composizione multi-materiale
- [x] Motore Frattale Seamless 4D: campionamento toroidale su $(x=\cos 2\pi u, y=\sin 2\pi u, z=\cos 2\pi v, w=\sin 2\pi v)$ che garantisce matematicamente il 100% di continuità nei bordi (tiling seamless infinito senza giunture)
- [x] 8 Preset di materiale BAR integrati: Roccia & Falesia, Erba & Prato, Sabbia & Dune, Argilla Crepata, Neve & Ghiaccio, Basalto Vulcanico, Ghiaia & Ciottoli, Suolo Cristallino Alieno
- [x] Modalità Material Designer: regolazione interattiva di scala frequenza, ottave (1-8), persistenza/rugosità, contrasto cromatico, forza del rilievo bump 3D e color ramp a 3 stop con color pickers
- [x] Modalità Terrain Splatter (Multi-Materiale): composizione a 4 layer (Layer 1 Base/Valle R, Layer 2 Pendenze G, Layer 3 Spiaggia/Mare B, Layer 4 Picchi A)
- [x] Regole di Splatting Intelligenti: soglia pendenza (es. > 22° per la roccia) con transizione sfumata morbida (falloff), fascia di battigia/costa parametrabile, quota picchi e perturbazione frattale organica dei bordi
- [x] Visualizzatore Canvas Interattivo: 8 modalità di visualizzazione (Diffuse Composito, Mappa Splatting RGBA, Singoli Canali R/G/B/A in B&W, Normal Map 3D con illuminazione solare rotante al mouse, e verifica Seamless Tiling a griglia 2x2)
- [x] Pulsante primario `[ ★ APPLICA AL TERRENO DELLA MAPPA ]`: trasferisce la texture fusa e le maschere di splatting `tLow` e `tHigh` (calibrate per `BarMapExporter`) direttamente alla mappa attiva, tornando automaticamente a `MAPS`
- [x] Salvataggio ed Esportazione: tasti dedicati `Salva Texture`, `Esporta Normal Map` e `Apri Cartella Textures` (in `custom_assets/textures/`)
- [x] Navigazione Superiore in `BarMapStudio.java`: aggiunta la voce `TEXTURES` posizionata tra `MAPS` e `UNITS`, corredata da nuova icona vettoriale della tavolozza artistica Java2D
- [x] Collegamento rapido nell'Editor: aggiunto pulsante `Apri Texture Studio (Splatting)...` nella sidebar sinistra di `MapStudioPanel`

### 18. Riprogettazione Texture Studio: Layout Compatto 240px e Default Metodo B (File Maschera) [Commit: eea4450]
- [x] Risoluzione radicale dell'ingombro a schermo: sidebar laterale snellita da mezza schermata a soli 240px-260px, lasciando oltre l'80% dello schermo per il canvas della mappa
- [x] Implementato `ScrollableTrackWidthPanel` con vincolo rigido della larghezza al viewport del `JScrollPane`, azzerando qualsiasi overflow o forzatura orizzontale
- [x] Attivato di **Default il Metodo B (Caricamento Maschere da File)** per ciascuna delle 4 Zone: ogni zona offre in prima evidenza il pulsante `Carica Maschera` con visualizzazione immediata del file PNG/BMP caricato
- [x] Mantenuto il **Metodo A (Regola Automatica da Heightmap)** come fallback intelligente: se per una zona non viene caricato un file, il programma calcola in automatico le pendenze e le quote dalla mappa, evitando schermate rotte o vuote
- [x] Schede di Zona compatte a righe verticali separate: riga 1 (titolo + indicatore a pallino colorato), riga 2 (pulsante maschera + nome file), riga 3 (selettore materiale preset + caricamento texture personale)
- [x] Barra divisore impostata a 275px con limite minimo a 220px, garantendo massima flessibilità sia in modalità finestra che a schermo intero

### 19. Ripristino "Pialla Pendenze [Ramps]" e Reintegrazione Sliders Metodo A & PBR [Commit: 8975273]
- [x] **Risolto blocco "Pialla Pendenze":** In `MapStudioPanel.java`, la card `heightCard` ("2. RILIEVO & PENDENZE") aveva un vincolo rigido `setMaximumSize(new Dimension(Integer.MAX_VALUE, 260))`. Avendo 8 righe e pulsanti interni per un'altezza totale > 288px, il layout BoxLayout schiacciava e rendeva invisibile il pulsante `btnSmoothSlopes`. Il vincolo è stato portato a 420px e il pulsante è stato rinominato in `"Leviga / Pialla Pendenze [Ramps]"` con tooltip `"Appiana, pialla e leviga i dislivelli impraticabili per bot e veicoli (Anti-Cliff / Ramps)"`.
- [x] **Reintegrazione Sliders Metodo A in Texture Studio:** Aggiunta la sezione "REGOLE TERRENO (METODO A)" subito sotto le schede delle zone con 6 slider procedurali completi:
  - *Soglia Pendenza Roccia* (10° - 60°)
  - *Morbidezza Transizione Falloff* (2° - 20°)
  - *Quota Livello Acqua* (-50m - 300m)
  - *Fascia di Spiaggia / Costa* (2m - 40m)
  - *Quota Picchi Rocciosi / Neve* (40% - 95%)
  - *Perturbazione Frattale Bordi* (0% - 100%)
- [x] **Reintegrazione Sliders Dettaglio PBR:** Aggiunta la sezione "DETTAGLIO PBR & MATERIALI" con 5 slider di regolazione del rumore frattale:
  - *Scala Frequenza Frattale* (0.5x - 8.0x)
  - *Livello Dettaglio Ottave* (1 - 8)
  - *Rugosità / Persistenza* (10% - 90%)
  - *Contrasto Cromatico* (0.5x - 3.0x)
  - *Intensità Rilievo Normal Map* (0.1x - 3.0x)
- [x] **Architettura Layout a Due Righe Compatta:** Per evitare che i controlli forzino la larghezza orizzontale, ogni slider è strutturato su due livelli verticali (Riga 1: etichetta + valore dinamico allineato a destra; Riga 2: slider esteso al 100% della larghezza). L'intero pannello adotta `ScrollableTrackWidthPanel`, consentendo di alloggiare sia il Metodo B che il Metodo A e i controlli PBR entro i consueti 240px-260px senza toccare la visuale del canvas.

### 20. Sincronizzazione Live Mappa in Texture Studio ed Eliminazione Artefatti Circolari [Commit: 13aa881]
- [x] **Sincronizzazione Automatica Mappa Attiva:** Risolto il disallineamento per cui Texture Studio mostrava un terreno sintetico di default invece della mappa generata dall'utente. Ora `TextureStudioPanel.setActiveMap(SCMap, BufferedImage)` riceve sia la mappa attiva che il suo `groundPreview` non appena generati o caricati in `MAPS`, e ogni volta che si clicca il tab `[ TEXTURES ]`.
- [x] **Rilevazione 16-bit Heightmap Reale:** La generazione procedurale di Texture Studio ora legge la quota fisica reale in metri tramite `Raster.getPixel(sx, sy, pixelH) * scale` da `activeMap.getHeightmap()`, calcolando le pendenze e distribuendo la roccia sui rilievi effettivi della mappa creata.
- [x] **Eliminazione Anelli Concentrici (Fix Artefatto Visivo):** Sostituita la precedente formula trigonometrica sinusoidale `simplex4D` (responsabile dei cerchi concentrici ad effetto tronco d'albero) con un generatore frattale periodico Perlin (`periodicNoise2D` e `fbmPeriodic2D`). I materiali ora risultano 100% seamless e con una grana organica e fotorealistica (erba rigogliosa, roccia frastagliata, sabbia fine, neve alpina).
- [x] **Tassellazione Dettagliata sul Terreno:** Introdotta ripetizione ad alta frequenza dei materiali (`tileReps`) sulla superficie della mappa in base alla scala PBR, garantendo dettagli nitidi e naturali sia da vicino che da lontano.
- [x] **Nuova Modalità Ispezione Bioma:** Aggiunta la vista `2. Texture Bioma Originale (MAPS)` nel selettore delle viste per confrontare in ogni momento il terreno generato dal motore BAR con la nuova composizione procedurale multi-materiale.
- [x] **Aggiornamento Immediato Canvas:** Implementato `MapStudioCanvas.setGroundPreview(scaled)` per fare in modo che la pressione di `[ ★ APPLICA AL TERRENO DELLA MAPPA ]` ridipinga istantaneamente il canvas 3D dell'editor senza residui della vecchia texture.

### 21. Avvio Pulito in Texture Studio, Supporto Heightmap Esterne e Sync Bidirezionale [Commit: 69e19c9]
- [x] **Avvio Pulito (Clean Slate):** Texture Studio si avvia senza generazioni sintetiche fittizie in background, mostrando un messaggio chiaro di Empty State con istruzioni per iniziare.
- [x] **Caricamento Diretto Heightmap Esterne:** Aggiunto pulsante `[Carica Heightmap]` nella toolbar superiore di Texture Studio per importare heightmap PNG (16-bit/8-bit), RAW o BMP indipendentemente dalla mappa attiva.
- [x] **Sincronizzazione Bidirezionale con MAPS:** Trasferimento coerente di heightmap personalizzate, texture composte e maschere di splatting all'editor principale `MAPS` tramite `applyCallback`.

### 22. Risoluzione Vincolo 256px in `SCMap.setPreview` e Icone Vettoriali Native Java2D [Commit: 810d1d6]
- [x] **Risoluzione Errore "Image size does not match required size":** Rimosso il vecchio controllo `checkImageSize(preview, 256)` in `SCMap.setPreview`. Questo vincolo legacy di Supreme Commander bloccava l'applicazione di anteprime e texture fusi a 512px o 1024px sulle mappe quadrate (`width == height`). Ora qualsiasi risoluzione valida viene accettata sia in `applyComposedTerrain` che in `loadCustomTexture`.
- [x] **Regola MUST di Progetto & Icone Vettoriali Native Java2D:** Eliminati definitivamente tutti i caratteri Unicode ed emoji da `TextureStudioPanel.java` (`★`, `📂`, `🎲`, `°`) per prevenire glifi tofu (`☒`).
- [x] **Disegno Vettoriale Antialiased:** Implementate icone Java2D native per la stella (`createVectorStarIcon` con `Path2D`), cartella (`createVectorFolderIcon`), e dadi (`createVectorDiceIcon`), integrate sia nei pulsanti ghost che nel pulsante primario `[APPLICA AL TERRENO DELLA MAPPA]`.
- [x] **Verifica e Integrazione Beyond All Reason:** Test di generazione procedurale ed esportazione `.sdz` convalidato al 100% da CLI e verifica diretta del metodo `SCMap.setPreview` su mappe 256x256 con runtime Java 25.

### 23. Supporto Mappe Rettangolari in Texture Studio ed Esportazione Texture Personalizzate in BAR [Commit: 4d149cb]
- [x] **Supporto Mappe Rettangolari (Eliminato lo Schiacciamento):** In `TextureStudioPanel.java`, implementato `computeTargetResolution()` che calcola dinamicamente le dimensioni `resW x resH` preservando l'esatto aspect ratio della mappa (es. mappe 256x384 generano texture e anteprime a 512x768).
- [x] **Campionamento Isotropo Seamless dei Materiali:** Il calcolo delle coordinate di tiling `tx` e `ty` avanza allo stesso identico passo su X e Y con `tileScale`, impedendo qualsiasi deformazione ellittica o allungamento dei granuli e dei dettagli PBR su mappe non quadrate.
- [x] **Maschere e Mappe Normali Rettangolari:** Aggiornati `computeZoneMask` e `createNormalMap` per operare con matrici bidimensionali `float[resH][resW]` e immagini `BufferedImage(resW, resH)`.
- [x] **Risoluzione del Bug "GENERA MAPPA FINALE" (Texture Personalizzate Ignorate):**
  - Aggiunto il campo `customTexture` con getter e setter in `SCMap.java`.
  - In `MapStudioPanel.java`, sia `applyComposedTerrain` (da Texture Studio) che `doLoadCustomTextureFile` (da file esterno) registrano l'immagine ad alta risoluzione in `currentMap.setCustomTexture(...)`.
  - In `BarMapExporter.java`, la pipeline di rendering dei tile grafici (`.smt`), della minimap (`.smf`) e delle anteprime lobby ora campiona direttamente da `customTexture` con interpolazione bilineare quando presente, anziché ricadere sui colori procedurali del bioma.
  - Vengono preservati e modulati l'illuminazione solare 3D e il relief shading (`brightness = shading3`), le bruciature dei mex e dei geo vents e lo scurimento dell'acqua, garantendo una perfetta integrazione nel motore 3D di Beyond All Reason.
- [x] **Rigida Aderenza alle Regole di Progetto:** Zero emoji e zero caratteri non-ASCII su tutti i sorgenti (utilizzati solo caratteri ASCII standard e icone Java2D native).
- [x] **Collaudo e Validazione Binaria:** Test programmatico automatizzato con generazione di mappa rettangolare (256x384 -> 8x12 unità BAR) ed esportazione `.sdz`, verificando che la texture custom venga effettivamente scritta e campionata nei tile `.smt` e nell'anteprima `.png`.

### 24. Allineamento a Sinistra Intestazioni Sezioni in Texture Studio [Commit: a040f9e]
- [x] **Risoluzione Spostamento a Destra (Swing BoxLayout Misalignment):** In `TextureStudioPanel.java`, i titoli e le descrizioni delle sezioni 1, 2 e 3 venivano disegnati a partire dal centro orizzontale della sidebar anziché a filo margine sinistro, causando anche il troncamento del testo `2. REGOLE AUTOMATICHE (SLIDE...`.
- [x] **Radice del Problema:** I `JPanel` (schede delle zone e contenitori degli slider) avevano il default Swing `alignmentX = 0.5f`, mentre i `JLabel` avevano `alignmentX = 0.0f`. In `BoxLayout.Y_AXIS`, Swing fissa l'asse verticale al centro dei componenti 0.5f, posizionando l'angolo sinistro dei componenti 0.0f sulla linea mediana (x = 120px-130px).
- [x] **Fix Completo su Tutta la Gerarchia:**
  - Applicato `setAlignmentX(0f)` su `sec1Title`, `sec1Desc`, `sec2Title`, `sec2Desc`, `sec3Title`, `sec3Desc`.
  - Applicato `setAlignmentX(0f)` su `card` e tutti i pannelli interni di riga (`r1`, `r2`, `r3`) in `buildZoneCard`.
  - Applicato `setAlignmentX(0f)` su `container`, `top` e `slider` in `createCompactSliderRow`.
  - Introdotto l'helper `vstrut(int height)` che imposta `alignmentX = 0f` su tutti i distanziatori verticali (evitando che `Box.Filler` erediti 0.5f).
- [x] Tutte le 3 sezioni sono ora perfettamente allineate a sinistra con margini puliti, testo 100% leggibile e zero troncamenti.

### 25. Controlli HUD Slope Map, Opacita' e Zoom 1:1 in Texture Studio [Commit: 9e800db]
- [x] **Dock Controlli HUD Flottante in Texture Studio:** Integrato il dock HUD identico a quello di `MAPS` nell'angolo inferiore sinistro del canvas di Texture Studio (`TextureCanvas`), contenente:
  - Tasto `[Slope Map [L]]` / `[Slope Map: ON [L]]` con illuminazione ambra/arancione e sincronizzazione tasto di scelta rapida `L`.
  - Slider `Opacita':` (10% - 100%, default 70%) con traccia e cursore ciano antialiased FlatLaf per modulare la trasparenza dello slope map.
  - Separatore grafico verticale sottile.
  - Tasti Zoom Rapido: `[-]` (Zoom Indietro), `[+]` (Zoom Avanti), `[1:1 Scala]` (Ripristino zoom e centratura vista).
- [x] **Pill Badge Overlay Coordinate e Stato:** Introdotto il badge rettangolare arrotondato a sfondo scuro semitrasparente e bordo ciano posizionato direttamente sopra il dock HUD (`getHeight() - 52px`), con tracking in tempo reale:
  - Posizione cursore nel mondo di gioco `Pos: [X, Y]` o nella texture attiva.
  - Dimensioni mappa in unita' BAR e km effettivi (es. `Mappa: 4x4 (4 x 4 km)`).
  - Percentuale di Zoom live (es. `Zoom: 100%`).
  - Modalita' di visualizzazione attiva (`Vista: ...`).
  - Quota in metri e Pendenza in gradi con notazione pulita ASCII `(deg)` quando il mouse e' posizionato sul terreno.
- [x] **Calcolo e Rendering Sovrapposizione Slope Map:**
  - `triggerAsyncGeneration()` genera `renderedSlopeOverlay` alla medesima risoluzione `resW x resH` della texture, con codifica cromatica standard Beyond All Reason (Blu acqua, Verde transitabile tutti, Giallo difficoltoso veicoli, Arancio solo bot bipedi, Rosso dirupo impraticabile).
  - Se la mappa e' gia' presente in `MAPS`, il layer di slope map viene trasferito e sincronizzato istantaneamente senza necessita' di ricalcolo.
  - Il layer viene fuso in `AlphaComposite.SRC_OVER` con trasparenza regolabile in tempo reale dallo slider.
- [x] **Hotkeys e Guard Typing:**
  - Tasto `L`: attiva/disattiva Slope Map.
  - Tasti `+`, `-`, `=`, `0`, tastierino numerico: regolazione e reset zoom.
  - Protezione da digitazione (`!isTypingInText()`): le scorciatoie non interferiscono quando si digita testo o numeri nei campi della UI.
- [x] **Rigida Aderenza alle Regole MUST:** Zero caratteri non-ASCII (`deg` al posto del simbolo di grado, apici singoli standard), icone e pulsanti Swing vettoriali conformi.



