package controllers;

import java.util.Calendar;
import java.util.Date;

import play.data.format.Formats;

public class DateTime {
	@Formats.DateTime(pattern="dd/MM/yyyy")
	private static Date date;
	
	public DateTime() {
		date = new Date();
	}

	public Date getDateTime() {
		date = Calendar.getInstance().getTime();
		return date;
	}
}
