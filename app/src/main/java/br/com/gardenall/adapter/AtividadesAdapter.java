package br.com.gardenall.adapter;

/**
 * Created by diego on 30/08/16.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import br.com.gardenall.R;
import br.com.gardenall.domain.Atividade;

public class AtividadesAdapter extends RecyclerView.Adapter<AtividadesAdapter.ViewHolder> {
    private final Context context;
    private final List<Atividade> atividades;
    private AtividadeOnClickListener atividadeOnClickListener;

    public AtividadesAdapter(Context context, List<Atividade> atividades, AtividadeOnClickListener atividadeOnClickListener){
        this.context = context;
        this.atividades = atividades;
        this.atividadeOnClickListener = atividadeOnClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_atividade, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // Atualiza a view
        final Atividade atividade = atividades.get(position);
        holder.titulo.setText(atividade.getTitulo());
        holder.descricao.setText(atividade.getDescricao());
        holder.horario.setText(atividade.getHorario());
        holder.switchAlarme.setChecked(atividade.getStatus());

        if(atividadeOnClickListener != null) {
            holder.btnLeft.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    atividadeOnClickListener.onClickBtnLeft(holder.btnLeft, holder.btnRight, position, holder.horario);
                }
            });

            holder.btnRight.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    atividadeOnClickListener.onClickBtnRight(holder.btnRight, position);
                }
            });

            holder.switchAlarme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        atividadeOnClickListener.onSwitchTurnedOn(holder.switchAlarme, position);
                    } else {
                        atividadeOnClickListener.onSwitchTurnedOff(holder.switchAlarme, position);
                    }
                }
            });

            holder.btnExcluir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    atividadeOnClickListener.onClickExcluir(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return this.atividades != null ? this.atividades.size() : 0;
    }

    public interface AtividadeOnClickListener{
        public void onClickBtnLeft(Button buttonL, Button buttonR, int idx, TextView horario);
        public void onClickBtnRight(Button button, int idx);
        public void onSwitchTurnedOn(Switch switcher, int idx);
        public void onSwitchTurnedOff(View view, int idx);
        public void onClickExcluir(int idx);
    }

    // ViewHolder com as views
    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView titulo;
        public TextView descricao;
        public TextView horario;
        Switch switchAlarme;
        Button btnLeft;
        Button btnRight;
        ImageButton btnExcluir;

        public ViewHolder(View view){
            super(view);
            titulo = (TextView) view.findViewById(R.id.text1);
            descricao = (TextView) view.findViewById(R.id.text2);
            horario = (TextView) view.findViewById(R.id.textId);
            btnLeft = (Button) view.findViewById(R.id.btnLeft);
            btnRight = (Button) view.findViewById(R.id.btnRight);
            btnExcluir = (ImageButton) view.findViewById(R.id.btnExcluir);
            switchAlarme = (Switch) view.findViewById(R.id.switchId);
        }
    }
}