// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: cachemesh.proto

package cachemesh.grpc;

public interface GetSingleRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:cachemesh.GetSingleRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>string cacheName = 1;</code>
   * @return The cacheName.
   */
  java.lang.String getCacheName();
  /**
   * <code>string cacheName = 1;</code>
   * @return The bytes for cacheName.
   */
  com.google.protobuf.ByteString
      getCacheNameBytes();

  /**
   * <code>string key = 2;</code>
   * @return The key.
   */
  java.lang.String getKey();
  /**
   * <code>string key = 2;</code>
   * @return The bytes for key.
   */
  com.google.protobuf.ByteString
      getKeyBytes();

  /**
   * <code>int64 version = 3;</code>
   * @return The version.
   */
  long getVersion();
}
