package fr.eseo.dis.amiaudluc.spinoffapp.ui.calendar;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.common.SearchInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.content.Content;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.DBInitializer.AppDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.episode.EpisodeActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Event;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment implements SearchInterface {

    private static final String TAG = CalendarFragment.class.getSimpleName();
    private Context ctx;
    private AppDatabase db;
    private EventAdapter eventAdapter;
    private List<Event> events;
    private List<Event> today;
    private String type;
    private MaterialCalendarView materialCalendarView;


    public CalendarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View myCalendarView = inflater.inflate(R.layout.fragment_calendar, container, false);
            this.ctx = myCalendarView.getContext();
            db = AppDatabase.getAppDatabase(ctx);
            this.events = new ArrayList<>();
            db.episodeDAO().getAllEpisodesBySerie().observe(this, calendarBeans -> {
                if (calendarBeans!= null) {
                    events = calendarBeans.stream()
                            .map(episodeDatabase -> new Event(episodeDatabase.getAirDate(), episodeDatabase.getSeriePosterPath(), episodeDatabase.getEpisodeName(), episodeDatabase. getWatched()))
                            .collect(Collectors.toList());
                }
            });
            this.materialCalendarView = myCalendarView.findViewById(R.id.calendar);
            final RecyclerView recycler = myCalendarView.findViewById(R.id.events);
            recycler.setHasFixedSize(true);
            recycler.setLayoutManager(new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false));
            this.eventAdapter = new EventAdapter(this, ctx, today);
            recycler.setAdapter(this.eventAdapter);
            recycler.setVisibility(View.GONE);

            materialCalendarView.setOnDateChangedListener((widget, date, selected) -> {
                today = events.stream().filter(event -> event.getDate()
                        .equals(date.getDate()))
                        .collect(Collectors.toCollection(ArrayList::new));
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
        public void onItemClick (Integer position){
            if (this.type.equals("event")) {
                //Content.currentSeason = DatabaseTransactionManager.getSeasonById(db, this.today.get(position).getEpisode().getIdSeason());
                //Content.currentSerie = DatabaseTransactionManager.getSerieById(db, Content.currentSeason.getSerieId());
                Intent intent = new Intent(ctx, EpisodeActivity.class);
                startActivity(intent);
            }
        }

        @Override
        public void onCreateCtxMenu (ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo, Integer position){

        }
}
