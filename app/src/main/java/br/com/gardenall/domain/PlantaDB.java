package br.com.gardenall.domain;

import android.content.Context;
import android.database.ContentObservable;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by diego on 30/08/16.
 */
public class PlantaDB extends SQLiteOpenHelper{
    private static final String TAG = "sql";
    private static final String NOME_BANCO = "plantas.sqlite";
    private static final int VERSAO_BANCO = 1;

    public PlantaDB(Context context) {
        // Context, nome do banco, factory, versão
        super(context, NOME_BANCO, null, VERSAO_BANCO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists planta (_id integer primary key " +
            "autoincrement, nomePlanta text, urlImagem text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}

    // Insere uma nova planta, ou atualiza se já existe


}
