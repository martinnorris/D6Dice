package D6Dice;

class D6DiceModel
{
	private D6DiceSuccessModel[] m_aSuccesses;
	
	private static final int D6_RESULTS = 3; // fail, 1 success, 2 successes
	private static final int FAIL = 0;
	
	public D6DiceModel()
	{
		m_aSuccesses = new D6DiceSuccessModel[D6_RESULTS];
		m_aSuccesses[FAIL] = new D6DiceSuccessModel(0, 24f / 36f); // 2,3,4 or 5 follwed by anything
		m_aSuccesses[1] = new D6DiceSuccessModel(1, 10f / 36f ); // 1 followed by anything or 6 followed by 2,3,4,5
		m_aSuccesses[2] = new D6DiceSuccessModel(2, 2f / 36f ); // 6 followed by 1 or 6 - do not re-roll 6 again
	}
	
	private D6DiceModel(D6DiceSuccessModel[] aSuccesses)
	{
		m_aSuccesses = aSuccesses;
	}
	
	public static D6DiceModel getEmpty()
	{
		D6DiceSuccessModel[] aSuccesses = new D6DiceSuccessModel[D6_RESULTS];
		aSuccesses[FAIL] = new D6DiceSuccessModel(0, 0.0f);
		aSuccesses[1] = new D6DiceSuccessModel(1, 0.0f);
		aSuccesses[2] = new D6DiceSuccessModel(2, 0.0f);
		return new D6DiceModel(aSuccesses);
	}
	
	public static D6DiceModel get0()
	{
		D6DiceSuccessModel dice0 = new D6DiceSuccessModel(0, 1.0f);
		D6DiceSuccessModel[] aSuccesses = {dice0};
		return new D6DiceModel(aSuccesses);
	}
	
	public static D6DiceModel get1()
	{
		D6DiceSuccessModel dice0 = new D6DiceSuccessModel(0, 0.0f);
		D6DiceSuccessModel dice1 = new D6DiceSuccessModel(1, 1.0f);
		D6DiceSuccessModel[] aSuccesses = {dice0, dice1};
		return new D6DiceModel(aSuccesses);
	}
	
	private int getSize()
	{
		return m_aSuccesses.length;
	}
	
	private D6DiceSuccessModel getSuccesses(int iIndex)
	{
		return m_aSuccesses[iIndex];
	}
	
	/// Combine the possible successes from current dice set adding another dice set
	
	public D6DiceModel addDice(D6DiceModel dice)
	{
		int iResults = getSize() + dice.getSize() - 1;
		D6DiceSuccessModel[] aSuccessesCalulated = new D6DiceSuccessModel[iResults];
		
		for (int iIndexO = 0; iIndexO<getSize(); iIndexO++)
		{
			for (int iIndexI = 0; iIndexI<dice.getSize(); iIndexI++)
			{
				D6DiceSuccessModel successes = m_aSuccesses[iIndexO].addSuccess(dice.getSuccesses(iIndexI));
				int iSuccesses = successes.getSuccesses();
				
				if (null==aSuccessesCalulated[iSuccesses])
					aSuccessesCalulated[iSuccesses] = successes;
				else
					aSuccessesCalulated[iSuccesses].addChance(successes.getChance());
			}			
		}
		
		for (int iIndex = 0; iIndex<iResults; iIndex++)
		{
			if (null==aSuccessesCalulated[iIndex]) aSuccessesCalulated[iIndex] = new D6DiceSuccessModel(iIndex, 0.0f);		
		}
		
		return new D6DiceModel(aSuccessesCalulated);
	}
	
	/// Add dice chance but number successes only from dice added
	public D6DiceModel addDiceTo(D6DiceModel dice)
	{
		int iResults = Math.max(getSize(), dice.getSize());
		D6DiceSuccessModel[] aSuccessesCalulated = new D6DiceSuccessModel[iResults];
		
		for (int iIndexO = 0; iIndexO<getSize(); iIndexO++)
		{
			for (int iIndexI = 0; iIndexI<dice.getSize(); iIndexI++)
			{
				D6DiceSuccessModel successes = m_aSuccesses[iIndexO].addSuccessTo(dice.getSuccesses(iIndexI));
				int iSuccesses = successes.getSuccesses();
				
				if (null==aSuccessesCalulated[iSuccesses])
					aSuccessesCalulated[iSuccesses] = successes;
				else
					aSuccessesCalulated[iSuccesses].addChance(successes.getChance());
			}			
		}
		
		for (int iIndex = 0; iIndex<iResults; iIndex++)
		{
			if (null==aSuccessesCalulated[iIndex]) aSuccessesCalulated[iIndex] = new D6DiceSuccessModel(iIndex, 0.0f);		
		}
		
		return new D6DiceModel(aSuccessesCalulated);
	}
	
