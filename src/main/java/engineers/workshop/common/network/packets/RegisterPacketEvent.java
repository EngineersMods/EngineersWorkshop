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

package engineers.workshop.common.network.packets;

import engineers.workshop.common.network.NetworkManager;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.lang.reflect.Constructor;
import java.util.Objects;

public class RegisterPacketEvent extends Event {

	public void registerPacket(Class<? extends INetworkPacket> packet, Side processingSide) {
		int id = getNextID(packet);
		if (Objects.equals(packet.getName(), INetworkPacket.class.getName())) {
			throw new RuntimeException("Cannot register a INetworkPacket, please register a child of this");
		}
		boolean hasEmptyConstructor = false;
		for (Constructor constructor : packet.getConstructors()) {
			if (constructor.getParameterCount() == 0) {
				hasEmptyConstructor = true;
			}
		}
		if (!hasEmptyConstructor) {
			throw new RuntimeException("The packet " + packet.getName() + " does not have an empty constructor");
		}
		NetworkManager.registerPacket(packet, processingSide);
	}

	public static int getNextID(Class<? extends INetworkPacket> packet) {
		SimpleNetworkWrapper wrapper = NetworkManager.getWrapperForPacket(packet);
		if(wrapper == null){
			return 0;
		}
		return NetworkManager.getNextIDForWrapper(wrapper);
	}
}