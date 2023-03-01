package cachemeshpoc.remote.grpc.url;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import cachemeshpoc.MeshProtocol;

public class Handler extends URLStreamHandler {

	public static void registerHandler() {
		String handlerPkgs = System.getProperty("java.protocol.handler.pkgs");
		handlerPkgs = "cachemeshpoc.remote.grpc.url|" + handlerPkgs;
		System.setProperty("java.protocol.handler.pkgs", handlerPkgs);
	}

	@Override
	protected URLConnection openConnection(URL u) throws IOException {
		if (MeshProtocol.valueOf(u.getProtocol()) != null) {
			return new Connection(u);
		}
		return null;
	}

}


