package cli;

import ast.Programa;
import interpreter.Interpreter;
import lexer.Lexer;
import lexer.Token;
import parser.Parser;
import translator.TraductorPython;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

/**
 * Interfaz de línea de comandos para el intérprete de pseudocódigo.
 * 
 * Modos de uso:
 * - Sin argumentos: Modo interactivo
 * - Con archivo: Interpreta el archivo
 * - Con archivo y -t: Traduce a Python
 */
public class PseudocodeCLI {
    
    public static void main(String[] args) {
        PseudocodeCLI cli = new PseudocodeCLI();
        cli.iniciar();
    }
    
    private void iniciar() {
        mostrarBanner();
        mostrarMenu();
        
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;
        
        while (!salir) {
            System.out.print("\nSelecciona una opción (1-4): ");
            String opcion = scanner.nextLine().trim();
            
            switch (opcion) {
                case "1":
                    System.out.print("Ingresa la ruta del archivo: ");
                    String archivoInterpretar = scanner.nextLine().trim();
                    interpretarArchivo(archivoInterpretar);
                    mostrarMenu();
                    break;
                    
                case "2":
                    System.out.print("Ingresa la ruta del archivo: ");
                    String archivoTraducir = scanner.nextLine().trim();
                    traducirArchivo(archivoTraducir);
                    mostrarMenu();
                    break;
                    
                case "3":
                    mostrarAyuda();
                    mostrarMenu();
                    break;
                    
                case "4":
                    salir = true;
                    System.out.println("¡Hasta pronto!");
                    break;
                    
                default:
                    System.out.println("Opción no válida. Por favor, selecciona 1-4.");
                    mostrarMenu();
            }
        }
        
        scanner.close();
    }
    
    private void mostrarMenu() {
        System.out.println("\nMenú Principal:");
        System.out.println("1. Interpretar archivo");
        System.out.println("2. Traducir a Python");
        System.out.println("3. Ayuda");
        System.out.println("4. Salir");
    }
    
    private void mostrarBanner() {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║              INTÉRPRETE DE PSEUDOCÓDIGO v1.0                ║");
        System.out.println("║                                                              ║");
        System.out.println("║  Intérprete y traductor de pseudocódigo a Python            ║");
        System.out.println("║  Estructura de paquetes simplificada                        ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
    }
    
    private void mostrarAyuda() {
        System.out.println("\nUso del programa:");
        System.out.println("1. Interpretar archivo: Ejecuta el código pseudocódigo directamente");
        System.out.println("2. Traducir a Python: Convierte el código a Python");
        System.out.println("3. Ayuda: Muestra esta información");
        System.out.println("4. Salir: Termina el programa");
        System.out.println("\nSintaxis del pseudocódigo:");
        System.out.println("  Variables:     x = 5");
        System.out.println("  Condicional:   si (x > 0) entonces ... sino ... fin_si");
        System.out.println("  Bucle:         repite (x > 0) ... fin_repite");
        System.out.println("  Salida:        escribir \"Hola mundo\"");
        System.out.println("  Operadores:    +, -, *, /, %, ==, !=, <, >, <=, >=, y, o, no");
    }
    
    private void interpretarArchivo(String nombreArchivo) {
        try {
            String codigo = leerArchivo(nombreArchivo);
            System.out.println("\nInterpretando archivo: " + nombreArchivo);
            System.out.println("═".repeat(50));
            ejecutarCodigo(codigo, false);
        } catch (IOException e) {
            System.err.println("Error: No se pudo leer el archivo: " + nombreArchivo);
        }
    }
    
    private void traducirArchivo(String nombreArchivo) {
        try {
            String codigo = leerArchivo(nombreArchivo);
            System.out.println("\nTraduciendo archivo: " + nombreArchivo);
            System.out.println("═".repeat(50));
            
            String codigoPython = ejecutarCodigo(codigo, true);
            
            // Guardar archivo Python
            String nombreSalida = nombreArchivo.replaceAll("\\.[^.]*$", "") + ".py";
            Path archivoSalida = Paths.get(nombreSalida);
            Files.write(archivoSalida, codigoPython.getBytes());
            
            System.out.println("\n" + "═".repeat(50));
            System.out.println("Traducción guardada en: " + nombreSalida);
            
        } catch (IOException e) {
            System.err.println("Error: No se pudo procesar el archivo: " + nombreArchivo);
        }
    }
    
    private String ejecutarCodigo(String codigo, boolean traducir) {
        try {
            // Análisis léxico
            Lexer lexer = new Lexer(codigo);
            List<Token> tokens = lexer.analizarTokens();
            
            // Análisis sintáctico
            Parser parser = new Parser(tokens);
            Programa programa = parser.analizarPrograma();
            
            if (traducir) {
                // Traducir a Python
                TraductorPython traductor = new TraductorPython();
                String codigoPython = traductor.traducir(programa);
                System.out.println(codigoPython);
                return codigoPython;
            } else {
                // Interpretar
                Interpreter interpreter = new Interpreter();
                interpreter.interpretar(programa);
                return null;
            }
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return null;
        }
    }
    
    private String leerArchivo(String nombreArchivo) throws IOException {
        Path archivo = Paths.get(nombreArchivo);
        if (!Files.exists(archivo)) {
            throw new IOException("El archivo no existe: " + nombreArchivo);
        }
        return Files.readString(archivo);
    }
} 