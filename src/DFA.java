import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author Clément Moreau
 */

// Automate fini déterministe
public class DFA extends FSM {

    // Unique état de départ
    private State start;

    public State getStart() {
        return start;
    }

    // Fonction de Transititon
    private Map<Transition<State>, State> delta;

    public Map<Transition<State>, State> getDelta() {
        return delta;
    }

    // Constructeur par défaut
    public DFA(Set<State> _states,
               Set<Symbol> _alphabet,
               State _start,
               Set<State> _ends,
               Map<Transition<State>, State> _delta) {

        super(_states, _alphabet, _ends);
        start = _start;
        delta = _delta;
    }

    // Constructeur par fichier
    public DFA(String path) {

        super(path);

        Object obj = null;
        try {
            obj = new JSONParser().parse(new FileReader(path));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        JSONObject jo = (JSONObject) obj;

        start = new State((String) jo.get("start"));
        System.err.print(this.getStates().contains(start) ? "" : "État initial " + start + " ∉ Q\n");

        JSONArray ja = (JSONArray) jo.get("delta");

        delta = new HashMap<Transition<State>, State>();

        for (Object _o : ja) {

            JSONObject _jo = (JSONObject) _o;

            State q = new State((String) _jo.get("state"));
            System.err.print(this.getStates().contains(q) ? "" : "État " + q.toString() + " ⊄ Q\n");

            Symbol a = new Symbol((String) _jo.get("symbol"));
            System.err.print(this.getAlphabet().contains(a) ? "" : "Symbole " + a.toString() + " ∉ ∑\n");

            Transition<State> t = new Transition<State>(q, a);

            State p = new State((String) _jo.get("image"));
            System.err.print(this.getStates().contains(p) ? "" : "Image " + p.toString() + " ⊄ Q\n");

            delta.put(t, p);
        }

    }

    // Application de la fonction de transition
    public State applyDelta(Transition<State> t) {

        return delta.get(t);

    }


    // A vous de jouer !
    public boolean accept(String x) {

        State current = this.start;

        for (char a : x.toCharArray()) {

            Transition t = new Transition(current, new Symbol("" + a));
            current = delta.get(t);

        }

        return this.getEnds().contains(current);

    }

    //Donne la transposée d'un DFA. Cela devient un NFA, car on peut avoir plusieurs état de début
    public NFA tranpose(){

        Set<State> newStarts = this.getEnds();
        HashSet<State> newEnds = new HashSet<>();
        newEnds.add(getStart());

        HashMap<Transition<State>, Set<State>> newDelta = new HashMap<>();

        for (Transition<State> t : delta.keySet()
             ) {
            HashSet<State> workingStates = new HashSet<>();
            workingStates.add(t.getP());
            newDelta.put(new Transition<>(delta.get(t), t.getA()), workingStates);
        }


        return new NFA(getStates(), getAlphabet(), newStarts, newEnds, newDelta);

    }

    //public NFA(Set<State> _states, Set<Symbol> _alphabet, Set<State> starts, Set<State> _ends, Map<Transition<State>, Set<State>> delta)

    @Override
    public String toString() {
        return super.toString() +
                "s = " + start.toString() + "\n" +
                "ẟ = \n" + delta.toString().replaceAll("(\\{)|(\\})", "")
                .replace(", ", "\n")
                .replace("(", "   (");
    }


}
