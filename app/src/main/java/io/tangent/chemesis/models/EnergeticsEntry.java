package io.tangent.chemesis.models;

import java.util.Map;

/**
 * Created by Jesse on 9/19/2015.
 */
public class EnergeticsEntry {
    

    public double gibbs;
    public double enthalpy;
    public double entropy;
    
    public EnergeticsEntry(double g, double enthalpy, double entropy){

        this.gibbs = g;
        this.enthalpy = enthalpy;
        this.entropy = entropy;
    }

    public void add( EnergeticsEntry other ){
        this.gibbs += other.gibbs;
        this.enthalpy += other.enthalpy;
        this.entropy += other.entropy;
    }

    public void subtract( EnergeticsEntry other ){
        this.gibbs -= other.gibbs;
        this.enthalpy -= other.enthalpy;
        this.entropy -= other.entropy;
    }

    public EnergeticsEntry multiply( int multiplier ){
        return new EnergeticsEntry( multiplier * this.gibbs, multiplier * this.enthalpy, multiplier * this.entropy);
    }

    public EnergeticsEntry copy(){
        return this.multiply(1);
    }


    public Double get(EnergeticsField field){
        switch(field){
            case GIBBS:
                return this.gibbs;
            case ENTHALPY:
                return this.enthalpy;
            case ENTROPY:
                return this.entropy;
            default:
                return null;
        }
    }


    public static EnergeticsEntry extrapolate(EnergeticsEntry pre, EnergeticsEntry post, double weight) {
        return new EnergeticsEntry(
                (pre.gibbs * weight)    + (post.gibbs * (1-weight)),
                (pre.enthalpy * weight) + (post.enthalpy * (1-weight)),
                (pre.entropy * weight)  + (post.entropy * (1-weight))
        );
    }
}
