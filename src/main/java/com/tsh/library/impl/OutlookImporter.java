package com.tsh.library.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.tsh.entities.ImportItem;
import com.tsh.entities.Process;
import com.tsh.entities.ProcessDetails;
import com.tsh.exception.TSHException;
import com.tsh.library.DataImporter;
import com.tsh.service.IProcessService;
import com.tsh.utility.TshUtil;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.enumeration.search.FolderTraversal;
import microsoft.exchange.webservices.data.core.exception.service.local.ServiceLocalException;
import microsoft.exchange.webservices.data.core.service.folder.CalendarFolder;
import microsoft.exchange.webservices.data.core.service.item.Appointment;
import microsoft.exchange.webservices.data.core.service.schema.AppointmentSchema;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.FolderId;
import microsoft.exchange.webservices.data.property.complex.Mailbox;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import microsoft.exchange.webservices.data.search.CalendarView;
import microsoft.exchange.webservices.data.search.FindItemsResults;
import microsoft.exchange.webservices.data.search.FolderView;

@Component
@ConfigurationProperties(prefix = "outlook")
public class OutlookImporter implements DataImporter {

	private ExchangeService service;
	private String credentials = "tshcalendar@thestudyhouse.com.au";
	private String password = "Calendar2769";
	private String URI = "https://outlook.office365.com/EWS/Exchange.asmx";
	private Date startDate, endDate;
	private FindItemsResults<Appointment> appointments = null;
	private List<ImportItem> importedData = new ArrayList<ImportItem>();
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private IProcessService processService;

	public OutlookImporter() {
	}

	public OutlookImporter(String credentials, String password) {
		this();
		this.credentials = credentials;
		this.password = password;
	}

	@Override
	public List<ImportItem> importData(Process parent) throws TSHException {
		ProcessDetails step1 = processService.newProcessStep("Connecting to " + this.credentials + " Calender service",
				1, 0.2, parent);
		this.connect();

		ProcessDetails step2 = processService.closeOldAndCreateNewStep(step1, "Accessing Caledar data", 2, 0.6, parent);
		this.setCalendarView();

		ProcessDetails step3 = processService.closeOldAndCreateNewStep(step2, "Loadig Caledar Properties", 3, 1,
				parent);
		this.loadAllPropertySets();

		ProcessDetails step4 = processService.closeOldAndCreateNewStep(step3, "Reading Calendar Data", 4, 0.5, parent);
		this.processAllAppointments();

		this.closeAll();
		processService.completeProcessStep(step4);
		return importedData;
	}

	private void connect() throws TSHException {
		logger.info("Connecting to Exchange Calendar : {}...", credentials);
		service = new ExchangeService(ExchangeVersion.Exchange2010_SP2);
		ExchangeCredentials credentials = new WebCredentials(this.credentials, this.password);
		service.setCredentials(credentials);
		try {
			service.setUrl(new URI(URI));
		} catch (URISyntaxException e) {
			logger.error(e.getCause().getLocalizedMessage());
			logger.error(
					"Most common cause : Credentials to connect is not correrct. Check the credentials and also the network connectivity.");
			throw new TSHException(
					"Failed to connect to Microsoft Exchange server due to internal error\n" + e.getMessage());
		}
		FolderView fv = new FolderView(100);
		fv.setTraversal(FolderTraversal.Deep);
		logger.info("Connected to mailbox {} successfully.", credentials);
	}

	/**
	 * Just set the calendar view. This only includes setting the start date and the
	 * end date. The start date should always be the first day of the current week.
	 * Which is Sunday So current date - Day of the week will give the day before
	 * the start of the week. So either add 1 to the result or subtract 1 less than
	 * the day of week.
	 * 
	 * @throws Exception
	 */
	private void setCalendarView() throws TSHException {

		FolderId confRoomFolderId = new FolderId(WellKnownFolderName.Calendar, new Mailbox(this.credentials));
		// Get data only for the current week starting from Sunday
		try {
			if (this.startDate == null)
				this.startDate = TshUtil.getFirstDayOfCurrentWeek();
			if (this.endDate == null)
				this.endDate = TshUtil.getLastDayOfCurrentWeek();
		} catch (ParseException p) {
			logger.error("Error parsing the current date format. for date : {}", startDate);
			logger.error("Either the date is not valid or the format is not a valid date format.");
			logger.error(p.getLocalizedMessage());
			TSHException t = new TSHException(p.getMessage());
			t.setStackTrace(p.getStackTrace());
			throw t;
		}
		logger.info("Fetching calendar data from {} to {}.", startDate, endDate);
		try {
			CalendarFolder calendarFolder = CalendarFolder.bind(service, confRoomFolderId);
			CalendarView cView = new CalendarView(startDate, endDate, 2000);
			cView.setPropertySet(new PropertySet(AppointmentSchema.Id));
			appointments = calendarFolder.findAppointments(cView);
		} catch (Exception e) {
			logger.error("Unable to locate the default folder of the calendar.");
			logger.error("Try connecting manually and recheck the mailbox credentials");
			logger.error(e.getLocalizedMessage());
			TSHException t = new TSHException(e.getMessage());
			t.setStackTrace(e.getStackTrace());
			throw t;
		}
	}

