package com.fredtm.desktop.controller.chart;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;

import com.fredtm.core.decorator.TimeSystem;
import com.fredtm.core.decorator.TimeSystems;
import com.fredtm.core.model.Collect;
import com.fredtm.core.model.TimeActivity;
import com.fredtm.desktop.controller.BaseController;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

public class TimesChartController extends BaseController implements Initializable {

	@FXML
	private VBox rootPane;

	@FXML
	private ChoiceBox<TimeSystems> chcBox;

	private ScatterChart<String, Number> pointsChart;

	private BarChart<Number, String> horizontalChart;

	@FXML
	private Button btnPoints;

	@FXML
	private Button btnArea;

	@FXML
	private Button btnHorizontal;

	private Charts selected;

	private TimeSystem collectSystem;

	@FXML
	void onHorizontalClicked(ActionEvent event) {

		this.selected = Charts.H_BARS;
		this.horizontalChart = new BarChart<>(getNumberAxis(), getCategoryAxis());

		LinkedHashMap<TimeActivity, Double> timeByActivities = collectSystem.getValueMap();

		XYChart.Series<Number, String> series = new Series<>();
		timeByActivities.forEach((k, v) -> {
			XYChart.Data<Number, String> data = new XYChart.Data<>(v, k.toString());
			series.getData().add(data);
		});

		this.horizontalChart.getData().add(series);
		this.horizontalChart.setLegendVisible(false);
		addChart(this.horizontalChart);

	}

	private void addChart(Node chartNode) {
		int size = rootPane.getChildren().size();
		if (size > 2) {
			rootPane.getChildren().remove(2, size);
		}
		VBox.setVgrow(chartNode, Priority.ALWAYS);
		rootPane.getChildren().add(chartNode);
	}

	NumberAxis getNumberAxis() {
		NumberAxis numberAxis = new NumberAxis();
		TimeSystems timeSystems = chcBox.getSelectionModel().getSelectedItem();
		if (timeSystems == TimeSystems.PCT) {
			numberAxis.setAutoRanging(false);
		}
		numberAxis.setLabel("Tempo parcial (" + timeSystems.getValue() + ")");
		return numberAxis;
	}

	CategoryAxis getCategoryAxis() {
		CategoryAxis categoryAxis = new CategoryAxis();
		categoryAxis.setLabel("Tempos coletados");
		return categoryAxis;
	}

	@FXML
	void onPointsClicked(ActionEvent event) {
		this.selected = Charts.POINTS;
		this.pointsChart = new ScatterChart<>(getCategoryAxis(), getNumberAxis());

		Series<String, Number> series = new Series<>();

		LinkedHashMap<TimeActivity, Double> times = collectSystem.getValueMap();
		AtomicInteger ai = new AtomicInteger(0);
		times.forEach((t, v) -> {
			Integer index = ai.incrementAndGet();
			String category = index.toString() + " - " + t.getActivity().getTitle();
			Data<String, Number> data = new XYChart.Data<>(category, v);
			series.getData().add(data);
		});
		this.pointsChart.setLegendVisible(false);
		this.pointsChart.getData().add(series);
		addChart(this.pointsChart);
	}

	public void setCollect(Collect collect) {
		selected = Charts.H_BARS;
		TimeSystems ts = chcBox.getSelectionModel().getSelectedItem();
		this.collectSystem = ts.build(collect);
		onHorizontalClicked(null);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		chcBox.setItems(FXCollections.observableArrayList(TimeSystems.values()));
		chcBox.getItems().remove(3);
		chcBox.getSelectionModel().select(2);
		chcBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TimeSystems>() {

			@Override
			public void changed(ObservableValue<? extends TimeSystems> observable, TimeSystems oldValue,
					TimeSystems newValue) {
				if (collectSystem != null) {
					collectSystem = newValue.build(collectSystem.getCollect());
				}

				switch (selected) {
				case H_BARS:
					onHorizontalClicked(null);
					break;
				case POINTS:
					onPointsClicked(null);
					break;
				default:
					break;
				}
			}
		});
	}

	@FXML
	void saveAsPng(ActionEvent event) {

		switch (selected) {
		case H_BARS:
			saveImage(horizontalChart);
			break;
		case POINTS:
			saveImage(pointsChart);
			break;
		// case AREA:
		// saveImage();
		// break;
		default:
			return;
		}
	}

	public void saveImage(Node node) {
		WritableImage image = node.snapshot(new SnapshotParameters(), null);
		DirectoryChooser dc = new DirectoryChooser();
		dc.setTitle("Escolha o local para salvar sua exportação");
		File directory = dc.showDialog(getWindow());
		if (directory != null && directory.isDirectory() && directory.canWrite()) {
			File f = new File(directory.getAbsolutePath() + "/tempo_atividade_"
					+ collectSystem.getCollect().getOperation().toString() + "_" + System.currentTimeMillis() + ".png");

			try {
				f.createNewFile();
				ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
