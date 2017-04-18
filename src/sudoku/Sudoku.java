/**
 * Author: dnj, Hank Huang
 * Date: March 7, 2009
 * 6.005 Elements of Software Construction
 * (c) 2007-2009, MIT 6.005 Staff
 */
package sudoku;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import sat.env.Bool;
import sat.env.Environment;
import sat.env.Variable;
import sat.formula.Clause;
import sat.formula.Formula;
import sat.formula.NegLiteral;

/**
 * Sudoku is an immutable abstract datatype representing instances of Sudoku.
 * Each object is a partially completed Sudoku puzzle.
 */
public class Sudoku {
    // dimension: standard puzzle has dim 3
    private final int dim;
    // number of rows and columns: standard puzzle has size 9
    private final int size;
    // known values: square[i][j] represents the square in the ith row and jth
    // column,
    // contains -1 if the digit is not present, else i>=0 to represent the digit
    // i+1
    // (digits are indexed from 0 and not 1 so that we can take the number k
    // from square[i][j] and
    // use it to index into occupies[i][j][k])
    private final int[][] square;
    // occupies [i,j,k] means that kth symbol occupies entry in row i, column j
    private final Variable[][][] occupies;

    // Rep invariant
    private void checkRep() {
    	assert size == dim*dim;
    	for(int i = 0; i < size; i++){
    		assert square.length == square[i].length && square.length == size;
	    	for(int j = 0; j<size; j++){
				for(int k = 0; k < size; k++){
					assert occupies[i][j][k] != null;
				}
	    	}
    	}
    }

    /**
     * create an empty Sudoku puzzle of dimension dim.
     * 
     * @param dim
     *            size of one block of the puzzle. For example, new Sudoku(3)
     *            makes a standard Sudoku puzzle with a 9x9 grid.
     */
    public Sudoku(int dim) {
    	this.dim = dim;
    	size = dim*dim;
    	square = new int[size][size];
    	occupies = new Variable[size][size][size];
    	
    	for(int i = 0; i < size; i++){
    		for(int j = 0; j < size; j++){
    			square[i][j] = -1;
    			for(int k = 0; k < size; k++){
    				occupies[i][j][k] = new Variable("occupies(" + i + "," + j + "," + k + ")");
    			}
    		}
    	}
    	checkRep();
    }

    /**
     * create Sudoku puzzle
     * 
     * @param square
     *            digits or blanks of the Sudoku grid. square[i][j] represents
     *            the square in the ith row and jth column, contains 0 for a
     *            blank, else i to represent the digit i. So { { 0, 0, 0, 1 }, {
     *            2, 3, 0, 4 }, { 0, 0, 0, 3 }, { 4, 1, 0, 2 } } represents the
     *            dimension-2 Sudoku grid: 
     *            
     *            ...1 
     *            23.4 
     *            ...3
     *            41.2
     * 
     * @param dim
     *            dimension of puzzle Requires that dim*dim == square.length ==
     *            square[i].length for 0<=i<dim.
     */
    public Sudoku(int dim, int[][] square) {
    	assert square.length == dim*dim;
    	this.dim = dim;
    	size = dim*dim;
    	this.square = square;
    	occupies = new Variable[size][size][size];
    	for(int i = 0; i<size; i++){
    		for(int j = 0; j<size; j++){
    			for(int k = 0; k < size; k++){
    				occupies[i][j][k] = new Variable("occupies(" + i + "," + j + "," + k + ")");
    			}
    		}
    	}
    	checkRep();
    }

    /**
     * Reads in a file containing a Sudoku puzzle.
     * 
     * @param dim
     *            Dimension of puzzle. Requires: at most dim of 3, because
     *            otherwise need different file format
     * @param filename
     *            of file containing puzzle. The file should contain one line
     *            per row, with each square in the row represented by a digit,
     *            if known, and a period otherwise. With dimension dim, the file
     *            should contain dim*dim rows, and each row should contain
     *            dim*dim characters.
     * @return Sudoku object corresponding to file contents
     * @throws IOException
     *             if file reading encounters an error
     * @throws ParseException
     *             if file has error in its format
     */
    public static Sudoku fromFile(int dim, String filename) throws IOException,
            ParseException {
    	BufferedReader file = new BufferedReader(new FileReader(filename));
    	String line;
    	int size = dim*dim;
    	int[][] square = new int[size][size];
    	
    	int i = 0;
    	while((line = file.readLine()) != null) {
    		for(int j = 0; j < size; j++){
    			char c = line.charAt(j);
    			int k = -1;
    			if(c != '.')
    				k = Character.getNumericValue(c)-1;
    			square[i][j] = k;
    		}
    		
    		i++;
    	}
    	file.close();
    	return new Sudoku(dim, square);
    }

    /**
     * Exception used for signaling grammatical errors in Sudoku puzzle files
     */
    @SuppressWarnings("serial")
    public static class ParseException extends Exception {
        public ParseException(String msg) {
            super(msg);
        }
    }

