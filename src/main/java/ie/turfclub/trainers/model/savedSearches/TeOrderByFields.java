package ie.turfclub.trainers.model.savedSearches;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
@Entity
@Table(name = "te_saved_searches_order_by", catalog = "trainers")
public class TeOrderByFields {
	


		private Integer fieldId;
		private TeSavedSearches fieldSavedSearchId;
		private String fieldOrder;
		private Integer fieldPriority;
		private String fieldTitle;
		
		
		@Id
		@GeneratedValue(strategy = IDENTITY)
		@Column(name = "field_id", unique = true, nullable = false)
		public Integer getFieldId() {
			return fieldId;
		}
		public void setFieldId(Integer fieldId) {
			this.fieldId = fieldId;
		}
		
		@ManyToOne(optional = true, targetEntity=TeSavedSearches.class , cascade = CascadeType.ALL)
		@JoinColumn( name = "field_saved_search_id", referencedColumnName = "search_id", nullable = true)
		@JsonBackReference
		public TeSavedSearches getFieldSavedSearchId() {
			return fieldSavedSearchId;
		}
		
		public void setFieldSavedSearchId(TeSavedSearches fieldSavedSearchId) {
			this.fieldSavedSearchId = fieldSavedSearchId;
		}
		
		@Column(name = "field_order", nullable = false)
		public String getFieldOrder() {
			return fieldOrder;
		}
		public void setFieldOrder(String fieldOrder) {
			this.fieldOrder = fieldOrder;
		}
		
		@Column(name = "field_priority", nullable = false)
		public Integer getFieldPriority() {
			return fieldPriority;
		}
		public void setFieldPriority(Integer fieldPriority) {
			this.fieldPriority = fieldPriority;
		}
		
		@Column(name = "field_title", nullable = false)
		public String getFieldTitle() {
			return fieldTitle;
		}
		public void setFieldTitle(String fieldTitle) {
			this.fieldTitle = fieldTitle;
		}
		
		
		
	}
