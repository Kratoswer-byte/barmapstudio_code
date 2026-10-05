package com.faforever.neroxis.generator.bar;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Scrive i "file di ingresso": un file per amico, da aprire con un doppio clic su un PC che ha solo BAR.
 * Il file scarica la mappa dall'indirizzo pubblico, ne controlla l'impronta, la mette nella cartella mappe di
 * BAR e avvia il gioco collegandosi alla partita dell'host.
 *
 * <p>Dentro ci sono soltanto: indirizzo pubblico della mappa, impronta, versione del motore, indirizzo
 * dell'host, nome del giocatore e password della partita. Nessuna chiave dello spazio cloud.</p>
 */
final class JoinFileWriter {
    /** Nomi dei giocatori: gli stessi caratteri ammessi da BAR. */
    static final Pattern NAME = Pattern.compile("[A-Za-z0-9_\\[\\]]{2,20}");
    static final Pattern PASSWORD = Pattern.compile("[A-Za-z0-9]{0,24}");
    static final Pattern HOST = Pattern.compile("[A-Za-z0-9.:\\-]{3,80}");
    private static final Pattern FILE = Pattern.compile("[A-Za-z0-9._\\-]{1,120}");
    private static final Pattern SHA = Pattern.compile("[0-9a-f]{64}");
    /** Usato solo nelle prove: una mappa presa da una cartella di questo PC. */
    private static final Pattern LOCAL_URL = Pattern.compile("file:///[A-Za-z0-9._~:/\\-]+");

    /** Tutto quello che serve a un amico per entrare. */
    record Match(String mapTitle, String mapUrl, String mapFile, String sha256, String engine, String host, int port, String password) {}

    private JoinFileWriter() {}

    /**
     * Scrive nella cartella un file .bat (Windows) e uno .sh (Linux e Mac) per ogni amico, piu' le istruzioni.
     *
     * @return i file scritti
     */
    static List<Path> write(Path folder, Match match, List<String> friends) throws IOException {
        require(SHA.matcher(match.sha256()).matches(), "sha256");
        require(FILE.matcher(match.mapFile()).matches(), "map file");
        require(FILE.matcher(match.engine()).matches(), "engine");
        require(HOST.matcher(match.host()).matches(), "host");
        require(PASSWORD.matcher(match.password()).matches(), "password");
        require(LOCAL_URL.matcher(match.mapUrl()).matches() || CloudStorage.SAFE_URL.matcher(match.mapUrl()).matches(), "map url");
        Files.createDirectories(folder);
        List<Path> written = new ArrayList<>();
        for (String friend : friends) {
            require(NAME.matcher(friend).matches(), "player name");
            Path bat = folder.resolve("join_" + friend + ".bat");
            Files.write(bat, fill(WINDOWS, match, friend, JoinFileWriter::batchText).replace("\n", "\r\n").getBytes(StandardCharsets.UTF_8));
            written.add(bat);
            Path sh = folder.resolve("join_" + friend + ".sh");
            Files.write(sh, fill(UNIX, match, friend, JoinFileWriter::shellText).getBytes(StandardCharsets.UTF_8));
            sh.toFile().setExecutable(true);
            written.add(sh);
        }
        Path readme = folder.resolve(I18n.t("join.readme.file"));
        Files.writeString(readme, I18n.t("join.readme", match.mapTitle(), match.host(), String.valueOf(match.port()), match.engine()).replace("\n", System.lineSeparator()));
        written.add(readme);
        return written;
    }

    private static void require(boolean ok, String what) {
        if (!ok) {
            throw new IllegalArgumentException("Not allowed in a join file: " + what);
        }
    }

    private static String fill(String template, Match match, String friend, java.util.function.UnaryOperator<String> text) {
        return template
            .replace("@TITLE@", text.apply(I18n.t("join.title", match.mapTitle())))
            .replace("@MSG_FIND@", text.apply(I18n.t("join.find")))
            .replace("@MSG_ASK@", text.apply(I18n.t("join.ask")))
            .replace("@MSG_NOBAR@", text.apply(I18n.t("join.nobar")))
            .replace("@MSG_NOENGINE@", text.apply(I18n.t("join.noengine", match.engine())))
            .replace("@MSG_HAVE@", text.apply(I18n.t("join.have")))
            .replace("@MSG_DOWNLOAD@", text.apply(I18n.t("join.download")))
            .replace("@MSG_DLFAIL@", text.apply(I18n.t("join.downloadfail")))
            .replace("@MSG_BADFILE@", text.apply(I18n.t("join.badfile")))
            .replace("@MSG_COPY@", text.apply(I18n.t("join.copy")))
            .replace("@MSG_ADMIN@", text.apply(I18n.t("join.admin")))
            .replace("@MSG_COPYFAIL@", text.apply(I18n.t("join.copyfail")))
            .replace("@MSG_CONNECT@", text.apply(I18n.t("join.connect", friend)))
            .replace("@MSG_KEY@", text.apply(I18n.t("join.key")))
            .replace("@URL@", match.mapUrl())
            .replace("@FILE@", match.mapFile())
            .replace("@SHA@", match.sha256())
            .replace("@ENGINE@", match.engine())
            .replace("@HOST@", match.host())
            .replace("@PORT@", String.valueOf(match.port()))
            .replace("@PLAYER@", friend)
            .replace("@PASSWORD@", match.password());
    }

