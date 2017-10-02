package ie.turfclub.trainers.model.savedSearches;

import ie.turfclub.model.stableStaff.TeEmployees;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


@Entity
@DiscriminatorValue(value = "EMPLOYEE_PENSION_LIST")
public class TeEmployeesPensionSavedSearch extends TeSavedSearches{
	
	private Set<TeEmployees> employeesSearch = new HashSet<TeEmployees>();

	
	@NotFound(action=NotFoundAction.IGNORE)  
	@ManyToMany(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name="te_saved_searches_employees", catalog="trainers", joinColumns={
			@JoinColumn(name="ss_search_id", nullable=false, updatable=false)},
			inverseJoinColumns={@JoinColumn(name="ss_employee_id", nullable=false, updatable=false)})
	public Set<TeEmployees> getEmployeesSearch() {
		return employeesSearch;
	}

	public void setEmployeesSearch(Set<TeEmployees> employeesSearch) {
		this.employeesSearch = employeesSearch;
	}
	
	
}
