package rulesystem;

import rulesystem.analyzer.StaticAnalyzer;
import rulesystem.ast.Program;
import rulesystem.interpreter.Interpreter;
import rulesystem.interpreter.State;

public class main {

    public static void main(String[] args) {

        // TODO: aquí irá el lexer y parser cuando los tengas
        // Por ahora Program se construye manualmente para probar

        Program program = null; // reemplaza esto con tu parser

        // 1. Construir el estado inicial
        State state = new State();
        state.setVariable("temp", 35);
        // state.addFact("a");  // si hay hechos iniciales activos

        // 2. Ejecutar el intérprete
        Interpreter interpreter = new Interpreter();
        interpreter.execute(program, state);

        // 3. Análisis estático (siempre DESPUÉS del output de ejecución)
        StaticAnalyzer analyzer = new StaticAnalyzer();
        analyzer.analyze(program);
    }
}