package xyz.directplan.candorchannels.lib.storage;

/**
 * @author DirectPlan
 */
public interface StorageConnection {

    String getName();

    void connect();

    void close();
}
