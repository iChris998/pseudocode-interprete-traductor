package ast;

/**
 * Nodo AST que representa un identificador (nombre de variable).
 * Ejemplo: x, contador, nombre
 */
public class Identificador extends Expresion {
    private final String nombre;

    public Identificador(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public <T> T aceptar(VisitanteAST<T> visitante) {
        return visitante.visitarIdentificador(this);
    }

    public String getNombre() {
        return nombre;
    }
} 