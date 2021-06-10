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
 * @author garganti
 *
 */
public class BasicDSSetgenerator extends DSSetGenerator {
	
	private DSgenPolicy policy;
	
	public static DSSetGenerator generator = new BasicDSSetgenerator(DSgenPolicy.RANDOM);

	public static DSSetGenerator generatorPOS = new BasicDSSetgenerator(DSgenPolicy.PREF_POSITIVE);

	public static DSSetGenerator generatorNEG = new BasicDSSetgenerator(DSgenPolicy.PREF_NEGATIVE);
	
	protected BasicDSSetgenerator() {
		this(DSgenPolicy.RANDOM);
	}
	
	protected BasicDSSetgenerator(DSgenPolicy policy) {
		this.policy = policy;
	}

	@Override
	public void addStringsToDSSet(DSSet result, RegExp regex, Iterator<MutatedRegExp> mutants) {
		//System.out.println("----->Current State: Generate String <------");
		Automaton regexAut = regex.toAutomaton();
		String baseString = regexAut.getShortestExample(true);
		char nicerChar = getNicerChar(baseString);
		AllMutators.mutator.allMutants.clear();
		int count = 0;
		while (mutants.hasNext()) {
			
			//double time = System.currentTimeMillis();
			MutatedRegExp mutant = mutants.next();
			//System.out.println(mutant);
			AllMutators.mutator.allMutants.add(mutant);
			count++;
			// generate a distinguishing string
			// DistinguishingString ds = DistStringCreator.getDS(regex,
			// mutant.mutatedRexExp, DSgenPolicy.RANDOM);
			DistinguishingString ds = DistStringCreator.getDS(regexAut, mutant.mutatedRexExp.toAutomaton(),
					policy, nicerChar);
			if (ds != null) {
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
	            future.get(5, TimeUnit.SECONDS);
	        } catch (InterruptedException | ExecutionException | TimeoutException e) {
	            future.cancel(true);
	            System.out.println(mutant);
	        } finally {
	        	
	        	executorService.shutdown();
	        }*/
	        /*time = System.currentTimeMillis() - time;
			if (time > 100) {
				System.out.println("time: " + time + " \t"+ mutant);
			}*/
		}
		
		AllMutators.mutator.setTotalCount(count);
		return;
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
