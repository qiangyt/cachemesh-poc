package cachemesh.grpc;

import io.grpc.BindableService;

public interface GrpcServer {

	void addService(BindableService service);

}

