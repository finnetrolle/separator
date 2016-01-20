package ru.finnetrolle.separator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by finnetrolle on 21.01.2016.
 */
public class Separator {

    private static <IN, OUT, EX extends Exception> Function<IN, OUT> safeApply(Task<IN, OUT, EX> task) {
        return t -> {
            try {
                return task.apply(t);
            } catch (Exception e) {
                throwAsUnchecked(e);
            }
            return null;
        };
    }

    @SuppressWarnings ("unchecked")
    private static <E extends Throwable> void throwAsUnchecked(Exception e) throws E {
        throw (E)e;
    }

    public interface Task<IN, OUT, EX extends Exception> {
        OUT apply(IN obj) throws EX;
    }

    public static <IN, OUT, EX extends Exception> List<OUT> forkJoin(List<IN> obj, Task<List<IN>, List<OUT>, EX> task, int forksCount) throws EX {
        return separate(obj, forksCount).parallelStream()
                .map(safeApply(task::apply))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private static <T> List<List<T>> separate(List<T> obj, int forksCount) {
        int cnt = obj.size() / forksCount;
        List<List<T>> accumulator = new ArrayList<>();

        for (int i = 0; i < cnt; ++i) {
            accumulator.add(new ArrayList<>());
        }

        int j = 0;
        for (int i = 0; i < obj.size(); ++i) {
            accumulator.get(j).add(obj.get(i));
            j = (j >= cnt - 1) ? j + 1 : 0;
        }

        return accumulator;
    }

}
