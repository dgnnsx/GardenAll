package br.com.gardenall.fragment;

/**
 * Created by diego on 26/08/16.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import br.com.gardenall.Callback.CallbackPlantas;
import br.com.gardenall.PlantasApplication;
import br.com.gardenall.R;
import br.com.gardenall.activity.PlantaActivity;
import br.com.gardenall.adapter.PlantasAdapter;
import br.com.gardenall.domain.Planta;
import br.com.gardenall.domain.PlantaDB;
import br.com.gardenall.domain.PlantaService;
import br.com.gardenall.domain.SQLiteHandler;
import br.com.gardenall.utils.NetworkUtils;

import static com.facebook.FacebookSdk.getApplicationContext;

public class PlantasFragment extends Fragment {
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected RecyclerView mRecyclerView;
    protected TextView textView;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<Planta> plantas, plantasAux;
    private int tabIdentifier;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            this.tabIdentifier = getArguments().getInt("tab");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        taskPlantas(false);
    }

    @Override
    public void onPause() {
        PlantasApplication.finishActionMode();
        super.onPause();
    }

    @Override
    public void onResume() {
        mRecyclerView.getAdapter().notifyDataSetChanged();
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plantas, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // Linear Layout Manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        // Swipe Refresh Layout
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(onRefreshListener(view));
        mSwipeRefreshLayout.setColorSchemeResources(R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3);

        /* Interface para receber o resultado do FAB imediatamente e atualizar a lista de plantas */
        CallbackPlantas.setFragmentRefreshListener(new CallbackPlantas.FragmentRefreshListener() {
            @Override
            public void onRefresh(Planta p) {
                plantas.add(p);
                Collections.sort(plantas, new Comparator<Planta>() {
                    @Override
                    public int compare(Planta p1, Planta p2) {
                        return p1.getNomePlanta().compareTo(p2.getNomePlanta());
                    }
                });
                mRecyclerView.getAdapter().notifyDataSetChanged();
            }
        });
        return view;
    }

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener(final View view) {
        return new SwipeRefreshLayout.OnRefreshListener() {
            // Atualiza ao fazer o gesto Pull to Refresh
            @Override
            public void onRefresh() {
                if(PlantasApplication.ACTION_MODE == null) { // Se o fragment não estiver no Action Mode
                    // Valida se existe conexão ao fazer o gesto Pull to Refresh
                    //if(NetworkUtils.isNetworkAvailable(getContext())){
                    taskPlantas(true);
                    mSwipeRefreshLayout.setRefreshing(false);
                    /*} else{
                        mSwipeRefreshLayout.setRefreshing(false);
                        android.support.design.widget.Snackbar.make(view, R.string.error_conexao_indisponivel,
                                Snackbar.LENGTH_LONG)
                                .setAction("Ok", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent it = new Intent(Settings.ACTION_WIFI_SETTINGS);
                                        startActivity(it);
                                    }
                                })
                                .setActionTextColor(getContext().getResources().getColor(R.color.colorLink))
                                .show();
                    }*/
                } else {
                    // Action Mode ativado, não faz o refresh
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        };
    }

    private void taskPlantas(boolean refresh){
        // Busca as plantas
        try {
            if(tabIdentifier == 0)
                this.plantas = PlantaService.getPlantas(getContext(), refresh);
            else {
                this.plantas = PlantaService.getFavorites(getContext());
            }
            // Atualiza a lista
            mRecyclerView.setAdapter(new PlantasAdapter(getContext(), plantas, onClickPlanta(), tabIdentifier));
        }
        catch (IOException e) {
            Toast.makeText(getContext(), "Erro ao ler dados.", Toast.LENGTH_SHORT).show();
        }
    }

    private PlantasAdapter.PlantaOnClickListener onClickPlanta(){
        return new PlantasAdapter.PlantaOnClickListener(){
            @Override
            public void onClickPlanta(View view, int idx){
                Planta planta = plantas.get(idx);
                if(tabIdentifier == 0) {
                    if(PlantasApplication.ACTION_MODE == null) {
                        Intent intent = new Intent(getContext(), PlantaActivity.class);
                        intent.putExtra("planta", planta);
                        startActivity(intent);
                    }
                    else { // Se a CAB estiver ativada
                        // Seleciona a planta
                        planta.invertSelected();
                        // Atualiza o título com a quantidade de plantas selecionadas
                        updateActionModeTitle();
                        // Redesenha a lista
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                    }
                } else {
                    Intent intent = new Intent(getContext(), PlantaActivity.class);
                    intent.putExtra("planta", planta);
                    startActivity(intent);
                }
            }

            @Override
            public void onLongCLickPlanta(View view, int idx) {
                Planta planta = plantas.get(idx);
                if(tabIdentifier == 0) {
                    if (PlantasApplication.ACTION_MODE != null) { // Se a CAB estiver ativada
                        // Seleciona a planta
                        planta.invertSelected();
                        // Atualiza o título com a quantidade de plantas selecionadas
                        updateActionModeTitle();
                        // Redesenha a lista
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                        return;
                    }

                    // Liga a action bar de contexto (CAB)
                    PlantasApplication.ACTION_MODE = getActivity().startActionMode(getActionModeCallback());
                    planta.setSelected(1); // Seleciona a planta
                    // Solicita ao Android para desenhar na lista novamente
                    mRecyclerView.getAdapter().notifyDataSetChanged();
                    // Atualiza o título para mostrar a quantidade de plantas selecionadas
                    updateActionModeTitle();
                } else {
                    Intent intent = new Intent(getContext(), PlantaActivity.class);
                    intent.putExtra("planta", planta);
                    startActivity(intent);
                }
            }
        };
    }

    private android.view.ActionMode.Callback getActionModeCallback(){
        return new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Infla o menu específico da action bar de contexto (CAB)
                MenuInflater inflater = getActivity().getMenuInflater();
                inflater.inflate(R.menu.menu_contextual_action_bar, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                ArrayList<Planta> selectedPlantas = getSelectedPlantas();
                SQLiteHandler db2 = new SQLiteHandler(getApplicationContext());
                HashMap<String,String> user;
                user = db2.getUserDetails();
                if(item.getItemId() == R.id.action_remove){
                    PlantaDB db = new PlantaDB(getContext());
                    try{
                        if (NetworkUtils.isNetworkAvailable(getApplicationContext())) { /* Internet disponivel */
                            for(Planta p : selectedPlantas) {
                                PlantaService.deletePlantaWeb(p.getId(), user.get("email"));
                                db.delete(p); // Deleta a planta do banco
                                plantas.remove(p); // remove da lista
                            }
                            if(selectedPlantas.size() == 1) {
                                Snackbar.make(mRecyclerView, "Planta excluída com sucesso", Snackbar.LENGTH_LONG)
                                        .setAction("Ok", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {}
                                        })
                                        .setActionTextColor(getContext().getResources().getColor(R.color.colorLink))
                                        .show();
                            } else if(selectedPlantas.size() > 1) {
                                Snackbar.make(mRecyclerView, "Plantas excluídas com sucesso", Snackbar.LENGTH_LONG)
                                        .setAction("Ok", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {}
                                        })
                                        .setActionTextColor(getContext().getResources().getColor(R.color.colorLink))
                                        .show();
                            }
                        } else { /* Internet indisponivel */
                            android.support.design.widget.Snackbar.make(mSwipeRefreshLayout,
                                    R.string.error_conexao_indisponivel,
                                    Snackbar.LENGTH_LONG)
                                    .setAction(R.string.ok, onClickSnackBar())
                                    .setActionTextColor(getContext().getResources().getColor(R.color.colorLink))
                                    .show();
                        }
                    } finally {
                        db.close();
                    }
                }
                // Encerra o action mode
                mRecyclerView.getAdapter().notifyDataSetChanged();
                mode.finish();
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // Limpa o estado
                PlantasApplication.ACTION_MODE = null;
                // Configura todas as plantas para não selecionadas
                for(Planta p : plantas){
                    p.setSelected(0);
                }
                mRecyclerView.getAdapter().notifyDataSetChanged();
            }
        };
    }

    // Atualiza o título da action bar (CAB)
    private void updateActionModeTitle() {
        if(PlantasApplication.ACTION_MODE != null) {
            PlantasApplication.ACTION_MODE.setTitle("Selecione as plantas");
            PlantasApplication.ACTION_MODE.setSubtitle(null);
            ArrayList<Planta> selectedPlantas = getSelectedPlantas();
            if(selectedPlantas.size() == 1) {
                PlantasApplication.ACTION_MODE.setSubtitle("1 planta selecionada");
            } else if(selectedPlantas.size() > 1) {
                PlantasApplication.ACTION_MODE.setSubtitle(selectedPlantas.size() + " plantas selecionadas");
            } else {
                PlantasApplication.finishActionMode();
            }
        }
    }

    // Retorna a lista de plantas selecionadas
    private ArrayList<Planta> getSelectedPlantas() {
        ArrayList<Planta> list = new ArrayList<>();
        for(Planta p : plantas) {
            if(p.getSelected() == 1) {
                list.add(p);
            }
        }
        return list;
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
