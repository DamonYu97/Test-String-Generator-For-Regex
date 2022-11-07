package regex.operators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.xml.crypto.MarshalException;

import org.omg.CORBA.INITIALIZE;

import dk.brics.automaton.OORegexConverter;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.oo.ooregex;
import regex.operators.RegexMutator.MutatedRegExp;
import regex.utils.IteratorUtils;
import regex.utils.JoinedIterator;
import regex.utils.JoinedRandomIterator;

/**
 *
 * @modified by Yu Lilin at 2020.03.19
 *
 */
public class AllMutators extends RegexMutator {
	public static AllMutators mutator = new AllMutators();
	public ArrayList<MutatedRegExp> allMutants = new ArrayList<MutatedRegExp>();

	protected AllMutators() {
		super(new RegexVisitorAdapterList());
	}

	// list of all the mutators that can possibly used
	public static List<RegexMutator> allMutators = new ArrayList<RegexMutator>();
	// those that are defined
	public static List<RegexMutator> definedMutators = new ArrayList<RegexMutator>();

	// probably a file or a plugin mechanism or reflection could be better
	static {
		add(CharacterClass2Group.mutator);
		add(CaseAddition.mutator);
		add(CaseChange.mutator);
		add(Char2MetaChar.mutator);
		add(MetaChar2Char.mutator);
		add(CharacterClassCreation.mutator);
		add(CharacterClassAddition.mutator);
		add(RangeModification.mutator);
		add(CharacterClassModification.mutator);
		add(CharacterClassNegation.mutator);
		add(CharacterClassRestriction.mutator);
		add(NegatedCharacterClassToOptional.mutator);
		add(NegationAddition.mutator);
		add(PrefixAddition.mutator);
		add(QuantifierChange.mutator);
		//add(WrongPrecedenceClosure.mutator);
		add(UnionRestriction.mutator);
	}

	public void init() {
		allMutators.clear();
		add(CharacterClass2Group.mutator);
		add(CaseAddition.mutator);
		add(CaseChange.mutator);
		add(Char2MetaChar.mutator);
		add(MetaChar2Char.mutator);
		add(CharacterClassCreation.mutator);
		add(CharacterClassAddition.mutator);
		add(RangeModification.mutator);
		add(CharacterClassModification.mutator);
		add(CharacterClassNegation.mutator);
		add(CharacterClassRestriction.mutator);
		add(NegatedCharacterClassToOptional.mutator);
		add(NegationAddition.mutator);
		add(PrefixAddition.mutator);
		add(QuantifierChange.mutator);
		//add(WrongPrecedenceClosure.mutator);
		add(UnionRestriction.mutator);
	}

	@Override
	public Iterator<MutatedRegExp> mutate(RegExp re) {
		List<Iterator<MutatedRegExp>> allIterator = new ArrayList<>();
		for (RegexMutator m : allMutators) {
			//System.out.println("----->Current State: " + m.getClass() + " mutate <------");
			allIterator.add(m.mutate(re));
			//System.out.println("----->Current State: " + m.getClass() + " mutate finished<------");
		}
		JoinedIterator<MutatedRegExp> joinedIterator = new JoinedIterator<MutatedRegExp>(allIterator);
		return joinedIterator;
	}

	@Override
	public Iterator<MutatedRegExp> mutateRandom(RegExp re) {
		List<Iterator<MutatedRegExp>> allIterator = new ArrayList<>();
		for (RegexMutator m : allMutators) {
			allIterator.add(m.mutateRandom(re));
		}
		JoinedRandomIterator<MutatedRegExp> joinedRandomIterator = new JoinedRandomIterator<MutatedRegExp>(allIterator);
		return joinedRandomIterator;
	}

	/**
	 * This only shuffle the mutants with the same mutation type, and all mutants with the same type are adjacent to each other.
	 * E.g. [CC-2, CC-1, CC-3, M2C-1, M2C-4,M2C-3,M2C-2, .......]
	 * @param re the regexp need to be tested.
	 * @return mutated expression with random mutation position for each mutation type.
	 */
	public Iterator<MutatedRegExp> mutateSameTypeRandom(RegExp re) {
		List<Iterator<MutatedRegExp>> allIterator = new ArrayList<>();
		for (RegexMutator m : allMutators) {
			allIterator.add(m.mutateRandom(re));
		}
		JoinedIterator<MutatedRegExp> joinedIterator = new JoinedIterator<MutatedRegExp>(allIterator);
		return joinedIterator;
	}


	@Override
	public Iterator<MutatedRegExp> secondMutate(MutatedRegExp mut) {
		List<Iterator<MutatedRegExp>> allIterator = new ArrayList<>();
		for (RegexMutator m : allMutators) {
			//System.out.println("----->Current State: " + m.getClass() + " mutate <------");
			allIterator.add(m.secondMutate(mut));
			//System.out.println("----->Current State: " + m.getClass() + " mutate finished<------");
		}
		JoinedIterator<MutatedRegExp> joinedIterator = new JoinedIterator<MutatedRegExp>(allIterator);
		return joinedIterator;
	}

