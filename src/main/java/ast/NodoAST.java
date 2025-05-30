package ast;

/**
 * Interfaz base para todos los nodos del Árbol de Sintaxis Abstracta (AST).
 */
public interface NodoAST {
    /**
     * Acepta un visitante para implementar el patrón Visitor.
     * @param visitante El visitante que procesará este nodo
     * @param <T> El tipo de retorno del visitante
     * @return El resultado del procesamiento
     */
    <T> T aceptar(VisitanteAST<T> visitante);
} 