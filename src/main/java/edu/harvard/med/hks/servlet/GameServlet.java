package edu.harvard.med.hks.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.gson.Gson;

import edu.harvard.med.hks.model.Slot;
import edu.harvard.med.hks.server.GeneralException;
import edu.harvard.med.hks.service.HksGameService;

@SuppressWarnings("serial")
public class GameServlet extends HttpServlet {
	private static Logger logger = Logger.getLogger(GameServlet.class);
	private HksGameService hksGameService;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String gameId = req.getParameter("gameId");
		if (StringUtils.isEmpty(gameId)) {
			PrintWriter out = resp.getWriter();
			out.println("Unknown gameId");
			return;
		}
		
//		int points = (!StringUtils.isEmpty(req.getParameter("points")))?Integer.parseInt(req.getParameter("points")):0;
//		boolean isNext = (!StringUtils.isEmpty(req.getParameter("next")))?Boolean.valueOf(req.getParameter("next")) : false;
//		if (isNext && points > 0){
//			String slotId = req.getParameter("slotId");
//			Slot slot = hksGameService.getSlotDao().getByProperty("slotId",	slotId).get(0);
//			slot.setStatus(Status.OCCUPIED.toString());
//			slot.setSurvival(true);
//			slot.setBlackMarkCount(0);
//			 try {
//				hksGameService.getSlotDao().update(slot);
//			} catch (GeneralException e) {
//				e.printStackTrace();
//			}
//			resp.sendRedirect(req.getRequestURL().toString());
//		}
		
		try {
			Slot slot = hksGameService.findEmptySlotForWorker(gameId, req.getParameter("workerId"));
			//String slotId = hksGameService.getEmptySlotId(gameId, req.getParameter("workerId"));
			String slotId = null ;
			if(slot != null) {
				slotId = slot.getSlotId() ;
			}
			if (slotId == null) {
				PrintWriter out = resp.getWriter();
				out.println("Game is full");
			} else {
				String url = "index.html?gameId=" + URLEncoder.encode(gameId, "UTF-8") + "&slotId=" + URLEncoder.encode(slotId, "UTF-8");
				if(!StringUtils.isEmpty(req.getParameter("workerId"))) {
					url += "&workerId=" + URLEncoder.encode(req.getParameter("workerId"), "UTF-8");
				}
				if (!StringUtils.isEmpty(req.getParameter("assignmentId"))) {
					url += "&assignmentId=" + URLEncoder.encode(req.getParameter("assignmentId"), "UTF-8");
				}
				if (!StringUtils.isEmpty(req.getParameter("turkSubmitTo"))) {
					url += "&turkSubmitTo=" + URLEncoder.encode(req.getParameter("turkSubmitTo"), "UTF-8");
				}
				if (!StringUtils.isEmpty(req.getParameter("hitId"))) {
					url += "&hitId=" + URLEncoder.encode(req.getParameter("hitId"), "UTF-8");
				}
				resp.sendRedirect(url);
			}
		} catch (GeneralException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String gameId = req.getParameter("gameId");
		if (StringUtils.isEmpty(gameId)) return;
		String action = req.getParameter("a");
		if (StringUtils.isEmpty(action)) return;

		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (action.equals("update")) {
				result = hksGameService.update(req);
			} else if (action.equals("doneTutorial")) {
				hksGameService.doneTutorial(req);
			} else if (action.equals("finishPractice")) {
				result = hksGameService.finishPractice(req);
			} else if (action.equals("betray")) {
				result = hksGameService.betray(req);
			} else if (action.equals("reward")) {
				result = hksGameService.reward(req);
			} else if (action.equals("payoffAck")) {
				result = hksGameService.payoffAck(req);
			} else if (action.equals("endChanceAck")) {
				result = hksGameService.endChanceAck(req);
			} else if (action.equals("sendFeedback")) {
				result = hksGameService.sendFeedback(req);
			}
		} catch (GeneralException e) {
			result.put("error", e.getMessage());
		} catch (Exception e) {
			result.put("error", "Error happen! Please contact us liscovich@gmail.com!");
			logger.error(e.getMessage(), e);
		}
		String responseString = new Gson().toJson(result);
		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();
		out.println(responseString);
	}

	@Override
	public void init() throws ServletException {
		super.init();
		ApplicationContext applicationContext = 
			WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		hksGameService = (HksGameService) applicationContext.getBean("hksGameService");
	}
}
