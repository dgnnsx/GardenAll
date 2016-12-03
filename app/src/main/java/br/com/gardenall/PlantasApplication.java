package br.com.gardenall;

/**
 * Created by diego on 03/09/16.
 */

import android.app.Application;
import android.view.ActionMode;

import java.util.ArrayList;
import java.util.List;

import br.com.gardenall.domain.Planta;

public class PlantasApplication extends Application {
    private static final String TAG = "PlantasApplication";
    private static PlantasApplication instance = null;


    public static ActionMode ACTION_MODE;
    public static int INDEX_OF_TAB;
    public static List<Planta> tempPlanta = new ArrayList<>();

    public static PlantasApplication getInstance(){
        return instance; // Singleton
    }

    public static void finishActionMode() {
        if(ACTION_MODE != null) {
            ACTION_MODE.finish();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Salva a instância para termos acesso como Singleton
        instance = this;
    }
}
