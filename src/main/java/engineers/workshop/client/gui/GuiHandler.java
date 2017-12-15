package engineers.workshop.client.gui;

import engineers.workshop.client.container.ContainerTable;
import engineers.workshop.common.table.TileTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
		if (te instanceof TileTable) {
			return new ContainerTable((TileTable) te, player);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
		if (te instanceof TileTable) {
			return new GuiTable((TileTable) te, player);
		}
		return null;
	}
}