	/**
	 * Body is a complex property of appointment and does not get loaded just by
	 * adding to the property set. So find the appointments first and then load all
	 * the rest of the properties along with the body.
	 * 
	 * @throws Exception
	 */
	private void loadAllPropertySets() throws TSHException {
		logger.info("Loading mandatory calendar property sets.");
		logger.info(
				"This might take 30 to 90 seconds depending on your internet connection. Please wait while we are loading data...");
		try {
			PropertySet remainingProps = new PropertySet();
			remainingProps.add(AppointmentSchema.Subject);
			remainingProps.add(AppointmentSchema.Start);
			remainingProps.add(AppointmentSchema.End);
			remainingProps.add(AppointmentSchema.Location);
			remainingProps.add(AppointmentSchema.Body);
			List<Appointment> appList = appointments.getItems();
			for (Appointment app : appList) {
				app.load(remainingProps);
			}
		} catch (Exception e) {
			logger.error("Error setting property sets to the appointment schema. Please comntact your administrator.");
			logger.error(e.getLocalizedMessage());
			TSHException t = new TSHException(e.getMessage());
			t.setStackTrace(e.getStackTrace());
			throw t;
		}
		logger.info("All property sets loaded from the exchange server.");
	}

	/**
	 * Each appointment data should be formatted, trimmed and then inserted to the
	 * database. Student name from Subject Location from Location Grade from Body
	 * line 1 Subject from Body line 2 Teacher from Body line 3 Batch Start time
	 * from start Batch End time from end
	 * 
	 * @throws Exception
	 */
	private void processAllAppointments() throws TSHException {
		ImportItem item;
		String bodyText = null;
		DateFormat formatter = new SimpleDateFormat("d/MM/yyyy", Locale.ENGLISH);
		DateFormat timeFormatter = new SimpleDateFormat("HH:mm");
		List<Appointment> appList = appointments.getItems();
		logger.info("Processing appointment data and extracting required information.");
		try {
			for (Appointment app : appList) {
//				logger.info(" " + i++);
//				if(i == 37) {
//					logger.info("Here");
//				}
				item = new ImportItem();
				bodyText = MessageBody.getStringFromMessageBody(app.getBody());
				if (bodyText == null || bodyText.length() > 100 || app.getSubject() == null || bodyText.length() == 0
						|| app.getSubject().length() == 0 || app.getSubject().contains("Home work")
						|| app.getSubject().contains("Home Work") || app.getSubject().contains("Homework")
						|| app.getSubject().contains("HomeWork"))
					continue;

				bodyText = bodyText.replaceAll("\t", " ");
				bodyText = bodyText.replaceAll("\n", " ");
				bodyText = bodyText.replaceAll("\r", " ");
				bodyText = bodyText.replaceAll("\f", " ");
				bodyText = bodyText.trim();
				StringTokenizer token = new StringTokenizer(bodyText, " ");
				token.nextToken(); // Don't want this string. Just need the grade number.
				item.setGradeNumber(Integer.parseInt(token.nextToken().trim()));
				item.setGrade("Year " + item.getGradeNumber());
				if (token.countTokens() > 2) { // In case the student is selective of Advance or Extended. Add the
												// Extension to Subject. This will become the course in DB.
					String extension = token.nextToken().trim();
					item.setSubject(token.nextToken().trim() + " " + extension);
				} else {
					item.setSubject(token.nextToken().trim());
				}
				item.setTeacher(token.nextToken().trim());
				item.setName(app.getSubject().trim());
				item.setLocation(app.getLocation().trim());
				item.setBatchDate(formatter.parse(formatter.format(app.getStart())));
				item.setBatchStartTime(Time.valueOf(timeFormatter.format(app.getStart()) + ":00"));
				item.setBatchEndTime(Time.valueOf(timeFormatter.format(app.getEnd()) + ":00"));
				importedData.add(item);
			}
		} catch (ParseException e) {
			logger.error("Error parsing appointment data - {}. Please comntact your administrator.", bodyText);
			logger.error(e.getLocalizedMessage());
			TSHException t = new TSHException(e.getMessage());
			t.setStackTrace(e.getStackTrace());
			throw t;
		} catch (ServiceLocalException e) {
			logger.error("Error parsing appointment data - {}. Please comntact your administrator.", bodyText);
			logger.error(e.getLocalizedMessage());
			TSHException t = new TSHException(e.getMessage());
			t.setStackTrace(e.getStackTrace());
			throw t;
		} catch (Exception e) {
			logger.error("Error parsing appointment data - {}. Please comntact your administrator.", bodyText);
			logger.error(e.getLocalizedMessage());
			TSHException t = new TSHException(e.getMessage());
			t.setStackTrace(e.getStackTrace());
			throw t;
		}
		logger.info("{} appointments processed and are ready to be imported into the system. ", importedData.size());
	}

	private void closeAll() {
		this.service.close();
		this.service = null;
		this.appointments = null;
		logger.info("All connections to microsoft excahnge closed.");
	}

	@SuppressWarnings("unused")
	private void displayOnCosole() throws ServiceLocalException {
		for (ImportItem it : importedData) {
			System.out.println(it.toString());
		}
	}

//	public static void main(String args[]) {
//		OutlookImporter io = new OutlookImporter();
//		try {
//			io.importData();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	public void setStartDate(String sDate) throws ParseException {
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
		this.startDate = format.parse(sDate);
	}

	public Date getStartDate() {
		return this.startDate;
	}

	public void setEndDate(String eDate) throws ParseException {
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
		this.endDate = format.parse(eDate);
	}

	public Date getEndDate() {
		return this.endDate;
	}
}
