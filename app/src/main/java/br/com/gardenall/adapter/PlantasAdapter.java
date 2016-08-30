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

import java.util.List;

import br.com.gardenall.R;
import br.com.gardenall.domain.Planta;

public class PlantasAdapter extends RecyclerView.Adapter<PlantasAdapter.ViewHolder> {
    private final Context context;
    private final List<Planta> plantas;
    private PlantaOnClickListener plantaOnClickListener;
    private int tabIdentifier;

    public PlantasAdapter(Context context, List<Planta> plantas, PlantaOnClickListener plantaOnClickListener, int tabIdentifier){
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
        Planta planta = plantas.get(position);
        holder.nomePlanta.setText(planta.getNomePlanta());
        holder.progress.setVisibility(View.VISIBLE);

        if(tabIdentifier == 0)
            holder.favorite.setVisibility(View.GONE);
        else
            holder.favorite.setVisibility(View.VISIBLE);

        if(position == (plantas.size() - 1))
            holder.separador.setVisibility(View.GONE);
        else
            holder.separador.setVisibility(View.VISIBLE);

        // Faz o download da foto e mostra o ProgressBar
        Picasso.with(context).load(planta.getUrlImagem()).fit().into(holder.image,
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
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    plantaOnClickListener.onClickPlanta(holder.itemView, position);
                }
            });

            holder.favorite.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
                @Override
                public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                    plantaOnClickListener.onClickFavorite(holder.favorite, position);
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
        public void onClickFavorite(View view, int idx);
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