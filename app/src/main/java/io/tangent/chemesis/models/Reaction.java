package io.tangent.chemesis.models;

import android.util.Log;


import org.apache.commons.math3.fraction.Fraction;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created by Jesse on 9/7/2015.
 */
public class Reaction {

    private final List<ReactionChemical> reactants;
    private final List<ReactionChemical> products;

    public Reaction(){
        this.reactants = new ArrayList<ReactionChemical>();
        this.products = new ArrayList<ReactionChemical>();
    }

    public List<ReactionChemical> getProducts() {
        return products;
    }

    public List<ReactionChemical> getReactants() {
        return reactants;
    }

    public void addReactant(Chemical chemical){
        this.reactants.add(new ReactionChemical(chemical, this));
    }

    public void removeReactant(Chemical chemical){
        for( ReactionChemical rc : this.reactants ) {
            if (rc.getChemical() == chemical) {
                this.reactants.remove(rc);
            }
        }
    }

    public void addProduct(Chemical chemical){
        this.products.add(new ReactionChemical(chemical, this));
    }

    public void removeProduct(Chemical chemical){
        for( ReactionChemical rc : this.products ) {
            if (rc.getChemical() == chemical) {
                this.products.remove(rc);
            }
        }
    }




    public void balance(){

        // compile a list of all involved elements
        int n = 0;
        Set<Element> elements = new HashSet<Element>();
        for( ReactionChemical reactionChemical : this.reactants ){
            elements.addAll( reactionChemical.getChemical().getComposition().keySet() );
            n++;
        }
        for( ReactionChemical reactionChemical : this.products ){
            elements.addAll( reactionChemical.getChemical().getComposition().keySet() );
            n++;
        }

        // populate Matrix A, a matrix for the elemental contributions from each chemical
        int m = elements.size();
        int diff = n - m;
        if( diff < 0 ){ // less chems than species. indeterminate
            throw new IllegalStateException("Equation is indeterminate - cannot balance");
        }

        RealMatrix a = MatrixUtils.createRealMatrix(m + diff, n);
        int col = 0;
        List<Element> orderedElements = new ArrayList<Element>(elements);
        for( ReactionChemical reactionChemical : this.reactants ) {
            HashMap<Element, Integer> composition = reactionChemical.getChemical().getComposition();
            for (Map.Entry<Element, Integer> compEntry : composition.entrySet()) {
                int row = orderedElements.indexOf(compEntry.getKey());
                a.setEntry(row, col, compEntry.getValue());
            }
            col++;
        }
        for( ReactionChemical reactionChemical : this.products ){
            HashMap<Element, Integer> composition = reactionChemical.getChemical().getComposition();
            for( Map.Entry<Element, Integer> compEntry : composition.entrySet() ){
                int row = orderedElements.indexOf(compEntry.getKey());
                a.setEntry(row, col, -1 * compEntry.getValue());
            }
            col++;
        }
        if( diff > 0 ){
            for( int ix = 1 ; ix <= diff; ix++ ){
                int varIdx = m + ix - 1;
                a.setEntry(varIdx, varIdx, 1);
            }
        }
        Log.i("A", Reaction.matrixToString(a));

        // set up B so that wide equations have unique solutions
        RealMatrix b = MatrixUtils.createRealMatrix(m + diff, 1);
        if( diff > 0 ){
            for( int ix = 1 ; ix <= diff; ix++ ){
                b.setEntry(m + ix - 1, 0, 1);
            }
        }
        Log.i("B", Reaction.matrixToString(b));

        // solve for A * X = B via LUDecomposition
        //RealMatrix result = MatrixUtils.inverse(a).multiply(b).scalarMultiply(new LUDecomposition(a).getDeterminant());
        //RealMatrix result = new SingularValueDecomposition(a).getSolver().solve(b);
        RealMatrix result = new LUDecomposition(a).getSolver().solve(b);

        Log.i("X", Reaction.matrixToString(result));

        // ensure all values are lcd
        Fraction[] rationalResult = new Fraction[result.getColumn(0).length];
        int ix = 0;
        int maxDenominator = 0;
        for( double res : result.getColumn(0) ){
            Fraction rational = toFraction(res);
            maxDenominator = Math.max(rational.getDenominator(), maxDenominator);
            rationalResult[ix++] = rational;
        }

        // populate reactionChemicals with result
        int chemicalIndex = 0;
        for( ReactionChemical reactionChemical : this.reactants ){
            Fraction parts = rationalResult[chemicalIndex++];
            int partsInt = (int)(parts.doubleValue() * maxDenominator);
            reactionChemical.setParts(partsInt);
        }
        for( ReactionChemical reactionChemical : this.products ){
            Fraction parts = rationalResult[chemicalIndex++];
            int partsInt = (int)(parts.doubleValue() * maxDenominator);
            reactionChemical.setParts(partsInt);
        }

    }


    static String matrixToString(RealMatrix matrix){
        StringBuilder sb = new StringBuilder("\n");
        for( double[] row : matrix.getData() ){
            for( double el : row ){
                sb.append(el + " ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    static Fraction toFraction(double number) {

        int largestRightOfDecimal = 8;
        int sign = 1;
        if (number < 0) {
            number = -number;
            sign = -1;
        }

        final int SECOND_MULTIPLIER_MAX = (int) Math.pow(10, largestRightOfDecimal - 1);
        final int FIRST_MULTIPLIER_MAX = SECOND_MULTIPLIER_MAX * 10;
        final double ERROR = Math.pow(10, -largestRightOfDecimal - 1);
        int firstMultiplier = 1;
        int secondMultiplier = 1;
        boolean notIntOrIrrational = false;
        int truncatedNumber = (int) number;
        Fraction rationalNumber = new Fraction((int)(sign * number * FIRST_MULTIPLIER_MAX), FIRST_MULTIPLIER_MAX);

        double error = number - truncatedNumber;
        while ((error >= ERROR) && (firstMultiplier <= FIRST_MULTIPLIER_MAX)) {
            secondMultiplier = 1;
            firstMultiplier *= 10;
            while ((secondMultiplier <= SECOND_MULTIPLIER_MAX) && (secondMultiplier < firstMultiplier)) {
                double difference = (number * firstMultiplier) - (number * secondMultiplier);
                truncatedNumber = (int) difference;
                error = difference - truncatedNumber;
                if (error < ERROR) {
                    notIntOrIrrational = true;
                    break;
                }
                secondMultiplier *= 10;
            }
        }

        if(notIntOrIrrational){
            rationalNumber = new Fraction(sign * truncatedNumber, firstMultiplier - secondMultiplier);
        }
        return rationalNumber;
    }


}
