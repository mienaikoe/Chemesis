package io.tangent.chemesis.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Created by Jesse on 9/19/2015.
 */
public class Energetics implements Parcelable{

    private NavigableMap<Double, EnergeticsEntry> data = new TreeMap<Double, EnergeticsEntry>();



    public Energetics(JSONObject energeticsJson, int multiplier){
        try {
            JSONArray data = energeticsJson.getJSONArray("data");
            for( int ix=0; ix < data.length(); ix++ ){
                JSONObject tempPoint = data.getJSONObject(ix);
                Double temperature = Double.valueOf(tempPoint.getString("T"));

                JSONObject values = tempPoint.getJSONObject("values");

                Double gibbs = null, enthalpy = null, entropy = null;
                String gibbsStr = values.optString(EnergeticsField.GIBBS.getFieldname());
                if( !gibbsStr.isEmpty() ){
                    gibbs = Double.valueOf(gibbsStr);
                }

                String enthStr = values.optString(EnergeticsField.ENTHALPY.getFieldname());
                if( !enthStr.isEmpty() ){
                    enthalpy = Double.valueOf(enthStr);
                }

                String entrStr = values.optString(EnergeticsField.ENTROPY.getFieldname());
                if( !entrStr.isEmpty() ){
                    entropy = Double.valueOf(entrStr);
                }
                this.data.put(temperature, new EnergeticsEntry(
                        gibbs != null ? multiplier * gibbs : null,
                        enthalpy != null ? multiplier * enthalpy : null,
                        entropy != null ? multiplier * entropy : null)
                );
            }
        } catch (JSONException ex){
            ex.printStackTrace();
            throw new IllegalArgumentException(ex);
        }
    }

    public Energetics( List<ReactionChemical> reactionChemicals, Context context ){
        for( ReactionChemical rc : reactionChemicals ){
            Energetics en = rc.getEnergetics( context );
            if( this.data.isEmpty() ){
                for( Map.Entry<Double, EnergeticsEntry> point : en.getData().entrySet() ){
                    this.data.put(point.getKey(), point.getValue().copy());
                }
            } else {
                Double highestLow = Math.max(this.data.firstKey(), en.getData().firstKey());
                Double lowestHigh = Math.min(this.data.lastKey(),  en.getData().lastKey());
                highestLow = this.data.ceilingKey(highestLow);
                lowestHigh = this.data.floorKey(lowestHigh);
                TreeMap<Double, EnergeticsEntry> newData = new TreeMap<Double, EnergeticsEntry>();
                for( Double indexTemp = highestLow ; indexTemp != null && indexTemp <= lowestHigh; indexTemp = this.data.higherKey(indexTemp) ){
                    EnergeticsEntry thisEnergy = this.data.get(indexTemp);
                    EnergeticsEntry incomingEnergy = en.getData().get(indexTemp);
                    if ( incomingEnergy == null ) {
                        Map.Entry<Double, EnergeticsEntry> lower  = en.getData().lowerEntry(indexTemp);
                        Map.Entry<Double, EnergeticsEntry> higher = en.getData().higherEntry(indexTemp);
                        incomingEnergy = EnergeticsEntry.extrapolate(
                                lower.getValue(), higher.getValue(),
                                (indexTemp - lower.getKey()) / (higher.getKey() - lower.getKey())
                        );

                    }
                    EnergeticsEntry newEnergy = thisEnergy.copy();
                    newEnergy.add(incomingEnergy);
                    newData.put(indexTemp, newEnergy);
                }
                this.data = newData;
            }
        }
    }



    public NavigableMap<Double, EnergeticsEntry> getData() {
        return data;
    }

    public Double extrapolateValue(Double cursorTemp, EnergeticsField mode) {
        Map.Entry<Double, EnergeticsEntry> floor = this.getData().floorEntry(cursorTemp);
        Map.Entry<Double, EnergeticsEntry> ceiling = this.getData().ceilingEntry(cursorTemp);

        if( floor == null ) {
            return ceiling.getValue().get(mode);
        } else if( floor.getKey().equals(cursorTemp) ){
            return floor.getValue().get(mode);
        } else if( ceiling == null ){
            return floor.getValue().get(mode);
        } else if ( ceiling.getKey().equals(cursorTemp) ) {
            return ceiling.getValue().get(mode);
        }

        double weight = ((cursorTemp - floor.getKey()) / (ceiling.getKey() - floor.getKey()));
        EnergeticsEntry entry = EnergeticsEntry.extrapolate(floor.getValue(), ceiling.getValue(), weight);
        return entry.get(mode);
    }

    
    
    // Parceling

    private Energetics(Parcel in){
        int size = in.readInt();
        for( int ix=0; ix < size; ix++ ){
            this.data.put(in.readDouble(), (EnergeticsEntry)in.readParcelable(EnergeticsEntry.class.getClassLoader()));
        }
    }

    public static final Parcelable.Creator<Energetics> CREATOR = new Parcelable.Creator<Energetics>() {
        public Energetics createFromParcel(Parcel in) {
            return new Energetics(in);
        }
        public Energetics[] newArray(int size) {
            return new Energetics[size];
        }
    };
    
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.data.size());
        for( Map.Entry<Double, EnergeticsEntry> entry : this.data.entrySet() ){
            dest.writeDouble(entry.getKey());
            dest.writeParcelable(entry.getValue(), 0);
        }
    }



    public String toString(){
        StringBuilder sb = new StringBuilder();
        for( Map.Entry<Double, EnergeticsEntry> entry : this.data.entrySet() ){
            sb.append(entry.getKey()).append(": ").append(entry.getValue().toString()).append("\r\n");
        }
        return sb.toString();
    }


}

