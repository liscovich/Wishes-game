function betray() {
  $('#reward_ticket').fadeOut(400);
  $('#betray_ticket > #header').fadeOut(400, function() {
    setTimeout("betrayNext()", 1000);
  });
}
function betrayNext() {
  var time = Game.surviveLeft * Game.speed;
  $('#survive').animate({
    marginLeft: "+=" + Game.surviveLeft + "px"
  }, time, function() {
  });
  
  var marginleft = - $('#betray_ticket').position().left + 11 + Game.surviveLeft;
  $('#betray_ticket').animate({
    marginLeft: marginleft + 'px'
  }, time, function() {
    $('#betray_ticket').toggleClass("borderless", true);
    $("#sm_betray .spinButton button").hide();
    $('#sm_betray .wheelOuter').show();
    $('#sm_betray .wheelOuter').animate({
      marginTop: '+=320px'
    }, 320 * Game.speed, function() {
      $("#sm_betray .spinButton button").show();
      $("#sm_betray .spinButton button").focus();
    });
  });
  $.ajax({
    type: "POST",
    url: "game",
    data: getPostData("betray"),
    success: function(returnData){
      Game.currentStatus = "PAYOFF";
      processReturnedData(returnData);
    }
  });
}
function reward() {
  $('#betray_ticket').fadeOut(400);
  $('#reward_ticket > #header').fadeOut(400, function() {
    setTimeout("rewardNext()", 1000);
  });
}
function rewardNext() {
  var time = Game.surviveLeft * Game.speed;
  $('#survive').animate({
    marginLeft: "+=" + Game.surviveLeft + "px"
  }, time, function() {
  });
  var marginleft = - $('#reward_ticket').position().left + 11 + Game.surviveLeft;
  $('#reward_ticket').animate({
    marginLeft: marginleft + 'px'
  }, time, function() {
    $('#reward_ticket').toggleClass("borderless", true);
    $("#sm_reward .spinButton button").hide();
    $('#sm_reward .wheelOuter').show();
    $('#sm_reward .wheelOuter').animate({
      marginTop: '+=320px'
    }, 320 * Game.speed, function() {
      $("#sm_reward .spinButton button").show();
      $("#sm_reward .spinButton button").focus();
    });
  });
  $.ajax({
    type: "POST",
    url: "game",
    data: getPostData("reward"),
    success: function(returnData){
      Game.currentStatus = "PAYOFF";
      processReturnedData(returnData);
    }
  });
}
function payoffAck() {
  $('.ticket').fadeOut();
  $('#survive').fadeOut(400, function() {
    $('.ticket').css('margin-left', 0);
    $('.ticket .chooseButton button').show();
    
    if (Game.spinSlotMachine) {
      var str = "" + Game.returnData.tempteeBonus;
      var point = $('#point #pointContainer');
      var html = point.html();
      increaseBonus(str, html, str.length - 1, html.length - 1);
    } else {
      $('#point #pointContainer').html(Game.returnData.tempteeBonus);
      displayBlackMarks();
      nextRound();
    }
  });
}
function endChanceAck() {
  $('.ticket').hide();
  $('#survive').hide();
  $('.ticket').css('margin-left', 0);
  $('.ticket .chooseButton button').show();
  
  $('#point #pointContainer').html(Game.returnData.tempteeBonus);
  displayBlackMarks();
  nextNotTrustRound();
}
function increaseBonus(source, dest, sourceIndex, destIndex) {
  if (source != dest) {
    var newDigit = parseInt(dest, 10) + 1;
    dest = "" + newDigit;
    $('#point #pointContainer').html(dest);
    setTimeout("increaseBonus('" + source + "','" + dest + "'," + sourceIndex + "," + destIndex + ")", 50);
  } else {
    displayBlackMarks();
  }
}
function nextRound() {
  $.ajax({
    type: "POST",
    url: "game",
    data: getPostData("payoffAck"),
    success: function(returnData){
      processReturnedData(returnData);
    }
  });
}
function nextNotTrustRound() {
  $.ajax({
    type: "POST",
    url: "game",
    data: getPostData("endChanceAck"),
    success: function(returnData){
      Game.currentStatus = 'PAYOFF';
      processReturnedData(returnData);
    }
  });
}
function sendFeedback() {
  var feedback = $('#feedback').val().trim();
  if (feedback == "Your feedback") {
    $('#feedback').val("")
  }
  var data = getPostData("sendFeedback");
  data["feedback"] = encodeURIComponent($('#feedback').val());
  $.ajax({
    type: "POST",
    url: "game",
    data: data,
    success: function(returnData){
      processReturnedData(returnData);
    }
  });
}
function displayBlackMarks() {
  $('#point #blackMarkHeader').html("Your Black Marks (" + Game.returnData.blackMarkCount + "/" + Game.returnData.blackMarkUpperLimit + ")");
  var w = 60;
  var outer = $('#point #blackMarkBar');
  outer.html("");
  for (var i = 0; i < Game.returnData.blackMarkCount; i++) {
    outer.append("<div class='blackMark'></div>");
  }
  if (Game.spinSlotMachine) {
    if (Game.returnData.rewardCaughtAsBetrayal || 
        Game.returnData.betrayCaught) {
      $('#point #blackMarkBar .blackMark:last').fadeOut(400, function() {
        $(this).fadeIn(400, function() {
          nextRound();
        }
      )});
    } else {
      nextRound();
    }
  } 
  for (var i = Game.returnData.blackMarkCount; i < Game.returnData.blackMarkUpperLimit; i++) {
    outer.append("<div class='blackMarkCell'></div>");
  }
  outer.append("<div style='clear:both;'></div>");
  $('#blackMarkBar').show();
}
function play() {
  $('#tutorial').hide();
  $('#play').show();
  if (!Game.spinSlotMachine) {
    $('#point #pointContainer').html(Game.returnData.tempteeBonus);
    $('#point').show();
    displayBlackMarks();
  }
  initSlotMachine('sm_reward', 'Black Mark?', Game.returnData.rewardNotCaughtAsBetrayalChance);
  initSlotMachine('sm_survive', 'Another Round?', Game.returnData.endChance);
  initSlotMachine('sm_betray', 'Black Mark?', Game.returnData.betrayNotCaughtChance);
  $('#reward_ticket.ticket .payoffContent').html("<b>+" + Game.returnData.rewardPayoff + "</b> points");
  $('#betray_ticket.ticket .payoffContent').html("<b>+" + Game.returnData.currentBetrayPayoff + "</b> points");
  $('.ticket .chooseButton').show();
  $('#survive .chooseButton button').removeAttr("disabled");
  $('#survive .redeemButton').hide();
  var survive = $('#survive');
  $('#survive').css('margin-left', 0)
  $('#survive').css('left', 0);
  $('#survive').show()
  var tickets = $('.ticket'); 
  tickets.css('margin-left', 0);
  tickets.css('margin-top', 0);
  tickets.toggleClass("borderless", false);
  tickets.css('top', Game.ticketsTop + "px");
  $('.ticket #header').show();
  tickets.show();
  
  var surviveLeft = - survive.offset().left - survive.width();
  survive.css('left', surviveLeft + 'px');
  
  var ticketsTop = - Game.ticketsTop - tickets.height();
  tickets.css('top', ticketsTop + 'px');
  survive.animate({
    marginLeft: '+=' + (-surviveLeft) + 'px'
  }, -surviveLeft * Game.speed, function() {
    tickets.animate({
      marginTop: '+=' + (-ticketsTop + Game.ticketsTop) + 'px'
    }, (-ticketsTop + Game.ticketsTop) * 2, function() {
      
    });
  });
  Game.spinSlotMachine = true;
}
function notTrust() {
  Game.spinSlotMachine = false;
  $('#tutorial').hide();
  $('#play').show();
  $('#point #pointContainer').html(Game.returnData.tempteeBonus);
  $('#point').show();
  displayBlackMarks();
  $('.ticket').hide();
  $('#survive').hide();
  $('#notTrust').show();
  initSlotMachine('sm_notTrust', 'Another Round?', Game.returnData.endChance);
  
  $('#notTrust').css('margin-left', Game.surviveLeft + "px");
  $("#sm_notTrust .spinButton button").hide();
  $("#sm_notTrust .spinButton button").removeAttr("disabled");
  $('#sm_notTrust .wheelOuter').show();
  $('#sm_notTrust .wheelOuter').animate({
    marginTop: '+=320px'
  }, 320 * Game.speed, function() {
    $("#sm_notTrust .spinButton button").show();
    $("#sm_notTrust .spinButton button").focus();
  });
}
function payoff() {
  $('.ticket .chooseButton').hide();
  $('#survive .redeemButton').show();
  $('#survive .redeemButton button').removeAttr("disabled");
  if (!Game.spinSlotMachine) {
    $('#play').show();
    $('#point').show();
    initSlotMachine('sm_reward', 'Black Mark?', Game.returnData.rewardNotCaughtAsBetrayalChance);
    initSlotMachine('sm_survive', 'Another Round?', Game.returnData.endChance);
    initSlotMachine('sm_betray', 'Black Mark?', Game.returnData.betrayNotCaughtChance);
    $('#reward_ticket.ticket .payoffContent').html("<b>+" + Game.returnData.rewardPayoff + "</b> points");
    $('#betray_ticket.ticket .payoffContent').html("<b>+" + Game.returnData.currentBetrayPayoff + "</b> points");
    displayBlackMarks();
    $('#point #pointContainer').html(Game.returnData.tempteeBonus);
    colorSlotMachine('sm_survive', Game.returnData.survival, Game.returnData.notContinueSampling);
    $('#survive').css('margin-left', Game.surviveLeft + "px");
    if (Game.returnData.lastAction == "Reward") {
      colorSlotMachine('sm_reward', Game.returnData.rewardCaughtAsBetrayal, Game.returnData.rewardNotCaughtAsBetrayalSampling);
      $('#betray_ticket').hide();
      $('#reward_ticket > #header').hide();
      var marginleft = - $('#reward_ticket').position().left + 11 + Game.surviveLeft;
      $('#reward_ticket').css('margin-left', marginleft + "px");
      $('#reward_ticket').toggleClass("borderless", true);
      $('#reward_ticket').show();
    } else {
      colorSlotMachine('sm_betray', Game.returnData.betrayCaught, Game.returnData.betrayNotCaughtSampling);
      $('#reward_ticket').hide();
      $('#betray_ticket > #header').hide();
      var marginleft = - $('#betray_ticket').position().left + 11 + Game.surviveLeft;
      $('#betray_ticket').css('margin-left', marginleft + "px");
      $('#betray_ticket').toggleClass("borderless", true);
      $('#betray_ticket').show();
    }
    
    $('.ticket .chooseButton').hide();
    $('#survive .redeemButton').show();
    $('#survive .redeemButton button').removeAttr("disabled");
    $('#survive .redeemButton button').focus();
  }
}
function thanks() {
  Game.title.html("");
  Game.message.html("<h3 style='text-align:center;'>Thank you for playing!</h3>");
  Game.navi.html("<div class='navi'></div>");
}
function finish() {
  $('#play').hide();
  $('#tutorial').show();
  Game.message = $("#tutorial #message");
  Game.title = $("#tutorial #title");
  Game.navi = $("#tutorial #navi");
  Game.title.html("The End!");
  Game.message.html("");
  Game.message.append("<p>Your final bonus is: <b>" + round_to_2_points(Game.returnData.tempteeBonus) + "</b> points</p>");  
  Game.message.append("<p>Could you please let us know your feedback about the game?</p>");  
  Game.message.append("<p><textarea id='feedback' style='border: 1px solid gray;width:275px;' cols='50' rows='5' onfocus='this.value=\"\"'>Your feedback</textarea></p>");  
  Game.navi.html("<div class='navi'><button id='center'>Send</button></div>");
  $('#center').click(function(){
    $('#center').attr("disabled", true);
    sendFeedback();
  });
}
function getPostData(action) {
  var result = {
      "gameId" : decodeURIComponent(Game.gameId), 
      "slotId" : decodeURIComponent(Game.slotId), 
      "assignmentId" : Game.assignmentId ? decodeURIComponent(Game.assignmentId) : "", 
      "turkSubmitTo" : Game.turkSubmitTo ? decodeURIComponent(Game.turkSubmitTo) : "", 
      "hitId" : Game.hitId ? decodeURIComponent(Game.hitId) : "", 
      "workerId" : Game.workerId ? decodeURIComponent(Game.workerId) : "",
      "workerNum" : Game.workerNum ? decodeURIComponent(Game.workerNum) : ""
  };
  action && (result["a"] = action);
  Game.tutorialStep && (result["tutorialStep"] = encodeURIComponent(Game.tutorialStep));
  return result;
}

