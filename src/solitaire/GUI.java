package solitaire;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.util.Stack;

import solitaire.Card.Suit;

public class GUI extends JFrame implements ActionListener, MouseListener, MouseMotionListener {

	Solitaire game;
	
	//sets up the UI, background, and listeners
	public GUI(Solitaire game) {
		this.game = game;
		
		// Create and set up the window.
		setTitle("Solitaire");
		setSize(1000, 700);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// this supplies the background
	       try {
	    	   setContentPane(new ImagePanel(ImageIO.read(getClass().getResource("/solitaire/images/background.jpeg")), this));
	       }catch(IOException e) {
	           e.printStackTrace();
	       }
		
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.X_AXIS));
		
		redraw();

		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
		this.setVisible(true);
	}
	
	//draws/redraws all the ui content based on the game model
	public void redraw() {
		this.getContentPane().removeAll();
		
		if (game.hasWon()) {
			JPanel p = new JPanel();
			p.setBackground(Color.yellow);
			JLabel label = new JLabel("YOU WIN!");
			label.setFont(new Font("SansSerif", Font.PLAIN, 160));
			label.setForeground(Color.green);
			p.add(label);
			this.add(p);
			
			this.repaint();
			this.revalidate();
			return;
		}
		
		JPanel drawPanel = new JPanel();
		drawPanel.setLayout(new BoxLayout(drawPanel, BoxLayout.Y_AXIS));
		drawPanel.setOpaque(false);
		drawPanel.setMaximumSize(new Dimension(1000, 1000));
		drawPanel.setPreferredSize(new Dimension(0, 0));
		drawPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		
		Card topOfDeck = game.deck.peek();
		if (topOfDeck.value != 100)
			topOfDeck.hide();
		drawPanel.add(Box.createGlue());
		drawPanel.add(topOfDeck);
		
		drawPanel.add(Box.createGlue());
		drawPanel.add(drawPile(game.visibleWaste, 3));
		drawPanel.add(Box.createGlue());
		
		this.add(drawPanel);
		
		for (int i = 0; i < 7; i++) {
			JPanel p = new JPanel();
			p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
			p.setOpaque(false);
			p.setMaximumSize(new Dimension(1000, 1000));
			p.setPreferredSize(new Dimension(0, 0));
			p.setBorder(BorderFactory.createLineBorder(Color.black));

			p.add(Box.createVerticalStrut(120));
			p.add(drawPile(this.game.columns.get(i), -1));
			for (int z = 0; z < 2; z++) {
				p.add(Box.createVerticalGlue());
			}
			this.add(p);
		}
		
		JPanel foundations = new JPanel();
		foundations.setLayout(new BoxLayout(foundations, BoxLayout.Y_AXIS));
		foundations.setOpaque(false);
		foundations.setMaximumSize(new Dimension(1000, 1000));
		foundations.setPreferredSize(new Dimension(0,0));
		foundations.setBorder(BorderFactory.createLineBorder(Color.black));
		this.add(foundations);
		
		foundations.add(Box.createGlue());
		for (int i = 0; i < 4; i++) {
			foundations.add(game.foundations.get(i).peek());
			foundations.add(Box.createGlue());
		}
		
		this.repaint();
		this.revalidate();
	}
	
	// precondition: stackIn is not null
	// postcondition: returns a JLayeredPane of the cards in a pile
	// height of return value is either as much as necessary (cardHeight = -1) or as given in terms of number of cards
	private JLayeredPane drawPile(Stack<Card> stackIn, int cardHeight) {
		Object[] cards = stackIn.toArray();
		JLayeredPane layeredPane = new JLayeredPane();
		for (int i = 0; i < cards.length; i++) {
			Card card = (Card)cards[i];
			if (card == game.deck.peek()) 
				System.out.println(card.positionOffset.x);
			card.setLocation(0, 32 * i);
			card.setAlignmentX(CENTER_ALIGNMENT);
			layeredPane.add(card, 5, 0);
		}
		Dimension size = new Dimension(100, 145 + 32 * ((cardHeight == -1 ? cards.length : cardHeight) - 1));
		layeredPane.setPreferredSize(size);
		layeredPane.setMaximumSize(size);
		layeredPane.setMinimumSize(size);
		return layeredPane;
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	//determines which card was clicked and passes it to the game model to handle
	@Override
	public void mousePressed(MouseEvent arg0) {
		Component component = this.findComponentAt(arg0.getX(), arg0.getY());
		if (arg0.getButton() == MouseEvent.BUTTON1 && component instanceof Card) {
			game.click((Card)component);
			redraw();
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub

	}
}
