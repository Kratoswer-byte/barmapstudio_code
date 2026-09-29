package com.faforever.neroxis.mask;

import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import com.faforever.neroxis.map.Symmetry;
import com.faforever.neroxis.map.SymmetrySettings;
import com.faforever.neroxis.map.SymmetryType;
import com.faforever.neroxis.mask.Mask;
import com.faforever.neroxis.util.DebugUtil;
import com.faforever.neroxis.util.Pipeline;
import com.faforever.neroxis.util.SymmetryUtil;
import com.faforever.neroxis.util.Vertex;
import com.faforever.neroxis.util.functional.BiIntConsumer;
import com.faforever.neroxis.util.functional.BiIntFunction;
import com.faforever.neroxis.util.functional.BiIntObjConsumer;
import com.faforever.neroxis.util.vector.Vector2;
import com.faforever.neroxis.util.vector.Vector3;
import com.faforever.neroxis.visualization.VisualDebugger;
import java.awt.image.BufferedImage;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.lang.runtime.ObjectMethods;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.IntUnaryOperator;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.Generated;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/mask/Mask.class */
public abstract class Mask<T, U extends Mask<T, U>> {
    private static final String MOCK_NAME = "Mock";
    private static final String COPY_NAME = "Copy";
    private final AtomicInteger copyCount;
    protected final RandomGenerator.SplittableGenerator random;
    private final String name;
    protected final SymmetrySettings symmetrySettings;
    private boolean immutable;
    private int plannedSize;
    private boolean visualDebug;
    private boolean visible;
    private boolean mock;
    private String visualName;

    public abstract U blur(int radius);

    public abstract U blur(int radius, BooleanMask other);

    protected abstract U copyFrom(U other);

    protected abstract void initializeMask(int size);

    protected abstract int getImmediateSize();

    public abstract BufferedImage writeToImage(BufferedImage image);

    public abstract BufferedImage toImage();

    public abstract String toHash() throws NoSuchAlgorithmException;

    protected abstract T get(int x, int y);

    protected abstract void set(int x, int y, T value);

    protected abstract U fill(T value);

    protected abstract T getZeroValue();

    protected abstract U setSizeInternal(int newSize);

    protected abstract U rotateInternal(float radians);

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public SymmetrySettings getSymmetrySettings() {
        return this.symmetrySettings;
    }

    @Generated
    public boolean isVisualDebug() {
        return this.visualDebug;
    }

    @Generated
    public void setVisualDebug(final boolean visualDebug) {
        this.visualDebug = visualDebug;
    }

    @Generated
    public void setVisualName(final String visualName) {
        this.visualName = visualName;
    }

    protected Mask(U other, String name) {
        this(other.getSize(), (name == null || !name.endsWith(MOCK_NAME)) ? other.getNextRandomGenerator() : null, other.getSymmetrySettings(), name);
        init(other);
    }

    protected Mask(int size, RandomGenerator.SplittableGenerator random, SymmetrySettings symmetrySettings, String name) {
        this.copyCount = new AtomicInteger();
        this.symmetrySettings = symmetrySettings;
        this.name = name == null ? String.valueOf(hashCode()) : name;
        this.plannedSize = size;
        this.random = random == null ? null : random.split();
        this.visible = true;
        initializeMask(size);
    }

    protected static int getShiftedValue(int val, int offset, int size, boolean wrapEdges) {
        return wrapEdges ? ((val + offset) + size) % size : val + offset;
    }

    protected static Map<Integer, Integer> getSymmetricScalingCoordinateMap(int currentSize, int scaledSize) {
        float scale = currentSize / scaledSize;
        float halfScaledSize = scaledSize / 2.0f;
        Map<Integer, Integer> map = new LinkedHashMap<>();
        for (int i = 0; i < StrictMath.ceil(halfScaledSize); i++) {
            int scaledI = (int) StrictMath.floor(i * scale);
            map.put(Integer.valueOf(i), Integer.valueOf(scaledI));
            map.put(Integer.valueOf((scaledSize - 1) - i), Integer.valueOf((currentSize - 1) - scaledI));
        }
        return map;
    }

    public U init(U u) {
        this.plannedSize = u.getSize();
        return (U) copyFrom(u);
    }

    public boolean isMock() {
        return this.mock || (this.name != null && this.name.endsWith(MOCK_NAME));
    }

    public int getSize() {
        if (Pipeline.isAccepting()) {
            return this.plannedSize;
        }
        return getImmediateSize();
    }

    public U setSize(int i) {
        if (i != getSize()) {
            this.plannedSize = i;
            return (U) enqueue(() -> {
                setSizeInternal(i);
            });
        }
        return this;
    }

    public U rotate(float f) {
        if (f % 360.0f != SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT) {
            return (U) enqueue(() -> {
                rotateInternal(f);
            });
        }
        return this;
    }

    protected RandomGenerator.SplittableGenerator getNextRandomGenerator() {
        if (this.random != null) {
            return this.random.split();
        }
        return null;
    }

    public String getVisualName() {
        if (this.visualName != null) {
            return this.visualName;
        }
        return this.name != null ? this.name : toString();
    }

    public String toString() {
        if (this.name != null) {
            return String.format("Mask(name=%s,size=%d)", this.name, Integer.valueOf(getSize()));
        }
        return getClass().getSimpleName() + "@" + Integer.toHexString(hashCode());
    }

    public T get(Vector3 location) {
        return get(StrictMath.round(location.x()), StrictMath.round(location.z()));
    }

    protected void set(Vector3 location, T value) {
        set(StrictMath.round(location.x()), StrictMath.round(location.z()), value);
    }

    protected void set(Vector2 location, T value) {
        set(StrictMath.round(location.x()), StrictMath.round(location.y()), value);
    }

    public U immutableCopy() {
        checkNotPipelined();
        U u = (U) copy(getName() + "Mock");
        u.makeImmutable();
        return u;
    }

