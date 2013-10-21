package atmosphere.android.constant;

public enum AtmosAction {
	FUN("fun"), GOOD("good"), MEMO("memo"), USE_FULL("usefull"), ;

	private String value;

	private AtmosAction(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
