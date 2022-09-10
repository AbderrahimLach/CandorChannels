package xyz.directplan.candorchannels.user;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChainFactory;
import lombok.Getter;
import org.bukkit.entity.Player;
import xyz.directplan.candorchannels.CandorChannel;
import xyz.directplan.candorchannels.lib.storage.Storage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * @author DirectPlan
 */
public class UserManager {

    @Getter
    private final Map<UUID, User> users = new HashMap<>();

    private final Logger logger;
    private final Storage storage;
    private final TaskChainFactory taskChainFactory;

    public UserManager(CandorChannel plugin) {
        logger = plugin.getLogger();
        storage = plugin.getStorage();
        taskChainFactory = BukkitTaskChainFactory.create(plugin);
    }

    public User getUser(Player player) {
        return getUser(player.getUniqueId());
    }
    public User getUser(UUID uuid) {
        return users.get(uuid);
    }

    public void handleJoin(Player player) {
        UUID uuid = player.getUniqueId();
        logger.info("Loading user " + player.getName() + "...");
        loadUser(uuid, user -> {
            user.setPlayer(player);
            users.put(uuid, user);
        });
    }

    public void loadUser(UUID uuid, Consumer<User> consumer) {
        taskChainFactory.newChain().asyncFirst(() -> {
            User user = getUser(uuid);
            if(user == null) {
                user = storage.loadPlayer(uuid);
            }
            return user;
        }).syncLast(consumer::accept).execute();
    }

    public void handleQuit(Player player) {
        User removed = users.remove(player.getUniqueId());
        saveUser(removed);
    }

    public void saveUser(User user) {
        taskChainFactory.newChain().async(() -> storage.saveUser(user)).execute();
    }

    /* This bulk update operation is synchronous. Should only be executed on shutdown */
    public void saveAllUsers() {
        users.forEach((uuid, user) -> saveUser(user));
    }
}
