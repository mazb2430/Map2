import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;

public class DescribedPlace extends Place{
	protected String description;
	
	public DescribedPlace(String name, int x, int y, Color color, String description, Category category) {
		super(name, x, y, color, category);
		this.setDescription(description);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	protected void draw(Graphics g) {
		g.setColor(getColor());
		int[] xes = {0,11,22};
		int[] yes = {0,20,0};
		g.fillPolygon(xes, yes, 3);
	}
	
	public String toString() {
		return String.format("Described,%s,%d,%d,%s,%s", category, pos.getX(), pos.getY(), name, description);
	}

	public void hide(Graphics g) {
		this.setVisible(false);
	}

}