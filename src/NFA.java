import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class NFA extends FSM {

    private Set<State> starts;

    public Set<State> getStarts() {
        return starts;
    }

    private Map<Transition<State>, Set<State>> delta;

    public Map<Transition<State>, Set<State>> getDelta() {
        return delta;
    }

    public NFA(Set<State> _states, Set<Symbol> _alphabet, Set<State> starts, Set<State> _ends) {
        super(_states, _alphabet, _ends);
    }

    public NFA(String path) {
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
        JSONArray js = (JSONArray) jo.get("starts");
        starts = new HashSet<State>();
        for (Object o : js) {
            starts.add(new State((String) o));
        }
        for (State s : starts) {
            System.err.print(this.getStates().contains(s) ? "" : "États initiaux " + s + " ∉ Q\n");
        }


        JSONArray ja = (JSONArray) jo.get("delta");

        delta = new HashMap<Transition<State>, Set<State>>();

        for (Object _o : ja) {

            JSONObject _jo = (JSONObject) _o;

            State q = new State((String) _jo.get("state"));
            System.err.print(this.getStates().contains(q) ? "" : "État " + q.toString() + " ⊄ Q\n");

            Symbol a = new Symbol((String) _jo.get("symbol"));
            System.err.print(this.getAlphabet().contains(a) ? "" : "Symbole " + a.toString() + " ∉ ∑\n");

            Transition<State> t = new Transition<State>(q, a);

            JSONArray ji = (JSONArray) _jo.get("images");

            HashSet<State> p = new HashSet<State>();
            for (Object y : ji) {
                State state = new State((String) y);
                p.add(state);

            }

            for (State s : p) {
                System.err.print(this.getStates().contains(s) ? "" : "Images " + s + " ⊄ Q\n");
            }


            delta.put(t, p);
        }

    }

    //Fonction retournant les états finaux
    public Set<State> applyDeltaTilde(Transition<Set<State>> t) {

        //Déclaration de l'ensemble de tout les états résultat
        HashSet<State> states = new HashSet<State>();

        //On itère pour chaque état de départ de la transition
        for (State q : t.getP()) {

            //Pour chaque état on récupère le résultat en fonction du symbole
            Transition<State> transition = new Transition<State>(q ,t.getA());

            //Puis on ajoute tout les états dans l'ensemble states
            states.addAll(this.delta.get(transition));

        }

        return states;
    }

    public boolean accept(String s){

        //Déclaration de l'ensemble de tout les états résultat
        HashSet<State> resultStates = new HashSet<State>();

        //On récupère les états initiaux
        HashSet<State> currentStates = new HashSet<>(this.getStarts());

        for (char a : s.toCharArray()
             ) {

            HashSet<State> workingStates = new HashSet<State>();

            for (State state : currentStates
                 ) {
                //Pour chaque état on créer une transition prenant l'état et le symbole
                Transition<State> t = new Transition<State>(state, new Symbol("" + a));
                workingStates.addAll(delta.get(t));
            }

            //Après avoir récupérer tout les états résultats pour les états précédents,
            //on applique à currentStates les nouveaux états et on passe au prochain symbole
            currentStates = workingStates;
        }

        //Maintenant on vérifie si un ou plusieurs des états résultats sont
        //contenus dans les états finaux



        //Si au moins un état résultats est contenu dans les états finaux,
        //on retourne vrai, sinon faux
        return currentStates.retainAll(getEnds());

    }

    @Override
    public String toString() {

        return super.toString() + "S = " + starts.toString() + "\n" + "ẟ = \n" + delta.toString().replaceAll("(\\{)|(\\})", "").replace(", ", "\n").replace("(", "   (");
    }

}
