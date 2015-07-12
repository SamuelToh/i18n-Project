package qut.localization.service.data.repository;

public abstract class DataManager {

	public static final int PATTERN_UPDATE_FAIL_PAT_NOT_FOUND = 100;
	public static final int PATTERN_CREATE_FAIL = -2;
	public static final int PATTERN_UPDATE_FAIL = -1;
	public static final int PATTERN_CREATED = 1;
	public static final int PATTERN_UPDATED = 2;
	public static final int PATTERN_DELETED = 3;
	
	
	protected static String FILTER_BY_PATTERN_NAME = "name == '%s'";
	
	protected static String getNameFilter(String patternName){
		return String.format(FILTER_BY_PATTERN_NAME, patternName);
	}
	
}
