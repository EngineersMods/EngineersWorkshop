/*
 * Copyright (c) 2017 modmuss50 and Gigabit101
 *
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 *
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package engineers.workshop.common.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.CRC32;

public class NetworkManager {

	public static HashMap<Class<? extends INetworkPacket>, SimpleNetworkWrapper> packetWrapperMap = new HashMap<>();
	public static HashMap<String, SimpleNetworkWrapper> packageWrapperMap = new HashMap<>();
	private static HashMap<SimpleNetworkWrapper, IntStore> wrapperIdList = new HashMap<>();

	public static void load() {
		MinecraftForge.EVENT_BUS.post(new RegisterPacketEvent());
	}

	public static ArrayList<PacketDetails> packetList = new ArrayList<>();

	public static void sendToServer(INetworkPacket packet) {
		checkPacket(packet);
		getWrapperForPacket(packet.getClass()).sendToServer(new PacketWrapper(packet));
	}

	public static void sendToAllAround(INetworkPacket packet, NetworkRegistry.TargetPoint point) {
		checkPacket(packet);
		getWrapperForPacket(packet.getClass()).sendToAllAround(new PacketWrapper(packet), point);
	}

	public static void sendToAll(INetworkPacket packet) {
		checkPacket(packet);
		getWrapperForPacket(packet.getClass()).sendToAll(new PacketWrapper(packet));
	}

	public static void sendToPlayer(INetworkPacket packet, EntityPlayerMP playerMP) {
		checkPacket(packet);
		getWrapperForPacket(packet.getClass()).sendTo(new PacketWrapper(packet), playerMP);
	}

	public static void sendToWorld(INetworkPacket packet, World world) {
		checkPacket(packet);
		getWrapperForPacket(packet.getClass()).sendToDimension(new PacketWrapper(packet), world.provider.getDimension());
	}

	public static void checkPacket(INetworkPacket packet){
		if (getPacketDetails(packet.getClass()) == null) {
			throw new RuntimeException("Packet " + packet.getClass().getName() + " has not been registered");
		}
	}

	public static PacketDetails getPacketDetails(Class<? extends INetworkPacket> clazz){
		return packetList.stream().filter(packetDetails -> packetDetails.packetClass.equals(clazz)).findAny().orElse(null);
	}

	public static SimpleNetworkWrapper getWrapperForPacket(Class<? extends INetworkPacket> packetClass){
		if(!packetWrapperMap.containsKey(packetClass)){
			return null;
		}
		return packetWrapperMap.get(packetClass);
	}

	public static SimpleNetworkWrapper createOrGetNetworkWrapper(Class<? extends INetworkPacket> packetClass){
		String wrapperName = getWrapperName(packetClass);
		if(packageWrapperMap.containsKey(wrapperName)){
			return packageWrapperMap.get(wrapperName);
		} else {
			SimpleNetworkWrapper newNetworkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(wrapperName);
			packageWrapperMap.put(wrapperName, newNetworkWrapper);
			return newNetworkWrapper;
		}
	}

	public static String getWrapperName(Class<? extends INetworkPacket> packetClass){
		String packageName = packetClass.getCanonicalName().substring(0, packetClass.getCanonicalName().lastIndexOf("."));
		CRC32 crc = new CRC32();
		crc.update(packageName.getBytes());
		//Packet network names have a max size of 20
		//3 chars on the rc bit, 11 on the package name, 1 to the & and the last 5 on the hash
		return "rc&" + packageName.substring(0, 11) + "&" + Long.toString(crc.getValue()).substring(0, 5);
	}

	public static PacketDetails registerPacket(Class<? extends INetworkPacket> packetClass, Side side){
		SimpleNetworkWrapper wrapper = createOrGetNetworkWrapper(packetClass);
		int id = getNextIDForWrapper(wrapper);
		wrapper.registerMessage(PacketWrapper.PacketWrapperHandler.class, PacketWrapper.class, id, side);
		packetWrapperMap.put(packetClass, wrapper);
		PacketDetails packetDetails = new PacketDetails(packetClass, id, wrapper);
		packetList.add(packetDetails);
		return packetDetails;
	}

	public static int getNextIDForWrapper(SimpleNetworkWrapper networkWrapper){
		if(wrapperIdList.containsKey(networkWrapper)){
			wrapperIdList.get(networkWrapper).id++;
			return wrapperIdList.get(networkWrapper).id;
		} else {
			wrapperIdList.put(networkWrapper, new IntStore());
			return 0;
		}
	}

	private static class IntStore {
		int id = 0;
	}

	public static class PacketDetails {
		public Class<? extends INetworkPacket> packetClass;
		public int id;
		SimpleNetworkWrapper networkWrapper;

		public PacketDetails(Class<? extends INetworkPacket> packetClass, int id, SimpleNetworkWrapper networkWrapper) {
			this.packetClass = packetClass;
			this.id = id;
			this.networkWrapper = networkWrapper;
		}
	}

}