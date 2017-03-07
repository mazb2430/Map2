import java.awt.Color;
import java.io.Serializable;

public class Category implements Serializable {
	private String name;
	private Color color;
	
	public Category(String name, Color color) {
		this.setName(name);
		this.setColor(color);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
	
	public Category getCategory() {
		return this;
	}
	
	public String toString() {
		return name;
	}
	
}
