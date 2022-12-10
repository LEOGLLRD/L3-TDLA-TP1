import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Main {
    public static void main(String[] args) {


        AFNDe a3 = new AFNDe("NFA3e.json");

        System.out.println("Minimize"+a3.minimize());


       // System.out.println(a3);
        // AFNDe transpo = a3.transpose();
        // System.out.println(transpo.getDelta());

        DFA d3 = new DFA("DFA3.json");
        System.out.println(d3);
        System.out.println("\n"+d3.tranpose());


    }
}
