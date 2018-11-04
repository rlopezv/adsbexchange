/**
 * 
 */
package net.upmt.moit.distributed.adsbexchange.util;

import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * 
 * Utility class
 * 
 * @author ramon
 *
 */
public final class Util {

	public static Properties convertResourceBundleToProperties(ResourceBundle resource) {
		Properties properties = new Properties();

		Enumeration<String> keys = resource.getKeys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			properties.put(key, resource.getString(key));
		}

		return properties;
	}
}
