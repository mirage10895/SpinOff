package fr.eseo.dis.amiaudluc.spinoffapp.ui.common.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;

/**
 * ArrayAdapter used to feed a {@code MaterialAutoCompleteTextView}.
 * <p>
 * By default the list is searchable (the dropdown is filtered while the user types). Pass
 * {@code searchable = false} to turn it into a plain non-editable select: the whole list is always
 * returned, whatever text is currently displayed in the field.
 */
public class ArraySearchAdapter<T> extends ArrayAdapter<T> {
    private final List<T> elements;
    private final Function<T, String> fieldMapper;
    private final boolean searchable;

    public ArraySearchAdapter(
            @NonNull Context context,
            int resource,
            List<T> elements,
            Function<T, String> fieldMapper
    ) {
        this(context, resource, elements, fieldMapper, true);
    }

    public ArraySearchAdapter(
            @NonNull Context context,
            int resource,
            List<T> elements,
            Function<T, String> fieldMapper,
            boolean searchable
    ) {
        // Feed the internal ArrayAdapter list so the dropdown has content before any filtering.
        super(context, resource, new ArrayList<>(elements));
        this.elements = elements;
        this.fieldMapper = fieldMapper;
        this.searchable = searchable;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected Filter.FilterResults performFiltering(CharSequence constraint) {
                Filter.FilterResults results = new Filter.FilterResults();
                List<T> suggestions;
                if (!searchable || constraint == null || constraint.isEmpty()) {
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
