package de.forsthaus.zksample.webui.util;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Constraint;
import org.zkoss.zul.Textbox;

/**
 * Constraint for comparing the value-strings from a textbox with a string.<br>
 * Throws an error message if not equals. Used in the userDialog for checking
 * that the reTyped password<br>
 * is same as first written password.<br>
 * 
 * @author sge(at)forsthaus(dot)de
 * @changes 07/24/2009: sge changes for clustering.<br>
 * 
 * 
 */
public class NoEmptyAndEqualStringsConstraint implements Constraint, java.io.Serializable {

	private static final long serialVersionUID = 4052163775381888061L;

	private transient Component compareComponent;

	public NoEmptyAndEqualStringsConstraint(Component compareComponent) {
		super();
		this.compareComponent = compareComponent;
	}

	@Override
	public void validate(Component comp, Object value) throws WrongValueException {

		if (comp instanceof Textbox) {

			String enteredValue = (String) value;
			String comparedValue = null;

			if (compareComponent instanceof Textbox) {
				comparedValue = ((Textbox) compareComponent).getValue();
			}

			if (enteredValue.isEmpty()) {
				throw new WrongValueException(comp, "Cannot be empty");
			} else if (!enteredValue.equals(comparedValue)) {
				throw new WrongValueException(comp, "Must be the same like " + compareComponent.getId());
			}
		}
	}

}
