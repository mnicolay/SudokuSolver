package sat;

import immutable.EmptyImList;
import immutable.ImList;
import sat.env.Bool;
import sat.env.Environment;
import sat.env.Variable;
import sat.formula.Clause;
import sat.formula.Formula;
import sat.formula.Literal;
import sat.formula.NegLiteral;
import sat.formula.PosLiteral;

/**
 * A simple DPLL SAT solver. See http://en.wikipedia.org/wiki/DPLL_algorithm
 */
public class SATSolver {
    /**
     * Solve the problem using a simple version of DPLL with backtracking and
     * unit propagation. The returned environment binds literals of class
     * bool.Variable rather than the special literals used in clausification of
     * class clausal.Literal, so that clients can more readily use it.
     * 
     * @return an environment for which the problem evaluates to Bool.TRUE, or
     *         null if no such environment exists.
     */
    public static Environment solve(Formula formula) {
    	return solve(formula.getClauses(), new Environment());
    }

    /**
     * Takes a partial assignment of variables to values, and recursively
     * searches for a complete satisfying assignment.
     * 
     * @param clauses
     *            formula in conjunctive normal form
     * @param env
     *            assignment of some or all variables in clauses to true or
     *            false values.
     * @return an environment for which all the clauses evaluate to Bool.TRUE,
     *         or null if no such environment exists.
     */
    private static Environment solve(ImList<Clause> clauses, Environment env) {
    	if(clauses.isEmpty()) return env;
    	Clause min = null;
    	for(Clause c : clauses){
    		if(c.isEmpty()) return null;
    		if (min == null || c.size() < min.size()) min = c;
    	}
    	Literal l = min.chooseLiteral();
    	Variable v = l.getVariable();
    	if (min.isUnit()) { // a unit clause was found, so propagate
    		env = env.put(v, l instanceof PosLiteral ? Bool.TRUE : Bool.FALSE);
    		return solve(reduceClauses (clauses,l), env); 
		} // else split
    		
    		
		if (l instanceof NegLiteral) 
			l = l.getNegation(); 
		Environment solvePos = solve (reduceClauses (clauses,l), env.put(v, Bool.TRUE));
		if (solvePos == null)
			return solve (reduceClauses (clauses,l.getNegation()), env.put(v, Bool.FALSE));
		else return solvePos;
    }
    
    /**
     * given a clause list and literal, produce a new list resulting from
     * setting that literal to true
     * 
     * @param clauses
     *            , a list of clauses
     * @param l
     *            , a literal to set to true
     * @return a new list of clauses resulting from setting l to true
     */
    private static ImList<Clause> reduceClauses(ImList<Clause> clauses, Literal l) {
    	ImList<Clause> reducedClauses = new EmptyImList<Clause>();
    	for (Clause c : clauses) {
	    	Clause r = c.reduce(l);
	    	if (r != null)
	    		reducedClauses = reducedClauses.add(r);
    	}
    	return reducedClauses;
	}

}
