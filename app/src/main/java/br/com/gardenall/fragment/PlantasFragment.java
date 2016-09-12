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
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.gardenall.PlantasApplication;
import br.com.gardenall.R;
import br.com.gardenall.adapter.PlantasAdapter;
import br.com.gardenall.domain.Planta;
import br.com.gardenall.domain.PlantaDB;
import br.com.gardenall.domain.PlantaService;

public class PlantasFragment extends Fragment {
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private List<Planta> plantas;
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
            this.plantas = PlantaService.getPlantas(getContext(), refresh);
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
                        Toast.makeText(getContext(), "0 " + planta.getNomePlanta(), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getContext(), "2 " + planta.getNomePlanta(), Toast.LENGTH_SHORT).show();
                    /*
                    Intent intent = new Intent(getContext(), PlantaActivity.class);
                    intent.putExtra("planta", planta);
                    startActivity(intent);
                    */
                }
            }

            @Override
            public void onLongCLickPlanta(View view, int idx) {
                Planta planta = plantas.get(idx);

                if(PlantasApplication.ACTION_MODE != null) { // Se a CAB estiver ativada
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
            }

            @Override
            public void onClickFavorite(View view, int idx) {

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
                List<Planta> selectedPlantas = getSelectedPlantas();
                if(item.getItemId() == R.id.action_remove){
                    PlantaDB db = new PlantaDB(getContext());
                    try{
                        for(Planta p : selectedPlantas){
                            db.delete(p); // Deleta a planta do banco
                            plantas.remove(p); // remove da lista
                        }
                    } finally {
                        db.close();
                    }
                    if(selectedPlantas.size() == 1) {
                        Snackbar.make(mRecyclerView, "Planta excluída com sucesso", Snackbar.LENGTH_LONG)
                                .setAction("Ok", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                })
                                .setActionTextColor(getContext().getResources().getColor(R.color.colorLink))
                                .show();
                    }
                    else if(selectedPlantas.size() > 1) {
                        Snackbar.make(mRecyclerView, "Plantas excluídas com sucesso", Snackbar.LENGTH_LONG)
                                .setAction("Ok", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                })
                                .setActionTextColor(getContext().getResources().getColor(R.color.colorLink))
                                .show();
                    }
                }
                // Encerra o action mode
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
            List<Planta> selectedPlantas = getSelectedPlantas();
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
    private List<Planta> getSelectedPlantas() {
        List<Planta> list = new ArrayList<>();
        for(Planta p : plantas) {
            if(p.getSelected() == 1) {
                list.add(p);
            }
        }
        return list;
    }
}
