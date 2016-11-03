package br.com.gardenall.domain;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();

    private RequestQueue mRequestQueue;

    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
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

    /**
     * Created by diego on 30/08/16.
     */

    public static class Atividade {
        private long id;
        private String titulo;
        private String descricao;
        private String horario;
        private static boolean status;

        public Atividade(){}

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getTitulo(){
            return titulo;
        }

        public void setTitulo(String titulo){
            this.titulo = titulo;
        }

        public String getDescricao(){
            return descricao;
        }

        public void setDescricao(String subTitulo){
            this.descricao = subTitulo;
        }

        public String getHorario(){
            return horario;
        }

        public void setHorario(String horario){
            this.horario = horario;
        }

        public boolean getStatus(){
            return status;
        }

        public void changeStatus(){
            status = !status;
        }
    }
}