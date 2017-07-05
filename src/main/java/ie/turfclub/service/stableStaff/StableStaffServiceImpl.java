package ie.turfclub.service.stableStaff;

import ie.turfclub.model.stableStaff.TeChangesLog;
import ie.turfclub.model.stableStaff.TeEmployees;
import ie.turfclub.model.stableStaff.TeEmployentHistory;
import ie.turfclub.model.stableStaff.TeTrainers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;

@Service
@Transactional
public class StableStaffServiceImpl implements StableStaffService {

	static Logger logger = LoggerFactory
			.getLogger(StableStaffServiceImpl.class);

	@Autowired
	private SessionFactory sessionFactory;
	List<String> sexEnum;
	List<String> maritalEnum;
	List<String> employmentCategoryEnum;
	List<String> cardTypeEnum;
	List<String> nationalityEnum;
	List<String> hoursWorkedEnum;
	List<String> titlesEnum;
	List<String> countiesEnum;
	List<String> countriesEnum;

	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	// @PostConstruct
	public void initialize() {
		// do some initialization work

		System.out.println("intialize dropdowns");
		SQLQuery q = getCurrentSession().createSQLQuery(
				"desc trainers.te_employees employees_sex");
		Object[] descRow = (Object[]) q.list().get(0);
		sexEnum = new ArrayList<>();

		// System.out.println(descRow[1].toString());
		for (String ob : descRow[1].toString().split("'")) {
			if (!ob.contains("enum") && !ob.contains(",") && !ob.contains(")")) {
				// System.out.println(ob);
				sexEnum.add(ob);
			}

		}
		q = getCurrentSession()
				.createSQLQuery(
						"desc trainers.te_employees employees_marital_status");

		descRow = (Object[]) q.list().get(0);
		maritalEnum = new ArrayList<>();

		// System.out.println(descRow[1].toString());
		for (String ob : descRow[1].toString().split("'")) {
			if (!ob.contains("enum") && !ob.contains(",") && !ob.contains(")")) {
				// System.out.println(ob);
				maritalEnum.add(ob);
			}

		}
		Collections.sort(maritalEnum);
		q = getCurrentSession()
				.createSQLQuery(
						"desc trainers.te_employment_history eh_employment_category");

		descRow = (Object[]) q.list().get(0);
		employmentCategoryEnum = new ArrayList<>();

		// System.out.println(descRow[1].toString());
		for (String ob : descRow[1].toString().split("'")) {
			if (!ob.contains("enum") && !ob.contains(",") && !ob.contains(")")) {
				// System.out.println(ob);
				employmentCategoryEnum.add(ob);
			}

		}
		Collections.sort(employmentCategoryEnum);
		q = getCurrentSession().createSQLQuery(
				"desc trainers.te_cards cards_card_type");

		descRow = (Object[]) q.list().get(0);
		cardTypeEnum = new ArrayList<>();

		// System.out.println(descRow[1].toString());
		for (String ob : descRow[1].toString().split("'")) {
			if (!ob.contains("enum") && !ob.contains(",") && !ob.contains(")")) {
				// System.out.println(ob);
				cardTypeEnum.add(ob);
			}

		}
		Collections.sort(cardTypeEnum);
		q = getCurrentSession().createSQLQuery(
				"desc trainers.te_employees  employees_nationality");

		descRow = (Object[]) q.list().get(0);
		nationalityEnum = new ArrayList<>();

		// System.out.println(descRow[1].toString());
		for (String ob : descRow[1].toString().split("'")) {
			if (!ob.contains("enum") && !ob.contains(",") && !ob.contains(")")) {
				// System.out.println(ob);
				nationalityEnum.add(ob);
			}

		}
		Collections.sort(nationalityEnum);
		q = getCurrentSession()
				.createSQLQuery(
						"desc trainers.te_employment_history eh_hours_worked");

		descRow = (Object[]) q.list().get(0);
		hoursWorkedEnum = new ArrayList<>();

		// System.out.println(descRow[1].toString());
		for (String ob : descRow[1].toString().split("'")) {
			if (!ob.contains("enum") && !ob.contains(",") && !ob.contains(")")) {
				// System.out.println(ob);
				hoursWorkedEnum.add(ob);
			}

		}
		q = getCurrentSession().createSQLQuery(
				"desc trainers.te_employees employees_title");

		descRow = (Object[]) q.list().get(0);
		titlesEnum = new ArrayList<>();

		titlesEnum.add("Mr.");
		titlesEnum.add("Ms.");
		titlesEnum.add("Mrs.");

		Collections.sort(titlesEnum);
		countriesEnum = new ArrayList<>();

		// System.out.println(descRow[1].toString());
		countriesEnum.add("Ireland");
		countriesEnum.add("Northern Ireland");
		Collections.sort(countriesEnum);
		countiesEnum = new ArrayList<>();

		// System.out.println(descRow[1].toString());
		countiesEnum.add("Antrim");
		countiesEnum.add("Armagh");
		countiesEnum.add("Carlow");
		countiesEnum.add("Cavan");
		countiesEnum.add("Clare");
		countiesEnum.add("Cork");
		countiesEnum.add("Derry");
		countiesEnum.add("Donegal");
		countiesEnum.add("Down");
		countiesEnum.add("Dublin");
		countiesEnum.add("Fermanagh");
		countiesEnum.add("Galway");
		countiesEnum.add("Kerry");
		countiesEnum.add("Kildare");
		countiesEnum.add("Kilkenny");
		countiesEnum.add("Laois");
		countiesEnum.add("Leitrim");
		countiesEnum.add("Limerick");
		countiesEnum.add("Longford");
		countiesEnum.add("Louth");
		countiesEnum.add("Mayo");
		countiesEnum.add("Meath");
		countiesEnum.add("Monaghan");
		countiesEnum.add("Offaly");
		countiesEnum.add("Roscommon");
		countiesEnum.add("Sligo");
		countiesEnum.add("Tipperary");
		countiesEnum.add("Tyrone");
		countiesEnum.add("Waterford");
		countiesEnum.add("Westmeath");
		countiesEnum.add("Wexford");
		countiesEnum.add("Wicklow");
		countiesEnum.add("Dublin 1");
		countiesEnum.add("Dublin 2");
		countiesEnum.add("Dublin 3");
		countiesEnum.add("Dublin 4");
		countiesEnum.add("Dublin 5");
		countiesEnum.add("Dublin 6");
		countiesEnum.add("Dublin 7");
		countiesEnum.add("Dublin 6W");
		countiesEnum.add("Dublin 8");
		countiesEnum.add("Dublin 9");
		countiesEnum.add("Dublin 10");
		countiesEnum.add("Dublin 11");
		countiesEnum.add("Dublin 12");
		countiesEnum.add("Dublin 13");
		countiesEnum.add("Dublin 14");
		countiesEnum.add("Dublin 15");
		countiesEnum.add("Dublin 16");
		countiesEnum.add("Dublin 17");
		countiesEnum.add("Dublin 18");
		countiesEnum.add("Dublin 20");
		countiesEnum.add("Dublin 22");
		countiesEnum.add("Dublin 24");
		Collections.sort(countiesEnum);

	}

