package engineers.workshop.items;

import java.util.EnumSet;
import java.util.List;

import engineers.workshop.EngineersWorkshop;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public enum Upgrade {
	BLANK			(new MaxCount(0), 			ParentType.NULL), 
	AUTO_CRAFTER	(new MaxCount(1), 			ParentType.CRAFTING), 
	STORAGE			(new MaxCount(1),			ParentType.CRAFTING), 
	CHARGED			(new ConfigurableMax(8),	ParentType.NULL), 
	SPEED			(new ConfigurableMax(8),	ParentType.BOTH), 
	QUEUE			(new MaxCount(3), 			ParentType.SMELTING), 
	EFFICIENCY		(new ConfigurableMax(4), 	ParentType.GLOBAL),
	LAVA			(new MaxCount(1), 			ParentType.GLOBAL),
	TESLA			(new MaxCount(1), 			ParentType.GLOBAL), 
	SOLAR			(new ConfigurableMax(1),	ParentType.GLOBAL),
	// EU			(new ConfigurableMax(1), 	ParentType.GLOBAL, "eu"),
	AUTO_TRANSFER	(new MaxCount(1),			ParentType.GLOBAL),
	FILTER			(new MaxCount(1),			ParentType.GLOBAL),
	TRANSFER		(new ConfigurableMax(6, 20),ParentType.GLOBAL);

	/**
	 * PATTERN("Pattern Crafting", "Remembers old recipes", 4,
	 * ParentType.CRAFTING), RESTOCK("Restock Control", "Only produce more items
	 * when there isn't enough of them", 1),
	 */

	private String name;
	private String description;
	private MaxCount maxCount;
	private EnumSet<ParentType> validParents;

	Upgrade(MaxCount maxCount, EnumSet<ParentType> validParents) {
		this.validParents = validParents;
		this.name = toString().toLowerCase();
		this.description = String.format("engineersworkshop:upgrade.%s.description", name);
		this.maxCount = maxCount;
		maxCount.init(this);
	}

	Upgrade(MaxCount maxCount, ParentType type) {
		this(maxCount, type == null || type == ParentType.NULL ? EnumSet.noneOf(ParentType.class)
				: (type == ParentType.BOTH ? EnumSet.of(ParentType.CRAFTING, ParentType.SMELTING) : EnumSet.of(type)));
	}

	public String getName() {
		return name;
	}

	public boolean isEnabled() {
		return maxCount.getConfigurableMax() == 0 || maxCount.getMax() > 0;
	}

	 public ItemStack getItemStack() {
	 return new ItemStack(EngineersWorkshop.Items.upgrade, 1, ordinal());
	 }
	
	 public static ItemStack getInvalidItemStack() {
	 return new ItemStack(EngineersWorkshop.Items.upgrade, 1, values().length);
	 }

	public void addInfo(List<String> info) {
		info.add(I18n.format(description));
		if (GuiScreen.isShiftKeyDown()) {
			if (getMaxCount() == 1)
				info.add(I18n.format("engineersworkshop:upgrade.unstackable"));
			else if (getMaxCount() > 1)
				info.add(I18n.format("engineersworkshop:upgrade.stackable", getMaxCount()));
			for (ParentType validParent : validParents) {
				info.add(I18n.format(validParent.description));
			}
		}
	}

	public boolean isValid(ItemStack parent) {
		for (ParentType validParent : validParents) {
			if (!validParent.isValidParent(parent)) {
				return false;
			}
		}
		return true;
	}

	public int getMaxCount() {
		return maxCount.getMax();
	}

	public MaxCount getMaxCountObject() {
		return maxCount;
	}

	public enum ParentType {
		CRAFTING("Works with Crafting Tables") {
			@Override
			protected boolean isValidParent(ItemStack item) {
				return item != null && Item.getItemFromBlock(Blocks.CRAFTING_TABLE).equals(item.getItem());
			}
		},
		SMELTING("Works with Furnaces") {
			@Override
			protected boolean isValidParent(ItemStack item) {
				return item != null && Item.getItemFromBlock(Blocks.FURNACE).equals(item.getItem());
			}
		},
		GLOBAL("Upgrades the entire Production Table") {
			@Override
			protected boolean isValidParent(ItemStack item) {
				return item == null;
			}
		},
		BOTH("upgrade.both") {
			@Override
			protected boolean isValidParent(ItemStack item) {
				return CRAFTING.isValidParent(item) || SMELTING.isValidParent(item);
			}

		}, NULL("upgrade.null"){
			@Override
			protected boolean isValidParent(ItemStack item) {
				return false;
			}
		};

		private String description;

		ParentType(String description) {
			this.description = description;
		}

		protected abstract boolean isValidParent(ItemStack item);
	}

	public static class MaxCount {
		private int max;
		private int defaultMax;

		public MaxCount(int max) {
			this.max = max;
			this.defaultMax = max;
		}

		public int getMax() {
			return max;
		}

		public void setMax(int value) {
			this.max = value;
		}

		public int getConfigurableMax() {
			return defaultMax;
		}

		public void init(Upgrade upgrade) {

		}
	}

	private static class ConfigurableMax extends MaxCount {
		private boolean isGlobal;
		private int configurableMax;

		private ConfigurableMax(int max, int configurableMax) {
			super(max);
			this.configurableMax = configurableMax;
		}

		private ConfigurableMax(int max) {
			this(max, -1);
		}

		private static final int GLOBAL_MAX_COUNT = 8 * 64;
		private static final int MAX_COUNT = 7 * 64;

		@Override
		public int getConfigurableMax() {
			return configurableMax != -1 ? configurableMax : isGlobal ? GLOBAL_MAX_COUNT : MAX_COUNT;
		}

		@Override
		public void init(Upgrade upgrade) {
			isGlobal = upgrade.validParents.contains(ParentType.GLOBAL);
		}
	}

}
