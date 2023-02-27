package com.github.qiangyt.cachemeshpoc.remote;

@lombok.Builder
@lombok.Data
public class GetSingleResult {

	private final RemoteValueStatus status;

	private final  byte[] value;

	private final long version;

}
