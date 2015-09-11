package io.tangent.chemesis;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import io.tangent.chemesis.views.ReactionChemicalArrayAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChemlistFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChemlistFragment extends Fragment implements View.OnClickListener {

    private int addRequestId;
    private ReactionChemicalArrayAdapter mAdapter;
    private OnTabInteractionListener mListener;
    private Activity parentActivity;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BuildFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChemlistFragment newInstance(ReactionChemicalArrayAdapter adapter, int addRequestId) {
        ChemlistFragment fragment = new ChemlistFragment();
        fragment.setAddRequestId(addRequestId);
        fragment.setAdapter(adapter);
        return fragment;
    }

    public ChemlistFragment() {
        // Required empty public constructor
    }

    private void setAddRequestId(int requestId){
        this.addRequestId = requestId;
    }

    private void setAdapter(ReactionChemicalArrayAdapter adapter){
        this.mAdapter = adapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret = inflater.inflate(R.layout.fragment_chemlist, container, false);
        ListView list = (ListView)ret.findViewById(R.id.chemlist);
        list.setAdapter(this.mAdapter);

        ImageButton addButton = (ImageButton)ret.findViewById(R.id.add_chemical);
        addButton.setOnClickListener(this);

        return ret;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(parentActivity, SearchActivity.class);
        getActivity().startActivityForResult(intent, addRequestId);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.parentActivity = activity;
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
