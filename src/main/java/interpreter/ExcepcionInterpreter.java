package interpreter;

/**
 * Excepción lanzada cuando ocurre un error durante la interpretación.
 */
public class ExcepcionInterpreter extends RuntimeException {
    
    public ExcepcionInterpreter(String mensaje) {
        super(mensaje);
    }
    
    public ExcepcionInterpreter(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
} 