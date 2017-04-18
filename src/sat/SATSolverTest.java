package sat;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import sat.env.Environment;
import sat.formula.Literal;
import sat.formula.PosLiteral;
import sudoku.Sudoku;
import sudoku.Sudoku.ParseException;

public class SATSolverTest {
    Literal a = PosLiteral.make("a");
    Literal b = PosLiteral.make("b");
    Literal c = PosLiteral.make("c");
    Literal na = a.getNegation();
    Literal nb = b.getNegation();
    Literal nc = c.getNegation();

    // make sure assertions are turned on!  
    // we don't want to run test cases without assertions too.
    // see the handout to find out how to turn them on.
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false;
    }

    // TODO: put your test cases here
    
    @Test
    public void testSolve(){
    	int dim = 1;
    	Sudoku s = new Sudoku(dim);
    	assertEquals(s.toString(), ".");
    	
    	Environment e = SATSolver.solve(s.getProblem());
    	
    	assertEquals(
    			"1",s.interpretSolution(e).toString());
    	dim = 2;
    	int[][] square = new int[][] {{-1, 1, 2, 3}, {2, 3, 0, -1}, {1, 0, 3, -1}, {-1, 2, 1, 0}};
    	s = new Sudoku(dim,square);
    	assertEquals(s.toString(),
    			".234\n"
    			+ "341.\n"
    			+ "214.\n"
    			+ ".321");
    	
    	square = new int[][] {{0, -1, -1, -1}, {-1, 1, -1, -1}, {-1, -1, 2, -1}, {-1, -1, -1, 3}};
    	s = new Sudoku(dim, square);
    	assertEquals(s.toString(),
    			"1...\n"
    			+ ".2..\n"
    			+ "..3.\n"
    			+ "...4");
    	
    	dim = 3;
    	
    	try {
			s = Sudoku.fromFile(dim, "C:\\Users\\mattn_000\\Documents\\DePaul_University\\SE 350\\workspace\\hw-04\\samples\\sudoku_easy.txt");
		} catch (IOException exc) {
			exc.printStackTrace();
			fail();
		} catch (ParseException exc) {
			exc.printStackTrace();
			fail();
		}
    	
    	e = SATSolver.solve(s.getProblem());
    	
    	assertEquals(
    			"278145693\n"
    			+ "354698712\n"
    			+ "916273485\n"
    			+ "692817354\n"
    			+ "837564129\n"
    			+ "145329876\n"
    			+ "423751968\n"
    			+ "581936247\n"
    			+ "769482531",
    			s.interpretSolution(e).toString());
    	
    	
    	try {
			s = Sudoku.fromFile(dim, "C:\\Users\\mattn_000\\Documents\\DePaul_University\\SE 350\\workspace\\hw-04\\samples\\sudoku_hard.txt");
		} catch (IOException exc) {
			exc.printStackTrace();
			fail();
		} catch (ParseException exc) {
			exc.printStackTrace();
			fail();
		}
    	
    	assertEquals("",s.getProblem());
    	
    }

    
}