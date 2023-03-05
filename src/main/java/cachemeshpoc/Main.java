package cachemeshpoc;

public class Main {

	public static void main(String[] args) throws Exception {

		var mesh = new MeshNetwork();

		mesh.addLocalNode("grpc://localhost:20001");
		//mesh.addRemoteNode("grpc://localhost:20002");
		//mesh.addRemoteNode("grpc://localhost:20003");
		mesh.addLocalNode("grpc://localhost:20002");
		mesh.addLocalNode("grpc://localhost:20003");

		mesh.bootstrap();
		try (mesh) {
			var cache = mesh.resolveCache("example", String.class);

			for (int i = 0; i < 10; i++) {
				String key = "k" + i;
				String v = "v" + i;
				cache.putSingle(key, v);
			}

			for (int i = 0; i < 10; i++) {
				String key = "k" + i;
				String v = cache.getSingle(key);
				System.out.printf("getSingle('%s') returns: %s\n", key, v);
			}
		}

	}


}
