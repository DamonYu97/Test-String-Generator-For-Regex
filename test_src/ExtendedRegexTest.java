


import dk.brics.automaton.Automaton;
import dk.brics.automaton.ExtendedRegex;
import dk.brics.automaton.RegExp;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExtendedRegexTest {

	@Test
	public void test_w() {
		RegExp res = ExtendedRegex.getSimplifiedRegexp("jjj\\w");
		Automaton automaton = res.toAutomaton();
		assertTrue(automaton.run("jjja"));		
	}
	@Test
	public void test_s() {
		RegExp res = ExtendedRegex.getSimplifiedRegexp("(\\s|\\n|^)(\\w+://[^\\s\\n]+)");
		Automaton automaton = res.toAutomaton();
		System.out.println(res);
	}

}