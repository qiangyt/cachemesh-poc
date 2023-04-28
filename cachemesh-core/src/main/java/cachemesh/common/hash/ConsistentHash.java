package cachemesh.common.hash;

import java.util.List;
import java.util.TreeMap;
import java.util.SortedMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.annotation.Nonnull;
import static com.google.common.base.Preconditions.*;

import cachemesh.common.err.BadValueException;

// originally, it is based on github.com/redis/jedis: redis.clients.jedis.providers.ShardedConnectionProvider
// TODO: another choice is akka.routing.ConsistentHash
public class ConsistentHash<T extends HasKey> {

    @lombok.Getter
    public static class VirtualNode<T extends HasKey> {

        @Nonnull
        private final T realNode;
        private final int index;
        @Nonnull
        private final String key;
        private final long hash;

        public VirtualNode(@Nonnull T realNode, int index, @Nonnull String key, long hash) {
            this.realNode = checkNotNull(realNode);
            this.index = index;
            this.key = key;
            this.hash = hash;
        }

    }

    private static final Logger LOG = LoggerFactory.getLogger(ConsistentHash.class);

    @Nonnull
    private final TreeMap<Long, VirtualNode<T>> ring = new TreeMap<>();

    @Nonnull
    private final SortedMap<String, T> nodes = new TreeMap<>();

    @Nonnull
    private final Hashing algo;

    public ConsistentHash(@Nonnull Hashing algo) {
        this.algo = algo;
    }

    @Nonnull
    public Iterable<T> nodes() {
        return this.nodes.values();
    }

    public int nodeSize() {
        return this.nodes.size();
    }

    public int virtualNodeSize() {
        return this.ring.size();
    }

    public void join(@Nonnull List<T> nodes) {
        checkNotNull(nodes);

        LOG.info("got {} nodes to join", nodes.size());
        nodes.forEach(this::join);
    }

    public void join(@Nonnull T node) {
        checkNotNull(node);

        boolean debug = LOG.isDebugEnabled();

        if (debug) {
            LOG.info("node {} is joining", node);
        }

        String nodeKey = node.getKey();
        if (this.nodes.putIfAbsent(node.getKey(), node) != null) {
            throw new BadValueException("duplicated node with key=%s", nodeKey);
        }

        var rg = this.ring;

        for (int i = 0; i < 160; i++) {
            String virtualNodeKey = nodeKey + i;
            long h = hash(virtualNodeKey);
            VirtualNode<T> virtualNode = new VirtualNode<>(node, i, virtualNodeKey, h);

            if (debug) {
                LOG.debug("node {} virtual nodes {}: key={}, hash={}", node, i, virtualNodeKey, h);
            }
            rg.put(h, virtualNode);
        }
    }

    public long hash(@Nonnull String key) {
        return this.algo.hash(key);
    }

    @Nonnull
    public Hashing algo() {
        return this.algo;
    }

    @Nullable
    public VirtualNode<T> virtualNodeFor(@Nonnull String key) {
        checkNotNull(key);

        long h = hash(key);
        return key != null ? virtualNodeFor(h) : null;
    }

    @Nullable
    VirtualNode<T> virtualNodeFor(long hash) {
        var r = this.ring;
        if (r.isEmpty()) {
            return null;
        }

        var tail = r.tailMap(hash);
        if (tail.isEmpty()) {
            return r.get(r.firstKey());
        }
        return tail.get(tail.firstKey());
    }

    @Nullable
    public T findNode(String key) {
        checkNotNull(key);

        long hash = hash(key);
        var virtualNode = virtualNodeFor(hash);
        return (virtualNode == null) ? null : virtualNode.getRealNode();
    }

}
