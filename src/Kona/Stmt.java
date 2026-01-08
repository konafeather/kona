package Kona;

import java.util.List;

abstract class Stmt {
    interface Visitor<R> {
        R visitDeclarationsStmt(Declarations stmt);
        R visitBlockStmt(Block stmt);
        R visitTemplateStmt(Template stmt);
        R visitTemplateFieldsStmt(TemplateFields stmt);
        R visitTemplateFieldStmt(TemplateField stmt);
        R visitTemplateMethodsStmt(TemplateMethods stmt);
        R visitTemplateMethodStmt(TemplateMethod stmt);
        R visitClassStmt(Class stmt);
        R visitRequireStmt(Require stmt);
        R visitTemplatesStmt(Templates stmt);
        R visitFieldsStmt(Fields stmt);
        R visitFieldStmt(Field stmt);
        R visitMethodsStmt(Methods stmt);
        R visitMethodStmt(Method stmt);
        R visitExpressionStmt(Expression stmt);
        R visitFunctionStmt(Function stmt);
        R visitParamsStmt(Params stmt);
        R visitIfStmt(If stmt);
        R visitElseIfsStmt(ElseIfs stmt);
        R visitElseIfStmt(ElseIf stmt);
        R visitPrintStmt(Print stmt);
        R visitReturnStmt(Return stmt);
        R visitValStmt(Val stmt);
        R visitMutStmt(Mut stmt);
        R visitLetStmt(Let stmt);
        R visitWhileStmt(While stmt);
        R visitForStmt(For stmt);
    }
    static class Declarations extends Stmt {
        Declarations(List<Stmt> declarations) {
            this.declarations = declarations;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitDeclarationsStmt(this);
        }

        final List<Stmt> declarations;
    }
    static class Block extends Stmt {
        Block(List<Stmt> statements) {
            this.statements = statements;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlockStmt(this);
        }

        final List<Stmt> statements;
    }
    static class Template extends Stmt {
        Template(Token name, Token parent, Stmt.TemplateFields fields, Stmt.TemplateMethods methods) {
            this.name = name;
            this.parent = parent;
            this.fields = fields;
            this.methods = methods;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitTemplateStmt(this);
        }

        final Token name;
        final Token parent;
        final Stmt.TemplateFields fields;
        final Stmt.TemplateMethods methods;
    }
    static class TemplateFields extends Stmt {
        TemplateFields(List<Stmt.TemplateField> fields) {
            this.fields = fields;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitTemplateFieldsStmt(this);
        }

        final List<Stmt.TemplateField> fields;
    }
    static class TemplateField extends Stmt {
        TemplateField(Token def, Token field) {
            this.def = def;
            this.field = field;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitTemplateFieldStmt(this);
        }

        final Token def;
        final Token field;
    }
    static class TemplateMethods extends Stmt {
        TemplateMethods(List<TemplateMethod> methods) {
            this.methods = methods;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitTemplateMethodsStmt(this);
        }

        final List<TemplateMethod> methods;
    }
    static class TemplateMethod extends Stmt {
        TemplateMethod(Token def, Stmt.Function method) {
            this.def = def;
            this.method = method;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitTemplateMethodStmt(this);
        }

        final Token def;
        final Stmt.Function method;
    }
    static class Class extends Stmt {
        Class(Token mut, Token name, Token parent, Stmt.Templates templates, Stmt.Require req, Stmt.Fields fields, Stmt.Methods methods) {
            this.mut = mut;
            this.name = name;
            this.parent = parent;
            this.templates = templates;
            this.req = req;
            this.fields = fields;
            this.methods = methods;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitClassStmt(this);
        }

        final Token mut;
        final Token name;
        final Token parent;
        final Stmt.Templates templates;
        final Stmt.Require req;
        final Stmt.Fields fields;
        final Stmt.Methods methods;
    }
    static class Require extends Stmt {
        Require(List<Token> templates) {
            this.templates = templates;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitRequireStmt(this);
        }

        final List<Token> templates;
    }
    static class Templates extends Stmt {
        Templates(List<Token> templates) {
            this.templates = templates;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitTemplatesStmt(this);
        }

