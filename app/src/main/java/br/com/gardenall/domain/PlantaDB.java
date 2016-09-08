package br.com.gardenall.domain;

/**
 * Created by diego on 30/08/16.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class PlantaDB extends SQLiteOpenHelper{
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
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table planta;");
        onCreate(db);
    }

    // Insere uma nova planta, ou atualiza se já existe
    public long insert(Planta planta) {
        long id = planta.getId();
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("nomePlanta", planta.getNomePlanta());
            values.put("urlImagem", planta.getUrlImagem());

            if(id != 0) {
                String _id = String.valueOf(planta.getId());
                String[] whereArgs = new String[]{_id};
                // update planta set values = ... where _id=?
                int count = db.update("planta", values, "_id=?", whereArgs);
                return count;
            } else {
                // insert into planta values (...)
                id = db.insert("planta", "", values);
                return id;
            }
        } finally {
            db.close();
        }
    }

    // Deleta a planta
    public int delete(Planta planta) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            // delete from planta where _id=?
            int count = db.delete("planta", "_id=?", new String[]{String.valueOf(planta.getId())});
            return count;
        } finally {
            db.close();
        }
    }

    // Deleta todas plantas
    public void deleteAll() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("planta", null, null);
    }

    public List<Planta> findAll() {
        SQLiteDatabase db = getWritableDatabase();
        try {
            // select * from planta
            Cursor c = db.query("planta", null, null, null, null, null, "nomePlanta ASC");
            return toList(c);
        } finally {
            db.close();
        }
    }

    // Busca a planta pelo nome
    public Planta findByNome(String nome) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            // select * from planta
            Cursor c = db.query("planta", null, "nomePlanta = ?", new String[]{nome}, null, null, null, null);
            if(c.moveToFirst()) {
                Planta planta = new Planta();
                read(c, planta);
                return planta;
            }
            return null;
        } finally {
            db.close();
        }
    }

    // Lê o cursor e cria a lista de plantas
    private List<Planta> toList(Cursor c) {
        List<Planta> plantas = new ArrayList<Planta>();
        if(c.moveToFirst()) {
            do {
                Planta planta = new Planta();
                // recupera os atributos de planta
                planta.setId(c.getLong(c.getColumnIndex("_id")));
                planta.setNomePlanta(c.getString(c.getColumnIndex("nomePlanta")));
                planta.setUrlImagem(c.getString(c.getColumnIndex("urlImagem")));
                plantas.add(planta);
            } while (c.moveToNext());
        }
        return plantas;
    }

    // Faz a leitura dos atributos de planta
    private void read(Cursor c, Planta planta) {
        planta.setId(c.getLong(c.getColumnIndex("_id")));
        planta.setNomePlanta(c.getString(c.getColumnIndex("nomePlanta")));
        planta.setUrlImagem(c.getString(c.getColumnIndex("urlImagem")));
    }
}
