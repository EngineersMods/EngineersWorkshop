package engineers.workshop.common.network.packets;

public enum PacketId {
	ALL(true),
	TYPE(true),
	CLOSE(false),
	UPGRADE_CHANGE(true),
	RE_OPEN(true),
	CLEAR(true),
	RENDER_UPDATE(false);

	private boolean inInterface;

	PacketId(boolean inInterface) {
		this.inInterface = inInterface;
	}

	public boolean isInInterface() {
		return inInterface;
	}
}
