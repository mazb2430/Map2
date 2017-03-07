import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;

import javax.swing.JComponent;

public class NamedPlace extends Place implements Serializable {
	private static final long serialVersionUID = 1012952135516846725L;
	
	public NamedPlace(String name, int x, int y, Color color, Category category) {
		super(name, x, y, color, category);
	}
	
	protected void draw(Graphics g) {
		g.setColor(getColor());
		int[] xes = {0,11,22};
		int[] yes = {0,20,0};
		g.fillPolygon(xes, yes, 3);
	}
	
	public String toString() {
		return String.format("Named,%s,%d,%d,%s", category, pos.getX(), pos.getY(), name);
	}

	public void hide(Graphics g) {
		this.setVisible(false);
	}
	
	public void unHide(Graphics g) {
		this.setVisible(true);
	}

}
