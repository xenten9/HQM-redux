package com.xenten9.hardcorequesting.commands;

import net.minecraft.command.ICommandSender;

import com.xenten9.hardcorequesting.QuestingData;

public class CommandQuest extends CommandBase {

    public CommandQuest() {
        super("quest");
    }

    @Override
    public void handleCommand(ICommandSender sender, String[] arguments) {
        sendChat(
            sender,
            QuestingData.isQuestActive() ? "hqm.message.questAlreadyActivated" : "hqm.message.questActivated");
        QuestingData.activateQuest();
    }
}
