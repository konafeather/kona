// todo: report error if spaces after indentation, or after no indentation, if there is characters after spaces

package Kona;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Kona.TokenType.*;

class Scanner {

    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("template", TEMPLATE);
        keywords.put("def",    DEF);
        keywords.put("class",  CLASS);
        keywords.put("ext",    EXT);
        keywords.put("of",     OF);
        keywords.put("req",    REQ);
        keywords.put("override", OVERRIDE);
        keywords.put("priv",   PRIV);
        keywords.put("mut",    MUT);
        keywords.put("if",     IF);
        keywords.put("elif",   ELIF);
        keywords.put("else",   ELSE);
        keywords.put("true",   TRUE);
        keywords.put("false",  FALSE);
        keywords.put("fun",    FUN);
        keywords.put("null",   NULL);
        keywords.put("echo",   ECHO);
        keywords.put("return", RETURN);
        keywords.put("super",  SUPER);
        keywords.put("self",   SELF);
        keywords.put("final",  FINAL);
        keywords.put("val",    VAL);
        keywords.put("let",    LET);
        keywords.put("while",  WHILE);
        keywords.put("for",    FOR);
        keywords.put("each",   EACH);
        keywords.put("block",  BLOCKSTMT);
    }

    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int column = 1;
    private int line = 1;

    Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        boolean has_tabs = false;
        boolean has_spaces = false;
        List<Token> indent_tokens = new ArrayList<>();
        while (!isAtEnd()) {
            if (peek() == '\t') {
                has_tabs = true;
                start = current;
                while (match('\t')) {

                }

                indent_tokens.add(new Token(TABS_INDENT, source.substring(start, current), line, column - (current - start), current - start));
            } else if (peek() == ' ') {
                has_spaces = true;
                start = current;
                while (match(' ')) {

                }

                indent_tokens.add(new Token(SPACES_INDENT, source.substring(start, current), line, column - (current - start), current - start));
            } else {
                break;
            }
        }

        if (!isAtEnd()) {
            if (has_tabs && has_spaces) {
                Kona.error(line, column, current - start, "Indentation must not have both tabs and spaces.");
                if (peek() != '\n') {
                    tokens.add(indent_tokens.get(0));
                }
            } else if (has_tabs || has_spaces){
                if (peek() != '\n') {
                    tokens.add(indent_tokens.get(0));
                }
            }
        }

        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", line, 0, 0));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '+': addToken(PLUS); break;
            case ':': addToken(COLON); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;
            case '/': addToken(SLASH); break;
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            case '-':
                addToken(match('>') ? FIELD_EQUAL : MINUS);
                break;
            case '&':
                addToken(match('&') ? AND : AMPERSAND);
                break;
            case '|':
                addToken(match('|') ? OR : PIPE);
                break;
            case '#':
                if (match('[')) {
                    while (peek() != ']' && peekNext() != '#' && !isAtEnd()) {
                        if (source.charAt(current) == '\n') {
                            line++;
                            column = 1;
                        }
                        advance();
                    }

                    advance();
                    advance();
                } else {
                    // A comment goes until the end of the line.
                    while (peek() != '\n' && !isAtEnd()) advance();
                }
                break;

            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                // could be optimized?
                // skip new line if previous token is new line
                if (!tokens.isEmpty() && tokens.getLast().type != NEWLINE) {
                    addToken(NEWLINE);
                }

                line++;
                column = 1;
                // resume

                boolean has_tabs = false;
                boolean has_spaces = false;
                List<Token> indent_tokens = new ArrayList<>();

                while (!isAtEnd()) {
                    if (peek() == '\t') {
                        has_tabs = true;
                        start = current;
                        while (match('\t')) {

                        }

                        indent_tokens.add(new Token(TABS_INDENT, source.substring(start, current), line, column - (current - start), current - start));
                    } else if (peek() == ' ') {
                        has_spaces = true;
                        start = current;
                        while (match(' ')) {

                        }

                        indent_tokens.add(new Token(SPACES_INDENT, source.substring(start, current), line, column - (current - start), current - start));
                    } else {
                        break;
                    }
                }

                if (!isAtEnd()) {
                    if (has_tabs && has_spaces) {
                        Kona.error(line, column, current - start, "Indentation must not have both tabs and spaces.");
                        if (peek() != '\n') {
                            tokens.add(indent_tokens.getFirst());
                        }
                    } else if (has_tabs || has_spaces){
                        if (peek() != '\n') {
                            tokens.add(indent_tokens.getFirst());
                        }
                    }
                }

                break;
            case '"': string(); break;

            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    Kona.error(line, column, current - start, "Unexpected character.");
                }
                break;
        }
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();

        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) type = IDENTIFIER;
        addToken(type);
    }

    private void number() {
        while (isDigit(peek())) advance();

        // Look for a fractional part.
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the "."
            advance();

            while (isDigit(peek())) advance();
        }

        addToken(NUMBER);
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }

        if (isAtEnd()) {
            Kona.error(line, column, current - start, "Unterminated string.");
            return;
        }

        // The closing ".
        advance();

        addToken(STRING);
    }

    private boolean match(char expected) {
        if (isAtEnd() || source.charAt(current) != expected) return false;

        advance();
        return true;
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char advance() {
        column++;
        return source.charAt(current++);    // current returns value before incrementing
    }

    private void addToken(TokenType type) {
        String text;
        if (type == STRING) {
            // Trim the surrounding quotes.
            text = source.substring(start + 1, current - 1);
        } else {
            text = source.substring(start, current);
        }
        tokens.add(new Token(type, text, line, column - (current - start), current - start));
    }
}