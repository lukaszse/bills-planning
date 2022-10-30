package pl.com.seremak.billsplaning.utils;

import org.springframework.web.server.ServerErrorException;
import pl.com.seremak.billsplaning.model.Metadata;
import pl.com.seremak.billsplaning.model.VersionedEntity;

import java.time.Instant;

public class VersionedEntityUtils {

    public static final String INTERNAL_DATA_ERROR_MESSAGE = "Internal data error occurred.";

    public static <T> T setMetadata(final T versionedEntity) {
        if (versionedEntity instanceof VersionedEntity) {
            ((VersionedEntity) versionedEntity).setMetadata(
                    Metadata.builder()
                            .createdAt(Instant.now())
                            .modifiedAt(Instant.now())
                            .version(1L)
                            .build());
            return versionedEntity;
        } else throw new ServerErrorException(INTERNAL_DATA_ERROR_MESSAGE);
    }
}
