import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.StreamSupport;

public class AFNDe extends FSM {

    //Fonction de transition
    private Map<Transition<State>, Set<State>> delta;

    //Retourne le fonction de transition
    public Map<Transition<State>, Set<State>> getDelta() {
        return delta;
    }

    //Ensemble des états initiaux
    private Set<State> starts;

    //Retourne les états initiaux
    public Set<State> getStarts() {
        return starts;
    }

    //Constructeur
    public AFNDe(Set<State> _states, Set<Symbol> _alphabet, Set<State> _ends, Set<State> _starts, Map<Transition<State>, Set<State>> _delta) {
        super(_states, _alphabet, _ends);
        this.starts = _starts;
        this.delta = _delta;
    }

    //Constructeur utilisant un fichier json
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

    //Retourne l'espilon Cloture d'un ensemble d'états
    public Set<State> epsilonClause(Set<State> A) {


        //Deux cas d'arrêts

        //Si A est vide
        if (A.isEmpty()) return new HashSet<>();
        //Si A contient tous les états de l'automate
        if (A.equals(getStates())) return getStates();

        HashSet<State> epsilons = new HashSet<>();

        HashSet<State> st = (HashSet<State>) A;

        HashSet<State> begin;

        do {

            begin = st;


            //On créer un itérateur sur l'ensemble st
            Iterator it = st.iterator();

            //Temps que l'on peut parcourir it
            while (it.hasNext()) {

                //On récupère l'état courant
                State state = (State) it.next();

                //On ajoute à l'epsilon cloture l'état courant
                epsilons.add(state);

                //Si la fonction de transition contient une transition associant
                //l'état courant à un nouvel état via le symbole e, on ajoute le nouvel état à epsilon
                if (delta.containsKey(new Transition<>(state, new Symbol("e")))) {

                    //Ajout ddu résultat de la transition
                    Set<State> stateGot = delta.get(new Transition<>(state, new Symbol("e")));

                    HashSet<State> correctedStates = new HashSet<>();

                    //Correction bug (Il arrive qu'on obtienne un état du type State("q2, q4"),
                    //Pour corriger cela, quand on a un état de ce type, on va le split en plusieurs états
                    for (State s : stateGot) {

                        //On split le nom par les espaces, et les virgules
                        String[] strings = s.toString().split("[, ]+");

                        //Si strings a une longueur de plus de 1 cela signifie qu'il y a plusieurs états,
                        //qui ont été considérés comme un seul ( State("q2, q4") devient State("q2"), et State("q4")
                        if (strings.length <= 1) {
                            //Si strings ne contient qu'un seul état
                            //On ajoute à epsilons le nouvel état
                            epsilons.add(s);
                            continue;
                        } else {
                            //Sinon pour chaque nom,
                            //On créer un nouvel état qu'on ajoute à correctedStates
                            for (String stateName : strings
                            ) {
                                correctedStates.add(new State(stateName));
                            }
                        }
                    }
                    //Et enfin on ajoute correctedStates à epsilons
                    epsilons.addAll(correctedStates);
                }
            }

            st = epsilons;

        } while (!(st.equals(getStates()) || st.isEmpty() || st.equals(begin)));

        return st;

    }


