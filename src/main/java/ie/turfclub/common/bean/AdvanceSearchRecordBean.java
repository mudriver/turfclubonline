package ie.turfclub.common.bean;

import java.io.Serializable;
import java.util.Date;

public class AdvanceSearchRecordBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String cardNumber;
	private String name;
	private Date dateOfBirth;
	private String trainerName;
	private Integer id;
	
	public String getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public String getTrainerName() {
		return trainerName;
	}
	public void setTrainerName(String trainerName) {
		this.trainerName = trainerName;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
}
