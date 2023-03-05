package cachemeshpoc;

public class Main {

	public static void main(String[] args) throws Exception {

		var mesh = new MeshNetwork();

		mesh.addLocalNode("grpc://localhost:20001");
		//mesh.addRemoteNode("grpc://localhost:20002");
		//mesh.addRemoteNode("grpc://localhost:20003");

		mesh.bootstrap();
		try (mesh) {
			var cache = mesh.resolveCache("example", String.class);
			cache.putSingle("k1", "v1");

			String v1 = cache.getSingle("k1");
			System.out.println("getSingle(key) returns:" + v1);
		}

	}


}
