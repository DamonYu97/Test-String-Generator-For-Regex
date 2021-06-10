package regex.mutrexpro.main;

public enum GeneratorType {
	BASIC, MONITORING, COLLECTING_NO_LIMIT, 
	COLLECTING_PAR_ONLY_POS_AND_NEG, 
	COLLECTING_PAR_NO_LIMIT, 
	COLLECTING_PAR_DAs, COLLECTING_QUIT_AFTER_N, 
	COLLECTING_PAR_MUTs;
	
	public static enum Orientation{
		RANDOM, PREF_ACCEPT, PREF_REJECT;
	}
}