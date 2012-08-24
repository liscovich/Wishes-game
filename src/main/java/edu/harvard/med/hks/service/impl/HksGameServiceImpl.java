package edu.harvard.med.hks.service.impl;

import java.net.URLDecoder;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.harvard.med.hks.dao.FeedbackDao;
import edu.harvard.med.hks.dao.HksGameDao;
import edu.harvard.med.hks.dao.SlotDao;
import edu.harvard.med.hks.model.Feedback;
import edu.harvard.med.hks.model.Game;
import edu.harvard.med.hks.model.Slot;
import edu.harvard.med.hks.model.Slot.Status;
import edu.harvard.med.hks.server.GeneralException;
import edu.harvard.med.hks.service.HksGameService;

@Service
public class HksGameServiceImpl implements HksGameService {
	
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(HksGameServiceImpl.class);

	@Autowired HksGameDao hksGameDao;
	@Autowired private SlotDao slotDao;
	@Autowired private FeedbackDao feedbackDao;

	private int roundsPlayed = 0;

	private void appendLog(Slot slot, String log) {
		String gameLog = "";
		if(slot.getLog() != null) gameLog = new String(slot.getLog()) ;
		gameLog += new Date().toString() + "\t"+log + "\n";
		slot.setLog(gameLog.getBytes());
	}

	private String getClientInfo(HttpServletRequest req) {
		StringBuilder sb = new StringBuilder();
		//sb.append("Remote addr. | Remote port | Remote Host | Remote user | Cookies | User Agent | OS    | Pixels \n");

		sb.append("  Remote addr = ").append(req.getRemoteAddr()).append("\n");
		sb.append("  Remote port = ").append(req.getRemotePort()).append("\n");
		sb.append("  Remote Host = ").append(req.getRemoteHost()).append("\n");
		sb.append("  Remote user = ").append(req.getRemoteUser()).append("\n");
		sb.append("  Cookies = ").append(req.getHeader("Cookie")).append("\n");
		sb.append("  User Agent = ").append(req.getHeader("User-Agent")).append("\n");
		sb.append("  OS = ").append(req.getHeader("UA-OS")).append("\n");
		sb.append("  Pixels = ").append(req.getHeader("UA-Pixels")).append("\n");
		return sb.toString() ;
	}

	private void outputSlotData(Slot slot){
		StringBuilder sb = new StringBuilder();

		sb.append("Slot ID\tSTatus\tGame ID\tSlot Number\tWorker Number\tWorker ID\tBlack Mark Count\tBetray Payoff\n");
		sb.append(slot.getSlotId()).append("\t").append(slot.getStatus()).append("\t").
		append(slot.getGame().getId()).append("\t").append(slot.getSlotNumber()).append("\t").
		append(slot.getWorkerNumber()).append("\t").append(slot.getWorkerId()).append("\t").
		append(slot.getBlackMarkCount()).append("\t").append(slot.getCurrentBetrayPayoff()).
		append("\n");

		appendLog(slot, sb.toString());
	}

	@Override
	public Map<String, Object> betray(HttpServletRequest req) throws GeneralException {
		Map<String, Object> result = new HashMap<String, Object>();
		List<Slot> byProperty = slotDao.getByProperty("slotId", req.getParameter("slotId"));
		if(byProperty.isEmpty()) return result;
		Slot slot = byProperty.get(0);
		if(!slot.getGame().getGameId().equals(req.getParameter("gameId"))) {
			result.put("status", Status.DROPPED);
			return result;
		}
		if(!slot.getStatus().equals(Status.PLAY.toString())) {
			return result;
		}
		slot.setLastAction("Betray");

		if(slot.getBetrayCaughtSampling() <= slot.getBetrayCaughtChance()) {
			slot.setBlackMarkCount(slot.getBlackMarkCount() + 1);
			slot.setBetrayCaught(true);
			appendLog(slot, "Betray caught. Number of black marks: " + slot.getBlackMarkCount());
		} else {
			slot.setBetrayCaught(false);
		}
		slot.setSurvival(slot.getSurvivalSampling() <= slot.getTempteeSurvivalChance());
		appendLog(slot, "Survive to next round: " + (slot.getSurvivalSampling() <= slot.getTempteeSurvivalChance()));
		slot.setRewardCaughtAsBetrayal(false);
		slot.setTempteeBonus(slot.getTempteeBonus() + slot.getCurrentBetrayPayoff());
		appendLog(slot, "Betray and earn " + (slot.getCurrentBetrayPayoff()));

		if(slot.getBlackMarkCount() < slot.getGame().getBlackMarkUpperLimit()) {
		  slot.setStatus(Status.PAYOFF.toString());
		} else {
			slot.setStatus(Status.FINISHED.toString());
		}
		
	  //Log player report for each round
		Slot.PlayerReport pReport = slot.getPlayerReport() ;
		Slot.PlayerRoundReport roundReport = pReport.getPlayerRoundReport(slot, slot.getCurrentRound(), false) ;
		roundReport.setRemainingWishes(slot.getGame().getBlackMarkUpperLimit() - slot.getBlackMarkCount());
		roundReport.setChoice(1) ;
		roundReport.setBalance(slot.getTempteeBonus()) ;
		roundReport.setStatus(slot.getStatus()) ;
		slot.setPlayerReport(pReport) ;
		
		slotDao.update(slot) ;
		output(result, slot) ; 
		return result;
	}

