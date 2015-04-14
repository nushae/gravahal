package com.nushae.gravahal.view;

import com.nushae.gravahal.model.Game;
import com.nushae.gravahal.server.Server;
import com.vaadin.navigator.Navigator;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class GameListEntry extends HorizontalLayout{

	private long uid;
	private Game game;
	private Navigator navigator;
	
	private Label gameName = new Label();
	private Label playerOne = new Label();
	private Label playerTwo = new Label();
	private Button gameActionButton;

	public GameListEntry(Navigator navigator, long UID, Game game) {
		setSpacing(true);
		this.navigator = navigator;
		this.game = game;
		uid = UID;
		gameActionButton = new Button();
		gameActionButton.addClickListener(new Button.ClickListener() {
    	@Override
    	public void buttonClick(Button.ClickEvent event) {
    		doGameAction();
    	}
		});
		addComponent(gameActionButton);
		addComponent(gameName);
		addComponent(playerOne);
		addComponent(playerTwo);
		updateViewState();
	}

	public Game getGame() {
		return game;
	}

	public void updateViewState() {
		gameName.setCaption(game.getName() + ": ");
		playerOne.setCaption(Server.getUserName(game.getStarterID()));
		switch (game.getState()) {
		case Game.OPEN:
			if (uid == game.getStarterID()) {
				gameActionButton.setCaption("CANCEL");
			} else {
				gameActionButton.setCaption("JOIN");
			}					
			gameActionButton.setEnabled(true);
			break;
		case Game.PLAYING:
			playerTwo.setCaption(" - " + Server.getUserName(game.getSecondPlayerID()));
			if (uid == game.getStarterID() || uid == game.getSecondPlayerID()) {
				gameActionButton.setCaption("GO TO");
				gameActionButton.setEnabled(true);
			} else {
				gameActionButton.setCaption("PLAYING");
				gameActionButton.setEnabled(false);
			}
			break;
		case Game.ENDED:
			playerOne.setCaption(Server.getUserName(game.getStarterID()) + (game.getWinner() == -1 ? " (d)": (game.getWinner() == 0?" (w)":" (l)")));
			playerTwo.setCaption(" - " + Server.getUserName(game.getSecondPlayerID()) + (game.getWinner() == -1 ? " (d)": (game.getWinner() == 1?" (w)":" (l)")));
			gameActionButton.setCaption("ENDED");
			gameActionButton.setEnabled(false);
		}
	}

	private void doGameAction() {
		if ("CANCEL".equals(gameActionButton.getCaption())) {
			Server.cancelGame(uid, game.getID());
		} else if ("JOIN".equals(gameActionButton.getCaption())) {
			Server.joinGame(uid, game.getID());
			navigator.addView(GameView.NAMEPREFIX+game.getID(), new GameView(navigator, game, uid));
			updateViewState();
			navigator.navigateTo(GameView.NAMEPREFIX+game.getID());
		} else if ("GO TO".equals(gameActionButton.getCaption())) {
			// The following bit looks rather hacky, doesn't it? Well, I guess it is...
			// It's a form of lazy evaluation - the view is created the first time the user tries to navigate to it.
			// We can't really create it proactively, since vaadin offers no way to trigger this kind of update proactively
			try {
				navigator.navigateTo(GameView.NAMEPREFIX+game.getID());
			} catch (IllegalArgumentException ie) {
				navigator.addView(GameView.NAMEPREFIX+game.getID(), new GameView(navigator, game, uid));
				navigator.navigateTo(GameView.NAMEPREFIX+game.getID());
			}
		}
	}
}
