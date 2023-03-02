package cachemeshpoc.remote.url;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class Connection extends URLConnection {

	Connection(URL url) {
		super(url);
	}

	@Override
	public void connect() throws IOException {
		// do nothing
	}

}
