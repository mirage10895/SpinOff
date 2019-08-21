package fr.eseo.dis.amiaudluc.spinoffapp.ui.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import fr.eseo.dis.amiaudluc.spinoffapp.utils.ConstUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.LogUtils;

/**
 * Created by lucasamiaud on 29/03/2018.
 */

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        LogUtils.d(LogUtils.DEBUG_TAG,"AlarmReceiver => onReceive !");
        int episodeId = intent.getIntExtra(ConstUtils.BUNDLE_KEY_EPISODE,-1);
        LogUtils.d(LogUtils.DEBUG_TAG,"Episode id => " + episodeId);
//        EpisodeDatabase episode = AppDatabase.getAppDatabase(context).episodeDAO().getEpisodeById(episodeId);
//        SeasonDatabase season = AppDatabase.getAppDatabase(context).seasonDAO().getSeasonById(episode.getSeasonId());
//        SerieDatabase serie = AppDatabase.getAppDatabase(context).serieDAO().getSerieById(season.getSerieId());
//        NotificationUtils.getInstance(context).createNotification(
//                context,
//                "Nouvel Episode !",
//                "Serie "+serie.getName()+" "+episode.toString()+" le "+ DateUtils.getStringFromDate(new Date()));

    }
}
