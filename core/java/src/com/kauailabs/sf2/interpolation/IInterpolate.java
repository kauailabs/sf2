package com.kauailabs.sf2.interpolation;

public interface IInterpolate<T> {
    public void interpolate(final T to, double time_ratio, T out);
}
