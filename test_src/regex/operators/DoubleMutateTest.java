package regex.operators;

import java.util.Iterator;

import org.junit.Test;

import dk.brics.automaton.ExtendedRegex;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.ToSimpleString;
import regex.operators.RegexMutator.MutatedRegExp;

/**
 * 
 * @author Yu Lilin
 * @date 2020.03.19
 *
 */
public class DoubleMutateTest {
	
	@Test
	public void testDoubleMutate() {
		//testWithRegex("[A-Za-z0-9](([_\\.\\-]?[a-zA-Z0-9]+)*)\\@([A-Za-z0-9]+)(([\\.\\-]?[a-zA-Z0-9]+)*)\\.([A-Za-z]{2,})");
		testWithRegex("(\\s*\\(?0\\d{4}\\)?\\s*\\d{6}\\s*)|(\\s*\\(?0\\d{3}\\)?\\s*\\d{3}\\s*\\d{4}\\s*)");
	}

	private void testWithRegex(String start) {
		// build the regexp
		RegExp correct = ExtendedRegex.getSimplifiedRegexp(start);
		// build mutations
		Iterator<MutatedRegExp> mutants = AllMutators.mutator.doubleMutate(correct);
		//Iterator<MutatedRegExp> mutants = WrongPrecedenceClosure.mutator.mutate(correct);
		int count = 0;
		while (mutants.hasNext()) {
			MutatedRegExp mut = mutants.next();
			System.out.println(mut);
			count++;
		}
		System.out.println(count);
	}
	
	
}