    /**
     * Produce readable string representation of this Sukoku grid, e.g. for a 4
     * x 4 sudoku problem: 
     *   12.4 
     *   3412 
     *   2.43 
     *   4321
     * 
     * @return a string corresponding to this grid
     */
    public String toString() {
    	String ret = "";
    	
    	for(int i = 0; i<=square.length-1; i++){
    		for(int j = 0; j<square[i].length; j++){
    			int k = square[i][j];
    			if(k == -1)
    				ret += ".";
    			else
    				ret += k+1;
    		}
    		if(i != square.length-1)
    			ret += "\n";
    	}
    	checkRep();
    	return ret;
    }
    


    /**
     * @return a SAT problem corresponding to the puzzle, using variables with
     *         names of the form occupies(i,j,k) to indicate that the kth symbol
     *         occupies the entry in row i, column j
     */
    public Formula getProblem() {

    	Formula formula = new Formula();
    	
    	
    				
    	// each symbol appears exactly once in each ROW
    	for (int k = 0; k < size; k++)
    		for (int i = 0; i < size; i++) {
		    	Formula atMost = new Formula();
		    	Formula atLeast = new Formula(new Clause());
		    	for (int j = 0; j < size; j++) {
	    			atLeast = atLeast.or (new Formula(occupies[i][j][k]));
	    			for(int j2 = 0; j2 < size; j2++){
	    				if(j < j2)
	    					atMost = atMost.and (new Formula(new Clause(NegLiteral.make(occupies[i][j][k])).merge(
	    							new Clause(NegLiteral.make(occupies[i][j2][k])))));
	    				if(k != j2)
	    					atMost = atMost.and (new Formula(new Clause(NegLiteral.make(occupies[i][j][k])).merge(
	    							new Clause(NegLiteral.make(occupies[i][j][j2])))));
	    			}
		    	}
		    	formula = formula.and (atMost).and (atLeast);
    		}
    	
    	//each symbol appears exactly ones in each COLUMN
    	for (int k = 0; k < size; k++)
    		for (int i = 0; i < size; i++) {
		    	Formula atMost = new Formula();
		    	Formula atLeast = new Formula(new Clause());
		    	for (int j = 0; j < size; j++) {
	    			atLeast = atLeast.or (new Formula(occupies[j][i][k]));
	    			for(int j2 = 0; j2 < size; j2++){
	    				if(j < j2)
	    					atMost = atMost.and (new Formula(new Clause(NegLiteral.make(occupies[j][i][k])).merge(
	    							new Clause(NegLiteral.make(occupies[j2][i][k])))));
	    			}
				    	
		    	}
		    	formula = formula.and (atMost).and (atLeast);
    		}
    	
    	//each symbol appears exactly once in each BLOCK
    	for (int k = 0; k < size; k++)
    		for(int xSt = 0; xSt < dim; xSt++){
    			int xEnd = xSt * (dim) + (dim - 1);
    			for(int ySt = 0; ySt < dim; ySt++){
    		    	Formula atMost = new Formula();
    		    	Formula atLeast = new Formula(new Clause());
        			int yEnd = ySt * (dim) + (dim - 1);
    				for(int x = xSt * dim; x <= xEnd; x++){
    					for(int y = ySt * dim; y <= yEnd; y++){
    		    			atLeast = atLeast.or (new Formula(occupies[x][y][k]));
    						for(int x2 = xSt * dim; x2 <= xEnd; x2++)
    								for(int y2 = ySt * dim; y2 <= yEnd; y2++){
    									if ((x != x2 || y != y2) && ((x<x2) || ((x == x2) && (y <y2))))
    										atMost = atMost.and (new Formula(new Clause(NegLiteral.make(occupies[x][y][k])).merge(
    												new Clause(NegLiteral.make(occupies[x2][y2][k])))));
    								}
    					}
    				}
        			formula = formula.and (atMost).and (atLeast);
    			}
    		}
    	//add in unit clauses for the numbers that are present in the sudoku board already
    	for(int i = 0; i < size; i++)
    		for(int j = 0; j < size; j++)
    			if(square[i][j] != -1){
    				Variable v = occupies[i][j][square[i][j]];
    				formula = formula.and(new Formula(v));
    			}
    	
    	checkRep();

    	return formula;
    	
    }

    /**
     * Interpret the solved SAT problem as a filled-in grid.
     * 
     * @param e
     *            Assignment of variables to values that solves this puzzle.
     *            Requires that e came from a solution to this.getProblem().
     * @return a new Sudoku grid containing the solution to the puzzle, with no
     *         blank entries.
     */
    public Sudoku interpretSolution(Environment e) {
    	Sudoku ret =new Sudoku(dim);

    	if(e != null)
	    	for (int i = 0; i < size; i++)
	    		for(int j = 0; j < size; j++)
	    			for(int k = 0; k < size; k++){
	    				if(occupies[i][j][k].eval(e) == Bool.TRUE){
	    					ret.square[i][j] = k;
	    				}
	    			}
    	checkRep();
    	return ret;
    }
    	

}
