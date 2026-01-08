package Kona;

import java.util.List;

class AstPrinter implements Expr.Visitor<String>, Stmt.Visitor<String> {
    String print(Stmt declarations) {
        return declarations.accept(this);
    }

    @Override
    public String visitDeclarationsStmt(Stmt.Declarations stmt) {
        StringBuilder builder = new StringBuilder();

        for (Stmt declaration : stmt.declarations) {
            builder.append(declaration.accept(this));
        }

        return builder.toString();
    }

    @Override
    public String visitAssignExpr(Expr.Assign expr) {
        return parenthesize("Assignment:", expr.name, expr.value);
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return parenthesize(expr.operator.lexeme,
                expr.left, expr.right);
    }

    @Override
    public String visitCastExpr(Expr.Cast expr) {
        return parenthesize("Cast:", expr.type, expr.expression);
    }

    @Override
    public String visitTypeExpr(Expr.Type stmt) {
        return parenthesizeListType("type:", stmt.type);
    }

    private String parenthesizeListType(String name, List<Object> exprs) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(name);

        for (Object expr : exprs) {
            builder.append(" ");
            if (expr instanceof Expr.Type) {
                builder.append(((Expr.Type)expr).accept(this));
            } else {
                builder.append(((Token)expr).lexeme);
            }
        }

