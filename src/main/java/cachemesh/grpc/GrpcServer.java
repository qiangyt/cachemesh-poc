package cachemesh.grpc;

import io.grpc.BindableService;

public interface GrpcServer {

	void start(int timeoutSeconds);

	void stop(int timeoutSeconds) throws InterruptedException;

	void addService(BindableService service);

	boolean isStarted();

}

