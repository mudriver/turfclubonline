package ie.turfclub.trainers.model.savedSearches;

import ie.turfclub.model.stableStaff.TeTrainers;

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


public class TeAuthRepsSavedSearches extends TeSavedSearches{

	
	private String savedSearchType;
	private Set<TeTrainers> trainersSearch = new HashSet<TeTrainers>();
	

	
	@Column(name = "search_type", nullable = false)
	@Filter(name = "searchType", condition = "search_type='AUTHREPS_LIST'")
	public String getSavedSearchType() {
		return savedSearchType;
	}
	public void setSavedSearchType(String savedSearchType) {
		this.savedSearchType = savedSearchType;
	}
	


	@NotFound(action=NotFoundAction.IGNORE)  
	@ManyToMany(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name="te_saved_searches_trainers", catalog="trainers", joinColumns={
			@JoinColumn(name="ss_search_id", nullable=false, updatable=false)},
			inverseJoinColumns={@JoinColumn(name="ss_trainer_id", nullable=false, updatable=false)})
	public Set<TeTrainers> getTrainersSearch() {
		return trainersSearch;
	}
	public void setTrainersSearch(Set<TeTrainers> trainersSearch) {
		this.trainersSearch = trainersSearch;
	}
	

	
}