        builder.append(")");
        return builder.toString();
    }

    @Override
    public String visitCallExpr(Expr.Call expr) {
        return parenthesize("Call:", expr.callee, expr.arguments);
    }
    // implement in parser
    @Override
    public String visitArgsExpr(Expr.Args expr) {
        return parenthesizeListArgs("Arguments:", expr.arguments);
    }

    private String parenthesizeListArgs(String name, List<Expr> exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (Expr expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }

    @Override
    public String visitGetExpr(Expr.Get expr) {
        return parenthesize("Get:", expr.object, expr.name);
    }

    @Override
    public String visitInstanceExpr(Expr.Instance expr) {
        return parenthesize("Instance:", expr.instanceName, expr.instanceFields);
    }
    // implement in parser
    @Override
    public String visitInstanceFieldsExpr(Expr.InstanceFields expr) {
        return parenthesizeListInstanceFields("InstanceFields:", expr.instanceFields);
    }


    private String parenthesizeListInstanceFields(String name, List<Expr.InstanceField> exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (Expr.InstanceField expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }

    @Override
    public String visitInstanceFieldExpr(Expr.InstanceField expr) {
        return parenthesize("InstanceField:", expr.name, expr.value);
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return parenthesize("group", expr.expression);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        return expr.value.lexeme;
    }

    @Override
    public String visitLogicalExpr(Expr.Logical expr) {
        return parenthesize("logical:", expr.left, expr.operator, expr.right);
    }

    @Override
    public String visitSetExpr(Expr.Set expr) {
        return parenthesize("set:", expr.object, expr.name, expr.value);
    }

    @Override
    public String visitSuperExpr(Expr.Super expr) {
        return parenthesize("super:", expr.keyword, expr.method);
    }

    @Override
    public String visitSelfExpr(Expr.Self expr) {
        return parenthesize("self:", expr.keyword);
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return parenthesize(expr.operator.lexeme, expr.right);
    }

    @Override
    public String visitVariableExpr(Expr.Variable expr) {
        return parenthesize(expr.name.lexeme);
    }

    private String parenthesize(String name, Object... exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (Object expr : exprs) {
            builder.append(" ");
            if (expr instanceof Expr) {
                // not sure if cast works
                builder.append(((Expr)expr).accept(this));
            } else if (expr instanceof Stmt) {
                builder.append(((Stmt)expr).accept(this));
            } else if (expr instanceof Token) {
                builder.append(((Token)expr).lexeme);
            }
        }
        builder.append(")");

        return builder.toString();
    }
/*
    public static void main(String[] args) {
        Stmt declarations = new Stmt.Declarations(
                List.of(new Stmt.Mut(
                        new Token(TokenType.IDENTIFIER, "birthday", 1, 1, 8),
                        new Expr.Binary(
                                new Expr.Unary(
                                        new Token(TokenType.MINUS, "-", 1, 1, 1),
                                        new Expr.Literal(new Token(TokenType.NUMBER, "123", 1, 2, 3))),
                                new Token(TokenType.STAR, "*",1, 6, 1),
                                new Expr.Grouping(
                                        new Expr.Literal(new Token(TokenType.NUMBER, "45.67",1, 8, 5))))
                ))
        );

        System.out.println(new AstPrinter().print(declarations));
    }
*/
    ////////////////////////////////////// statements ////////////////////////////////////////////

    @Override
    public String visitBlockStmt(Stmt.Block stmt) {
        return parenthesizeListBlock("block:", stmt.statements);
    }

    private String parenthesizeListBlock(String name, List<Stmt> stmts) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(name);

        if (stmts == null) {
            builder.append(" ");
            builder.append("null_indent");
        } else {
            for (Stmt stmt : stmts) {
                builder.append(" ");
                builder.append(stmt.accept(this));
            }
        }

        builder.append(")");
        return builder.toString();
    }

    @Override
    public String visitTemplateStmt(Stmt.Template stmt) {
        return parenthesize("template:", stmt.name, stmt.parent, stmt.fields, stmt.methods);
    }

    @Override
    public String visitTemplateFieldsStmt(Stmt.TemplateFields stmt) {
        return parenthesizeListTemplateFields("fields:", stmt.fields);
    }

    private String parenthesizeListTemplateFields(String name, List<Stmt.TemplateField> stmts) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(name);

        for (Stmt.TemplateField stmt : stmts) {
            builder.append(" ");
            builder.append(stmt.accept(this));
        }

        builder.append(")");
        return builder.toString();
    }

    @Override
    public String visitTemplateFieldStmt(Stmt.TemplateField stmt) {
        return parenthesize("field:", stmt.def, stmt.field);
    }

    @Override
    public String visitTemplateMethodsStmt(Stmt.TemplateMethods stmt) {
        return parenthesizeListTemplateMethods("methods:", stmt.methods);
    }

    private String parenthesizeListTemplateMethods(String name, List<Stmt.TemplateMethod> stmts) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(name);

        for (Stmt.TemplateMethod stmt : stmts) {
            builder.append(" ");
            builder.append(stmt.accept(this));
        }

        builder.append(")");
        return builder.toString();
    }

    @Override
    public String visitTemplateMethodStmt(Stmt.TemplateMethod stmt) {
        return parenthesize("method", stmt.def, stmt.method);
    }

    @Override
    public String visitClassStmt(Stmt.Class stmt) {
        return parenthesize("class:", stmt.name, stmt.parent, stmt.templates, stmt.fields, stmt.methods);
    }

    @Override
    public String visitTemplatesStmt(Stmt.Templates stmt) {
        return parenthesizeListOfTokens("templates:", stmt.templates);
    }

    private String parenthesizeListOfTokens(String name, List<Token> list) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(name);

        for (Token token : list) {
            builder.append(" ");
            builder.append(token.lexeme);
        }

        builder.append(")");
        return builder.toString();
    }

    @Override
    public String visitFieldsStmt(Stmt.Fields stmt) {
        return parenthesizeListFields("fields:", stmt.fields);
    }

    private String parenthesizeListFields(String name, List<Stmt.Field> stmts) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(name);

        for (Stmt.Field stmt : stmts) {
            builder.append(" ");
            builder.append(stmt.accept(this));
        }

        builder.append(")");
        return builder.toString();
    }

    @Override
    public String visitFieldStmt(Stmt.Field stmt) {
        return parenthesize("field:", stmt.priv, stmt.mutable, stmt.name);
    }

    @Override
    public String visitMethodsStmt(Stmt.Methods stmt) {
        return parenthesizeListMethods("methods:", stmt.methods);
    }

    private String parenthesizeListMethods(String name, List<Stmt.Method> stmts) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(name);

        for (Stmt.Method stmt : stmts) {
            builder.append(" ");
            builder.append(stmt.accept(this));
        }

        builder.append(")");
        return builder.toString();
    }

    @Override
    public String visitMethodStmt(Stmt.Method stmt) {
        return parenthesize("method:", stmt.priv, stmt.method);
    }

    @Override
    public String visitExpressionStmt(Stmt.Expression stmt) {
        return parenthesize("expression:", stmt.expression);
    }

    @Override
    public String visitFunctionStmt(Stmt.Function stmt) {
        return parenthesize("fun:", stmt.name, stmt.parameters, stmt.body);
    }

    @Override
    public String visitParamsStmt(Stmt.Params stmt) {
        return parenthesizeListOfTokens("parameters:", stmt.parameters);
    }

    @Override
    public String visitIfStmt(Stmt.If stmt) {
        return parenthesize("if:", stmt.condition, stmt.thenBranch, stmt.elseIfBranches, stmt.elseBranch);
    }

    @Override
    public String visitElseIfsStmt(Stmt.ElseIfs stmt) {
        return parenthesizeListElseIfs("elifs:", stmt.elseIfBranches);
    }

    private String parenthesizeListElseIfs(String name, List<Stmt.ElseIf> stmts) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(name);

        for (Stmt.ElseIf stmt : stmts) {
            builder.append(" ");
            builder.append(stmt.accept(this));
        }

        builder.append(")");
        return builder.toString();
    }

    @Override
    public String visitElseIfStmt(Stmt.ElseIf stmt) {
        return parenthesize("elif:", stmt.condition, stmt.body);
    }

    @Override
    public String visitPrintStmt(Stmt.Print stmt) {
        return parenthesize("echo:", stmt.expression);
    }

    @Override
    public String visitReturnStmt(Stmt.Return stmt) {
        return parenthesize("return:", stmt.value);
    }

    @Override
    public String visitValStmt(Stmt.Val stmt) {
        return parenthesizeListsVarDecl("val:", stmt.names, stmt.initializer);
    }

    private String parenthesizeListsVarDecl(String name, List<Token> names, List<Expr> initializer) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(name);

        for (Token var_name : names) {
            builder.append(" ");
            builder.append(var_name.lexeme);
        }

        for (Expr expr : initializer) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }

        builder.append(")");
        return builder.toString();
    }

    @Override
    public String visitMutStmt(Stmt.Mut stmt) {
        return parenthesizeListsVarDecl("mut:", stmt.names, stmt.initializer);
    }

    @Override
    public String visitLetStmt(Stmt.Let stmt) {
        return parenthesizeListsVarDecl("let:", stmt.names, stmt.initializer);
    }

    @Override
    public String visitWhileStmt(Stmt.While stmt) {
        return parenthesize("while:", stmt.condition, stmt.body);
    }

    @Override
    public String visitForStmt(Stmt.For stmt) {
        return parenthesize("for:", stmt.arrayName, stmt.itemName, stmt.itemIndex, stmt.body);
    }
}