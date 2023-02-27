package com.github.qiangyt.cachemeshpoc.local;

@lombok.Data
public class LocalEntry<V> {

	private String key;

	private V value;

	private long version;

}
