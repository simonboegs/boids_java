import java.util.TimerTask;
public class TimeTick extends TimerTask {
	private Game game;
	public TimeTick(Game game)
	{
		this.game = game;
	}
	public void run()
	{
		game.update();
		game.render();
		if(!game.running)
		{
			game.timer.cancel();
		}
	}
}
