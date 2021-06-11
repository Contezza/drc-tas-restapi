package nl.contezza.drc.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.joda.time.DateTime;

public class StringDate {

	private static final String FORMAT_DATE = "yyyy-MM-dd";
	private static final String FORMAT_DATETIME = "yyyy-MM-dd'T'HH:mm:ss";
	private static final String FORMAT_ISO8601 = "yyyy-MM-dd'T'HH:mm:ssX";

	public static String toDateString(int year, int month, int day) {
		return formatDate(getDate(year, month - 1, day));
	}

	public static String toDatetimeString(int year, int month, int day) {
		return formatDatetime(getDate(year, month - 1, day));
	}

	public static String toDatetimeString(Date date) {
		return formatDatetime(date);
	}

	public static Date getDateTime(String date) {
		DateTime dt = new DateTime(date);
		return dt.toDate();
	}

	public static String toISO8601(Date date) {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat(FORMAT_ISO8601);
		df.setTimeZone(tz);
		return df.format(date);
	}

	private static Date getDate(int year, int month, int day) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day);
		return cal.getTime();
	}

	public static String formatDate(Date date) {
		if (date == null)
			return null;
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE);
		return sdf.format(date);
	}

	private static String formatDatetime(Date date) {
		if (date == null)
			return null;
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATETIME);
		return sdf.format(date);
	}
}
