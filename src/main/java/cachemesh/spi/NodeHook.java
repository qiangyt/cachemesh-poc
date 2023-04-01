package cachemesh.spi;

import cachemesh.core.MeshNode;

public interface NodeHook {

	@FunctionalInterface
	public static interface Func {
		void apply(MeshNode node);
	}

	default void beforeNodeStart(MeshNode node, int timeoutSeconds) throws InterruptedException {
	}

	default void afterNodeStart(MeshNode node, int timeoutSeconds) throws InterruptedException {
	}

	default void beforeNodeStop(MeshNode node, int timeoutSeconds) throws InterruptedException {
	}

	default void afterNodeStop(MeshNode node, int timeoutSeconds) throws InterruptedException {
	}

}
