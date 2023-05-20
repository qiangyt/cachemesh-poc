/*
 * Copyright © 2023 Yiting Qiang (qiangyt@wxcount.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cachemesh.grpc;

import io.grpc.stub.StreamObserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import lombok.Getter;
import cachemesh.core.cache.local.LocalCacheManager;
import cachemesh.grpc.proto.*;

import javax.annotation.Nonnull;
import static com.google.common.base.Preconditions.*;

@Getter
public class GrpcService extends CacheServiceGrpc.CacheServiceImplBase {

    private static final Logger LOG = LoggerFactory.getLogger(GrpcService.class);

    @Nonnull
    private final LocalCacheManager localCacheManager;

    @Nonnull
    private final GrpcConfig config;

    public GrpcService(@Nonnull GrpcConfig config, @Nonnull LocalCacheManager localCacheManager) {
        checkNotNull(config);
        checkNotNull(localCacheManager);

        this.config = config;
        this.localCacheManager = localCacheManager;
    }

    @Override
    public void getSingle(@Nonnull CacheServiceGetSingleRequest req,
            @Nonnull StreamObserver<CacheServiceGetSingleResponse> respObserver) {
        checkNotNull(req);
        checkNotNull(respObserver);

        var cacheName = req.getCacheName();
        var key = req.getKey();
        var ver = req.getVersion();
        var debug = LOG.isDebugEnabled();

        if (debug) {
            LOG.debug("getSingle(): cache name={}, key={}, version={}", cacheName, key, ver);
        }

        var resp = CacheServiceGetSingleResponse.newBuilder();

        var store = getLocalCacheManager().getBytesStore(cacheName);
        if (store == null) {
            resp.setStatus(CacheServiceValueStatus.NotFound);
        } else {
            var value = store.getSingle(key, ver);
            if (value == null) {
                resp.setStatus(CacheServiceValueStatus.NotFound);
            } else {
                switch (value.getStatus()) {
                case NULL: {
                    resp.setStatus(CacheServiceValueStatus.Null);
                    resp.setVersion(value.getVersion());
                }
                    break;
                case OK: {
                    resp.setStatus(CacheServiceValueStatus.Ok);
                    resp.setValue(ByteString.copyFrom(value.getData()));
                    resp.setVersion(value.getVersion());
                }
                    break;
                case NO_CHANGE: {
                    resp.setStatus(CacheServiceValueStatus.NoChange);
                }
                    break;
                default: {
                    throw new IllegalStateException("unexpected status: " + value.getStatus());
                }
            }
        }

        if (debug) {
            LOG.debug("getSingle(): resp={}", resp);
        }

        GrpcHelper.complete(respObserver, resp.build());
    }

    @Override
    public void putSingle(@Nonnull CacheServicePutSingleRequest req,
            @Nonnull StreamObserver<CacheServicePutSingleResponse> respObserver) {
        checkNotNull(req);
        checkNotNull(respObserver);

        var cacheName = req.getCacheName();
        var key = req.getKey();
        var debug = LOG.isDebugEnabled();

        if (debug) {
            LOG.debug("putSingle(): cache name={}, key={}", cacheName, key);
        }


        var cache = getLocalCacheManager().resolve(cacheName);
        var store = cache.getBytesStore();
        
        var reqV = req.getValue();
        var value = (reqV == null) ? null : reqV.toByteArray();

        if (value == null) {

        }
        var resp = CacheServicePutSingleResponse.newBuilder();

        resp.setVersion(CacheServiceValueStatus.NotFound);
        
        

        var ver = getLocalTransport().putSingle(cacheName, key, value);
        if (debug) {
            LOG.debug("putSingle(): version={}", ver);
        }

        var respBuilder = PutSingleResponse.newBuilder();
        respBuilder.setVersion(ver);
        GrpcHelper.complete(respObserver, respBuilder.build());
    }

}
