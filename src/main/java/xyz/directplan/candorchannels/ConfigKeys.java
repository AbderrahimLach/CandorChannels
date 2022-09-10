package xyz.directplan.candorchannels;

import lombok.Getter;
import lombok.Setter;
import xyz.directplan.candorchannels.lib.config.ConfigEntry;
import xyz.directplan.candorchannels.lib.config.replacement.ReplacementBoundary;

import java.util.HashMap;
import java.util.Map;

/**
 * @author DirectPlan
 */
@Getter
public enum ConfigKeys implements ConfigEntry {

    STORAGE_HOST("storage.host", "localhost"),
    STORAGE_PORT("storage.port", 3306),
    STORAGE_USERNAME("storage.username", "username"),
    STORAGE_PASSWORD("storage.password", "password"),
    STORAGE_DATABASE("storage.database", "database"),
    STORAGE_MAXIMUM_POOL_SIZE("storage.maximum-pool-size",10),

    ;
    private final String key;
    private final boolean forcedEntryDeclaration;
    @Setter private Object value;
    private final Map<String, ReplacementBoundary> replacementBoundaries = new HashMap<>();

    ConfigKeys(String key, Object defaultValue, boolean overwrite) {
        this.key = key;
        this.value = defaultValue;

        this.forcedEntryDeclaration = overwrite;
    }

    ConfigKeys(String key, Object value) {
        this(key, value, true);
    }
}
