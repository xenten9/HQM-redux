package hardcorequesting.tileentity;

import net.minecraft.entity.player.EntityPlayer;

import com.xenten9.hardcorequesting.network.DataReader;
import com.xenten9.hardcorequesting.network.DataWriter;

public interface IBlockSync {

    public void writeData(DataWriter dw, EntityPlayer player, boolean onServer, int id);

    public void readData(DataReader dr, EntityPlayer player, boolean onServer, int id);

    public int infoBitLength();
}
