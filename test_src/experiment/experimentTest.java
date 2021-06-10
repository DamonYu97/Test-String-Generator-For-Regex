
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;


import dk.brics.automaton.ExtendedRegex;
import dk.brics.automaton.RegExp;
import org.junit.jupiter.api.Test;
import regex.distinguishing.DistinguishingString;
import regex.mutrexpro.ds.DSSet;
import regex.mutrexpro.ds.RegExpSet;
import regex.mutrexpro.main.GeneratorType;
import regex.mutrexpro.main.MutRexPro;
import regex.operators.AllMutators;

/**
 * 
 * @author Yu Lilin
 * @date 2020.04.05
 *
 */
public class experimentTest {
	
	@Test
	public void testDataGeneral() {
		/*File dataFile = new File("./data/data.txt");
		File resultFile = new File("./data/result.txt");
		try {
			Scanner in = new Scanner(dataFile);
			FileWriter fileWriter = new FileWriter(resultFile);
			int count = 0;
			while (in.hasNextLine()) {
				//count++;
				String line = in.nextLine();
				int pos = line.indexOf(' ');
				String tag = line.substring(0, pos);
				String regex = line.substring(pos, line.length()).trim();
				System.out.println(tag + "\t" + regex);
				//double time = System.currentTimeMillis();
				int number = testWithRegex(regex);
				AllMutators.mutator.init();
				//time = System.currentTimeMillis() - time;
				System.out.println(AllMutators.mutator.getMutateCountMap());
				System.out.println("----> Writing into file <-----");
				
				AllMutators mutator = AllMutators.mutator;
				String content = "#String: " + number + " \ttotal count: " + mutator.getTotalCount() + " \tsecond: " + mutator.getSecondMutateCount() 
									+ " \ttag: " + tag +  " \tdetail:" + mutator.getMutateCountMap() + "\n";
				fileWriter.write(content);
				count += number;
			}
			//System.out.println(count);
			fileWriter.append("total number of String: " + count);
			fileWriter.append("\nDistribution of mutate: " + AllMutators.totalMutateCountMap);
			fileWriter.append("\nsize of mutate type: " + AllMutators.totalMutateCountMap.size());
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
	}
	
	@Test
	public void testDataDistribution() {
		ArrayList<String> typeNames = new ArrayList<String>();
		ArrayList<Integer> typeNumbers = new ArrayList<Integer>();
		File dataFile = new File("./data/data.txt");
		File resultFile = new File("./data/distributionResult1.txt");
		try {
			FileWriter fileWriter = new FileWriter(resultFile);
			for (int i = 0; i < 10; i++) {
				AllMutators.totalMutateCountMap.clear();
				Scanner in = new Scanner(dataFile);
				int count = 0;
				while (in.hasNextLine()) {
					//count++;
					String line = in.nextLine();
					int pos = line.indexOf(' ');
					String tag = line.substring(0, pos);
					String regex = line.substring(pos, line.length()).trim();
					//System.out.println(tag + "\t" + regex);
					//double time = System.currentTimeMillis();
					int number = 0;
					try {
						Pattern.compile(regex);
						
						RegExp extendExp = ExtendedRegex.getSimplifiedRegexp(regex);
						AllMutators.mutator.doubleMutate(extendExp);
						//System.out.println(regex);
						//number = testWithRegex(regex);
						AllMutators.mutator.init();
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}

					//time = System.currentTimeMillis() - time;
					//System.out.println(AllMutators.mutator.getMutateCountMap());
					//System.out.println("----> Writing into file <-----");
					
					//AllMutators mutator = AllMutators.mutator;
					//String content = " \ttag: " + tag +"\tnumber: " + number + "\tcount: "+ mutator.getTotalCount() +" \tdetail:" + mutator.unKilledMutants + "\n";
					//fileWriter.append(content);
					//System.out.println(content);
					//count += number;
				}
				//System.out.println(count);
				//fileWriter.append("total number of String: " + count);
				Map<String, Integer> map = new HashMap<String, Integer>();
				map = AllMutators.totalMutateCountMap;
				System.out.println(map);
				//fileWriter.append("\n" + map);
				if (typeNames.size() == 0) {
					for (String typeName: map.keySet()) {
						
						if (typeNames.size() == 0 || !typeName.contains("+")) {
							typeNames.add(0, typeName);
							typeNumbers.add(0, map.get(typeName));
						} else {
							typeNames.add(typeNames.size() - 1, typeName);
							typeNumbers.add(typeNumbers.size() - 1, map.get(typeName));
						}
						
					}
					fileWriter.append("" + typeNames);
					fileWriter.append("\n" + typeNumbers);
				} else {
					for (String typeName: typeNames) {
						typeNumbers.add(map.get(typeName));
					}
					fileWriter.append("\n" + typeNumbers);
				}
				//fileWriter.append("\nsize of mutate type: " + AllMutators.totalMutateCountMap.size());
				in.close();
				typeNumbers.clear();
			}
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testTech() {
		File dataFile = new File("./collectingData/data.txt");
		int fileNum = 5;
		for (; fileNum < 7; fileNum ++) {
			String resultFileName = "./collectingData/techParallelCollectingResult" + fileNum + ".txt";
			File resultFile = new File(resultFileName);
			try {
				FileWriter fileWriter = new FileWriter(resultFile);
				Scanner in = new Scanner(dataFile);
				//int count = 0;
				while (in.hasNextLine()) {
					//count++;
					String line = in.nextLine();
					int pos = line.indexOf(' ');
					String tag = line.substring(0, pos);
					String regex = line.substring(pos, line.length()).trim();
					System.out.println(tag + "\t" + regex);
					//double time = System.currentTimeMillis();
					Result result = testResultWithRegex(regex);
					fileWriter.append(tag + "\t" + result + "\n");
					AllMutators.mutator.init();
					//count += number;
				}
				in.close();
				fileWriter.close();

				//System.out.println(count);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Test
	public void testRegexlibEmail2() {
		Result result = testResultWithRegex("(.&~(((((((((((((([\\0-\\9]|[\\a-\\z])|\\_)|\\.)|\\%)|\\&)|\\=)|\\()|\\))|\\<)|\\>)|\\*)|\\/)|(\\+|\\-))))");
		System.out.println(result);
	}
	
	@Test
	public void testRegexlibDateTime() {
		Result result = testResultWithRegex("((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[1-9])|(1[0-2]))\\:([0-5][0-9])((\\s)|(\\:([0-5][0-9])\\s))([AM|PM|am|pm]{2,2})))?");
		System.out.println(result);
	}
	
	@Test
	public void testTour116() {
		File resultFile = new File("./data/testTour116.txt");
		try {
			FileWriter fileWriter = new FileWriter(resultFile);
			DSSet x = new DSSet();
			//RegExp regExp = ExtendedRegex.getSimplifiedRegexp(start);
			//BasicDSSetgenerator.generator.addStringsToDSSet(x, regExp, AllMutators.mutator.mutate(regExp));
			String regex = "(\"xd0xb7xd0xb2xd0xb5xd0xb7xd0xb4xd0xbdxd0xb0xd1x8f\"|(\"xd0xb7xd0xb2xd0xb5xd0xb7xd0xb4xd0xbdxd0xbexd0xb9\"|(\"xd0xb7xd0xb2xd1x91xd0xb7xd0xb4xd0xbdxd0xb0xd1x8f\"|\"xd0xb7xd0xb2xd1x91xd0xb7xd0xb4xd0xbdxd0xbexd0xb9\")))";
			x = MutRexPro.generateStrings(regex, GeneratorType.BASIC);
			Iterator<DistinguishingString> it = x.iterator();
			int countString = 0;
			int countMutants = 0;
			while(it.hasNext()) {
				DistinguishingString ds = it.next();
				RegExpSet value = x.getKilledMutants(ds);
				assert !value.isEmpty();
				//System.out.println(ds + " killed " + value);
				countString++;
				countMutants+=value.size();
				fileWriter.append(ds+"\n");
			}
			System.out.println("#String: " + countString);
			System.out.println("#killed mutanst:" +countMutants + "\t#all mutants:" + AllMutators.mutator.getTotalCount());
			System.out.println("#killed mutants: " + AllMutators.mutator.killedMutants.size());
			System.out.println("#not killed mutants: " + AllMutators.mutator.unKilledMutants);
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testTour119() {
    	String regex = "(\"sobid\"|(\"xd0xb0xd0xb3xd0xb5xd0xbdxd1x82xd1x81xd0xbaxd0xb8xd0xb9\"|(\"xd0xb0xd0xb3xd0xb5xd0xbdxd1x82xd1x81xd0xbaxd0xb8xd0xb5\"|(\"xd1x81xd0xbaxd0xb8xd0xb4xd0xbaxd0xb8\"|(\"xd0xb0xd0xb3xd0xb5xd0xbdxd1x81xd1x82xd0xb2xd0xbe\"|(\"xd1x80xd0xb8xd1x8dxd0xbbxd1x82xd0xbexd1x80xd1x81xd0xbaxd0xb8xd0xb5\"|(\"xd0xb2xd0xbexd0xb7xd0xbdxd0xb0xd0xb3xd1x80xd0xb0xd0xb6xd0xb4\"|(\"xd0xb0xd0xb3xd0xb5xd0xbdxd1x82xd1x81xd1x82xd0xb2xd0xbe\"|(\"xd0xbaxd0xbexd0xbcxd0xb8xd1x81xd1x81xd0xb8xd1x8f\"|(\"xd0xbaxd0xbexd0xbcxd0xbcxd0xb8xd1x81xd1x81xd0xb8xd1x8f\"|(\"xd0xbaxd0xbexd0xbcxd0xb8xd1x81xd1x81xd0xb8xd0xbexd0xbd\"|(\"xd0xbaxd0xbexd0xbcxd0xbcxd0xb8xd1x81xd0xb8xd1x8f\"|(\"xd0xbaxd0xbexd0xbcxd0xbcxd0xb8xd1x81xd0xb8xd1x8f\"|(\"xd1x81xd0xbaxd0xb8xd0xb4xd0xbaxd0xb0 xd0xbfxd0xbe xd0xbaxd0xbexd0xbcxd0xb8xd1x81\"|(\"xd1x81xd0xbaxd0xb8xd0xb4xd0xbaxd0xb8 xd0xbfxd0xbe xd0xbaxd0xbexd0xbcxd0xb8xd1x81\"|(\"xd0xbaxd0xbexd0xbcxd0xb8xd1x81xd1x81xd0xb8xd1x8e\"|\"xd0xbaxd0xbexd0xbcxd0xb8xd1x81xd1x81xd0xb8xd1x8f xd0xb0xd0xb3xd0xb5xd0xbdxd1x81xd1x82xd0xb2xd0xb0\"))))))))))))))))";
		Result aResult = testResultWithRegex(regex);
		System.out.println(aResult);
	}
	
	@Test
	public void testReglibEmail4() {
		String regex = "\\w+([-+.]\\w+)*\\@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*([,;]\\s*\\w+([-+.]\\w+)*\\@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*)*";
		testSinglewithRegex(regex);
	}
	
	@Test
	public void testReglibEUri10() {
		String regex = "[^\\\\\\./:\\*\\?\\\"<>\\|]{1}[^\\\\/:\\*\\?\\\"<>\\|]{0,254}";
		testSinglewithRegex(regex);	
	}
	
	@Test
	public void testReglibEUri6() {
		String regex = "[a-zA-Z0-9]+([a-zA-Z0-9\\-\\.]+)?\\.(com|org|net|mil|edu|COM|ORG|NET|MIL|EDU)";
		testResultWithRegex(regex);	
	}
	
	@Test
	public void testReglibNumber1() {
		String regex = "(0|(\\+)?[1-9]{1}[0-9]{0,8}|(\\+)?[1-3]{1}[0-9]{1,9}|(\\+)?[4]{1}([0-1]{1}[0-9]{8}|[2]{1}([0-8]{1}[0-9]{7}|[9]{1}([0-3]{1}[0-9]{6}|[4]{1}([0-8]{1}[0-9]{5}|[9]{1}([0-5]{1}[0-9]{4}|[6]{1}([0-6]{1}[0-9]{3}|[7]{1}([0-1]{1}[0-9]{2}|[2]{1}([0-8]{1}[0-9]{1}|[9]{1}[0-5]{1})))))))))";
		testSinglewithRegex(regex);	
		System.out.println(AllMutators.mutator.mutateCountMap);
	}
	
	@Test
	public void testReglibNumber2() {
		String regex = "[1-9]+[0-9]*";
		testSinglewithRegex(regex);	
		System.out.println(AllMutators.mutator.mutateCountMap);
	}
	
	@Test
	public void testReglibNumber7() {
		String regex = "(((\\d{1,3})(,\\d{3})*)|(\\d+))(.\\d+)?";
		testSinglewithRegex(regex);	
		System.out.println(AllMutators.mutator.mutateCountMap);
	}
	
	@Test
	public void testReglibNumber16() {
		String regex = "([0-9]*|\\d*\\.\\d{1}?\\d*)";
		testSinglewithRegex(regex);	
		System.out.println(AllMutators.mutator.mutateCountMap);
	}
	
	@Test
	public void testReglibDatetime13() {
		String regex = "((\\d{2})|(\\d))\\/((\\d{2})|(\\d))\\/((\\d{4})|(\\d{2}))";
		testSinglewithRegex(regex);	
		System.out.println(AllMutators.mutator.mutateCountMap);
	}
	
	@Test
	public void testReglibDatetime14() {
		String regex = "((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[1-9])|(1[0-2]))\\:([0-5][0-9])((\\s)|(\\:([0-5][0-9])\\s))([AM|PM|am|pm]{2,2})))?";
		testSinglewithRegex(regex);	
		System.out.println(AllMutators.mutator.mutateCountMap);
	}
	
	@Test
	public void testReglibAddressPhone1() {
		String regex = "[\\\\(]{0,1}([0-9]){3}[\\\\)]{0,1}[ ]?([^0-1]){1}([0-9]){2}[ ]?[-]?[ ]?([0-9]){4}[ ]*((x){0,1}([0-9]){1,5}){0,1}";
		testSinglewithRegex(regex);
	}
	
	@Test
	public void testReglibAddressPhone14() {
		String regex = "(\\s*\\(?0\\d{4}\\)?(\\s*|-)\\d{3}(\\s*|-)\\d{3}\\s*)|(\\s*\\(?0\\d{3}\\)?(\\s*|-)\\d{3}(\\s*|-)\\d{4}\\s*)|(\\s*(7|8)(\\d{7}|\\d{3}(\\-|\\s{1})\\d{4})\\s*)";
		testSinglewithRegex(regex);
		System.out.println(AllMutators.mutator.mutateCountMap);
	}
	
	@Test
	public void testREinfoProgramming() {
		String regex = "/\\*.*?\\*/";
		testSinglewithRegex(regex);	
		System.out.println(AllMutators.mutator.mutateCountMap);
	}
	
