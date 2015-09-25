package io.tangent.chemesis.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jesse on 9/24/2015.
 */
public class EnergeticsSet implements Parcelable {

    private Energetics reactantEnergetics;
    private Energetics productEnergetics;
    private Energetics combinedEnergetics;

    public EnergeticsSet(Energetics reactantEnergetics, Energetics productEnergetics, Energetics combinedEnergetics){
        this.reactantEnergetics = reactantEnergetics;
        this.productEnergetics = productEnergetics;
        this.combinedEnergetics = combinedEnergetics;
    }



    public Energetics getCombinedEnergetics() {
        return combinedEnergetics;
    }

    public Energetics getProductEnergetics() {
        return productEnergetics;
    }

    public Energetics getReactantEnergetics() {
        return reactantEnergetics;
    }




    // Parcelable


    public static final Parcelable.Creator<EnergeticsSet> CREATOR = new Parcelable.Creator<EnergeticsSet>() {
        public EnergeticsSet createFromParcel(Parcel in) {
            return new EnergeticsSet(in);
        }
        public EnergeticsSet[] newArray(int size) {
            return new EnergeticsSet[size];
        }
    };

    public EnergeticsSet(Parcel in) {
        this.reactantEnergetics = in.readParcelable(EnergeticsSet.class.getClassLoader());
        this.productEnergetics = in.readParcelable(EnergeticsSet.class.getClassLoader());
        this.combinedEnergetics = in.readParcelable(EnergeticsSet.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.reactantEnergetics, 0);
        dest.writeParcelable(this.productEnergetics, 0);
        dest.writeParcelable(this.combinedEnergetics, 0);
    }

}
