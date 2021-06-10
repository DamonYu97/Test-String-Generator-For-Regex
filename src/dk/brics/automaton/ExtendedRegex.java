package dk.brics.automaton;

// extend regex with some extra metachar
//\w 	Find a word character => ADDED
//\d 	Find a digit  => ADDED
//\s 	Find a whitespace character

/**
 * This class allows to build a regex from a string with more metachar than
 * those supported by RegExp dk.brics.automaton library
 * 
 */
public class ExtendedRegex {

	public static RegExp getSimplifiedRegexp(String s) {
		String eString = extendRegex(s);
		return new RegExp(eString);
	}

	public static String extendRegex(String s) {
		//System.out.println("toSimplified: " + s);
		// \w is replaced by [a-zA-Z0-9_]
		s = s.replaceAll("\\\\w", "[a-zA-Z0-9_]");
		//System.out.println(s);
		// \d is replaced by [0-9]
		s = s.replaceAll("\\\\d", "[0-9]");
		//System.out.println(s);
		// \s is replaced by [ \t\r\n\f]
		s = s.replaceAll("\\\\s", " ");
		//System.out.println(s);
		return s;
	}

}