	@Test
	public void testTour8() {
		String regex = "\"//\"(.)*\\:(.)*@";
		testSinglewithRegex(regex);	
		System.out.println(AllMutators.mutator.mutateCountMap);
	}
	
	@Test
	public void testTour7() {
		String regex = "\\\\.\\_((.)*)?";
		testSinglewithRegex(regex);	
		System.out.println(AllMutators.mutator.mutateCountMap);
	}
	
	@Test
	public void testTour24() {
		String regex = "((.&~((\\-|\\/)))){1,}(\\-)?(((.&~(\\/))){1,})?(\\/)?(.)*";
		testSinglewithRegex(regex);	
		System.out.println(AllMutators.mutator.mutateCountMap);
	}
	
	@Test
	public void testTour78() {
		String regex = "[9-0]";
		testSinglewithRegex(regex);	
		System.out.println(AllMutators.mutator.mutateCountMap);
	}
	
	@Test
	public void testGithubPython6() {
		String regex = "\\[\\[[^\\[\\]]*?\\|\\s*([^\\|\\[]*?)\\s*\\]\\]";
		testSinglewithRegex(regex);	
		System.out.println(AllMutators.mutator.mutateCountMap);
	}
	
	@Test
	public void testEglibmic8() {
		String regex = "([a-zA-Z0-9@*#]{8,15})";
		testSinglewithRegex(regex);	
		System.out.println(AllMutators.mutator.mutateCountMap);
	}
	
	
	@Test
	public void testSingle() {
		//testWithRegex("(\\w*)\\s*=\\s*bits\\s*,\\s*(.*?),\\s*(.*?),*\\s*\\[(\\d):(.*?)\\](\\s*,\\s*(\\\".*\\\"))*.*");
		//System.out.println(testWithRegex("\\[\\[[^\\[\\]]*?\\|\\s*([^\\|\\[]*?)\\s*\\]\\]"));
		//System.out.println(testWithRegex("<tr.*?>\\s*<td>(.*?)</td>\\s*<td>(.*?)</td>"));
		//System.out.println(testWithRegex("<[a-zA-Z][^>]*\\son\\w+=(\\w+|'[^']*'|\"[^\"]*\")[^>]*>"));
		//System.out.println(testWithRegex("#define[ \\t][ \\t]*([A-Z0-9][A-Z0-9_]*)[ \\t][ \\t]*([0-9][0-9]*)"));
		//System.out.println(testWithRegex("([+-])(\\d\\d):?(\\d\\d?)?"));
		//testWithRegex("[0]{1}[,\\\\.][0-9]{1}[0-9]{1}[0-9]{1}");
		//testWithRegex("(\\s*\\(?0\\d{4}\\)?\\s*\\d{6}\\s*)|(\\s*\\(?0\\d{3}\\)?\\s*\\d{3}\\s*\\d{4}\\s*)");
		
		//ArrayList<String> typeNames = new ArrayList<String>();
		//ArrayList<Integer> typeNumbers = new ArrayList<Integer>();
		//double time = System.currentTimeMillis();
		Result result = testResultWithRegex("[{|\\(]?[0-9a-fA-F]{8}[-]?([0-9a-fA-F]{4}[-]?){3}[0-9a-fA-F]{12}[\\)|}]?");
		System.out.println(result);
		//time = System.currentTimeMillis() - time;
		/*System.out.println(time);
		Map<String, Integer> map = new HashMap<String, Integer>();
		map = AllMutators.totalMutateCountMap;
		System.out.println(map);
		//System.out.println("total mutate: " + AllMutators.mutator.getTotalCount() + " second: " + AllMutators.mutator.getSecondMutateCount());
		for (String typeName: map.keySet()) {
			
			if (typeNames.size() == 0 || !typeName.contains("+")) {
				typeNames.add(0, typeName);
				typeNumbers.add(0, map.get(typeName));
			} else {
				typeNames.add(typeNames.size() - 1, typeName);
				typeNumbers.add(typeNames.size() - 1, map.get(typeName));
			}
			
		}
		System.out.println(typeNames);
		System.out.println(typeNumbers);
		time = System.currentTimeMillis();
		testWithRegex("(#|0x)?([0-9A-F]{2}){3,4}");
		time = System.currentTimeMillis() - time;
		System.out.println(time);
		System.out.println(AllMutators.mutator.getMutateCountMap());
		System.out.println("total mutate: " + AllMutators.mutator.getTotalCount() + " second: " + AllMutators.mutator.getSecondMutateCount());
		
		time = System.currentTimeMillis();
		testWithRegex("(5[1-5][0-9]{2}|222[1-9]|22[3-9][0-9]|2[3-6][0-9]{2}|27[01][0-9]|2720)[0-9]{12}");
		time = System.currentTimeMillis() - time;
		System.out.println(time);
		System.out.println(AllMutators.mutator.getMutateCountMap());
		System.out.println("total mutate: " + AllMutators.mutator.getTotalCount() + " second: " + AllMutators.mutator.getSecondMutateCount());
		
		time = System.currentTimeMillis();
		testWithRegex("(4[0-9]{12}([0-9]{3})? |  (5[1-5][0-9]{2} | 222[1-9]|22[3-9][0-9]|2[3-6][0-9]{2}|27[01][0-9]|2720)[0-9]{12} | 3[47][0-9]{13} | 3(0[0-5]|[68][0-9])[0-9]{11} | 6(011|5[0-9]{2})[0-9]{12} | (2131|1800|35\\d{3})\\d{11})");
		time = System.currentTimeMillis() - time;
		System.out.println(time);
		System.out.println(AllMutators.mutator.getMutateCountMap());
		System.out.println("total mutate: " + AllMutators.mutator.getTotalCount() + " second: " + AllMutators.mutator.getSecondMutateCount());
		
		int number = testWithRegex("(\"25\"[\\0-\\5]|(\\2[\\0-\\4][\\0-\\9]|((\\0|\\1))?[\\0-\\9]([\\0-\\9])?))\\\\.(\"25\"[\\0-\\5]|(\\2[\\0-\\4][\\0-\\9]|((\\0|\\1))?[\\0-\\9]([\\0-\\9])?))\\\\.(\"25\"[\\0-\\5]|(\\2[\\0-\\4][\\0-\\9]|((\\0|\\1))?[\\0-\\9]([\\0-\\9])?))\\\\.(\"25\"[\\0-\\5]|(\\2[\\0-\\4][\\0-\\9]|((\\0|\\1))?[\\0-\\9]([\\0-\\9])?))");
		System.out.println(number);*/
		
		/*time = System.currentTimeMillis();
		testWithRegex("test(er|ing|ed|s)?");
		time = System.currentTimeMillis() - time;
		System.out.println(time);
		System.out.println(AllMutators.mutator.getMutateCountMap());
		System.out.println("total mutate: " + AllMutators.mutator.getTotalCount() + " second: " + AllMutators.mutator.getSecondMutateCount());
		
		System.out.println("Distribution: " + AllMutators.totalMutateCountMap);
		System.out.println("mutate size: " + AllMutators.totalMutateCountMap.size());*/
	}
	
