package edu.harvard.med.hks.model;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Entity
public class Slot extends AbstractTimestampEntity {
	final static SimpleDateFormat TIME_FT = new SimpleDateFormat("dd/MM/yyyy@HH:mm:ss")  ;
	
	public static enum Status {
		INIT, OCCUPIED, TUTORIAL, PLAY, PAYOFF, FINISHED, DROPPED, THANKS
	}
	@NotNull
	@ManyToOne
	private Game game;
	@NotEmpty
	private String status = Status.INIT.toString();
	private String assignmentId = "";
	private String hitId = "";
	@Column(length=1000)
	private String turkSubmitTo = "";
	
	private int slotNumber;
	private String workerNumber = "";
	private String slotId;
	/**
	 * Work ID in Amazon Mechanical Turk.
	 */
	private String workerId = "";
	
	private int    workerPlayTracker = 0 ;
	
	/**
	 * Reputation of temptee. It is number of black marks.
	 */
	private int blackMarkCount;
	/**
	 * Bonus temptee earned.
	 */
	private int tempteeBonus;
	/**
	 * The payoff the temptee receives if he betrays in the current round.
	 */
	private int currentBetrayPayoff;
	/**
	 * Current tutorial step.
	 */
	private int tutorialStep = 1;
	
	private boolean practice = false ;
	/**
	 * Last action: betray or reward.
	 */
	private String lastAction = "";
	/**
	 * RewardCaughtAsBetrayal.
	 */
	private boolean rewardCaughtAsBetrayal;
	/**
	 * Betray caught.
	 */
	private boolean betrayCaught;
	/**
	 * Survival.
	 */
	private boolean survival = true;
	/**
	 * RewardCaughtAsBetrayal.
	 */
	private double rewardCaughtAsBetrayalSampling = 0;
	/**
	 * Betray caught.
	 */
	private double betrayCaughtSampling = 0;
	/**
	 * Survival.
	 */
	private double survivalSampling = 0;
	/**
	 * Log.
	 */
	@Lob
	private byte[] log;
	
	private   String playerReportJson ;
	/**
	 * The max payoff temptee can receive when she betrays
	 */
	private int maxBetrayPayoff;
	/**
	 * Damage of truster when he is betrayed.
	 */
	private int rewardPayoff;
	/**
	 * The probability a betrayal is not caught.
	 */
	private double betrayCaughtChance;
	/**
	 * The probability a reward is counted as a betrayal.
	 */
	private double rewardCaughtAsBetrayalChance;
	/**
	 * The probability a temptee survive after each round.
	 */
	private double tempteeSurvivalChance;
	/**
	 * Number of black marks at which truster stops trusting temptee.
	 */
	private int blackMarkUpperLimit;
	/**
	 * Init bonus temptee earned.
	 */
	private int initTempteeBonus;
	private int currentRound = 1;
	
	public String getAssignmentId() { return assignmentId; }
	public double getBetrayCaughtSampling() { return betrayCaughtSampling; }

	public int getBlackMarkCount() { return blackMarkCount; }
	public int getBlackMarkUpperLimit() { return blackMarkUpperLimit; }
	
	public int getCurrentRound() { return currentRound; }
	public void setCurrentRound(int currentRound) { this.currentRound = currentRound; }
	
	public Game getGame() { return game; }

	public String getHitId() { return hitId; }
	public int getInitTempteeBonus() { return initTempteeBonus; }
	
	public String getLastAction() { return lastAction; }
	
	public byte[] getLog() { return log; }
	
	public PlayerReport getPlayerReport() {
		if(getPlayerReportJson() == null) {
			return new Slot.PlayerReport() ;
		} else {
			return new Gson().fromJson(getPlayerReportJson(), Slot.PlayerReport.class) ;
		}
	}

