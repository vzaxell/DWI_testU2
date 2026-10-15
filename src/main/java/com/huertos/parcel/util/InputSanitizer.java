package com.huertos.parcel.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Sanitización de entradas de texto. Se ejecuta ANTES de la validación (Bean Validation),
 * como primera capa de defensa. Las consultas JPA parametrizadas cubren inyección SQL.
 */
public final class InputSanitizer {

    private static final Pattern SCRIPT_STYLE_BLOCKS =
            Pattern.compile("<\\s*(script|style)[^>]*>.*?<\\s*/\\s*\\1\\s*>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern HTML_TAGS = Pattern.compile("<[^>]*>");
    private static final Pattern ANGLE_BRACKETS = Pattern.compile("[<>]");
    private static final Pattern CONTROL_CHARS = Pattern.compile("[\\p{Cntrl}\\p{Cf}]");
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    private InputSanitizer() { }

    public static String sanitize(String input) {
        if (input == null) {
            return null;
        }
        String s = Normalizer.normalize(input, Normalizer.Form.NFKC); // unifica formas Unicode
        s = SCRIPT_STYLE_BLOCKS.matcher(s).replaceAll(" ");           // elimina <script>/<style> completos
        s = HTML_TAGS.matcher(s).replaceAll(" ");                     // elimina etiquetas HTML
        s = ANGLE_BRACKETS.matcher(s).replaceAll("");                 // restos de < >
        s = CONTROL_CHARS.matcher(s).replaceAll(" ");                 // caracteres de control / invisibles
        s = WHITESPACE.matcher(s).replaceAll(" ");                    // colapsa espacios
        return s.trim();
    }
}
