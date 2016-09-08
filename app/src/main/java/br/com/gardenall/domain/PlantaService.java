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

    public static List<Planta> getPlantas(Context context, boolean refresh) throws IOException {
        List<Planta> plantas = null;
        boolean searchInDB = !refresh;
        if(searchInDB) {
            // Busca no banco de dados
            plantas = getPlantasFromDB(context);
            if(plantas != null && plantas.size() > 0) {
                // Retorna as plantas encontradas no banco
                return plantas;
            }
        }
        // Se não encontrar, busca na web
        plantas = getPlantasFromWeb(context);
        return plantas;
    }

    private static List<Planta> getPlantasFromDB(Context context) throws IOException {
        PlantaDB db = new PlantaDB(context);
        try {
            List<Planta> plantas = db.findAll();
            return plantas;
        } finally {
            db.close();
        }
    }

    private static List<Planta> getPlantasFromWeb(Context context) throws IOException {
        String json = FileUtils.readRawFileString(context, R.raw.plantas, "UTF-8");
        List<Planta> plantas = parserJSON(context, json);
        // Depois de buscar, salva as plantas
        savePlantas(context, plantas);
        return plantas;
    }

    // Salva as plantas no banco de dados interno
    private static void savePlantas(Context context, List<Planta> plantas) {
        PlantaDB db = new PlantaDB(context);
        try {
            // Deleta as plantas antigas para limpar o banco
            db.deleteAll();
            // Salva todas as plantas
            for(Planta planta : plantas) {
                // p.tipo = tipo;
                db.insert(planta);
            }
        } finally {
            db.close();
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
