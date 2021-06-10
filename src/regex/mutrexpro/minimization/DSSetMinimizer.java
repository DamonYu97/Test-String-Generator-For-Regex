package regex.mutrexpro.minimization;

import dk.brics.automaton.RegExp;
import regex.mutrexpro.ds.DSSet;

public abstract class DSSetMinimizer {

	public abstract void minimize(DSSet dsS, RegExp regex);

}
