package org.addy.swing;

import java.awt.Image;
import java.beans.SimpleBeanInfo;

public class BasicBeanInfo extends SimpleBeanInfo {
	private final String beanClassName;
	private final String resourcesPath;

	public BasicBeanInfo(String beanClassName, String resourcesPath) {
		this.beanClassName = beanClassName;
		this.resourcesPath = resourcesPath;
	}

	@Override
	public Image getIcon(int iconKind) {
		switch (iconKind) {
			case 1:
				return loadImage(resourcesPath + beanClassName + "Color16.gif");
			case 2:
				return loadImage(resourcesPath + beanClassName + "Color32.gif");
			case 3:
				return loadImage(resourcesPath + beanClassName + "Mono16.gif");
			case 4:
				return loadImage(resourcesPath + beanClassName + "Mono32.gif");
			default:
				throw new IllegalArgumentException("Invalid iconKind value");
		}
	}
}