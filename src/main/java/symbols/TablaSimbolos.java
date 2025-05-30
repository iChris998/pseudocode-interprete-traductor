package symbols;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Tabla de símbolos que maneja variables y ámbitos anidados.
 */
public class TablaSimbolos {
    private final Stack<Map<String, Simbolo>> pilaAmbitos;
    
    public TablaSimbolos() {
        this.pilaAmbitos = new Stack<>();
        // Crear el ámbito global
        entrarAmbito();
    }
    
    /**
     * Entra a un nuevo ámbito.
     */
    public void entrarAmbito() {
        pilaAmbitos.push(new HashMap<>());
    }
    
    /**
     * Sale del ámbito actual.
     */
    public void salirAmbito() {
        if (pilaAmbitos.size() > 1) { // Mantener al menos el ámbito global
            pilaAmbitos.pop();
        }
    }
    
    /**
     * Define una variable en el ámbito actual.
     */
    public void definir(String nombre, Object valor) {
        Simbolo.TipoSimbolo tipo = Simbolo.inferirTipo(valor);
        Simbolo simbolo = new Simbolo(nombre, valor, tipo);
        pilaAmbitos.peek().put(nombre, simbolo);
    }
    
    /**
     * Obtiene el valor de una variable.
     */
    public Object obtener(String nombre) {
        Simbolo simbolo = buscarSimbolo(nombre);
        if (simbolo == null) {
            throw new ExcepcionSimbolos("Variable no definida: " + nombre);
        }
        return simbolo.getValor();
    }
    
    /**
     * Asigna un nuevo valor a una variable existente.
     */
    public void asignar(String nombre, Object valor) {
        Simbolo simbolo = buscarSimbolo(nombre);
        if (simbolo == null) {
            throw new ExcepcionSimbolos("Variable no definida: " + nombre);
        }
        
        // Verificar compatibilidad de tipos
        Simbolo.TipoSimbolo nuevoTipo = Simbolo.inferirTipo(valor);
        if (!sonTiposCompatibles(simbolo.getTipo(), nuevoTipo)) {
            throw new ExcepcionSimbolos(
                String.format("Incompatibilidad de tipos: no se puede asignar %s a %s", 
                            nuevoTipo, simbolo.getTipo()));
        }
        
        simbolo.setValor(valor);
    }
    
    /**
     * Busca un símbolo en todos los ámbitos, desde el más interno al más externo.
     */
    private Simbolo buscarSimbolo(String nombre) {
        // Buscar desde el ámbito más interno hacia el más externo
        for (int i = pilaAmbitos.size() - 1; i >= 0; i--) {
            Map<String, Simbolo> ambito = pilaAmbitos.get(i);
            if (ambito.containsKey(nombre)) {
                return ambito.get(nombre);
            }
        }
        return null;
    }
    
    /**
     * Verifica si existe una variable en cualquier ámbito.
     */
    public boolean existe(String nombre) {
        return buscarSimbolo(nombre) != null;
    }
    
    /**
     * Verifica si dos tipos son compatibles para asignación.
     */
    private boolean sonTiposCompatibles(Simbolo.TipoSimbolo tipoOriginal, Simbolo.TipoSimbolo nuevoTipo) {
        // Mismos tipos son compatibles
        if (tipoOriginal == nuevoTipo) {
            return true;
        }
        
        // Permitir asignación de entero a decimal
        if (tipoOriginal == Simbolo.TipoSimbolo.DECIMAL && nuevoTipo == Simbolo.TipoSimbolo.ENTERO) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Obtiene el símbolo completo (para debugging).
     */
    public Simbolo obtenerSimbolo(String nombre) {
        return buscarSimbolo(nombre);
    }
    
    /**
     * Retorna el número de ámbitos activos.
     */
    public int numeroAmbitos() {
        return pilaAmbitos.size();
    }
    
    /**
     * Limpia todas las variables del ámbito actual.
     */
    public void limpiarAmbitoActual() {
        if (!pilaAmbitos.isEmpty()) {
            pilaAmbitos.peek().clear();
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TablaSimbolos{\n");
        
        for (int i = pilaAmbitos.size() - 1; i >= 0; i--) {
            sb.append("  Ámbito ").append(i).append(": ");
            sb.append(pilaAmbitos.get(i)).append("\n");
        }
        
        sb.append("}");
        return sb.toString();
    }
} 