package regex.mutrexpro;

import regex.mutrexpro.ds.DSSetGenerator;
import regex.mutrexpro.ds.DistinguishingAutomaton;

public class CollectDSSetGeneratorNoLimit extends CollectDSSetGenerator {
	public static DSSetGenerator generator = new CollectDSSetGeneratorNoLimit();

	protected CollectDSSetGeneratorNoLimit() {
	}

	@Override
	boolean stop(DistinguishingAutomaton da) {
		return false;
	}
}