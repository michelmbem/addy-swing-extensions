package org.addy.swing;

import java.beans.BeanDescriptor;

public class JPictureBoxBeanInfo extends BasicBeanInfo {
	public JPictureBoxBeanInfo() {
		super("JPictureBox", "resources/");
	}

	@Override
	public BeanDescriptor getBeanDescriptor() {
		BeanDescriptor beanDescriptor = new BeanDescriptor(JCalendar.class);
		beanDescriptor.setDisplayName("Picture Box");
		beanDescriptor.setShortDescription("A component that displays an image in various layouts");
		return beanDescriptor;
	}
}