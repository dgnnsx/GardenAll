package br.com.gardenall;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import br.com.gardenall.activity.PlantaActivity;
import br.com.gardenall.adapter.PlantasAdapter;
import br.com.gardenall.domain.Planta;
import br.com.gardenall.domain.PlantaService;
import br.com.gardenall.provider.SearchableProvider;

/**
 * Created by diego on 09/10/16.
 */
public class SearchableActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private ArrayList<Planta> mList;
    private ArrayList<Planta> mListAux;
    private SearchView searchView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Transição
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TransitionInflater inflater = TransitionInflater.from(this);
            Transition transition = inflater.inflateTransition(R.transition.transitions);
            getWindow().setSharedElementExitTransition(transition);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(savedInstanceState != null){
            mList = savedInstanceState.getParcelableArrayList("mList");
            mListAux = savedInstanceState.getParcelableArrayList("mListAux");
        }
        else{
            try {
                mList = PlantaService.getCatalogoDePlantas(this);
                mListAux = new ArrayList<>();
            }
            catch (IOException e) {
                Toast.makeText(getBaseContext(), "Ocorreu algum erro ao realizar a pesquisa.", Toast.LENGTH_SHORT).show();
            }
        }

        textView = (TextView) findViewById(R.id.textSearch);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setAdapter(new PlantasAdapter(this, mListAux, onClickPlanta(), PlantasApplication.INDEX_OF_TAB));

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    public void handleIntent(Intent intent) {
        if(Intent.ACTION_SEARCH.equalsIgnoreCase(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            toolbar.setTitle(query);
            searchPlantas(query);

            SearchRecentSuggestions searchRecentSuggestions = new SearchRecentSuggestions(this,
                    SearchableProvider.AUTHORITY,
                    SearchableProvider.MODE);
            searchRecentSuggestions.saveRecentQuery(query, null);
        }
    }

    public void searchPlantas(String q){
        mListAux.clear();

        for(int i = 0, tamI = mList.size(); i < tamI; i++){
            if(mList.get(i).getNomePlanta().toLowerCase().contains(q.toLowerCase())){
                mListAux.add(mList.get(i));
            }
        }
        for(int i = 0, tamI = mList.size(); i < tamI; i++){
            if(!mListAux.contains(mList.get(i))
                    && mList.get(i).getNomePlanta().toLowerCase().contains(q.toLowerCase())){
                mListAux.add(mList.get(i));
            }
        }

        mRecyclerView.setVisibility(mListAux.isEmpty() ? View.GONE : View.VISIBLE);
        textView.setVisibility(mListAux.isEmpty() ? View.VISIBLE : View.GONE);
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("mList", mList);
        outState.putParcelableArrayList("mListAux", mListAux);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_searchable, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem item = menu.findItem(R.id.action_searchable);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            searchView = (SearchView) item.getActionView();
        }
        else{
            searchView = (SearchView) MenuItemCompat.getActionView(item);
        }

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            finish();
        }
        else if(id == R.id.action_delete){
            SearchRecentSuggestions searchRecentSuggestions = new SearchRecentSuggestions(this,
                    SearchableProvider.AUTHORITY,
                    SearchableProvider.MODE);
            searchRecentSuggestions.clearHistory();
            Toast.makeText(this, "Pesquisas recentes removidas", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private PlantasAdapter.PlantaOnClickListener onClickPlanta(){
        return new PlantasAdapter.PlantaOnClickListener() {
            @Override
            public void onClickPlanta(View view, int idx) {
                Planta p = mListAux.get(idx);
                Intent intent = new Intent(SearchableActivity.this, PlantaActivity.class);
                intent.putExtra("planta", p);
                startActivity(intent);
            }

            @Override
            public void onLongCLickPlanta(View view, int idx) {
                Planta p = mListAux.get(idx);
                Intent intent = new Intent(SearchableActivity.this, PlantaActivity.class);
                intent.putExtra("planta", p);
                startActivity(intent);
            }
        };
    }
}