	public void doneTutorial(HttpServletRequest req) throws GeneralException {
		List<Slot> byProperty = slotDao.getByProperty("slotId", req.getParameter("slotId"));
		if (byProperty.isEmpty()) return;
		Slot slot = byProperty.get(0);
		if (!slot.getGame().getGameId().equals(req.getParameter("gameId"))) {
			return;
		}
		slot.setStatus(Status.PLAY.toString());
		slot.setPractice(true) ;
		
		Slot.PlayerReport pReport = slot.getPlayerReport() ;
		pReport.finishTutorial() ;
		slot.setPlayerReport(pReport) ;
		slotDao.update(slot);
	}
	
	public Map<String, Object> finishPractice(HttpServletRequest req) throws GeneralException {
		Map<String, Object> result = new HashMap<String, Object>();
		List<Slot> byProperty = slotDao.getByProperty("slotId", req.getParameter("slotId"));
		if(byProperty.isEmpty()) {
			result.put("status", Status.DROPPED);
			return result ;
		}
		Slot slot = byProperty.get(0);
		if(!slot.getGame().getGameId().equals(req.getParameter("gameId"))) {
			result.put("status", Status.DROPPED);
			return result ;
		}
		initPlay(slot);
		slot.setPractice(false) ;
		slot.setCurrentRound(1) ;
		slotDao.update(slot);
		output(result, slot);
		return result;
	}

	private String fillZero(double number) {
		if (number == 1) number = 0.999;
		String result = "" + ((int) (number * 1000));
		while (result.length() < 3) {
			result = "0" + result;
		}
		return result;
	}
	
	public Slot findEmptySlotForWorker(String gameId, String workerId) throws GeneralException {
		/*
		 1. user/player/worker enters the game through the url with two appended parameters: gameid, and workerid
		 2. the server assigns them a new slot and appends the slot id to the URL of the player
		 3. player goes through a tutorial, practice game, and a real game
		 4. after the real game the player can click on a button "play again"
		 5. in this case he is forwarded to the same URL that he used to enter the game originally
		 6. server assigns a new slot to this player and appends the new slot id to players URL as 
		    long as the player has not exceeded the total allowed number of players, and as long 
		    as there are slots free slots available
		 7. when playing the game for the second, third or later times, the players is not shown the 
		    tutorial and the practice game
		 8. when they play the last allowed game, at the end of the game, the button "play again" does not appear
		 */
		if (StringUtils.isEmpty(workerId)) return null;
		List<Game> byProperty = hksGameDao.getByProperty("gameId", gameId);
		if (byProperty.isEmpty()) return null;
		Game game = byProperty.get(0);
		Map<String, Object> rect = new HashMap<String, Object>();
		rect.put("game", game);
		//rect.put("status", Status.INIT.toString());
		List<Slot> slots = slotDao.getByProperties(rect, "slotNumber", true);
		if(slots.isEmpty()) return null ;
		List<Slot> freeSlots = new ArrayList<Slot>() ;
	  List<Slot> workerPlayedSlots = new ArrayList<Slot>() ;
	  for(int i = 0; i < slots.size(); i++) {
	  	Slot slot = slots.get(i);
	  	if(Slot.Status.INIT.toString().equals(slot.getStatus())) {
	  		freeSlots.add(slot) ;
	  	} else if(slot.getWorkerId().equals(workerId)) {
	  		workerPlayedSlots.add(slot) ;
	  	}
	  }
	  if(freeSlots.size() == 0) return null; 
	  if(!workerId.equals("admin7") && workerPlayedSlots.size() >= game.getMaxRoundsNum()) return null ;
		Slot slot = freeSlots.get(0);
		if(workerPlayedSlots.size() == 0 || "admin7".equals(workerId)) {
		  slot.setStatus(Status.INIT.toString());
		} else {
			slot.setStatus(Status.PLAY.toString());
		}
		slot.setWorkerPlayTracker(workerPlayedSlots.size() + 1) ;
		return slot;
	}
	
