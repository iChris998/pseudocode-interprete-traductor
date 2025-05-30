package ast;

/**
 * Nodo AST que representa un valor literal.
 * Ejemplo: 42, 3.14, "Hola mundo"
 */
public class Literal extends Expresion {
    private final Object valor;

    public Literal(Object valor) {
        this.valor = valor;
    }

    @Override
    public <T> T aceptar(VisitanteAST<T> visitante) {
        return visitante.visitarLiteral(this);
    }

    public Object getValor() {
        return valor;
    }
} 