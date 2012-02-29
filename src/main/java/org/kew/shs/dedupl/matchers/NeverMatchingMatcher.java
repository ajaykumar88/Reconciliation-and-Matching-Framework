package org.kew.shs.dedupl.matchers;

/**
 * This matcher returns false in all cases.
 * @author nn00kg
 *
 */
public class NeverMatchingMatcher implements Matcher {

	public static int COST = 0;
	
	public int getCost() {
		return COST;
	}

	public boolean matches(String s1, String s2) {
		return false;
	}

	public boolean isExact() {
		return true;
	}

	public String getExecutionReport() {
		return null;
	}
	
}