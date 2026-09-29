package com.faforever.neroxis.generator.bar;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.util.Locale;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/IdleTime.class */
final class IdleTime {
    private static MethodHandle lastInput;
    private static MethodHandle tickCount;
    private static boolean unavailable;
    private static volatile long lastSeenInput = System.currentTimeMillis();

    private IdleTime() {
    }

    static void touched() {
        lastSeenInput = System.currentTimeMillis();
    }

    static synchronized long millis() {
        if (!unavailable && System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win")) {
            try {
                if (lastInput == null) {
                    Linker linker = Linker.nativeLinker();
                    lastInput = linker.downcallHandle((MemorySegment) SymbolLookup.libraryLookup("user32", Arena.global()).find("GetLastInputInfo").orElseThrow(), FunctionDescriptor.of(ValueLayout.JAVA_INT, new MemoryLayout[]{ValueLayout.ADDRESS}), new Linker.Option[0]);
                    tickCount = linker.downcallHandle((MemorySegment) SymbolLookup.libraryLookup("kernel32", Arena.global()).find("GetTickCount").orElseThrow(), FunctionDescriptor.of(ValueLayout.JAVA_INT, new MemoryLayout[0]), new Linker.Option[0]);
                }
                Arena arena = Arena.ofConfined();
                try {
                    MemorySegment info = arena.allocate(8L);
                    info.set(ValueLayout.JAVA_INT, 0L, 8);
                    if ((int) lastInput.invoke(info) != 0) {
                        int now = (int) tickCount.invoke();
                        long unsignedLong = Integer.toUnsignedLong(now - info.get(ValueLayout.JAVA_INT, 4L));
                        if (arena != null) {
                            arena.close();
                        }
                        return unsignedLong;
                    }
                    if (arena != null) {
                        arena.close();
                    }
                } catch (Throwable th) {
                    if (arena != null) {
                        try {
                            arena.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                unavailable = true;
            }
            unavailable = true;
        }
        return System.currentTimeMillis() - lastSeenInput;
    }
}
