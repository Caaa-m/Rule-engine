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

        State state = parseState(stateText);
        State initialState = snapshotState(state);

        Interpreter interpreter = new Interpreter();
        interpreter.execute(program, state);

        StaticAnalyzer analyzer = new StaticAnalyzer();
        analyzer.analyze(program, initialState);
    }

    private static String readFromStdin() throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while ((line = reader.readLine()) != null) sb.append(line).append("\n");
        return sb.toString();
    }

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

    private static State snapshotState(State original) {
        State copy = new State();
        for (String fact : original.getActiveFacts()) copy.addFact(fact);
        for (Map.Entry<String, Integer> entry : original.getVariables().entrySet()) {
            copy.setVariable(entry.getKey(), entry.getValue());
        }
        return copy;
    }
}
