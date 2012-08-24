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
<%@ page import="edu.harvard.med.hks.model.Slot.TutorialReport" %>
<%@ page import="edu.harvard.med.hks.model.Game" %>
<%@ page import="edu.harvard.med.hks.model.Feedback" %>
<%@ page import="edu.harvard.med.hks.service.AdminService" %>

<%!
  public String formatNull(String string) {
    if(string == null) return "" ;
    return string ;
  }
%>
<%
  DecimalFormat DEC_FT = new DecimalFormat("#.00") ;
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
        <td>Number of game slots</td><td><%=game.getNumberOfGameSlot()%></td>
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
        <td>Feedback Bonus</td><td><%=game.getFeedbackBonus()%></td>
      </tr>
      <tr>
        <td>Max Rounds Number</td><td><%=game.getMaxRoundsNum()%></td>
      </tr>
    </table>

    <h1>Cumulative Report</h1>

    <table>
        <tr>
          <th rowspan="2">Game Slot</th>
          <th rowspan="2">Status</th>
          <th rowspan="2">Worker Id</th>
          <th rowspan="2">Assignment Id</th>
          <th rowspan="2">Hit Id</th>
          <th rowspan="2">Earnings</th>
          <th colspan="11">Tutorial Screen (sec)</th>
          <th colspan="8">Feedback</th>
          <th colspan="2">Client Info</th>
        </tr>
        <tr>
          <th>0</th>
          <th>1</th>
          <th>2</th>
          <th>3</th>
          <th>4</th>
          <th>5</th>
          <th>6</th>
          <th>7</th>
          <th>8</th>
          <th>9</th>
          <th>Total Duration</th>

          <th>Clarity<br/>(5=high)</th>
          <th>Interest<br/>(5=high)</th>
          <th>Speed<br/>(5=high)</th>
          <th>Strategy</th>
          <th>Thoughts</th>
          <th>Age</th>
          <th>Gender<br/>(1=male)</th>
          <th>English<br/>(1=yes)</th>
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
            <td>$<%=DEC_FT.format(balance)%></td>
            <%// Tutorial columns %>
            <%Iterator<TutorialReport> tutReItr = slot.getPlayerReport().getMergeTutorialReports().values().iterator() ; %>
            <%double timeToGoThroughTutorial = 0 ; %>
            <%int counter = 0 ; %>
            <%while(tutReItr.hasNext()) { %>
            <%  TutorialReport cEntry = tutReItr.next() ; %>
            <%  double time = cEntry.mergeDuration()/(double)1000 ; %>
            <%  if(time < 0) time = 0; %>
            <%  timeToGoThroughTutorial += time ; %>
            <%  counter++ ; %>
                <td><%=time%></td>
            <%}%>

            <%for(int k = counter; k <= 9; k++) { %>
              <td></td>
            <%}%>

            <td><%=DEC_FT.format(timeToGoThroughTutorial)%></td>

            <%// Feedback columns %>
            <%
              Feedback fb = service.findFeedback(slot);
            %>
            <%if(fb != null) { %>
              <td><%=fb.getInstruction()%></td>
              <td><%=fb.getInteresting()%></td>
              <td><%=fb.getSpeed()%></td>
              <td><%=fb.getStrategy()%></td>
              <td><%=fb.getThoughts()%></td>
              <td><%=fb.getAge()%></td>
              <td><%=fb.getGender()%></td>
              <td><%=fb.getNativeLanguage()%></td>
            <%} else {%>
              <td></td>
              <td></td>
              <td></td>
              <td></td>
              <td></td>
              <td></td>
              <td></td>
              <td></td>
            <%}%>
            <td><pre><%=formatNull(slot.getPlayerReport().getClientInfo())%></pre></td>
          </tr>
      <%}%>
    </table>

    <h1>Individual reports</h1>

    <%for(int i = 0; i < slots.size(); i++) { %>
    <%  Slot slot = slots.get(i) ; %>
    <%  if("INIT".equals(slot.getStatus())) continue ; %>
    <%  Iterator<TutorialReport> tutReItr = slot.getPlayerReport().getTutorialReports().values().iterator() ; %>
      <h2>Slot <%=slot.getSlotNumber()%></h2>
      <table style="margin: 10px">
        <tr>
          <th>Start Time</th>
          <th>End Time</th>
          <th>Duration (sec)</th>
          <th>Screen No</th>
          <th>Action</th>
        </tr>
        <%double timeToGoThroughTutorial = 0 ; %>
        <%while(tutReItr.hasNext()) { %>
        <%  TutorialReport cEntry = tutReItr.next() ; %>
        <%  double time = (cEntry.getEndTime() - cEntry.getStartTime())/(double)1000 ; %>
        <%  if(time < 0) time = 0; %>
        <%  timeToGoThroughTutorial += time ; %>
            <tr>
              <td><%=new Date(cEntry.getStartTime())%></td>
              <td><%=new Date(cEntry.getEndTime())%></td>
              <td><%=time%></td>
              <td><%=cEntry.getScreenNo()%></td>
              <td><%=cEntry.getAction()%></td>
            </tr>
        <%}%>
      </table>
      <div style="padding-left: 10px; margin-bottom: 20px">
        Tutorial Duration: <%=DEC_FT.format(timeToGoThroughTutorial)%> sec
      </div>
    <%  Iterator<Map.Entry<String, PlayerRoundReport>> itr = slot.getPlayerReport().getRoundReports().entrySet().iterator() ; %>
      <table style="margin: 10px">
        <tr>
          <th>Start Time</th>
          <th>End Time</th>
          <th>Duration (sec)</th>
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
        <%double totalDuration = 0; %>
        <%while(itr.hasNext()) { %>
        <%  Map.Entry<String, PlayerRoundReport> entry = itr.next() ;
            PlayerRoundReport rReport = entry.getValue() ; 
            if(rReport.getBalance() == 0) continue; 
            String anotherRound  = "" ;
            if(!Slot.Status.FINISHED.toString().equals(rReport.getStatus())) {
              anotherRound = "" + rReport.getAnotherRound() ;
            }
            double duration = (rReport.getEndTime() - rReport.getStartTime())/(double)1000;
            totalDuration += duration ;

        %>
            <tr>
              <td><%=new Date(rReport.getStartTime())%></td>
              <td><%=new Date(rReport.getEndTime())%></td>
              <td><%=duration%></td>
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
            Earnings: $<%= DEC_FT.format(balance)%>
          </div>
          <div>
            Total Duration: <%=DEC_FT.format(totalDuration)%> sec. 
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
               <br/>
               Age = <%=fb.getAge()%>;
               Gender = <%=fb.getGender()%>;
               Native Language = <%=fb.getNativeLanguage()%>
            <%}%>
          </div>

          <div>
            Client Info: 
            <pre><%=formatNull(slot.getPlayerReport().getClientInfo())%></pre>
          </div>
        </div>
    <%}%>
  </body>
</html>
