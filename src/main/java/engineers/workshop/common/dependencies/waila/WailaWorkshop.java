package engineers.workshop.common.dependencies.waila;


import java.util.List;

import engineers.workshop.common.items.Upgrade;
import engineers.workshop.common.table.TileTable;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WailaWorkshop implements IWailaDataProvider {

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return Upgrade.CHARGED.getItemStack();
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
		currenttip.add(String.format("Power: %s / %s", formatNumber(accessor.getNBTData().getInteger("power")), formatNumber(accessor.getNBTData().getInteger("maxPower"))));
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world,
			BlockPos pos) {
		TileTable table = (TileTable) te;
		tag.setInteger("power", (int)table.getStoredPower());
		tag.setInteger("maxPower", (int)table.getCapacity());
		return tag;
	}
	
    private String formatNumber(int number) {
        return String.format("%,d", number).replace((char)160,(char)32);
    }
}
