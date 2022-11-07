package regex.mutrexpro.parallel.mutant;

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
import regex.distinguishing.DistinguishingString;
import regex.mutrexpro.ds.DSSet;
import regex.mutrexpro.ds.DSSetGenerator;
import regex.mutrexpro.ds.DistinguishingAutomaton;
import regex.mutrexpro.ds.RegexWAutomata;
import regex.operators.AllMutators;
import regex.operators.RegexMutator.MutatedRegExp;

public class MutantParallelCollectDSSetGenerator extends DSSetGenerator {
	//static Logger logger = Logger.getLogger(MutantParallelCollectDSSetGenerator.class.getName());
	public static DSSetGenerator generator = new MutantParallelCollectDSSetGenerator();
	int numRunningMutants = 0;

	public synchronized MutantForMutParallelCollector getMutant(Iterator<MutatedRegExp> mutants) {
		int count = 0;
		while (mutants.hasNext()) {
			count++;
			if (numRunningMutants >= Runtime.getRuntime().availableProcessors()) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			} else {
				numRunningMutants++;
				return new MutantForMutParallelCollector(mutants.next());
			}
		}
		return null;
	}

	public synchronized void decreaseRunningMuts() {
		numRunningMutants--;
		notifyAll();
	}

	@Override
	public void addStringsToDSSet(DSSet dsS, RegExp regex, Iterator<MutatedRegExp> mutants) {
		Automaton regexAut = regex.toAutomaton();
		String baseString = regexAut.getShortestExample(true);
		char nicerChar = getNicerChar(baseString);

		DasManager dasManager = new DasManager(regex);
		MutantForMutParallelCollector mutant = null;
		Set<MutTh> mutThs = new HashSet<MutTh>();
		while ((mutant = getMutant(mutants)) != null) {
			//logger.log(Level.INFO, "new mutant " + mutant);
			MutTh mutTh = new MutTh(mutant, dasManager, this, nicerChar);
			mutTh.start();
			mutThs.add(mutTh);
		}
		for (MutTh mutTh : mutThs) {
			try {
				mutTh.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		for (DistinguishingAutomatonClass dac : dasManager.daS) {
			DistinguishingString ds = new DistinguishingString(dac.da.getExample(), dac.da.isPositive());
			dsS.add(ds, dac.da.getMutants());
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

class MutTh extends Thread {
	//static Logger logger = Logger.getLogger(MutTh.class.getName());
	MutantForMutParallelCollector mutant;
	DasManager dasManager;
	MutantParallelCollectDSSetGenerator gen;
	char niceChar;

	public MutTh(MutantForMutParallelCollector mutant, DasManager dasManager, MutantParallelCollectDSSetGenerator gen, char niceChar) {
		this.mutant = mutant;
		this.dasManager = dasManager;
		this.gen = gen;
		this.niceChar = niceChar;
	}

	@Override
	public void run() {
		List<Boolean> trueFalse = Arrays.asList(true, false);
		boolean isCovered = false;
		while (!isCovered) {
			DistinguishingAutomatonClass dac = dasManager.getDA(mutant);
			if (dac == null) {
				//logger.log(Level.INFO, "look for new da");
				Collections.shuffle(trueFalse);
				for (boolean b : trueFalse) {
					DistinguishingAutomaton newDa = new DistinguishingAutomaton(dasManager.regexWithAutomata, b, niceChar);
					if (newDa.add(mutant.description, mutant.mutant)) {
						DistinguishingAutomatonClass newDaC = new DistinguishingAutomatonClass(newDa);
						dasManager.addDA(newDaC);
						//logger.log(Level.INFO, "new da found");
						break;
					}
				}
				isCovered = true;
				mutant.isEquivalent = true;
			} else {
				if (dac.da.add(mutant.description, mutant.mutant)) {
					isCovered = true;
				}
				mutant.visited.add(dac);
				dasManager.unlock(dac);
			}
		}
		gen.decreaseRunningMuts();
	}

}

class DasManager {
	//static Logger logger = Logger.getLogger(DasManager.class.getName());
	Set<DistinguishingAutomatonClass> daS = new HashSet<DistinguishingAutomatonClass>();
	List<Boolean> trueFalse = Arrays.asList(true, false);
	RegexWAutomata regexWithAutomata;

	public DasManager(RegExp regex) {
		regexWithAutomata = new RegexWAutomata(regex);
	}

	public synchronized void addDA(DistinguishingAutomatonClass da) {
		daS.add(da);
		notifyAll();
	}

	public synchronized void unlock(DistinguishingAutomatonClass da) {
		da.locked = false;
		notifyAll();
	}

	public synchronized DistinguishingAutomatonClass getDA(MutantForMutParallelCollector mutant) {
		Set<DistinguishingAutomatonClass> checkedByMut = mutant.visited;
		while (true) {
			boolean addNewDa = true;
			for (DistinguishingAutomatonClass da : daS) {
				if (!checkedByMut.contains(da)) {
					addNewDa = false;
					if (!da.locked) {
						da.locked = true;
						return da;
					}
				}
			}
			if (addNewDa) {
				return null;
			} else {
				try {
					//logger.log(Level.INFO, mutant + " waits");
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

class DistinguishingAutomatonClass {
	DistinguishingAutomaton da;
	boolean locked = false;

	public DistinguishingAutomatonClass(DistinguishingAutomaton da) {
		this.da = da;
	}
}

class MutantForMutParallelCollector {
	RegexWAutomata mutant;
	Set<DistinguishingAutomatonClass> visited;
	boolean isEquivalent;
	String description;

	public MutantForMutParallelCollector(MutatedRegExp mutatedRegExp) {
		this.mutant = new RegexWAutomata(mutatedRegExp.mutatedRexExp);
		visited = new HashSet<DistinguishingAutomatonClass>();
		description = mutatedRegExp.description;
	}

	public RegExp getRegex() {
		return mutant.getMutant();
	}
}
