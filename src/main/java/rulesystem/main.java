package rulesystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rulesystem.analyzer.StaticAnalyzer;
import rulesystem.ast.Program;
import rulesystem.interpreter.Interpreter;
import rulesystem.interpreter.State;

public class Main {

    public static void main(String[] args) throws IOException, Parser.ParseException {

        String input;
        if (args.length > 0) {
            input = new String(Files.readAllBytes(Paths.get(args[0])));
        } else {
            input = readFromStdin();
        }

        String[] sections = splitInputByKeyword(input);
        String rulesText = sections[0];
        String stateText = sections[1];

        Program program;
        if (rulesText.trim().isEmpty()) {
            program = new Program(new ArrayList<>());
        } else {
            Lexer lexer = new Lexer(rulesText);
            List<Lexer.Token> tokens = lexer.tokenize();
            Parser parser = new Parser(tokens);
            program = parser.parse();
        }

        // Build initial state and take snapshot before interpreter modifies it
        State state = parseState(stateText);
        State initialState = snapshotState(state);

        // Run interpreter
        Interpreter interpreter = new Interpreter();
        interpreter.execute(program, state);

        // Static analysis uses the original initial state (before execution)
        StaticAnalyzer analyzer = new StaticAnalyzer();
        analyzer.analyze(program, initialState);
    }

    /** Reads all of stdin until EOF */
    private static String readFromStdin() throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while ((line = reader.readLine()) != null) sb.append(line).append("\n");
        return sb.toString();
    }

    /**
     * Splits input into two parts:
     *   - lines starting with "rule" or "if" → rules section
     *   - everything else → state section
     *
     * Note: this assumes "if <condition> then <action>" is always on a single line,
     * which matches the format defined in the project spec.
     */
    private static String[] splitInputByKeyword(String input) {
        StringBuilder rulesBuilder = new StringBuilder();
        StringBuilder stateBuilder = new StringBuilder();

        for (String line : input.split("\n")) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;

            if (trimmed.startsWith("rule ") || trimmed.startsWith("rule\t")
                    || trimmed.startsWith("if ") || trimmed.startsWith("if\t")) {
                rulesBuilder.append(trimmed).append("\n");
            } else {
                stateBuilder.append(trimmed).append("\n");
            }
        }

        return new String[]{rulesBuilder.toString(), stateBuilder.toString()};
    }

    /**
     * Parses the state section:
     *   - "id = integer" lines → variable assignments
     *   - bare "id" lines → active facts
     */
    private static State parseState(String stateText) {
        State state = new State();
        if (stateText == null || stateText.trim().isEmpty()) return state;

        for (String line : stateText.split("\n")) {
            line = line.trim();
            if (line.isEmpty()) continue;

            if (line.contains("=")) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    String varName = parts[0].trim();
                    try {
                        int value = Integer.parseInt(parts[1].trim());
                        state.setVariable(varName, value);
                    } catch (NumberFormatException e) {
                        System.err.println("Warning: invalid value in line: " + line);
                    }
                }
            } else {
                state.addFact(line);
            }
        }

        return state;
    }

    /** Copies the initial state (facts + variables) before the interpreter modifies it */
    private static State snapshotState(State original) {
        State copy = new State();
        for (String fact : original.getActiveFacts()) copy.addFact(fact);
        for (Map.Entry<String, Integer> entry : original.getVariables().entrySet()) {
            copy.setVariable(entry.getKey(), entry.getValue());
        }
        return copy;
    }

    // Lee todo stdin hasta EOF
    private static String readFromStdin() throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    // Líneas que empiezan con "rule" o "if" son reglas, todo lo demás es estado
    private static String[] splitInputByKeyword(String input) {
        StringBuilder rulesBuilder = new StringBuilder();
        StringBuilder stateBuilder = new StringBuilder();

        for (String line : input.split("\n")) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;

            if (trimmed.startsWith("rule ") || trimmed.startsWith("rule\t")
                    || trimmed.startsWith("if ") || trimmed.startsWith("if\t")) {
                rulesBuilder.append(trimmed).append("\n");
            } else {
                stateBuilder.append(trimmed).append("\n");
            }
        }

        return new String[]{rulesBuilder.toString(), stateBuilder.toString()};
    }

    // "id = integer" → variable, "id" solo → fact activo
    private static State parseState(String stateText) {
        State state = new State();
        if (stateText == null || stateText.trim().isEmpty()) return state;

        for (String line : stateText.split("\n")) {
            line = line.trim();
            if (line.isEmpty()) continue;

            if (line.contains("=")) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    String varName = parts[0].trim();
                    try {
                        int value = Integer.parseInt(parts[1].trim());
                        state.setVariable(varName, value);
                    } catch (NumberFormatException e) {
                        System.err.println("Warning: valor inválido en: " + line);
                    }
                }
            } else {
                state.addFact(line);
            }
        }

        return state;
    }
}
