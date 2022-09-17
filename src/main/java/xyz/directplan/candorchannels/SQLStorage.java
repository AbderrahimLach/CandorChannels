package xyz.directplan.candorchannels;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import xyz.directplan.candorchannels.channel.Channel;
import xyz.directplan.candorchannels.channel.ChannelManager;
import xyz.directplan.candorchannels.lib.storage.StorageRepository;
import xyz.directplan.candorchannels.lib.storage.misc.ConnectionData;
import xyz.directplan.candorchannels.user.User;

import java.sql.*;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author DirectPlan
 */
@Data
public class SQLStorage implements StorageRepository {


    private final CandorChannel plugin;
    private final ConnectionData connectionData;
    private HikariDataSource dataSource;
    private final String name = "MySQL";

    private final String DATABASE_TABLE = "users_channel";

    @Override
    public void connect() {

        HikariConfig config = new HikariConfig();

        config.setPoolName("CandorChannels by DirectPlan - MySQL Connection Pool");

        config.setMaximumPoolSize(connectionData.getMaximumPoolSize());

        config.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");

        Properties properties = new Properties();
        properties.put("serverName", connectionData.getHost());
        properties.put("port", connectionData.getPort());
        properties.put("databaseName", connectionData.getDatabase());
        properties.put("user", connectionData.getUsername());
        properties.put("password", connectionData.getPassword());
        config.setDataSourceProperties(properties);

        this.dataSource = new HikariDataSource(config);

        plugin.getLogger().info("MySQL Connection has been established!");
        initTables();
    }

    @Override
    public void close() {
        dataSource.close();
    }

    @Override
    public User loadUser(UUID uuid) {
        User user = new User(uuid);
        ChannelManager channelManager = plugin.getChannelManager();
        requestConnection(connection -> {
            try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + DATABASE_TABLE + " WHERE uuid = ?")) {
                ps.setString(1, uuid.toString());

                try (ResultSet result = ps.executeQuery()) {
                    if(result.next()) {
                        String currentChannel = result.getString(2);
                        Channel channel = channelManager.getChannel(currentChannel);
                        if(channel == null) return;
                        channelManager.switchChannel(user, channel);
                    }
                }
            }catch (SQLException e) {
                e.printStackTrace();
            }
         });
        return user;
    }

    @Override
    public void saveUser(User user) {
        String stringUuid = user.getUuid().toString();
        requestConnection(connection -> {
            try (PreparedStatement ps = connection.prepareStatement("INSERT INTO " + DATABASE_TABLE + "(uuid, current_channel) VALUES (?,?) " +
                    "ON DUPLICATE KEY UPDATE current_channel = ?")) {
                ps.setString(1, stringUuid);
                Channel channel = user.getCurrentChannel();
                String channelName = channel == null ? null : channel.getName();
                ps.setString(2, channelName);
                ps.setString(3, channelName);
                ps.executeUpdate();
            }catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void requestConnection(Consumer<Connection> consumer) {
        try (Connection connection = dataSource.getConnection()) {
            consumer.accept(connection);
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void initTables() {
        requestConnection(connection -> {
            try (Statement statement = connection.createStatement()) {
                statement.addBatch("CREATE TABLE IF NOT EXISTS " + DATABASE_TABLE +
                        "(uuid varchar(36), current_channel TEXT, CONSTRAINT players_pk PRIMARY KEY (uuid));");
                statement.executeBatch();
            }catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
