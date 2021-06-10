
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import dk.brics.automaton.ExtendedRegex;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.ToSimpleString;
import regex.distinguishing.DistinguishingString;
import regex.mutrexpro.ds.DSSet;
import regex.mutrexpro.ds.RegExpSet;
import regex.mutrexpro.main.GeneratorType;
import regex.mutrexpro.main.MutRexPro;
import regex.operators.AllMutators;
import regex.operators.RegexMutator.MutatedRegExp;

public class mutationScoreTest {
	private int CC = 0;

	@Test
	public void resultExport() {
		File dataFile = new File("./data/data0607.txt");
		File resultFile = new File("./input/regex.txt");
		try {
			Scanner in = new Scanner(dataFile);
			FileWriter fileWriter = new FileWriter(resultFile);
			int count = 0;
			while (in.hasNextLine()) {
				count++;
				String line = in.nextLine();
				int pos = line.indexOf(' ');
				String tag = line.substring(0, pos);
				String regex = line.substring(pos, line.length()).trim();
				//System.out.println(tag + "\t" + regex);
				//double time = System.currentTimeMillis();
				//export formated regex
				fileWriter.write(ExtendedRegex.getSimplifiedRegexp(regex) +"\n");
				System.out.println(count);
				//System.out.println(ExtendedRegex.getSimplifiedRegexp(regex));
				exportDS(regex,count);
				exportMutants(regex,count);
			}
			//System.out.println(count);
			System.out.println("#CC: " + CC);
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void singleExportTest() {
		String regex = "([a-zA-Z0-9_\\-\\.]+)\\@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})";
		exportDS(regex,1);
		exportMutants(regex,1);
	}
	
	private void exportDS(String regex, int count) {
		File positiveResultFile = new File("./input/positiveStrings/" + count + ".txt");
		File negativeResultFile = new File("./input/negativeStrings/" + count + ".txt");
		StringBuilder pStr = new StringBuilder();
		StringBuilder nStr = new StringBuilder();
		try {
			FileWriter pWriter = new FileWriter(positiveResultFile);
			FileWriter nWriter = new FileWriter(negativeResultFile);
			DSSet x;
			x = MutRexPro.generateStrings(regex, GeneratorType.BASIC, GeneratorType.Orientation.PREF_REJECT);
			Iterator<DistinguishingString> it = x.iterator();
			while(it.hasNext()) {
				DistinguishingString ds = it.next();
				RegExpSet value = x.getKilledMutants(ds);
				assert !value.isEmpty();
				//System.out.print(ds);
				if (ds.isConfirming()) {
					pStr.append(ds.getDs() + "\n");
				} else {
					nStr.append(ds.getDs());
					/*for (RegExp regExp : value) {
						 nStr.append(value.getDescription(regExp) + "" + regExp + "  ");
					}*/
					nStr.append("\n");
				}
				//System.out.println();
			}
			if (pStr.length() > 0) {
				pStr.deleteCharAt(pStr.length()-1);
			}
			if (nStr.length() > 0) {
				nStr.deleteCharAt(nStr.length()-1);
			}
			pWriter.append(pStr);
			nWriter.append(nStr);
			pWriter.close();
			nWriter.close();
			AllMutators.mutator.init();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	private void exportMutants(String regex, int count) {
		File mutantFile = new File("./input/mutations/" + count + ".txt");
		StringBuilder str = new StringBuilder();
		try {
			FileWriter writer = new FileWriter(mutantFile);
			ArrayList<MutatedRegExp> mutants = AllMutators.mutator.allMutants;
			//System.out.println("mut");
			for (MutatedRegExp mutant : mutants) {
				str.append(mutant.mutatedRexExp + "\n");
				if (mutant.description.equals("CC")) {
					CC++;
				}
			}
			if (str.length() > 0) {
				str.deleteCharAt(str.length()-1);
			}
			writer.append(str);
			writer.close();
			mutants.clear();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
