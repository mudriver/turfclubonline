package ie.turfclub.service.trainer;

import ie.turfclub.common.bean.SearchByNameTrainerBean;
import ie.turfclub.common.enums.ConfigEnum;
import ie.turfclub.common.enums.RoleEnum;
import ie.turfclub.model.stableStaff.Config;
import ie.turfclub.model.stableStaff.TeAuthorisedReps;
import ie.turfclub.model.stableStaff.TeEmployeeTrainerVerified;
import ie.turfclub.model.stableStaff.TeEmployees;
import ie.turfclub.model.stableStaff.TeEmployentHistory;
import ie.turfclub.model.stableStaff.TeFile;
import ie.turfclub.model.stableStaff.TeTrainers;
import ie.turfclub.model.stableStaff.TeTrainers.VerifiedStatus;
import ie.turfclub.person.model.Person;
import ie.turfclub.trainers.model.savedSearches.TeAuthRepsSavedSearches;
import ie.turfclub.trainers.model.savedSearches.TeBooleanSavedSearch;
import ie.turfclub.trainers.model.savedSearches.TeEmployeesPensionSavedSearch;
import ie.turfclub.trainers.model.savedSearches.TeEmployeesSavedSearch;
import ie.turfclub.trainers.model.savedSearches.TeEnumSavedSearch;
import ie.turfclub.trainers.model.savedSearches.TeOrderByFields;
import ie.turfclub.trainers.model.savedSearches.TeTrainersPensionSavedSearch;
import ie.turfclub.trainers.model.savedSearches.TeTrainersSavedSearch;

import java.awt.Color;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.itextpdf.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

@PropertySource("classpath:ie/turfclub/trainers/resources/config/config.properties")
@Service
@Transactional
public class TrainerServiceImpl implements TrainersService {

	@Autowired
	private SessionFactory sessionFactory;
	@Resource
	private Environment env;
	@Autowired
	private MessageSource messageSource;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private EmployeeService employeeService;
	
	private String PROPERTY_TAX_YEAR;
	private Date minDateFrom;
	private Date maxDateFrom;

