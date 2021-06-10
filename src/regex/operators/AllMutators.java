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
	
	public Iterator<MutatedRegExp> secondMutate(MutatedRegExp mut) {
		List<MutatedRegExp> allMutatedRegExps = new ArrayList<MutatedRegExp>();
		for (RegexMutator m : allMutators) {
			//System.out.println("double mutate " + mut + "with " + m.getClass());
			MutatedRegExp secondMutatedRegExp = m.mutateSingle(mut.mutatedRexExp);
			if (secondMutatedRegExp != null) {
				secondMutatedRegExp.description = mut.description + "+" + m.getCode();
				allMutatedRegExps.add(secondMutatedRegExp);
			}
		}
		//System.out.println(allMutatedRegExps);
		return allMutatedRegExps.iterator();
	}
	
	public Iterator<MutatedRegExp> doubleMutate(RegExp re) {
		//System.out.println("----->Current State: Double mutate <------");
		List<Iterator<MutatedRegExp>> allMutatedExpIterator = new ArrayList<>();
		//System.out.println("----->Current State: First mutate<------");
		Iterator<MutatedRegExp> mutatedExpIterator = mutate(re);
		//System.out.println("----->Current State: First mutate Finished<------");
		List<MutatedRegExp> firstMutatedRegExps = new ArrayList<MutatedRegExp>();
		
		String lastMutateType = "";
		Random rnd = new Random();
		disable("CC2G");
		disable("NCCO");
		disable("QC");
		disable("NA");
		int sameTypeCount = 0;
		while (mutatedExpIterator.hasNext()) {
			MutatedRegExp mutatedRegExp = mutatedExpIterator.next();
			//System.out.println("double" + mutatedRegExp);
			String currentMutateType = mutatedRegExp.description;
			int count = 1;
			firstMutatedRegExps.add(mutatedRegExp);
			//System.out.println("mute: " + mutatedRegExp);
			
			if (lastMutateType.equals("")) {
				lastMutateType = currentMutateType;
			} else if (!lastMutateType.equals(currentMutateType)) {
				sameTypeCount = 0;
				lastMutateType = currentMutateType;
			} else if (lastMutateType.equals(currentMutateType) && sameTypeCount >= 5) {
				continue;
			}
			boolean mutate = rnd.nextBoolean();
			//System.out.println(mutatedRegExp);
			if (mutatedRegExp.description.equals("CC2G")) {
				//System.out.println("CC2G " + mutatedRegExp);
				mutate = true;
				disable("CCC");
			} else if (mutatedRegExp.description.equals("CC") || mutatedRegExp.description.equals("CA")) {
				disable("CC");
				disable("CA");
				disable("CCR");
			} else if (mutatedRegExp.description.equals("M2C") || mutatedRegExp.description.equals("C2M")) {
				disable("M2C");
				disable("C2M");	
			} else if (mutatedRegExp.description.equals("CCC")) {
				disable("CCC");
			} else if (mutatedRegExp.description.equals("CCA") || mutatedRegExp.description.equals("CCR") || mutatedRegExp.description.equals("RM")){
				disable("CCA");
				disable("CC");
				disable("CA");
				disable("CCR");
				disable("PA");
				disable("RM");
			} else if (mutatedRegExp.description.equals("CCM")) {
				disable("CCM");
				disable("CCN");
				disable("UR");
			} else if (mutatedRegExp.description.equals("CCN")) {
				disable("CCM");
				disable("CCN");
				disable("NA");
				disable("UR");
			} else if (mutatedRegExp.description.equals("NCCO")) {
				disable("NCCO");
				disable("QC");
			} else if (mutatedRegExp.description.equals("NA") || mutatedRegExp.description.equals("PA")) {
				disable("NA");
				disable("PA");
				disable("UR");
			} else if (mutatedRegExp.description.equals("QC")) {
				disable("QC");
			} else if (mutatedRegExp.description.equals("UR")) {
				disable("UR");
			} else {
				allMutators.clear();
			}
			if (mutate) {
				//System.out.println("double mute: " + mutatedRegExp);
				Iterator<MutatedRegExp> doubleMutatedExpIterator = secondMutate(mutatedRegExp);
				allMutatedExpIterator.add(doubleMutatedExpIterator);
				sameTypeCount++;
			}
		}
		allMutatedExpIterator.add(firstMutatedRegExps.iterator());
		//System.out.println(allMutatedExpIterator.size());
		JoinedRandomIterator<MutatedRegExp> joinedRandomIterator = new JoinedRandomIterator<MutatedRegExp>(allMutatedExpIterator);
		//System.out.println("----->Current State: Double mutate Finished<------");
		return joinedRandomIterator;
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
