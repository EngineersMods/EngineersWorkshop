package engineers.workshop.common.table;

import engineers.workshop.EngineersWorkshop;
import engineers.workshop.common.loaders.CreativeTabLoader;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

import static engineers.workshop.common.util.Reference.Info.MODID;

public class BlockTable extends Block implements ITileEntityProvider {

	public static final PropertyInteger POWER = PropertyInteger.create("power", 0, 8);

	public BlockTable() {
		super(Material.ROCK);
        setHardness(3.5f);
		setCreativeTab(CreativeTabLoader.tabWorkshop);
		setRegistryName(MODID + ":" + "blockTable");
		setUnlocalizedName(MODID + ":" + "blockTable");
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this), getRegistryName());
		GameRegistry.registerTileEntity(TileTable.class, MODID + ":" + "blockTable");
		setDefaultState(blockState.getBaseState());
	}

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(POWER, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(POWER);
    }

    @Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, POWER);
	}

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        super.updateTick(worldIn, pos, state, rand);
    }

    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        worldIn.setBlockState(pos, stateIn.withProperty(POWER, (int) (Math.random() * (8 - 1)) + 1));
    }

    @SideOnly(Side.CLIENT)
	public void registerModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileTable();
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			FMLNetworkHandler.openGui(playerIn, EngineersWorkshop.instance, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	}

	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		if (!world.isRemote) {
            boolean isCreative = true;
            if (!player.isCreative()) {
                dropInventory(world, pos);
                isCreative = false;
            }
			world.destroyBlock(pos, !isCreative);
		}
		return false;
	}

	protected void dropInventory(World world, BlockPos pos) {
		if (!world.isRemote) {
            int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();

            TileEntity tileEntity = world.getTileEntity(pos);
			if (tileEntity instanceof IInventory) {
				IInventory inventory = (IInventory) tileEntity;
				for (int i = 0; i < inventory.getSizeInventory(); i++) {
					ItemStack itemStack = inventory.getStackInSlot(i);
					if (itemStack != null && itemStack.stackSize > 0) {
						Random random = new Random();

                        float dX = random.nextFloat() * 0.8F + 0.1F;
						float dY = random.nextFloat() * 0.8F + 0.1F;
						float dZ = random.nextFloat() * 0.8F + 0.1F;

						EntityItem entityItem = new EntityItem(world, (double) ((float) x + dX), (double) ((float) y + dY), (double) ((float) z + dZ), itemStack.copy());
						if (itemStack.hasTagCompound()) {
							entityItem.getEntityItem().setTagCompound(itemStack.getTagCompound().copy());
						}
						float factor = 0.05F;

                        entityItem.motionX = random.nextGaussian() * (double) factor;
						entityItem.motionX = random.nextGaussian() * (double) factor + 0.2D;
						entityItem.motionX = random.nextGaussian() * (double) factor;

                        world.spawnEntityInWorld(entityItem);
						itemStack.stackSize = 0;
					}
				}
			}
		}
	}

}
