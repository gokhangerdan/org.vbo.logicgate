package org.vbo.logicgate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataTableSpecCreator;
import org.knime.core.data.DataType;
import org.knime.core.data.container.CloseableRowIterator;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.IntCell.IntCellFactory;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnName;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * <code>NodeModel</code> for the "LogicGate" node.
 *
 * @author Gökhan Gerdan
 */
public class LogicGateNodeModel extends NodeModel {
	
	private static final NodeLogger LOGGER = NodeLogger.getLogger(LogicGateNodeModel.class);
	
	private static final String KEY_GATE_TYPE = "gate_type";
	private static final String DEFAULT_GATE_TYPE = "AND";
	private final SettingsModelString m_logicGateSettings = createGateTypeSettingsModel();
	
	private static final String KEY_INPUT_A = "input_a";
	private static final String DEFAULT_INPUT_A = "";
	private final SettingsModelColumnName m_inputASettings = createInputASettingsModel();
	
	private static final String KEY_INPUT_B = "input_b";
	private static final String DEFAULT_INPUT_B = "";
	private final SettingsModelColumnName m_inputBSettings = createInputBSettingsModel();
    
    /**
     * Constructor for the node model.
     */
    protected LogicGateNodeModel() {
    
        // TODO: Specify the amount of input and output ports needed.
        super(1, 1);
    }
    
    static SettingsModelString createGateTypeSettingsModel() {
		return new SettingsModelString(KEY_GATE_TYPE, DEFAULT_GATE_TYPE);
	}
    
    static SettingsModelColumnName createInputASettingsModel() {
		return new SettingsModelColumnName(KEY_INPUT_A, DEFAULT_INPUT_A);
	}
    
    static SettingsModelColumnName createInputBSettingsModel() {
		return new SettingsModelColumnName(KEY_INPUT_B, DEFAULT_INPUT_B);
	}

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {
    	
    	LOGGER.info("This is an example info.");
    	
		BufferedDataTable inputTable = inData[0];
		
		DataTableSpec inputTableSpec = inputTable.getDataTableSpec();
		
		String logicGateString = m_logicGateSettings.getStringValue();
		String inputAString = m_inputASettings.getColumnName();
		String inputBString = m_inputBSettings.getColumnName();
		
		int inputAColumnIndex = inputTableSpec.findColumnIndex(inputAString);
		int inputBColumnIndex = inputTableSpec.findColumnIndex(inputBString);
		
		List<DataColumnSpec> newColumnSpecs = new ArrayList<>();
		
		for (int i = 0; i < inputTableSpec.getNumColumns(); i++) {
			DataColumnSpec columnSpec = inputTableSpec.getColumnSpec(i);
			String newName = columnSpec.getName();
			DataType newDataType = columnSpec.getType();
			DataColumnSpecCreator specCreator = new DataColumnSpecCreator(newName, newDataType);
			newColumnSpecs.add(specCreator.createSpec());
		}
		
		// Create output column
		String newName = inputAString + "_" + logicGateString + "_" + inputBString;
		DataType newDataType = IntCellFactory.TYPE;
		DataColumnSpecCreator specCreator = new DataColumnSpecCreator(newName, newDataType);
		newColumnSpecs.add(specCreator.createSpec());
		
		DataColumnSpec[] newColumnSpecsArray = newColumnSpecs.toArray(new DataColumnSpec[newColumnSpecs.size()]);
		DataTableSpec outputSpec = new DataTableSpec(newColumnSpecsArray);
		
		BufferedDataContainer container = exec.createDataContainer(outputSpec);
		
		CloseableRowIterator rowIterator = inputTable.iterator();
		
		int currentRowCounter = 0;
		while (rowIterator.hasNext()) {
			DataRow currentRow = rowIterator.next();
			int numberOfCells = currentRow.getNumCells();
			
			List<DataCell> cells = new ArrayList<>();
			for (int i = 0; i < numberOfCells; i++) {
				DataCell cell = currentRow.getCell(i);
				cells.add(cell);
			}
			
			int inputAValue = Integer.parseInt(currentRow.getCell(inputAColumnIndex).toString());
			int inputBValue = Integer.parseInt(currentRow.getCell(inputBColumnIndex).toString());
			int outputValue = 0;
			
			if (logicGateString == "AND") {
				if ((inputAValue == 1) && (inputBValue == 1)) {
					outputValue = 1;
				} else if ((inputAValue == 1) && (inputBValue == 0)) {
					outputValue = 0;
				} else if ((inputAValue == 0) && (inputBValue == 0)) {
					outputValue = 0;
				} else if ((inputAValue == 0) && (inputBValue == 1)) {
					outputValue = 0;
				}
			} else if (logicGateString == "OR") {
				if ((inputAValue == 1) && (inputBValue == 1)) {
					outputValue = 1;
				} else if ((inputAValue == 1) && (inputBValue == 0)) {
					outputValue = 1;
				} else if ((inputAValue == 0) && (inputBValue == 0)) {
					outputValue = 0;
				} else if ((inputAValue == 0) && (inputBValue == 1)) {
					outputValue = 1;
				}
			}
			
			DataCell outputCell = IntCellFactory.create(outputValue);
			cells.add(outputCell);
			
			DataRow row = new DefaultRow(currentRow.getKey(), cells);
			container.addRowToTable(row);
			
			currentRowCounter++;
			
			exec.checkCanceled();
			
			exec.setProgress(currentRowCounter / (double) inputTable.size(), "Formatting row " + currentRowCounter);
		}
		
		container.close();
		BufferedDataTable out = container.getTable();

        // TODO: Return a BufferedDataTable for each output port 
        return new BufferedDataTable[] { out };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        // TODO: generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
            throws InvalidSettingsException {

        // TODO: generated method stub
        return new DataTableSpec[]{null};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
         // TODO: generated method stub
    	m_logicGateSettings.saveSettingsTo(settings);
    	m_inputASettings.saveSettingsTo(settings);
    	m_inputBSettings.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        // TODO: generated method stub
    	m_logicGateSettings.loadSettingsFrom(settings);
    	m_inputASettings.loadSettingsFrom(settings);
    	m_inputBSettings.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        // TODO: generated method stub
    	m_logicGateSettings.validateSettings(settings);
    	m_inputASettings.validateSettings(settings);
    	m_inputBSettings.validateSettings(settings);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        // TODO: generated method stub
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        // TODO: generated method stub
    }

}

