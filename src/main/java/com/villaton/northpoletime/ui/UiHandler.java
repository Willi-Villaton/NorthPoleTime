package com.villaton.northpoletime.ui;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

public class UiHandler {

    public static @NotNull TextComponent[] toggle_active(String world, int min, int max) {
        return new TextComponent[] {
                new TextComponent(ChatColor.GOLD + "NPTime now active with old settings."),
                new TextComponent(ChatColor.GOLD + "Settings: " + ChatColor.WHITE + "World: " + world + ", MinT: " + min + ", MaxT: " + max)
        };
    }

    public static @NotNull TextComponent[] toggle_inactive(String world) {
        return new TextComponent[] {
                new TextComponent(ChatColor.GOLD + "NPTime now inactive in world: " + ChatColor.WHITE + world)
        };
    }


//----------------------------------------- Error Outputs --------------------------------------------------------------
    // --- Player errors ---
    public static @NotNull TextComponent[] no_player_error() {
        return new TextComponent[] {
                new TextComponent(ChatColor.RED + "Oh oh oh. This is a player based plug-in. Not working with command prompt.")
        };
    }

    public static @NotNull TextComponent[] insufficient_permission() {
        return new TextComponent[] {
                new TextComponent(ChatColor.RED + "Insufficient authorisation level."),
                new TextComponent(ChatColor.RED + "You are not entitled to perform this command!")
        };
    }

    // --- Parameter errors ---
    public static @NotNull TextComponent[] too_few_parameters_error() {
        return new TextComponent[] {
                new TextComponent(ChatColor.RED + "Missing parameters."),
                new TextComponent(ChatColor.GOLD + "/nptime (<toggle>) / (<Time minimum> <Time maximum>)")
        };
    }

    public static @NotNull TextComponent[] too_many_parameters_error() {
        return new TextComponent[] {
                new TextComponent(ChatColor.RED + "Too many parameters."),
                new TextComponent(ChatColor.GOLD + "/nptime (<toggle>) / (<Time minimum> <Time maximum>)")
        };
    }

    public static @NotNull TextComponent[] wrong_parameters_error(String cause) {
        return new TextComponent[] {
                new TextComponent(ChatColor.RED + "Wrong parameters."),
                new TextComponent(ChatColor.GOLD + "/nptime (<toggle>) / (<Time minimum> <Time maximum>)"),
                new TextComponent(ChatColor.GOLD + "Caused by: " + ChatColor.WHITE + cause)
        };
    }

    // --- Saving errors ---
    public static @NotNull TextComponent[] could_not_save_error() {
        return new TextComponent[] {
                new TextComponent(ChatColor.RED + "Something went wrong while saving!"),
                new TextComponent(ChatColor.RED + "If you want to save your settings run the command again.")
        };
    }

    public static @NotNull TextComponent[] toggle_error() {
        return new TextComponent[] {
                new TextComponent(ChatColor.RED + "Something went wrong!"),
                new TextComponent(ChatColor.RED + "Maybe there is something odd with the config file.")
        };
    }

    // --- Game errors ---
    public static @NotNull TextComponent[] game_rule_error() {
        return new TextComponent[] {
                new TextComponent(ChatColor.RED + "Change game rule 'DoDaylightCycle' to false!")
        };
    }
}
