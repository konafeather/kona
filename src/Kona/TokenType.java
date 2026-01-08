package Kona;

enum TokenType {
    // Single-character tokens. todo:
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    COMMA, DOT, PLUS, STAR, SLASH, COLON, SEMICOLON, NEWLINE,

    // One or two character tokens. todo:
    AMPERSAND, AND,
    PIPE, OR,
    BANG, BANG_EQUAL,
    EQUAL, EQUAL_EQUAL,
    LESS, LESS_EQUAL,
    GREATER, GREATER_EQUAL,
    MINUS, FIELD_EQUAL,

    // One or more character tokens
    TABS_INDENT, SPACES_INDENT,

    // Literals.
    IDENTIFIER, STRING, NUMBER,

    // Keywords todo:
    TEMPLATE, DEF, CLASS, EXT, OF, REQ, OVERRIDE, PRIV, MUT, IF, ELIF, ELSE, TRUE, FALSE, FUN, NULL,
    ECHO, RETURN, SUPER, SELF, FINAL, VAL, LET, WHILE, FOR, EACH, BLOCKSTMT,

    EOF
}