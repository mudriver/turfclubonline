package ie.turfclub.service.stableStaff;

import ie.turfclub.model.stableStaff.TeChangesLog;
import ie.turfclub.model.stableStaff.TeEmployees;
import ie.turfclub.model.stableStaff.TeEmployentHistory;
import ie.turfclub.model.stableStaff.TeTrainers;

import java.util.List;

public interface StableStaffService {

	public List<TeEmployees> getEmployees(Integer trainerId);
	public TeTrainers getTrainer(int id);
	public TeEmployees getEmployee(int id);
	public void deleteEmployementHistories(List<TeEmployentHistory> oldHistories);
	public List<TeEmployentHistory> getEmploymentHistories(int employeeId, int trainerId);
	public List<String> getSexEnum();
	public List<String> getMaritalStatusEnum();
	public List<String> getEmploymentCategoryEnum();
	public List<String> getCardTypeEnum();
	public List<String> getNationalityEnum();
	public List<String> getHoursWorkedEnum();
	public String getTitlesEnum();
	public String getCountiesEnum();
	public String getCountriesEnum();
	public void saveEmployee(TeEmployees employee);
	public void saveTrainer(TeTrainers trainer);
	public int saveNewEmployee(TeEmployees employee);
	public void saveHistory(TeEmployentHistory employmentHistory);
	public int saveNewHistory(TeEmployentHistory employmentHistory);
	public void saveLog(TeChangesLog log);
	public void initialize();
	
}