    /** Testo sicuro dopo un "echo" di un file .bat. */
    static String batchText(String text) {
        StringBuilder out = new StringBuilder();
        for (char c : text.replace('\n', ' ').replace("\r", "").toCharArray()) {
            switch (c) {
                case '%' -> out.append("%%");
                case '^', '&', '<', '>', '|', '(', ')', '"' -> out.append('^').append(c);
                default -> out.append(c);
            }
        }
        return out.toString();
    }

    /** Testo sicuro tra apici singoli in uno script di shell. */
    static String shellText(String text) {
        return text.replace('\n', ' ').replace("\r", "").replace("'", "'\\''");
    }

    private static final String WINDOWS = """
        @echo off
        setlocal EnableExtensions DisableDelayedExpansion
        chcp 65001 >nul
        title @TITLE@
        set "MAPURL=@URL@"
        set "MAPFILE=@FILE@"
        set "MAPSHA=@SHA@"
        set "ENGINE=@ENGINE@"
        set "HOSTIP=@HOST@"
        set "HOSTPORT=@PORT@"
        set "PLAYER=@PLAYER@"
        set "PASSWORD=@PASSWORD@"

        echo @MSG_FIND@
        set "DATA="
        if defined BAR_DATA if exist "%BAR_DATA%\\engine" set "DATA=%BAR_DATA%"
        if not defined DATA if exist "%LOCALAPPDATA%\\Programs\\Beyond-All-Reason\\data\\engine" set "DATA=%LOCALAPPDATA%\\Programs\\Beyond-All-Reason\\data"
        if not defined DATA if exist "%ProgramFiles%\\Beyond-All-Reason\\data\\engine" set "DATA=%ProgramFiles%\\Beyond-All-Reason\\data"
        if not defined DATA if exist "%ProgramFiles(x86)%\\Beyond-All-Reason\\data\\engine" set "DATA=%ProgramFiles(x86)%\\Beyond-All-Reason\\data"
        if defined DATA goto :havebar
        echo @MSG_ASK@
        set /p "DATA=> "
        if not defined DATA goto :nobar
        set "DATA=%DATA:"=%"
        if exist "%DATA%\\engine" goto :havebar
        if exist "%DATA%\\data\\engine" set "DATA=%DATA%\\data"
        if exist "%DATA%\\engine" goto :havebar
        :nobar
        echo @MSG_NOBAR@
        goto :fail

        :havebar
        if exist "%DATA%\\engine\\%ENGINE%\\spring.exe" goto :haveengine
        echo @MSG_NOENGINE@
        goto :fail

        :haveengine
        if not exist "%DATA%\\maps" mkdir "%DATA%\\maps" >nul 2>&1
        set "TARGET=%DATA%\\maps\\%MAPFILE%"
        set "TMPMAP=%TEMP%\\%MAPFILE%"
        if not exist "%TARGET%" goto :download
        call :hash "%TARGET%"
        if /i not "%GOT%"=="%MAPSHA%" goto :download
        echo @MSG_HAVE@
        goto :launch

        :download
        echo @MSG_DOWNLOAD@
        curl.exe -L --fail --progress-bar -o "%TMPMAP%" "%MAPURL%"
        if errorlevel 1 goto :downloadfail
        call :hash "%TMPMAP%"
        if /i not "%GOT%"=="%MAPSHA%" goto :badfile
        echo @MSG_COPY@
        copy /y /b "%TMPMAP%" "%TARGET%" >nul 2>&1
        if not errorlevel 1 goto :copied
        echo @MSG_ADMIN@
        powershell -NoProfile -Command "Start-Process -FilePath $env:ComSpec -Verb RunAs -Wait -WindowStyle Hidden -ArgumentList ('/c copy /y /b \\"' + $env:TMPMAP + '\\" \\"' + $env:TARGET + '\\"')" >nul 2>&1
        call :hash "%TARGET%"
        if /i not "%GOT%"=="%MAPSHA%" goto :copyfail
        :copied
        del "%TMPMAP%" >nul 2>&1

        :launch
        set "SCRIPT=%TEMP%\\bar_join_%PLAYER%.txt"
        > "%SCRIPT%" echo [game]
        >>"%SCRIPT%" echo {
        >>"%SCRIPT%" echo \thostip=%HOSTIP%;
        >>"%SCRIPT%" echo \thostport=%HOSTPORT%;
        >>"%SCRIPT%" echo \tishost=0;
        >>"%SCRIPT%" echo \tmyplayername=%PLAYER%;
        if defined PASSWORD >>"%SCRIPT%" echo \tmypasswd=%PASSWORD%;
        >>"%SCRIPT%" echo }
        echo @MSG_CONNECT@
        if defined BAR_JOIN_TEST exit /b 0
        start "" /d "%DATA%\\engine\\%ENGINE%" "%DATA%\\engine\\%ENGINE%\\spring.exe" --write-dir "%DATA%" --isolation "%SCRIPT%"
        timeout /t 4 /nobreak >nul
        exit /b 0

        :hash
        set "GOT="
        for /f "skip=1 delims=" %%H in ('certutil -hashfile "%~1" SHA256 2^>nul') do if not defined GOT set "GOT=%%H"
        if defined GOT set "GOT=%GOT: =%"
        exit /b 0

        :downloadfail
        echo @MSG_DLFAIL@
        goto :fail
        :badfile
        del "%TMPMAP%" >nul 2>&1
        echo @MSG_BADFILE@
        goto :fail
        :copyfail
        echo @MSG_COPYFAIL@
        :fail
        echo.
        if defined BAR_JOIN_TEST exit /b 1
        pause
        exit /b 1
        """;

