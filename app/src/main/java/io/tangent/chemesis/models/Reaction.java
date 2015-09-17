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

import io.tangent.chemesis.util.Callback;


/**
 * Created by Jesse on 9/7/2015.
 */
public class Reaction {

    private final List<ReactionChemical> reactants;
    private final List<ReactionChemical> products;
    private boolean isBalanced = false;
    private Callback onInvalidate;

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
        this.invalidateBalance();
    }

    public void addProduct(Chemical chemical){
        this.products.add(new ReactionChemical(chemical, this));
        this.invalidateBalance();
    }

    public void remove(ReactionChemical chemical){
        List<ReactionChemical> chemlist = null;
        if( this.reactants.contains(chemical) ){
            chemlist = this.reactants;
        } else if( this.products.contains(chemical) ){
            chemlist = this.products;
        }

        if( chemlist != null ){
            chemlist.remove(chemical);
            this.invalidateBalance();
        }
    }

    private void invalidateBalance(){
        if( this.isBalanced ) {
            this.isBalanced = false;
            for (ReactionChemical rc : this.reactants) {
                rc.setParts(null);
            }
            for (ReactionChemical rc : this.products) {
                rc.setParts(null);
            }
            if( this.onInvalidate != null ){
                this.onInvalidate.perform(null);
            }
        }
    }

    public void setOnInvalidate( Callback cb ){
        this.onInvalidate = cb;
    }

    public boolean isBalanced(){
        return this.isBalanced;
    }




    public void balance() throws IllegalStateException{
        ReactionBalancer balancer = new ReactionBalancer();
        balancer.balanceThorne(this);
        this.isBalanced = true;
    }








}



