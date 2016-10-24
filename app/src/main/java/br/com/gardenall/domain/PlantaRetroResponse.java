package br.com.gardenall.domain;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
/**
 * Created by joaoulian on 13/10/16.
 */
public class PlantaRetroResponse {
    @SerializedName("plantas")
    private ArrayList<Planta> plantas;

    public ArrayList<Planta> getPlantas() {
        return plantas;
    }

    public void setPlantas(ArrayList<Planta> newPlantas) {
        this.plantas = newPlantas;
    }
}


