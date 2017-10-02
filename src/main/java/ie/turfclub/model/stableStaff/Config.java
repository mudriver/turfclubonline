package ie.turfclub.model.stableStaff;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "config", catalog = "trainers")
public class Config {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name="bi_id")
    private long id;
	
	@Column(name="v_name")
	private String name;
	
	@Column(name="v_key")
	private String key;
	
	@Column(name="v_value")
	private String value;
	
	@Column(name="d_created_date")
	private Date createdDate;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
}
