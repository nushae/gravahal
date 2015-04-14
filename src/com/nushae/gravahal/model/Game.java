package com.nushae.gravahal.model;

import java.io.Serializable;

import com.nushae.gravahal.server.Server;
import com.nushae.gravahal.server.ServerMessage;

public class Game implements Serializable {
	private static final long serialVersionUID = 718265866501L;

	public static final int OPEN = 0;
	public static final int PLAYING = 1;
	public static final int ENDED = 2;

	private static final boolean FULLSOWING = false; // sow in both players' pits if true

	public static final int DEFAULT_PITS = 6;
	private long gameID;
	private String gameName;
	private int state;

	private int pits;

	private Player[] players = new Player[2];
	private int activePlayer; // either the player whose turn it is, or (if game over) the last player to have moved
	private int winner; // -1 = draw, else 0 or 1 for start/other player respectively

	public Game(long starter, long id, String name) {
		this(starter, id, name, DEFAULT_PITS);
	}

	public Game(long starter, long id, String name, int pits) {
		players[0] = new Player(pits, starter);
		gameID = id;
		gameName = name;
		state = OPEN;
		this.pits = pits;
	}

	public void setSecondPlayer(long id) {
		if (state == OPEN) {
			players[1] = new Player(pits, id);
			activePlayer = 0; // game starter is also the player to go first (for simplicity's sake)
			state = PLAYING;
		}
	}

	public int getActivePlayer() {
		return activePlayer;
	}

	public long getStarterID() {
		return players[0].getID();
	}

	public long getSecondPlayerID() {
		return players[1].getID();
	}

	public Player getPlayer(int index) {
		return players[index];
	}

	public long getID() {
		return gameID;
	}

	public String getName() {
		return gameName;
	}

	public void finish(int winner) {
		if (state == PLAYING) {
			state = ENDED;
			this.winner = winner;
			Server.broadcast(new ServerMessage(ServerMessage.GAMEOVER, gameID));
		}
	}

	public int getWinner() {
		return winner;
	}

	public int getState() {
		return state;
	}

	public int getNumberOfPits() {
		return pits;
	}

	// The game description was ambiguous whether you sow only in your own pits or also the opponents' (it was explicit about the grava hals)
	// I decided to implement both cases, although which is set at compile time (use the FULLSOWING flag above);
	public void performMove(int player, int pit) {
		int stones = players[player].getContentsAtPosition(pit);
		players[player].clearPitAt(pit);

		// sowing part:
		for (int i=0, sowPit=pit+1; i<stones; i++) {
			if (sowPit == pits) { // Grava Hal
				players[player].addToGravaHal(1);
			} else {
				if (sowPit<pits) { // this is always the case if !FULLSOWING
					players[player].addAmountToPitAtPos(1, sowPit);
				} else { // FULLSOWING must be true; pits < sowPit <= 2*pits
					players[1-player].addAmountToPitAtPos(1, sowPit-pits-1);
				}
			}
			if (FULLSOWING) {
				sowPit = (sowPit+1) % (2*pits+1);
			} else {
				sowPit = (sowPit+1) % (pits+1);
			}
		}

		// capture:
		int lastPit = (stones + pit) % ((FULLSOWING?2:1)*pits+1);
		if (lastPit < pits) {
			if (players[player].getContentsAtPosition(lastPit)==1) {
				// capture
				int captured = players[1-player].getContentsAtPosition(pits-1-lastPit);
				players[1-player].clearPitAt(pits-1-lastPit);
				players[player].clearPitAt(lastPit);
				players[player].addToGravaHal(captured+1);
			}
		}

		// game end:
		if (players[player].areAllPitsEmpty() || players[1-player].areAllPitsEmpty()) {
			players[player].sweep();
			players[1-player].sweep();
			int matchResult = (int) Math.signum(players[player].getGravaHal() - players[1-player].getGravaHal());
			if (matchResult == -1) {
				finish(1-player); // other player won
			} else if (matchResult == 1) {
				finish(player); // this player won
			} else {
				finish(-1); // draw
			}
		} else { // we don't want to switch active player if the game ended
			if (lastPit < pits) { // nor if the last stone landed in Grava Hal
				activePlayer = 1-activePlayer;
			}
		}

		sendMove(player+"+"+pit);
	}

	private void sendMove(String move) {
		Server.broadcast(new ServerMessage(ServerMessage.MOVE, gameID, move));
	}
}
