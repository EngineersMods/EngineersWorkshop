package engineers.workshop.client.page.security;

public enum PermissionNode {
	CAN_OPEN("can_open", 0, 1), CAN_EDIT("can_edit_slots", 0, 1), CAN_MANAGE_UPGRADES("can_edit_upgrades", 0, 1), CAN_MANAGE_TRANSFER("can_edit_transfer", 0, 1), SECURITY_EDITING_LEVEL("security_edit_level", 1, 3);
	
	
	public final String key;
	public final int defaultValue, maxValue;
	PermissionNode(String key, int defaultValue, int maxValue){
		this.key = key;
		this.defaultValue = defaultValue;
		this.maxValue = maxValue;
	}
	
	public int getDefaultValue() {
		return defaultValue;
	}
	
}