	public void initialize() {
		PROPERTY_TAX_YEAR = env.getRequiredProperty("tax_year");
		System.out.println("Create TrainerService");
		System.out.println(PROPERTY_TAX_YEAR);
		try {
			SimpleDateFormat sqlFormat = new SimpleDateFormat("yyyy-MM-dd");

			minDateFrom = sqlFormat.parse((Integer.parseInt(PROPERTY_TAX_YEAR)
					 -1)+ "-12-31");
			
			maxDateFrom = sqlFormat.parse((Integer.parseInt(PROPERTY_TAX_YEAR)
					+1) + "-12-31");
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public HashMap<String, Object> getTrainers(TeTrainersSavedSearch savedSearch) {
		// TODO Auto-generated method stub

		Criteria criteria = getCurrentSession()
				.createCriteria(TeTrainers.class)
				.addOrder(Order.asc("trainerFullName"))
				.setFirstResult(savedSearch.getCurrentRecordStart())
				.setMaxResults(savedSearch.getMaxToShow());

		Criteria criteriaCount = getCurrentSession().createCriteria(
				TeTrainers.class);
		criteriaCount.setProjection(Projections.rowCount());
		Long count = (Long) criteriaCount.uniqueResult();

		@SuppressWarnings("unchecked")
		List<TeTrainers> trainers = (List<TeTrainers>) criteria.list();

		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("data", trainers);
		data.put("max", count);
		for (TeTrainers trainer : trainers) {
			trainer.setCanEdit(true);
			System.out.println(trainer.getTrainerAccountNo());
		}

		return data;
	}

	@Override
	public HashMap<String, Object> getTrainersPension(
			TeTrainersPensionSavedSearch savedSearch) {

		DetachedCriteria criteria = DetachedCriteria.forClass(TeTrainers.class);

		// if the search contains trainers add each to the search
		if (savedSearch.getTrainersSearch().size() > 0) {

			Disjunction disjunction = Restrictions.disjunction();
			for (TeTrainers trainer : savedSearch.getTrainersSearch()) {
				System.out.println("Trainer" + trainer.getTrainerId());
				disjunction.add(Restrictions.and(Restrictions.eq("trainerId",
						trainer.getTrainerId())));

			}
			criteria.add(disjunction);
		}

		if (savedSearch.getReturnCompleteSearch() != null
				&& savedSearch.getReturnCompleteSearch().isInUse()) {
			System.out.println("saved search complete"
					+ savedSearch.getReturnCompleteSearch());
			criteria.add(Restrictions.eq("trainerReturnComplete", savedSearch
					.getReturnCompleteSearch().isSsBoolean()));
		}

		if (savedSearch.getVerified() != null
				&& savedSearch.getVerified().isInUse()) {
			System.out.println(savedSearch.getVerified().getSsEnum());
			VerifiedStatus status = null;
			switch (savedSearch.getVerified().getSsEnum()) {
			case "PENDING":
				status = VerifiedStatus.PENDING;
				break;
			case "NOTVERIFIED":
				status = VerifiedStatus.NOTVERIFIED;
				break;
			case "VERIFIED":
				status = VerifiedStatus.VERIFIED;
				break;
			}
			criteria.add(Restrictions.eq("trainerVerifiedStatus", status));
		}

		if (savedSearch.getDocumentsAttached() != null
				&& savedSearch.getDocumentsAttached().isInUse()) {

			if (savedSearch.getDocumentsAttached().isSsBoolean()) {
				criteria.createAlias("trainerFile", "file",
						JoinType.INNER_JOIN);
				criteria.add(Restrictions.isNotNull("file.id"));

			} else {
				criteria.createAlias("trainerFile", "file",
						JoinType.LEFT_OUTER_JOIN);
				criteria.add(Restrictions.isNull("file.id"));
			}

		}

		// Batch No's for currently licenced trainers
		criteria.add(Restrictions.between("trainerBatchNo", 500, 599));
		criteria.setProjection(Projections.distinct(Projections
				.property("trainerId")));

		Criteria outer = getCurrentSession().createCriteria(TeTrainers.class,
				"trainer");
		// inner join

		outer.add(Subqueries.propertyIn("trainer.trainerId", criteria));
		// by
		// default

		outer.setProjection(Projections.rowCount());
		Long count = (Long) outer.uniqueResult();
		outer.setProjection(null);
		// SET ORDER BY Criteria
		if (savedSearch.getOrderByFields().size() > 0) {
			// using saved search

			SortedSet<Map.Entry<String, TeOrderByFields>> sortedEntries = orderSorted(savedSearch
					.getOrderByFields());

			Iterator<Map.Entry<String, TeOrderByFields>> sortedItr = sortedEntries
					.iterator();
			while (sortedItr.hasNext()) {
				Map.Entry<String, TeOrderByFields> orderBy = sortedItr.next();
				System.out.println("Order by:" + orderBy.getKey() + " "
						+ orderBy.getValue().getFieldOrder());
				switch (orderBy.getKey()) {
				case "Return Complete":
					if (orderBy.getValue().getFieldOrder().equals("DESC")) {
						outer.addOrder(Order.desc("trainerReturnComplete"));
					} else {

						outer.addOrder(Order.asc("trainerReturnComplete"));
					}
					break;
				case "Name":
					System.out.println("NAME SORT");
					if (orderBy.getValue().getFieldOrder().equals("DESC")) {
						outer.addOrder(Order.desc("trainerFullName"));
					} else {
						outer.addOrder(Order.asc("trainerFullName"));
					}
					break;
				case "Verfied":
					if (orderBy.getValue().getFieldOrder().equals("DESC")) {
						outer.addOrder(Order.desc("trainerVerified"));
					} else {
						outer.addOrder(Order.asc("trainerVerified"));
					}
					break;
				case "Date Completed":
					if (orderBy.getValue().getFieldOrder().equals("DESC")) {
						outer.addOrder(Order.desc("trainerDateCompleted"));
					} else {
						outer.addOrder(Order.asc("trainerDateCompleted"));
					}
					break;
				}

			}

		}
		else{
			outer.addOrder(Order.asc("trainerFullName"));
		}
		outer.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		outer.setFirstResult(savedSearch.getCurrentRecordStart())

		.setMaxResults(savedSearch.getMaxToShow());
		System.out.println(savedSearch.getCurrentRecordStart() + " "
				+ savedSearch.getMaxToShow());
		@SuppressWarnings("unchecked")
		List<TeTrainers> trainers = (List<TeTrainers>) outer.list();

		for (TeTrainers trainer : trainers) {

			trainer.setCanEdit(true);

			/*if (trainer.getTrainerFile().size() > 0) {
				trainer.setHasDocuments(true);
			}*/

		}

		System.out.println(count);
		HashMap<String, Object> data = new HashMap<String, Object>();

		data.put("data", trainers);
		data.put("max", count);

		return data;
	}

	@Override
	public List<HashMap<String, String>> getTrainers(String chars) {
		chars = chars.toLowerCase();
		System.out.println(chars);
		Query query = getCurrentSession()
				.createQuery(
						"SELECT trainerId,trainerFullName from TeTrainers WHERE trainerFullName Like '%"
								+ chars + "%'");
		List<Object[]> list = (List<Object[]>) query.list();

		return convertObjectListToSelect2List(list, false);
	}


	@SuppressWarnings("unchecked")
	@Override
	public HashMap<String, Object> getEmployees(Integer trainerId,
			TeEmployeesSavedSearch savedSearch) {
		List<TeEmployees> employees = null;
		DetachedCriteria c = DetachedCriteria.forClass(TeEmployees.class,
				"employee");

		// if the search is specific to a single trainer only get employees
		// associated
		if (trainerId != 0) {

			c.createAlias("employee.teEmployentHistories",
					"teEmployentHistories"); // inner join
			// by
			// default
			c.createAlias("teEmployentHistories.teTrainers", "teTrainers");

			c.add(Restrictions.eq("teTrainers.trainerId", trainerId));

		}

		if (savedSearch.getEmployeesSearch().size() > 0) {

			Disjunction disjunction = Restrictions.disjunction();
			for (TeEmployees employee : savedSearch.getEmployeesSearch()) {
				System.out.println(employee.getEmployeesEmployeeId());
				disjunction.add(Restrictions.and(Restrictions.eq(
						"employee.employeesEmployeeId",
						employee.getEmployeesEmployeeId())));

			}
			c.add(disjunction);
		}

		// SET ORDER BY Criteria
		if (savedSearch.getOrderByFields().size() > 0) {
			// using saved search

			SortedSet<Map.Entry<String, TeOrderByFields>> sortedEntries = orderSorted(savedSearch
					.getOrderByFields());

			Iterator<Map.Entry<String, TeOrderByFields>> sortedItr = sortedEntries
					.iterator();
			while (sortedItr.hasNext()) {
				Map.Entry<String, TeOrderByFields> orderBy = sortedItr.next();
				switch (orderBy.getKey()) {
				case "Name":
					if (orderBy.getValue().getFieldOrder().equals("DESC")) {
						c.addOrder(Order.desc("employee.employeesFullName"));
					} else {
						c.addOrder(Order.asc("employee.employeesFullName"));
					}
					break;
				case "Address":
					if (orderBy.getValue().getFieldOrder().equals("DESC")) {
						c.addOrder(Order.desc("employee.employeesAddress1"));
					} else {
						c.addOrder(Order.asc("employee.employeesAddress1"));
					}
					break;
				}

			}

		} else {
			// otherwise order by name asc
			c.addOrder(Order.asc("employee.employeesFullName"));
		}

		// c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		c.setProjection(Projections.distinct(Projections.id()));

		Criteria outer = getCurrentSession().createCriteria(TeEmployees.class,
				"employ");
		outer.add(Subqueries.propertyIn("employ.employeesEmployeeId", c));
		outer.setFirstResult(savedSearch.getCurrentRecordStart());
		outer.setMaxResults(savedSearch.getMaxToShow());

		employees = (List<TeEmployees>) outer.list();
		for (TeEmployees employ : employees) {
			System.out.println(employ.getEmployeesFullName());
		}
		outer.setProjection(Projections
				.countDistinct("employ.employeesEmployeeId"));
		Long count = (Long) outer.uniqueResult();

		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("data", employees);
		data.put("max", count);
		/*
		 * for(TeEmployees employee : employees){ employee.setCanEdit(true);
		 * 
		 * }
		 */

		return data;
	}

	@Override
	public List<HashMap<String, String>> getEmployees(String chars) {
		chars = chars.toLowerCase();
		System.out.println(chars);
		Query query = getCurrentSession()
				.createQuery(
						"SELECT employeesEmployeeId, employeesFullName from TeEmployees WHERE employeesFullName Like '%"
								+ chars + "%'");
		List<Object[]> list = (List<Object[]>) query.list();

		return convertObjectListToSelect2List(list, false);
	}

	@Override
	public TeEmployees getEmployee(Integer id) {
		return (TeEmployees) getCurrentSession().get(TeEmployees.class, id);
	}

	@Override
	public HashMap<String, Object> getAuthorisedReps(Integer trainerId,
			TeAuthRepsSavedSearches savedSearch) {

		System.out.println("Get Reps" + trainerId);
		Criteria c = getCurrentSession().createCriteria(TeAuthorisedReps.class,
				"reps");
		c.createAlias("reps.authrepsTrainerId", "teTrainers");
		if (trainerId != 0) {

			c.add(Restrictions.eq("teTrainers.trainerId", trainerId));

		}

		// SET ORDER BY Criteria
		if (savedSearch.getOrderByFields().size() > 0) {
			// using saved search

			SortedSet<Map.Entry<String, TeOrderByFields>> sortedEntries = orderSorted(savedSearch
					.getOrderByFields());

			Iterator<Map.Entry<String, TeOrderByFields>> sortedItr = sortedEntries
					.iterator();
			while (sortedItr.hasNext()) {
				Map.Entry<String, TeOrderByFields> orderBy = sortedItr.next();
				switch (orderBy.getKey()) {
				case "Name":
					if (orderBy.getValue().getFieldOrder().equals("DESC")) {
						c.addOrder(Order.desc("employee.employeesFullName"));
					} else {
						c.addOrder(Order.asc("employee.employeesFullName"));
					}
					break;
				case "Address":
					if (orderBy.getValue().getFieldOrder().equals("DESC")) {
						c.addOrder(Order.desc("employee.employeesAddress1"));
					} else {
						c.addOrder(Order.asc("employee.employeesAddress1"));
					}
					break;
				}

			}

		} else {
			// otherwise order by name asc
			c.addOrder(Order.asc("reps.authrepsName"));
		}

		c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		c.setFirstResult(savedSearch.getCurrentRecordStart());
		c.setMaxResults(savedSearch.getMaxToShow());

		List<TeAuthorisedReps> reps = (List<TeAuthorisedReps>) c.list();

		c.setProjection(Projections.countDistinct("reps.authrepsId"));
		Long count = (Long) c.uniqueResult();

		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("data", reps);
		data.put("max", count);
		/*
		 * for(TeEmployees employee : employees){ employee.setCanEdit(true);
		 * 
		 * }
		 */

		return data;
	}

	@Override
	public List<HashMap<String, String>> getAuthorisedReps(String chars) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TeAuthorisedReps getAuthorisedRep(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> getAuthRepSavedSearches(int userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveAuthRepSavedSearches(TeAuthRepsSavedSearches savedsearch) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Object> getEmployeesSavedSearches(int userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveEmployeeSavedSearches(TeEmployeesSavedSearch savedsearch) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Object> getTrainersSavedSearches(int userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Object> getTrainersPensionSavedSearches(int userId) {
		Session session = getCurrentSession();
		System.out.println("Savedsearch" + userId);
		Query query = session
				.createQuery("from TeTrainersPensionSavedSearch s where s.savedSearchUserId="
						+ userId);

		List<Object> list = query.list();
		
		
		System.out.println("Savedsearch" + list.size());
		return list;
	}


	@Override
	public List<TeFile> getTrainersPensionFileNames(Integer trainerId) {
		Criteria c = getCurrentSession().createCriteria(TeFile.class, "file");
		//c.createAlias("file.fileUserId", "trainer"); // inner join by default
		//c.add(Restrictions.eq("trainer.trainerId", trainerId));
		List<TeFile> files = c.list();

		return files;
	}

	// converts a list of objects into a select to list of hashmaps with keys id
	// and value
	private ArrayList<HashMap<String, String>> convertObjectListToSelect2List(
			List<Object[]> objectList, boolean linkedValue) {
		ArrayList<HashMap<String, String>> convertedList = new ArrayList<>();
		HashMap<String, String> map;
		for (Object[] objArray : objectList) {
			String id = "";
			String value = "";
			String linkedId = "";
			map = new HashMap<>();
			for (int i = 0; i < objArray.length; i++) {
				if (i == 0) {
					id = objArray[i].toString();
				} else if (i == (objArray.length - 1) && linkedValue) {
					linkedId = linkedId.concat(objArray[i].toString());
				} else {
					System.out.println(objArray[i].toString());
					value = value.concat(objArray[i].toString() + " ");

				}

			}
			System.out.println("Array:" + id + " " + value + " " + "Link"
					+ linkedId);
			map.put("id", id);
			map.put("value", value);
			map.put("linkedId", linkedId);
			convertedList.add(map);
		}
		return convertedList;
	}

	private static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<String, TeOrderByFields>> orderSorted(
			Map<String, TeOrderByFields> map) {
		SortedSet<Map.Entry<String, TeOrderByFields>> sortedEntries = new TreeSet<Map.Entry<String, TeOrderByFields>>(
				new Comparator<Map.Entry<String, TeOrderByFields>>() {
					@Override
					public int compare(Map.Entry<String, TeOrderByFields> e1,
							Map.Entry<String, TeOrderByFields> e2) {

						return e1.getValue().getFieldPriority()
								.compareTo(e2.getValue().getFieldPriority());
					}
				});
		sortedEntries.addAll(map.entrySet());
		return sortedEntries;
	}

	@Override
	@Transactional(readOnly = true)
	public HashMap<String, Object> getEmployeesPension(Integer trainerId,
			TeEmployeesPensionSavedSearch savedSearch) {
		List<TeEmployees> employees = null;
		// only get earnings and pps numbers from previous year
		Date date = new Date(); // your date
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		//CHANGE EARNINGS YEAR HERE
		//comment out line below to change to current year  
		cal.add(Calendar.YEAR, -1);
		int year = cal.get(Calendar.YEAR);
		Date startDate = null;
		Date endDate = null;
		try {
			startDate = new SimpleDateFormat("yyyy-MM-dd").parse((year-2)
					+ "-12-31");
			endDate = new SimpleDateFormat("yyyy-MM-dd").parse(year + "-01-01");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DetachedCriteria c = DetachedCriteria.forClass(TeEmployees.class,
				"employee");
		

		// if the search is specific to a single trainer only get employees
		// associated
		if (trainerId != 0) {

			c.createAlias("employee.teEmployentHistories",
					"teEmployentHistories", JoinType.LEFT_OUTER_JOIN); // inner
																		// join
			// by
			// default
			c.createAlias("teEmployentHistories.teTrainers", "teTrainers");

			c.add(Restrictions.eq("teTrainers.trainerId", trainerId));
			c.add(Restrictions.ge("teEmployentHistories.ehDateFrom", startDate));
		}

		/*
		 * if (savedSearch.getEmployeesSearch().size() > 0) {
		 * 
		 * Disjunction disjunction = Restrictions.disjunction(); for
		 * (TeEmployees employee : savedSearch.getEmployeesSearch()) {
		 * System.out.println(employee.getEmployeesEmployeeId());
		 * disjunction.add(Restrictions.and(Restrictions.eq(
		 * "employee.employeesEmployeeId", employee.getEmployeesEmployeeId())));
		 * 
		 * } c.add(disjunction); }
		 */
		// SET ORDER BY Criteria
		if (savedSearch.getOrderByFields().size() > 0) {
			// using saved search

			SortedSet<Map.Entry<String, TeOrderByFields>> sortedEntries = orderSorted(savedSearch
					.getOrderByFields());

			Iterator<Map.Entry<String, TeOrderByFields>> sortedItr = sortedEntries
					.iterator();
			while (sortedItr.hasNext()) {
				Map.Entry<String, TeOrderByFields> orderBy = sortedItr.next();
				switch (orderBy.getKey()) {
				case "Name":
					if (orderBy.getValue().getFieldOrder().equals("DESC")) {
						c.addOrder(Order.desc("employee.employeesFullName"));
					} else {
						c.addOrder(Order.asc("employee.employeesFullName"));
					}
					break;

				}

			}

		} else {
			// otherwise order by name asc
			c.addOrder(Order.asc("employee.employeesFullName"));
		}

		
		c.setProjection(Projections.distinct(Projections.id()));

		Criteria outer = getCurrentSession().createCriteria(TeEmployees.class,
				"employ" );
		
		outer.add(Subqueries.propertyIn("employ.employeesEmployeeId", c));
		
		outer.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		
		
		
		outer.setProjection(Projections
				.countDistinct("employ.employeesEmployeeId"));
		Long count = (Long) outer.uniqueResult();
		outer.setProjection(null);
		outer.setFirstResult(savedSearch.getCurrentRecordStart());
		outer.setMaxResults(savedSearch.getMaxToShow());
		outer.addOrder(Order.asc("employ.employeesFullName"));
		employees = (List<TeEmployees>) outer.list();
		
		System.out.println("SEARCH START " + savedSearch.getCurrentRecordStart());
		System.out.println("MAX " + count);
		// c.setMaxResults(savedSearch.getMaxToShow());



		
		for (TeEmployees employ : employees) {

			//System.out.println("GET HISTORIES " + minDateFrom + " " + maxDateFrom);
			List<TeEmployentHistory> histories  = this.getEmploymentHistories(trainerId, employ.getEmployeesEmployeeId(), minDateFrom, maxDateFrom);
			if(histories.size() > 0){
				//System.out.println(histories.size());
				
				for (TeEmployentHistory history : histories) {

					//System.out.println(history.getEhDateFrom());
						if (history.getEhEarnings() != null) {
							employ.setEmployeesEarnings("�"
									+ String.valueOf(history.getEhEarnings()) );
							if(employ.getEmployeesEarnings() .endsWith(".0")){
								employ.setEmployeesEarnings(employ.getEmployeesEarnings().replaceAll("\\.0", "\\.00"));
							}
							else if(employ.getEmployeesEarnings() .endsWith(".*")){
								employ.setEmployeesEarnings(employ.getEmployeesEarnings() + "0");
							}
							if(history.getEhPpsNumber() != null){
								employ.setEmployeesPps(history.getEhPpsNumber());
							}
							
							
						} else {
							if(employ.getEmployeesEarnings() == null){
								employ.setEmployeesEarnings("�" + "0.00");
							}
							
							if(history.getEhPpsNumber() != null && employ.getEmployeesPps() == null){
								employ.setEmployeesPps(history.getEhPpsNumber());
							}
							else if(employ.getEmployeesPps() == null){
								employ.setEmployeesPps("N/A");
							}
							
							
						}

						//System.out.println(history.getEhDateFrom() + "    "+ startDate + "   " + endDate);
						if(history.getEhDateFrom().after(startDate) && history.getEhDateFrom().before(endDate)){
							employ.setEmployeeWorkedWithTrainerInTaxYear(true);
						}
						if(history.isEhVerified()){
							employ.setEmployeeVerified(true);
						}
						
					

				}
			}
			else{

				outer = getCurrentSession().createCriteria(
						TeEmployeeTrainerVerified.class, "verified");
				outer.createAlias("verified.trainerId", "trainer");
				outer.createAlias("verified.employeeId", "employee");
				outer.add(Restrictions.eq("trainer.trainerId", trainerId));
				outer.add(Restrictions.eq("employee.employeesEmployeeId",
						employ.getEmployeesEmployeeId()));
				List<TeEmployeeTrainerVerified> verifiedEmployees = (List<TeEmployeeTrainerVerified>) outer
						.list();
				if (verifiedEmployees.size() > 0) {
					for (TeEmployeeTrainerVerified verified : verifiedEmployees) {
						if (verified.isVerified()) {
							employ.setEmployeeVerified(verified.isVerified());
						}

					}
				} else {
					employ.setEmployeeVerified(false);
				}

				employ.setEmployeeWorkedWithTrainerInTaxYear(false);
				employ.setEmployeesEarnings("N/A");
				employ.setEmployeesPps("N/A");
			}

		}

		/*
		if(employees.isEmpty() || employees == null){
			TeEmployees employee = new TeEmployees();
			System.out.println("This trainer has no employees");
			employee.setEmployeesFirstname("This trainer has no employees");
			employee.setEmployeeVerified(false);
			employees.add(employee);
		}*/
		
		
		System.out.println("Return Employees");
		//Long count = (long) employees.size();

		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("data", employees);
		data.put("max", count);
		/*
		 * for(TeEmployees employee : employees){ employee.setCanEdit(true);
		 * 
		 * }
		 */
		System.out.println("Return Employees");
		return data;
	}


	@Override
	@Transactional(readOnly = true)
	public List<TeEmployees> getEmployeesPension(Integer trainerId) {
		List<TeEmployees> employees = null;
		// only get earnings and pps numbers from previous year
		Date date = new Date(); // your date
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		
		
		int year = cal.get(Calendar.YEAR);
		//CHANGE EARNINGS YEAR HERE
				//change line below to year = year -2 for current year  
				year = year-3;
		Date startDate = null;
		Date endDate = null;
		try {
			startDate = new SimpleDateFormat("yyyy-MM-dd").parse(year
					+ "-12-31");
			endDate = new SimpleDateFormat("yyyy-MM-dd").parse(year + "-12-31");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("GET EMPLOYEES " + startDate);
		DetachedCriteria c = DetachedCriteria.forClass(TeEmployees.class, "employee");
		

		// if the search is specific to a single trainer only get employees
		// associated
		if (trainerId != 0) {

			c.createAlias("employee.teEmployentHistories",
					"teEmployentHistories"); // inner join
			// by
			// default
			c.createAlias("teEmployentHistories.teTrainers", "teTrainers");

			c.add(Restrictions.eq("teTrainers.trainerId", trainerId));
			c.add(Restrictions.ge("teEmployentHistories.ehDateFrom", startDate));
			//c.addOrder(Order.desc("teEmployentHistories.ehDateFrom"));
		}

	    
		c.addOrder(Order.asc("employee.employeesFullName"));

		//c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		c.setProjection(Projections.distinct(Projections.id()));

		Criteria outer = getCurrentSession().createCriteria(TeEmployees.class,
				"employ");
		outer.add(Subqueries.propertyIn("employ.employeesEmployeeId", c));
		
		employees = (List<TeEmployees>) outer.list();
		return employees;
	}

	@Override
	public List<TeEmployentHistory> getEmploymentHistories(Integer trainerId,
			Integer employeeId, Date yearFrom, Date yearTo) {
		Criteria c = getCurrentSession().createCriteria(
				TeEmployentHistory.class, "histories");
		c.createAlias("histories.teTrainers", "trainer");
		c.createAlias("histories.teEmployees", "employee");
		c.add(Restrictions.eq("employee.employeesEmployeeId", employeeId));
		c.add(Restrictions.eq("trainer.trainerId", trainerId));
		System.out.println(yearFrom + " " + yearTo);
		c.add(Restrictions.gt("ehDateFrom", yearFrom));
		c.add(Restrictions.lt("ehDateFrom", yearTo));
		return c.list();
	}

	@Override
	public List<TeEmployeeTrainerVerified> getEmployeeTrainerVerified(
			Integer trainerId, Integer employeeId) {
		Criteria c = getCurrentSession().createCriteria(
				TeEmployeeTrainerVerified.class, "verified");
		c.createAlias("verified.trainerId", "trainer");
		c.createAlias("verified.employeeId", "employee");
		c.add(Restrictions.eq("trainer.trainerId", trainerId));
		c.add(Restrictions.eq("employee.employeesEmployeeId", employeeId));

		return c.list();
	}

	@Override
	public void updateTrainer(TeTrainers trainer) {
		getCurrentSession().saveOrUpdate(trainer);
		
	}

	@Override
	public List<HashMap<String, Object>> getAllTrainers() {
		
		Criteria criteria = getCurrentSession().createCriteria(TeTrainers.class);
		List<TeTrainers> records = criteria.list();
		List<HashMap<String, Object>> results = new ArrayList<HashMap<String,Object>>();
		if(records != null) {
			for (TeTrainers trainer : records) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("name", trainer.getTrainerFullName());
				map.put("id", trainer.getTrainerId());
				results.add(map);
			}
		}
		return results;
	}
	
	@Override
	public Object getVerifiedStatus() {
		
		Gson gson = new Gson();
		List<Map<String,Object>> statuses = new ArrayList<Map<String,Object>>();
		VerifiedStatus verifiedStatuses = new TeTrainers().getTrainerVerifiedStatus();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", verifiedStatuses.NOTVERIFIED);
		map.put("text", verifiedStatuses.NOTVERIFIED);
		statuses.add(map);
		
		map = new HashMap<String, Object>();
		map.put("id", verifiedStatuses.PENDING);
		map.put("text", verifiedStatuses.PENDING);
		statuses.add(map);
		
		map = new HashMap<String, Object>();
		map.put("id", verifiedStatuses.RESET);
		map.put("text", verifiedStatuses.RESET);
		statuses.add(map);
		
		map = new HashMap<String, Object>();
		map.put("id", verifiedStatuses.VERIFIED);
		map.put("text", verifiedStatuses.VERIFIED);
		statuses.add(map);
		
		String json = gson.toJson(statuses);
		return json;
	}
	
	@Override
	public String saveOrUpdate(TeTrainers trainer) throws SQLException {
		
		trainer.setTrainerDateRequested(new Date());
		trainer.setPwd(passwordEncoder.encode("test"));
		getCurrentSession().saveOrUpdate(trainer);
		Person person = createPerson(trainer);
		
		return null;
	}

	private Person createPerson(TeTrainers trainer) {
		
		Person person = new Person();
		person.setRefId(trainer.getTrainerId());
		person.setSurname(trainer.getTrainerSurname());
		person.setFirstname(trainer.getTrainerFirstName());
		person.setDateOfBirth(trainer.getTrainerDateOfBirth());
		person.setRequestDate(trainer.getTrainerDateRequested());
		person.setDateEntered(trainer.getTrainerTimeEntered());
		person.setAddress1(trainer.getTrainerAddress1());
		person.setAddress2(trainer.getTrainerAddress2());
		person.setAddress3(trainer.getTrainerAddress3());
		person.setPhoneNo(trainer.getTrainerHomePhone());
		person.setMobileNo(trainer.getTrainerMobilePhone());
		person.setEmail(trainer.getTrainerEmail());
		person.setComments(trainer.getTrainerNotes());
		person.setRoleId(RoleEnum.TRAINER.getId());
		person.setTitle(trainer.getTitle());
		person.setSex(trainer.getSex());
		person.setNationality(trainer.getNationality());
		person.setMaritalStatus(trainer.getMaritalStatus());
		person.setSpouseName(trainer.getSpouseName());
		person.setCounty(trainer.getCounty());
		person.setCountry(trainer.getCountry());
		person.setPostCode(trainer.getPostCode());
		person.setAccountNumber(trainer.getTrainerAccountNo());
		return person;
	}
	
	
	@Override
	public String getCSVStringForTrainerEmployees(Integer id, String type) {
		
		List<TeEmployentHistory> records = employeeService.getListOfTrainersEmployees(id, type);
		String csvString = "";
		csvString += "Card Type,Card Number,Surname,Firstname,From Year,To Year,Num\n";
		for (TeEmployentHistory record : records) {
			csvString += record.getTeEmployees().getTeCard().getCardsCardType()+",";
			csvString += record.getTeEmployees().getTeCard().getCardsCardNumber()+",";
			csvString += record.getTeEmployees().getEmployeesSurname()+",";
			csvString += record.getTeEmployees().getEmployeesFirstname()+",";
			csvString += record.getEhDateFrom()+",";
			if(record.getEhDateTo() != null)
				csvString += record.getEhDateTo()+",";
			else
				csvString += ",";
			if(record.getEhHoursWorked() != null)
				csvString += record.getEhHoursWorked()+"\n";
			else
				csvString += "\n";
		}
		return csvString;
	}
	
	@Override
	public PdfPTable createPDFDocumentWithDetails(Integer id, String type) {
		
		PdfPTable pdfPTable = new PdfPTable(8);
		int year = Integer.parseInt(this.getYearForTrainerEmployeeOnline());
		int prevYear = year-1;
		try {
			Criteria criteria = getCurrentSession().createCriteria(TeTrainers.class);
			criteria.add(Restrictions.eq("trainerId", id));
			List<TeTrainers> trainers = criteria.list();
			TeTrainers trainer = (trainers != null && trainers.size() > 0) ? trainers.get(0) : null;
			pdfPTable.setWidthPercentage(100);
			
			Font bold = FontFactory.getFont("Bold", 12, Color.BLACK);
			PdfPCell cell = new PdfPCell(new Phrase("The Employees of "+trainer.getTrainerFullName()+"  Acc No. "+trainer.getTrainerAccountNo(), bold));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setBorder(0);
			cell.setColspan(8);
			pdfPTable.addCell(cell);
			
			cell = new PdfPCell(new Phrase(" ", bold));
			cell.setColspan(8);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			cell = new PdfPCell(new Phrase(" ", bold));
			cell.setColspan(8);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			
			bold = FontFactory.getFont("Bold", 8, Color.BLACK);
			cell = new PdfPCell(new Phrase("Hours Worked Weekly", bold));
			cell.setBorder(0);
			cell.setPadding(5);
			pdfPTable.addCell(cell);
			cell = new PdfPCell(new Phrase("Card No", bold));
			cell.setBorder(0);
			cell.setPadding(5);
			pdfPTable.addCell(cell);
			cell = new PdfPCell(new Phrase("Name", bold));
			cell.setBorder(0);
			cell.setPadding(5);
			pdfPTable.addCell(cell);
			cell = new PdfPCell(new Phrase("Date Of Birth", bold));
			cell.setBorder(0);
			cell.setPadding(5);
			pdfPTable.addCell(cell);
			cell = new PdfPCell(new Phrase("Marital Status", bold));
			cell.setBorder(0);
			cell.setPadding(5);
			pdfPTable.addCell(cell);
			cell = new PdfPCell(new Phrase("Spouse's Name (if applicable) ", bold));
			cell.setBorder(0);
			cell.setPadding(5);
			pdfPTable.addCell(cell);
			cell = new PdfPCell(new Phrase("Category Of Employment", bold));
			cell.setBorder(0);
			cell.setPadding(5);
			pdfPTable.addCell(cell);
			cell = new PdfPCell(new Phrase("Date Left (if applicable) ", bold));
			cell.setBorder(0);
			cell.setPadding(5);
			pdfPTable.addCell(cell);
			
			/*List<TeEmployentHistory> records = employeeService.getListOfTrainersEmployees(id, type);
			HashMap<String, Object> map = new HashMap<String, Object>();
			
			if(records != null && records.size() > 0) {
				Integer currEmp = records.get(0).getTeEmployees().getEmployeesEmployeeId();
				Integer nextEmp = records.get(0).getTeEmployees().getEmployeesEmployeeId();
				for(int i=0; i < records.size(); i++) {
					if(currEmp != nextEmp) {
						
					}
					
				}
			}*/
			
			List<TeEmployentHistory> records = null;
			if(type.equalsIgnoreCase("all")) {
				criteria = sessionFactory.getCurrentSession().createCriteria(TeEmployentHistory.class);
				criteria.add(Restrictions.eq("teTrainers.trainerId", id));
				//criteria.add(Restrictions.eq("ehDateTo", null));
				criteria.add(Restrictions.isNull("ehDateTo"));
				records = criteria.list();
			} else {
				criteria = sessionFactory.getCurrentSession().createCriteria(TeEmployentHistory.class);
				criteria.add(Restrictions.eq("teTrainers.trainerId", id));
				criteria.add(Restrictions.isNull("ehDateTo"));
				DateTime date = new DateTime();
				Date today = new Date();
				Date firstDay = date.dayOfYear().withMinimumValue().toDate();
				criteria.add(Restrictions.between("ehDateFrom", firstDay, today));
				records = criteria.list();
			}
			
			List<TeEmployentHistory> moreThan8HoursRecords = new ArrayList<TeEmployentHistory>();
			List<TeEmployentHistory> lessThan8HoursRecords = new ArrayList<TeEmployentHistory>();
			if(records != null && records.size() > 0) {
				Integer currEmp = 0;
				Integer prevEmp = records.get(0).getTeEmployees().getEmployeesEmployeeId();
				for (int i = 0; i < records.size(); i++) {
					TeEmployentHistory history = records.get(i);
					currEmp = records.get(i).getTeEmployees().getEmployeesEmployeeId();
					if(currEmp != prevEmp || i == 0) {
						if(history.getEhHoursWorked() != null) {
							moreThan8HoursRecords.add(history);
						} else {
							lessThan8HoursRecords.add(history);
						}
					}
					prevEmp = records.get(i).getTeEmployees().getEmployeesEmployeeId();
				}
			}
			
			if(moreThan8HoursRecords != null && moreThan8HoursRecords.size() > 0) {
				for (int i = 0; i < moreThan8HoursRecords.size(); i++) {
					TeEmployentHistory history = moreThan8HoursRecords.get(i);
					cell = new PdfPCell(new Phrase("8 hours or Over", bold));
					cell.setPadding(5);
					pdfPTable.addCell(cell);
					cell = new PdfPCell(new Phrase(history.getTeEmployees().getTeCard().getCardsCardNumber(), bold));
					cell.setPadding(5);
					pdfPTable.addCell(cell);
					cell = new PdfPCell(new Phrase(history.getTeEmployees().getEmployeesFullName(), bold));
					cell.setPadding(5);
					pdfPTable.addCell(cell);
					SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
					cell = new PdfPCell(new Phrase(formatter.format(history.getTeEmployees().getEmployeesDateOfBirth()), bold));
					cell.setPadding(5);
					pdfPTable.addCell(cell);
					String maritalStatus = history.getTeEmployees().getEmployeesMaritalStatus();
					maritalStatus = maritalStatus != null ? maritalStatus : "";
					cell = new PdfPCell(new Phrase(maritalStatus, bold));
					cell.setPadding(5);
					pdfPTable.addCell(cell);
					String spouseName = history.getTeEmployees().getEmployeesSpouseName();
					spouseName = spouseName != null ? spouseName : "";
					cell = new PdfPCell(new Phrase(spouseName+"", bold));
					cell.setPadding(5);
					pdfPTable.addCell(cell);
					cell = new PdfPCell(new Phrase(history.getTeEmployees().getEmployeeCategoryOfEmployment(), bold));
					cell.setPadding(5);
					pdfPTable.addCell(cell);
					cell = new PdfPCell(new Phrase("", bold));
					cell.setPadding(5);
					pdfPTable.addCell(cell);
				}
			}
			
			cell = new PdfPCell(new Phrase(" ", bold));
			cell.setColspan(8);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			cell = new PdfPCell(new Phrase(" ", bold));
			cell.setColspan(8);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			
			cell = new PdfPCell(new Phrase(" * Please indicate from any of the list of examples below and insert "
					+ " under \"Category of Employment \". (This is for information and statistical purposes only) ", bold));
			cell.setColspan(8);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			
			cell = new PdfPCell(new Phrase(" ", bold));
			cell.setColspan(8);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			cell = new PdfPCell(new Phrase(" ", bold));
			cell.setColspan(8);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			
			cell = new PdfPCell(new Phrase("Assistant Trainer ", bold));
			cell.setColspan(2);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			cell = new PdfPCell(new Phrase("Stable Lad ", bold));
			cell.setColspan(2);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			cell = new PdfPCell(new Phrase("Employed Horse Box Driver ", bold));
			cell.setColspan(2);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			cell = new PdfPCell(new Phrase("Feedman ", bold));
			cell.setColspan(2);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			
			cell = new PdfPCell(new Phrase("Head Lad ", bold));
			cell.setColspan(2);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			cell = new PdfPCell(new Phrase("Stable Girl ", bold));
			cell.setColspan(2);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			cell = new PdfPCell(new Phrase("Trainers Secretary ", bold));
			cell.setColspan(2);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			cell = new PdfPCell(new Phrase("Work Rider ", bold));
			cell.setColspan(2);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			
			cell = new PdfPCell(new Phrase("Travelling Head Lad ", bold));
			cell.setColspan(2);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			cell = new PdfPCell(new Phrase("Apprentice Jockey ", bold));
			cell.setColspan(2);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			cell = new PdfPCell(new Phrase("Yardman ", bold));
			cell.setColspan(2);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			cell = new PdfPCell(new Phrase("Other Duty (Please specify) ", bold));
			cell.setColspan(2);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			
			cell = new PdfPCell(new Phrase("Race Day Help", bold));
			cell.setColspan(2);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			cell = new PdfPCell(new Phrase("Conditional Jockey ", bold));
			cell.setColspan(2);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			cell = new PdfPCell(new Phrase("Black Smith ", bold));
			cell.setColspan(2);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			cell = new PdfPCell(new Phrase(" ", bold));
			cell.setColspan(2);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			
			cell = new PdfPCell(new Phrase(" ", bold));
			cell.setColspan(8);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			
			cell = new PdfPCell(new Phrase("This is the list of employees to be renewed for "+prevYear+"/"+year+" ", bold));
			cell.setColspan(8);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			
			cell = new PdfPCell(new Phrase(" ", bold));
			cell.setColspan(8);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			
			cell = new PdfPCell(new Phrase("Signed : ___________________________________ ", bold));
			cell.setColspan(8);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			
			for(int i = 0; i < 16; i++) {
				cell = new PdfPCell(new Phrase(" ", bold));
				cell.setColspan(8);
				cell.setBorder(0);
				cell.setPadding(10);
				pdfPTable.addCell(cell);
			}
			
			bold = FontFactory.getFont("Bold", 12, Color.BLACK);
			cell = new PdfPCell(new Phrase("The Employees of "+trainer.getTrainerFullName()+"  Acc No. "+trainer.getTrainerAccountNo(), bold));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setBorder(0);
			cell.setColspan(8);
			pdfPTable.addCell(cell);
			
			cell = new PdfPCell(new Phrase(" ", bold));
			cell.setColspan(8);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			cell = new PdfPCell(new Phrase(" ", bold));
			cell.setColspan(8);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			
			bold = FontFactory.getFont("Bold", 8, Color.BLACK);
			cell = new PdfPCell(new Phrase("Hours Worked Weekly", bold));
			cell.setBorder(0);
			cell.setPadding(5);
			pdfPTable.addCell(cell);
			cell = new PdfPCell(new Phrase("Card No", bold));
			cell.setBorder(0);
			cell.setPadding(5);
			pdfPTable.addCell(cell);
			cell = new PdfPCell(new Phrase("Name", bold));
			cell.setBorder(0);
			cell.setPadding(5);
			pdfPTable.addCell(cell);
			cell = new PdfPCell(new Phrase("Date Of Birth", bold));
			cell.setBorder(0);
			cell.setPadding(5);
			pdfPTable.addCell(cell);
			cell = new PdfPCell(new Phrase("Marital Status", bold));
			cell.setBorder(0);
			cell.setPadding(5);
			pdfPTable.addCell(cell);
			cell = new PdfPCell(new Phrase("Spouse's Name (if applicable) ", bold));
			cell.setBorder(0);
			cell.setPadding(5);
			pdfPTable.addCell(cell);
			cell = new PdfPCell(new Phrase("Category Of Employment", bold));
			cell.setBorder(0);
			cell.setPadding(5);
			pdfPTable.addCell(cell);
			cell = new PdfPCell(new Phrase("Date Left (if applicable) ", bold));
			cell.setBorder(0);
			cell.setPadding(5);
			pdfPTable.addCell(cell);
			
			if(lessThan8HoursRecords != null && lessThan8HoursRecords.size() > 0) {
				for (int i = 0; i < lessThan8HoursRecords.size(); i++) {
					TeEmployentHistory history = lessThan8HoursRecords.get(i);
					cell = new PdfPCell(new Phrase("Less than 8 Hours", bold));
					cell.setPadding(5);
					pdfPTable.addCell(cell);
					cell = new PdfPCell(new Phrase(history.getTeEmployees().getTeCard().getCardsCardNumber(), bold));
					cell.setPadding(5);
					pdfPTable.addCell(cell);
					cell = new PdfPCell(new Phrase(history.getTeEmployees().getEmployeesFullName(), bold));
					cell.setPadding(5);
					pdfPTable.addCell(cell);
					SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
					cell = new PdfPCell(new Phrase(formatter.format(history.getTeEmployees().getEmployeesDateOfBirth()), bold));
					cell.setPadding(5);
					pdfPTable.addCell(cell);
					String maritalStatus = history.getTeEmployees().getEmployeesMaritalStatus();
					maritalStatus = maritalStatus != null ? maritalStatus : "";
					cell = new PdfPCell(new Phrase(maritalStatus, bold));
					cell.setPadding(5);
					pdfPTable.addCell(cell);
					String spouseName = history.getTeEmployees().getEmployeesSpouseName();
					spouseName = spouseName != null ? spouseName : "";
					cell = new PdfPCell(new Phrase(spouseName+"", bold));
					cell.setPadding(5);
					pdfPTable.addCell(cell);
					cell = new PdfPCell(new Phrase(history.getTeEmployees().getEmployeeCategoryOfEmployment(), bold));
					cell.setPadding(5);
					pdfPTable.addCell(cell);
					cell = new PdfPCell(new Phrase("", bold));
					cell.setPadding(5);
					pdfPTable.addCell(cell);
				}
			}
			
			cell = new PdfPCell(new Phrase(" ", bold));
			cell.setColspan(8);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			cell = new PdfPCell(new Phrase(" ", bold));
			cell.setColspan(8);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			
			cell = new PdfPCell(new Phrase(" * Please indicate from any of the list of examples below and insert "
					+ " under \"Category of Employment \". (This is for information and statistical purposes only) ", bold));
			cell.setColspan(8);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			
			cell = new PdfPCell(new Phrase(" ", bold));
			cell.setColspan(8);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			cell = new PdfPCell(new Phrase(" ", bold));
			cell.setColspan(8);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			
			cell = new PdfPCell(new Phrase("Assistant Trainer ", bold));
			cell.setColspan(2);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			cell = new PdfPCell(new Phrase("Stable Lad ", bold));
			cell.setColspan(2);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			cell = new PdfPCell(new Phrase("Employed Horse Box Driver ", bold));
			cell.setColspan(2);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			cell = new PdfPCell(new Phrase("Feedman ", bold));
			cell.setColspan(2);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			
			cell = new PdfPCell(new Phrase("Head Lad ", bold));
			cell.setColspan(2);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			cell = new PdfPCell(new Phrase("Stable Girl ", bold));
			cell.setColspan(2);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			cell = new PdfPCell(new Phrase("Trainers Secretary ", bold));
			cell.setColspan(2);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			cell = new PdfPCell(new Phrase("Work Rider ", bold));
			cell.setColspan(2);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			
			cell = new PdfPCell(new Phrase("Travelling Head Lad ", bold));
			cell.setColspan(2);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			cell = new PdfPCell(new Phrase("Apprentice Jockey ", bold));
			cell.setColspan(2);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			cell = new PdfPCell(new Phrase("Yardman ", bold));
			cell.setColspan(2);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			cell = new PdfPCell(new Phrase("Other Duty (Please specify) ", bold));
			cell.setColspan(2);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			
			cell = new PdfPCell(new Phrase("Race Day Help", bold));
			cell.setColspan(2);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			cell = new PdfPCell(new Phrase("Conditional Jockey ", bold));
			cell.setColspan(2);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			cell = new PdfPCell(new Phrase("Black Smith ", bold));
			cell.setColspan(2);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			cell = new PdfPCell(new Phrase(" ", bold));
			cell.setColspan(2);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			
			cell = new PdfPCell(new Phrase(" ", bold));
			cell.setColspan(8);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			
			cell = new PdfPCell(new Phrase("This is the list of employees to be renewed for "+prevYear+"/"+year+" ", bold));
			cell.setColspan(8);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			
			cell = new PdfPCell(new Phrase(" ", bold));
			cell.setColspan(8);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			
			cell = new PdfPCell(new Phrase("Signed : ___________________________________ ", bold));
			cell.setColspan(8);
			cell.setBorder(0);
			pdfPTable.addCell(cell);
			
			
			return pdfPTable;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	

	@Override
	public void handleStableListAdministrationReturYearPage(String year) {
		ConfigEnum configEnum = ConfigEnum.STAFFLISTADMINISTRATORYEAR;
		Criteria criteria = getCurrentSession().createCriteria(Config.class);
		criteria.add(Restrictions.eq("key", configEnum.getKey()).ignoreCase());
		List<Config> records = criteria.list();
		Config config = new Config();
		config.setCreatedDate(new Date());
		if(records != null && records.size() > 0) 
			config = records.get(0);
		
		config.setName(configEnum.getName());
		config.setKey(configEnum.getKey());
		config.setValue(year);
		getCurrentSession().saveOrUpdate(config);
	}

	@Override
	public void handleTrainerEmployeeOnlineReturYearPage(String year) {
		ConfigEnum configEnum = ConfigEnum.TRAINEREMPLOYEEONLINEYEAR;
		Criteria criteria = getCurrentSession().createCriteria(Config.class);
		criteria.add(Restrictions.eq("key", configEnum.getKey()).ignoreCase());
		List<Config> records = criteria.list();
		Config config = new Config();
		config.setCreatedDate(new Date());
		if(records != null && records.size() > 0) 
			config = records.get(0);
		
		config.setName(configEnum.getName());
		config.setKey(configEnum.getKey());
		config.setValue(year);
		getCurrentSession().saveOrUpdate(config);
	}
	
	@Override
	public String getYearForStaffListAdministrator() {
		
		ConfigEnum configEnum = ConfigEnum.STAFFLISTADMINISTRATORYEAR;
		Criteria criteria = getCurrentSession().createCriteria(Config.class);
		criteria.add(Restrictions.eq("key", configEnum.getKey()).ignoreCase());
		List<Config> records = criteria.list();
		return (records != null && records.size() > 0) ? records.get(0).getValue() : "2017";
	}
	
	@Override
	public String getYearForTrainerEmployeeOnline() {
		ConfigEnum configEnum = ConfigEnum.TRAINEREMPLOYEEONLINEYEAR;
		Criteria criteria = getCurrentSession().createCriteria(Config.class);
		criteria.add(Restrictions.eq("key", configEnum.getKey()).ignoreCase());
		List<Config> records = criteria.list();
		return (records != null && records.size() > 0) ? records.get(0).getValue() : "2017";
	}

}
