package ie.turfclub.common.enums;

public enum ConfigEnum {
	
	STAFFLISTADMINISTRATORYEAR(1, "Staff List Administrator Return Year", "SLARY"),
	TRAINEREMPLOYEEONLINEYEAR(2, "Trainer Employee Online Return Year", "TEORY");
	
	private long id;
	private String name;
	private String key;
	
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
	public static ConfigEnum[] ALL = {STAFFLISTADMINISTRATORYEAR, TRAINEREMPLOYEEONLINEYEAR};
	
	ConfigEnum(long id, String name, String key) {
		this.id = id; this.name = name; this.key = key;
	}
	
	public static ConfigEnum getConfigEnumById(long id) {
		
		ConfigEnum configEnum = null;
		switch(Integer.parseInt(String.valueOf(id))) {
		case 1:
			configEnum = STAFFLISTADMINISTRATORYEAR;
			break;
		case 2:
			configEnum = TRAINEREMPLOYEEONLINEYEAR;
			break;
		}
		return configEnum;
	}
}
