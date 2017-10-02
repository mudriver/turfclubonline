package ie.turfclub.model.stableStaff;

import static javax.persistence.GenerationType.IDENTITY;





import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "te_authreps", catalog = "trainers")
public class TeAuthorisedReps {

	private Integer authrepsId;
	private String authrepsAccountNo;
	private String authrepsName;
	private TeTrainers authrepsTrainerId;
	private boolean canEdit = true;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "authreps_id", unique = true, nullable = false)
	public Integer getAuthrepsId() {
		return authrepsId;
	}
	public void setAuthrepsId(Integer authrepsId) {
		this.authrepsId = authrepsId;
	}
	
	@Column(name = "authreps_trainer_account_no", nullable = false, length = 20)
	public String getAuthrepsAccountNo() {
		return authrepsAccountNo;
	}
	public void setAuthrepsAccountNo(String authrepsAccountNo) {
		this.authrepsAccountNo = authrepsAccountNo;
	}
	@Column(name = "authreps_authrep_name") 
	public String getAuthrepsName() {
		return authrepsName;
	}
	public void setAuthrepsName(String authrepsName) {
		this.authrepsName = authrepsName;
	}
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "authreps_trainer_id", nullable = false)
	public TeTrainers getAuthrepsTrainerId() {
		return authrepsTrainerId;
	}
	public void setAuthrepsTrainerId(TeTrainers authrepsTrainerId) {
		this.authrepsTrainerId = authrepsTrainerId;
	}
	@Transient
	public boolean isCanEdit() {
		return canEdit;
	}
	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}
	
	
	
}
