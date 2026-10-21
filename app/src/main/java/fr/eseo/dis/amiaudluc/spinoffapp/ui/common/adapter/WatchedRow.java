package fr.eseo.dis.amiaudluc.spinoffapp.ui.common.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.util.ArrayList;
import java.util.List;

import fr.eseo.dis.amiaudluc.spinoffapp.utils.AlphabetUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.library.WatchedMediaItem;

/**
 * A row of the watched list: either a letter divider or a media tile.
 * <p>
 * The letter dividers are purely visual: the screen has no index bar, so nothing navigates to
 * a section for now. Grouping relies on the incoming list being ordered by section letter,
 * which {@link fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.library.WatchedListBuilder} guarantees.
 */
public sealed interface WatchedRow {
    String getReference();

    DiffUtil.ItemCallback<WatchedRow> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull WatchedRow oldItem, @NonNull WatchedRow newItem) {
            if (oldItem instanceof LetterHeader oldHeader
                    && newItem instanceof LetterHeader newHeader) {
                return oldHeader.letter() == newHeader.letter();
            }
            if (oldItem instanceof MediaTile oldTile && newItem instanceof MediaTile newTile) {
                return oldTile.item().id() == newTile.item().id()
                        && oldTile.item().fragmentType() == newTile.item().fragmentType();
            }
            return false;
        }

        @Override
        public boolean areContentsTheSame(@NonNull WatchedRow oldItem, @NonNull WatchedRow newItem) {
            return oldItem.getReference().equals(newItem.getReference());
        }
    };

    /**
     * Splits an alphabetically ordered list of tiles into letter dividers followed by their
     * tiles. Each divider carries the number of tiles it introduces.
     */
    static List<WatchedRow> group(List<WatchedMediaItem> items) {
        List<WatchedRow> rows = new ArrayList<>();
        if (items == null || items.isEmpty()) {
            return rows;
        }

        char currentLetter = 0;
        List<WatchedMediaItem> currentRun = new ArrayList<>();

        for (WatchedMediaItem item : items) {
            char letter = AlphabetUtils.letterOf(item.title());
            if (letter != currentLetter && !currentRun.isEmpty()) {
                appendRun(rows, currentLetter, currentRun);
                currentRun.clear();
            }
            currentLetter = letter;
            currentRun.add(item);
        }
        appendRun(rows, currentLetter, currentRun);
        return rows;
    }

    private static void appendRun(List<WatchedRow> rows, char letter, List<WatchedMediaItem> run) {
        if (run.isEmpty()) {
            return;
        }
        rows.add(new LetterHeader(letter, run.size()));
        for (WatchedMediaItem item : run) {
            rows.add(new MediaTile(item));
        }
    }

    /** Section divider showing the letter and how many tiles it introduces. */
    record LetterHeader(char letter, int count) implements WatchedRow {
        @Override
        public String getReference() {
            return String.valueOf(letter);
        }
    }

    /** A media tile. */
    record MediaTile(WatchedMediaItem item) implements WatchedRow {
        @Override
        public String getReference() {
            return String.valueOf(item.id());
        }
    }
}
