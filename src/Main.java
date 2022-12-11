import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Main {
    public static void main(String[] args) {


        DFA dfa1 = new DFA("DFA1.json");
        System.out.println("DFA1.json");
        System.out.println("Accept aaab ? : " + dfa1.accept("aaab"));
        System.out.println("Accept abab ? : " + dfa1.accept("abab"));
        System.out.println("Accept \"\" ? : " + dfa1.accept(""));
        System.out.println();

        DFA dfa2 = new DFA("DFA2.json");
        System.out.println("DFA2.json");
        System.out.println("Accept 00011 ? : " + dfa2.accept("00011"));
        System.out.println("Accept 000 ? : " + dfa2.accept("000"));
        System.out.println();


        DFA dfa3 = new DFA("DFA3.json");
        System.out.println("DFA3.json");
        System.out.println("Accept aaa ? : " + dfa3.accept("aaa"));
        System.out.println("Accept bbababbb ? : " + dfa3.accept("bbababbb"));
        System.out.println("Accept b ? : " + dfa3.accept("b"));
        System.out.println();


        NFA nfa1 = new NFA("NFA1.json");
        System.out.println("NFA1.json");
        System.out.println("Accept abbbb ? : " + nfa1.accept("abbbb"));
        System.out.println("Accept b ? : " + nfa1.accept("b"));
        System.out.println("Accept aabb ? : " + nfa1.accept("aabb"));
        System.out.println();

        NFA nfa2 = new NFA("NFA2.json");
        System.out.println("NFA2.json");
        System.out.println("Accept 00001 ? : " + nfa2.accept("00001"));
        System.out.println("Accept 0000000 ? : " + nfa2.accept("0000000"));
        System.out.println("Accept 01 ? : " + nfa2.accept("01"));
        System.out.println();

        NFA nfa3 = new NFA("NFA3.json");
        System.out.println("NFA3.json");
        System.out.println("Accept abbaab ? : " + nfa3.accept("abbaab"));
        System.out.println("Accept bbaaaba ? : " + nfa3.accept("bbaaaba"));
        System.out.println();

        AFNDe afnDe1 = new AFNDe("NFA1e.json");
        System.out.println("NFA1e.json");
        System.out.println("Accept 010 ? : " + afnDe1.accept("010"));
        System.out.println("Accept 11 ? : " + afnDe1.accept("11"));
        System.out.println();


        AFNDe afnDe3 = new AFNDe("NFA3e.json");
        System.out.println("NFA3e.json");
        System.out.println("Accept 01 ? : " + afnDe3.accept("01"));
        System.out.println("Accept 0 ? : " + afnDe3.accept("0"));
        System.out.println();


        System.out.println("Transposé de NFA1e : ");
        System.out.println(afnDe1);
        System.out.println("\ndevient : ");
        System.out.println(afnDe1.transpose());
        System.out.println();

        System.out.println("Transposé de NFA3e : ");
        System.out.println(afnDe3);
        System.out.println("\ndevient : ");
        System.out.println(afnDe3.transpose());
        System.out.println();

        System.out.println("Minimisation de NFA3e : \n" + afnDe3);
        System.out.println("\ndevient : \n" + afnDe3.minimize());

        System.out.println("\nTest si même résultat avec automate minimisé : ");
        System.out.println("Accept 01 ? : " + afnDe3.minimize().accept("01"));
        System.out.println("Accept 0 ? : " + afnDe3.minimize().accept("0"));




    }
}
