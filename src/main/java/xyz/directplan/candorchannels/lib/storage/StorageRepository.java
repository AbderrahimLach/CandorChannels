package xyz.directplan.candorchannels.lib.storage;

import xyz.directplan.candorchannels.user.User;

import java.util.UUID;

/**
 * @author DirectPlan
 */
public interface StorageRepository extends StorageConnection {

    User loadUser(UUID uuid);

    void saveUser(User user);
}
