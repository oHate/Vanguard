package dev.ohate.vanguard.util;

import lombok.Getter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

@Getter
public abstract class Command extends ListenerAdapter {

    public String commandName;

    public Command(String commandName) {
        this.commandName = commandName;
    }

    public abstract SlashCommandData getCommand();

    public abstract void onSlashCommandInteraction(SlashCommandInteractionEvent event);

    public boolean isCommand(SlashCommandInteractionEvent event) {
        return event.getName().equalsIgnoreCase(commandName);
    }

}
