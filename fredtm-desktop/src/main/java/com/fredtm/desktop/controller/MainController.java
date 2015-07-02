package com.fredtm.desktop.controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;

import com.fredtm.core.model.Collect;
import com.fredtm.core.model.Operation;
import com.fredtm.core.util.OperacoesJsonUtils;
import com.fredtm.desktop.controller.grafico.DistribuicaoTempoAtividadeController;
import com.fredtm.desktop.controller.grafico.TempoObtidoPorClassificacaoController;
import com.fredtm.desktop.controller.utils.MainControllerTabCreator;
import com.fredtm.desktop.controller.utils.TiposGrafico;
import com.fredtm.desktop.sync.ClientConnection;
import com.fredtm.desktop.sync.SwingQRCodeGenerator;
import com.fredtm.desktop.sync.SyncServer;

public class MainController extends BaseController implements Initializable,
		ClientConnection {

	@FXML
	private ToggleButton btnSincronizar;

	@FXML
	private Button btnInstrucoes;

	@FXML
	private Button btnSair;

	@FXML
	private TabPane tabPane;

	@FXML
	private VBox boxNenhumSync;

	private MainControllerTabCreator tabCreator;

	private JDialog jDialog;

	private WindowAdapter adapter;

	private ExecutorService service;

	private SyncServer syncServer;

	@Override
	public void initialize(URL url, ResourceBundle bundle) {
		tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.SELECTED_TAB);
		tabCreator = new MainControllerTabCreator(tabPane);
		tabPane.getTabs().clear();
	}

	@FXML
	private void onSincronizarClicked(ActionEvent event) {
		if (!btnSincronizar.isSelected()) {
			tabPane.getTabs().clear();
		} else {
			acaoSincronizarAtivo();
		}
	}

	private void acaoSincronizarAtivo() {
		configurarAdapter();
		criarServidorDeTransferencia();
		Optional<BufferedImage> gerarQRCode = new SwingQRCodeGenerator()
				.gerarQRCode();
		criarJDialog(gerarQRCode.orElseThrow(IllegalStateException::new));
	}

	private void configurarAdapter() {
		adapter = new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				syncServer.stop();
				System.out.println("Fechando");
				btnSincronizar.setSelected(false);
				jDialog = null;
			}
		};

	}

	private void criarServidorDeTransferencia() {
		service = Executors.newSingleThreadExecutor();
		syncServer = new SyncServer(MainController.this, service);
		service.execute(() -> syncServer.start());
	}

	private void criarJDialog(BufferedImage image) {
		JLabel canvasLabel = new JLabel(new ImageIcon(image));
		JLabel textTopLabel = new JLabel(
				"Leia esse QRCode com a op��o \"Sincronizar com PC\" "
						+ "no aplicativo Fred TM para sincronizar.");
		textTopLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 3, 10));
		if (jDialog == null) {
			jDialog = new JDialog();
			jDialog.setTitle("Sincronizar com aplicativo Fred TM");
			jDialog.setSize(670, 200);
			jDialog.setLayout(new BorderLayout(10, 10));
			jDialog.getContentPane().setBackground(new Color(226, 237, 222));
			jDialog.getContentPane().setForeground(new Color(226, 237, 222));
			jDialog.setLocationRelativeTo(null);
			jDialog.add(canvasLabel, BorderLayout.CENTER);
			jDialog.add(textTopLabel, BorderLayout.NORTH);
			jDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			jDialog.addWindowListener(adapter);
			jDialog.setVisible(true);
			jDialog.setAlwaysOnTop(true);
		} else {
			jDialog.toFront();
		}
	}

	@Override
	public void onConnection(String jsonContent) {
		OperacoesJsonUtils utils = new OperacoesJsonUtils();
		Optional<List<Operation>> operations = utils
				.converterJsonParaJava(jsonContent);
		criarJanelaProjetos(operations.orElseGet(ArrayList::new));
		jDialog.setVisible(false);
		jDialog = null;
	}

	private void criarJanelaProjetos(List<Operation> list) {
		Consumer<ProjectsController> consumer = c -> c.setOperacoes(list);
		criarView("/fxml/projects.fxml", "Projetos", consumer);
	}

	@FXML
	void onConfigurarClicked(ActionEvent event) {
		criarView("/fxml/configure.fxml", "Configurar");
	}

	@FXML
	void onImportarJsonClicked(ActionEvent event) {
		FileChooser fc = new FileChooser();
		fc.setTitle("Escolha o arquivo \"operations.json\" gerado por seu aplicativo ou exportado por voc�.");
		fc.setSelectedExtensionFilter(new ExtensionFilter(
				"Arquivos .json gerados por esse software ou pelo Fred TM (mobile)",
				"*.<json>"));
		File selectedItem = fc.showOpenDialog(getWindow());

		if (selectedItem != null) {
			OperacoesJsonUtils oju = new OperacoesJsonUtils();
			Optional<List<Operation>> operations = oju
					.converterJsonParaJava(selectedItem);
			criarJanelaProjetos(operations.get());
		}

	}

	@FXML
	void onSairClicked(ActionEvent event) {
		System.exit(0);
	}

	public void abrirAtividades(Operation operation) {
		Consumer<AtividadesController> controllerAction = c -> c
				.setOperacao(operation);
		criarView("/fxml/activities.fxml",
				"Atividades - " + operation.getName(), controllerAction);
	}

	public void abrircollects(Operation operation) {
		Consumer<CollectsController> consumidor = c -> c.setOperacao(operation);
		criarView("/fxml/collects.fxml", "Coletas - " + operation.getName(),
				consumidor);
	}

	public void habilitarExportarAtividades(Operation operation) {
		Consumer<AtividadesController> consumidor = c -> c
				.setOperacao(operation);
		criarView("/fxml/export_activities.fxml", "Exportar atividades - "
				+ operation.getName(), consumidor);
	}

	public void abrirTemposcollectdos(Collect collect) {
		Consumer<CollectedTimesController> consumidor = c -> c
				.setTempos(collect.getTimeInChronologicalOrder());
		criarView("/fxml/collected_times.fxml",
				"Tempos coletados - " + collect.toString(), consumidor);
	}

	public void exportarcollects(List<Collect> collects) {
		Consumer<ExportCollectsController> consumidor = c -> c
				.setcollects(collects);
		criarView("/fxml/export_collect.fxml", "Exportar coletas", consumidor);
	}

	public void abrirTiposDeGraficos(Collect collect, List<Collect> collects) {
		Consumer<TiposGraficosController> consumidor = c -> {
			c.setcollect(collect);
			c.setcollects(collects);
		};
		criarView("/fxml/types_chart.fxml",
				"An�lises da coleta " + collect.toString(), consumidor);
	}

	public void abrirAnaliseGrafica(TiposGrafico tipo, Collect collect,
			List<Collect> collects) {
		switch (tipo) {
		case DISTRIBUICAO_TEMPO_ATIVIDADE_PIZZA:
			Consumer<DistribuicaoTempoAtividadeController> pizzaConsumer = c -> c
					.setcollect(collect);
			criarView("/fxml/pizza_chart.fxml",
					"Distribui��o tempo/atividade: " + collect.toString(),
					pizzaConsumer);
			break;

		case CLASSIFICACAO_POR_BARRAS:
			Consumer<TempoObtidoPorClassificacaoController> barConsumer = c -> c
					.setcollect(collect);
			criarView("/fxml/chart_line_classification.fxml",
					"Tempo por classifica��o: " + collect.toString(),
					barConsumer);
			break;

		case CLASSIFICACAO_CICLOS_POR_BARRAS:
			Consumer<TempoObtidoPorClassificacaoController> barCicloConsumer = c -> c
					.setcollects(collects);
			criarView("/fxml/chart_line_classification.fxml",
					"Tempo por classifica��o/ciclo: " + collect.toString(),
					barCicloConsumer);
			break;

		default:
			break;
		}
	}

	private <T extends BaseController> void criarView(String fxml, String titulo) {
		tabCreator.criaViewMecanismo(fxml, titulo, Optional.empty());
	}

	private <T extends BaseController> void criarView(String fxml,
			String titulo, Consumer<T> consumidor) {
		tabCreator.criaViewMecanismo(fxml, titulo, Optional.of(consumidor));
	}

}
