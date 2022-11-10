package regex.operators;

import java.util.Collections;
import java.util.List;

import dk.brics.automaton.OORegexConverter;
import dk.brics.automaton.oo.REGEXP_CHAR;
import dk.brics.automaton.oo.REGEXP_CHAR_RANGE;
import dk.brics.automaton.oo.REGEXP_INTERSECTION;
import dk.brics.automaton.oo.REGEXP_UNION;
import dk.brics.automaton.oo.ooregex;
import dk.brics.automaton.oo.oosimpleexp;

/**
 *
 * @author Yu Lilin
 * @data 2020.03.10
 *
 * The operation CharacterClass2Concatenation transforms a regular expression r contains u
 * interpreted as a character class to a regular expression r' in which u is interpreted as
 * a concatenation. i.e. transform [] to ()
 * e.g. [abc] ---> (abc)
 *
 */
public class CharacterClass2Group extends RegexMutator {
	public static CharacterClass2Group mutator = new CharacterClass2Group();

	private CharacterClass2Group() {
		super(new CharacterClass2SimpleexpVisitor());
	}

	/**
	 *
	 * A subclass to visit regular expressions
	 * Change [] to ()
	 * i.e. return concatenation when visit union, range, and negated class
	 */
	static class CharacterClass2SimpleexpVisitor extends RegexVisitorAdapterList {

		/**
		 * If visit a character class, return a concatenation of elements in this class
		 */
		@Override
		public List<ooregex> visit(REGEXP_UNION r) {
			//transform r to a string without brackets of its original form ((a|b)|c) --> abc
			String newExpString = toConcatenationString(r);
			if (newExpString == null) {
				return Collections.EMPTY_LIST;
			}
			if (newExpString.charAt(0) == '\\' && newExpString.length() == 2) {
				return Collections.EMPTY_LIST;
			}
			try {
				//construct a new regular expression according to newExpString
				ooregex newRE = OORegexConverter.getOOExtRegex(newExpString);
				//System.out.println(newExpString);
				return Collections.singletonList((ooregex) newRE);
			} catch (Exception e) { 
				System.out.println(e.getMessage());
			}
			return Collections.EMPTY_LIST;
		}

		/**
		 *
		 * @param REGEXP_UNION r
		 * @return a {@code String} indicates r's original form without brackets
		 */
		private String toConcatenationString(REGEXP_UNION r) {
			String newExpString = "";
			//divide r into a list of single elements. e.g. [abc] -> [a, b, c]
			List<ooregex> parts = REGEXP_UNION.splitUnion(r);
			for (ooregex part: parts) {
				if (part instanceof REGEXP_CHAR_RANGE) {
					//convert REGEXP_CHAR_RANGE to simpleexp. e.g. [a-z] - > "a-z"
					REGEXP_CHAR_RANGE range = (REGEXP_CHAR_RANGE)part;
					newExpString += range.from + "-" + range.to;
				} else if (part instanceof REGEXP_CHAR) {
					newExpString += ((REGEXP_CHAR) part).c;
				} else {
					//if r is not a character class. e.g. (ab|c)
					newExpString = null;
					break;
				}
			}
			return newExpString;
		}

		/**
		 * if visit a single range like [a-z], return a simple expression "a-z"
		 */
		@Override
		public List<ooregex> visit(REGEXP_CHAR_RANGE r) {
			String simpleString = r.from + "-" + r.to;
			//construct a simple expression according to simpleString
			ooregex simpleexp = oosimpleexp.createoosimpleexp(simpleString);
			return Collections.singletonList((ooregex) simpleexp);
		}

		/**
		 * If visit a negated character class, return a concatenation of elements in this class
		 */
		@Override
		public List<ooregex> visit(REGEXP_INTERSECTION r) {
			//if r is a negated character class
			//transform r to a string without brackets of its original form .&~((a|b)|c) (i.e. [^abc]) --> ^abc
			ooregex ncc = r.getNegatedCharClass();
			if (ncc != null) { //if r is a negated character class
				String newExpString = null;
				if (ncc instanceof REGEXP_CHAR) { //[^a]
					newExpString = "^" + ((REGEXP_CHAR)ncc).c;
					ooregex simpleexp = oosimpleexp.createoosimpleexp(newExpString);
					return Collections.singletonList((ooregex) simpleexp);
				} else if (ncc instanceof REGEXP_CHAR_RANGE) { //[^a-z]
					REGEXP_CHAR_RANGE range = (REGEXP_CHAR_RANGE)ncc;
					newExpString = "^" + range.from + "-" + range.to;
					ooregex simpleexp = oosimpleexp.createoosimpleexp(newExpString);
					return Collections.singletonList((ooregex) simpleexp);
				} else { // [^abc]
					newExpString = toConcatenationString((REGEXP_UNION)ncc);

					if (newExpString == null) {
						return Collections.EMPTY_LIST;
					}
					newExpString = "^" + newExpString; //^abc
					//System.out.println(newExpString);
					try {
						//construct a new regular expression according to conString
						ooregex re = OORegexConverter.getOOExtRegex(newExpString);
						return Collections.singletonList((ooregex) re);
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
				}
			}

			return Collections.EMPTY_LIST;
		}

		@Override
		public String getCode() {
			return "CC2G";
		}
	}

	@Override
	public String getCode() {
		// TODO Auto-generated method stub
		return "CC2G";
	}

}
