package ast;

/**
 * Interfaz del visitante para implementar el patrón Visitor sobre el AST.
 * @param <T> El tipo de resultado que retorna el visitante
 */
public interface VisitanteAST<T> {
    T visitarPrograma(Programa nodo);
    T visitarAsignacion(Asignacion nodo);
    T visitarSi(Si nodo);
    T visitarRepite(Repite nodo);
    T visitarEscribir(Escribir nodo);
    T visitarExpresionBinaria(ExpresionBinaria nodo);
    T visitarExpresionUnaria(ExpresionUnaria nodo);
    T visitarLiteral(Literal nodo);
    T visitarIdentificador(Identificador nodo);
} 