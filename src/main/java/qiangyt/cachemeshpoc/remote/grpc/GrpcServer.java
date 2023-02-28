package qiangyt.cachemeshpoc.remote.grpc;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrpcServer {

	private static final Logger LOG = LoggerFactory.getLogger(GrpcServer.class);

  private Server server;

	@lombok.Getter
	private final GrpcConfig config;

	private final GrpcService service;

	public GrpcServer(GrpcConfig config, GrpcService service) {
		this.config = config;
		this.service = service;

		this.server = Grpc.newServerBuilderForPort(this.config.getPort(), InsecureServerCredentials.create())
											.addService(this.service)
											.build();
	}

  public void start() throws IOException {
    this.server.start();
		LOG.info("server started, listening on " + this.config.getPort());

    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        // Use stderr here since the logger may have been reset by its JVM shutdown hook.
        System.err.println("*** shutting down gRPC server since JVM is shutting down");
        try {
          GrpcServer.this.stop();
        } catch (InterruptedException e) {
          e.printStackTrace(System.err);
        }
        System.err.println("*** server shut down");
      }
    });
  }

  public void stop() throws InterruptedException {
    if (this.server != null) {
      this.server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
    }
  }

  public void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      this.server.awaitTermination();
    }
  }

}
