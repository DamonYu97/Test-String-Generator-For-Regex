package operators;

import java.util.List;

import org.junit.jupiter.api.Test;

import dk.brics.automaton.OORegexConverter;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.oo.ooregex;
import regex.operators.CharacterClassRestriction;
import regex.operators.RegexMutator.MutatedRegExp;
import regex.utils.IteratorUtils;

public class CCRTest {
	static CharacterClassRestriction mutator = CharacterClassRestriction.mutator;
	
	@Test
	public void testMutateMix() {
		RegExp re1 = new RegExp("(a|A)");
		ooregex oore1 = OORegexConverter.getOORegex(re1);
		List<MutatedRegExp> m1 = IteratorUtils.iteratorToList(mutator.mutate(re1));
		System.out.println(m1);
		
		RegExp re2 = new RegExp("((d){1,3}'((d){3,3}')*(d){3,3}\\.(d){1,3}\\?|(d){1,3}(\\.(d){3,3})?)");
		ooregex oore2 = OORegexConverter.getOORegex(re2);
		List<MutatedRegExp> m2 = IteratorUtils.iteratorToList(mutator.mutate(re2));
		System.out.println(m2);
	}
}
