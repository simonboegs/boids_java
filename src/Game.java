import java.awt.*;
import java.util.Random;
import javax.swing.*;
import java.util.Timer;
import java.awt.image.BufferStrategy;
import java.net.http.HttpResponse.BodyHandler;
import java.util.ArrayList;
import java.util.Arrays;

public class Game extends Canvas
{
	public boolean running;
	public static int width = 800, height = 800;
	
	public Timer timer;
	public ArrayList<Boid> boids = new ArrayList<Boid>();
	public int range = 30;
	public boolean repelBool = true;
	public boolean cohesionBool = true;
	public boolean velMatchBool = true;
	public double repelConst = 2; //1.5
	public double cohesionConst = .1; //.1
	public double velMatchConst = .5; //.0001
	public double highSpeed = 5;
	public double lowSpeed = 1;
	public double border = 100;
	public static void main(String [] args)
	{
		new Window(width, height, "BOIDS");
		Game game = new Game();
		game.startGame(game);
	}
	public void startGame(Game game)
	{
		int width = 5;
		int height = 10;
		int numOfBoids = 1000;
		Color white = new Color(255,255,255);
		Color red = new Color(255,0,0);
		for(int i = 0; i < numOfBoids; i++)
		{
			Random r = new Random();
			double[] pos = {r.nextInt(Game.width-50),r.nextInt(Game.height-50)};
			double[] vel = {randomDouble(-1,1),randomDouble(-1,1)};
			boids.add(new Boid(pos,vel,width,height,white));
		}
		running = true;
		timer = new Timer();
		timer.schedule(new TimeTick(game), 0 , 1000 / 60); /////////FRAME RATE
	}
	public void update()
	{
		for (Boid boid : boids)
		{
			ArrayList<Boid> localBoids = getLocalBoids(boid);
			if(localBoids.size() > 0)
			{
				//REPEL
				double[] repel = getRepel(boid, localBoids);
				double[] cohesion = cohesion(boid, localBoids);
				double[] velMatch = velocityMatch(boid, localBoids);
				if(!repelBool) repel = new double[] {0, 0};
				if(!cohesionBool) cohesion = new double[] {0, 0};
				if(!velMatchBool) velMatch = new double[] {0, 0};
				boid.vel[0] += (repel[0] + cohesion[0] + velMatch[0]);
				boid.vel[1] += (repel[1] + cohesion[1] + velMatch[1]);
				//double[] align = getAlign(boid, localBoids);
				//boid.vel = align;
				boid.calcDirection();
				double velMag = distance(new double[] {0,0}, boid.vel);
//				if(velMag >= 0 && velMag > speedLimit)
//					boid.vel = convert(speedLimit, boid.direction, new double[] {0,0});
//				else if(velMag <= 0 && velMag < -speedLimit)
//					boid.vel = convert(-speedLimit, boid.direction, new double[] {0,0});
				if(velMag < lowSpeed)
					boid.vel = convert(lowSpeed, boid.direction, new double[] {0,0});
			}
		}
		for (Boid boid: boids)
		{
			//UPDATE POSITION
			boid.pos[0] = boid.pos[0] + boid.vel[0];
			boid.pos[1] = boid.pos[1] + boid.vel[1];
			//CHECK POSITION
			if(boid.pos[0] < -border)
				boid.pos[0] += 2 * border + Game.width;
			else if(boid.pos[0] > Game.width + border)
				boid.pos[0] -= (2 * border + Game.width);
			if(boid.pos[1] < -border)
				boid.pos[1] += 2 * border + Game.height;
			else if(boid.pos[1] > Game.height + border)
				boid.pos[1] -= (2 * border + Game.height);
			//PRINT
			System.out.println(boid.color + ": " + Arrays.toString(boid.vel) + " " + (boid.direction / Math.PI));
		}
	}
	
	private ArrayList<Boid> getLocalBoids(Boid boid)
	{
		ArrayList<Boid> localBoids = new ArrayList<Boid>();
		for(Boid b : boids)
		{
			double dist = Math.sqrt(Math.pow(boid.pos[0] - b.pos[0],2) + Math.pow(boid.pos[1] - b.pos[1],2));
			if(dist <= range && b != boid)
				localBoids.add(b);
		}
		return localBoids;
	}
	
	private double[] getRepel(Boid boid, ArrayList<Boid> localBoids)
	{
		double repelX = 0, repelY = 0;
		for(Boid b : localBoids)
		{
			double repelForce = repelFunction(boid.pos, b.pos);
			double repelDirection = getAngle(b.pos, boid.pos);
			double[] repel = convert(repelForce, repelDirection, new double[] {0,0});
			repelX += repel[0];
			repelY += repel[1];
		}
		return new double[] {repelX, repelY};
	}
	
	private double repelFunction(double[] pos1, double[] pos2)
	{
		double dist = distance(pos1,pos2);
		return repelConst / dist;
	}
	
	private double[] velocityMatch(Boid boid, ArrayList<Boid> localBoids)
	{
		double[] avgVel = getAvgVelocity(localBoids);
		double[] diff = {avgVel[0] - boid.vel[0], avgVel[1] - boid.vel[1]};
		return new double[] {diff[0] * velMatchConst, diff[1] * velMatchConst};
	}
	
