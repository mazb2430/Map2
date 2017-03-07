import java.awt.*;
import javax.swing.*;

public abstract class Triangle extends JComponent {
	private boolean drawn = false;

	public Triangle(int x, int y) {
		setBounds(x, y, 70, 70);
		Dimension d = new Dimension(70, 70);
		setPreferredSize(d);
		setMaximumSize(d);
		setMinimumSize(d);
	}

	abstract protected void draw(Graphics g);
	abstract public boolean lookLike(Triangle b);

	protected void paintComponent(Graphics g) {
		super.paintComponents(g);
		if (!drawn) {
			g.setColor(Color.BLUE);
			g.fillRect(0, 0, getWidth(), getHeight());
		}
		else {
			draw(g);
		}
	}

	public void setDisplayed(boolean b) {
		drawn = b;
		repaint();
	}

	public boolean isDrawn() {
		return drawn;
	}

}
