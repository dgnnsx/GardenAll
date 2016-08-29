package br.com.gardenall.domain;

/**
 * Created by diego on 29/08/16.
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

public class PlantaService {
    public static final String TAG = "PlantaService";

    public static List<Planta> getPlantas(Context context){
        try{
            String json = FileUtils.readRawFileString(context, R.raw.plantas, "UTF-8");
            List<Planta> plantas = parserJSON(context, json);
            return plantas;
        }
        catch(Exception e){
            Log.e(TAG, "Erro ao ler dados: " +e.getMessage(), e);
            return null;
        }
    }

    private static List<Planta> parserJSON(Context context, String json) throws IOException {
        List<Planta> plantas = new ArrayList<Planta>();
        try{
            JSONObject root = new JSONObject(json);
            JSONObject object = root.getJSONObject("plantas");
            JSONArray jsonPlantas = object.getJSONArray("planta");
            // Insere as plantas na lista
            for(int i = 0; i < jsonPlantas.length(); i++){
                JSONObject jsonLinha = jsonPlantas.getJSONObject(i);
                Planta planta = new Planta();
                // Lê as informações de cada planta
                planta.setNomePlanta(jsonLinha.optString("nomePlanta"));
                planta.setUrlImagem(jsonLinha.optString("urlPlanta"));
                plantas.add(planta);
            }
        }
        catch(JSONException e){
            throw new IOException(e.getMessage(), e);
        }
        return plantas;
    }
}
