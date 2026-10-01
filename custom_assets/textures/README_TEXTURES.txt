# CARTELLA TEXTURE PERSONALI
Inserisci qui le tue texture diffuse, normali e maschere di terreno personalizzate.

SPECIFICHE TECNICHE PER TEXTURE BAR (RECOIL ENGINE):
1. DIFFUSE / PAVIMENTAZIONE GLOBALE:
   - Formati: DDS (compresso BC1/DXT1 senza alpha, oppure BC3/DXT5 con alpha) o PNG a 24-bit.
   - Risoluzione: Potenze di 2 (1024x1024, 2048x2048, fino a 4096x4096 per mappe grandi).
   - Tonalità: Evita neri puri (0,0,0) o bianchi puri (255,255,255) per consentire agli shader di illuminazione solare e ombre di risaltare la profondità.

2. DETAIL NORMAL & TEXTURE (DNTS - Microdettaglio terreno):
   - Formato: TGA 32-bit non compresso o DDS BC5/BC3.
   - Risoluzione: 1024 x 1024 pixel.
   - Canali:
     - Rosso (R): Normale Tangente X (nx)
     - Verde (G): Normale Tangente Y (ny)
     - Blu (B): Normale Tangente Z (nz, default 1.0)
     - Alpha (A): Mappa di altezza e rugosità del micro-dettaglio (Detail Heightpass)

3. MASCHERA DI SPLATTING (_splats.dds):
   - Formato: DDS BC3 / DXT5 a 4 canali RGBA:
     - Canale R: Peso Materiale 1 (Terreno base / Erba)
     - Canale G: Peso Materiale 2 (Pareti rocciose / Falesie)
     - Canale B: Peso Materiale 3 (Sabbia / Fondale marino)
     - Canale A: Peso Materiale 4 (Neve / Crepe / Detriti)
   - Tiling: Seamless (ripetizione continua senza giunture visibili).
