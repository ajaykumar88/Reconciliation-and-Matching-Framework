package org.kew.shs.dedupl.matchers;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This abstract class defines a composite matcher, a wrapper for multiple matchers
 * @author nn00kg
 *
 */
public abstract class CompositeMatcher implements Matcher{

	protected List<Matcher> matchers;

	public abstract boolean matches(String s1, String s2);
	
	public List<Matcher> getMatchers() {
		return matchers;
	}

	public void setMatchers(List<Matcher> matchers) {
		this.matchers = matchers;
		Collections.sort(matchers,  new Comparator<Matcher>() {
	        public int compare(Matcher m1,Matcher m2) {
	        	return Integer.valueOf(m1.getCost()).compareTo(Integer.valueOf(m2.getCost()));
	        }
	    });
	}

	public boolean isExact() {
		boolean exact = true;
		for (Matcher m : matchers){
			exact = m.isExact();
			if (!exact)
				break;
		}
		return exact;
	}

	public int getCost() {
		int cost = 0;
		for (Matcher m : matchers)
			cost += m.getCost();
		return cost;
	}

	public String getExecutionReport() {
		StringBuffer sb = new StringBuffer();
		for (Matcher m : matchers)
			sb.append(m.getExecutionReport()).append("\n");
		return sb.toString();
	}

}