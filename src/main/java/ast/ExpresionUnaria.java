package ast;

import lexer.Token;

/**
 * Nodo AST que representa una expresión unaria.
 * Ejemplo: -x, no condicion
 */
public class ExpresionUnaria extends Expresion {
    private final Token operador;
    private final Expresion expresion;

    public ExpresionUnaria(Token operador, Expresion expresion) {
        this.operador = operador;
        this.expresion = expresion;
    }

    @Override
    public <T> T aceptar(VisitanteAST<T> visitante) {
        return visitante.visitarExpresionUnaria(this);
    }

    public Token getOperador() {
        return operador;
    }

    public Expresion getExpresion() {
        return expresion;
    }
} 