	private void output(Map<String, Object> result, Slot slot) {
		NumberFormat formatter = NumberFormat.getInstance();
		formatter.setMaximumFractionDigits(2);
		result.put("status", slot.getStatus());
		result.put("currentBetrayPayoff", formatter.format(slot.getCurrentBetrayPayoff()));
		result.put("blackMarkCount", slot.getBlackMarkCount());
		result.put("tempteeBonus", formatter.format(slot.getTempteeBonus()));
		result.put("version", slot.getUpdated().getTime());
		result.put("tutorialStep", slot.getTutorialStep());
		result.put("survival", slot.isSurvival());
		result.put("rewardCaughtAsBetrayal", slot.isRewardCaughtAsBetrayal());
		result.put("lastAction", slot.getLastAction());
		result.put("betrayCaught", slot.isBetrayCaught());
		result.put("rewardPayoff", slot.getRewardPayoff());
		result.put("betrayNotCaughtChance", 1 - slot.getBetrayCaughtChance());
		result.put("rewardNotCaughtAsBetrayalChance", 1 - slot.getRewardCaughtAsBetrayalChance());
		result.put("endChance", 1 - slot.getTempteeSurvivalChance());
		result.put("blackMarkUpperLimit", slot.getBlackMarkUpperLimit());
		result.put("betrayNotCaughtSampling", fillZero(1 - slot.getBetrayCaughtSampling()));
		result.put("rewardNotCaughtAsBetrayalSampling", fillZero(1 - slot.getRewardCaughtAsBetrayalSampling()));
		result.put("notContinueSampling", fillZero(1 - slot.getSurvivalSampling()));
		result.put("currentRound", slot.getCurrentRound());
		result.put("maxBetrayPayoff", slot.getMaxBetrayPayoff());
		result.put("mturkRate", slot.getGame().getExchangeRate());
		result.put("feedbackBonus", slot.getGame().getFeedbackBonus());
		result.put("practice", slot.getPractice());
		result.put("gameCanPlay", (slot.getGame().getMaxRoundsNum() - slot.getWorkerPlayTracker()));
		boolean lastGame = !"admin7".equals(slot.getWorkerId()) && slot.getWorkerPlayTracker() >= slot.getGame().getMaxRoundsNum();
		result.put("lastGameForPlayer", lastGame);
	}

	@Override
	public Map<String, Object> payoffAck(HttpServletRequest req) throws GeneralException {
		Map<String, Object> result = new HashMap<String, Object>();
		List<Slot> byProperty = slotDao.getByProperty("slotId", req.getParameter("slotId"));
		if (byProperty.isEmpty())	return result;
		Slot slot = byProperty.get(0);
		if (!slot.getGame().getGameId().equals(req.getParameter("gameId"))) {
			result.put("status", Status.DROPPED);
			return result;
		}
		
		if (slot.isSurvival()) {
		  //Log player, finish a round
			Slot.PlayerReport pReport = slot.getPlayerReport() ;
			Slot.PlayerRoundReport roundReport = pReport.getPlayerRoundReport(slot, slot.getCurrentRound(), false) ;
			roundReport.setAnotherRound(1) ;
			roundReport.setStatus(slot.getStatus()) ;
			slot.setCurrentRound(slot.getCurrentRound() + 1);
			slot.setStatus(Status.PLAY.toString());
			samplingNewRound(slot);
			//Log player, init a new round
			roundReport = pReport.getPlayerRoundReport(slot, slot.getCurrentRound(), true) ;
			roundReport.setLowPayoff(slot.getGame().getRewardPayoff());
			roundReport.setBetrayPayoff(slot.getCurrentBetrayPayoff() - roundReport.getLowPayoff()) ;
			roundReport.setHighPayoff(slot.getCurrentBetrayPayoff());
			roundReport.setStatus(slot.getStatus()) ;
			slot.setPlayerReport(pReport) ;
			slotDao.update(slot);
		} else {
			slot.setStatus(Status.FINISHED.toString());
		}
		output(result, slot);
		return result;
	}

