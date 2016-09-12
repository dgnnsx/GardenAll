package br.com.gardenall.activity;

/**
 * Created by diego on 04/09/16.
 */

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
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
import java.util.List;

import br.com.gardenall.R;
import br.com.gardenall.adapter.CatalogoAdapter;
import br.com.gardenall.adapter.PlantasAdapter;
import br.com.gardenall.domain.Planta;
import br.com.gardenall.domain.PlantaService;
import br.com.gardenall.utils.NetworkUtils;

public class CatalogoActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Toolbar toolbar;
    private List<Planta> plantas;
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

        // Swipe Refresh Layout
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(onRefreshListener());
        mSwipeRefreshLayout.setColorSchemeResources(R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3);

        // Prepara o conjunto de dados
        taskPlantas(false);
    }

    private void taskPlantas(boolean refresh){
        // Busca as plantas
        try {
            this.plantas = PlantaService.getCatalogoDePlantas(this, refresh);
            // Atualiza a lista
            gridView.setAdapter(new CatalogoAdapter(this, plantas));
            // ((CatalogoAdapter) gridView.getAdapter()).notifyDataSetChanged();
        }
        catch (IOException e) {
            Toast.makeText(getBaseContext(), "Erro ao ler dados.", Toast.LENGTH_SHORT).show();
        }
    }

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener() {
        return new SwipeRefreshLayout.OnRefreshListener() {
            // Atualiza ao fazer o gesto Pull to Refresh
            @Override
            public void onRefresh() {
                // Valida se existe conexão ao fazer o gesto Pull to Refresh
                if(NetworkUtils.isNetworkAvailable(getBaseContext())){
                    taskPlantas(true);
                    mSwipeRefreshLayout.setRefreshing(false);
                } else{
                    mSwipeRefreshLayout.setRefreshing(false);
                    android.support.design.widget.Snackbar.make(gridView, R.string.error_conexao_indisponivel,
                            Snackbar.LENGTH_LONG)
                            .setAction("Ok", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent it = new Intent(Settings.ACTION_WIFI_SETTINGS);
                                    startActivity(it);
                                }
                            })
                            .setActionTextColor(getBaseContext().getResources().getColor(R.color.colorLink))
                            .show();
                }
            }
        };
    }

    /*@Override
    public void onResume(){
        super.onResume();
        if(CarrosApplication.getInstance().isPrecisaAtualizar(this.tipo)){
            // Se teve alterações no banco de dados, vamos atualizar a lista.
            taskCarros(false);
            toast("Lista de carros atualizada!");
        }
    }*/

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_disconnect) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}