package edu.harvard.med.hks.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.gson.Gson;

import edu.harvard.med.hks.model.Feedback;
import edu.harvard.med.hks.model.Game;
import edu.harvard.med.hks.model.Slot;
import edu.harvard.med.hks.server.GeneralException;
import edu.harvard.med.hks.service.AdminService;

@SuppressWarnings("serial")
public class AdminServlet extends HttpServlet {
	private static Logger logger = Logger.getLogger(AdminServlet.class);
	private ApplicationContext applicationContext;
	private AdminService adminService;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String action = req.getParameter("a");
		try {
			if("gameReport".equals(action)) {
				String gameId = req.getParameter("gameId") ;
				List<Slot> slots = adminService.findGameSlots(gameId) ;
				req.setAttribute("gameSlots", slots) ;
				req.setAttribute("service", adminService) ;
				req.getRequestDispatcher("GameReport.jsp").forward(req, resp) ;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e) ;
			PrintWriter out = resp.getWriter();
			out.println(e.getMessage());
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String action = req.getParameter("a");
		if (action == null || action.isEmpty()) {
			return;
		}

		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (action.equals("createGame")) {
				adminService.createHksGame(req);
			} else if (action.equals("getHksGames")) {
				result = adminService.getHksGames(req);
			} else if (action.equals("deleteGame")) {
				adminService.deleteGame(req.getParameter("gameId"));
			} else if(action.equals("gameReport")) {
				req.getRequestDispatcher("GameReport.jsp").forward(req, resp) ;
			}
		} catch (GeneralException e) {
			result.put("error", e.getMessage());
		} catch (Exception e) {
			result.put("error", "Server Error");
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
		applicationContext = WebApplicationContextUtils
			.getWebApplicationContext(getServletContext());
		adminService = (AdminService) applicationContext.getBean("adminService");
	}
}
