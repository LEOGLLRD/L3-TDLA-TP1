import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Main {
    public static void main(String[] args) {


        AFNDe a3 = new AFNDe("NFA3e.json");

//        HashSet<State> s2 = new HashSet<>();
//        s2.add(new State("q1"));
//        System.out.println(a3.epsilonClause(s2));


        System.out.println(a3);

        AFNDe transpo = a3.transpose();
        System.out.println(transpo.getDelta());


    }
}
