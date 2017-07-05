package ie.turfclub.model.stableStaff;
// Generated 28-Apr-2015 14:57:35 by Hibernate Tools 3.4.0.CR1

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.CascadeType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

/**
 * TeEmployees generated by hbm2java
 */
@Entity
@Table(name = "te_employees", catalog = "trainers", uniqueConstraints = @UniqueConstraint(columnNames = "employees_pps_number"))
public class TeEmployees implements java.io.Serializable {

	private Integer employeesEmployeeId;
	private String employeesPpsNumber;
	private String employeesTitle;
	private String employeesSurname;
	private String employeesFirstname;
	private Date employeesDateOfBirth;
	private String employeesNationality;
	private String employeesSex;
	private String employeesMaritalStatus;
	private String employeesSpouseName;
	private String employeesAddress1;
	private String employeesAddress2;
	private String employeesAddress3;
	private String employeesAddress4;
	private String employeesAddress5;
	private String employeesPostCode;
	private String employeesPhoneNo;
	private String employeesMobileNo;
	private String employeesEmail;
	private String employeesComments;
	private String employeesHriAccountNo;
	private Date employeesLastUpdated;
	private Date employeesDateEntered;
	private Boolean employeesIsNew;
	private Boolean employeesHasTaxableEarnings;
	private TeCards teCard;
	private Set<TeEmployentHistory> teEmployentHistories = new HashSet(0);
	private Set<TeChances> teChanceses = new HashSet(0);
	private Set<TePension> tePensions = new HashSet(0);
	

	public TeEmployees() {
	}

	public TeEmployees(String employeesSurname, String employeesFirstname) {
		this.employeesSurname = employeesSurname;
		this.employeesFirstname = employeesFirstname;
	}

	public TeEmployees(String employeesPpsNumber, String employeesSurname,
			String employeesFirstname, Date employeesDateOfBirth,
			String employeesSex, String employeesMaritalStatus,
			String employeesSpouseName, String employeesAddress1,
			String employeesAddress2, String employeesAddress3,
			String employeesAddress4, String employeesAddress5,
			String employeesPostCode, String employeesPhoneNo,
			String employeesMobileNo, String employeesEmail,
			String employeesComments, String employeesHriAccountNo,
			String employeesCardNoTemp, Set<TeEmployentHistory> teEmployentHistories,
			Set<TeChances> teChanceses, Set<TePension> tePensions, TeCards teCard) {
		this.employeesPpsNumber = employeesPpsNumber;
		this.employeesSurname = employeesSurname;
		this.employeesFirstname = employeesFirstname;
		this.employeesDateOfBirth = employeesDateOfBirth;
		this.employeesSex = employeesSex;
		this.employeesMaritalStatus = employeesMaritalStatus;
		this.employeesSpouseName = employeesSpouseName;
		this.employeesAddress1 = employeesAddress1;
		this.employeesAddress2 = employeesAddress2;
		this.employeesAddress3 = employeesAddress3;
		this.employeesAddress4 = employeesAddress4;
		this.employeesAddress5 = employeesAddress5;
		this.employeesPostCode = employeesPostCode;
		this.employeesPhoneNo = employeesPhoneNo;
		this.employeesMobileNo = employeesMobileNo;
		this.employeesEmail = employeesEmail;
		this.employeesComments = employeesComments;
		this.employeesHriAccountNo = employeesHriAccountNo;
		this.teEmployentHistories = teEmployentHistories;
		this.teChanceses = teChanceses;
		this.tePensions = tePensions;
		this.teCard = teCard;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "employees_employee_id", unique = true, nullable = false)
	public Integer getEmployeesEmployeeId() {
		return this.employeesEmployeeId;
	}

	public void setEmployeesEmployeeId(Integer employeesEmployeeId) {
		this.employeesEmployeeId = employeesEmployeeId;
	}

	@Column(name = "employees_pps_number", unique = true, length = 8)
	public String getEmployeesPpsNumber() {
		return this.employeesPpsNumber;
	}

