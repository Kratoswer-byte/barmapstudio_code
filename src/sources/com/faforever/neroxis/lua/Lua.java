package com.faforever.neroxis.lua;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.jspecify.annotations.NullMarked;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua.class */
@NullMarked
public interface Lua {

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$Expression.class */
    public interface Expression extends Lua {
    }

    static Block parse(InputStream inputStream) throws IOException {
        CharStream charStream = CharStreams.fromStream(inputStream);
        LuaLexer expressionLexer = new LuaLexer(charStream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(expressionLexer);
        LuaParser luaParser = new LuaParser(commonTokenStream);
        return new LuaParserVisitorImpl().visitStart2(luaParser.start());
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$Block.class */
    public static final class Block extends Record implements Lua {
        private final List<? extends Statement> statements;

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Block.class), Block.class, "statements", "FIELD:Lcom/faforever/neroxis/lua/Lua$Block;->statements:Ljava/util/List;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Block.class), Block.class, "statements", "FIELD:Lcom/faforever/neroxis/lua/Lua$Block;->statements:Ljava/util/List;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Block.class, Object.class), Block.class, "statements", "FIELD:Lcom/faforever/neroxis/lua/Lua$Block;->statements:Ljava/util/List;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public List<? extends Statement> statements() {
            return this.statements;
        }

        public Block(List<? extends Statement> statements) {
            this.statements = List.copyOf(statements);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$Statement.class */
    public interface Statement extends Lua {

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$Statement$Assignment.class */
        public static final class Assignment extends Record implements Statement {
            private final List<? extends Variable> targets;
            private final List<? extends Expression> values;

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Assignment.class), Assignment.class, "targets;values", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$Assignment;->targets:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$Assignment;->values:Ljava/util/List;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Assignment.class), Assignment.class, "targets;values", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$Assignment;->targets:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$Assignment;->values:Ljava/util/List;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Assignment.class, Object.class), Assignment.class, "targets;values", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$Assignment;->targets:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$Assignment;->values:Ljava/util/List;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public List<? extends Variable> targets() {
                return this.targets;
            }

            public List<? extends Expression> values() {
                return this.values;
            }

