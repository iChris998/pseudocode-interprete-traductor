package ast;

/**
 * Nodo AST que representa una declaración de escritura/salida.
 * Ejemplo: escribir "Hola mundo"
 */
public class Escribir extends Declaracion {
    private final Expresion expresion;

    public Escribir(Expresion expresion) {
        this.expresion = expresion;
    }

    @Override
    public <T> T aceptar(VisitanteAST<T> visitante) {
        return visitante.visitarEscribir(this);
    }

    public Expresion getExpresion() {
        return expresion;
    }
} 