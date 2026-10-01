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
| `pending` | `fix` | 01/10/2026 13:05 | Ripristino massimizzazione all'avvio, bounds spaziosi e fix troncamento testi nei pannelli |

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

