package fr.eseo.dis.amiaudluc.spinoffapp.calendar;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.Utils.LogUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.common.SearchInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.content.Content;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.DBInitializer.AppDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.DBInitializer.DatabaseTransactionManager;
import fr.eseo.dis.amiaudluc.spinoffapp.episode.EpisodeActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Episode;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Event;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Season;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.DBOptimizer.SerieWithSeasons;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment implements SearchInterface {

    private static final String TAG = CalendarFragment.class.getSimpleName();
    private Context ctx;
    private AppDatabase db;
    private EventAdapter eventAdapter;
    private ArrayList<Event> events;
    private ArrayList<Event> today = new ArrayList<>();
    private String type;
    private MaterialCalendarView materialCalendarView;


    public CalendarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View myCalendarView = inflater.inflate(R.layout.fragment_calendar, container, false);
            this.ctx = myCalendarView.getContext();
            db = AppDatabase.getAppDatabase(ctx);
            events = new ArrayList<>();

            materialCalendarView = myCalendarView.findViewById(R.id.calendar);
            final RecyclerView recycler = myCalendarView.findViewById(R.id.events);
            recycler.setHasFixedSize(true);
            recycler.setLayoutManager(new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false));
            eventAdapter = new EventAdapter(this, ctx, today);
            recycler.setAdapter(eventAdapter);
            recycler.setVisibility(View.GONE);

            //myCalendarView.findViewById(R.id.fab).setVisibility(View.GONE);

            GetCalendar mGetCalendar = new GetCalendar();
            mGetCalendar.execute();

            materialCalendarView.setOnDateChangedListener((widget, date, selected) -> {
                today = new ArrayList<>(events.stream().filter(event -> event.getDate().equals(date.getDate())).collect(Collectors.toList()));
                eventAdapter.setEvent(today);
                eventAdapter.notifyDataSetChanged();
                recycler.setVisibility(View.VISIBLE);
            });

            return myCalendarView;
        }

        @Override
        public void setType (String type){
            this.type = type;
        }

        @Override
        public void onItemClick ( int position){
            if (this.type.equals("event")) {
                Content.currentEpisode = this.today.get(position).getEpisode();
                Content.currentSeason = DatabaseTransactionManager.getSeasonById(db, this.today.get(position).getEpisode().getIdSeason());
                Content.currentSerie = DatabaseTransactionManager.getSerieById(db, Content.currentSeason.getSerieId());
                Intent intent = new Intent(ctx, EpisodeActivity.class);
                startActivity(intent);
            }
        }

        @Override
        public void onCreateCtxMenu (ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo
        menuInfo,int position){

        }

        private class GetCalendar extends AsyncTask<Void, Void, List<SerieWithSeasons>>{

            @Override
            protected List<SerieWithSeasons> doInBackground(Void... params) {
                return db.serieWithSeasonsDAO().getSerieWithSeasons();
            }

            @Override
            protected void onPostExecute(List<SerieWithSeasons> series) {
                ArrayList<CalendarDay> listAirDate = new ArrayList<>();
                //List<SerieWithSeasons> series = db.serieWithSeasonsDAO().getSerieWithSeasons();
                for (SerieWithSeasons serie : series) {
                    //List<Season> seasons = db.seasonDAO().getSeasonsBySerieId(serie.getId());
                    List<Season> seasons = serie.seasonList;
                    if (!seasons.isEmpty()) {
                        Season last = seasons.get(seasons.size() - 1);
                        List<Episode> episodes = last.getFutureEpisodes();
                        for (Episode episode : episodes) {
                            Event event = new Event(episode.getAirDate(), episode);
                            CalendarDay cd = CalendarDay.from(episode.getAirDate());
                            events.add(event);
                            listAirDate.add(cd);
                        }
                        episodes = last.getOldEpisodes();
                        for (Episode episode : episodes) {
                            Event event = new Event(episode.getAirDate(), episode);
                            events.add(event);
                        }
                    }
                }
                materialCalendarView.addDecorator(new EventHandler(ctx.getColor(R.color.colorAccent), listAirDate));
            }
        }
}
