package regex.mutrexpro.parallel;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import regex.distinguishing.DSgenPolicy;
import regex.distinguishing.DistStringCreator;
import regex.mutrexpro.ds.DSSet;
import regex.mutrexpro.ds.DSSetGenerator;
import regex.mutrexpro.ds.DistinguishingAutomaton;
import regex.mutrexpro.ds.RegexWAutomata;
import regex.operators.AllMutators;
import regex.operators.RegexMutator.MutatedRegExp;

public class DAsParallelCollectDSSetGenerator extends DSSetGenerator {
	//private static Logger logger = Logger.getLogger(DAsParallelCollectDSSetGenerator.class.getName());
	public static DSSetGenerator generator = new DAsParallelCollectDSSetGenerator();

	@Override
	public void addStringsToDSSet(DSSet dsS, RegExp regex, Iterator<MutatedRegExp> mutants) {
		Automaton regexAut = regex.toAutomaton();
		String baseString = regexAut.getShortestExample(true);
		char nicerChar = getNicerChar(baseString);
		List<Boolean> trueFalse = Arrays.asList(true, false);
		MutantsManager mutantsManager = new MutantsManager(mutants);
		Set<DistinguishingAutomatonTh> datS = new HashSet<DistinguishingAutomatonTh>();
		RegexWAutomata regexWithAutomata = new RegexWAutomata(regex);
		while (!mutantsManager.stop) {
			// mutant not covered by the created distinguishing automata
			MutantForDasParallelCollector mutant = mutantsManager.getNotCoveredByCurrentDAs(datS);
			if (mutant != null) {
				assert mutant.isLocked();
				// randomly generate a positive or negative da
				DistinguishingAutomatonTh dat = null;
				Collections.shuffle(trueFalse);
				for (boolean b : trueFalse) {
					DistinguishingAutomaton newDa = new DistinguishingAutomaton(regexWithAutomata, b, nicerChar);
					if (newDa.add(mutant.description,mutant.getRegexWithAutomata())) {
						//logger.log(Level.INFO, "new da for " + mutant);
						assert newDa.getMutants().size() == 1;
						assert DistStringCreator.getDS(regex, mutant.getRegex(), DSgenPolicy.RANDOM) != null;
						dat = new DistinguishingAutomatonTh(newDa, mutantsManager, dsS);
						datS.add(dat);
						mutant.setVisitedDA(dat);
						mutantsManager.coverMutant(mutant);
						mutant.unlock();
						dat.start();
						mutantsManager.mutantConsidered();
						break;
					}
				}
				// if no da has been created, it means that the mutant is
				// equivalent (tested both with positive and negative das)
				if (dat == null) {
					//logger.log(Level.INFO, "Equiv " + mutant);
					mutant.setTestedPositiveWithR();
					mutant.setTestedNegativeWithR();
					assert mutant.isEquivalent();
					mutant.unlock();
					mutantsManager.mutantConsidered();
					assert DistStringCreator.getDS(regex, mutant.getRegex(), DSgenPolicy.RANDOM) == null;
				}
			}
		}
		for (DistinguishingAutomatonTh dat : datS) {
			try {
				dat.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//System.out.print(datS.size() + "\t");
		// assert mutantsManager.mutants.parallelStream().allMatch(m ->
		// (m.isCovered || m.isEquivalent()));
		if (this.getClass().desiredAssertionStatus()) {
			for (MutantForDasParallelCollector m : mutantsManager.mutants) {
				assert m.isCovered || m.isEquivalent();
			}
		}
	}
	
	private char getNicerChar(String s) {
		if (s == null) {
			return '^';
		}
		boolean hasLetter = false;
		boolean hasDigit = false;
		boolean[] special = new boolean[14];
		//Initialise special
		for (int i = 0; i < special.length; i++) {
			special[i] = false; 
		}
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (Character.isAlphabetic(c)) {
				hasLetter = true;
			} else if (Character.isDigit(c)) {
				hasDigit = true;
			} else {
				if (c >= ' ' && c <= '-') { //ASCII 32-45
					special[c - ' '] = true;
				}
			}
		}
		
		if (!hasLetter) {
			return 'A';
		} else if (!hasDigit) {
			return '4';
		} else {
			for (int i = 0; i < special.length; i++) {
				if (!special[i]) {
					return (char)(' ' + i);
				}
			}
		}
		return '^';
		
	}
}

/*class RandomList<T> implements Iterable<T> {
	private List<T> elements;

	public RandomList() {
		elements = new ArrayList<T>();
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			// Java 8
			// List<Integer> indexes = IntStream.range(0, elements.size() -
			// 1).boxed().collect(Collectors.toList());
			List<Integer> indexes;
			{
				indexes = new ArrayList<Integer>();
				for (int i = 0; i < elements.size(); i++) {
					indexes.add(i);
				}
			}

			Random rnd = new Random();

			@Override
			public T next() {
				int i = rnd.nextInt(indexes.size());
				T element = elements.get(indexes.remove(i));
				return element;
			}

			@Override
			public boolean hasNext() {
				return indexes.size() > 0;
			}
		};
	}
}*/