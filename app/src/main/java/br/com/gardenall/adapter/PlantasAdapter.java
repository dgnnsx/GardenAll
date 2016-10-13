package br.com.gardenall.adapter;

/**
 * Created by diego on 29/08/16.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import br.com.gardenall.R;
import br.com.gardenall.domain.Planta;
import br.com.gardenall.domain.PlantaDB;

public class PlantasAdapter extends RecyclerView.Adapter<PlantasAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<Planta> plantas;
    private PlantaOnClickListener plantaOnClickListener;
    private int tabIdentifier;

    public PlantasAdapter(Context context, ArrayList<Planta> plantas, PlantaOnClickListener plantaOnClickListener, int tabIdentifier){
        this.context = context;
        this.plantas = plantas;
        this.plantaOnClickListener = plantaOnClickListener;
        this.tabIdentifier = tabIdentifier;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_planta, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // Atualiza a view
        final Planta planta = plantas.get(position);
        holder.nomePlanta.setText(planta.getNomePlanta());
        holder.progress.setVisibility(View.VISIBLE);

        if(tabIdentifier == 0)
            holder.favorite.setVisibility(View.GONE);
        else {
            holder.favorite.setVisibility(View.VISIBLE);
            holder.favorite.setFavorite(true);
        }

        if(position == (plantas.size() - 1))
            holder.separador.setVisibility(View.GONE);
        else
            holder.separador.setVisibility(View.VISIBLE);

        // Faz o download da foto e mostra o ProgressBar
        Picasso.with(context).load(planta.getUrlImagem()).noFade().into(holder.image,
                new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        holder.progress.setVisibility(View.GONE); // Download Ok
                    }

                    @Override
                    public void onError() {
                        holder.progress.setVisibility(View.GONE); // Download failed
                    }
                });

        // Click
        if(plantaOnClickListener != null){
            // Click normal
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    plantaOnClickListener.onClickPlanta(holder.itemView, position);
                }
            });

            // Click longo
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    plantaOnClickListener.onLongCLickPlanta(holder.itemView, position);
                    return true;
                }
            });

            // Pinta o fundo de verde se a linha estiver selecionada
            int corFundo = context.getResources().getColor(planta.getSelected() == 1
                    ? R.color.colorCAB : R.color.white);
            holder.itemView.setBackgroundColor(corFundo);
            // A cor do texto é branca ou verde, depende da cor do fundo.
            int corFonte = context.getResources().getColor(planta.getSelected() == 1
                    ? R.color.white : R.color.black);
            holder.nomePlanta.setTextColor(corFonte);

            holder.favorite.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
                @Override
                public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                    PlantaDB db = new PlantaDB(context);
                    if(favorite) {
                        planta.setFavorito(1);
                        db.updateFavorito(planta);
                    } else {
                        planta.setFavorito(0);
                        db.updateFavorito(planta);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return this.plantas != null ? this.plantas.size() : 0;
    }

    public interface PlantaOnClickListener{
        public void onClickPlanta(View view, int idx);
        public void onLongCLickPlanta(View view, int idx);
    }

    // ViewHolder com as views
    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView nomePlanta;
        ImageView image;
        ProgressBar progress;
        View separador;
        MaterialFavoriteButton favorite;

        public ViewHolder(View view){
            super(view);
            nomePlanta = (TextView) view.findViewById(R.id.text1);
            image = (ImageView) view.findViewById(R.id.image);
            progress = (ProgressBar) view.findViewById(R.id.progress);
            separador = view.findViewById(R.id.view);
            favorite = (MaterialFavoriteButton) view.findViewById(R.id.favorite);
        }
    }
}