package io.tangent.chemesis;



import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import io.tangent.chemesis.models.EnergeticsField;
import io.tangent.chemesis.models.EnergeticsSet;
import io.tangent.chemesis.models.Reaction;
import io.tangent.chemesis.models.Energetics;
import io.tangent.chemesis.models.ReactionChemical;
import io.tangent.chemesis.views.EnergeticsGraph;
import io.tangent.chemesis.views.TextViewPlus;


public class EnergeticsActivity extends ActionBarActivity implements EnergeticsGraphFragment.OnFragmentInteractionListener {

    private EnergeticsSet energetics;
    private EnergeticsGraph graph;

    //private EnergeticsGraphFragment gibbsFragment;
    ///private EnergeticsGraphFragment enthalpyFragment;
    //private EnergeticsGraphFragment entropyFragment;

    //private ViewPager mViewPager;

    private EnergeticsField[] fieldsOrder = EnergeticsField.values();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_energetics);

        this.initTabs();
        //this.initSwipes();

        if( savedInstanceState != null ){
            this.energetics = savedInstanceState.getParcelable("set");
        } else {
            Energetics reactantEnergetics, productEnergetics, combinedEnergetics;

            Reaction reaction = this.getIntent().getParcelableExtra("reaction");
            reactantEnergetics = new Energetics(reaction.getReactants(), this);
            productEnergetics = new Energetics(reaction.getProducts(), this);

            ArrayList<ReactionChemical> chems = new ArrayList<>(reaction.getReactants().size() + reaction.getProducts().size());
            chems.addAll(reaction.getReactants());
            chems.addAll(reaction.getProducts());
            combinedEnergetics = new Energetics(chems, this);

            this.energetics = new EnergeticsSet(reactantEnergetics, productEnergetics, combinedEnergetics);
        }

        this.graph = (EnergeticsGraph)this.findViewById(R.id.energetics_graph);
        this.graph.setEnergetics(this.energetics);



/*
        this.gibbsFragment = EnergeticsGraphFragment.newInstance( this.energetics, EnergeticsField.GIBBS );
        this.enthalpyFragment = EnergeticsGraphFragment.newInstance( this.energetics, EnergeticsField.ENTHALPY );
        this.entropyFragment = EnergeticsGraphFragment.newInstance( this.energetics, EnergeticsField.ENTROPY );
*/

    }

    private void initTabs(){
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.nist_purple)));

        // Specify that tabs should be displayed in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create a tab listener that is called when the user changes tabs.
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                if( graph != null ) {
                    graph.setMode(fieldsOrder[tab.getPosition()]);
                }
            }
            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            }
            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            }
        };

        // Add 3 tabs, specifying the tab's text and TabListener
        String[] tabNames = {
            EnergeticsField.GIBBS.getShortname(),
            EnergeticsField.ENTHALPY.getShortname(),
            EnergeticsField.ENTROPY.getShortname()
        };
        for (int i = 0; i < 3; i++) {
            TextViewPlus tvp = new TextViewPlus(this);
            tvp.setText(tabNames[i]);
            tvp.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            tvp.setGravity(Gravity.CENTER);
            tvp.setTextColor(Color.WHITE);
            actionBar.addTab(
                    actionBar.newTab()
                            .setCustomView(tvp)
                            .setTabListener(tabListener));
        }
    }

/*
    private void initSwipes(){
        FragmentPagerAdapter mDemoCollectionPagerAdapter = new FragmentPagerAdapter(this.getSupportFragmentManager()){
            @Override
            public int getCount() {
                return 3;
            }
            @Override
            public Fragment getItem(int position) {
                switch(position){
                    case 0:
                        return gibbsFragment;
                    case 1:
                        return enthalpyFragment;
                    case 2:
                        return entropyFragment;
                    default:
                        throw new IllegalArgumentException("Asked for an out of range fragment");
                }
            }
        };
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mDemoCollectionPagerAdapter);
        mViewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        getSupportActionBar().setSelectedNavigationItem(position);
                    }
                });
    }
*/




    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("set", this.energetics);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_energetics, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