	/// Add dice chance but zero result when source has 0 successes
	public D6DiceModel addDiceZero(D6DiceModel dice)
	{
		int iResults = getSize() + dice.getSize() - 1;
		D6DiceSuccessModel[] aSuccessesCalulated = new D6DiceSuccessModel[iResults];
		
		for (int iIndexO = 0; iIndexO<getSize(); iIndexO++)
		{
			for (int iIndexI = 0; iIndexI<dice.getSize(); iIndexI++)
			{
				D6DiceSuccessModel successes = m_aSuccesses[iIndexO].addSuccessZero(dice.getSuccesses(iIndexI));
				int iSuccesses = successes.getSuccesses();
				
				if (null==aSuccessesCalulated[iSuccesses])
					aSuccessesCalulated[iSuccesses] = successes;
				else
					aSuccessesCalulated[iSuccesses].addChance(successes.getChance());
			}	
		}
		
		for (int iIndex = 0; iIndex<iResults; iIndex++)
		{
			if (null==aSuccessesCalulated[iIndex]) aSuccessesCalulated[iIndex] = new D6DiceSuccessModel(iIndex, 0.0f);		
		}
		
		return new D6DiceModel(aSuccessesCalulated);
	}
	
	public D6DiceModel cancelDice(D6DiceModel dice)
	{
		int iResults = getSize();
		D6DiceSuccessModel[] aSuccessesCalulated = new D6DiceSuccessModel[iResults];
		
		for (int iIndexO = 0; iIndexO<iResults; iIndexO++)
		{
			for (int iIndexI = 0; iIndexI<dice.getSize(); iIndexI++)
			{
				D6DiceSuccessModel successes = m_aSuccesses[iIndexO].reduceSuccess(dice.getSuccesses(iIndexI));
				int iSuccesses = successes.getSuccesses();
				
				if (0>iSuccesses) iSuccesses = 0;
				
				if (null==aSuccessesCalulated[iSuccesses])
					aSuccessesCalulated[iSuccesses] = new D6DiceSuccessModel(iSuccesses, successes.getChance());
				else
					aSuccessesCalulated[iSuccesses].addChance(successes.getChance());
			}			
		}
		
		return new D6DiceModel(aSuccessesCalulated);
	}
	
	public D6DiceModel addChance(D6DiceModel dice)
	{
		int iResults = Math.max(getSize(), dice.getSize());
		D6DiceSuccessModel[] aSuccessesCalulated = new D6DiceSuccessModel[iResults];
	
		for (int iIndex = 0; iIndex<iResults; iIndex++)
		{
			if (getSize()<=iIndex)
			{
				D6DiceSuccessModel successes = dice.getSuccesses(iIndex);
				aSuccessesCalulated[iIndex] = new D6DiceSuccessModel(iIndex, successes.getChance());
				continue;
			}
			
			if (dice.getSize()<=iIndex)
			{
				D6DiceSuccessModel successes = getSuccesses(iIndex);
				aSuccessesCalulated[iIndex] = new D6DiceSuccessModel(iIndex, successes.getChance());
				continue;
			}
			
			D6DiceSuccessModel successes = dice.getSuccesses(iIndex);
			float fChance = m_aSuccesses[iIndex].getChance() + successes.getChance();
			aSuccessesCalulated[iIndex] = new D6DiceSuccessModel(iIndex, fChance);
		}
		
		return new D6DiceModel(aSuccessesCalulated);
	}
	
	public D6DiceModel divideChance(int iNumber)
	{
		int iResults = getSize();
		D6DiceSuccessModel[] aSuccessesCalulated = new D6DiceSuccessModel[iResults];
	
		for (int iIndex = 0; iIndex<iResults; iIndex++)
		{
			D6DiceSuccessModel successes = m_aSuccesses[iIndex];
			aSuccessesCalulated[iIndex] = new D6DiceSuccessModel(iIndex, successes.getChance()/iNumber);
		}
		
		return new D6DiceModel(aSuccessesCalulated);		
	}
	
	public D6DiceModel rationaliseDice(int iMaximumSuccesses) 
	{
		int iIndex = getSize()-1;
		float fRemainder = 0.0f;
		D6DiceSuccessModel[] aSuccessesCalulated = null;
		
		for (; iIndex>=0; iIndex--)
		{
			D6DiceSuccessModel successes = getSuccesses(iIndex);
			int iSuccesses = successes.getSuccesses();

			fRemainder += successes.getChance();
			if (iMaximumSuccesses<iSuccesses) continue;
			
			if (null==aSuccessesCalulated)
			{
				aSuccessesCalulated = new D6DiceSuccessModel[iIndex+1];
				aSuccessesCalulated[iIndex] = new D6DiceSuccessModel(iIndex, fRemainder);
			}
			else
			{
				aSuccessesCalulated[iIndex] = successes;				
			}
		}

		return new D6DiceModel(aSuccessesCalulated);
	}

