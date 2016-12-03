package br.com.gardenall.Callback;

import br.com.gardenall.domain.Planta;

/**
 * Created by diego on 03/12/16.
 */

public class CallbackPlantas {
    /* Interface para atualizacao da lista de plantas (add) */
    private static FragmentRefreshListener fragmentRefreshListener;

    public interface FragmentRefreshListener {
        void onRefresh(Planta planta);
    }

    public static FragmentRefreshListener getFragmentRefreshListener() {
        return fragmentRefreshListener;
    }

    public static void setFragmentRefreshListener(FragmentRefreshListener refreshListener) {
        fragmentRefreshListener = refreshListener;
    }
}