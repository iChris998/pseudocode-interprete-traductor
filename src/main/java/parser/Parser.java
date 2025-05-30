package parser;

import ast.*;
import lexer.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * Analizador sintáctico que convierte una secuencia de tokens en un AST.
 * 
 * Gramática del pseudocódigo (CFG):
 * 
 * programa         → declaracion* FIN_ARCHIVO
 * declaracion      → asignacion | si | repite | escribir
 * asignacion       → IDENTIFICADOR "=" expresion
 * si               → "si" "(" expresion ")" "entonces" declaracion* ("sino" declaracion*)? "fin_si"
 * repite           → "repite" "(" expresion ")" declaracion* "fin_repite"
 * escribir         → "escribir" expresion
 * 
 * expresion        → logica
 * logica           → igualdad ( ("y" | "o") igualdad )*
 * igualdad         → comparacion ( ("==" | "!=") comparacion )*
 * comparacion      → termino ( (">" | ">=" | "<" | "<=") termino )*
 * termino          → factor ( ("+" | "-") factor )*
 * factor           → unario ( ("*" | "/" | "%") unario )*
 * unario           → ("no" | "-") unario | primario
 * primario         → NUMERO | CADENA | IDENTIFICADOR | "(" expresion ")"
 */
public class Parser {
    private final List<Token> tokens;
    private int actual = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    /**
     * Analiza los tokens y retorna el AST del programa.
     */
    public Programa analizarPrograma() {
        List<Declaracion> declaraciones = new ArrayList<>();
        
        while (!esFin()) {
            declaraciones.add(declaracion());
        }
        
        return new Programa(declaraciones);
    }

    private Declaracion declaracion() {
        if (coincidir(Token.Tipo.IDENTIFICADOR)) {
            return asignacion();
        }
        if (coincidir(Token.Tipo.SI)) {
            return si();
        }
        if (coincidir(Token.Tipo.REPITE)) {
            return repite();
        }
        if (coincidir(Token.Tipo.ESCRIBIR)) {
            return escribir();
        }
        
        throw new ExcepcionParser("Se esperaba una declaración", actual());
    }

    private Asignacion asignacion() {
        Token nombre = anterior();
        consumir(Token.Tipo.ASIGNACION, "Se esperaba '=' después del identificador");
        Expresion valor = expresion();
        return new Asignacion(nombre.getLexema(), valor);
    }

    private Si si() {
        consumir(Token.Tipo.PARENTESIS_IZQ, "Se esperaba '(' después de 'si'");
        Expresion condicion = expresion();
        consumir(Token.Tipo.PARENTESIS_DER, "Se esperaba ')' después de la condición");
        consumir(Token.Tipo.ENTONCES, "Se esperaba 'entonces' después de la condición");
        
        List<Declaracion> bloqueEntonces = new ArrayList<>();
        while (!verificar(Token.Tipo.SINO) && !verificar(Token.Tipo.FIN_SI) && !esFin()) {
            bloqueEntonces.add(declaracion());
        }
        
        List<Declaracion> bloqueSino = null;
        if (coincidir(Token.Tipo.SINO)) {
            bloqueSino = new ArrayList<>();
            while (!verificar(Token.Tipo.FIN_SI) && !esFin()) {
                bloqueSino.add(declaracion());
            }
        }
        
        consumir(Token.Tipo.FIN_SI, "Se esperaba 'fin_si'");
        return new Si(condicion, bloqueEntonces, bloqueSino);
    }

    private Repite repite() {
        consumir(Token.Tipo.PARENTESIS_IZQ, "Se esperaba '(' después de 'repite'");
        Expresion condicion = expresion();
        consumir(Token.Tipo.PARENTESIS_DER, "Se esperaba ')' después de la condición");
        
        List<Declaracion> cuerpo = new ArrayList<>();
        while (!verificar(Token.Tipo.FIN_REPITE) && !esFin()) {
            cuerpo.add(declaracion());
        }
        
        consumir(Token.Tipo.FIN_REPITE, "Se esperaba 'fin_repite'");
        return new Repite(condicion, cuerpo);
    }

