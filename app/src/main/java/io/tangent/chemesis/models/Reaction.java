package io.tangent.chemesis.models;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

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
        Log.w("NOT DONE","NOT DONE");
    }



}
