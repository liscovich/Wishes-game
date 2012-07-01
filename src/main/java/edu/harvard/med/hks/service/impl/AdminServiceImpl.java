package edu.harvard.med.hks.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.harvard.med.hks.dao.HksGameDao;
import edu.harvard.med.hks.dao.SlotDao;
import edu.harvard.med.hks.model.Game;
import edu.harvard.med.hks.model.Slot;
import edu.harvard.med.hks.model.Slot.Status;
import edu.harvard.med.hks.server.GeneralException;
import edu.harvard.med.hks.service.AdminService;

@Service
public class AdminServiceImpl implements AdminService {
	@Autowired HksGameDao hksGameDao;
	@Autowired private SlotDao slotDao;

	@Override
	public void createHksGame(HttpServletRequest req) throws GeneralException {
		Game game = new Game();
		game.setGameId(req.getParameter("gameId"));
		game.setRewardPayoff(Integer.parseInt(req.getParameter("rewardPayoff")));
		game.setBlackMarkUpperLimit(Integer.parseInt(req.getParameter("blackMarkUpperLimit")));
		game.setBetrayCaughtChance(Double.parseDouble(req.getParameter("betrayCaughtChance")));
		game.setInitTempteeBonus(Integer.parseInt(req.getParameter("initTempteeBonus")));
		game.setMaxBetrayPayoff(Integer.parseInt(req.getParameter("maxBetrayPayoff")));
		game.setNumberOfGameSlot(Integer.parseInt(req.getParameter("numberOfGameSlot")));
		game.setRewardCaughtAsBetrayalChance(Double.parseDouble(req.getParameter("rewardCaughtAsBetrayalChance")));
		game.setTempteeSurvivalChance(Double.parseDouble(req.getParameter("tempteeSurvivalChance")));
		
		game.setInitialTrusterBonus(Integer.parseInt(req.getParameter("initialTrusterBonus")));
		game.setExchangeRate(Float.parseFloat(req.getParameter("exchangeRate")));
		game.setBetrayalCost(Integer.parseInt(req.getParameter("betrayalCost")));
		game.setMaxRoundsNum(Integer.parseInt(req.getParameter("maxRoundsNum")));
		
		if (!hksGameDao.getByProperty("gameId", game.getGameId()).isEmpty()) {
			throw new GeneralException("Game ID is duplicated with another game");
		}
		hksGameDao.addObject(game);
		for (int i = 0; i < game.getNumberOfGameSlot(); i++) {
			Slot slot = new Slot();
			slot.setSlotNumber(i + 1);
			slot.setStatus(Status.INIT.toString());
			slot.setSlotId(UUID.randomUUID().toString());
			slot.setGame(game);
			slot.setRewardPayoff(game.getRewardPayoff());
			slot.setBetrayCaughtChance(game.getBetrayCaughtChance());
			slot.setBlackMarkUpperLimit(game.getBlackMarkUpperLimit());
			slot.setInitTempteeBonus(game.getInitTempteeBonus());
			slot.setMaxBetrayPayoff(game.getMaxBetrayPayoff());
			slot.setRewardCaughtAsBetrayalChance(game.getRewardCaughtAsBetrayalChance());
			slot.setTempteeSurvivalChance(game.getTempteeSurvivalChance());
			slotDao.addObject(slot);
		}
	}

	@Override
	public void deleteGame(String gid) throws GeneralException {
		List<Game> byProperty = hksGameDao.getByProperty("gameId", gid);
		if (!byProperty.isEmpty()) {
			Game game = byProperty.get(0);
			for (Slot slot : game.getSlots()) {
				slotDao.deleteObject(slot);
			}
			hksGameDao.deleteObject(game);
		}
	}

	@Override
	public Map<String, Object> getHksGames(HttpServletRequest req) throws GeneralException {
		List<Game> all = hksGameDao.getAllWithOrder("updated", true);
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		Map<String, Object> result = new HashMap<String, Object>();
		String gameId = req.getParameter("gameId");
		for (Game hksGame : all) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("rewardPayoff", hksGame.getRewardPayoff());
			map.put("betrayCaughtChance", hksGame.getBetrayCaughtChance());
			map.put("blackMarkUpperLimit", hksGame.getBlackMarkUpperLimit());
			map.put("gameId", hksGame.getGameId());
			map.put("initTempteeBonus", hksGame.getInitTempteeBonus());
			map.put("maxBetrayPayoff", hksGame.getMaxBetrayPayoff());
			map.put("rewardCaughtAsBetrayalChance", hksGame.getRewardCaughtAsBetrayalChance());
			map.put("tempteeSurvivalChance", hksGame.getTempteeSurvivalChance());
			map.put("numberOfGameSlot", hksGame.getNumberOfGameSlot());
			map.put("initialTrusterBonus", hksGame.getInitialTrusterBonus());
			map.put("betrayalCost", hksGame.getBetrayalCost());
			map.put("exchangeRate", hksGame.getExchangeRate());
			map.put("maxRoundsNum", hksGame.getMaxRoundsNum());
			
			list.add(map);
			if (!StringUtils.isEmpty(gameId) && gameId.equals(hksGame.getGameId())) {
				List<Map<String, Object>> slotList = new ArrayList<Map<String,Object>>();
				List<Slot> slots = hksGame.getSlots();
				for (Slot slot : slots) {
					Map<String, Object> slotMap = new HashMap<String, Object>();
					slotMap.put("blackMarkCount", slot.getBlackMarkCount());
					slotMap.put("blackMarkUpperLimit", slot.getBlackMarkUpperLimit());
					slotMap.put("currentBetrayPayoff", slot.getCurrentBetrayPayoff());
					slotMap.put("lastAction", slot.getLastAction());
					slotMap.put("slotId", slot.getSlotId());
					slotMap.put("slotNumber", slot.getSlotNumber());
					slotMap.put("status", slot.getStatus());
					slotMap.put("tempteeBonus", slot.getTempteeBonus());
					slotMap.put("workerNumber", slot.getWorkerNumber());
					slotMap.put("betrayCaught", slot.isBetrayCaught());
					slotMap.put("rewardCaughtAsBetrayal", slot.isRewardCaughtAsBetrayal());
					slotMap.put("survival", slot.isSurvival());
					slotMap.put("workerId", slot.getWorkerId());
					slotList.add(slotMap);
					String slotId = req.getParameter("slotId");
					if (!StringUtils.isEmpty(slotId) && slotId.equals(slot.getSlotId())) {
						Map<String, Object> currentSlot = new HashMap<String, Object>();
						currentSlot.put("report", slot.getPlayerReportFormatted());
						if (slot.getLog() == null) {
							currentSlot.put("log", new String());
						} else {
							currentSlot.put("log", new String(slot.getLog()));
						}
						result.put("currentSlot", currentSlot);
					}
				}
				result.put("slots", slotList);
			}
		}
		result.put("hksGames", list);
		return result ;
	}

	public void setHksGameDao(HksGameDao hksGameDao) { this.hksGameDao = hksGameDao; }

	public void setSlotDao(SlotDao slotDao) { this.slotDao = slotDao; }
}