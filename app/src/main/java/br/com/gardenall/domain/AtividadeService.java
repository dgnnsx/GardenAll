package br.com.gardenall.domain;

/**
 * Created by diego on 30/08/16.
 */

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.gardenall.R;
import br.com.gardenall.utils.FileUtils;


public class AtividadeService {
    public static final String TAG = "AtividadeService";

    public static List<Atividade> getAtividades(Context context){
        try{
            String json = FileUtils.readRawFileString(context, R.raw.atividades, "UTF-8");
            List<Atividade> atividades = parserJSON(context, json);
            return atividades;
        }
        catch(Exception e){
            Log.e(TAG, "Erro ao ler dados: " +e.getMessage(), e);
            return null;
        }
    }

    private static List<Atividade> parserJSON(Context context, String json) throws IOException {
        List<Atividade> atividades = new ArrayList<Atividade>();
        try{
            JSONObject root = new JSONObject(json);
            JSONObject object = root.getJSONObject("atividades");
            JSONArray jsonAtividades = object.getJSONArray("atividade");
            // Insere as atividades na lista
            for(int i = 0; i < jsonAtividades.length(); i++){
                JSONObject jsonAtividade = jsonAtividades.getJSONObject(i);
                Atividade atividade = new Atividade();
                // Lê as informações de cada atividade
                atividade.setTitulo(jsonAtividade.optString("titulo"));
                atividade.setSubTitulo(jsonAtividade.optString("subTitulo"));
                atividade.setHorario(jsonAtividade.optString("horario"));
                atividades.add(atividade);
            }
        }
        catch(JSONException e){
            throw new IOException(e.getMessage(), e);
        }
        return atividades;
    }
}
