package io.tangent.chemesis.models;

import android.util.Log;

import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularMatrixException;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created by Jesse on 9/15/2015.
 */
public class ReactionBalancer {

    public ReactionBalancer(){
    }

    public void balance(Reaction reaction) throws IllegalStateException{
        RealMatrix matrix = this.matrixA(reaction);
        matrix = this.reducedRowEchelonForm(matrix);

        // add rows to make square
        if( matrix.getColumnDimension() > matrix.getRowDimension() ){
            RealMatrix squareMatrix = MatrixUtils.createRealMatrix(matrix.getColumnDimension(), matrix.getColumnDimension());
            squareMatrix.setSubMatrix(matrix.getData(), 0, 0);
            final int diff = matrix.getColumnDimension() - matrix.getRowDimension();
            for( int ix=1; ix <= diff; ix++ ){
                squareMatrix.setEntry(matrix.getRowDimension()-1+ix, matrix.getRowDimension()-1+ix, 1);
            }
            matrix = squareMatrix;
        }


        Log.i("sq rref(A)", matrixToString(matrix));

        // invert it
        try {
            matrix = new LUDecomposition(matrix).getSolver().getInverse();
        } catch (SingularMatrixException ex){
            throw new IllegalStateException("This equation cannot be balanced");
        }

        Log.i("sq inv rref(A)", matrixToString(matrix));

        // scale it
        double smallestVal = 1;
        for( double[] fj : matrix.getData() ){
            for( double fi : fj ){
                if( fi < smallestVal ){
                    smallestVal = fi;
                }
            }
        }
        matrix = matrix.scalarMultiply(smallestVal);

        Log.i("scaled sq inv rref(A)", matrixToString(matrix));

        int ix=0;
        for( ReactionChemical rc : reaction.getReactants() ){
            Log.i("TAG",rc.getChemical().getName());
            Log.i("TAG",String.valueOf(ix));
            rc.setParts((int)Math.round(-1 * matrix.getEntry(ix, matrix.getColumnDimension() - 1)));
            ix++;
        }
        for( ReactionChemical rc : reaction.getProducts() ){
            rc.setParts((int)Math.round(matrix.getEntry(ix, matrix.getColumnDimension()-1)));
            ix++;
        }
    }



    // http://arxiv.org/ftp/arxiv/papers/1110/1110.4321.pdf
    public void balanceThorne(Reaction reaction) throws IllegalStateException{
        /*(a) Construct a chemical-composition matrix for the
        chemical-reaction equation.*/
        RealMatrix matrix = this.matrixA(reaction);
        Log.i("(a)", matrixToString(matrix));

        /*(b) Determine the nullity, or dimensionality, of the matrix
        null space of the chemical- composition matrix.*/
        int rank = new SingularValueDecomposition(matrix).getRank();
        int nullity = matrix.getColumnDimension() - rank;
        Log.i("(b)", "Nullity: " + String.valueOf(nullity));

        /*(c) Augment the matrix with a number of rows equal to the
        nullity number.*/
        RealMatrix augmentedMatrix = MatrixUtils.createRealMatrix(matrix.getRowDimension() + nullity, matrix.getColumnDimension());
        augmentedMatrix.setSubMatrix(matrix.getData(), 0, 0);
        int maxRows = matrix.getRowDimension() + nullity;
        for( int ix = matrix.getRowDimension(); ix < maxRows; ix++ ){
            augmentedMatrix.setEntry(ix, ix, 1);
        }
        Log.i("(c)", matrixToString(augmentedMatrix));

        /*(d) Compute the matrix inverse of the augmented matrix by
        using the built-in functions of a scientific calculator or
        computer spreadsheet program.*/
        RealMatrix matrixInverse = new LUDecomposition(augmentedMatrix).getSolver().getInverse();
        Log.i("(d)", matrixToString(matrixInverse));

        /*(e) Extract the null-space basis vectors from the inverted
        matrix. (The vectors will be the columns at the far right of
        the inverted matrix; the number of columns included—0,
                1 or more—should equal the nullity of the row-echelon
        matrix.) This defines the null space of the original
        chemical-composition matrix!*/
        int nsvIndex = matrixInverse.getColumnDimension()-1;
        RealVector nullSpaceVector = matrixInverse.getColumnVector(nsvIndex);
        Log.i("(e)", nullSpaceVector.toString());

        /*(f) Take the transpose of the null space vectors. (Each
        transposed null-space vector is proportional to a set of
        coefficients which will ultimately balance the chemicalreaction
        equation.)*/
        // Not Needed because Vector is implemented in both ways

        /*(g) Make the smallest number in each transposed vector equal
        to 1 by dividing each vector element by the element of the
        smallest magnitude. (These scaled vectors are the
        coefficients that balance the equation, term by term, left to
        right.)*/
        double minValue = 1;
        for( double val : nullSpaceVector.toArray() ){
            if( Math.abs(val) < minValue && val != 0 ){
                minValue = Math.abs(val);
            }
        }
        nullSpaceVector.mapDivideToSelf(minValue);
        Log.i("(g)", nullSpaceVector.toString());

        /*(h) Construct a new chemical-reaction equation by placing
        positive terms on one side, negative terms on the other.
        The equation is now balanced!*/
        int ix=0;
        for( ReactionChemical rc : reaction.getReactants() ){
            Log.i("TAG",rc.getChemical().getName());
            Log.i("TAG", String.valueOf(ix));
            rc.setParts((int)Math.round(-1 * nullSpaceVector.getEntry(ix)));
            ix++;
        }
        for( ReactionChemical rc : reaction.getProducts() ){
            rc.setParts((int)Math.round(nullSpaceVector.getEntry(ix)));
            ix++;
        }
    }




