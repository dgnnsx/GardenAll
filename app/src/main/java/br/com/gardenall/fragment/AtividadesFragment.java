package br.com.gardenall.fragment;

/**
 * Created by diego on 29/08/16.
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import br.com.gardenall.R;
import br.com.gardenall.adapter.AtividadesAdapter;
import br.com.gardenall.domain.Atividade;
import br.com.gardenall.domain.AtividadeService;
import br.com.gardenall.extra.AlarmReceiver;
import br.com.gardenall.utils.AlarmUtil;

public class AtividadesFragment extends Fragment
        implements DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener,
        DialogInterface.OnCancelListener {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private static RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<Atividade> atividades;
    private Atividade atividade;
    private int year, month, day, hour, minute;
    private boolean isProgrammed;
    TextView textView;
    Button btn;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        taskAtividades(false);
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
                taskAtividades(true);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        };
    }

    private void taskAtividades(boolean refresh){
        // Busca as atividades
        try {
            this.atividades = AtividadeService.getAtividades(getContext(), refresh);
        } catch (IOException e) {
            Toast.makeText(getContext(), "Erro ao ler dados.", Toast.LENGTH_SHORT).show();
        }
        // Atualiza a lista
        mRecyclerView.setAdapter(new AtividadesAdapter(getContext(), atividades, onClickAtividade()));
    }

    // Data/Tempo para agendar o alarme
    public long getTime() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.add(Calendar.HOUR, hour - (c.get(Calendar.HOUR_OF_DAY)));
        c.add(Calendar.MINUTE, minute - (c.get(Calendar.MINUTE)));
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
            public void onClickBtnLeft(Button buttonL, Button buttonR, int idx, TextView horario) {
                atividade = atividades.get(idx);
                textView = horario;
                btn = buttonR;
                schedule();
            }

            @Override
            public void onClickBtnRight(Button button, int idx) {
                if(isProgrammed) {
                    textView.setText(
                            (hour < 10 ? "0"+hour : hour)+"h"+
                            (minute < 10 ? "0"+minute : minute));
                    updateAtividade(
                            (hour < 10 ? "0"+hour : hour)+"h"+
                                    (minute < 10 ? "0"+minute : minute));
                }
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

    private void schedule(){
        initData();
        Calendar cDefault = Calendar.getInstance();
        cDefault.set(year, month, day);

        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                this,
                cDefault.get(Calendar.YEAR),
                cDefault.get(Calendar.MONTH),
                cDefault.get(Calendar.DAY_OF_MONTH)
        );

        Calendar cMin = Calendar.getInstance();
        Calendar cMax = Calendar.getInstance();
        cMax.set( cMax.get(Calendar.YEAR) , 11, 31);

        datePickerDialog.setMinDate(cMin);
        datePickerDialog.setMaxDate(cMax);

        datePickerDialog.setOnCancelListener(this);
        datePickerDialog.show(getActivity().getFragmentManager(), "DatePickerDialog");
    }

    private void initData(){
        if(year == 0){
            Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        year = month = day = hour = minute = 0;
        isProgrammed = false;
        btn.setVisibility(View.INVISIBLE);
        textView.setText("");
        updateAtividade("");
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int i, int i1, int i2) {
        Calendar tDefault = Calendar.getInstance();
        tDefault.set(year, month, day, hour, minute);

        year = i;
        month = i1; // BASE 0
        day = i2;

        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                this,
                tDefault.get(Calendar.HOUR_OF_DAY),
                tDefault.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.setOnCancelListener(this);
        timePickerDialog.show(getActivity().getFragmentManager(), "timePickerDialog");
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute2, int second) {
        hour = hourOfDay;
        minute = minute2;
        isProgrammed = true;

        textView.setText( "Confirmar programação para " +(day < 10 ? "0"+day : day)+"/"+
                (month+1 < 10 ? "0"+(month+1) : month+1)+"/"+
                year + " às " +
                (hour < 10 ? "0"+hour : hour)+"h"+
                (minute < 10 ? "0"+minute : minute) +"?");
        btn.setVisibility(View.VISIBLE);
        updateAtividade("Confirmar programação para " +(day < 10 ? "0"+day : day)+"/"+
                (month+1 < 10 ? "0"+(month+1) : month+1)+"/"+
                year + " às " +
                (hour < 10 ? "0"+hour : hour)+"h"+
                (minute < 10 ? "0"+minute : minute) +"?");
    }

    private void updateAtividade(String s) {
        atividade.setHorario(s);
        AtividadeService.updateAtividade(getContext(), atividade);
    }
}