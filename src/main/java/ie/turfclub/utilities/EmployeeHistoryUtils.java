package ie.turfclub.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;

import ie.turfclub.model.stableStaff.TeEmployees;
import ie.turfclub.model.stableStaff.TeEmployentHistory;

public class EmployeeHistoryUtils {

	private static Calendar c = Calendar.getInstance();
	private List<TeEmployees> currentEmployees;
	private List<TeEmployees> newEmployees;
	private List<TeEmployees> employeesLeft;
	private List<TeEmployees> employeesOver65;

	public List<TeEmployentHistory> createListOfEmploymentHistoriesFromSingleForNewEmployee(
			TeEmployentHistory history) {

		List<TeEmployentHistory> histories = new ArrayList<>();
		// if the person entered is a new employee create a record for each year
		// of employment.
		// if a value was entered for last years earnings set value on the
		// record for last year
		// set the employment category on

		System.out.println(history.getEhDateFrom());
		c.setTime(history.getEhDateFrom());
		int startYear = c.get(Calendar.YEAR);
		int yearToSave = c.get(Calendar.YEAR);
		c.setTime(new Date());

		int currentYear = c.get(Calendar.YEAR);
		// CHANGE EARNINGS YEAR HERE
		// comment out this line when current year changes from 2014 to 2015
		currentYear -= 2;
		// change this to -1 from -2 when current earnings year changes from
		// 2014 to 2015
		c.add(Calendar.YEAR, -3);
		int lastYear = c.get(Calendar.YEAR);
		int endYear = currentYear;

		// if the end date is not null set end
		if (history.getEhDateTo() != null) {
			System.out.println("End Date Not Null");
			c.setTime(history.getEhDateTo());
			endYear = c.get(Calendar.YEAR);
		}

		Date tempDate = null;

		while (yearToSave <= currentYear) {
			TeEmployentHistory newHistory = new TeEmployentHistory();
			// if the start year is used then save the date from otherwise set
			// to 01-01
			if (yearToSave == startYear) {
				newHistory.setEhDateFrom(history.getEhDateFrom());

			} else {

				try {
					tempDate = new SimpleDateFormat("yyyy-MM-dd")
							.parse(yearToSave + "-01-01");
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				newHistory.setEhDateFrom(tempDate);

			}

			// if the year to save is the same as the end year use the end date
			// otherwise 31-12
			if (yearToSave == endYear) {
				if (history.getEhDateTo() != null) {
					newHistory.setEhDateTo(history.getEhDateTo());

				} else {
					newHistory.setEhDateTo(null);

				}
			} else {
				System.out.println("SET DATE ON END YEAR");
				try {
					tempDate = new SimpleDateFormat("yyyy-MM-dd")
							.parse(yearToSave + "-12-31");
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				newHistory.setEhDateTo(tempDate);

			}

			// if the year to save is last year then set the earnings field on
			// the record
			if (yearToSave == lastYear) {
				newHistory.setEhEarnings(history.getEhEarnings());
			}
			// set the employment category & pps on each entry
			newHistory.setEhPpsNumber(history.getEhPpsNumber());
			newHistory.setEhEmploymentCategory(history
					.getEhEmploymentCategory());
			histories.add(newHistory);
			yearToSave++;
		}

		return histories;
	}

	public List<TeEmployentHistory> updateExistingEmploymentHistory(
			List<TeEmployentHistory> existingHistories,
			TeEmployentHistory newHistory) {

		System.out.println("Category: " + newHistory.getEhEmploymentCategory());
		if (newHistory.getEhEmploymentCategory() != null
				&& newHistory.getEhEmploymentCategory().length() == 0) {
			newHistory.setEhEmploymentCategory(null);
		}
		System.out.println(newHistory.getEhDateFrom());
		c.setTime(newHistory.getEhDateFrom());

		c.setTime(new Date());
		int currentYear = c.get(Calendar.YEAR);
		// CHANGE EARNINGS YEAR HERE
		// comment out this line when current year changes from 2014 to 2015
		currentYear -= 2;
		// change this to -1 from -2 when current earnings year changes from
		// 2014 to 2015
		c.add(Calendar.YEAR, -3);

		int lastYear = c.get(Calendar.YEAR);
		int endYear = currentYear;

		// if the end date is not null set end
		if (newHistory.getEhDateTo() != null) {
			c.setTime(newHistory.getEhDateTo());
			endYear = c.get(Calendar.YEAR);
		}

		Date tempDate = null;
		if (existingHistories.size() > 1) {
			Collections
					.sort(existingHistories,
							new TeEmployentHistory.TeEmployentHistoryComparatorDateTo());
		}

		// if new end date is null save last year earnings + and this year
		// category
		if (newHistory.getEhDateTo() == null) {
			for (TeEmployentHistory history : existingHistories) {
				c.setTime(history.getEhDateFrom());
				int historyYear = c.get(Calendar.YEAR);
				if (historyYear == lastYear) {
					history.setEhEmploymentCategory(newHistory
							.getEhEmploymentCategory());
					history.setEhEarnings(newHistory.getEhEarnings());
					history.setEhPpsNumber(newHistory.getEhPpsNumber());
				} else if (historyYear == currentYear) {
					history.setEhEmploymentCategory(newHistory
							.getEhEmploymentCategory());
					history.setEhPpsNumber(newHistory.getEhPpsNumber());
				}
			}
		} else if (newHistory.getEhDateTo() != null) {

			for (TeEmployentHistory history : existingHistories) {
				// remove all other records if end date set

				c.setTime(history.getEhDateFrom());
				int historyYear = c.get(Calendar.YEAR);
				if (historyYear == lastYear) {
					history.setEhEmploymentCategory(newHistory
							.getEhEmploymentCategory());
					history.setEhEarnings(newHistory.getEhEarnings());
					// if the end year entered is last year set the end date on
					// the 2014 record
					if (endYear == lastYear) {
						history.setEhDateTo(newHistory.getEhDateTo());

					}
				} else if (historyYear == currentYear) {
					history.setEhEmploymentCategory(newHistory
							.getEhEmploymentCategory());
					history.setEhPpsNumber(newHistory.getEhPpsNumber());
					if (endYear == currentYear) {
						history.setEhDateTo(newHistory.getEhDateTo());

					}
				}

			}
		}
		return existingHistories;

	}

	public TeEmployentHistory createSingleEmploymentHistoryFromList(
			List<TeEmployentHistory> histories) {

		if (histories.size() > 1) {
			Collections
					.sort(histories,
							new TeEmployentHistory.TeEmployentHistoryComparatorDateTo());
		}

		//System.out.println("History");
		Date finishDatePlusOne = null;
		List<TeEmployentHistory> listOfContinuousEmployment = new ArrayList<>();
		// loop the employment histories and check for continuity
		// if there is a break between the dates then clear the list and only
		// add the following histories which are continuous
		String ppsNumber = "";
		float earnings2014 = new Float(0.0);
		c.setTime(new Date());
		//CHANGE EARNINGS YEAR HERE
		//comment out line below to change report to show current year  
		c.add(Calendar.YEAR, -1);
		int year = c.get(Calendar.YEAR);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = sdf.parse(year + "-01-01");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Collections.sort(histories, new Comparator<TeEmployentHistory>() {

			@Override
			public int compare(TeEmployentHistory o1, TeEmployentHistory o2) {
				// TODO Auto-generated method stub
				return o1.getEhDateFrom().compareTo(o2.getEhDateFrom());
			}
			
		});
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.YEAR, -3);
		Date startDate = new DateTime(cal.get(Calendar.YEAR),1,1,0,0).toDate();
		Date endDate = new DateTime(cal.get(Calendar.YEAR), 12,31, 0, 0).toDate();
		//System.out.println(date);
		for (TeEmployentHistory history : histories) {
			//System.out.println(history.getEhPpsNumber() + " " + history.getEhDateFrom().before(date));
			if (history.getEhPpsNumber() != null && history.getEhDateFrom().before(date)) {
				ppsNumber = history.getEhPpsNumber();
			}
			//System.out.println(history.getEhEarnings());
			if (history.getEhEarnings() != null) {
				earnings2014 = history.getEhEarnings();
			}
			listOfContinuousEmployment.add(history);

			// section to split history if break in serivce
			/*
			 * Date historyDateFrom = history.getEhDateFrom();
			 * c.setTime(historyDateFrom); historyDateFrom = c.getTime();
			 * 
			 * System.out.println("Check:" + " " + finishDatePlusOne + " " +
			 * historyDateFrom); if (finishDatePlusOne != null &&
			 * !finishDatePlusOne.equals(historyDateFrom)) {
			 * listOfContinuousEmployment = new ArrayList<>();
			 * System.out.println("Service Break"); }
			 * listOfContinuousEmployment.add(history); if
			 * (history.getEhDateTo() != null) {
			 * c.setTime(history.getEhDateTo()); } else { c.setTime(new Date());
			 * } c.add(Calendar.DATE, 1); finishDatePlusOne = c.getTime();
			 */

		}
		TeEmployentHistory temphistory = new TeEmployentHistory();
		//System.out.println("MIN");
		temphistory = Collections.min(listOfContinuousEmployment,
				new TeEmployentHistory.TeEmployentHistoryComparatorDateFrom());
		TeEmployentHistory history = new TeEmployentHistory();
		//System.out.println("MAX");
		history = Collections.max(listOfContinuousEmployment,
				new TeEmployentHistory.TeEmployentHistoryComparatorDateTo());
		//System.out.println("FROM");
		for(TeEmployentHistory his : listOfContinuousEmployment) {
			if((startDate.compareTo(his.getEhDateFrom()) * his.getEhDateFrom().compareTo(endDate)) >= 0) {
				history.setExistsYearRecord(true);
				break;
			}
		}
		history.setEhDateFrom(temphistory.getEhDateFrom());
		if(listOfContinuousEmployment != null && listOfContinuousEmployment.size() > 0) {
			TeEmployentHistory firsthistory = listOfContinuousEmployment.get(listOfContinuousEmployment.size()-1);
			if(firsthistory.getEhDateTo() == null) history.setEhDateTo(null);
			//history.setEhDateTo(firsthistory.getEhDateTo());
		}
		history.setEhPpsNumber(ppsNumber);
		history.setEhEarnings(earnings2014);

		/*
		 * if the min start date is before the 01/01/2011 show it otherwise
		 * provide null value Date startDate = null; try { startDate = new
		 * SimpleDateFormat("yyyy-MM-dd").parse("2011-01-01"); } catch
		 * (ParseException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 * if(!history.getEhDateFrom().before(startDate)){
		 * history.setEhDateFrom(null); }
		 */
		//System.out.println("SORTED");
		return history;
	}

	public void sortEmployees(List<TeEmployees> employees, int trainerId) {

		System.out.println("SORT EMPLO" + employees.size());
		this.currentEmployees = new ArrayList<>();
		this.employeesLeft = new ArrayList<>();
		this.newEmployees = new ArrayList<>();
		this.employeesOver65 = new ArrayList<>();
		
		
		Date today = new Date();
		
		c.setTime(today);
		//EARNINGS YEAR 
		c.add(Calendar.YEAR, -1);
		
		
		
		
		Date startLastYear = null;
		Date endLastYear = null;
		Date earliestDateOfBirth = null;
		Date endDateFor2014 = null;

		System.out.println(today);
		System.out.println(c
				.get(Calendar.YEAR) + "-12-31");
		try {
			c.add(Calendar.YEAR, -65);
			earliestDateOfBirth = new SimpleDateFormat("yyyy-MM-dd").parse(c
					.get(Calendar.YEAR) + "-12-31");
			System.out.println("Earliest birth:" + earliestDateOfBirth);
			c.setTime(today);
			//CHANGE EARNINGS YEAR HERE
			//change this to -1 from -2 when current earnings year changes from 2014 to 2015
			c.add(Calendar.YEAR, -2);
			endLastYear = new SimpleDateFormat("yyyy-MM-dd").parse(c
					.get(Calendar.YEAR) + "-12-31");
			System.out.println(c
					.get(Calendar.YEAR) + "-12-31");
			c.add(Calendar.YEAR, -1);
			System.out.println(c
					.get(Calendar.YEAR) + "-12-31");
			startLastYear = new SimpleDateFormat("yyyy-MM-dd").parse(c
					.get(Calendar.YEAR) + "-01-01");
			endDateFor2014 = new DateTime(c.get(Calendar.YEAR), 12, 31, 0, 0).toDate();

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (TeEmployees employee : employees) {

			System.out.println(today
					+ " "
					+ employee.getEmployeesDateEntered()
					+ " "
					+ DateTimeComparator.getDateOnlyInstance().compare(
							employee.getEmployeesDateEntered(), today));
			List<TeEmployentHistory> histories = new ArrayList<>();
			
			for (TeEmployentHistory history : employee
					.getTeEmployentHistories()) {
				//System.out.println(history.getTeTrainers().getTrainerId() + " " + history.getTeEmployees().getEmployeesEmployeeId());
				if (history.getTeTrainers().getTrainerId() == trainerId) {
					histories.add(history);
				}

			}

			System.out.println("Single History Make");
			TeEmployentHistory continuoushistory = createSingleEmploymentHistoryFromList(histories);
			// if the employee is not over 65 years old then check to see if
			// he/she is a current employee
			
			//get employeeLastUpdated date and set into "employee" object
			employee.setEmployeesLastUpdated(continuoushistory.getEmployeeLastUpdated());

			System.out.println("Categories" + employee.getEmployeesDateOfBirth());
			if (employee.getEmployeesDateOfBirth().after(earliestDateOfBirth)) {

				// if the current employment does not have an end date
				if (continuoushistory.getEhDateTo() == null
						&& DateTimeComparator.getDateOnlyInstance().compare(
								employee.getEmployeesLastUpdated(), today) <= 0
						&& DateTimeComparator.getDateOnlyInstance().compare(
								continuoushistory.getEhDateFrom(), endDateFor2014) < 0 
						&& continuoushistory.isExistsYearRecord()) {
					currentEmployees.add(employee);
				}
				// if the employee left last year add to employees left list
				else if (continuoushistory.getEhDateTo() != null
						&& continuoushistory.getEhDateTo().after(startLastYear) && continuoushistory.isExistsYearRecord()) {
					employeesLeft.add(employee);
				}
				// if the employee date entered on the system is today show as
				// new employee
				else if (DateTimeComparator.getDateOnlyInstance().compare(
						employee.getEmployeesDateEntered(), today) == 0) {
					newEmployees.add(employee);
				}
			}
			
			// add to over 65 list if still working
			else if (continuoushistory.getEhDateTo() == null) {
				employeesOver65.add(employee);
			} else if (continuoushistory.isExistsYearRecord()) {
				employeesOver65.add(employee);
			}
			System.out.println("Categories Done");
		}

	}

	public List<TeEmployees> getEmployeesOver65() {
		return employeesOver65;
	}

	public List<TeEmployees> getEmployeesLeft() {
		return employeesLeft;
	}

	public List<TeEmployees> getNewEmployees() {
		return newEmployees;
	}

	public List<TeEmployees> getCurrentEmployees() {
		return currentEmployees;
	}
}
