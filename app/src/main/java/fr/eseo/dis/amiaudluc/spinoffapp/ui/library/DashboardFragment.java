package fr.eseo.dis.amiaudluc.spinoffapp.ui.library;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Objects;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.MovieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SerieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.MovieViewModel;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.SerieViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment {

    private MovieViewModel movieViewModel;
    private SerieViewModel serieViewModel;

    // view
    private TextView seriesCunt;
    private TextView moviesCunt;
    private TextView seriesLength;
    private TextView moviesLength;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        this.movieViewModel = new ViewModelProvider(requireActivity()).get(MovieViewModel.class);
        this.serieViewModel = new ViewModelProvider(requireActivity()).get(SerieViewModel.class);

        this.movieViewModel.initDatabaseMovies();
        this.serieViewModel.initDatabaseSeries();

        this.seriesCunt = view.findViewById(R.id.nb_series);
        this.moviesCunt = view.findViewById(R.id.nb_movies);
        this.seriesLength = view.findViewById(R.id.series_length);
        this.moviesLength = view.findViewById(R.id.movies_length);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        this.movieViewModel.getDatabaseMovies().observe(this, movieDatabases -> {
            if (movieDatabases != null) {
                this.moviesLength.setText(
                        String.valueOf(movieDatabases.stream().mapToDouble(MovieDatabase::getRuntime).sum())
                );
                this.moviesCunt.setText(String.valueOf(movieDatabases.size()));
            }
        });
        this.serieViewModel.getDatabaseSeries().observe(this, serieDatabases -> {
            if (serieDatabases != null) {
                this.seriesCunt.setText(String.valueOf(serieDatabases.size()));
                this.seriesLength.setText(
                        String.valueOf(
                                serieDatabases.stream()
                                        .mapToInt(SerieDatabase::getRuntime)
                                        .filter(Objects::nonNull)
                                        .sum()
                        )
                );
            }
        });
    }
}
