package br.com.gardenall.domain;

/**
 * Created by diego on 30/08/16.
 */

import android.content.Context;
import java.io.IOException;
import java.util.ArrayList;

public class AtividadeService {
    public static final String TAG = "AtividadeService";

    public static ArrayList<Atividade> getAtividades(Context context) throws IOException {
        ArrayList<Atividade> ats;
        // Busca no banco de dados
        ats = getAtividadesFromDB(context);
        if(ats != null && ats.size() > 0) {
            // Retorna as plantas encontradas no banco
            return ats;
        }
        // Se não existe nada no BD, inicializa o vetor
        return new ArrayList<>();
    }

    private static ArrayList<Atividade> getAtividadesFromDB(Context context) throws IOException {
        AtividadeDB db = new AtividadeDB(context);
        try {
            ArrayList<Atividade> ats = db.findAll();
            return ats;
        } finally {
            db.close();
        }
    }

    public static void saveAtividade(Context context, Atividade atividade) {
        AtividadeDB db = new AtividadeDB(context);
        try {
            // Salva a atividade
            db.save(atividade);
        } finally {
            db.close();
        }
    }

    public static void updateAtividade(Context context, Atividade atividade) {
        AtividadeDB db = new AtividadeDB(context);
        try {
            // Atualiza a atividade
            db.update(atividade);
        } finally {
            db.close();
        }
    }
}