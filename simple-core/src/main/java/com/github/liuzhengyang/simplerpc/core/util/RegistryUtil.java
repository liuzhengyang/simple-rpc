package com.github.liuzhengyang.simplerpc.core.util;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2017-02-28
 */
public class RegistryUtil {
	public static final String ZK_BASE_PATH = "/simplerpc";
	public static String getServicePath(String serviceName) {
		return ZK_BASE_PATH + "/services/" + serviceName;
	}

	public static String getServiceInstancePath(String serviceName, String ip, int port) {
		String servicePath = RegistryUtil.getServicePath(serviceName);
		return servicePath + "/" + ip + ":" + port;
	}
}
