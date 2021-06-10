package regex.operators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import dk.brics.automaton.oo.*;

/**
 * 
 * @author Yu Lilin
 * @date 2020.03.14
 * 
 * the operation UnionRestriction creates a list of mutated regular expressions by 
 * restricting elements in union of given regular expression r.
 * e.g. abc|efg -->  a(bc|e)fg
 *
 */
public class UnionRestriction extends RegexMutator {
	public static UnionRestriction mutator = new UnionRestriction();
	
	private UnionRestriction() {
		super(new UnionChangeVisitor());
	}
	
	
	static class UnionChangeVisitor extends RegexVisitorAdapterList {
		
		/**
		 * If visit a repeat operation, then assume there is no error
		 */
		@Override
		public List<ooregex> visit(REGEXP_REPEAT r) {
			return Collections.EMPTY_LIST;
		}
		
		/**
		 * If visit a union instead of char class, return a list of mutated regexs of limited combination of r
		 */
		@Override
		public List<ooregex> visit(REGEXP_UNION r) {
			//if r is char class, then no mutation
			if (isCharacterClass(r)) {
				return Collections.EMPTY_LIST;
			}
			//get left and right parts of r 
			ooregex leftPart = r.exp1;
			ooregex rightPart = r.exp2;
			
			//create left and right combinations
			List<Combination> leftCombinations = createCombinations(leftPart, true);
			List<Combination> rightCombinations = createCombinations(rightPart, false);
			
			List<ooregex> result = new ArrayList<ooregex>();
			//combine left and right parts
			if (leftCombinations.size() == 0 && rightCombinations.size() == 0 ) {
				//if no combinations available
				//e.g. (a*|b)  
				return Collections.EMPTY_LIST;
			} /*else if (leftCombinations.size() == 0 && rightCombinations.size() > 0) {
				//e.g. (a|bcd)
				for (int i = 0; i < rightCombinations.size(); i++) {
					Combination combination = rightCombinations.get(i);
					REGEXP_UNION union = new REGEXP_UNION(leftPart, combination.leftOoregex);
					result.add(new REGEXP_CONCATENATION(union, combination.rightOoregex));
				}
			} else if (rightCombinations.size() == 0) {
				//e.g. (bcd|a)
				for (int i = 0; i < leftCombinations.size(); i++) {
					Combination combination = leftCombinations.get(i);
					REGEXP_UNION union = new REGEXP_UNION(combination.rightOoregex, rightPart);
					result.add(new REGEXP_CONCATENATION(combination.leftOoregex, union));
				}
			} */else {
				//e.g. (a|bcd)
				for (int i = 0; i < rightCombinations.size(); i++) {
					Combination combination = rightCombinations.get(i);
					REGEXP_UNION union = new REGEXP_UNION(leftPart, combination.leftOoregex);
					result.add(new REGEXP_CONCATENATION(union, combination.rightOoregex));
				}
				//e.g. (bcd|a)
				for (int i = 0; i < leftCombinations.size(); i++) {
					Combination combination = leftCombinations.get(i);
					REGEXP_UNION union = new REGEXP_UNION(combination.rightOoregex, rightPart);
					result.add(new REGEXP_CONCATENATION(combination.leftOoregex, union));
				}
				//e.g (ab|cde)
				for (int i = 0; i < leftCombinations.size(); i++) {
					Combination leftCombination = leftCombinations.get(i);
					for (int j = 0; j < rightCombinations.size(); j++) {
						Combination rightCombination = rightCombinations.get(j);
						REGEXP_UNION union = new REGEXP_UNION(leftCombination.rightOoregex, rightCombination.leftOoregex);
						result.add(new REGEXP_CONCATENATION(
								new REGEXP_CONCATENATION(leftCombination.leftOoregex, union), rightCombination.rightOoregex));
					}	
				}
			}
			//System.out.println("combinations: " + leftCombinations + rightCombinations);
			return result;
		}
		
