package br.com.gardenall.activity;

/**
 * Created by diego on 04/09/16.
 */

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.provider.SearchRecentSuggestions;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import br.com.gardenall.R;
import br.com.gardenall.adapter.CatalogoAdapter;
import br.com.gardenall.domain.Planta;
import br.com.gardenall.domain.PlantaService;
import br.com.gardenall.provider.SearchableProvider;

public class CatalogoActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private Toolbar toolbar;
    private ArrayList<Planta> plantas;
    private GridView gridView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Transição
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TransitionInflater inflater = TransitionInflater.from(this);
            Transition transition = inflater.inflateTransition(R.transition.transitions);
            getWindow().setSharedElementExitTransition(transition);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogo);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Catalogo");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Configura o GridView e seu adaptador
        gridView = (GridView) findViewById(R.id.gridView);
        gridView.setOnItemClickListener(this);

        // Prepara o conjunto de dados
        taskPlantas();
    }

    private void taskPlantas() {
        // Busca as plantas
        try {
            this.plantas = PlantaService.getCatalogoDePlantas(this);
            // Atualiza a lista
            gridView.setAdapter(new CatalogoAdapter(this, plantas));
            ((CatalogoAdapter) gridView.getAdapter()).notifyDataSetChanged();
        }
        catch (IOException e) {
            Toast.makeText(getBaseContext(), "Erro ao ler dados.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalogo, menu);
        SearchView searchView;
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            searchView = (SearchView) menu.findItem(R.id.action_searchable).getActionView();
        }
        else {
            MenuItem item = menu.findItem(R.id.action_searchable);
            searchView = (SearchView) MenuItemCompat.getActionView(item);
        }

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_delete) {
            SearchRecentSuggestions searchRecentSuggestions = new SearchRecentSuggestions(this,
                    SearchableProvider.AUTHORITY,
                    SearchableProvider.MODE);
            searchRecentSuggestions.clearHistory();
            Toast.makeText(this, "Pesquisas recentes removidas", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Intent intent = new Intent(this, PlantaActivity.class);
        intent.putExtra("planta", plantas.get(position));

        // Transição
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View image = view.findViewById(R.id.imageCatalog);
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this,
                    Pair.create(image, "element1")
            );
            startActivity(intent, options.toBundle());
        }
        else {
            startActivity(intent);
        }
    }
}
