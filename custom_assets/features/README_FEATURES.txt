# CARTELLA OGGETTI 3D & FLORA (FEATURES / PROPS)
Inserisci qui i tuoi modelli 3D personalizzati, file di definizione Lua e texture associate.

SPECIFICHE TECNICHE PER MODELLI 3D BAR (RECOIL ENGINE):
1. FORMATI FILE 3D:
   - Formato Nativo: Spring .s3o (Spring 3D Object - mesh binaria ad albero con orientamento +Z frontale, +Y verso l'alto).
   - Conversione da .OBJ: Esporta in formato OBJ triangolato dal tuo programma 3D (Blender, Maya, 3ds Max) e converti con UpSpring o script s3o-tools.

2. BUDGET POLIGONI (CRUCIALE PER LE PRESTAZIONI):
   - Flora (Alberi, Cespugli, Arbusti, Piante): 150 - 500 poligoni (Triangoli). MASSIMO 800 per modelli molto complessi.
     *AVVERTENZA:* Una mappa può contenere da 5.000 a oltre 20.000 alberi; modelli pesanti causano crolli drastici di FPS.
   - Rocce, Massi e Formazioni naturali: 100 - 600 poligoni.
   - Relitti piccoli (Tank, Bot distrutti): 300 - 1.000 poligoni.
   - Grandi Relitti / Landmark (Fari, Strutture aliene, Basi): 1.000 - 2.500 poligoni.

3. TEXTURE ATLAS:
   - Risoluzione: 512x512 o 1024x1024 pixel DDS (BC1 per opaco, BC3 per rami/foglie trasparenti).
   - Due texture atlas per modello:
     - Texture 1 (RGB = Colore Diffuse, Alpha = Colore Squadra / TeamColor).
     - Texture 2 (RGB = Normal Map, Alpha = Auto-illuminazione notturna o riflessi speculari).

4. FILE DI DEFINIZIONE LUA (features/<nome_feature>.lua):
   Ogni oggetto 3D deve avere un file .lua con questo schema:
   ```lua
   local featureDef = {
     name = "nome_oggetto",
     description = "Descrizione leggibile",
     object = "nome_modello.s3o",
     footprintX = 2,
     footprintZ = 2,
     blocking = true,
     reclaimable = true,
     energy = 50,     -- Energia ricavabile dal reclaim (legno)
     metal = 0,       -- Metallo ricavabile dal reclaim (relitti)
     damage = 250,    -- Punti ferita prima della distruzione
     collisionVolume = {
       type = "box",
       scales = { 32, 48, 32 }
     }
   }
   return lowerkeys({ [featureDef.name] = featureDef })
   ```
