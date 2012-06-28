package edu.harvard.med.hks.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
public class Slot extends AbstractTimestampEntity {
	public static enum Status {
		INIT,
		OCCUPIED,
		TUTORIAL,
		PLAY,
		PAYOFF,
		FINISHED,
		DROPPED,
		THANKS
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
	/**
	 * Slot number.
	 */
	private int slotNumber;
	/**
	 * Worker number.
	 */
	private String workerNumber = "";
	/**
	 * Slot ID.
	 */
	private String slotId;
	/**
	 * Work ID in Amazon Mechanical Turk.
	 */
	private String workerId = "";
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
	public String getAssignmentId() {
		return assignmentId;
	}
	public double getBetrayCaughtSampling() {
		return betrayCaughtSampling;
	}
	public int getBlackMarkCount() {
		return blackMarkCount;
	}
	public int getBlackMarkUpperLimit() {
		return blackMarkUpperLimit;
	}
	public int getCurrentRound() {
		return currentRound;
	}
	public Game getGame() {
		return game;
	}
	public String getHitId() {
		return hitId;
	}
	public int getInitTempteeBonus() {
		return initTempteeBonus;
	}
	public String getLastAction() {
		return lastAction;
	}
	public byte[] getLog() {
		return log;
	}
	public int getRewardPayoff() {
		return rewardPayoff;
	}
	public void setRewardPayoff(int rewardPayoff) {
		this.rewardPayoff = rewardPayoff;
	}
	public double getBetrayCaughtChance() {
		return betrayCaughtChance;
	}
	public void setBetrayCaughtChance(double betrayCaughtChance) {
		this.betrayCaughtChance = betrayCaughtChance;
	}
	public double getRewardCaughtAsBetrayalChance() {
		return rewardCaughtAsBetrayalChance;
	}
	public double getRewardCaughtAsBetrayalSampling() {
		return rewardCaughtAsBetrayalSampling;
	}
	public String getSlotId() {
		return slotId;
	}
	public String getStatus() {
		return status;
	}
	public double getSurvivalSampling() {
		return survivalSampling;
	}
	public double getTempteeSurvivalChance() {
		return tempteeSurvivalChance;
	}
	public String getTurkSubmitTo() {
		return turkSubmitTo;
	}
	public int getTutorialStep() {
		return tutorialStep;
	}
	public String getWorkerId() {
		return workerId;
	}
	public boolean isBetrayCaught() {
		return betrayCaught;
	}
	public boolean isRewardCaughtAsBetrayal() {
		return rewardCaughtAsBetrayal;
	}
	public boolean isSurvival() {
		return survival;
	}
	public void setAssignmentId(String assignmentId) {
		this.assignmentId = assignmentId;
	}
	public void setBetrayCaught(boolean betrayCaught) {
		this.betrayCaught = betrayCaught;
	}
	public void setBetrayCaughtSampling(double betrayCaughtSampling) {
		this.betrayCaughtSampling = betrayCaughtSampling;
	}
	public void setBlackMarkCount(int blackMarkCount) {
		this.blackMarkCount = blackMarkCount;
	}
	public void setBlackMarkUpperLimit(int blackMarkUpperLimit) {
		this.blackMarkUpperLimit = blackMarkUpperLimit;
	}
	public void setCurrentRound(int currentRound) {
		this.currentRound = currentRound;
	}
	public void setGame(Game game) {
		this.game = game;
	}
	public void setHitId(String hitId) {
		this.hitId = hitId;
	}
	public void setInitTempteeBonus(int initTempteeBonus) {
		this.initTempteeBonus = initTempteeBonus;
	}
	public void setLastAction(String lastAction) {
		this.lastAction = lastAction;
	}
	public void setLog(byte[] log) {
		this.log = log;
	}
	public void setRewardCaughtAsBetrayal(boolean rewardCaughtAsBetrayal) {
		this.rewardCaughtAsBetrayal = rewardCaughtAsBetrayal;
	}
	public void setRewardCaughtAsBetrayalChance(double rewardCaughtAsBetrayalChance) {
		this.rewardCaughtAsBetrayalChance = rewardCaughtAsBetrayalChance;
	}
	public void setRewardCaughtAsBetrayalSampling(double rewardCaughtAsBetrayalSampling) {
		this.rewardCaughtAsBetrayalSampling = rewardCaughtAsBetrayalSampling;
	}
	public void setSlotId(String slotId) {
		this.slotId = slotId;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public void setSurvival(boolean survival) {
		this.survival = survival;
	}
	public void setSurvivalSampling(double survivalSampling) {
		this.survivalSampling = survivalSampling;
	}
	public void setTempteeSurvivalChance(double tempteeSurvivalChance) {
		this.tempteeSurvivalChance = tempteeSurvivalChance;
	}
	public void setTurkSubmitTo(String turkSubmitTo) {
		this.turkSubmitTo = turkSubmitTo;
	}
	public void setTutorialStep(int tutorialStep) {
		this.tutorialStep = tutorialStep;
	}
	public void setWorkerId(String workerId) {
		this.workerId = workerId;
	}
	public void setWorkerNumber(String workerNumber) {
		this.workerNumber = workerNumber;
	}
	public String getWorkerNumber() {
		return workerNumber;
	}
	public int getTempteeBonus() {
		return tempteeBonus;
	}
	public void setTempteeBonus(int tempteeBonus) {
		this.tempteeBonus = tempteeBonus;
	}
	public int getCurrentBetrayPayoff() {
		return currentBetrayPayoff;
	}
	public void setCurrentBetrayPayoff(int currentBetrayPayoff) {
		this.currentBetrayPayoff = currentBetrayPayoff;
	}
	public int getMaxBetrayPayoff() {
		return maxBetrayPayoff;
	}
	public void setMaxBetrayPayoff(int maxBetrayPayoff) {
		this.maxBetrayPayoff = maxBetrayPayoff;
	}
	public void setSlotNumber(int slotNumber) {
		this.slotNumber = slotNumber;
	}
	public int getSlotNumber() {
		return slotNumber;
	}
}
