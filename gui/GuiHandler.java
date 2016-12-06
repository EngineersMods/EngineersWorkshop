package us.engineersworkshop.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import us.engineersworkshop.gui.container.ContainerTable;
import us.engineersworkshop.tileentity.TileEntityTable;

public class GuiHandler implements IGuiHandler {
    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(new BlockPos(x,y,z));
        if (te instanceof TileEntityTable) {
            return new ContainerTable((TileEntityTable)te, player);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(new BlockPos(x,y,z));
        if (te instanceof TileEntityTable) {
            return new GuiTable((TileEntityTable)te, player);
        }
        return null;
    }
}