    private static final String UNIX = """
        #!/bin/sh
        # @TITLE@
        MAPURL='@URL@'
        MAPFILE='@FILE@'
        MAPSHA='@SHA@'
        ENGINE='@ENGINE@'
        HOSTIP='@HOST@'
        HOSTPORT='@PORT@'
        PLAYER='@PLAYER@'
        PASSWORD='@PASSWORD@'

        fail() {
            printf '%s\\n\\n%s\\n' "$1" '@MSG_KEY@'
            [ -n "$BAR_JOIN_TEST" ] || read -r _
            exit 1
        }
        hash_of() {
            if command -v sha256sum >/dev/null 2>&1; then
                sha256sum "$1" | cut -d ' ' -f 1
            else
                shasum -a 256 "$1" | cut -d ' ' -f 1
            fi
        }

        printf '%s\\n' '@MSG_FIND@'
        DATA=""
        for d in "$BAR_DATA" \\
            "${XDG_STATE_HOME:-$HOME/.local/state}/Beyond-All-Reason" \\
            "$HOME/Documents/Beyond-All-Reason" \\
            "$HOME/.var/app/info.beyondallreason.bar/.local/state/Beyond-All-Reason" \\
            "$HOME/.local/share/Beyond-All-Reason" \\
            "$HOME/Library/Application Support/Beyond-All-Reason-mac"; do
            if [ -n "$d" ] && [ -d "$d/engine" ]; then
                DATA="$d"
                break
            fi
        done
        if [ -z "$DATA" ]; then
            printf '%s\\n> ' '@MSG_ASK@'
            read -r DATA
        fi
        [ -n "$DATA" ] && [ -d "$DATA/engine" ] || fail '@MSG_NOBAR@'
        SPRING="$DATA/engine/$ENGINE/spring"
        [ -x "$SPRING" ] || fail '@MSG_NOENGINE@'

        mkdir -p "$DATA/maps"
        TARGET="$DATA/maps/$MAPFILE"
        if [ -f "$TARGET" ] && [ "$(hash_of "$TARGET")" = "$MAPSHA" ]; then
            printf '%s\\n' '@MSG_HAVE@'
        else
            printf '%s\\n' '@MSG_DOWNLOAD@'
            TMPMAP="${TMPDIR:-/tmp}/$MAPFILE"
            curl -L --fail --progress-bar -o "$TMPMAP" "$MAPURL" || fail '@MSG_DLFAIL@'
            if [ "$(hash_of "$TMPMAP")" != "$MAPSHA" ]; then
                rm -f "$TMPMAP"
                fail '@MSG_BADFILE@'
            fi
            printf '%s\\n' '@MSG_COPY@'
            mv -f "$TMPMAP" "$TARGET" || fail '@MSG_COPYFAIL@'
        fi

        SCRIPT="${TMPDIR:-/tmp}/bar_join_$PLAYER.txt"
        {
            printf '[game]\\n{\\n'
            printf '\\thostip=%s;\\n' "$HOSTIP"
            printf '\\thostport=%s;\\n' "$HOSTPORT"
            printf '\\tishost=0;\\n'
            printf '\\tmyplayername=%s;\\n' "$PLAYER"
            [ -z "$PASSWORD" ] || printf '\\tmypasswd=%s;\\n' "$PASSWORD"
            printf '}\\n'
        } > "$SCRIPT"
        printf '%s\\n' '@MSG_CONNECT@'
        [ -z "$BAR_JOIN_TEST" ] || exit 0
        cd "$DATA/engine/$ENGINE" && exec "$SPRING" --write-dir "$DATA" --isolation "$SCRIPT"
        """;
}
