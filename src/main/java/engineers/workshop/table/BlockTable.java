package engineers.workshop.table;

import java.util.Random;

import engineers.workshop.EngineersWorkshop;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
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
        GameRegistry.registerTileEntity(TileTable.class, "engineersworkshop:table");
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileTable();
	}
	

    @Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			FMLNetworkHandler.openGui(playerIn, EngineersWorkshop.instance, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
		}
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, heldItem, side, hitX, hitY, hitZ);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity te = world.getTileEntity(pos);

		if (te instanceof IInventory) {
			IInventory inventory = (IInventory) te;
			for (int i = 0; i < inventory.getSizeInventory(); ++i) {
				ItemStack item = ((TileTable) inventory).getStackInSlotOnClosing(i);

				if (item != null) {
					float offsetX = world.rand.nextFloat() * 0.8F + 0.1F;
					float offsetY = world.rand.nextFloat() * 0.8F + 0.1F;
					float offsetZ = world.rand.nextFloat() * 0.8F + 0.1F;

					EntityItem entityItem = new EntityItem(world, pos.getX() + offsetX, pos.getY() + offsetY, pos.getY() + offsetZ, item.copy());
					entityItem.motionX = world.rand.nextGaussian() * 0.05F;
					entityItem.motionY = world.rand.nextGaussian() * 0.05F + 0.2F;
					entityItem.motionZ = world.rand.nextGaussian() * 0.05F;

					world.spawnEntityInWorld(entityItem);
				}
			}
		}
		super.breakBlock(world, pos,state);
	}
	
	
	
	
	

    protected void dropInventory(World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
        if(tileEntity instanceof IInventory) {
            IInventory inventory = (IInventory) tileEntity;
            for(int i = 0; i < inventory.getSizeInventory();i++) {
                ItemStack itemStack = inventory.getStackInSlot(i);
                if (itemStack != null && itemStack.stackSize > 0) {
                    Random random = new Random();
                    float dX = random.nextFloat() * 0.8F + 0.1F;
                    float dY = random.nextFloat() * 0.8F + 0.1F;
                    float dZ = random.nextFloat() * 0.8F + 0.1F;
                    EntityItem entityItem = new EntityItem(world, (double)((float)x + dX), (double)((float)y + dY), (double)((float)z + dZ), itemStack.copy());
                    if (itemStack.hasTagCompound()) {
                        entityItem.getEntityItem().setTagCompound((NBTTagCompound)itemStack.getTagCompound().copy());
                    }
                    float factor = 0.05F;
                    entityItem.motionX = random.nextGaussian() * (double)factor;
                    entityItem.motionX = random.nextGaussian() * (double)factor + 0.20000000298023224D;
                    entityItem.motionX = random.nextGaussian() * (double)factor;
                    world.spawnEntityInWorld(entityItem);
                    itemStack.stackSize = 0;
                }

            }
        }
    }
	
	
	private static final int[] SIDES_INDICES = { 0, 2, 3, 1 };
	private static final int[] SIDES = { 0, 3, 1, 2 };
	public static int getSideFromSideAndMeta(int side, int meta) {
		if (side <= 1) {
			return side;
		} else {
			int index = SIDES_INDICES[side - 2] - meta;
			if (index < 0) {
				index += SIDES.length;
			}
			return SIDES[index] + 2;
		}
	}

	public static int getSideFromSideAndMetaReversed(int side, int meta) {
		if (side <= 1) {
			return side;
		} else {
			int index = SIDES_INDICES[side - 2] + meta;
			index %= SIDES.length;

			return SIDES[index] + 2;
		}
	}

}
