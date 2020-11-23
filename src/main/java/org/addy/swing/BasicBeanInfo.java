package org.addy.swing;

import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.SimpleBeanInfo;
import java.lang.reflect.ParameterizedType;

public class BasicBeanInfo<T> extends SimpleBeanInfo {
	
	private final Class<?> beanClass;
	
	public BasicBeanInfo() {
		this.beanClass = (Class<?>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	@Override
	public Image getIcon(int iconKind) {
		String beanClassName = beanClass.getSimpleName();
		switch (iconKind) {
			case BeanInfo.ICON_COLOR_16x16:
				return loadImage(beanClassName + "Color16.gif");
			case BeanInfo.ICON_COLOR_32x32:
				return loadImage(beanClassName + "Color32.gif");
			case BeanInfo.ICON_MONO_16x16:
				return loadImage(beanClassName + "Mono16.gif");
			case BeanInfo.ICON_MONO_32x32:
				return loadImage(beanClassName + "Mono32.gif");
			default:
				throw new IllegalArgumentException("Invalid iconKind value");
		}
	}
	
	@Override
	public BeanDescriptor getBeanDescriptor() {
		return new BeanDescriptor(beanClass);
	}
}