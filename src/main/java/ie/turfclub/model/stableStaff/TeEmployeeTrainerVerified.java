package ie.turfclub.model.stableStaff;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "te_employee_trainer_verified", catalog = "trainers")
public class TeEmployeeTrainerVerified implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer verifiedId;
	private TeTrainers trainerId;
	private TeEmployees employeeId;
	private boolean isVerified;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "verified_id", unique = true, nullable = false)
	public Integer getVerifiedId() {
		return verifiedId;
	}
	public void setVerifiedId(Integer verifiedId) {
		this.verifiedId = verifiedId;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "verified_trainer_id", nullable = false)
	public TeTrainers getTrainerId() {
		return trainerId;
	}
	public void setTrainerId(TeTrainers trainerId) {
		this.trainerId = trainerId;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "verified_employee_id", nullable = false)
	public TeEmployees getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(TeEmployees employeeId) {
		this.employeeId = employeeId;
	}

	@Column(name = "verified_is_verified")
	public boolean isVerified() {
		return isVerified;
	}
	public void setVerified(boolean isVerified) {
		this.isVerified = isVerified;
	}
	
	
	
}
