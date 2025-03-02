package utils;

import org.junit.jupiter.api.Assertions;

import java.lang.reflect.Field;
import java.util.UUID;

public class TestUtils {

    /**
     * Sets the private ID field of a BaseEntity subclass using reflection.
     * This is used in tests to assign specific UUIDs to entities that would
     * normally get their IDs from the database.
     *
     * @param entity The entity object to set the ID on
     * @param id The UUID to set as the entity's ID
     */
    public static void setPrivateId(Object entity, UUID id) {
        try {
            Field idField = entity.getClass().getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (Exception e) {
            Assertions.fail("Failed to set ID: " + e.getMessage());
        }
    }
}