	public D6DiceModel rationaliseDice(float fMinimumChance) 
	{
		int iIndex = getSize()-1;
		float fRemainder = 0.0f;
		D6DiceSuccessModel[] aSuccessesCalulated = null;
		
		for (; iIndex>=0; iIndex--)
		{
			D6DiceSuccessModel successes = getSuccesses(iIndex);

			fRemainder += successes.getChance();
			if (fMinimumChance>successes.getChance()) continue;
			
			if (null==aSuccessesCalulated)
			{
				aSuccessesCalulated = new D6DiceSuccessModel[iIndex+1];
				aSuccessesCalulated[iIndex] = new D6DiceSuccessModel(iIndex, fRemainder);
			}
			else
			{
				aSuccessesCalulated[iIndex] = successes;				
			}
			
			fMinimumChance = 0.0f;
		}
		
		if (null==aSuccessesCalulated) return get0();
				
		return new D6DiceModel(aSuccessesCalulated);
	}
	
	public D6DiceModel cancelSuccesses(int iMinimum)
	{
		int iResults = getSize();
		
		if (iResults<=iMinimum+1)
		{
			return get0();
		}
		
		D6DiceSuccessModel[] aSuccessesCalulated = new D6DiceSuccessModel[iResults-iMinimum];

		for (int iIndex = 0; iIndex<iResults; iIndex++)
		{
			D6DiceSuccessModel successes = m_aSuccesses[iIndex];

			int iSuccesses = iIndex - iMinimum;
			if (0>iSuccesses) iSuccesses = 0;
			
			if (null==aSuccessesCalulated[iSuccesses])
				aSuccessesCalulated[iSuccesses] = new D6DiceSuccessModel(iSuccesses, successes.getChance());
			else
				aSuccessesCalulated[iSuccesses].addChance(successes.getChance());				
		}
		
		return new D6DiceModel(aSuccessesCalulated);		
	}

	public float averageSuccesses() 
	{
		int iResults = getSize();
		
		if (1>iResults)
		{
			return 0.0f;
		}
		
		if (2>iResults)
		{
			return m_aSuccesses[1].getChance();
		}
		
		float fAverage = 0.000f;
		
		for (int iIndex = 1; iIndex<iResults; iIndex++)
		{
			fAverage += iIndex * m_aSuccesses[iIndex].getChance();
		}
				
		return fAverage;		
	}
	
	public float sumSuccesses() 
	{
		int iResults = getSize();
		
		if (2>iResults)
		{
			// First value is for 0 successes
			return 0.0f;
		}
		
		float fSum = 0.000f;
		
		for (int iIndex = 1; iIndex<iResults; iIndex++)
		{
			fSum += m_aSuccesses[iIndex].getChance();
		}
				
		return fSum;		
	}
	
	public float weightSuccesses(int iAgainst)
	{
		float fTotal = 0.0f;
		
		for (int iIndex = 1; iIndex<getSize(); iIndex++)
		{
			float fWeightedWounds = iIndex * m_aSuccesses[iIndex].getChance();
			float fRounds = iAgainst / fWeightedWounds;
			float fReciprocal = 1/fRounds;
			fTotal += fReciprocal;
		}

		return fTotal / getSize();
	}
	
	float getChance(int iSuccesses)
	{
		if (iSuccesses>=getSize()) return 0.0f;
		return m_aSuccesses[iSuccesses].getChance();
	}
	
	float sumChance(int iHealth, float fCutOff)
	{
		float fTotal = 0.0f;
		
		/*
		for (int iIndex = getSize()-1; iIndex>0; iIndex--)
		{
			float fRounds = iHealth / iIndex;
			if (fRounds>fCutOff) break;
			fTotal += m_aSuccesses[iIndex].getChance();
		}
		*/

		for (int iIndex = getSize()-1; iIndex>0; iIndex--)
		{
			float fWeightedChance = iIndex * m_aSuccesses[iIndex].getChance();
			float fRounds = fWeightedChance / iHealth;
			fTotal += fRounds;
		}
		
		return fTotal;
	}
	
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		for (int iIndex = 0; iIndex<getSize(); iIndex++)
		{
			D6DiceSuccessModel successes = getSuccesses(iIndex);
			sb.append(successes.toString());
			sb.append(" ");
		}
		return sb.toString();
	}
}