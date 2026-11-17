package fr.eseo.dis.amiaudluc.spinoffapp.ui.common.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.library.LibraryItem;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.library.LibrarySort;

/**
 * A row of the library list screen: either a section divider or a media tile.
 * <p>
 * The dividers are purely visual: the screen has no index bar, so nothing navigates to a section
 * for now. Grouping relies on the incoming list being ordered by the active {@link LibrarySort},
 * which {@link fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.library.LibraryBuilder} guarantees, so a
 * single pass is enough to turn the tiles into dividers followed by their tiles.
 */
public sealed interface LibraryRow {
    String getReference();

    DiffUtil.ItemCallback<LibraryRow> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull LibraryRow oldItem, @NonNull LibraryRow newItem) {
            if (oldItem instanceof SectionHeader oldHeader
                    && newItem instanceof SectionHeader newHeader) {
                return oldHeader.sort() == newHeader.sort()
                        && Objects.equals(oldHeader.value(), newHeader.value());
            }
            if (oldItem instanceof MediaTile oldTile && newItem instanceof MediaTile newTile) {
                return oldTile.item().id() == newTile.item().id()
                        && oldTile.item().fragmentType() == newTile.item().fragmentType();
            }
            return false;
        }

        @Override
        public boolean areContentsTheSame(@NonNull LibraryRow oldItem, @NonNull LibraryRow newItem) {
            return oldItem.getReference().equals(newItem.getReference());
        }
    };

    /**
     * Splits a list ordered by {@code sort} into section dividers followed by their tiles. Each
     * divider carries the raw section value and the number of tiles it introduces.
     */
    static List<LibraryRow> group(List<LibraryItem> items, LibrarySort sort) {
        List<LibraryRow> rows = new ArrayList<>();
        if (items == null || items.isEmpty()) {
            return rows;
        }

        LibrarySort activeSort = sort != null ? sort : LibrarySort.ALPHABETICAL;
        String currentSection = activeSort.sectionValue(items.get(0));
        List<LibraryItem> currentRun = new ArrayList<>();

        for (LibraryItem item : items) {
            String section = activeSort.sectionValue(item);
            if (!currentRun.isEmpty() && !Objects.equals(section, currentSection)) {
                appendRun(rows, activeSort, currentSection, currentRun);
                currentRun.clear();
                currentSection = section;
            }
            currentRun.add(item);
        }
        appendRun(rows, activeSort, currentSection, currentRun);
        return rows;
    }

    private static void appendRun(
            List<LibraryRow> rows, LibrarySort sort, String section, List<LibraryItem> run) {
        if (run.isEmpty()) {
            return;
        }
        rows.add(new SectionHeader(sort, section, run.size()));
        for (LibraryItem item : run) {
            rows.add(new MediaTile(item));
        }
    }

    /**
     * Section divider showing the raw section value and how many tiles it introduces. The value is
     * {@code null} for the "unknown" section, which the adapter renders with its own label.
     */
    record SectionHeader(LibrarySort sort, String value, int count) implements LibraryRow {
        @Override
        public String getReference() {
            // The count belongs to the content: a divider must rebind when its tile count changes.
            return sort + ":" + value + ":" + count;
        }
    }

    /** A media tile. */
    record MediaTile(LibraryItem item) implements LibraryRow {
        @Override
        public String getReference() {
            return String.valueOf(item.id());
        }
    }
}
