package xyz.directplan.candorchannels.lib.storage;

import xyz.directplan.candorchannels.CandorChannel;
import xyz.directplan.candorchannels.ConfigKeys;
import xyz.directplan.candorchannels.SQLStorage;
import xyz.directplan.candorchannels.lib.storage.misc.ConnectionData;
import xyz.directplan.candorchannels.user.User;

import java.util.UUID;

/**
 * @author DirectPlan
 */

public class Storage {

    private final StorageRepository repository;

    public Storage(CandorChannel plugin) {

        String host = ConfigKeys.STORAGE_HOST.getStringValue();
        int port = ConfigKeys.STORAGE_PORT.getInteger();
        String username = ConfigKeys.STORAGE_USERNAME.getStringValue();
        String password = ConfigKeys.STORAGE_PASSWORD.getStringValue();
        String database = ConfigKeys.STORAGE_DATABASE.getStringValue();
        int maximumPoolSize = ConfigKeys.STORAGE_MAXIMUM_POOL_SIZE.getInteger();
        ConnectionData credentials = new ConnectionData(host, username, password, database, port, maximumPoolSize);

        repository = new SQLStorage(plugin, credentials);
        plugin.getLogger().info("Using " + repository.getName() + " for data storage!");
    }

    public String getName() {
        return repository.getName();
    }

    public void connect() {
        repository.connect();
    }

    public User loadPlayer(UUID uuid) {
        return repository.loadUser(uuid); // Make this an async completable future
    }

    public void saveUser(User user) { // Make this an async completable future
        repository.saveUser(user);
    }

    public void close() {
        repository.close();
    }
}