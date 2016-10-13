package br.com.gardenall.fragment;

/**
 * Created by diego on 29/08/16.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

import br.com.gardenall.R;
import br.com.gardenall.adapter.AtividadesAdapter;
import br.com.gardenall.domain.Atividade;
import br.com.gardenall.domain.AtividadeService;
import br.com.gardenall.extra.AlarmReceiver;
import br.com.gardenall.utils.AlarmUtil;

public class AtividadesFragment extends Fragment {
    protected RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private List<Atividade> atividades;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        taskAtividades();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_atividades, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // Linear Layout Manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        return view;
    }

    private void taskAtividades(){
        // Busca as atividades
        this.atividades = AtividadeService.getAtividades(getContext());
        // Atualiza a lista
        mRecyclerView.setAdapter(new AtividadesAdapter(getContext(), atividades, onClickAtividade()));
    }

    // Data/Tempo para agendar o alarme
    public long getTime() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.add(Calendar.SECOND, 5);
        long time = c.getTimeInMillis();
        return time;
    }

    public void agendar(View view) {
        Intent intent = new Intent(AlarmReceiver.ACTION);
        // Agenda para daqui a 5 seg
        AlarmUtil.schedule(getContext(), intent, getTime());
        //sendBroadcast(intent);
        Toast.makeText(getContext(),"Alarme agendado.",Toast.LENGTH_SHORT).show();
    }

    public void onClickAgendarComRepeat(View view) {
        Intent intent = new Intent(AlarmReceiver.ACTION);
        // Agenda para daqui a 5 seg, repete a cada 30 seg
        AlarmUtil.scheduleRepeat(getContext(), intent, getTime(), 30 * 1000);
        Toast.makeText(getContext(),"Alarme agendado com repetir.",Toast.LENGTH_SHORT).show();
    }

    public void cancelar(View view) {
        Intent intent = new Intent(AlarmReceiver.ACTION);
        AlarmUtil.cancel(getContext(),intent);
        Toast.makeText(getContext(),"Alarme cancelado",Toast.LENGTH_SHORT).show();
    }

    private AtividadesAdapter.AtividadeOnClickListener onClickAtividade(){
        return new AtividadesAdapter.AtividadeOnClickListener(){
            @Override
            public void onClickBtnLeft(View view, int idx) {
                Toast.makeText(getContext(), "Left Button", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClickBtnRight(View view, int idx) {
                Toast.makeText(getContext(), "Right Button", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSwitchTurnedOn(View view, int idx) {
                agendar(view);
            }

            @Override
            public void onSwitchTurnedOff(View view, int idx) {
                cancelar(view);
            }
        };
    }
}