function updateClient() {
  $.ajax({
    type: "POST",
    url: "game",
    data: getPostData("update"),
    success: function(returnData){
      processReturnedData(returnData);
    },
    error: function(returnData, textStatus, errorThrown){
      $('#tutorial').show();
      $('#play').hide();
      Game.message = $("#tutorial #message");
      Game.title = $("#tutorial #title");
      Game.navi = $("#tutorial #navi");
      Game.title.html("Error happen!");
      Game.message.html("<p>Please contact <a href='mailto:liscovich@gmail.com'>liscovich@gmail.com</a> " +
        "and provide the following error message:</p>" +
        "<textarea cols='30' rows = '5' style='border: 1px solid gray;'>" + errorThrown + "</textarea>");
      Game.navi.html("<div class='navi'></div>");
    }
  });
}
function firstStep() {
  Game.message.html("<p> This tutorial will quickly teach you how to play.</p>");
  Game.navi.html("<div class='navi'><div class='begin'><button id='begin'>Begin</button></div></div>");
  $('#begin').click(function(){
    $('#begin').attr("disabled", true);
    tutorial(2);
  });
}

function processReturnedData(returnData) {
  Game.returnData = returnData;
  if (Game.currentStatus != Game.returnData.status) {
    Game.currentStatus = Game.returnData.status;
    switch (Game.returnData.status) {
    case "TUTORIAL":
      tutorial(1);
      break;
    case "PLAY":
      if (Game.returnData.blackMarkCount < Game.returnData.blackMarkUpperLimit) {
        play();
      } else {
        notTrust();
      }
      break;
    case "PAYOFF":
      payoff();
      break;
    case "FINISHED":
      finish();
      break;
    case "THANKS":
      thanks();
      break;
    case "DROPPED":
      $('#blackMarkBar').hide();
      $('#point').hide();
      Game.title.html("");
      Game.message.html("<h3 style='text-align:center;'>Game does not exist</h3>");
      Game.navi.html("<div class='navi'></div>");
      $(".container").parent().parent().show();
      break;
    }
  } 
}
function spinReward() {
  spinem("sm_reward", Game.returnData.rewardNotCaughtAsBetrayalSampling, 
      Game.returnData.rewardNotCaughtAsBetrayalChance, 
      "readyToSpinSurvive(\"sm_survive\", \"sm_reward\")");
  $("#sm_reward .spinButton button").hide();
}
function spinBetray() {
  spinem("sm_betray", Game.returnData.betrayNotCaughtSampling,
      Game.returnData.betrayNotCaughtChance, 
      "readyToSpinSurvive(\"sm_survive\", \"sm_betray\")");
  $("#sm_betray .spinButton button").hide();
}
function spinSurvive() {
  spinem('sm_survive', Game.returnData.notContinueSampling,
      Game.returnData.endChance,
      "showRedeem(\"sm_survive\")");
  $("#sm_survive .spinButton button").hide();
}
function spinSurviveNotTrust() {
  spinem('sm_notTrust', Game.returnData.notContinueSampling,
      Game.returnData.endChance, "spinSurviveNotTrustComplete()");
  $("#sm_notTrust .spinButton button").hide();
}
function spinSurviveNotTrustComplete() {
  var marginTop = $(window).height() - $('#sm_notTrust .wheelOuter').offset().top;
  $('body').css('overflow-y', 'hidden');
  $('#sm_notTrust .wheelOuter').animate({
    marginTop: '+=' + marginTop + 'px'
  }, marginTop * Game.speed, function() {
    $('#sm_notTrust .wheelOuter').hide();
    $('#sm_notTrust .wheelOuter').css('margin-top', 0);
    endChanceAck();
  });
}
function readyToSpinSurvive(surviveName, slotMachine) {
  $('#' + slotMachine + " .wheelOuter").animate({
    marginTop: '+=133px'
  }, 133 * Game.speed, function() {
    $("#" + slotMachine + " .wheelOuter").hide().css("margin-top", "0");
    $("#" + slotMachine + " .spinButton button").show();
    $('#' + surviveName + " .wheelOuter").show();
    $("#" + surviveName + " .spinButton button").focus();
    for (var a = 1; a <= 3; a++) {
      $("#" + slotMachine + " #slot" + a).html("0");
      $("#" + slotMachine + " #slot" + a).css("backgroundColor", "");
      $("#" + slotMachine + " #slot" + a).css("color", "#909090");
    }
  });
}
function showRedeem(name) {
  $('body').css('overflow-y', 'hidden');
  $('.ticket .chooseButton').hide();
  $('#survive .redeemButton').show();
  $('#survive .redeemButton button').removeAttr("disabled");
  $('#survive .redeemButton button').focus();
  
  var marginTop = $(window).height() - $('#' + name + " .wheelOuter").offset().top;
  $('#' + name + " .wheelOuter").animate({
    marginTop: '+=' + marginTop + 'px'
  }, marginTop * Game.speed, function() {
    $("#" + name + " .spinButton button").show();
    $('#' + name + " .wheelOuter").hide();
    $('#' + name + " .wheelOuter").css("margin-top", "0");
    $('body').css('overflow-y', 'auto');
    for (var a = 1; a <= 3; a++) {
      var slot = $("#" + name + " #slot" + a);
      slot.html("0");
      slot.css("backgroundColor", "");
      slot.css("color", "#909090");
    }
    payoff();
  });
}
function spinem(slotMachine, number, chance, statement) {
  var occur = parseInt(number, 10) >= chance * 1000;
  var turns = [];
  for (var digit = 1; digit <= 3; digit++) {
    turns[digit] = (4 - digit) * 10 + parseInt(number.substring(digit - 1, digit), 10);
  }
  for (var digit = 1; digit <= 3; digit++) {
    spinChangeImage(slotMachine, number, parseInt(number.substring(digit - 1, digit), 10), turns[digit], digit, occur, statement);
  }
}
function spinChangeImage(slotMachine, number, current, turns, digit, occur, statement) {
  $("#" + slotMachine + " #slot" + digit).html(current % 10);
  if (current + 1 <= turns) {
    setTimeout("spinChangeImage('" + slotMachine + "', '" + number + "'," + (current + 1) + ", " + turns + ", " + digit + "," + occur + ",'" + statement + "')", 50);
  } else {
    $("#" + slotMachine + " #slot" + digit).css("color", "black");
    if (digit == 1) {
      colorSlotMachine(slotMachine, occur, number, statement);
    }
  }
}
function initSlotMachine(slotMachine, header, thresholdChance) {
  var threshold = "" + Math.round(thresholdChance * 1000);
  while (threshold.length < 3) {
    threshold = "0" + threshold;
  }
  var w = 150;
  var curlyLimit = 15;
  var thresholdInt = Math.round(thresholdChance * 1000);
  for (var a = 1; a <= 3; a++) {
    $("#" + slotMachine + " #slot" + a).css("backgroundColor", "");
    $("#" + slotMachine + " #slot" + a).css("color", "#909090");
    $("#" + slotMachine + " #slot" + a).html("0");
  }
  $("#" + slotMachine).attr("class", "slotMachine inactive");
  $("#" + slotMachine + " .scaleNo").attr("class", "scaleNo");
  $("#" + slotMachine + " #curlyNo").attr("class", "curly");
  $("#" + slotMachine + " .no").attr("class", "no");
  $("#" + slotMachine + " .scaleYes").attr("class", "scaleYes");
  $("#" + slotMachine + " #curlyYes").attr("class", "curly");
  $("#" + slotMachine + " .yes").attr("class", "yes");
  
  $("#" + slotMachine + " #header1").html(header);
  $("#" + slotMachine + " .thresholdChance").html(threshold);
  
  var thresholdLeft = Math.floor(thresholdInt / 1000 * w);
  $("#" + slotMachine + " .thresholdChance").css("left", thresholdLeft + "px");
  $("#" + slotMachine + " .scaleNo").width(thresholdLeft + "px");// 1px white stick is right border of No
  $("#" + slotMachine + " #curlyNo").width(thresholdLeft + "px");
  if (thresholdLeft >= curlyLimit) {
    $("#" + slotMachine + " #curlyNo").attr("class", "curly");
  } else {
    $("#" + slotMachine + " #curlyNo").attr("class", "curly vertical");
  }
  $("#" + slotMachine + " .no").css("left", (thresholdLeft / 2) + "px");
  $("#" + slotMachine + " .scaleYes").width((w - thresholdLeft) + "px");// 2px white stick is left border of Yes
  $("#" + slotMachine + " #curlyYes").width((w - thresholdLeft) + "px");
  if (w - thresholdLeft >= curlyLimit) {
    $("#" + slotMachine + " #curlyYes").attr("class", "curly");
  } else {
    $("#" + slotMachine + " #curlyYes").attr("class", "curly vertical");
  }
  // the white stick width is 3px;
  $("#" + slotMachine + " .scaleThreshold").css("left", (thresholdLeft - 1) + "px");
  $("#" + slotMachine + " .scaleYes").css("left", (thresholdLeft) + "px");
  $("#" + slotMachine + " #curlyYes").css("left", (thresholdLeft) + "px");
  $("#" + slotMachine + " .yes").css("left", (thresholdLeft + (w - thresholdLeft) / 2) + "px");
  $("#" + slotMachine).show();
}
function colorSlotMachine(slotMachine, occur, number, statement) {
  $("#" + slotMachine + " .realChance").html(number);
  var w = 150;
  var realChanceInt = parseInt(number, 10);
  var realChanceLeft = Math.floor(realChanceInt / 1000 * w);
  $("#" + slotMachine + " .realChance").css("left", realChanceLeft + "px");
  if (occur) {
    $("#" + slotMachine + " #curlyYes").toggleClass("occur");
    $("#" + slotMachine + " .yes").toggleClass("occur");
    $("#" + slotMachine + " .scaleYes").toggleClass("occur");
  } else {
    $("#" + slotMachine + " #curlyNo").toggleClass("occur");
    $("#" + slotMachine + " .no").toggleClass("occur");
    $("#" + slotMachine + " .scaleNo").toggleClass("occur");
  }
  $("#" + slotMachine).toggleClass("inactive", false);
  $("#" + slotMachine).toggleClass("active", true);
  statement && setTimeout("" + statement, 1000);
}
function tutorial(tutorialStep) {
  Game.message = $("#tutorial #message");
  Game.title = $("#tutorial #title");
  Game.navi = $("#tutorial #navi");
  Game.title.html("Tutorial");
  Game.tutorialStep = tutorialStep;
  $('#tutorial').show();
  switch(tutorialStep) {
    case 1:
      if (Game.returnData.tutorialStep > 1) {
        tutorial(Game.returnData.tutorialStep);
      } else {
        firstStep();
      }
      break;
    case 2:
      Game.message.html("<p>Sed ut perspiciatis,  accusantium doloremque laudantium, totam rem aperiam eaque ipsa, quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt, explicabo</p>");
      Game.navi.html("<div class='navi'><button id='left'>Back</button><button id='right'>Next</button><div class='steps'>" 
          + (tutorialStep - Game.tutorialStepOffset) + "/" + Game.numberOfTutorialStep + "</div></div>");
      $('#right').click(function(){
        $('#right').css("visibility", "hidden");
        $('#left').css("visibility", "hidden");
        tutorial(tutorialStep + 1);
      });
      $('#left').click(function(){
        $('#right').css("visibility", "hidden");
        $('#left').css("visibility", "hidden");
        tutorial(tutorialStep - 1);
      });
      break;
    case 3:
      Game.message.html("<p>Et harum quidem rerum. Nam libero tempore, cum soluta nobis est eligendi optio, cumque nihil impedit, quo minus id, quod maxime placeat, facere possimus, omnis voluptas assumenda est, omnis dolor repellendus. Temporibus autem quibusdam et aut officiis debitis aut rerum necessitatibus saepe eveniet, ut et voluptates repudiandae sint et molestiae non recusandae.</p>");
      Game.navi.html("<div class='navi'><button id='left'>Back</button><button id='right'>Next</button><div class='steps'>" 
          + (tutorialStep - Game.tutorialStepOffset) + "/" + Game.numberOfTutorialStep + "</div></div>");
      $('#right').click(function(){
        $('#right').css("visibility", "hidden");
        $('#left').css("visibility", "hidden");
        tutorial(tutorialStep + 1);
      });
      $('#left').click(function(){
        $('#right').css("visibility", "hidden");
        $('#left').css("visibility", "hidden");
        tutorial(tutorialStep - 1);
      });
      break;
    case 4:
      Game.message.html("<p>You are now ready to play. Click \"Next\" button to begin the game.</p>");
      Game.navi.html("<div class='navi'><button id='left'>Back</button><button id='right'>Next</button><div class='steps'>" 
          + (tutorialStep - Game.tutorialStepOffset) + "/" + Game.numberOfTutorialStep + "</div></div>");
      $('#right').click(function(){
        $('#right').css("visibility", "hidden");
        $('#left').css("visibility", "hidden");
        doneTutorial();
        Game.currentStatus = "PLAY";
        play();
      });
      $('#left').click(function(){
        $('#right').css("visibility", "hidden");
        $('#left').css("visibility", "hidden");
        tutorial(tutorialStep - 1);
      });
      break;
  }
}
function doneTutorial() {
  $.ajax({
    type: "POST",
    url: "game",
    data: getPostData("doneTutorial"),
  });
}