    private Escribir escribir() {
        Expresion expresion = expresion();
        return new Escribir(expresion);
    }

    private Expresion expresion() {
        return logica();
    }

    private Expresion logica() {
        Expresion expr = igualdad();
        
        while (coincidir(Token.Tipo.Y, Token.Tipo.O)) {
            Token operador = anterior();
            Expresion derecha = igualdad();
            expr = new ExpresionBinaria(expr, operador, derecha);
        }
        
        return expr;
    }

    private Expresion igualdad() {
        Expresion expr = comparacion();
        
        while (coincidir(Token.Tipo.IGUAL, Token.Tipo.DIFERENTE)) {
            Token operador = anterior();
            Expresion derecha = comparacion();
            expr = new ExpresionBinaria(expr, operador, derecha);
        }
        
        return expr;
    }

    private Expresion comparacion() {
        Expresion expr = termino();
        
        while (coincidir(Token.Tipo.MAYOR, Token.Tipo.MAYOR_IGUAL, 
                        Token.Tipo.MENOR, Token.Tipo.MENOR_IGUAL)) {
            Token operador = anterior();
            Expresion derecha = termino();
            expr = new ExpresionBinaria(expr, operador, derecha);
        }
        
        return expr;
    }

    private Expresion termino() {
        Expresion expr = factor();
        
        while (coincidir(Token.Tipo.SUMA, Token.Tipo.RESTA)) {
            Token operador = anterior();
            Expresion derecha = factor();
            expr = new ExpresionBinaria(expr, operador, derecha);
        }
        
        return expr;
    }

    private Expresion factor() {
        Expresion expr = unario();
        
        while (coincidir(Token.Tipo.MULTIPLICACION, Token.Tipo.DIVISION, Token.Tipo.MODULO)) {
            Token operador = anterior();
            Expresion derecha = unario();
            expr = new ExpresionBinaria(expr, operador, derecha);
        }
        
        return expr;
    }

    private Expresion unario() {
        if (coincidir(Token.Tipo.NO, Token.Tipo.RESTA)) {
            Token operador = anterior();
            Expresion expr = unario();
            return new ExpresionUnaria(operador, expr);
        }
        
        return primario();
    }

    private Expresion primario() {
        if (coincidir(Token.Tipo.NUMERO)) {
            String lexema = anterior().getLexema();
            try {
                if (lexema.contains(".")) {
                    return new Literal(Double.parseDouble(lexema));
                } else {
                    return new Literal(Integer.parseInt(lexema));
                }
            } catch (NumberFormatException e) {
                throw new ExcepcionParser("Error al parsear número: " + lexema, anterior());
            }
        }
        if (coincidir(Token.Tipo.CADENA)) {
            return new Literal(anterior().getLexema());
        }
        if (coincidir(Token.Tipo.IDENTIFICADOR)) {
            return new Identificador(anterior().getLexema());
        }
        if (coincidir(Token.Tipo.PARENTESIS_IZQ)) {
            Expresion expr = expresion();
            consumir(Token.Tipo.PARENTESIS_DER, "Se esperaba ')' después de la expresión");
            return expr;
        }
        throw new ExcepcionParser("Se esperaba una expresión", actual());
    }

    // Métodos auxiliares
    private boolean coincidir(Token.Tipo... tipos) {
        for (Token.Tipo tipo : tipos) {
            if (verificar(tipo)) {
                avanzar();
                return true;
            }
        }
        return false;
    }

    private boolean verificar(Token.Tipo tipo) {
        if (esFin()) return false;
        return actual().getTipo() == tipo;
    }

    private Token avanzar() {
        if (!esFin()) actual++;
        return anterior();
    }

    private boolean esFin() {
        return actual().getTipo() == Token.Tipo.FIN_ARCHIVO;
    }

    private Token actual() {
        return tokens.get(actual);
    }

    private Token anterior() {
        return tokens.get(actual - 1);
    }

    private Token consumir(Token.Tipo tipo, String mensaje) {
        if (verificar(tipo)) return avanzar();
        
        throw new ExcepcionParser(mensaje, actual());
    }
} 