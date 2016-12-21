package engineers.workshop.common.dependencies.waila;

import engineers.workshop.common.loaders.BlockLoader;
import engineers.workshop.common.table.TileTable;
import engineers.workshop.common.util.helpers.ColorHelper;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class WailaWorkshop implements IWailaDataProvider {

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return new ItemStack(BlockLoader.blockTable);
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		currenttip.add(String.format(ColorHelper.getPowerColor(accessor.getNBTInteger(accessor.getNBTData(), "power"), accessor.getNBTInteger(accessor.getNBTData(), "maxPower")) + "Power: %s / %s", formatNumber(accessor.getNBTData().getInteger("power")), formatNumber(accessor.getNBTData().getInteger("maxPower"))));
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
		TileTable table = (TileTable) te;
		tag.setInteger("power", table.getStoredPower());
		tag.setInteger("maxPower", table.getCapacity());

		return tag;
	}

	private String formatNumber(int number) {
		return NumberFormat.getIntegerInstance(Locale.forLanguageTag(String.valueOf(Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage()))).format(number);
	}
}
