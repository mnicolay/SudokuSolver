package sudoku;

import java.io.IOException;

import sat.SATSolver;
import sat.env.Environment;
import sat.formula.Formula;
import sudoku.Sudoku.ParseException;

public class Main {


    /*
     * Uncomment line(s) below to test your implementation! 
     */
    public static void main (String[] args) {
        timedSolve (new Sudoku(2));
        timedSolve (new Sudoku(2, new int[][] { 
                    new int[] { -1, 0, -1, 3 }, 
                    new int[] { -1, -1, -1, -1 }, 
                    new int[] { 1, -1, 2, -1 }, 
                    new int[] { -1, -1, -1, -1 }, 
        }));
        timedSolveFromFile(2, "samples/sudoku_4x4.txt");  
        timedSolveFromFile(3, "samples/sudoku_easy.txt");
        timedSolveFromFile(3, "samples/sudoku_easy2.txt");
        timedSolveFromFile(3, "samples/sudoku_hard.txt");  
        timedSolveFromFile(3, "samples/sudoku_hard2.txt");  
        timedSolveFromFile(3, "samples/sudoku_evil.txt");  
    }

    /**
     * Solve a puzzle and display the solution and the time it took.
     * @param sudoku
     */
    private static void timedSolve (Sudoku sudoku) {
        long started = System.nanoTime();

        System.out.println ("Creating SAT formula...");
        Formula f = sudoku.getProblem();
        
        long time = System.nanoTime();
        long timeTaken = (time - started);
        System.out.println ("Time:" + timeTaken/1000000 + "ms");
        
        
        started = System.nanoTime();
        
        
        System.out.println ("Solving...");
        Environment e = SATSolver.solve(f);
        
        System.out.println ("Interpreting solution...");
        Sudoku solution = sudoku.interpretSolution(e);
        
        System.out.println ("Solution is: \n" + solution);    

        time = System.nanoTime();
        timeTaken = (time - started);
        System.out.println ("Time:" + timeTaken/1000000 + "ms");
    }

    /**
     * Solve a puzzle loaded from a file and display the solution and the time it took.
     * @param dim  dimension of puzzle
     * @param filename  name of puzzle file to load
     */
    private static void timedSolveFromFile(int dim, String filename) {
        try {
        	System.out.println("About to solve " + filename);
            timedSolve (Sudoku.fromFile (dim, filename));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }        
    }
}
