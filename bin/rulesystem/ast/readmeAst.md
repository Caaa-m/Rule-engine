# AST (Abstract Syntax Tree)

## ¿Qué es el AST?

El AST (Abstract Syntax Tree) es la representación estructurada del programa de reglas. En lugar de trabajar con texto o tokens, el sistema trabaja con objetos que representan la lógica del programa.

Ejemplo:

rule r1:
if temp > 30 then alert

Se representa como:

Program
└── Rule(r1)
├── Condition (temp > 30)
└── Action (alert)

---

## ¿Para qué sirve?

* Separar la estructura de la ejecución
* Permitir que el parser construya una representación clara
* Servir como base para:

  * Intérprete
  * Análisis estático


## Clases del AST

### Program.java

Responsabilidad:
Contener todas las reglas del sistema.

Uso:

* Punto de entrada del AST
* Se pasa al intérprete y al analyzer

Estructura típica:

* List<Rule> rules

Errores comunes:

* No inicializar la lista
* No exponer getRules()


### Rule.java

Responsabilidad:
Representar una regla completa.

Contiene:

* Nombre (name)
* Condición (Condition)
* Acción (Action)

Métodos clave:

* getSignature() usado por el analyzer para detectar duplicados

Errores comunes:

* No implementar getSignature()
* Incluir lógica de ejecución (no debe ejecutar nada)


### Condition.java (interfaz)

Responsabilidad:
Definir el comportamiento común de todas las condiciones.

Métodos:

* extractFacts() usado por el analyzer
* asString() usado para comparar reglas

Importante:

* No tiene implementación
* Obliga a las clases a definir comportamiento

Errores comunes:

* Intentar poner lógica dentro de la interfaz
* No implementar los métodos en las clases que la implementan


### FactCondition.java

Ejemplo:
if alert

Responsabilidad:
Representar condiciones basadas en hechos.

Métodos clave:

* extractFacts() devuelve el fact
* asString() devuelve el nombre del fact

Errores comunes:

* No agregar el fact al Set
* Retornar null en lugar de un Set


### ComparisonCondition.java

Ejemplo:
if temp > 30

Responsabilidad:
Representar comparaciones numéricas.

Métodos clave:

* extractFacts() retorna vacío
* asString() representación textual

Errores comunes:

* Tratar variables como facts
* No manejar correctamente operador y valor


### AndCondition.java

Ejemplo:
if alert AND temp > 30

Responsabilidad:
Combinar dos condiciones.

Métodos clave:

* extractFacts() combina ambos lados
* asString() representación recursiva

Errores comunes:

* No combinar correctamente los Sets
* No usar recursividad


### Action.java

Responsabilidad:
Representar el resultado de una regla.

Ejemplo:
then alert

Método clave:

* getFactName()

Errores comunes:

* Confundir acción con condición
* No encapsular correctamente el nombre


## Principios aplicados

* Encapsulación
* Polimorfismo (Condition)
* Bajo acoplamiento
* Separación de responsabilidades


## Errores comunes del AST

* Mezclar lógica del intérprete dentro del AST
* No usar interfaces correctamente
* Usar instanceof en lugar de polimorfismo
* No respetar los packages (rulesystem.ast)


## Resumen

El AST es el núcleo del sistema. Representa el programa de forma estructurada y permite que otros módulos trabajen sobre él sin depender del texto original.
