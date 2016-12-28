package com.kauailabs.sf2.quantity;

public interface ICopy<T> {
	public void copy(T t);
	public T instantiate_copy();
}
