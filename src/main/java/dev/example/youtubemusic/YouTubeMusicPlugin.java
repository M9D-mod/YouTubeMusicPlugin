package dev.example.youtubemusic;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class YouTubeMusicPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("YouTubeMusic enabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command,
                             String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("music")) return false;

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sender.sendMessage("/music play <namespace:sound>");
            sender.sendMessage("/music stop");
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Players only.");
            return true;
        }

        if (args[0].equalsIgnoreCase("play") && args.length >= 2) {
            player.playSound(player.getLocation(), args[1], 1.0f, 1.0f);
            sender.sendMessage("Requested sound: " + args[1]);
            return true;
        }

        if (args[0].equalsIgnoreCase("stop")) {
            player.stopAllSounds();
            sender.sendMessage("Stopped sounds.");
            return true;
        }

        sender.sendMessage("Unknown command. Use /music help");
        return true;
    }
}


