package ie.turfclub.trainers.model.savedSearches;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Filter;

import com.fasterxml.jackson.annotation.JsonManagedReference;


@Entity
@Table(name = "te_saved_searches", catalog = "trainers")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "saved_search_type")
public class TeSavedSearches {


	private Integer savedSearchId;
	private String savedSearchName;
	private Integer savedSearchUserId;
	private Integer maxToShow = 25;
	private Integer currentRecordStart = 0;
	private Map<String, TeOrderByFields> teOrderByFields = new HashMap<String, TeOrderByFields>();
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "search_id", unique = true, nullable = false)
	public Integer getSavedSearchId() {
		return savedSearchId;
	}
	public void setSavedSearchId(Integer savedSearchId) {
		this.savedSearchId = savedSearchId;
	}
	
	@Column(name = "search_name", nullable = false)
	public String getSavedSearchName() {
		return savedSearchName;
	}
	public void setSavedSearchName(String savedSearchName) {
		this.savedSearchName = savedSearchName;
	}
	

	
	@Column(name = "saved_search_user_id", nullable = false)
	public Integer getSavedSearchUserId() {
		return savedSearchUserId;
	}
	public void setSavedSearchUserId(Integer savedSearchUserId) {
		this.savedSearchUserId = savedSearchUserId;
	}
	
	@Column(name = "saved_search_max_show", nullable = false)
	public Integer getMaxToShow() {
		return maxToShow;
	}
	public void setMaxToShow(Integer maxToShow) {
		this.maxToShow = maxToShow;
	}
	
	@Column(name = "saved_search_record_start", nullable = false)
	public Integer getCurrentRecordStart() {
		return currentRecordStart;
	}
	public void setCurrentRecordStart(Integer currentRecordStart) {
		this.currentRecordStart = currentRecordStart;
	}


	
	
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy="fieldSavedSearchId")
    @MapKey(name="fieldTitle")   
	@JsonManagedReference
	public Map<String, TeOrderByFields> getOrderByFields() {
		return teOrderByFields;
	}
	public void setOrderByFields(Map<String, TeOrderByFields> orderByFields) {
		this.teOrderByFields = orderByFields;
	}
	


	
}
