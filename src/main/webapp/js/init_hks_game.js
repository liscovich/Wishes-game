var Game = {};
Game.numberOfTutorialStep = 3;
Game.tutorialStepOffset = 1;
Game.gameId = $.getUrlVar('gameId');
Game.slotId = $.getUrlVar('slotId');
Game.assignmentId = $.getUrlVar('assignmentId');
Game.turkSubmitTo = $.getUrlVar('turkSubmitTo');
Game.hitId = $.getUrlVar('hitId');
Game.workerId = $.getUrlVar('workerId');
Game.workerNum = $.getUrlVar('workerNum');
// This variable keeps track of the JavaScript timer
Game.message;
Game.currentStatus;
Game.returnData;
Game.spinSlotMachine;

Game.surviveLeft = 150;
Game.ticketsTop = 21;
Game.speed = 2;
$(document).ready(function(){
  updateClient();
  $("#sm_reward .spinButton button").click(function(){
    spinReward();
  });
  $("#sm_betray .spinButton button").click(function(){
    spinBetray();
  });
  $("#sm_survive .spinButton button").click(function(){
    spinSurvive();
  });
  $("#sm_notTrust .spinButton button").click(function(){
    spinSurviveNotTrust();
  });
  $('#betray').click(function(){
    $(this).hide();
    betray();
  });
  $('#reward').click(function(){
    $(this).hide();
    reward();
  });
  $('#survive .redeemButton button').click(function(){
    $(this).attr("disabled", true);
    payoffAck();
  });
});
