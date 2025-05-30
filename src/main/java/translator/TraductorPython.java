package translator;

import ast.*;
import lexer.Token;

/**
 * Traductor que convierte el AST de pseudocódigo a código Python equivalente.
 */
public class TraductorPython implements VisitanteAST<String> {
    private int nivelIndentacion = 0;
    private static final String INDENTACION = "    "; // 4 espacios
    
    /**
     * Traduce un programa completo a Python.
     */
    public String traducir(Programa programa) {
        StringBuilder resultado = new StringBuilder();
        resultado.append("#!/usr/bin/env python3\n");
        resultado.append("# -*- coding: utf-8 -*-\n");
        resultado.append("# Código generado automáticamente desde pseudocódigo\n\n");
        
        String cuerpoPrincipal = programa.aceptar(this);
        if (!cuerpoPrincipal.trim().isEmpty()) {
            resultado.append("def main():\n");
            nivelIndentacion++;
            resultado.append(indentarLineas(cuerpoPrincipal));
            nivelIndentacion--;
            resultado.append("\n\nif __name__ == '__main__':\n");
            resultado.append("    main()\n");
        }
        
        return resultado.toString();
    }
    
    @Override
    public String visitarPrograma(Programa nodo) {
        StringBuilder resultado = new StringBuilder();
        
        for (Declaracion declaracion : nodo.getDeclaraciones()) {
            String linea = declaracion.aceptar(this);
            if (!linea.trim().isEmpty()) {
                resultado.append(linea);
                if (!linea.endsWith("\n")) {
                    resultado.append("\n");
                }
            }
        }
        
        return resultado.toString();
    }
    
    @Override
    public String visitarAsignacion(Asignacion nodo) {
        String nombre = nodo.getNombre();
        String valor = nodo.getValor().aceptar(this);
        return obtenerIndentacion() + nombre + " = " + valor;
    }
    
    @Override
    public String visitarSi(Si nodo) {
        StringBuilder resultado = new StringBuilder();
        
        String condicion = nodo.getCondicion().aceptar(this);
        resultado.append(obtenerIndentacion()).append("if ").append(condicion).append(":\n");
        
        nivelIndentacion++;
        if (nodo.getBloqueEntonces().isEmpty()) {
            resultado.append(obtenerIndentacion()).append("pass\n");
        } else {
            for (Declaracion declaracion : nodo.getBloqueEntonces()) {
                resultado.append(declaracion.aceptar(this));
                if (!resultado.toString().endsWith("\n")) {
                    resultado.append("\n");
                }
            }
        }
        nivelIndentacion--;
        
        if (nodo.getBloqueSino() != null && !nodo.getBloqueSino().isEmpty()) {
            resultado.append(obtenerIndentacion()).append("else:\n");
            nivelIndentacion++;
            for (Declaracion declaracion : nodo.getBloqueSino()) {
                resultado.append(declaracion.aceptar(this));
                if (!resultado.toString().endsWith("\n")) {
                    resultado.append("\n");
                }
            }
            nivelIndentacion--;
        }
        
        return resultado.toString();
    }
    
    @Override
    public String visitarRepite(Repite nodo) {
        StringBuilder resultado = new StringBuilder();
        
        String condicion = nodo.getCondicion().aceptar(this);
        resultado.append(obtenerIndentacion()).append("while ").append(condicion).append(":\n");
        
        nivelIndentacion++;
        if (nodo.getCuerpo().isEmpty()) {
            resultado.append(obtenerIndentacion()).append("pass\n");
        } else {
            for (Declaracion declaracion : nodo.getCuerpo()) {
                resultado.append(declaracion.aceptar(this));
                if (!resultado.toString().endsWith("\n")) {
                    resultado.append("\n");
                }
            }
        }
        nivelIndentacion--;
        
        return resultado.toString();
    }
    
    @Override
    public String visitarEscribir(Escribir nodo) {
        String expresion = nodo.getExpresion().aceptar(this);
        return obtenerIndentacion() + "print(" + expresion + ")";
    }
    
    @Override
    public String visitarExpresionBinaria(ExpresionBinaria nodo) {
        String izquierda = nodo.getIzquierda().aceptar(this);
        String derecha = nodo.getDerecha().aceptar(this);
        String operador = convertirOperadorBinario(nodo.getOperador());
        
        // Manejar precedencia con paréntesis si es necesario
        if (necesitaParentesis(nodo)) {
            return "(" + izquierda + " " + operador + " " + derecha + ")";
        }
        
        return izquierda + " " + operador + " " + derecha;
    }
    
    @Override
    public String visitarExpresionUnaria(ExpresionUnaria nodo) {
        String expresion = nodo.getExpresion().aceptar(this);
        String operador = convertirOperadorUnario(nodo.getOperador());
        
        return operador + expresion;
    }
    
    @Override
    public String visitarLiteral(Literal nodo) {
        Object valor = nodo.getValor();
        
        if (valor instanceof String) {
            // Escapar caracteres especiales en strings
            String cadena = (String) valor;
            cadena = cadena.replace("\\", "\\\\")
                          .replace("\"", "\\\"")
                          .replace("\n", "\\n")
                          .replace("\r", "\\r")
                          .replace("\t", "\\t");
            return "\"" + cadena + "\"";
        } else if (valor instanceof Integer) {
            return valor.toString();
        } else if (valor instanceof Double) {
            return valor.toString();
        } else if (valor instanceof Boolean) {
            return (Boolean) valor ? "True" : "False";
        } else {
            return "None";
        }
    }
    
    @Override
    public String visitarIdentificador(Identificador nodo) {
        return nodo.getNombre();
    }
    
    // Métodos auxiliares
    
    private String convertirOperadorBinario(Token operador) {
        switch (operador.getTipo()) {
            case SUMA: return "+";
            case RESTA: return "-";
            case MULTIPLICACION: return "*";
            case DIVISION: return "/";
            case MODULO: return "%";
            case IGUAL: return "==";
            case DIFERENTE: return "!=";
            case MAYOR: return ">";
            case MAYOR_IGUAL: return ">=";
            case MENOR: return "<";
            case MENOR_IGUAL: return "<=";
            case Y: return "and";
            case O: return "or";
            default:
                throw new ExcepcionTraductor("Operador binario no soportado: " + operador.getTipo());
        }
    }
    
    private String convertirOperadorUnario(Token operador) {
        switch (operador.getTipo()) {
            case RESTA: return "-";
            case NO: return "not ";
            default:
                throw new ExcepcionTraductor("Operador unario no soportado: " + operador.getTipo());
        }
    }
    
    private boolean necesitaParentesis(ExpresionBinaria nodo) {
        // Para simplificar, siempre usar paréntesis en expresiones complejas
        // Una implementación más sofisticada verificaría la precedencia real
        return !(nodo.getIzquierda() instanceof Literal || nodo.getIzquierda() instanceof Identificador) ||
               !(nodo.getDerecha() instanceof Literal || nodo.getDerecha() instanceof Identificador);
    }
    
    private String obtenerIndentacion() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nivelIndentacion; i++) {
            sb.append(INDENTACION);
        }
        return sb.toString();
    }
    
    private String indentarLineas(String texto) {
        if (texto.trim().isEmpty()) {
            return INDENTACION + "pass\n";
        }
        
        String[] lineas = texto.split("\n");
        StringBuilder resultado = new StringBuilder();
        
        for (String linea : lineas) {
            if (!linea.trim().isEmpty()) {
                resultado.append(INDENTACION).append(linea).append("\n");
            } else {
                resultado.append("\n");
            }
        }
        
        return resultado.toString();
    }
} 