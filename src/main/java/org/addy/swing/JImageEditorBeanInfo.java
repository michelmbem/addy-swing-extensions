package org.addy.swing;

import java.beans.BeanDescriptor;

public class JImageEditorBeanInfo extends BasicBeanInfo<JImageEditor> {
    @Override
    public BeanDescriptor getBeanDescriptor() {
        BeanDescriptor beanDescriptor = super.getBeanDescriptor();
        beanDescriptor.setDisplayName("Image Editor");
        beanDescriptor.setShortDescription("A component for basic image editing");
        return beanDescriptor;
    }
}
