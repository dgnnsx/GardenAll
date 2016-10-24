package br.com.gardenall.domain;

import java.util.Observable;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by joaoulian on 13/10/16.
 */
public interface ApiInterface {
    @GET("listarCatalogo.php")
    rx.Observable<PlantaRetroResponse> getPlantas();
}
