package br.com.gardenall.adapter;

/**
 * Created by diego on 04/09/16.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import br.com.gardenall.R;
import br.com.gardenall.domain.AppController;
import br.com.gardenall.domain.Planta;

public class CatalogoAdapter extends BaseAdapter {
    private final Context context;
    private static ArrayList<Planta> plantas;
    private LayoutInflater mLayoutInflater;

    public CatalogoAdapter(Context context, ArrayList<Planta> plantas) {
        this.context = context;
        this.plantas = plantas;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // Função que determina quantos itens serão inflados
    @Override
    public int getCount() {
        return this.plantas != null ? this.plantas.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return plantas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        final Planta planta = plantas.get(position);

        if (view == null) {
            holder = new ViewHolder();
            view = mLayoutInflater.inflate(R.layout.grid_item_catalogo, null);
            holder.nomePlanta = (TextView) view.findViewById(R.id.textCatalog);
            holder.image = (ImageView) view.findViewById(R.id.imageCatalog);
            holder.progress = (ProgressBar) view.findViewById(R.id.progressCatalog);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        // Muda o conteúdo
        if (planta != null) {
            holder.nomePlanta.setText(planta.getNomePlanta());
            holder.progress.setVisibility(View.VISIBLE);

            // Faz o download da foto e mostra o ProgressBar
            Picasso.with(context).load(AppController.imagens.get(planta.getId() - 1)).noFade().into(holder.image,
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
        }
        return view;
    }

    // Classe ViewHolder usada para reusar a view inflada
    private static class ViewHolder {
        TextView nomePlanta;
        ImageView image;
        ProgressBar progress;
    }
}