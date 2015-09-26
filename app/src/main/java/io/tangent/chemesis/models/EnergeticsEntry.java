package io.tangent.chemesis.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.Map;

/**
 * Created by Jesse on 9/19/2015.
 */
public class EnergeticsEntry implements Parcelable {
    

    public Double gibbs;
    public Double enthalpy;
    public Double entropy;
    
    public EnergeticsEntry(Double g, Double enthalpy, Double entropy){
        this.gibbs = g;
        this.enthalpy = enthalpy;
        this.entropy = entropy;
    }


    public void add( EnergeticsEntry other ){
        if( this.gibbs != null && other.gibbs != null ) {
            this.gibbs += other.gibbs;
        } else {
            this.gibbs = null;
        }
        if( this.enthalpy != null && other.enthalpy != null ) {
            this.enthalpy += other.enthalpy;
        } else {
            this.enthalpy = null;
        }
        if( this.entropy != null && other.entropy != null ) {
            this.entropy += other.entropy;
        } else {
            this.entropy = null;
        }
    }

    public void subtract( EnergeticsEntry other ){
        if( this.gibbs != null && other.gibbs != null ) {
            this.gibbs -= other.gibbs;
        } else {
            this.gibbs = null;
        }
        if( this.enthalpy != null && other.enthalpy != null ) {
            this.enthalpy -= other.enthalpy;
        } else {
            this.enthalpy = null;
        }
        if( this.entropy != null && other.entropy != null ) {
            this.entropy -= other.entropy;
        } else {
            this.entropy = null;
        }
    }

    public EnergeticsEntry multiply( int multiplier ){
        return new EnergeticsEntry(
                this.gibbs != null ? multiplier * this.gibbs : null,
                this.enthalpy != null ? multiplier * this.enthalpy : null,
                this.entropy != null ? multiplier * this.entropy : null );
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

    @Override
    public String toString(){
        return  (this.gibbs != null ? this.gibbs : "") + "," +
                (this.enthalpy != null ? this.enthalpy : "") + "," +
                (this.entropy != null ? this.entropy : "");
    }


    public static EnergeticsEntry extrapolate(EnergeticsEntry pre, EnergeticsEntry post, double weight) {
        return new EnergeticsEntry(
                (pre.gibbs != null && post.gibbs != null) ? ((pre.gibbs * (1-weight)) + (post.gibbs * (weight))) : null,
                (pre.enthalpy != null && post.enthalpy != null) ? ((pre.enthalpy * (1-weight)) + (post.enthalpy * (weight))) : null,
                (pre.entropy != null && post.entropy != null) ? ((pre.entropy * (1-weight))  + (post.entropy * (weight))) : null
        );
    }




    // Parceling

    private EnergeticsEntry(Parcel in){
        this.gibbs = (Double)in.readSerializable();
        this.enthalpy = (Double)in.readSerializable();
        this.entropy = (Double)in.readSerializable();
    }

    public static final Parcelable.Creator<EnergeticsEntry> CREATOR = new Parcelable.Creator<EnergeticsEntry>() {
        public EnergeticsEntry createFromParcel(Parcel in) {
            return new EnergeticsEntry(in);
        }
        public EnergeticsEntry[] newArray(int size) {
            return new EnergeticsEntry[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.gibbs);
        dest.writeSerializable(this.enthalpy);
        dest.writeSerializable(this.entropy);
    }
}
