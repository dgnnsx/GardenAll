package br.com.gardenall.Callback;

/**
 * Created by diego on 13/10/16.
 */

import java.util.ArrayList;
import java.util.List;

import br.com.gardenall.domain.Planta;

public interface RetroCallBack {
    void onSuccess(ArrayList<Planta> plantas);
}