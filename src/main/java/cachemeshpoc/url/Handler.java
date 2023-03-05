package cachemeshpoc.url;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import static java.lang.System.err;
import cachemeshpoc.Protocol;

public class Handler extends URLStreamHandler {

	public static Handler DEFAULT = new Handler();

	public static final String PROPERTY_KEY = "java.protocol.handler.pkgs";

	public static void registerHandler() {
		String newHandlerPkg =  Handler.class.getPackageName() ;
		err.printf("register protocol handler (%s): ...\n", newHandlerPkg);

		String handlerPkgs = System.getProperty(PROPERTY_KEY);
		err.printf("existing protocol handler: %s\n", handlerPkgs);

		if (handlerPkgs == null || handlerPkgs.isEmpty()) {
			handlerPkgs = newHandlerPkg;
		} else {
			handlerPkgs = newHandlerPkg + "|" + handlerPkgs;
		}
		err.printf("merged protocol handlers: %s\n", handlerPkgs);
		System.setProperty(PROPERTY_KEY, handlerPkgs);

		err.println("register protocol handler: done");
	}

	@Override
	protected URLConnection openConnection(URL u) throws IOException {
		if (Protocol.valueOf(u.getProtocol()) != null) {
			return new Connection(u);
		}
		return null;
	}

}


