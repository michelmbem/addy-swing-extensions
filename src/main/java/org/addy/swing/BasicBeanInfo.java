package org.addy.swing;

import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.SimpleBeanInfo;
import java.lang.reflect.ParameterizedType;

public class BasicBeanInfo<T> extends SimpleBeanInfo {
	private final Class<T> beanType;

	@SuppressWarnings("unchecked")
	public BasicBeanInfo() {
		beanType = (Class<T>) ((ParameterizedType) getClass()
				.getGenericSuperclass())
				.getActualTypeArguments()[0];
	}

	@Override
	public Image getIcon(int iconKind) {
		String beanTypeName = beanType.getSimpleName();
        return switch (iconKind) {
            case BeanInfo.ICON_COLOR_16x16 -> loadImage(beanTypeName + "Color16.gif");
            case BeanInfo.ICON_COLOR_32x32 -> loadImage(beanTypeName + "Color32.gif");
            case BeanInfo.ICON_MONO_16x16 -> loadImage(beanTypeName + "Mono16.gif");
            case BeanInfo.ICON_MONO_32x32 -> loadImage(beanTypeName + "Mono32.gif");
            default -> throw new IllegalArgumentException("Invalid iconKind value");
        };
	}
	
	@Override
	public BeanDescriptor getBeanDescriptor() {
		return new BeanDescriptor(beanType);
	}
}
