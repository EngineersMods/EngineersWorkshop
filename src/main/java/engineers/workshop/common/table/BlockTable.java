package engineers.workshop.common.table;

import com.ewyboy.bibliotheca.common.block.BlockBaseModeledFacing;
import engineers.workshop.EngineersWorkshop;
import engineers.workshop.client.container.slot.SlotBase;
import engineers.workshop.common.loaders.CreativeTabLoader;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;

import java.util.Random;

public class BlockTable extends BlockBaseModeledFacing implements ITileEntityProvider {

	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

	public BlockTable() {
		super(Material.ROCK);
		setHardness(3.5f);
		setCreativeTab(CreativeTabLoader.workshop);
		//TODO Donno wat to do with this stuff below
		//GameData.register_impl(this);
		//GameData.register_impl(itemBlock);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileTable();
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			FMLNetworkHandler.openGui(playerIn, EngineersWorkshop.instance, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	}

	//TODO fix the double braking of the block
	//Removes the tile from the world
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		super.breakBlock(worldIn, pos, state);
		worldIn.removeTileEntity(pos);
	}

	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		if (!world.isRemote) {
			if (!player.isCreative()) {
				dropInventory(world, pos);
			}
			world.destroyBlock(pos, !player.isCreative());
		}
		return false;
	}

	private void dropInventory(World world, BlockPos pos) {
		if (!world.isRemote) {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();

			TileEntity tileEntity = world.getTileEntity(pos);

			if (tileEntity instanceof TileTable) {
				TileTable table = (TileTable) tileEntity;
				for (SlotBase slot : table.getSlots()) {
					if (slot.shouldDropOnClosing()) {
						ItemStack itemStack = slot.getStack();
						if (!itemStack.isEmpty()) {
							Random random = new Random();

							float dX = random.nextFloat() * 0.8F + 0.1F;
							float dY = random.nextFloat() * 0.8F + 0.1F;
							float dZ = random.nextFloat() * 0.8F + 0.1F;

							EntityItem entityItem = new EntityItem(world, (double) ((float) x + dX),
								(double) ((float) y + dY), (double) ((float) z + dZ), itemStack.copy());
							if (itemStack.hasTagCompound()) {
								entityItem.getItem().setTagCompound(itemStack.getTagCompound().copy());
							}
							float factor = 0.05F;

							entityItem.motionX = random.nextGaussian() * (double) factor;
							entityItem.motionX = random.nextGaussian() * (double) factor + 0.2D;
							entityItem.motionX = random.nextGaussian() * (double) factor;

							world.spawnEntity(entityItem);
							itemStack.setCount(0);
						}
					}
				}
			}
		}
	}
}
