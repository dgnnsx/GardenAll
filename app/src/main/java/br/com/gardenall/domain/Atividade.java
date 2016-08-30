package br.com.gardenall.domain;

/**
 * Created by diego on 30/08/16.
 */

public class Atividade {
    private String titulo;
    private String subTitulo;
    private String horario;
    private static boolean status;

    public Atividade(){}

    public String getTitulo(){
        return titulo;
    }

    public void setTitulo(String titulo){
        this.titulo = titulo;
    }

    public String getSubTitulo(){
        return subTitulo;
    }

    public void setSubTitulo(String subTitulo){
        this.subTitulo = subTitulo;
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
