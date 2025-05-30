package ast;

import lexer.Token;

/**
 * Nodo AST que representa una expresión binaria.
 * Ejemplo: x + 5, a == b, x y y
 */
public class ExpresionBinaria extends Expresion {
    private final Expresion izquierda;
    private final Token operador;
    private final Expresion derecha;

    public ExpresionBinaria(Expresion izquierda, Token operador, Expresion derecha) {
        this.izquierda = izquierda;
        this.operador = operador;
        this.derecha = derecha;
    }

    @Override
    public <T> T aceptar(VisitanteAST<T> visitante) {
        return visitante.visitarExpresionBinaria(this);
    }

    public Expresion getIzquierda() {
        return izquierda;
    }

    public Expresion getDerecha() {
        return derecha;
    }

    public Token getOperador() {
        return operador;
    }
} 