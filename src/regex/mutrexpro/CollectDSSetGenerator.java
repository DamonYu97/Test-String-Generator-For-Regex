package regex.mutrexpro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import regex.distinguishing.DSgenPolicy;
import regex.distinguishing.DistStringCreator;
import regex.distinguishing.DistinguishingString;
import regex.mutrexpro.ds.DSSet;
import regex.mutrexpro.ds.DSSetGenerator;
import regex.mutrexpro.ds.DistinguishingAutomaton;
import regex.mutrexpro.ds.RegexWAutomata;
import regex.operators.AllMutators;
import regex.operators.RegexMutator.MutatedRegExp;

/**
 * generates a ds that tries to kill as many mutants as possible
 * 
 * @author garganti
 *
 */
abstract class CollectDSSetGenerator extends DSSetGenerator {
	//private static Logger logger = Logger.getLogger(CollectDSSetGenerator.class.getName());
	List<Integer> coveredMutsNum = new ArrayList<Integer>();

	@Override
	public void addStringsToDSSet(DSSet result, RegExp regex, Iterator<MutatedRegExp> mutants) {
		List<Boolean> trueFalse = Arrays.asList(true, false);
		//Automaton rexAut = regex.toAutomaton();
		RegexWAutomata r = new RegexWAutomata(regex);
		List<DistinguishingAutomaton> das = new ArrayList<>();
		
		Automaton regexAut = regex.toAutomaton();
		String baseString = regexAut.getShortestExample(true);
		final char nicerChar = getNicerChar(baseString);
		int count = 0;
		nextMut: while (mutants.hasNext()) {
			MutatedRegExp mutant = mutants.next();
			System.out.println("current: " + mutant);
			count++;
			sortDAs(das);
			double time = System.currentTimeMillis();
			ExecutorService executorService= Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*2);
			Future<String> future = executorService.submit(new Callable<String>() {
	            @Override
	            public String call() {
	    			RegexWAutomata m = new RegexWAutomata(mutant.mutatedRexExp);
	    			Iterator<DistinguishingAutomaton> dasIt = das.iterator();
	    			while (dasIt.hasNext()) {
	    				DistinguishingAutomaton da = dasIt.next();
	    				// solution 2: invalidating the da
	    				/*if(!da.isActive) {
	    					continue;
	    				}*/

	    				if (da.add(mutant.description,m)) {
	    					//logger.log(Level.INFO, "collected mutation " + mutant + " into " + da);
	    					if (stop(da)) {
	    						// solution 1: removing the da
	    						genTest(result, da);
	    						dasIt.remove();

	    						//solution 2: invalidating the da
	    						//da.isActive = false;
	    					}
	    					return "continue";
	    				}
	    			}
	    			// it is not collectable
	    			// try to collect rexAut
	    			Collections.shuffle(trueFalse);
	    			for (boolean b : trueFalse) {
	    				DistinguishingAutomaton newDa = new DistinguishingAutomaton(r, b, nicerChar);
	    				if (newDa.add(mutant.description,m)) {
	    					das.add(newDa);
	    					//logger.log(Level.INFO, "new da for mutation " + mutant + " into " + newDa);
	    					return "continue";
	    				}
	    			}
	    			// it is equivalent
	    			//logger.log(Level.INFO, "equivalent mutation " + mutant);
	                return  "OK";
	            }
	        });
	        try {
	            String responseString = future.get(1, TimeUnit.SECONDS);
	            if (responseString.equals("continue")) {
					continue nextMut;
				}
	        } catch (InterruptedException | ExecutionException | TimeoutException e) {
	            future.cancel(true);
	            
	        } finally {
	        	executorService.shutdown();
	        }
	        time = System.currentTimeMillis() - time;
			if (time > 100) {
				System.out.println("time: " + time + " \t"+ mutant);
			}
		}
		AllMutators.mutator.setTotalCount(count);
		// now get the remaining DS
		for (DistinguishingAutomaton da : das) {
			genTest(result, da);
		}
		//System.out.print(das.size() + "\t");
		/*System.out.println();
		for(Integer c: coveredMutsNum) {
			System.out.print(c + " ");
		}
		System.out.println();*/
		//System.out.print(coveredMutsNum.size() + "\t");
	}

	private void sortDAs(List<DistinguishingAutomaton> das) {
		Collections.shuffle(das);
		//smallest ones first
		//das.sort((DistinguishingAutomaton o1, DistinguishingAutomaton o2) -> o1.mutatedRegexes.size() - o2.mutatedRegexes.size());
		//biggest ones first
		//das.sort((DistinguishingAutomaton o1, DistinguishingAutomaton o2) -> o2.mutatedRegexes.size() - o1.mutatedRegexes.size());
	}

	private void genTest(DSSet result, DistinguishingAutomaton da) {
		DistinguishingString ds = new DistinguishingString(da.getExample(), da.isPositive());
		result.add(ds, da.getMutants());
		//logger.log(Level.INFO, da + " covers " + da.mutatedRegexes.size() + " mutants");
		coveredMutsNum.add(da.size());
	}

	abstract boolean stop(DistinguishingAutomaton da);
	
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