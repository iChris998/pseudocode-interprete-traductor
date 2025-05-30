package symbols;

/**
 * Excepción lanzada cuando ocurre un error relacionado con la tabla de símbolos.
 */
public class ExcepcionSimbolos extends RuntimeException {
    
    public ExcepcionSimbolos(String mensaje) {
        super(mensaje);
    }
    
    public ExcepcionSimbolos(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
} 