	private Result testResultWithRegex(String regex) {
		// TODO Auto-generated method stub
		AllMutators.mutator.setTotalCount(0);
		double time = System.currentTimeMillis();
		try {
			DSSet x = new DSSet();
			//RegExp regExp = ExtendedRegex.getSimplifiedRegexp(start);
			//BasicDSSetgenerator.generator.addStringsToDSSet(x, regExp, AllMutators.mutator.mutate(regExp));
			x = MutRexPro.generateStrings(regex, GeneratorType.COLLECTING_PAR_DAs);
			if (x == null) {
				time = System.currentTimeMillis() - time;
				return new Result(0, time, 0);
			}
			Iterator<DistinguishingString> it = x.iterator();
			int countString = 0;
			int countKilled = 0;
			while(it.hasNext()) {
				DistinguishingString ds = it.next();
				RegExpSet value = x.getKilledMutants(ds);
				assert !value.isEmpty();
				//System.out.println(ds);
				countString++;
				countKilled += value.size();
			}
			
			time = System.currentTimeMillis() - time;
			//System.out.println(countKilled + " " + AllMutators.mutator.getTotalCount());
			double mutationScore = countKilled/(AllMutators.mutator.getTotalCount()*1.0);
			//System.out.println("count: " + countString);
			return new Result(countString, time, mutationScore);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
		}
		time = System.currentTimeMillis() - time;
		return new Result(0, time, 0);
	}
	
