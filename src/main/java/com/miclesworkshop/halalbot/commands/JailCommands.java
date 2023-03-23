package com.miclesworkshop.halalbot.commands;

import com.miclesworkshop.halalbot.HalalBot;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

public class JailCommands extends AbstractCommands {
    public JailCommands(HalalBot bot) {
        super(bot);
    }

    @Override
    protected void executeCommand(Server server, User user, ServerTextChannel channel, Message message,
                                  String channelName, String cmd, String[] args) {
        if (!cmd.equals("*pc") && !cmd.equals("*upc")) {
            return;
        }

        boolean jail = cmd.equals("*pc");

        if (!server.hasPermission(user, PermissionType.KICK_MEMBERS)) {
            channel.sendMessage(user.getMentionTag() + " You don't have the KICK_MEMBERS permission!");
            return;
        }

        if (message.getMentionedUsers().isEmpty()) {
            channel.sendMessage("Usage: " + cmd + " <user(s)>");
        }

        for (User target : message.getMentionedUsers()) {
            if (jail && server.hasPermission(target, PermissionType.KICK_MEMBERS)) {
                channel.sendMessage("Can't place in Private Channel" + target.getDiscriminatedName());
                continue;
            }

            if (isJailed(target, server) == jail) {
                if (jail) {
                    channel.sendMessage(target.getDiscriminatedName() + " is already in private channel!");
                } else {
                    channel.sendMessage(target.getDiscriminatedName() + " is not in private channel!");
                }
                continue;
            }

            if (jail) {
                target.addRole(bot.getJailedRole(server), "Put in Private Channel by " + user.getDiscriminatedName());
                target.sendMessage("You have been placed in Private Channel in" + server.getName() + "!");
                channel.sendMessage("Placed in Private Channel " + target.getDiscriminatedName());
            } else {
                target.removeRole(bot.getJailedRole(server), "Unjailed by " + user.getDiscriminatedName());
                target.sendMessage("You have been removed from Private Channel in " + server.getName() + "!");
                channel.sendMessage("Removed from Private Channel " + target.getDiscriminatedName());
            }
        }
    }

    private boolean isJailed(User user, Server server) {
        return server.getRoles(user).contains(bot.getJailedRole(server));
    }
}
