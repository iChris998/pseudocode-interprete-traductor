package interpreter;

import ast.*;
import lexer.Token;
import symbols.TablaSimbolos;

import java.io.PrintStream;
import java.util.List;

/**
 * Intérprete que ejecuta el código pseudocódigo representado en el AST.
 */
public class Interpreter implements VisitanteAST<Object> {
    private final TablaSimbolos tablaSimbolos;
    private final PrintStream salida;
    
    public Interpreter() {
        this(System.out);
    }
    
    public Interpreter(PrintStream salida) {
        this.tablaSimbolos = new TablaSimbolos();
        this.salida = salida;
    }
    
    /**
     * Interpreta un programa completo.
     */
    public void interpretar(Programa programa) {
        try {
            programa.aceptar(this);
        } catch (Exception e) {
            throw new ExcepcionInterpreter("Error durante la interpretación: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Object visitarPrograma(Programa nodo) {
        for (Declaracion declaracion : nodo.getDeclaraciones()) {
            declaracion.aceptar(this);
        }
        return null;
    }
    
    @Override
    public Object visitarAsignacion(Asignacion nodo) {
        Object valor = nodo.getValor().aceptar(this);
        
        // Si la variable ya existe, asignar. Si no, definir nueva.
        if (tablaSimbolos.existe(nodo.getNombre())) {
            tablaSimbolos.asignar(nodo.getNombre(), valor);
        } else {
            tablaSimbolos.definir(nodo.getNombre(), valor);
        }
        
        return null;
    }
    
    @Override
    public Object visitarSi(Si nodo) {
        Object condicion = nodo.getCondicion().aceptar(this);
        
        if (esVerdadero(condicion)) {
            tablaSimbolos.entrarAmbito();
            try {
                for (Declaracion declaracion : nodo.getBloqueEntonces()) {
                    declaracion.aceptar(this);
                }
            } finally {
                tablaSimbolos.salirAmbito();
            }
        } else if (nodo.getBloqueSino() != null) {
            tablaSimbolos.entrarAmbito();
            try {
                for (Declaracion declaracion : nodo.getBloqueSino()) {
                    declaracion.aceptar(this);
                }
            } finally {
                tablaSimbolos.salirAmbito();
            }
        }
        
        return null;
    }
    
    @Override
    public Object visitarRepite(Repite nodo) {
        while (true) {
            Object condicion = nodo.getCondicion().aceptar(this);
            if (!esVerdadero(condicion)) {
                break;
            }
            
            tablaSimbolos.entrarAmbito();
            try {
                for (Declaracion declaracion : nodo.getCuerpo()) {
                    declaracion.aceptar(this);
                }
            } finally {
                tablaSimbolos.salirAmbito();
            }
        }
        
        return null;
    }
    
    @Override
    public Object visitarEscribir(Escribir nodo) {
        Object valor = nodo.getExpresion().aceptar(this);
        salida.println(convertirACadena(valor));
        return null;
    }
    
    @Override
    public Object visitarExpresionBinaria(ExpresionBinaria nodo) {
        Object izquierda = nodo.getIzquierda().aceptar(this);
        Object derecha = nodo.getDerecha().aceptar(this);
        
        switch (nodo.getOperador().getTipo()) {
            case SUMA:
                return sumar(izquierda, derecha);
            case RESTA:
                return restar(izquierda, derecha);
            case MULTIPLICACION:
                return multiplicar(izquierda, derecha);
            case DIVISION:
                return dividir(izquierda, derecha);
            case MODULO:
                return modulo(izquierda, derecha);
            case IGUAL:
                return sonIguales(izquierda, derecha);
            case DIFERENTE:
                return !sonIguales(izquierda, derecha);
            case MAYOR:
                return comparar(izquierda, derecha) > 0;
            case MAYOR_IGUAL:
                return comparar(izquierda, derecha) >= 0;
            case MENOR:
                return comparar(izquierda, derecha) < 0;
            case MENOR_IGUAL:
                return comparar(izquierda, derecha) <= 0;
            case Y:
                return esVerdadero(izquierda) && esVerdadero(derecha);
            case O:
                return esVerdadero(izquierda) || esVerdadero(derecha);
            default:
                throw new ExcepcionInterpreter("Operador binario no soportado: " + nodo.getOperador().getTipo());
        }
    }
    
    @Override
    public Object visitarExpresionUnaria(ExpresionUnaria nodo) {
        Object operando = nodo.getExpresion().aceptar(this);
        
        switch (nodo.getOperador().getTipo()) {
            case RESTA:
                if (operando instanceof Integer) {
                    return -(Integer) operando;
                } else if (operando instanceof Double) {
                    return -(Double) operando;
                } else {
                    throw new ExcepcionInterpreter("No se puede negar: " + operando);
                }
            case NO:
                return !esVerdadero(operando);
            default:
                throw new ExcepcionInterpreter("Operador unario no soportado: " + nodo.getOperador().getTipo());
        }
    }
    
    @Override
    public Object visitarLiteral(Literal nodo) {
        return nodo.getValor();
    }
    
    @Override
    public Object visitarIdentificador(Identificador nodo) {
        return tablaSimbolos.obtener(nodo.getNombre());
    }
    
    // Métodos auxiliares para operaciones
    
    private Object sumar(Object izquierda, Object derecha) {
        if (izquierda instanceof String || derecha instanceof String) {
            return convertirACadena(izquierda) + convertirACadena(derecha);
        }
        
        if (izquierda instanceof Integer && derecha instanceof Integer) {
            return (Integer) izquierda + (Integer) derecha;
        }
        
        return convertirADecimal(izquierda) + convertirADecimal(derecha);
    }
    
    private Object restar(Object izquierda, Object derecha) {
        if (izquierda instanceof Integer && derecha instanceof Integer) {
            return (Integer) izquierda - (Integer) derecha;
        }
        
        return convertirADecimal(izquierda) - convertirADecimal(derecha);
    }
    
    private Object multiplicar(Object izquierda, Object derecha) {
        if (izquierda instanceof Integer && derecha instanceof Integer) {
            return (Integer) izquierda * (Integer) derecha;
        }
        
        return convertirADecimal(izquierda) * convertirADecimal(derecha);
    }
    
    private Object dividir(Object izquierda, Object derecha) {
        double der = convertirADecimal(derecha);
        if (der == 0) {
            throw new ExcepcionInterpreter("División por cero");
        }
        
        return convertirADecimal(izquierda) / der;
    }
    
    private Object modulo(Object izquierda, Object derecha) {
        if (!(izquierda instanceof Integer) || !(derecha instanceof Integer)) {
            throw new ExcepcionInterpreter("El operador módulo solo funciona con enteros");
        }
        
        int der = (Integer) derecha;
        if (der == 0) {
            throw new ExcepcionInterpreter("División por cero en módulo");
        }
        
        return (Integer) izquierda % der;
    }
    
    private boolean sonIguales(Object izquierda, Object derecha) {
        if (izquierda == null && derecha == null) return true;
        if (izquierda == null || derecha == null) return false;
        
        // Comparación especial para números
        if (esNumero(izquierda) && esNumero(derecha)) {
            return convertirADecimal(izquierda).equals(convertirADecimal(derecha));
        }
        
        return izquierda.equals(derecha);
    }
    
    private int comparar(Object izquierda, Object derecha) {
        if (esNumero(izquierda) && esNumero(derecha)) {
            Double izq = convertirADecimal(izquierda);
            Double der = convertirADecimal(derecha);
            return izq.compareTo(der);
        }
        
        if (izquierda instanceof String && derecha instanceof String) {
            return ((String) izquierda).compareTo((String) derecha);
        }
        
        throw new ExcepcionInterpreter("No se pueden comparar estos tipos: " + 
                                     izquierda.getClass() + " y " + derecha.getClass());
    }
    
    private boolean esVerdadero(Object objeto) {
        if (objeto == null) return false;
        if (objeto instanceof Boolean) return (Boolean) objeto;
        if (objeto instanceof Integer) return (Integer) objeto != 0;
        if (objeto instanceof Double) return (Double) objeto != 0.0;
        if (objeto instanceof String) return !((String) objeto).isEmpty();
        return true;
    }
    
    private boolean esNumero(Object objeto) {
        return objeto instanceof Integer || objeto instanceof Double;
    }
    
    private Double convertirADecimal(Object objeto) {
        if (objeto instanceof Integer) {
            return ((Integer) objeto).doubleValue();
        } else if (objeto instanceof Double) {
            return (Double) objeto;
        } else {
            throw new ExcepcionInterpreter("No se puede convertir a número: " + objeto);
        }
    }
    
    private String convertirACadena(Object objeto) {
        if (objeto == null) return "null";
        return objeto.toString();
    }
    
    /**
     * Obtiene la tabla de símbolos actual (para debugging/testing).
     */
    public TablaSimbolos getTablaSimbolos() {
        return tablaSimbolos;
    }
} 