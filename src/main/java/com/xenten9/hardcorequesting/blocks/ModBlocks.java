package hardcorequesting.blocks;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import com.xenten9.hardcorequesting.items.ItemBlockPortal;
import com.xenten9.hardcorequesting.items.ModItems;
import com.xenten9.hardcorequesting.tileentity.TileEntityBarrel;
import com.xenten9.hardcorequesting.tileentity.TileEntityPortal;
import com.xenten9.hardcorequesting.tileentity.TileEntityTracker;

import cpw.mods.fml.common.registry.GameRegistry;

public class ModBlocks {

    public static Block itemBarrel;
    public static Block itemTracker;
    public static Block itemPortal;

    public static void init() {
        itemBarrel = new BlockDelivery();
        itemTracker = new BlockTracker();
        itemPortal = new BlockPortal();
    }

    public static void registerBlocks() {
        GameRegistry.registerBlock(itemBarrel, BlockInfo.ITEMBARREL_UNLOCALIZED_NAME);
        GameRegistry.registerBlock(itemTracker, BlockInfo.QUEST_TRACKER_UNLOCALIZED_NAME);
        GameRegistry.registerBlock(itemPortal, ItemBlockPortal.class, BlockInfo.QUEST_PORTAL_UNLOCALIZED_NAME);
    }

    public static void registerTileEntities() {
        GameRegistry.registerTileEntity(TileEntityBarrel.class, BlockInfo.ITEMBARREL_TE_KEY);
        GameRegistry.registerTileEntity(TileEntityTracker.class, BlockInfo.QUEST_TRACKER_TE_KEY);
        GameRegistry.registerTileEntity(TileEntityPortal.class, BlockInfo.QUEST_PORTAL_TE_KEY);
    }

    public static void registerRecipes() {
        GameRegistry.addRecipe(
            new ItemStack(ModBlocks.itemBarrel),
            "wgw",
            "gqg",
            "wgw",
            'w',
            Blocks.planks,
            'q',
            ModItems.book.setContainerItem(ModItems.book),
            'g',
            Blocks.glass);
    }

    private ModBlocks() {}
}
