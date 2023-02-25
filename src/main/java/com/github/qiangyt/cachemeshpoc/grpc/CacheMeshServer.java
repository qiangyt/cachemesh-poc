package com.github.qiangyt.cachemeshpoc.grpc;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheMeshServer {
	private static final Logger LOG = LoggerFactory.getLogger(CacheMeshServer.class);

  private Server server;

  private void start() throws IOException {
    /* The port on which the server should run */
    int port = 50051;
    this.server = Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create())
											.addService(new CacheMeshImpl())
											.build()
											.start();
		LOG.info("Server started, listening on " + port);

    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        // Use stderr here since the logger may have been reset by its JVM shutdown hook.
        System.err.println("*** shutting down gRPC server since JVM is shutting down");
        try {
          CacheMeshServer.this.stop();
        } catch (InterruptedException e) {
          e.printStackTrace(System.err);
        }
        System.err.println("*** server shut down");
      }
    });
  }

  private void stop() throws InterruptedException {
    if (this.server != null) {
      this.server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
    }
  }

  private void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      this.server.awaitTermination();
    }
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    final CacheMeshServer server = new CacheMeshServer();
    server.start();
    server.blockUntilShutdown();
  }

}
