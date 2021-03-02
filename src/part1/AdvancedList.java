package part1;

import java.util.Comparator;

public interface AdvancedList<T> extends SimpleList<T> {
    AdvancedList<T> shuffle();
    AdvancedList<T> sort(int i, int i1, Comparator<T> comparator);
}
