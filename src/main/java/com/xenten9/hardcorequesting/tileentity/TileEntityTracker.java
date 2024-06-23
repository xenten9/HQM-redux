package com.xenten9.hardcorequesting.tileentity;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.xenten9.hardcorequesting.client.interfaces.GuiBase;
import com.xenten9.hardcorequesting.client.interfaces.GuiEditMenuTracker;
import com.xenten9.hardcorequesting.client.interfaces.GuiWrapperEditMenu;
import com.xenten9.hardcorequesting.network.*;
import com.xenten9.hardcorequesting.quests.Quest;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityTracker extends TileEntity {

    private Quest quest;
    private int questId = -1;
    private int radius;
    private TrackerType type = TrackerType.TEAM;

    private static final String NBT_QUEST = "Quest";
    private static final String NBT_RADIUS = "Radius";
    private static final String NBT_TYPE = "TrackerType";

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        if (quest != null) {
            compound.setShort(NBT_QUEST, quest.getId());
        }
        compound.setInteger(NBT_RADIUS, radius);
        compound.setByte(NBT_TYPE, (byte) type.ordinal());
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        if (compound.hasKey(NBT_QUEST)) {
            questId = compound.getShort(NBT_QUEST);
        } else {
            quest = null;
        }
        radius = compound.getInteger(NBT_RADIUS);
        type = TrackerType.values()[compound.getByte(NBT_TYPE)];
    }

    private int delay = 0;

    @Override
    public void updateEntity() {
        if (quest == null && questId != -1) {
            quest = Quest.getQuest(questId);
            questId = -1;
        }

        if (!worldObj.isRemote && delay++ == 20) {
            if (quest != null && Quest.getQuest(quest.getId()) == null) {
                quest = null;
            }
            int oldMeta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
            int meta = 0;
            if (quest != null) {
                meta = type.getMeta(this, quest, radius);
            }

            if (oldMeta != meta) {
                worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, meta, 3);
                notifyUpdate(xCoord, yCoord, zCoord, 2);
            }

            delay = 0;
        }
    }

    private void notifyUpdate(int x, int y, int z, int i) {
        if (i == 2 || x != xCoord || y != yCoord || z != zCoord) {
            worldObj.notifyBlockOfNeighborChange(x, y, z, getBlockType());

            if (i > 0) {
                notifyUpdate(x - 1, y, z, i - 1);
                notifyUpdate(x + 1, y, z, i - 1);
                notifyUpdate(x, y - 1, z, i - 1);
                notifyUpdate(x, y + 1, z, i - 1);
                notifyUpdate(x, y, z - 1, i - 1);
                notifyUpdate(x, y, z + 1, i - 1);
            }
        }
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public TrackerType getType() {
        return type;
    }

    public void setType(TrackerType type) {
        this.type = type;
    }

    public void setCurrentQuest() {
        quest = Quest.getQuest(Quest.selectedQuestId);
    }

    public Quest getCurrentQuest() {
        return quest;
    }

    public void openInterface(EntityPlayer player) {
        DataWriter dw = PacketHandler.getWriter(PacketId.TRACKER_ACTIVATE);
        saveCoordinate(dw);
        save(dw, true);
        PacketHandler.sendToRawPlayer(player, dw);
    }

    private void saveCoordinate(DataWriter dw) {
        dw.writeData(xCoord, DataBitHelper.WORLD_COORDINATE);
        dw.writeData(yCoord, DataBitHelper.WORLD_COORDINATE);
        dw.writeData(zCoord, DataBitHelper.WORLD_COORDINATE);
    }

    private void save(DataWriter dw, boolean saveQuest) {
        if (saveQuest) {
            dw.writeBoolean(quest != null);
            if (quest != null) {
                dw.writeData(quest.getId(), DataBitHelper.QUESTS);
            }
        }
        dw.writeData(radius, DataBitHelper.WORLD_COORDINATE);
        dw.writeData(type.ordinal(), DataBitHelper.TRACKER_TYPE);
    }

    private void load(DataReader dr, boolean loadQuest) {
        if (loadQuest) {
            if (dr.readBoolean()) {
                quest = Quest.getQuest(dr.readData(DataBitHelper.QUESTS));
            } else {
                quest = null;
            }
        }
        radius = dr.readData(DataBitHelper.WORLD_COORDINATE);
        type = TrackerType.values()[dr.readData(DataBitHelper.TRACKER_TYPE)];
    }

    private static TileEntityTracker getTracker(World world, DataReader dr) {
        int x = dr.readData(DataBitHelper.WORLD_COORDINATE);
        int y = dr.readData(DataBitHelper.WORLD_COORDINATE);
        int z = dr.readData(DataBitHelper.WORLD_COORDINATE);

        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityTracker) {
            return (TileEntityTracker) te;
        } else {
            return null;
        }
    }

    @SideOnly(Side.CLIENT)
    public static void openInterface(EntityPlayer player, DataReader dr) {
        TileEntityTracker tracker = getTracker(player.worldObj, dr);
        if (tracker != null) {
            tracker.load(dr, true);
            GuiBase gui = new GuiWrapperEditMenu();
            gui.setEditMenu(new GuiEditMenuTracker(gui, player, tracker));
            Minecraft.getMinecraft()
                .displayGuiScreen(gui);
        }
    }

    public void sendToServer() {
        DataWriter dw = PacketHandler.getWriter(PacketId.TRACKER_RESPONSE);
        saveCoordinate(dw);
        save(dw, false);
        PacketHandler.sendToServer(dw);
    }

    public static void saveToServer(EntityPlayer player, DataReader dr) {
        TileEntityTracker tracker = getTracker(player.worldObj, dr);
        if (Quest.isEditing && tracker != null) {
            tracker.load(dr, false);
        }
    }
}
