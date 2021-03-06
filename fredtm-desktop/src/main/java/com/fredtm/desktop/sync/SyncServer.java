package com.fredtm.desktop.sync;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

import javafx.application.Platform;

public class SyncServer {

	private ClientConnection connected;
	private ServerSocket server;
	private ExecutorService service;

	public SyncServer(ClientConnection connected, ExecutorService service) {
		this.connected = connected;
		this.service = service;
	}

	public void start() {
		String port = new PropertiesConfig().getPort().getValue();
		if (connected != null) {
			startServer(Integer.valueOf(port));
		}
	}

	private void startServer(int port) {
		try {
			server = new ServerSocket(port);
			System.out.println(server.getInetAddress());
			System.out.println(port);
			System.out.println("INIT socket");
			Socket client = server.accept();

			System.out.println("socket acc");
			if (client != null) {
				System.out.println("C N N");
				onAcceptClient(client);
				service.shutdownNow();
			}
		} catch (IOException e) {
			try {
				if (server != null) {
					server.close();
				}
			} catch (IOException e2) {
				e.printStackTrace();
			}
		} finally {
			try {
				if (server != null) {
					server.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void onAcceptClient(Socket client) {
		OutputStream outputStream = null;
		PrintWriter pw = null;
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(
					new InputStreamReader(client.getInputStream(), Charset.forName("UTF-8")));
			String line = "";
			StringBuilder builder = new StringBuilder();
			if ((line = bufferedReader.readLine()) != null) {
				builder.append(line);
			}
			outputStream = client.getOutputStream();
			pw = new PrintWriter(new OutputStreamWriter(outputStream, Charset.forName("UTF-8")), true);
			pw.println("OK");

			// Run json processing
			Platform.runLater(() -> connected.onConnection(builder.toString()));

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bufferedReader.close();
				pw.close();
				outputStream.close();
				server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @return Thanks to "Ayush" on StackOverflow
	 */
	public static Optional<String> getIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();

				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address)) {
						String ipAddress = inetAddress.getHostAddress().toString();
						return Optional.of(ipAddress);
					}
				}

			}
		} catch (SocketException ex) {
			ex.printStackTrace();
		}
		return Optional.empty();
	}

	public void stop() {
		try {
			if (server != null) {
				server.close();
				service.shutdownNow();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
