package D6Dice;

import java.util.Random;

class D6DiceOpponentSimulate extends D6DiceOpponentModel 
{
	static D6DiceOpponentSimulate[] create(D6DiceOpponentModel[] aopponents)
	{
		int iNumber = aopponents.length;
		D6DiceOpponentSimulate[] aReturn = new D6DiceOpponentSimulate[iNumber];
		for (int iIndex = 0; iIndex<iNumber; iIndex++)
		{
			aReturn[iIndex] = new D6DiceOpponentSimulate(aopponents[iIndex]);
		}
		return aReturn;
	}
	
	private Random m_random;
	
	private int m_iHealth;
	private int m_iWounds;
	private int m_iRounds;
	
	private D6DiceOpponentSimulate(D6DiceOpponentModel dataSource)
	{
		super(dataSource);
		m_random = new Random();
	}
	
	boolean isHealthy()
	{
		return (0<m_iHealth);
	}
	
	void reset()
	{
		m_iHealth = m_iHEALTH * 2;
		m_iWounds = 0;
		m_iRounds = 0;
	}
	
	private void log(String scPrefix, int iIndex, int iValue)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(scPrefix);
		sb.append(iIndex);
		sb.append(" ");
		sb.append(iValue);
		sb.append(" ");
		System.out.println(sb.toString());
		return;
	}
	
	private void log(String scPrefix, int iIndex, float fValue)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(scPrefix);
		sb.append(iIndex);
		sb.append(" ");
		sb.append(String.format("%3.3f", fValue));
		sb.append(" ");
		System.out.println(sb.toString());
		return;
	}
	
	private int getSuccesses()
	{
		int iRandom = m_random.nextInt(6) + 1;
		if (iRandom==1) return 1;
		if (iRandom==6) return 1 + getSuccesses();
		return 0;
	}
	
	private int getSuccesses(int iNumber)
	{
		int iSuccesses = 0;
		for (int iCount = 0; iCount<iNumber; iCount++)
			iSuccesses += getSuccesses();
		return iSuccesses;
	}
	
	private int getCancels()
	{
		int iRandom = m_random.nextInt(6) + 1;
		if (iRandom==1) return 1;
		if (iRandom==6) return 1;
		return 0;
	}
	
	private int getCancels(int iNumber)
	{
		int iSuccesses = 0;
		for (int iCount = 0; iCount<iNumber; iCount++)
			iSuccesses += getCancels();
		return iSuccesses;
	}
	
	private int m_iNoInit;
	
	void calculateINIT(D6DiceResultModel result, int iBar) 
	{
		m_iNoInit = getSuccesses(m_iINIT);
		
		log(">Init ", iBar, m_iNoInit);	
		return;
	}

	private int m_iNoAdvantage;
	
	void calculateAdvantage(D6DiceOpponentSimulate[] aOpponents, int iSkip, D6DiceResultModel result, int iBar) 
	{
		m_iNoAdvantage = 0;
		
		int iNumberOpponents = aOpponents.length;
		// Start assuming will win initiative
		int iHighestInit = iSkip;

		for (int iIndex = 0; iIndex<iNumberOpponents; iIndex++)
		{
			if (iIndex==iSkip) continue;
			if (1>aOpponents[iIndex].m_iHealth) continue;
			// Any opponent with same or better takes away advantage
			if (aOpponents[iIndex].m_iNoInit>=aOpponents[iHighestInit].m_iNoInit)
				iHighestInit = iIndex;
		}
		// Only have advantage if no other has same or better init
		if (iHighestInit==iSkip) m_iNoAdvantage = 2;
		
		log(">Adv  ", iBar, m_iNoAdvantage);
		
		return;
	}
	
	void noAdvantage(D6DiceOpponentSimulate[] aOpponents, int iSkip, D6DiceResultModel result, int iBar) 
	{
		m_iNoAdvantage = 0;
		log(">Adv  ", iSkip, m_iNoAdvantage);		
	}
	
	private int m_iNoHit;
	
	void calculateHIT(int iOpponents, D6DiceResultModel result, int iBar) 
	{
		// Calculate a range of successes for the HIT dice including additional advantages
		m_iNoHit = getSuccesses(m_iHIT + m_iNoAdvantage);
		log(">Hit  ", iBar, m_iNoHit);
		
		if (0==iOpponents) return;
		
		int iCancelled = getCancels(3*iOpponents);
		log("-Can  ", iOpponents+1, iCancelled);
		
		m_iNoHit -= iCancelled;
		if (0>m_iNoHit) m_iNoHit = 0;
		
		log(">Hit  ", iBar, m_iNoHit);
				
		return;
	}

	void calculateDefend(int iOpponents, D6DiceResultModel result, int iBar) 
	{
		// Add 1 success
		m_iNoHit += 1;
		return;
	}
	
	private int m_iNoWin;
	
	void calculateWin(D6DiceOpponentSimulate[] aOpponents, int iSkip, D6DiceResultModel result, int iBar) 
	{
		m_iNoWin = m_iNoHit;

		// First opponent average against _all_ others - others only first
		int iNumberOpponents = aOpponents.length;
		
		// No of wins is no hit reduced by smallest no hits
		int iHits = m_iNoHit;
		
		for (int iIndex = 0; iIndex<iNumberOpponents; iIndex++)
		{
			if (iHits>aOpponents[iIndex].m_iNoHit) iHits = aOpponents[iIndex].m_iNoHit;
			log(".Hits ", iIndex, iHits);
		}
		
		m_iNoWin = m_iNoHit - iHits;
		log(">Win  ", iSkip, m_iNoWin);
		
		return;
	}

	private int m_iNoDamage;
	
	void calculateDAMAGE(D6DiceResultModel result, int iBar) 
	{
		if (0==m_iNoWin)
			m_iNoDamage = 0;
		else
			m_iNoDamage = getSuccesses(m_iDAMAGE);
		
		log(">Dmg  ", iBar, m_iNoDamage);
		return;
	}
	
	void calculateDAMAGEWithHIT(D6DiceResultModel result, int iBar) 
	{
		if (0==m_iNoWin)
			m_iNoDamage = 0;
		else
			m_iNoDamage = getSuccesses(m_iDAMAGE + m_iNoWin);
		
		log(">Dmg  ", iBar, m_iNoDamage);
		return;
	}
	
	private int m_iNoWounds;
	private int m_iTarget;
	
	void calculateWounds(D6DiceOpponentSimulate[] aOpponents, int iSkip, D6DiceResultModel result, int iBar) 
	{
		m_iNoWounds = 0;
		if (0==m_iNoDamage) return;
				
		// First opponent must damage _all_ others - all others only first
		int iNumberOpponents = aOpponents.length;
		int iSumDamage = 0==iSkip?iNumberOpponents:1;
		
		// Compare against all opponents
		for (int iIndex = 0; iIndex<iSumDamage; iIndex++)
		{
			if (iIndex==iSkip) continue;
			if (!aOpponents[iIndex].isHealthy()) continue;
			if (aOpponents[iIndex].m_iNoHit>m_iNoHit) continue;
			
			int iPossible = m_iNoDamage - aOpponents[iIndex].m_iARMOUR;

			if (1>iPossible) continue;
			log("_Wnd  ", iIndex, iPossible);
			
			if (iPossible<m_iNoWounds) continue;
			
			m_iTarget = iIndex;
			m_iNoWounds = iPossible;
		}
		log(">Wnd  ", iSkip, m_iNoWounds);
		
		return;
	}
	
	boolean calculateAverages(D6DiceOpponentSimulate[] aOpponents, int iSkip, D6DiceResultModel result, int iBar)
	{
		m_iWounds += m_iNoWounds;
		m_iRounds += 1;
		
		aOpponents[m_iTarget].m_iHealth -= m_iNoWounds;
		
		int iNumberOpponents = aOpponents.length;
		
		// First opponent must damage _all_ others - all others only first
		int iSumDamage = 0==iSkip?iNumberOpponents:1;
		boolean zEnd = true;
		
		// Compare against all opponents
		for (int iIndex = 0; iIndex<iSumDamage; iIndex++)
		{
			if (iIndex==iSkip) continue;
			if (1>aOpponents[m_iTarget].m_iHealth) continue;
			zEnd = false;
			break;
		}
		
		// Average wounds per round
		float fWounds = m_iWounds / m_iRounds;
		log("=AvgW ", iSkip, fWounds);
		
		// Average rounds
		float fRounds = m_iRounds;
		log("=AvgR ", iSkip, fRounds);
		
		// Avoid mutual death
		boolean zWon = zEnd && 0<m_iHealth;
		
		result.addBar(iBar, zWon?1:0, fRounds, fWounds);
		
		return zEnd;
	}
	
	public static void main(String[] args) 
	{
		D6DiceOpponentModel data = new D6DiceOpponentModel();
		D6DiceOpponentSimulate main = new D6DiceOpponentSimulate(data);
		
		float fChance = 0.0f;
		
		for (int iIteration = 0; iIteration<1000; iIteration++)
		{
			int iSuccesses = main.getSuccesses(2);
			if (2<=iSuccesses) fChance += 1;
		}
		main.log("Test> ", 2, fChance/1000);
	}
}
