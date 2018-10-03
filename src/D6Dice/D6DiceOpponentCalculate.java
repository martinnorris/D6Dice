package D6Dice;

class D6DiceOpponentCalculate extends D6DiceOpponentModel
{
	static D6DiceOpponentCalculate[] create(D6DiceOpponentModel[] aopponents)
	{
		int iNumber = aopponents.length;
		D6DiceOpponentCalculate[] aReturn = new D6DiceOpponentCalculate[iNumber];
		for (int iIndex = 0; iIndex<iNumber; iIndex++)
		{
			aReturn[iIndex] = new D6DiceOpponentCalculate(aopponents[iIndex]);
		}
		return aReturn;
	}
	
	private float m_fPrecision;
	
	private D6DiceOpponentCalculate(D6DiceOpponentModel dataSource)
	{
		super(dataSource);
		m_fPrecision = 0.005f;
	}
	
	private void log(String scPrefix, int iIndex, D6DiceModel dice)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(scPrefix);
		sb.append(iIndex);
		sb.append(" ");
		sb.append(dice.toString());
		System.out.println(sb.toString());
		return;
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
	
	private D6DiceModel calculateDistribution(int iTotal)
	{
		if (1>iTotal) return D6DiceModel.get0();
		
		D6DiceModel dice = new D6DiceModel();
		D6DiceModel diceReturn = dice;
		log(".Bell ", 1, diceReturn);
		
		for (int iNumber = 1; iNumber<iTotal; iNumber++)
		{
			diceReturn = diceReturn.addDice(dice);
			log(".Bell ", iNumber+1, diceReturn);
		}
		
		return diceReturn.rationaliseDice(m_fPrecision);
	}
	
	private D6DiceModel m_diceINIT;
	
	void calculateINIT(D6DiceResultModel result, int iBar) 
	{
		// Calculate a range of successes for the INIT dice
		m_diceINIT = calculateDistribution(m_iINIT);
		log(">Init ", iBar, m_diceINIT);
		
		return;
	}

	private D6DiceModel m_diceAdvantage;
	
	void calculateAdvantage(D6DiceOpponentCalculate[] aOpponents, int iSkip, D6DiceResultModel result, int iBar) 
	{
		int iNumberOpponents = aOpponents.length;
		D6DiceModel[] aPairs = new D6DiceModel[iNumberOpponents];

		for (int iIndex = 0; iIndex<iNumberOpponents; iIndex++)
		{
			if (iIndex==iSkip) continue;
			
			aPairs[iIndex] = m_diceINIT;
			log("+Init ", iIndex, aPairs[iIndex]);
			log("-Init ", iIndex, aOpponents[iIndex].m_diceINIT);
			
			aPairs[iIndex] = aPairs[iIndex].cancelDice(aOpponents[iIndex].m_diceINIT);
			log("=Init  ", iIndex, aPairs[iIndex]);
		}
		
		D6DiceModel diceAdvantage = D6DiceModel.getEmpty();
		
		for (int iIndex = 0; iIndex<iNumberOpponents; iIndex++)
		{
			if (iIndex==iSkip) continue;
			diceAdvantage = diceAdvantage.addChance(aPairs[iIndex]);
			log("+Comb ", iIndex, diceAdvantage);
		}
		
		diceAdvantage = diceAdvantage.divideChance(iNumberOpponents - 1);
		log("=1st  ", iSkip, diceAdvantage);
		
		diceAdvantage = diceAdvantage.rationaliseDice(2);
		log(">1st  ", iSkip, diceAdvantage);
		
		// When init dice used in HIT provide 2 dice which translates as (0 successes chance and 2D successes)
		//m_diceAdvantage = diceAdvantage.promoteDice(1, 2);
		
		D6DiceModel dice = new D6DiceModel();
		
		for (int iNumber = 0; iNumber<2; iNumber++)
		{
			diceAdvantage = diceAdvantage.addDice(dice);
			log(".Adv  ", iNumber+1, diceAdvantage);
		}
		
		m_diceAdvantage = diceAdvantage.rationaliseDice(m_fPrecision);
		log(">Adv  ", iBar, m_diceAdvantage);
		
		return;
	}
	
