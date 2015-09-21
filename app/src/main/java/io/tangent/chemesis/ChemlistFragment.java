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

import java.util.List;

import io.tangent.chemesis.models.Reaction;
import io.tangent.chemesis.models.ReactionChemical;
import io.tangent.chemesis.views.ReactionChemicalArrayAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChemlistFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChemlistFragment extends Fragment implements View.OnClickListener {

    private int addRequestId;
    private Reaction reaction;
    private boolean isProduct;

    private ReactionChemicalArrayAdapter mAdapter;
    private Activity parentActivity;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BuildFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChemlistFragment newInstance(Reaction reaction, boolean isProduct, int addRequestId) {
        ChemlistFragment fragment = new ChemlistFragment();
        fragment.init(reaction, isProduct, addRequestId);
        return fragment;
    }

    public ChemlistFragment() {
        // Required empty public constructor
    }

    private void init(Reaction reaction, boolean isProduct, int addRequestId) {
        this.reaction = reaction;
        this.isProduct = isProduct;
        this.addRequestId = addRequestId;
        this.initAdapter();
    }

    private void initAdapter(){
        if( this.mAdapter == null && this.parentActivity != null && this.reaction != null ) {
            this.mAdapter = new ReactionChemicalArrayAdapter(
                    this.getActivity(), (isProduct ? this.reaction.getProducts() : this.reaction.getReactants()), this.reaction
            );
        }
    }


    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            //savedInstanceState.setClassLoader(getClass().getClassLoader());
            this.init(
                    (Reaction) savedInstanceState.getParcelable("reaction"),
                    savedInstanceState.getBoolean("isProduct"),
                    savedInstanceState.getInt("addRequestId")
            );
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret = inflater.inflate(R.layout.fragment_chemlist, container, false);
        ListView list = (ListView)ret.findViewById(R.id.chemlist);

        this.initAdapter();
        list.setAdapter(this.mAdapter);

        ImageButton addButton = (ImageButton)ret.findViewById(R.id.add_chemical);
        addButton.setOnClickListener(this);



        return ret;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this.getActivity(), SearchActivity.class);
        getActivity().startActivityForResult(intent, addRequestId);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.parentActivity = activity;
        this.initAdapter();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        // Note: getValues() is a method in your ArrayAdaptor subclass
        savedState.putParcelable("reaction", this.reaction);
        savedState.putBoolean("isProduct", this.isProduct);
        savedState.putInt("addRequestId", this.addRequestId);
    }

    public void notifyDataSetChanged(){
        this.mAdapter.notifyDataSetChanged();
    }


}
