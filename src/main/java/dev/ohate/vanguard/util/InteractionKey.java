package dev.ohate.vanguard.util;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class InteractionKey {

    private final String key;
    private final UUID id;
    private final String action;

    public static InteractionKey fromString(String componentId) {
        return fromArray(componentId.split(":"));
    }

    public static InteractionKey fromArray(String[] array) {
        if (array.length != 3) {
            return null;
        }

        return new InteractionKey(
                array[0],
                UUID.fromString(array[1]),
                array[2]
        );
    }

}
