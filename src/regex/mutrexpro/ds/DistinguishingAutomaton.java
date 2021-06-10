package regex.mutrexpro.ds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dk.brics.automaton.Automaton;
import regex.distinguishing.DistStringCreator;
import regex.operators.RegexMutator.MutatedRegExp;

public class DistinguishingAutomaton {
	public boolean positive;
	private char niceChar;
	private Automaton content;
	List<MutatedRegExp> mutatedRegexes;
	// solution 2: invalidating the da
	// boolean isActive;

	/**
	 * 
	 * @param rexAut
	 * @param b
	 *            positive? those that generate strings accepted by regex and
	 *            rejected by mutations are positive // those that generate
	 *            strings rejected by regex and accepted by // mutations
	 */
	// public DistinguishingAutomaton(RegExp regex, Automaton rexAut, Automaton
	// rexNegAut, boolean b) {
	public DistinguishingAutomaton(RegexWAutomata r, boolean b, char niceChar) {
		positive = b;
		this.niceChar = niceChar;
		// content = b ? rexAut : rexAut.complement();

		if (positive) {
			content = r.getmAut();
		} else {
			content = r.getNegMaut();
		}

		mutatedRegexes = new ArrayList<>();
		// solution 2: invalidating the da
		// isActive = true;
	}

	/**
	 * Adds the mutant
	 *
	 * @param mutant
	 *            the mutant (as regex)
	 * @param mAut
	 *            the automaton (passed in order to avoid to build each automata
	 *            many times)
	 * @param negMaut
	 *            the negated automaton (passed in order to avoid to build each
	 *            automata many times)
	 * @return true, if successful
	 */
	// public boolean add(RegExp mutant, Automaton mAut, Automaton negMaut) {
	public boolean add(String mutantDescription, RegexWAutomata mutant) {
		// try to add
		Automaton result;
		if (positive) {
			result = content.intersection(mutant.getNegMaut());
		} else {
			result = content.intersection(mutant.getmAut());
		}
		// if no intersection is possible
		if (result.isEmpty()) {
			return false;
		}
		mutatedRegexes.add(new MutatedRegExp(mutantDescription, mutant.getMutant()));
		content = result;
		return true;
	}

	public String getExample() {
		return DistStringCreator.getExample(content, niceChar);
	}

	public boolean isPositive() {
		return positive;
	}

	public List<MutatedRegExp> getMutants() {
		return Collections.unmodifiableList(mutatedRegexes);
	}
	
	public int size() {
		return mutatedRegexes.size();
	}
}