    public U set(BiIntFunction<T> biIntFunction) {
        return (U) apply((x, y) -> {
            set(x, y, biIntFunction.apply(x, y));
        });
    }

    public U apply(BiIntConsumer biIntConsumer) {
        return (U) enqueue(() -> {
            loop(biIntConsumer);
        });
    }

    protected U enqueue(Runnable runnable) {
        return (U) enqueue(list -> {
            runnable.run();
        }, new Mask[0]);
    }

    private void makeImmutable() {
        this.immutable = true;
        this.mock = true;
    }

    public U clear() {
        return (U) fill(getZeroValue());
    }

    protected U enqueue(Consumer<List<Mask<?, ?>>> function, Mask<?, ?>... usedMasks) {
        assertMutable();
        List<Mask<?, ?>> dependencies = List.of((Object[]) usedMasks);
        if (Pipeline.isAccepting()) {
            Pipeline.add(this, dependencies, function);
        } else {
            boolean visibleState = this.visible;
            this.visible = false;
            function.accept(dependencies);
            this.visible = visibleState;
            if (isVisualDebug() && this.visible) {
                String callingMethod = DebugUtil.getLastStackTraceMethodInPackage("com.faforever.neroxis.mask");
                String callingLine = DebugUtil.getLastStackTraceLineAfterPackage("com.faforever.neroxis.mask");
                VisualDebugger.visualizeMask(this, callingMethod, callingLine);
            }
        }
        return this;
    }

    protected void assertMutable() {
        if (this.immutable) {
            throw new IllegalStateException("Mask is a mock and cannot be modified");
        }
    }

