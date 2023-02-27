package com.github.qiangyt.cachemeshpoc.impl;

import com.github.qiangyt.cachemeshpoc.LocalCache;

public abstract class AbstractCache<V> implements LocalCache<V> {

	private final String name;

	private final Class<V> valueClass;

	protected AbstractCache(String name, Class<V> valueClass) {
		this.name = name;
		this.valueClass = valueClass;
	}

	public String name() {
		return this.name;
	}

	public Class<V> valueClass() {
		return this.valueClass;
	}

}
