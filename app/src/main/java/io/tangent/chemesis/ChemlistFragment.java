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
import android.widget.ListView;

import java.util.List;

import io.tangent.chemesis.models.ReactionChemical;
import io.tangent.chemesis.views.ChemicalArrayAdapter;
import io.tangent.chemesis.views.TextViewPlus;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChemlistFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChemlistFragment extends Fragment {

    private String name;
    private ChemicalArrayAdapter mAdapter;
    private OnTabInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BuildFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChemlistFragment newInstance(ChemicalArrayAdapter adapter, String name) {
        ChemlistFragment fragment = new ChemlistFragment();
        fragment.setName(name);
        fragment.setAdapter(adapter);
        return fragment;
    }

    public ChemlistFragment() {
        // Required empty public constructor
    }

    private void setName(String name){
        this.name = name;
    }

    private void setAdapter(ChemicalArrayAdapter adapter){
        this.mAdapter = adapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret = inflater.inflate(R.layout.fragment_chemlist, container, false);
        ListView list = (ListView)ret.findViewById(R.id.chemlist);
        list.setAdapter(this.mAdapter);
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
