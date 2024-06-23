package com.xenten9.hardcorequesting;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;

import com.xenten9.hardcorequesting.config.ModConfig;
import com.xenten9.hardcorequesting.network.PacketHandler;
import com.xenten9.hardcorequesting.quests.QuestLine;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class PlayerTracker {

    public PlayerTracker() {
        FMLCommonHandler.instance()
            .bus()
            .register(this);
    }

    public int getRemainingLives(ICommandSender sender) {
        return QuestingData.getQuestingData((EntityPlayer) sender)
            .getLives();
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        EntityPlayer player = event.player;
        if (!QuestingData.hasData(player)) {
            QuestingData.getQuestingData(player)
                .getDeathStat()
                .refreshSync();
        }

        QuestLine.sendServerSync(player);

        if (QuestingData.isHardcoreActive()) sendLoginMessage(player);
        else if (ModConfig.NO_HARDCORE_MESSAGE)
            player.addChatMessage(new ChatComponentTranslation("hqm.message.noHardcore"));

        NBTTagCompound tags = player.getEntityData();
        if (tags.hasKey("HardcoreQuesting")) {
            if (tags.getCompoundTag("HardcoreQuesting")
                .getBoolean("questBook")) {
                QuestingData.getQuestingData(player).receivedBook = true;
            }
            if (QuestingData.isQuestActive()) {
                tags.removeTag("HardcoreQuesting");
            }
        }

        QuestingData.spawnBook(player);

    }

    private void sendLoginMessage(EntityPlayer player) {
        player.addChatMessage(
            new ChatComponentText(
                Translator.translate("hqm.message.hardcore") + " "
                    + Translator.translate(
                        getRemainingLives(player) != 1,
                        "hqm.message.livesLeft",
                        getRemainingLives(player))));

    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        EntityPlayer player = event.player;
        if (!player.worldObj.isRemote) {
            PacketHandler.remove(player);
        }
    }

}
