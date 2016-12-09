package engineers.workshop.common.table;

import engineers.workshop.EngineersWorkshop;
import engineers.workshop.common.loaders.BlockLoader;
import engineers.workshop.common.loaders.CreativeTabLoader;
import engineers.workshop.common.util.Logger;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
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

	public static final PropertyDirection FACING = PropertyDirection.create("facing");

	public BlockTable() {
		super(Material.PISTON);
		setCreativeTab(CreativeTabLoader.tabWorkshop);
		setRegistryName(MODID + ":" + "blockTable");
		setUnlocalizedName(MODID + ":" + "blockTable");
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this), getRegistryName());
		GameRegistry.registerTileEntity(TileTable.class, MODID + ":" + "blockTable");
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}

	@SideOnly(Side.CLIENT)
	public void registerModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}

	public static EnumFacing getFacingFromEntity(BlockPos clickedBlock, EntityLivingBase entity) {
		return EnumFacing.getFacingFromVector((float) (entity.posX - clickedBlock.getX()), (float) (entity.posY - clickedBlock.getY()), (float) (entity.posZ - clickedBlock.getZ()));
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		Logger.info("FACING: " + state.getValue(FACING));
		world.setBlockState(pos, state.withProperty(FACING, getFacingFromEntity(pos, placer)), 2);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState()
				.withProperty(FACING, EnumFacing.getFront(meta & 7));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
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
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, heldItem, side, hitX, hitY, hitZ);
	}

	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		if (!world.isRemote) {
			try {
				ItemStack stack = player.getHeldItemMainhand();
				if (stack != null) {
					if (EnchantmentHelper.getEnchantments(stack).keySet().contains(Enchantment.getEnchantmentByLocation("minecraft:silk_touch"))) {
						TileEntity te = world.getTileEntity(pos);
						ItemStack eStack = getPickBlock(state, null, world, pos, null);
						eStack = Minecraft.getMinecraft().storeTEInStack(eStack, te);
						EntityItem entityItem = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), eStack);
						world.spawnEntityInWorld(entityItem);

						world.destroyBlock(pos, false);
						return false;
					}
				}
			} catch (Throwable ignored) {
				ignored.printStackTrace();
			}

			dropInventory(world, pos);
			EntityItem entityItem = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(BlockLoader.blockTable));
			world.spawnEntityInWorld(entityItem);
			world.destroyBlock(pos, false);
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
						EntityItem entityItem = new EntityItem(world, (double) ((float) x + dX),
								(double) ((float) y + dY), (double) ((float) z + dZ), itemStack.copy());
						if (itemStack.hasTagCompound()) {
							entityItem.getEntityItem()
									.setTagCompound(itemStack.getTagCompound().copy());
						}
						float factor = 0.05F;
						entityItem.motionX = random.nextGaussian() * (double) factor;
						entityItem.motionX = random.nextGaussian() * (double) factor + 0.20000000298023224D;
						entityItem.motionX = random.nextGaussian() * (double) factor;
						world.spawnEntityInWorld(entityItem);
						itemStack.stackSize = 0;
					}

				}
			}
		}
	}

	private static final int[] SIDES_INDICES = {0, 2, 3, 1};
	private static final int[] SIDES = {0, 3, 1, 2};

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
