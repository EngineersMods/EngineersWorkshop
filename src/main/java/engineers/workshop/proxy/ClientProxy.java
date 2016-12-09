package engineers.workshop.proxy;

import engineers.workshop.common.items.Upgrade;
import engineers.workshop.common.loaders.BlockLoader;
import engineers.workshop.common.loaders.ItemLoader;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

import static engineers.workshop.common.util.Reference.Info.MODID;

public class ClientProxy extends CommonProxy {

    @Override
    public Side getSide() {
        return Side.CLIENT;
    }

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
        for (int i = 0; i < Upgrade.values().length; ++i) {
            Upgrade[] upgrades = Upgrade.values().clone();
            ModelLoader.setCustomModelResourceLocation(ItemLoader.itemUpgrade, i, new ModelResourceLocation(MODID + ":" + upgrades[i].getName()));
        }
        BlockLoader.registerModels();
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}
}
