package com.faforever.neroxis.generator;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.lang.runtime.SwitchBootstraps;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.random.RandomGenerator;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/WeightedOptionsWithFallback.class */
public interface WeightedOptionsWithFallback<T> {
    @SafeVarargs
    static <T> WeightedOptionsWithFallback<T> of(T fallback, WeightedOption<T>... options) {
        return of(fallback, List.of((Object[]) options));
    }

    static <T> WeightedOptionsWithFallback<T> of(T fallback, Collection<WeightedOption<T>> options) {
        if (options.isEmpty()) {
            return new Single(fallback);
        }
        return new Multi(fallback, List.copyOf(options));
    }

    default T select(RandomGenerator random) {
        return select(random, obj -> {
            return true;
        });
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    default T select(RandomGenerator randomGenerator, Predicate<? super T> predicate) throws MatchException {
        T tOption;
        Objects.requireNonNull(this);
        try {
            switch ((int) SwitchBootstraps.typeSwitch(MethodHandles.lookup(), "typeSwitch", MethodType.methodType(Integer.TYPE, WeightedOptionsWithFallback.class, Integer.TYPE), Single.class, Multi.class).dynamicInvoker().invoke(this, 0) /* invoke-custom */) {
                case 0:
                    tOption = (T) ((Single) this).option();
                    break;
                case 1:
                    Multi multi = (Multi) this;
                    Object objFallbackOption = multi.fallbackOption();
                    List<WeightedOption<T>> list = multi.options().stream().filter(weightedOption -> {
                        return predicate.test(weightedOption.option());
                    }).toList();
                    if (list.isEmpty()) {
                        tOption = (T) objFallbackOption;
                    } else {
                        double[] array = list.stream().mapToDouble((v0) -> {
                            return v0.weight();
                        }).toArray();
                        double[] dArr = new double[array.length];
                        double d = 0.0d;
                        for (int i = 0; i < array.length; i++) {
                            d += array[i];
                            dArr[i] = d;
                        }
                        int iBinarySearch = Arrays.binarySearch(dArr, randomGenerator.nextDouble(d));
                        if (iBinarySearch < 0) {
                            iBinarySearch = (iBinarySearch + 1) * (-1);
                        }
                        tOption = list.get(iBinarySearch).option();
                    }
                    break;
                default:
                    throw new MatchException((String) null, (Throwable) null);
            }
            return tOption;
        } catch (Throwable th) {
            throw new MatchException(th.toString(), th);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/WeightedOptionsWithFallback$Multi.class */
    public static final class Multi<T> extends Record implements WeightedOptionsWithFallback<T> {
        private final T fallbackOption;
        private final List<WeightedOption<T>> options;

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Multi.class), Multi.class, "fallbackOption;options", "FIELD:Lcom/faforever/neroxis/generator/WeightedOptionsWithFallback$Multi;->fallbackOption:Ljava/lang/Object;", "FIELD:Lcom/faforever/neroxis/generator/WeightedOptionsWithFallback$Multi;->options:Ljava/util/List;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Multi.class), Multi.class, "fallbackOption;options", "FIELD:Lcom/faforever/neroxis/generator/WeightedOptionsWithFallback$Multi;->fallbackOption:Ljava/lang/Object;", "FIELD:Lcom/faforever/neroxis/generator/WeightedOptionsWithFallback$Multi;->options:Ljava/util/List;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Multi.class, Object.class), Multi.class, "fallbackOption;options", "FIELD:Lcom/faforever/neroxis/generator/WeightedOptionsWithFallback$Multi;->fallbackOption:Ljava/lang/Object;", "FIELD:Lcom/faforever/neroxis/generator/WeightedOptionsWithFallback$Multi;->options:Ljava/util/List;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public T fallbackOption() {
            return this.fallbackOption;
        }

        public List<WeightedOption<T>> options() {
            return this.options;
        }

        public Multi(T fallbackOption, List<WeightedOption<T>> options) {
            Objects.requireNonNull(fallbackOption, "Fallback cannot be null");
            if (options.size() < 2) {
                throw new IllegalArgumentException("At least two options must be provided");
            }
            List<WeightedOption<T>> options2 = List.copyOf(options);
            this.fallbackOption = fallbackOption;
            this.options = options2;
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/WeightedOptionsWithFallback$Single.class */
    public static final class Single<T> extends Record implements WeightedOptionsWithFallback<T> {
        private final T option;

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Single.class), Single.class, "option", "FIELD:Lcom/faforever/neroxis/generator/WeightedOptionsWithFallback$Single;->option:Ljava/lang/Object;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Single.class), Single.class, "option", "FIELD:Lcom/faforever/neroxis/generator/WeightedOptionsWithFallback$Single;->option:Ljava/lang/Object;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Single.class, Object.class), Single.class, "option", "FIELD:Lcom/faforever/neroxis/generator/WeightedOptionsWithFallback$Single;->option:Ljava/lang/Object;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public T option() {
            return this.option;
        }

        public Single(T option) {
            Objects.requireNonNull(option, "Option cannot be null");
            this.option = option;
        }
    }
}
