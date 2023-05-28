package cachemesh.core.spi;

import cachemesh.core.config.CacheConfig;

import javax.annotation.Nonnull;


public interface CacheProvider {
    
    @Nonnull
    Cache<Object> createObjectCache(@Nonnull CacheConfig config);

    @Nonnull
    Cache<byte[]> createBytesCache(@Nonnull CacheConfig config);

}
