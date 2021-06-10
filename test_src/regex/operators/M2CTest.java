package operators;

import java.util.List;

import org.junit.jupiter.api.Test;

import dk.brics.automaton.OORegexConverter;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.oo.ooregex;
import regex.operators.MetaChar2Char;
import regex.operators.RegexMutator.MutatedRegExp;
import regex.utils.IteratorUtils;

public class M2CTest {
	static MetaChar2Char mutator = MetaChar2Char.mutator;
	@Test
	public void testMutateMix() {
		RegExp re1 = new RegExp("[0-9].");
		ooregex oore1 = OORegexConverter.getOORegex(re1);
		List<MutatedRegExp> m1 = IteratorUtils.iteratorToList(mutator.mutateRandom(re1));
		for(MutatedRegExp m: m1) {
			System.out.print(m.mutatedRexExp + "\t");
		}
		System.out.println();
		
		/*RegExp re2 = new RegExp("(\\d{1,3}'(\\d{3}')*\\d{3}(\\.\\d{1,3})?|\\d{1,3}(\\.\\d{3})?)");
		ooregex oore2 = OORegexConverter.getOORegex(re2);
		List<MutatedRegExp> m2 = IteratorUtils.iteratorToList(mutator.mutate(re2));
		for(MutatedRegExp m: m2) {
			System.out.print(m.mutatedRexExp + "\t");
		}*/
	}
}
