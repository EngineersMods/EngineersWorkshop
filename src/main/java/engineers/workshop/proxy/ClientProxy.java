package engineers.workshop.proxy;

import engineers.workshop.common.items.Upgrade;
import engineers.workshop.common.register.Register;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

import static engineers.workshop.common.Reference.Info.MODID;

public class ClientProxy extends CommonProxy {

	public Side getSide() {
		return Side.CLIENT;
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		//TODO Move this over to lib - in some way
		for (int i = 0; i < Upgrade.values().length; ++i) {
			Upgrade[] upgrades = Upgrade.values().clone();
			ModelLoader.setCustomModelResourceLocation(Register.Items.itemUpgrade, i, new ModelResourceLocation(MODID + ":upgrades/" + upgrades[i].getName()));
		}
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}

	@Override
	public EntityPlayer getPlayer() {
		return FMLClientHandler.instance().getClient().player;
	}

}
