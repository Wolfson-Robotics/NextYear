package org.firstinspires.ftc.teamcode.util;

import androidx.annotation.NonNull;

public class ObjBounds<T> {

    private T lower, upper;

    public ObjBounds(T lower, T upper) {
        this.lower = lower;
        this.upper = upper;
    }
    public ObjBounds(T lower) {
        this(lower, null);
    }
    public ObjBounds() {
        this(null, null);
    }

    public T lower() {
        return this.lower;
    }
    public T upper() {
        return this.upper;
    }

    public void lower(T lower) {
        this.lower = lower;
    }
    public void upper(T upper) {
        this.upper = upper;
    }

    public boolean hasLower() {
        return this.lower != null;
    }
    public boolean hasUpper() {
        return this.upper != null;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ObjBounds)) return false;
        return ((ObjBounds<T>) o).lower().equals(lower) && ((ObjBounds<T>) o).upper().equals(upper);
    }

    @NonNull
    @Override
    public String toString() {
        return "[" + lower + ", " + upper + "]";
    }
}
