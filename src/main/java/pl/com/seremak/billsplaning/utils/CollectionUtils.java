package pl.com.seremak.billsplaning.utils;

import pl.com.seremak.billsplaning.exceptions.DuplicatedElementsException;
import pl.com.seremak.billsplaning.exceptions.NotFoundException;

import java.util.Collection;

public class CollectionUtils {

    public static <E> E getSoleElementOrThrowException(final Collection<E> collection) {
        return getSoleElementOrThrowException(collection, true);
    }

    @SuppressWarnings("all")
    public static <E> E getSoleElementOrThrowException(final Collection<E> collection, final boolean required) {
        if (collection.size() > 1) {
            throw new DuplicatedElementsException();
        }
        if (required && collection.isEmpty()) {
            throw new NotFoundException();
        }
        return collection.stream()
                .findFirst()
                .orElse(null);
    }
}
