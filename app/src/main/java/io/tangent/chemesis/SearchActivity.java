package io.tangent.chemesis;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import io.tangent.chemesis.models.Chemical;
import io.tangent.chemesis.views.ChemicalArrayAdapter;
import io.tangent.chemesis.views.ReactionChemicalArrayAdapter;


public class SearchActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    private ChemicalArrayAdapter adapter;
    private Chemical choice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        this.adapter = new ChemicalArrayAdapter(this, new ArrayList<Chemical>());
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.nist_purple)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public void onPostCreate(Bundle savedState) {
        super.onPostCreate(savedState);
        EditText searchInput = (EditText)findViewById(R.id.searchInput);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString();
                if (query.length() > 2) {
                    List<Chemical> chemicals = Chemical.find(query);
                    adapter.setObjects(chemicals);
                }
            }
        });
        ListView searchList = (ListView)findViewById(R.id.searchResults);
        searchList.setAdapter(this.adapter);
        searchList.setOnItemClickListener(this);
    }

    public void finish(){
        Intent responseData = new Intent();
        if( this.choice != null ) {
            responseData.putExtra("chemical", this.choice.name());
        }
        this.setResult(Activity.RESULT_OK, responseData);
        super.finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.choice = this.adapter.getObjects().get(position);
        this.finish();
    }
}
