package edu.harvard.med.hks.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import edu.harvard.med.hks.model.Feedback;
import edu.harvard.med.hks.model.Game;
import edu.harvard.med.hks.model.Slot;
import edu.harvard.med.hks.server.GeneralException;


public interface AdminService {
	void createHksGame(HttpServletRequest req) throws GeneralException;
	void deleteGame(String gid) throws GeneralException;
	Map<String, Object> getHksGames(HttpServletRequest req) throws GeneralException;
	List<Slot> findGameSlots(String gameId) throws GeneralException ;
	public Game getGame(String gameId) throws GeneralException ;
	public List<Game> getAllGames() throws GeneralException ;
	public List<Feedback> findFeedbacks(Game game) throws GeneralException ;
	public Feedback findFeedback(Slot slot) throws GeneralException ;
}
