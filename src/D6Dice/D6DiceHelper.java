package D6Dice;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

class D6DiceHelper 
{
	static void frameView(final String scFrameTitle, final JComponent componentPanel)
	{
		Runnable startLater = new Runnable()
		{
			@Override
			public void run()
			{
				JFrame frameDice = new JFrame(scFrameTitle);
				frameDice.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				
				frameDice.add(componentPanel);
				frameDice.pack();
				
				frameDice.setLocation(new Point(200, 200));
				frameDice.setVisible(true);
			}
		};
		
		SwingUtilities.invokeLater(startLater);
	}
	
	private static String readLine(final BufferedReader readerSystem, final String scPrompt, final int iSeconds)
	{
		Callable<String> callWithTimeout = new Callable<String>()
		{
			@Override
			public String call() throws IOException 
			{
				String scReturn;

				System.out.print(scPrompt);
				
				try 
				{
					while (!readerSystem.ready()) Thread.sleep(100);
					scReturn = readerSystem.readLine();
				} 
				catch (InterruptedException e) 
				{
					return null;
				}
				
				return scReturn;
			}			
		};
		
		ExecutorService threadExecute = Executors.newSingleThreadExecutor();
		Future<String> futureReturn = threadExecute.submit(callWithTimeout);
		
		String scReturn = "Q";
		
		try 
		{
			scReturn = futureReturn.get(iSeconds, TimeUnit.SECONDS);
		} 
		catch (InterruptedException e) 
		{
		} 
		catch (ExecutionException e) 
		{
		} 
		catch (TimeoutException e) 
		{
			scReturn = "W";
		}
		finally
		{
			threadExecute.shutdownNow();
		}
		
		return scReturn;
	}
	
	static void writeLine(String scFormat)
	{
		System.out.println(scFormat);
		return;
	}
	
	static boolean createConsole(ActionListener listener, String[] ascArguments)
	{
		// Console does not exist in some cases so use System.in
		BufferedReader readerSystem = new BufferedReader(new InputStreamReader(System.in));
		
		writeLine("Hit 'W' to display view, 'Q' to quit, or");
		listener.actionPerformed(new ActionEvent(readerSystem, 0, "HELP"));
		
		for (int iTimeout = 5;; iTimeout = 300)
		{
			String scAction = readLine(readerSystem, "> ", iTimeout);
			scAction = scAction.toUpperCase();
			if (1>scAction.length()) continue;
		
			if (scAction.startsWith("W")) return false; // On return opens view
			if (scAction.startsWith("Q")) break; // break to return true and quit

			if (0>scAction.indexOf(' '))
			{
				// single word action
				listener.actionPerformed(new ActionEvent(readerSystem, 0, scAction));
				continue;
			}
			
			// Parameterized action - string is the source
			listener.actionPerformed(new ActionEvent(scAction, 0, "PARAMETERS"));
		}
		
		writeLine("... quit");
		
		return true;
	}
	


}
