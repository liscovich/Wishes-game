package edu.harvard.med.hks.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import edu.harvard.med.hks.server.GeneralException;


public interface AdminService {
	void createHksGame(HttpServletRequest req) throws GeneralException;

	void deleteGame(String gid) throws GeneralException;

	Map<String, Object> getHksGames(HttpServletRequest req)
			throws GeneralException;
}
