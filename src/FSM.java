import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 * @author Clément Moreau
 */

// Finite State Automata Structure
public abstract class FSM {
	
	// Ensemble des états 
	private Set<State> states;
	public Set<State> getStates() { return states; }

	// Alphabet
	private Set<Symbol> alphabet;
	public Set<Symbol> getAlphabet() { return alphabet; }
	
	// Ensemble des états finaux
	private Set<State> ends;
	public Set<State> getEnds() { return ends; }

	
	// Constructeur par défaut
	public FSM(Set<State> _states, Set<Symbol> _alphabet, Set<State> _ends) {

		states = _states;
		alphabet = _alphabet;
		ends = _ends;
	}

	// Constructeur par fichier
	public FSM(String path) {

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
        
        // typecasting obj to JSONObject
        JSONObject jo = (JSONObject) obj;
          
        JSONArray ja = (JSONArray) jo.get("states");
        states = new HashSet<State>();
        for(Object o : ja) {
        	states.add(new State((String) o));
        }
        
        ja = (JSONArray) jo.get("alphabet");
        alphabet = new HashSet<Symbol>();
        for(Object o : ja) {
        	alphabet.add(new Symbol((String) o));
        }
        
        ja = (JSONArray) jo.get("ends");
        ends = new HashSet<State>();
        for(Object o : ja) {
        	ends.add(new State((String) o));
        }
        
        if(! states.containsAll(ends)) {
        	System.err.println("États finaux non contenus dans l'ensemble des états");
        }

	}

	@Override
	public String toString() {
		return "Q = " + states.toString() +
				"\n∑ = " + alphabet.toString() +
				"\nF = " + ends.toString()
				+ "\n";
	}

}
