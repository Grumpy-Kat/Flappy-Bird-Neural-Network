package main;

import machineLearning.GeneticAlgorithm;
import machineLearning.NeuralNetwork;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import java.awt.image.BufferedImage;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonReader;

//TODO: Fix loading bird prob

public class Game implements Runnable, KeyListener, ActionListener {
	public static final int SCREEN_WIDTH = 1000;
	public static final int SCREEN_HEIGHT = 600;
	
	private static String BIRD_FILE_LOC = "/saves/bestBird.json";
	
	private static BufferedImage birdImg;
	private static BufferedImage pipeImg;
	private static BufferedImage pipeEndImg;
	private static BufferedImage pipeEnd2Img;
	private static BufferedImage backgroundImg;
	
	private ArrayList<Bird> birds;
	private ArrayList<Bird> savedBirds;
	private PipesPanel pipes;
	private GeneticAlgorithm geneticAlgorithm;
	
	private GridBagConstraints c;
	private JButton resetBtn;
	
	private Thread t;
	private static double lastTick;
	private static double deltaTime;
	
	private static int fps = 60;
	private double speed = 1;
	
	private boolean isRunning = true;
	//0 is single player, 1 is best bird, 2 is machine learning
	public static int mode = 2;
	
	private int maxPoints = 0;
	private Bird bestBird;
	
	public Game() {
		speed = 1;
		try {
			birdImg = ImageIO.read(new File("imgs/FlappyBird.png"));
			pipeImg = ImageIO.read(new File("imgs/Pipe.png"));
			pipeEndImg = ImageIO.read(new File("imgs/PipeEnd.png"));
			pipeEnd2Img = ImageIO.read(new File("imgs/PipeEnd2.png"));
			backgroundImg = ImageIO.read(new File("imgs/Background.png"));
		} catch(IOException e) {
			System.out.println(e.getStackTrace());
		}
		birds = new ArrayList<Bird>();
		savedBirds = new ArrayList<Bird>();
		if(mode == 0) {
			addBird(new Bird(null, false));
		} else if(mode == 1) {
			addBird(new Bird(loadBird(BIRD_FILE_LOC), false));
		} else {
			for(int i = 0; i < GeneticAlgorithm.POPULATION_SIZE; i++) {
				addBird(new Bird(null, false));
			}
			geneticAlgorithm = new GeneticAlgorithm();
		}
		Main.frame.setLayout(new GridBagLayout());
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.ipadx = 100;
		c.ipady = 20;
		c.anchor = GridBagConstraints.FIRST_LINE_END;
		c.insets = new Insets(50, 50, 20, 20);
		JButton backBtn = new JButton("Back");
		backBtn.setOpaque(false);
		backBtn.setFont(Main.TEXT_FONT);
		backBtn.addActionListener(this);
		Main.frame.add(backBtn, c);
		c.gridx = 4;
		c.gridy = 3;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.ipadx = 100;
		c.ipady = 20;
		c.anchor = GridBagConstraints.PAGE_END;
		c.insets = new Insets(255, 0, 50, 0);
		resetBtn = new JButton("Reset");
		resetBtn.setOpaque(false);
		resetBtn.setFont(Main.TEXT_FONT);
		resetBtn.addActionListener(this);
		Main.frame.add(resetBtn, c);
		resetBtn.setVisible(false);
		resetBtn.setEnabled(false);
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 5;
		c.gridheight = 5;
		c.ipadx = SCREEN_WIDTH;
		c.ipady = SCREEN_HEIGHT;
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(0, 0, 0, 0);
		pipes = new PipesPanel(birds, this);
		pipes.setOpaque(false);
		Main.frame.add(pipes, c);
		Main.frame.addKeyListener(this);
		Main.frame.requestFocus();
		Main.frame.setVisible(true);
		t = new Thread(this);
		t.start();
		isRunning = true;
	}
	
	@Override
	public void run() {
		lastTick = System.currentTimeMillis();
		while(isRunning) {
			deltaTime = (System.currentTimeMillis() - lastTick) / 1000 * speed;
			lastTick = System.currentTimeMillis();
			for(int i = 0; i < birds.size(); i++) {
				birds.get(i).update();
				if(mode == 1 || mode == 2) {
					birds.get(i).think(pipes);
				}
			}
			if(birds.size() == 0 && mode == 2) {
				geneticAlgorithm.nextGeneration(savedBirds, this);
			}
			if(bestBird != null) {
				saveBird(bestBird, BIRD_FILE_LOC, true);
			}
			pipes.repaint();
			try {
				Thread.sleep((long)((1000 / fps) / speed));
			} catch(InterruptedException e) {
				System.out.println(e.toString());
			}
		}
		pipes.repaint();
		t.interrupt();
		resetBtn.setVisible(true);
		resetBtn.setEnabled(true);
	}

