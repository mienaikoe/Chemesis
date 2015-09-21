package io.tangent.chemesis.models;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
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

public class Reaction implements Parcelable{

    private final ArrayList<ReactionChemical> reactants;
    private final ArrayList<ReactionChemical> products;
    private boolean isBalanced = false;
    private Callback onInvalidate;

    public Reaction(){
        this.reactants = new ArrayList<ReactionChemical>();
        this.products = new ArrayList<ReactionChemical>();
    }


    public ArrayList<ReactionChemical> getProducts() {
        return products;
    }

    public ArrayList<ReactionChemical> getReactants() {
        return reactants;
    }

    public void addReactant(Chemical chemical){
        this.reactants.add(new ReactionChemical(chemical, false));
        this.invalidateBalance();
    }

    public void addProduct(Chemical chemical){
        this.products.add(new ReactionChemical(chemical, true));
        this.invalidateBalance();
    }

    public void remove(ReactionChemical chemical){
        ArrayList<ReactionChemical> chemlist = null;
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





    // Parceling


    private Reaction(Parcel in){
        this.reactants = new ArrayList<ReactionChemical>();
        this.products = new ArrayList<ReactionChemical>();

        in.readTypedList(this.reactants, ReactionChemical.CREATOR);
        in.readTypedList(this.products, ReactionChemical.CREATOR);
        this.isBalanced = (in.readInt() == 1);
    }

    public static final Parcelable.Creator<Reaction> CREATOR = new Parcelable.Creator<Reaction>() {
        public Reaction createFromParcel(Parcel in) {
            return new Reaction(in);
        }
        public Reaction[] newArray(int size) {
            return new Reaction[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.reactants);
        dest.writeTypedList(this.products);
        dest.writeInt(this.isBalanced ? 1 : 0);
    }
}



