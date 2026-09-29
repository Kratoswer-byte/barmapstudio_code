package com.faforever.neroxis.lua;

import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParserBaseVisitor.class */
public class LuaParserBaseVisitor<T> extends AbstractParseTreeVisitor<T> implements LuaParserVisitor<T> {
    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitStart(LuaParser.StartContext startContext) {
        return (T) visitChildren(startContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitBlock(LuaParser.BlockContext blockContext) {
        return (T) visitChildren(blockContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitAssignmentStatement(LuaParser.AssignmentStatementContext assignmentStatementContext) {
        return (T) visitChildren(assignmentStatementContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitFunctionCallStatement(LuaParser.FunctionCallStatementContext functionCallStatementContext) {
        return (T) visitChildren(functionCallStatementContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitDoStatement(LuaParser.DoStatementContext doStatementContext) {
        return (T) visitChildren(doStatementContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitWhileStatement(LuaParser.WhileStatementContext whileStatementContext) {
        return (T) visitChildren(whileStatementContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitRepeatStatement(LuaParser.RepeatStatementContext repeatStatementContext) {
        return (T) visitChildren(repeatStatementContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitIfStatement(LuaParser.IfStatementContext ifStatementContext) {
        return (T) visitChildren(ifStatementContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitNumericForStatement(LuaParser.NumericForStatementContext numericForStatementContext) {
        return (T) visitChildren(numericForStatementContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitGenericForStatement(LuaParser.GenericForStatementContext genericForStatementContext) {
        return (T) visitChildren(genericForStatementContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitFunctionStatement(LuaParser.FunctionStatementContext functionStatementContext) {
        return (T) visitChildren(functionStatementContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitLocalFunctionStatement(LuaParser.LocalFunctionStatementContext localFunctionStatementContext) {
        return (T) visitChildren(localFunctionStatementContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitLocalAssignmentStatement(LuaParser.LocalAssignmentStatementContext localAssignmentStatementContext) {
        return (T) visitChildren(localAssignmentStatementContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitReturnStatement(LuaParser.ReturnStatementContext returnStatementContext) {
        return (T) visitChildren(returnStatementContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitBreakStatement(LuaParser.BreakStatementContext breakStatementContext) {
        return (T) visitChildren(breakStatementContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitContinueStatement(LuaParser.ContinueStatementContext continueStatementContext) {
        return (T) visitChildren(continueStatementContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitFunctionName(LuaParser.FunctionNameContext functionNameContext) {
        return (T) visitChildren(functionNameContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitVarList(LuaParser.VarListContext varListContext) {
        return (T) visitChildren(varListContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitMemberVar(LuaParser.MemberVarContext memberVarContext) {
        return (T) visitChildren(memberVarContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitFunctionVar(LuaParser.FunctionVarContext functionVarContext) {
        return (T) visitChildren(functionVarContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitExpressionVar(LuaParser.ExpressionVarContext expressionVarContext) {
        return (T) visitChildren(expressionVarContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitNameList(LuaParser.NameListContext nameListContext) {
        return (T) visitChildren(nameListContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitExpressionList(LuaParser.ExpressionListContext expressionListContext) {
        return (T) visitChildren(expressionListContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitExpressionAccessLiteral(LuaParser.ExpressionAccessLiteralContext expressionAccessLiteralContext) {
        return (T) visitChildren(expressionAccessLiteralContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitVariableLiteral(LuaParser.VariableLiteralContext variableLiteralContext) {
        return (T) visitChildren(variableLiteralContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitExpressionUnary(LuaParser.ExpressionUnaryContext expressionUnaryContext) {
        return (T) visitChildren(expressionUnaryContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitExpressionPower(LuaParser.ExpressionPowerContext expressionPowerContext) {
        return (T) visitChildren(expressionPowerContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitMemberAccessLiteral(LuaParser.MemberAccessLiteralContext memberAccessLiteralContext) {
        return (T) visitChildren(memberAccessLiteralContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitExpressionBitwise(LuaParser.ExpressionBitwiseContext expressionBitwiseContext) {
        return (T) visitChildren(expressionBitwiseContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitNilLiteral(LuaParser.NilLiteralContext nilLiteralContext) {
        return (T) visitChildren(nilLiteralContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitTrueLiteral(LuaParser.TrueLiteralContext trueLiteralContext) {
        return (T) visitChildren(trueLiteralContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitVarargLiteral(LuaParser.VarargLiteralContext varargLiteralContext) {
        return (T) visitChildren(varargLiteralContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitExpressionAdditive(LuaParser.ExpressionAdditiveContext expressionAdditiveContext) {
        return (T) visitChildren(expressionAdditiveContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitParenthesizedExpressionLiteral(LuaParser.ParenthesizedExpressionLiteralContext parenthesizedExpressionLiteralContext) {
        return (T) visitChildren(parenthesizedExpressionLiteralContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitExpressionMultiplicative(LuaParser.ExpressionMultiplicativeContext expressionMultiplicativeContext) {
        return (T) visitChildren(expressionMultiplicativeContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitExpressionConcat(LuaParser.ExpressionConcatContext expressionConcatContext) {
        return (T) visitChildren(expressionConcatContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitTableLiteral(LuaParser.TableLiteralContext tableLiteralContext) {
        return (T) visitChildren(tableLiteralContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitFalseLiteral(LuaParser.FalseLiteralContext falseLiteralContext) {
        return (T) visitChildren(falseLiteralContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitStringLiteral(LuaParser.StringLiteralContext stringLiteralContext) {
        return (T) visitChildren(stringLiteralContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitFunctionAccessLiteral(LuaParser.FunctionAccessLiteralContext functionAccessLiteralContext) {
        return (T) visitChildren(functionAccessLiteralContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitExpressionComparative(LuaParser.ExpressionComparativeContext expressionComparativeContext) {
        return (T) visitChildren(expressionComparativeContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitExpressionAnd(LuaParser.ExpressionAndContext expressionAndContext) {
        return (T) visitChildren(expressionAndContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitFunctionLiteral(LuaParser.FunctionLiteralContext functionLiteralContext) {
        return (T) visitChildren(functionLiteralContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitFunctionCallLiteral(LuaParser.FunctionCallLiteralContext functionCallLiteralContext) {
        return (T) visitChildren(functionCallLiteralContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitExpressionOr(LuaParser.ExpressionOrContext expressionOrContext) {
        return (T) visitChildren(expressionOrContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitNumberLiteral(LuaParser.NumberLiteralContext numberLiteralContext) {
        return (T) visitChildren(numberLiteralContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitDirectFunctionCall(LuaParser.DirectFunctionCallContext directFunctionCallContext) {
        return (T) visitChildren(directFunctionCallContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitNestedSelfCall(LuaParser.NestedSelfCallContext nestedSelfCallContext) {
        return (T) visitChildren(nestedSelfCallContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitExpressionFunctionCall(LuaParser.ExpressionFunctionCallContext expressionFunctionCallContext) {
        return (T) visitChildren(expressionFunctionCallContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitExpressionSelfCall(LuaParser.ExpressionSelfCallContext expressionSelfCallContext) {
        return (T) visitChildren(expressionSelfCallContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitDirectSelfCall(LuaParser.DirectSelfCallContext directSelfCallContext) {
        return (T) visitChildren(directSelfCallContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitNestedFunctionCall(LuaParser.NestedFunctionCallContext nestedFunctionCallContext) {
        return (T) visitChildren(nestedFunctionCallContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitExpressionAccess(LuaParser.ExpressionAccessContext expressionAccessContext) {
        return (T) visitChildren(expressionAccessContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitNamedAccess(LuaParser.NamedAccessContext namedAccessContext) {
        return (T) visitChildren(namedAccessContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitExpressionArguments(LuaParser.ExpressionArgumentsContext expressionArgumentsContext) {
        return (T) visitChildren(expressionArgumentsContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitTableArgument(LuaParser.TableArgumentContext tableArgumentContext) {
        return (T) visitChildren(tableArgumentContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitStringArgument(LuaParser.StringArgumentContext stringArgumentContext) {
        return (T) visitChildren(stringArgumentContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitFunctionBody(LuaParser.FunctionBodyContext functionBodyContext) {
        return (T) visitChildren(functionBodyContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitPopulatedParList(LuaParser.PopulatedParListContext populatedParListContext) {
        return (T) visitChildren(populatedParListContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitVarargParList(LuaParser.VarargParListContext varargParListContext) {
        return (T) visitChildren(varargParListContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitEmptyParList(LuaParser.EmptyParListContext emptyParListContext) {
        return (T) visitChildren(emptyParListContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitTableConstructor(LuaParser.TableConstructorContext tableConstructorContext) {
        return (T) visitChildren(tableConstructorContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitFieldList(LuaParser.FieldListContext fieldListContext) {
        return (T) visitChildren(fieldListContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitExpressionFieldAssignment(LuaParser.ExpressionFieldAssignmentContext expressionFieldAssignmentContext) {
        return (T) visitChildren(expressionFieldAssignmentContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitDirectFieldAssignment(LuaParser.DirectFieldAssignmentContext directFieldAssignmentContext) {
        return (T) visitChildren(directFieldAssignmentContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitIndexFieldAssingment(LuaParser.IndexFieldAssingmentContext indexFieldAssingmentContext) {
        return (T) visitChildren(indexFieldAssingmentContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitIntNumber(LuaParser.IntNumberContext intNumberContext) {
        return (T) visitChildren(intNumberContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitHexNumber(LuaParser.HexNumberContext hexNumberContext) {
        return (T) visitChildren(hexNumberContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitFloatNumber(LuaParser.FloatNumberContext floatNumberContext) {
        return (T) visitChildren(floatNumberContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitHexFloatNumber(LuaParser.HexFloatNumberContext hexFloatNumberContext) {
        return (T) visitChildren(hexFloatNumberContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitNormalString(LuaParser.NormalStringContext normalStringContext) {
        return (T) visitChildren(normalStringContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitCharString(LuaParser.CharStringContext charStringContext) {
        return (T) visitChildren(charStringContext);
    }

    @Override // com.faforever.neroxis.lua.LuaParserVisitor
    public T visitLongString(LuaParser.LongStringContext longStringContext) {
        return (T) visitChildren(longStringContext);
    }
}
