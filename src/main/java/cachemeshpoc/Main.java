package cachemeshpoc;

import cachemeshpoc.jgroup.JGroupsListener;
import cachemeshpoc.jgroup.JGroupsMembers;

public class Main {

	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.out.println("usage: <set-key> <primary url> [url-1] [url-2] ...");
			return;
		}

		boolean sayHello = "true".equals(args[0]);
		System.out.printf("syHello=%s\n", sayHello);

		String primaryUrl = args[1];

		System.out.println("mesh bootstrap: ...");
		var mesh = new MeshNetwork();
		mesh.addLocalNode(primaryUrl);
		//mesh.addRedisNode("redis://localhost:6379");
		//mesh.addRemoteNode("grpc://localhost:20001");

		mesh.bootstrap();
		Thread.sleep(5000);
		System.out.println("mesh bootstrap: done");

		var jgroups = new JGroupsMembers(primaryUrl, "example", "jgroups.xml",
			new JGroupsListener() {
				@Override
				public void onNodeJoin(String nodeUrl) throws Exception {
					mesh.addRemoteNode(nodeUrl);
				}

				@Override
				public void onNodeLeave(String nodeUrl) throws Exception {
					//mesh.addRemoteNode("grpc://localhost:20001");
				}
			});
		jgroups.start();

		//String[] nodeUrls = args;

		try {
			if (!sayHello) {
				for (;;) {
					Thread.sleep(100);
				}
			} else {
				var cache = mesh.resolveCache("example", String.class);

				for (int i = 0; i < 100; i++) {
					Thread.sleep(2000);

					String key = "k" + i;
					String v = "v" + i;
					System.out.printf("putSingle('%s', '%s')\n", key, v);
					cache.putSingle(key, v);
				}

				for (int i = 0; i < 100; i++) {
					Thread.sleep(2000);

					String key = "k" + i;
					String v = cache.getSingle(key);
					System.out.printf("getSingle('%s') returns: %s\n", key, v);
				}

				for (int i = 0; i < 100; i++) {
					Thread.sleep(2000);

					String key = "k" + i;
					String v = "v" + (i*2);
					System.out.printf("putSingle('%s', '%s')\n", key, v);
					cache.putSingle(key, v);
				}

				for (int i = 0; i < 100; i++) {
					Thread.sleep(2000);

					String key = "k" + i;
					String v = cache.getSingle(key);
					System.out.printf("getSingle('%s') returns: %s\n", key, v);
				}
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		} finally {
			jgroups.stop();
			mesh.close();
		}
	}

}
