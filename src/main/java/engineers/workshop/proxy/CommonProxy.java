package engineers.workshop.proxy;

import com.ewyboy.bibliotheca.common.loaders.BlockLoader;
import com.ewyboy.bibliotheca.common.loaders.ItemLoader;
import com.ewyboy.bibliotheca.common.loaders.TileEntityLoader;

import engineers.workshop.EngineersWorkshop;
import engineers.workshop.client.gui.GuiHandler;
import static engineers.workshop.common.Reference.Info.MODID;
import engineers.workshop.common.loaders.ConfigLoader;
import engineers.workshop.common.loaders.PacketLoader;
import engineers.workshop.common.loaders.RecipeLoader;
import engineers.workshop.common.register.Register;
import engineers.workshop.common.util.EWLogger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy {

	public static FMLEventChannel packetHandler;

	public Side getSide() {
		return Side.SERVER;
	}

	public void preInit(FMLPreInitializationEvent event) {
		EWLogger.setLogger(event.getModLog());
		ConfigLoader.loadConfig(event.getSuggestedConfigurationFile());
		BlockLoader.init(MODID, Register.Blocks.class);
		ItemLoader.registerItem(MODID, Register.Items.itemUpgrade, "upgrade");
		TileEntityLoader.init(Register.Tiles.class);
		PacketLoader.loadPackets();
	}

	public void init(FMLInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(EngineersWorkshop.instance, new GuiHandler());
		RecipeLoader.loadRecipes();
	}

	public void postInit(FMLPostInitializationEvent event) {
	}

	public EntityPlayer getPlayer() {
		throw new RuntimeException("Not supported on the server");
	}

}
