package com.villaton.northpoletime.commands;

import com.villaton.northpoletime.NorthPoleTime;
import com.villaton.northpoletime.ui.UiHandler;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class NPTime implements TabExecutor {

    private static NPTimer timer;
    private final static String PATH_TO_CONFIG = "plugins/NorthPoleTime/config.yml";

    private static class NPTimer implements Runnable {

        private final BukkitTask task;
        private final World world;
        private final int min_time;
        private final int max_time;
        private int time = 0;
        private boolean ascending;
        private int changing;

        public NPTimer(World world, int min_time, int max_time) {
            this.world = world;
            this.min_time = min_time;
            this.max_time = max_time;
            this.time = min_time;
            this.task = Bukkit.getScheduler().runTaskTimer(NorthPoleTime.getInstance(), this,0, 5);
            this.ascending = true;
            this.changing = 0;
        }

        @Override
        public void run() {

            if (time == min_time) {
                Bukkit.getLogger().info("Tick Tack");
            }

            //Little hold if on highest/lowest point
            if (changing != 0) {
                changing--;
            } else {
                if (ascending) {
                    time++;
                } else {
                    time--;
                }

                world.setFullTime(time);

                //Highest point of sun
                if (time >= max_time) {
                    ascending = false;
                    changing = 100;
                }

                //Lowest point of sun
                if (time <= min_time) {
                    ascending = true;
                    changing = 100;
                }
            }
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        //Check if command is executed from a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(UiHandler.no_player_error());
            return true;
        }

        //Validate player
        Player player = (Player) sender;
        if (!player.hasPermission("NPTime")) {
            output_results(player, UiHandler.insufficient_permission());
            return false;
        }

        World world = player.getWorld();
        if (Boolean.TRUE.equals(world.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE))) {
            output_results(player, UiHandler.game_rule_error());
            return false;
        }

        if (args.length == 1 && args[0].equals("toggle")) {

            boolean result;
            if (timer != null) {
                result = stop_nptime(player);
            } else {
                result = start_nptime(player);
            }

            if (!result) {
                output_results(player, UiHandler.toggle_error());
                return false;
            }

            return true;
        }

        //Validate arguments
        if (args.length > 2) {
            output_results(player, UiHandler.too_many_parameters_error());
            return false;
        }

        if (args.length < 2) {
            output_results(player, UiHandler.too_few_parameters_error());
            return false;
        }

        int min_time = 0;
        int max_time = 0;
        try {
            min_time = Integer.parseInt(args[0]);
            max_time = Integer.parseInt(args[1]);
        } catch(Exception e) {
            output_results(player, UiHandler.wrong_parameters_error("Are the given values integers?"));
            return false;
        }

        //Wrong min_time
        if (min_time < 0 || min_time > max_time || min_time > 24000) {
            output_results(player, UiHandler.wrong_parameters_error("The given minimum times seems to be invalid"));
            return false;
        }
        //Wrong max_time
        if (max_time > 24000 || (max_time - min_time) < 50) {
            output_results(player, UiHandler.wrong_parameters_error("The given minimum times seems to be invalid or the values are too close together"));
            return false;
        }

        if (!save_config(true, world.getName(), min_time, max_time)) {
            output_results(player, UiHandler.could_not_save_error());
        }

        if (timer != null) {
            timer.task.cancel();
            timer = null;
        }

        start_nptime(player, world, min_time, max_time);
        return false;
    }

// ----------------------------------------- START ---------------------------------------------------------------------
    public static boolean start_nptime(Player player) {
        String[] config = get_config();
        if (config == null) {
            return false;
        }

        boolean active;
        switch (config[0]) {
            case "true":
                active = true;
                break;
            case "false":
                active = false;
                break;
            default:
                return false;
        }
        
        World world = Bukkit.getServer().getWorld(config[1]);
        if (world == null) {
            return false;
        }

        int min_time;
        int max_time;

        min_time = Integer.parseInt(config[2]);
        max_time = Integer.parseInt(config[3]);

        timer = new NPTimer(world, min_time, max_time);

        save_config(true, timer.world.getName(), timer.min_time, timer.max_time);

        output_results(player, UiHandler.toggle_active(world.getName(), min_time, max_time));
        return true;
    }

    public static void start_nptime(Player player, World world, int min_time, int max_time) {
        timer = new NPTimer(world, min_time, max_time);
        output_results(player, UiHandler.toggle_active(world.getName(), min_time, max_time));
    }

// ----------------------------------------- STOP ----------------------------------------------------------------------
    public static boolean stop_nptime(Player player) {

        String world_name = timer.world.getName();
        save_config(false, timer.world.getName(), timer.min_time, timer.max_time);
        timer.task.cancel();
        timer = null;

        output_results(player, UiHandler.toggle_inactive(world_name));
        return true;
    }


// ----------------------------------------- FILE UTILITIES ------------------------------------------------------------
    public static String[] get_config() {
        File file = new File(PATH_TO_CONFIG);
        if (file != null && !file.exists()) {
            return null;
        }

        String[] output;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String active = reader.readLine();
            active = active.substring(11);
            active = active.endsWith("\"") ? active.substring(0, active.length() - 1) : active;

            String world_name = reader.readLine();
            world_name = world_name.substring(14);
            world_name = world_name.endsWith("\"") ? world_name.substring(0, world_name.length() - 1) : world_name;

            String min_time = reader.readLine();
            min_time = min_time.substring(10, min_time.length());
            min_time = min_time.endsWith("\"") ? min_time.substring(0, min_time.length() - 1) : min_time;

            String max_time = reader.readLine();
            max_time = max_time.substring(10, max_time.length());
            max_time = max_time.endsWith("\"") ? max_time.substring(0, max_time.length() - 1) : max_time;

            output = new String[] {active, world_name, min_time, max_time};

        } catch (IOException e) {
            return null;
        }

        return output;
    }

    public static boolean get_running() {
        String[] config = get_config();
        if (config == null) {
            return false;
        }

        boolean active;
        if (config[0].equals("true")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean save_config(boolean active, String world, int min_time, int max_time) {

        File file = new File(PATH_TO_CONFIG);
        if (file != null && !file.exists()) {
            new File("plugins/NorthPoleTime/").mkdir();
        }

        try {
            if (file.exists()) {
                file.delete();
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(PATH_TO_CONFIG));
            writer.write("Is active:\"" + active + "\"\n");
            writer.write("Active world:\"" + world + "\"\n");
            writer.write("Time min:\"" + min_time + "\"\n");
            writer.write("Time max:\"" + max_time + "\"\n");
            writer.close();

        } catch (IOException e) {
            return false;
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {

        LinkedList<String> output = new LinkedList<>();

        if (args.length == 0) {
            output.add("<Minimum Time> <Maximum Time>");
            return output;
        } else {
            return null;
        }
    }

    private static void output_results(Player player, TextComponent[] output) {
        if (player == null) {
            return;
        }

        if (output == null || output.length <= 0) {
            return;
        }

        for (TextComponent t : output) {
            player.sendMessage(t);
        }
    }
}
