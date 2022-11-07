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

	public Iterator<MutatedRegExp> secondMutate(MutatedRegExp re) {
		//mutate
		List<ooregex> results = OORegexConverter.getOORegex(re.mutatedRexExp).accept(mutator);

		// randomly select first n * RATE mutants from the results.
		Collections.shuffle(results);
		final double RATE = 0.25;
		int requiredNumOfMutants = (int) Math.ceil(results.size() * RATE);
		results = results.subList(0, requiredNumOfMutants);

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
				return new MutatedRegExp(re.description + "+" +  mutator.getCode(), s);
			}
		};
	}


	private Iterator<MutatedRegExp> mutate(RegExp re, boolean shuffle) {
		List<ooregex> results = OORegexConverter.getOORegex(re).accept(mutator);

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