    public U init(BooleanMask booleanMask, T t, T t2) {
        this.plannedSize = booleanMask.getSize();
        return (U) enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.getFirst();
            initializeMask(source.getSize());
            set((x, y) -> {
                return source.getPrimitive(x, y) ? t2 : t;
            });
        }, booleanMask);
    }

    protected void loop(BiIntConsumer maskAction) {
        checkNotPipelined();
        int size = getSize();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                maskAction.accept(x, y);
            }
        }
    }

    protected void checkNotPipelined() {
        if (Pipeline.isAccepting()) {
            throw new IllegalStateException("Mask is pipelined and cannot return an immediate result");
        }
    }

    private Vector2 getRotatedPoint(float x, float y, float angle) {
        float halfSize = getSize() / 2.0f;
        double xt = x - halfSize;
        double yt = y - halfSize;
        double tanHalf = StrictMath.tan(((double) angle) / 2.0d);
        double sin = StrictMath.sin(angle);
        double x1 = StrictMath.round(xt - (yt * tanHalf));
        double y1 = StrictMath.round(yt);
        double x2 = StrictMath.round(x1);
        double y2 = StrictMath.round(y1 + (x1 * sin));
        double xr = StrictMath.round(x2 - (y2 * tanHalf));
        double yr = StrictMath.round(y2);
        return new Vector2((float) (xr + ((double) halfSize)), (float) (yr + ((double) halfSize)));
    }

    protected static boolean inBounds(int x, int y, int size) {
        return x >= 0 && x < size && y >= 0 && y < size;
    }

    public boolean inTeam(int x, int y, boolean reverse) {
        return ((x >= 0 && x < getMaxXBound(SymmetryType.TEAM) && y >= getMinYBoundFunction(SymmetryType.TEAM).applyAsInt(x) && y < getMaxYBoundFunction(SymmetryType.TEAM).applyAsInt(x)) ^ reverse) && inBounds(x, y);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    protected int getMaxXBound(SymmetryType symmetryType) throws MatchException {
        Symmetry symmetry = this.symmetrySettings.getSymmetry(symmetryType);
        return SymmetryUtil.getMaxXBound(symmetry, getSize());
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    protected IntUnaryOperator getMinYBoundFunction(SymmetryType symmetryType) throws MatchException {
        Symmetry symmetry = this.symmetrySettings.getSymmetry(symmetryType);
        return SymmetryUtil.getMinYBoundFunction(symmetry, getSize());
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    protected IntUnaryOperator getMaxYBoundFunction(SymmetryType symmetryType) throws MatchException {
        Symmetry symmetry = this.symmetrySettings.getSymmetry(symmetryType);
        return SymmetryUtil.getMaxYBoundFunction(symmetry, getSize());
    }

    public U setToValue(BooleanMask booleanMask, T t) {
        assertCompatibleMask(booleanMask);
        return (U) enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.getFirst();
            apply((x, y) -> {
                if (source.getPrimitive(x, y)) {
                    set(x, y, t);
                }
            });
        }, booleanMask);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public U setToValue(BooleanMask booleanMask, U u) {
        assertCompatibleMask(booleanMask);
        assertCompatibleMask(u);
        return (U) enqueue(dependencies -> {
            BooleanMask placement = (BooleanMask) dependencies.get(0);
            Mask mask = (Mask) dependencies.get(1);
            apply((x, y) -> {
                if (placement.getPrimitive(x, y)) {
                    set(x, y, mask.get(x, y));
                }
            });
        }, booleanMask, u);
    }

    public boolean inBounds(int x, int y) {
        getSize();
        return inBounds(x, y, getSize());
    }

    public boolean onBoundary(Vector2 location) {
        return onBoundary((int) location.x(), (int) location.y());
    }

    public boolean onBoundary(int x, int y) {
        int size = getSize();
        return x == 0 || x == size - 1 || y == 0 || y == size - 1;
    }

    public List<Vector2> getSymmetryPoints(Vector3 point, SymmetryType symmetryType) {
        return getSymmetryPoints(new Vector2(point), symmetryType);
    }

    public List<Vector2> getSymmetryPoints(Vector2 point, SymmetryType symmetryType) {
        return getSymmetryPoints(point.x(), point.y(), symmetryType);
    }

    public List<Vector2> getSymmetryPoints(float x, float y, SymmetryType symmetryType) {
        List<Vector2> symmetryPoints = new ArrayList<>(getSymmetryPointsWithOutOfBounds(x, y, symmetryType));
        symmetryPoints.removeIf(point -> {
            return !inBounds(point);
        });
        return List.copyOf(symmetryPoints);
    }

    public List<Vector2> getSymmetryPointsWithOutOfBounds(Vector3 point, SymmetryType symmetryType) {
        return getSymmetryPointsWithOutOfBounds(new Vector2(point), symmetryType);
    }

    public List<Vector2> getSymmetryPointsWithOutOfBounds(Vector2 point, SymmetryType symmetryType) {
        return getSymmetryPointsWithOutOfBounds(point.x(), point.y(), symmetryType);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    public List<Vector2> getSymmetryPointsWithOutOfBounds(float x, float y, SymmetryType symmetryType) throws MatchException {
        Symmetry symmetry = this.symmetrySettings.getSymmetry(symmetryType);
        Symmetry secondarySymmetry = this.symmetrySettings.getSymmetry(SymmetryType.TEAM);
        return SymmetryUtil.getSymmetryPoints(x, y, getSize(), symmetry, secondarySymmetry);
    }

    public List<Float> getSymmetryRotations(float rot) {
        return getSymmetryRotations(rot, SymmetryType.SPAWN);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    public List<Float> getSymmetryRotations(float rot, SymmetryType symmetryType) throws MatchException {
        Symmetry symmetry = this.symmetrySettings.getSymmetry(symmetryType);
        Symmetry teamSymmetry = this.symmetrySettings.teamSymmetry();
        return SymmetryUtil.getSymmetryRotations(rot, symmetry, teamSymmetry);
    }

    public U resample(int i) {
        int size = getSize();
        if (i != size) {
            this.plannedSize = i;
            return (U) enqueue(() -> {
                if (size < i) {
                    setSize(i);
                    blur(StrictMath.round(((i / size) / 2.0f) - 1.0f));
                } else {
                    blur(StrictMath.round(((size / i) / 2.0f) - 1.0f));
                    setSize(i);
                }
            });
        }
        return this;
    }

    public boolean inTeamNoBounds(int x, int y, boolean reverse) {
        return (x >= 0 && x < getMaxXBound(SymmetryType.TEAM) && y >= getMinYBoundFunction(SymmetryType.TEAM).applyAsInt(x) && y < getMaxYBoundFunction(SymmetryType.TEAM).applyAsInt(x)) ^ reverse;
    }

    public boolean inTeam(Vector3 pos, boolean reverse) {
        return inTeam(new Vector2(pos), reverse);
    }

    public boolean inTeam(Vector2 pos, boolean reverse) {
        return inTeam((int) pos.x(), (int) pos.y(), reverse);
    }

    public boolean inHalfNoBounds(int x, int y, float angle) {
        return inHalfNoBounds(new Vector2(x, y), angle);
    }

    public boolean inTeamNoBounds(Vector3 pos, boolean reverse) {
        return inTeam(new Vector2(pos), reverse);
    }

    public boolean inTeamNoBounds(Vector2 pos, boolean reverse) {
        return inTeam((int) pos.x(), (int) pos.y(), reverse);
    }

    public boolean inHalfNoBounds(Vector2 pos, float angle) {
        float halfSize = getSize() / 2.0f;
        float vectorAngle = ((float) (((((double) (new Vector2(halfSize, halfSize).angleTo(pos) * 180.0f)) / 3.141592653589793d) + 90.0d) + 360.0d)) % 360.0f;
        float adjustedAngle = (angle + 180.0f) % 360.0f;
        if (angle >= 180.0f) {
            return vectorAngle >= angle || vectorAngle < adjustedAngle;
        }
        return vectorAngle >= angle && vectorAngle < adjustedAngle;
    }

    public boolean inHalf(Vector3 pos, float angle) {
        return inHalf(new Vector2(pos), angle);
    }

    public U forceSymmetry(SymmetryType symmetryType, boolean z) {
        if (!z) {
            return (U) applyWithSymmetry(symmetryType, (x, y) -> {
                T value = get(x, y);
                applyAtSymmetryPoints(x, y, symmetryType, (sx, sy) -> {
                    set(sx, sy, value);
                });
            });
        }
        if (this.symmetrySettings.getSymmetry(symmetryType).getNumSymPoints() != 2) {
            throw new IllegalArgumentException("Symmetry has more than two symmetry points");
        }
        return (U) applyWithSymmetry(symmetryType, (x2, y2) -> {
            List<Vector2> symPoints = getSymmetryPoints(x2, y2, symmetryType);
            symPoints.forEach(symPoint -> {
                set(x2, y2, get((int) symPoint.x(), (int) symPoint.y()));
            });
        });
    }

    public T get(Vector2 location) {
        return get(StrictMath.round(location.x()), StrictMath.round(location.y()));
    }

    public boolean inHalfNoBounds(Vector3 pos, float angle) {
        return inHalfNoBounds(new Vector2(pos), angle);
    }

    protected U applyWithSymmetry(SymmetryType symmetryType, BiIntConsumer biIntConsumer) {
        return (U) enqueue(() -> {
            loopInSymmetryRegion(symmetryType, biIntConsumer);
            if (!this.symmetrySettings.getSymmetry(symmetryType).isPerfectSymmetry() && this.symmetrySettings.spawnSymmetry().isPerfectSymmetry()) {
                forceSymmetry(SymmetryType.SPAWN);
            }
        });
    }

    public U forceSymmetry(float f) {
        if (this.symmetrySettings.getSymmetry(SymmetryType.SPAWN) != Symmetry.POINT2) {
            throw new IllegalArgumentException("Spawn Symmetry must equal POINT2");
        }
        return (U) apply((x, y) -> {
            if (inHalf(x, y, f)) {
                T value = get(x, y);
                applyAtSymmetryPoints(x, y, SymmetryType.SPAWN, (sx, sy) -> {
                    set(sx, sy, value);
                });
            }
        });
    }

    public boolean inHalf(int x, int y, float angle) {
        return inHalf(new Vector2(x, y), angle);
    }

    public boolean inHalf(Vector2 pos, float angle) {
        float halfSize = getSize() / 2.0f;
        float vectorAngle = ((float) (((((double) (new Vector2(halfSize, halfSize).angleTo(pos) * 180.0f)) / 3.141592653589793d) + 90.0d) + 360.0d)) % 360.0f;
        float adjustedAngle = (angle + 180.0f) % 360.0f;
        if (angle >= 180.0f) {
            return (vectorAngle >= angle || vectorAngle < adjustedAngle) && inBounds(pos);
        }
        return vectorAngle >= angle && vectorAngle < adjustedAngle && inBounds(pos);
    }

    public boolean inBounds(Vector2 location) {
        return inBounds(StrictMath.round(location.x()), StrictMath.round(location.y()));
    }

    public U forceSymmetry(SymmetryType symmetryType) {
        return (U) forceSymmetry(symmetryType, false);
    }

    public U forceSymmetry() {
        return (U) forceSymmetry(SymmetryType.SPAWN);
    }

    protected U setWithSymmetry(SymmetryType symmetryType, BiIntFunction<T> biIntFunction) {
        return (U) applyWithSymmetry(symmetryType, (x, y) -> {
            Object objApply = biIntFunction.apply(x, y);
            applyAtSymmetryPoints(x, y, symmetryType, (sx, sy) -> {
                set(sx, sy, objApply);
            });
        });
    }

    protected U applyAtSymmetryPointsWithOutOfBounds(Vector2 vector2, SymmetryType symmetryType, BiIntConsumer biIntConsumer) {
        return (U) applyAtSymmetryPointsWithOutOfBounds((int) vector2.x(), (int) vector2.y(), symmetryType, biIntConsumer);
    }

    protected U applyAtSymmetryPoints(Vector2 vector2, SymmetryType symmetryType, BiIntConsumer biIntConsumer) {
        return (U) applyAtSymmetryPoints((int) vector2.x(), (int) vector2.y(), symmetryType, biIntConsumer);
    }

    protected U applyAtSymmetryPoints(int i, int i2, SymmetryType symmetryType, BiIntConsumer biIntConsumer) {
        return (U) enqueue(() -> {
            int size = getSize();
            BiIntConsumer protectedAction = (px, py) -> {
                if (!inBounds(px, py, size)) {
                    return;
                }
                biIntConsumer.accept(px, py);
            };
            Symmetry symmetry = this.symmetrySettings.getSymmetry(symmetryType);
            protectedAction.accept(i, i2);
            switch (symmetry) {
                case POINT2:
                    protectedAction.accept((size - i) - 1, (size - i2) - 1);
                    break;
                case POINT4:
                    protectedAction.accept((size - i) - 1, (size - i2) - 1);
                    protectedAction.accept(i2, (size - i) - 1);
                    protectedAction.accept((size - i2) - 1, i);
                    break;
                case POINT6:
                case POINT8:
                case POINT10:
                case POINT12:
                case POINT14:
                case POINT16:
                    protectedAction.accept((size - i) - 1, (size - i2) - 1);
                    int numSymPoints = symmetry.getNumSymPoints();
                    for (int i3 = 1; i3 < numSymPoints / 2; i3++) {
                        float angle = (float) ((6.283185307179586d * ((double) i3)) / ((double) numSymPoints));
                        Vector2 rotated = getRotatedPoint(i, i2, angle);
                        protectedAction.accept((int) rotated.x(), (int) rotated.y());
                        Vector2 antiRotated = getRotatedPoint(i, i2, (float) (((double) angle) + 3.141592653589793d));
                        protectedAction.accept((int) antiRotated.x(), (int) antiRotated.y());
                    }
                    break;
                case POINT3:
                case POINT5:
                case POINT7:
                case POINT9:
                case POINT11:
                case POINT13:
                case POINT15:
                    int numSymPoints2 = symmetry.getNumSymPoints();
                    for (int i4 = 1; i4 < numSymPoints2; i4++) {
                        Vector2 rotated2 = getRotatedPoint(i, i2, (float) ((6.283185307179586d * ((double) i4)) / ((double) numSymPoints2)));
                        protectedAction.accept((int) rotated2.x(), (int) rotated2.y());
                    }
                    break;
                case X:
                    protectedAction.accept((size - i) - 1, i2);
                    break;
                case Z:
                    protectedAction.accept(i, (size - i2) - 1);
                    break;
                case XZ:
                    protectedAction.accept(i2, i);
                    break;
                case ZX:
                    protectedAction.accept((size - i2) - 1, (size - i) - 1);
                    break;
                case QUAD:
                    if (this.symmetrySettings.teamSymmetry() == Symmetry.Z) {
                        protectedAction.accept(i, (size - i2) - 1);
                        protectedAction.accept((size - i) - 1, i2);
                        protectedAction.accept((size - i) - 1, (size - i2) - 1);
                    } else {
                        protectedAction.accept((size - i) - 1, i2);
                        protectedAction.accept(i, (size - i2) - 1);
                        protectedAction.accept((size - i) - 1, (size - i2) - 1);
                    }
                    break;
                case DIAG:
                    if (this.symmetrySettings.teamSymmetry() == Symmetry.ZX) {
                        protectedAction.accept((size - i2) - 1, (size - i) - 1);
                        protectedAction.accept(i2, i);
                        protectedAction.accept((size - i) - 1, (size - i2) - 1);
                    } else {
                        protectedAction.accept(i2, i);
                        protectedAction.accept((size - i2) - 1, (size - i) - 1);
                        protectedAction.accept((size - i) - 1, (size - i2) - 1);
                    }
                    break;
            }
        });
    }

    protected U applyWithOffset(U u, BiIntObjConsumer<T> biIntObjConsumer, int i, int i2, boolean z, boolean z2) {
        return (U) enqueue(() -> {
            int size = getSize();
            int otherSize = u.getSize();
            int smallerSize = StrictMath.min(size, otherSize);
            StrictMath.max(size, otherSize);
            if (smallerSize == otherSize) {
                if (this.symmetrySettings.spawnSymmetry().isPerfectSymmetry()) {
                    Map<Integer, Integer> coordinateXMap = getShiftedCoordinateMap(i, z, z2, otherSize, size);
                    Map<Integer, Integer> coordinateYMap = getShiftedCoordinateMap(i2, z, z2, otherSize, size);
                    u.apply((x, y) -> {
                        int shiftX = ((Integer) coordinateXMap.get(Integer.valueOf(x))).intValue();
                        int shiftY = ((Integer) coordinateYMap.get(Integer.valueOf(y))).intValue();
                        if (inBounds(shiftX, shiftY, size)) {
                            Object obj = u.get(x, y);
                            applyAtSymmetryPoints(shiftX, shiftY, SymmetryType.SPAWN, (sx, sy) -> {
                                biIntObjConsumer.accept(sx, sy, obj);
                            });
                        }
                    });
                    return;
                }
                applyAtSymmetryPointsWithOutOfBounds(i, i2, SymmetryType.SPAWN, (sx, sy) -> {
                    Map<Integer, Integer> coordinateXMap2 = getShiftedCoordinateMap(sx, z, z2, otherSize, size);
                    Map<Integer, Integer> coordinateYMap2 = getShiftedCoordinateMap(sy, z, z2, otherSize, size);
                    u.apply((x2, y2) -> {
                        int shiftX = ((Integer) coordinateXMap2.get(Integer.valueOf(x2))).intValue();
                        int shiftY = ((Integer) coordinateYMap2.get(Integer.valueOf(y2))).intValue();
                        if (inBounds(shiftX, shiftY, size)) {
                            biIntObjConsumer.accept(shiftX, shiftY, u.get(x2, y2));
                        }
                    });
                });
                return;
            }
            Map<Integer, Integer> coordinateXMap2 = getShiftedCoordinateMap(i, z, z2, size, otherSize);
            Map<Integer, Integer> coordinateYMap2 = getShiftedCoordinateMap(i2, z, z2, size, otherSize);
            apply((x2, y2) -> {
                int shiftX = ((Integer) coordinateXMap2.get(Integer.valueOf(x2))).intValue();
                int shiftY = ((Integer) coordinateYMap2.get(Integer.valueOf(y2))).intValue();
                if (inBounds(shiftX, shiftY, otherSize)) {
                    biIntObjConsumer.accept(x2, y2, u.get(shiftX, shiftY));
                }
            });
        });
    }

    protected U applyAtSymmetryPointsWithOutOfBounds(int i, int i2, SymmetryType symmetryType, BiIntConsumer biIntConsumer) {
        return (U) enqueue(() -> {
            biIntConsumer.accept(i, i2);
            List<Vector2> symPoints = getSymmetryPointsWithOutOfBounds(i, i2, symmetryType);
            symPoints.forEach(point -> {
                biIntConsumer.accept((int) point.x(), (int) point.y());
            });
        });
    }

    protected void populateCoordinateMaps(int xCoordinate, int yCoordinate, boolean center, boolean wrapEdges, int fromSize, int toSize, Map<Integer, Integer> coordinateXMap, Map<Integer, Integer> coordinateYMap) {
        int offsetX;
        int offsetY;
        if (center) {
            offsetX = xCoordinate - (fromSize / 2);
            offsetY = yCoordinate - (fromSize / 2);
        } else {
            offsetX = xCoordinate;
            offsetY = yCoordinate;
        }
        for (int i = 0; i < fromSize; i++) {
            coordinateXMap.put(Integer.valueOf(i), Integer.valueOf(getShiftedValue(i, offsetX, toSize, wrapEdges)));
            coordinateYMap.put(Integer.valueOf(i), Integer.valueOf(getShiftedValue(i, offsetY, toSize, wrapEdges)));
        }
    }

    protected Map<Integer, Integer> getShiftedCoordinateMap(int offset, boolean center, boolean wrapEdges, int fromSize, int toSize) {
        int trueOffset;
        if (center) {
            trueOffset = offset - (fromSize / 2);
        } else {
            trueOffset = offset;
        }
        int i = trueOffset;
        return (Map) IntStream.range(0, fromSize).boxed().collect(Collectors.toMap(i2 -> {
            return i2;
        }, i3 -> {
            return Integer.valueOf(getShiftedValue(i3.intValue(), i, toSize, wrapEdges));
        }));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    protected void loopInSymmetryRegion(SymmetryType symmetryType, BiIntConsumer maskAction) throws MatchException {
        checkNotPipelined();
        int maxX = getMaxXBound(symmetryType);
        IntUnaryOperator minYBoundFunction = getMinYBoundFunction(symmetryType);
        IntUnaryOperator maxYBoundFunction = getMaxYBoundFunction(symmetryType);
        for (int x = 0; x < maxX; x++) {
            int minY = minYBoundFunction.applyAsInt(x);
            int maxY = maxYBoundFunction.applyAsInt(x);
            for (int y = minY; y < maxY; y++) {
                maskAction.accept(x, y);
            }
        }
    }

    protected void assertCompatibleMask(Mask<?, ?> other) {
        int otherSize = other.getSize();
        int size = getSize();
        String name = getName();
        String otherName = other.getName();
        if (otherSize != size) {
            throw new IllegalArgumentException(String.format("Masks not the same size: %s is %d and %s is %d", name, Integer.valueOf(size), otherName, Integer.valueOf(otherSize)));
        }
        SymmetrySettings symmetrySettings = getSymmetrySettings();
        SymmetrySettings otherSymmetrySettings = other.getSymmetrySettings();
        if (symmetrySettings.spawnSymmetry() != Symmetry.NONE && !symmetrySettings.equals(otherSymmetrySettings)) {
            throw new IllegalArgumentException(String.format("Masks not the same symmetry: %s is %s and %s is %s", name, symmetrySettings, otherName, otherSymmetrySettings));
        }
    }

    protected void assertSmallerSize(int size) {
        int actualSize = getSize();
        if (size > actualSize) {
            throw new IllegalArgumentException("Intended mask size is larger than base mask size: Mask is " + actualSize + " and size is " + size);
        }
    }

    protected void assertSize(int size) {
        int actualSize = getSize();
        if (size != actualSize) {
            throw new IllegalArgumentException("Mask size is incorrect: Mask is " + actualSize + " and size is " + size);
        }
    }

    public U copy() {
        return (U) copy(getName() + "Copy" + this.copyCount.getAndIncrement());
    }

    public U getFinalMask() {
        return (U) copy(getName() + "Final");
    }

    private U copy(String maskName) {
        Class<?> clazz = getClass();
        try {
            U u = (U) clazz.getDeclaredConstructor(clazz, String.class).newInstance(this, maskName);
            u.setVisualDebug(isVisualDebug());
            return u;
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public U startVisualDebugger() {
        return (U) startVisualDebugger(this.name == null ? toString() : this.name);
    }

    public U startVisualDebugger(String maskName) {
        this.visualName = maskName;
        this.visualDebug = true;
        this.visible = true;
        show();
        return this;
    }

    public U show() {
        if (!Pipeline.isAccepting() && isVisualDebug() && this.visible) {
            VisualDebugger.visualizeMask(this, "show");
        }
        return this;
    }

    public U fillSides(int i, T t) {
        return (U) fillSides(i, t, SymmetryType.SPAWN);
    }

    public U fillSides(int i, T t, SymmetryType symmetryType) {
        return (U) enqueue(() -> {
            int size = getSize();
            switch (this.symmetrySettings.getSymmetry(symmetryType)) {
                case X:
                    fillRect(0, 0, size, i / 2, t).fillRect(0, size - (i / 2), size, i / 2, t);
                    break;
                case Z:
                    fillRect(0, 0, i / 2, size, t).fillRect(size - (i / 2), 0, size - (i / 2), size, t);
                    break;
                case XZ:
                    fillParallelogram(0, 0, size, (i * 3) / 4, 0, -1, t).fillParallelogram(size - ((i * 3) / 4), size, size, (i * 3) / 4, 0, -1, t);
                    break;
                case ZX:
                    fillParallelogram(size - ((i * 3) / 4), 0, (i * 3) / 4, (i * 3) / 4, 1, 0, t).fillParallelogram(((-i) * 3) / 4, size - ((i * 3) / 4), (i * 3) / 4, (i * 3) / 4, 1, 0, t);
                    break;
            }
            forceSymmetry(symmetryType);
        });
    }

    public U fillCenter(int i, T t) {
        return (U) fillCenter(i, t, SymmetryType.TEAM);
    }

    public U fillCenter(int i, T t, SymmetryType symmetryType) {
        return (U) enqueue(() -> {
            int size = getSize();
            switch (this.symmetrySettings.getSymmetry(symmetryType)) {
                case POINT2:
                case POINT4:
                case POINT6:
                case POINT8:
                case POINT10:
                case POINT12:
                case POINT14:
                case POINT16:
                case POINT3:
                case POINT5:
                case POINT7:
                case POINT9:
                case POINT11:
                case POINT13:
                case POINT15:
                    fillCircle(size / 2.0f, size / 2.0f, (i * 3) / 4.0f, t);
                    break;
                case X:
                    fillRect((size / 2) - (i / 2), 0, i, size, t);
                    break;
                case Z:
                    fillRect(0, (size / 2) - (i / 2), size, i, t);
                    break;
                case XZ:
                    fillDiagonal((i * 3) / 4, false, t);
                    break;
                case ZX:
                    fillDiagonal((i * 3) / 4, true, t);
                    break;
                case QUAD:
                    if (this.symmetrySettings.teamSymmetry() == Symmetry.QUAD) {
                        fillRect((size / 2) - (i / 4), 0, i / 2, size, t);
                        fillRect(0, (size / 2) - (i / 4), size, i / 2, t);
                    } else {
                        fillRect((size / 2) - (i / 8), 0, i / 4, size, t);
                        fillRect(0, (size / 2) - (i / 8), size, i / 4, t);
                        fillCenter(i, t, SymmetryType.TEAM);
                    }
                    break;
                case DIAG:
                    if (this.symmetrySettings.teamSymmetry() == Symmetry.DIAG) {
                        fillDiagonal((i * 3) / 8, false, t);
                        fillDiagonal((i * 3) / 8, true, t);
                    } else {
                        fillDiagonal((i * 3) / 16, false, t);
                        fillDiagonal((i * 3) / 16, true, t);
                        fillCenter(i, t, SymmetryType.TEAM);
                    }
                    break;
            }
            forceSymmetry(SymmetryType.SPAWN);
        });
    }

    public U fillCircle(Vector3 vector3, float f, T t) {
        return (U) fillCircle(vector3.x(), vector3.z(), f, t);
    }

    public U fillCircle(Vector2 vector2, float f, T t) {
        return (U) fillCircle(vector2.x(), vector2.y(), f, t);
    }

    public U fillCircle(float f, float f2, float f3, T t) {
        return (U) enqueue(() -> {
            fillArc(f, f2, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 360.0f, f3, t);
        });
    }

    public U fillArc(float f, float f2, float f3, float f4, float f5, T t) {
        return (U) enqueue(() -> {
            int size = getSize();
            float radius2 = (f5 + 0.5f) * (f5 + 0.5f);
            for (int cx = StrictMath.round(f - f5); cx < StrictMath.round(f + f5 + 1.0f); cx++) {
                for (int cy = StrictMath.round(f2 - f5); cy < StrictMath.round(f2 + f5 + 1.0f); cy++) {
                    float dx = f - cx;
                    float dy = f2 - cy;
                    float angle = ((float) ((StrictMath.atan2(dy, dx) / ((double) 57.29578f)) + 360.0d)) % 360.0f;
                    if (inBounds(cx, cy, size) && (dx * dx) + (dy * dy) <= radius2 && angle >= f3 && angle <= f4) {
                        set(cx, cy, t);
                    }
                }
            }
        });
    }

    public U fillSquare(Vector2 vector2, int i, T t) {
        return (U) fillSquare((int) vector2.x(), (int) vector2.y(), i, t);
    }

    public U fillSquare(int i, int i2, int i3, T t) {
        return (U) enqueue(() -> {
            fillRect(i, i2, i3, i3, t);
        });
    }

    public U fillRect(Vector2 vector2, int i, int i2, T t) {
        return (U) fillRect((int) vector2.x(), (int) vector2.y(), i, i2, t);
    }

    public U fillRect(int i, int i2, int i3, int i4, T t) {
        return (U) enqueue(() -> {
            fillParallelogram(i, i2, i3, i4, 0, 0, t);
        });
    }

    public U fillRectFromPoints(int i, int i2, int i3, int i4, T t) {
        int iMin = StrictMath.min(i, i2);
        int iMax = StrictMath.max(i, i2);
        int iMin2 = StrictMath.min(i3, i4);
        return (U) fillRect(iMin, iMin2, iMax - iMin, StrictMath.max(i3, i4) - iMin2, t);
    }

    public U fillParallelogram(Vector2 vector2, int i, int i2, int i3, int i4, T t) {
        return (U) fillParallelogram((int) vector2.x(), (int) vector2.y(), i, i2, i3, i4, t);
    }

    public U fillParallelogram(int i, int i2, int i3, int i4, int i5, int i6, T t) {
        return (U) enqueue(() -> {
            int size = getSize();
            for (int px = 0; px < i3; px++) {
                for (int py = 0; py < i4; py++) {
                    int calcX = i + px + (py * i5);
                    int calcY = i2 + py + (px * i6);
                    if (inBounds(calcX, calcY, size)) {
                        set(calcX, calcY, t);
                    }
                }
            }
        });
    }

    public U fillDiagonal(int i, boolean z, T t) {
        return (U) enqueue(() -> {
            int x;
            int size = getSize();
            for (int cx = -i; cx < i; cx++) {
                for (int y = 0; y < size; y++) {
                    if (z) {
                        x = size - (cx + y);
                    } else {
                        x = cx + y;
                    }
                    if (x >= 0 && x < size) {
                        set(x, y, t);
                    }
                }
            }
        });
    }

    public U fillEdge(int i, T t) {
        return (U) enqueue(() -> {
            int size = getSize();
            for (int a = 0; a < i; a++) {
                for (int b = 0; b < size - i; b++) {
                    set(a, b, t);
                    set((size - 1) - a, (size - 1) - b, t);
                    set(b, (size - 1) - a, t);
                    set((size - 1) - b, a, t);
                }
            }
        });
    }

    protected U fillCoordinates(Collection<Vector2> coordinates, T value) {
        coordinates.forEach(location -> {
            applyAtSymmetryPoints((int) location.x(), (int) location.y(), SymmetryType.SPAWN, (x, y) -> {
                set(x, y, value);
            });
        });
        return this;
    }

    public U fillQuadrilateral(Vertex vertex, Vertex vertex2, Vertex vertex3, Vertex vertex4, T t) {
        return (U) enqueue(() -> {
            List listOf = List.of(vertex, vertex2, vertex3, vertex4);
            C1Centroid c1Centroid = (C1Centroid) listOf.stream().collect(Collectors.teeing(Collectors.summingInt((v0) -> {
                return v0.x();
            }), Collectors.summingInt((v0) -> {
                return v0.y();
            }), (sx, sy) -> {
                return new C1Centroid(sx.intValue() / 4, sy.intValue() / 4);
            }));
            float fCx = c1Centroid.cx();
            float fCy = c1Centroid.cy();
            List<T> list = listOf.stream().sorted(Comparator.comparingDouble(v -> {
                return StrictMath.atan2(v.y() - fCy, v.x() - fCx);
            })).toList();
            Vertex vertex5 = (Vertex) list.get(0);
            Vertex vertex6 = (Vertex) list.get(1);
            Vertex vertex7 = (Vertex) list.get(2);
            Vertex vertex8 = (Vertex) list.get(3);
            float fCross = cross(vertex8, vertex5, vertex6);
            float fCross2 = cross(vertex5, vertex6, vertex7);
            float fCross3 = cross(vertex6, vertex7, vertex8);
            float fCross4 = cross(vertex7, vertex8, vertex5);
            char c = 65535;
            if (((((fCross > SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT ? 1 : (fCross == SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT ? 0 : -1)) > 0 ? 1 : 0) + ((fCross2 > SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT ? 1 : (fCross2 == SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT ? 0 : -1)) > 0 ? 1 : 0)) + ((fCross3 > SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT ? 1 : (fCross3 == SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT ? 0 : -1)) > 0 ? 1 : 0)) + ((fCross4 > SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT ? 1 : (fCross4 == SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT ? 0 : -1)) > 0 ? 1 : 0) >= 3) {
                if (fCross < SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT) {
                    c = 0;
                } else if (fCross2 < SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT) {
                    c = 1;
                } else if (fCross3 < SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT) {
                    c = 2;
                } else if (fCross4 < SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT) {
                    c = 3;
                }
            } else if (fCross > SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT) {
                c = 0;
            } else if (fCross2 > SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT) {
                c = 1;
            } else if (fCross3 > SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT) {
                c = 2;
            } else if (fCross4 > SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT) {
                c = 3;
            }
            if (c == 65535) {
                fillTriangle(vertex5, vertex6, vertex7, t);
                fillTriangle(vertex5, vertex7, vertex8, t);
                return;
            }
            switch (c) {
                case 0:
                    fillTriangle(vertex5, vertex6, vertex8, t);
                    fillTriangle(vertex6, vertex7, vertex8, t);
                    break;
                case 1:
                    fillTriangle(vertex6, vertex7, vertex5, t);
                    fillTriangle(vertex7, vertex8, vertex5, t);
                    break;
                case 2:
                    fillTriangle(vertex7, vertex8, vertex6, t);
                    fillTriangle(vertex8, vertex5, vertex6, t);
                    break;
                case 3:
                    fillTriangle(vertex8, vertex5, vertex7, t);
                    fillTriangle(vertex5, vertex6, vertex7, t);
                    break;
            }
        });
    }

    /* JADX INFO: renamed from: com.faforever.neroxis.mask.Mask$1Centroid, reason: invalid class name */
    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/mask/Mask$1Centroid.class */
    static final class C1Centroid extends Record {
        private final int cx;
        private final int cy;

        C1Centroid(int cx, int cy) {
            this.cx = cx;
            this.cy = cy;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, C1Centroid.class), C1Centroid.class, "cx;cy", "FIELD:Lcom/faforever/neroxis/mask/Mask$1Centroid;->cx:I", "FIELD:Lcom/faforever/neroxis/mask/Mask$1Centroid;->cy:I").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, C1Centroid.class), C1Centroid.class, "cx;cy", "FIELD:Lcom/faforever/neroxis/mask/Mask$1Centroid;->cx:I", "FIELD:Lcom/faforever/neroxis/mask/Mask$1Centroid;->cy:I").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, C1Centroid.class, Object.class), C1Centroid.class, "cx;cy", "FIELD:Lcom/faforever/neroxis/mask/Mask$1Centroid;->cx:I", "FIELD:Lcom/faforever/neroxis/mask/Mask$1Centroid;->cy:I").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public int cx() {
            return this.cx;
        }

        public int cy() {
            return this.cy;
        }
    }

    private float cross(Vertex a, Vertex b, Vertex c) {
        return ((b.x() - a.x()) * (c.y() - a.y())) - ((b.y() - a.y()) * (c.x() - a.x()));
    }

    public U fillTriangle(Vertex vertex, Vertex vertex2, Vertex vertex3, T t) {
        return (U) enqueue(() -> {
            List<T> list = Stream.of((Object[]) new Vertex[]{vertex, vertex2, vertex3}).sorted(Comparator.comparing((v0) -> {
                return v0.y();
            })).toList();
            if (((Vertex) list.getFirst()).y() == ((Vertex) list.getLast()).y()) {
                drawScanline(list.stream().mapToInt((v0) -> {
                    return v0.x();
                }).min().orElseThrow(), list.stream().mapToInt((v0) -> {
                    return v0.x();
                }).max().orElseThrow(), ((Vertex) list.getFirst()).y(), t);
                return;
            }
            if (((Vertex) list.get(1)).y() == ((Vertex) list.get(2)).y()) {
                fillFlatBottomTriangle((Vertex) list.get(0), (Vertex) list.get(1), (Vertex) list.get(2), t);
                return;
            }
            if (((Vertex) list.get(0)).y() == ((Vertex) list.get(1)).y()) {
                fillFlatTopTriangle((Vertex) list.get(0), (Vertex) list.get(1), (Vertex) list.get(2), t);
                return;
            }
            int iX = ((Vertex) list.get(0)).x() + ((int) (((((Vertex) list.get(1)).y() - ((Vertex) list.get(0)).y()) / (((Vertex) list.get(2)).y() - ((Vertex) list.get(0)).y())) * (((Vertex) list.get(2)).x() - ((Vertex) list.get(0)).x())));
            int iY = ((Vertex) list.get(1)).y();
            fillFlatBottomTriangle((Vertex) list.get(0), (Vertex) list.get(1), new Vertex(iX, iY), t);
            fillFlatTopTriangle((Vertex) list.get(1), new Vertex(iX, iY), (Vertex) list.get(2), t);
        });
    }

    private void fillFlatBottomTriangle(Vertex v1, Vertex v2, Vertex v3, T value) {
        float invSlope1 = (v2.x() - v1.x()) / (v2.y() - v1.y());
        float invSlope2 = (v3.x() - v1.x()) / (v3.y() - v1.y());
        float curx1 = v1.x();
        float curx2 = v1.x();
        for (int y = v1.y(); y <= v2.y(); y++) {
            drawScanline((int) curx1, (int) curx2, y, value);
            curx1 += invSlope1;
            curx2 += invSlope2;
        }
    }

    private void fillFlatTopTriangle(Vertex v1, Vertex v2, Vertex v3, T value) {
        float invSlope1 = (v3.x() - v1.x()) / (v3.y() - v1.y());
        float invSlope2 = (v3.x() - v2.x()) / (v3.y() - v2.y());
        float curx1 = v3.x();
        float curx2 = v3.x();
        for (int y = v3.y(); y >= v1.y(); y--) {
            drawScanline((int) curx1, (int) curx2, y, value);
            curx1 -= invSlope1;
            curx2 -= invSlope2;
        }
    }

    private void drawScanline(int xStart, int xEnd, int y, T value) {
        int size = getSize();
        if (y < 0 || y >= size) {
            return;
        }
        int start = StrictMath.min(xStart, xEnd);
        int end = StrictMath.max(xStart, xEnd);
        if (start < 0) {
            start = 0;
        } else if (start >= size) {
            return;
        }
        if (end >= size) {
            end = size - 1;
        } else if (end < 0) {
            return;
        }
        for (int x = start; x <= end; x++) {
            set(x, y, value);
        }
    }
}
