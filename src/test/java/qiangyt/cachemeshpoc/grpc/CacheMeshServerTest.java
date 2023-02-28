package qiangyt.cachemeshpoc.grpc;

import static org.junit.Assert.assertEquals;

import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import qiangyt.cachemeshpoc.remote.grpc.GrpcService;

@RunWith(JUnit4.class)
public class CacheMeshServerTest {

  @Rule
  public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

  @Test
  public void greeterImpl_replyMessage() throws Exception {
    String serverName = InProcessServerBuilder.generateName();

    this.grpcCleanup.register(InProcessServerBuilder.forName(serverName).directExecutor().addService(new GrpcService()).build().start());

    var stub = CacheMeshGrpc.newBlockingStub(this.grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build()));

    var reply = stub.getSingle(GetSingleRequest.newBuilder().setKey( "test").build());

    assertEquals("test-value", reply.getValue().toStringUtf8());
  }
}
