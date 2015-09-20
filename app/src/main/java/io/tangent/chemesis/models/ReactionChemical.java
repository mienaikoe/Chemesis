package io.tangent.chemesis.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by Jesse on 9/7/2015.
 */
public class ReactionChemical implements Parcelable{

    private final Chemical chemical;
    private Integer parts;
    private boolean isProduct;

    public ReactionChemical(Chemical chemical, boolean isProduct){
        this.chemical = chemical;
        this.isProduct = isProduct;
    }

    private ReactionChemical(Parcel in){
        this.chemical = Chemical.valueOf(in.readString());
        this.parts = in.readInt();
        this.isProduct = in.readInt() == 1;
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




    public Energetics getEnergetics(Context context){
        try {
            InputStream istr = null;
            istr = context.getAssets().open("chemistry/"+this.chemical.getFilename());

            BufferedReader streamReader = new BufferedReader(new InputStreamReader(istr, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null) {
                responseStrBuilder.append(inputStr);
            }
            JSONObject energeticsJson = new JSONObject(responseStrBuilder.toString());

            return new Energetics(energeticsJson, (this.isProduct ? this.parts : -this.parts) );
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("File "+this.chemical.getFilename()+" does not exist");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new IllegalStateException("Bad Encoding");
        } catch (JSONException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }






    // Parceling

    public static final Parcelable.Creator<ReactionChemical> CREATOR = new Parcelable.Creator<ReactionChemical>() {
        public ReactionChemical createFromParcel(Parcel in) {
            return new ReactionChemical(in);
        }
        public ReactionChemical[] newArray(int size) {
            return new ReactionChemical[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(chemical.name());
        dest.writeInt(parts);
        dest.writeInt(isProduct ? 1 : 0);
    }
}