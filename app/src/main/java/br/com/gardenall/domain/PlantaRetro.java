package br.com.gardenall.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by joaoulian on 13/10/16.
 */
public class PlantaRetro {
    @SerializedName("id_p")
    private int id_p;
    @SerializedName("nome")
    private String nome;
    @SerializedName("url")
    private String url;

    public PlantaRetro(int id_p, String nome, String url) {
        this.id_p = id_p;
        this.nome = nome;
        this.url = url;
    }

    public int getId(){
        return id_p;
    }

    public void setId(int newId){
        this.id_p = newId;
    }

    public String getNome(){
        return nome;
    }

    public void setNome(String newNome){
        this.nome = newNome;
    }

    public String getUrl(){
        return url;
    }

    public void setUrl(String newUrl){
        this.url = newUrl;
    }

}