    //Retourne vrai si le mot x est accepté par l'automate, faux sinon
    public boolean accept(String x) {

        //On récupère les états initiaux
        HashSet<State> currentStates = new HashSet<>(getStarts());

        //Si x est vide on vérifie si dans les états initiaux il y a un état final
        if(x.length()==0){

            Set<State> inter = new HashSet<>();
            inter.addAll(getStarts());
            inter.retainAll(getEnds());

            return getEnds().containsAll(inter) || getEnds().equals(inter);

        }

        //Sinon
        currentStates.addAll(epsilonClause(currentStates));
        //Pour chaque symbole
        for (char a : x.toCharArray()
        ) {

            HashSet<State> workingStates = new HashSet<State>();
            //Pour chaque état courant
            for (State state : currentStates
            ) {

                HashSet<State> states = new HashSet<>();

                states.add(state);
                //On créer une transition avec l'ensemble et le caractère actuel
                Transition<Set<State>> t = new Transition<Set<State>>(states, new Symbol("" + a));

                //Enfin on applique la transition sur l'epsilon cloture de l'ensemble d'états
                //On verifie si une transition existe pour chaque couple (state, symbole)
                //Pour éviter les erreurs (get qui ne trouve pas de clé)
                //On récupère les états résultats de la fonction delta
                workingStates.addAll((HashSet<State>) applyDelta(t));

            }


            //Après avoir récupéré tous les états résultats pour les états précédents,
            //on applique à currentStates les nouveaux états et on passe au prochain symbole
            currentStates = workingStates;
        }

        //Maintenant on vérifie si un ou plusieurs des états résultats sont
        //contenus dans les états finaux


        //Si au moins un état résultat est contenu dans les états finaux,
        //on retourne vrai, sinon faux


        currentStates.retainAll(getEnds());

        //Si currentStates est vide cela veut dire que l'on a obtenu aucun état
        //avec les transitions. Donc le mot n'est pas accepté
        if(currentStates.isEmpty()) return false;

        currentStates.retainAll(getEnds());
        return getEnds().contains(currentStates) || getEnds().equals(currentStates);

    }


    //Retourne les états obtenus quand applique la transition t  (etat, couple)
    public Set<State> applyDelta(Transition<Set<State>> t) {


        Set<State> states = new HashSet<State>();

        //D'abord on vérifie si l'espilon cloture de t.getP(),
        //ne nous donne pas de nouveaux états

        if (t.getP().equals(epsilonClause(t.getP()))) {
            //Si oui, on applique la transition sur chaque état de base

            for (State state : t.getP()
            ) {
                //Pour chaque état on récupère le résultat en fonction du symbole
                Transition<State> transition = new Transition<State>(state, t.getA());

                //Puis on ajoute tout les états dans l'ensemble states
                if (delta.get(transition) != null) {
                    states.addAll(this.delta.get(transition));
                } else continue;

            }

            //Sinon
        } else {
            Set<State> all = t.getP();
            all.addAll(epsilonClause(t.getP()));

            //on applique la transition sur chaque état de base
            //et les nouveaux états de l'espilon cloture


            for (State state : all
            ) {
                //Pour chaque état on récupère le résultat en fonction du symbole
                Transition<State> tnew = new Transition<State>(state, t.getA());

                //Puis on ajoute tout les états dans l'ensemble states
                //On verifie si une transition existe pour chaque couple (state, symbole)
                //Pour éviter les erreurs (get qui ne trouve pas de clé)
                if (delta.containsKey(tnew)) {
                    states.addAll(this.delta.get(tnew));
                }

                //Si pas de transition qui existe, on passe au prochain état
                else continue;
            }


        }

        //On applique l'epsilon cloture
        states.addAll(epsilonClause(states));


        //Enfin on retourne tous les états qu'on a obtenu
        return states;

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
                        workingStates.addAll(epsilonClause(applyDelta(t)));


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


        //Pour chaque ensemble d'états de map
        for (Set<State> s : map.keySet()
        ) {
            //On récupère le nouvel état associé à chaque ensemble
            State getS = map.get(s);

            //On ajoute aux états du DFA, l'état associé à l'ensemble d'états
            statesDFA.add(getS);

            //Enfin on récupère l'intersection de l'ensemble d'états courant avec les états finaux
            Set<State> var = s;
            var.retainAll(getEnds());

            if (getEnds().containsAll(var)) {
                endsDFA.add(getS);
            }

        }
        //Puis on retourne le nouvel automate DFA
        return new DFA(statesDFA, getAlphabet(), startDFA, endsDFA, newDelta);

    }

    public AFNDe transpose() {


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

        return new AFNDe(this.getStates(), getAlphabet(), getStarts(), getEnds(), newDelta);

    }


    //Minimise un automate en utilisant l'algorithme de Brzozowski
    public DFA minimize() {

        return this.transpose().toDFA().tranpose().toDFA();

    }


    @Override
    public String toString() {

        return super.toString() + "S = " + starts.toString() + "\n" + "ẟ = \n" + delta.toString().replaceAll("(\\{)|(\\})", "").replace(", ", "\n").replace("(", "   (");
    }
}
