# Manual de Usuario - Intérprete de Pseudocódigo

## Descripción
Este intérprete permite ejecutar código en pseudocódigo y traducirlo a Python. Está diseñado para ser una herramienta educativa que ayuda a entender los conceptos básicos de programación.

## Requisitos
- Java 17 o superior
- Maven (para compilar el proyecto)

## Instalación
1. Clona el repositorio
2. Navega al directorio del proyecto
3. Compila el proyecto con Maven:
   ```bash
   `mvn clean package`
   ```
   Este comando:
   - Limpia cualquier compilación anterior (`clean`)
   - Compila el código fuente
   - Genera el archivo JAR en la carpeta `target/`
   - El archivo JAR se llamará `pseudocode-interpreter.jar`

## Uso
Para usar el intérprete-traductor, simplemente ejecuta:
```bash
java -jar target/pseudocode-interpreter.jar
```

El programa mostrará un menú interactivo con las siguientes opciones:
1. Interpretar archivo
2. Traducir a Python
3. Ayuda
4. Salir

### Opciones del Menú

#### 1. Interpretar archivo
- Selecciona esta opción para ejecutar un archivo de pseudocódigo
- El programa te pedirá la ruta del archivo
- Ejemplo: `ejemplos/factorial.pseudo`

#### 2. Traducir a Python
- Selecciona esta opción para convertir un archivo de pseudocódigo a Python
- El programa te pedirá la ruta del archivo
- Generará un archivo `.py` en el mismo directorio
- Ejemplo: `ejemplos/factorial.pseudo` → `ejemplos/factorial.py`

#### 3. Ayuda
- Muestra información sobre el uso del programa
- Incluye ejemplos de sintaxis del pseudocódigo

#### 4. Salir
- Termina el programa

## Sintaxis del Pseudocódigo

### Variables
```pseudocode
x = 5
y = "Hola"
z = verdadero
```

### Condicionales
```pseudocode
si (x > 0) entonces
    escribir "x es positivo"
sino
    escribir "x es negativo"
fin_si
```

### Bucles
```pseudocode
repite (x > 0)
    x = x - 1
fin_repite
```

### Operadores
- Aritméticos: +, -, *, /, %
- Comparación: ==, !=, <, >, <=, >=
- Lógicos: y, o, no

### Entrada/Salida
```pseudocode
escribir "Hola mundo"
escribir x
```

## Ejemplos

### Ejemplo 1: Cálculo de Factorial
```pseudocode
// factorial.pseudo
numero = 5
factorial = 1
contador = 1

repite (contador <= numero)
    factorial = factorial * contador
    contador = contador + 1
fin_repite

escribir "El factorial de "
escribir numero
escribir " es: "
escribir factorial
```

**Salida esperada:**
```
El factorial de 
5
 es: 
120
```

### Ejemplo 2: Números Pares
```pseudocode
// numeros_pares.pseudo
numero = 1

escribir "Números pares del 1 al 20:"

repite (numero <= 20)
    si (numero % 2 == 0) entonces
        escribir numero
    fin_si
    numero = numero + 1
fin_repite

escribir "¡Terminado!"
```

**Salida esperada:**
```
Números pares del 1 al 20:
2
4
6
8
10
12
14
16
18
20
¡Terminado!
```

### Ejemplo 3: Operaciones Lógicas
```pseudocode
// Programa que demuestra operaciones lógicas y condicionales
a = 10
b = 5
c = 15

escribir "Valores iniciales:"
escribir "a = "
escribir a
escribir "b = "
escribir b
escribir "c = "
escribir c

// Comparaciones básicas
si (a > b) entonces
    escribir "a es mayor que b"
sino
    escribir "a no es mayor que b"
fin_si

// Operaciones lógicas
si (a > b y c > a) entonces
    escribir "a > b Y c > a es verdadero"
fin_si

si (a < b o a > 0) entonces
    escribir "a < b O a > 0 es verdadero"
fin_si

// Negación
si (no (a == b)) entonces
    escribir "a no es igual a b"
fin_si

// Cálculos
suma = a + b + c
escribir "La suma de a + b + c = "
escribir suma

promedio = suma / 3
escribir "El promedio es: "
escribir promedio
```

**Salida esperada:**
```
Valores iniciales:
a = 
10
b = 
5
c = 
15
a es mayor que b
a > b Y c > a es verdadero
a < b O a > 0 es verdadero
a no es igual a b
La suma de a + b + c = 
30
El promedio es: 
10
```

## Solución de Problemas
Si encuentras algún error:
1. Verifica que el archivo existe y tiene la extensión correcta (.pseudo)
2. Asegúrate de que la sintaxis del pseudocódigo es correcta
3. Revisa que estás usando el comando correcto
4. Asegúrate de que el JAR está en el directorio `target/`
5. Si el JAR no existe, ejecuta `mvn clean package` para generarlo

## Notas
- El intérprete es sensible a mayúsculas/minúsculas
- Los comentarios de una línea (usando `//`) son ignorados por el intérprete y no se traducen al código Python
- Las variables deben ser declaradas antes de usarse