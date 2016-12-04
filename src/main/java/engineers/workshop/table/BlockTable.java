package engineers.workshop.table;

import engineers.workshop.EngineersWorkshop;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockTable extends Block implements ITileEntityProvider{

	public BlockTable() {
		super(Material.PISTON);
		setCreativeTab(EngineersWorkshop.tabWorkshop);
		
		//Register
		setRegistryName("engineersworkshop:blockTable");
		setUnlocalizedName("engineersworkshop:blockTable");
		GameRegistry.register(this);
        GameRegistry.register(new ItemBlock(this), getRegistryName());
        
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileTable();
	}

}