	public void reset() {
		birds.clear();
		savedBirds.clear();
		resetBtn.setVisible(false);
		resetBtn.setEnabled(false);
		pipes.reset();
		Main.frame.addKeyListener(this);
		Main.frame.requestFocus();
		Main.frame.setVisible(true);
		if(mode == 0) {
			speed = 1;
			addBird(new Bird(null, false));
			t = new Thread(this);
			t.start();
			isRunning = true;
		} else if(mode == 1) {
			addBird(new Bird(loadBird(BIRD_FILE_LOC), false));
		}
	}
	
	public void addBird(Bird bird) {
		birds.add(bird);
		savedBirds.add(bird);
	}

	public void addBirds(ArrayList<Bird> birds) {
		this.birds.addAll(birds);
		savedBirds.addAll(birds);
	}
	
	public void removeBirds() {
		if(bestBird != null && !bestBird.getIsAlive()) {
			bestBird = null;
		}
		for(int i = 0; i < birds.size(); i++) {
			if(!birds.get(i).getIsAlive()) {
				if(mode == 0) {
					isRunning = false;
				} else if(mode == 1) {
					reset();
				} else {
					birds.remove(birds.get(i));
				}
			}
		}
	}
	
	private void saveBird(Bird bird, String fileName, boolean checkBest) {
		Gson gson = new Gson();
		JsonArray arr = new JsonArray();
		arr.add(gson.toJsonTree(bird.points));
		JsonElement brain = gson.toJsonTree(bird.getBrain());
		arr.add(brain);
		String fileContents = gson.toJson(arr);
		fileContents = fileContents.substring(0, fileContents.lastIndexOf(",")) + "}]";
		try {
			String directory = new File(Game.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
			File file = new File(directory + fileName);
			if(!file.exists()) {
					File directoryFile = new File(file.getParent());
					if(!directoryFile.exists()) {
						directoryFile.mkdirs();
					}
					file.createNewFile();
			} else if(checkBest) {
				InputStreamReader reader = new InputStreamReader(new FileInputStream(file), "UTF-8");
				JsonReader jsonReader = new JsonReader(reader);
				if(((JsonArray)gson.fromJson(jsonReader, JsonArray.class)).get(0).getAsInt() > bird.points) {
					return;
				}
			}
			FileWriter writer = new FileWriter(file.getAbsoluteFile(), false);
			BufferedWriter bufferedWriter = new BufferedWriter(writer);
			bufferedWriter.write(fileContents);
			bufferedWriter.close();
		} catch(Exception e) {
			System.out.println(e.toString());;
		}
	}
	
	private NeuralNetwork loadBird(String fileName) {
		try {
			String directory = new File(Game.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
			File file = new File(directory + fileName);
			if(!file.exists()) {
				System.out.println(fileName + " not found.");
				return null;
			}
			InputStreamReader reader = new InputStreamReader(new FileInputStream(file), "UTF-8");
			JsonReader jsonReader = new JsonReader(reader);
			Gson gson = new Gson();
			JsonArray arr = gson.fromJson(jsonReader, JsonArray.class);
			return gson.fromJson(arr.get(1), NeuralNetwork.class);
		} catch(Exception e) {
			System.out.println(e.toString());
		}
		return null;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if(mode == 0) {
			if(Character.isSpaceChar(e.getKeyChar())) {
				for(Bird bird : birds) {
					bird.jump();
				}
			}
		} else {
			if(Character.isDigit(e.getKeyChar()) && e.getKeyChar() != '0') {
				speed = Character.getNumericValue(e.getKeyChar());
			}
			if(Character.isSpaceChar(e.getKeyChar())) {
				saveBird(bestBird, BIRD_FILE_LOC, false);
			}
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e) { }

	@Override
	public void keyTyped(KeyEvent e) { }
	
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand() == "Back") {
			t.interrupt();
			Main.reset();
			Main.init();
		}
		if(e.getActionCommand() == "Reset") {
			reset();
		}
	}
	
	public void setMaxPoints(Bird bestBird) {
		this.bestBird = bestBird;
		maxPoints = bestBird.points;
	}
	
	public int getMaxPoints() {
		return maxPoints;
	}
	
	public static double getDeltaTime() {
		return deltaTime;
	}
	
	public boolean getIsRunning() {
		return isRunning;
	}

	public static BufferedImage getBirdImg() {
		return birdImg;
	}
	
	public static BufferedImage getPipeImg() {
		return pipeImg;
	}
	
	public static BufferedImage getPipeEndImg() {
		return pipeEndImg;
	}
	
	public static BufferedImage getPipeEnd2Img() {
		return pipeEnd2Img;
	}
	
	public static BufferedImage getBackgroundImg() {
		return backgroundImg;
	}
	
	public GeneticAlgorithm getGeneticAlgorithm() {
		return geneticAlgorithm;
	}
}