	public void setEmployeesPpsNumber(String employeesPpsNumber) {
		this.employeesPpsNumber = employeesPpsNumber;
	}

	@Column(name = "employees_surname", nullable = false, length = 200)
	public String getEmployeesSurname() {
		return this.employeesSurname;
	}

	public void setEmployeesSurname(String employeesSurname) {
		this.employeesSurname = employeesSurname;
	}

	@Column(name = "employees_firstname", nullable = false, length = 200)
	public String getEmployeesFirstname() {
		return this.employeesFirstname;
	}

	public void setEmployeesFirstname(String employeesFirstname) {
		this.employeesFirstname = employeesFirstname;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "employees_date_of_birth", length = 10)
	public Date getEmployeesDateOfBirth() {
		return this.employeesDateOfBirth;
	}

	public void setEmployeesDateOfBirth(Date employeesDateOfBirth) {
		this.employeesDateOfBirth = employeesDateOfBirth;
	}

	@Column(name = "employees_sex", length = 2)
	public String getEmployeesSex() {
		return this.employeesSex;
	}

	public void setEmployeesSex(String employeesSex) {
		this.employeesSex = employeesSex;
	}

	@Column(name = "employees_marital_status", length = 13)
	public String getEmployeesMaritalStatus() {
		return this.employeesMaritalStatus;
	}

	public void setEmployeesMaritalStatus(String employeesMaritalStatus) {
		this.employeesMaritalStatus = employeesMaritalStatus;
	}

	@Column(name = "employees_spouse_name", length = 200)
	public String getEmployeesSpouseName() {
		return this.employeesSpouseName;
	}

	public void setEmployeesSpouseName(String employeesSpouseName) {
		this.employeesSpouseName = employeesSpouseName;
	}

	@Column(name = "employees_address1", length = 200)
	public String getEmployeesAddress1() {
		return this.employeesAddress1;
	}

	public void setEmployeesAddress1(String employeesAddress1) {
		this.employeesAddress1 = employeesAddress1;
	}

	@Column(name = "employees_address2", length = 200)
	public String getEmployeesAddress2() {
		return this.employeesAddress2;
	}

	public void setEmployeesAddress2(String employeesAddress2) {
		this.employeesAddress2 = employeesAddress2;
	}

	@Column(name = "employees_address3", length = 200)
	public String getEmployeesAddress3() {
		return this.employeesAddress3;
	}

	public void setEmployeesAddress3(String employeesAddress3) {
		this.employeesAddress3 = employeesAddress3;
	}

	@Column(name = "employees_address4", length = 200)
	public String getEmployeesAddress4() {
		return this.employeesAddress4;
	}

	public void setEmployeesAddress4(String employeesAddress4) {
		this.employeesAddress4 = employeesAddress4;
	}

	@Column(name = "employees_address5", length = 200)
	public String getEmployeesAddress5() {
		return this.employeesAddress5;
	}

	public void setEmployeesAddress5(String employeesAddress5) {
		this.employeesAddress5 = employeesAddress5;
	}

	@Column(name = "employees_post_code", length = 20)
	public String getEmployeesPostCode() {
		return this.employeesPostCode;
	}

	public void setEmployeesPostCode(String employeesPostCode) {
		this.employeesPostCode = employeesPostCode;
	}

	@Column(name = "employees_phone_no", length = 20)
	public String getEmployeesPhoneNo() {
		return this.employeesPhoneNo;
	}

	public void setEmployeesPhoneNo(String employeesPhoneNo) {
		this.employeesPhoneNo = employeesPhoneNo;
	}

	@Column(name = "employees_mobile_no", length = 20)
	public String getEmployeesMobileNo() {
		return this.employeesMobileNo;
	}

	public void setEmployeesMobileNo(String employeesMobileNo) {
		this.employeesMobileNo = employeesMobileNo;
	}

	@Column(name = "employees_email", length = 200)
	public String getEmployeesEmail() {
		return this.employeesEmail;
	}

