package com.kauailabs.sf2.interpolation;

public interface IInterpolate<T> {
    public T interpolate(final T to, double time_ratio);
}
