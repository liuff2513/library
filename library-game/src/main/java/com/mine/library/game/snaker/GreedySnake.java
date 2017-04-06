package com.mine.library.game.snaker;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Color;
import java.util.Random;
import java.util.List;
import java.util.LinkedList;
import javax.swing.JPanel;
import javax.swing.JFrame;

class Food {
	Snake snake;
	int x, y;
	boolean flag;

	Food(Snake snake) {
		this.snake = snake;
		flag = true;
		int x = 0;
		int y = 0;
		while (flag) {
			flag = false;
			x = Math.abs(new Random().nextInt() % 40);
			y = Math.abs(new Random().nextInt() % 40);
			if (snake.head.x == x && snake.head.y == y)
				flag = true;
			for (Point it : snake.body)
				if (it.x == x && it.y == y)
					flag = true;
		}
		this.x = x;
		this.y = y;
	}

}

class Snake {
	int size, length, dir;
	List<Point> body;
	Point head;

	Snake(Point head) {
		size = 10;
		length = 5;
		dir = 4;
		this.head = head;
		body = new LinkedList<Point>();
		for (int i = 0; i < length - 1; i++) {
			Point p = new Point(head.x, head.y + 1 + i);
			body.add(p);
		}
	}

	boolean isEat(Food food) {
		if (head.x == food.x && head.y == food.y)
			return true;
		return false;
	}

	boolean isOut() {
		if (head.x < 0 || head.y < 0 || head.x >= 40 || head.y >= 40)
			return true;
		return false;
	}

	boolean isPitch() {
		for (Point it : body)
			if (head.x == it.x && head.y == it.y) {
				dead(1);
				return true;
			}
		return false;
	}

	void dead(int way) {
		System.exit(way);
	}
}

class MyPanel extends JPanel implements KeyListener, Runnable {
	Snake snake;
	Food food;
	int speed;

	MyPanel(Snake snake, Food food) {
		this.snake = snake;
		this.food = food;
		speed = 526;
	}

	public void paint(Graphics g) {
		super.paint(g);
		g.fillRect(0, 0, 400, 400);
		g.setColor(Color.YELLOW);
		g.fillRect(snake.head.x * snake.size, snake.head.y * snake.size,
				snake.size, snake.size);
		g.setColor(Color.ORANGE);
		for (Point it : snake.body)
			g.fillRect(it.x * snake.size, it.y * snake.size, snake.size,
					snake.size);
		g.setColor(Color.GREEN);
		g.fillRect(food.x * snake.size, food.y * snake.size, snake.size,
				snake.size);
	}

	// 自动行进
	public void keyPressed(Integer dir) {

		Point oxy = new Point(snake.head);
		if (dir == 3) {
			snake.head.x--;
		} else if (dir == 4) {
			snake.head.x++;
		} else if (dir == 1) {
			snake.head.y--;
		} else if (dir == 2) {
			snake.head.y++;
		} else
			return;

		if (snake.isOut())
			snake.dead(1);

		snake.body.add(0, oxy);
		if (snake.isEat(food)) {
			snake.length++;
			if (speed >= 50)
				speed -= 25;
			food = new Food(snake);
		} else
			snake.body.remove(snake.length - 1);

		if (snake.isPitch())
			snake.dead(2);
		repaint();
	}

	// 按键仅用于修改蛇的方向
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		// 按鍵和行進方向相反，按鍵無效直接返回！
		if (((key == KeyEvent.VK_LEFT && snake.dir == 4)
				|| (key == KeyEvent.VK_UP && snake.dir == 2)
				|| (key == KeyEvent.VK_DOWN && snake.dir == 1) || (key == KeyEvent.VK_RIGHT && snake.dir == 3))) {
			return;
		}
		if (key == KeyEvent.VK_LEFT) {
			snake.dir = 3;
		} else if (key == KeyEvent.VK_RIGHT) {
			snake.dir = 4;
		} else if (key == KeyEvent.VK_UP) {
			snake.dir = 1;
		} else if (key == KeyEvent.VK_DOWN) {
			snake.dir = 2;
		} else
			return;

	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	public synchronized void run() {
		while (true) {
			try {
				Thread.sleep(speed);
			} catch (Exception e) {
			}
			keyPressed(snake.dir);
		}
	}
}

public class GreedySnake extends JFrame {
	MyPanel mp;
	Snake snake;
	Food food;
	Thread t;

	public GreedySnake() {
		snake = new Snake(new Point(5, 5));
		food = new Food(snake);
		mp = new MyPanel(snake, food);
		t = new Thread(mp);
		t.start();
		add(mp);
		addKeyListener(mp);
		setTitle("GreedySnake");
		setSize(40 * 10 + 15, 40 * 10 + 38);
		setLocation(200, 200);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public static void main(String[] args) {
		new GreedySnake();
	}
}