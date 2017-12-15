package engineers.workshop.common.network.packets;

import engineers.workshop.EngineersWorkshop;
import engineers.workshop.client.container.ContainerTable;
import engineers.workshop.common.network.data.DataType;
import engineers.workshop.common.table.TileTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.io.IOException;

public class DataPacket implements INetworkPacket<DataPacket> {

	public BlockPos tablePos;
	public PacketId packetId;
	public NBTTagCompound compound;
	public DataType dataType;

	public NBTTagCompound createCompound() {
		NBTTagCompound tagCompound = new NBTTagCompound();
		this.compound = tagCompound;
		return tagCompound;
	}

	@Override
	public void writeData(ExtendedPacketBuffer buffer) throws IOException {
		if (tablePos != null) {
			buffer.writeBoolean(true);
			buffer.writeBlockPos(tablePos);
		} else {
			buffer.writeBoolean(false);
			buffer.writeBlockPos(new BlockPos(0, 0, 0));
		}

		if (compound != null) {
			buffer.writeBoolean(true);
			buffer.writeCompoundTag(compound);
		} else {
			buffer.writeBoolean(false);
			buffer.writeCompoundTag(new NBTTagCompound());
		}

		if (dataType != null) {
			buffer.writeBoolean(true);
			buffer.writeInt(dataType.ordinal());
		} else {
			buffer.writeBoolean(false);
			buffer.writeInt(0);
		}

		buffer.writeInt(packetId.ordinal());
	}

	@Override
	public void readData(ExtendedPacketBuffer buffer) throws IOException {
		if (buffer.readBoolean()) {
			tablePos = buffer.readBlockPos();
		} else {
			buffer.readBlockPos();
			tablePos = null;
		}
		if (buffer.readBoolean()) {
			compound = buffer.readCompoundTag();
		} else {
			buffer.readCompoundTag();
		}

		if (buffer.readBoolean()) {
			dataType = DataType.values()[buffer.readInt()];
		} else {
			buffer.readInt();
		}

		packetId = PacketId.values()[buffer.readInt()];
	}

	@Override
	public void processData(DataPacket message, MessageContext context) {
		if (context.side == Side.CLIENT) {
			onPacket(message, EngineersWorkshop.proxy.getPlayer(), false);
		} else {
			onPacket(message, context.getServerHandler().player, true);
		}
	}

	private void onPacket(DataPacket message, EntityPlayer player,
	                      boolean onServer) {
		PacketId id = message.packetId;
		TileTable table = null;

		if (id.isInInterface()) {
			if (player.openContainer instanceof ContainerTable) {
				table = ((ContainerTable) player.openContainer).getTable();
			}
		}
		if (table == null && message.tablePos != null) {
			BlockPos tablePos = message.tablePos;
			World world = player.world;
			if (!world.isBlockLoaded(tablePos))
				return;
			TileEntity te = world.getTileEntity(tablePos);
			if (te instanceof TileTable) {
				table = (TileTable) te;
			}
		}

		if (table != null) {
			if (onServer) {
				table.receiveServerPacket(message, id, player);
			} else {
				table.receiveClientPacket(message, id);
			}
		}
	}
}
