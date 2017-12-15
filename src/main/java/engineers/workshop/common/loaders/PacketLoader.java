package engineers.workshop.common.loaders;

import engineers.workshop.common.network.packets.DataPacket;
import engineers.workshop.common.network.packets.RegisterPacketEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by EwyBoy
 */
public class PacketLoader {

    public static void loadPackets() {
        RegisterPacketEvent event = new RegisterPacketEvent();
        event.registerPacket(DataPacket.class, Side.SERVER);
        event.registerPacket(DataPacket.class, Side.CLIENT);
    }
}
