package cachemesh.core;

import cachemesh.common.hash.Hashing;
import cachemesh.common.hash.MurmurHash;
import lombok.Getter;
import lombok.Builder;

@Getter
@Builder
public class MeshNetworkConfig {

	private final String name;

	private Hashing hashing;

	public MeshNetworkConfig(String name, Hashing hashing) {
		this.name = name;
		this.hashing = hashing;
	}

	public MeshNetworkConfig(String name) {
		this(name, MurmurHash.DEFAULT);
	}

}
