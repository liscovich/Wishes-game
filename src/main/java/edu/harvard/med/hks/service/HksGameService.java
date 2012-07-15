package edu.harvard.med.hks.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import edu.harvard.med.hks.dao.HksGameDao;
import edu.harvard.med.hks.dao.SlotDao;
import edu.harvard.med.hks.model.Slot;
import edu.harvard.med.hks.server.GeneralException;


public interface HksGameService {
	Map<String, Object> betray(HttpServletRequest req) throws GeneralException;

	void doneTutorial(HttpServletRequest req) throws GeneralException;
	
	Map<String, Object> finishPractice(HttpServletRequest req) throws GeneralException;
	
	Slot findEmptySlotForWorker(String gameId, String workerId) throws GeneralException;

	Map<String, Object> payoffAck(HttpServletRequest req) throws GeneralException ;

	Map<String, Object> reward(HttpServletRequest req) throws GeneralException;

	Map<String, Object> trackTutorial(HttpServletRequest req) throws GeneralException;
	
	Map<String, Object> sendFeedback(HttpServletRequest req) throws GeneralException;

	Map<String, Object> update(HttpServletRequest req) throws GeneralException;

	Map<String, Object> endChanceAck(HttpServletRequest req);
	
	void setRoundsPlayed(int roundsPlayed);
	
	int getRoundsPlayed();
	
	HksGameDao getGameDao();
	SlotDao getSlotDao();	
}
