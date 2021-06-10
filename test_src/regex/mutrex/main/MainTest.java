package mutrex.main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dk.brics.automaton.ToSimpleString;
import org.junit.internal.runners.model.EachTestNotifier;

import dk.brics.automaton.ExtendedRegex;
import dk.brics.automaton.RegExp;
import org.junit.jupiter.api.Test;
import regex.distinguishing.DistinguishingString;
import regex.mutrexpro.BasicDSSetgenerator;
import regex.mutrexpro.ds.DSSet;
import regex.mutrexpro.ds.RegExpSet;
import regex.mutrexpro.main.GeneratorType;
import regex.mutrexpro.main.MutRexPro;
import regex.operators.AllMutators;
import regex.operators.RegexMutator;

/**
 * 
 * @author Yu Lilin
 * @date 2020.03.19
 *
 */
public class MainTest {

	@Test
	public void testDoubleMutate() {
		//testWithRegex("(\\s*\\(?0\\d{4}\\)?\\s*\\d{6}\\s*)|(\\s*\\(?0\\d{3}\\)?\\s*\\d{3}\\s*\\d{4}\\s*)");
		testWithRegex("([a-zA-Z0-9_\\-\\.]+)\\@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})");
	}

	private void testWithRegex(String start) {
		// TODO Auto-generated method stub
		try {
			//Pattern.compile(start);
			DSSet x = new DSSet();
			//RegExp regExp = ExtendedRegex.getSimplifiedRegexp(start);
			//BasicDSSetgenerator.generator.addStringsToDSSet(x, regExp, AllMutators.mutator.mutate(regExp));
			x = MutRexPro.generateStrings(start, GeneratorType.BASIC, GeneratorType.Orientation.PREF_REJECT);
			Iterator<DistinguishingString> it = x.iterator();
			int count = 0;
			System.out.println("start");
			ArrayList<RegexMutator.MutatedRegExp> mutants = AllMutators.mutator.allMutants;
			for (RegexMutator.MutatedRegExp mutant : mutants) {
				System.out.println(mutant.description + " " + mutant.mutatedRexExp);
				System.out.println(mutant.description + " " + ToSimpleString.convertToReadableString(mutant.mutatedRexExp));

			}
			while(it.hasNext()) {
				DistinguishingString ds = it.next();
				RegExpSet value = x.getKilledMutants(ds);
				assert !value.isEmpty();
				System.out.print(ds);
				for (RegExp regExp : value) {
					//System.out.print(value.getDescription(regExp) + " " + regExp);
				}
				System.out.println();
				count++;
			}
			System.out.println(count);
			AllMutators.mutator.init();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
		}
		
		//System.out.println(AllMutators.mutator.allMutants.get(0));
		System.out.println("end");
		
	}

}
