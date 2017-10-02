package ie.turfclub.trainers.model.savedSearches;

import ie.turfclub.model.stableStaff.TeTrainers;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonManagedReference;


@Entity
@DiscriminatorValue(value = "TRAINER_PENSION_LIST")
public class TeTrainersPensionSavedSearch extends TeSavedSearches{



	private Set<TeTrainers> trainersSearch = new HashSet<TeTrainers>();
	private TeTrainerCompleteBooleanSavedSearch returnCompleteSearch = new TeTrainerCompleteBooleanSavedSearch();
	private TeTrainerVerifiedEnumSavedSearch verified = new TeTrainerVerifiedEnumSavedSearch();
	private TeTrainerDocumentsAttachedBooleanSavedSearch documentsAttached = new TeTrainerDocumentsAttachedBooleanSavedSearch();
	
	


	@OneToOne(fetch = FetchType.EAGER, mappedBy = "savedSearch", cascade=CascadeType.ALL)
	@JsonManagedReference
	public TeTrainerDocumentsAttachedBooleanSavedSearch getDocumentsAttached() {
		return documentsAttached;
	}
	public void setDocumentsAttached(
			TeTrainerDocumentsAttachedBooleanSavedSearch documentsAttached) {
		if(documentsAttached == null){
			this.documentsAttached = new TeTrainerDocumentsAttachedBooleanSavedSearch();
		}
		else{
			this.documentsAttached = documentsAttached;
		}
		
	}

	@OneToOne(fetch = FetchType.EAGER, mappedBy = "savedSearch", cascade=CascadeType.ALL)
	@JsonManagedReference
	public TeTrainerVerifiedEnumSavedSearch getVerified() {
		return verified;
	}
	public void setVerified(TeTrainerVerifiedEnumSavedSearch verified) {
		if(verified == null){
			System.out.println("SETTING NULL NEW");
			this.verified = new TeTrainerVerifiedEnumSavedSearch();
		}
		else{
			this.verified = verified;
		}
		
	}
	

	@OneToOne(fetch = FetchType.EAGER, mappedBy = "savedSearch", cascade=CascadeType.ALL)
	@JsonManagedReference
	public TeTrainerCompleteBooleanSavedSearch getReturnCompleteSearch() {
		return returnCompleteSearch;
	}
	public void setReturnCompleteSearch(TeTrainerCompleteBooleanSavedSearch returnCompleteSearch) {
		
		
			this.returnCompleteSearch = returnCompleteSearch;
	
		
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
