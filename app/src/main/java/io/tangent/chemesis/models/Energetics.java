package io.tangent.chemesis.models;

import android.content.Context;

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
public class Energetics {

    private NavigableMap<Double, EnergeticsEntry> data = new TreeMap<Double, EnergeticsEntry>();



    public Energetics(JSONObject energeticsJson, int multiplier){
        try {
            JSONArray data = energeticsJson.getJSONArray("data");
            for( int ix=0; ix < data.length(); ix++ ){
                JSONObject tempPoint = data.getJSONObject(ix);
                Double temperature = Double.valueOf(tempPoint.getString("T"));

                JSONObject values = tempPoint.getJSONObject("values");
                double gibbs = Double.valueOf(values.optString("delta-f G°", "0"));
                double enthalpy = Double.valueOf(values.optString("delta-f H°", "0"));
                double entropy = Double.valueOf(values.optString("S°", "0"));

                this.data.put(temperature, new EnergeticsEntry(multiplier * gibbs, multiplier * enthalpy, multiplier * entropy));
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
                this.data.putAll(en.getData());
            } else {
                for (Map.Entry<Double, EnergeticsEntry> entry : en.getData().entrySet()) {
                    EnergeticsEntry point = entry.getValue();
                    if (this.data.containsKey(entry.getKey())) {
                        this.data.get(entry.getKey()).add(point);
                    } else {
                        Map.Entry<Double, EnergeticsEntry> ceil = this.data.ceilingEntry(entry.getKey());
                        Map.Entry<Double, EnergeticsEntry> floor = this.data.floorEntry(entry.getKey());
                        EnergeticsEntry newEntry;
                        if (ceil == null) {
                            newEntry = floor.getValue().copy();
                        } else if (floor == null) {
                            newEntry = ceil.getValue().copy();
                        } else {
                            newEntry = EnergeticsEntry.extrapolate(
                                    floor.getValue(), ceil.getValue(),
                                    ((entry.getKey() - floor.getKey()) / (ceil.getKey() - floor.getKey()))
                            );
                        }
                        newEntry.add(point);
                        this.data.put(entry.getKey(), newEntry);
                    }
                }
            }
        }
    }



    public NavigableMap<Double, EnergeticsEntry> getData() {
        return data;
    }


}

