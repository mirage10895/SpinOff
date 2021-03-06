package fr.eseo.dis.amiaudluc.spinoffapp.library;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.DBInitializer.AppDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.DBInitializer.DatabaseTransactionManager;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment {

    private Context ctx;
    private AppDatabase db;

    public DashboardFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        ctx = view.getContext();

        db = AppDatabase.getAppDatabase(ctx);

        TextView seriesCunt = view.findViewById(R.id.nb_series);
        TextView moviesCunt = view.findViewById(R.id.nb_movies);
        TextView seriesLength = view.findViewById(R.id.series_length);
        TextView moviesLength = view.findViewById(R.id.movies_length);

        seriesCunt.setText(String.valueOf(DatabaseTransactionManager.getAllSeries(db).size()));
        moviesCunt.setText(String.valueOf(DatabaseTransactionManager.getAllMovies(db).size()));

        return view;
    }
}
