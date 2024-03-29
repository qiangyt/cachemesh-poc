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
import cachemesh.core.cache.local.LocalTransport;
import cachemesh.grpc.cache.CacheServiceGrpc;
import cachemesh.grpc.cache.GetSingleRequest;
import cachemesh.grpc.cache.GetSingleResponse;
import cachemesh.grpc.cache.PutSingleRequest;
import cachemesh.grpc.cache.PutSingleResponse;

import javax.annotation.Nonnull;
import static com.google.common.base.Preconditions.*;

@Getter
public class GrpcService extends CacheServiceGrpc.CacheServiceImplBase {

    private static final Logger LOG = LoggerFactory.getLogger(GrpcService.class);

    @Nonnull
    private final LocalTransport localTransport;

    @Nonnull
    private final GrpcConfig config;

    public GrpcService(@Nonnull GrpcConfig config, @Nonnull LocalTransport localTransport) {
        checkNotNull(config);
        checkNotNull(localTransport);

        this.config = config;
        this.localTransport = localTransport;
    }

    @Override
    public void getSingle(@Nonnull GetSingleRequest req, @Nonnull StreamObserver<GetSingleResponse> respObserver) {
        checkNotNull(req);
        checkNotNull(respObserver);

        var cacheName = req.getCacheName();
        var key = req.getKey();
        var ver = req.getVersion();
        var debug = LOG.isDebugEnabled();

        if (debug) {
            LOG.debug("getSingle(): cache name={}, key={}, version={}", cacheName, key, ver);
        }

        var resp = getLocalTransport().getSingle(cacheName, key, ver);
        if (debug) {
            LOG.debug("getSingle(): resp={}", resp);
        }

        var respBuilder = GetSingleResponse.newBuilder();
        respBuilder.setStatus(GrpcHelper.convertStatus(resp.getStatus()));
        respBuilder.setVersion(resp.getVersion());

        var value = resp.getValue();
        if (value != null) {
            respBuilder.setValue(ByteString.copyFrom(value));
        }

        GrpcHelper.complete(respObserver, respBuilder.build());
    }

    @Override
    public void putSingle(@Nonnull PutSingleRequest req, @Nonnull StreamObserver<PutSingleResponse> respObserver) {
        checkNotNull(req);
        checkNotNull(respObserver);

        var cacheName = req.getCacheName();
        var key = req.getKey();
        var debug = LOG.isDebugEnabled();

        if (debug) {
            LOG.debug("putSingle(): cache name={}, key={}", cacheName, key);
        }

        var reqV = req.getValue();
        var value = (reqV == null) ? null : reqV.toByteArray();

        var ver = getLocalTransport().putSingle(cacheName, key, value);
        if (debug) {
            LOG.debug("putSingle(): version={}", ver);
        }

        var respBuilder = PutSingleResponse.newBuilder();
        respBuilder.setVersion(ver);
        GrpcHelper.complete(respObserver, respBuilder.build());
    }

}
