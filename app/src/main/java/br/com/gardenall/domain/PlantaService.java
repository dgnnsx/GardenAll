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
import java.util.HashMap;
import java.util.Map;

import br.com.gardenall.R;
import br.com.gardenall.utils.FileUtils;

import static com.facebook.FacebookSdk.getApplicationContext;

public class PlantaService {
    public static final String TAG = "PlantaService";

    public static ArrayList<Planta> getPlantas(Context context, boolean refresh) throws IOException {
        boolean searchInDB = !refresh;
        ArrayList<Planta> plantasUser;
        if(searchInDB) {
            // Busca no banco de dados
            plantasUser = getPlantasFromDB(context);
            if(plantasUser != null && plantasUser.size() > 0) {
                // Retorna as plantas encontradas no banco
                return plantasUser;
            }
        } else {
            // Busca no banco de dados
            plantasUser = getPlantasFromDB(context);
            if(plantasUser != null && plantasUser.size() > 0) {
                // Retorna as plantas encontradas no banco
                return plantasUser;
            }
        }
        // Se não existe nada no BD, inicializa o vetor
        return new ArrayList<>();
    }

    public static ArrayList<Planta> getCatalogoDePlantas(Context context) throws IOException {
        ArrayList<Planta> plantas;
        // Busca no banco de dados
        plantas = getCatalogoDePlantasFromDB(context);
        if(plantas != null && plantas.size() > 0) {
            // Retorna as plantas encontradas no banco
            return plantas;
        } else { // Se não encontrar, busca no json
            plantas = getCatalogoDePlantasFromJSON(context);
            return plantas;
        }
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

    private static ArrayList<Planta> getCatalogoDePlantasFromJSON(Context context) throws IOException {
        String json = FileUtils.readRawFileString(context, R.raw.plantas, "UTF-8");
        ArrayList<Planta> plantas = parserJSON(context, json);
        // Depois de buscar, salva as plantas
        saveCatalogoDePlantas(context, plantas);
        return getCatalogoDePlantasFromDB(context);
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

    public static void savePlantaWeb(final int id_p, final String email) {
        // Tag used to cancel the request
        String tag_string_req = "req_plantada";
        Log.d("email: ", email);
        Log.d("id_p: ", Integer.toString(id_p));
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Variaveis.URL_PLANTADA, new com.android.volley.Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Register Response: " + response.toString());
            }
        }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("id_p", Integer.toString(id_p));
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public static void deletePlantaWeb(final int id_p, final String email) {
        // Tag used to cancel the request
        String tag_string_req = "req_plantada";
        Log.d("id_p: ", Integer.toString(id_p));
        Log.d("email: ", email);
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Variaveis.URL_DELETE, new com.android.volley.Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Register Response: " + response.toString());
            }
        }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("id_p", Integer.toString(id_p));
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
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
                planta.setId(jsonLinha.optInt("_id"));
                planta.setNomePlanta(jsonLinha.optString("nomePlanta"));
                planta.setUrlImagem(jsonLinha.optString("urlPlanta"));
                planta.setColheitaMin(jsonLinha.optString("colheitaMin"));
                planta.setEpocaSul(jsonLinha.optString("epocaSul"));
                planta.setEpocaSudeste(jsonLinha.optString("epocaSudeste"));
                planta.setEpocaCentroOeste(jsonLinha.optString("epocaCentroOeste"));
                planta.setEpocaNordeste(jsonLinha.optString("epocaNordeste"));
                planta.setEpocaNorte(jsonLinha.optString("epocaNorte"));
                planta.setSol(jsonLinha.optString("sol"));
                planta.setRegar(jsonLinha.optString("regar"));
                plantas.add(planta);
            }
        }
        catch(JSONException e){
            throw new IOException(e.getMessage(), e);
        }
        return plantas;
    }

    public static void getPlantasFromWeb (final String email) throws IOException {
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Variaveis.URL_PLANTAS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parserVolley(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq);
    }


   private static ArrayList<Planta> parserVolley(String response) {
        ArrayList<Planta> plantas = new ArrayList<Planta>();
        PlantaDB db = new PlantaDB(getApplicationContext());
        try {
            JSONObject root = new JSONObject(response.toString());
            JSONArray jsonPlantas = root.getJSONArray("plantas");
            for (int i = 0; i < jsonPlantas.length(); i++) {
                JSONObject jsonLinha = jsonPlantas.getJSONObject(i);
                Planta planta = new Planta();
                // Lê as informações de cada planta
                planta.setId(jsonLinha.optInt("id_p"));
                Planta planta2 = db.findByIdOnCatalogo(jsonLinha.optLong("id_p"));
                if (planta2 == null){
                    try{
                        getCatalogoDePlantas(getApplicationContext());
                        planta2 = db.findByIdOnCatalogo(jsonLinha.optLong("id_p"));
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
                Log.d("planta: ", Long.toString(planta2.getId()));
                plantas.add(planta2);
            }
            savePlantas(getApplicationContext(), plantas);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return plantas;
   }
}
