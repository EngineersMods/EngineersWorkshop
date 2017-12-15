package engineers.workshop.common.network;

import engineers.workshop.common.table.TileTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class PacketHandler {

	public static DataPacket getPacket(TileTable table, PacketId id) {
		DataPacket dw = new DataPacket();
		dw.packetId = id;
		if (!id.isInInterface()) {
			dw.tablePos = table.getPos();
		}
		return dw;
	}

	public static void sendToPlayer(DataPacket dw, EntityPlayer player) {
		NetworkManager.sendToPlayer(dw, (EntityPlayerMP) player);
	}

	public static void sendToServer(DataPacket dw) {
		NetworkManager.sendToServer(dw);
	}

}
