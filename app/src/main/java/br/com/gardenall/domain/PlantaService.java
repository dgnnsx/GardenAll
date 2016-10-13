package br.com.gardenall.domain;

/**
 * Created by diego on 29/08/16.
 */

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import br.com.gardenall.Callback.VolleyCallback;

public class PlantaService {
    public static final String TAG = "PlantaService";

    private static ArrayList<Planta> p;

    public static ArrayList<Planta> getPlantas(Context context, boolean refresh) throws IOException {
        ArrayList<Planta> plantas = null;
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
        return plantas;
    }

    public static ArrayList<Planta> getCatalogoDePlantas(final Context context, boolean refresh) throws IOException {
        ArrayList<Planta> plantas = new ArrayList<Planta>();
        boolean searchInDB = !refresh;
        if(searchInDB) {
            // Busca no banco de dados
            plantas = getCatalogoDePlantasFromDB(context);
            if(plantas != null && plantas.size() > 0) {
                // Retorna as plantas encontradas no banco
                return plantas;
            }
        }
        // Se não encontrar, busca na web
        getCatalogoDePlantasFromWeb(new VolleyCallback() {
            @Override
            public void onSuccess(ArrayList<Planta> plantas) {
                saveCatalogoDePlantas(context, plantas);
            }
        });
        return getCatalogoDePlantasFromDB(context);
    }

    private static ArrayList<Planta> getPlantasFromDB(Context context) throws IOException {
        PlantaDB db = new PlantaDB(context);
        try {
            ArrayList<Planta> plantas = db.findAll();
            return plantas;
        } finally {
            db.close();
        }
    }

    private static ArrayList<Planta> getCatalogoDePlantasFromDB(Context context) throws IOException {
        PlantaDB db = new PlantaDB(context);
        try {
            ArrayList<Planta> plantas = db.findAllOnCatalogo();
            return plantas;
        } finally {
            db.close();
        }
    }

    private static void getCatalogoDePlantasFromWeb (final VolleyCallback callback) throws IOException {
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Variaveis.URL_LIST_CATALOGO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback.onSuccess(parserVolley(response));
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Registration Error: " + error.getMessage());
                    }
                });
        AppController.getInstance().addToRequestQueue(strReq);
    }

    private static ArrayList<Planta> parserVolley(String response) {
        ArrayList<Planta> plantas = new ArrayList<Planta>();
        try {
            JSONObject root = new JSONObject(response.toString());
            JSONArray jsonPlantas = root.getJSONArray("plantas");
            for (int i = 0; i < jsonPlantas.length(); i++) {
                JSONObject jsonLinha = jsonPlantas.getJSONObject(i);
                Planta planta = new Planta();
                // Lê as informações de cada planta
                planta.setNomePlanta(jsonLinha.optString("nome"));
                planta.setUrlImagem(jsonLinha.optString("url"));
                plantas.add(planta);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return plantas;
    }

    public static ArrayList<Planta> getFavorites(Context context) throws IOException {
        ArrayList<Planta> plantasAux = PlantaService.getCatalogoDePlantasFromDB(context);
        ArrayList<Planta> plantas = new ArrayList<>();
        for(Planta p : plantasAux){
            if(p.getFavorito() == 1)
                plantas.add(p);
        }
        return plantas;
    }

    // Salva as plantas no banco de dados interno
    public static void savePlanta(Context context, Planta planta) {
        PlantaDB db = new PlantaDB(context);
        try {
            // Salva a planta
            db.save(planta);
        } finally {
            db.close();
        }
    }

    // Salva as plantas no banco de dados interno
    private static void savePlantas(Context context, ArrayList<Planta> plantas) {
        PlantaDB db = new PlantaDB(context);
        try {
            // Deleta as plantas antigas para limpar o banco
            db.deleteAll();
            // Salva todas as plantas
            for(Planta planta : plantas) {
                // p.tipo = tipo;
                db.save(planta);
            }
        } finally {
            db.close();
        }
    }

    // Salva as plantas no banco de dados interno
    private static void saveCatalogoDePlantas(Context context, ArrayList<Planta> plantas) {
        PlantaDB db = new PlantaDB(context);
        try {
            // Deleta as plantas antigas para limpar o banco
            db.deleteAllOnCatalogo();
            // Salva todas as plantas
            for(Planta planta : plantas) {
                // p.tipo = tipo;
                db.saveOnCatalogo(planta);
            }
        } finally {
            db.close();
        }
    }

    private static ArrayList<Planta> parserJSON(Context context, String json) throws IOException {
        ArrayList<Planta> plantas = new ArrayList<Planta>();
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
                // planta.setFavorito(jsonLinha.optInt("favorito"));
                plantas.add(planta);
            }
        }
        catch(JSONException e){
            throw new IOException(e.getMessage(), e);
        }
        return plantas;
    }
}
