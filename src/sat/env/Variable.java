/**
 * Author: dnj
 * Date: Mar 5, 2008, 8:15:32 PM
 * 6.005 Elements of Software Construction
 * (c) 2008, MIT and Daniel Jackson
 */
package sat.env;


/**
 * A Variable is a logical propositional variable
 * This datatype is immutable.
 */
public class Variable {
    /*
     * Rep invariant
     *     name != null
     */
    private final String name;

    public Variable (String name) {
        this.name = name;
    }

    public Bool eval (Environment e) {
        return e.get(this);
    }

    public String toString () {
        return name;
    }
    public String getName () {
        return name;
    }
    
    public boolean isImplies(){
    	if(name.contains("=>"))
    		return true;
    	return false;
    }
    
    public boolean isNegation(){
    	if(name.startsWith("~"))
    		return true;
    	return false;
    }
    
    /**
     * 
     * @param v Variable that is on the right side of "implies"
     * @return a new Variable whose name is this + " => " + v
     */
    public Variable implies(Variable v){
    	return new Variable(name + " => " + v.getName());
    }
    
    /**
     * 
     * @return a new Variable whose name is "~" + the name of this
     */
    public Variable not(){
    	return new Variable("~" + name);
    }
    
    /**
     * @return true iff this and o represent the same literal
     * (that is, they have the same string name)
     */
    public boolean equals (Object o) {
        if (o == this) return true;
        if (!(o instanceof Variable)) return false;
        Variable v = (Variable) o;
        return v.name.equals(name); 
    }
}
