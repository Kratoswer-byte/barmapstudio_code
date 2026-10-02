package com.faforever.neroxis.generator.bar;

import com.faforever.neroxis.lua.LuaLexer;
import com.faforever.neroxis.lua.LuaParser;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.util.ArrayList;
import java.util.List;

/**
 * Validatore di sintassi Lua nativo per script di animazione e gadget in BAR MAP STUDIO.
 * Utilizza il parser ANTLR4 integrato nel progetto per rilevare istantaneamente
 * errori sintattici, blocchi non chiusi e token inattesi.
 */
public final class LuaValidator {

    public static final record ValidationResult(
        boolean isValid,
        int errorLine,
        int errorColumn,
        String errorMessage,
        int totalLines,
        int statementCount
    ) {
        public static ValidationResult success(int lines, int statements) {
            return new ValidationResult(true, 0, 0, "Sintassi Lua corretta. Nessun errore rilevato.", lines, statements);
        }

        public static ValidationResult error(int line, int col, String msg, int lines) {
            return new ValidationResult(false, line, col, msg, lines, 0);
        }
    }

    private LuaValidator() {}

    /**
     * Valida il codice Lua passato come stringa.
     * Restituisce un ValidationResult con esito positivo o dettaglio puntuale dell'errore.
     */
    public static ValidationResult validate(String luaCode) {
        if (luaCode == null || luaCode.trim().isEmpty()) {
            return ValidationResult.success(0, 0);
        }

        int totalLines = luaCode.split("\r\n|\r|\n", -1).length;
        List<String> errors = new ArrayList<>();
        int[] firstErrorLoc = new int[2]; // line, col

        BaseErrorListener errorListener = new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                if (errors.isEmpty()) {
                    firstErrorLoc[0] = line;
                    firstErrorLoc[1] = charPositionInLine;
                    errors.add("Riga " + line + ", Col " + (charPositionInLine + 1) + ": " + cleanMessage(msg));
                }
            }
        };

        try {
            CharStream stream = CharStreams.fromString(luaCode);
            LuaLexer lexer = new LuaLexer(stream);
            lexer.removeErrorListeners();
            lexer.addErrorListener(errorListener);

            CommonTokenStream tokens = new CommonTokenStream(lexer);
            LuaParser parser = new LuaParser(tokens);
            parser.removeErrorListeners();
            parser.addErrorListener(errorListener);

            LuaParser.StartContext start = parser.start();

            if (!errors.isEmpty()) {
                return ValidationResult.error(firstErrorLoc[0], firstErrorLoc[1], errors.get(0), totalLines);
            }

            int statements = 0;
            if (start != null && start.block() != null && start.block().statement() != null) {
                statements = start.block().statement().size();
            }

            return ValidationResult.success(totalLines, statements);

        } catch (Exception ex) {
            return ValidationResult.error(firstErrorLoc[0] > 0 ? firstErrorLoc[0] : 1, firstErrorLoc[1], "Errore di analisi: " + ex.getMessage(), totalLines);
        }
    }

    private static String cleanMessage(String msg) {
        if (msg == null) return "Errore di sintassi sconosciuto";
        // Rende il messaggio leggibile in italiano per l'utente
        if (msg.contains("missing 'end'")) {
            return "Manca la parola chiave 'end' a chiusura del blocco";
        }
        if (msg.contains("mismatched input")) {
            return "Token inatteso: " + msg;
        }
        if (msg.contains("no viable alternative")) {
            return "Espressione o istruzione non valida: " + msg;
        }
        return msg;
    }
}
