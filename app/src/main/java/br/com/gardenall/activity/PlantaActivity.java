package br.com.gardenall.activity;

/**
 * Created by diego on 05/09/16.
 */

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import br.com.gardenall.R;
import br.com.gardenall.domain.AppController;
import br.com.gardenall.domain.Planta;
import br.com.gardenall.domain.PlantaDB;
import br.com.gardenall.domain.PlantaService;
import br.com.gardenall.domain.SQLiteHandler;
import br.com.gardenall.utils.NetworkUtils;

import static br.com.gardenall.Callback.CallbackPlantas.getFragmentRefreshListener;

public class PlantaActivity extends AppCompatActivity {
    private boolean isUsingTransition = false;
    private FloatingActionButton fab;
    private ImageView image;
    private Planta planta, p;
    private MaterialFavoriteButton favorite;
    private HashMap<String,String> user;
    private SQLiteHandler db2;

    private TextView colheitaMin;
    private TextView epocaSul;
    private TextView epocaSudeste;
    private TextView epocaCentroOeste;
    private TextView epocaNorte;
    private TextView epocaNordeste;
    private TextView sol;
    private TextView regar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Transição
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TransitionInflater inflater = TransitionInflater.from(this);
            Transition transition = inflater.inflateTransition(R.transition.transitions);
            getWindow().setSharedElementEnterTransition(transition);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planta);

        db2 = new SQLiteHandler(getApplicationContext());

        if(savedInstanceState != null) {
            planta = savedInstanceState.getParcelable("planta");
        }
        else {
            if(getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().getParcelable("planta") != null) {
                planta = getIntent().getExtras().getParcelable("planta");
            }
            else {
                Toast.makeText(this, "Fail!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(planta.getNomePlanta());

        image = (ImageView) findViewById(R.id.backdrop);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        favorite = (MaterialFavoriteButton) findViewById(R.id.favorite);
        fab.setOnClickListener(onClickFab());
        PlantaDB db = new PlantaDB(getBaseContext());
        p = db.findByNomeOnCatalogo(planta.getNomePlanta());
        if(p.getFavorito() == 1)
            favorite.setFavorite(true);
        else
            favorite.setFavorite(false);
        favorite.setOnFavoriteChangeListener(onFavoriteChange());
        setInfo();
        loadItem();
    }

    private void setInfo() {
        colheitaMin = (TextView) findViewById(R.id.colheitaMin);
        epocaSul = (TextView) findViewById(R.id.epocaSul);
        epocaSudeste = (TextView) findViewById(R.id.epocaSudeste);
        epocaCentroOeste = (TextView) findViewById(R.id.epocaCentroOeste);
        epocaNorte = (TextView) findViewById(R.id.epocaNorte);
        epocaNordeste = (TextView) findViewById(R.id.epocaNordeste);
        sol = (TextView) findViewById(R.id.sol);
        regar = (TextView) findViewById(R.id.regar);
        colheitaMin.setText("Tempo mínimo para colheita: " + planta.getColheitaMin() + " dias");
        epocaSul.setText("Época região Sul: " + planta.getEpocaSul());
        epocaSudeste.setText("Época região Sudeste: " + planta.getEpocaSudeste());
        epocaCentroOeste.setText("Época região Centro Oeste: " + planta.getEpocaCentroOeste());
        epocaNordeste.setText("Época região Nordeste: " + planta.getEpocaNordeste());
        epocaNorte.setText("Época região Norte: " + planta.getEpocaNorte());
        sol.setText("Exposição ao Sol: " + planta.getSol());
        regar.setText("Como regar: " + planta.getRegar());
    }

    private MaterialFavoriteButton.OnFavoriteChangeListener onFavoriteChange() {
        return new MaterialFavoriteButton.OnFavoriteChangeListener() {
            @Override
            public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                PlantaDB db = new PlantaDB(getBaseContext());
                if(favorite) {
                    planta.setFavorito(1);
                    db.updateFavorito(planta);
                } else {
                    planta.setFavorito(0);
                    db.updateFavorito(planta);
                }
            }
        };
    }

    private FloatingActionButton.OnClickListener onClickFab() {
        return new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlantaDB db = new PlantaDB(getBaseContext());
                p = db.findByNome(planta.getNomePlanta());
                user = db2.getUserDetails();
                if(p == null) {
                    if (NetworkUtils.isNetworkAvailable(getApplicationContext())) { /* Internet disponivel */
                        if(getFragmentRefreshListener() != null) {
                            getFragmentRefreshListener().onRefresh(planta);
                        }
                        PlantaService.savePlanta(getBaseContext(), planta);
                        PlantaService.savePlantaWeb(planta.getId(), user.get("email"));
                        Toast.makeText(getBaseContext(), "Planta salva com sucesso!", Toast.LENGTH_SHORT).show();
                    } else { /* Internet indisponivel */
                        android.support.design.widget.Snackbar.make(findViewById(R.id.planta_content),
                                R.string.error_conexao_indisponivel,
                                Snackbar.LENGTH_LONG)
                                .setAction(R.string.ok, onClickSnackBar())
                                .setActionTextColor(getBaseContext().getResources().getColor(R.color.colorLink))
                                .show();
                    }
                } else {
                    Toast.makeText(getBaseContext(), "Planta já salva na lista do usuário!", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private void loadItem() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && isUsingTransition) {
            loadThumbnail();
        } else {
            loadFullSizeImage();
        }
    }

    private void loadThumbnail() {
        Picasso.with(this)
                .load(AppController.imagens.get(planta.getId() - 1))
                .noFade()
                .into(image);
    }

    private void loadFullSizeImage() {
        Picasso.with(this)
                .load(AppController.imagens.get(planta.getId() - 1))
                .noFade()
                .noPlaceholder()
                .into(image);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("planta", planta);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private View.OnClickListener onClickSnackBar() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* Intent it = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivity(it); */
            }
        };
    }
}
