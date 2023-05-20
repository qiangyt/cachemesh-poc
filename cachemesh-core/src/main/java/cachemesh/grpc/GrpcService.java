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
import cachemesh.core.cache.store.ValueStatus;
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

        var n = req.getCacheName();
        var k = req.getKey();
        var ver = req.getVersion();

        var debug = LOG.isDebugEnabled();
        if (debug) {
            LOG.debug("getSingle(): cache name={}, key={}, version={}", n, k, ver);
        }

        var r = CacheServiceGetSingleResponse.newBuilder();

        var store = getLocalCacheManager().getBytesStore(n);
        if (store == null) {
            r.setStatus(CacheServiceValueStatus.NotFound);
        } else {
            var v = store.getSingle(k, ver);
            if (v == null) {
                r.setStatus(CacheServiceValueStatus.NotFound);
            } else {
                var st = v.getStatus();
                if (st == ValueStatus.NO_CHANGE) {
                    r.setStatus(CacheServiceValueStatus.NoChange);
                } else if (st == ValueStatus.OK) {
                    r.setStatus(CacheServiceValueStatus.Ok);

                    var vv = v.getValue();
                    r.setValue(ByteString.copyFrom(vv.getData()));
                    r.setVersion(vv.getVersion());
                }
                throw new IllegalStateException("unexpected status: " + st);
            }
        }

        if (debug) {
            LOG.debug("getSingle(): resp={}", r);
        }

        GrpcHelper.complete(respObserver, r.build());
    }

    @Override
    public void putSingle(@Nonnull CacheServicePutSingleRequest req,
            @Nonnull StreamObserver<CacheServicePutSingleResponse> respObserver) {
        checkNotNull(req);
        checkNotNull(respObserver);

        var n = req.getCacheName();
        var k = req.getKey();
        var reqv = req.getValue();
        var bytev = (reqv == null) ? null : reqv.toByteArray();

        var debug = LOG.isDebugEnabled();
        if (debug) {
            LOG.debug("putSingle(): cache name={}, key={}, value={}", n, k, bytev);
        }

        var r = CacheServicePutSingleResponse.newBuilder();

        var store = getLocalCacheManager().resolveBytesStore(n);
        var oldv = store.getSingle(k, 0);
        if (debug) {
            LOG.debug("putSingle(): version={}", ver);
        }

        long version;
        if (oldv == null) {
            version = 1;
        } else {
            version = oldv.getValue()
        }

        var respBuilder = PutSingleResponse.newBuilder();
        respBuilder.setVersion(ver);
        GrpcHelper.complete(respObserver, respBuilder.build());
    }

}
