package engineers.workshop.client.page.setting;

import engineers.workshop.common.items.Upgrade;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Side {
	private int x;
	private int y;
	private EnumFacing direction;
	private Setting setting;
	private Transfer input;
	private Transfer output;

	public Side(Setting setting, EnumFacing direction, int x, int y) {
		this.x = x;
		this.y = y;
		this.direction = direction;
		this.setting = setting;

		input = new Transfer(true);
		output = new Transfer(false);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public boolean isOutputEnabled() {
		return output.isEnabled();
	}

	public void setOutputEnabled(boolean value) {
		output.setEnabled(value);
	}

	public boolean isInputEnabled() {
		return input.isEnabled();
	}

	public void setInputEnabled(boolean value) {
		input.setEnabled(value);
	}

	public EnumFacing getDirection() {
		return direction;
	}

	public Setting getSetting() {
		return setting;
	}

	public Transfer getOutput() {
		return output;
	}

	public Transfer getInput() {
		return input;
	}

	public List<String> getDescription(boolean selected) {
		List<String> str = new ArrayList<String>();
		str.add(StringUtils.capitalize(direction.getName()));

		if (selected) {
			str.add(TextFormatting.YELLOW + "Selected");
		}

		str.add("");
		addTransferInfo(str, input, TextFormatting.BLUE);
		addTransferInfo(str, output, TextFormatting.RED);

		return str;
	}

	private void addTransferInfo(List<String> lst, Transfer transfer, TextFormatting color) {
		String name = transfer.isInput() ? "Input" : "Output";
		if (transfer.isEnabled()) {
			lst.add(color + name + ": Enabled");
			if (transfer.isAuto() && setting.table.getUpgradePage().hasGlobalUpgrade(Upgrade.AUTO_TRANSFER)) {
				lst.add(TextFormatting.GRAY + name + " Transfer: " + TextFormatting.GREEN + "Auto");
			}
			if (transfer.hasFilter(setting.table)) {
				if (transfer.hasWhiteList()) {
					lst.add(TextFormatting.GRAY + name + " Filter: " + TextFormatting.WHITE + "White list");
				} else {
					lst.add(TextFormatting.GRAY + name + " Filter: " + TextFormatting.DARK_GRAY + "Black list");
				}
			}
		} else {
			lst.add(TextFormatting.GRAY + name + ": Disabled");
		}
	}
}
