package com.github.qiangyt.cachemeshpoc.route;

// Based on github.com/redis/jedis: redis.clients.jedis.HostAndPort
public class Node {

  private final String host;
  private final int port;

  public Node(String host, int port) {
    this.host = host;
    this.port = port;
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (obj == this) return true;
    if (!(obj instanceof Node)) return false;
    Node other = (Node) obj;
    return this.port == other.port && this.host.equals(other.host);
  }

  @Override
  public int hashCode() {
    return 31 * host.hashCode() + port;
  }

  @Override
  public String toString() {
    return host + ":" + port;
  }

  /**
   * Creates Peer with <i>unconverted</i> host.
   * @param string String to parse. Must be in <b>"host:port"</b> format. Port is mandatory.
   * @return parsed Peer
   */
  public static Node from(String string) {
    int lastColon = string.lastIndexOf(":");
    String host = string.substring(0, lastColon);
    int port = Integer.parseInt(string.substring(lastColon + 1));
    return new Node(host, port);
  }
}
