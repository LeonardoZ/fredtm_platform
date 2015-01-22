package com.fredtm.desktop.controller;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.stage.DirectoryChooser;

import com.fredtm.core.model.Operacao;
import com.fredtm.desktop.eventbus.MainEventBus;
import com.fredtm.exportar.OperacoesToJson;

public class ProjetosController extends BaseController implements Initializable {

	@FXML
	private ListView<Operacao> listViewProjetos;

	private Operacao operacao;

	private List<Operacao> operacoes;

	public void setOperacoes(List<Operacao> operacoes) {
		this.operacoes = operacoes;
		listViewProjetos.getItems().addAll(operacoes);
	}

	@Override
	public void initialize(URL url, ResourceBundle bundle) {
		MenuItem menuColeta = new MenuItem("Ver coletas");
		MenuItem menuAtividade = new MenuItem("Ver atividades");
		MenuItem menuExportar = new MenuItem("Exportar todas operações");
		menuAtividade.setOnAction(ev -> MainEventBus.INSTANCE
				.eventoAbrirAtividades(operacao));
		menuColeta.setOnAction(ev -> MainEventBus.INSTANCE
				.eventoAbrirColetas(operacao));
		menuExportar.setOnAction(ev -> exportarOperacoes());
		ContextMenu contextMenu = new ContextMenu(menuColeta, menuAtividade,menuExportar);
		contextMenu.setStyle("-fx-background-color: #fff");
		contextMenu.setAutoFix(true);
		listViewProjetos.setContextMenu(contextMenu);

		listViewProjetos.setOnMouseClicked(event -> {
			operacao = this.listViewProjetos.getSelectionModel()
					.getSelectedItem();
		});
	}

	private void exportarOperacoes() {
		DirectoryChooser dc = new DirectoryChooser();
		dc.setTitle("Escolha o diretório destino de exportação");
		File selectedDirectory = dc.showDialog(getWindow());
		if (selectedDirectory.isDirectory() && selectedDirectory.canWrite()) {
			OperacoesToJson op = new OperacoesToJson();
			op.exportar(operacoes, selectedDirectory.getAbsolutePath());
			JOptionPane.showMessageDialog(null, "Exportação completa!");
		} else {
			JOptionPane
					.showMessageDialog(null,
							"Erro ao exportar. Verifique se é o destino é um diretório válido.");
		}
	}

}
