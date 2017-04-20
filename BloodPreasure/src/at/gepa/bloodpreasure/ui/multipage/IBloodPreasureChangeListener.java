package at.gepa.bloodpreasure.ui.multipage;

import java.io.Serializable;

import at.gepa.net.IElement;

public interface IBloodPreasureChangeListener
extends Serializable
{

	void dataChanged(IElement element, int page, Object newValue);

}