	public void setEmployeesEmail(String employeesEmail) {
		this.employeesEmail = employeesEmail;
	}

	@Column(name = "employees_comments", length = 65535)
	public String getEmployeesComments() {
		return this.employeesComments;
	}

	public void setEmployeesComments(String employeesComments) {
		this.employeesComments = employeesComments;
	}

	@Column(name = "employees_hri_account_no", length = 5)
	public String getEmployeesHriAccountNo() {
		return this.employeesHriAccountNo;
	}

	public void setEmployeesHriAccountNo(String employeesHriAccountNo) {
		this.employeesHriAccountNo = employeesHriAccountNo;
	}

	
	@Column(name = "employees_title", length = 50)
	public String getEmployeesTitle() {
		return employeesTitle;
	}

	public void setEmployeesTitle(String employeesTitle) {
		this.employeesTitle = employeesTitle;
	}

	@Column(name = "employees_nationality")
	public String getEmployeesNationality() {
		return employeesNationality;
	}

	public void setEmployeesNationality(String employeesNationality) {
		this.employeesNationality = employeesNationality;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "employees_date_entered", nullable = false, length = 10)
	public Date getEmployeesDateEntered() {
		return employeesDateEntered;
	}

	public void setEmployeesDateEntered(Date employeesDateEntered) {
		this.employeesDateEntered = employeesDateEntered;
	}

	@Column(name = "employees_is_new")
	public Boolean getEmployeesIsNew() {
		return employeesIsNew;
	}

	public void setEmployeesIsNew(
			Boolean employeesIsNew) {
		this.employeesIsNew = employeesIsNew;
	}

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "teEmployees", orphanRemoval=true)
	public Set<TeEmployentHistory> getTeEmployentHistories() {
		return this.teEmployentHistories;
	}

	public void setTeEmployentHistories(Set<TeEmployentHistory> teEmployentHistories) {
		this.teEmployentHistories = teEmployentHistories;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "teEmployees")
	public Set<TeChances> getTeChanceses() {
		return this.teChanceses;
	}

	public void setTeChanceses(Set<TeChances> teChanceses) {
		this.teChanceses = teChanceses;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "teEmployees")
	public Set<TePension> getTePensions() {
		return this.tePensions;
	}

	public void setTePensions(Set<TePension> tePensions) {
		this.tePensions = tePensions;
	}

	@OneToOne(fetch = FetchType.EAGER, mappedBy = "teEmployees", cascade=CascadeType.DETACH)
	public TeCards getTeCard() {
		return teCard;
	}

	public void setTeCard(TeCards teCard) {
		this.teCard = teCard;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "employees_last_updated", length = 10)
	public Date getEmployeesLastUpdated() {
		return employeesLastUpdated;
	}

	

	public void setEmployeesLastUpdated(Date employeesLastUpdated) {
		this.employeesLastUpdated = employeesLastUpdated;
	}
	
	
	@Column(name = "employees_has_taxable_earnings")
	public Boolean getEmployeesHasTaxableEarnings() {
		return employeesHasTaxableEarnings;
	}

	public void setEmployeesHasTaxableEarnings(Boolean employeesHasTaxableEarnings) {
		this.employeesHasTaxableEarnings = employeesHasTaxableEarnings;
	}

	@Transient
	public boolean isUpdated(){
		Calendar now = Calendar.getInstance();
		System.out.println("In isUpdated Method");
	    		
	    Date yearStart = null;
		try {
			//CHANGE EARNINGS YEAR HERE
			//Comment out this line to change back to 2015 earnings year  
			now.add(Calendar.YEAR, -2);
			yearStart = new SimpleDateFormat("dd/MM/yyyy").parse("01/01/" + now.get(Calendar.YEAR));
			System.out.println(yearStart);
			System.out.println(this.employeesLastUpdated);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		if(this.employeesLastUpdated != null && (this.employeesLastUpdated.after(yearStart) || this.employeesLastUpdated.equals(yearStart))) {
			return true;
		}
		return false;
	}

	
}