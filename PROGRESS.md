# STATO AVANZAMENTO: BAR MAP STUDIO

**Ultimo Aggiornamento:** 01 Ottobre 2026  
**Stato Generale:** In Corso (Step 0 completato)

---

## Registro dei Punti di Ripristino (Git Commits)

| Commit ID | Tipo | Data / Ora | Descrizione |
| :--- | :---: | :---: | :--- |
| *Baseline* | `chore` | 01/10/2026 09:47 | Baseline savepoint prima delle feature heightmap, pendenze e slope map |
| *Step 1* | `feat` | In attesa | Motore di calcolo pendenze, rigenerazione ground e smooth slopes |
| *Step 2* | `feat` | In attesa | Visualizzatore Slope Map in Canvas, HUD e ispezione quote/pendenza al cursore |
| *Step 3* | `feat` | In attesa | Controlli GUI per MaxHeight, WaterLevel, Smooth Slopes e importazione da file |
| *Step 4* | `build` | In attesa | Ricompilazione patch, iniezione launcher.jar e collaudo finale |

---

## Roadmap e Stato delle Funzionalità

### 1. Engine Rilievo e Pendenze (`GroundGenerator.java` & `SCMap.java`)
- [ ] Analisi quote minime e massime da heightmap (`minHeight`, `maxHeight`)
- [ ] Calcolo pendenze discrete con gradiente normalizzato
- [ ] Generazione maschera Slope Map colorata (standard Recoil: verde, giallo, arancio, rosso, blu)
- [ ] Rigenerazione ground (texture, bioma, normali, acqua) da heightmap
- [ ] Algoritmo di levigatura pendenze (*Smooth Slopes / Ramps*)

### 2. Rendering e Interfaccia Canvas (`MapStudioCanvas.java`)
- [ ] Layer sovrapposto Slope Map con trasparenza regolabile (0% - 100%)
- [ ] Scorciatoia da tastiera rapida `L` (Slope Map Toggle)
- [ ] Legenda grafica HUD a schermo (Veicoli, Bot, Invalicabile, Acqua)
- [ ] Ispezione dinamica al mouse (quota in metri e pendenza in gradi/valore)

### 3. Controlli Pannello Studio (`MapStudioPanel.java`)
- [ ] Slider e Spinner Quota Massima (`maxHeight`, 50m - 800m)
- [ ] Slider e Spinner Livello Acqua (`waterLevel`, -50m - +150m)
- [ ] Tasto "Rigenera Ground da Heightmap"
- [ ] Tasto "Carica Heightmap da File..." (supporto immagini esterne PNG/JPG)
- [ ] Tasto "Leviga Rampe / Smooth Slopes"
- [ ] Tasto rapido e slider trasparenza per la Slope Map nella HUD

### 4. Compilazione e Verifica Binari
- [ ] Compilazione classi in `patch/`
- [ ] Iniezione classi aggiornate in `extracted/{app}/app/launcher.jar`
- [ ] Test di generazione da CLI e verifica esportazione BAR
