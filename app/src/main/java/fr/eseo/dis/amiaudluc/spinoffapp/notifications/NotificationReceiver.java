package fr.eseo.dis.amiaudluc.spinoffapp.notifications;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import java.util.List;

import fr.eseo.dis.amiaudluc.spinoffapp.MainActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.Utils.ConstUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.Utils.DateUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.Utils.LogUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.Utils.NotificationUtils;
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
