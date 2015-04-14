package com.nushae.gravahal;

import javax.servlet.annotation.WebServlet;

import com.nushae.gravahal.view.LoginView;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;

@SuppressWarnings("serial")
@Theme("gravahal")
@Push
@PreserveOnRefresh
public class MainUI extends UI {
	Navigator navigator;
	LoginView loginView;

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = MainUI.class)
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {
		getPage().setTitle("Grava Hal");

		// Create a navigator to control the views
		navigator = new Navigator(this, this);

		// Create and register the views
		loginView = new LoginView(navigator);
		navigator.addView(LoginView.NAME, loginView);
	}

}