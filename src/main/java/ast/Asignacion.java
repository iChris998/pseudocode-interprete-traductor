package ast;

/**
 * Nodo AST que representa una asignación de variable.
 * Ejemplo: x = 5 + 3
 */
public class Asignacion extends Declaracion {
    private final String nombre;
    private final Expresion valor;

    public Asignacion(String nombre, Expresion valor) {
        this.nombre = nombre;
        this.valor = valor;
    }

    @Override
    public <T> T aceptar(VisitanteAST<T> visitante) {
        return visitante.visitarAsignacion(this);
    }

    public String getNombre() {
        return nombre;
    }

    public Expresion getValor() {
        return valor;
    }
} 