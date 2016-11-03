package br.com.gardenall.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;

import br.com.gardenall.PlantasApplication;
import br.com.gardenall.R;
import br.com.gardenall.adapter.TabsAdapter;
import br.com.gardenall.domain.Atividade;
import br.com.gardenall.domain.AtividadeDB;
import br.com.gardenall.domain.AtividadeService;
import br.com.gardenall.provider.SearchableProvider;
import br.com.gardenall.utils.Prefs;
import br.com.gardenall.domain.SQLiteHandler;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ViewPager viewPager;
    private FloatingActionButton fab;
    private TabsAdapter adapter;
    private View mHeaderView;
    private SQLiteHandler db;
    private boolean isValidNome, isValidAtividade;
    private ProfileTracker mProfileTracker;
    private Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean login = Prefs.getBoolean(this, "login");
        if(login == false) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.activity_main);

        setupFacebookSdk();
        setUpToolbar();
        setupNavDrawer();
        setupSession();

        // Cor de fundo da barra de status
        drawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);

        // ViewPager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        if(viewPager != null){
            setupViewPager();
        }

        // Tabs
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        if(viewPager != null && tabLayout != null) {
            tabLayout.setupWithViewPager(viewPager);
        }

        // ViewPagerChangeListener
        setupViewPagerListener();

        // Floating Action Button
        fab = (FloatingActionButton) findViewById(R.id.fab);

        // Seleciona a tab salva pelo ViewPager
        int tabIdx = Prefs.getInteger(this, "tabIdx");
        // Variável global
        PlantasApplication.INDEX_OF_TAB = tabIdx;
        setupFABOnClickListener();
        viewPager.setCurrentItem(tabIdx);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
    }

    private void setupFacebookSdk() {
        FacebookSdk.sdkInitialize(getApplicationContext());
    }

    private void setUpToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    private void setupNavDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,                               /* host Activity */
                drawerLayout,                       /* DrawerLayout object */
                toolbar,                            /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,    /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close    /* "close drawer" description for accessibility */

        ){
            @Override
            public void onDrawerStateChanged(int newState) {
                PlantasApplication.finishActionMode();
                super.onDrawerStateChanged(newState);
            }
        };
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        mHeaderView = navigationView.getHeaderView(0);
    }

    private void setupSession() {
        if(AccessToken.getCurrentAccessToken() != null) {
            /* Login feito via Facebook */
            if(Profile.getCurrentProfile() == null) {
                mProfileTracker = new ProfileTracker() {
                    @Override
                    protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                        mProfileTracker.stopTracking();
                    }
                };
            } else {
                profile = Profile.getCurrentProfile();
                ImageView img = (ImageView) mHeaderView.findViewById(R.id.app_image);
                Picasso.with(this)
                        .load("https://graph.facebook.com/" + profile.getId() + "/picture?type=large")
                        .into(img);
                TextView nome = (TextView) mHeaderView.findViewById(R.id.text_nav_name);
                nome.setText(profile.getName());
                TextView email = (TextView) mHeaderView.findViewById(R.id.text_nav_city);
                String emailFb = Prefs.getString(getBaseContext(), "facebook_login");
                email.setText(emailFb);
            }
        } else {
            /* Login via conta GardenAll */
            /* faz nada */
        }
    }

    private void setupViewPager(){
        adapter = new TabsAdapter(getSupportFragmentManager());
        adapter.addFragment(new Fragment(), "Plantas");
        adapter.addFragment(new Fragment(), "Atividades");
        adapter.addFragment(new Fragment(), "Favoritos");
        viewPager.setAdapter(adapter);
    }

    private void setupFABOnClickListener() {
        int position = PlantasApplication.INDEX_OF_TAB;
        switch (position) {
            case 0:
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getBaseContext(), CatalogoActivity.class);
                        startActivity(intent);
                    }
                });
                break;

            case 1:
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addAtividade();
                    }
                });
                break;

            default:
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Do nothing
                    }
                });
                break;
        }
    }

    private void setupFABAction() {
        int position = PlantasApplication.INDEX_OF_TAB;
        switch (position) {
            case 0:
                //fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                fab.show();
                break;

            case 1:
                //fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorLink)));
                fab.show();
                break;

            default:
                fab.hide();
                break;
        }
    }

    private void setupViewPagerListener(){
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                if(position != 0) {
                    PlantasApplication.finishActionMode();
                }
                // Salva o índice da página/tab selecionada
                Prefs.setInteger(getBaseContext(), "tabIdx", position);
                PlantasApplication.INDEX_OF_TAB = position;
                setupFABOnClickListener();
                setupFABAction();
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_plantas, menu);
        SearchView searchView;
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            searchView = (SearchView) menu.findItem(R.id.action_searchable).getActionView();
        }
        else{
            MenuItem item = menu.findItem(R.id.action_searchable);
            searchView = (SearchView) MenuItemCompat.getActionView(item);
        }

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_disconnect) {
            disconnect();
            return true;
        } else if(id == R.id.action_delete){
            SearchRecentSuggestions searchRecentSuggestions = new SearchRecentSuggestions(this,
                    SearchableProvider.AUTHORITY,
                    SearchableProvider.MODE);
            searchRecentSuggestions.clearHistory();
            Toast.makeText(this, "Pesquisas recentes removidas", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.opcao_1) {
            Toast.makeText(this, "Opção 1", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.opcao_2) {
            Toast.makeText(this, "Opção 2", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.opcao_3) {
            Toast.makeText(this, "Opção 3", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void disconnect(){
        AlertDialog.Builder builder = new AlertDialog.Builder(
                new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        builder.setTitle(R.string.action_exit);
        builder.setMessage(R.string.action_sure_disconnect);

        builder.setNegativeButton(R.string.negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                return;
            }
        });
        builder.setPositiveButton(R.string.positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finishFacebookSession();
                finishSession();
                return;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void finishFacebookSession() {
        if(AccessToken.getCurrentAccessToken() != null) {
            new GraphRequest(AccessToken.getCurrentAccessToken(),
                    "/me/permissions/",
                    null,
                    HttpMethod.DELETE,
                    new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse graphResponse) {
                            LoginManager.getInstance().logOut();
                            return;
                        }
                    }).executeAsync();
        }
    }

    private void finishSession() {
        Prefs.clearPreferences(getBaseContext());
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void addAtividade() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setTitle("Adicionar atividade");
        builder.setIcon(R.drawable.ic_alarm_add_black);
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.dialog_add_atividade, null);
        //aqui o conteudo
        final Button button = (Button) layout.findViewById(R.id.botao_Add);
        final TextInputLayout nomeAtividadeLayout = (TextInputLayout) layout.findViewById(R.id.nomeAtividadeLayout);
        final TextInputLayout descAtividadeLayout = (TextInputLayout) layout.findViewById(R.id.descAtividadeLayout);
        final EditText descAtividade = (EditText) layout.findViewById(R.id.descAtividade);
        final EditText nomeAtividade = (EditText) layout.findViewById(R.id.nomeAtividade);
        builder.setView(layout);
        final AlertDialog dialog = builder.create();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nomeAtividade.length() == 0) {
                    isValidNome = false;
                    nomeAtividadeLayout.setError("Dê um nome para identificar sua atividade");
                } else {
                    isValidNome = true;
                    nomeAtividadeLayout.setError(null);
                }

                if (descAtividade.length() == 0) {
                    isValidAtividade = false;
                    descAtividadeLayout.setError("Digite uma breve descrição da atividade");
                } else {
                    isValidAtividade = true;
                    descAtividadeLayout.setError(null);
                }

                if(isValidNome && isValidAtividade) {
                    Atividade at = new Atividade();
                    at.setTitulo(nomeAtividade.getText().toString());
                    at.setDescricao(descAtividade.getText().toString());
                    AtividadeDB db = new AtividadeDB(getBaseContext());
                    Atividade at2 = db.findByNome(at.getTitulo());
                    if(at2 == null) {
                        AtividadeService.saveAtividade(getBaseContext(), at);
                    } else {
                        Toast.makeText(getBaseContext(), "Atividade já salva na lista do usuário!", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }
}