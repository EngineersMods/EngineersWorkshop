package engineers.workshop.common.items;

import engineers.workshop.common.Config;
import engineers.workshop.proxy.CommonProxy;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import static engineers.workshop.common.util.Reference.Info.MODID;

public enum Upgrade {
	BLANK(new MaxCount(0), ParentType.NULL),                        // Max count = 0  (Not usable as an upgrade)
	AUTO_CRAFTER(new MaxCount(1), ParentType.CRAFTING),                    // Max count = 1
	STORAGE(new MaxCount(1), ParentType.CRAFTING),                    // Max count = 1
	CHARGED(new ConfigurableMax(8), ParentType.MachineSet),                    // Max count = 8  (Configable)
	SPEED(new ConfigurableMax(8), ParentType.MachineSet),                    // Max count = 8  (Configable)
	QUEUE(new MaxCount(3), EnumSet.of(ParentType.CRUSHING, ParentType.SMELTING)),    // Max count = 3
	AUTO_TRANSFER(new MaxCount(1), ParentType.GLOBAL),                        // Max count = 1
	FILTER(new MaxCount(1), ParentType.GLOBAL),                        // Max count = 1
	TRANSFER(new ConfigurableMax(6, 20), ParentType.GLOBAL),                        // Max count = 6  (Configable upto 20)
	AXE(new MaxCount(1), ParentType.CRUSHING);                                    // Max count = 1
	//COMPACTOR		(new MaxCount(1), 			    ParentType.CRAFTING, AUTO_CRAFTER); 	// Max count = 1

	/**
	 * PATTERN("Pattern Crafting", "Remembers old recipes", 4, ParentType.CRAFTING),
	 * RESTOCK("Restock Control", "Only produce more items* when there isn't enough of them", 1),
	 */

	private String name;
	private String description;
	private MaxCount maxCount;
	private EnumSet<ParentType> validParents;
	private Upgrade dep;

	Upgrade(MaxCount maxCount, EnumSet<ParentType> validParents, Upgrade dep) {
		this.validParents = validParents;
		this.name = toString().toLowerCase();
		this.description = String.format(MODID + ":" + "upgrade" + "." + "%s" + "." + "description", name);
		this.maxCount = maxCount;
		maxCount.init(this);
		this.dep = dep;
	}

	Upgrade(MaxCount maxCount, EnumSet<ParentType> validParents) {
		this(maxCount, validParents, null);
	}

	Upgrade(MaxCount maxCount, ParentType type) {
		this(maxCount, type == null ? EnumSet.of(ParentType.NULL) : EnumSet.of((type)), null);
	}

	Upgrade(MaxCount maxCount, ParentType type, Upgrade dep) {
		this(maxCount, type == null ? EnumSet.of(ParentType.NULL) : EnumSet.of((type)), dep);
	}

	public static ItemStack getInvalidItemStack() {
		return new ItemStack(CommonProxy.itemUpgrade, 1, values().length);
	}

	public Upgrade getDependency() {
		return dep;
	}

	public String getName() {
		return name;
	}

	public boolean isEnabled() {
		return maxCount.getConfigurableMax() == 0 || maxCount.getMax() > 0;
	}

	@Nonnull
	public ItemStack getItemStack() {
		return new ItemStack(CommonProxy.itemUpgrade, 1, ordinal());
	}

	public void addInfo(List<String> info) {
		info.add(I18n.format(description));

		if (!GuiScreen.isShiftKeyDown() && !GuiScreen.isCtrlKeyDown()) {
			info.add(I18n.format("<hold shift for stack info>"));
			info.add(I18n.format("<hold control for parent info>"));
		}

		if (GuiScreen.isShiftKeyDown()) {
			if (getMaxCount() == 1)
				info.add(I18n.format("engineersworkshop:upgrade.unstackable"));
			else if (getMaxCount() > 1)
				info.add(I18n.format("engineersworkshop:upgrade.stackable", getMaxCount()));
			info.addAll(validParents.stream().map(validParent -> I18n.format(validParent.description)).collect(Collectors.toList()));
		}

		if (GuiScreen.isCtrlKeyDown()) {
			if (validParents.size() > 0) {
				info.add("Parents: ");
				info.addAll(validParents.stream().map(ParentType::name).collect(Collectors.toList()));
			}
		}
	}

