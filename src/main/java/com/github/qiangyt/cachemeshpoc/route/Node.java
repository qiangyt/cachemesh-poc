package com.github.qiangyt.cachemeshpoc.route;

import com.github.qiangyt.cachemeshpoc.RemoteCache;

// Based on github.com/redis/jedis: redis.clients.jedis.HostAndPort
@lombok.Getter
public class Node {

	private final RemoteCache remoteCache;
	private final String host;
	private final int port;
	private final String key;
	private final int hashCode;

	public Node(RemoteCache remoteCache, String host, int port) {
		this.remoteCache = remoteCache;
		this.host = host;
		this.port = port;
		this.key = host + ":" + port;
		this.hashCode = this.key.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Node)) {
			return false;
		}
		Node other = (Node) obj;
		return this.key.equals(other.key);
	}

	@Override
	public int hashCode() {
		return this.hashCode;
	}

	@Override
	public String toString() {
		return this.key;
	}

	/**
	 * Creates node with <i>unconverted</i> host.
	 *
	 * @param string String to parse. Must be in <b>"host:port"</b> format. Port is
	 *               mandatory.
	 * @return parsed Peer
	 */
	public static Node from(String str) {
		int lastColon = str.lastIndexOf(":");
		String host = str.substring(0, lastColon);
		int port = Integer.parseInt(str.substring(lastColon + 1));
		return new Node(host, port);
	}
}
