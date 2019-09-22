package fr.eseo.dis.amiaudluc.spinoffapp.ui.library;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DBInitializer.AppDatabase;

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
        
        db.moviesDAO().getAll().observe(this, movieDatabases -> moviesCunt.setText(String.valueOf(movieDatabases.size())));
        db.serieDAO().getAll().observe(this, serieDatabases -> moviesCunt.setText(String.valueOf(serieDatabases.size())));

        return view;
    }
}
