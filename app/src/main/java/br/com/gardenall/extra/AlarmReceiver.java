package br.com.gardenall.extra;

/**
 * Created by diego on 16/09/16.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Date;

import br.com.gardenall.R;
import br.com.gardenall.activity.MainActivity;
import br.com.gardenall.utils.NotificationUtil;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "livroandroid";
    public static final String ACTION = "br.com.gardenall.ALARME";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"Você precisa comer: " + new Date());

        Intent notifIntent = new Intent(context,MainActivity.class);

        NotificationUtil.create(context, 1, notifIntent, R.mipmap.ic_launcher,"Hora de comer algo...","Que tal uma fruta?");
    }
}
