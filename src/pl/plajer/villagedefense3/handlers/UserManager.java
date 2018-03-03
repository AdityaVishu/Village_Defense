package pl.plajer.villagedefense3.handlers;

import org.bukkit.entity.Player;
import pl.plajer.villagedefense3.User;
import pl.plajer.villagedefense3.game.GameInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Tom on 27/07/2014.
 */
public class UserManager {

    private static HashMap<UUID, User> users = new HashMap<>();

    public static User getUser(UUID uuid) {
        if(users.containsKey(uuid)) {
            return users.get(uuid);
        } else {
            users.put(uuid, new User(uuid));
            return users.get(uuid);
        }
    }

    public static List<User> getUsers(GameInstance GameInstance) {
        List<User> users = new ArrayList<>();
        for(Player player : GameInstance.getPlayers()) {
            users.add(getUser(player.getUniqueId()));
        }
        return users;
    }

    public static void removeUser(UUID uuid) {
        users.remove(uuid);
    }

}
