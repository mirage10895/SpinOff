package fr.eseo.dis.amiaudluc.spinoffapp.common;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filterable;
import android.widget.Spinner;

import java.util.ArrayList;

import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.content.Content;
import fr.eseo.dis.amiaudluc.spinoffapp.https.HttpsHandler;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Genre;
import fr.eseo.dis.amiaudluc.spinoffapp.parser.WebServiceParser;

import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 */
public class FilterKingFragment extends Fragment {


    private Context ctx;
    private ArrayAdapter<Genre> genresDisplay;
    private Filterable adapter;
    private View filterKingView;

    public FilterKingFragment() {
        // Required empty public constructor
    }

    public void instanciateFrag(Filterable filterable){
        this.adapter = filterable;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        filterKingView = inflater.inflate(R.layout.fragment_filter_king, container, false);
        ctx = filterKingView.getContext();

        Spinner spinner = filterKingView.findViewById(R.id.spinner);
        genresDisplay = new ArrayAdapter<>(ctx,R.layout.support_simple_spinner_dropdown_item);
        genresDisplay.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(genresDisplay);

        Button button = filterKingView.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            Spinner spinner = filterKingView.findViewById(R.id.spinner);
            @Override
            public void onClick(View view) {
                Genre genre = (Genre)spinner.getSelectedItem();
                String sub = Content.currentFragment.substring(Content.currentFragment.length()-6,Content.currentFragment.length());
                if (sub.equals("Movies")){
                    adapter.getFilter().filter(String.valueOf(genre.getId()));
                }else if(sub.equals("Series")){
                    //applySerieFilter(genre);
                }
                filterKingView.setVisibility(GONE);
            }
        });

        GetGenres mTaskGetGenre = new GetGenres();
        mTaskGetGenre.execute();

        return filterKingView;
    }

    private void setSpinnerData(ArrayList<Genre> allGenres){
        genresDisplay.addAll(allGenres);
        genresDisplay.notifyDataSetChanged();
    }


    /**
     * Async task class to get json by making HTTP call
     */
    private class GetGenres extends android.os.AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            HttpsHandler sh = new HttpsHandler();
            String args = "&language=en-US";

            // Making a request to url and getting response

            return sh.makeServiceCall("genre","movie/list",args);
        }

        @Override
        protected void onPostExecute(String result) {
            //Log.e("TAG",result);
            if(!result.isEmpty()) {
                setSpinnerData(WebServiceParser.multiGenresParserForFilter(result));
            }
        }

    }

}
