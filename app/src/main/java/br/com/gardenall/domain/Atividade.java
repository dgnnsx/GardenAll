package br.com.gardenall.domain;

/**
 * Created by diego on 30/08/16.
 */

public class Atividade {
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
