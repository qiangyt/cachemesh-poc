package cachemeshpoc.url;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import cachemeshpoc.MeshProtocol;;

public class Handler extends URLStreamHandler {

	public static final String PROPERTY_KEY = "java.protocol.handler.pkgs";

	public static void registerHandler() {
		String handlerPkgs = System.getProperty(PROPERTY_KEY);
		handlerPkgs = Handler.class.getPackageName() + "|" + handlerPkgs;
		System.setProperty(PROPERTY_KEY, handlerPkgs);
	}

	@Override
	protected URLConnection openConnection(URL u) throws IOException {
		if (MeshProtocol.valueOf(u.getProtocol()) != null) {
			return new Connection(u);
		}
		return null;
	}

}


