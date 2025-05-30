package symbols;

import lombok.Getter;
import lombok.Setter;

/**
 * Representa un símbolo (variable) en la tabla de símbolos.
 */
@Getter
@Setter
public class Simbolo {
    private final String nombre;
    private Object valor;
    private final TipoSimbolo tipo;
    
    public Simbolo(String nombre, Object valor, TipoSimbolo tipo) {
        this.nombre = nombre;
        this.valor = valor;
        this.tipo = tipo;
    }
    
    /**
     * Tipos de datos soportados en el lenguaje.
     */
    public enum TipoSimbolo {
        ENTERO,
        DECIMAL,
        CADENA,
        BOOLEANO
    }
    
    /**
     * Infiere el tipo de un valor.
     */
    public static TipoSimbolo inferirTipo(Object valor) {
        if (valor instanceof Integer) {
            return TipoSimbolo.ENTERO;
        } else if (valor instanceof Double) {
            return TipoSimbolo.DECIMAL;
        } else if (valor instanceof String) {
            return TipoSimbolo.CADENA;
        } else if (valor instanceof Boolean) {
            return TipoSimbolo.BOOLEANO;
        }
        throw new IllegalArgumentException("Tipo no soportado: " + valor.getClass());
    }
    
    @Override
    public String toString() {
        return String.format("Simbolo{nombre='%s', valor=%s, tipo=%s}", 
                nombre, valor, tipo);
    }
    
    public Object getValor() { return valor; }
    public TipoSimbolo getTipo() { return tipo; }
    public void setValor(Object valor) { this.valor = valor; }
} 