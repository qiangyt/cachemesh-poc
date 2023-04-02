package cachemesh.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * The service definition.
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.53.0)",
    comments = "Source: cachemesh.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class CacheMeshGrpc {

  private CacheMeshGrpc() {}

  public static final String SERVICE_NAME = "cachemesh.CacheMesh";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<cachemesh.grpc.GetSingleRequest,
      cachemesh.grpc.GetSingleResponse> getGetSingleMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetSingle",
      requestType = cachemesh.grpc.GetSingleRequest.class,
      responseType = cachemesh.grpc.GetSingleResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<cachemesh.grpc.GetSingleRequest,
      cachemesh.grpc.GetSingleResponse> getGetSingleMethod() {
    io.grpc.MethodDescriptor<cachemesh.grpc.GetSingleRequest, cachemesh.grpc.GetSingleResponse> getGetSingleMethod;
    if ((getGetSingleMethod = CacheMeshGrpc.getGetSingleMethod) == null) {
      synchronized (CacheMeshGrpc.class) {
        if ((getGetSingleMethod = CacheMeshGrpc.getGetSingleMethod) == null) {
          CacheMeshGrpc.getGetSingleMethod = getGetSingleMethod =
              io.grpc.MethodDescriptor.<cachemesh.grpc.GetSingleRequest, cachemesh.grpc.GetSingleResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetSingle"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  cachemesh.grpc.GetSingleRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  cachemesh.grpc.GetSingleResponse.getDefaultInstance()))
              .setSchemaDescriptor(new CacheMeshMethodDescriptorSupplier("GetSingle"))
              .build();
        }
      }
    }
    return getGetSingleMethod;
  }

  private static volatile io.grpc.MethodDescriptor<cachemesh.grpc.PutSingleRequest,
      cachemesh.grpc.PutSingleResponse> getPutSingleMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "PutSingle",
      requestType = cachemesh.grpc.PutSingleRequest.class,
      responseType = cachemesh.grpc.PutSingleResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<cachemesh.grpc.PutSingleRequest,
      cachemesh.grpc.PutSingleResponse> getPutSingleMethod() {
    io.grpc.MethodDescriptor<cachemesh.grpc.PutSingleRequest, cachemesh.grpc.PutSingleResponse> getPutSingleMethod;
    if ((getPutSingleMethod = CacheMeshGrpc.getPutSingleMethod) == null) {
      synchronized (CacheMeshGrpc.class) {
        if ((getPutSingleMethod = CacheMeshGrpc.getPutSingleMethod) == null) {
          CacheMeshGrpc.getPutSingleMethod = getPutSingleMethod =
              io.grpc.MethodDescriptor.<cachemesh.grpc.PutSingleRequest, cachemesh.grpc.PutSingleResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "PutSingle"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  cachemesh.grpc.PutSingleRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  cachemesh.grpc.PutSingleResponse.getDefaultInstance()))
              .setSchemaDescriptor(new CacheMeshMethodDescriptorSupplier("PutSingle"))
              .build();
        }
      }
    }
    return getPutSingleMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static CacheMeshStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CacheMeshStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CacheMeshStub>() {
        @java.lang.Override
        public CacheMeshStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CacheMeshStub(channel, callOptions);
        }
      };
    return CacheMeshStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static CacheMeshBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CacheMeshBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CacheMeshBlockingStub>() {
        @java.lang.Override
        public CacheMeshBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CacheMeshBlockingStub(channel, callOptions);
        }
      };
    return CacheMeshBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static CacheMeshFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CacheMeshFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CacheMeshFutureStub>() {
        @java.lang.Override
        public CacheMeshFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CacheMeshFutureStub(channel, callOptions);
        }
      };
    return CacheMeshFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * The service definition.
   * </pre>
   */
  public static abstract class CacheMeshImplBase implements io.grpc.BindableService {

    /**
     */
    public void getSingle(cachemesh.grpc.GetSingleRequest request,
        io.grpc.stub.StreamObserver<cachemesh.grpc.GetSingleResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetSingleMethod(), responseObserver);
    }

    /**
     */
    public void putSingle(cachemesh.grpc.PutSingleRequest request,
        io.grpc.stub.StreamObserver<cachemesh.grpc.PutSingleResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getPutSingleMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getGetSingleMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                cachemesh.grpc.GetSingleRequest,
                cachemesh.grpc.GetSingleResponse>(
                  this, METHODID_GET_SINGLE)))
          .addMethod(
            getPutSingleMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                cachemesh.grpc.PutSingleRequest,
                cachemesh.grpc.PutSingleResponse>(
                  this, METHODID_PUT_SINGLE)))
          .build();
    }
  }

  /**
   * <pre>
   * The service definition.
   * </pre>
   */
  public static final class CacheMeshStub extends io.grpc.stub.AbstractAsyncStub<CacheMeshStub> {
    private CacheMeshStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CacheMeshStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CacheMeshStub(channel, callOptions);
    }

    /**
     */
    public void getSingle(cachemesh.grpc.GetSingleRequest request,
        io.grpc.stub.StreamObserver<cachemesh.grpc.GetSingleResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetSingleMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void putSingle(cachemesh.grpc.PutSingleRequest request,
        io.grpc.stub.StreamObserver<cachemesh.grpc.PutSingleResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getPutSingleMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * <pre>
   * The service definition.
   * </pre>
   */
  public static final class CacheMeshBlockingStub extends io.grpc.stub.AbstractBlockingStub<CacheMeshBlockingStub> {
    private CacheMeshBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CacheMeshBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CacheMeshBlockingStub(channel, callOptions);
    }

    /**
     */
    public cachemesh.grpc.GetSingleResponse getSingle(cachemesh.grpc.GetSingleRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetSingleMethod(), getCallOptions(), request);
    }

    /**
     */
    public cachemesh.grpc.PutSingleResponse putSingle(cachemesh.grpc.PutSingleRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getPutSingleMethod(), getCallOptions(), request);
    }
  }

  /**
   * <pre>
   * The service definition.
   * </pre>
   */
  public static final class CacheMeshFutureStub extends io.grpc.stub.AbstractFutureStub<CacheMeshFutureStub> {
    private CacheMeshFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CacheMeshFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CacheMeshFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<cachemesh.grpc.GetSingleResponse> getSingle(
        cachemesh.grpc.GetSingleRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetSingleMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<cachemesh.grpc.PutSingleResponse> putSingle(
        cachemesh.grpc.PutSingleRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getPutSingleMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_SINGLE = 0;
  private static final int METHODID_PUT_SINGLE = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final CacheMeshImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(CacheMeshImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_SINGLE:
          serviceImpl.getSingle((cachemesh.grpc.GetSingleRequest) request,
              (io.grpc.stub.StreamObserver<cachemesh.grpc.GetSingleResponse>) responseObserver);
          break;
        case METHODID_PUT_SINGLE:
          serviceImpl.putSingle((cachemesh.grpc.PutSingleRequest) request,
              (io.grpc.stub.StreamObserver<cachemesh.grpc.PutSingleResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class CacheMeshBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    CacheMeshBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return cachemesh.grpc.CacheMeshProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("CacheMesh");
    }
  }

  private static final class CacheMeshFileDescriptorSupplier
      extends CacheMeshBaseDescriptorSupplier {
    CacheMeshFileDescriptorSupplier() {}
  }

  private static final class CacheMeshMethodDescriptorSupplier
      extends CacheMeshBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    CacheMeshMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (CacheMeshGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new CacheMeshFileDescriptorSupplier())
              .addMethod(getGetSingleMethod())
              .addMethod(getPutSingleMethod())
              .build();
        }
      }
    }
    return result;
  }
}
