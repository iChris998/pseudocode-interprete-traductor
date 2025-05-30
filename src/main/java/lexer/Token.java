package lexer;

/**
 * Representa un token léxico en el código fuente.
 */
public class Token {
    /**
     * Tipos de tokens posibles en el lenguaje.
     */
    public enum Tipo {
        // Palabras clave
        SI("si"),
        ENTONCES("entonces"),
        SINO("sino"),
        FIN_SI("fin_si"),
        REPITE("repite"),
        FIN_REPITE("fin_repite"),
        ESCRIBIR("escribir"),

        // Identificadores y literales
        IDENTIFICADOR(null),
        NUMERO(null),
        CADENA(null),

        // Operadores
        SUMA("+"),
        RESTA("-"),
        MULTIPLICACION("*"),
        DIVISION("/"),
        MODULO("%"),
        ASIGNACION("="),
        IGUAL("=="),
        DIFERENTE("!="),
        MENOR("<"),
        MAYOR(">"),
        MENOR_IGUAL("<="),
        MAYOR_IGUAL(">="),
        Y("y"),
        O("o"),
        NO("no"),

        // Delimitadores
        PARENTESIS_IZQ("("),
        PARENTESIS_DER(")"),
        PUNTO_COMA(";"),

        // Especiales
        FIN_ARCHIVO(null),
        ERROR(null);

        private final String lexema;

        Tipo(String lexema) {
            this.lexema = lexema;
        }
    }

    private final Tipo tipo;
    private final String lexema;
    private final int linea;
    private final int columna;

    public Token(Tipo tipo, String lexema, int linea, int columna) {
        this.tipo = tipo;
        this.lexema = lexema;
        this.linea = linea;
        this.columna = columna;
    }

    @Override
    public String toString() {
        return String.format("Token{tipo=%s, lexema='%s', linea=%d, columna=%d}",
                tipo, lexema, linea, columna);
    }

    public Tipo getTipo() {
        return tipo;
    }

    public String getLexema() {
        return lexema;
    }

    public int getLinea() {
        return linea;
    }

    public int getColumna() {
        return columna;
    }
} 