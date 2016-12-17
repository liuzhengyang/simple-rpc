package com.github.liuzhengyang.simplerpc.core.lb;

import lombok.Data;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2016-12-17
 */
@Data
public class Endpoint {
	private String ip;
	private int port;
}
