package cn.clexus.distancePlaceholderAPIExpansion;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class DistanceExpansion extends PlaceholderExpansion {


    @Override
    public @NotNull String getIdentifier() {
        return "distance";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Clexus";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer offline, @NotNull String params) {
        /*
        %distance_x1,y1,z1[,x2,y2,z2][,decimals]%
        %distance_player1[,player2][,decimals]%
        %distance_player[,x,y,z][,decimals]%
        %distance_UUID1[,UUID2][,decimals]%
        */
        try {
            String[] rawArgs = params.split(",");
            String[] args = new String[rawArgs.length];
            for (int i = 0; i < rawArgs.length; i++) {
                args[i] = PlaceholderAPI.setBracketPlaceholders(offline, rawArgs[i].trim());
            }
            if (args.length == 0) return "-1";

            int decimals = 2;
            Player onlinePlayer = offline != null && offline.isOnline() ? offline.getPlayer() : null;

            if (isNumber(args[0]) && args.length >= 3) {
                double x1 = Double.parseDouble(args[0]);
                double y1 = Double.parseDouble(args[1]);
                double z1 = Double.parseDouble(args[2]);

                double x2, y2, z2;

                if (args.length >= 6 && isNumber(args[3])) {
                    x2 = Double.parseDouble(args[3]);
                    y2 = Double.parseDouble(args[4]);
                    z2 = Double.parseDouble(args[5]);
                    if (args.length >= 7 && isInteger(args[6])) decimals = Integer.parseInt(args[6]);

                    double dist = distance(x1, y1, z1, x2, y2, z2);
                    return format(dist, decimals);
                } else {
                    if (onlinePlayer == null) return "-1";
                    Location loc = onlinePlayer.getLocation();
                    if (args.length >= 4 && isInteger(args[3])) decimals = Integer.parseInt(args[3]);

                    double dist = new Location(onlinePlayer.getWorld(), x1, y1, z1).distance(loc);
                    return format(dist, decimals);
                }
            }

            Player p1 = Bukkit.getPlayerExact(args[0]);
            if (p1 != null) {
                Player p2 = null;
                if (args.length >= 2) {
                    p2 = Bukkit.getPlayerExact(args[1]);
                    if (p2 != null) {
                        if (args.length >= 3 && isInteger(args[2])) decimals = Integer.parseInt(args[2]);
                    } else if (isInteger(args[1])) {
                        decimals = Integer.parseInt(args[1]);
                    }
                }
                if (p2 == null) {
                    if (onlinePlayer == null) return "-1";
                    p2 = onlinePlayer;
                }

                if (!p1.getWorld().equals(p2.getWorld())) return "-1";
                return format(p1.getLocation().distance(p2.getLocation()), decimals);
            }

            Player p = Bukkit.getPlayerExact(args[0]);
            if (args.length >= 4 && isNumber(args[1])) {
                if (p == null) return "-1";

                double x = Double.parseDouble(args[1]);
                double y = Double.parseDouble(args[2]);
                double z = Double.parseDouble(args[3]);
                if (args.length >= 5 && isInteger(args[4])) decimals = Integer.parseInt(args[4]);

                Location loc = new Location(p.getWorld(), x, y, z);
                return format(p.getLocation().distance(loc), decimals);
            }

            try {
                UUID uuid1 = UUID.fromString(args[0]);
                Entity e1 = Bukkit.getEntity(uuid1);
                if (e1 == null) return "-1";

                Entity e2 = null;
                if (args.length >= 2) {
                    try {
                        UUID uuid2 = UUID.fromString(args[1]);
                        Entity tmp = Bukkit.getEntity(uuid2);
                        if (tmp != null) {
                            e2 = tmp;
                            if (args.length >= 3 && isInteger(args[2])) decimals = Integer.parseInt(args[2]);
                        } else if (isInteger(args[1])) {
                            decimals = Integer.parseInt(args[1]);
                        }
                    } catch (IllegalArgumentException ex) {
                        if (isInteger(args[1])) decimals = Integer.parseInt(args[1]);
                    }
                }

                if (e2 == null) {
                    if (onlinePlayer == null) return "-1";
                    e2 = onlinePlayer;
                }

                if (!e1.getWorld().equals(e2.getWorld())) return "-1";
                return format(e1.getLocation().distance(e2.getLocation()), decimals);
            } catch (IllegalArgumentException ignored) {
            }

            return "-1";
        } catch (Exception e) {
            return "-1";
        }
    }
    private boolean isNumber(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String format(double value, int decimals) {
        return String.format("%." + decimals + "f", value);
    }

    private double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) + Math.pow(z2 - z1, 2));
    }
}
