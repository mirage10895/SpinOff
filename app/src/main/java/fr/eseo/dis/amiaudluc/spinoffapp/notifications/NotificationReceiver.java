package fr.eseo.dis.amiaudluc.spinoffapp.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import fr.eseo.dis.amiaudluc.spinoffapp.utils.ConstUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.DateUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.LogUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.NotificationUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.DBInitializer.AppDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Episode;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Season;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Serie;

/**
 * Created by lucasamiaud on 29/03/2018.
 */

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        LogUtils.d(LogUtils.DEBUG_TAG,"AlarmReceiver => onReceive !");
        int episodeId = intent.getIntExtra(ConstUtils.BUNDLE_KEY_EPISODE,-1);
        LogUtils.d(LogUtils.DEBUG_TAG,"Episode id => " + episodeId);
        Episode episode = AppDatabase.getAppDatabase(context).episodesDAO().getEpisodeById(episodeId);
        Season season = AppDatabase.getAppDatabase(context).seasonDAO().getSeasonById(episode.getIdSeason());
        Serie serie = AppDatabase.getAppDatabase(context).seriesDAO().getSerieById(season.getSerieId());
        NotificationUtils.getInstance(context).createNotification(
                context,
                "Nouvel Episode !",
                "Serie "+serie.getOriginalName()+" "+episode.toString()+" le "+ DateUtils.getStringFromDate(episode.getAirDate()));

    }
}
