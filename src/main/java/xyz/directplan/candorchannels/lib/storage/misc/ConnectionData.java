package xyz.directplan.candorchannels.lib.storage.misc;

import lombok.Data;

/**
 * @author DirectPlan
 */

@Data
public class ConnectionData {

    private final String host, username, password, database;
    private final int port, maximumPoolSize;
}
