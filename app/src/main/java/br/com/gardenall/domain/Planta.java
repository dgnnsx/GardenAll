package br.com.gardenall.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by diego on 26/08/16.
 */
public class Planta implements Parcelable {
    private int favorito;
    private int selected;

    @SerializedName("id_p")
    private int id;
    @SerializedName("nome")
    private String nomePlanta;
    @SerializedName("url")
    private String urlImagem;
    private String colheitaMin;
    private String epocaSul;
    private String epocaSudeste;
    private String epocaCentroOeste;
    private String epocaNorte;
    private String epocaNordeste;
    private String sol;
    private String regar;

    public Planta() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomePlanta(){
        return nomePlanta;
    }

    public void setNomePlanta(String nomePlanta){
        this.nomePlanta = nomePlanta;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public void invertSelected() {
        if(selected == 0)
            selected = 1;
        else
            selected = 0;
    }

    public void setFavorito(int favorito) {
        this.favorito = favorito;
    }

    public int getFavorito() {
        return favorito;
    }

    public String getColheitaMin() {
        return colheitaMin;
    }

    public void setColheitaMin(String colheitaMin) {
        this.colheitaMin = colheitaMin;
    }

    public String getEpocaSul() {
        return epocaSul;
    }

    public void setEpocaSul(String epocaSul) {
        this.epocaSul = epocaSul;
    }

    public String getEpocaSudeste() {
        return epocaSudeste;
    }

    public void setEpocaSudeste(String epocaSudeste) {
        this.epocaSudeste = epocaSudeste;
    }

    public String getEpocaCentroOeste() {
        return epocaCentroOeste;
    }

    public void setEpocaCentroOeste(String epocaCentroOeste) {
        this.epocaCentroOeste = epocaCentroOeste;
    }

    public String getEpocaNorte() {
        return epocaNorte;
    }

    public void setEpocaNorte(String epocaNorte) {
        this.epocaNorte = epocaNorte;
    }

    public String getEpocaNordeste() {
        return epocaNordeste;
    }

    public void setEpocaNordeste(String epocaNordeste) {
        this.epocaNordeste = epocaNordeste;
    }

    public String getSol() {
        return sol;
    }

    public void setSol(String sol) {
        this.sol = sol;
    }

    public String getRegar() {
        return regar;
    }

    public void setRegar(String regar) {
        this.regar = regar;
    }

    // Parcelable
    public Planta(Parcel parcel){
        id = parcel.readInt();
        nomePlanta = parcel.readString();
        urlImagem = parcel.readString();
        colheitaMin = parcel.readString();
        epocaSul = parcel.readString();
        epocaSudeste = parcel.readString();
        epocaCentroOeste = parcel.readString();
        epocaNorte = parcel.readString();
        epocaNordeste = parcel.readString();
        sol = parcel.readString();
        regar = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(nomePlanta);
        dest.writeString(urlImagem);
        dest.writeString(colheitaMin);
        dest.writeString(epocaSul);
        dest.writeString(epocaSudeste);
        dest.writeString(epocaCentroOeste);
        dest.writeString(epocaNordeste);
        dest.writeString(epocaNorte);
        dest.writeString(sol);
        dest.writeString(regar);
    }

    public static final Parcelable.Creator<Planta> CREATOR = new Parcelable.Creator<Planta>(){
        @Override
        public Planta createFromParcel(Parcel source){
            return new Planta(source);
        }

        @Override
        public Planta[] newArray(int size){
            return new Planta[size];
        }
    };
}