package com.fredtm.desktop;

import java.io.File;
import java.util.List;
import java.util.Optional;

import com.fredtm.core.model.Operation;
import com.fredtm.core.util.FredObjectMapper;
import com.fredtm.core.util.OperationJsonUtils;

public class FindDevice {

	private File selectedDirectory;
	private OperationJsonUtils jsonUtils = new OperationJsonUtils();
	private List<Operation> operations;

	public FindDevice(File selectedDirectory) {
		this.selectedDirectory = selectedDirectory;
		buscarEmDispositivo();
	}
	
	public Optional<List<Operation>> getOperacoes() {
		return operations.isEmpty() ? Optional.empty() : Optional.ofNullable(operations);
	}

	private void buscarEmDispositivo() {
		File file = new File(selectedDirectory.getPath()
				+ "/Documents/fred_tm/json-export/operations.json");
		operations = FredObjectMapper.mapResourcesToEntities(jsonUtils.jsonToJava(file));
	}
	

}
