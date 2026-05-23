package fr.eseo.dis.amiaudluc.spinoffapp.ui.common.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;

public class ArraySearchAdapter<T> extends ArrayAdapter<T> {
    private final List<T> elements;
    private final Function<T, String> fieldMapper;

    public ArraySearchAdapter(
            @NonNull @android.support.annotation.NonNull Context context,
            int resource,
            List<T> elements,
            Function<T, String> fieldMapper
    ) {
        super(context, resource);
        this.elements = elements;
        this.fieldMapper = fieldMapper;
    }

    @NonNull
    @android.support.annotation.NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected Filter.FilterResults performFiltering(CharSequence constraint) {
                Filter.FilterResults results = new Filter.FilterResults();
                List<T> suggestions;
                if (constraint == null || constraint.length() == 0) {
                    suggestions = new ArrayList<>(elements);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    suggestions = elements.stream()
                            .filter(c -> fieldMapper.apply(c).toLowerCase().contains(filterPattern))
                            .collect(Collectors.toList());
                }
                results.values = suggestions;
                results.count = suggestions.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
                clear();
                if (results != null && results.count > 0) {
                    addAll((List<T>) results.values);
                }
                notifyDataSetChanged();
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                return fieldMapper.apply((T) resultValue);
            }
        };
    }
}
