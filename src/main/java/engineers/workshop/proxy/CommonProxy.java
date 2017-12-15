package engineers.workshop.proxy;

import com.ewyboy.bibliotheca.common.loaders.BlockLoader;
import com.ewyboy.bibliotheca.common.loaders.ItemLoader;
import com.ewyboy.bibliotheca.common.loaders.TileEntityLoader;
import engineers.workshop.EngineersWorkshop;
import engineers.workshop.client.gui.GuiHandler;
import engineers.workshop.common.Config;
import engineers.workshop.common.items.ItemUpgrade;
import engineers.workshop.common.network.DataPacket;
import engineers.workshop.common.network.RegisterPacketEvent;
import engineers.workshop.common.register.Register;
import engineers.workshop.common.util.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
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
	//TODO
	public void preInit(FMLPreInitializationEvent event) {
		Config.loadConfig(event.getSuggestedConfigurationFile());
		BlockLoader.init(Reference.Info.MODID, Register.Blocks.class);
		ItemLoader.init(Reference.Info.MODID, Register.Items.class);
		TileEntityLoader.init(Register.Tiles.class);
		itemUpgrade = new ItemUpgrade();
		RegisterPacketEvent event1 = new RegisterPacketEvent();
		event1.registerPacket(DataPacket.class, Side.SERVER);
		event1.registerPacket(DataPacket.class, Side.CLIENT);
		MinecraftForge.EVENT_BUS.register(this);
		//loadRecipes();
	}

	public void init(FMLInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(EngineersWorkshop.instance, new GuiHandler());
	}

	public void postInit(FMLPostInitializationEvent event) {

	}

	public EntityPlayer getPlayer() {
		throw new RuntimeException("Not supported on the server");
	}

}
