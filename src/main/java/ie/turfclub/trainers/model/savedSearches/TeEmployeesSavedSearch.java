package ie.turfclub.trainers.model.savedSearches;

import ie.turfclub.model.stableStaff.TeEmployees;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;



public class TeEmployeesSavedSearch extends TeSavedSearches{


	private Set<TeEmployees> employeesSearch = new HashSet<TeEmployees>();
	private String savedSearchType;

	

	
	@Column(name = "search_type", nullable = false)
	@Filter(name = "searchType", condition = "search_type='EMPLOYEE_LIST'")
	public String getSavedSearchType() {
		return savedSearchType;
	}
	public void setSavedSearchType(String savedSearchType) {
		this.savedSearchType = savedSearchType;
	}

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
