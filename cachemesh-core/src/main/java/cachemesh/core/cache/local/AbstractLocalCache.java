package cachemesh.core.cache.local;

import cachemesh.core.config.LocalCacheConfig;

import lombok.Getter;
import cachemesh.common.shutdown.AbstractShutdownable;
import cachemesh.common.shutdown.ShutdownManager;
import cachemesh.core.cache.spi.BytesStore;
import cachemesh.core.cache.spi.LocalCache;
import cachemesh.core.cache.spi.LocalCacheProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import static com.google.common.base.Preconditions.*;

@Getter
public abstract class AbstractLocalCache<T, CONFIG extends LocalCacheConfig> 
extends AbstractShutdownable implements LocalCache<T> {
    
    @Nonnull
    private final LocalCacheProvider provider;
    
    @Nonnull
    private final CONFIG config;

    @Nonnull
    private final BytesStore bytesStore;

    public AbstractLocalCache(@Nonnull LocalCacheProvider provider, @Nonnull CONFIG config, @Nullable ShutdownManager shutdownManager) {
        super(config.getName(), shutdownManager);

        checkNotNull(provider);
        checkNotNull(config);

        this.provider = provider;
        this.config = config;

        this.bytesStore = createBytesStore(config);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    protected BytesStore createBytesStore(@Nonnull CONFIG config) {
        if (config.getValueClass() == byte[].class) {
            return new DirectBytesStore((LocalCache<byte[]>)this);
        }

        var ostore = new ObjectBytesStore(this);
        if (config.isCacheBytes() == false) {
            return ostore;
        }

        var bytesCacheConfig = config.createAnother(config.getName() + "-bytes", byte[].class);
        var bytesCache = (LocalCache<byte[]>)getProvider().createCache(bytesCacheConfig);
        var dstore = new DirectBytesStore(bytesCache);

        return new CombinedBytesStore(ostore, dstore);
    }

    @Override
    public String toString() {
        return getConfig().toString();
    }

    @Override
    public void open(int timeoutSeconds) throws InterruptedException {
        // no nothing by default
    }

}
