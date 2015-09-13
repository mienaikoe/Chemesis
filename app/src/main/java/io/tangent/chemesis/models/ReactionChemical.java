package io.tangent.chemesis.models;

/**
 * Created by Jesse on 9/7/2015.
 */
public class ReactionChemical {

    private final Chemical chemical;
    private final Reaction reaction;
    private Integer parts;

    public ReactionChemical(Chemical chemical, Reaction reaction){
        this.chemical = chemical;
        this.reaction = reaction;
    }

    public Integer getParts() {
        return parts;
    }

    public void setParts(Integer parts) {
        this.parts = parts;
    }

    public Chemical getChemical() {
        return chemical;
    }

    public void remove(){
        this.reaction.remove(this);
    }
}