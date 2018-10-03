package D6Dice;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class D6DiceMain implements ActionListener // NO_UCD (unused code) cos root main of program
{
	private int m_iNumber;
	
	public D6DiceMain()
	{
		m_iNumber = 2;
		m_zAdvantage = false;
		m_zAddWounds = false;
		m_zDefend = false;
	}
	
	private D6DiceOpponentModel[] m_aopponents;
	private boolean m_zAdvantage;
	private boolean m_zAddWounds;
	private boolean m_zDefend;
	private D6DiceResultModel m_result;
	
	public void create()
	{
		m_aopponents = new D6DiceOpponentModel[m_iNumber];
		int[] aiFields = {6, 2, 4, 9, 1};
		
		for (int iIndex = 0; iIndex<m_iNumber; iIndex++)
		{
			m_aopponents[iIndex] = new D6DiceOpponentModel();
			m_aopponents[iIndex].create(aiFields);
		}
		
		m_result = new D6DiceResultModel(m_iNumber);		
	}
	
	public D6DiceView createPanel()
	{
		D6DiceOpponentChange[] aopponentData = new D6DiceOpponentChange[m_iNumber];
		
		for (int iIndex = 0; iIndex<m_iNumber; iIndex++)
		{
			aopponentData[iIndex] = new D6DiceOpponentChange(m_aopponents[iIndex]);
		}
		m_aopponents = aopponentData;
		
		D6DiceResultChange result = new D6DiceResultChange(m_result);
		m_result = result;
		
		D6DiceView viewPanel = new D6DiceView();
		viewPanel.createPanel(aopponentData, m_zAdvantage, m_zAddWounds, m_zDefend, result);
		viewPanel.addActionListener(this);
		
		return viewPanel;
	}
	
	@Override
	public void actionPerformed(ActionEvent event) 
	{
		String scActionCommand = event.getActionCommand();
		
		// From the view buttons
		
		if (scActionCommand.equals("GO"))
		{
			m_result.reset(m_iNumber);
			calculate();
			return;
		}
		if (scActionCommand.equals("SIMULATE"))
		{
			D6DiceResultChange calculateResult = new D6DiceResultChange(m_result);	
			D6DiceResultChange simulateResult = new D6DiceResultChange(m_iNumber);
			
			Object oSource = event.getSource();
			if (oSource instanceof D6DiceView)
			{
				D6DiceView panelView = (D6DiceView)oSource;
				panelView.addSimulation(calculateResult, simulateResult);
			}
			
			m_result = calculateResult;
			
			simulate(simulateResult);
			
			return;
		}
		
		// From the view check boxes
		
		if (scActionCommand.equals("ADD"))
		{
			D6DiceResultChange result = new D6DiceResultChange(m_iNumber+1);			
			D6DiceOpponentModel[] aopponentsNew = new D6DiceOpponentModel[m_iNumber+1];
			D6DiceOpponentChange opponentData = new D6DiceOpponentChange(m_aopponents[m_iNumber-1]);
			
			for (int iIndex = 0; iIndex<m_iNumber; iIndex++) aopponentsNew[iIndex] = m_aopponents[iIndex];
			aopponentsNew[m_iNumber] = opponentData;
			
			Object oSource = event.getSource();
			if (oSource instanceof D6DiceView)
			{
				D6DiceView panelView = (D6DiceView)oSource;
				panelView.addOpponent(opponentData, result);
			}
			
			m_iNumber += 1;
			m_aopponents = aopponentsNew;
			m_result = result;
			
			return;
		}
		if (scActionCommand.equals("REMOVE"))
		{
			if (2==m_iNumber) return;
			
			D6DiceResultChange result = new D6DiceResultChange(m_iNumber-1);
			D6DiceOpponentModel[] aopponentsNew = new D6DiceOpponentModel[m_iNumber-1];
			
			for (int iIndex = 0; iIndex<m_iNumber-1; iIndex++) aopponentsNew[iIndex] = m_aopponents[iIndex];
			
			Object oSource = event.getSource();
			if (oSource instanceof D6DiceView)
			{
				D6DiceView panelView = (D6DiceView)oSource;
				panelView.removeOpponent(m_aopponents[m_iNumber-1], result);
			}
			
			m_iNumber -= 1;
			m_aopponents = aopponentsNew;
			m_result = result;
			
			return;
		}
		
		if (scActionCommand.equals("MELEE"))
		{
			Boolean zState = (Boolean)event.getSource();
			m_zAdvantage =zState;
			return;
		}
		if (scActionCommand.equals("RANGED"))
		{
			Boolean zState = (Boolean)event.getSource();
			m_zAddWounds = zState;
			return;
		}
		if (scActionCommand.equals("DEFEND"))
		{
			Boolean zState = (Boolean)event.getSource();
			m_zDefend = zState;
			return;
		}
		
		// From the opponents
		
		if (scActionCommand.equals("OPPONENT"))
		{
			D6DiceOpponentControl controlOpponent = (D6DiceOpponentControl)event;
			controlOpponent.performAction();			
			return;
		}
		
		if (scActionCommand.equals("FIELD"))
		{
			D6DiceOpponentControl controlOpponent = (D6DiceOpponentControl)event;
			controlOpponent.performAction();			
			return;
		}
		
		// From the console
		
		if (scActionCommand.equals("HELP"))
		{
			D6DiceHelper.writeLine("GO - calculate");
			D6DiceHelper.writeLine("SIMULATE - simulate");
			D6DiceHelper.writeLine("ADD - increase number of opponents");
			D6DiceHelper.writeLine("REMOVE - remove opponent");
			D6DiceHelper.writeLine("MELEE - re/set INIT advantage to HIT");
			D6DiceHelper.writeLine("RANGED - re/set HIT increase to DAMAGE");
			D6DiceHelper.writeLine("DEFEND - re/set HIT only to prevent DAMAGE");
			D6DiceHelper.writeLine("SHOW - show opponent set");
			D6DiceHelper.writeLine("OPPONENT <n> <health> <init> <hit> <damage> <armour> - set OPPONENT <n> values");
			D6DiceHelper.writeLine("OPPONENT <n> FIELD <F> <v> - set OPPONENT <n> values");
			return;
		}
		
		if (scActionCommand.equals("SHOW"))
		{
			for (int iIndex = 0; iIndex<m_iNumber; iIndex++)
			{
				D6DiceHelper.writeLine(String.format("%d - %s", iIndex, m_aopponents[iIndex].toString()));
			}
			return;
		}
		
		// From the console as a string
		
		if (scActionCommand.equals("PARAMETERS"))
		{
			commandLine(event);
		}
		
		return;
	}
	
	private void commandLine(ActionEvent event)
	{
		String scCommand = (String)event.getSource();
		Scanner scannerCommand = new Scanner(scCommand);
		
		try
		{
			String scAction = scannerCommand.next();
			
			if (scAction.equals("MELEE"))
			{
				Boolean zState = scannerCommand.nextBoolean();
				actionPerformed(new ActionEvent(zState, 0, "MELEE"));
				return;
			}
			
			if (scAction.equals("RANGED"))
			{
				Boolean zState = scannerCommand.nextBoolean();
				actionPerformed(new ActionEvent(zState, 0, "RANGED"));
				return;
			}
			
			if (scAction.equals("DEFEND"))
			{
				Boolean zState = scannerCommand.nextBoolean();
				actionPerformed(new ActionEvent(zState, 0, "DEFEND"));
				return;
			}
			
			D6DiceOpponentModel opponentData = null;
			
			if (scAction.equals("OPPONENT"))
			{
				int iOpponent = scannerCommand.nextInt();
				opponentData = m_aopponents[iOpponent];
				if (scannerCommand.hasNext() && !scannerCommand.hasNextInt()) scAction = scannerCommand.next();
			}
			
			if (scAction.equals("OPPONENT"))
			{
				int[] aiFields = new int[5];
				for (int iIndex = D6DiceOpponentModel.HEALTH; iIndex<D6DiceOpponentModel._ALL; iIndex++)
					aiFields[iIndex] = scannerCommand.nextInt();
				actionPerformed(new D6DiceOpponentControl(opponentData, D6DiceOpponentModel.HEALTH, "OPPONENT", aiFields));
				return;
			}
			
			if (scAction.equals("HEALTH"))
			{
				int iValue = scannerCommand.nextInt();
				actionPerformed(D6DiceOpponentControl.eventField(event, opponentData, D6DiceOpponentModel.HEALTH, "FIELD", iValue));
				return;
			}
			
			if (scAction.equals("INIT"))
			{
				int iValue = scannerCommand.nextInt();
				actionPerformed(D6DiceOpponentControl.eventField(event, opponentData, D6DiceOpponentModel.INIT, "FIELD", iValue));
				return;
			}
			
			if (scAction.equals("HIT"))
			{
				int iValue = scannerCommand.nextInt();
				actionPerformed(D6DiceOpponentControl.eventField(event, opponentData, D6DiceOpponentModel.HIT, "FIELD", iValue));
				return;
			}
			
			if (scAction.equals("DAMAGE"))
			{
				int iValue = scannerCommand.nextInt();
				actionPerformed(D6DiceOpponentControl.eventField(event, opponentData, D6DiceOpponentModel.DAMAGE, "FIELD", iValue));
				return;
			}
			
			if (scAction.equals("ARMOUR"))
			{
				int iValue = scannerCommand.nextInt();
				actionPerformed(D6DiceOpponentControl.eventField(event, opponentData, D6DiceOpponentModel.ARMOUR, "FIELD", iValue));
				return;
			}
		}
		catch (InputMismatchException x)
		{
		}
		catch (NoSuchElementException x)
		{
		}
		finally
		{
			scannerCommand.close();
		}
		
		return;
	}
	
	public void calculate()
	{
		D6DiceConflictModel conflict = new D6DiceConflictModel();
		conflict.calculate(m_aopponents, m_zAdvantage, m_zAddWounds, m_zDefend, m_result);
		return;
	}

	public void simulate(D6DiceResultModel simulateResult)
	{
		D6DiceConflictModel conflict = new D6DiceConflictModel(1000);
		conflict.simulate(m_aopponents, m_zAdvantage, m_zAddWounds, m_zDefend, simulateResult);
		return;
	}
	
	public static void main(String[] args) 
	{
		D6DiceMain main = new D6DiceMain();
		main.create();
		
		// Check if want to try anything from console
		if (D6DiceHelper.createConsole(main, args)) return;
		
		// Open view
		D6DiceView panelView = main.createPanel();
		D6DiceHelper.frameView("Fill in opponents and simulate", panelView);
	}

}
