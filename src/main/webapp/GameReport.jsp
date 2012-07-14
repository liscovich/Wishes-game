<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">

<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>

<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="edu.harvard.med.hks.model.Slot" %>
<%@ page import="edu.harvard.med.hks.model.Slot.PlayerReport" %>
<%@ page import="edu.harvard.med.hks.model.Slot.PlayerRoundReport" %>
<%@ page import="edu.harvard.med.hks.model.Game" %>
<%@ page import="edu.harvard.med.hks.model.Feedback" %>
<%@ page import="edu.harvard.med.hks.service.AdminService" %>

<%
  DecimalFormat CURR_FT = new DecimalFormat("#.00") ;
  List<Slot> slots = (List<Slot>)request.getAttribute("gameSlots") ;
  Game game = slots.get(0).getGame() ;
  AdminService service = (AdminService)request.getAttribute("service") ;
%>

<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="keyword" content="Game Report" />
    <meta name="description" content="Game Report" />
    <meta name="robots" content="all" />
    <title>Game Report</title>
    <style>
      table {
        border: 1px solid black ;
        border-collapse:collapse;
      }
      td, th {
        border: 1px solid black ;
        padding: 5px 10px ;
      }
    </style>
</script>
</head>
  <body>
    <h1>Game Parameters</h1>
    <table>
      <tr>
        <td>Game ID</td><td><%=game.getGameId()%></td>
      </tr>
      <tr>
        <td>Number of game slot</td><td><%=game.getNumberOfGameSlot()%></td>
      </tr>
      <tr>
        <td>Max Betray Payoff</td><td><%=game.getMaxBetrayPayoff()%></td>
      </tr>
      <tr>
        <td>Reward Paypoff</td><td><%=game.getRewardPayoff()%></td>
      </tr>
      <tr>
        <td>Initial Truster Bonus</td><td><%=game.getInitialTrusterBonus()%></td>
      </tr>
      <tr>
        <td>Chance of black mark for Betrayal</td><td><%=game.getBetrayCaughtChance()%></td>
      </tr>
      <tr>
        <td>Chance of black mark for Reward</td><td><%=game.getRewardCaughtAsBetrayalChance()%></td>
      </tr>
      <tr>
        <td>Betrayal Cost</td><td><%=game.getBetrayalCost()%></td>
      </tr>
      <tr>
        <td>Survival (0-1)</td><td><%=game.getTempteeSurvivalChance()%></td>
      </tr>
      <tr>
        <td>Black Mark Threshold</td><td><%=game.getBlackMarkUpperLimit()%></td>
      </tr>
      <tr>
        <td>Init Temptee Bonus</td><td><%=game.getInitTempteeBonus()%></td>
      </tr>
      <tr>
        <td>Exchange rate</td><td><%=game.getExchangeRate()%></td>
      </tr>
      <tr>
        <td>Max Rounds Number</td><td><%=game.getMaxRoundsNum()%></td>
      </tr>
    </table>

    <h1>Earnings Table</h1>

    <table>
        <tr>
          <th>Game Slot</th>
          <th>Status</th>
          <th>Worker Id</th>
          <th>Assignment Id</th>
          <th>Hit Id</th>
          <th>Earnings</th>
        </tr>
      <%for(int i = 0; i < slots.size(); i++) { %>
      <%  Slot slot = slots.get(i) ; %>
      <%  if("INIT".equals(slot.getStatus())) continue ; %>
          <tr>
            <td><%=slot.getSlotNumber()%></td>
            <td><%=slot.getStatus()%></td>
            <td><%=slot.getWorkerId()%></td>
            <td><%=slot.getAssignmentId()%></td>
            <td><%=slot.getHitId()%></td>
            <%
              double balance = 0 ;
              if(Slot.Status.FINISHED.toString().equals(slot.getStatus())) {
                balance = slot.getTempteeBonus() * game.getExchangeRate() ;
              }
            %>
            <td>$<%=CURR_FT.format(balance)%></td>
          </tr>
      <%}%>
    </table>

    <h1>Individual reports</h1>

    <%for(int i = 0; i < slots.size(); i++) { %>
    <%  Slot slot = slots.get(i) ; %>
    <%  if("INIT".equals(slot.getStatus())) continue ; %>
    <%  Iterator<Map.Entry<String, PlayerRoundReport>> itr = slot.getPlayerReport().getRoundReports().entrySet().iterator() ; %>
      <table style="margin: 10px">
        <tr>
          <th>Time</th>
          <th>Round</th>
          <th>Status</th>
          <th>Low Payoff</th>
          <th>Betray Payoff</th>
          <th>High Payoff</th>
          <th>Choice</th>
          <th>Another Round</th>
          <th>Remaining Wishes</th>
          <th>Balance</th>
        </tr>
        <%while(itr.hasNext()) { %>
        <%  Map.Entry<String, PlayerRoundReport> entry = itr.next() ;
            PlayerRoundReport rReport = entry.getValue() ; 
            if(rReport.getBalance() == 0) continue; 
            String anotherRound  = "" ;
            if(!Slot.Status.FINISHED.toString().equals(rReport.getStatus())) {
              anotherRound = "" + rReport.getAnotherRound() ;
            }

        %>
            <tr>
              <td><%=new Date(rReport.getTime())%></td>
              <td><%=rReport.getId()%></td>
              <td><%=rReport.getStatus()%></td>
              <td><%=rReport.getLowPayoff()%></td>
              <td><%=rReport.getBetrayPayoff()%></td>
              <td><%=rReport.getHighPayoff()%></td>
              <td><%=rReport.getChoice()%></td>
              <td><%=anotherRound%></td>
              <td><%=rReport.getRemainingWishes()%></td>
              <td><%=rReport.getBalance()%></td>
            </tr>
        <%}%>
        </table>
        <div style="padding-left: 10px; margin-bottom: 50px">
          <div>
            <%
              double balance = 0 ;
              if(Slot.Status.FINISHED.toString().equals(slot.getStatus())) {
                balance = slot.getTempteeBonus() * game.getExchangeRate() ;
              }
            %>
            Slot = <%=slot.getSlotNumber()%>; Worker Id =<%=slot.getWorkerId()%>;
            Earnings: $<%= CURR_FT.format(balance)%>
          </div>
          <div>
            <%
              Feedback fb = service.findFeedback(slot);
            %>
            Feedback: 
            <%if(fb != null) { %>
               Clarity of instructions(0 = not clear, 5 = clear) = <%=fb.getInstruction()%>;
               Interesting( 0 = not interesting, 5 = very interesting) = <%=fb.getInteresting()%>;
               Speed (0 = too slow, 5 = too fast) = <%=fb.getSpeed()%>;
               Strategy = <%=fb.getStrategy()%>;
               <br/>
               Think: <%=fb.getThoughts()%>
            <%}%>
          </div>
        </div>
    <%}%>
  </body>
</html>
