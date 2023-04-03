/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import cachemesh.common.err.ServiceException;
import cachemesh.core.ResultStatus;
import io.grpc.stub.StreamObserver;

public class GrpcHelper {

    public static <V> void complete(StreamObserver<V> observer, V response) {
        observer.onNext(response);
        observer.onCompleted();
        return;
    }

    public static ResultStatus convertStatus(ValueStatus status) {
        switch (status) {
        case NotFound:
            return ResultStatus.NOT_FOUND;
        case Ok:
            return ResultStatus.OK;
        case NoChange:
            return ResultStatus.NO_CHANGE;
        case Redirect:
            return ResultStatus.REDIRECT;
        default:
            throw new ServiceException("unrecognized remote value status: %d", status);
        }
    }

    public static ValueStatus convertStatus(ResultStatus status) {
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
            throw new ServiceException("unrecognized remote value status: %d", status);
        }
    }

}
