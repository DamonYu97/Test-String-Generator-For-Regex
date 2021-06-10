package regex.operators;

import java.util.List;

import org.junit.Test;

import dk.brics.automaton.OORegexConverter;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.oo.ooregex;
import regex.operators.RegexMutator.MutatedRegExp;
import regex.utils.IteratorUtils;

public class QCTest {
	static QuantifierChange mutator = QuantifierChange.mutator;
	
	@Test
	public void testMutateMix() {
		RegExp re1 = new RegExp("[a-zA-Z0-9]+([a-zA-Z0-9\\-\\.]+)?\\.(com|org|net|mil|edu|COM|ORG|NET|MIL|EDU)");
		ooregex oore1 = OORegexConverter.getOORegex(re1);
		List<MutatedRegExp> m1 = IteratorUtils.iteratorToList(mutator.mutate(re1));
		System.out.println(m1);
		
	}
}
