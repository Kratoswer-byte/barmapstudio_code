package com.faforever.neroxis.lua;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.SwitchBootstraps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParserVisitorImpl.class */
public class LuaParserVisitorImpl extends AbstractParseTreeVisitor<Lua> implements LuaParserVisitor<Lua> {
    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitStart, reason: merged with bridge method [inline-methods] */
    public Lua visitStart2(LuaParser.StartContext ctx) {
        return visitBlock2(ctx.block());
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitBlock, reason: merged with bridge method [inline-methods] */
    public Lua visitBlock2(LuaParser.BlockContext ctx) {
        List<Lua.Statement> statements = new ArrayList<>();
        Stream<R> map = ctx.statement().stream().map(this::visitStatement);
        Objects.requireNonNull(statements);
        map.forEach((v1) -> {
            r1.add(v1);
        });
        LuaParser.LastStatementContext lastStatement = ctx.lastStatement();
        if (lastStatement != null) {
            statements.add(visitLastStatement(lastStatement));
        }
        return new Lua.Block(statements);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitAssignmentStatement, reason: merged with bridge method [inline-methods] */
    public Lua visitAssignmentStatement2(LuaParser.AssignmentStatementContext ctx) {
        List<Lua.Variable> receivers = ctx.varList().var().stream().map(this::visitVar).toList();
        List<Lua.Expression> values = ctx.expressionList().expression().stream().map(this::visitExpression).toList();
        return new Lua.Statement.Assignment(receivers, values);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitFunctionCallStatement, reason: merged with bridge method [inline-methods] */
    public Lua visitFunctionCallStatement2(LuaParser.FunctionCallStatementContext ctx) {
        return visitFunctionCall(ctx.functionCall());
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitDoStatement, reason: merged with bridge method [inline-methods] */
    public Lua visitDoStatement2(LuaParser.DoStatementContext ctx) {
        Lua.Block block = visitBlock2(ctx.block());
        return new Lua.Statement.Do(block);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitWhileStatement, reason: merged with bridge method [inline-methods] */
    public Lua visitWhileStatement2(LuaParser.WhileStatementContext ctx) {
        Lua.Expression condition = visitExpression(ctx.expression());
        Lua.Block block = visitBlock2(ctx.block());
        return new Lua.Statement.While(condition, block);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitRepeatStatement, reason: merged with bridge method [inline-methods] */
    public Lua visitRepeatStatement2(LuaParser.RepeatStatementContext ctx) {
        Lua.Block block = visitBlock2(ctx.block());
        Lua.Expression condition = visitExpression(ctx.expression());
        return new Lua.Statement.Repeat(block, condition);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitIfStatement, reason: merged with bridge method [inline-methods] */
    public Lua visitIfStatement2(LuaParser.IfStatementContext ctx) {
        List<Lua.Expression> expressions = ctx.expression().stream().map(this::visitExpression).toList();
        List<Lua.Block> bodies = ctx.block().stream().map(this::visitBlock2).toList();
        List<Lua.Statement.If.Condition> conditions = new ArrayList<>();
        for (int i = 0; i < expressions.size(); i++) {
            conditions.add(new Lua.Statement.If.Condition.Expression(expressions.get(i), bodies.get(i)));
        }
        if (bodies.size() > expressions.size()) {
            conditions.add(new Lua.Statement.If.Condition.Else((Lua.Block) bodies.getLast()));
        }
        return new Lua.Statement.If(conditions);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitNumericForStatement, reason: merged with bridge method [inline-methods] */
    public Lua visitNumericForStatement2(LuaParser.NumericForStatementContext ctx) {
        Lua.Expression start = visitExpression(ctx.startValue);
        Lua.Expression end = visitExpression(ctx.endValue);
        Lua.Expression step = ctx.stepValue == null ? new Lua.Value.Num(1.0d) : visitExpression(ctx.stepValue);
        Lua.Block body = visitBlock2(ctx.block());
        return new Lua.Statement.NumericFor(ctx.NAME().getText(), start, end, step, body);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitGenericForStatement, reason: merged with bridge method [inline-methods] */
    public Lua visitGenericForStatement2(LuaParser.GenericForStatementContext ctx) {
        List<String> variables = ctx.nameList().NAME().stream().map((v0) -> {
            return v0.getText();
        }).toList();
        List<Lua.Expression> iterators = ctx.expressionList().expression().stream().map(this::visitExpression).toList();
        Lua.Block body = visitBlock2(ctx.block());
        return new Lua.Statement.GenericFor(variables, iterators, body);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitFunctionStatement, reason: merged with bridge method [inline-methods] */
    public Lua visitFunctionStatement2(LuaParser.FunctionStatementContext ctx) {
        Lua.FunctionBody body = visitFunctionBody2(ctx.functionBody());
        String name = ctx.functionName().getText();
        return new Lua.Statement.Function(name, body);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitLocalFunctionStatement, reason: merged with bridge method [inline-methods] */
    public Lua visitLocalFunctionStatement2(LuaParser.LocalFunctionStatementContext ctx) {
        Lua.FunctionBody body = visitFunctionBody2(ctx.functionBody());
        String name = ctx.NAME().getText();
        return new Lua.Statement.LocalFunction(name, body);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitLocalAssignmentStatement, reason: merged with bridge method [inline-methods] */
    public Lua visitLocalAssignmentStatement2(LuaParser.LocalAssignmentStatementContext ctx) {
        List<String> names = ctx.nameList().NAME().stream().map((v0) -> {
            return v0.getText();
        }).toList();
        LuaParser.ExpressionListContext expressionListContext = ctx.expressionList();
        List<Lua.Expression> values = expressionListContext == null ? List.of() : expressionListContext.expression().stream().map(this::visitExpression).toList();
        return new Lua.Statement.LocalAssignment(names, values);
    }

    public Lua.Statement visitStatement(LuaParser.StatementContext ctx) {
        Objects.requireNonNull(ctx);
        switch ((int) SwitchBootstraps.typeSwitch(MethodHandles.lookup(), "typeSwitch", MethodType.methodType(Integer.TYPE, LuaParser.StatementContext.class, Integer.TYPE), LuaParser.AssignmentStatementContext.class, LuaParser.FunctionCallStatementContext.class, LuaParser.DoStatementContext.class, LuaParser.WhileStatementContext.class, LuaParser.RepeatStatementContext.class, LuaParser.IfStatementContext.class, LuaParser.NumericForStatementContext.class, LuaParser.GenericForStatementContext.class, LuaParser.FunctionStatementContext.class, LuaParser.LocalFunctionStatementContext.class, LuaParser.LocalAssignmentStatementContext.class, LuaParser.StatementContext.class).dynamicInvoker().invoke(ctx, 0) /* invoke-custom */) {
            case 0:
                LuaParser.AssignmentStatementContext assignmentStatementContext = (LuaParser.AssignmentStatementContext) ctx;
                return visitAssignmentStatement2(assignmentStatementContext);
            case 1:
                LuaParser.FunctionCallStatementContext functionCallStatementContext = (LuaParser.FunctionCallStatementContext) ctx;
                return visitFunctionCallStatement2(functionCallStatementContext);
            case 2:
                LuaParser.DoStatementContext doStatementContext = (LuaParser.DoStatementContext) ctx;
                return visitDoStatement2(doStatementContext);
            case 3:
                LuaParser.WhileStatementContext whileStatementContext = (LuaParser.WhileStatementContext) ctx;
                return visitWhileStatement2(whileStatementContext);
            case 4:
                LuaParser.RepeatStatementContext repeatStatementContext = (LuaParser.RepeatStatementContext) ctx;
                return visitRepeatStatement2(repeatStatementContext);
            case 5:
                LuaParser.IfStatementContext ifStatementContext = (LuaParser.IfStatementContext) ctx;
                return visitIfStatement2(ifStatementContext);
            case 6:
                LuaParser.NumericForStatementContext forStatementContext = (LuaParser.NumericForStatementContext) ctx;
                return visitNumericForStatement2(forStatementContext);
            case 7:
                LuaParser.GenericForStatementContext forInStatementContext = (LuaParser.GenericForStatementContext) ctx;
                return visitGenericForStatement2(forInStatementContext);
            case 8:
                LuaParser.FunctionStatementContext functionStatementContext = (LuaParser.FunctionStatementContext) ctx;
                return visitFunctionStatement2(functionStatementContext);
            case 9:
                LuaParser.LocalFunctionStatementContext localFunctionStatementContext = (LuaParser.LocalFunctionStatementContext) ctx;
                return visitLocalFunctionStatement2(localFunctionStatementContext);
            case 10:
                LuaParser.LocalAssignmentStatementContext localAssignmentStatementContext = (LuaParser.LocalAssignmentStatementContext) ctx;
                return visitLocalAssignmentStatement2(localAssignmentStatementContext);
            default:
                throw new UnsupportedOperationException("Unable to handle statement of type %s".formatted(ctx.getClass().getCanonicalName()));
        }
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitBreakStatement, reason: merged with bridge method [inline-methods] */
    public Lua visitBreakStatement2(LuaParser.BreakStatementContext ctx) {
        return new Lua.Statement.Break();
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitContinueStatement, reason: merged with bridge method [inline-methods] */
    public Lua visitContinueStatement2(LuaParser.ContinueStatementContext ctx) {
        return new Lua.Statement.Continue();
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitReturnStatement, reason: merged with bridge method [inline-methods] */
    public Lua visitReturnStatement2(LuaParser.ReturnStatementContext ctx) {
        List<Lua.Expression> values = ctx.expressionList().expression().stream().map(this::visitExpression).toList();
        return new Lua.Statement.Return(values);
    }

    public Lua.Statement visitLastStatement(LuaParser.LastStatementContext ctx) {
        Objects.requireNonNull(ctx);
        switch ((int) SwitchBootstraps.typeSwitch(MethodHandles.lookup(), "typeSwitch", MethodType.methodType(Integer.TYPE, LuaParser.LastStatementContext.class, Integer.TYPE), LuaParser.BreakStatementContext.class, LuaParser.ContinueStatementContext.class, LuaParser.ReturnStatementContext.class, LuaParser.LastStatementContext.class).dynamicInvoker().invoke(ctx, 0) /* invoke-custom */) {
            case 0:
                LuaParser.BreakStatementContext breakStatementContext = (LuaParser.BreakStatementContext) ctx;
                return visitBreakStatement2(breakStatementContext);
            case 1:
                LuaParser.ContinueStatementContext continueStatementContext = (LuaParser.ContinueStatementContext) ctx;
                return visitContinueStatement2(continueStatementContext);
            case 2:
                LuaParser.ReturnStatementContext returnStatementContext = (LuaParser.ReturnStatementContext) ctx;
                return visitReturnStatement2(returnStatementContext);
            default:
                throw new UnsupportedOperationException("Unable to handle last statement of type %s".formatted(ctx.getClass().getCanonicalName()));
        }
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitVariableLiteral, reason: merged with bridge method [inline-methods] */
    public Lua visitVariableLiteral2(LuaParser.VariableLiteralContext ctx) {
        return new Lua.Variable.Named(ctx.NAME().getText(), List.of());
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitMemberAccessLiteral, reason: merged with bridge method [inline-methods] */
    public Lua visitMemberAccessLiteral2(LuaParser.MemberAccessLiteralContext ctx) {
        List<Lua.MemberAccessor> memberAccessors = ctx.memberAccess().stream().map(this::visitMemberAccess).toList();
        return new Lua.Variable.Named(ctx.NAME().getText(), memberAccessors);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitExpressionAccessLiteral, reason: merged with bridge method [inline-methods] */
    public Lua visitExpressionAccessLiteral2(LuaParser.ExpressionAccessLiteralContext ctx) {
        Lua.Expression expression = visitExpression(ctx.expression());
        List<Lua.MemberAccessor> memberAccessors = ctx.memberAccess().stream().map(this::visitMemberAccess).toList();
        return new Lua.Variable.Expression(expression, memberAccessors);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitParenthesizedExpressionLiteral, reason: merged with bridge method [inline-methods] */
    public Lua visitParenthesizedExpressionLiteral2(LuaParser.ParenthesizedExpressionLiteralContext ctx) {
        return visitExpression(ctx.expression());
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitFunctionAccessLiteral, reason: merged with bridge method [inline-methods] */
    public Lua visitFunctionAccessLiteral2(LuaParser.FunctionAccessLiteralContext ctx) {
        Lua.FunctionCall functionCall = visitFunctionCall(ctx.functionCall());
        List<Lua.MemberAccessor> memberAccessors = ctx.memberAccess().stream().map(this::visitMemberAccess).toList();
        return new Lua.Variable.Function(functionCall, memberAccessors);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitFunctionCallLiteral, reason: merged with bridge method [inline-methods] */
    public Lua visitFunctionCallLiteral2(LuaParser.FunctionCallLiteralContext ctx) {
        return visitFunctionCall(ctx.functionCall());
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitExpressionUnary, reason: merged with bridge method [inline-methods] */
    public Lua visitExpressionUnary2(LuaParser.ExpressionUnaryContext ctx) {
        Lua.Expression expression = visitExpression(ctx.expression());
        String string = ctx.operator.getText();
        Objects.requireNonNull(string);
        switch ((int) SwitchBootstraps.typeSwitch(MethodHandles.lookup(), "typeSwitch", MethodType.methodType(Integer.TYPE, String.class, Integer.TYPE), "not", "#", "-", "~", String.class).dynamicInvoker().invoke(string, 0) /* invoke-custom */) {
            case 0:
                return new Lua.UnaryOperator.Not(expression);
            case 1:
                return new Lua.UnaryOperator.Length(expression);
            case 2:
                return new Lua.UnaryOperator.Negate(expression);
            case 3:
                return new Lua.UnaryOperator.BitNot(expression);
            default:
                throw new UnsupportedOperationException("Unable to handle unary operator %s".formatted(string));
        }
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitExpressionPower, reason: merged with bridge method [inline-methods] */
    public Lua visitExpressionPower2(LuaParser.ExpressionPowerContext ctx) {
        Lua.Expression left = visitExpression(ctx.left);
        Lua.Expression right = visitExpression(ctx.right);
        return new Lua.BinaryOperator.Power(left, right);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitExpressionBitwise, reason: merged with bridge method [inline-methods] */
    public Lua visitExpressionBitwise2(LuaParser.ExpressionBitwiseContext ctx) {
        Lua.Expression left = visitExpression(ctx.left);
        Lua.Expression right = visitExpression(ctx.right);
        String string = ctx.operator.getText();
        Objects.requireNonNull(string);
        switch ((int) SwitchBootstraps.typeSwitch(MethodHandles.lookup(), "typeSwitch", MethodType.methodType(Integer.TYPE, String.class, Integer.TYPE), "&", "|", "~", "<<", ">>", String.class).dynamicInvoker().invoke(string, 0) /* invoke-custom */) {
            case 0:
                return new Lua.BinaryOperator.BitAnd(left, right);
            case 1:
                return new Lua.BinaryOperator.BitOr(left, right);
            case 2:
                return new Lua.BinaryOperator.BitXor(left, right);
            case 3:
                return new Lua.BinaryOperator.LeftShift(left, right);
            case 4:
                return new Lua.BinaryOperator.RightShift(left, right);
            default:
                throw new UnsupportedOperationException("Unable to handle bitwise operator %s".formatted(string));
        }
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitNilLiteral, reason: merged with bridge method [inline-methods] */
    public Lua visitNilLiteral2(LuaParser.NilLiteralContext ctx) {
        return new Lua.Value.Nil();
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitTrueLiteral, reason: merged with bridge method [inline-methods] */
    public Lua visitTrueLiteral2(LuaParser.TrueLiteralContext ctx) {
        return new Lua.Value.Bool(true);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitVarargLiteral, reason: merged with bridge method [inline-methods] */
    public Lua visitVarargLiteral2(LuaParser.VarargLiteralContext ctx) {
        return new Lua.Value.VarArg();
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitExpressionAdditive, reason: merged with bridge method [inline-methods] */
    public Lua visitExpressionAdditive2(LuaParser.ExpressionAdditiveContext ctx) {
        Lua.Expression left = visitExpression(ctx.left);
        Lua.Expression right = visitExpression(ctx.right);
        String string = ctx.operator.getText();
        Objects.requireNonNull(string);
        switch ((int) SwitchBootstraps.typeSwitch(MethodHandles.lookup(), "typeSwitch", MethodType.methodType(Integer.TYPE, String.class, Integer.TYPE), "+", "-", String.class).dynamicInvoker().invoke(string, 0) /* invoke-custom */) {
            case 0:
                return new Lua.BinaryOperator.Add(left, right);
            case 1:
                return new Lua.BinaryOperator.Subtract(left, right);
            default:
                throw new UnsupportedOperationException("Unable to handle additive operator %s".formatted(string));
        }
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitExpressionMultiplicative, reason: merged with bridge method [inline-methods] */
    public Lua visitExpressionMultiplicative2(LuaParser.ExpressionMultiplicativeContext ctx) {
        Lua.Expression left = visitExpression(ctx.left);
        Lua.Expression right = visitExpression(ctx.right);
        String string = ctx.operator.getText();
        Objects.requireNonNull(string);
        switch ((int) SwitchBootstraps.typeSwitch(MethodHandles.lookup(), "typeSwitch", MethodType.methodType(Integer.TYPE, String.class, Integer.TYPE), "*", "/", "%", "//", String.class).dynamicInvoker().invoke(string, 0) /* invoke-custom */) {
            case 0:
                return new Lua.BinaryOperator.Multiply(left, right);
            case 1:
                return new Lua.BinaryOperator.Divide(left, right);
            case 2:
                return new Lua.BinaryOperator.Modulo(left, right);
            case 3:
                return new Lua.BinaryOperator.FloorDivide(left, right);
            default:
                throw new UnsupportedOperationException("Unable to handle multiplicative operator %s".formatted(string));
        }
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitExpressionConcat, reason: merged with bridge method [inline-methods] */
    public Lua visitExpressionConcat2(LuaParser.ExpressionConcatContext ctx) {
        Lua.Expression left = visitExpression(ctx.left);
        Lua.Expression right = visitExpression(ctx.right);
        return new Lua.BinaryOperator.Concat(left, right);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitTableLiteral, reason: merged with bridge method [inline-methods] */
    public Lua visitTableLiteral2(LuaParser.TableLiteralContext ctx) {
        return visitTableConstructor2(ctx.tableConstructor());
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitFalseLiteral, reason: merged with bridge method [inline-methods] */
    public Lua visitFalseLiteral2(LuaParser.FalseLiteralContext ctx) {
        return new Lua.Value.Bool(false);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitStringLiteral, reason: merged with bridge method [inline-methods] */
    public Lua visitStringLiteral2(LuaParser.StringLiteralContext ctx) {
        return visitString(ctx.string());
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitExpressionComparative, reason: merged with bridge method [inline-methods] */
    public Lua visitExpressionComparative2(LuaParser.ExpressionComparativeContext ctx) {
        Lua.Expression left = visitExpression(ctx.left);
        Lua.Expression right = visitExpression(ctx.right);
        String string = ctx.operator.getText();
        Objects.requireNonNull(string);
        switch ((int) SwitchBootstraps.typeSwitch(MethodHandles.lookup(), "typeSwitch", MethodType.methodType(Integer.TYPE, String.class, Integer.TYPE), "<", ">", "<=", ">=", "==", "~=", "!=", String.class).dynamicInvoker().invoke(string, 0) /* invoke-custom */) {
            case 0:
                return new Lua.BinaryOperator.LessThan(left, right);
            case 1:
                return new Lua.BinaryOperator.GreaterThan(left, right);
            case 2:
                return new Lua.BinaryOperator.LessThanOrEqual(left, right);
            case 3:
                return new Lua.BinaryOperator.GreaterThanOrEqual(left, right);
            case 4:
                return new Lua.BinaryOperator.Equal(left, right);
            case 5:
            case 6:
                return new Lua.BinaryOperator.NotEqual(left, right);
            default:
                throw new UnsupportedOperationException("Unable to handle comparative operator %s".formatted(string));
        }
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitExpressionAnd, reason: merged with bridge method [inline-methods] */
    public Lua visitExpressionAnd2(LuaParser.ExpressionAndContext ctx) {
        Lua.Expression left = visitExpression(ctx.left);
        Lua.Expression right = visitExpression(ctx.right);
        return new Lua.BinaryOperator.And(left, right);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitExpressionOr, reason: merged with bridge method [inline-methods] */
    public Lua visitExpressionOr2(LuaParser.ExpressionOrContext ctx) {
        Lua.Expression left = visitExpression(ctx.left);
        Lua.Expression right = visitExpression(ctx.right);
        return new Lua.BinaryOperator.Or(left, right);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitFunctionLiteral, reason: merged with bridge method [inline-methods] */
    public Lua visitFunctionLiteral2(LuaParser.FunctionLiteralContext ctx) {
        return new Lua.Value.Function(visitFunctionBody2(ctx.functionBody()));
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitNumberLiteral, reason: merged with bridge method [inline-methods] */
    public Lua visitNumberLiteral2(LuaParser.NumberLiteralContext ctx) {
        LuaParser.NumberContext numberContext = ctx.number();
        Objects.requireNonNull(numberContext);
        switch ((int) SwitchBootstraps.typeSwitch(MethodHandles.lookup(), "typeSwitch", MethodType.methodType(Integer.TYPE, LuaParser.NumberContext.class, Integer.TYPE), LuaParser.HexFloatNumberContext.class, LuaParser.FloatNumberContext.class, LuaParser.IntNumberContext.class, LuaParser.HexNumberContext.class, LuaParser.NumberContext.class).dynamicInvoker().invoke(numberContext, 0) /* invoke-custom */) {
            case 0:
                LuaParser.HexFloatNumberContext hexFloatNumberContext = (LuaParser.HexFloatNumberContext) numberContext;
                return visitHexFloatNumber2(hexFloatNumberContext);
            case 1:
                LuaParser.FloatNumberContext floatNumberContext = (LuaParser.FloatNumberContext) numberContext;
                return visitFloatNumber2(floatNumberContext);
            case 2:
                LuaParser.IntNumberContext intNumberContext = (LuaParser.IntNumberContext) numberContext;
                return visitIntNumber2(intNumberContext);
            case 3:
                LuaParser.HexNumberContext hexNumberContext = (LuaParser.HexNumberContext) numberContext;
                return visitHexNumber2(hexNumberContext);
            default:
                throw new UnsupportedOperationException("Unable to handle number of type %s".formatted(numberContext.getClass().getCanonicalName()));
        }
    }

    public Lua.Expression visitExpression(LuaParser.ExpressionContext ctx) {
        Objects.requireNonNull(ctx);
        switch ((int) SwitchBootstraps.typeSwitch(MethodHandles.lookup(), "typeSwitch", MethodType.methodType(Integer.TYPE, LuaParser.ExpressionContext.class, Integer.TYPE), LuaParser.NilLiteralContext.class, LuaParser.FalseLiteralContext.class, LuaParser.TrueLiteralContext.class, LuaParser.NumberLiteralContext.class, LuaParser.StringLiteralContext.class, LuaParser.VarargLiteralContext.class, LuaParser.FunctionLiteralContext.class, LuaParser.VariableLiteralContext.class, LuaParser.MemberAccessLiteralContext.class, LuaParser.FunctionAccessLiteralContext.class, LuaParser.FunctionCallLiteralContext.class, LuaParser.ExpressionAccessLiteralContext.class, LuaParser.ParenthesizedExpressionLiteralContext.class, LuaParser.TableLiteralContext.class, LuaParser.ExpressionPowerContext.class, LuaParser.ExpressionUnaryContext.class, LuaParser.ExpressionMultiplicativeContext.class, LuaParser.ExpressionAdditiveContext.class, LuaParser.ExpressionConcatContext.class, LuaParser.ExpressionComparativeContext.class, LuaParser.ExpressionAndContext.class, LuaParser.ExpressionOrContext.class, LuaParser.ExpressionBitwiseContext.class, LuaParser.ExpressionContext.class).dynamicInvoker().invoke(ctx, 0) /* invoke-custom */) {
            case 0:
                LuaParser.NilLiteralContext nilLiteralContext = (LuaParser.NilLiteralContext) ctx;
                return visitNilLiteral2(nilLiteralContext);
            case 1:
                LuaParser.FalseLiteralContext falseLiteralContext = (LuaParser.FalseLiteralContext) ctx;
                return visitFalseLiteral2(falseLiteralContext);
            case 2:
                LuaParser.TrueLiteralContext trueLiteralContext = (LuaParser.TrueLiteralContext) ctx;
                return visitTrueLiteral2(trueLiteralContext);
            case 3:
                LuaParser.NumberLiteralContext numberLiteralContext = (LuaParser.NumberLiteralContext) ctx;
                return visitNumberLiteral2(numberLiteralContext);
            case 4:
                LuaParser.StringLiteralContext stringLiteralContext = (LuaParser.StringLiteralContext) ctx;
                return visitStringLiteral2(stringLiteralContext);
            case 5:
                LuaParser.VarargLiteralContext varargLiteralContext = (LuaParser.VarargLiteralContext) ctx;
                return visitVarargLiteral2(varargLiteralContext);
            case 6:
                LuaParser.FunctionLiteralContext functionLiteralContext = (LuaParser.FunctionLiteralContext) ctx;
                return visitFunctionLiteral2(functionLiteralContext);
            case 7:
                LuaParser.VariableLiteralContext variableLiteralContext = (LuaParser.VariableLiteralContext) ctx;
                return visitVariableLiteral2(variableLiteralContext);
            case 8:
                LuaParser.MemberAccessLiteralContext memberAccessLiteralContext = (LuaParser.MemberAccessLiteralContext) ctx;
                return visitMemberAccessLiteral2(memberAccessLiteralContext);
            case 9:
                LuaParser.FunctionAccessLiteralContext functionAccessLiteralContext = (LuaParser.FunctionAccessLiteralContext) ctx;
                return visitFunctionAccessLiteral2(functionAccessLiteralContext);
            case 10:
                LuaParser.FunctionCallLiteralContext functionCallLiteralContext = (LuaParser.FunctionCallLiteralContext) ctx;
                return visitFunctionCallLiteral2(functionCallLiteralContext);
            case 11:
                LuaParser.ExpressionAccessLiteralContext expressionAccessLiteralContext = (LuaParser.ExpressionAccessLiteralContext) ctx;
                return visitExpressionAccessLiteral2(expressionAccessLiteralContext);
            case 12:
                LuaParser.ParenthesizedExpressionLiteralContext parenthesizedExpressionLiteralContext = (LuaParser.ParenthesizedExpressionLiteralContext) ctx;
                return visitParenthesizedExpressionLiteral2(parenthesizedExpressionLiteralContext);
            case 13:
                LuaParser.TableLiteralContext tableLiteralContext = (LuaParser.TableLiteralContext) ctx;
                return visitTableLiteral2(tableLiteralContext);
            case 14:
                LuaParser.ExpressionPowerContext expressionPowerContext = (LuaParser.ExpressionPowerContext) ctx;
                return visitExpressionPower2(expressionPowerContext);
            case 15:
                LuaParser.ExpressionUnaryContext expressionUnaryContext = (LuaParser.ExpressionUnaryContext) ctx;
                return visitExpressionUnary2(expressionUnaryContext);
            case 16:
                LuaParser.ExpressionMultiplicativeContext expressionMultiplicativeContext = (LuaParser.ExpressionMultiplicativeContext) ctx;
                return visitExpressionMultiplicative2(expressionMultiplicativeContext);
            case 17:
                LuaParser.ExpressionAdditiveContext expressionAdditiveContext = (LuaParser.ExpressionAdditiveContext) ctx;
                return visitExpressionAdditive2(expressionAdditiveContext);
            case 18:
                LuaParser.ExpressionConcatContext expressionConcatContext = (LuaParser.ExpressionConcatContext) ctx;
                return visitExpressionConcat2(expressionConcatContext);
            case 19:
                LuaParser.ExpressionComparativeContext expressionComparativeContext = (LuaParser.ExpressionComparativeContext) ctx;
                return visitExpressionComparative2(expressionComparativeContext);
            case 20:
                LuaParser.ExpressionAndContext expressionAndContext = (LuaParser.ExpressionAndContext) ctx;
                return visitExpressionAnd2(expressionAndContext);
            case 21:
                LuaParser.ExpressionOrContext expressionOrContext = (LuaParser.ExpressionOrContext) ctx;
                return visitExpressionOr2(expressionOrContext);
            case 22:
                LuaParser.ExpressionBitwiseContext expressionBitwiseContext = (LuaParser.ExpressionBitwiseContext) ctx;
                return visitExpressionBitwise2(expressionBitwiseContext);
            default:
                throw new UnsupportedOperationException("Unable to handle expression of type %s".formatted(ctx.getClass().getCanonicalName()));
        }
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitMemberVar, reason: merged with bridge method [inline-methods] */
    public Lua visitMemberVar2(LuaParser.MemberVarContext ctx) {
        List<Lua.MemberAccessor> memberAccessors = ctx.memberAccess().stream().map(this::visitMemberAccess).toList();
        return new Lua.Variable.Named(ctx.NAME().getText(), memberAccessors);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitFunctionVar, reason: merged with bridge method [inline-methods] */
    public Lua visitFunctionVar2(LuaParser.FunctionVarContext ctx) {
        Lua.FunctionCall functionCall = visitFunctionCall(ctx.functionCall());
        List<Lua.MemberAccessor> memberAccessors = ctx.memberAccess().stream().map(this::visitMemberAccess).toList();
        return new Lua.Variable.Function(functionCall, memberAccessors);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitExpressionVar, reason: merged with bridge method [inline-methods] */
    public Lua visitExpressionVar2(LuaParser.ExpressionVarContext ctx) {
        Lua.Expression expression = visitExpression(ctx.expression());
        List<Lua.MemberAccessor> memberAccessors = ctx.memberAccess().stream().map(this::visitMemberAccess).toList();
        return new Lua.Variable.Expression(expression, memberAccessors);
    }

    public Lua.Variable visitVar(LuaParser.VarContext ctx) {
        Objects.requireNonNull(ctx);
        switch ((int) SwitchBootstraps.typeSwitch(MethodHandles.lookup(), "typeSwitch", MethodType.methodType(Integer.TYPE, LuaParser.VarContext.class, Integer.TYPE), LuaParser.MemberVarContext.class, LuaParser.FunctionVarContext.class, LuaParser.ExpressionVarContext.class, LuaParser.VarContext.class).dynamicInvoker().invoke(ctx, 0) /* invoke-custom */) {
            case 0:
                LuaParser.MemberVarContext memberVarContext = (LuaParser.MemberVarContext) ctx;
                return visitMemberVar2(memberVarContext);
            case 1:
                LuaParser.FunctionVarContext functionVarContext = (LuaParser.FunctionVarContext) ctx;
                return visitFunctionVar2(functionVarContext);
            case 2:
                LuaParser.ExpressionVarContext expressionVarContext = (LuaParser.ExpressionVarContext) ctx;
                return visitExpressionVar2(expressionVarContext);
            default:
                throw new UnsupportedOperationException("Unable to handle var of type %s".formatted(ctx.getClass().getCanonicalName()));
        }
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitDirectFunctionCall, reason: merged with bridge method [inline-methods] */
    public Lua visitDirectFunctionCall2(LuaParser.DirectFunctionCallContext ctx) {
        List<Lua.MemberAccessor> memberAccessors = ctx.memberAccess().stream().map(this::visitMemberAccess).toList();
        Lua.Variable receiver = new Lua.Variable.Named(ctx.receiver.getText(), memberAccessors);
        List<Lua.Expression> args = extractArgs(ctx.args());
        return new Lua.FunctionCall.Direct(receiver, args);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitDirectSelfCall, reason: merged with bridge method [inline-methods] */
    public Lua visitDirectSelfCall2(LuaParser.DirectSelfCallContext ctx) {
        List<Lua.MemberAccessor> memberAccessors = ctx.memberAccess().stream().map(this::visitMemberAccess).toList();
        Lua.Variable receiver = new Lua.Variable.Named(ctx.receiver.getText(), memberAccessors);
        List<Lua.Expression> args = extractArgs(ctx.args());
        return new Lua.FunctionCall.Self(receiver, ctx.methodName.getText(), args);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitExpressionFunctionCall, reason: merged with bridge method [inline-methods] */
    public Lua visitExpressionFunctionCall2(LuaParser.ExpressionFunctionCallContext ctx) {
        Lua.Expression receiverExpression = visitExpression(ctx.expression());
        List<Lua.MemberAccessor> memberAccessors = ctx.memberAccess().stream().map(this::visitMemberAccess).toList();
        Lua.Variable receiver = new Lua.Variable.Expression(receiverExpression, memberAccessors);
        List<Lua.Expression> args = extractArgs(ctx.args());
        return new Lua.FunctionCall.Direct(receiver, args);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitExpressionSelfCall, reason: merged with bridge method [inline-methods] */
    public Lua visitExpressionSelfCall2(LuaParser.ExpressionSelfCallContext ctx) {
        Lua.Expression receiverExpression = visitExpression(ctx.expression());
        List<Lua.MemberAccessor> memberAccessors = ctx.memberAccess().stream().map(this::visitMemberAccess).toList();
        Lua.Variable receiver = new Lua.Variable.Expression(receiverExpression, memberAccessors);
        List<Lua.Expression> args = extractArgs(ctx.args());
        return new Lua.FunctionCall.Self(receiver, ctx.methodName.getText(), args);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitNestedFunctionCall, reason: merged with bridge method [inline-methods] */
    public Lua visitNestedFunctionCall2(LuaParser.NestedFunctionCallContext ctx) {
        Lua.FunctionCall receiverFunction = visitFunctionCall(ctx.functionCall());
        List<Lua.MemberAccessor> memberAccessors = ctx.memberAccess().stream().map(this::visitMemberAccess).toList();
        Lua.Variable receiver = new Lua.Variable.Function(receiverFunction, memberAccessors);
        List<Lua.Expression> args = extractArgs(ctx.args());
        return new Lua.FunctionCall.Direct(receiver, args);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitNestedSelfCall, reason: merged with bridge method [inline-methods] */
    public Lua visitNestedSelfCall2(LuaParser.NestedSelfCallContext ctx) {
        Lua.FunctionCall receiverFunction = visitFunctionCall(ctx.functionCall());
        List<Lua.MemberAccessor> memberAccessors = ctx.memberAccess().stream().map(this::visitMemberAccess).toList();
        Lua.Variable receiver = new Lua.Variable.Function(receiverFunction, memberAccessors);
        List<Lua.Expression> args = extractArgs(ctx.args());
        return new Lua.FunctionCall.Self(receiver, ctx.methodName.getText(), args);
    }

    public Lua.FunctionCall visitFunctionCall(LuaParser.FunctionCallContext ctx) {
        Objects.requireNonNull(ctx);
        switch ((int) SwitchBootstraps.typeSwitch(MethodHandles.lookup(), "typeSwitch", MethodType.methodType(Integer.TYPE, LuaParser.FunctionCallContext.class, Integer.TYPE), LuaParser.DirectFunctionCallContext.class, LuaParser.NestedFunctionCallContext.class, LuaParser.ExpressionFunctionCallContext.class, LuaParser.DirectSelfCallContext.class, LuaParser.NestedSelfCallContext.class, LuaParser.ExpressionSelfCallContext.class, LuaParser.FunctionCallContext.class).dynamicInvoker().invoke(ctx, 0) /* invoke-custom */) {
            case 0:
                LuaParser.DirectFunctionCallContext directFunctionCallContext = (LuaParser.DirectFunctionCallContext) ctx;
                return visitDirectFunctionCall2(directFunctionCallContext);
            case 1:
                LuaParser.NestedFunctionCallContext nestedFunctionCallContext = (LuaParser.NestedFunctionCallContext) ctx;
                return visitNestedFunctionCall2(nestedFunctionCallContext);
            case 2:
                LuaParser.ExpressionFunctionCallContext expressionFunctionCallContext = (LuaParser.ExpressionFunctionCallContext) ctx;
                return visitExpressionFunctionCall2(expressionFunctionCallContext);
            case 3:
                LuaParser.DirectSelfCallContext directSelfCallContext = (LuaParser.DirectSelfCallContext) ctx;
                return visitDirectSelfCall2(directSelfCallContext);
            case 4:
                LuaParser.NestedSelfCallContext nestedSelfCallContext = (LuaParser.NestedSelfCallContext) ctx;
                return visitNestedSelfCall2(nestedSelfCallContext);
            case 5:
                LuaParser.ExpressionSelfCallContext expressionSelfCallContext = (LuaParser.ExpressionSelfCallContext) ctx;
                return visitExpressionSelfCall2(expressionSelfCallContext);
            default:
                throw new UnsupportedOperationException("Unable to handle function call of type %s".formatted(ctx.getClass().getCanonicalName()));
        }
    }

    private List<Lua.Expression> extractArgs(LuaParser.ArgsContext ctx) {
        Objects.requireNonNull(ctx);
        switch ((int) SwitchBootstraps.typeSwitch(MethodHandles.lookup(), "typeSwitch", MethodType.methodType(Integer.TYPE, LuaParser.ArgsContext.class, Integer.TYPE), LuaParser.StringArgumentContext.class, LuaParser.TableArgumentContext.class, LuaParser.ExpressionArgumentsContext.class, LuaParser.ArgsContext.class).dynamicInvoker().invoke(ctx, 0) /* invoke-custom */) {
            case 0:
                LuaParser.StringArgumentContext stringArgumentContext = (LuaParser.StringArgumentContext) ctx;
                return List.of(visitStringArgument2(stringArgumentContext));
            case 1:
                LuaParser.TableArgumentContext tableArgumentContext = (LuaParser.TableArgumentContext) ctx;
                return List.of(visitTableArgument2(tableArgumentContext));
            case 2:
                LuaParser.ExpressionArgumentsContext expressionArgumentsContext = (LuaParser.ExpressionArgumentsContext) ctx;
                LuaParser.ExpressionListContext expressionListContext = expressionArgumentsContext.expressionList();
                return expressionListContext == null ? List.of() : expressionListContext.expression().stream().map(this::visitExpression).toList();
            default:
                throw new UnsupportedOperationException("Unable to handle argument of type %s".formatted(ctx.getClass().getCanonicalName()));
        }
    }

    public Lua.MemberAccessor visitMemberAccess(LuaParser.MemberAccessContext ctx) {
        Objects.requireNonNull(ctx);
        switch ((int) SwitchBootstraps.typeSwitch(MethodHandles.lookup(), "typeSwitch", MethodType.methodType(Integer.TYPE, LuaParser.MemberAccessContext.class, Integer.TYPE), LuaParser.ExpressionAccessContext.class, LuaParser.NamedAccessContext.class, LuaParser.MemberAccessContext.class).dynamicInvoker().invoke(ctx, 0) /* invoke-custom */) {
            case 0:
                LuaParser.ExpressionAccessContext expressionAccessContext = (LuaParser.ExpressionAccessContext) ctx;
                return visitExpressionAccess2(expressionAccessContext);
            case 1:
                LuaParser.NamedAccessContext namedAccessContext = (LuaParser.NamedAccessContext) ctx;
                return visitNamedAccess2(namedAccessContext);
            default:
                throw new UnsupportedOperationException("Unable to handle member access of type %s".formatted(ctx.getClass().getCanonicalName()));
        }
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitExpressionAccess, reason: merged with bridge method [inline-methods] */
    public Lua visitExpressionAccess2(LuaParser.ExpressionAccessContext ctx) {
        return new Lua.MemberAccessor.Expression(visitExpression(ctx.expression()));
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitNamedAccess, reason: merged with bridge method [inline-methods] */
    public Lua visitNamedAccess2(LuaParser.NamedAccessContext ctx) {
        return new Lua.MemberAccessor.Named(ctx.NAME().getText());
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitTableArgument, reason: merged with bridge method [inline-methods] */
    public Lua visitTableArgument2(LuaParser.TableArgumentContext ctx) {
        return visitTableConstructor2(ctx.tableConstructor());
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitStringArgument, reason: merged with bridge method [inline-methods] */
    public Lua visitStringArgument2(LuaParser.StringArgumentContext ctx) {
        return visitString(ctx.string());
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitFunctionBody, reason: merged with bridge method [inline-methods] */
    public Lua visitFunctionBody2(LuaParser.FunctionBodyContext ctx) {
        List<Lua.Arg> listOf;
        LuaParser.ParameterListContext parlistContext = ctx.parameterList();
        Objects.requireNonNull(parlistContext);
        switch ((int) SwitchBootstraps.typeSwitch(MethodHandles.lookup(), "typeSwitch", MethodType.methodType(Integer.TYPE, LuaParser.ParameterListContext.class, Integer.TYPE), LuaParser.PopulatedParListContext.class, LuaParser.VarargParListContext.class, LuaParser.EmptyParListContext.class, LuaParser.ParameterListContext.class).dynamicInvoker().invoke(parlistContext, 0) /* invoke-custom */) {
            case 0:
                LuaParser.PopulatedParListContext populatedParListContext = (LuaParser.PopulatedParListContext) parlistContext;
                List<Lua.Arg> args = new ArrayList<>();
                Stream map = populatedParListContext.nameList().NAME().stream().map((v0) -> {
                    return v0.getText();
                }).map(Lua.Arg.Named::new);
                Objects.requireNonNull(args);
                map.forEach((v1) -> {
                    r1.add(v1);
                });
                if (populatedParListContext.vararg != null) {
                    args.add(new Lua.Arg.Var());
                }
                listOf = args;
                break;
            case 1:
                listOf = List.of(new Lua.Arg.Var());
                break;
            case 2:
                listOf = List.of();
                break;
            default:
                throw new UnsupportedOperationException("Unable to handle par list of type %s".formatted(parlistContext.getClass().getCanonicalName()));
        }
        List<Lua.Arg> arguments = listOf;
        Lua.Block block = visitBlock2(ctx.block());
        return new Lua.FunctionBody(arguments, block);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public Lua visitPopulatedParList(LuaParser.PopulatedParListContext ctx) {
        throw new UnsupportedOperationException();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public Lua visitVarargParList(LuaParser.VarargParListContext ctx) {
        throw new UnsupportedOperationException();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public Lua visitEmptyParList(LuaParser.EmptyParListContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitTableConstructor, reason: merged with bridge method [inline-methods] */
    public Lua visitTableConstructor2(LuaParser.TableConstructorContext ctx) {
        LuaParser.ExpressionContext expressionContext;
        Lua.Expression num;
        LuaParser.FieldListContext fieldListContext = ctx.fieldList();
        if (fieldListContext == null) {
            return new Lua.Value.Table(Map.of());
        }
        Map<Lua.Expression, Lua.Expression> values = new HashMap<>();
        int index = 1;
        for (LuaParser.FieldContext field : fieldListContext.field()) {
            Objects.requireNonNull(field);
            switch ((int) SwitchBootstraps.typeSwitch(MethodHandles.lookup(), "typeSwitch", MethodType.methodType(Integer.TYPE, LuaParser.FieldContext.class, Integer.TYPE), LuaParser.ExpressionFieldAssignmentContext.class, LuaParser.DirectFieldAssignmentContext.class, LuaParser.IndexFieldAssingmentContext.class, LuaParser.FieldContext.class).dynamicInvoker().invoke(field, 0) /* invoke-custom */) {
                case 0:
                    LuaParser.ExpressionFieldAssignmentContext expressionFieldAssignmentContext = (LuaParser.ExpressionFieldAssignmentContext) field;
                    expressionContext = expressionFieldAssignmentContext.value;
                    break;
                case 1:
                    LuaParser.DirectFieldAssignmentContext directFieldAssignmentContext = (LuaParser.DirectFieldAssignmentContext) field;
                    expressionContext = directFieldAssignmentContext.value;
                    break;
                case 2:
                    LuaParser.IndexFieldAssingmentContext indexFieldAssingmentContext = (LuaParser.IndexFieldAssingmentContext) field;
                    expressionContext = indexFieldAssingmentContext.value;
                    break;
                default:
                    throw new UnsupportedOperationException("Unable to handle field assignments of type %s".formatted(field.getClass().getCanonicalName()));
            }
            Lua.Expression value = visitExpression(expressionContext);
            Objects.requireNonNull(field);
            switch ((int) SwitchBootstraps.typeSwitch(MethodHandles.lookup(), "typeSwitch", MethodType.methodType(Integer.TYPE, LuaParser.FieldContext.class, Integer.TYPE), LuaParser.ExpressionFieldAssignmentContext.class, LuaParser.DirectFieldAssignmentContext.class, LuaParser.IndexFieldAssingmentContext.class, LuaParser.FieldContext.class).dynamicInvoker().invoke(field, 0) /* invoke-custom */) {
                case 0:
                    LuaParser.ExpressionFieldAssignmentContext expressionFieldAssignmentContext2 = (LuaParser.ExpressionFieldAssignmentContext) field;
                    num = visitExpression(expressionFieldAssignmentContext2.key);
                    break;
                case 1:
                    LuaParser.DirectFieldAssignmentContext directFieldAssignmentContext2 = (LuaParser.DirectFieldAssignmentContext) field;
                    num = new Lua.Value.Str(directFieldAssignmentContext2.NAME().getText());
                    break;
                case 2:
                    int i = index;
                    index++;
                    num = new Lua.Value.Num(i);
                    break;
                default:
                    throw new UnsupportedOperationException("Unable to handle field assignments of type %s".formatted(field.getClass().getCanonicalName()));
            }
            Lua.Expression key = num;
            values.put(key, value);
        }
        return new Lua.Value.Table(values);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitIntNumber, reason: merged with bridge method [inline-methods] */
    public Lua visitIntNumber2(LuaParser.IntNumberContext ctx) {
        return new Lua.Value.Num(Double.parseDouble(ctx.getText()));
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitHexNumber, reason: merged with bridge method [inline-methods] */
    public Lua visitHexNumber2(LuaParser.HexNumberContext ctx) {
        return new Lua.Value.Num(Double.parseDouble(ctx.getText()));
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitFloatNumber, reason: merged with bridge method [inline-methods] */
    public Lua visitFloatNumber2(LuaParser.FloatNumberContext ctx) {
        return new Lua.Value.Num(Double.parseDouble(ctx.getText()));
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitHexFloatNumber, reason: merged with bridge method [inline-methods] */
    public Lua visitHexFloatNumber2(LuaParser.HexFloatNumberContext ctx) {
        return new Lua.Value.Num(Double.parseDouble(ctx.getText()));
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitNormalString, reason: merged with bridge method [inline-methods] */
    public Lua visitNormalString2(LuaParser.NormalStringContext ctx) {
        String text = ctx.getText();
        return new Lua.Value.Str(text.substring(1, text.length() - 1));
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitCharString, reason: merged with bridge method [inline-methods] */
    public Lua visitCharString2(LuaParser.CharStringContext ctx) {
        String text = ctx.getText();
        return new Lua.Value.Str(text.substring(1, text.length() - 1));
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    /* JADX INFO: renamed from: visitLongString, reason: merged with bridge method [inline-methods] */
    public Lua visitLongString2(LuaParser.LongStringContext ctx) {
        String text = ctx.getText();
        return new Lua.Value.Str(text.substring(2, text.length() - 2));
    }

    public Lua.Value.Str visitString(LuaParser.StringContext ctx) {
        Objects.requireNonNull(ctx);
        switch ((int) SwitchBootstraps.typeSwitch(MethodHandles.lookup(), "typeSwitch", MethodType.methodType(Integer.TYPE, LuaParser.StringContext.class, Integer.TYPE), LuaParser.NormalStringContext.class, LuaParser.CharStringContext.class, LuaParser.LongStringContext.class, LuaParser.StringContext.class).dynamicInvoker().invoke(ctx, 0) /* invoke-custom */) {
            case 0:
                LuaParser.NormalStringContext normalStringContext = (LuaParser.NormalStringContext) ctx;
                return visitNormalString2(normalStringContext);
            case 1:
                LuaParser.CharStringContext charStringContext = (LuaParser.CharStringContext) ctx;
                return visitCharString2(charStringContext);
            case 2:
                LuaParser.LongStringContext longStringContext = (LuaParser.LongStringContext) ctx;
                return visitLongString2(longStringContext);
            default:
                throw new UnsupportedOperationException("Unable to handle string of type %s".formatted(ctx.getClass().getCanonicalName()));
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public Lua visitFunctionName(LuaParser.FunctionNameContext ctx) {
        throw new UnsupportedOperationException("Function names are not supported independently");
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public Lua visitVarList(LuaParser.VarListContext ctx) {
        throw new UnsupportedOperationException("Var lists are not supported independently");
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public Lua visitNameList(LuaParser.NameListContext ctx) {
        throw new UnsupportedOperationException("Name lists are not supported independently");
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public Lua visitExpressionList(LuaParser.ExpressionListContext ctx) {
        throw new UnsupportedOperationException("Expression lists are not supported independently");
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public Lua visitExpressionArguments(LuaParser.ExpressionArgumentsContext ctx) {
        throw new UnsupportedOperationException("Expression arguments are not supported independently");
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public Lua visitFieldList(LuaParser.FieldListContext ctx) {
        throw new UnsupportedOperationException("Field lists are not supported independently");
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public Lua visitExpressionFieldAssignment(LuaParser.ExpressionFieldAssignmentContext ctx) {
        throw new UnsupportedOperationException("Expression field assignments are not supported independently");
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public Lua visitDirectFieldAssignment(LuaParser.DirectFieldAssignmentContext ctx) {
        throw new UnsupportedOperationException("Direct field assignments are not supported independently");
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public Lua visitIndexFieldAssingment(LuaParser.IndexFieldAssingmentContext ctx) {
        throw new UnsupportedOperationException("Index field assignments are not supported independently");
    }
}
