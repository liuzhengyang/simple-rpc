package com.github.liuzhengyang.simplerpc.core.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2016-12-17
 */
public class InetUtil {
	private InetUtil(){}

	// TODO review this method
	public static String getLocalIp() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}
}
