package D6Dice;

class D6DiceConflictModel 
{
	private int m_iIterations;
	
	D6DiceConflictModel()
	{
		m_iIterations = 10000;
	}
	
	D6DiceConflictModel(int iIterations)
	{
		m_iIterations = iIterations;
	}
	
	void calculate(D6DiceOpponentModel[] m_aopponents, final boolean zIncludeINIT, final boolean zIncludeHIT, final boolean zDefend, final D6DiceResultModel result)
	{
		final D6DiceOpponentCalculate[] aopponents = D6DiceOpponentCalculate.create(m_aopponents);
		Thread threadCalculate = new Thread()
		{
			@Override
			public void run() 
			{
				calculateRun(aopponents, zIncludeINIT, zIncludeHIT, zDefend, result);
				return;
			}			
		};
		
		threadCalculate.start();
	}
	
	private void calculateRun(D6DiceOpponentCalculate[] aopponents, boolean zIncludeINIT, boolean zIncludeHIT, boolean zDefend, D6DiceResultModel result) 
	{
		// Create range of INIT successes for each component
		int iNumberOpponents = aopponents.length;
		
		// INIT dice pools when calculating for melee
		for (int iIndex = 0; iIndex<iNumberOpponents; iIndex++)
		{
			if (zIncludeINIT)
				aopponents[iIndex].calculateINIT(result, iIndex);
			System.out.println();
		}
		
		// INIT dice chance when calculating for melee or '1.0' when for ranged
		for (int iIndex = 0; iIndex<iNumberOpponents; iIndex++)
		{
			if (zIncludeINIT)
				aopponents[iIndex].calculateAdvantage(aopponents, iIndex, result, iIndex);
			else
				aopponents[iIndex].noAdvantage(aopponents, iIndex, result, iIndex);
			System.out.println();
		}
		
		// HIT dice pools including disadvantage for multiple opponents and advantage from INIT
		for (int iIndex = 0; iIndex<iNumberOpponents; iIndex++)
		{
			// First is outnumbered by others {when 3 opponents iOpponents = 1 and etc.,. but only for first opponent}
			int iOpponents = 0==iIndex?iNumberOpponents-2:0;
			aopponents[iIndex].calculateHIT(iOpponents, result, iIndex);
			System.out.println();
		}
		
		if (zDefend)
		{
			// If only defending
			aopponents[0].calculateDefend(iNumberOpponents, result, 0);
			System.out.println();			
		}
		
		// Win dice chance from comparing HIT pools
		for (int iIndex = zDefend?1:0; iIndex<iNumberOpponents; iIndex++)
		{
			// When zDefend first opponent cannot win
			aopponents[iIndex].calculateWin(aopponents, iIndex, result, iIndex);
			System.out.println();
		}
		
		// Calculate stalemate {no wins} and re-scale successes across whole range
		for (int iIndex = 0; iIndex<iNumberOpponents; iIndex++)
		{
			//m_aopponents[iIndex].calculateStalemate(m_aopponents, iIndex, result, iIndex);
			//System.out.println();
		}
			
		for (int iIndex = zDefend?1:0; iIndex<iNumberOpponents; iIndex++)
		{
			// When zDefend first opponent does no damage
			if (zIncludeHIT)
				aopponents[iIndex].calculateDAMAGEWithHIT(result, iIndex);
			else
				aopponents[iIndex].calculateDAMAGE(result, iIndex);
			System.out.println();
		}
		
		for (int iIndex = 0; iIndex<iNumberOpponents; iIndex++)
		{
			aopponents[iIndex].calculateWounds(aopponents, iIndex, result, iIndex);
			System.out.println();
		}
		
		float fSmallestCutoff = 1000.0f;
		
		for (int iIndex = 0; iIndex<iNumberOpponents; iIndex++)
		{
			// First step for combination calculate damage could be done 
			aopponents[iIndex].calculateHealth(aopponents, iIndex);
			aopponents[iIndex].calculateCombined_1(aopponents, iIndex);
			System.out.println();
		}
		for (int iIndex = 0; iIndex<iNumberOpponents; iIndex++)
		{
			// Second step for combination calculate damage combined 
			aopponents[iIndex].calculateCombined_O(aopponents, iIndex);
			float fCutoff = aopponents[iIndex].calculateCutoff(aopponents, iIndex);
			if (fCutoff<fSmallestCutoff) fSmallestCutoff = fCutoff;
			System.out.println();
		}		
		for (int iIndex = 0; iIndex<iNumberOpponents; iIndex++)
		{
			// Third step for combination calculate total chance less than smallest cutoff 
			aopponents[iIndex].calculateChance(aopponents, iIndex, fSmallestCutoff);
			System.out.println();
		}		
		
		for (int iIndex = 0; iIndex<iNumberOpponents; iIndex++)
		{
			aopponents[iIndex].calculateAverages(aopponents, iIndex, result, iIndex);
			System.out.println();
		}
		
		return;
	}
	
	void simulate(D6DiceOpponentModel[] aopponentsSimulate, final boolean zIncludeINIT, final boolean zIncludeHIT, final boolean zDefend, final D6DiceResultModel result)
	{
		final D6DiceOpponentSimulate[] aopponents = D6DiceOpponentSimulate.create(aopponentsSimulate);
		
		Thread threadSimulate = new Thread()
		{
			@Override
			public void run() 
			{
				simulateRun(aopponents, zIncludeINIT, zIncludeHIT, zDefend, result);
				return;
			}			
		};
		
		threadSimulate.start();
	}
	
