package hardcorequesting.network;

import static hardcorequesting.HardcoreQuesting.packetHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import com.xenten9.hardcorequesting.ModInformation;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class DataWriter {

    private OutputStream stream;
    private int byteBuffer;
    private int bitCountBuffer;
    private int bits;

    DataWriter() {
        stream = new ByteArrayOutputStream();
    }

    DataWriter(OutputStream stream) {
        this.stream = stream;
    }

    public void writeByte(int data) {
        writeData(data, DataBitHelper.BYTE);
    }

    public void writeBoolean(boolean data) {
        writeData(data ? 1 : 0, DataBitHelper.BOOLEAN);
    }

    public void writeData(int data, DataBitHelper bitCount) {
        writeData(data, bitCount.getBitCount());
    }

    public void writeData(int data, int bitCount) {
        if (buffer != null) {
            buffer.writeData(data, bitCount);
            return;
        }
        bits += bitCount;

        int mask = PacketHandler.BIT_MASK[bitCount];
        data &= mask;

        while (true) {
            if (bitCountBuffer + bitCount >= 8) {
                int bitsToAdd = 8 - bitCountBuffer;
                int addMask = PacketHandler.BIT_MASK[bitsToAdd];
                int addData = data & addMask;
                data >>>= bitsToAdd;
                addData <<= bitCountBuffer;
                byteBuffer |= addData;

                try {
                    stream.write(byteBuffer);
                } catch (IOException ignored) {}

                byteBuffer = 0;
                bitCount -= bitsToAdd;
                bitCountBuffer = 0;
            } else {
                byteBuffer |= data << bitCountBuffer;
                bitCountBuffer += bitCount;
                break;
            }
        }
    }

    void close() {
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeString(String str, DataBitHelper bits) {
        if (str != null) {
            byte[] bytes = str.getBytes();
            int l = Math.min(bytes.length, bits.getMaximum());
            writeData(l, bits);
            for (int i = 0; i < l; i++) {
                writeByte(bytes[i]);
            }
        } else {
            writeData(0, bits);
        }
    }

    public void writeNBT(NBTTagCompound nbtTagCompound) {
        byte[] bytes = null;

        if (nbtTagCompound != null) {
            try {
                bytes = CompressedStreamTools.compress(nbtTagCompound);
            } catch (IOException ex) {
                bytes = null;
            }
        }

        writeBoolean(bytes != null);
        if (bytes != null) {
            writeData(bytes.length, DataBitHelper.NBT_LENGTH);
            for (byte b : bytes) {
                writeByte(b);
            }
        }
    }

    private static final int MAX_PACKET_SIZE = 32766;

    void sendToPlayer(EntityPlayerMP player) {
        writeFinalBits();

        byte[] bytes = ((ByteArrayOutputStream) stream).toByteArray();
        if (bytes.length < MAX_PACKET_SIZE) {
            sendBytesToPlayer(bytes, player);
        } else {
            int packets = (int) Math.ceil((float) bytes.length / MAX_PACKET_SIZE);
            bytes[0] = (byte) packets;
            for (int i = 0; i < packets; i++) {
                int start = i * MAX_PACKET_SIZE;
                int len = Math.min(MAX_PACKET_SIZE, bytes.length - start);
                byte[] packetBytes = new byte[len];
                System.arraycopy(bytes, start, packetBytes, 0, len);
                sendBytesToPlayer(packetBytes, player);
            }
        }

    }

    private void sendBytesToPlayer(byte[] bytes, EntityPlayerMP player) {
        packetHandler.sendTo(createPacket(bytes), player);
    }

    void writeFinalBits() {
        if (bitCountBuffer > 0) {
            try {
                stream.write(byteBuffer);
            } catch (IOException ignored) {}
            bitCountBuffer = 0;
        }

    }

    private FMLProxyPacket createPacket() {
        writeFinalBits();
        return createPacket(((ByteArrayOutputStream) stream).toByteArray());
    }

    private FMLProxyPacket createPacket(byte[] bytes) {
        ByteBuf buf = Unpooled.copiedBuffer(bytes);
        return new FMLProxyPacket(buf, ModInformation.CHANNEL);
    }

    public void sendToServer() {
        packetHandler.sendToServer(createPacket());
    }

    public void sendToAllPlayers() {
        packetHandler.sendToAll(createPacket());
    }

    public void sendToAllPlayersAround(TileEntity te, double range) {
        packetHandler.sendToAllAround(
            createPacket(),
            new NetworkRegistry.TargetPoint(
                te.getWorldObj().provider.dimensionId,
                te.xCoord + 0.5,
                te.yCoord + 0.5,
                te.zCoord,
                range));
    }

    private DataBitHelper bufferSize;
    private DataWriter buffer;

    public void createBuffer(DataBitHelper bits) {
        buffer = new DataWriter();
        bufferSize = bits;
    }

    public void flushBuffer() {
        DataWriter buffer = this.buffer;
        this.buffer = null;
        buffer.writeFinalBits();

        int count = buffer.bits;
        byte[] bytes = ((ByteArrayOutputStream) buffer.stream).toByteArray();
        writeData(count, bufferSize);
        bufferSize = null;

        for (byte b : bytes) {
            int bitCount = Math.min(8, count);
            count -= bitCount;
            writeData(b, bitCount);
        }

        buffer.close();
    }

    public void writeItemStack(ItemStack itemStack, boolean useSize) {
        writeItem(itemStack.getItem());
        if (useSize) {
            writeData(itemStack.stackSize, DataBitHelper.SHORT);
        }
        writeData(itemStack.getItemDamage(), DataBitHelper.SHORT);
        writeNBT(itemStack.getTagCompound());
    }

    public void writeItem(Item item) {
        writeString(Item.itemRegistry.getNameForObject(item), DataBitHelper.SHORT);
    }

    private static final double LOG_2 = Math.log10(2);

    public void writeEnum(Enum data) {
        try {
            Class<? extends Enum> clazz = data.getClass();
            int length = ((Object[]) clazz.getMethod("values")
                .invoke(null)).length;
            if (length == 0) {
                return;
            }
            int bitCount = (int) (Math.log10(length) / LOG_2) + 1;

            writeData(data.ordinal(), bitCount);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
