package com.faforever.neroxis.util.jsquish;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/jsquish/Vec.class */
final class Vec {
    private float x;
    private float y;
    private float z;

    Vec() {
    }

    Vec(final float a) {
        this(a, a, a);
    }

    Vec(final float a, final float b, final float c) {
        this.x = a;
        this.y = b;
        this.z = c;
    }

    Vec(final Vec v) {
        this(v.x, v.y, v.z);
    }

    float x() {
        return this.x;
    }

    float y() {
        return this.y;
    }

    float z() {
        return this.z;
    }

    Vec set(final float a) {
        this.x = a;
        this.y = a;
        this.z = a;
        return this;
    }

    Vec set(final float x, final float y, final float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    Vec set(final Vec v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        return this;
    }

    Vec add(final Vec v) {
        this.x += v.x;
        this.y += v.y;
        this.z += v.z;
        return this;
    }

    Vec add(final float x, final float y, final float z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    Vec sub(final Vec v) {
        this.x -= v.x;
        this.y -= v.y;
        this.z -= v.z;
        return this;
    }

    Vec mul(final float s) {
        this.x *= s;
        this.y *= s;
        this.z *= s;
        return this;
    }

    Vec mul(final Vec v) {
        this.x *= v.x;
        this.y *= v.y;
        this.z *= v.z;
        return this;
    }

    Vec div(final float s) {
        float t = 1.0f / s;
        this.x *= t;
        this.y *= t;
        this.z *= t;
        return this;
    }

    float lengthSQ() {
        return dot(this);
    }

    float dot(final Vec v) {
        return (this.x * v.x) + (this.y * v.y) + (this.z * v.z);
    }
}
