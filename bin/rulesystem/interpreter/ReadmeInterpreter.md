# Intérprete

## ¿Qué es el Intérprete?

El Intérprete ejecuta un programa de reglas parseado sobre un estado inicial.
Aplica las reglas de forma iterativa hasta que no se deriven nuevos hechos (evaluación por punto fijo).

## ¿Cómo funciona?

1. Recibe un `Program` (lista de reglas) y un `State` inicial
2. Evalúa la condición de cada regla contra el estado actual
3. Si una condición es verdadera, activa el hecho correspondiente
4. Repite hasta que no se agreguen nuevos hechos (punto fijo)
5. Imprime los hechos activos resultantes en orden lexicográfico

## Modelo de Ejecución

La evaluación sigue una estrategia de **punto fijo**:

- Todas las reglas se verifican en cada iteración
- Un hecho se agrega solo una vez (sin duplicados)
- El orden de procesamiento de las reglas no afecta el resultado final (determinista)
- La ejecución se detiene cuando no se producen nuevos hechos

## Evaluación de Condiciones

| Tipo de condición      | Se evalúa como                                          |
|------------------------|---------------------------------------------------------|
| `ComparisonCondition`  | Compara el valor de una variable usando `>`, `<` o `=` |
| `FactCondition`        | Verifica si un hecho está actualmente activo            |
| `AndCondition`         | Ambas condiciones izquierda y derecha deben ser verdaderas |

## Formato de Salida

- Cada hecho activado se imprime en una línea separada
- Los hechos se ordenan en orden lexicográfico (alfabético)
- Si no se activa ningún hecho, imprime exactamente: `(no output)`
- Los mensajes de análisis estático se imprimen después de la salida de ejecución

## Clases

### Interpreter.java

Responsabilidad: Ejecutar el programa sobre un estado dado.

Método principal:
```java
interpreter.execute(program, state);
```

Internamente:
- Itera hasta alcanzar el punto fijo
- Llama a `evaluateCondition()` de forma recursiva para `AndCondition`
- Llama a `printOutput()` para mostrar los resultados en orden

### State.java

Responsabilidad: Mantener el estado en tiempo de ejecución del programa.

Contiene:
- `Map<String, Integer> variables` — asignaciones de variables (ej. `temp = 35`)
- `Set<String> activeFacts` — hechos actualmente activos (ej. `alert`)

Métodos clave:
```java
state.setVariable("temp", 35);
state.addFact("alert");
state.hasVariable("temp");
state.hasFact("alert");
state.getActiveFacts();
```

## Ejemplo de Uso

```java
State state = new State();
state.setVariable("temp", 35);

Interpreter interpreter = new Interpreter();
interpreter.execute(program, state);
```

## Relación con Otros Módulos

- Depende de: `rulesystem.ast` (Program, Rule, Condition, Action)
- Usado por: `rulesystem.main`
- Independiente de: `rulesystem.analyzer`

## Principios de Diseño Aplicados

- Responsabilidad única: `State` guarda datos, `Interpreter` ejecuta la lógica
- Bajo acoplamiento: el Intérprete no modifica el AST
- Determinismo: el punto fijo garantiza resultados independientes del orden
- Modularidad: claramente separado del lexer, parser y analyzer

## Errores Comunes

- Pasar `null` como `program` (debe ser un objeto `Program` parseado)
- Confundir variables con hechos (son independientes en `State`)
- Modificar el AST durante la ejecución (el AST debe permanecer sin cambios)
- Asumir que el orden de ejecución afecta el resultado (no lo hace)