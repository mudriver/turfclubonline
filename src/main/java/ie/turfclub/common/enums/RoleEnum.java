package ie.turfclub.common.enums;

public enum RoleEnum {
	EMPLOYEE(1, "employee"),
	TRAINER(2, "trainer");
	
	private long id;
	
	private String name;

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
	
	RoleEnum(long id, String name) { this.id = id; this.name = name; }
	
	public static RoleEnum[] ALL = {EMPLOYEE, TRAINER};
}
