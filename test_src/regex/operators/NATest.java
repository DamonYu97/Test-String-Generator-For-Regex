package operators;

import java.util.List;

import org.junit.jupiter.api.Test;

import dk.brics.automaton.OORegexConverter;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.oo.ooregex;
import regex.operators.NegationAddition;
import regex.operators.RegexMutator.MutatedRegExp;
import regex.utils.IteratorUtils;

public class NATest {
static NegationAddition mutator = NegationAddition.mutator;
	
	@Test
	public void testMutateMix() {
		RegExp re1 = new RegExp("[a-zA-Z]");
		ooregex oore1 = OORegexConverter.getOORegex(re1);
		List<MutatedRegExp> m1 = IteratorUtils.iteratorToList(mutator.mutate(re1));
		System.out.println(m1);
		
	}
}