	public boolean isValid(
		@Nonnull
			ItemStack parent) {
		for (ParentType validParent : validParents) {
			if (validParent.isValidParent(parent)) {
				return true;
			}
		}
		return false;
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
			public boolean isValidParent(
				@Nonnull
					ItemStack item) {
				if (!item.isEmpty()) {
					for (String parent : Config.MACHINES.CRAFTER_BLOCKS) {
						String[] _s = parent.replace(",", "").split("/");
						String regName = parent;
						int meta = -1;
						if (_s.length > 1) {
							regName = _s[0];
							meta = Integer.parseInt(_s[1]);
						}
						if (item.getItem().getRegistryName().toString().equals(regName)) {
							if (meta == -1 || item.getMetadata() == meta)
								return true;
						}
					}
				}

				return false;
			}
		},
		SMELTING("Works with Furnaces") {
			@Override
			public boolean isValidParent(
				@Nonnull
					ItemStack item) {
				if (!item.isEmpty()) {
					for (String parent : Config.MACHINES.FURNACE_BLOCKS) {
						String[] _s = parent.replace(",", "").split("/");
						String regName = parent;
						int meta = -1;
						if (_s.length > 1) {
							regName = _s[0];
							meta = Integer.parseInt(_s[1]);
						}
						if (item.getItem().getRegistryName().toString().equals(regName)) {
							if (meta == -1 || item.getMetadata() == meta)
								return true;
						}
					}
				}

				return false;
			}
		},
		CRUSHING("Works with Crushers") {
			@Override
			public boolean isValidParent(
				@Nonnull
					ItemStack item) {
				if (!item.isEmpty()) {
					for (String parent : Config.MACHINES.CRUSHER_BLOCKS) {
						String[] _s = parent.replace(",", "").split("/");
						String regName = parent;
						int meta = -1;
						if (_s.length > 1) {
							regName = _s[0];
							meta = Integer.parseInt(_s[1]);
						}
						if (item.getItem().getRegistryName().toString().equals(regName)) {

							if (meta == -1 || item.getMetadata() == meta)
								return true;
						}
					}
				}

				return false;
			}

		},

		ALLOY("Works with Alloy Smelters") {
			@Override
			public boolean isValidParent(
				@Nonnull
					ItemStack item) {
				if (!item.isEmpty()) {
					for (String parent : Config.MACHINES.ALLOY_BLOCKS) {
						String[] _s = parent.replace(",", "").split("/");
						String regName = parent;
						int meta = -1;
						if (_s.length > 1) {
							regName = _s[0];
							meta = Integer.parseInt(_s[1]);
						}
						if (item.getItem().getRegistryName().toString().equals(regName)) {
							if (meta == -1 || item.getMetadata() == meta)
								return true;
						}
					}
				}

				return false;
			}

		},

		STORAGE("Works with Chests") {
			@Override
			public boolean isValidParent(
				@Nonnull
					ItemStack item) {
				if (!item.isEmpty()) {
					for (String parent : Config.MACHINES.STORAGE_BLOCKS) {
						String[] _s = parent.replace(",", "").split("/");
						String regName = parent;
						int meta = -1;
						if (_s.length > 1) {
							regName = _s[0];
							meta = Integer.parseInt(_s[1]);
						}
						if (item.getItem().getRegistryName().toString().equals(regName)) {
							if (meta == -1 || item.getMetadata() == meta)
								return true;
						}
					}
				}

				return false;
			}

		},

		GLOBAL("Upgrades the entire Table") {
			@Override
			public boolean isValidParent(
				@Nonnull
					ItemStack item) {
				return item.isEmpty();
			}
		},
		NULL("you shouldn't be seeing this.") {
			@Override
			public boolean isValidParent(
				@Nonnull
					ItemStack item) {
				return false;
			}
		};

		private static final EnumSet<ParentType> MachineSet = EnumSet.of(ParentType.CRAFTING, ParentType.SMELTING, ParentType.CRUSHING, ParentType.ALLOY);
		private String description;

		ParentType(String description) {
			this.description = description;
		}

		public abstract boolean isValidParent(
			@Nonnull
				ItemStack item);
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

		public void init(Upgrade upgrade) {}
	}

	private static class ConfigurableMax extends MaxCount {
		private static final int GLOBAL_MAX_COUNT = 8 * 64;
		private static final int MAX_COUNT = 7 * 64;
		private boolean isGlobal;
		private int configurableMax;

		private ConfigurableMax(int max, int configurableMax) {
			super(max);
			this.configurableMax = configurableMax;
		}
		private ConfigurableMax(int max) {
			this(max, -1);
		}

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
