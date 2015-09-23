package io.tangent.chemesis;

import java.util.Locale;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.Toast;

import io.tangent.chemesis.models.Chemical;
import io.tangent.chemesis.models.Reaction;
import io.tangent.chemesis.util.Callback;


public class MainActivity extends ActionBarActivity {

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

    private MenuItem balanceButton;
    private MenuItem energeticsButton;

    private static final int REACTANTS = 0;
    private static final int PRODUCTS = 1;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if( savedInstanceState != null ) {
           this.reaction = savedInstanceState.getParcelable("reaction");
        } else {
            // create fake reaction
            this.reaction = new Reaction();
            // TODO: Debugging Code. Remove and replace with empty state!!
            this.reaction.addReactant(Chemical.CH4_g);
            this.reaction.addReactant(Chemical.O2_ref);
            this.reaction.addProduct(Chemical.C5_g);
            this.reaction.addProduct(Chemical.H2O_g);
        }

        this.reaction.setOnInvalidate(new Callback<Object>() {
            public void perform(Object thing) {
                invalidateOptionsMenu();
            }
        });

        // paging adapter for fragments
        mSectionsPagerAdapter = new SectionsPagerAdapter( getSupportFragmentManager(), this.reaction );

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.nist_purple)));
    }


    @Override
    public void onSaveInstanceState(Bundle bundle){
        bundle.putParcelable("reaction", this.reaction);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.balanceButton = menu.findItem(R.id.action_balance);
        this.energeticsButton = menu.findItem(R.id.action_energetics);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        this.energeticsButton.setEnabled(this.reaction.isBalanced());
        this.energeticsButton.setVisible(this.reaction.isBalanced());
        this.balanceButton.setEnabled(!this.reaction.isBalanced());
        this.balanceButton.setVisible( !this.reaction.isBalanced() );
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_balance) {
            try {
                this.reaction.balance();
                this.mSectionsPagerAdapter.getReactantsFragment().notifyDataSetChanged();
                this.mSectionsPagerAdapter.getProductsFragment().notifyDataSetChanged();
                this.invalidateOptionsMenu();
            } catch (IllegalStateException ex){
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else if( id == R.id.action_energetics ){
            Intent intent = new Intent(this, EnergeticsActivity.class);
            intent.putExtra("reaction", this.reaction);
            this.startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
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
        if( requestCode == REACTANTS){
            this.reaction.addReactant(chem);
        } else if( requestCode == PRODUCTS){
            this.reaction.addProduct(chem);
        } else {
            Log.e("TAG", "Invalid Request Code: "+String.valueOf(requestCode));
        }
        this.mSectionsPagerAdapter.getReactantsFragment().notifyDataSetChanged();
        this.mSectionsPagerAdapter.getProductsFragment().notifyDataSetChanged();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private ChemlistFragment reactantsFragment;
        private ChemlistFragment productsFragment;

        public SectionsPagerAdapter(FragmentManager fm, Reaction reaction) {
            super(fm);
            this.reactantsFragment = ChemlistFragment.newInstance(reaction, false, REACTANTS);
            this.productsFragment = ChemlistFragment.newInstance(reaction, true, PRODUCTS);
        }

        public ChemlistFragment getReactantsFragment(){
            return this.reactantsFragment;
        }

        public ChemlistFragment getProductsFragment(){
            return this.productsFragment;
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case REACTANTS:
                    return reactantsFragment;
                case PRODUCTS:
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
                    return getString(R.string.title_section1);
                case 1:
                    return getString(R.string.title_section2);
            }
            return null;
        }
    }


}
