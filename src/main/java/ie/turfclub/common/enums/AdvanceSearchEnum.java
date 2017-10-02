package ie.turfclub.common.enums;

public enum AdvanceSearchEnum {

	ALLACARD(1, "All 'A' Card Holders"),
	ALLBCARD(2, "All 'B' Card Holders"),
	CURRENTACARD(3, "Current 'A' Card Holders"),
	CURRENTBCARD(4, "Current 'B' Card Holders"),
	ACARDRENEWALS(5, "'A' Card Holder Renewals"),
	BCARDRENEWALS(6, "'B' Card Holder Renewals"),
	LOSTCARDS(7, "Lost Cards");
	
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
	
	AdvanceSearchEnum(long id, String name) { this.id=id; this.name= name; }
	
	public static AdvanceSearchEnum[] ALL = {ALLACARD, ALLBCARD, CURRENTACARD, CURRENTBCARD, ACARDRENEWALS, BCARDRENEWALS, LOSTCARDS};
	
	public static String getNameById(String id) {
		switch (id) {
		case "1":
			return ALLACARD.getName();
		case "2":
			return ALLBCARD.getName();
		case "3":
			return CURRENTACARD.getName();
		case "4":
			return CURRENTBCARD.getName();
		case "5":
			return ACARDRENEWALS.getName();
		case "6":
			return BCARDRENEWALS.getName();
		case "7":
			return LOSTCARDS.getName();
		default:
			return null;
		}
	}
}