		/**
		 * 
		 * @param REGEXP_UNION r
		 * @return {code true} if r is char class. Otherwise, {code false}
		 */
		private boolean isCharacterClass(REGEXP_UNION r) {
			List<ooregex> parts = REGEXP_UNION.splitUnion(r);
			for (ooregex part : parts) {
				if (!(part instanceof REGEXP_CHAR || part instanceof REGEXP_CHAR_RANGE)) {
					return false;
				}
			}
			return true;
		}
		
		/**
		 * Container class to store two regexs
		 *
		 */
		private class Combination {
			public ooregex leftOoregex;
			public ooregex rightOoregex;
			public Combination(ooregex leftOoregex, ooregex rightOoregex) {
				this.leftOoregex = leftOoregex;
				this.rightOoregex = rightOoregex;
			}
			public String toString() {
				return leftOoregex.toString() + " " + rightOoregex.toString();
			}
		}
		
		/**
		 * 
		 * @param ooregex sidePart
		 * @return a list of different combination of sidePart: List<Combination> 
		 */
		private List<Combination> createCombinations(ooregex sidePart, boolean isLeft) {
			List<Combination> combinations = new ArrayList<Combination>();
			if (sidePart instanceof oosimpleexp) {  
				//if sidePart is simple expression 
				//combine different connected chars.  e,g.  abcd --> [a bcd,  ab cd,  abc d] 
				String sidePartString = ((oosimpleexp) sidePart).s;
				int length = sidePartString.length();
				int i = 1;
				int end = length;
				if (length > 4) {
					if (isLeft) {
						i = length - 3;
					} else {
						end = i + 3;
					}
				}
				for (; i < end; i++) {
					ooregex left, right;
					if (i == 1) {
						//first char. --> a
						left = new REGEXP_CHAR(sidePartString.charAt(0));
					} else {
						//not first char. --> ab, abc
						left = oosimpleexp.createoosimpleexp(sidePartString.substring(0,i));
					}
					if (i == length - 1) {
						//last char. --> d
						right = new REGEXP_CHAR(sidePartString.charAt(i));
					} else {
						//not last char.  --> bcd, cd
						right = oosimpleexp.createoosimpleexp(sidePartString.substring(i));
					}
					combinations.add(new Combination(left, right));
				}
			} else if (sidePart instanceof REGEXP_CONCATENATION) {
				//if sidePart is concatenation of regexs
				//combine different connected regexs. e.g.  abc[def]g* --> [abc [def]g,  abc[def] g]
				//note that we ignore the combination inside simple expressions here like abc.
				
				/**
				 * Solution 1: get all parts of concatenation first, then keep combining all the different 
				 * 				left parts and right parts, and combine left and right part.
				 * 
				 
				List<ooregex> parts = REGEXP_CONCATENATION.splitConcatenation(sidePart);
				int length = parts.size();
				for (int i = 1; i < length; i++) {
					ooregex left, right;
					if (i == 1) {
						left = parts.get(0);
					} else {
						left = oobinregex.makeBinExpression(REGEXP_CONCATENATION.class, parts.subList(0, i));
					}
					if (i == length - 1) {
						right = parts.get(i);
					} else {
						right = oobinregex.makeBinExpression(REGEXP_CONCATENATION.class, parts.subList(i, length));
					}
					combinations.add(new Combination(left, right));
				}**/
				
				/**
				 * Solution 2: Keep combining left parts, and combine left part with rest right part.
				 */
				ooregex left = ((REGEXP_CONCATENATION) sidePart).exp1;
				ooregex right = ((REGEXP_CONCATENATION) sidePart).exp2;
				combinations.add(new Combination(left, right));
				while (right instanceof REGEXP_CONCATENATION) {
					//combine new left parts
					left = new REGEXP_CONCATENATION(left, ((REGEXP_CONCATENATION) right).exp1);
					//get rest right parts
					right = ((REGEXP_CONCATENATION) right).exp2;
		
					combinations.add(new Combination(left, right));
				}
				int size = combinations.size();
				if (size > 3) {
					if (isLeft) {
						combinations.subList(0, size - 3).clear();
					} else {
						combinations.subList(3, size).clear();
					}
				}

				Object[] combTmp = combinations.toArray();
				for (Object o : combTmp) {
					Combination c = (Combination)o;
					if (c.leftOoregex instanceof oosimpleexp) {
						//System.out.println("case1: " + c.leftOoregex);
						String tmp = ((oosimpleexp) c.leftOoregex).s;
						int start = 0, end = tmp.length() - 1;
						if (end > 3) {
							if (isLeft) {
								start = end - 3;
							} else {
								end = start + 3;
							}
						}
						for (int i = start; i < end; i++) {
							left = oosimpleexp.createoosimpleexp(tmp.substring(0,i+1));
							right = new REGEXP_CONCATENATION(oosimpleexp.createoosimpleexp(tmp.substring(i+1)),
									c.rightOoregex);
							combinations.add(new Combination(left, right));

						}
					} else if (c.leftOoregex instanceof REGEXP_CONCATENATION && ((REGEXP_CONCATENATION) c.leftOoregex).exp2 instanceof oosimpleexp) {
						//System.out.println("case2: " + ((REGEXP_CONCATENATION) c.leftOoregex).exp2);
						String tmp = ((oosimpleexp) ((REGEXP_CONCATENATION) c.leftOoregex).exp2).s;
						int start = 0, end = tmp.length() - 1;
						if (end > 3) {
							if (isLeft) {
								start = end - 3;
							} else {
								end = start + 3;
							}
						}
						for (int i = start; i < end; i++) {
							left = new REGEXP_CONCATENATION(((REGEXP_CONCATENATION) c.leftOoregex).exp1,
									oosimpleexp.createoosimpleexp(tmp.substring(0,i+1)));
							right = new REGEXP_CONCATENATION(oosimpleexp.createoosimpleexp(tmp.substring(i+1)),
									c.rightOoregex);
							combinations.add(new Combination(left, right));
						}
					}
				}
				Combination last = (Combination) combTmp[combTmp.length-1];
				if (last.rightOoregex instanceof oosimpleexp) {
					//System.out.println("case3: " + last.rightOoregex);
					String tmp = ((oosimpleexp) last.rightOoregex).s;
					int start = 0, end = tmp.length() - 1;
					if (end > 3) {
						if (isLeft) {
							start = end - 3;
						} else {
							end = start + 3;
						}
					}
					for (int i = start; i < end; i++) {
						left = new REGEXP_CONCATENATION(last.leftOoregex,
								oosimpleexp.createoosimpleexp(tmp.substring(0,i+1)));
						right = oosimpleexp.createoosimpleexp(tmp.substring(i+1));
						combinations.add(new Combination(left, right));
					}
				} else if (last.rightOoregex instanceof REGEXP_CONCATENATION && ((REGEXP_CONCATENATION) last.rightOoregex).exp1 instanceof oosimpleexp) {
					//System.out.println("case3: " + ((REGEXP_CONCATENATION) last.rightOoregex).exp1);
					String tmp = ((oosimpleexp) ((REGEXP_CONCATENATION) last.rightOoregex).exp1).s;
					int start = 0, end = tmp.length() - 1;
					if (end > 3) {
						if (isLeft) {
							start = end - 3;
						} else {
							end = start + 3;
						}
					}
					for (int i = start; i < end; i++) {
						left = new REGEXP_CONCATENATION(last.leftOoregex,
								oosimpleexp.createoosimpleexp(tmp.substring(0,i+1)));
						right = new REGEXP_CONCATENATION(oosimpleexp.createoosimpleexp(tmp.substring(i+1)),
								((REGEXP_CONCATENATION) last.rightOoregex).exp2);
						combinations.add(new Combination(left, right));
					}
				}

			} else {
				//Char range, char, complement, intersection, interval, repeat, special char, union
				
			}
			return combinations;
		}

		
		@Override
		public String getCode() {
			return "UR";
		}
	}
	@Override
	public String getCode() {
		return "UR";
	}

}
