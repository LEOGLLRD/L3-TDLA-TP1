import java.util.Set;

public class Main {
    public static void main(String[] args) {

//        DFA automate = new DFA("DFA1.json");
//        System.out.println(automate.toString());
//        System.out.println(automate.accept("ab"));
//
//        NFA autoNFA = new NFA("NFA1.json");
//        System.out.println("starts : " + autoNFA.getStarts().toString());
//        Transition<Set<State>> t = new Transition<Set<State>>(autoNFA.getStarts(), new Symbol("a"));
//        System.out.println("results : " + autoNFA.applyDeltaTilde(t).toString());
//
//        System.out.println(autoNFA.accept("a"));

        AFNDe afnDe = new AFNDe("NFA1e.json");
        System.out.println(afnDe.toString());


    }
}
