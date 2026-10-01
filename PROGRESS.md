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
| *Step 3* | `feat` | 01/10/2026 10:02 | Controlli GUI per MaxHeight, WaterLevel, Smooth Slopes e importazione da file |
| *Step 4* | `build` | In attesa | Ricompilazione patch, iniezione launcher.jar e collaudo finale |

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
- [ ] Compilazione classi in `patch/`
- [ ] Iniezione classi aggiornate in `extracted/{app}/app/launcher.jar`
- [ ] Test di generazione da CLI e verifica esportazione BAR
