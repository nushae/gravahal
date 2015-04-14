package com.nushae.gravahal.view;

import com.nushae.gravahal.model.Game;
import com.nushae.gravahal.server.Server;
import com.nushae.gravahal.server.Server.GameEventListener;
import com.nushae.gravahal.server.ServerMessage;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

public class GameView extends VerticalLayout implements View, GameEventListener {
	public static final String NAMEPREFIX = "GAME";

	private long myID;
	private int myIndex;
	private Game myGame;
	private Navigator navigator;

	HorizontalLayout statusBar;
	HorizontalLayout gamePanel;
	HorizontalLayout controlPanel;


	private Label userNameLabel;
	private Label gameNameLabel;
	private Button concedeButton = new Button("Concede");
	private Button lobbyButton = new Button("Lobby");

	Button[] botButtons;
	Button[] topButtons;

	VerticalLayout middle;
	HorizontalLayout top;
	HorizontalLayout bottom;
	Button topGH;
	Button botGH;

	VerticalLayout infoPanel;
	Label gameStateLabel;
	Label turnIndicatorLabel; // ended; awaiting opp move; your move

	int pitNum;

	Button.ClickListener bl = new Button.ClickListener() {
		@Override
		public void buttonClick(ClickEvent event) {
			Component butClicked = event.getComponent();
			String idStr = butClicked.getId();

			if (idStr.length()==1) {
				int id = idStr.charAt(0)-49;
				if (id>=0 && id <6) {
					myGame.performMove(myIndex, id); // do move for that box
				}
			}
		}
	};

	public GameView(Navigator navigator, Game game, long uid) {
		myID = uid;
		myGame = game;
		pitNum = game.getNumberOfPits();
		if (myGame.getStarterID() == myID) {
			myIndex = 0;
		} else {
			myIndex = 1;
		}

		this.navigator = navigator;

		statusBar = new HorizontalLayout();
		statusBar.setDefaultComponentAlignment(Alignment.TOP_RIGHT);
		statusBar.setSizeFull();
		statusBar.setMargin(true);
		userNameLabel = new Label(Server.getUserName(myID));
		gameNameLabel = new Label();
		statusBar.addComponent(userNameLabel);
		statusBar.addComponent(gameNameLabel);
		addComponent(statusBar);

		gamePanel = new HorizontalLayout();
		botButtons = new Button[pitNum];
		topButtons = new Button[pitNum];
		middle = new VerticalLayout();
		top = new HorizontalLayout();
		bottom = new HorizontalLayout();
		middle.addComponent(top);
		middle.addComponent(bottom);
		for (int i=0; i<pitNum; i++) {
			topButtons[i] = new Button("("+(pitNum-i)+")");
			topButtons[i].setEnabled(false);
			topButtons[i].setWidth("5em");
			topButtons[i].setHeight("5em");
			topButtons[i].setStyleName("gravahal opponentwidget");
			top.addComponent(topButtons[i]);
			botButtons[i] = new Button("("+(i+1)+")");
			botButtons[i].setId(""+(i+1));
			botButtons[i].setStyleName("gravahal playerwidget");
			botButtons[i].setWidth("5em");
			botButtons[i].setHeight("5em");
		  botButtons[i].addClickListener(bl);
		  bottom.addComponent(botButtons[i]);
		}
		topGH = new Button("(GH)");
		botGH = new Button("(GH)");
		topGH.setWidth("5em");
		topGH.setHeight("10em");
		botGH.setWidth("5em");
		botGH.setHeight("10em");
		topGH.setEnabled(false);
		topGH.setStyleName("gravahal opponentwidget");
		botGH.setEnabled(false);
		botGH.setStyleName("gravahal playerwidget");
		infoPanel = new VerticalLayout();
		gameStateLabel = new Label();
		turnIndicatorLabel = new Label();
		infoPanel.addComponent(gameStateLabel);
		infoPanel.addComponent(turnIndicatorLabel);
		gamePanel.addComponent(topGH);
		gamePanel.addComponent(middle);
		gamePanel.addComponent(botGH);
		gamePanel.addComponent(infoPanel);
		addComponent(gamePanel);
		
		controlPanel = new HorizontalLayout();
		controlPanel.addComponent(concedeButton);
		concedeButton.addClickListener(new Button.ClickListener() {
    	@Override
    	public void buttonClick(Button.ClickEvent event) {
    		doConcede();
    	}
    });
		controlPanel.addComponent(lobbyButton);
		lobbyButton.addClickListener(new Button.ClickListener() {
    	@Override
    	public void buttonClick(Button.ClickEvent event) {
    		goToLobby();
    	}
    });
		addComponent(controlPanel);
		Server.register(this);
		updateView();
	}

	private void doConcede() {
		concedeButton.setEnabled(false);
		myGame.finish(myGame.getStarterID() == myID ? 1 : 0);
	}

	private void goToLobby() {
 		navigator.navigateTo(LobbyView.NAME);
	}

	public void updateView() {
		gameNameLabel.setCaption(myGame.getName()+": " + Server.getUserName(myGame.getPlayer(0).getID())+" - " + Server.getUserName(myGame.getPlayer(1).getID()));
		int currentPlayer = myGame.getActivePlayer();

		if (myGame.getState() == Game.ENDED) {
			turnIndicatorLabel.setCaption(myGame.getWinner()==-1?"[draw]":(myGame.getWinner()==myIndex?"[you won]":"[they won]"));
			concedeButton.setEnabled(false);
		} else {
			concedeButton.setEnabled(true);
			if (myIndex == currentPlayer) {
				turnIndicatorLabel.setCaption("[your move]");
			} else {
				turnIndicatorLabel.setCaption("[their move]");
			}
		}

		for (int i=0; i< pitNum; i++) {
			topButtons[pitNum-i-1].setCaption(""+myGame.getPlayer(1-myIndex).getContentsAtPosition(i));
			int botStones = myGame.getPlayer(myIndex).getContentsAtPosition(i);
			botButtons[i].setCaption(""+botStones);
			if (botStones == 0 || myIndex != currentPlayer) {
				botButtons[i].setEnabled(false);
			} else {
				botButtons[i].setEnabled(true);
			}
		}
		topGH.setCaption(""+myGame.getPlayer(1-myIndex).getGravaHal());
		botGH.setCaption(""+myGame.getPlayer(myIndex).getGravaHal());
	}
	
	@Override
	public void receiveMessage(ServerMessage message) {
		if (message.getType() == ServerMessage.UPDATE
				|| message.getType() == ServerMessage.GAMEOVER && myGame.getID() == message.getID()
				|| message.getType() == ServerMessage.MOVE && myGame.getID() == message.getID()
				) {
			updateView();
		}
	}

	@Override
	public void enter(ViewChangeEvent event) {
		updateView();
	}
}
