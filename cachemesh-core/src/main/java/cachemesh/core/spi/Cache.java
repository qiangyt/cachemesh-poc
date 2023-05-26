package cachemesh.core.spi;

import javax.annotation.Nonnull;
import cachemesh.common.shutdown.Shutdownable;
import cachemesh.core.config.CacheConfig;


public interface Cache extends Shutdownable {
    
    void open(int timeoutSeconds) throws InterruptedException;

    @Nonnull CacheConfig getConfig();

    @Nonnull BytesStore getBytesStore();

    @Nonnull ObjectStore getObjectStore();

}
