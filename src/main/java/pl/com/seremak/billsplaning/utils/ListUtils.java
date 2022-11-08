package pl.com.seremak.billsplaning.utils;

import java.util.ArrayList;
import java.util.List;

public class ListUtils {

    public static <E> List<E> addAndReturn(final List<E> list, final E element) {
        final List<E> newList = new ArrayList<>(list);
        newList.add(element);
        return newList;
    }
}
