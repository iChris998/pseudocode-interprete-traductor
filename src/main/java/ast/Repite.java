package ast;

import java.util.List;

/**
 * Nodo AST que representa un bucle repite.
 * Ejemplo: repite (x > 0) ... fin_repite
 */
public class Repite extends Declaracion {
    private final Expresion condicion;
    private final List<Declaracion> cuerpo;

    public Repite(Expresion condicion, List<Declaracion> cuerpo) {
        this.condicion = condicion;
        this.cuerpo = cuerpo;
    }

    @Override
    public <T> T aceptar(VisitanteAST<T> visitante) {
        return visitante.visitarRepite(this);
    }

    public Expresion getCondicion() {
        return condicion;
    }

    public List<Declaracion> getCuerpo() {
        return cuerpo;
    }
} 