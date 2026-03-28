package fr.eseo.dis.amiaudluc.spinoffapp.ui.calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.spinoffapp.services.model.Event;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.ItemInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.DateUtils;

/**
 * Created by lucasamiaud on 16/03/2018.
 */

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventsViewHolder> {

    private final ItemInterface fragment;
    private final Context ctx;

    private List<Event> events;

    EventAdapter(ItemInterface fragment, Context ctx, List<Event> eventList) {
        this.fragment = fragment;
        this.ctx = ctx;
        this.setEvent(eventList);
    }

    @NonNull
    @Override
    public EventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mySerieView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_calendar, parent, false);
        return new EventsViewHolder(mySerieView);
    }

    public void setEvent(List<Event> events) {
        this.events = events;
    }

    @Override
    public void onBindViewHolder(@NonNull EventsViewHolder holder, int position) {
        if (getItemCount() != 0) {
            Event event = events.get(position);

            if (event.getPosterPath() != null) {
                String link = ctx.getResources().getString(R.string.base_url_poster_500) + event.getPosterPath();
                Picasso.get()
                        .load(link)
                        .fit()
                        .error(R.drawable.ic_launcher_foreground)
                        .into(holder.seriePoster);
            }

            holder.textEpisode.setText(R.string.emptyField);
            if (!"".equals(event.getName())) {
                holder.textEpisode.setText(event.getName());
            }

            holder.textAirDate.setText(R.string.emptyField);
            if (event.getDate() != null) {
                holder.textAirDate.setText(DateUtils.toDisplayString(event.getDate()));
            }
        }
    }

    @Override
    public int getItemCount() {
        return this.events.size();
    }


    public class EventsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView seriePoster;
        private final TextView textEpisode;
        private final TextView textAirDate;

        EventsViewHolder(View view) {
            super(view);

            seriePoster = view.findViewById(R.id.poster_ic);
            textEpisode = view.findViewById(R.id.episode);
            textAirDate = view.findViewById(R.id.air_date);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            fragment.onItemClick(getAdapterPosition(), FragmentType.EVENT);
        }
    }
}