	private double[] cohesion(Boid boid, ArrayList<Boid> localBoids)
	{
		double[] avgPos = getAvgPosition(localBoids);
		double[] diff = {avgPos[0] - boid.pos[0], avgPos[1] - boid.pos[1]};
		double[] unitVector = getUnitVector(diff);
		return new double[] {unitVector[0] * cohesionConst, unitVector[1] * cohesionConst};
	}
	
	private double getAvgDirection(ArrayList<Boid> boids)
	{
		double directionSum = 0;
		for(Boid b : boids)
		{
			directionSum += b.direction;
		}
		return directionSum / boids.size();
	}
	
	private double[] getAvgPosition(ArrayList<Boid> boids)
	{
		double x = 0, y = 0;
		for(Boid b : boids)
		{
			x += b.pos[0];
			y += b.pos[1];
		}
		return new double[] {x / boids.size(), y / boids.size()};
	}
	
	private double[] getAvgVelocity(ArrayList<Boid> boids)
	{
		double x = 0, y = 0;
		for(Boid b: boids)
		{
			x += b.vel[0];
			y += b.vel[1];
		}
		return new double[] {x / boids.size(), y / boids.size()};
	}
		
	
	public void render()
	{
		BufferStrategy strat = Window.getFrame().getBufferStrategy();
		if(strat == null)
		{
			Window.getFrame().createBufferStrategy(10);
			return;
		}
		Graphics g = strat.getDrawGraphics();
		Color bg = new Color(61, 61, 61);
		g.setColor(bg);
		g.fillRect(0, 0, width, height);
		for (Boid boid : boids)
		{
//			g.setColor(Color.blue);
//			g.fillRect(round(boid.pos[0]),round(boid.pos[1]),5,5);
			int[][] triCords = roundArr2(getTriCords(boid));
			g.setColor(boid.color);
			g.drawPolygon(triCords[0], triCords[1], 3);
		}
		g.dispose();
		strat.show();
	}
	private double[] convert(double r, double theta, double[] orgin)
	{
		double x = r * Math.cos(theta);
		double y = r * Math.sin(theta);
		return new double[] {x + orgin[0], y + orgin[1]};
	}
	private double getAngle(double[] pos1) //angle FROM pos1 TO pos2
	{
		double a = Math.atan(Math.abs(pos1[1] - 0) / Math.abs(pos1[0] - 0));
		if(0 - pos1[0] >= 0)
		{
			if(0 - pos1[1] >= 0)
				return a;
			else
				return a + Math.PI * 1.5;
		}
		else
		{
			if(0 - pos1[1] >= 0)
				return a + Math.PI / 2;
			else
				return a + Math.PI;
		}
	}
	private double getAngle(double[] pos1, double[] pos2) //angle FROM pos1 TO pos2
	{
		double a = Math.atan(Math.abs(pos1[1] - pos2[1]) / Math.abs(pos1[0] - pos2[0]));
		if(pos2[0] - pos1[0] >= 0)
		{
			if(pos2[1] - pos1[1] >= 0)
				return a;
			else
				return a + Math.PI * 1.5;
		}
		else
		{
			if(pos2[1] - pos1[1] >= 0)
				return a + Math.PI / 2;
			else
				return a + Math.PI;
		}
	}
	private double[][] getTriCords(Boid boid)
	{
		double dist = Math.sqrt(Math.pow(boid.width/2,2) + Math.pow(boid.height/2,2));
		double A = Math.atan(boid.width / boid.height);
		double angle = Math.PI - A;
		double[] point1 = convert(dist, checkAngle(boid.direction + angle), boid.pos);
		double[] point2 = convert(dist, checkAngle(boid.direction - angle), boid.pos);
		double[] head = convert(boid.height / 2, boid.direction, boid.pos);
		return new double[][] {{head[0],point1[0],point2[0]},{head[1],point1[1],point2[1]}};
	}
	private int round(double num)
	{
		return (int)Math.round(num);
	}
	private int[] roundArr(double[] nums)
	{
		int[] arr = new int[nums.length];
		for(int i = 0; i < nums.length; i++)
		{
			arr[i] = round(nums[i]);
		}
		return arr;
	}
	private int[][] roundArr2(double[][] nums)
	{
		int[][] arr = new int[nums.length][];
		for(int i = 0; i < nums.length; i++)
		{
			arr[i] = new int[nums[i].length];
			arr[i] = roundArr(nums[i]);
		}
		return arr;
	}
	private double checkAngle(double angle)
	{
		if(angle >= 2 * Math.PI)
			return angle - 2 * Math.PI;
		if(angle < 0)
			return angle + 2 * Math.PI;
		return angle;
	}
	private double distance(double[] pos1, double[] pos2)
	{
		return Math.sqrt(Math.pow(pos2[0] - pos1[0],2) + Math.pow(pos2[1] - pos1[1],2));
	}
	private double[] getUnitVector(double[] pos)
	{
		double magnitude = distance(pos, new double[] {0,0});
		return new double[] {pos[0] / magnitude, pos[1] / magnitude};
	}
	private double randomDouble(double min, double max)
	{
		//double randomValue = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
		Random r = new Random();
		return min + (max - min) * r.nextDouble();
	}
}
