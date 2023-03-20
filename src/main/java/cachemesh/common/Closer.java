package cachemesh.common;


import net.logstash.logback.argument.StructuredArgument;
import static net.logstash.logback.argument.StructuredArguments.kv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cachemesh.common.util.StringHelper;

public class Closer {

	private static final Logger DEFAULT_LOGGER = LoggerFactory.getLogger(Closer.class);

	public static void closeSilently(Iterable<? extends AutoCloseable> closeables) {
		closeSilently(closeables, null, DEFAULT_LOGGER);
	}

	public static void closeSilently(Iterable<? extends AutoCloseable> closeables, Logger logger) {
		closeSilently(closeables, null, logger);
	}

	public static void closeSilently(Iterable<? extends AutoCloseable> closeables, String name, Logger logger) {
		boolean debug = logger.isDebugEnabled();
		StructuredArgument nameKv = null;
		if (debug) {
			if (StringHelper.isBlank(name)) {
				name = "anonymous";
			}
			nameKv = kv("closables", name);

			logger.debug("closing {}", nameKv);
		}

		closeables.forEach(closeable -> closeSilently(closeable, logger));

		if (debug) {
			logger.debug("closed {}", nameKv);
		}
	}

	public static void closeSilently(AutoCloseable closeable) {
		closeSilently(closeable, null, DEFAULT_LOGGER);
	}

	public static void closeSilently(AutoCloseable closeable, Logger logger) {
		closeSilently(closeable, null, logger);
	}

	public static void closeSilently(AutoCloseable closeable, String nameOfClosable, Logger logger) {
		boolean debug = logger.isDebugEnabled();

		StructuredArgument closeableLogKv = null;
		if (debug) {
			if (StringHelper.isBlank(nameOfClosable)) {
				nameOfClosable = closeable.toString();
			}
			closeableLogKv = kv("closable", nameOfClosable);

			logger.debug("closing {}", closeableLogKv);
		}

		try {
			closeable.close();
			if (debug) {
				logger.debug("closed {}", closeableLogKv);
			}
		} catch (Exception e) {
			logger.warn("error occurred when closing {}. {}", closeableLogKv, kv("cause", e));
		}
	}

}
