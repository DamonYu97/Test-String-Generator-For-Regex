package regex.mutrexpro.main;

import java.util.Iterator;
import java.util.Scanner;

import dk.brics.automaton.RegExp;
import regex.distinguishing.DistinguishingString;
import regex.mutrexpro.ds.DSSet;
import regex.mutrexpro.ds.RegExpSet;
import regex.operators.AllMutators;

public class Simulator {

	public static void main(String[] args) {
		//[AM|PM|am|pm]{2,2}
		//192.168.142.1(02|12)
		Scanner in = new Scanner(System.in);
		//get the type of generation technique
		GeneratorType type = GeneratorType.MONITORING; //default type
		System.out.println("Choose a generation technique:");
		System.out.println("1: Basic, 2: Monitoring(default), 3: Parallel Collecting");
		if (in.hasNextInt()) {
			int typeNum = in.nextInt();
			switch (typeNum) {
			case 1:
				type = GeneratorType.BASIC;
				break;
			case 2:
				type = GeneratorType.MONITORING;
				break;
			case 3 :
				type = GeneratorType.COLLECTING_PAR_DAs;
			default:
				break;
			}
			in.nextLine();
		} else {
			in.nextLine();
		}
		//Get regular expressions and generate test strings
		System.out.println("\nEnter a regular expression:");
		while (in.hasNextLine()) {
			String regexString = in.nextLine();
			try {
				DSSet x = new DSSet();
				//generate test strings
				x = MutRexPro.generateStrings(regexString, type, GeneratorType.Orientation.PREF_REJECT);
				Iterator<DistinguishingString> it = x.iterator();
				String acceptedStringsInfo = "";
				String rejectedStringsInfo = "";
				while(it.hasNext()) {
					DistinguishingString ds = it.next();
					RegExpSet value = x.getKilledMutants(ds);
					assert !value.isEmpty();
					String infoString = "\"" + ds.getDs() + "\" killed by ";
					for (RegExp regExp : value) {
						infoString += value.getDescription(regExp) + "" + regExp + "  ";
					}
					infoString += "\n";
					if (ds.isConfirming()) {
						acceptedStringsInfo += infoString;
					} else {
						rejectedStringsInfo += infoString;
					}
				}
				//print the result
				System.out.println("---The generated test strings---\n");
				System.out.println("-->Accepted Strings<--:");
				System.out.println(acceptedStringsInfo);
				System.out.println("-->Rejected Strings<--:");
				System.out.println(rejectedStringsInfo);
				AllMutators.mutator.init();
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println(e.getMessage());
			}
			System.out.println("\nEnter a regular expression:");
		}

	}

}
