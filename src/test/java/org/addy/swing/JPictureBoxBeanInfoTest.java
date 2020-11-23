package org.addy.swing;

import java.awt.Image;
import java.beans.BeanInfo;

import javax.swing.ImageIcon;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class JPictureBoxBeanInfoTest {

	@Test
	void iconIsFound() {
		JPictureBoxBeanInfo beanInfo = new JPictureBoxBeanInfo();
		Image image = beanInfo.getIcon(BeanInfo.ICON_COLOR_16x16);
		Assertions.assertNotNull(image);
		
		ImageIcon icon = new ImageIcon(image);
		Assertions.assertEquals(16, icon.getIconWidth());
		Assertions.assertEquals(16, icon.getIconHeight());
	}

	@Test
	void beanDescriptorIsCorrect() {
		JPictureBoxBeanInfo beanInfo = new JPictureBoxBeanInfo();
		Assertions.assertEquals("Picture Box", beanInfo.getBeanDescriptor().getDisplayName());
		Assertions.assertEquals("A component that displays an image in various layouts", beanInfo.getBeanDescriptor().getShortDescription());
	}
}
