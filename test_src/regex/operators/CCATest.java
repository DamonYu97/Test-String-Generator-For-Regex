package operators;

import java.util.List;

import dk.brics.automaton.OORegexConverter;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.oo.ooregex;
import org.junit.jupiter.api.Test;
import regex.operators.CharacterClassAddition;
import regex.operators.RegexMutator.MutatedRegExp;
import regex.utils.IteratorUtils;

public class CCATest {
	static CharacterClassAddition mutator = CharacterClassAddition.mutator;
	
	@Test
	public void testMutateMix() {
		RegExp re1 = new RegExp("[A-Za-z0-9](([_\\.\\-]?[a-zA-Z0-9]+)*)\\@([A-Za-z0-9]+)(([\\.\\-]?[a-zA-Z0-9]+)*)\\.([A-Za-z]{2,})");
		ooregex oore1 = OORegexConverter.getOORegex(re1);
		List<MutatedRegExp> m1 = IteratorUtils.iteratorToList(mutator.mutate(re1));
		System.out.println(m1);
		
	}
}
