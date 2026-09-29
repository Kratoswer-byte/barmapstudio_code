package com.faforever.neroxis.util;

import java.util.Arrays;
import java.util.concurrent.Callable;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/DebugUtil.class */
public class DebugUtil {
    public static boolean VERBOSE = false;
    public static boolean DEBUG = false;

    public static String getStackTraceMethodInPackage(String packageName, String... excludedMethodNames) {
        return (String) StackWalker.getInstance().walk(stackFrameStream -> {
            return (String) stackFrameStream.filter(stackTraceElement -> {
                return stackTraceElement.getClassName().startsWith(packageName);
            }).filter(stackTraceElement2 -> {
                return Arrays.stream(excludedMethodNames).noneMatch(excludedMethod -> {
                    return stackTraceElement2.getMethodName().contains(excludedMethod);
                });
            }).findFirst().map((v0) -> {
                return v0.getMethodName();
            }).orElse("not found");
        });
    }

    public static String getLastStackTraceMethodInPackage(String packageName) {
        return (String) StackWalker.getInstance().walk(stackFrameStream -> {
            return (String) stackFrameStream.filter(stackFrame -> {
                return stackFrame.getClassName().startsWith(packageName);
            }).reduce((stackFrame2, stackFrame3) -> {
                return stackFrame3;
            }).map((v0) -> {
                return v0.getMethodName();
            }).orElse("not found");
        });
    }

    public static void timedRun(Runnable runnable) {
        String packageName = getStackTraceParentClass();
        timedRun(packageName.substring(0, packageName.lastIndexOf(".")), (String) null, runnable);
    }

    public static void timedRun(String description, Runnable runnable) {
        String className = getStackTraceParentClass();
        timedRun(className.substring(0, className.lastIndexOf(".")), description, runnable);
    }

    public static <T> T timedRun(Callable<T> callable) {
        String stackTraceParentClass = getStackTraceParentClass();
        return (T) timedRun(stackTraceParentClass.substring(0, stackTraceParentClass.lastIndexOf(".")), (String) null, callable);
    }

    public static <T> T timedRun(String str, Callable<T> callable) {
        String stackTraceParentClass = getStackTraceParentClass();
        return (T) timedRun(stackTraceParentClass.substring(0, stackTraceParentClass.lastIndexOf(".")), str, callable);
    }

    private static String getStackTraceParentClass() {
        return (String) StackWalker.getInstance().walk(stackFrameStream -> {
            return (String) stackFrameStream.skip(3L).findFirst().map((v0) -> {
                return v0.getClassName();
            }).orElse("No Parent.");
        });
    }

    public static void timedRun(String packageName, String description, Runnable runnable) {
        long sTime = System.nanoTime();
        if (VERBOSE && DEBUG) {
            System.out.printf("Started %s: %s\n", description, getStackTraceLineInPackage(packageName, new String[0]));
        }
        runnable.run();
        if (DEBUG) {
            System.out.printf("Done %s: %4.2f ms, %s\n", description, Double.valueOf((System.nanoTime() - sTime) / 1000000.0d), getStackTraceLineInPackage(packageName, new String[0]));
        }
    }

    public static <T> T timedRun(String packageName, String description, Callable<T> callable) {
        long sTime = System.nanoTime();
        if (VERBOSE && DEBUG) {
            System.out.printf("Started %s: %s\n", description, getStackTraceLineInPackage(packageName, new String[0]));
        }
        try {
            T result = callable.call();
            if (DEBUG) {
                System.out.printf("Done %s: %4.2f ms, %s\n", description, Double.valueOf((System.nanoTime() - sTime) / 1000000.0d), getStackTraceLineInPackage(packageName, new String[0]));
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getLastStackTraceLineAfterPackage(String packageName) {
        return (String) StackWalker.getInstance().walk(stackFrameStream -> {
            return (String) stackFrameStream.reduce((stackFrame1, stackFrame2) -> {
                if (stackFrame1.getClassName().startsWith(packageName) || stackFrame2.getClassName().startsWith(packageName)) {
                    return stackFrame2;
                }
                return stackFrame1;
            }).map(stackFrame -> {
                return stackFrame.getFileName() + ":" + stackFrame.getLineNumber();
            }).orElse("not found");
        });
    }

    public static String getStackTraceLineInPackage(String packageName, String... excludedMethodNames) {
        return (String) StackWalker.getInstance().walk(stackFrameStream -> {
            return (String) stackFrameStream.filter(stackTraceElement -> {
                return stackTraceElement.getClassName().startsWith(packageName);
            }).filter(stackTraceElement2 -> {
                return Arrays.stream(excludedMethodNames).noneMatch(excludedMethod -> {
                    return stackTraceElement2.getMethodName().contains(excludedMethod);
                });
            }).findFirst().map(stackFrame -> {
                return stackFrame.getFileName() + ":" + stackFrame.getLineNumber();
            }).orElse("not found");
        });
    }
}
