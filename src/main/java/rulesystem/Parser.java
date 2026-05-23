package rulesystem;

import rulesystem.ast.*;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    
    private List<Lexer.Token> tokens;
    private int position;
    
    public Parser(List<Lexer.Token> tokens) { this.tokens = tokens; this.position = 0; }
    
    public Program parse() throws ParseException {
        List<Rule> rules = parseRuleList();
        if (!isAtEnd()) throw new ParseException("Expected EOF, but found: " + peek().value);
        return new Program(rules);
    }
    
    private List<Rule> parseRuleList() throws ParseException {
        List<Rule> rules = new ArrayList<>();
        while (peek().type == Lexer.TokenType.RULE) rules.add(parseRule());
        return rules;
    }
    
    private Rule parseRule() throws ParseException {
        consume(Lexer.TokenType.RULE, "Expected 'rule'");
        String name = consume(Lexer.TokenType.ID, "Expected rule name").value;
        consume(Lexer.TokenType.COLON, "Expected ':'");
        consume(Lexer.TokenType.IF, "Expected 'if'");
        Condition condition = parseCondition();
        consume(Lexer.TokenType.THEN, "Expected 'then'");
        String actionName = consume(Lexer.TokenType.ID, "Expected action name").value;
        return new Rule(name, condition, new Action(actionName));
    }
    
    private Condition parseCondition() throws ParseException {
        Condition left = parseAtomicCondition();
        if (peek().type == Lexer.TokenType.AND) {
            consume(Lexer.TokenType.AND, "Expected 'AND'");
            Condition right = parseCondition();
            return new AndCondition(left, right);
        }
        return left;
    }
    
    private Condition parseAtomicCondition() throws ParseException {
        String name = consume(Lexer.TokenType.ID, "Expected identifier").value;
        if (peek().type == Lexer.TokenType.GT || peek().type == Lexer.TokenType.LT || peek().type == Lexer.TokenType.EQ) {
            String op = consume(peek().type, "Expected operator").value;
            int value = Integer.parseInt(consume(Lexer.TokenType.VALUE, "Expected integer").value);
            return new ComparisonCondition(name, op, value);
        }
        return new FactCondition(name);
    }
    
    private Lexer.Token consume(Lexer.TokenType expected, String msg) throws ParseException {
        if (peek().type != expected) throw new ParseException(msg + ". Got: " + peek().value);
        return tokens.get(position++);
    }
    
    private Lexer.Token peek() {
        if (position >= tokens.size()) return tokens.get(tokens.size() - 1);
        return tokens.get(position);
    }
    
    private boolean isAtEnd() {
        return position >= tokens.size() || tokens.get(position).type == Lexer.TokenType.EOF;
    }
    
    public static class ParseException extends Exception {
        public ParseException(String message) { super(message); }
    }
}
