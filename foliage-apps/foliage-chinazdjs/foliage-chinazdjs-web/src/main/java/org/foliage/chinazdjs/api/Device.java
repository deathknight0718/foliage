/**
 * 
 */
package org.foliage.chinazdjs.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @author foliage
 *
 */
public class Device {

	private UUID id;

	private String devcode;

	private LocalDateTime createTime, updateTime;

	public static List<Device> list() {
		return null;
	}

	public static Device sync(String devcode) {
		return null;
	}

	public static Device query(String devcode) {
		return null;
	}

	public Device create(Device device) {
		return null;
	}

}
