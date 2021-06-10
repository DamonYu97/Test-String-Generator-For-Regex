package regex.operators;

import java.util.ArrayList;
import java.util.List;

import dk.brics.automaton.oo.REGEXP_CHAR_RANGE;
import dk.brics.automaton.oo.ooregex;

/**
 * the user has used a char range but has a wrong boundary (but the new
 * mutation will be a char again)
 * 
 * In MUTATION 2017 is RM
 *
 */
public class RangeModification extends RegexMutator {
	public static RangeModification mutator = new RangeModification();

	private RangeModification() {
		super(new RangeModificationVisitor());
	}

	static class RangeModificationVisitor extends RegexVisitorAdapterList {

		@Override
		public List<ooregex> visit(REGEXP_CHAR_RANGE r) {
			List<ooregex> result = new ArrayList<ooregex>();
			char[] fromV = vary(r.from);
			char[] toV = vary(r.to);
			// System.out.println(r.from + " " + r.to + " " +
			// Arrays.toString(fromV));
			for (char c : fromV) {
				if (c <= r.to)
					result.add(new REGEXP_CHAR_RANGE(c, r.to));
			}
			for (char c : toV) {
				if (r.from <= c)
					result.add(new REGEXP_CHAR_RANGE(r.from, c));
			}
			/*if (fromV.length > 0 && fromV[0] < r.from) {
				result.add(new REGEXP_CHAR_RANGE(fromV[0], r.to));
			}
			if (toV.length > 0 && r.to < toV[toV.length-1]) {
				result.add(new REGEXP_CHAR_RANGE(r.from, toV[toV.length-1]));
			}*/
			return result;
		}

		// varia c in modo + o -1 ma che sia ancora un carattere leggibile
		private char[] vary(char c) {
			if ((c > 'a' && c < 'z') || (c > 'A' && c < 'Z') || (c > '0' && c < '9'))
				return new char[] { (char) (c - 1), (char) (c + 1) };
			if (c == 'z' || c == 'Z' || c == '9')
				return new char[] { (char) (c - 1) };
			if (c == 'a' || c == 'A' || c == '0')
				return new char[] { (char) (c + 1) };
			return new char[] {};
		}

		@Override
		public String getCode() {
			return "RM";
		}
	}

	@Override
	public String getCode() {
		return "RM";
	}
}