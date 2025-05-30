package lexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Analizador léxico que convierte el código fuente en una secuencia de tokens.
 */
public class Lexer {
    private final String fuente;
    private final List<Token> tokens;
    private int inicio;
    private int actual;
    private int linea;
    private int columna;
    private static final Map<String, Token.Tipo> palabrasClave;

    static {
        palabrasClave = new HashMap<>();
        palabrasClave.put("si", Token.Tipo.SI);
        palabrasClave.put("entonces", Token.Tipo.ENTONCES);
        palabrasClave.put("sino", Token.Tipo.SINO);
        palabrasClave.put("fin_si", Token.Tipo.FIN_SI);
        palabrasClave.put("repite", Token.Tipo.REPITE);
        palabrasClave.put("fin_repite", Token.Tipo.FIN_REPITE);
        palabrasClave.put("escribir", Token.Tipo.ESCRIBIR);
        palabrasClave.put("y", Token.Tipo.Y);
        palabrasClave.put("o", Token.Tipo.O);
        palabrasClave.put("no", Token.Tipo.NO);
    }

    public Lexer(String fuente) {
        this.fuente = fuente;
        this.tokens = new ArrayList<>();
        this.inicio = 0;
        this.actual = 0;
        this.linea = 1;
        this.columna = 1;
    }

    /**
     * Analiza el código fuente y retorna la lista de tokens.
     */
    public List<Token> analizarTokens() {
        while (!esFin()) {
            inicio = actual;
            analizarToken();
        }

        tokens.add(new Token(Token.Tipo.FIN_ARCHIVO, "", linea, columna));
        return tokens;
    }

    private void analizarToken() {
        char c = avanzar();
        switch (c) {
            case '(': agregarToken(Token.Tipo.PARENTESIS_IZQ); break;
            case ')': agregarToken(Token.Tipo.PARENTESIS_DER); break;
            case ';': agregarToken(Token.Tipo.PUNTO_COMA); break;
            case '+': agregarToken(Token.Tipo.SUMA); break;
            case '-': agregarToken(Token.Tipo.RESTA); break;
            case '*': agregarToken(Token.Tipo.MULTIPLICACION); break;
            case '/': 
                if (coincidir('/')) {
                    // Comentario de una línea
                    while (mirar() != '\n' && !esFin()) avanzar();
                } else {
                    agregarToken(Token.Tipo.DIVISION);
                }
                break;
            case '%': agregarToken(Token.Tipo.MODULO); break;
            case '=': agregarToken(coincidir('=') ? Token.Tipo.IGUAL : Token.Tipo.ASIGNACION); break;
            case '!': agregarToken(coincidir('=') ? Token.Tipo.DIFERENTE : Token.Tipo.ERROR); break;
            case '<': agregarToken(coincidir('=') ? Token.Tipo.MENOR_IGUAL : Token.Tipo.MENOR); break;
            case '>': agregarToken(coincidir('=') ? Token.Tipo.MAYOR_IGUAL : Token.Tipo.MAYOR); break;
            case ' ':
            case '\r':
            case '\t':
                // Ignorar espacios en blanco
                break;
            case '\n':
                linea++;
                columna = 1;
                break;
            case '"': cadena(); break;
            default:
                if (esDigito(c)) {
                    numero();
                } else if (esLetra(c)) {
                    identificador();
                } else {
                    agregarToken(Token.Tipo.ERROR);
                }
                break;
        }
    }

    private void cadena() {
        while (mirar() != '"' && !esFin()) {
            if (mirar() == '\n') {
                linea++;
                columna = 1;
            }
            avanzar();
        }

        if (esFin()) {
            agregarToken(Token.Tipo.ERROR);
            return;
        }

        // Consumir el cierre de la comilla
        avanzar();

        // Extraer el string sin las comillas
        String valor = fuente.substring(inicio + 1, actual - 1);
        agregarToken(Token.Tipo.CADENA, valor);
    }

    private void numero() {
        while (esDigito(mirar())) avanzar();

        // Buscar punto decimal
        if (mirar() == '.' && esDigito(mirarSiguiente())) {
            avanzar(); // Consumir el punto

            while (esDigito(mirar())) avanzar();
        }

        agregarToken(Token.Tipo.NUMERO, fuente.substring(inicio, actual));
    }

    private void identificador() {
        while (esAlfanumerico(mirar())) avanzar();

        String texto = fuente.substring(inicio, actual);
        Token.Tipo tipo = palabrasClave.getOrDefault(texto, Token.Tipo.IDENTIFICADOR);
        agregarToken(tipo);
    }

    private boolean coincidir(char esperado) {
        if (esFin()) return false;
        if (fuente.charAt(actual) != esperado) return false;

        actual++;
        columna++;
        return true;
    }

    private char mirar() {
        if (esFin()) return '\0';
        return fuente.charAt(actual);
    }

    private char mirarSiguiente() {
        if (actual + 1 >= fuente.length()) return '\0';
        return fuente.charAt(actual + 1);
    }

    private boolean esLetra(char c) {
        return (c >= 'a' && c <= 'z') ||
               (c >= 'A' && c <= 'Z') ||
               c == '_';
    }

    private boolean esAlfanumerico(char c) {
        return esLetra(c) || esDigito(c);
    }

    private boolean esDigito(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean esFin() {
        return actual >= fuente.length();
    }

    private char avanzar() {
        actual++;
        columna++;
        return fuente.charAt(actual - 1);
    }

    private void agregarToken(Token.Tipo tipo) {
        agregarToken(tipo, null);
    }

    private void agregarToken(Token.Tipo tipo, String lexema) {
        String texto = lexema != null ? lexema : fuente.substring(inicio, actual);
        tokens.add(new Token(tipo, texto, linea, columna - texto.length()));
    }
} 