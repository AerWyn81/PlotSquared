/*
 * Copyright (c) IntellectualCrafters - 2014. You are not allowed to distribute
 * and/or monetize any of our intellectual property. IntellectualCrafters is not
 * affiliated with Mojang AB. Minecraft is a trademark of Mojang AB.
 *
 * >> File = Help.java >> Generated by: Citymonstret at 2014-08-11 17:32
 */

package com.intellectualcrafters.plot.commands;

import org.bukkit.entity.Player;


public class Help extends SubCommand {
    public Help() {
        super("help", "", "Get this help menu", "help", "he", SubCommand.CommandCategory.INFO, false);
    }

    @Override
    public boolean execute(final Player plr, final String... args) {
        return false;
    }
}
