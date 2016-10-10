package br.com.gardenall.domain;

/**
 * Created by diego on 29/08/16.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.gardenall.PlantasApplication;
import br.com.gardenall.R;
import br.com.gardenall.activity.CatalogoActivity;
import br.com.gardenall.activity.WelcomeActivity;
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
        } else {
            // Busca no banco de dados
            plantas = getPlantasFromDB(context);
            if(plantas != null && plantas.size() > 0) {
                // Retorna as plantas encontradas no banco
                return plantas;
            }
        }
        plantas = getPlantasFromWeb(context);
        return plantas;
    }

    public static void getCatalogoDePlantas(Context context, boolean refresh, final CatalogoActivity.CatalogoCallback catCallback) throws IOException {
        List<Planta> plantas = null;
        boolean searchInDB = !refresh;
        if(searchInDB) {
            // Busca no banco de dados
            plantas = getCatalogoDePlantasFromDB(context);
            if(plantas != null && plantas.size() > 0) {
                // Retorna as plantas encontradas no banco
                catCallback.onSuccess(plantas);
                return;
            }
        }
        // Se não encontrar, busca na web
        Toast.makeText(context, "Deu bosta!", Toast.LENGTH_SHORT).show();
        getCatalogoDePlantasFromWeb(new VolleyCallback() {
                    @Override
                    public void onSuccess(List<Planta> p) {
                        catCallback.onSuccess(p);
                        return;
                    }
                });
    }

    private static List<Planta> getPlantasFromDB(Context context) throws IOException {
        PlantaDB db = new PlantaDB(context);
        try {
            List<Planta> plantas = db.findAll();

            if(plantas == null || plantas.size() == 0)
                Toast.makeText(context, "Lista vazia!", Toast.LENGTH_SHORT).show();
            return plantas;
        } finally {
            db.close();
        }
    }

    private static List<Planta> getCatalogoDePlantasFromDB(Context context) throws IOException {
        PlantaDB db = new PlantaDB(context);
        try {
            List<Planta> plantas = db.findAllCatalogo();
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
    private static void getCatalogoDePlantasFromWeb (final VolleyCallback callback){
        final List<Planta>tempPlantas = new ArrayList<>();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Variaveis.URL_LIST_PLANTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                        Log.d(TAG, response.toString());
                        try {
                            JSONObject root = new JSONObject(response.toString());
                            JSONArray jsonPlantas = root.getJSONArray("plantas");
                            Log.d(TAG, "aqui");
                            Log.d(TAG, Integer.toString(jsonPlantas.length()));
                            for (int i = 0; i < jsonPlantas.length(); i++) {
                                Log.d(TAG, Integer.toString(i));
                                JSONObject jsonLinha = jsonPlantas.getJSONObject(i);
                                Planta planta = new Planta();
                                // Lê as informações de cada planta
                                planta.setNomePlanta(jsonLinha.optString("nome"));
                                Log.d(TAG, jsonLinha.optString("nome"));
                                planta.setUrlImagem("naotemimagem");
                                tempPlantas.add(planta);
                            }
                            callback.onSuccess(tempPlantas);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
            }
        });
        AppController.getInstance().addToRequestQueue(strReq);
    }

    public interface VolleyCallback{
        void onSuccess(List<Planta> p);
    }

    // Salva as plantas no banco de dados interno
    public static void savePlanta(Context context, Planta planta) {
        PlantaDB db = new PlantaDB(context);
        try {
            // Salva a planta
            db.insert(planta);
        } finally {
            db.close();
        }
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

    // Salva as plantas no banco de dados interno
    private static void saveCatalogoDePlantas(Context context, List<Planta> plantas) {
        PlantaDB db = new PlantaDB(context);
        try {
            // Deleta as plantas antigas para limpar o banco
            db.deleteAllCatalogo();
            // Salva todas as plantas
            for(Planta planta : plantas) {
                // p.tipo = tipo;
                db.insertCatalogo(planta);
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
