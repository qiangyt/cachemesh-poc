package cachemesh.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cachemesh.common.HasName;
import cachemesh.common.Mappable;
import net.logstash.logback.argument.StructuredArgument;
import net.logstash.logback.argument.StructuredArguments;

public class LogHelper {

	public static Logger getLogger(HasName hasName) {
		return getLogger(hasName.getClass().getName(), hasName.getName());
	}

	public static Logger getLogger(String type, String name) {
		return LoggerFactory.getLogger(name + "@" + type);
	}

	public static StructuredArgument entries(Mappable mappable) {
		var map = mappable.toMap();
		return StructuredArguments.entries(map);
	}

	public static StructuredArgument kv(String key, Mappable mappable) {
		var map = mappable.toMap();
		return StructuredArguments.kv(key, map);
	}

}
