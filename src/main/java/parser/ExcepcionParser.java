package parser;

import lexer.Token;

/**
 * Excepción lanzada cuando ocurre un error durante el análisis sintáctico.
 */
public class ExcepcionParser extends RuntimeException {
    private final Token token;

    public ExcepcionParser(String mensaje, Token token) {
        super(String.format("%s en línea %d, columna %d cerca de '%s'",
                mensaje, token.getLinea(), token.getColumna(), token.getLexema()));
        this.token = token;
    }

    public Token getToken() {
        return token;
    }
} 