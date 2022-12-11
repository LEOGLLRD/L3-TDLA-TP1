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

    //Retourne l'état initial
    public State getStart() {
        return start;
    }

    // Fonction de Transititon
    private Map<Transition<State>, State> delta;

    //Retourne la fonction de transition
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

        //On récupère l'état initial
        State current = this.start;

        //Pour chaque symbole
        for (char a : x.toCharArray()) {

            //On créer une transition prenant l'état courant et le symbole courant
            Transition t = new Transition(current, new Symbol("" + a));
            //Puis on met current à l'état du résultat de t via la fonction de transition
            current = delta.get(t);

        }

        //Enfin on vérifie si l'état obtenu est contenu dans les états finaux
        return this.getEnds().contains(current);

    }

    //Donne la transposée d'un DFA. Cela devient un NFA, car on peut avoir plusieurs état de début
    public NFA tranpose(){

        //Etats initiaux du NFA
        //Les états finaux deviennent les états initiaux
        Set<State> newStarts = this.getEnds();
        //Etats finaux du NFA
        HashSet<State> newEnds = new HashSet<>();
        //Les états initiaux deviennent les états finaux
        newEnds.add(getStart());

        //Fonction de transition
        HashMap<Transition<State>, Set<State>> newDelta = new HashMap<>();

        //Pour chaque Transition de la fonction de transition
        for (Transition<State> t : delta.keySet()
             ) {
            //On créer un ensemble d'états
            HashSet<State> workingStates = new HashSet<>();
            //Auquel on ajoute l'état de la transition courante
            workingStates.add(t.getP());
            //Puis on "inverse" les états dans la nouvelle fonction de transition
            newDelta.put(new Transition<>(delta.get(t), t.getA()), workingStates);
        }


        //Enfin on retourne le NFA
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
