package dev.ohate.vanguard.modules.poll.commands;

import dev.ohate.vanguard.modules.poll.handlers.PollBuilderHandler;
import dev.ohate.vanguard.modules.poll.models.Poll;
import dev.ohate.vanguard.modules.poll.util.PollUtil;
import dev.ohate.vanguard.util.Command;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.UUID;

public class PollCommand extends Command {

    public PollCommand() {
        super("poll");
    }

    @Override
    public SlashCommandData getCommand() {
        return Commands.slash(getCommandName(), "Starts the creation process of a poll.");
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!isCommand(event)) {
            return;
        }

        Poll.Builder builder = Poll.builder(event.getMember().getIdLong());

        UUID id = UUID.randomUUID();
        PollBuilderHandler.getInstance().addPollBuilder(id, builder);

        event.replyEmbeds(builder.buildEmbed())
                .addComponents(PollUtil.getPollBuilderButtons(id))
                .setEphemeral(true)
                .queue();
    }

}
