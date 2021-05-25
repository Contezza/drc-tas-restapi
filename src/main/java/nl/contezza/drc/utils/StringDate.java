package nl.contezza.drc.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;

public class StringDate {

	private static final String FORMAT_DATE = "yyyy-MM-dd";
	private static final String FORMAT_DATETIME = "yyyy-MM-dd'T'HH:mm:ss";
	
	public static String toDateString(int year, int month, int day) {
		return formatDate(getDate(year, month -1, day));
	}
	
	public static String toDatetimeString(int year, int month, int day) {
		return formatDatetime(getDate(year, month -1, day));
	}
	
	public static String toDatetimeString(Date date) {
		return formatDatetime(date);
	}
	
	public static Date getDateTime(String date) {
		DateTime dt = new DateTime(date);
		return dt.toDate();
	}
	
	private static Date getDate(int year, int month, int day) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day);
		return cal.getTime();	
	}
	
	private static String formatDate(Date date) {
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
