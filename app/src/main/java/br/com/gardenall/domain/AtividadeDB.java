package br.com.gardenall.domain;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
/**
 * Created by diego on 22/10/16.
 */
public class AtividadeDB extends SQLiteOpenHelper {
    private static final String NOME_BANCO = "atividades.sqlite";
    private static final String ATIVIDADE_DB = "atividade";
    private static final int VERSAO_BANCO = 1;

    public AtividadeDB(Context context) {
        // Context, nome do banco, factory, versão
        super(context, NOME_BANCO, null, VERSAO_BANCO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists atividade (_id integer primary key " +
                "autoincrement, nomeAtividade text, desc text, horario text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists atividade;");
        onCreate(db);
    }

    // Insere uma nova atividade na lista do usuario, ou atualiza se já existe
    public long save(Atividade at) {
        long id = at.getId();
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("nomeAtividade", at.getTitulo());
            values.put("desc", at.getDescricao());
            values.put("horario", at.getHorario());
            id = (int) db.insertWithOnConflict(ATIVIDADE_DB, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            if (id != 0) {
                String _id = String.valueOf(at.getId());
                String[] whereArgs = new String[]{_id};
                // update atividade set values = ... where _id=?
                int count = db.update(ATIVIDADE_DB, values, "_id=?", whereArgs);
                return count;
            } else {
                // insert into atividade values (...)
                id = db.insert(ATIVIDADE_DB, "", values);
                return id;
            }
        } finally {
            db.close();
        }
    }

    // Deleta a atividade
    public int delete(Atividade at) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            // delete from atividade where _id=?
            int count = db.delete(ATIVIDADE_DB, "_id=?", new String[]{String.valueOf(at.getId())});
            return count;
        } finally {
            db.close();
        }
    }

    // Deleta todas atividades da lista do usuario
    public void deleteAll() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(ATIVIDADE_DB, null, null);
    }

    // Busca a atividade pelo nome
    public Atividade findByNome(String nome) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            // select * from atividade
            Cursor c = db.query(ATIVIDADE_DB, null, "nomeAtividade = ?", new String[]{nome}, null, null, null, null);
            if (c.moveToFirst()) {
                Atividade at = new Atividade();
                read(c, at);
                return at;
            }
            return null;
        } finally {
            db.close();
        }
    }

    // Lista todas atividades do usuario
    public ArrayList<Atividade> findAll() {
        SQLiteDatabase db = getWritableDatabase();
        try {
            // select * from atividade
            Cursor c = db.query(ATIVIDADE_DB, null, null, null, null, null, "nomeAtividade ASC");
            return toList(c);
        } finally {
            db.close();
        }
    }

    // Lê o cursor e cria a lista de atividades
    private ArrayList<Atividade> toList(Cursor c) {
        ArrayList<Atividade> ats = new ArrayList<Atividade>();
        if (c.moveToFirst()) {
            do {
                Atividade at = new Atividade();
                // recupera os atributos de atividade
                at.setId(c.getLong(c.getColumnIndex("_id")));
                at.setTitulo(c.getString(c.getColumnIndex("nomeAtividade")));
                at.setDescricao(c.getString(c.getColumnIndex("desc")));
                at.setHorario(c.getString(c.getColumnIndex("horario")));
                ats.add(at);
            } while (c.moveToNext());
        }
        return ats;
    }

    // Faz a leitura dos atributos de planta
    private void read(Cursor c, Atividade at) {
        at.setId(c.getLong(c.getColumnIndex("_id")));
        at.setTitulo(c.getString(c.getColumnIndex("nomeAtividade")));
        at.setDescricao(c.getString(c.getColumnIndex("desc")));
        at.setHorario(c.getString(c.getColumnIndex("horario")));
    }

    public void update(Atividade atividade) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("nomeAtividade", atividade.getTitulo());
            values.put("desc", atividade.getDescricao());
            values.put("horario", atividade.getHorario());
            String[] whereArgs = new String[]{atividade.getTitulo()};
            // update planta set values = ... where _id=?
            db.update(ATIVIDADE_DB, values, "nomeAtividade=?", whereArgs);
        }  finally {
            db.close();
        }
    }
}