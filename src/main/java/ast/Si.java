package ast;

import java.util.List;

/**
 * Nodo AST que representa una declaración condicional si-sino.
 * Ejemplo: si (x > 0) entonces ... sino ... fin_si
 */
public class Si extends Declaracion {
    private final Expresion condicion;
    private final List<Declaracion> bloqueEntonces;
    private final List<Declaracion> bloqueSino; // Puede ser null si no hay bloque sino

    public Si(Expresion condicion, List<Declaracion> bloqueEntonces, List<Declaracion> bloqueSino) {
        this.condicion = condicion;
        this.bloqueEntonces = bloqueEntonces;
        this.bloqueSino = bloqueSino;
    }

    @Override
    public <T> T aceptar(VisitanteAST<T> visitante) {
        return visitante.visitarSi(this);
    }

    public Expresion getCondicion() {
        return condicion;
    }

    public List<Declaracion> getBloqueEntonces() {
        return bloqueEntonces;
    }

    public List<Declaracion> getBloqueSino() {
        return bloqueSino;
    }
} 