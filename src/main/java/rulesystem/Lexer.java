package rulesystem;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
    
    public enum TokenType {
        // Keywords
        RULE, IF, THEN, AND,
        
        // Operators
        GT, LT, EQ, COLON,
        
        // Identifiers and literals
        ID, VALUE,
        
        // End of file
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
        
        @Override
        public String toString() {
            return String.format("Token(%s, %s, %d:%d)", type, value, line, column);
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
    
    /**
     * Tokenizes the entire input and returns a list of tokens
     */
    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        
        while (position < input.length()) {
            skipWhitespaceAndComments();
            
            if (position >= input.length()) {
                break;
            }
            
            Token token = nextToken();
            if (token != null && token.type != TokenType.EOF) {
                tokens.add(token);
            }
        }
        
        tokens.add(new Token(TokenType.EOF, "", line, column));
        return tokens;
    }
    
    /**
     * Gets the next token without consuming it
     */
    private Token nextToken() {
        if (position >= input.length()) {
            return new Token(TokenType.EOF, "", line, column);
        }
        
        char current = input.charAt(position);
        int startLine = line;
        int startColumn = column;
        
        // Single character operators
        if (current == '>') {
            advance();
            return new Token(TokenType.GT, ">", startLine, startColumn);
        }
        if (current == '<') {
            advance();
            return new Token(TokenType.LT, "<", startLine, startColumn);
        }
        if (current == '=') {
            advance();
            return new Token(TokenType.EQ, "=", startLine, startColumn);
        }
        if (current == ':') {
            advance();
            return new Token(TokenType.COLON, ":", startLine, startColumn);
        }
        
        // Numbers (VALUES)
        if (Character.isDigit(current)) {
            return readNumber(startLine, startColumn);
        }
        
        // Identifiers and keywords
        if (Character.isLetter(current) || current == '_') {
            return readIdentifierOrKeyword(startLine, startColumn);
        }
        
        // Unknown character - skip it
        advance();
        return nextToken();
    }
    
    /**
     * Reads a number from the input
     */
    private Token readNumber(int startLine, int startColumn) {
        StringBuilder sb = new StringBuilder();
        
        while (position < input.length() && Character.isDigit(input.charAt(position))) {
            sb.append(input.charAt(position));
            advance();
        }
        
        return new Token(TokenType.VALUE, sb.toString(), startLine, startColumn);
    }
    
    /**
     * Reads an identifier or keyword from the input
     */
    private Token readIdentifierOrKeyword(int startLine, int startColumn) {
        StringBuilder sb = new StringBuilder();
        
        while (position < input.length() && 
               (Character.isLetterOrDigit(input.charAt(position)) || input.charAt(position) == '_')) {
            sb.append(input.charAt(position));
            advance();
        }
        
        String value = sb.toString();
        
        // Check if it's a keyword
        switch (value) {
            case "rule":
                return new Token(TokenType.RULE, value, startLine, startColumn);
            case "if":
                return new Token(TokenType.IF, value, startLine, startColumn);
            case "then":
                return new Token(TokenType.THEN, value, startLine, startColumn);
            case "AND":
                return new Token(TokenType.AND, value, startLine, startColumn);
            default:
                return new Token(TokenType.ID, value, startLine, startColumn);
        }
    }
    
    /**
     * Skips whitespace and blank lines
     */
    private void skipWhitespaceAndComments() {
        while (position < input.length()) {
            char current = input.charAt(position);
            
            if (current == ' ' || current == '\t') {
                advance();
            } else if (current == '\n') {
                advance();
                line++;
                column = 1;
            } else if (current == '\r') {
                advance();
                if (position < input.length() && input.charAt(position) == '\n') {
                    advance();
                }
                line++;
                column = 1;
            } else {
                break;
            }
        }
    }
    
    /**
     * Advances to the next character
     */
    private void advance() {
        if (position < input.length()) {
            position++;
            column++;
        }
    }
}
