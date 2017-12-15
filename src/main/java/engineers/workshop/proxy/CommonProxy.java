package engineers.workshop.proxy;

import com.ewyboy.bibliotheca.common.loaders.BlockLoader;
import com.ewyboy.bibliotheca.common.loaders.ItemLoader;
import com.ewyboy.bibliotheca.common.loaders.TileEntityLoader;
import engineers.workshop.EngineersWorkshop;
import engineers.workshop.client.gui.GuiHandler;
import engineers.workshop.common.Reference;
import engineers.workshop.common.items.ItemUpgrade;
import engineers.workshop.common.loaders.ConfigLoader;
import engineers.workshop.common.loaders.PacketLoader;
import engineers.workshop.common.register.Register;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy {

	public static FMLEventChannel packetHandler;

	public Side getSide(){return Side.SERVER;}

	public static Item itemUpgrade;
	//TODO move itemUpgrade out of this and register it with lib instead.

	public void preInit(FMLPreInitializationEvent event) {
		ConfigLoader.loadConfig(event.getSuggestedConfigurationFile());
		BlockLoader.init(Reference.Info.MODID, Register.Blocks.class);
		ItemLoader.init(Reference.Info.MODID, Register.Items.class);
		itemUpgrade = new ItemUpgrade();
		TileEntityLoader.init(Register.Tiles.class);
		PacketLoader.loadPackets();
	}

	public void init(FMLInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(EngineersWorkshop.instance, new GuiHandler());
	}

	public void postInit(FMLPostInitializationEvent event) {}

	public EntityPlayer getPlayer() {
		throw new RuntimeException("Not supported on the server");
	}

}
