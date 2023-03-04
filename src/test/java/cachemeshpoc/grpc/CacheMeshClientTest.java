package cachemeshpoc.grpc;

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
          public void getSingle(GetSingleRequest request, StreamObserver<GetSingleResponse> respObserver) {
             respObserver.onNext(GetSingleResponse.getDefaultInstance());
             respObserver.onCompleted();
           }
          }));

  private GrpcClient client;

  @Before
  public void setUp() throws Exception {
    String serverName = InProcessServerBuilder.generateName();

    this.grpcCleanup.register(InProcessServerBuilder.forName(serverName).directExecutor().addService(serviceImpl).build().start());

    var channel = this.grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build());

		GrpcConfig serverConfig = new GrpcConfig("localhost", 34567);
    this.client = new GrpcClient(serverConfig, channel);
  }

  @Test
  public void getSingle_messageDeliveredToServer() {
    var reqCaptor = ArgumentCaptor.forClass(GetSingleRequest.class);

    this.client.getSingle("test", "key", 123);

    verify(serviceImpl).getSingle(reqCaptor.capture(), ArgumentMatchers.<StreamObserver<GetSingleResponse>>any());

		assertEquals("test", reqCaptor.getValue().getCacheName());
		assertEquals("key", reqCaptor.getValue().getKey());
		assertEquals(123, reqCaptor.getValue().getVersh());
  }
}
