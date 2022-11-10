package regex.mutrexpro;

import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import regex.distinguishing.DSgenPolicy;
import regex.distinguishing.DistStringCreator;
import regex.distinguishing.DistinguishingString;
import regex.mutrexpro.ds.DSSet;
import regex.mutrexpro.ds.DSSetGenerator;
import regex.operators.AllMutators;
import regex.operators.RegexMutator.MutatedRegExp;

/**
 * generates a ds for each mutation and then it collects the ds and keeps track
 * if one kills many
 *
 */
public class MonitoringDSSetgenerator extends DSSetGenerator {
	public static DSSetGenerator generator = new MonitoringDSSetgenerator();

	protected MonitoringDSSetgenerator() {
	}

	@Override
	public void addStringsToDSSet(DSSet result, RegExp regex, Iterator<MutatedRegExp> mutants) {
		Automaton regexAut = regex.toAutomaton();
		String baseString = regexAut.getShortestExample(true);
		char nicerChar = getNicerChar(baseString);
		// all the regexes that are accepted by regex
		AllMutators.mutator.allMutants.clear();
		//System.out.println("generating.....");
		//System.out.println("regex: " + regex);
		mutLoop: while (mutants.hasNext()) {
			MutatedRegExp mutant = mutants.next();
			//System.out.println("mutex: " + mutant);
			AllMutators.mutator.allMutants.add(mutant);
			// monitoring
			//double time = System.currentTimeMillis();
			Automaton mutAutom = mutant.mutatedRexExp.toAutomaton();
			// check if there exists a ds in results that covers this mutant
			for (DistinguishingString ds : result) {
				String dsStr = ds.getDs();
				assert ds.isConfirming() == regexAut.run(dsStr);
				boolean acceptedByMut = mutAutom.run(dsStr);
				if (ds.isConfirming() != acceptedByMut) {
					result.add(ds, Collections.singletonList(mutant));
					// ds also distinguishes r and mutant
					continue mutLoop;
				}
			}
			//generation
			DistinguishingString ds = DistStringCreator.getDS(regexAut, mutAutom, DSgenPolicy.ONLY_NEGATIVE, nicerChar);
			//System.out.println("ds: " + ds);
			if (ds != null) {
				assert ds.isConfirming() == regexAut.run(ds.getDs());
				assert ds.isConfirming() != mutAutom.run(ds.getDs());
				result.add(ds, Collections.singletonList(mutant));
			}
			/*ExecutorService executorService= Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*2);
			Future<String> future = executorService.submit(new Callable<String>() {
	            @Override
	            public String call() {
	            	
	                return  "OK";
	            }
	        });
	        try {
	            String responseString = future.get(1, TimeUnit.SECONDS);
	            if (responseString.equals("continue")) {
					continue mutLoop;
				}
	        } catch (InterruptedException | ExecutionException | TimeoutException e) {
	            future.cancel(true);
	            
	        } finally {
	        	executorService.shutdown();
	        }*/
	        
	        /*time = System.currentTimeMillis() - time;
			if (time > 100) {
				System.out.println("time: " + time + " \t"+ mutant);
			}*/
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