	void noAdvantage(D6DiceOpponentCalculate[] aOpponents, int iSkip, D6DiceResultModel result, int iBar) 
	{
		m_diceAdvantage = D6DiceModel.get0();
		log(">Adv  ", iSkip, m_diceAdvantage);		
	}
	
	private D6DiceModel m_diceHIT;
	
	void calculateHIT(int iOpponents, D6DiceResultModel result, int iBar) 
	{
		// Calculate a range of successes for the HIT dice including additional advantages
		D6DiceModel diceHit = calculateDistribution(m_iHIT);
		log(">Hit  ", iBar, diceHit);
		
		// Include advantage
		m_diceHIT = diceHit.addDice(m_diceAdvantage);
		log("+Adv  ", iBar, m_diceHIT);		
		
		m_diceHIT = m_diceHIT.rationaliseDice(m_fPrecision);
		log(">Hit  ", iBar, m_diceHIT);
		
		if (0==iOpponents) return;
		
		D6DiceModel diceCancelled = m_diceHIT;
		D6DiceModel dice = new D6DiceModel();
		
		for (int iNumber = 0; iNumber<(3*iOpponents); iNumber++)
		{
			diceCancelled = diceCancelled.cancelDice(dice);
			log(".Rem  ", iNumber+1, diceCancelled);
		}
		
		m_diceHIT = diceCancelled.rationaliseDice(m_fPrecision);
		log(">Hit  ", iBar, m_diceHIT);
				
		return;
	}

	void calculateDefend(int iOpponents, D6DiceResultModel result, int iBar) 
	{
		// Add 1 success
		m_diceHIT = m_diceHIT.addDice(D6DiceModel.get1());
		log("+Adv  ", iBar, m_diceHIT);		
		
		m_diceHIT = m_diceHIT.rationaliseDice(m_fPrecision);
		log(">Hit  ", iBar, m_diceHIT);
		
		m_diceWin = D6DiceModel.get0();
		m_diceDAMAGE = D6DiceModel.get0();
						
		return;
	}
	
	private D6DiceModel m_diceWin;
	
	void calculateWin(D6DiceOpponentCalculate[] aOpponents, int iSkip, D6DiceResultModel result, int iBar) 
	{
		int iNumberOpponents = aOpponents.length;
		D6DiceModel[] aPairs = new D6DiceModel[iNumberOpponents];

		// First opponent average against _all_ others - others only first
		int iSumHits = 0==iSkip?iNumberOpponents:1;
		
		// Compare against all opponents
		for (int iIndex = 0; iIndex<iSumHits; iIndex++)
		{
			if (iIndex==iSkip) continue;
			
			aPairs[iIndex] = m_diceHIT;
			log("+Hit  ", iIndex, aPairs[iIndex]);
			log("-Hit  ", iIndex, aOpponents[iIndex].m_diceHIT);
			
			aPairs[iIndex] = aPairs[iIndex].cancelDice(aOpponents[iIndex].m_diceHIT);			
			log("=Hit  ", iIndex, aPairs[iIndex]);
		}
		
		D6DiceModel diceWins = D6DiceModel.getEmpty();
		
		for (int iIndex = 0; iIndex<iSumHits; iIndex++)
		{
			if (iIndex==iSkip) continue;
			diceWins = diceWins.addChance(aPairs[iIndex]);
			log("_Comb ", iIndex, diceWins);
		}
		
		int iDivideWins = 0==iSkip?iNumberOpponents-1:1;
		diceWins = diceWins.divideChance(iDivideWins);
		log("=Win  ", iSkip, diceWins);
		
		m_diceWin = diceWins.rationaliseDice(m_fPrecision);
		log(">Win  ", iSkip, m_diceWin);
		
		return;
	}
	
	private D6DiceModel m_diceDAMAGE;
	
