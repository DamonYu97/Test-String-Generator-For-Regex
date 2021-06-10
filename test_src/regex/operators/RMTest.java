package operators;

import dk.brics.automaton.OORegexConverter;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.oo.ooregex;
import org.junit.jupiter.api.Test;
import regex.operators.RangeModification;
import regex.operators.RegexMutator;
import regex.utils.IteratorUtils;

import java.util.List;

public class RMTest {
    public RangeModification mutator = RangeModification.mutator;

    @Test
    public void test() {
        RegExp re1  = new RegExp("[0-9]");
        ooregex oore1 = OORegexConverter.getOORegex(re1);
        List<RegexMutator.MutatedRegExp> m1 = IteratorUtils.iteratorToList(mutator.mutate(re1));
        System.out.println(m1);
    }
}
