package com.nushae.gravahal.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.nushae.gravahal.model.Game;

public class Server implements Serializable {
	private static final long serialVersionUID = 718265866501L;

	private static Map<Long, String> users = new HashMap<Long, String>();
	private static Map<Long, Game> games = new HashMap<Long, Game>();
	private static List<GameEventListener> listeners = new ArrayList<GameEventListener>();
	
	public static synchronized long login(String user) {
		long id;

		do {
			id = new Random().nextLong();
		} while (users.containsKey(id));

		users.put(id, user);
		broadcast(new ServerMessage(ServerMessage.LOGIN, id));
		return id;
	}

	public static synchronized void logout(long UID) {
		// end all games for this user:
		for (Game g: games.values()) {
			if (g.getState() == Game.PLAYING && (g.getStarterID() == UID || g.getSecondPlayerID() == UID)) {
				g.finish(g.getStarterID() == UID ? 1 : 0);
			} else if (g.getState() == Game.OPEN && g.getStarterID() == UID) {
				cancelGame(UID, g.getID());
			}
		}
		users.remove(UID);
		broadcast(new ServerMessage(ServerMessage.LOGOUT, UID));
	}

	public static synchronized Map<Long, Game> getGameList() {
		return Collections.unmodifiableMap(games);
	}

	public static synchronized String getUserName(long id) {
		return users.get(id);
	}

	public static synchronized int getUserCount() {
		return users.size();
	}

	public static synchronized Game openGame(long userID, String gameName) {
		long id;

		do {
			id = new Random().nextLong();
		} while (games.containsKey(id));

		Game game = new Game(userID, id, gameName);
		games.put(id, game);
		broadcast(new ServerMessage(ServerMessage.ADDGAME, game.getID()));
		return game;
	}

	public static synchronized void joinGame(long uid, long gameID) {
		Game game = games.get(gameID);
		if (game.getState() == Game.OPEN) {
			game.setSecondPlayer(uid);
			broadcast(new ServerMessage(ServerMessage.STARTGAME, game.getID()));
		}
	}

	public static synchronized void cancelGame(long uid, long gameID) {
		Game game = games.get(gameID);
		if (game.getStarterID() == uid && game.getState() == Game.OPEN) {
			games.remove(gameID);
			broadcast(new ServerMessage(ServerMessage.REMOVEGAME, game.getID()));
		}
	}

	public static synchronized Game getGame(long gameID) {
		return games.get(gameID);
	}

	public static synchronized void register(GameEventListener listener) {
		listeners.add(listener);
	}

	public static synchronized void unregister(GameEventListener listener) {
		listeners.remove(listener);
	}

	public static synchronized void broadcast(final ServerMessage message) {
		for (final GameEventListener listener : listeners) {
			listener.receiveMessage(message);
		}
	}

	public interface GameEventListener {
		public void receiveMessage(ServerMessage message);
	}
}
