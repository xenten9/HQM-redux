package com.xenten9.hardcorequesting.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.xenten9.hardcorequesting.client.interfaces.GuiColor;
import com.xenten9.hardcorequesting.quests.Quest;
import com.xenten9.hardcorequesting.tileentity.PortalType;
import com.xenten9.hardcorequesting.tileentity.TileEntityPortal;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockPortal extends ItemBlock {

    public ItemBlockPortal(Block block) {
        super(block);
        block.setBlockUnbreakable();
        block.setResistance(6000000.0F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List lst, boolean useExtraInfo) {
        try {
            if (itemStack != null && itemStack.getTagCompound() != null
                && itemStack.getTagCompound()
                    .hasKey("Portal")) {
                NBTTagCompound compound = itemStack.getTagCompound()
                    .getCompoundTag("Portal");
                if (compound.hasKey(TileEntityPortal.NBT_QUEST)) {
                    Quest quest = Quest.getQuest(compound.getShort(TileEntityPortal.NBT_QUEST));
                    if (quest != null) {
                        lst.add(GuiColor.GREEN + "Quest: " + quest.getName());
                    } else {
                        lst.add(GuiColor.GRAY + "No quest selected.");
                    }
                } else {
                    lst.add(GuiColor.GRAY + "No quest selected.");
                }

                PortalType type = PortalType.values()[compound.getByte(TileEntityPortal.NBT_TYPE)];
                lst.add(GuiColor.ORANGE + "Type: " + type.getName());
                if (!type.isPreset()) {
                    if (compound.hasKey(TileEntityPortal.NBT_ID)) {
                        int id = compound.getShort(TileEntityPortal.NBT_ID);
                        int dmg = compound.getShort(TileEntityPortal.NBT_DMG);

                        ItemStack item = new ItemStack(Item.getItemById(id), 1, dmg);
                        lst.add(GuiColor.YELLOW + "Item: " + item.getDisplayName());
                    } else {
                        lst.add(GuiColor.GRAY + "No item selected.");
                    }
                }

                boolean completedCollision, completedTexture, uncompletedCollision, uncompletedTexture;
                if (compound.hasKey(TileEntityPortal.NBT_COLLISION)) {
                    completedCollision = compound.getBoolean(TileEntityPortal.NBT_COLLISION);
                    completedTexture = compound.getBoolean(TileEntityPortal.NBT_COLLISION);
                    uncompletedCollision = compound.getBoolean(TileEntityPortal.NBT_NOT_COLLISION);
                    uncompletedTexture = compound.getBoolean(TileEntityPortal.NBT_NOT_TEXTURES);
                } else {
                    completedCollision = completedTexture = false;
                    uncompletedCollision = uncompletedTexture = true;
                }

                lst.add(" ");
                lst.add("Completed collision: " + formatBoolean(completedCollision));
                lst.add("Completed textures: " + formatBoolean(completedTexture));
                lst.add("Uncompleted collision: " + formatBoolean(uncompletedCollision));
                lst.add("Uncompleted textures: " + formatBoolean(uncompletedTexture));

            }
        } catch (Exception ignored) {} // just to make sure it doesn't crash because it tries to get some weird quests
                                       // or items or whatever
    }

    private String formatBoolean(boolean val) {
        return (val ? GuiColor.GREEN : GuiColor.GRAY) + String.valueOf(val);
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack) {
        return super.getUnlocalizedName(itemStack) + "_" + itemStack.getItemDamage();
    }
}
