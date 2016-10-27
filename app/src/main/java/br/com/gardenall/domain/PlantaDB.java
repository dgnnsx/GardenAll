package br.com.gardenall.domain;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by diego on 30/08/16.
 */
public class PlantaDB extends SQLiteOpenHelper {
    private static final String NOME_BANCO = "plantas.sqlite";
    private static final String PLANTA_DB = "planta";
    private static final String CATALOGO_DB = "catalogo";
    private static final int VERSAO_BANCO = 1;

    public PlantaDB(Context context) {
        // Context, nome do banco, factory, versão
        super(context, NOME_BANCO, null, VERSAO_BANCO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists planta (_id integer primary key " +
                "autoincrement, nomePlanta text, urlImagem text, favorito integer);");

        db.execSQL("create table if not exists catalogo (_id integer primary key " +
                "autoincrement, nomePlanta text, urlImagem text, favorito integer);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists planta;");
        db.execSQL("drop table if exists catalogo;");
        onCreate(db);
    }

    // Insere uma nova planta na lista do usuario, ou atualiza se já existe
    public long save(Planta planta) {
        long id = planta.getId();
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("nomePlanta", planta.getNomePlanta());
            values.put("urlImagem", planta.getUrlImagem());
            values.put("favorito", planta.getFavorito());
            id = (int) db.insertWithOnConflict(PLANTA_DB, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            if(id == -1) {
                String _id = String.valueOf(planta.getId());
                String[] whereArgs = new String[]{_id};
                db.update(PLANTA_DB, values, "_id=?", whereArgs);
            }
            return id;
        } finally {
            db.close();
        }
    }

    // Deleta a planta
    public int delete(Planta planta) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            // delete from planta where _id=?
            int count = db.delete(PLANTA_DB, "_id=?", new String[]{String.valueOf(planta.getId())});
            return count;
        } finally {
            db.close();
        }
    }

    // Deleta todas plantas da lista do usuario
    public void deleteAll() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(PLANTA_DB, null, null);
    }

    // Busca a planta pelo nome
    public Planta findByNome(String nome) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            // select * from planta
            Cursor c = db.query(PLANTA_DB, null, "nomePlanta = ?", new String[]{nome}, null, null, null, null);
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

    // Lista todas plantas do usuario
    public ArrayList<Planta> findAll() {
        SQLiteDatabase db = getWritableDatabase();
        try {
            // select * from planta
            Cursor c = db.query(PLANTA_DB, null, null, null, null, null, "nomePlanta ASC");
            return toList(c);
        } finally {
            db.close();
        }
    }

    // Lê o cursor e cria a lista de plantas
    private ArrayList<Planta> toList(Cursor c) {
        ArrayList<Planta> plantas = new ArrayList<Planta>();
        if(c.moveToFirst()) {
            do {
                Planta planta = new Planta();
                // recupera os atributos de planta
                planta.setId(c.getLong(c.getColumnIndex("_id")));
                planta.setNomePlanta(c.getString(c.getColumnIndex("nomePlanta")));
                planta.setUrlImagem(c.getString(c.getColumnIndex("urlImagem")));
                planta.setFavorito(c.getInt(c.getColumnIndex("favorito")));
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
        planta.setFavorito(c.getInt(c.getColumnIndex("favorito")));
    }

    /*
     * Métodos do Catalago
     */

    public void updateFavorito(Planta planta) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("nomePlanta", planta.getNomePlanta());
            values.put("urlImagem", planta.getUrlImagem());
            values.put("favorito", planta.getFavorito());
            String[] whereArgs = new String[]{planta.getNomePlanta()};
            // update planta set values = ... where _id=?
            db.update(CATALOGO_DB, values, "nomePlanta=?", whereArgs);
        }  finally {
            db.close();
        }
    }

    // Insere uma nova planta no catalogo, ou atualiza se já existe
    public long saveOnCatalogo(Planta planta) {
        long id = planta.getId();
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("nomePlanta", planta.getNomePlanta());
            values.put("urlImagem", planta.getUrlImagem());
            values.put("favorito", "");
            if(id != 0) {
                String _id = String.valueOf(planta.getId());
                String[] whereArgs = new String[]{_id};
                // update planta set values = ... where _id=?
                int count = db.update(CATALOGO_DB, values, "_id=?", whereArgs);
                return count;
            } else {
                // insert into planta values (...)
                id = db.insert(CATALOGO_DB, "", values);
                return id;
            }
        } finally {
            db.close();
        }
    }

    // Deleta a planta
    public int deleteOnCatalogo(Planta planta) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            // delete from planta where _id=?
            int count = db.delete(CATALOGO_DB, "_id=?", new String[]{String.valueOf(planta.getId())});
            return count;
        } finally {
            db.close();
        }
    }

    // Deleta todas plantas do catalogo
    public void deleteAllOnCatalogo() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(CATALOGO_DB, null, null);
    }

    // Busca a planta pelo nome no catalogo
    public Planta findByNomeOnCatalogo(String nome) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            // select * from planta
            Cursor c = db.query(CATALOGO_DB, null, "nomePlanta=?", new String[]{nome}, null, null, null, null);
            if(c.moveToFirst()) {
                Planta planta = new Planta();
                readOnCatalogo(c, planta);
                return planta;
            }
            return null;
        } finally {
            db.close();
        }
    }

    // Lista todas plantas do catalogo
    public ArrayList<Planta> findAllOnCatalogo() {
        SQLiteDatabase db = getWritableDatabase();
        try {
            // select * from catalogo
            Cursor c = db.query(CATALOGO_DB, null, null, null, null, null, "nomePlanta ASC");
            return toListOnCatalogo(c);
        } finally {
            db.close();
        }
    }

    // Lê o cursor e cria a lista do catalogo
    private ArrayList<Planta> toListOnCatalogo(Cursor c) {
        ArrayList<Planta> plantas = new ArrayList<Planta>();
        if(c.moveToFirst()) {
            do {
                Planta planta = new Planta();
                // recupera os atributos de planta
                planta.setId(c.getLong(c.getColumnIndex("_id")));
                planta.setNomePlanta(c.getString(c.getColumnIndex("nomePlanta")));
                planta.setUrlImagem(c.getString(c.getColumnIndex("urlImagem")));
                planta.setFavorito(c.getInt(c.getColumnIndex("favorito")));
                plantas.add(planta);
            } while (c.moveToNext());
        }
        return plantas;
    }

    // Faz a leitura dos atributos de planta
    private void readOnCatalogo(Cursor c, Planta planta) {
        planta.setId(c.getLong(c.getColumnIndex("_id")));
        planta.setNomePlanta(c.getString(c.getColumnIndex("nomePlanta")));
        planta.setUrlImagem(c.getString(c.getColumnIndex("urlImagem")));
        planta.setFavorito(c.getInt(c.getColumnIndex("favorito")));
    }
}