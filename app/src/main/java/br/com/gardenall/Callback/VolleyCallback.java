package br.com.gardenall.Callback;

/**
 * Created by diego on 13/10/16.
 */

import java.util.ArrayList;
import br.com.gardenall.domain.Planta;

public interface VolleyCallback {
    void onSuccess(ArrayList<Planta> plantas);
}