package br.com.gardenall.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import br.com.gardenall.PlantasApplication;
import br.com.gardenall.R;
import br.com.gardenall.adapter.TabsAdapter;
import br.com.gardenall.domain.SQLiteHandler;
import br.com.gardenall.domain.SessionManager;
import br.com.gardenall.utils.Prefs;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ViewPager viewPager;
    private FloatingActionButton fab;
    private TabsAdapter adapter;
    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            disconnect();
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
                        Toast.makeText(getBaseContext(), "CLifdgfdgk", Toast.LENGTH_SHORT).show();
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
                fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                fab.show();
                break;

            case 1:
                fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorLink)));
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
            disconnect();
            return true;
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

    private void disconnect(){
        AlertDialog.Builder builder = new AlertDialog.Builder(
                new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        builder.setIcon(R.drawable.ic_menu_manage);
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
                Prefs.setBoolean(getBaseContext(), "login", false);
                session.setLogin(false);
                db.deleteUsers();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
