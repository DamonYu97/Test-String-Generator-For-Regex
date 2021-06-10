package dk.brics.automaton.oo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * all the regexes with two arguments
 */
public abstract class oobinregex extends ooregex {
	public ooregex exp1;
	public ooregex exp2;

	oobinregex(ooregex ooRegex, ooregex ooRegex2) {
		exp1 = ooRegex;
		exp2 = ooRegex2;
	}

	/** in case of concattion also non concattaion can be rstunred (for example two strings*/
	public static ooregex makeBinExpression(Class<? extends ooregex> clazz, ooregex e1, ooregex e2) {
		if (clazz == REGEXP_CONCATENATION.class) {
			// the concatenation could actually generate ONE regex that is not binary
			return REGEXP_CONCATENATION.makeREGEXP_CONCATENATION(e1, e2);
			//PA 2018/08/05
			//it doesn't work because of the return type
			/*if(e1 instanceof oosimpleexp && e2 instanceof oosimpleexp) {
				return oosimpleexp.createoosimpleexp(((oosimpleexp)e1).s + ((oosimpleexp)e2).s);
			}
			else {
				return (T) new REGEXP_CONCATENATION(e1, e2);
			}*/
		} else if (clazz == REGEXP_UNION.class) {
			return new REGEXP_UNION(e1, e2);
		} else if (clazz == REGEXP_INTERSECTION.class) {
			return new REGEXP_INTERSECTION(e1, e2);
		}
		throw new RuntimeException("class of binary " + clazz.getName());
	}

	/**
	 * in case a list is given build the union
	 * 
	 * @param regexpes
	 * @return
	 */
	public static ooregex makeBinExpression(Class<? extends ooregex> clazz, List<ooregex> regexpes) {
		assert regexpes.size() >= 2;
		int last = regexpes.size() - 1;
		ooregex result = regexpes.get(last);
		// try to build in the same order as is given in the
		for (int i = last - 1; i >= 0; i--) {
			result = makeBinExpression(clazz, regexpes.get(i), result);
		}
		return result;
	}

	// split in case there are a concatenation of same opratoro
	public static List<ooregex> split(ooregex r, Class<? extends oobinregex> clazz) {
		if (r.getClass() == clazz) {
			List<ooregex> prefixes = new ArrayList<ooregex>();
			prefixes.addAll(split(((oobinregex) r).exp1,clazz));
			prefixes.addAll(split(((oobinregex) r).exp2,clazz));
			return prefixes;
		} else {
			return Collections.singletonList(r);
		}
	}

}