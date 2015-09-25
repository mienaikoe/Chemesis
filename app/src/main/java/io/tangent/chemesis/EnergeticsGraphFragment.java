package io.tangent.chemesis;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.tangent.chemesis.models.EnergeticsField;
import io.tangent.chemesis.models.EnergeticsSet;
import io.tangent.chemesis.views.EnergeticsGraph;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EnergeticsGraphFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EnergeticsGraphFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EnergeticsGraphFragment extends Fragment {
    private OnFragmentInteractionListener mListener;


    private EnergeticsSet energetics;

    private EnergeticsField mode;




    public static EnergeticsGraphFragment newInstance(EnergeticsSet energetics, EnergeticsField field) {
        EnergeticsGraphFragment fragment = new EnergeticsGraphFragment();
        fragment.init(energetics, field);
        return fragment;
    }

    public EnergeticsGraphFragment() {
        // Required empty public constructor
    }

    private void init(EnergeticsSet energetics, EnergeticsField field){
        this.energetics = energetics;
        this.mode = field;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_energetics_graph, container, false);

        EnergeticsGraph graph = (EnergeticsGraph)view.findViewById(R.id.energetics_graph);
        graph.setEnergetics(this.energetics);
        graph.setMode( this.mode );

        return view;
    }






    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
