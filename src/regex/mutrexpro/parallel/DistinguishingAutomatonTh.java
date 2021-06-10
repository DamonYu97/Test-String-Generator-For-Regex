package regex.mutrexpro.parallel;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import regex.distinguishing.DistinguishingString;
import regex.mutrexpro.ds.DSSet;
import regex.mutrexpro.ds.DistinguishingAutomaton;
import regex.operators.RegexMutator.MutatedRegExp;

public class DistinguishingAutomatonTh extends Thread {
	//private static Logger logger = Logger.getLogger(DistinguishingAutomatonTh.class.getName());
	private DistinguishingAutomaton da;
	private MutantsManager mutantsManager;
	private boolean run;
	private DSSet dsS;

	public DistinguishingAutomatonTh(DistinguishingAutomaton da, MutantsManager mutantsManager, DSSet dsS) {
		this.da = da;
		this.mutantsManager = mutantsManager;
		run = true;
		this.dsS = dsS;
		assert da.getMutants().size() == 1;
	}

	@Override
	public void run() {
		while (run) {
			MutantForDasParallelCollector mutant = mutantsManager.getMutant(this);
			//logger.log(Level.INFO, da + " retrieved mutant " + mutant);
			if (mutant != null) {
				if (da.add(mutant.description,mutant.getRegexWithAutomata())) {
					//logger.log(Level.INFO, da + " added " + mutant);
					assert da.getMutants().size() > 1;
					mutantsManager.coverMutant(mutant);
				}
				mutant.unlock();
			}
			mutantsManager.mutantConsidered();
		}
		List<MutatedRegExp> daCoveredMuts = da.getMutants();
		assert daCoveredMuts.size() > 0;
		dsS.add(new DistinguishingString(da.getExample(), da.positive), daCoveredMuts);
		da = null;
		//logger.log(Level.INFO, da + " exiting");
	}

	public void stopThread() {
		run = false;
	}
}