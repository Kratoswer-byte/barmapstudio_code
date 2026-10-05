# Offuscamento di BAR Map Studio.
#
# Vengono rinominati solo i pacchetti dello Studio (generator.bar ed exporter.bar): il resto e' il generatore
# Neroxis, software libero con licenza MIT, e le librerie. Le classi vengono solo rinominate: niente
# rimozioni e niente ottimizzazioni, cosi' il comportamento resta identico a quello provato.
#
# -injars, -outjars, -libraryjars e -printmapping li passa build.ps1.

-dontshrink
-dontoptimize
-dontusemixedcaseclassnames
-dontnote
-dontwarn
-ignorewarnings

# i nomi dei pacchetti restano: icone e immagini vengono cercate accanto alla classe, e le preferenze
# dell'utente (lingua, cloud, ultime cartelle) sono salvate sotto il nome del pacchetto
-keeppackagenames com.faforever.neroxis.**

# niente nomi di file sorgente, numeri di riga e nomi di variabili locali nel programma distribuito
-keepattributes Signature,InnerClasses,EnclosingMethod,Record,PermittedSubclasses,NestHost,NestMembers,BootstrapMethods,RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations,RuntimeVisibleTypeAnnotations,AnnotationDefault

# tutto cio' che non e' lo Studio resta com'e'
-keep class !com.faforever.neroxis.generator.bar.**,!com.faforever.neroxis.exporter.bar.**,com.faforever.neroxis.** { *; }

# punto di ingresso
-keep class com.faforever.neroxis.generator.bar.BarMapStudio { public static void main(java.lang.String[]); }

# enumerazioni: valueOf e values vengono chiamati per nome dalla libreria di Java
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
