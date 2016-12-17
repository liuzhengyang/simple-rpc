package com.github.liuzhengyang.simplerpc.core.lb;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2016-12-17
 */
public class RegisterUtil {
	private static Registry registry = new MapRegistryImpl();

	public static void setImpl(Registry registry) {
		RegisterUtil.registry = registry;
	}

	public static void Register(String service, String ip, int port) {
		Endpoint endpoint = new Endpoint();
		endpoint.setIp(ip);
		endpoint.setPort(port);
		registry.registerService(service, endpoint);
	}
}