            public Assignment(List<? extends Variable> targets, List<? extends Expression> values) {
                List<? extends Variable> targets2 = List.copyOf(targets);
                List<? extends Expression> values2 = List.copyOf(values);
                this.targets = targets2;
                this.values = values2;
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$Statement$LocalAssignment.class */
        public static final class LocalAssignment extends Record implements Statement {
            private final List<String> targets;
            private final List<? extends Expression> values;

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, LocalAssignment.class), LocalAssignment.class, "targets;values", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$LocalAssignment;->targets:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$LocalAssignment;->values:Ljava/util/List;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, LocalAssignment.class), LocalAssignment.class, "targets;values", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$LocalAssignment;->targets:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$LocalAssignment;->values:Ljava/util/List;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, LocalAssignment.class, Object.class), LocalAssignment.class, "targets;values", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$LocalAssignment;->targets:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$LocalAssignment;->values:Ljava/util/List;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public List<String> targets() {
                return this.targets;
            }

            public List<? extends Expression> values() {
                return this.values;
            }

            public LocalAssignment(List<String> targets, List<? extends Expression> values) {
                List<String> targets2 = List.copyOf(targets);
                List<? extends Expression> values2 = List.copyOf(values);
                this.targets = targets2;
                this.values = values2;
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$Statement$Break.class */
        public static final class Break extends Record implements Statement {
            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Break.class), Break.class, "").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Break.class), Break.class, "").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Break.class, Object.class), Break.class, "").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$Statement$Do.class */
        public static final class Do extends Record implements Statement {
            private final Block block;

            public Do(Block block) {
                this.block = block;
            }

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Do.class), Do.class, "block", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$Do;->block:Lcom/faforever/neroxis/lua/Lua$Block;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Do.class), Do.class, "block", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$Do;->block:Lcom/faforever/neroxis/lua/Lua$Block;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Do.class, Object.class), Do.class, "block", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$Do;->block:Lcom/faforever/neroxis/lua/Lua$Block;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public Block block() {
                return this.block;
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$Statement$While.class */
        public static final class While extends Record implements Statement {
            private final Expression condition;
            private final Block block;

            public While(Expression condition, Block block) {
                this.condition = condition;
                this.block = block;
            }

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, While.class), While.class, "condition;block", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$While;->condition:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$While;->block:Lcom/faforever/neroxis/lua/Lua$Block;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, While.class), While.class, "condition;block", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$While;->condition:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$While;->block:Lcom/faforever/neroxis/lua/Lua$Block;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, While.class, Object.class), While.class, "condition;block", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$While;->condition:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$While;->block:Lcom/faforever/neroxis/lua/Lua$Block;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public Expression condition() {
                return this.condition;
            }

            public Block block() {
                return this.block;
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$Statement$Repeat.class */
        public static final class Repeat extends Record implements Statement {
            private final Block block;
            private final Expression condition;

            public Repeat(Block block, Expression condition) {
                this.block = block;
                this.condition = condition;
            }

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Repeat.class), Repeat.class, "block;condition", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$Repeat;->block:Lcom/faforever/neroxis/lua/Lua$Block;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$Repeat;->condition:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Repeat.class), Repeat.class, "block;condition", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$Repeat;->block:Lcom/faforever/neroxis/lua/Lua$Block;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$Repeat;->condition:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Repeat.class, Object.class), Repeat.class, "block;condition", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$Repeat;->block:Lcom/faforever/neroxis/lua/Lua$Block;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$Repeat;->condition:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public Block block() {
                return this.block;
            }

            public Expression condition() {
                return this.condition;
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$Statement$If.class */
        public static final class If extends Record implements Statement {
            private final List<Condition> conditions;

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, If.class), If.class, "conditions", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$If;->conditions:Ljava/util/List;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, If.class), If.class, "conditions", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$If;->conditions:Ljava/util/List;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, If.class, Object.class), If.class, "conditions", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$If;->conditions:Ljava/util/List;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public List<Condition> conditions() {
                return this.conditions;
            }

            public If(List<Condition> conditions) {
                this.conditions = List.copyOf(conditions);
            }

            /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$Statement$If$Condition.class */
            public interface Condition {

                /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$Statement$If$Condition$Expression.class */
                public static final class Expression extends Record implements Condition {
                    private final Expression condition;
                    private final Block block;

                    public Expression(Expression condition, Block block) {
                        this.condition = condition;
                        this.block = block;
                    }

                    @Override // java.lang.Record
                    public final String toString() {
                        return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Expression.class), Expression.class, "condition;block", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$If$Condition$Expression;->condition:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$If$Condition$Expression;->block:Lcom/faforever/neroxis/lua/Lua$Block;").dynamicInvoker().invoke(this) /* invoke-custom */;
                    }

                    @Override // java.lang.Record
                    public final int hashCode() {
                        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Expression.class), Expression.class, "condition;block", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$If$Condition$Expression;->condition:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$If$Condition$Expression;->block:Lcom/faforever/neroxis/lua/Lua$Block;").dynamicInvoker().invoke(this) /* invoke-custom */;
                    }

                    @Override // java.lang.Record
                    public final boolean equals(Object o) {
                        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Expression.class, Object.class), Expression.class, "condition;block", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$If$Condition$Expression;->condition:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$If$Condition$Expression;->block:Lcom/faforever/neroxis/lua/Lua$Block;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
                    }

                    public Expression condition() {
                        return this.condition;
                    }

                    public Block block() {
                        return this.block;
                    }
                }

                /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$Statement$If$Condition$Else.class */
                public static final class Else extends Record implements Condition {
                    private final Block block;

                    public Else(Block block) {
                        this.block = block;
                    }

                    @Override // java.lang.Record
                    public final String toString() {
                        return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Else.class), Else.class, "block", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$If$Condition$Else;->block:Lcom/faforever/neroxis/lua/Lua$Block;").dynamicInvoker().invoke(this) /* invoke-custom */;
                    }

                    @Override // java.lang.Record
                    public final int hashCode() {
                        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Else.class), Else.class, "block", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$If$Condition$Else;->block:Lcom/faforever/neroxis/lua/Lua$Block;").dynamicInvoker().invoke(this) /* invoke-custom */;
                    }

                    @Override // java.lang.Record
                    public final boolean equals(Object o) {
                        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Else.class, Object.class), Else.class, "block", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$If$Condition$Else;->block:Lcom/faforever/neroxis/lua/Lua$Block;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
                    }

                    public Block block() {
                        return this.block;
                    }
                }
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$Statement$NumericFor.class */
        public static final class NumericFor extends Record implements Statement {
            private final String variable;
            private final Expression start;
            private final Expression end;
            private final Expression step;
            private final Block block;

            public NumericFor(String variable, Expression start, Expression end, Expression step, Block block) {
                this.variable = variable;
                this.start = start;
                this.end = end;
                this.step = step;
                this.block = block;
            }

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, NumericFor.class), NumericFor.class, "variable;start;end;step;block", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$NumericFor;->variable:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$NumericFor;->start:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$NumericFor;->end:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$NumericFor;->step:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$NumericFor;->block:Lcom/faforever/neroxis/lua/Lua$Block;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, NumericFor.class), NumericFor.class, "variable;start;end;step;block", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$NumericFor;->variable:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$NumericFor;->start:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$NumericFor;->end:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$NumericFor;->step:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$NumericFor;->block:Lcom/faforever/neroxis/lua/Lua$Block;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, NumericFor.class, Object.class), NumericFor.class, "variable;start;end;step;block", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$NumericFor;->variable:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$NumericFor;->start:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$NumericFor;->end:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$NumericFor;->step:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$NumericFor;->block:Lcom/faforever/neroxis/lua/Lua$Block;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public String variable() {
                return this.variable;
            }

            public Expression start() {
                return this.start;
            }

            public Expression end() {
                return this.end;
            }

            public Expression step() {
                return this.step;
            }

            public Block block() {
                return this.block;
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$Statement$GenericFor.class */
        public static final class GenericFor extends Record implements Statement {
            private final List<String> variables;
            private final List<? extends Expression> iterators;
            private final Block block;

            public GenericFor(List<String> variables, List<? extends Expression> iterators, Block block) {
                this.variables = variables;
                this.iterators = iterators;
                this.block = block;
            }

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, GenericFor.class), GenericFor.class, "variables;iterators;block", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$GenericFor;->variables:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$GenericFor;->iterators:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$GenericFor;->block:Lcom/faforever/neroxis/lua/Lua$Block;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, GenericFor.class), GenericFor.class, "variables;iterators;block", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$GenericFor;->variables:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$GenericFor;->iterators:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$GenericFor;->block:Lcom/faforever/neroxis/lua/Lua$Block;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, GenericFor.class, Object.class), GenericFor.class, "variables;iterators;block", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$GenericFor;->variables:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$GenericFor;->iterators:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$GenericFor;->block:Lcom/faforever/neroxis/lua/Lua$Block;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public List<String> variables() {
                return this.variables;
            }

            public List<? extends Expression> iterators() {
                return this.iterators;
            }

            public Block block() {
                return this.block;
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$Statement$Function.class */
        public static final class Function extends Record implements Statement {
            private final String name;
            private final FunctionBody body;

            public Function(String name, FunctionBody body) {
                this.name = name;
                this.body = body;
            }

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Function.class), Function.class, "name;body", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$Function;->name:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$Function;->body:Lcom/faforever/neroxis/lua/Lua$FunctionBody;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Function.class), Function.class, "name;body", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$Function;->name:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$Function;->body:Lcom/faforever/neroxis/lua/Lua$FunctionBody;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Function.class, Object.class), Function.class, "name;body", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$Function;->name:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$Function;->body:Lcom/faforever/neroxis/lua/Lua$FunctionBody;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public String name() {
                return this.name;
            }

            public FunctionBody body() {
                return this.body;
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$Statement$LocalFunction.class */
        public static final class LocalFunction extends Record implements Statement {
            private final String name;
            private final FunctionBody body;

            public LocalFunction(String name, FunctionBody body) {
                this.name = name;
                this.body = body;
            }

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, LocalFunction.class), LocalFunction.class, "name;body", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$LocalFunction;->name:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$LocalFunction;->body:Lcom/faforever/neroxis/lua/Lua$FunctionBody;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, LocalFunction.class), LocalFunction.class, "name;body", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$LocalFunction;->name:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$LocalFunction;->body:Lcom/faforever/neroxis/lua/Lua$FunctionBody;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, LocalFunction.class, Object.class), LocalFunction.class, "name;body", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$LocalFunction;->name:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$LocalFunction;->body:Lcom/faforever/neroxis/lua/Lua$FunctionBody;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public String name() {
                return this.name;
            }

            public FunctionBody body() {
                return this.body;
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$Statement$Continue.class */
        public static final class Continue extends Record implements Statement {
            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Continue.class), Continue.class, "").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Continue.class), Continue.class, "").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Continue.class, Object.class), Continue.class, "").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$Statement$Return.class */
        public static final class Return extends Record implements Statement {
            private final List<? extends Expression> values;

            public Return(List<? extends Expression> values) {
                this.values = values;
            }

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Return.class), Return.class, "values", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$Return;->values:Ljava/util/List;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Return.class), Return.class, "values", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$Return;->values:Ljava/util/List;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Return.class, Object.class), Return.class, "values", "FIELD:Lcom/faforever/neroxis/lua/Lua$Statement$Return;->values:Ljava/util/List;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public List<? extends Expression> values() {
                return this.values;
            }
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$Value.class */
    public interface Value extends Expression {

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$Value$Nil.class */
        public static final class Nil extends Record implements Value {
            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Nil.class), Nil.class, "").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Nil.class), Nil.class, "").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Nil.class, Object.class), Nil.class, "").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$Value$Bool.class */
        public static final class Bool extends Record implements Value {
            private final boolean value;

            public Bool(boolean value) {
                this.value = value;
            }

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Bool.class), Bool.class, "value", "FIELD:Lcom/faforever/neroxis/lua/Lua$Value$Bool;->value:Z").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Bool.class), Bool.class, "value", "FIELD:Lcom/faforever/neroxis/lua/Lua$Value$Bool;->value:Z").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Bool.class, Object.class), Bool.class, "value", "FIELD:Lcom/faforever/neroxis/lua/Lua$Value$Bool;->value:Z").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public boolean value() {
                return this.value;
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$Value$Num.class */
        public static final class Num extends Record implements Value {
            private final double value;

            public Num(double value) {
                this.value = value;
            }

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Num.class), Num.class, "value", "FIELD:Lcom/faforever/neroxis/lua/Lua$Value$Num;->value:D").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Num.class), Num.class, "value", "FIELD:Lcom/faforever/neroxis/lua/Lua$Value$Num;->value:D").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Num.class, Object.class), Num.class, "value", "FIELD:Lcom/faforever/neroxis/lua/Lua$Value$Num;->value:D").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public double value() {
                return this.value;
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$Value$Str.class */
        public static final class Str extends Record implements Value {
            private final String value;

            public Str(String value) {
                this.value = value;
            }

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Str.class), Str.class, "value", "FIELD:Lcom/faforever/neroxis/lua/Lua$Value$Str;->value:Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Str.class), Str.class, "value", "FIELD:Lcom/faforever/neroxis/lua/Lua$Value$Str;->value:Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Str.class, Object.class), Str.class, "value", "FIELD:Lcom/faforever/neroxis/lua/Lua$Value$Str;->value:Ljava/lang/String;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public String value() {
                return this.value;
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$Value$VarArg.class */
        public static final class VarArg extends Record implements Value {
            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, VarArg.class), VarArg.class, "").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, VarArg.class), VarArg.class, "").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, VarArg.class, Object.class), VarArg.class, "").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$Value$Table.class */
        public static final class Table extends Record implements Value {
            private final Map<? extends Expression, ? extends Expression> contents;

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Table.class), Table.class, "contents", "FIELD:Lcom/faforever/neroxis/lua/Lua$Value$Table;->contents:Ljava/util/Map;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Table.class), Table.class, "contents", "FIELD:Lcom/faforever/neroxis/lua/Lua$Value$Table;->contents:Ljava/util/Map;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Table.class, Object.class), Table.class, "contents", "FIELD:Lcom/faforever/neroxis/lua/Lua$Value$Table;->contents:Ljava/util/Map;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public Map<? extends Expression, ? extends Expression> contents() {
                return this.contents;
            }

            public Table(Map<? extends Expression, ? extends Expression> contents) {
                this.contents = Map.copyOf(contents);
            }

            public Expression get(String key) {
                return contents().get(new Str(key));
            }

            public Expression get(Number key) {
                return contents().get(new Num(key.doubleValue()));
            }

            public Expression get(Expression key) {
                return contents().get(key);
            }

            public void forEach(BiConsumer<Expression, Expression> action) {
                contents().forEach(action);
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$Value$Function.class */
        public static final class Function extends Record implements Value {
            private final FunctionBody body;

            public Function(FunctionBody body) {
                this.body = body;
            }

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Function.class), Function.class, "body", "FIELD:Lcom/faforever/neroxis/lua/Lua$Value$Function;->body:Lcom/faforever/neroxis/lua/Lua$FunctionBody;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Function.class), Function.class, "body", "FIELD:Lcom/faforever/neroxis/lua/Lua$Value$Function;->body:Lcom/faforever/neroxis/lua/Lua$FunctionBody;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Function.class, Object.class), Function.class, "body", "FIELD:Lcom/faforever/neroxis/lua/Lua$Value$Function;->body:Lcom/faforever/neroxis/lua/Lua$FunctionBody;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public FunctionBody body() {
                return this.body;
            }
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$UnaryOperator.class */
    public interface UnaryOperator extends Expression {

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$UnaryOperator$Not.class */
        public static final class Not extends Record implements UnaryOperator {
            private final Expression expression;

            public Not(Expression expression) {
                this.expression = expression;
            }

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Not.class), Not.class, "expression", "FIELD:Lcom/faforever/neroxis/lua/Lua$UnaryOperator$Not;->expression:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Not.class), Not.class, "expression", "FIELD:Lcom/faforever/neroxis/lua/Lua$UnaryOperator$Not;->expression:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Not.class, Object.class), Not.class, "expression", "FIELD:Lcom/faforever/neroxis/lua/Lua$UnaryOperator$Not;->expression:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public Expression expression() {
                return this.expression;
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$UnaryOperator$BitNot.class */
        public static final class BitNot extends Record implements UnaryOperator {
            private final Expression expression;

            public BitNot(Expression expression) {
                this.expression = expression;
            }

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, BitNot.class), BitNot.class, "expression", "FIELD:Lcom/faforever/neroxis/lua/Lua$UnaryOperator$BitNot;->expression:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, BitNot.class), BitNot.class, "expression", "FIELD:Lcom/faforever/neroxis/lua/Lua$UnaryOperator$BitNot;->expression:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, BitNot.class, Object.class), BitNot.class, "expression", "FIELD:Lcom/faforever/neroxis/lua/Lua$UnaryOperator$BitNot;->expression:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public Expression expression() {
                return this.expression;
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$UnaryOperator$Negate.class */
        public static final class Negate extends Record implements UnaryOperator {
            private final Expression expression;

            public Negate(Expression expression) {
                this.expression = expression;
            }

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Negate.class), Negate.class, "expression", "FIELD:Lcom/faforever/neroxis/lua/Lua$UnaryOperator$Negate;->expression:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Negate.class), Negate.class, "expression", "FIELD:Lcom/faforever/neroxis/lua/Lua$UnaryOperator$Negate;->expression:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Negate.class, Object.class), Negate.class, "expression", "FIELD:Lcom/faforever/neroxis/lua/Lua$UnaryOperator$Negate;->expression:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public Expression expression() {
                return this.expression;
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$UnaryOperator$Length.class */
        public static final class Length extends Record implements UnaryOperator {
            private final Expression expression;

            public Length(Expression expression) {
                this.expression = expression;
            }

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Length.class), Length.class, "expression", "FIELD:Lcom/faforever/neroxis/lua/Lua$UnaryOperator$Length;->expression:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Length.class), Length.class, "expression", "FIELD:Lcom/faforever/neroxis/lua/Lua$UnaryOperator$Length;->expression:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Length.class, Object.class), Length.class, "expression", "FIELD:Lcom/faforever/neroxis/lua/Lua$UnaryOperator$Length;->expression:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public Expression expression() {
                return this.expression;
            }
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$BinaryOperator.class */
    public interface BinaryOperator extends Expression {

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$BinaryOperator$Add.class */
        public static final class Add extends Record implements BinaryOperator {
            private final Expression left;
            private final Expression right;

            public Add(Expression left, Expression right) {
                this.left = left;
                this.right = right;
            }

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Add.class), Add.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Add;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Add;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Add.class), Add.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Add;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Add;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Add.class, Object.class), Add.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Add;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Add;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public Expression left() {
                return this.left;
            }

            public Expression right() {
                return this.right;
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$BinaryOperator$Subtract.class */
        public static final class Subtract extends Record implements BinaryOperator {
            private final Expression left;
            private final Expression right;

            public Subtract(Expression left, Expression right) {
                this.left = left;
                this.right = right;
            }

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Subtract.class), Subtract.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Subtract;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Subtract;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Subtract.class), Subtract.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Subtract;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Subtract;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Subtract.class, Object.class), Subtract.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Subtract;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Subtract;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public Expression left() {
                return this.left;
            }

            public Expression right() {
                return this.right;
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$BinaryOperator$Multiply.class */
        public static final class Multiply extends Record implements BinaryOperator {
            private final Expression left;
            private final Expression right;

            public Multiply(Expression left, Expression right) {
                this.left = left;
                this.right = right;
            }

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Multiply.class), Multiply.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Multiply;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Multiply;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Multiply.class), Multiply.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Multiply;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Multiply;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Multiply.class, Object.class), Multiply.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Multiply;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Multiply;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public Expression left() {
                return this.left;
            }

            public Expression right() {
                return this.right;
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$BinaryOperator$Divide.class */
        public static final class Divide extends Record implements BinaryOperator {
            private final Expression left;
            private final Expression right;

            public Divide(Expression left, Expression right) {
                this.left = left;
                this.right = right;
            }

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Divide.class), Divide.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Divide;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Divide;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Divide.class), Divide.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Divide;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Divide;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Divide.class, Object.class), Divide.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Divide;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Divide;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public Expression left() {
                return this.left;
            }

            public Expression right() {
                return this.right;
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$BinaryOperator$FloorDivide.class */
        public static final class FloorDivide extends Record implements BinaryOperator {
            private final Expression left;
            private final Expression right;

            public FloorDivide(Expression left, Expression right) {
                this.left = left;
                this.right = right;
            }

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, FloorDivide.class), FloorDivide.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$FloorDivide;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$FloorDivide;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, FloorDivide.class), FloorDivide.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$FloorDivide;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$FloorDivide;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, FloorDivide.class, Object.class), FloorDivide.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$FloorDivide;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$FloorDivide;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public Expression left() {
                return this.left;
            }

            public Expression right() {
                return this.right;
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$BinaryOperator$Power.class */
        public static final class Power extends Record implements BinaryOperator {
            private final Expression left;
            private final Expression right;

            public Power(Expression left, Expression right) {
                this.left = left;
                this.right = right;
            }

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Power.class), Power.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Power;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Power;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Power.class), Power.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Power;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Power;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Power.class, Object.class), Power.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Power;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Power;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public Expression left() {
                return this.left;
            }

            public Expression right() {
                return this.right;
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$BinaryOperator$Modulo.class */
        public static final class Modulo extends Record implements BinaryOperator {
            private final Expression left;
            private final Expression right;

            public Modulo(Expression left, Expression right) {
                this.left = left;
                this.right = right;
            }

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Modulo.class), Modulo.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Modulo;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Modulo;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Modulo.class), Modulo.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Modulo;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Modulo;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Modulo.class, Object.class), Modulo.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Modulo;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Modulo;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public Expression left() {
                return this.left;
            }

            public Expression right() {
                return this.right;
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$BinaryOperator$Concat.class */
        public static final class Concat extends Record implements BinaryOperator {
            private final Expression left;
            private final Expression right;

            public Concat(Expression left, Expression right) {
                this.left = left;
                this.right = right;
            }

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Concat.class), Concat.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Concat;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Concat;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Concat.class), Concat.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Concat;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Concat;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Concat.class, Object.class), Concat.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Concat;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Concat;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public Expression left() {
                return this.left;
            }

            public Expression right() {
                return this.right;
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$BinaryOperator$And.class */
        public static final class And extends Record implements BinaryOperator {
            private final Expression left;
            private final Expression right;

            public And(Expression left, Expression right) {
                this.left = left;
                this.right = right;
            }

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, And.class), And.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$And;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$And;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, And.class), And.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$And;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$And;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, And.class, Object.class), And.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$And;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$And;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public Expression left() {
                return this.left;
            }

            public Expression right() {
                return this.right;
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$BinaryOperator$Or.class */
        public static final class Or extends Record implements BinaryOperator {
            private final Expression left;
            private final Expression right;

            public Or(Expression left, Expression right) {
                this.left = left;
                this.right = right;
            }

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Or.class), Or.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Or;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Or;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Or.class), Or.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Or;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Or;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Or.class, Object.class), Or.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Or;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Or;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public Expression left() {
                return this.left;
            }

            public Expression right() {
                return this.right;
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$BinaryOperator$LeftShift.class */
        public static final class LeftShift extends Record implements BinaryOperator {
            private final Expression left;
            private final Expression right;

            public LeftShift(Expression left, Expression right) {
                this.left = left;
                this.right = right;
            }

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, LeftShift.class), LeftShift.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$LeftShift;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$LeftShift;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, LeftShift.class), LeftShift.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$LeftShift;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$LeftShift;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, LeftShift.class, Object.class), LeftShift.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$LeftShift;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$LeftShift;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public Expression left() {
                return this.left;
            }

            public Expression right() {
                return this.right;
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$BinaryOperator$RightShift.class */
        public static final class RightShift extends Record implements BinaryOperator {
            private final Expression left;
            private final Expression right;

            public RightShift(Expression left, Expression right) {
                this.left = left;
                this.right = right;
            }

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, RightShift.class), RightShift.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$RightShift;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$RightShift;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, RightShift.class), RightShift.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$RightShift;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$RightShift;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, RightShift.class, Object.class), RightShift.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$RightShift;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$RightShift;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public Expression left() {
                return this.left;
            }

            public Expression right() {
                return this.right;
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$BinaryOperator$BitAnd.class */
        public static final class BitAnd extends Record implements BinaryOperator {
            private final Expression left;
            private final Expression right;

            public BitAnd(Expression left, Expression right) {
                this.left = left;
                this.right = right;
            }

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, BitAnd.class), BitAnd.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$BitAnd;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$BitAnd;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, BitAnd.class), BitAnd.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$BitAnd;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$BitAnd;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, BitAnd.class, Object.class), BitAnd.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$BitAnd;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$BitAnd;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public Expression left() {
                return this.left;
            }

            public Expression right() {
                return this.right;
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$BinaryOperator$BitOr.class */
        public static final class BitOr extends Record implements BinaryOperator {
            private final Expression left;
            private final Expression right;

            public BitOr(Expression left, Expression right) {
                this.left = left;
                this.right = right;
            }

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, BitOr.class), BitOr.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$BitOr;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$BitOr;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, BitOr.class), BitOr.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$BitOr;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$BitOr;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, BitOr.class, Object.class), BitOr.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$BitOr;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$BitOr;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public Expression left() {
                return this.left;
            }

            public Expression right() {
                return this.right;
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$BinaryOperator$BitXor.class */
        public static final class BitXor extends Record implements BinaryOperator {
            private final Expression left;
            private final Expression right;

            public BitXor(Expression left, Expression right) {
                this.left = left;
                this.right = right;
            }

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, BitXor.class), BitXor.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$BitXor;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$BitXor;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, BitXor.class), BitXor.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$BitXor;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$BitXor;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, BitXor.class, Object.class), BitXor.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$BitXor;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$BitXor;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public Expression left() {
                return this.left;
            }

            public Expression right() {
                return this.right;
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$BinaryOperator$GreaterThan.class */
        public static final class GreaterThan extends Record implements BinaryOperator {
            private final Expression left;
            private final Expression right;

            public GreaterThan(Expression left, Expression right) {
                this.left = left;
                this.right = right;
            }

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, GreaterThan.class), GreaterThan.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$GreaterThan;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$GreaterThan;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, GreaterThan.class), GreaterThan.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$GreaterThan;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$GreaterThan;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, GreaterThan.class, Object.class), GreaterThan.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$GreaterThan;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$GreaterThan;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public Expression left() {
                return this.left;
            }

            public Expression right() {
                return this.right;
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$BinaryOperator$GreaterThanOrEqual.class */
        public static final class GreaterThanOrEqual extends Record implements BinaryOperator {
            private final Expression left;
            private final Expression right;

            public GreaterThanOrEqual(Expression left, Expression right) {
                this.left = left;
                this.right = right;
            }

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, GreaterThanOrEqual.class), GreaterThanOrEqual.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$GreaterThanOrEqual;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$GreaterThanOrEqual;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, GreaterThanOrEqual.class), GreaterThanOrEqual.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$GreaterThanOrEqual;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$GreaterThanOrEqual;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, GreaterThanOrEqual.class, Object.class), GreaterThanOrEqual.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$GreaterThanOrEqual;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$GreaterThanOrEqual;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public Expression left() {
                return this.left;
            }

            public Expression right() {
                return this.right;
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$BinaryOperator$LessThan.class */
        public static final class LessThan extends Record implements BinaryOperator {
            private final Expression left;
            private final Expression right;

            public LessThan(Expression left, Expression right) {
                this.left = left;
                this.right = right;
            }

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, LessThan.class), LessThan.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$LessThan;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$LessThan;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, LessThan.class), LessThan.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$LessThan;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$LessThan;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, LessThan.class, Object.class), LessThan.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$LessThan;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$LessThan;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public Expression left() {
                return this.left;
            }

            public Expression right() {
                return this.right;
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$BinaryOperator$LessThanOrEqual.class */
        public static final class LessThanOrEqual extends Record implements BinaryOperator {
            private final Expression left;
            private final Expression right;

            public LessThanOrEqual(Expression left, Expression right) {
                this.left = left;
                this.right = right;
            }

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, LessThanOrEqual.class), LessThanOrEqual.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$LessThanOrEqual;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$LessThanOrEqual;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, LessThanOrEqual.class), LessThanOrEqual.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$LessThanOrEqual;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$LessThanOrEqual;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, LessThanOrEqual.class, Object.class), LessThanOrEqual.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$LessThanOrEqual;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$LessThanOrEqual;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public Expression left() {
                return this.left;
            }

            public Expression right() {
                return this.right;
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$BinaryOperator$Equal.class */
        public static final class Equal extends Record implements BinaryOperator {
            private final Expression left;
            private final Expression right;

            public Equal(Expression left, Expression right) {
                this.left = left;
                this.right = right;
            }

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Equal.class), Equal.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Equal;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Equal;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Equal.class), Equal.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Equal;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Equal;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Equal.class, Object.class), Equal.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Equal;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$Equal;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public Expression left() {
                return this.left;
            }

            public Expression right() {
                return this.right;
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$BinaryOperator$NotEqual.class */
        public static final class NotEqual extends Record implements BinaryOperator {
            private final Expression left;
            private final Expression right;

            public NotEqual(Expression left, Expression right) {
                this.left = left;
                this.right = right;
            }

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, NotEqual.class), NotEqual.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$NotEqual;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$NotEqual;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, NotEqual.class), NotEqual.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$NotEqual;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$NotEqual;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, NotEqual.class, Object.class), NotEqual.class, "left;right", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$NotEqual;->left:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$BinaryOperator$NotEqual;->right:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public Expression left() {
                return this.left;
            }

            public Expression right() {
                return this.right;
            }
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$FunctionCall.class */
    public interface FunctionCall extends Statement, Expression {

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$FunctionCall$Direct.class */
        public static final class Direct extends Record implements FunctionCall {
            private final Variable receiver;
            private final List<? extends Expression> arguments;

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Direct.class), Direct.class, "receiver;arguments", "FIELD:Lcom/faforever/neroxis/lua/Lua$FunctionCall$Direct;->receiver:Lcom/faforever/neroxis/lua/Lua$Variable;", "FIELD:Lcom/faforever/neroxis/lua/Lua$FunctionCall$Direct;->arguments:Ljava/util/List;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Direct.class), Direct.class, "receiver;arguments", "FIELD:Lcom/faforever/neroxis/lua/Lua$FunctionCall$Direct;->receiver:Lcom/faforever/neroxis/lua/Lua$Variable;", "FIELD:Lcom/faforever/neroxis/lua/Lua$FunctionCall$Direct;->arguments:Ljava/util/List;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Direct.class, Object.class), Direct.class, "receiver;arguments", "FIELD:Lcom/faforever/neroxis/lua/Lua$FunctionCall$Direct;->receiver:Lcom/faforever/neroxis/lua/Lua$Variable;", "FIELD:Lcom/faforever/neroxis/lua/Lua$FunctionCall$Direct;->arguments:Ljava/util/List;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public Variable receiver() {
                return this.receiver;
            }

            public List<? extends Expression> arguments() {
                return this.arguments;
            }

            public Direct(Variable receiver, List<? extends Expression> arguments) {
                List<? extends Expression> arguments2 = List.copyOf(arguments);
                this.receiver = receiver;
                this.arguments = arguments2;
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$FunctionCall$Self.class */
        public static final class Self extends Record implements FunctionCall {
            private final Variable receiver;
            private final String methodName;
            private final List<? extends Expression> arguments;

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Self.class), Self.class, "receiver;methodName;arguments", "FIELD:Lcom/faforever/neroxis/lua/Lua$FunctionCall$Self;->receiver:Lcom/faforever/neroxis/lua/Lua$Variable;", "FIELD:Lcom/faforever/neroxis/lua/Lua$FunctionCall$Self;->methodName:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/lua/Lua$FunctionCall$Self;->arguments:Ljava/util/List;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Self.class), Self.class, "receiver;methodName;arguments", "FIELD:Lcom/faforever/neroxis/lua/Lua$FunctionCall$Self;->receiver:Lcom/faforever/neroxis/lua/Lua$Variable;", "FIELD:Lcom/faforever/neroxis/lua/Lua$FunctionCall$Self;->methodName:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/lua/Lua$FunctionCall$Self;->arguments:Ljava/util/List;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Self.class, Object.class), Self.class, "receiver;methodName;arguments", "FIELD:Lcom/faforever/neroxis/lua/Lua$FunctionCall$Self;->receiver:Lcom/faforever/neroxis/lua/Lua$Variable;", "FIELD:Lcom/faforever/neroxis/lua/Lua$FunctionCall$Self;->methodName:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/lua/Lua$FunctionCall$Self;->arguments:Ljava/util/List;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public Variable receiver() {
                return this.receiver;
            }

            public String methodName() {
                return this.methodName;
            }

            public List<? extends Expression> arguments() {
                return this.arguments;
            }

            public Self(Variable receiver, String methodName, List<? extends Expression> arguments) {
                List<? extends Expression> arguments2 = List.copyOf(arguments);
                this.receiver = receiver;
                this.methodName = methodName;
                this.arguments = arguments2;
            }
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$Variable.class */
    public interface Variable extends Expression {

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$Variable$Named.class */
        public static final class Named extends Record implements Variable {
            private final String name;
            private final List<? extends MemberAccessor> memberAccessors;

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Named.class), Named.class, "name;memberAccessors", "FIELD:Lcom/faforever/neroxis/lua/Lua$Variable$Named;->name:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Variable$Named;->memberAccessors:Ljava/util/List;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Named.class), Named.class, "name;memberAccessors", "FIELD:Lcom/faforever/neroxis/lua/Lua$Variable$Named;->name:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Variable$Named;->memberAccessors:Ljava/util/List;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Named.class, Object.class), Named.class, "name;memberAccessors", "FIELD:Lcom/faforever/neroxis/lua/Lua$Variable$Named;->name:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Variable$Named;->memberAccessors:Ljava/util/List;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public String name() {
                return this.name;
            }

            public List<? extends MemberAccessor> memberAccessors() {
                return this.memberAccessors;
            }

            public Named(String name, List<? extends MemberAccessor> memberAccessors) {
                List<? extends MemberAccessor> memberAccessors2 = List.copyOf(memberAccessors);
                this.name = name;
                this.memberAccessors = memberAccessors2;
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$Variable$Function.class */
        public static final class Function extends Record implements Variable {
            private final FunctionCall functionCall;
            private final List<? extends MemberAccessor> memberAccessors;

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Function.class), Function.class, "functionCall;memberAccessors", "FIELD:Lcom/faforever/neroxis/lua/Lua$Variable$Function;->functionCall:Lcom/faforever/neroxis/lua/Lua$FunctionCall;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Variable$Function;->memberAccessors:Ljava/util/List;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Function.class), Function.class, "functionCall;memberAccessors", "FIELD:Lcom/faforever/neroxis/lua/Lua$Variable$Function;->functionCall:Lcom/faforever/neroxis/lua/Lua$FunctionCall;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Variable$Function;->memberAccessors:Ljava/util/List;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Function.class, Object.class), Function.class, "functionCall;memberAccessors", "FIELD:Lcom/faforever/neroxis/lua/Lua$Variable$Function;->functionCall:Lcom/faforever/neroxis/lua/Lua$FunctionCall;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Variable$Function;->memberAccessors:Ljava/util/List;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public FunctionCall functionCall() {
                return this.functionCall;
            }

            public List<? extends MemberAccessor> memberAccessors() {
                return this.memberAccessors;
            }

            public Function(FunctionCall functionCall, List<? extends MemberAccessor> memberAccessors) {
                List<? extends MemberAccessor> memberAccessors2 = List.copyOf(memberAccessors);
                this.functionCall = functionCall;
                this.memberAccessors = memberAccessors2;
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$Variable$Expression.class */
        public static final class Expression extends Record implements Variable {
            private final Expression expression;
            private final List<? extends MemberAccessor> memberAccessors;

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Expression.class), Expression.class, "expression;memberAccessors", "FIELD:Lcom/faforever/neroxis/lua/Lua$Variable$Expression;->expression:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Variable$Expression;->memberAccessors:Ljava/util/List;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Expression.class), Expression.class, "expression;memberAccessors", "FIELD:Lcom/faforever/neroxis/lua/Lua$Variable$Expression;->expression:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Variable$Expression;->memberAccessors:Ljava/util/List;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Expression.class, Object.class), Expression.class, "expression;memberAccessors", "FIELD:Lcom/faforever/neroxis/lua/Lua$Variable$Expression;->expression:Lcom/faforever/neroxis/lua/Lua$Expression;", "FIELD:Lcom/faforever/neroxis/lua/Lua$Variable$Expression;->memberAccessors:Ljava/util/List;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public Expression expression() {
                return this.expression;
            }

            public List<? extends MemberAccessor> memberAccessors() {
                return this.memberAccessors;
            }

            public Expression(Expression expression, List<? extends MemberAccessor> memberAccessors) {
                List<? extends MemberAccessor> memberAccessors2 = List.copyOf(memberAccessors);
                this.expression = expression;
                this.memberAccessors = memberAccessors2;
            }
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$MemberAccessor.class */
    public interface MemberAccessor extends Lua {

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$MemberAccessor$Named.class */
        public static final class Named extends Record implements MemberAccessor {
            private final String name;

            public Named(String name) {
                this.name = name;
            }

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Named.class), Named.class, "name", "FIELD:Lcom/faforever/neroxis/lua/Lua$MemberAccessor$Named;->name:Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Named.class), Named.class, "name", "FIELD:Lcom/faforever/neroxis/lua/Lua$MemberAccessor$Named;->name:Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Named.class, Object.class), Named.class, "name", "FIELD:Lcom/faforever/neroxis/lua/Lua$MemberAccessor$Named;->name:Ljava/lang/String;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public String name() {
                return this.name;
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$MemberAccessor$Expression.class */
        public static final class Expression extends Record implements MemberAccessor {
            private final Expression expression;

            public Expression(Expression expression) {
                this.expression = expression;
            }

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Expression.class), Expression.class, "expression", "FIELD:Lcom/faforever/neroxis/lua/Lua$MemberAccessor$Expression;->expression:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Expression.class), Expression.class, "expression", "FIELD:Lcom/faforever/neroxis/lua/Lua$MemberAccessor$Expression;->expression:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Expression.class, Object.class), Expression.class, "expression", "FIELD:Lcom/faforever/neroxis/lua/Lua$MemberAccessor$Expression;->expression:Lcom/faforever/neroxis/lua/Lua$Expression;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public Expression expression() {
                return this.expression;
            }
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$Arg.class */
    public interface Arg extends Lua {

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$Arg$Var.class */
        public static final class Var extends Record implements Arg {
            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Var.class), Var.class, "").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Var.class), Var.class, "").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Var.class, Object.class), Var.class, "").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }
        }

        /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$Arg$Named.class */
        public static final class Named extends Record implements Arg {
            private final String name;

            public Named(String name) {
                this.name = name;
            }

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Named.class), Named.class, "name", "FIELD:Lcom/faforever/neroxis/lua/Lua$Arg$Named;->name:Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Named.class), Named.class, "name", "FIELD:Lcom/faforever/neroxis/lua/Lua$Arg$Named;->name:Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Named.class, Object.class), Named.class, "name", "FIELD:Lcom/faforever/neroxis/lua/Lua$Arg$Named;->name:Ljava/lang/String;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public String name() {
                return this.name;
            }
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/Lua$FunctionBody.class */
    public static final class FunctionBody extends Record implements Lua {
        private final List<? extends Arg> arguments;
        private final Block body;

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, FunctionBody.class), FunctionBody.class, "arguments;body", "FIELD:Lcom/faforever/neroxis/lua/Lua$FunctionBody;->arguments:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/lua/Lua$FunctionBody;->body:Lcom/faforever/neroxis/lua/Lua$Block;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, FunctionBody.class), FunctionBody.class, "arguments;body", "FIELD:Lcom/faforever/neroxis/lua/Lua$FunctionBody;->arguments:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/lua/Lua$FunctionBody;->body:Lcom/faforever/neroxis/lua/Lua$Block;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, FunctionBody.class, Object.class), FunctionBody.class, "arguments;body", "FIELD:Lcom/faforever/neroxis/lua/Lua$FunctionBody;->arguments:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/lua/Lua$FunctionBody;->body:Lcom/faforever/neroxis/lua/Lua$Block;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public List<? extends Arg> arguments() {
            return this.arguments;
        }

        public Block body() {
            return this.body;
        }

        public FunctionBody(List<? extends Arg> arguments, Block body) {
            this.arguments = List.copyOf(arguments);
            this.body = body;
        }
    }
}
