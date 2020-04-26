import java.awt.Color;

public class Boid {
	public double[] pos;
	public double[] vel;
	public double width;
	public double height;
	public double direction;
	public Color color;
	public Boid(double[] pos, double[] vel, double width, double height, Color color)
	{
		this.pos = pos;
		this.vel = vel;
		this.width = width;
		this.height = height;
		this.color = color;
		calcDirection();
	}
	
//	public void calcDirection() 
//	{
//		double d = Math.atan(vel[1] / vel[0]);
//		if(vel[0] < 0 && vel[1] <= 0)
//			d += Math.PI;
//		else if(vel[0] < 0 && vel[1] > 0)
//			d += Math.PI / 2;
//		if(d < 0)
//			d += 2 * Math.PI;
//		if(d > Math.PI * 2)
//			d -= 2 * Math.PI;
//		this.direction = d;
//	}
	
	public void calcDirection()
	{
		double a = Math.abs(Math.atan(vel[1] / vel[0]));
		// if quad 1 do nothing
		// quad 2 = pi - angle
		// quad 3 = pi + angle
		// quad 4 = 2pi - angle
		if(vel[0] >= 0 && vel[1] >= 0)
			this.direction = a;
		else if(vel[0] < 0 && vel[1] >= 0)
			this.direction = Math.PI - a;
		else if(vel[0] <= 0 && vel[1] < 0)
			this.direction = Math.PI + a;
		else if(vel[0] > 0 && vel[1] < 0)
			this.direction = 2 * Math.PI - a;
	}
}
