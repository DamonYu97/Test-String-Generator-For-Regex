package mutrex.main;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import org.junit.jupiter.api.Test;

public class test {

    @Test
    void negatedTest() {
        RegExp regex = new RegExp("([a-zA-Z0-9_\\-\\.]+)\\@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})");
        RegExp exp = new RegExp("((([a-zA-Z0-9]|_)|-)|.)+\\@((([a-zA-Z0-9]|_)|-)|\\.)+\\.[a-zA-Z]{2,5}");
        Automaton a = regex.toAutomaton();
        Automaton b = exp.toAutomaton();
        System.out.println(b.minus(a).getShortestExample(true));
    }
}
