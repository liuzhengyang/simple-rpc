package com.github.liuzhengyang.simplerpc.core;

import lombok.Data;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2017-01-07
 */
@Data
public class NotifyEvent {
	private String ip;
	private String port;
	private boolean up;
}
