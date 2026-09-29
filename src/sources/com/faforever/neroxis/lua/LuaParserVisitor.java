package com.faforever.neroxis.lua;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParserVisitor.class */
public interface LuaParserVisitor<T> extends ParseTreeVisitor<T> {
    T visitStart(LuaParser.StartContext ctx);

    T visitBlock(LuaParser.BlockContext ctx);

    T visitAssignmentStatement(LuaParser.AssignmentStatementContext ctx);

    T visitFunctionCallStatement(LuaParser.FunctionCallStatementContext ctx);

    T visitDoStatement(LuaParser.DoStatementContext ctx);

    T visitWhileStatement(LuaParser.WhileStatementContext ctx);

    T visitRepeatStatement(LuaParser.RepeatStatementContext ctx);

    T visitIfStatement(LuaParser.IfStatementContext ctx);

    T visitNumericForStatement(LuaParser.NumericForStatementContext ctx);

    T visitGenericForStatement(LuaParser.GenericForStatementContext ctx);

    T visitFunctionStatement(LuaParser.FunctionStatementContext ctx);

    T visitLocalFunctionStatement(LuaParser.LocalFunctionStatementContext ctx);

    T visitLocalAssignmentStatement(LuaParser.LocalAssignmentStatementContext ctx);

    T visitReturnStatement(LuaParser.ReturnStatementContext ctx);

    T visitBreakStatement(LuaParser.BreakStatementContext ctx);

    T visitContinueStatement(LuaParser.ContinueStatementContext ctx);

    T visitFunctionName(LuaParser.FunctionNameContext ctx);

    T visitVarList(LuaParser.VarListContext ctx);

    T visitMemberVar(LuaParser.MemberVarContext ctx);

    T visitFunctionVar(LuaParser.FunctionVarContext ctx);

    T visitExpressionVar(LuaParser.ExpressionVarContext ctx);

    T visitNameList(LuaParser.NameListContext ctx);

    T visitExpressionList(LuaParser.ExpressionListContext ctx);

    T visitExpressionAccessLiteral(LuaParser.ExpressionAccessLiteralContext ctx);

    T visitVariableLiteral(LuaParser.VariableLiteralContext ctx);

    T visitExpressionUnary(LuaParser.ExpressionUnaryContext ctx);

    T visitExpressionPower(LuaParser.ExpressionPowerContext ctx);

    T visitMemberAccessLiteral(LuaParser.MemberAccessLiteralContext ctx);

    T visitExpressionBitwise(LuaParser.ExpressionBitwiseContext ctx);

    T visitNilLiteral(LuaParser.NilLiteralContext ctx);

    T visitTrueLiteral(LuaParser.TrueLiteralContext ctx);

    T visitVarargLiteral(LuaParser.VarargLiteralContext ctx);

    T visitExpressionAdditive(LuaParser.ExpressionAdditiveContext ctx);

    T visitParenthesizedExpressionLiteral(LuaParser.ParenthesizedExpressionLiteralContext ctx);

    T visitExpressionMultiplicative(LuaParser.ExpressionMultiplicativeContext ctx);

    T visitExpressionConcat(LuaParser.ExpressionConcatContext ctx);

    T visitTableLiteral(LuaParser.TableLiteralContext ctx);

    T visitFalseLiteral(LuaParser.FalseLiteralContext ctx);

    T visitStringLiteral(LuaParser.StringLiteralContext ctx);

    T visitFunctionAccessLiteral(LuaParser.FunctionAccessLiteralContext ctx);

    T visitExpressionComparative(LuaParser.ExpressionComparativeContext ctx);

    T visitExpressionAnd(LuaParser.ExpressionAndContext ctx);

    T visitFunctionLiteral(LuaParser.FunctionLiteralContext ctx);

    T visitFunctionCallLiteral(LuaParser.FunctionCallLiteralContext ctx);

    T visitExpressionOr(LuaParser.ExpressionOrContext ctx);

    T visitNumberLiteral(LuaParser.NumberLiteralContext ctx);

    T visitDirectFunctionCall(LuaParser.DirectFunctionCallContext ctx);

    T visitNestedSelfCall(LuaParser.NestedSelfCallContext ctx);

    T visitExpressionFunctionCall(LuaParser.ExpressionFunctionCallContext ctx);

    T visitExpressionSelfCall(LuaParser.ExpressionSelfCallContext ctx);

    T visitDirectSelfCall(LuaParser.DirectSelfCallContext ctx);

    T visitNestedFunctionCall(LuaParser.NestedFunctionCallContext ctx);

    T visitExpressionAccess(LuaParser.ExpressionAccessContext ctx);

    T visitNamedAccess(LuaParser.NamedAccessContext ctx);

    T visitExpressionArguments(LuaParser.ExpressionArgumentsContext ctx);

    T visitTableArgument(LuaParser.TableArgumentContext ctx);

    T visitStringArgument(LuaParser.StringArgumentContext ctx);

    T visitFunctionBody(LuaParser.FunctionBodyContext ctx);

    T visitPopulatedParList(LuaParser.PopulatedParListContext ctx);

    T visitVarargParList(LuaParser.VarargParListContext ctx);

    T visitEmptyParList(LuaParser.EmptyParListContext ctx);

    T visitTableConstructor(LuaParser.TableConstructorContext ctx);

    T visitFieldList(LuaParser.FieldListContext ctx);

    T visitExpressionFieldAssignment(LuaParser.ExpressionFieldAssignmentContext ctx);

    T visitDirectFieldAssignment(LuaParser.DirectFieldAssignmentContext ctx);

    T visitIndexFieldAssingment(LuaParser.IndexFieldAssingmentContext ctx);

    T visitIntNumber(LuaParser.IntNumberContext ctx);

    T visitHexNumber(LuaParser.HexNumberContext ctx);

    T visitFloatNumber(LuaParser.FloatNumberContext ctx);

    T visitHexFloatNumber(LuaParser.HexFloatNumberContext ctx);

    T visitNormalString(LuaParser.NormalStringContext ctx);

    T visitCharString(LuaParser.CharStringContext ctx);

    T visitLongString(LuaParser.LongStringContext ctx);
}
