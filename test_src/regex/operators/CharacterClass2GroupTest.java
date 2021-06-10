package regex.operators;

import java.util.List;

import org.junit.Test;

import dk.brics.automaton.ExtendedRegex;
import dk.brics.automaton.OORegexConverter;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.oo.REGEXP_CHAR_RANGE;
import dk.brics.automaton.oo.REGEXP_UNION;
import dk.brics.automaton.oo.ooregex;
import regex.operators.RegexMutator.MutatedRegExp;
import regex.utils.IteratorUtils;

/**
 * 
 * @author Yu Lilin
 * @date 2020.03.12
 *
 */
public class CharacterClass2GroupTest extends RegexMutationTest{
	static CharacterClass2Group mutator = CharacterClass2Group.mutator;
	
	@Test
	public void testMutateRange() {
		RegExp re = new RegExp("[a-z]");
		ooregex oore = OORegexConverter.getOORegex(re);
		assert oore instanceof REGEXP_CHAR_RANGE;
		List<MutatedRegExp> m = IteratorUtils.iteratorToList(mutator.mutate(re));
		System.out.println("mutate [a-z] : " + m);
		assertOneEqualTo(m, "a-z");
	}
	
	@Test
	public void testMutateSimpleClass() {
		RegExp re = new RegExp("[abc]");
		ooregex oore = OORegexConverter.getOORegex(re);
		List<MutatedRegExp> m = IteratorUtils.iteratorToList(mutator.mutate(re));
		System.out.println("mutate [abc] : " + m);
		assertOneEqualTo(m, "abc");
	}
	
	@Test
	public void testMutateAnySingleCharacterClass() {
		RegExp re = new RegExp("[.a]");
		ooregex oore = OORegexConverter.getOORegex(re);
		List<MutatedRegExp> m = IteratorUtils.iteratorToList(mutator.mutate(re));
		System.out.println("mutate [.a] : " + m);
		assertOneEqualTo(m, ".a");
	}
	
	@Test
	public void testMutateAnyStringClass() {
		RegExp re = new RegExp("[@efg]");
		ooregex oore = OORegexConverter.getOORegex(re);
		List<MutatedRegExp> m = IteratorUtils.iteratorToList(mutator.mutate(re));
		System.out.println("mutate [@efg] : " + m);
		assertOneEqualTo(m, "@efg");
	}
	
	@Test
	public void testMutateNestedClass() {
		RegExp re = new RegExp("[[0-9]a]");
		ooregex oore = OORegexConverter.getOORegex(re);
		List<MutatedRegExp> m = IteratorUtils.iteratorToList(mutator.mutate(re));
		System.out.println("mutate [[0-9]] : " + m);
	}
	
	@Test
	public void testMutateInsideRepeatClass() {
		RegExp re = new RegExp("[c(ab)*]");
		ooregex oore = OORegexConverter.getOORegex(re);
		List<MutatedRegExp> m = IteratorUtils.iteratorToList(mutator.mutate(re));
		System.out.println("mutate [c(ab)*] : " + m);
		assertOneEqualTo(m, "c(ab)*");
	}
	
	@Test
	public void testMutateOutsideRepeatClass() {
		RegExp re = new RegExp("[abc]?");
		ooregex oore = OORegexConverter.getOORegex(re);
		List<MutatedRegExp> m = IteratorUtils.iteratorToList(mutator.mutate(re));
		System.out.println("mutate [abc]? : " + m);
		assertOneEqualTo(m, "(abc)?");
	}
	
	@Test
	public void testMutateNegatedClass() {
		RegExp re = new RegExp("[^abc]");
		ooregex oore = OORegexConverter.getOORegex(re);
		List<MutatedRegExp> m = IteratorUtils.iteratorToList(mutator.mutate(re));
		System.out.println("mutate [^abc] : " + m);
		assertOneEqualTo(m, "^abc");
	}
	
	
	@Test
	public void testMutateMiexedClass() {
		RegExp re = new RegExp("((([0-9]{1,3})(,\\d{3})*)|(\\d+))(.[0-9]+)?");
		//RegExp re = new RegExp("[abcA-Z0(.|\\+)*]{2}07[abc]");
		ooregex oore = OORegexConverter.getOORegex(re);
		List<ooregex> m = oore.accept(mutator.getMutator());
		System.out.println("mutate [a-z]90[123] : " + m);
	}
	
	@Test
	public void testReglibAddressPhone1() {
		String regex = "[\\\\(]{0,1}([0-9]){3}[\\\\)]{0,1}[ ]?([^0-1]){1}([0-9]){2}[ ]?[-]?[ ]?([0-9]){4}[ ]*((x){0,1}([0-9]){1,5}){0,1}";
		RegExp re = new RegExp(regex);
		//RegExp re = new RegExp("[abcA-Z0(.|\\+)*]{2}07[abc]");
		ooregex oore = OORegexConverter.getOORegex(re);
		List<ooregex> m = oore.accept(mutator.getMutator());
		System.out.println(m);
	}
	
	@Test
	public void testGithubPython6() {
		String regex = "\\[\\[[^\\[\\]]*?\\|\\s*([^\\|\\[]*?)\\s*\\]\\]";
		RegExp re = ExtendedRegex.getSimplifiedRegexp(regex);
		//RegExp re = new RegExp("[abcA-Z0(.|\\+)*]{2}07[abc]");
		ooregex oore = OORegexConverter.getOORegex(re);
		List<ooregex> m = oore.accept(mutator.getMutator());
		System.out.println(m);
	}
	
	@Test
	public void testTour22() {
		String regex = "(.&~(((((((((((((([\\0-\\9]|[\\a-\\z])|\\_)|\\.)|\\%)|\\&)|\\=)|\\()|\\))|\\<)|\\>)|\\*)|\\/)|(\\+|\\-))))";
		RegExp re = ExtendedRegex.getSimplifiedRegexp(regex);
		//RegExp re = new RegExp("[abcA-Z0(.|\\+)*]{2}07[abc]");
		ooregex oore = OORegexConverter.getOORegex(re);
		List<ooregex> m = oore.accept(mutator.getMutator());
		System.out.println(m);
	}
	
	@Test
	public void testURI4() {
		RegExp re = new RegExp("(\\s|\\n|^)(\\w+://[^\\s\\n]+)");
		ooregex oore = OORegexConverter.getOORegex(re);
		List<MutatedRegExp> m = IteratorUtils.iteratorToList(mutator.mutate(re));
		System.out.println(m);
	}

}
