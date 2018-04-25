/**
 * (C) 2016 HealthConnect NV. All rights reserved.
 */
package be.healthconnect.testeidutil;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.smartcardio.Card;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import be.healthconnect.testeidutil.view.Dialogs;
import be.healthconnect.testeidutil.view.javafx.FXDialogs;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * GUI utility for test EID cards.
 * 
 * @author <a href="mailto:dennis.wagelaar@healthconnect.be">Dennis Wagelaar</a>
 * @see https://securehomes.esat.kuleuven.be/~decockd/wiki/bin/view.cgi/EidForums/ForumEidCardProgrammingExamples0023
 */
@SuppressWarnings("restriction")
public class TestEIDUtil extends Application {

	private static final Logger LOG = Logger.getLogger(TestEIDUtil.class.getName());

	/**
	 * Main method.
	 * 
	 * @param args
	 */
	public static void main(final String[] args) {
		Application.launch(TestEIDUtil.class, args);
	}

	@Override
	public void start(final Stage primaryStage) throws Exception {
		primaryStage.setTitle(getClass().getSimpleName());
		final Dialogs dialogs = new FXDialogs(primaryStage);

		final TextField command = new TextField();
		command.setPromptText("command in hex");
		final Button sendBtn = new Button();
		sendBtn.getStyleClass().add("emr-button");
		sendBtn.setText("Send to eID");
		sendBtn.setDefaultButton(true);
		final Button clearBtn = new Button();
		clearBtn.getStyleClass().add("emr-button");
		clearBtn.setText("Clear");
		clearBtn.setCancelButton(true);
		final Button unblockBtn = new Button();
		unblockBtn.getStyleClass().add("emr-button");
		unblockBtn.setText("Unblock eID (1)");
		final Button unblockBtn2 = new Button();
		unblockBtn2.getStyleClass().add("emr-button");
		unblockBtn2.setText("Unblock eID (2)");
		final Button unblockBtn3 = new Button();
		unblockBtn3.getStyleClass().add("emr-button");
		unblockBtn3.setText("Unblock eID (3)");
		final HBox btnPane = new HBox();
		btnPane.setSpacing(10.0);
		btnPane.getChildren().addAll(sendBtn, clearBtn, unblockBtn, unblockBtn2, unblockBtn3);
		final TextField response = new TextField();
		response.setPromptText("response in hex");
		response.setEditable(false);

		final VBox pane = new VBox();
		pane.getStyleClass().add("application-bg");
		pane.setSpacing(10.0);
		pane.setPadding(new Insets(10.0));
		pane.setMinWidth(300.0);
		pane.getChildren().addAll(command, btnPane, response);

		sendBtn.setOnAction(actionEvent -> {
			try {
				response.setText(sendCommand(command.getText()));
			} catch (final Exception e) {
				LOG.log(Level.WARNING, "An error occurred", e);
				dialogs.error(e, null);
			}
		});

		clearBtn.setOnAction(actionEvent -> {
			command.clear();
			response.clear();
		});

		unblockBtn.setOnAction(actionEvent -> {
			try {
				response.setText(sendCommand("00200084082C222222111111FF"));
			} catch (final Exception e) {
				LOG.log(Level.WARNING, "An error occurred", e);
				dialogs.error(e, null);
			}
		});

		unblockBtn2.setOnAction(actionEvent -> {
			try {
				response.setText(sendCommand("00200084082C111111222222FF"));
			} catch (final Exception e) {
				LOG.log(Level.WARNING, "An error occurred", e);
				dialogs.error(e, null);
			}
		});

		unblockBtn3.setOnAction(actionEvent -> {
			try {
				response.setText(sendCommand("002C0001082C222222111111FF"));
			} catch (final Exception e) {
				LOG.log(Level.WARNING, "An error occurred", e);
				dialogs.error(e, null);
			}
		});

		final Scene scene = new Scene(pane);
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.toFront();
	}

	/**
	 * Sends the given command string to the eID.
	 * 
	 * @param command
	 *            the command string in hex
	 * @return the response in hex
	 * @throws CardException
	 * @throws DecoderException
	 */
	private String sendCommand(final String command) throws CardException, DecoderException, NoSuchAlgorithmException {
		final byte[] commandBytes = Hex.decodeHex(command.toCharArray());
		final CommandAPDU cAPDU = new CommandAPDU(commandBytes);
		final CardTerminals terminals = TerminalFactory.getInstance("PC/SC", null).terminals();
		final List<CardTerminal> terminalList = terminals.list();
		final Card card = terminalList.iterator().next().connect("*");
		card.beginExclusive();
		final ResponseAPDU rAPDU = card.getBasicChannel().transmit(cAPDU);
		card.endExclusive();
		card.disconnect(true);
		return new String(Hex.encodeHex(rAPDU.getBytes()));
	}

}
