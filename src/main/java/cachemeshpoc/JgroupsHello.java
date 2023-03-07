package cachemeshpoc;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.Receiver;
import org.jgroups.View;
import org.jgroups.util.Util;

public class JgroupsHello {

	protected JChannel ch;

	protected void start(String name) throws Exception {
		ch = new JChannel("jgroups.xml").name(name)
				.setReceiver(new MyReceiver(name))
				.connect("demo-cluster");
		int counter = 1;
		for (;;) {
			ch.send(null, "msg-" + counter++);
			Util.sleep(3000);
		}
	}

	protected static class MyReceiver implements Receiver {
		protected final String name;

		protected MyReceiver(String name) {
			this.name = name;
		}

		public void receive(Message msg) {
			System.out.printf("-- [%s] msg from %s: %s\n", name, msg.src(), msg.getObject());
		}

		public void viewAccepted(View v) {
			System.out.printf("-- [%s] new view: %s\n", name, v);
		}
	}

	public static void main(String[] args) throws Exception {
		new JgroupsHello().start(args[0]);
	}
}
