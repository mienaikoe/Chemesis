package io.tangent.chemesis;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import io.tangent.chemesis.models.Reaction;
import io.tangent.chemesis.models.Energetics;
import io.tangent.chemesis.models.ReactionChemical;
import io.tangent.chemesis.views.EnergeticsGraph;


public class EnergeticsActivity extends ActionBarActivity {

    private Energetics reactantEnergetics;
    private Energetics productEnergetics;
    private Energetics combinedEnergetics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_energetics);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.nist_purple)));

        if( savedInstanceState != null ){
            this.reactantEnergetics = savedInstanceState.getParcelable("reactant");
            this.productEnergetics = savedInstanceState.getParcelable("product");
            this.combinedEnergetics = savedInstanceState.getParcelable("combined");
        } else {
            Reaction reaction = this.getIntent().getParcelableExtra("reaction");
            this.reactantEnergetics = new Energetics(reaction.getReactants(), this);
            this.productEnergetics = new Energetics(reaction.getProducts(), this);

            ArrayList<ReactionChemical> chems = new ArrayList<>(reaction.getReactants().size() + reaction.getProducts().size());
            chems.addAll(reaction.getReactants());
            chems.addAll(reaction.getProducts());
            this.combinedEnergetics = new Energetics(chems, this);
        }

        EnergeticsGraph graph = (EnergeticsGraph)this.findViewById(R.id.energetics_graph);
        graph.setEnergetics(this.reactantEnergetics, this.productEnergetics, this.combinedEnergetics);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("reactant", this.reactantEnergetics);
        outState.putParcelable("product", this.productEnergetics);
        outState.putParcelable("combined", this.combinedEnergetics);
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
}
