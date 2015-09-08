package io.tangent.chemesis;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;

import io.tangent.chemesis.models.ReactionChemical;
import io.tangent.chemesis.views.TextViewPlus;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChemlistFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChemlistFragment extends Fragment {

    private static String PARAM1 = "chemlistName";
    private String chemlistName;

    private List<ReactionChemical> chemicals;

    private OnTabInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BuildFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChemlistFragment newInstance(String chemlistName) {
        ChemlistFragment fragment = new ChemlistFragment();
        fragment.setChemlistName(chemlistName);
        return fragment;
    }

    public ChemlistFragment() {
        // Required empty public constructor
    }

    private void setChemlistName(String chemlistName){
        this.chemlistName = chemlistName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret = inflater.inflate(R.layout.fragment_chemlist, container, false);

        LinearLayout list = (LinearLayout)ret.findViewById(R.id.chemicals);
        this.chemicals = ((MainActivity)getActivity()).getChemlist(this.chemlistName);
        for( ReactionChemical c : this.chemicals ){
            TextViewPlus chemicalView = new TextViewPlus(this.getActivity().getApplicationContext());
            chemicalView.setText(c.getChemical().getName());
            chemicalView.setPadding(20, 20, 20, 20);
            chemicalView.setTextColor(Color.WHITE);
            chemicalView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            list.addView(chemicalView);
        }

        return ret;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnTabInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnTabInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


}
