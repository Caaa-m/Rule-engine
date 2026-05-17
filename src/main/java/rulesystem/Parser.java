package rulesystem;

import rulesystem.ast.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Recursive descent LL(1) parser for the rule-based language.
 * 
 * Grammar (transformed for LL(1)):
 *   Program → RuleList EOF
 *   RuleList → Rule RuleList | ε
 *   Rule → RULE ID COLON IF Condition THEN ID
 *   Condition → AtomicCondition ConditionTail
 *   ConditionTail → AND Condition | ε
 *   AtomicCondition → ID (ComparisonOp VALUE)?
 *   ComparisonOp → GT | LT | EQ
 */
public class Parser {
    
    private List<Lexer.Token> tokens;
    private int position;
    
    public Parser(List<Lexer.Token> tokens) {
        this.tokens = tokens;
        this.position = 0;
    }
    
    /**
     * Parses the token list and returns an AST Program node
     */
    public Program parse() throws ParseException {
        List<Rule> rules = parseRuleList();
        
        if (!isAtEnd()) {
            throw new ParseException("Expected EOF, but found: " + peek().value);
        }
        
        return new Program(rules);
    }
    
    /**
     * RuleList → Rule RuleList | ε
     */
    private List<Rule> parseRuleList() throws ParseException {
        List<Rule> rules = new ArrayList<>();
        
        while (peek().type == Lexer.TokenType.RULE) {
            rules.add(parseRule());
        }
        
        return rules;
    }
    
    /**
     * Rule → RULE ID COLON IF Condition THEN ID
     */
    private Rule parseRule() throws ParseException {
        consume(Lexer.TokenType.RULE, "Expected 'rule'");
        
        String ruleName = consume(Lexer.TokenType.ID, "Expected rule name").value;
        consume(Lexer.TokenType.COLON, "Expected ':'");
        consume(Lexer.TokenType.IF, "Expected 'if'");
        
        Condition condition = parseCondition();
        
        consume(Lexer.TokenType.THEN, "Expected 'then'");
        String actionName = consume(Lexer.TokenType.ID, "Expected action name").value;
        
        Action action = new Action(actionName);
        
        return new Rule(ruleName, condition, action);
    }
    
    /**
     * Condition → AtomicCondition ConditionTail
     */
    private Condition parseCondition() throws ParseException {
        Condition left = parseAtomicCondition();
        return parseConditionTail(left);
    }
    
    /**
     * ConditionTail → AND Condition | ε
     */
    private Condition parseConditionTail(Condition left) throws ParseException {
        if (peek().type == Lexer.TokenType.AND) {
            consume(Lexer.TokenType.AND, "Expected 'AND'");
            Condition right = parseCondition();
            return new AndCondition(left, right);
        }
        
        // epsilon case
        return left;
    }
    
    /**
     * AtomicCondition → ID (ComparisonOp VALUE)?
     * This produces either a ComparisonCondition or a FactCondition
     */
    private Condition parseAtomicCondition() throws ParseException {
        String variableName = consume(Lexer.TokenType.ID, "Expected identifier").value;
        
        // Check if there's a comparison operator
        if (isComparisonOp(peek().type)) {
            String operator = consume(peek().type, "Expected comparison operator").value;
            int value = Integer.parseInt(consume(Lexer.TokenType.VALUE, "Expected integer value").value);
            
            return new ComparisonCondition(variableName, operator, value);
        } else {
            // It's just a fact reference
            return new FactCondition(variableName);
        }
    }
    
    /**
     * Checks if the given token type is a comparison operator
     */
    private boolean isComparisonOp(Lexer.TokenType type) {
        return type == Lexer.TokenType.GT || 
               type == Lexer.TokenType.LT || 
               type == Lexer.TokenType.EQ;
    }
    
    /**
     * Consumes a token of the expected type
     */
    private Lexer.Token consume(Lexer.TokenType expected, String errorMessage) throws ParseException {
        if (peek().type != expected) {
            throw new ParseException(errorMessage + ". Got: " + peek().value);
        }
        
        return tokens.get(position++);
    }
    
    /**
     * Looks at the current token without consuming it
     */
    private Lexer.Token peek() {
        if (position >= tokens.size()) {
            return tokens.get(tokens.size() - 1); // EOF token
        }
        return tokens.get(position);
    }
    
    /**
     * Checks if we've reached the end of the token list
     */
    private boolean isAtEnd() {
        return position >= tokens.size() || (position < tokens.size() && tokens.get(position).type == Lexer.TokenType.EOF);
    }
    
    /**
     * Exception class for parsing errors
     */
    public static class ParseException extends Exception {
        public ParseException(String message) {
            super(message);
        }
    }
}
