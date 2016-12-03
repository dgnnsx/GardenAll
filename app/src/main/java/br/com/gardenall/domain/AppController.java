package br.com.gardenall.domain;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

import br.com.gardenall.R;

public class AppController extends Application {

    public static ArrayList<Integer> imagens;
    public static final String TAG = AppController.class.getSimpleName();

    private RequestQueue mRequestQueue;

    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        imagens = new ArrayList<>();
        setupImagens();
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    private void setupImagens() {
        imagens.add(R.drawable.agriao);
        imagens.add(R.drawable.alface);
        imagens.add(R.drawable.batata);
        imagens.add(R.drawable.beringela);
        imagens.add(R.drawable.beterraba);
        imagens.add(R.drawable.cebolinha);
        imagens.add(R.drawable.cenoura);
        imagens.add(R.drawable.chicoria);
        imagens.add(R.drawable.couve);
        imagens.add(R.drawable.espinafre);
        imagens.add(R.drawable.vagem);
        imagens.add(R.drawable.hortela);
        imagens.add(R.drawable.jilo);
        imagens.add(R.drawable.milho);
        imagens.add(R.drawable.morango);
        imagens.add(R.drawable.pepino);
        imagens.add(R.drawable.pimentao);
        imagens.add(R.drawable.quiabo);
        imagens.add(R.drawable.salsa);
        imagens.add(R.drawable.tomate);
    }

    public ArrayList<Integer> getImagens() {
        return imagens;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}