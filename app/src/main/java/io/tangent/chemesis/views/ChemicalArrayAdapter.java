package io.tangent.chemesis.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import io.tangent.chemesis.R;
import io.tangent.chemesis.models.Chemical;

/**
 * Created by Jesse on 9/11/2015.
 */
public class ChemicalArrayAdapter extends ArrayAdapter<Chemical> {

    private List<Chemical> mObjects;
    private LayoutInflater mInflater;


    public ChemicalArrayAdapter(Context context, List<Chemical> objects) {
        super(context, 0, objects);
        this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mObjects = objects;
    }

    public int getCount(){
        return this.mObjects.size();
    }

    public List<Chemical> getObjects() {
        return mObjects;
    }

    public void setObjects(List<Chemical> objects) {
        this.mObjects = objects;
        this.notifyDataSetChanged();
    }

    public View getView( int position, View ret, ViewGroup parent  ) {
        Chemical chem = this.mObjects.get(position);

        if (ret == null) {
            ret = mInflater.inflate(R.layout.chemical_list_item_view, null);
        }

        TextView chemicalName = (TextView) ret.findViewById(R.id.chemical_name);
        chemicalName.setText(chem.getName() + " (" + chem.getState() + ")");

        TextView chemicalFormula = (TextView) ret.findViewById(R.id.chemical_formula);
        chemicalFormula.setText(chem.getFormula());

        // conditionals
        return ret;
    }

    public void insert(Chemical chemical, int index){
        this.mObjects.add(index, chemical);
    }
}