	private Result testResultWithRegexTimeout(String regex) {
		// TODO Auto-generated method stub
		AllMutators.mutator.setTotalCount(0);
		double time = System.currentTimeMillis();
		ExecutorService executorService= Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*2);
		Future<String> future = executorService.submit(new Callable<String>() {
            @Override
            public String call() {
            	try {
        			DSSet x = new DSSet();
        			//RegExp regExp = ExtendedRegex.getSimplifiedRegexp(start);
        			//BasicDSSetgenerator.generator.addStringsToDSSet(x, regExp, AllMutators.mutator.mutate(regExp));
        			x = MutRexPro.generateStrings(regex, GeneratorType.COLLECTING_PAR_DAs);
        			if (x == null) {
        				return "null";
        			}
        			Iterator<DistinguishingString> it = x.iterator();
        			int countString = 0;
        			int countKilled = 0;
        			while(it.hasNext()) {
        				DistinguishingString ds = it.next();
        				RegExpSet value = x.getKilledMutants(ds);
        				assert !value.isEmpty();
        				System.out.println(ds);
        				countString++;
        				countKilled += value.size();
        			}
        			//System.out.println(countKilled + " " + AllMutators.mutator.getTotalCount());
        			double mutationScore = countKilled/(AllMutators.mutator.getTotalCount()*1.0);
        			//System.out.println("count: " + countString);
        			String resultString = countString + " " + mutationScore;
        			return resultString;
        		} catch (Exception e) {
        			// TODO: handle exception
        			System.out.println(e.getMessage());
        		}
        		
                return  "null";
            }
        });
        try {
        	time = System.currentTimeMillis() - time;
            String reString = future.get(5, TimeUnit.SECONDS);
            if (reString.equals("null")) {
        		return new Result(0, time, 0);
			} else {
				Scanner inScanner = new Scanner(reString);
				int count = inScanner.nextInt();
				double score= inScanner.nextDouble();
				return new Result(count, time, score);
			}
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
        	e.printStackTrace();
            future.cancel(true);
            System.out.println("hello");
        } finally {
        	executorService.shutdown();
        }
		
