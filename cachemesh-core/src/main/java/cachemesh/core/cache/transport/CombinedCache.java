package cachemesh.core.cache.transport;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import cachemesh.common.err.BadStateException;
import cachemesh.common.misc.Serderializer;
import cachemesh.common.shutdown.Shutdownable;
import cachemesh.core.cache.bean.LocalValue;

import static com.google.common.base.Preconditions.checkNotNull;

import lombok.Getter;

@Getter
public class CombinedCache<T> implements Shutdownable {

    @Nonnull 
    private final LocalCache<T> localCache;

    @Nonnull
    private final String name;
    
    @Nonnull
    private final Class<T> valueClass;
    
    @Nonnull
    private final Serderializer serder;

    @Nonnull 
    private final RemoteCache remoteCache;

    @SuppressWarnings("unchecked")
    public CombinedCache(@Nonnull LocalCache<T> localCache, @Nonnull RemoteCache remoteCache) {
        checkNotNull(localCache);
        checkNotNull(remoteCache);

        this.localCache = localCache;
        this.remoteCache = remoteCache;

        var cfg = localCache.getConfig();
        this.name = cfg.getName();
        this.valueClass = (Class<T>)cfg.getValueClass();
        this.serder = cfg.getSerder().getKind().instance;
    }

    public void open(int timeoutSeconds) throws InterruptedException {
        getLocalCache().open(timeoutSeconds);
        getRemoteCache().open(timeoutSeconds);
    }

    @Override
    public void shutdown(int timeoutSeconds) throws InterruptedException {
        try {
            getRemoteCache().shutdown(timeoutSeconds);
        } finally {
            getLocalCache().shutdown(timeoutSeconds);
        }
    }

    @Nullable 
    public LocalValue<T> getSingle(@Nonnull String key) {
        checkNotNull(key);

        var lcache = getLocalCache();

        var lVal = lcache.getSingle(key);
        long ver = (lVal == null) ? 0 : lVal.getVersion();

        var rVal = getRemoteCache().getSingle(getName(), key, ver);
        if (rVal == null) {
            lcache.invalidateSingle(key);
            return null;
        }

        var status = rVal.getStatus();

        switch (status) {
        case OK: {
            T data = getSerder().deserialize(rVal.getBytes(), getValueClass());
            return lcache.putSingle(key, (k, v) -> new LocalValue<T>(data, rVal.getVersion()));
        }
        case NO_CHANGE:
            return lVal;
        case NULL: {
            return lcache.putSingle(key, (k, v) -> LocalValue.Null(rVal.getVersion()));
        }
        default:
            throw new BadStateException("unexpected status: %s", status);
        }
    }

    public void putSingle(@Nonnull String key, @Nullable T value) {
        checkNotNull(key);

        var bytes = getSerder().serialize(value);
        getRemoteCache().putSingle(getName(), key, bytes);

        getLocalCache().invalidateSingle(key);
        
    }
    
}
