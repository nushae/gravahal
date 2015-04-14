package com.nushae.gravahal.view;

import com.nushae.gravahal.server.Server;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class LoginView extends VerticalLayout implements View {
	public static final String NAME = ""; // empty string: default view

  private Button btnLogin = new Button("Login");
  private TextField login = new TextField ( "Username");
  private PasswordField password = new PasswordField ( "Password");
  private Navigator navigator;
	LobbyView lobbyView;

	public LoginView(Navigator navigator) {
		this.navigator = navigator;
    addComponent ( new Label ("Before you can play the game, you must first login") );
    addComponent ( new Label () );
    addComponent ( login );
    addComponent ( password );
    addComponent ( btnLogin );

    btnLogin.addClickListener(new Button.ClickListener() {
    	@Override
    	public void buttonClick(Button.ClickEvent event) {
    		try {
    			authenticate(login.getValue(), password.getValue());
    		} catch (AuthenticationException e) {
    			// login failed
    			login.clear();
    			password.clear();
    		}
    	}
    });
	}

	// I've implemented minimal authorization to achieve user identity:
	// Everyone has the same password, and any username is ok.
	private void authenticate(String user, String password) throws AuthenticationException {
		if ("SECRET".equals(password)) {
			// Create an ID for this user
			long myID = Server.login(user);
			lobbyView = new LobbyView(navigator, myID);
			navigator.addView(LobbyView.NAME, lobbyView);
			navigator.removeView(LoginView.NAME);
			navigator.navigateTo(LobbyView.NAME);
		} else {
			throw new AuthenticationException("Failed to authenticate");
		}
	}

	@Override
	public void enter(ViewChangeEvent event) { }

	public class AuthenticationException extends Exception {
		public AuthenticationException(String message) {
			super(message);
		}
	}
}
