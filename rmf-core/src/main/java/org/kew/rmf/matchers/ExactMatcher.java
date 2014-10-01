package org.kew.rmf.matchers;

import org.apache.commons.lang.StringUtils;

/**
 * This matcher tests for equality between the two inputs (exact matches).
 * @author nn00kg
 *
 */
public class ExactMatcher implements Matcher {

	public static int COST = 0;
	protected boolean blanksMatch = true;

	@Override
	public int getCost() {
		return COST;
	}

	@Override
	public boolean matches(String s1, String s2) {
		boolean matches = false;
		//if (s1 == null && s2 == null && this.blanksMatch) {
		if (this.blanksMatch && (StringUtils.isBlank(s1) || StringUtils.isBlank(s2))) {
			matches = true;
		} else{
			try{
				matches = s1.equals(s2);
			}
			catch (Exception e) {
				;
			}
		}
		return matches;
	}

	@Override
	public boolean isExact() {
		return true;
	}

	@Override
	public String getExecutionReport() {
		return null;
	}
	
	public void setBlanksMatch(boolean blanksMatch) {
		this.blanksMatch = blanksMatch;
	}
}