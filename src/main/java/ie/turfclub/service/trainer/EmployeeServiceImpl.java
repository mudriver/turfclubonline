package ie.turfclub.service.trainer;

import ie.turfclub.common.bean.AdvanceSearchRecordBean;
import ie.turfclub.common.bean.SearchByNameEmployeeBean;
import ie.turfclub.common.enums.RoleEnum;
import ie.turfclub.common.service.NullAwareBeanUtilsBean;
import ie.turfclub.model.stableStaff.TeCards;
import ie.turfclub.model.stableStaff.TeEmployees;
import ie.turfclub.model.stableStaff.TeEmployeesApproved;
import ie.turfclub.model.stableStaff.TeEmployentHistory;
import ie.turfclub.model.stableStaff.TeEmploymentApprovedHistory;
import ie.turfclub.model.stableStaff.TePension;
import ie.turfclub.model.stableStaff.TePensionApproved;
import ie.turfclub.model.stableStaff.TeTrainers;
import ie.turfclub.person.model.Person;
import ie.turfclub.service.stableStaff.StableStaffService;
import ie.turfclub.utilities.EncryptDecryptUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

@PropertySource("classpath:ie/turfclub/trainers/resources/config/config.properties")
@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	private SessionFactory sessionFactory;
	@Resource
	private Environment env;
	@Autowired
	private StableStaffService stableStaffService;
	@Autowired
	private TrainersService trainersService;
	@Autowired
	private MessageSource messageSource;
	
	List<String> sexEnum;
	List<String> maritalEnum;
	List<String> employmentCategoryEnum;
	List<String> cardTypeEnum;
	List<String> nationalityEnum;
	List<String> hoursWorkedEnum;
	List<String> titlesEnum;
	List<String> countiesEnum;
	List<String> countriesEnum;
	List<String> pensionEnum; 
	
	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}
	
	@Override
	public void saveOrUpdate(TeEmployees emp) {
	
		emp.setEmployeesPpsNumber(EncryptDecryptUtils.encrypt(emp.getEmployeesPpsNumber()));
		getCurrentSession().saveOrUpdate(emp);
	}
	
	@Override
	public String getAllCardType() {
		
		cardTypeEnum = new ArrayList<String>();
		cardTypeEnum.add("A");
		cardTypeEnum.add("B");
		
		return getEnumCommonResponse(cardTypeEnum);
	}
	
	public String getEnumCommonResponse(List<String> records) {
		
		Gson gson = new Gson();
		List<Map<String, String>> titlesList = new ArrayList<>();
		for (String title : records) {
			Map<String, String> map = new HashMap<>();
			map.put("id", title);
			map.put("text", title);
			titlesList.add(map);
		}
		// convert java object to JSON format,
		// and returned as JSON formatted string
		String json = gson.toJson(titlesList);

		return json;
	}
	
	@Override
	public String getPension() {
		
		pensionEnum = new ArrayList<String>();
		pensionEnum.add("F");
		pensionEnum.add("P");
		pensionEnum.add("N");
		
		return getEnumCommonResponse(pensionEnum);
	}

	@Override
	public String getSexEnum() {
		
		sexEnum = stableStaffService.getSexEnum();
		
		return getEnumCommonResponse(sexEnum);
	}

	@Override
	public String getMaritalStatusEnum() {
		
		maritalEnum = stableStaffService.getMaritalStatusEnum();
		return getEnumCommonResponse(maritalEnum);
	}

	@Override
	public String getEmploymentCategoryEnum() {
		
		employmentCategoryEnum = stableStaffService.getEmploymentCategoryEnum();
		return getEnumCommonResponse(employmentCategoryEnum);
	}

	@Override
	public String getTitlesEnum() {
		
		return stableStaffService.getTitlesEnum();
	}

	@Override
	public String getCountiesEnum() {
		
		return stableStaffService.getCountiesEnum();
	}

	@Override
	public String getCountriesEnum() {
		
		return stableStaffService.getCountriesEnum();
	}
	
	@Override
	public String getNationalityEnum() {
		
		nationalityEnum = stableStaffService.getNationalityEnum();
		return getEnumCommonResponse(nationalityEnum);
	}
	
	@Override
	public List<HashMap<String, Object>> getAllCards() {
		
		Criteria criteria = getCurrentSession().createCriteria(TeCards.class);
		List<TeCards> cards = criteria.list();
		List<HashMap<String, Object>> records = new ArrayList<HashMap<String,Object>>();
		if(cards != null && cards.size() > 0) {
			for (TeCards teCards : cards) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("name", teCards.getCardsCardNumber());
				map.put("id", teCards.getCardsCardId());
				records.add(map);
			}
		}
		return records;
	}
	
	@Override
	public TeEmployees getEmployeeByCardId(Integer cardId) {
		
		Criteria criteria = getCurrentSession().createCriteria(TeCards.class);
		criteria.add(Restrictions.eq("cardsCardId", cardId));
		List<TeCards> cards = criteria.list();
		TeEmployees employee = null;
		if(cards != null && cards.size() > 0) {
			if(cards.get(0).getTeEmployees().getEmployeesEmployeeId() != null) {
				String sql = "From TeEmployees where employeesEmployeeId = "+cards.get(0).getTeEmployees().getEmployeesEmployeeId();
				List<TeEmployees> employees = getCurrentSession().createQuery(sql).list();
				if(employees != null && employees.size() > 0) {
					employees.get(0).setEmployeesPpsNumber(EncryptDecryptUtils.encrypt(employees.get(0).getEmployeesPpsNumber()));
					return employees.get(0);
				}
			}
		}
		return employee;
	}
	
	@Override
	public List<SearchByNameEmployeeBean> searchByNameEmployees() {
		
		Criteria criteria = getCurrentSession().createCriteria(TeEmployees.class);
		List<TeEmployees> employees = criteria.list();
		return convertEmployeesToBean(employees);
	}

	private List<SearchByNameEmployeeBean> convertEmployeesToBean(
			List<TeEmployees> employees) {
		
		List<SearchByNameEmployeeBean> records = new ArrayList<SearchByNameEmployeeBean>();
		for (TeEmployees teEmployees : employees) {
			SearchByNameEmployeeBean bean = new SearchByNameEmployeeBean();
			bean.setId(teEmployees.getEmployeesEmployeeId());
			if(teEmployees.getTeCard() != null) {
				bean.setCardType(teEmployees.getTeCard().getCardsCardType());
				bean.setCardNumber(teEmployees.getTeCard().getCardsCardNumber());
			}
			bean.setFullName(teEmployees.getEmployeesFullName());
			records.add(bean);
		}
		return records;
	}
	
	@Override
	public TeEmployees getEmployeeById(Integer id) throws IllegalAccessException, InvocationTargetException {
		
		Criteria criteria = getCurrentSession().createCriteria(TeEmployees.class);
		criteria.add(Restrictions.eq("employeesEmployeeId", id));
		List<TeEmployees> employees = criteria.list();
		TeEmployees emp =  (employees != null && employees.size() > 0) ? employees.get(0) : null;
		
		criteria = getCurrentSession().createCriteria(TeEmployentHistory.class);
		criteria.add(Restrictions.eq("teEmployees.employeesEmployeeId", id));
		List<TeEmployentHistory> currHistories = criteria.list();
		if(currHistories != null && currHistories.size() > 0) {
			List<TeEmployentHistory> histories = new ArrayList<TeEmployentHistory>();
			for (TeEmployentHistory teEmployentHistory : currHistories) {
				TeEmployentHistory history = new TeEmployentHistory();
				BeanUtilsBean notNull=new NullAwareBeanUtilsBean();
				notNull.copyProperties(history, teEmployentHistory);
				history.setTrainerName(history.getTeTrainers().getTrainerFullName());
				histories.add(history);
			}
			emp.setHistories(histories);
		}
		
		criteria = getCurrentSession().createCriteria(TePension.class);
		criteria.add(Restrictions.eq("teEmployees.employeesEmployeeId", id));
		List<TePension> currPensions = criteria.list();
		
		if(emp.getTePensions() != null && !emp.getTePensions().isEmpty()) {
			List<TePension> pensions = new ArrayList<TePension>();
			for (TePension tePension : currPensions) {
				TePension pension = new TePension();
				BeanUtilsBean notNull=new NullAwareBeanUtilsBean();
				notNull.copyProperties(pension, tePension);
				pension.setPensionTrainerName(pension.getPensionTrainer().getTrainerFullName());
				pensions.add(pension);
			}
			emp.setPensions(pensions);
		}
		LocalDate ld = LocalDate.fromDateFields(emp.getEmployeesDateOfBirth());

		Period p = Period.fieldDifference(ld, LocalDate.now());
		emp.setAge(p.getYears());
		emp.setEmployeesPpsNumber(EncryptDecryptUtils.decrypt(emp.getEmployeesPpsNumber()));
 		return emp;
	}
	
	@Override
	public void deleteRecordById(Integer id) throws IllegalAccessException, InvocationTargetException {
	
		TeEmployees emp = getEmployeeById(id);
		getCurrentSession().delete(emp);
	}
	
	@Override
	public HashMap<String, Object> getPartFullTimeRecords(String hours) {
		
		HashMap<String, Object> records = new HashMap<String, Object>();
		
		Criteria partTimeCriteria = getCurrentSession().createCriteria(TeEmployentHistory.class);
		Date startDate = new DateTime().dayOfYear().withMinimumValue().toDate();
		Date endDate = new DateTime().dayOfYear().withMaximumValue().toDate();
		partTimeCriteria.setProjection(Projections.rowCount());
		partTimeCriteria.add(
				Restrictions.or(
						Restrictions.between("ehDateFrom", startDate, endDate), 
						Restrictions.between("ehDateTo", startDate, endDate)));
		partTimeCriteria.add(Restrictions.lt("employeeNumHourWorked", Integer.parseInt(hours)));
		Long partTimeCount = (Long) partTimeCriteria.uniqueResult();
		Criteria fullTimeCriteria = getCurrentSession().createCriteria(TeEmployentHistory.class);
		fullTimeCriteria.setProjection(Projections.rowCount());
		fullTimeCriteria.add(
				Restrictions.or(
						Restrictions.between("ehDateFrom", startDate, endDate), 
						Restrictions.between("ehDateTo", startDate, endDate)));
		fullTimeCriteria.add(Restrictions.ge("employeeNumHourWorked", Integer.parseInt(hours)));
		Long fullTimeCount = (Long) fullTimeCriteria.uniqueResult();
		records.put("partTime", partTimeCount);
		records.put("fullTime", fullTimeCount);
		return records;
	}
	
	@Override
	public void handleSaveOrUpdate(TeEmployees emp, TeTrainers trainer) throws Exception {
		
		emp.setEmployeesDateEntered(new Date());
		emp.setEmployeesLastUpdated(new Date());
		emp.getTeCard().setCardsCardStatus("Applied");
		emp.getTeCard().setTeEmployees(emp);
		
		if(emp.getHistories() != null && emp.getHistories().size() > 0) {
			for (TeEmployentHistory history : emp.getHistories()) {
				history.setTeEmployees(emp);
				history.setTeTrainers(trainer);
			}
			emp.setTeEmployentHistories(new HashSet<TeEmployentHistory>(emp.getHistories()));
		}
		
		if(emp.getPensions() != null && emp.getPensions().size() > 0) {
			for (TePension pension : emp.getPensions()) {
				
				if(pension != null && pension.getPensionCardType() != null && pension.getPensionType() != null &&
						pension.getPensionDateJoinedScheme() != null) {
					pension.setTeEmployees(emp);
					pension.setPensionTrainer(trainer);
				}
			}
			emp.setTePensions(new HashSet<TePension>(emp.getPensions()));
		}
		
		saveOrUpdate(emp);
		
	}
	
	
	//set all value from employee to person object
	private Person createPerson(TeEmployees emp) {
		
		Person person = new Person();
		person.setRefId(emp.getEmployeesEmployeeId());
		person.setSurname(emp.getEmployeesSurname());
		person.setFirstname(emp.getEmployeesFirstname());
		person.setDateOfBirth(emp.getEmployeesDateOfBirth());
		person.setRequestDate(emp.getEmployeeRequestDate());
		person.setDateEntered(emp.getEmployeesDateEntered());
		person.setAddress1(emp.getEmployeesAddress1());
		person.setAddress2(emp.getEmployeesAddress2());
		person.setAddress3(emp.getEmployeesAddress3());
		person.setPhoneNo(emp.getEmployeesPhoneNo());
		person.setMobileNo(emp.getEmployeesMobileNo());
		person.setEmail(emp.getEmployeesEmail());
		person.setComments(emp.getEmployeesComments());
		person.setRoleId(RoleEnum.EMPLOYEE.getId());
		person.setTitle(emp.getEmployeesTitle());
		person.setSex(emp.getEmployeesSex());
		person.setNationality(emp.getEmployeesNationality());
		person.setMaritalStatus(emp.getEmployeesMaritalStatus());
		person.setSpouseName(emp.getEmployeesSpouseName());
		person.setCounty(emp.getEmployeesAddress4());
		person.setCountry(emp.getEmployeesAddress5());
		person.setPostCode(emp.getEmployeesPostCode());
		person.setCardType(emp.getTeCard() != null ? emp.getTeCard().getCardsCardType() : null);
		person.setCardNumber(emp.getTeCard() != null ? emp.getTeCard().getCardsCardNumber() : null);
		if(emp.getTeEmployentHistories() != null && !emp.getTeEmployentHistories().isEmpty()) {
			List<TeEmployentHistory> histories = new ArrayList<TeEmployentHistory>();
			histories.addAll(emp.getTeEmployentHistories());
			
			try {
				Collections.sort(histories, new Comparator<TeEmployentHistory>() {
				    @Override
				    public int compare(TeEmployentHistory o1, TeEmployentHistory o2) {
				        return -1 * o1.getEhDateFrom().compareTo(o2.getEhDateFrom());
				    }
				});
				if(histories != null && histories.size() > 0 && histories.get(0).getTeTrainers() != null) {
					person.setTrainerName(histories.get(0).getTeTrainers().getTrainerFullName());
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return person;
	}
	

	/**
	 * Get CSV String for exportCSV file
	 * 
	 * @param records
	 * @return
	 */
	private String getCSVStringByData(List<AdvanceSearchRecordBean> records, String title) {
		
		String csvString = ",,";
		csvString += title+",";
		csvString += "\n\n";
		csvString += "Card Number,Name,Date Of Birth,Current/Last Trainer\n";
		if(records != null && records.size() > 0) {
			for (AdvanceSearchRecordBean bean : records) {
				csvString += bean.getCardNumber()+","+bean.getName()+","+bean.getDateOfBirth()+","+bean.getTrainerName()+"\n";
			}
		}
		return csvString;
	}
	
	@Override
	public void handleEncryptPPSNumber() {
		
		Criteria criteria = getCurrentSession().createCriteria(TeEmployees.class);
		List<TeEmployees> employees = criteria.list();
		if(employees != null && employees.size() > 0) {
			for (TeEmployees teEmployees : employees) {
				if(teEmployees.getEmployeesPpsNumber() != null && teEmployees.getEmployeesPpsNumber().length() <= 8) {
					
					teEmployees.setEmployeesPpsNumber(EncryptDecryptUtils.encrypt(teEmployees.getEmployeesPpsNumber()));
					getCurrentSession().saveOrUpdate(teEmployees);
				}
			}
		}
	}
	
	@Override
	public List<TeEmployentHistory> getListOfTrainersEmployees(Integer id, String type) {
		
		List<TeEmployentHistory> records = new ArrayList<TeEmployentHistory>();
		if(type.equalsIgnoreCase("all")) {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(TeEmployentHistory.class);
			criteria.add(Restrictions.eq("teTrainers.trainerId", id));
			//criteria.add(Restrictions.eq("ehDateTo", null));
			criteria.add(Restrictions.isNull("ehDateTo"));
			records = criteria.list();
			
		} else {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(TeEmployentHistory.class);
			criteria.add(Restrictions.eq("teTrainers.trainerId", id));
			criteria.add(Restrictions.isNull("ehDateTo"));
			DateTime date = new DateTime();
			Date today = new Date();
			Date firstDay = date.dayOfYear().withMinimumValue().toDate();
			criteria.add(Restrictions.between("ehDateFrom", firstDay, today));
			records = criteria.list();
		}
		
		if(records != null && records.size() > 0) {
			for (TeEmployentHistory history : records) {
				Criteria criteria = sessionFactory.getCurrentSession().createCriteria(TeEmployentHistory.class);
				criteria.add(Restrictions.eq("teTrainers.trainerId", id));
				criteria.add(Restrictions.eq("teEmployees.employeesEmployeeId", history.getTeEmployees().getEmployeesEmployeeId()));
				criteria.addOrder(Order.asc("ehDateFrom"));
				List<TeEmployentHistory> historyRecords = criteria.list();
				history.setStartDate(historyRecords.get(0).getEhDateFrom());
			}
		}
		return records;
	}

	@Override
	public void handleSaveOrUpdateEmployeeApproved(TeEmployeesApproved emp,
			TeTrainers trainer) {
		
		emp.setEmployeesDateEntered(new Date());
		emp.setEmployeesLastUpdated(new Date());
		emp.setApproved(false);
		emp.getTeCard().setCardsCardStatus("Applied");
		emp.getTeCard().setTeEmployees(emp);
		
		if(emp.getHistories() != null && emp.getHistories().size() > 0) {
			for (TeEmploymentApprovedHistory history : emp.getHistories()) {
				history.setTeEmployees(emp);
				history.setTeTrainers(trainer);
			}
			emp.setTeEmployentHistories(new HashSet<TeEmploymentApprovedHistory>(emp.getHistories()));
		}
		
		if(emp.getPensions() != null && emp.getPensions().size() > 0) {
			for (TePensionApproved pension : emp.getPensions()) {
				
				pension.setTeEmployees(emp);
				pension.setPensionTrainer(trainer);
			}
			emp.setTePensions(new HashSet<TePensionApproved>(emp.getPensions()));
		}
		
		saveOrUpdateEmployeeApproved(emp);
	}

	private void saveOrUpdateEmployeeApproved(TeEmployeesApproved emp) {

		emp.setEmployeesPpsNumber(EncryptDecryptUtils.encrypt(emp.getEmployeesPpsNumber()));
		getCurrentSession().saveOrUpdate(emp);
	}
}
