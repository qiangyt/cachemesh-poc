package cachemesh.common.hash;

import java.util.List;
import java.util.TreeMap;
import java.util.SortedMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cachemesh.common.HasName;
import cachemesh.common.err.InternalException;

// originally, it is based on github.com/redis/jedis: redis.clients.jedis.providers.ShardedConnectionProvider
// TODO: another choice is akka.routing.ConsistentHash
public class ConsistentHash<T extends ConsistentHash.Node> {

    public static interface Node extends HasName {
        String getKey();

        @Override
        default String getName() {
            return getKey();
        }
    }

    @lombok.Getter
    public static class VirtualNode<T extends Node> {

        private final T realNode;
        private final int index;
        private final String key;
        private final long hash;

        public VirtualNode(T realNode, int index, String key, long hash) {
            this.realNode = realNode;
            this.index = index;
            this.key = key;
            this.hash = hash;
        }

    }

    private static final Logger LOG = LoggerFactory.getLogger(ConsistentHash.class);

    private final TreeMap<Long, VirtualNode<T>> ring = new TreeMap<>();

    private final SortedMap<String, T> nodes = new TreeMap<>();

    private final Hashing algo;

    public ConsistentHash(Hashing algo) {
        this.algo = algo;
    }

    public Iterable<T> nodes() {
        return this.nodes.values();
    }

    public void join(List<T> nodes) {
        LOG.info("got {} nodes to join", nodes.size());
        nodes.forEach(this::join);
    }

    public void join(T node) {
        boolean debug = LOG.isDebugEnabled();

        if (debug) {
            LOG.info("node {} is joining", node);
        }

        String nodeKey = node.getKey();
        if (this.nodes.putIfAbsent(node.getKey(), node) != null) {
            throw new InternalException("duplicated node with key=%s", nodeKey);
        }

        var rg = this.ring;

        for (int i = 0; i < 160; i++) {
            String virtualNodeKey = String.format("%s-%d", nodeKey, i);
            long h = hash(virtualNodeKey);
            VirtualNode<T> virtualNode = new VirtualNode<>(node, i, virtualNodeKey, h);

            if (debug) {
                LOG.debug("node {} virtual nodes {}: key={}, hash={}", node, i, virtualNodeKey, h);
            }
            rg.put(h, virtualNode);
        }
    }

    public long hash(String key) {
        return this.algo.hash(key);
    }

    public Hashing algo() {
        return this.algo;
    }

    public VirtualNode<T> virtualNodeFor(String key) {
        long h = hash(key);
        return key != null ? virtualNodeFor(h) : null;
    }

    public VirtualNode<T> virtualNodeFor(long hash) {
        var r = this.ring;
        var tail = r.tailMap(hash);
        if (tail.isEmpty()) {
            return r.get(r.firstKey());
        }
        return tail.get(tail.firstKey());
    }

    public T findNode(String key) {
        long hash = hash(key);
        var virtualNode = virtualNodeFor(hash);
        return virtualNode.getRealNode();
    }

}
