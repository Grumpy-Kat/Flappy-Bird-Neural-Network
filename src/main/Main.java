package main;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Main implements ActionListener {
	public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 55);
	public static final Font TEXT_FONT = new Font("Arial", Font.PLAIN, 25);
	
	public static JFrame frame;
	
	public static void main(String[] args) {
		frame = new JFrame("Flappy Bird");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT));
		frame.setResizable(false);
		try {
			frame.setIconImage(ImageIO.read(new File("imgs/FlappyBird.png")));
			frame.setContentPane(new JLabel(new ImageIcon(ImageIO.read(new File("imgs/Background.png")))));
		} catch(IOException e) {
			System.out.println(e.toString());
		}
		init();
	}

	public static void init() {
		Main main = new Main();
		frame.setLayout(new GridLayout(2, 1));
		//title text
		JLabel title = new JLabel("Flappy Bird", JLabel.CENTER);
		title.setFont(TITLE_FONT);
		frame.add(title);
		//buttons panel
		JPanel btns = new JPanel();
		btns.setOpaque(false);
		btns.setBackground(new Color(0, 0, 0, 0));
		btns.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 0));
		//single player button
		JButton btn0 = new JButton("Single Player");
		btn0.addActionListener(main);
		btn0.setPreferredSize(new Dimension(200, 75));
		btn0.setFont(TEXT_FONT);
		btns.add(btn0);
		//best bird button
		JButton btn1 = new JButton("Best Bird");
		btn1.addActionListener(main);
		btn1.setPreferredSize(new Dimension(200, 75));
		btn1.setFont(TEXT_FONT);
		btns.add(btn1);
		//train model button
		JButton btn2 = new JButton("Train Model");
		btn2.addActionListener(main);
		btn2.setPreferredSize(new Dimension(200, 75));
		btn2.setFont(TEXT_FONT);
		btns.add(btn2);
		frame.add(btns);
		frame.setVisible(true);
		frame.pack();
	}
	
	public static void reset() {
		frame.getContentPane().removeAll();
		frame.setLayout(new BorderLayout());
		frame.repaint();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		int mode;
		switch(e.getActionCommand()) {
			case "Single Player":
				mode = 0;
				break;
			case "Best Bird":
				mode = 1;
				break;
			case "Train Model":
			default:
				mode = 2;
				break;
		}
		reset();
		Game.mode = mode;
		new Game();
	}
}
