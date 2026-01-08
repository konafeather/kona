package Kona;

import java.util.List;

abstract class Expr {
    interface Visitor<R> {
        R visitAssignExpr(Assign expr);
        R visitBinaryExpr(Binary expr);
        R visitCastExpr(Cast expr);
        R visitTypeExpr(Type expr);
        R visitCallExpr(Call expr);
        R visitArgsExpr(Args expr);
        R visitGetExpr(Get expr);
        R visitInstanceExpr(Instance expr);
        R visitInstanceFieldsExpr(InstanceFields expr);
        R visitInstanceFieldExpr(InstanceField expr);
        R visitGroupingExpr(Grouping expr);
        R visitLiteralExpr(Literal expr);
        R visitLogicalExpr(Logical expr);
        R visitSetExpr(Set expr);
        R visitSuperExpr(Super expr);
        R visitSelfExpr(Self expr);
        R visitUnaryExpr(Unary expr);
        R visitVariableExpr(Variable expr);
    }
    static class Assign extends Expr {
        Assign(Token name, Expr value) {
            this.name = name;
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitAssignExpr(this);
        }

        final Token name;
        final Expr value;
    }
    static class Binary extends Expr {
        Binary(String type, Expr left, Token operator, Expr right) {
            this.type = type;
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }

        final String type;
        final Expr left;
        final Token operator;
        final Expr right;
    }
    static class Cast extends Expr {
        Cast(Expr.Type type, Expr expression) {
            this.type = type;
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitCastExpr(this);
        }

        final Expr.Type type;
        final Expr expression;
    }
    static class Type extends Expr {
        Type(List<Object> type) {
            this.type = type;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitTypeExpr(this);
        }

        final List<Object> type;
    }
    static class Call extends Expr {
        Call(Expr callee, Expr.Args arguments) {
            this.callee = callee;
            this.arguments = arguments;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitCallExpr(this);
        }

        final Expr callee;
        final Expr.Args arguments;
    }
    static class Args extends Expr {
        Args(List<Expr> arguments) {
            this.arguments = arguments;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitArgsExpr(this);
        }

        final List<Expr> arguments;
    }
    static class Get extends Expr {
        Get(Expr object, Token name) {
            this.object = object;
            this.name = name;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGetExpr(this);
        }

        final Expr object;
        final Token name;
    }
    static class Instance extends Expr {
        Instance(Token instanceName, Expr.InstanceFields instanceFields) {
            this.instanceName = instanceName;
            this.instanceFields = instanceFields;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitInstanceExpr(this);
        }

        final Token instanceName;
        final Expr.InstanceFields instanceFields;
    }
    static class InstanceFields extends Expr {
        InstanceFields(List<Expr.InstanceField> instanceFields) {
            this.instanceFields = instanceFields;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitInstanceFieldsExpr(this);
        }

        final List<Expr.InstanceField> instanceFields;
    }
    static class InstanceField extends Expr {
        InstanceField(Token name, Expr value) {
            this.name = name;
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitInstanceFieldExpr(this);
        }

        final Token name;
        final Expr value;
    }
    static class Grouping extends Expr {
        Grouping(String type, Expr expression) {
            this.type = type;
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }

        final String type;
        final Expr expression;
    }
    static class Literal extends Expr {
        Literal(Token value) {
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }

        final Token value;
    }
    static class Logical extends Expr {
        Logical(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLogicalExpr(this);
        }

        final Expr left;
        final Token operator;
        final Expr right;
    }
    static class Set extends Expr {
        Set(Expr object, Token name, Expr value) {
            this.object = object;
            this.name = name;
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitSetExpr(this);
        }

        final Expr object;
        final Token name;
        final Expr value;
    }
    static class Super extends Expr {
        Super(Token keyword, Token method) {
            this.keyword = keyword;
            this.method = method;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitSuperExpr(this);
        }

        final Token keyword;
        final Token method;
    }
    static class Self extends Expr {
        Self(Token keyword) {
            this.keyword = keyword;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitSelfExpr(this);
        }

        final Token keyword;
    }
    static class Unary extends Expr {
        Unary(String type, Token operator, Expr right) {
            this.type = type;
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }

        final String type;
        final Token operator;
        final Expr right;
    }
    static class Variable extends Expr {
        Variable(Token name) {
            this.name = name;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableExpr(this);
        }

        final Token name;
    }

    abstract <R> R accept(Visitor<R> visitor);
}
