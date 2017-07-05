package ie.turfclub.model.login;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "te_user_roles", catalog = "trainers")
public class UserRole implements GrantedAuthority {

	@Id
	@GeneratedValue
	@Column(name = "role_id")
	private Integer role_id;
	@Column(name = "role_user_name")
	private String user_name;
	@Column(name = "role_type", length = 200)
	private String role_type_name;

	@Override
	public String getAuthority() {

		return role_type_name;
	}

	public Integer getRole_id() {
		return role_id;
	}

	public void setRole_id(Integer role_id) {
		this.role_id = role_id;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getRole_type_name() {
		return role_type_name;
	}

	public void setRole_type_name(String role_type_name) {
		this.role_type_name = role_type_name;
	}

}
