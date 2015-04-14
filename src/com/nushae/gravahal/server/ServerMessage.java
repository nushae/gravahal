package com.nushae.gravahal.server;

import java.io.Serializable;

/**
 * A ServerMessage is a tuple (type, id, payload).
 * Depending on the type, the id and/or payload have a different interpretation.
 * 
 * @author Angelo Wentzler
 *
 */
public class ServerMessage implements Serializable { 

	protected static final long serialVersionUID = 718265866501L;

	private int type;
	private long id;
	private String payload;

	public final static int UPDATE = 0; // tells any listener to update its view(s)
	public final static int LOGIN = 1; // someone logged in (id = userID)
	public final static int LOGOUT = 2; // someone logged out (id = userID)
	public final static int STARTGAME = 10; // a game started (id = gameID)
	public final static int ADDGAME = 11; // a new game is added to the open list (id = gameID)
	public final static int REMOVEGAME = 12; // an open game was cancelled (id = gameID)
	public final static int GAMEOVER = 13; // a game ended (id = gameID)
	public final static int MOVE = 20; // a move was made in a game (id = gameID, payload = game state representation)

	public ServerMessage(int type, long id) {
		this(type, id, "");
	}

	public ServerMessage(int type, long id, String payload) {
		this.type = type;
		this.id = id;
		this.payload = payload;
	}

	public int getType() {
		return type;
	}

	public long getID() {
		return id;
	}

	public String getPayload() {
		return payload;
	}
}
