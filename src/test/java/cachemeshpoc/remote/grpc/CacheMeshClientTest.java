package cachemeshpoc.remote.grpc;

import static org.junit.Assert.assertEquals;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;

@RunWith(JUnit4.class)
public class CacheMeshClientTest {

  @Rule
  public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

  private final CacheMeshGrpc.CacheMeshImplBase serviceImpl =
      mock(CacheMeshGrpc.CacheMeshImplBase.class, delegatesTo(
          new CacheMeshGrpc.CacheMeshImplBase() {
          @Override
          public void resolveSingle(ResolveSingleRequest request, StreamObserver<ResolveSingleResponse> respObserver) {
             respObserver.onNext(ResolveSingleResponse.getDefaultInstance());
             respObserver.onCompleted();
           }
          }));

  private GrpcClient client;

  @Before
  public void setUp() throws Exception {
    String serverName = InProcessServerBuilder.generateName();

    this.grpcCleanup.register(InProcessServerBuilder.forName(serverName).directExecutor().addService(serviceImpl).build().start());

    var channel = this.grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build());

		GrpcConfig serverConfig = new GrpcConfig(34567, "localhost");
    this.client = new GrpcClient(serverConfig, channel);
  }

  @Test
  public void resolveSingle_messageDeliveredToServer() {
    var reqCaptor = ArgumentCaptor.forClass(ResolveSingleRequest.class);

    this.client.resolveSingle("test", "key", 123);

    verify(serviceImpl).resolveSingle(reqCaptor.capture(), ArgumentMatchers.<StreamObserver<ResolveSingleResponse>>any());

		assertEquals("test", reqCaptor.getValue().getCacheName());
		assertEquals("key", reqCaptor.getValue().getKey());
		assertEquals(123, reqCaptor.getValue().getVersion());
  }
}
