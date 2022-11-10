
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import org.junit.jupiter.api.Test;

import dk.brics.automaton.ExtendedRegex;
import dk.brics.automaton.RegExp;
import regex.distinguishing.DistinguishingString;
import regex.mutrexpro.ds.DSSet;
import regex.mutrexpro.ds.RegExpSet;
import regex.mutrexpro.main.GeneratorType;
import regex.mutrexpro.main.MutRexPro;
import regex.operators.AllMutators;
import regex.operators.RegexMutator;

public class mutationScoreTest {

	@Test
	public void countMutants() {
		//read mutations file of each regex in total dir
		//classify first and double mutants and write into files
		//count the number of mutants for each regex
		StringBuilder countStr = new StringBuilder();
		Map<String, Integer> typeCountMap = new HashMap<>();
		for (int i = 1; i < 300; i++) {
			File total = new File("./input/1/total/" + i + ".txt");
			String first = "./input/1/first/" + i + ".txt";
			String second = "./input/1/second/" + i + ".txt";
			int firstCount = 0, secondCount = 0;
			try {
				Scanner in = new Scanner(total);
				StringBuilder firstStr = new StringBuilder();
				StringBuilder secondStr = new StringBuilder();
				while (in.hasNextLine()) {
					String line = in.nextLine();
					//System.out.println(line);
					int index = line.indexOf(" ");
					String tag = line.substring(0,index);
					String mutant = line.substring(index+1);
					//first or second
					int currentTypeNum = typeCountMap.get(tag)!=null?typeCountMap.get(tag):0;
					typeCountMap.put(tag, currentTypeNum+1);
					if (tag.contains("+")) {
						secondCount++;
						secondStr.append(mutant + "\n");
					} else {
						firstCount++;
						firstStr.append(mutant + "\n");
					}
				}
				FileWriter firstWriter = new FileWriter(first);
				FileWriter secondWriter = new FileWriter(second);
				firstWriter.append(firstStr);
				secondWriter.append(secondStr);
				//System.out.println(firstStr);
				//System.out.println("--------");
				//System.out.println(secondStr);
				in.close();
				firstWriter.close();
				secondWriter.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			countStr.append(firstCount + "\t" + secondCount + "\t" + (firstCount+secondCount) + "\n");
		}
		//System.out.println(typeCountMap);
		StringBuilder typeCountStr = new StringBuilder();
		typeCountMap.forEach((type, count) -> typeCountStr.append(count + "\t" + type + "\n"));
		try {
			FileWriter typeCountWriter = new FileWriter("./input/1/typeCount.txt");
			FileWriter countWriter = new FileWriter("./input/1/count.txt");
			countWriter.append(countStr);
			typeCountWriter.append(typeCountStr);
			typeCountWriter.close();
			countWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void resultExport() {
		File dataFile = new File("./data/data0607.txt");
		File resultFile = new File("./input/regex.txt");

		File timeFile = new File("./input/3/" + "time.txt");
		File stringCountFile = new File("./input/2/" + "count.txt");
		try {
			Scanner in = new Scanner(dataFile);
			FileWriter fileWriter = new FileWriter(resultFile);
			FileWriter timeWriter = new FileWriter(timeFile);
			FileWriter stringCountWriter = new FileWriter(stringCountFile);
			int count = 0;
			while (in.hasNextLine()) {
				count++;
				String line = in.nextLine();
				int pos = line.indexOf(' ');
				String tag = line.substring(0, pos);
				String regex = line.substring(pos).trim();
				//System.out.println(tag + "\t" + regex);
				//double time = System.currentTimeMillis();
				//export formated regex
				fileWriter.write(ExtendedRegex.getSimplifiedRegexp(regex) +"\n");
				System.out.println(count);
				//System.out.println(ExtendedRegex.getSimplifiedRegexp(regex));
				DSCount dsCount = exportDS(regex,count);
				timeWriter.append(dsCount.time + "\n");
				stringCountWriter.append(dsCount.pCount + "\t" + dsCount.nCount + "\t" +
						(dsCount.nCount+ dsCount.pCount) + "\n");
				exportMutants(regex,count);
			}
			in.close();
			fileWriter.close();
			timeWriter.close();
			stringCountWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void singleExportTest() {
		String regex = "(\\d{3})(.\\d{3}){3}";
		exportDS(regex,1);
		exportMutants(regex,1);
	}

	private DSCount exportDS(String regex, int count) {
		File positiveResultFile = new File("./input/positiveStrings/" + count + ".txt");
		File negativeResultFile = new File("./input/negativeStrings/" + count + ".txt");
		File dePositiveResultFile = new File("./input/2/positiveStrings/" + count + ".txt");
		File deNegativeResultFile = new File("./input/2/negativeStrings/" + count + ".txt");
		StringBuilder pStr = new StringBuilder();
		StringBuilder nStr = new StringBuilder();
		StringBuilder dpStr = new StringBuilder();
		StringBuilder dnStr = new StringBuilder();
		double duration = 0;
		int dsPCount = 0, dsNCount = 0;
		try {
			FileWriter pWriter = new FileWriter(positiveResultFile);
			FileWriter nWriter = new FileWriter(negativeResultFile);
			FileWriter dpWriter = new FileWriter(dePositiveResultFile);
			FileWriter dnWriter = new FileWriter(deNegativeResultFile);
			DSSet x;
			//start time
			double start = System.currentTimeMillis();
			x = MutRexPro.generateStrings(regex, GeneratorType.MONITORING, GeneratorType.Orientation.PREF_REJECT);
			//end time
			duration = System.currentTimeMillis() - start;
			Iterator<DistinguishingString> it = x.iterator();
			while(it.hasNext()) {
				DistinguishingString ds = it.next();
				RegExpSet value = x.getKilledMutants(ds);
				assert !value.isEmpty();
				//System.out.print(ds);
				StringBuilder tmp = new StringBuilder(ds.getDs());
				//System.out.println(tmp + "\n");
				if (ds.isConfirming()) {
					dsPCount++;
					pStr.append(tmp + "\n");
				} else {
					dsNCount++;
					nStr.append(tmp + "\n");
				}
				for (RegExp regExp : value) {
					tmp.append(" " + value.getDescription(regExp) + " " + regExp);
				}
				if (ds.isConfirming()) {
					dpStr.append(tmp + "\n");
				} else {
					dnStr.append(tmp + "\n");
				}
				//System.out.println();
			}
			if (pStr.length() > 0) {
				pStr.deleteCharAt(pStr.length()-1);
			}
			if (nStr.length() > 0) {
				nStr.deleteCharAt(nStr.length()-1);
			}
			if (dpStr.length() > 0) {
				dpStr.deleteCharAt(dpStr.length()-1);
			}
			if (dnStr.length() > 0) {
				dnStr.deleteCharAt(dnStr.length()-1);
			}
			pWriter.append(pStr);
			nWriter.append(nStr);
			pWriter.close();
			nWriter.close();
			dpWriter.append(dpStr);
			dnWriter.append(dnStr);
			dpWriter.close();
			dnWriter.close();
			AllMutators.mutator.init();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return new DSCount(dsPCount, dsNCount, duration);
	}

	class DSCount {
		int pCount, nCount;
		double time;
		public DSCount(int pCount, int nCount, double time) {
			this.pCount = pCount;
			this.nCount = nCount;
			this.time = time;
		}
	}

	private void exportMutants(String regex, int count) {
		File mutantFile = new File("./input/mutations/" + count + ".txt");
		File detailFile = new File("./input/1/total/" + count + ".txt");
		StringBuilder str = new StringBuilder();
		StringBuilder detailStr = new StringBuilder();
		int mutantCount = 0;
		try {
			FileWriter writer = new FileWriter(mutantFile);
			FileWriter detailWriter = new FileWriter(detailFile);
			ArrayList<RegexMutator.MutatedRegExp> mutants = AllMutators.mutator.allMutants;

			for (RegexMutator.MutatedRegExp mutant : mutants) {
				mutantCount++;
				str.append(mutant.mutatedRexExp + "\n");
				detailStr.append(mutant.description + " " + mutant.mutatedRexExp + "\n");
			}
			if (str.length() > 0) {
				str.deleteCharAt(str.length()-1);
			}
			if (detailStr.length() > 0) {
				detailStr.deleteCharAt(detailStr.length()-1);
			}
			writer.append(str);
			writer.close();
			detailWriter.append(detailStr);
			detailWriter.close();
			mutants.clear();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	class MutantCount {
		int count;
		public MutantCount(int count) {
			this.count = count;
		}
	}

}
