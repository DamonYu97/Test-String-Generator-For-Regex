package regex.operators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import dk.brics.automaton.OORegexConverter;
import dk.brics.automaton.oo.REGEXP_CHAR;
import dk.brics.automaton.oo.REGEXP_CHAR_RANGE;
import dk.brics.automaton.oo.REGEXP_CONCATENATION;
import dk.brics.automaton.oo.REGEXP_REPEAT;
import dk.brics.automaton.oo.REGEXP_UNION;
import dk.brics.automaton.oo.oobinregex;
import dk.brics.automaton.oo.ooregex;
import dk.brics.automaton.oo.oosimpleexp;

/**
 * we want to make the expression case unsensitive
 *
 * In MUTATION 2017 is CA
 * 
 * modified by Yu Lilin
 */
public class CaseAddition extends RegexMutator {
	public static CaseAddition mutator = new CaseAddition();

	private CaseAddition() {
		super(new CaseAdditionVisitor());
	}

	static class CaseAdditionVisitor extends RegexVisitorAdapterList {
		
		//consider letter in class
		//added by Yu Lilin
		@Override
		public List<ooregex> visit(REGEXP_UNION r) {
			List<ooregex> result = new ArrayList<ooregex>();
			List<ooregex> parts = REGEXP_UNION.splitUnion(r);
			Vector<REGEXP_CHAR_RANGE> rangeVector = new Vector<REGEXP_CHAR_RANGE>();
			Vector<Character> charVector = new Vector<Character>();
			for (ooregex part: parts) {
				if (part instanceof REGEXP_CHAR) {
					char c = ((REGEXP_CHAR)part).c;
					charVector.add(c);
				} else if (part instanceof REGEXP_CHAR_RANGE) {
					rangeVector.add((REGEXP_CHAR_RANGE)part);
				}
			}
			for (int i = 0; i < parts.size(); i++) {
				ooregex part = parts.get(i);
				if (part instanceof REGEXP_CHAR_RANGE) {
					REGEXP_CHAR_RANGE range = (REGEXP_CHAR_RANGE)part;
					List<ooregex> newRangeList = visit(range);
					if (newRangeList.size() > 0) {
						REGEXP_UNION newRangeUnion = (REGEXP_UNION) newRangeList.get(0);
						REGEXP_CHAR_RANGE newRange = (REGEXP_CHAR_RANGE)newRangeUnion.exp2;
						if (!rangeVector.contains(newRange)) {
							parts.set(i, newRangeUnion);
							result.add(oobinregex.makeBinExpression(REGEXP_UNION.class, parts));
							parts.set(i, part);
						} 
					} 
				} else if (part instanceof REGEXP_CHAR) {
					REGEXP_CHAR c = (REGEXP_CHAR)part;
					List<ooregex> newCharList = visit(c);
					if (newCharList.size() > 0) {
						REGEXP_UNION newCharUnion = (REGEXP_UNION) newCharList.get(0);
						REGEXP_CHAR newChar = (REGEXP_CHAR)newCharUnion.exp2;
						if (!charVector.contains(newChar.c)) {
							parts.set(i, newCharUnion);
							result.add(oobinregex.makeBinExpression(REGEXP_UNION.class, parts));
							parts.set(i, part);
						} 
					} 
				} else if(part instanceof REGEXP_CONCATENATION){
					//System.out.println(part);
					REGEXP_CONCATENATION con = (REGEXP_CONCATENATION)part;
					for (ooregex subPart: visit(con)) {
						parts.set(i, subPart);
						result.add(oobinregex.makeBinExpression(REGEXP_UNION.class, parts));
					}
					parts.set(i, part);
				} else if(part instanceof oosimpleexp){
					//System.out.println(part);
					oosimpleexp simple = (oosimpleexp)part;
					for (ooregex subPart: visit(simple)) {
						parts.set(i, subPart);
						result.add(oobinregex.makeBinExpression(REGEXP_UNION.class, parts));
					}
					parts.set(i, part);
				} else if(part instanceof REGEXP_REPEAT){
					//System.out.println(part);
					REGEXP_REPEAT repeat = (REGEXP_REPEAT)part;
					for (ooregex subPart: visit(repeat)) {
						parts.set(i, subPart);
						result.add(oobinregex.makeBinExpression(REGEXP_UNION.class, parts));
					}
					parts.set(i, part);
				} else {
					//if r is not a character class. e.g. (ab|c)
					//return Collections.EMPTY_LIST;
				}
			}
			return result;
		}

		@Override
		public List<ooregex> visit(REGEXP_CHAR_RANGE r) {
			// add also the
			CaseChar fromCase = caseChar(r.from);
			if (fromCase == CaseChar.NOT_ALPHABETIC)
				return Collections.EMPTY_LIST;
			CaseChar toCase = caseChar(r.to);
			if (toCase == CaseChar.NOT_ALPHABETIC)
				return Collections.EMPTY_LIST;
			if (fromCase == toCase) {
				REGEXP_CHAR_RANGE ooRegexChangedCase = new REGEXP_CHAR_RANGE(changeCase(r.from), changeCase(r.to));
				return Collections.singletonList((ooregex) new REGEXP_UNION(r, ooRegexChangedCase));
			}
			return Collections.EMPTY_LIST;
		}

		@Override
		public List<ooregex> visit(REGEXP_CHAR r) {
			Character changeCase = changeCase(r.c);
			if (changeCase == null) {
				return Collections.EMPTY_LIST;
			}
			ooregex ooRegexChangedCase = new REGEXP_CHAR(changeCase);
			return Collections.singletonList((ooregex) new REGEXP_UNION(r, ooRegexChangedCase));
		}

		@Override
		public List<ooregex> visit(oosimpleexp r) {
			List<ooregex> result = new ArrayList<>();
			for (int i = 0; i < r.s.length(); i += 10) {
				List<ooregex> parts = new ArrayList<>();
				int index = (i + 10) <= r.s.length()? i + 5 : i + (r.s.length() - i) / 2;
				if (index > 0) {
					String first = r.s.substring(0, index);
					parts.add(oosimpleexp.createoosimpleexp(first));
				}
				char mid = r.s.charAt(index);
				Character nc = changeCase(mid);
				// if the char cannot be changed in the case, leave as it is
				if (nc == null)
					continue;
				parts.add(OORegexConverter.getOORegex("[" + mid + nc + "]"));
				if (index < r.s.length() - 1) {
					String last = r.s.substring(index + 1, r.s.length());
					parts.add(oosimpleexp.createoosimpleexp(last));
				}
				// only one char
				if (parts.size() == 1)
					result.add(parts.get(0));
				else 
					result.add(oobinregex.makeBinExpression(REGEXP_CONCATENATION.class, parts));
			}
			return result;
		}

		@Override
		public String getCode() {
			return "CA";
		}
	}

	public static CaseChar caseChar(char c) {
		if (Character.isLowerCase(c))
			return CaseChar.LOWERCASE;
		if (Character.isUpperCase(c))
			return CaseChar.UPPERCASE;
		return CaseChar.NOT_ALPHABETIC;
	}

	public static Character changeCase(char c) {
		if (Character.isLowerCase(c))
			return Character.toUpperCase(c);
		if (Character.isUpperCase(c))
			return Character.toLowerCase(c);
		return null;
	}

	enum CaseChar {
		UPPERCASE, LOWERCASE, NOT_ALPHABETIC
	}

	@Override
	public String getCode() {
		return "CA";
	}
}
