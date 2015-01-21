package com.fredtm.desktop.controller.grafico;

import java.util.HashMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;

import com.fredtm.core.model.Coleta;
import com.fredtm.desktop.controller.BaseController;

public class DistribuicaoTempoAtividadeController extends BaseController {

	@FXML
	private PieChart graficoPizzaTempos;
	private Coleta coleta;

	public void setColeta(Coleta coleta) {
		this.coleta = coleta;
		configuraGraficoPizza();
	}

	private void configuraGraficoPizza() {
		ObservableList<PieChart.Data> list = FXCollections
				.observableArrayList();
		HashMap<String, Long> somaDosTempos = coleta.getSomaDosTempos();
		somaDosTempos.forEach(
				(k, v) -> list.add(new PieChart.Data(k, Double.valueOf(v)))
		);
		graficoPizzaTempos.setData(list);
	}

}