package com.nushae.gravahal.view;

import java.util.Iterator;
import java.util.Map;

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
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class LobbyView extends VerticalLayout implements View, GameEventListener {
	public static final String NAME = "LOBBY";

	private long myID;
	private Navigator navigator;
	private LoginView loginView;

	private HorizontalLayout statusBar;
	private Button logoutButton;
	private Label usernameLabel;
	private Label userCountLabel;

	private Panel midSection;
	private VerticalLayout gameList;

	private HorizontalLayout makeNewGame;
	private TextField gameName = new TextField("Game Name:");
	private Button createGameButton;

	public LobbyView(Navigator navigator, long UID) {
		setSizeFull();
		setImmediate(true);
		myID = UID;
		this.navigator = navigator;

		// Initialize Status Bar 
		statusBar = new HorizontalLayout();
		statusBar.setDefaultComponentAlignment(Alignment.TOP_RIGHT);
		statusBar.setSizeFull();
		statusBar.setMargin(true);
		logoutButton = new Button("logout");
		logoutButton.addClickListener(new Button.ClickListener() {
    	@Override
    	public void buttonClick(Button.ClickEvent event) {
    		doLogout();
    	}
    });
		usernameLabel = new Label(Server.getUserName(myID));
		userCountLabel = new Label();
		statusBar.addComponent(usernameLabel);
		statusBar.addComponent(userCountLabel);
		statusBar.addComponent(logoutButton);
		addComponent(statusBar);

		// Initialize the gamelist
		midSection = new Panel();
		midSection.setSizeFull();
		gameList = new VerticalLayout();
		gameList.setSizeFull();
		initGameList();
		midSection.setContent(gameList);
		addComponent(midSection);

		// Initialize "create new game" Bar
		makeNewGame = new HorizontalLayout();
		makeNewGame.setSizeFull();
		makeNewGame.setMargin(true);
		createGameButton = new Button("New Game");
		createGameButton.addClickListener(new Button.ClickListener() {
    	@Override
    	public void buttonClick(Button.ClickEvent event) {
    		doGameAdd();
    	}
    });
		makeNewGame.addComponent(gameName);
		makeNewGame.addComponent(createGameButton);
		addComponent(makeNewGame);
		Server.register(this);
	}

	private void doLogout() {
		Server.logout(myID);
		Server.unregister(this);
		loginView = new LoginView(navigator);
		navigator.addView(LoginView.NAME, loginView);
		navigator.removeView(LobbyView.NAME);
		navigator.navigateTo(LoginView.NAME);
	}

	private void doGameAdd() {
		// A user can start exactly one game at a time; the button is therefore disabled as soon as (s)he opens a game.
		createGameButton.setEnabled(false);
		String name = gameName.getValue();
		if ("".equals(name)) {
			name = Server.getUserName(myID)+"'s Game";
		}
		Server.openGame(myID,  name);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		updateView();
	}

	@Override
	public void receiveMessage(ServerMessage message) {
		switch (message.getType()) {
		case ServerMessage.UPDATE:
			updateView();
			break;
		case ServerMessage.LOGIN:
		case ServerMessage.LOGOUT:
			userCountLabel.setCaption("# online: " + Server.getUserCount());
			break;			
		case ServerMessage.ADDGAME:
			gameList.addComponent(new GameListEntry(navigator, myID, Server.getGame(message.getID())));
			break;
		case ServerMessage.REMOVEGAME:
			Iterator<Component> it = gameList.iterator();
			while (it.hasNext()) {
        Component c = it.next();
        if (c instanceof GameListEntry) {
        	Game g = ((GameListEntry) c).getGame(); 
        	if (g.getID() == message.getID()) {
        		if (g.getStarterID() == myID) {
        			createGameButton.setEnabled(true);
        		}
        		it.remove();
        		gameList.markAsDirty();
        	}
        }
			}
			break;
		case ServerMessage.STARTGAME:
			updateGameList();
			break;
		case ServerMessage.GAMEOVER:
			Game g = Server.getGame(message.getID());
			if (g.getStarterID() == myID) {
				createGameButton.setEnabled(true);
			}
			updateGameList();
			break;
		}
	}

	private void initGameList() {
		Iterator<Map.Entry<Long, Game>> it = Server.getGameList().entrySet().iterator();
    while (it.hasNext()) {
        Map.Entry<Long, Game> pair = it.next();
        gameList.addComponent(new GameListEntry(navigator, pair.getKey(), pair.getValue()));
    }
	}

	private void updateGameList() {
		Iterator<Component> it = gameList.iterator();
		while (it.hasNext()) {
      Component c = it.next();
      if (c instanceof GameListEntry) {
      	((GameListEntry) c).updateViewState();
      }
		}
	}

	private void updateView() {
		userCountLabel.setCaption("# online: " + Server.getUserCount());
		updateGameList();
	}
}