    private RealMatrix matrixA(Reaction reaction) throws IllegalStateException{
        // compile a list of all involved elements
        int n = 0;
        Set<Element> reactantElements = new HashSet<Element>();
        for( ReactionChemical reactionChemical : reaction.getReactants() ){
            reactantElements.addAll( reactionChemical.getChemical().getComposition().keySet() );
            n++;
        }
        Set<Element> productElements = new HashSet<Element>();
        for( ReactionChemical reactionChemical : reaction.getProducts() ){
            productElements.addAll( reactionChemical.getChemical().getComposition().keySet() );
            n++;
        }

        if( !reactantElements.equals(productElements) ){
            throw new IllegalStateException("Equation cannot be balanced. Please make sure all elements are on both sides");
        }

        Set<Element> elements = new HashSet<Element>(reactantElements.size() + productElements.size());
        elements.addAll(reactantElements);
        elements.addAll(productElements);

        // populate Matrix A, a matrix for the elemental contributions from each chemical
        int m = elements.size();
        if( n - m < 0 ){ // More Elements than Chems. Overdetermined Linear System. Cannot Solve
            throw new IllegalStateException("Equation cannot be solved");
        }

        RealMatrix a = MatrixUtils.createRealMatrix(m, n);
        int col = 0;
        List<Element> orderedElements = new ArrayList<Element>(elements);
        for( ReactionChemical reactionChemical : reaction.getReactants() ) {
            HashMap<Element, Integer> composition = reactionChemical.getChemical().getComposition();
            for (Map.Entry<Element, Integer> compEntry : composition.entrySet()) {
                int row = orderedElements.indexOf(compEntry.getKey());
                a.setEntry(row, col, compEntry.getValue());
            }
            col++;
        }
        for( ReactionChemical reactionChemical : reaction.getProducts() ){
            HashMap<Element, Integer> composition = reactionChemical.getChemical().getComposition();
            for( Map.Entry<Element, Integer> compEntry : composition.entrySet() ){
                int row = orderedElements.indexOf(compEntry.getKey());
                a.setEntry(row, col, compEntry.getValue());
            }
            col++;
        }

        Log.i("A", matrixToString(a));

        return a;
    }

    private RealMatrix reducedRowEchelonForm(RealMatrix matrix ) throws IllegalStateException{
        int i=0, j=0;

        /*
        1. If aij = 0 swap the i-th row with some other row below to guarantee that aij != 0.
        The non-zero entry in the (i, j)-position is called a pivot. If all entries in the column
        are zero, increase j by 1.
        */
        while( i < matrix.getRowDimension() && j < matrix.getColumnDimension() ){
            boolean found = false;
            Double aij = null;
            while( !found ) {
                aij = matrix.getEntry(i, j);
                if (aij.intValue() == 0) {
                    int search_i = i + 1;
                    double search_aij;
                    while (search_i < matrix.getRowDimension()) {
                        search_aij = matrix.getEntry(search_i, j);
                        if (search_aij != 0) {
                            double[] baseRow = matrix.getRow(i);
                            double[] swapRow = matrix.getRow(search_i);
                            matrix.setRow(i, swapRow);
                            matrix.setRow(search_i, baseRow);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        j++;
                    }
                } else {
                    found = true;
                }
            }

            /*
            2. Divide the i-th row by aij to make the pivot entry = 1.
            */
            RealVector divRow = matrix.getRowVector(i).mapDivide(aij);
            matrix.setRowVector(i, divRow);

            /*
            3. Eliminate all other entries in the j-th column by subtracting suitable multiples of the
            i-th row from the other rows.
            */
            for( int elim_row = 0; elim_row < matrix.getRowDimension(); elim_row++ ){
                if( elim_row == i ){
                    continue;
                }
                double elimValue = matrix.getEntry(elim_row, j);
                if( elimValue == 0 ){
                    continue;
                }
                for( int elim_col = 0; elim_col < matrix.getColumnDimension(); elim_col++ ){
                    double entry = matrix.getEntry(elim_row, elim_col);
                    double entry_i = matrix.getEntry(i, elim_col);
                    matrix.setEntry(elim_row, elim_col, entry - (entry_i * elimValue));
                }
            }

            /*
            4. Increase i by 1 and j by 1 to choose the new pivot element. Return to Step 1.
            */
            i++;
            j++;
        }

        //Log.i("rref(A)", matrixToString(matrix));

        return matrix;
    }




    private static String matrixToString(RealMatrix matrix){
        StringBuilder sb = new StringBuilder("\n\n");
        for( double[] row : matrix.getData() ){
            for( double el : row ){
                String base = String.valueOf(el);
                if( base.length() > 5 ) {
                    base = base.substring(0, 5);
                } else {
                    while( base.length() < 5 ) {
                        base += " ";
                    }
                }
                sb.append(  base  + " ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }




}
