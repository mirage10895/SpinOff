package fr.eseo.dis.amiaudluc.spinoffapp.ui.library;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.model.MovieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.model.SerieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DBInitializer.AppDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment {

    private AppDatabase db;

    // view
    private TextView seriesCunt;
    private TextView moviesCunt;
    private TextView seriesLength;
    private TextView moviesLength;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        this.db = AppDatabase.getAppDatabase(view.getContext());

        this.seriesCunt = view.findViewById(R.id.nb_series);
        this.moviesCunt = view.findViewById(R.id.nb_movies);
        this.seriesLength = view.findViewById(R.id.series_length);
        this.moviesLength = view.findViewById(R.id.movies_length);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        this.db.moviesDAO().getAll().observe(this, movieDatabases -> {
            if (movieDatabases != null) {
                this.moviesLength.setText(
                        String.valueOf(movieDatabases.stream().mapToDouble(MovieDatabase::getRuntime).sum())
                );
                this.moviesCunt.setText(String.valueOf(movieDatabases.size()));
            }
        });
        this.db.serieDAO().getAll().observe(this, serieDatabases -> {
            if (serieDatabases != null) {
                this.seriesCunt.setText(String.valueOf(serieDatabases.size()));
                this.seriesLength.setText(
                        String.valueOf(serieDatabases.stream().mapToDouble(SerieDatabase::getAverageEpisodeRunTime).sum())
                );
            }
        });
    }
}
