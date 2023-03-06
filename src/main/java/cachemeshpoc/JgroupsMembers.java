package cachemeshpoc;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.Receiver;
import org.jgroups.View;
import org.jgroups.blocks.cs.ReceiverAdapter;
import org.jgroups.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cachemeshpoc.err.MeshInternalException;

public class JgroupsMembers {

	public static interface Listener {
		default void onNodeJoin(String url) {}
		default void onNodeLeave(String url) {}
	}

	private final Listener listener;

	@lombok.Getter
	private final String nodeName;

	@lombok.Getter
	private final String meshNetworkName;

	@lombok.Getter
	private final String configXmlClasspath;

	private final Logger log;

	private JChannel channel;

	private View currentView;

	public JgroupsMembers(String nodeName, String meshNetworkName, String configXmlClasspath, Listener listener) {
		this.listener = listener;
		this.nodeName = nodeName;
		this.meshNetworkName = meshNetworkName;
		this.configXmlClasspath = configXmlClasspath;

		this.log = LoggerFactory.getLogger(toString());
	}

	@Override
	public String toString() {
		return this.meshNetworkName + "=" + this.nodeName;
	}

	public void start() {
		try {
			this.log.info("join the mesh: ...");
			var ch = new JChannel(this.configXmlClasspath);
			ch.name(this.nodeName).setReceiver(new MyReceiver());

			ch.connect(this.meshNetworkName);
			this.log.info("join the mesh: done");

			this.channel = ch;
		} catch (Exception e) {
			throw new MeshInternalException(e, "failed to join the mesh: %s", toString());
		}
	}

	public void stop() {
		if (this.channel != null) {
			this.log.info("leave the mesh: ...");
			this.channel.close();
			this.log.info("leave the mesh: done");
		}
	}

	static class MyReceiver implements Receiver {

		final JgroupsMembers me;

		MyReceiver(JgroupsMembers me) {
			this.me = me;
		}

		@Override
		public void receive(Message msg) {
			String json = msg.getObject();
		}

		@Override
		public void viewAccepted(View v) {
			me.currentView = v;
		}

	}

}
