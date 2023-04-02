package cachemesh.common.util;

import java.time.format.DateTimeFormatter;

public class DateHelper {

	public static final String DAY_PATTERN = "yyyy-MM-dd";
	public static final String TIME_PATTERN = "HH:mm:ss.SSS";
	public static final String RFC3339_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	public static final String DAYTIME_PATTERN = DAY_PATTERN + " " + TIME_PATTERN;

	public static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern(DAY_PATTERN);
	public static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern(TIME_PATTERN);
	public static final DateTimeFormatter RFC3339 = DateTimeFormatter.ofPattern(RFC3339_PATTERN);
	public static final DateTimeFormatter DAYTIME = DateTimeFormatter.ofPattern(DAYTIME_PATTERN);
}
