package cachemeshpoc;

public class Main {

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("usage: <mode>");
			System.out.println("args:");
			System.out.println("    <mode>: 'local', or 'remote'");
			return;
		}

		boolean local = args[0].equals("local");

		var mesh = new MeshNetwork();
		if (local) {
			System.out.println("local mode");
			mesh.addLocalNode("grpc://localhost:20000");
			mesh.addRemoteNode("grpc://localhost:20001");
		} else {
			System.out.println("remote mode");
			mesh.addRemoteNode("grpc://localhost:20000");
			mesh.addLocalNode("grpc://localhost:20001");
		}

		mesh.bootstrap();

		try (mesh) {
			if (local) {
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

				for (int i = 0; i < 10; i++) {
					String key = "k" + i;
					String v = "v" + (i*2);
					cache.putSingle(key, v);
				}

				for (int i = 0; i < 10; i++) {
					String key = "k" + i;
					String v = cache.getSingle(key);
					System.out.printf("getSingle('%s') returns: %s\n", key, v);
				}
			} else {
				mesh.blockUntilTermination(true);
			}
		}
	}

}
