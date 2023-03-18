package cachemeshpoc.jgroup;

public interface JGroupsListener {

	default void onNodeJoin(String nodeUrl) throws Exception {}

	default void onNodeLeave(String nodeUrl) throws Exception {}

}
