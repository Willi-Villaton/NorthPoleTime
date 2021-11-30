package com.villaton.northpoletime;

import com.villaton.northpoletime.commands.NPTime;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class NorthPoleTime extends JavaPlugin {

    private static NorthPoleTime instance;

    @Override
    public void onEnable() {

        instance = this;

        // Plugin startup logic
        command_registration();

        if (NPTime.get_running()) {
            NPTime.start_nptime(null);
        }

        //Finished startup
        getLogger().info("SchematicSorter activated successfully.");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        // Plugin shutdown logic
        Bukkit.getLogger().info("SchematicSorter deactivated successfully.");
    }

    private void command_registration() {
        getCommand("nptime").setExecutor(new NPTime());
        getCommand("nptime").setTabCompleter(new NPTime());
    }

    public static NorthPoleTime getInstance() {
        return instance;
    }
}
