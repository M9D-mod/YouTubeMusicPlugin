package dev.example.youtubemusic;

import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URI;
import java.util.Locale;
import java.util.regex.Pattern;

public final class YouTubeMusicPlugin extends JavaPlugin {
    private static final Pattern SOUND_KEY =
            Pattern.compile("[a-z0-9_.-]+:[a-z0-9_./-]+");

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getLogger().info("YouTubeMusic enabled. Use /music help.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("music")) return false;

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sender.sendMessage("§b/music play <namespace:sound> §7- Play a sound from the resource pack");
            sender.sendMessage("§b/music stop §7- Stop the current music category");
            sender.sendMessage("§b/music pack §7- Request the configured resource pack");
            sender.sendMessage("§b/music youtube <URL> §7- Validate a YouTube link for authorized import");
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);
        switch (sub) {
            case "play" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("Players only.");
                    return true;
                }
                if (!sender.hasPermission("youtubemusic.play")) {
                    sender.sendMessage("§cYou don't have permission.");
                    return true;
                }
                if (args.length < 2 || !SOUND_KEY.matcher(args[1]).matches()) {
                    sender.sendMessage("§cUsage: /music play <namespace:sound>");
                    sender.sendMessage("§7The sound event must exist in the resource pack.");
                    return true;
                }
                float volume = (float) getConfig().getDouble("playback.default-volume", 1.0);
                float max = (float) getConfig().getDouble("playback.max-volume", 1.0);
                volume = Math.max(0.0f, Math.min(volume, max));
                player.stopSound(SoundCategory.MASTER);
                player.playSound(player.getLocation(), args[1], SoundCategory.MASTER, volume, 1.0f);
                player.sendMessage("§aPlaying sound event: §f" + args[1]);
                return true;
            }
            case "stop" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("Players only.");
                    return true;
                }
                if (!sender.hasPermission("youtubemusic.control")) {
                    sender.sendMessage("§cYou don't have permission.");
                    return true;
                }
                player.stopSound(SoundCategory.MASTER);
                sender.sendMessage("§eStopped sounds in the MASTER category.");
                return true;
            }
            case "pack" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("Players only.");
                    return true;
                }
                if (!sender.hasPermission("youtubemusic.play")) {
                    sender.sendMessage("§cYou don't have permission.");
                    return true;
                }
                String url = getConfig().getString("resource-pack.url", "").trim();
                String sha1 = getConfig().getString("resource-pack.sha1", "").trim();
                if (!isHttpsUrl(url)) {
                    sender.sendMessage("§cConfigure a valid HTTPS resource-pack URL in plugins/YouTubeMusic/config.yml");
                    return true;
                }
                if (!sha1.isEmpty() && !sha1.matches("(?i)[0-9a-f]{40}")) {
                    sender.sendMessage("§cresource-pack.sha1 must be a 40-character SHA-1 hex digest, or empty.");
                    return true;
                }
                if (sha1.isEmpty()) player.setResourcePack(url);
                else player.setResourcePack(url, sha1.toLowerCase(Locale.ROOT));
                sender.sendMessage("§aResource pack request sent. Accept it in Minecraft.");
                return true;
            }
            case "youtube" -> {
                if (!sender.hasPermission("youtubemusic.admin")) {
                    sender.sendMessage("§cOnly an administrator can submit an import URL.");
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage("§cUsage: /music youtube <YouTube URL>");
                    return true;
                }
                String url = args[1].trim();
                int maxLength = getConfig().getInt("playback.max-youtube-url-length", 300);
                if (url.length() > maxLength || !isYouTubeUrl(url)) {
                    sender.sendMessage("§cPlease provide a valid youtube.com or youtu.be HTTPS URL.");
                    return true;
                }
                sender.sendMessage("§eURL validated, but audio has NOT been downloaded or played.");
                sender.sendMessage("§7Import the audio only if you own it or have permission, convert it to OGG Vorbis,");
                sender.sendMessage("§7add it to the resource pack, publish the updated pack, then use /music play <namespace:sound>.");
                getLogger().info("Authorized audio import requested by " + sender.getName() + "; URL validation only.");
                return true;
            }
            default -> {
                sender.sendMessage("§cUnknown subcommand. Use /music help.");
                return true;
            }
        }
    }

    private boolean isHttpsUrl(String value) {
        try {
            URI uri = URI.create(value);
            return "https".equalsIgnoreCase(uri.getScheme())
                    && uri.getHost() != null
                    && !uri.getHost().isBlank();
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    private boolean isYouTubeUrl(String value) {
        if (!isHttpsUrl(value)) return false;
        try {
            String host = URI.create(value).getHost().toLowerCase(Locale.ROOT);
            return host.equals("youtube.com")
                    || host.endsWith(".youtube.com")
                    || host.equals("youtu.be")
                    || host.endsWith(".youtu.be");
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}
