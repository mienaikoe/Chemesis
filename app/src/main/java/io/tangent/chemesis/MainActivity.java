package io.tangent.chemesis;

import java.util.Locale;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import io.tangent.chemesis.models.Chemical;
import io.tangent.chemesis.models.Reaction;
import io.tangent.chemesis.views.ReactionChemicalArrayAdapter;


public class MainActivity extends ActionBarActivity implements OnTabInteractionListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    /*
     * The reaction
     */

    private Reaction reaction;
    private ReactionChemicalArrayAdapter reactantsAdapter;
    private ReactionChemicalArrayAdapter productsAdapter;

    private static final int REACTANTS_ADD = 0;
    private static final int PRODUCTS_ADD = 1;


    public Reaction getReaction() {
        return reaction;
    }






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create fake reaction
        this.reaction = new Reaction();
        this.reaction.addReactant(Chemical.CH4_g);
        this.reaction.addReactant(Chemical.O2_ref);
        this.reaction.addProduct(Chemical.CO2_g);
        this.reaction.addProduct(Chemical.H2O_g);

        this.reactantsAdapter = new ReactionChemicalArrayAdapter(
                this, this.reaction.getReactants(), this.reaction);

        this.productsAdapter = new ReactionChemicalArrayAdapter(
                this, this.reaction.getProducts(), this.reaction);

        // paging adapter for fragments
        mSectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager(),
                this.reactantsAdapter, this.productsAdapter
        );

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_balance) {
            this.reaction.balance();
            this.reactantsAdapter.notifyDataSetChanged();
            this.productsAdapter.notifyDataSetChanged();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        // TODO: IDK
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle b = data.getExtras();
        if( b == null ){
            return; // used back button
        }
        String chemName = b.getString("chemical");
        if( chemName == null ){
            Log.w("TAG", "No Chemname");
            return;
        }
        Chemical chem = Chemical.valueOf(chemName);
        if( chem == null ){
            Log.w("TAG", "Invalid Chemical: "+chemName);
            return;
        }
        if( requestCode == REACTANTS_ADD ){
            this.reaction.addReactant(chem);
            Log.i("TAG",String.valueOf(this.reaction.getReactants().size()));
            this.reactantsAdapter.notifyDataSetChanged();

        } else if( requestCode == PRODUCTS_ADD ){
            this.reaction.addProduct(chem);
            this.productsAdapter.notifyDataSetChanged();
        } else {
            Log.e("TAG", "Invalid Request Code: "+String.valueOf(requestCode));
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private ChemlistFragment reactantsFragment;
        private ChemlistFragment productsFragment;

        public SectionsPagerAdapter(FragmentManager fm,
                                    ReactionChemicalArrayAdapter reactantsAdapter,
                                    ReactionChemicalArrayAdapter productsAdapter) {
            super(fm);
            this.reactantsFragment = ChemlistFragment.newInstance(
                    reactantsAdapter, REACTANTS_ADD);
            this.productsFragment = ChemlistFragment.newInstance(
                    productsAdapter, PRODUCTS_ADD);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:
                    return reactantsFragment;
                case 1:
                    return productsFragment;
                default:
                    throw new IllegalArgumentException("Invalid Index for Fragment");
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
            }
            return null;
        }
    }


}