	public void setPlayerReport(PlayerReport report) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		this.setPlayerReportJson(gson.toJson(report)) ;
	}
	
	public String getPlayerReportJson() { return playerReportJson ; }
	public void setPlayerReportJson(String report) { this.playerReportJson = report ;}

	public String getPlayerReportFormatted() {
		DecimalFormat CURR_FT = new DecimalFormat("#.00") ;
		PlayerReport  pReport = getPlayerReport() ;
		if(pReport == null) return "" ;
		StringBuilder b = new StringBuilder() ;
		b.append(pReport.getClientInfo()).append("\n") ;
		b.append("Inital game parameters").append("\n");
		b.append("Game Id: ").append(game.getId()).append("\n") ;
		b.append("Slot Id: ").append(slotId).append("\n") ;
		b.append("Worker Id: ").append(workerId).append("\n") ;
		b.append("Assignment Id: ").append(assignmentId).append("\n") ;
		b.append("Hit Id: ").append(hitId).append("\n") ;
		b.append("Number Of Game Slot: ").append(game.getNumberOfGameSlot()).append("\n") ;
		b.append("Max Betray Payoff: ").append(game.getMaxBetrayPayoff()).append("\n") ;
		b.append("Reward Payoff: ").append(game.getRewardPayoff()).append("\n") ;
		b.append("Initial Truster Bonus: ").append(game.getInitialTrusterBonus()).append("\n") ;
		b.append("Chance of black mark for Betrayal: ").append(game.getBetrayCaughtChance()).append("\n") ;
		b.append("Chance of black mark for Reward: ").append(game.getRewardCaughtAsBetrayalChance()).append("\n") ;
		b.append("Betrayal Cost: ").append(game.getBetrayalCost()).append("\n") ;
		b.append("Survival(0 - 1): ").append(game.getTempteeSurvivalChance()).append("\n") ;
		b.append("Black Mark Threshold: ").append(game.getBlackMarkUpperLimit()).append("\n") ;
		b.append("Init Temptee Bonus: ").append(game.getInitTempteeBonus()).append("\n") ;
		b.append("Exchange rate: ").append(game.getExchangeRate()).append("\n") ;
		b.append("Feedback Bonus: ").append(game.getFeedbackBonus()).append("\n") ;
		b.append("Max Rounds Number: ").append(game.getMaxRoundsNum()).append("\n\n") ;
		
		
		printColumn(b, "Start Time", 25) ;
		printColumn(b, "End Time", 25) ;
		printColumn(b, "Duration (sec)", 15) ;
		printColumn(b, "Screen No", 20) ;
		printColumn(b, "Action", 20) ;
		b.append("\n");
		Iterator<TutorialReport> tutReItr = pReport.getTutorialReports().values().iterator() ;
		double timeToComplete = 0 ;
		while(tutReItr.hasNext()) {
			TutorialReport entry = tutReItr.next() ;
			double time = (entry.getEndTime() - entry.getStartTime())/(double)1000;
			if(time < 0) time = 0 ;
			timeToComplete += time ;
			printColumn(b, TIME_FT.format(new Date(entry.getStartTime())), 25) ;
			printColumn(b, TIME_FT.format(new Date(entry.getEndTime())), 25) ;
			printColumn(b, "" + time, 15) ;
			printColumn(b, Integer.toString(entry.getScreenNo()), 20) ;
			printColumn(b, entry.getAction(), 20) ;
			b.append("\n");
		}
		b.append("\n"); 
		b.append("Tutorial Duration: " + timeToComplete).append(" sec. \n\n");
		
		printColumn(b, "Start Time", 25) ;
		printColumn(b, "End Time", 25) ;
		printColumn(b, "Duration (sec)", 15) ;
		printColumn(b, "Round", 16) ;
		printColumn(b, "Status", 10) ;
		printColumn(b, "Low Payoff", 12) ;
		printColumn(b, "Betray Payoff", 15) ;
		printColumn(b, "High Payoff", 12) ;
		printColumn(b, "Choice", 10) ;
		printColumn(b, "Another Round", 15) ;
		printColumn(b, "Remaining Wishes", 20) ;
		printColumn(b, "Balance", 10) ;
		Iterator<Map.Entry<String, PlayerRoundReport>> i = pReport.getRoundReports().entrySet().iterator() ;
		double totalDuration = 0; 
		while(i.hasNext()) {
			Map.Entry<String, PlayerRoundReport> entry = i.next() ;
			PlayerRoundReport rReport = entry.getValue() ;
			if(rReport.getBalance() == 0) {
			  //hack to remove the junk log, to do it correctly , should fix the service and log.
				//But the service code is very messy and I do not have enough time
				continue ; 
			}
			b.append("\n") ;
			double duration = (rReport.getEndTime() - rReport.getStartTime())/(double)1000 ;
			totalDuration += duration ;
			printColumn(b, TIME_FT.format(new Date(rReport.getStartTime())), 25) ;
			printColumn(b, TIME_FT.format(new Date(rReport.getEndTime())), 25) ;
			printColumn(b, "" + duration, 15) ;
			printColumn(b, rReport.getId(), 16) ;
			printColumn(b, rReport.getStatus(), 10) ;
			printColumn(b, Integer.toString(rReport.getLowPayoff()), 12) ;
			printColumn(b, Integer.toString(rReport.getBetrayPayoff()), 15) ;
			printColumn(b, Integer.toString(rReport.getHighPayoff()), 12) ;
			printColumn(b, Integer.toString(rReport.getChoice()), 10) ;
			String anotherRound  = "" ;
      if(!Slot.Status.FINISHED.toString().equals(rReport.getStatus())) {
        anotherRound = "" + rReport.getAnotherRound() ;
      }
			printColumn(b, anotherRound, 15) ;
			printColumn(b, Integer.toString(rReport.getRemainingWishes()), 20) ;
			printColumn(b, Integer.toString(rReport.getBalance()), 10) ;
		}
		b.append("\n\n") ;
		printColumn(b, "Worker Id", 25) ;
		printColumn(b, "Status", 25) ;
		printColumn(b, "Total Earnings", 25) ;
		b.append("\n") ;
		printColumn(b, workerId, 25) ;
		printColumn(b, status, 25) ;
		double balance = 0 ;
		if(Status.FINISHED.toString().equals(status)) {
			balance = getTempteeBonus() * game.getExchangeRate() ;
		}
		printColumn(b, "$" + CURR_FT.format(balance), 25) ;
		b.append("\n\n") ;
		b.append("Total Duration: " + totalDuration).append(" sec.\n") ;
		return b.toString() ;
	}
	
	private void printColumn(StringBuilder b, String string, int width) {
		b.append(string) ;
		for(int i = string.length(); i < width; i++) {
			b.append(' ') ;
		}
	}
	
	public int getRewardPayoff() { return rewardPayoff;}
	public void setRewardPayoff(int rewardPayoff) { this.rewardPayoff = rewardPayoff; }
	
	public double getBetrayCaughtChance() { return betrayCaughtChance; }
	public void setBetrayCaughtChance(double betrayCaughtChance) {
		this.betrayCaughtChance = betrayCaughtChance;
	}
	
	public double getRewardCaughtAsBetrayalChance() { return rewardCaughtAsBetrayalChance; }
	
	public double getRewardCaughtAsBetrayalSampling() { return rewardCaughtAsBetrayalSampling; }
	
	public String getSlotId() { return slotId; }
	
	public String getStatus() { return status; }
	
	public boolean getPractice() { return practice ; }
	
	public void setPractice(boolean b) { this.practice = b ; }
	
	public double getSurvivalSampling() { return survivalSampling; }
	
	public double getTempteeSurvivalChance() { return tempteeSurvivalChance; }
	
	public String getTurkSubmitTo() { return turkSubmitTo; }
	
	public int getTutorialStep() { return tutorialStep; }
	
	public String getWorkerId() { return workerId; }
	
	public int getWorkerPlayTracker() { return this.workerPlayTracker ; }
	public void setWorkerPlayTracker(int number) { this.workerPlayTracker = number ; } 
	
	public boolean isBetrayCaught() { return betrayCaught; }
	
	public boolean isRewardCaughtAsBetrayal() { return rewardCaughtAsBetrayal; }
	
	public void setAssignmentId(String assignmentId) { 
		if(assignmentId == null) assignmentId = "" ;
		this.assignmentId = assignmentId; 
	}
	
	public void setBetrayCaught(boolean betrayCaught) { this.betrayCaught = betrayCaught; }
	
	public void setBetrayCaughtSampling(double betrayCaughtSampling) {
		this.betrayCaughtSampling = betrayCaughtSampling;
	}
	
	public void setBlackMarkCount(int blackMarkCount) {
		this.blackMarkCount = blackMarkCount;
	}
	
	public void setBlackMarkUpperLimit(int blackMarkUpperLimit) {
		this.blackMarkUpperLimit = blackMarkUpperLimit;
	}
	
	public void setGame(Game game) { this.game = game ;}
	
	public void setHitId(String hitId) { 
		if(hitId == null) hitId = "" ;
		this.hitId = hitId; 
	}
	
	public void setInitTempteeBonus(int initTempteeBonus) { this.initTempteeBonus = initTempteeBonus; }
	
	public void setLastAction(String lastAction) { this.lastAction = lastAction; }
	
	public void setLog(byte[] log) { this.log = log; }
	
	public void setRewardCaughtAsBetrayal(boolean rewardCaughtAsBetrayal) {
		this.rewardCaughtAsBetrayal = rewardCaughtAsBetrayal;
	}
	
	public void setRewardCaughtAsBetrayalChance(double rewardCaughtAsBetrayalChance) {
		this.rewardCaughtAsBetrayalChance = rewardCaughtAsBetrayalChance;
	}
	
	public void setRewardCaughtAsBetrayalSampling(double rewardCaughtAsBetrayalSampling) {
		this.rewardCaughtAsBetrayalSampling = rewardCaughtAsBetrayalSampling;
	}
	
	public void setSlotId(String slotId) { this.slotId = slotId; }
	
	public void setStatus(String status) { this.status = status; }
	
	public boolean isSurvival() { return survival; }
	public void setSurvival(boolean survival) { this.survival = survival; }
	
	public void setSurvivalSampling(double survivalSampling) {
		this.survivalSampling = survivalSampling;
	}
	
	public void setTempteeSurvivalChance(double tempteeSurvivalChance) {
		this.tempteeSurvivalChance = tempteeSurvivalChance;
	}
	
	public void setTurkSubmitTo(String turkSubmitTo) {
		this.turkSubmitTo = turkSubmitTo;
	}
	
	public void setTutorialStep(int tutorialStep) { this.tutorialStep = tutorialStep; }
	
	public void setWorkerId(String workerId) { this.workerId = workerId; }
	
	public String getWorkerNumber() { return workerNumber; }
	public void   setWorkerNumber(String workerNumber) { this.workerNumber = workerNumber; }
	
	public int getTempteeBonus() { return tempteeBonus; }
	
	public void setTempteeBonus(int tempteeBonus) { this.tempteeBonus = tempteeBonus; }
	
	public int getCurrentBetrayPayoff() { return currentBetrayPayoff; }
	
	public void setCurrentBetrayPayoff(int currentBetrayPayoff) { this.currentBetrayPayoff = currentBetrayPayoff; }
	
	public int getMaxBetrayPayoff() { return maxBetrayPayoff; }
	
	public void setMaxBetrayPayoff(int maxBetrayPayoff) { this.maxBetrayPayoff = maxBetrayPayoff ;}
	
	public int  getSlotNumber() { return slotNumber; }
	public void setSlotNumber(int slotNumber) { this.slotNumber = slotNumber; }
	
	static public class PlayerReport {
		private Map<Integer, TutorialReport> tutorialReports = new LinkedHashMap<Integer, TutorialReport>() ;
		private Map<String, PlayerRoundReport> roundReports  = new LinkedHashMap<String, PlayerRoundReport>() ;
		private String clientInfo ;
		
		public TutorialReport addTutorialReport(int screenNo, String action) {
			TutorialReport tutorialReport = new TutorialReport() ;
			int id = tutorialReports.size() ;
			long ctime = System.currentTimeMillis() ;
			if(id > 0) {
				TutorialReport pScreen = this.tutorialReports.get(id - 1) ;
				pScreen.setEndTime(ctime) ;
			}
			tutorialReport.setStartTime(ctime) ;
			tutorialReport.setScreenNo(screenNo) ;
			tutorialReport.setAction(action) ;
			tutorialReports.put(id, tutorialReport) ;
			return tutorialReport ;
		}
		
		public void finishTutorial() {
			int id = tutorialReports.size() ;
			long ctime = System.currentTimeMillis() ;
			if(id > 0) {
				TutorialReport pScreen = this.tutorialReports.get(id - 1) ;
				pScreen.setEndTime(ctime) ;
			}
		}
		
		public Map<Integer, TutorialReport> getTutorialReports() { return this.tutorialReports ; }
		public void setTutorialReports(Map<Integer, TutorialReport> aReport) {
			this.tutorialReports = aReport;
    }
		
		public Map<Integer, TutorialReport> getMergeTutorialReports() { 
			Map<Integer, TutorialReport> holder = new LinkedHashMap<Integer, TutorialReport>() ;
			Iterator<TutorialReport> i = tutorialReports.values().iterator() ;
			while(i.hasNext()) {
				TutorialReport report = i.next() ;
				TutorialReport exist = holder.get(report.getScreenNo()) ;
				if(exist == null) {
					exist = report;
					holder.put(exist.getScreenNo(), exist) ;
				} 
				exist.addMergeDuration(report.getDuration()) ;
			}
			return holder ; 
		}
		
		
		public PlayerRoundReport getPlayerRoundReport(Slot slot, int round, boolean create) {
			String key = "PLAY ROUND ";
			if(slot.getPractice()) key = "PRACTICE ROUND " ;
			key += round ;
			PlayerRoundReport rReport = roundReports.get(key) ;
			long ctime = System.currentTimeMillis() ;
			if(create && rReport == null) {
				rReport = new PlayerRoundReport() ;
				rReport.setId(key) ;
				rReport.setRound(round) ;
				rReport.setStartTime(ctime) ;
				roundReports.put(key, rReport) ;
			}
			if(rReport.getEndTime() == 0) rReport.setEndTime(ctime + 3000) ; 
			else rReport.setEndTime(ctime) ;
			return rReport ;
		}
		
		public Map<String, PlayerRoundReport> getRoundReports() { return roundReports; }
		public void setRoundReports(Map<String, PlayerRoundReport> roundReports) {
			this.roundReports = roundReports;
    }
		
		public String getClientInfo() { return this.clientInfo ; }
		public void   setClientInfo(String info) { this.clientInfo = info ; }
	}
	
	static public class PlayerRoundReport {
		private String id ;
		private long startTime ;
		private long endTime ;
		private int  round ;
		private String status ;
		private int  lowPayoff ;
		private int  betrayPayoff ;
		private int  highPayoff ;
		private int  choice  ;
		private int  anotherRound ;
		private int  remainingWishes ;
		private int  balance ;
		private String clientIp ;
		
		public String getId() { return id ; }
		public void   setId(String id) { this.id = id ; }
		
		public long getStartTime() { return this.startTime ; }
		public void setStartTime(long time) { this.startTime = time ;}
		
		public long getEndTime() { return this.endTime ; }
		public void setEndTime(long time) { this.endTime = time ;}
		
		public int getRound() { return round; }
		public void setRound(int round) { this.round = round; }
		
		public String getStatus() { return this.status ; }
		public void   setStatus(String status) { this.status = status ; }
		
		public int getLowPayoff() { return lowPayoff; }
		public void setLowPayoff(int lowPayoff) { this.lowPayoff = lowPayoff; }
		
		public int getBetrayPayoff() { return betrayPayoff; }
		public void setBetrayPayoff(int betrayPayoff) { this.betrayPayoff = betrayPayoff; }
		
		public int getHighPayoff() { return highPayoff; }
		public void setHighPayoff(int highPayoff) { this.highPayoff = highPayoff; }

		public int getChoice() { return choice; }
		public void setChoice(int choice) { this.choice = choice; }
		
		public int getAnotherRound() { return anotherRound; }
		public void setAnotherRound(int anotherRound) { this.anotherRound = anotherRound; }
		
		public int getRemainingWishes() { return remainingWishes; }
		public void setRemainingWishes(int remainingWishes) { this.remainingWishes = remainingWishes; }
		
		public int getBalance() { return balance; }
		public void setBalance(int balance) { this.balance = balance; }
	
		public String getClientIp() { return this.clientIp ; }
		public void   setClientIp(String ip) { this.clientIp = ip ; }
	}
	
	static public class TutorialReport {
		private int     id ;
		private long    startTime ;
		private long    endTime ;
		private int     screenNo ;
		private String  action ;
		
		private transient long mergeDuration ;

		public int getId() { return id ; }
		public void setId(int id) { this.id = id ; }
		
		public long getStartTime() { return startTime; }
		public void setStartTime(long time) { this.startTime = time; }

		public long getEndTime() { return this.endTime ; }
		public void setEndTime(long time) { this.endTime = time ;}
		
		public int getScreenNo() { return screenNo; }
		public void   setScreenNo(int step) { this.screenNo = step; }

		public String getAction() { return action; }
		public void setAction(String action) { this.action = action; }

		public long mergeDuration() { return this.mergeDuration ; }
		public void addMergeDuration(long time) {
			if(time < 0) return ;
			this.mergeDuration += time ;
		}
		
		public long getDuration() {
			long time = this.endTime - this.startTime ;
			if(time < 0) return 0 ;
			return time ;
		}
		
		public String toString() {
			StringBuilder b = new StringBuilder() ;
			Date date = new Date(startTime) ;
			b.append(date).append(" Tutorial ").append(screenNo).append(" ").append(action) ;
			return b.toString() ;
		}
	}
}