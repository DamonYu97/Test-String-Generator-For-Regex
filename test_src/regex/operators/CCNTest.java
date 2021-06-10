package operators;

import dk.brics.automaton.OORegexConverter;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.oo.ooregex;
import org.junit.Test;
import regex.operators.CharacterClassNegation;
import regex.operators.RegexMutator;
import regex.utils.IteratorUtils;

import java.util.List;

public class CCNTest {
    static CharacterClassNegation mutator = CharacterClassNegation.mutator;

    @Test
    public void testMutateMix() {
        RegExp re1 = new RegExp("[a-zA-Z]|cd");
        ooregex oore1 = OORegexConverter.getOORegex(re1);
        List<RegexMutator.MutatedRegExp> m1 = IteratorUtils.iteratorToList(mutator.mutate(re1));
        System.out.println(m1);

    }
}
