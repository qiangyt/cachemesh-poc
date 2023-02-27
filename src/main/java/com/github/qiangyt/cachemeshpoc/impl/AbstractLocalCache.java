package com.github.qiangyt.cachemeshpoc.impl;

import com.github.qiangyt.cachemeshpoc.local.LocalCache;

@lombok.Getter
public abstract class AbstractLocalCache<V> implements LocalCache<V> {

	private final String name;

	private final Class<V> valueClass;

	protected AbstractLocalCache(String name, Class<V> valueClass) {
		this.name = name;
		this.valueClass = valueClass;
	}

}
