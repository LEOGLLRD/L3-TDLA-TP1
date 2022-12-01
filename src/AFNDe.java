import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AFNDe extends FSM {

    private Map<Transition<State>, Set<State>> delta;

    //Retourne les états finaux
    public Map<Transition<State>, Set<State>> getDelta() {
        return delta;
    }

    private Set<State> starts;

    //Retourne les états initiaux
    public Set<State> getStarts() {
        return starts;
    }

    public AFNDe(Set<State> _states, Set<Symbol> _alphabet, Set<State> _ends) {
        super(_states, _alphabet, _ends);
    }

    public AFNDe(String path) {
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

            System.err.print((this.getAlphabet().contains(a) || a.equals(new Symbol("e"))) ? "" : "Symbole " + a.toString() + " ∉ ∑\n");

            Transition<State> t = new Transition<State>(q, a);

            JSONArray ji = (JSONArray) _jo.get("images");

            HashSet<State> p = new HashSet<State>();
            for (Object y : ji) {
                State state = new State((String) y);
                p.add(state);

            }

            for (State s : p) {

                String[] strings = s.toString().split("[, ]+");

                if (strings.length > 1) {

                    for (String string : strings
                    ) {
                        State state = new State(string);
                        System.err.print(this.getStates().contains(state) ? "" : "Images " + state + " ⊄ Q\n");
                    }
                } else {
                    System.err.print(this.getStates().contains(s) ? "" : "Images " + s + " ⊄ Q\n");
                }
            }

            delta.put(t, p);
        }

    }

    public Set<State> epsilonClause(Set<State> A){

        HashSet<State> result = new HashSet<State>();

        for (State state : A
             ) {
            
        }
        
        
        return null;
    }

    @Override
    public String toString() {

        return super.toString() + "S = " + starts.toString() + "\n" + "ẟ = \n" + delta.toString().replaceAll("(\\{)|(\\})", "").replace(", ", "\n").replace("(", "   (");
    }
}
