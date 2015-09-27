package io.tangent.chemesis.views;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import io.tangent.chemesis.ChemlistFragment;
import io.tangent.chemesis.R;
import io.tangent.chemesis.models.Chemical;
import io.tangent.chemesis.models.EnergeticsField;
import io.tangent.chemesis.models.Reaction;
import io.tangent.chemesis.models.ReactionChemical;

/**
 * TODO: document your custom view class.
 */
public class ReactionChemicalArrayAdapter extends ArrayAdapter<ReactionChemical> {

    private List<ReactionChemical> chemicals;
    private Reaction reaction;
    private LayoutInflater mInflater;
    private ChemlistFragment parent;


    public ReactionChemicalArrayAdapter(Context context, List<ReactionChemical> chemicals, Reaction reaction) {
        super(context, 0);
        this.chemicals = chemicals;
        this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.reaction = reaction;
    }


    public int getCount(){
        return this.chemicals.size();
    }

    public View getView( int position, View ret, ViewGroup parent  ) {
        ReactionChemical chem = this.chemicals.get(position);

        if (ret == null) {
            ret = mInflater.inflate(R.layout.reaction_chemical_list_item_view, null);
        }

        TextView chemicalCount = (TextView) ret.findViewById(R.id.chemical_count);
        if( chem.getParts() == null ) {
            chemicalCount.setText("");
        } else {
            chemicalCount.setText(String.valueOf(chem.getParts()));
        }

        TextView chemicalName = (TextView) ret.findViewById(R.id.chemical_name);
        chemicalName.setText(chem.getChemical().getName() + " (" + chem.getChemical().getState() + ")");

        TextView chemicalFormula = (TextView) ret.findViewById(R.id.chemical_formula);
        chemicalFormula.setText(chem.getChemical().getFormula());

        ImageButton remover = (ImageButton) ret.findViewById(R.id.remove_chemical);
        remover.setOnClickListener(new ReactionChemicalRemoverClickListener(chem));

        // conditionals
        return ret;
    }


    public void setParent(ChemlistFragment fragment){
        this.parent = fragment;
    }



    class ReactionChemicalRemoverClickListener implements View.OnClickListener {

        private ReactionChemical reactionChemical;

        public ReactionChemicalRemoverClickListener(ReactionChemical reactionChemical){
            this.reactionChemical = reactionChemical;
        }

        @Override
        public void onClick(View v) {
            reaction.remove(this.reactionChemical);
            if( parent != null ) {
                parent.notifyDataSetChanged();
            } else {
                notifyDataSetChanged();
            }
            this.reactionChemical = null; // for gc
        }
    }
}


