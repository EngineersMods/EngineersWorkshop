package engineers.workshop.client.page.security;

import java.util.HashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class Security {
	private HashMap<String, PlayerSecurity> psecs;

	public Security() {
		psecs = new HashMap<>();
	}

	public String getOwner() {
		for (String i : psecs.keySet())
			if (psecs.get(i).getPermission(PermissionNode.SECURITY_EDITING_LEVEL) == 3)
				return i;
		return null;
	}

	public void readFromNBT(NBTTagCompound tag) {
		if (tag.hasKey("players")) {
			NBTTagList players = tag.getTagList("players", 9); // 9 is the id
																// for list
			for (int i = 0; i < players.tagCount(); i++) {
				NBTTagCompound player = players.getCompoundTagAt(i);
				PlayerSecurity psec = new PlayerSecurity(player.getString("name"));
				for (PermissionNode node : PermissionNode.values()) {
					if (player.hasKey(node.key)) {
						psec.setNode(node, player.getInteger(node.key));
					}
				}

				psecs.put(player.getString("name"), psec);
			}
		}
	}
	
	public NBTTagCompound writeToNBT(NBTTagCompound comp){
		NBTTagList players = new NBTTagList();
		for(String player : psecs.keySet()){
			NBTTagCompound playerComp = new NBTTagCompound();
			for(PermissionNode node : PermissionNode.values()){
				playerComp.setInteger(node.key, psecs.get(player).getPermission(node));
			}
			players.appendTag(playerComp);
		}
		comp.setTag("players", players);
		return comp;
	}
}