	@Override
	public TeEmployees getEmployee(int id) {
		TeEmployees employee = (TeEmployees) getCurrentSession().get(
				TeEmployees.class, id);
		return employee;
	}

	public List<String> getSexEnum() {

		return this.sexEnum;
	}

	@Override
	public List<String> getMaritalStatusEnum() {
		return maritalEnum;
	}

	@Override
	public List<String> getEmploymentCategoryEnum() {

		return employmentCategoryEnum;
	}

	@Override
	public List<String> getCardTypeEnum() {

		return cardTypeEnum;
	}

	@Override
	public List<String> getNationalityEnum() {
		return nationalityEnum;
	}

	@Override
	public List<String> getHoursWorkedEnum() {

		return hoursWorkedEnum;
	}

	@Override
	public String getTitlesEnum() {

		Gson gson = new Gson();
		List<Map<String, String>> titlesList = new ArrayList<>();
		for (String title : titlesEnum) {
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
	public List<TeEmployentHistory> getEmploymentHistories(int employeeId,
			int trainerId) {
		String hql = "select eh from TeEmployentHistory eh LEFT JOIN eh.teEmployees as em LEFT JOIN eh.teTrainers as tr WHERE em.employeesEmployeeId= :employeeId AND tr.trainerId = :trainerId";
		System.out.println("Getting EMP");
		List<TeEmployentHistory> result = getCurrentSession().createQuery(hql)
				.setParameter("employeeId", employeeId)
				.setParameter("trainerId", trainerId).list();

		return result;
	}

	@Override
	public void saveEmployee(TeEmployees employee) {
		getCurrentSession().update(employee);

	}

	@Override
	public int saveNewEmployee(TeEmployees employee) {

		return (Integer) getCurrentSession().save(employee);

	}

	@Override
	public void saveHistory(TeEmployentHistory employmentHistory) {
		getCurrentSession().update(employmentHistory);

	}

	@Override
	public int saveNewHistory(TeEmployentHistory employmentHistory) {
		return (Integer) getCurrentSession().save(employmentHistory);

	}

	@Override
	public String getCountriesEnum() {

		Gson gson = new Gson();
		List<Map<String, String>> countryList = new ArrayList<>();
		for (String country : countriesEnum) {
			Map<String, String> map = new HashMap<>();
			map.put("id", country);
			map.put("text", country);
			countryList.add(map);
		}
		// convert java object to JSON format,
		// and returned as JSON formatted string
		String json = gson.toJson(countryList);

		return json;
	}

	@Override
	public void saveLog(TeChangesLog log) {

		getCurrentSession().save(log);

	}

	@Override
	public String getCountiesEnum() {

		Gson gson = new Gson();
		List<Map<String, String>> countyList = new ArrayList<>();
		for (String county : countiesEnum) {
			Map<String, String> map = new HashMap<>();
			map.put("id", county);
			map.put("text", county);
			countyList.add(map);
		}
		// convert java object to JSON format,
		// and returned as JSON formatted string
		String json = gson.toJson(countyList);

		return json;
	}

	@Override
	public List<TeEmployees> getEmployees(Integer trainerId) {
		String hql = "select distinct em from TeEmployees em LEFT JOIN fetch em.teEmployentHistories as eh WHERE eh.teTrainers.trainerId= :trainerId ORDER BY em.employeesSurname, em.employeesFirstname";
		List<TeEmployees> result = getCurrentSession().createQuery(hql)
				.setParameter("trainerId", trainerId).list();

		for(TeEmployees employee : result){
			System.out.println(employee.getTeEmployentHistories().size());
		}
		System.out.println(result == null);
		return result;
	}

	@Override
	public TeTrainers getTrainer(int id) {
		TeTrainers trainer = (TeTrainers) getCurrentSession().get(
				TeTrainers.class, id);
		return trainer;
	}

	@Override
	public void deleteEmployementHistories(List<TeEmployentHistory> oldHistories) {

		for (TeEmployentHistory oldHistory : oldHistories) {
			getCurrentSession().delete(oldHistory);
		}

	}

	@Override
	public void saveTrainer(TeTrainers trainer) {
		getCurrentSession().update(trainer);

	}

}
