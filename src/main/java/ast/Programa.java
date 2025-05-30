package ast;

import java.util.List;

/**
 * Nodo raíz que representa un programa completo.
 */
public class Programa implements NodoAST {
    private final List<Declaracion> declaraciones;

    public Programa() {
        this.declaraciones = new java.util.ArrayList<>();
    }

    public Programa(List<Declaracion> declaraciones) {
        this.declaraciones = declaraciones;
    }

    @Override
    public <T> T aceptar(VisitanteAST<T> visitante) {
        return visitante.visitarPrograma(this);
    }

    public List<Declaracion> getDeclaraciones() {
        return declaraciones;
    }
} 