package io.tangent.chemesis.views;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import io.tangent.chemesis.R;
import io.tangent.chemesis.models.Chemical;
import io.tangent.chemesis.models.ReactionChemical;

/**
 * TODO: document your custom view class.
 */
public class ChemicalArrayAdapter extends ArrayAdapter<ReactionChemical> {

    private List<ReactionChemical> mObjects;
    private Context mContext;
    private LayoutInflater mInflater;


    public ChemicalArrayAdapter(Context context, int resource, List<ReactionChemical> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mObjects = objects;
    }

    public int getCount(){
        return this.mObjects.size();
    }

    public View getView( int position, View convertView, ViewGroup parent  ){
        ReactionChemical chem = this.mObjects.get(position);
        // no re-use yet
        View ret = mInflater.inflate(R.layout.chemical_list_item_view, null);

        TextView chemicalName = (TextView)ret.findViewById(R.id.chemical_name);
        chemicalName.setText(chem.getChemical().getName());

        TextView chemicalFormula = (TextView)ret.findViewById(R.id.chemical_formula);
        chemicalFormula.setText(chem.getChemical().getFormula());

        // conditionals
        return ret;
    }

    public void insert(ReactionChemical object, int index){
        this.mObjects.add(index, object);
    }
}
