package engineers.workshop.common.network;

import engineers.workshop.client.gui.container.ContainerTable;
import engineers.workshop.common.table.TileTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketHandler {

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onClientPacket(FMLNetworkEvent.ClientCustomPacketEvent event) {
        onPacket(event, FMLClientHandler.instance().getClient().thePlayer, false);
    }

    @SubscribeEvent
    public void onServerPacket(FMLNetworkEvent.ServerCustomPacketEvent event) {
        onPacket(event, ((NetHandlerPlayServer)event.getHandler()).playerEntity, true);
    }

    private void onPacket(@SuppressWarnings("rawtypes") FMLNetworkEvent.CustomPacketEvent event, EntityPlayer player, boolean onServer) {
        DataReader dr = new DataReader(event.getPacket().payload());
        PacketId id = dr.readEnum(PacketId.class);
        TileTable table = null;
        if (id.isInInterface()) {
            if (player.openContainer instanceof ContainerTable) {
                table = ((engineers.workshop.client.gui.container.ContainerTable)player.openContainer).getTable();
            }
        }else{
            int x = dr.readSignedInteger();
            int y = dr.readSignedInteger();
            int z = dr.readSignedInteger();
            World world = player.worldObj;
            TileEntity te = world.getTileEntity(new BlockPos(x,y,z));
            if (te instanceof TileTable) {
                table = (TileTable)te;
            }
        }
        if (table != null) {
            if (onServer) {
                table.receiveServerPacket(dr, id, player);
            }else{
                table.receiveClientPacket(dr, id);
            }
        }
    }

    public static DataWriter getWriter(TileTable table, PacketId id) {
        DataWriter dw = new DataWriter();
        dw.writeEnum(id);
        if (!id.isInInterface()) {
            dw.writeInteger(table.getPos().getX());
            dw.writeInteger(table.getPos().getY());
            dw.writeInteger(table.getPos().getZ());
        }
        return dw;
    }

    public static void sendToPlayer(DataWriter dw, EntityPlayer player) {
        dw.sendToPlayer((EntityPlayerMP)player);
    }

    public static void sendToServer(DataWriter dw) {
        dw.sendToServer();
    }

}
