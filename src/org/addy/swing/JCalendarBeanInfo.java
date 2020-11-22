package org.addy.swing;

import java.beans.BeanDescriptor;

public class JCalendarBeanInfo extends BasicBeanInfo {
	public JCalendarBeanInfo() {
		super("JCalendar", "resources/");
	}

	public BeanDescriptor getBeanDescriptor() {
		BeanDescriptor beanDescriptor = new BeanDescriptor(JCalendar.class);
		beanDescriptor.setDisplayName("Calendar");
		beanDescriptor
				.setShortDescription("A component that displays a calendar for viewing and editing date values");
		return beanDescriptor;
	}
}