	@Override
	public Map<String, Object> endChanceAck(HttpServletRequest req) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<Slot> byProperty = slotDao.getByProperty("slotId", req.getParameter("slotId"));
		if (byProperty.isEmpty()) return result;
		Slot slot = byProperty.get(0);
		if (!slot.getGame().getGameId().equals(req.getParameter("gameId"))) {
			result.put("status", Status.DROPPED);
			return result;
		}

		/*slot.setSurvival(slot.getSurvivalSampling() <= slot.getTempteeSurvivalChance());*/

		if (roundsPlayed < slot.getGame().getMaxRoundsNum()){
			slot.setSurvival(true);
			roundsPlayed++;
		} else {
			slot.setSurvival(false);
		}

		//appendLog(slot, "Survive to next round: " + 
		//	(slot.getSurvivalSampling() <= slot.getTempteeSurvivalChance()));

		appendLog(slot, "Survive to next round: " + slot.isSurvival());

		if (slot.isSurvival()) {
			slot.setCurrentRound(slot.getCurrentRound() + 1);
			slot.setStatus(Status.PLAY.toString());
			samplingNewRound(slot);
		} else {
			slot.setStatus(Status.FINISHED.toString());
		}
		output(result, slot);
		return result;
	}

	private void samplingNewRound(Slot slot) {
		Random RANDOM = new Random() ;
		int radomBetrayPayoff = slot.getRewardPayoff() + (RANDOM.nextInt(slot.getMaxBetrayPayoff()) + 1);
		//System.out.println("currentBetrayPayoff  = " + radomBetrayPayoff);
		slot.setCurrentBetrayPayoff(radomBetrayPayoff);
		slot.setSurvivalSampling(RANDOM.nextDouble());
		slot.setBetrayCaughtSampling(RANDOM.nextDouble());
		slot.setRewardCaughtAsBetrayalSampling(RANDOM.nextDouble());
	}
	
	private void initPlay(Slot slot) {
		slot.setStatus(Status.PLAY.toString());
		slot.setSurvival(true);
		slot.setLastAction("");
		slot.setBetrayCaught(false);
		slot.setCurrentRound(1) ;
		slot.setRewardCaughtAsBetrayalSampling(466);
		slot.setBetrayCaughtSampling(713);
		slot.setBlackMarkCount(0) ;
		slot.setTempteeBonus(slot.getInitTempteeBonus());
		samplingNewRound(slot);
	}

	@Override
	public Map<String, Object> reward(HttpServletRequest req) throws GeneralException {
		Map<String, Object> result = new HashMap<String, Object>();
		List<Slot> byProperty = slotDao.getByProperty("slotId", req.getParameter("slotId"));
		if (byProperty.isEmpty()) {
			result.put("status", Status.DROPPED);
			return result;
		}
		Slot slot = byProperty.get(0);
		if (!slot.getGame().getGameId().equals(req.getParameter("gameId"))) {
			result.put("status", Status.DROPPED);
			return result;
		}
		if (!slot.getStatus().equals(Status.PLAY.toString())) {
			return result;
		}

		slot.setLastAction("Reward");
		appendLog(slot, "Reward");
		if (slot.getRewardCaughtAsBetrayalSampling() <= slot.getRewardCaughtAsBetrayalChance()) {
			slot.setBlackMarkCount(slot.getBlackMarkCount() + 1);
			slot.setRewardCaughtAsBetrayal(true);
			appendLog(slot, "Reward but caught as betrayal. Number of black marks: " + slot.getBlackMarkCount());
		} else {
			slot.setRewardCaughtAsBetrayal(false);
		}
		slot.setSurvival(slot.getSurvivalSampling() <= slot.getTempteeSurvivalChance());
		appendLog(slot, "Survive to next round: " + (slot.getSurvivalSampling() <= slot.getTempteeSurvivalChance()));
		slot.setBetrayCaught(false);
		slot.setTempteeBonus(slot.getTempteeBonus() + slot.getRewardPayoff());
		slot.setStatus(Status.PAYOFF.toString());
		
	  //Log player report for each round
		Slot.PlayerReport pReport = slot.getPlayerReport() ;
		Slot.PlayerRoundReport roundReport = pReport.getPlayerRoundReport(slot, slot.getCurrentRound(), false) ;
		roundReport.setChoice(0) ;
		roundReport.setRemainingWishes(slot.getGame().getBlackMarkUpperLimit() - slot.getBlackMarkCount());
		roundReport.setBalance(slot.getTempteeBonus()) ;
		roundReport.setStatus(slot.getStatus()) ;
		slot.setPlayerReport(pReport) ;
		
		slotDao.update(slot);
		output(result, slot);
		return result;
	}

	public Map<String, Object> trackTutorial(HttpServletRequest req) throws GeneralException {
		String screenNo = req.getParameter("screenNo") ;
		Map<String, Object> result = new HashMap<String, Object>();
		List<Slot> byProperty = slotDao.getByProperty("slotId", req.getParameter("slotId"));
		if (byProperty.isEmpty()) return result;
		Slot slot = byProperty.get(0);
		int step = Integer.parseInt(screenNo) ;
		String action = req.getParameter("tutorialAction") ;
		Slot.PlayerReport pReport = slot.getPlayerReport() ;
		Slot.TutorialReport tutorialReport = pReport.addTutorialReport(step, action);
		slot.setPlayerReport(pReport) ;
		appendLog(slot, tutorialReport.toString());
		slotDao.update(slot) ;
		outputSlotData(slot);
		return result;
	}
	
	public Map<String, Object> sendFeedback(HttpServletRequest req) throws GeneralException {
		Map<String, Object> result = new HashMap<String, Object>();
		List<Slot> byProperty = slotDao.getByProperty("slotId", req.getParameter("slotId"));
		if (byProperty.isEmpty()) {
			result.put("status", Status.DROPPED);
			return result;
		}
		
		Slot slot = byProperty.get(0);
		if(!slot.getGame().getGameId().equals(req.getParameter("gameId"))) {
			result.put("status", Status.DROPPED);
			return result;
		}
		
		Map<String, Object> rect = new HashMap<String, Object>();
		rect.put("slot", slot);
		List<Feedback> feedbacks = feedbackDao.getByProperties(rect, "slot", true);
		Feedback feedback = null ;
		if(feedbacks.size() == 0) {
			feedback = new Feedback() ;
			feedback.setGame(slot.getGame()) ;
			feedback.setSlot(slot) ;
		} else {
			feedback = feedbacks.get(0); 
		}
		feedback.setAge(parseInt(req.getParameter("age"), -1)) ;
		feedback.setGender(Integer.parseInt(req.getParameter("gender"))) ;
		feedback.setNativeLanguage(Integer.parseInt(req.getParameter("nativeLanguage"))) ;
		feedback.setInstruction(Integer.parseInt(req.getParameter("instr"))) ;
		feedback.setInteresting(Integer.parseInt(req.getParameter("inter"))) ;
		feedback.setSpeed(Integer.parseInt(req.getParameter("speed"))) ;
		String strat = URLDecoder.decode(req.getParameter("strat")) ;
		feedback.setStrategy(strat) ;
		String think = URLDecoder.decode(req.getParameter("think")) ;
		feedback.setThoughts(think) ;
		if(feedbacks.size() == 0) {
		  feedbackDao.addObject(feedback) ;
		} else {
			feedbackDao.update(feedback) ;
		}
		appendLog(slot, "Feedback from player:\n" + feedback.getThoughts());
		outputSlotData(slot);
		return result;
	}

	public void setHksGameDao(HksGameDao hksGameDao) { this.hksGameDao = hksGameDao; }

	public void setSlotDao(SlotDao slotDao) { this.slotDao = slotDao; }
	
	@Override
	public Map<String, Object> update(HttpServletRequest req)throws GeneralException {
		List<Slot> byProperty = slotDao.getByProperty("slotId", req.getParameter("slotId"));
		Map<String, Object> result = new HashMap<String, Object>();
		if(byProperty.isEmpty()) {
			result.put("status", Status.DROPPED);
			return result;
		}
		
		Slot slot = byProperty.get(0);
		
		if (!slot.getGame().getGameId().equals(req.getParameter("gameId"))) {
			result.put("status", Status.DROPPED);
			return result;
		}
		
		boolean init = slot.getStatus().equals(Status.INIT.toString()) ;
		boolean tutorial = slot.getStatus().equals(Status.TUTORIAL.toString()) ;
		if(init) {
			slot.setStatus(Status.TUTORIAL.toString());
			slot.setTempteeBonus(slot.getInitTempteeBonus());
			samplingNewRound(slot);
			
			appendLog(slot, getClientInfo(req));
			Slot.PlayerReport pReport = slot.getPlayerReport() ;
			pReport.setClientInfo(getClientInfo(req)) ;
			slot.setPlayerReport(pReport) ;
		}
		
		if(slot.getStatus().equals(Status.PLAY.toString())) {
			initPlay(slot) ;
		}
		
		if (!StringUtils.isEmpty(req.getParameter("tutorialStep"))) {
			slot.setTutorialStep(Integer.parseInt(req.getParameter("tutorialStep")));
		}
		
		if (StringUtils.isEmpty(slot.getAssignmentId()) && !StringUtils.isEmpty(req.getParameter("assignmentId"))) {
			slot.setAssignmentId(req.getParameter("assignmentId"));
		}
		
		if (StringUtils.isEmpty(slot.getWorkerId()) && !StringUtils.isEmpty(req.getParameter("workerId"))) {
			slot.setWorkerId(req.getParameter("workerId"));
		}
		
		if (StringUtils.isEmpty(slot.getWorkerNumber()) && !StringUtils.isEmpty(req.getParameter("workerNum"))) {
			slot.setWorkerId(req.getParameter("workerNum"));
		}
		
		if (StringUtils.isEmpty(slot.getTurkSubmitTo()) && !StringUtils.isEmpty(req.getParameter("turkSubmitTo"))) {
			slot.setTurkSubmitTo(req.getParameter("turkSubmitTo"));
		}
		
		if (StringUtils.isEmpty(slot.getHitId()) && !StringUtils.isEmpty(req.getParameter("hitId"))) {
			slot.setHitId(req.getParameter("hitId"));
		}
		if(slot.getLog() == null) {
			appendLog(slot, "Game Id: " + slot.getGame().getGameId()) ;
			appendLog(slot, "Slot Id: " + slot.getSlotId()) ;
			appendLog(slot, "Worker Id: " + slot.getWorkerId()) ;
			appendLog(slot, "Assignment Id: " + slot.getAssignmentId()) ;
			appendLog(slot, "Hit Id: " + slot.getHitId()) ;
		}
	  //Log init a new round
		//slot.setPlayerReportJson(null) ;
		
		if(!init && !tutorial) {
			Slot.PlayerReport pReport = slot.getPlayerReport() ;
			Slot.PlayerRoundReport roundReport = pReport.getPlayerRoundReport(slot, slot.getCurrentRound(), true) ;
			roundReport.setLowPayoff(slot.getGame().getRewardPayoff());
			roundReport.setBetrayPayoff(slot.getCurrentBetrayPayoff() - roundReport.getLowPayoff()) ;
			roundReport.setHighPayoff(slot.getCurrentBetrayPayoff());
			roundReport.setStatus(slot.getStatus()) ;
			slot.setPlayerReport(pReport) ;
		}
		slotDao.update(slot);
		output(result, slot);
		return result;
	}

	public void setRoundsPlayed(int roundsPlayed) { this.roundsPlayed = roundsPlayed; }

	public int getRoundsPlayed() { return this.roundsPlayed; }

	public HksGameDao getGameDao() { return hksGameDao; }

	public SlotDao getSlotDao() { return slotDao; }
	
	private int parseInt(String string, int defaultVal) {
		try {
			return Integer.parseInt(string) ;
		} catch(Throwable t) {
		}
		return defaultVal; 
	}
}