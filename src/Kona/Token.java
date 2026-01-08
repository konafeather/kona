package Kona;

class Token {
    final TokenType type;
    final String lexeme;
    final int line;
    final int column;
    final int length;

    Token(TokenType type, String lexeme, int line, int column, int length) {
        this.type = type;
        this.lexeme = lexeme;
        this.line = line;
        this.column = column;
        this.length = length;
    }

    public String toString() {
        return type + " " + lexeme + " " + line + " " + column + " " + length;
    }
}