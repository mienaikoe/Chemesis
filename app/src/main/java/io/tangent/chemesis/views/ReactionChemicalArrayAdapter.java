package io.tangent.chemesis.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
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
            ret = mInflater.inflate(R.layout.reaction_chemical_list_item_view, null);
        }

        if( chem.getParts() != null ){
            TextView chemicalCount = (TextView) ret.findViewById(R.id.chemical_count);
            chemicalCount.setText(String.valueOf(chem.getParts()));
        }

        TextView chemicalName = (TextView) ret.findViewById(R.id.chemical_name);
        chemicalName.setText(chem.getChemical().getName());

        TextView chemicalFormula = (TextView) ret.findViewById(R.id.chemical_formula);
        chemicalFormula.setText(chem.getChemical().getFormula());

        ImageButton remover = (ImageButton) ret.findViewById(R.id.remove_chemical);
        remover.setOnClickListener(new ReactionChemicalRemoverClickListener(chem));

        // conditionals
        return ret;
    }





    class ReactionChemicalRemoverClickListener implements View.OnClickListener {

        private ReactionChemical reactionChemical;

        public ReactionChemicalRemoverClickListener(ReactionChemical reactionChemical){
            this.reactionChemical = reactionChemical;
        }

        @Override
        public void onClick(View v) {
            mObjects.remove(this.reactionChemical);
            notifyDataSetChanged();
            this.reactionChemical = null; // for gc
        }
    }
}


