package Kona;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static Kona.TokenType.*;

class Parser {
    private static class ParseError extends RuntimeException {}

    private final List<Token> tokens;
    private int current = 0;
    private int indent_level = 0;
    private boolean indent_set = false;
    // true means tabs
    private boolean tabs_or_spaces = true;
    private int spaces = 0;


    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    Stmt parse() {
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(declaration());
        }

        return new Stmt.Declarations(statements);
    }

    private Expr expression() {
        return assignment();
    }
    // complete
    private Stmt declaration() {
        try {
            if (match(TEMPLATE)) return templateDeclaration();
            if (check(CLASS)) return classDeclaration();
            if (check(MUT) && checkNext(CLASS)) return classDeclaration();
            if (match(FUN)) return function("function");
            // Book: if (match(VAL)) return varDeclaration();
            return statement();
        // reset indentation level?
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }
    // complete
    private Stmt templateDeclaration() {
        Token name = consume(IDENTIFIER, "Expect template name.");
        Token parent = null;

        if (match(EXT)) {
            parent = consume(IDENTIFIER, "Expect parent template name after \"ext\".");
        }

        consume(NEWLINE, "Template header must occupy the rest of the line.");
        List<Stmt.TemplateField> fields = new ArrayList<>();
        indent_level++;

        if (isIndents() && (checkNext(IDENTIFIER) || (checkNext(DEF) && checkNextNext(IDENTIFIER)))) {
            if (!indent_set) {
                if (check(SPACES_INDENT)) {
                    tabs_or_spaces = false;
                    spaces = peek().length;
                }
                indent_set = true;
            }

            while(true) {
                consumeIndents();
                if (match(IDENTIFIER)) {
                    fields.add(new Stmt.TemplateField(null, previous()));
                } else if (match(DEF)) {
                    consume(IDENTIFIER, "Expect field name.");
                    fields.add(new Stmt.TemplateField(previousPrevious(), previous()));
                } else {
                    throw error(peek(), "Expect field name.");
                }

                if (isAtEnd() || !match(COMMA)) break;
                consume(NEWLINE, "Expect another field after \",\".");
            }

            if (!isAtEnd()) {
                consume(NEWLINE, "Field must occupy the rest of the line.");
            }
        }

        List<Stmt.TemplateMethod> methods = new ArrayList<>();

        if (isIndents() && (checkNext(FUN) || (checkNext(DEF) && checkNextNext(FUN)))) {
            if (!indent_set) {
                if (check(SPACES_INDENT)) {
                    tabs_or_spaces = false;
                    spaces = peek().length;
                }

                indent_set = true;
            }

            while(true) {
                if (isIndents()) {
                    consumeIndents();

                    if (match(FUN)) {
                        methods.add(new Stmt.TemplateMethod(null, function("method")));
                    } else if (match(DEF)) {
                        consume(FUN, "Expect \"fun\" after \"def\".");
                        methods.add(new Stmt.TemplateMethod(previousPrevious(), function("method")));
                    } else {
                        throw error(peek(), "Expect method after indentation.");
                    }
                } else {
                    break;
                }
            }
        }

        if (isIndents() && checkNext(DEF)) {
            consumeIndents();
            advance();
            throw error(peek(), "Expect field or method after \"def\".");
        } else if (isIndents()) {
            consumeIndents();
            throw error(peek(), "Expect a field or method.");
        } else {
            indent_level--;
            return new Stmt.Template(name, parent, new Stmt.TemplateFields(fields), new Stmt.TemplateMethods(methods));
        }
    }

    // complete
    private Stmt classDeclaration() {
        Token has_mut = null;

        // check if class has "mut" and advance past "class"
        if (check(CLASS)) {
            advance();
        } else {
            has_mut = peek();
            advance();
            advance();
        }

        Token name = consume(IDENTIFIER, "Expect class name.");

        Token parent = null;
        if (match(EXT)) {
            parent = consume(IDENTIFIER, "Expect parent class name after \"ext\".");
        }

        List<Token> templates = new ArrayList<>();
        if (match(OF)) {
            do {
                templates.add(consume(IDENTIFIER, "Expect template name."));
            } while (match(COMMA));
        }
        // empty class handled by compiler checks
        if (!isAtEnd()) {
            consume(NEWLINE, "Class header must occupy the rest of the line.");
        }

        indent_level++;
        List<Token> req_templates = new ArrayList<>();

        if (isIndents() && checkNext(REQ)) {
            if (!indent_set) {
                if (check(SPACES_INDENT)) {
                    tabs_or_spaces = false;
                    spaces = peek().length;
                }
                indent_set = true;
            }

            consumeIndents();
            advance();

            do {
                req_templates.add(consume(IDENTIFIER, "Expected template name."));
            } while (match(COMMA));

            if (!isAtEnd()) {
                consume(NEWLINE, "\"req\" statement must occupy the rest of the line.");
            }
        }

        List<Stmt.Field> fields = new ArrayList<>();
        boolean first_time = true;

        while(true) {
            Token priv = null;
            Token mut = null;

            if (isIndents() && checkNext(PRIV) && checkNextNext(MUT) && checkNextNextNext(IDENTIFIER)) {
                if (!indent_set) {
                    if (check(SPACES_INDENT)) {
                        tabs_or_spaces = false;
                        spaces = peek().length;
                    }
                    indent_set = true;
                }

                consumeIndents();
                priv = peek();
                advance();
                mut = peek();
                advance();
                fields.add(new Stmt.Field(priv, mut, peek()));
                advance();

            } else if (isIndents() && checkNext(MUT) && checkNextNext(IDENTIFIER)) {
                if (!indent_set) {
                    if (check(SPACES_INDENT)) {
                        tabs_or_spaces = false;
                        spaces = peek().length;
                    }
                    indent_set = true;
                }

                consumeIndents();
                mut = peek();
                advance();
                fields.add(new Stmt.Field(priv, mut, peek()));
                advance();

            } else if (isIndents() && checkNext(PRIV) && checkNextNext(IDENTIFIER)) {
                if (!indent_set) {
                    if (check(SPACES_INDENT)) {
                        tabs_or_spaces = false;
                        spaces = peek().length;
                    }
                    indent_set = true;
                }

                consumeIndents();
                priv = peek();
                advance();
                fields.add(new Stmt.Field(priv, mut, peek()));
                advance();

            } else if (isIndents() && checkNext(IDENTIFIER)) {
                if (!indent_set) {
                    if (check(SPACES_INDENT)) {
                        tabs_or_spaces = false;
                        spaces = peek().length;
                    }
                    indent_set = true;
                }

                consumeIndents();
                fields.add(new Stmt.Field(priv, mut, peek()));
                advance();

            } else {
                if (first_time) {
                    break;
                }

                throw error(peek(), "Expect field after \",\".");
            }

            first_time = false;

            if (!match(COMMA)) {
                if (!isAtEnd()) {
                    consume(NEWLINE, "Field must occupy the rest of the line.");
                }
                break;
            }

            consume(NEWLINE, "Field must occupy the rest of the line.");
        }

        List<Stmt.Method> methods = new ArrayList<>();

        while(true) {
            if (isIndents()) {
                if (!indent_set) {
                    if (check(SPACES_INDENT)) {
                        tabs_or_spaces = false;
                        spaces = peek().length;
                    }

                    indent_set = true;
                }

                consumeIndents();
                Token override = null;

                if (match(OVERRIDE)) {
                    override = previous();
                }

                Token priv = null;

                if (match(PRIV)) {
                    priv = previous();
                }

                Token mut = null;

                if (match(MUT)) {
                    mut = previous();
                }

                if (match(FUN)) {
                    methods.add(new Stmt.Method(override, priv, mut, function("method")));
                } else {
                    throw error(peek(), "Expected method.");
                }

            } else {
                break;
            }
        }

        indent_level--;
        return new Stmt.Class(has_mut, name, parent, new Stmt.Templates(templates), new Stmt.Require(req_templates), new Stmt.Fields(fields), new Stmt.Methods(methods));
    }

    // complete
    private Stmt statement() {
        if (!indent_set && current > 0 && isIndents()) {
            if (check(SPACES_INDENT)) {
                tabs_or_spaces = false;
                spaces = peek().length;
            }
            indent_set = true;
        }

        try {
            if (indent_level > 0) {
                if (isIndents()) {
                    isIndentType();
                    if (indentsLevel() < indent_level) {
                        return null;
                    } else if (indentsLevel() == indent_level) {
                        advance();
                    } else if (indentsLevel() > indent_level) {
                        throw error(peek(), "incorrect indentation");
                    }
                } else {
                    return null;
                }
            }

            if (match(FOR)) return forStatement();
            if (match(IF)) return ifStatement();
            if (match(WHILE)) return whileStatement();
            if (match(VAL)) return valDeclaration();
            if (match(MUT)) return mutDeclaration();
            if (match(LET)) return letDeclaration();
            return branchThenStatements();
            // Book: return expressionStatement();
            // reset indentation level?
        } catch (ParseError error) {
            if (synchronize()) {
                return null;
            }

            return statement();
        }
    }

    private Stmt branchThenStatements() {
        if (match(ECHO)) return printStatement();
        if (match(RETURN)) return returnStatement();
        return expressionStatement();
    }
    // complete
    private Stmt forStatement() {
        Token arrayName = consume(IDENTIFIER, "Expect array name after 'for'.");
        consume(EACH, "Expect 'each' after array name");
        Token itemName = consume(IDENTIFIER, "Expect item name after 'each'.");

        Token itemIndex = null;
        if (match(COMMA)) {
            consume(IDENTIFIER, "Index variable must come after \",\"");
            itemIndex = previous();
        }

        consume(NEWLINE, "Statement must occupy the rest of the line");
        List<Stmt> body = block();

        return new Stmt.For(arrayName, itemName, itemIndex, new Stmt.Block(body));
    }
    // complete
    private Stmt ifStatement() {

        Expr condition = expression();
        // todo: "then"
        consume(NEWLINE, "Condition must occupy the rest of the line.");
        List<Stmt> thenBranch = block();

        List<Stmt.ElseIf> else_if_branches = new ArrayList<>();
        while (!isAtEnd()) {
            if (match(ELIF)) {
                Expr else_if_condition = expression();
                consume(NEWLINE, "Condition must occupy the rest of the line.");
                List<Stmt> else_if_body = block();
                else_if_branches.add(new Stmt.ElseIf(else_if_condition, new Stmt.Block(else_if_body)));
            } else {
                break;
            }
        }

        List<Stmt> elseBranch = null;
        if (match(ELSE)) {
            consume(NEWLINE, "\"else\" must occupy the rest of the line.");
            elseBranch = block();
        }

        return new Stmt.If(condition, new Stmt.Block(thenBranch), new Stmt.ElseIfs(else_if_branches), new Stmt.Block(elseBranch));
    }
    // complete
    private Stmt printStatement() {
        Expr value = expression();

        if (!isAtEnd()) {
            consume(NEWLINE, "Expression must occupy the rest of the line.");
        }

        return new Stmt.Print(value);
    }
    // complete
    private Stmt returnStatement() {
        Token keyword = previous();

        Expr value = null;
        if (!isAtEnd() && !check(NEWLINE)) {
            value = expression();
        }

        if (!isAtEnd()) {
            consume(NEWLINE, "Expression must occupy the rest of the line.");
        }

        return new Stmt.Return(keyword, value);
    }
    // complete
    private Stmt valDeclaration() {
        List<Token> names = new ArrayList<>();

        do {
            names.add(consume(IDENTIFIER, "Expect variable name."));
        } while (match(COMMA));

        List<Expr> initializer = new ArrayList<>();

        if (match(EQUAL)) {
            indent_level++;

            while (true) {

                initializer.add(expression());

                if (!match(COMMA)) {
                    break;
                }
                if (match(NEWLINE)) {
                    consumeIndents();
                }
            }

            indent_level--;
        }

        if (!isAtEnd()) {
            consume(NEWLINE, "Expression must occupy the rest of the line.");
        }

        return new Stmt.Val(names, initializer);
    }
    // complete
    private Stmt mutDeclaration() {
        List<Token> names = new ArrayList<>();

        do {
            names.add(consume(IDENTIFIER, "Expect variable name."));
        } while (match(COMMA));

        List<Expr> initializer = new ArrayList<>();

        if (match(EQUAL)) {
            indent_level++;

            while (true) {

                initializer.add(expression());

                if (!match(COMMA)) {
                    break;
                }
                if (match(NEWLINE)) {
                    consumeIndents();
                }
            }

            indent_level--;
        }

        if (!isAtEnd()) {
            consume(NEWLINE, "Expression must occupy the rest of the line.");
        }

        return new Stmt.Mut(names, initializer);
    }
    // complete
    private Stmt letDeclaration() {
        List<Token> names = new ArrayList<>();

        do {
            names.add(consume(IDENTIFIER, "Expect variable name."));
        } while (match(COMMA));

        List<Expr> initializer = new ArrayList<>();

        if (match(EQUAL)) {
            indent_level++;

            while (true) {

                initializer.add(expression());

                if (!match(COMMA)) {
                    break;
                }
                if (match(NEWLINE)) {
                    consumeIndents();
                }
            }

            indent_level--;
        }

        if (!isAtEnd()) {
            consume(NEWLINE, "Expression must occupy the rest of the line.");
        }

        return new Stmt.Let(names, initializer);
    }
    // complete
    private Stmt whileStatement() {
        Expr condition = expression();
        consume(NEWLINE, "Only condition goes on this line");
        List<Stmt> body = block();

        return new Stmt.While(condition, new Stmt.Block(body));
    }

    private Stmt expressionStatement() {
        Expr expr = expression();
        if (!isAtEnd()) {
            consume(NEWLINE, "Expression must occupy the rest of the line.");
        }
        return new Stmt.Expression(expr);
    }
    // complete
    private Stmt.Function function(String kind) {
        Token name = consume(IDENTIFIER, "Expect " + kind + " name.");
        List<Token> params = new ArrayList<>();

        if (match(SEMICOLON)) {
            consume(NEWLINE, "Function header must occupy the rest of the line.");
        } else if (match(COLON)) {
            do {
                Token param_name = consume(IDENTIFIER, "Expect parameter name.");
                params.add(param_name);
            } while (match(COMMA));

            if (!isAtEnd()) {
                consume(NEWLINE, "Function header must occupy the rest of the line.");
            }
        } else {
            throw error(peek(), "Expect \":\" or \";\" after function name.");
        }

        List<Stmt> body = block();
        return new Stmt.Function(name, new Stmt.Params(params), new Stmt.Block(body));
    }
    // complete
    private Expr type() {
        List<Object> type = new ArrayList<>();

        if (match(IDENTIFIER)) {
            type.add(previous());
        } else if (match(LEFT_PAREN)) {
            type.add(type());
            consume(RIGHT_PAREN, "Expect \")\" to close type union.");
        } else {
            throw error(peek(), "Expect type.");
        }

        while (!isAtEnd()) {
            if (match(IDENTIFIER)) {
                type.add(previous());
            } else if (match(LEFT_PAREN)) {
                type.add(type());
                consume(RIGHT_PAREN, "Expect \")\" to close type union.");
            } else {
                break;
            }
        }

        return new Expr.Type(type);
    }
    // complete
    private List<Stmt> block() {
        indent_level++;
        List<Stmt> statements = new ArrayList<>();

        while (!isAtEnd()) {
            Stmt statement = statement();

            if (statement != null) {
                statements.add(statement);
            } else {
                indent_level--;
                return statements;
            }
        }

        indent_level--;
        return statements;
    }
    // complete
    private Expr assignment() {
        Expr expr = or();

        if (match(EQUAL)) {
            Token equals = previous();
            Expr value = assignment();

            if (expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable)expr).name;
                return new Expr.Assign(name, value);
            } else if (expr instanceof Expr.Get) {
                Expr.Get get = (Expr.Get)expr;
                return new Expr.Set(get.object, get.name, value);
            }

            error(equals, "Invalid assignment target.");
        }

        return expr;
    }
    // complete
    private Expr or() {
        Expr expr = and();

        while (match(OR)) {
            Token operator = previous();
            Expr right = and();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
    }
    // complete
    private Expr and() {
        Expr expr = equality();

        while (match(AND)) {
            Token operator = previous();
            Expr right = equality();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
    }
    // complete
    private Expr equality() {
        Expr expr = comparison();

        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(null, expr, operator, right);
        }

        return expr;
    }
    // complete
    private Expr comparison() {
        Expr expr = term();

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(null, expr, operator, right);
        }

        return expr;
    }
    // complete
    private Expr term() {
        Expr expr = factor();

        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(null, expr, operator, right);
        }

        return expr;
    }
    // complete
    private Expr factor() {
        Expr expr = cast();

        while (match(SLASH, STAR)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(null, expr, operator, right);
        }

        return expr;
    }
    // todo
    private Expr cast() {
        if (match(PIPE)) {
            int left_pipe_column = previous().column;
            Expr type = type();

            if (firstCharPos((Expr.Type)type) != left_pipe_column + 1) {
                throw error(peek(), "No spaces allowed between \"|\" and type.");
            }

            consume(PIPE, "Expect \"|\" after type.");

            if (lastCharPos((Expr.Type)type) != previous().column - 1) {
                throw error(previous(), "No spaces allowed between type and \"|\"");
            }

            return new Expr.Cast((Expr.Type)type, expression());
        }

        return unary();
    }
    // complete
    private int firstCharPos(Expr.Type type) {
        if (type.type.getFirst() instanceof Token) {
            return ((Token)type.type.getFirst()).column;
        } else {
            firstCharPos((Expr.Type)type.type.getFirst());
        }

        return 0;
    }
    // complete
    private int lastCharPos(Expr.Type type) {
        if (type.type.getLast() instanceof Token) {
            return ((Token)type.type.getLast()).column + ((Token)type.type.getLast()).length - 1;
        } else {
            lastCharPos((Expr.Type)type.type.getLast());
        }

        return 0;
    }
    // complete
    private Expr unary() {
        if (match(BANG, MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(null, operator, right);
        }

        return call();
    }
    // complete
    private Expr call() {
        Boolean is_enclosed_call = false;

        if (current > 0 && previous().type == LEFT_PAREN) {
            is_enclosed_call = true;
        }

        Expr callee = instance();

        while(true) {
            if (check(SEMICOLON) && peek().column == previous().column + previous().length) {
                advance();
                callee = new Expr.Call(callee, new Expr.Args(new ArrayList<>()));
            } else if (check(SEMICOLON)) {
                throw error(peek(), "Expect semicolon immediately after function call name.");
            } else if (check(COLON) && peek().column == previous().column + previous().length) {
                advance();

                if (is_enclosed_call) {
                    indent_level++;
                    List<Expr> arguments = new ArrayList<>();

                    do {
                        if (match(NEWLINE)) {
                            if (isIndents()) {
                                consumeIndents();
                                arguments.add(expression());
                            } else {
                                throw error(peek(), "Expected indented arguments.");
                            }
                        } else {
                            arguments.add(expression());
                        }
                    } while (match(COMMA));

                    indent_level--;
                    return new Expr.Call(callee, new Expr.Args(arguments));
                } else {
                    List<Expr> arguments = new ArrayList<>();

                    do {
                        arguments.add(expression());
                    } while (match(COMMA));
                    /*
                    if(!isAtEnd()) {
                        consume(NEWLINE, "This function call must occupy the rest of the line.");
                    }
                    */
                    return new Expr.Call(callee, new Expr.Args(arguments));
                }
            } else if (check(COLON)) {
                throw error(peek(), "Expect colon immediately after function call name.");
            } else if (match(DOT)) {
                Token name = consume(IDENTIFIER, "Expect property name after \".\".");
                callee = new Expr.Get(callee, name);
            } else {
                break;
            }
        }

        return callee;
    }
    // complete
    private Expr instance() {
        // checkNext()?
        if (check(IDENTIFIER) && peekNext().type == LEFT_BRACE) {
            Token name = peek();
            advance();
            advance();
            List<Expr.InstanceField> fields = new ArrayList<>();
            Boolean atleastOneField = false;

            if (!match(NEWLINE)) {
                Boolean line_close = true;
                do {
                    if (match(NEWLINE)) {
                        line_close = false;
                        break;
                    }

                    Token field_name = consume(IDENTIFIER, "Expect field name.");
                    consume(FIELD_EQUAL, "Expect field assignment operator \"->\".");
                    Expr value = expression();
                    fields.add(new Expr.InstanceField(field_name, value));
                    atleastOneField = true;
                } while (match(COMMA));

                if (line_close) {
                    consume(RIGHT_BRACE, "Expect \"}\" after fields.");
                    // "atleastOneField" is always true
                    return new Expr.Instance(name, new Expr.InstanceFields(fields));
                }
            }

            indent_level++;
            while (!isAtEnd()) {
                // INDENTS expected for check(INDENTS)
                if (check(RIGHT_BRACE) && indent_level == 1) {
                    advance();
                    indent_level--;

                    if (atleastOneField) {
                        return new Expr.Instance(name, new Expr.InstanceFields(fields));
                    } else {
                        error(previous(), "Instance must have at least one field.");
                    }
                } else if (isIndents() && peekNext().type == RIGHT_BRACE) {
                    indent_level--;
                    consumeIndents();
                    advance();

                    if (atleastOneField) {
                        return new Expr.Instance(name, new Expr.InstanceFields(fields));
                    } else {
                        error(previous(), "Instance must have at least one field.");
                    }
                } else {
                    consumeIndents();

                    Boolean line_close = true;
                    do {
                        Token field_name = consume(IDENTIFIER, "Expect field name.");
                        consume(FIELD_EQUAL, "Expect field assignment operator \"->\".");
                        Expr value = expression();
                        fields.add(new Expr.InstanceField(field_name, value));
                        atleastOneField = true;

                        if (match(NEWLINE)) {
                            line_close = false;
                            break;
                        }
                    } while (match(COMMA));

                    if (line_close) {
                        consume(RIGHT_BRACE, "Expect \"}\" after fields.");
                        indent_level--;
                        return new Expr.Instance(name, new Expr.InstanceFields(fields));
                    }
                }
            }
        }

        return primary();
    }
    // complete
    private Expr primary() {

        if (match(FALSE, TRUE, NULL, NUMBER, STRING)) {
            return new Expr.Literal(previous());
        }

        if (match(SUPER)) {
            Token keyword = previous();
            consume(DOT, "Expect '.' after 'super'.");
            Token method = consume(IDENTIFIER,
                    "Expect superclass method name.");
            return new Expr.Super(keyword, method);
        }

        if (match(SELF)) return new Expr.Self(previous());

        if (match(IDENTIFIER)) {
            return new Expr.Variable(previous());
        }

        /* Book says use:
        if (match(FALSE)) return new Expr.Literal("false");
        if (match(TRUE)) return new Expr.Literal("true");
        if (match(NULL)) return new Expr.Literal("null");

        if (match(NUMBER, STRING)) {
            return new Expr.Literal(previous().literal);
        }
        */

        if (match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(null, expr);
        }

        throw error(peek(), "Expect expression.");
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();

        throw error(peek(), message);

    }

    private boolean isIndents() {
        return check(TABS_INDENT) || check(SPACES_INDENT);
    }

    private void isIndentType() {
        if (tabs_or_spaces && !check(TABS_INDENT)) {
            throw error(peek(), "Expect indentation using tabs.");
        } else if (!tabs_or_spaces && !check(SPACES_INDENT)) {
            throw error(peek(), "Expect indentation using spaces.");
        }
    }

    private Token consumeIndents() {
        isIndentType();
        // purposely no check if indentation level is zero
        if (isIndents() && indentsLevel() == indent_level) return advance();
        if (tabs_or_spaces) {
            throw error(peek(), "Expect " + indent_level + " tabs of indentation.");
        }
        throw error(peek(), "Expect " + (indent_level * spaces) + " spaces of indentation.");
    }

    private float indentsLevel() {
        if (tabs_or_spaces) {
            return peek().length;
        } else {
            return (float) peek().length / (float) spaces;
        }
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private boolean checkNext(TokenType type) {
        if (isAtEnd()) return false;
        return peekNext().type == type;
    }

    private boolean checkNextNext(TokenType type) {
        if (isAtEnd()) return false;
        return peekNextNext().type == type;
    }

    private boolean checkNextNextNext(TokenType type) {
        if (isAtEnd()) return false;
        return peekNextNextNext().type == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token peekNext() {
        return tokens.get(current + 1);
    }

    private Token peekNextNext() {
        return tokens.get(current + 2);
    }

    private Token peekNextNextNext() {
        return tokens.get(current + 3);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private Token previousPrevious() {
        return tokens.get(current - 2);
    }

    private ParseError error(Token token, String message) {
        Kona.error(token, message);
        return new ParseError();
    }
    // true means exit scope, false means do nothing
    private Boolean synchronize() {
        advance();

        while (!isAtEnd()) {
            if (indent_level > 0) {
                if (match(NEWLINE) && !isIndents()) {
                    return true;
                } else if (isIndents()) {
                    isIndentType();
                    if (indentsLevel() < indent_level) return true;
                    if (indentsLevel() == indent_level) {
                        Token t = peekNext();

                        switch (t.type) {
                            // case can be any statement, parser check correctness later
                            case CLASS:
                            case FUN:
                            case FINAL:
                            case VAL:
                            case MUT:
                            case LET:
                            case FOR:
                            case IF:
                            case WHILE:
                            case ECHO:
                            case RETURN:
                                return false;
                        }
                    }
                    if (indentsLevel() > indent_level) {
                        while (!isAtEnd()) {
                            if (check(NEWLINE)) {
                                break;
                            } else {
                                advance();
                            }
                        }
                    }
                }
            } else if (indent_level == 0) {
                if (match(NEWLINE)) {
                    Token t = peek();

                    switch (t.type) {
                        // case can be any statement, parser check correctness later
                        case CLASS:
                        case FUN:
                        case FINAL:
                        case VAL:
                        case MUT:
                        case LET:
                        case FOR:
                        case IF:
                        case WHILE:
                        case ECHO:
                        case RETURN:
                            return false;
                    }
                }
            }

            advance();
        }

        return true;
    }
}