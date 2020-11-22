package org.addy.swing;

import java.beans.BeanDescriptor;

public class JCalendarComboBeanInfo extends BasicBeanInfo {
	public JCalendarComboBeanInfo() {
		super("JCalendarCombo", "resources/");
	}

	@Override
	public BeanDescriptor getBeanDescriptor() {
		BeanDescriptor beanDescriptor = new BeanDescriptor(JCalendar.class);
		beanDescriptor.setDisplayName("Calendar Combo");
		beanDescriptor.setShortDescription("A component that allows the user to edit a date value either by typing it in a spinner box or by selecting it in a drop down calendar");

		return beanDescriptor;
	}
}