package cachemesh.common.shutdown;


import static net.logstash.logback.argument.StructuredArguments.kv;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cachemesh.common.HasName;
import cachemesh.common.util.DateHelper;
import lombok.Getter;
import lombok.Setter;

@Getter
public class ShutdownLogger implements HasName {

	public static final ShutdownLogger DEFAULT = new ShutdownLogger(ShutdownLogger.class);

	private final Logger logger;

	@Setter
	private volatile boolean inShutdownHook;

	public ShutdownLogger(Class<?> klass) {
		this(klass.getSimpleName());
	}

	public ShutdownLogger(String name) {
		this(LoggerFactory.getLogger(name));
	}

	public ShutdownLogger(Logger logger) {
		this.logger = logger;
	}


	public void info(String msgFormat, Object... args) {
		String msg = String.format(msgFormat, args);

		if (isInShutdownHook()) {
			System.out.printf("%s INFO (SHUTDOWN) - %s: %s \n", LocalDateTime.now().format(DateHelper.DAYTIME),
					getName(), msg);
		} else {
			logger.info("{}: %s", kv("name", getName()), msg);
		}
	}

	public void error(Throwable cause, String msgFormat, Object... args) {
		String msg = String.format(msgFormat, args);

		if (isInShutdownHook()) {
			System.out.printf("%s ERROR (SHUTDOWN) - %s: %s \n", LocalDateTime.now().format(DateHelper.DAYTIME),
					getName(), msg);
			if (cause != null) {
				cause.printStackTrace(System.err);
			}
		} else {
			logger.error("{}: %s", kv("name", getName()), msg);
		}
	}

	@Override
	public String getName() {
		return this.logger.getName();
	}

}
