package com.faforever.neroxis.util;

import com.faforever.neroxis.mask.Mask;
import com.faforever.neroxis.visualization.VisualDebugger;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.SequencedMap;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Generated;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/Pipeline.class */
public class Pipeline {
    private static final ScopedValue<Pipeline> PIPELINE = ScopedValue.newInstance();
    private static final ThreadGroup THREAD_GROUP = new ThreadGroup("Pipeline");
    private static final ExecutorService PIPELINE_EXECUTOR_SERVICE = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), Thread.ofPlatform().daemon().group(THREAD_GROUP).name("pipeline-worker-", 0).factory());
    private final List<Entry> entries = new ArrayList();
    private final CompletableFuture<?> started = new CompletableFuture<>();
    private boolean debug;
    private boolean visualize;

    @Generated
    public void setDebug(final boolean debug) {
        this.debug = debug;
    }

    @Generated
    public boolean isDebug() {
        return this.debug;
    }

    @Generated
    public boolean isVisualize() {
        return this.visualize;
    }

    @Generated
    public void setVisualize(final boolean visualize) {
        this.visualize = visualize;
    }

    private Pipeline() {
    }

    public static List<Entry> run(Runnable setup) {
        return run(pipeline -> {
        }, setup);
    }

    public static List<Entry> run(Consumer<Pipeline> pipelineConfigurator, Runnable setup) {
        Pipeline pipeline = new Pipeline();
        pipelineConfigurator.accept(pipeline);
        ScopedValue.where(PIPELINE, pipeline).run(setup);
        pipeline.run();
        return List.copyOf(pipeline.entries);
    }

    public static void add(Mask<?, ?> executingMask, List<Mask<?, ?>> maskDependencies, Consumer<List<Mask<?, ?>>> function) {
        Pipeline thisPipeline = (Pipeline) PIPELINE.orElseThrow(() -> {
            return new IllegalStateException("Pipeline not bound");
        });
        thisPipeline.addInternal(executingMask, maskDependencies, function);
    }

    public static boolean isAccepting() {
        return PIPELINE.isBound();
    }

    private void addInternal(Mask<?, ?> executingMask, List<Mask<?, ?>> maskDependencies, Consumer<List<Mask<?, ?>>> function) {
        int index = this.entries.size();
        if (this.started.isDone()) {
            throw new UnsupportedOperationException("Mask added after pipeline started");
        }
        String callingMethod = null;
        String callingLine = null;
        if (isDebug() || isVisualize()) {
            callingMethod = DebugUtil.getLastStackTraceMethodInPackage("com.faforever.neroxis.mask");
            callingLine = DebugUtil.getLastStackTraceLineAfterPackage("com.faforever.neroxis.mask");
        }
        SequencedMap<Mask<?, ?>, Optional<Entry>> entryDependencies = getDependencyMap(maskDependencies, executingMask);
        String finalCallingLine = callingLine;
        String finalCallingMethod = callingMethod;
        CompletableFuture<Void> newFuture = getDependencyFuture(entryDependencies).thenAcceptAsync(dependencies -> {
            long startTime = System.currentTimeMillis();
            boolean visualDebug = executingMask.isVisualDebug();
            executingMask.setVisualDebug(false);
            function.accept(dependencies);
            long functionTime = System.currentTimeMillis() - startTime;
            long startTime2 = System.currentTimeMillis();
            long hashTime = System.currentTimeMillis() - startTime2;
            if (isDebug()) {
                System.out.printf("Entry Done: function time %4d ms; hash time %4d ms; %s(%d); %s  -> %s\n", Long.valueOf(functionTime), Long.valueOf(hashTime), executingMask.getName(), Integer.valueOf(index), finalCallingLine, finalCallingMethod);
            }
            executingMask.setVisualDebug(visualDebug);
            if ((isDebug() && visualDebug) || (isVisualize() && !executingMask.isMock())) {
                VisualDebugger.visualizeMask(executingMask, finalCallingMethod, finalCallingLine, null);
            }
        }, (Executor) PIPELINE_EXECUTOR_SERVICE);
        Entry entry = new Entry(index, executingMask, entryDependencies.values().stream().flatMap((v0) -> {
            return v0.stream();
        }).toList(), newFuture, callingMethod, callingLine);
        this.entries.add(entry);
    }

    private SequencedMap<Mask<?, ?>, Optional<Entry>> getDependencyMap(List<Mask<?, ?>> requiredMasks, Mask<?, ?> executingMask) {
        List<Mask<?, ?>> requiredMasks2 = new ArrayList<>(requiredMasks);
        if (!requiredMasks2.contains(executingMask)) {
            requiredMasks2.add(executingMask);
        }
        return getDependencyMap(requiredMasks2);
    }

    private SequencedMap<Mask<?, ?>, Optional<Entry>> getDependencyMap(List<Mask<?, ?>> requiredMasks) {
        return (SequencedMap) requiredMasks.stream().collect(Collectors.toMap(Function.identity(), this::getMostRecentEntryForMask, (optional, optional2) -> {
            throw new IllegalStateException("Multiple entries for the same map");
        }, LinkedHashMap::new));
    }

    private CompletableFuture<List<Mask<?, ?>>> getDependencyFuture(SequencedMap<Mask<?, ?>, Optional<Entry>> dependencyMap) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        futures.add(this.started);
        Stream map = dependencyMap.values().stream().flatMap((v0) -> {
            return v0.stream();
        }).map(entry -> {
            return entry.future;
        });
        Objects.requireNonNull(futures);
        map.forEach((v1) -> {
            r1.add(v1);
        });
        return CompletableFuture.allOf((CompletableFuture[]) futures.toArray(x$0 -> {
            return new CompletableFuture[x$0];
        })).thenApplyAsync(r4 -> {
            return (List) dependencyMap.entrySet().stream().map(entry2 -> {
                return (Mask) ((Optional) entry2.getValue()).map((v0) -> {
                    return v0.getResult();
                }).orElse((Mask) entry2.getKey());
            }).collect(Collectors.toList());
        }, (Executor) PIPELINE_EXECUTOR_SERVICE);
    }

    private Optional<Entry> getMostRecentEntryForMask(Mask<?, ?> mask) {
        return this.entries.reversed().stream().filter(entry -> {
            return mask.equals(entry.executingMask);
        }).findFirst();
    }

    private void run() {
        System.out.println("Starting pipeline");
        if (isDebug()) {
            this.entries.forEach(entry -> {
                System.out.printf("Pipeline entry: %s;\tdependencies:[%s];\tdependants:[%s];\texecuteMask %s;\tLine: %s;\t Method: %s\n", entry, entry.dependencies.stream().map((v0) -> {
                    return v0.toString();
                }).collect(Collectors.joining(", ")), entry.dependants.stream().map((v0) -> {
                    return v0.toString();
                }).collect(Collectors.joining(", ")), entry.executingMask.getName(), entry.line, entry.methodName);
            });
        }
        this.started.complete(null);
        CompletableFuture<?>[] futures = (CompletableFuture[]) this.entries.stream().map(entry2 -> {
            return entry2.future;
        }).toArray(x$0 -> {
            return new CompletableFuture[x$0];
        });
        CompletableFuture.allOf(futures).join();
        System.out.println("Pipeline completed!");
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/Pipeline$Entry.class */
    public static class Entry {
        private final Mask<?, ?> executingMask;
        private final CompletableFuture<Void> future;
        private final int index;
        private final String methodName;
        private final String line;
        private Mask<?, ?> immutableResult;
        private final Set<Entry> dependencies = new HashSet();
        private final Set<Entry> dependants = new HashSet();

        private Entry(int index, Mask<?, ?> executingMask, Collection<Entry> dependencies, CompletableFuture<Void> future, String method, String line) {
            this.index = index;
            this.executingMask = executingMask;
            this.dependencies.addAll(dependencies);
            this.methodName = method;
            this.line = line;
            this.future = future.thenRunAsync(() -> {
                if (!executingMask.isMock()) {
                    this.immutableResult = executingMask.immutableCopy();
                } else {
                    this.immutableResult = executingMask;
                }
            }, (Executor) Pipeline.PIPELINE_EXECUTOR_SERVICE);
            dependencies.forEach(dependency -> {
                dependency.dependants.add(this);
            });
        }

        public Mask<?, ?> getResult() {
            if (this.immutableResult == null) {
                throw new IllegalStateException("Entry not done computing");
            }
            return this.immutableResult;
        }

        public void write(OutputStream out) throws IOException {
            try {
                Mask<?, ?> result = getResult();
                out.write(String.format("%s,\t%s,\t%s,\t%s%n", result.toHash(), this.line, result.getName(), this.methodName).getBytes(StandardCharsets.UTF_8));
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }

        public String toString() {
            return String.format("%s(%d)", this.executingMask.getName(), Integer.valueOf(this.index));
        }
    }
}