	void calculateDAMAGE(D6DiceResultModel result, int iBar) 
	{
		// Calculate damage dice distribution
		D6DiceModel diceDamage = calculateDistribution(m_iDAMAGE);
		log(">Dmg  ", iBar, diceDamage);
		
		// Damage based on wins {either win or not}
		m_diceDAMAGE = m_diceWin.rationaliseDice(1);
		log("=Base ", iBar, m_diceDAMAGE);		
		
		m_diceDAMAGE = m_diceDAMAGE.addDiceTo(diceDamage);		
		log("+Base ", iBar, m_diceDAMAGE);		
		
		m_diceDAMAGE = m_diceDAMAGE.rationaliseDice(m_fPrecision);
		log(">Dmg  ", iBar, m_diceDAMAGE);
		
		return;
	}
	
	void calculateDAMAGEWithHIT(D6DiceResultModel result, int iBar) 
	{
		// Calculate damage dice distribution
		D6DiceModel diceDamage = calculateDistribution(m_iDAMAGE);
		log(">Dmg  ", iBar, diceDamage);
		
		// Damage based on wins
		m_diceDAMAGE = m_diceWin;
		log("=Base ", iBar, m_diceDAMAGE);		
		
		m_diceDAMAGE = m_diceDAMAGE.addDiceZero(diceDamage);
		log("+Base ", iBar, m_diceDAMAGE);
		
		m_diceDAMAGE = m_diceDAMAGE.rationaliseDice(m_fPrecision);
		log(">Dmg  ", iBar, m_diceDAMAGE);
		
		return;
	}
	
	private D6DiceModel m_diceWounds;
	
	void calculateWounds(D6DiceOpponentCalculate[] aOpponents, int iSkip, D6DiceResultModel result, int iBar) 
	{
		int iNumberOpponents = aOpponents.length;
		D6DiceModel[] aPairs = new D6DiceModel[iNumberOpponents];
		
		// First opponent must damage _all_ others - all others only first
		int iSumDamage = 0==iSkip?iNumberOpponents:1;

		// Compare against all opponents
		for (int iIndex = 0; iIndex<iSumDamage; iIndex++)
		{
			if (iIndex==iSkip) continue;
			
			aPairs[iIndex] = m_diceDAMAGE;
			log("+Dmg  ", iIndex, aPairs[iIndex]);
			log("-Arm  ", iIndex, aOpponents[iIndex].m_iARMOUR);
			
			aPairs[iIndex] = aPairs[iIndex].cancelSuccesses(aOpponents[iIndex].m_iARMOUR);
			log("=Wnds ", iIndex, aPairs[iIndex]);
		}
		
		// Combine the wounds done per round to either opponent
		
		D6DiceModel diceWounds = D6DiceModel.getEmpty();
		
		for (int iIndex = 0; iIndex<iSumDamage; iIndex++)
		{
			if (iIndex==iSkip) continue;
			diceWounds = diceWounds.addChance(aPairs[iIndex]);
			log("_Comb ", iIndex, diceWounds);
		}
		
		int iDivideDamage = 0==iSkip?iNumberOpponents-1:1;
		m_diceWounds = diceWounds.divideChance(iDivideDamage);
		log(">Wnds ", iSkip, m_diceWounds);
		
		return;
	}
	
	private int m_iTotalHealth;
	
	void calculateHealth(D6DiceOpponentCalculate[] aOpponents, int iSkip)
	{
		int iNumberOpponents = aOpponents.length;
		
		// Combine all opponents health
		m_iTotalHealth = 0;
		
		// First opponent must damage _all_ others - all others only first
		int iSumHealth = 0==iSkip?iNumberOpponents:1;

		for (int iIndex = 0; iIndex<iSumHealth; iIndex++)
		{
			if (iIndex==iSkip) continue;
			m_iTotalHealth += aOpponents[iIndex].m_iHEALTH * 2;
			log("+Heal ", iIndex, m_iTotalHealth);
		}
		
		log(">Heal ", iSkip, m_iTotalHealth);
		
		return;
	}
	
	private D6DiceModel m_diceCombined_1;
	
