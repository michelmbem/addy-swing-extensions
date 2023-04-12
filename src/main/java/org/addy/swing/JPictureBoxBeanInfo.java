package org.addy.swing;

import java.beans.BeanDescriptor;

public class JPictureBoxBeanInfo extends BasicBeanInfo<JCalendar> {
	@Override
	public BeanDescriptor getBeanDescriptor() {
		BeanDescriptor beanDescriptor = super.getBeanDescriptor();
		beanDescriptor.setDisplayName("Picture Box");
		beanDescriptor.setShortDescription("A component that displays an image in various layouts");
		return beanDescriptor;
	}
}