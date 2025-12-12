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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.common.SearchInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DBInitializer.AppDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Event;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.episode.EpisodeActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment implements SearchInterface {

    private static final String TAG = CalendarFragment.class.getSimpleName();
    private Context ctx;
    private AppDatabase db;

    private RecyclerView recycler;
    private MaterialCalendarView materialCalendarView;

    private EventAdapter eventAdapter;
    private List<Event> events;
    private List<Event> today;
    private FragmentType type;


    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myCalendarView = inflater.inflate(R.layout.fragment_calendar, container, false);
        this.ctx = myCalendarView.getContext();
        this.db = AppDatabase.getAppDatabase(ctx);
        this.events = new ArrayList<>();
        this.materialCalendarView = myCalendarView.findViewById(R.id.calendar);
        this.recycler = myCalendarView.findViewById(R.id.events);

        return myCalendarView;
    }

    @Override
    public void onStart() {
        super.onStart();
        this.db.episodeDAO().getAllEpisodesBySerie().observe(this, calendarBeans -> {
            if (calendarBeans != null) {
                this.events = calendarBeans.stream()
                        .map(episodeDatabase -> new Event(
                                episodeDatabase.getAirDate(),
                                episodeDatabase.getSeriePosterPath(),
                                episodeDatabase.getEpisodeName(),
                                episodeDatabase.getWatched()
                        ))
                        .collect(Collectors.toList());
            }
        });

        this.recycler.setHasFixedSize(true);
        this.recycler.setLayoutManager(new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false));
        this.recycler.setAdapter(this.eventAdapter);
        this.recycler.setVisibility(View.GONE);

        this.eventAdapter = new EventAdapter(this, ctx, today);

        this.materialCalendarView.setOnDateChangedListener((widget, date, selected) -> {
            this.today = this.events.stream().filter(event -> event.getDate()
                    .isEqual(LocalDate.ofEpochDay(date.getDate().getTime())))
                    .collect(Collectors.toList());
            this.eventAdapter.setEvent(today);
            this.eventAdapter.notifyDataSetChanged();
            this.recycler.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void setType(FragmentType type) {
        this.type = type;
    }

    @Override
    public void onItemClick(Integer position) {
        if (this.type.equals(FragmentType.EVENT)) {
            //Content.currentSeason = DatabaseTransactionManager.getSeasonById(db, this.today.get(position).getEpisode().getIdSeason());
            //Content.currentSerie = DatabaseTransactionManager.getSerieById(db, Content.currentSeason.getSerieId());
            Intent intent = new Intent(ctx, EpisodeActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onCreateCtxMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo, Integer position) {

    }
}
