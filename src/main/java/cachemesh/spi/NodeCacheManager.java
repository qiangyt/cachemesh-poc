package cachemesh.spi;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static net.logstash.logback.argument.StructuredArguments.kv;

import cachemesh.spi.base.NodeByteCache;
import cachemesh.spi.base.NodeGenericCache;


@ThreadSafe
public class NodeCacheManager implements AutoCloseable {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private final Map<String, NodeCache> caches = new ConcurrentHashMap<>();

	private final LocalCacheManager backend;

	public NodeCacheManager(LocalCacheManager backend) {
		this.backend = backend;
	}

	public NodeCache get(String name) {
		return this.caches.get(name);
	}

	public NodeCache resolve(String name) {
		return this.caches.computeIfAbsent(name, k -> {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("node cache not found, so build it: {}", kv("name", name));
			}
			return create(name);
		});
	}

	NodeCache create(String name) {
		var bkCache = this.backend.get(name);
		var bkCacheCfg = bkCache.getConfig();

		var valueClass = bkCacheCfg.getValueClass();
		if (valueClass == byte[].class) {
			return new NodeByteCache((ByteCache)bkCache);
		}
		if (bkCacheCfg.isCacheBytes()) {

		}
		return new NodeGenericCache(bkCache);
	}

	@Override
	public void close() throws Exception {
		//TODO
	}

}
