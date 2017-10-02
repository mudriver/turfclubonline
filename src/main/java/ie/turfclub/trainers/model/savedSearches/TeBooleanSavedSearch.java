package ie.turfclub.trainers.model.savedSearches;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterDefs;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "te_saved_searches_boolean", catalog = "trainers")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "ss_boolean_type")
public class TeBooleanSavedSearch {

	private Integer ssBooleanId;
	private TeSavedSearches savedSearch;
	private boolean ssBoolean;
	private boolean inUse = false;
	


	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "ss_boolean_id", unique = true, nullable = false)
	public Integer getSsBooleanId() {
		
		return ssBooleanId;
	}

	public void setSsBooleanId(Integer ssBooleanId) {
		this.inUse = true;
		this.ssBooleanId = ssBooleanId;
	}

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ss_search_id", nullable = false)
	@JsonBackReference
	public TeSavedSearches getSavedSearch() {
		return savedSearch;
	}

	public void setSavedSearch(TeSavedSearches savedSearch) {
		this.savedSearch = savedSearch;
	}

	@Column(name = "ss_boolean", nullable = false)
	public boolean isSsBoolean() {
		return ssBoolean;
	}

	public void setSsBoolean(boolean ssBoolean) {
		this.ssBoolean = ssBoolean;
	}



	@Transient
	public boolean isInUse() {
		return inUse;
	}

	public void setInUse(boolean inUse) {
		this.inUse = inUse;
	}
	
	
	
}
