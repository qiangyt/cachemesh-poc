package cachemesh.core.spi;

import cachemesh.core.config.CacheConfig;

import javax.annotation.Nonnull;


public interface CacheProvider {
    
    @Nonnull
    Cache createCache(@Nonnull CacheConfig config);

    @Nonnull
    Cache createDefaultCache(@Nonnull String name);

}
