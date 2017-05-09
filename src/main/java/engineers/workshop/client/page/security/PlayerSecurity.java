package engineers.workshop.client.page.security;

import java.util.HashMap;

public class PlayerSecurity {
	
	private String name;
	private HashMap<PermissionNode, Integer> perms;
	
	public PlayerSecurity(String name) {
		perms = new HashMap<>();
		this.name = name;
	}
	
	public void setNode(PermissionNode node, int value) {
		perms.put(node, new Integer(value));
	}
	
	public int getPermission(PermissionNode node){
		return perms.getOrDefault(node.key, node.defaultValue);
	}
	
	public String getName(){
		return name;
	}

}
