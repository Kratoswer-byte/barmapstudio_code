#!/bin/sh
# BAR Map Studio per Linux e macOS. NON PROVATO: preparato insieme alla versione Windows.
# Richiede Java 25 o successivo (comando "java", oppure la variabile JAVA_HOME).
ROOT="$(cd "$(dirname "$0")" && pwd)"

if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then
    JAVA="$JAVA_HOME/bin/java"
elif command -v java >/dev/null 2>&1; then
    JAVA="java"
else
    echo "Java 25 or newer is required: install it and start this file again."
    exit 1
fi

# Dove il programma scrive impostazioni, cache e registri: accanto al programma se la cartella e' scrivibile,
# altrimenti nella cartella dati dell'utente.
HOME_DIR="$ROOT"
APPARG=""
if ! ( : > "$ROOT/.write-test" ) 2>/dev/null; then
    case "$(uname -s)" in
        Darwin) HOME_DIR="$HOME/Library/Application Support/BARMapStudio" ;;
        *) HOME_DIR="${XDG_CONFIG_HOME:-$HOME/.config}/BARMapStudio" ;;
    esac
    APPARG="-Dbarstudio.app=$ROOT"
else
    rm -f "$ROOT/.write-test"
fi
mkdir -p "$HOME_DIR/cache"

if [ -n "$APPARG" ]; then
    exec "$JAVA" -Xms48m -Xmx1g --enable-native-access=ALL-UNNAMED "-Dneroxis.home=$HOME_DIR" "$APPARG" \
        -cp "$ROOT/app/launcher.jar" com.faforever.neroxis.generator.bar.BarMapStudio
fi
exec "$JAVA" -Xms48m -Xmx1g --enable-native-access=ALL-UNNAMED "-Dneroxis.home=$HOME_DIR" \
    -cp "$ROOT/app/launcher.jar" com.faforever.neroxis.generator.bar.BarMapStudio
