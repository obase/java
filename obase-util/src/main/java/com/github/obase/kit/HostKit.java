package com.github.obase.kit;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import com.github.obase.WrappedException;
import com.github.obase.coding.Hex;

public class HostKit {

	private HostKit() {
	}

	public static final String HOSTID = joinAllMacaddr();

	// Join all not null mac addrs
	static String joinAllMacaddr() {
		try {
			List<String> list = new LinkedList<String>();
			for (Enumeration<NetworkInterface> iter = NetworkInterface.getNetworkInterfaces(); iter.hasMoreElements();) {
				byte[] bs = iter.nextElement().getHardwareAddress();
				if (bs != null) {
					list.add(Hex.encode(bs));
				}
			}
			Collections.sort(list);
			StringBuilder sb = new StringBuilder(17 * list.size());
			for (String it : list) {
				sb.append(it);
			}
			return sb.toString();
		} catch (SocketException se) {
			throw new WrappedException(se);
		}
	}

}
