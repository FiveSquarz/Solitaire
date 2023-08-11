package solitaire;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import javax.swing.BorderFactory;

import solitaire.Card.Suit;

import java.util.Collections;

public class Solitaire {
	ArrayList<Stack<Card>> columns = new ArrayList<Stack<Card>>(7);
	ArrayList<Stack<Card>> foundations = new ArrayList<Stack<Card>>(4);
	Queue<Card> deck = new LinkedList<Card>();
	Stack<Card> waste = new Stack<Card>();
	Stack<Card> visibleWaste = new Stack<Card>();
	
	private Card selected;
	
	public Solitaire() {
		initiate();
	}

	// precondition: all instance variables have been initialized
	// postcondition: deck contains a fully shuffled deck of cards and the column and foundation piles are properly populated.
	private void initiate() {
		for (int value = 1; value <= 13; value++) {
			for (Suit suit : Suit.values()) {
				deck.add(new Card(value, suit));
			}
		}
		List<Card> temp = new ArrayList<Card>(deck);
		//Collections.shuffle(temp);
		Collections.reverse(temp);
		deck = new LinkedList<Card>(temp);

		for (int iColumn = 0; iColumn < 7; iColumn++) {
			Stack<Card> column = new Stack<Card>();
			columns.add(column);
			for (int iCard = 0; iCard < iColumn; iCard++) {
				Card card = deck.poll();
				card.hide();
				column.add(card);
			}
			Card card = deck.poll();
			card.show();
			column.add(card);
		}
		
		for (int i = 0; i < 4; i++) {
			foundations.add(new Stack<Card>());
			foundations.get(i).add(new Card(100, Suit.Spades));
		}
	}

	// pre-condition: both cards being sent in exist and toMove is not reversed and not a non-first visible waste card
	// post-condition: returns “true” if the proposed move is a legal one
	boolean legalMove(Card toMove, Card location) {
		Stack<Card> originalContainer = container(toMove);
		Stack<Card> targetContainer = container(location);
		
		if (toMove.isReversed || originalContainer == waste && waste.peek() != toMove)
			return false;
		
		if (columns.contains(targetContainer)) { //want to move into a column
			if (targetContainer.peek() != location) //desired location is not the top of its pile
				return false;
			if (toMove.value == 13 && targetContainer.peek().value == 100) //want to move king into empty column
				return true;
			if (toMove.value == location.value - 1 && toMove.suit.isRed != location.suit.isRed) //want to move into a valid column position
				return true;
			return false;
		}
		if (foundations.contains(targetContainer)) { //want to move into a foundation
			if (originalContainer.peek() != toMove) //desired card to move is not the top of its pile
				return false;
			if (toMove.value == 1 && targetContainer.peek().value == 100) //want to move Ace into foundation pile
				return true;
			if (toMove.value == location.value + 1 && toMove.suit == location.suit) //want to move into a valid foundation position
				return true;
			return false;
		}
		return false;
	}
	
	//make extra visible waste cards invisible, or shift an old waste card back into visible waste
	void updateVisibleWaste() {
		if (visibleWaste.size() > 3) {
			waste.add(visibleWaste.remove(0));
			waste.peek().hide();
			updateVisibleWaste();
		} else if (visibleWaste.size() < 3 && !waste.empty()) {
			Card card = waste.pop();
			card.show();
			visibleWaste.add(0, card);
		}
	}
	
	//marks the chosen card as selected
	private void select(Card card) {
		if (selected != null)
			selected.setBorder(null);
		selected = card;
		if (selected != null)
			selected.setBorder(BorderFactory.createLineBorder(Color.cyan, 3));
	}
	
	//handle a click on a card
	public void click(Card card) {
		if (deck.contains(card)) { //draw from deck
			if (deck.peek().value != 100) { //deck isn't empty
				int i = 0;
				do { //shift up to 3 new cards into visible waste
					waste.add(deck.poll());
					visibleWaste.add(waste.pop());
					visibleWaste.peek().show();
					if (deck.isEmpty()) { //add temporary card
						deck.add(new Card(100, Suit.Spades));
					}
					i++;
				} while (deck.peek().value != 100 && i < 3);
			} else { //deck is empty, refill from waste
				deck.clear(); //remove temporary card
				while (!waste.empty()) {
					deck.add(waste.remove(0));
				}
				while (!visibleWaste.empty()) {
					deck.add(visibleWaste.remove(0));
				}
			}
			if (deck.isEmpty()) { //add temporary card
				deck.add(new Card(100, Suit.Spades));
			}
			select(null);
			updateVisibleWaste();
			return;
		}
		if (card.isReversed || container(card) == visibleWaste && visibleWaste.peek() != card) { //reversed or non-first visible waste card
			select(null);
			return;
		}
		if (selected == null) {
			if (card.value == 100) { //select placeholder card
				select(null);
				return;
			}
			select(card);
			return;
		}
		if (!legalMove(selected, card)) {
			select(card.value == 100 ? null : card);
			return;
		}
		
		Stack<Card> originalContainer = container(selected);
		Stack<Card> targetContainer = container(card);
		
		// move single card from (visible waste to column or foundation) or (foundation to column)
		if (visibleWaste.contains(selected) || foundations.contains(originalContainer)) {
			if (columns.contains(targetContainer) && targetContainer.peek().value == 100) { //add King to empty column
				targetContainer.clear(); //remove placeholder
			}
			targetContainer.add(originalContainer.pop());
		} else if (foundations.contains(targetContainer)) { //single card from column to foundation
			targetContainer.add(originalContainer.pop());
			if (originalContainer.empty()) { //add placeholder
				originalContainer.add(new Card(100, Suit.Spades));
			} else { //show next card
				originalContainer.peek().show();
			}
		} else { //move stack of cards between columns
			if (targetContainer.peek().value == 100) { //add King to empty column
				targetContainer.clear(); //remove placeholder
			}
			int originalIndex = originalContainer.indexOf(selected);
			while (originalIndex < originalContainer.size()) {
				targetContainer.add(originalContainer.remove(originalIndex));
			}
			if (originalContainer.empty()) { //add placeholder
				originalContainer.add(new Card(100, Suit.Spades));
			} else { //show next card
				originalContainer.peek().show();
			}
		}
		updateVisibleWaste();
		select(null);
		return;
	}
	
	//precondition: card is not null
	//postcondition: returns the Stack<Card> the card belongs to, or null if it's in some other container
	private Stack<Card> container(Card card) {
		if (visibleWaste.contains(card))
			return visibleWaste;
		for (Stack<Card> column : columns) {
			if (column.contains(card))
				return column;
		}
		for (Stack<Card> foundation : foundations) {
			if (foundation.contains(card))
				return foundation;
		}
		return null;
	}
	
	//precondition: instance variables have been initialized
	//postcondition: returns if the game has been won
	boolean hasWon() {
		return foundations.get(0).peek().value == 13 && foundations.get(1).peek().value == 13 && foundations.get(2).peek().value == 13 && foundations.get(3).peek().value == 13;
	}
}