	void calculateCombined_1(D6DiceOpponentCalculate[] aOpponents, int iSkip) 
	{
		int iNumberOpponents = aOpponents.length;
		
		// First opponent must damage _all_ others - all others only first
		int iSumDamage = 0==iSkip?iNumberOpponents:1;

		D6DiceModel diceWounds = D6DiceModel.get0();
		
		// Compare against all opponents
		for (int iIndex = 0; iIndex<iSumDamage; iIndex++)
		{
			if (iIndex==iSkip) continue;
			
			D6DiceModel diceArmour = m_diceDAMAGE;
			log("+Dmg1 ", iIndex, diceArmour);
			log("-Arm1 ", iIndex, aOpponents[iIndex].m_iARMOUR);
			
			diceArmour = diceArmour.cancelSuccesses(aOpponents[iIndex].m_iARMOUR);
			log("=Wnd1 ", iIndex, diceArmour);
			
			diceWounds = diceWounds.addDice(diceArmour);
			log("=WndN ", iIndex, diceWounds);
		}
		
		m_diceCombined_1 = diceWounds.rationaliseDice(m_fPrecision);
		log(">WndN ", iSkip, m_diceCombined_1);
		
		return;
	}
	
	private D6DiceModel m_diceCombined_O;
	
	void calculateCombined_O(D6DiceOpponentCalculate[] aOpponents, int iSkip) 
	{
		int iNumberOpponents = aOpponents.length;
		
		// First opponent only 1 set damage, combine for others
		int iSumOpponents = 0==iSkip?1:iNumberOpponents;
		
		D6DiceModel diceCombined = m_diceCombined_1;
		log(".WndN ", iSkip, m_diceCombined_1);

		for (int iIndex = 1; iIndex<iSumOpponents; iIndex++)
		{
			if (iIndex==iSkip) continue;
			diceCombined = diceCombined.addChance(aOpponents[iIndex].m_diceCombined_1);
			log("_Comb ", iIndex, diceCombined);
		}		
		
		m_diceCombined_O = diceCombined.rationaliseDice(m_fPrecision);
		log(">WndC ", iSkip, m_diceCombined_O);
		
		return;
	}
	
	private float m_fCutoff;
	
	float calculateCutoff(D6DiceOpponentCalculate[] aOpponents, int iSkip)
	{
		float fDamage = m_diceCombined_O.getChance(1);
		
		m_fCutoff = m_iTotalHealth / fDamage;
		log("=Cut@ ", iSkip, m_fCutoff);

		return m_fCutoff;
	}
	
	private float m_fChance;
	
	float calculateChance(D6DiceModel diceCombined, int iTotalHealth, float fCutoff)
	{
		return diceCombined.sumChance(iTotalHealth, fCutoff);
	}
	
	float calculateChance(D6DiceOpponentCalculate[] aOpponents, int iSkip, float fCutoff)
	{
		//m_fChance = m_diceCombined_O.sumChance(m_iTotalHealth, fCutoff);
		m_fChance = calculateChance(m_diceCombined_1, m_iTotalHealth, fCutoff);
		return m_fChance;		
	}
	
	void calculateAverages(D6DiceOpponentCalculate[] aOpponents, int iSkip, D6DiceResultModel result, int iBar)
	{
		// Average wounds per round
		float fWounds = m_diceWounds.averageSuccesses();
		log("=AvgW ", iSkip, fWounds);
		
		// Average rounds
		float fRounds = m_iTotalHealth / fWounds;
		log("=AvgR ", iSkip, fRounds);
		
		// Average wins
		int iWins = (int)(1000 * m_diceWin.sumSuccesses());
		log("=AvgW ", iSkip, iWins);
		
		// Calculate rounds
		int iWeighted = (int)(1000 * m_diceDAMAGE.weightSuccesses(m_iTotalHealth));
		log("Weighted ", iSkip, iWeighted);
		int iDistribution = (int)(m_fChance * 1000);
		log("Distribution ", iSkip, iDistribution);
		
		result.addBar(iBar, iDistribution, fRounds, fWounds);
		
		return;
	}
	
	public static void main(String[] args) 
	{
		D6DiceOpponentModel data = new D6DiceOpponentModel();
		D6DiceOpponentCalculate main = new D6DiceOpponentCalculate(data);
		D6DiceModel diceModel = main.calculateDistribution(2);
		float fChance = main.calculateChance(diceModel, 2, 0.0f);
		main.log("Test> ", 2, fChance);
	}
}