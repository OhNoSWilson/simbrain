package org.simbrain.world.odorworld.entities;

import org.simbrain.util.propertyeditor.CopyableObject;
import org.simbrain.util.propertyeditor.EditableObject;
import org.simbrain.workspace.AttributeContainer;
import org.simbrain.world.odorworld.sensors.Sensor;

/**
 * Interface for effectors and sensors. "Peripheral" is supposed to suggest
 * the peripheral nervous system, which encompasses sensory and motor neurons.
 * It's the best I could come up with... :/
 *
 * @author Jeff Yoshimi
 */
public interface PeripheralAttribute extends AttributeContainer, EditableObject {


    public String getId();

    public String getLabel();

    public OdorWorldEntity getParent();

    public void setParent(OdorWorldEntity parent);

    public void setLabel(String label);

    /**
     * Called by reflection to return a custom description for the {@link
     * org.simbrain.workspace.gui.couplingmanager.AttributePanel.ProducerOrConsumer}
     * corresponding to object sensors and effectors.
     */
    default String getAttributeDescription() {
        return getParent().getName() + ":" + getId() + ":" + getLabel();
    }
}
