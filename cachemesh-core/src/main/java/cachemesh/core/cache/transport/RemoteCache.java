package cachemesh.core.cache.transport;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import cachemesh.common.shutdown.Shutdownable;
import cachemesh.core.cache.bean.RemoteValue;

public interface RemoteCache extends Shutdownable {

    void open(int timeoutSeconds) throws InterruptedException;

    @Nullable
    RemoteValue getSingle(@Nonnull String cacheName, @Nonnull String key, long version);

    void putSingle(@Nonnull String cacheName, @Nonnull String key, @Nullable byte[] bytes);

}
