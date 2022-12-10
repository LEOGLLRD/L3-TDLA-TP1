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

    public NFA(Set<State> _states, Set<Symbol> _alphabet, Set<State> starts, Set<State> _ends, Map<Transition<State>, Set<State>> delta) {
        super(_states, _alphabet, _ends);
        this.starts = starts;
        this.delta = delta;
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


        //On récupère les états initiaux
        HashSet<State> currentStates = new HashSet<>(this.getStarts());

        for (char a : s.toCharArray()
             ) {

            HashSet<State> workingStates = new HashSet<State>();


            for (State state : currentStates
                 ) {
                //Pour chaque état, on crée une transition prenant l'état et le symbole
                Transition<State> t = new Transition<State>(state, new Symbol("" + a));

                if(delta.containsKey(t)) {
                    workingStates.addAll(delta.get(t));
                }else continue;
            }

            //Après avoir récupérer tout les états résultats pour les états précédents,
            //on applique à currentStates les nouveaux états et on passe au prochain symbole
            currentStates = workingStates;
        }

        //Maintenant on vérifie si un ou plusieurs des états résultats sont
        //contenus dans les états finaux
        //Si au moins un état résultats est contenu dans les états finaux,
        //on retourne vrai, sinon faux

        currentStates.retainAll(getEnds());
        return getEnds().contains(currentStates) || getEnds().equals(currentStates);

    }

    public Set<State> epsilonClause(Set<State> A) {
        return A;

    }

    //Converti un automate AFNDe en automate DFA
    public DFA toDFA() {

        //Associe un ensemble d'états, à un nouvel état
        HashMap<Set<State>, State> map = new HashMap<>();

        //Le delta de l'automate DFA
        HashMap<Transition<State>, State> newDelta = new HashMap<>();

        //Variable tampon / tamporaire
        HashSet<State> workingStates = new HashSet<>();

        //Ensemble de sous ensemble nécessaire afin d'éviter une erreur lors d'insertion dans statesToDo
        //(Pour éviter des erreurs du type ConcurrentModificationException dû à la tentative d'insertion dans un ensemble pendant qu'on le traverse)
        HashSet<Set<State>> temporaryStatesToDo = new HashSet<>();

        //Ensemble des états sur lesquels on travail
        //ce sont les ensembles colonne pi
        HashSet<Set<State>> statesToDo = new HashSet<>();

        //On y ajoute les epsilon clotures des états de début
        statesToDo.add(epsilonClause(getStarts()));

        //On associe ces états à un nouvel état q1
        map.put(epsilonClause(getStarts()), new State("q1"));


        //Compteur
        int i = 2;

        do {

            //On récupère tout les ensembles d'états qu'il nous reste à traiter
            //Pour chaque ensemble d'états
            statesToDo.addAll(temporaryStatesToDo);

            //Iterateur de l'ensemble statesToDo
            Iterator it = statesToDo.iterator();
            //Temps que statesToDo à des éléments à parcourir
            while (it.hasNext()
            ) {

                //On récupère un sous ensemble de statesToDo
                Set<State> states = (Set<State>) it.next();

                //Pour chaque Symbol
                for (Symbol a : getAlphabet()
                ) {

                    //On créer un ensemble qui va récupérer les états
                    workingStates = null;
                    workingStates = new HashSet<>();

                    //Pour chaque état
                    for (State s : states
                    ) {


                        //On créer un nouvel ensemble d'états tamporaire
                        HashSet<State> tampo = new HashSet<>();
                        //On y ajoute l'état courant
                        tampo.add(s);
                        //On créer une Transition de l'ensemble tamporaire et du symbole courant
                        Transition<Set<State>> t = new Transition<>(tampo, a);
                        //Et on ajoute à workingStates l'epsilon Cloture du résultat de la transition t
                        workingStates.addAll(epsilonClause(applyDeltaTilde(t)));


                    }

                    //Si statesToDo ne contient pas les états obtenus dans workingStates
                    if (!statesToDo.contains(workingStates)) {
                        //On les ajoutes à temporaryStatesToDo
                        //On doit passer via
                        temporaryStatesToDo.add(workingStates);
                    }


                    //Si workingStates est vide, cela veut dire
                    //que null doit être associé à un état "vide"
                    if (workingStates.isEmpty()) {


                        map.put(workingStates, new State("empty"));

                    }

                    //Si map ne contient pas le sous ensemble workingStates
                    else if (!map.containsKey(workingStates)) {

                        //On l'ajoute en tant que clé de la valeur qi
                        //Ici on associe un ensemble d'états à un nouvel état
                        map.put(workingStates, new State("q" + i));
                        i++;


                    }


                    //Enfin on créer une transition du type : (l'état associé aux états obtenus avec workingStates, le symbole courant)
                    //Et on l'associe à l'état lié aux états obtenus avec workingStates
                    if (map.containsKey(workingStates)) {
                        newDelta.put(new Transition<State>(map.get(states), a), map.get(workingStates));
                    } else if (workingStates.isEmpty()) {
                        newDelta.put(new Transition<State>(map.get(states), a), new State("empty"));

                    } else newDelta.put(new Transition<State>(map.get(states), a), new State("empty"));

                }


            }


        } while (!statesToDo.containsAll(temporaryStatesToDo));
        //Temps que stateToDo ne contient pas tout les sous ensembles de tamporaryStatesToDo

        //Ensemble des états du DFA
        HashSet<State> statesDFA = new HashSet<>();

        //Ensemble des états finaux du DFA
        HashSet<State> endsDFA = new HashSet<>();

        //Etat d'origine du DFA
        State startDFA = map.get(epsilonClause(getStarts()));

        System.out.println("map : " + map);


        for (Set<State> s : map.keySet()
        ) {
            State getS = map.get(s);

            statesDFA.add(getS);

            Set<State> var = s;
            var.retainAll(getEnds());

            if (getEnds().containsAll(var)) {
                endsDFA.add(getS);
            }

        }

        return new DFA(statesDFA, getAlphabet(), startDFA, endsDFA, newDelta);

    }

    public NFA transpose() {

        //On inverse les starts et les ends
        Set<State> newEnds = getStarts();
        Set<State> newStarts = getEnds();

        //Onn créer la hashmap qui va contenir la nouvelle fonction delta
        HashMap<Transition<State>, Set<State>> newDelta = new HashMap<>();

        HashSet<State> statesDone = new HashSet<>();


        //Pour chaque clé de this.delta (une transition)
        for (Transition<State> transition : delta.keySet()
        ) {

            //On créer un l'ensemble d'états que donne cette transition dans l'automate
            Set<State> states = delta.get(transition);

            //Pour chaque état
            for (State s : states
            ) {

                if (newDelta.containsKey(new Transition<>(s, transition.getA()))) {


                    HashSet<State> tampo2 = (HashSet<State>) newDelta.get(new Transition<>(s, transition.getA()));
                    tampo2.add(transition.getP());
                    newDelta.put(new Transition<>(s, transition.getA()), tampo2);

                } else {

                    //On l'ajoute dans un ensemble
                    HashSet<State> tampo = new HashSet<>();
                    tampo.add(transition.getP());

                    //Enfin on inverse l'état source avec l'état résultat
                    newDelta.put(new Transition<State>(s, transition.getA()), tampo);

                }

                statesDone.add(s);

            }

        }


        return new NFA(this.getStates(), getAlphabet(), newStarts, newEnds, newDelta);
    }

    //Minimise un automate en utilisant l'algorithme de Brzozowski
    public DFA minimize(){
        return this.transpose().toDFA().tranpose().toDFA();
    }

    @Override
    public String toString() {

        return super.toString() + "S = " + starts.toString() + "\n" + "ẟ = \n" + delta.toString().replaceAll("(\\{)|(\\})", "").replace(", ", "\n").replace("(", "   (");
    }

}
