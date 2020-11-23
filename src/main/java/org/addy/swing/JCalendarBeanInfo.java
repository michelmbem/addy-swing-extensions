package org.addy.swing;

import java.beans.BeanDescriptor;

public class JCalendarBeanInfo extends BasicBeanInfo<JCalendar> {
	
	@Override
	public BeanDescriptor getBeanDescriptor() {
		BeanDescriptor beanDescriptor = super.getBeanDescriptor();
		beanDescriptor.setDisplayName("Calendar");
		beanDescriptor.setShortDescription("A component that displays a calendar for viewing and editing date values");
		return beanDescriptor;
	}
}