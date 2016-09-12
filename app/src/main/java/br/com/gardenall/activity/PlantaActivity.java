package br.com.gardenall.activity;

/**
 * Created by diego on 05/09/16.
 */

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import br.com.gardenall.R;
import br.com.gardenall.domain.Planta;
import br.com.gardenall.domain.PlantaDB;
import br.com.gardenall.domain.PlantaService;

public class PlantaActivity extends AppCompatActivity {
    private boolean isUsingTransition = false;
    private FloatingActionButton fab;
    private ImageView image;
    private Planta planta;

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
        fab.setOnClickListener(onClickFab());
        loadItem();
    }

    private FloatingActionButton.OnClickListener onClickFab() {
        return new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                Planta p;
                PlantaDB db = new PlantaDB(getBaseContext());
                p = db.findByNome(planta.getNomePlanta());
                if(p == null) {
                    PlantaService.savePlanta(getBaseContext(), planta);
                    Toast.makeText(getBaseContext(), "Planta salva com sucesso!", Toast.LENGTH_SHORT).show();
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
                .load(planta.getUrlImagem())
                .noFade()
                .into(image);
    }

    private void loadFullSizeImage() {
        Picasso.with(this)
                .load(planta.getUrlImagem())
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
}
