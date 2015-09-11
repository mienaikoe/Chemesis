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
import io.tangent.chemesis.models.Reaction;
import io.tangent.chemesis.models.ReactionChemical;

/**
 * TODO: document your custom view class.
 */
public class ReactionChemicalArrayAdapter extends ArrayAdapter<ReactionChemical> {

    private List<ReactionChemical> mObjects;
    private Reaction reaction;
    private LayoutInflater mInflater;


    public ReactionChemicalArrayAdapter(Context context, List<ReactionChemical> objects, Reaction reaction) {
        super(context, 0, objects);
        this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mObjects = objects;
        this.reaction = reaction;
    }

    public int getCount(){
        return this.mObjects.size();
    }

    public View getView( int position, View ret, ViewGroup parent  ) {
        ReactionChemical chem = this.mObjects.get(position);

        if (ret == null) {
            ret = mInflater.inflate(R.layout.chemical_list_item_view, null);
        }

        TextView chemicalName = (TextView) ret.findViewById(R.id.chemical_name);
        chemicalName.setText(chem.getChemical().getName());

        TextView chemicalFormula = (TextView) ret.findViewById(R.id.chemical_formula);
        chemicalFormula.setText(chem.getChemical().getFormula());

        // conditionals
        return ret;
    }

    public void insert(Chemical chemical, int index){
        ReactionChemical rc = new ReactionChemical(chemical, this.reaction);
        this.mObjects.add(index, rc);
    }
}
