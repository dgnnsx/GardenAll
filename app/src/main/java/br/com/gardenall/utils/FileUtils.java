package br.com.gardenall.utils;

/**
 * Created by diego on 29/08/16.
 */

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Encapsula a leitura de arquivos JSON
 */

public class FileUtils {
    public static final String TAG = "gardenallFileUtils";

    public static String readRawFileString(Context context, int raw, String charset) throws IOException {
        Resources resources = context.getResources();
        InputStream in = resources.openRawResource(raw);
        return toString(in, charset);
    }

    public static String toString(InputStream in, String charset) throws IOException {
        byte[] bytes = toBytes(in);
        String texto = new String(bytes, charset);
        return texto;
    }

    public static byte[] toBytes(InputStream in) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) > 0) {
                bos.write(buffer, 0, len);
            }
            byte[] bytes = bos.toByteArray();
            return bytes;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        } finally {
            try {
                bos.close();
                in.close();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }
}