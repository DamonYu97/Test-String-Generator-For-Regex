package operators;

import java.util.List;

import org.junit.Test;

import dk.brics.automaton.OORegexConverter;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.oo.ooregex;
import regex.operators.CharacterClassCreation;
import regex.operators.RegexMutator.MutatedRegExp;
import regex.utils.IteratorUtils;

public class CCCTest {
	static CharacterClassCreation mutator = CharacterClassCreation.mutator;
	
	@Test
	public void testMutateMix() {
		RegExp re1 = new RegExp("\"xd0xbfxd0xb5xd1x82xd1x80xd0xbexd0xb2xd1x81xd0xbaxd0xbe-xd1x80xd0xb0xd0xb7xd1x83xd0xbcxd0xbexd0xb2xd1x81xd0xba\"(\"xd0xb0xd1x8f\"|\"xd0xbexd0xb9\")");
		ooregex oore1 = OORegexConverter.getOORegex(re1);
		List<MutatedRegExp> m1 = IteratorUtils.iteratorToList(mutator.mutate(re1));
		System.out.println(m1);
		
	}
}
