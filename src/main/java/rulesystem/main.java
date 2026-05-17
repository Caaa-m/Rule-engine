package rulesystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import rulesystem.analyzer.StaticAnalyzer;
import rulesystem.ast.Program;
import rulesystem.interpreter.Interpreter;
import rulesystem.interpreter.State;

class Main {

    public static void main(String[] args) throws IOException, Parser.ParseException {
        
        // Read input from file or stdin
        String input;
        if (args.length > 0) {
            input = new String(Files.readAllBytes(Paths.get(args[0])));
        } else {
            input = readFromStdin();
        }
        
        // Parse the input into two parts: rules and initial state
        // Rules are separated from state by one or more blank lines
        String[] lines = input.split("\n");
        StringBuilder rulesBuilder = new StringBuilder();
        StringBuilder stateBuilder = new StringBuilder();
        
        boolean inState = false;
        for (String line : lines) {
            String trimmed = line.trim();
            
            // Empty line marks transition from rules to state
            if (trimmed.isEmpty()) {
                if (!inState) {
                    inState = true; // next non-empty line starts state section
                }
            } else {
                if (inState) {
                    stateBuilder.append(line).append("\n");
                } else {
                    rulesBuilder.append(line).append("\n");
                }
            }
        }
        
        String rulesInput = rulesBuilder.toString();
        String stateInput = stateBuilder.toString();
        
        // 1. Lex and parse the rules
        Lexer lexer = new Lexer(rulesInput);
        List<Lexer.Token> tokens = lexer.tokenize();
        
        Parser parser = new Parser(tokens);
        Program program = parser.parse();
        
        // 2. Build the initial state
        State state = parseState(stateInput);
        
        // 3. Execute the interpreter
        Interpreter interpreter = new Interpreter();
        interpreter.execute(program, state);
        
        // 4. Static analysis (AFTER execution output)
        StaticAnalyzer analyzer = new StaticAnalyzer();
        analyzer.analyze(program);
    }
    
    /**
     * Reads input from stdin until EOF
     */
    private static String readFromStdin() throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line;
        
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        
        return sb.toString();
    }
    
    /**
     * Parses the state section and returns a State object
     * Format:
     *   variable_name = value
     *   fact_name
     */
    private static State parseState(String stateInput) {
        State state = new State();
        
        if (stateInput == null || stateInput.trim().isEmpty()) {
            return state;
        }
        
        String[] lines = stateInput.split("\n");
        
        for (String line : lines) {
            line = line.trim();
            
            if (line.isEmpty()) {
                continue;
            }
            
            // Check if it's an assignment (variable = value)
            if (line.contains("=")) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    String varName = parts[0].trim();
                    try {
                        int value = Integer.parseInt(parts[1].trim());
                        state.setVariable(varName, value);
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing value: " + line);
                    }
                }
            } else {
                // It's a fact
                state.addFact(line);
            }
        }
        
        return state;
    }
}