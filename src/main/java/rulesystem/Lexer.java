package rulesystem;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
    
    public enum TokenType {
        RULE, IF, THEN, AND,
        GT, LT, EQ, COLON,
        ID, VALUE,
        EOF
    }
    
    public static class Token {
        public TokenType type;
        public String value;
        public int line;
        public int column;
        
        public Token(TokenType type, String value, int line, int column) {
            this.type = type;
            this.value = value;
            this.line = line;
            this.column = column;
        }
    }
    
    private String input;
    private int position;
    private int line;
    private int column;
    
    public Lexer(String input) {
        this.input = input;
        this.position = 0;
        this.line = 1;
        this.column = 1;
    }
    
    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        while (position < input.length()) {
            skipWhitespace();
            if (position >= input.length()) break;
            Token token = nextToken();
            if (token != null && token.type != TokenType.EOF) tokens.add(token);
        }
        tokens.add(new Token(TokenType.EOF, "", line, column));
        return tokens;
    }
    
    private Token nextToken() {
        if (position >= input.length()) return new Token(TokenType.EOF, "", line, column);
        char c = input.charAt(position);
        int sl = line, sc = column;
        if (c == '>') { advance(); return new Token(TokenType.GT, ">", sl, sc); }
        if (c == '<') { advance(); return new Token(TokenType.LT, "<", sl, sc); }
        if (c == '=') { advance(); return new Token(TokenType.EQ, "=", sl, sc); }
        if (c == ':') { advance(); return new Token(TokenType.COLON, ":", sl, sc); }
        if (Character.isDigit(c)) return readNumber(sl, sc);
        if (Character.isLetter(c)) return readWord(sl, sc);
        advance();
        return nextToken();
    }
    
    private Token readNumber(int sl, int sc) {
        StringBuilder sb = new StringBuilder();
        while (position < input.length() && Character.isDigit(input.charAt(position))) {
            sb.append(input.charAt(position)); advance();
        }
        return new Token(TokenType.VALUE, sb.toString(), sl, sc);
    }
    
    private Token readWord(int sl, int sc) {
        StringBuilder sb = new StringBuilder();
        while (position < input.length() && (Character.isLetterOrDigit(input.charAt(position)) || input.charAt(position) == '_')) {
            sb.append(input.charAt(position)); advance();
        }
        String v = sb.toString();
        switch (v) {
            case "rule": return new Token(TokenType.RULE, v, sl, sc);
            case "if":   return new Token(TokenType.IF, v, sl, sc);
            case "then": return new Token(TokenType.THEN, v, sl, sc);
            case "AND":  return new Token(TokenType.AND, v, sl, sc);
            default:     return new Token(TokenType.ID, v, sl, sc);
        }
    }
    
    private void skipWhitespace() {
        while (position < input.length()) {
            char c = input.charAt(position);
            if (c == ' ' || c == '\t') { advance(); }
            else if (c == '\n') { advance(); line++; column = 1; }
            else if (c == '\r') {
                advance();
                if (position < input.length() && input.charAt(position) == '\n') advance();
                line++; column = 1;
            } else break;
        }
    }
    
    private void advance() {
        if (position < input.length()) { position++; column++; }
    }
}
