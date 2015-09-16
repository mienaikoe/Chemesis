package io.tangent.chemesis.models;

import android.util.Log;

import org.apache.commons.math3.fraction.Fraction;
import org.apache.commons.math3.fraction.FractionField;
import org.apache.commons.math3.linear.FieldLUDecomposition;
import org.apache.commons.math3.linear.FieldMatrix;
import org.apache.commons.math3.linear.FieldVector;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;

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

    public void balance(Reaction reaction){
        FieldMatrix<Fraction> matrixA = this.matrixA(reaction);
        matrixA = this.reducedRowEchelonForm(matrixA);

        int ix=0;
        for( ReactionChemical rc : reaction.getReactants() ){
            Log.i("TAG",rc.getChemical().getName());
            Log.i("TAG",String.valueOf(ix));
            rc.setParts(-1 * matrixA.getEntry(ix, matrixA.getColumnDimension()-1).intValue());
            ix++;
        }
        for( ReactionChemical rc : reaction.getProducts() ){
            rc.setParts(matrixA.getEntry(ix, matrixA.getColumnDimension()-1).intValue());
            ix++;
        }
    }



    private FieldMatrix<Fraction> matrixA(Reaction reaction){
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

        FieldMatrix<Fraction> a = MatrixUtils.createFieldMatrix( FractionField.getInstance(), m, n);
        int col = 0;
        List<Element> orderedElements = new ArrayList<Element>(elements);
        for( ReactionChemical reactionChemical : reaction.getReactants() ) {
            HashMap<Element, Integer> composition = reactionChemical.getChemical().getComposition();
            for (Map.Entry<Element, Integer> compEntry : composition.entrySet()) {
                int row = orderedElements.indexOf(compEntry.getKey());
                a.setEntry(row, col, new Fraction(compEntry.getValue()));
            }
            col++;
        }
        for( ReactionChemical reactionChemical : reaction.getProducts() ){
            HashMap<Element, Integer> composition = reactionChemical.getChemical().getComposition();
            for( Map.Entry<Element, Integer> compEntry : composition.entrySet() ){
                int row = orderedElements.indexOf(compEntry.getKey());
                a.setEntry(row, col, new Fraction(compEntry.getValue()));
            }
            col++;
        }

        Log.i("A", matrixToString(a));

        return a;
    }

    private FieldMatrix<Fraction> reducedRowEchelonForm(FieldMatrix<Fraction> matrix ){
        int i=0, j=0;

        /*
        1. If aij = 0 swap the i-th row with some other row below to guarantee that aij != 0.
        The non-zero entry in the (i, j)-position is called a pivot. If all entries in the column
        are zero, increase j by 1.
        */
        while( i < matrix.getRowDimension() && j < matrix.getColumnDimension() ){
            Fraction aij = matrix.getEntry(i, j);
            if( aij.intValue() == 0 ){
                int search_i = i+1;
                int search_aij;
                boolean found = false;
                while( search_i < matrix.getRowDimension() ){
                    search_aij = matrix.getEntry(search_i, j).intValue();
                    if( search_aij != 0 ){
                        Fraction[] baseRow = matrix.getRow(i);
                        Fraction[] swapRow = matrix.getRow(search_i);
                        matrix.setRow(i, swapRow);
                        matrix.setRow(search_i, baseRow);
                        break;
                    }
                }
                if( !found ){
                    j++;
                    continue;
                }
            }

            /*
            2. Divide the i-th row by aij to make the pivot entry = 1.
            */
            FieldVector<Fraction> divRow = matrix.getRowVector(i).mapDivide(aij);
            matrix.setRowVector(i, divRow);

            /*
            3. Eliminate all other entries in the j-th column by subtracting suitable multiples of the
            i-th row from the other rows.
            */
            for( int elim_row = 0; elim_row < matrix.getRowDimension(); elim_row++ ){
                if( elim_row == i ){
                    continue;
                }
                Fraction elimValue = matrix.getEntry(elim_row, j);
                if( elimValue.getNumerator() == 0 ){
                    continue;
                }
                for( int elim_col = 0; elim_col < matrix.getColumnDimension(); elim_col++ ){
                    Fraction entry = matrix.getEntry(elim_row, elim_col);
                    Fraction entry_i = matrix.getEntry(i, elim_col);
                    matrix.setEntry(elim_row, elim_col, entry.subtract(entry_i.multiply(elimValue)));
                }
            }

            /*
            4. Increase i by 1 and j by 1 to choose the new pivot element. Return to Step 1.
            */
            i++;
            j++;
        }

        Log.i("rref(A)", matrixToString(matrix));

        // add rows to make square
        if( matrix.getColumnDimension() > matrix.getRowDimension() ){
            FieldMatrix<Fraction> squareMatrix = MatrixUtils.createFieldMatrix( FractionField.getInstance(), matrix.getColumnDimension(), matrix.getColumnDimension() );
            squareMatrix.setSubMatrix(matrix.getData(), 0, 0);
            final int diff = matrix.getColumnDimension() - matrix.getRowDimension();
            for( int ix=1; ix <= diff; ix++ ){
                squareMatrix.setEntry(matrix.getRowDimension()-1+ix, matrix.getRowDimension()-1+ix, new Fraction(1));
            }
            matrix = squareMatrix;
        }


        Log.i("sq rref(A)", matrixToString(matrix));

        // invert it
        matrix = new FieldLUDecomposition<Fraction>(matrix).getSolver().getInverse();

        Log.i("sq inv rref(A)", matrixToString(matrix));

        // scale it
        int greatestDenom = 0;
        for( Fraction[] fj : matrix.getData() ){
            for( Fraction fi : fj ){
                if( greatestDenom < fi.getDenominator() ){
                    greatestDenom = fi.getDenominator();
                }
            }
        }
        matrix = matrix.scalarMultiply(new Fraction(greatestDenom));

        Log.i("scaled sq inv rref(A)", matrixToString(matrix));

        return matrix;
    }




    private static String matrixToString(FieldMatrix<Fraction> matrix){
        StringBuilder sb = new StringBuilder("\n");
        for( Fraction[] row : matrix.getData() ){
            for( Fraction el : row ){
                sb.append(el.toString() + " ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }




}
