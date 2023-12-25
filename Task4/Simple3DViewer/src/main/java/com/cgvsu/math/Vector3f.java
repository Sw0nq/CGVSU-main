package com.cgvsu.math;

public class Vector3f {
    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3f() {
    }

    public boolean equals(Vector3f other) {
        final float eps = 1e-7f;
        return Math.abs(x - other.x) < eps && Math.abs(y - other.y) < eps && Math.abs(z - other.z) < eps;
    }

    public float x, y, z;

    public void add(Vector3f other) {
        if (other == null) {
            throw new IllegalArgumentException("Vector3f can not be null");
        }

        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
    }

    public void cross(Vector3f v1, Vector3f v2) {
        this.x = v1.y * v2.z - v1.z * v2.y;
        this.y = v1.z * v2.x - v1.x * v2.z;
        this.z = v1.x * v2.y - v1.y * v2.x;
    }

    public void normalize() {
        float length = (float) Math.sqrt(x * x + y * y + z * z);
        if (length != 0.0f) {
            float invLength = 1.0f / length;
            x *= invLength;
            y *= invLength;
            z *= invLength;
        }
    }
}
