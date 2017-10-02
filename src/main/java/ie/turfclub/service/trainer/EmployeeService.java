package ie.turfclub.service.trainer;

import ie.turfclub.common.bean.SearchByNameEmployeeBean;
import ie.turfclub.model.stableStaff.TeEmployees;
import ie.turfclub.model.stableStaff.TeEmployeesApproved;
import ie.turfclub.model.stableStaff.TeEmployentHistory;
import ie.turfclub.model.stableStaff.TeTrainers;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public interface EmployeeService {

	void saveOrUpdate(TeEmployees emp);

	String getAllCardType();

	String getPension();

	String getSexEnum();

	String getMaritalStatusEnum();

	String getEmploymentCategoryEnum();

	String getTitlesEnum();

	String getCountiesEnum();

	String getCountriesEnum();

	String getNationalityEnum();

	List<HashMap<String, Object>> getAllCards();

	TeEmployees getEmployeeByCardId(Integer cardId);

	List<SearchByNameEmployeeBean> searchByNameEmployees();

	TeEmployees getEmployeeById(Integer id) throws IllegalAccessException, InvocationTargetException;

	void deleteRecordById(Integer id) throws IllegalAccessException, InvocationTargetException;

	HashMap<String, Object> getPartFullTimeRecords(String hours);

	void handleSaveOrUpdate(TeEmployees emp, TeTrainers trainer) throws Exception;

	void handleEncryptPPSNumber();

	List<TeEmployentHistory> getListOfTrainersEmployees(Integer id, String type);

	void handleSaveOrUpdateEmployeeApproved(TeEmployeesApproved emp,
			TeTrainers trainer);

}
