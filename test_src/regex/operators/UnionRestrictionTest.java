package regex.operators;

import java.util.List;

import org.junit.Test;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.OORegexConverter;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.oo.ooregex;
import regex.operators.RegexMutator.MutatedRegExp;
import regex.utils.IteratorUtils;

/**
 * 
 * @author Yu Lilin
 * @date 2020.03.14
 *
 */
public class UnionRestrictionTest {
	static UnionRestriction mutator = UnionRestriction.mutator;
	
	@Test
	public void testMutateMix() {
		RegExp re1 = new RegExp("(ab|[0-9]de(xy)*)");
		//ooregex oore1 = OORegexConverter.getOORegex(re1);
		List<MutatedRegExp> m1 = IteratorUtils.iteratorToList(mutator.mutateRandom(re1));
		System.out.println(m1);
		
		/*RegExp re2 = new RegExp("((([0-9]{1,3})(,[0-9]{3})*)|([0-9]+))([0-9]+)?");
		ooregex oore2 = OORegexConverter.getOORegex(re2);
		List<MutatedRegExp> m2 = IteratorUtils.iteratorToList(mutator.mutate(re2));
		Automaton a1 = re2.toAutomaton();
		Automaton a2 = m2.get(0).mutatedRexExp.toAutomaton();
		System.out.println(a2.minus(a1));
		System.out.println(m2);*/
	}
	

}
