package translator;

/**
 * Excepción lanzada cuando ocurre un error durante la traducción.
 */
public class ExcepcionTraductor extends RuntimeException {
    
    public ExcepcionTraductor(String mensaje) {
        super(mensaje);
    }
    
    public ExcepcionTraductor(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
} 