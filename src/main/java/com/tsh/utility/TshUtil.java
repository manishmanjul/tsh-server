package com.tsh.utility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.tsh.entities.TimeSlot;
import com.tsh.exception.TSHException;

public class TshUtil {
	
	/**
	 * Finds the first day of the current week considering Sunday as the first day of the week.
	 * @return Java.util.Date
	 * @throws ParseException
	 */
	public static Date getFirstDayOfCurrentWeek() throws ParseException{
		Calendar now = Calendar.getInstance();
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

		now.setFirstDayOfWeek(1);
		now.add(Calendar.DATE, - (now.get(Calendar.DAY_OF_WEEK)-1));
		return formatter.parse(formatter.format(now.getTime()));
	}
	
	/**
	 * Finds the first day of the week of the date passed considering Sunday as the first day of the week.
	 * @return Java.util.Date
	 * @throws ParseException
	 */
	public static Date getFirstDayOfWeek(Date date) throws ParseException{
		Calendar now = Calendar.getInstance();
		now.setTime(date);
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

		now.setFirstDayOfWeek(1);
		now.add(Calendar.DATE, - (now.get(Calendar.DAY_OF_WEEK)-1));
		return formatter.parse(formatter.format(now.getTime()));
	}
	
	/**
	 * Returns the last day of the current week considering Sunday to the first day of the week.
	 * @return java.util.Date
	 * @throws ParseException
	 */
	public static Date getLastDayOfCurrentWeek() throws ParseException {
		Calendar now = Calendar.getInstance();
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

		now.add(Calendar.DATE, (7-now.get(Calendar.DAY_OF_WEEK)));
		return formatter.parse(formatter.format(now.getTime()));
	}

	 /* Returns the last day of the week considering Sunday as the first day of the week.
	 * @return java.util.Date
	 * @throws ParseException
	 */
	public static Date getLastDayOfWeek(Date date) throws ParseException {
		Calendar now = Calendar.getInstance();
		now.setTime(date);
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

		now.add(Calendar.DATE, (7-now.get(Calendar.DAY_OF_WEEK)));
		return formatter.parse(formatter.format(now.getTime()));
	}
	
	public static Date getCurrentDate() throws TSHException {
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
		Date today = Calendar.getInstance().getTime();
		try {
			today = formatter.parse(formatter.format(today));
		} catch (ParseException e) {
			throw new TSHException(e.getMessage());
		}
		return today;
	}
	
	public static Date nextWeek() throws TSHException {
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
		Calendar today = Calendar.getInstance();
		today.add(Calendar.DATE, 7);
		Date nextWeek = null;
		try {
			nextWeek = formatter.parse(formatter.format(today.getTime()));
		} catch (ParseException e) {
			throw new TSHException(e.getMessage());
		}
		return nextWeek;
	}
	
	/**
	 * Returns the date of the next class based of the batch timeslot.
	 * @param timeSlot
	 * @return
	 * @throws TSHException
	 */
	public static Date nextClass(TimeSlot timeSlot) throws TSHException {
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
		Date nextClassDate = null;
		Calendar today = Calendar.getInstance();
		today.add(Calendar.DATE, 7);
		int weekDay = today.get(Calendar.DAY_OF_WEEK);
		int offSet = timeSlot.getWeekday() - weekDay;
		today.add(Calendar.DATE, offSet);
		
		try {
			nextClassDate = formatter.parse(formatter.format(today.getTime()));
		} catch (ParseException e) {
			throw new TSHException(e.getMessage());
		}
		return nextClassDate;
	}
	
	/**
	 * Returns todays week day. 
	 */
	public static int getTodaysWeekDay() {
		Calendar today = Calendar.getInstance();
		return today.get(Calendar.DAY_OF_WEEK);
	}
	
	public static Date format(Date input) throws TSHException {
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
		Date returnDate = null;
		
		try {
			returnDate = formatter.parse(formatter.format(input));
		} catch (ParseException e) {
			throw new TSHException(e.getMessage());
		}
		return returnDate;
	}
}
