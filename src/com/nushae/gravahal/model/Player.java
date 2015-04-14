package com.nushae.gravahal.model;

public class Player {

	private int[] pits;
	private int gravaHal;
	private int totalPitContents = 0;
	private long myID;

	public Player(int numPits, long id) {
		pits = new int[numPits];
		for (int i = 0; i < numPits; i++) {
			pits[i] = numPits;
			totalPitContents+=numPits;
		}
		gravaHal = 0;
		myID = id;
	}

	public long getID() {
		return myID;
	}

	public int getContentsAtPosition(int pos) {
		return pits[pos];
	}

	public void addToGravaHal(int amount) {
		gravaHal+=amount;
	}

	public int getGravaHal() {
		return gravaHal;
	}

	public void addAmountToPitAtPos(int amount, int pos) {
		pits[pos] += amount;
		totalPitContents += amount;
	}

	public void clearPitAt(int pos) {
		totalPitContents -= pits[pos];
		pits[pos] = 0;
	}

	public void setAmountatPos(int amount, int pos) {
		totalPitContents += amount - pits[pos];
		pits[pos] = amount;
	}

	public void sweep() {
		if (totalPitContents>0) {
			for (int i=0; i<pits.length; i++) {
				gravaHal += pits[i];
				totalPitContents -= pits[i];
				pits[i] = 0;
			}
		}
	}

	public boolean areAllPitsEmpty() {
		return totalPitContents == 0;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		for (int i=0; i<pits.length; i++) {
			result.append("(").append(pits[i]).append(")");
		}
		result.append("[").append(gravaHal).append("]");
		return result.toString();
	}

}
