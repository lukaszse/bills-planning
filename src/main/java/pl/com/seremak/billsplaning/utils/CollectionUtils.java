package pl.com.seremak.billsplaning.utils;

import pl.com.seremak.billsplaning.exceptions.DuplicatedUniqueElementsException;
import pl.com.seremak.billsplaning.exceptions.NotFoundException;

import java.util.Collection;

public class CollectionUtils {

    public static <E> E getSoleElementOrThrowException(final Collection<E> collection) {
        if (collection.size() > 1) {
            throw new DuplicatedUniqueElementsException();
        }
        return collection.stream()
                .findFirst()
                .orElseThrow(NotFoundException::new);
    }
}
