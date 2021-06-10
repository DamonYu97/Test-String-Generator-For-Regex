package regex.operators;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import dk.brics.automaton.ExtendedRegex;
import dk.brics.automaton.OORegexConverter;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.ToSimpleString;
import dk.brics.automaton.oo.ooregex;

/**
 * 
 * @modified by Yu Lilin at 2020.03.27
 *
 */
public abstract class RegexMutator {

	private RegexVisitorAdapterList mutator;
	
	public RegexVisitorAdapterList getMutator() {
		return mutator;
	}

	protected RegexMutator(RegexVisitorAdapterList v) {
		mutator = v;
	}

	/** given a regex, it builds all the possible mutations */
	public Iterator<MutatedRegExp> mutate(RegExp re) {
		return mutate(re, false);
	}

	public Iterator<MutatedRegExp> mutateRandom(RegExp re) {
		return mutate(re, true);
	}
	
	//modified by Yu Lilin
	private Iterator<MutatedRegExp> mutate(RegExp re, boolean shuffle) {
		List<ooregex> results = OORegexConverter.getOORegex(re).accept(mutator);
		//modified
		//if it is M2C, then only last mutated expression makes sense
		//int size = results.size();
		/*if (size > 0 && (mutator.getCode().equals("M2C") || mutator.getCode().equals("C2M"))) {
			results.subList(0, size - 1).clear();
		} */

		if (shuffle) {
			Collections.shuffle(results);
		}

		final Iterator<ooregex> resultsOO = results.iterator();
		return new Iterator<MutatedRegExp>() {

			@Override
			public boolean hasNext() {
				return resultsOO.hasNext();
			}

			@Override
			public MutatedRegExp next() {
				RegExp s = OORegexConverter.convertBackToRegex(resultsOO.next());
				// return new
				// MutatedRegExp(mutator.getClass().getEnclosingClass().getSimpleName(),
				// new RegExp(s));
				return new MutatedRegExp(mutator.getCode(), s);
			}
		};
	}
	
	//make sure any mutator only generate one mutated expression, if more than one expressions,
	//then randomly choose one from them. Added by Yu Lilin
	public MutatedRegExp mutateSingle(RegExp re) {
		List<ooregex> results = OORegexConverter.getOORegex(re).accept(mutator);
		int size = results.size();
		if (size == 0) {
			return null;
		} else if (size == 1) {
			RegExp s = OORegexConverter.convertBackToRegex(results.get(0));
			return new MutatedRegExp(mutator.getCode(), s);
		} else {
			Random rnd = new Random();
			int selectedPos = rnd.nextInt(size);
			RegExp s = OORegexConverter.convertBackToRegex(results.get(selectedPos));
			return new MutatedRegExp(mutator.getCode(), s);
		}
	}

	static public class MutatedRegExp {// extends RegExp{
		public String description;
		public RegExp mutatedRexExp;

		public MutatedRegExp(String simpleName, RegExp regex) {
			// super(regex);
			description = simpleName;
			mutatedRexExp = regex;
		}

		@Override
		public String toString() {
			return description + ": " + ToSimpleString.convertToReadableString(mutatedRexExp);
		}
	}

	public abstract String getCode();
}