        final List<Token> templates;
    }
    static class Fields extends Stmt {
        Fields(List<Stmt.Field> fields) {
            this.fields = fields;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitFieldsStmt(this);
        }

        final List<Stmt.Field> fields;
    }
    static class Field extends Stmt {
        Field(Token priv, Token mutable, Token name) {
            this.priv = priv;
            this.mutable = mutable;
            this.name = name;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitFieldStmt(this);
        }

        final Token priv;
        final Token mutable;
        final Token name;
    }
    static class Methods extends Stmt {
        Methods(List<Stmt.Method> methods) {
            this.methods = methods;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitMethodsStmt(this);
        }

        final List<Stmt.Method> methods;
    }
    static class Method extends Stmt {
        Method(Token override, Token priv, Token mut, Stmt.Function method) {
            this.override = override;
            this.priv = priv;
            this.mut = mut;
            this.method = method;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitMethodStmt(this);
        }

        final Token override;
        final Token priv;
        final Token mut;
        final Stmt.Function method;
    }
    static class Expression extends Stmt {
        Expression(Expr expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStmt(this);
        }

        final Expr expression;
    }
    static class Function extends Stmt {
        Function(Token name, Stmt.Params parameters, Stmt.Block body) {
            this.name = name;
            this.parameters = parameters;
            this.body = body;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitFunctionStmt(this);
        }

        final Token name;
        final Stmt.Params parameters;
        final Stmt.Block body;
    }
    static class Params extends Stmt {
        Params(List<Token> parameters) {
            this.parameters = parameters;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitParamsStmt(this);
        }

        final List<Token> parameters;
    }
    static class If extends Stmt {
        If(Expr condition, Block thenBranch, Stmt.ElseIfs elseIfBranches, Block elseBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.elseIfBranches = elseIfBranches;
            this.elseBranch = elseBranch;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfStmt(this);
        }

        final Expr condition;
        final Block thenBranch;
        final Stmt.ElseIfs elseIfBranches;
        final Block elseBranch;
    }
    static class ElseIfs extends Stmt {
        ElseIfs(List<Stmt.ElseIf> elseIfBranches) {
            this.elseIfBranches = elseIfBranches;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitElseIfsStmt(this);
        }

        final List<Stmt.ElseIf> elseIfBranches;
    }
    static class ElseIf extends Stmt {
        ElseIf(Expr condition, Block body) {
            this.condition = condition;
            this.body = body;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitElseIfStmt(this);
        }

        final Expr condition;
        final Block body;
    }
    static class Print extends Stmt {
        Print(Expr expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintStmt(this);
        }

        final Expr expression;
    }
    static class Return extends Stmt {
        Return(Token keyword, Expr value) {
            this.keyword = keyword;
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitReturnStmt(this);
        }

        final Token keyword;
        final Expr value;
    }
    static class Val extends Stmt {
        Val(List<Token> names, List<Expr> initializer) {
            this.names = names;
            this.initializer = initializer;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitValStmt(this);
        }

        final List<Token> names;
        final List<Expr> initializer;
    }
    static class Mut extends Stmt {
        Mut(List<Token> names, List<Expr> initializer) {
            this.names = names;
            this.initializer = initializer;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitMutStmt(this);
        }

        final List<Token> names;
        final List<Expr> initializer;
    }
    static class Let extends Stmt {
        Let(List<Token> names, List<Expr> initializer) {
            this.names = names;
            this.initializer = initializer;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLetStmt(this);
        }

        final List<Token> names;
        final List<Expr> initializer;
    }
    static class While extends Stmt {
        While(Expr condition, Block body) {
            this.condition = condition;
            this.body = body;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitWhileStmt(this);
        }

        final Expr condition;
        final Block body;
    }
    static class For extends Stmt {
        For(Token arrayName, Token itemName, Token itemIndex, Block body) {
            this.arrayName = arrayName;
            this.itemName = itemName;
            this.itemIndex = itemIndex;
            this.body = body;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitForStmt(this);
        }

        final Token arrayName;
        final Token itemName;
        final Token itemIndex;
        final Block body;
    }

    abstract <R> R accept(Visitor<R> visitor);
}