	public Iterator<MutatedRegExp> doubleMutate(RegExp re) {
		//System.out.println("----->Current State: Double mutate <------");
		List<Iterator<MutatedRegExp>> allMutatedExpIterator = new ArrayList<>();

		//System.out.println("----->Current State: First mutate<------");
		//Shuffle mutants for later randomly selection.
		Iterator<MutatedRegExp> mutatedExpIterator = mutateSameTypeRandom(re);
		//System.out.println("----->Current State: First mutate Finished<------");

		// add first mutated regexps to the list
		List<MutatedRegExp> firstMutatedRegExps = new ArrayList<MutatedRegExp>();
		while (mutatedExpIterator.hasNext()) {
			firstMutatedRegExps.add(mutatedExpIterator.next());
		}
		// select mutants to perform second mutation.
		List<MutatedRegExp> selectedFirstMutants = selectMutantsForSecondMutation(firstMutatedRegExps);

		disable("CC2G");
		disable("NCCO");
		disable("QC");
		disable("NA");
		for (MutatedRegExp selectedFirstMutant: selectedFirstMutants ) {
			if (selectedFirstMutant.description.equals("CC2G")) {
				//System.out.println("CC2G " + mutatedRegExp);
				disable("CCC");
			} else if (selectedFirstMutant.description.equals("CC") || selectedFirstMutant.description.equals("CA")) {
				disable("CC");
				disable("CA");
				disable("CCR");
			} else if (selectedFirstMutant.description.equals("M2C") || selectedFirstMutant.description.equals("C2M")) {
				disable("M2C");
				disable("C2M");
			} else if (selectedFirstMutant.description.equals("CCC")) {
				disable("CCC");
			} else if (selectedFirstMutant.description.equals("CCA") || selectedFirstMutant.description.equals("CCR") || selectedFirstMutant.description.equals("RM")){
				disable("CCA");
				disable("CC");
				disable("CA");
				disable("CCR");
				disable("PA");
				disable("RM");
			} else if (selectedFirstMutant.description.equals("CCM")) {
				disable("CCM");
				disable("CCN");
				disable("UR");
			} else if (selectedFirstMutant.description.equals("CCN")) {
				disable("CCM");
				disable("CCN");
				disable("NA");
				disable("UR");
			} else if (selectedFirstMutant.description.equals("NCCO")) {
				disable("NCCO");
				disable("QC");
			} else if (selectedFirstMutant.description.equals("NA") || selectedFirstMutant.description.equals("PA")) {
				disable("NA");
				disable("PA");
				disable("UR");
			} else if (selectedFirstMutant.description.equals("QC")) {
				disable("QC");
			} else if (selectedFirstMutant.description.equals("UR")) {
				disable("UR");
			} else {
				allMutators.clear();
			}
			//System.out.println("double mute: " + mutatedRegExp);
			Iterator<MutatedRegExp> doubleMutatedExpIterator = secondMutate(selectedFirstMutant);
			allMutatedExpIterator.add(doubleMutatedExpIterator);
		}


		allMutatedExpIterator.add(firstMutatedRegExps.iterator());
		//System.out.println(allMutatedExpIterator.size());
		JoinedRandomIterator<MutatedRegExp> joinedRandomIterator = new JoinedRandomIterator<MutatedRegExp>(allMutatedExpIterator);
		//System.out.println("----->Current State: Double mutate Finished<------");
		return joinedRandomIterator;
	}

	private List<MutatedRegExp> selectMutantsForSecondMutation(List<MutatedRegExp> firstMutatedRegExps) {
		final double RATE = 0.75;
		//Count the number of mutants of each mutation type.
		List<Integer> numOfMutants = new ArrayList<>();
		String lastMutantType = "";
		for (MutatedRegExp mutant: firstMutatedRegExps) {
			if (mutant.description.equals(lastMutantType)) {
				int previousNum = numOfMutants.get(numOfMutants.size() - 1);
				numOfMutants.set(numOfMutants.size() - 1, previousNum + 1);
			} else {
				numOfMutants.add(1);
				lastMutantType = mutant.description;
			}
		}
		//Select mutants based on the rate.
		List<MutatedRegExp> result = new ArrayList<>();
		int currentIndex = 0;
		int typeCount = 0;

		while (currentIndex < firstMutatedRegExps.size()) {
			int totalNumOfMutantsForCurrentType = numOfMutants.get(typeCount);
			// Make sure at lease one mutant for each existing type will be selected.
			int requiredNumOfMutantsForCurrentType = (int) Math.ceil(totalNumOfMutantsForCurrentType * RATE);

			result.addAll(firstMutatedRegExps.subList(currentIndex, currentIndex + requiredNumOfMutantsForCurrentType));
			currentIndex = currentIndex + totalNumOfMutantsForCurrentType;
			typeCount++;
		}
		return result;
	}


	private static void add(RegexMutator mutator) {
		allMutators.add(mutator);
		definedMutators.add(mutator);
	}

	@Override
	public String getCode() {
		return "ALL";
	}

	/** enable only these operators */
	public static void enableOnly(String[] operators) {
		allMutators.clear();
		// if null or none, put all
		if (operators == null || operators.length == 0) {
			allMutators.addAll(definedMutators);
		} else {
			List<String> ops = Arrays.asList(operators);
			for (RegexMutator m : definedMutators) {
				if (ops.contains(m.getCode()))
					allMutators.add(m);
			}
		}
	}

	/** disable this operator */
	public static void disable(String op) {
		for (Iterator<RegexMutator> iterator = allMutators.iterator(); iterator.hasNext();) {
			RegexMutator m = iterator.next();
			if (op.equals(m.getCode()))
				iterator.remove();
		}
	}

	/*public Map<String, Integer> getMutateCountMap() {
		return mutateCountMap;
	}

	public int getSecondMutateCount() {
		return secondMutateCount;
	}*/
}