	private void simulateRun(final D6DiceOpponentSimulate[] aopponents, final boolean zIncludeINIT, final boolean zIncludeHIT, final boolean zDefend, final D6DiceResultModel result)
	{
		for (int iIterate = 0; iIterate<m_iIterations; iIterate++)
		{
			for (int iIndex = 0; iIndex<aopponents.length; iIndex++)
				aopponents[iIndex].reset();
			
			while (simulateOne(aopponents, zIncludeINIT, zIncludeHIT, zDefend, result))
			{
				try 
				{
					Thread.sleep(10);
				} 
				catch (InterruptedException x) 
				{
				}
			}
		}
		return;
	}
	
	// There is a tiny error where the last opponent can 'steal' a victory from an earlier one - so randomise who is calculated first
	private int m_iStart;
	
	private boolean simulateOne(final D6DiceOpponentSimulate[] aopponents, final boolean zIncludeINIT, final boolean zIncludeHIT, final boolean zDefend, final D6DiceResultModel result)
	{
		int iNumberOpponents = aopponents.length;
		
		// Roll dice for init, but only if relevant
		for (int iIndex = 0; iIndex<iNumberOpponents; iIndex++)
		{
			if (!aopponents[iIndex].isHealthy()) continue;
			if (zIncludeINIT)
				aopponents[iIndex].calculateINIT(result, iIndex);
			System.out.println();
		}
		
		// Include init but only if relevant
		for (int iIndex = 0; iIndex<iNumberOpponents; iIndex++)
		{
			if (!aopponents[iIndex].isHealthy()) continue;
			if (zIncludeINIT)
				aopponents[iIndex].calculateAdvantage(aopponents, iIndex, result, iIndex);
			else
				aopponents[iIndex].noAdvantage(aopponents, iIndex, result, iIndex);
			System.out.println();
		}
		
		// HIT dice pools including disadvantage for multiple opponents and advantage from INIT
		for (int iIndex = 0; iIndex<iNumberOpponents; iIndex++)
		{
			if (!aopponents[iIndex].isHealthy()) continue;
			// First is outnumbered by others {when 3 opponents iOpponents = 1 and etc.,. but only for first opponent}
			int iOpponents = 0==iIndex?iNumberOpponents-2:0;
			aopponents[iIndex].calculateHIT(iOpponents, result, iIndex);
			System.out.println();
		}
		
		if (zDefend)
		{
			// If only defending
			aopponents[0].calculateDefend(iNumberOpponents, result, 0);
			System.out.println();			
		}
		
		// Win dice chance from comparing HIT pools
		for (int iIndex = zDefend?1:0; iIndex<iNumberOpponents; iIndex++)
		{
			if (!aopponents[iIndex].isHealthy()) continue;
			// When zDefend first opponent cannot win
			aopponents[iIndex].calculateWin(aopponents, iIndex, result, iIndex);
			System.out.println();
		}
		
		for (int iIndex = zDefend?1:0; iIndex<iNumberOpponents; iIndex++)
		{
			if (!aopponents[iIndex].isHealthy()) continue;
			// When zDefend first opponent does no damage
			if (zIncludeHIT)
				aopponents[iIndex].calculateDAMAGEWithHIT(result, iIndex);
			else
				aopponents[iIndex].calculateDAMAGE(result, iIndex);
			System.out.println();
		}
		
		for (int iIndex = 0; iIndex<iNumberOpponents; iIndex++)
		{
			if (!aopponents[iIndex].isHealthy()) continue;
			aopponents[iIndex].calculateWounds(aopponents, iIndex, result, iIndex);
			System.out.println();
		}
		
		m_iStart = (m_iStart + 1) % iNumberOpponents;
		
		for (int iIndex = 0; iIndex<iNumberOpponents; iIndex++)
		{
			int iCheck = (iIndex + m_iStart) % iNumberOpponents;
			if (aopponents[iCheck].calculateAverages(aopponents, iCheck, result, iCheck)) return false;
			System.out.println();
		}
		
		return true;
	}
	
	public static void main(String[] args) 
	{
		int[] aiFieldsA = {5, 1, 3, 6, 2};
		int[] aiFieldsB = {5, 1, 3, 8, 2};
		int[] aiFieldsC = {5, 1, 1, 6, 2};
		
		// Health, Init, Hit, Damage, Armour
		D6DiceOpponentModel opponentA = new D6DiceOpponentModel();
		opponentA.create(aiFieldsA);
		D6DiceOpponentModel opponentB = new D6DiceOpponentModel();
		opponentB.create(aiFieldsB);
		D6DiceOpponentModel opponentC = new D6DiceOpponentModel();
		opponentC.create(aiFieldsC);
		
		D6DiceOpponentModel[] aOpponents = {opponentA, opponentB};
		//D6DiceOpponentModel[] aOpponents = {opponentA, opponentB, opponentC};
		
		//D6DiceResultModel result = new D6DiceResultModel(aOpponents.length);
		D6DiceResultChange result = new D6DiceResultChange(aOpponents.length);
		
		D6DiceResult show = new D6DiceResult();
		show.createPanel(result);
		D6DiceHelper.frameView("Fill in opponents and simulate", show);

		D6DiceConflictModel main = new D6DiceConflictModel();
		main.calculate(aOpponents, false, false, false, result);		
	}
}
