package org.vbo.logicgate;

import org.knime.core.data.IntValue;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * <code>NodeDialog</code> for the "LogicGate" node.
 * 
 * @author Gökhan Gerdan
 */
public class LogicGateNodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring the LogicGate node.
     */
    @SuppressWarnings("unchecked")
	protected LogicGateNodeDialog() {
    	super();
    	
    	createNewGroup("Logic Gate");
    	
    	SettingsModelString gateTypeSettings = LogicGateNodeModel.createGateTypeSettingsModel();
    	addDialogComponent(new DialogComponentStringSelection(gateTypeSettings, "Gate Type", "AND", "OR"));
    	
    	createNewGroup("Input Columns");
    	
    	SettingsModelString inputASettings = LogicGateNodeModel.createInputASettingsModel();
    	addDialogComponent(new DialogComponentColumnNameSelection(inputASettings, "Input A", 0, true, IntValue.class));
    	
    	SettingsModelString inputBSettings = LogicGateNodeModel.createInputBSettingsModel();
    	addDialogComponent(new DialogComponentColumnNameSelection(inputBSettings, "Input B", 0, true, IntValue.class));
    }
}

