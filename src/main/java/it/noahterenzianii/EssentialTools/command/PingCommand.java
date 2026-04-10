package it.noahterenzianii.EssentialTools.command;

import it.noahterenzianii.EssentialTools.Main;
import it.noahterenzianii.EssentialTools.manager.CoordinateManager;
import it.noahterenzianii.EssentialTools.model.SavedLocation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.bukkit.Color;
import org.bukkit.Particle.DustOptions;

public class PingCommand implements CommandExecutor {
    private static final int PING_DURATION_TICKS = 20;
    private static final int PING_INTERVAL_TICKS = 10;

    private final Main plugin;

    public PingCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return handlePingCommand(sender, args);
    }

    private boolean handlePingCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can run this command!");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /ping <player_or_saved_location>");
            return true;
        }

        Player targetPlayer = Bukkit.getPlayer(args[0]);
        Location targetLoc;

        if (targetPlayer != null) {
            targetLoc = targetPlayer.getLocation();
        } else {
            SavedLocation saved = plugin.getCoordinateManager().getCoordinate(args[0]);
            if (saved == null) {
                player.sendMessage(ChatColor.RED + "Target not found!");
                return true;
            }
            targetLoc = saved.toLocation();
        }

        if (!player.getWorld().equals(targetLoc.getWorld())) {
            player.sendMessage(ChatColor.RED + "Target is in a different world!");
            return true;
        }

        player.sendMessage(ChatColor.GREEN + "Indicator pointing at " + ChatColor.WHITE + args[0] +
                ChatColor.GREEN + " (" + ChatColor.GOLD + Math.round(player.getLocation().distance(targetLoc)) + " blocks away" + ChatColor.GREEN + ")!");

        new PingRunnable(plugin, player, targetLoc, targetPlayer)
            .runTaskTimer(plugin, 0L, PING_INTERVAL_TICKS);

        return true;
    }

    public static class PingRunnable extends BukkitRunnable {
        private final Main plugin;
        private final Player player;
        private final Location targetLoc;
        private final Player targetPlayer;
        private int ticks;

        private static final DustOptions[] BODY_LAYERS = {
            new DustOptions(Color.fromRGB(220, 255, 255), 0.5f),
            new DustOptions(Color.fromRGB(0, 230, 255), 1.0f),
            new DustOptions(Color.fromRGB(0, 140, 180), 1.6f),
        };

        private static final DustOptions[] TIP_LAYERS = {
            new DustOptions(Color.fromRGB(255, 255, 255), 0.5f),
            new DustOptions(Color.fromRGB(0, 255, 255), 1.1f),
            new DustOptions(Color.fromRGB(0, 160, 200), 1.8f),
        };

        PingRunnable(Main plugin, Player player, Location targetLoc, Player targetPlayer) {
            this.plugin = plugin;
            this.player = player;
            this.targetLoc = targetLoc;
            this.targetPlayer = targetPlayer;
        }

        @Override
        public void run() {
            if (!player.isOnline() || ticks >= PING_DURATION_TICKS) {
                cancel();
                return;
            }

            Location playerLoc = player.getLocation().clone().add(0, 1, 0);
            Location currentTargetLoc = targetPlayer != null ? targetPlayer.getLocation() : targetLoc;

            Vector direction = currentTargetLoc.toVector().subtract(playerLoc.toVector()).normalize();

            Vector perp = calculatePerpendicular(direction);
            Vector up = perp.clone().crossProduct(direction).normalize();

            spawnArrowBody(playerLoc, direction);
            spawnArrowWings(playerLoc, direction, perp, up);

            ticks++;
        }

        private Vector calculatePerpendicular(Vector direction) {
            Vector perp = direction.clone().crossProduct(new Vector(0, 1, 0)).normalize();
            if (perp.lengthSquared() < 0.001) {
                perp = direction.clone().crossProduct(new Vector(1, 0, 0)).normalize();
            }
            return perp;
        }

        private void spawnArrowBody(Location playerLoc, Vector direction) {
            for (int i = 1; i <= 8; i++) {
                Location bodyLoc = playerLoc.clone().add(direction.clone().multiply(i * 0.9));
                for (DustOptions layer : BODY_LAYERS) {
                    player.getWorld().spawnParticle(Particle.DUST, bodyLoc, 0, layer);
                }
            }
        }

        private void spawnArrowWings(Location playerLoc, Vector direction, Vector perp, Vector up) {
            Location tip = playerLoc.clone().add(direction.clone().multiply(9.5));
            Location wingBase = playerLoc.clone().add(direction.clone().multiply(7.8));

            Location[] wings = {
                wingBase.clone().add(perp.clone().multiply( 0.75)),
                wingBase.clone().add(perp.clone().multiply(-0.75)),
                wingBase.clone().add(up.clone().multiply(   0.75)),
                wingBase.clone().add(up.clone().multiply(  -0.75)),
            };

            for (Location wing : wings) {
                for (int j = 0; j <= 5; j++) {
                    double s = j / 5.0;
                    double px = lerp(wing.getX(), tip.getX(), s);
                    double py = lerp(wing.getY(), tip.getY(), s);
                    double pz = lerp(wing.getZ(), tip.getZ(), s);

                    Location pLoc = new Location(player.getWorld(), px, py, pz);
                    for (DustOptions layer : TIP_LAYERS) {
                        player.getWorld().spawnParticle(Particle.DUST, pLoc, 0, layer);
                    }
                }
            }
        }

        private double lerp(double a, double b, double t) {
            return a + t * (b - a);
        }
    }
}