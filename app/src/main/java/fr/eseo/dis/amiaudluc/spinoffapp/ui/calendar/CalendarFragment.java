package fr.eseo.dis.amiaudluc.spinoffapp.ui.calendar;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DBInitializer.AppDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.services.model.Event;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.SearchInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.episode.EpisodeActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment implements SearchInterface {
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
                                episodeDatabase.isWatched()
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
            Intent intent = new Intent(ctx, EpisodeActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onRegisterContextMenu(View view, Integer id) {
        // unused
    }
}
