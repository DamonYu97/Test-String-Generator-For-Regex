package dk.brics.automaton;

import static dk.brics.automaton.OORegexConverter.escapeDq;
import static dk.brics.automaton.OORegexConverter.getOORegex;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import dk.brics.automaton.RegExp.Kind;
import dk.brics.automaton.oo.REGEXP_CHAR_RANGE;
import dk.brics.automaton.oo.ooregex;
import dk.brics.automaton.oo.oosimpleexp;

public class OORegexConverterTest {

	@Test
	public void testGetOORegexAB() {
		test("abc[a-b]");
	}

	@Test
	public void testGetOORegexDw() {
		test("\\w");
	}

	@Test
	public void testGetOO() {
		test("\"--help\"");
	}
	
	@Test
	public void testGetQUote() {
		test("lib(64)?/lib((.&~(\\/)))*\\\\.so\\\".(.)*");
	}
	
	
	
	private void test(String reg) {
		// get the regex
		RegExp re = new RegExp(reg);
		// transform to objects
		ooregex oor = getOORegex(re);
		// come faccio ad essere sicuro di acer convertiro in modo corretto
		// una possibilita' e' convertire indietro in stringhe
		// questo ancora fallisce
		// back to string
		String rs = ToRegexString.convertToRegexString(oor);
		System.out.println(reg + " --> " +rs);
		assertEquals(reg, rs);
	}

	@Test
	public void testInterval() {
		RegExp re = new RegExp("[a-z]");
		assertEquals(RegExp.Kind.REGEXP_CHAR_RANGE, re.kind);
		ooregex reoo = OORegexConverter.getOORegex(re);
		assertTrue(reoo instanceof REGEXP_CHAR_RANGE);
	}

	@Test
	public void testMissedRange() {
		RegExp re = new RegExp("a-z");
		assertEquals(RegExp.Kind.REGEXP_STRING, re.kind);
		ooregex reoo = OORegexConverter.getOORegex(re);
		assertTrue(reoo instanceof oosimpleexp);
	}

	@Test
	public void testComplement() {
		RegExp re = new RegExp("~[a-z]");
		assertEquals(RegExp.Kind.REGEXP_COMPLEMENT, re.kind);
		RunAutomaton ra = new RunAutomaton(re.toAutomaton());
		// assert ra.
		// ooregex reoo = OORegexConverter.getOORegex(re);
		// assertTrue(reoo instanceof REGEXP_COMPLEMENT);
	}

	@Test
	public void testNegateCharClass() {
		RegExp re = new RegExp("[^a-zA-Z]");
		// purtroppo costruisce una intesection
		assertEquals(RegExp.Kind.REGEXP_INTERSECTION, re.kind);
		System.out.println(re);
	}
	
}