		return new Result(0, time, 0);
	}

	private int testWithRegex(String regex) {
		// TODO Auto-generated method stub
		try {
			DSSet x = new DSSet();
			//RegExp regExp = ExtendedRegex.getSimplifiedRegexp(start);
			//BasicDSSetgenerator.generator.addStringsToDSSet(x, regExp, AllMutators.mutator.mutate(regExp));
			x = MutRexPro.generateStrings(regex, GeneratorType.BASIC);
			if (x == null) {
				return 0;
			}
			Iterator<DistinguishingString> it = x.iterator();
			int countString = 0;
			while(it.hasNext()) {
				DistinguishingString ds = it.next();
				RegExpSet value = x.getKilledMutants(ds);
				assert !value.isEmpty();
				System.out.println(ds + " killed " + value);
				countString++;
			}
			//System.out.println("count: " + countString);
			return countString;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
		}
		return 0;
	}
	
	private void testSinglewithRegex(String regex) {
		//RegExp regExp = ExtendedRegex.getSimplifiedRegexp(start);
		//BasicDSSetgenerator.generator.addStringsToDSSet(x, regExp, AllMutators.mutator.mutate(regExp));
	
    	DSSet x = new DSSet();
    	x = MutRexPro.generateStrings(regex, GeneratorType.COLLECTING_PAR_DAs);
		Iterator<DistinguishingString> it = x.iterator();
		int countString = 0;
		int countMutants = 0;
		while(it.hasNext()) {
			DistinguishingString ds = it.next();
			RegExpSet value = x.getKilledMutants(ds);
			assert !value.isEmpty();
			System.out.println(ds + " killed " + value);
			countString++;
			countMutants+=value.size();
		}
		System.out.println("#String: " + countString);
		System.out.println("#killed mutanst:" +countMutants + "\t#all mutants:" + AllMutators.mutator.getTotalCount());
		System.out.println("#killed mutants: " + AllMutators.mutator.killedMutants.size());
		System.out.println("#not killed mutants: " + AllMutators.mutator.unKilledMutants);
    
	}
	
	
	
	class Result {
		int countString;
		double time;
		double mutationScore;
		
		public Result(int count, double time, double score) {
			this.countString = count;
			this.time = time;
			this.mutationScore = score;
		}
		
		@Override
		public String toString() {
			return countString + "\t" + time + "\t" + mutationScore;
		}
	}
}
