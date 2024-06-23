package com.xenten9.hardcorequesting.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.xenten9.hardcorequesting.QuestingData;
import com.xenten9.hardcorequesting.items.ItemQuestBook;

public class CommandEdit extends CommandBase {

    public CommandEdit() {
        super("edit");
    }

    @Override
    public void handleCommand(ICommandSender sender, String[] arguments) {
        if (sender instanceof EntityPlayer && isPlayerOp(sender)) {
            EntityPlayer player = (EntityPlayer) sender;

            String name = arguments.length == 1 ? arguments[0] : QuestingData.getUserName(player);
            if (QuestingData.hasData(name)) {
                player.inventory.addItemStackToInventory(ItemQuestBook.getOPBook(name));
            } else {
                sendChat(player, "hqm.message.noPlayer");
            }
        }
    }
}
