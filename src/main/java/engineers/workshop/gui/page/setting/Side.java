package engineers.workshop.gui.page.setting;


import java.util.ArrayList;
import java.util.List;

import engineers.workshop.items.Upgrade;
import net.minecraft.util.text.TextFormatting;

public class Side {
    private int x;
    private int y;
    private engineers.workshop.gui.page.setting.Direction direction;
    private engineers.workshop.gui.page.setting.Setting setting;
    private engineers.workshop.gui.page.setting.Transfer input;
    private engineers.workshop.gui.page.setting.Transfer output;

    public Side(engineers.workshop.gui.page.setting.Setting setting, engineers.workshop.gui.page.setting.Direction direction, int x, int y) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.setting = setting;

        input = new engineers.workshop.gui.page.setting.Transfer(true);
        output = new engineers.workshop.gui.page.setting.Transfer(false);
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

    public boolean isInputEnabled() {
        return input.isEnabled();
    }

    public void setOutputEnabled(boolean value) {
        output.setEnabled(value);
    }

    public void setInputEnabled(boolean value) {
        input.setEnabled(value);
    }

    public engineers.workshop.gui.page.setting.Direction getDirection() {
        return direction;
    }

    public engineers.workshop.gui.page.setting.Setting getSetting() {
        return setting;
    }

    public engineers.workshop.gui.page.setting.Transfer getOutput() {
        return output;
    }

    public engineers.workshop.gui.page.setting.Transfer getInput() {
        return input;
    }

    public List<String> getDescription(boolean selected) {
        List<String> str = new ArrayList<String>();
        str.add(direction.getName());

        String description = direction.getDescription();
        if (description != null) {
            str.add(TextFormatting.GRAY + description);
        }
        if (selected) {
            str.add(TextFormatting.YELLOW + "Selected");
        }

        str.add("");
        addTransferInfo(str, input, TextFormatting.BLUE);
        addTransferInfo(str, output, TextFormatting.RED);

        return str;
    }

    private void addTransferInfo(List<String> lst, engineers.workshop.gui.page.setting.Transfer transfer, TextFormatting color) {
        String name = transfer.isInput() ? "Input" : "Output";
        if (transfer.isEnabled()) {
            lst.add(color + name + ": Enabled");
            if (transfer.isAuto() && setting.table.getUpgradePage().hasGlobalUpgrade(Upgrade.AUTO_TRANSFER)) {
                lst.add(TextFormatting.GRAY + name + " Transfer: " + TextFormatting.GREEN + "Auto");
            }
            if (transfer.hasFilter(setting.table)) {
                if (transfer.hasWhiteList()) {
                    lst.add(TextFormatting.GRAY + name + " Filter: " + TextFormatting.WHITE + "White list");
                }else{
                    lst.add(TextFormatting.GRAY + name + " Filter: " + TextFormatting.DARK_GRAY + "Black list");
                }
            }
        }else{
            lst.add(TextFormatting.GRAY + name + ": Disabled");
        }
    }
}
