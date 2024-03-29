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

import cachemesh.common.err.BadValueException;
import cachemesh.core.cache.bean.Value.Status;
import cachemesh.grpc.cache.ValueStatus;
import io.grpc.stub.StreamObserver;

import javax.annotation.Nonnull;
import static com.google.common.base.Preconditions.*;

public class GrpcHelper {

    public static <V> void complete(@Nonnull StreamObserver<V> observer, @Nonnull V response) {
        checkNotNull(observer);
        checkNotNull(response);

        observer.onNext(response);
        observer.onCompleted();
        return;
    }

    @Nonnull
    public static Status convertStatus(@Nonnull ValueStatus status) {
        checkNotNull(status);

        switch (status) {
        case NotFound:
            return Status.NOT_FOUND;
        case Ok:
            return Status.OK;
        case NoChange:
            return Status.NO_CHANGE;
        case Redirect:
            return Status.REDIRECT;
        default:
            throw new BadValueException("unrecognized remote value status: %d", status);
        }
    }

    @Nonnull
    public static ValueStatus convertStatus(@Nonnull Status status) {
        checkNotNull(status);

        switch (status) {
        case NOT_FOUND:
            return ValueStatus.NotFound;
        case OK:
            return ValueStatus.Ok;
        case NO_CHANGE:
            return ValueStatus.NoChange;
        case REDIRECT:
            return ValueStatus.Redirect;
        default:
            throw new BadValueException("unrecognized remote value status: %d", status);
        }
    }

}
