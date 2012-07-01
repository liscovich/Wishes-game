function getRandomString(length) {
  var digits = ["0", "1", "2", "3", "4", "5", "6", "7", "8", "9"];
  var returnString = "";
  for (var i = 0; i < length; i++) {
    returnString += digits[Math.floor(Math.random() * digits.length)];
  }
  return returnString;
}


function generateGameID() {
  var date = new Date();
  var gameId = "g"
  if (date.getMonth() + 1 < 10) {
    gameId += "0";
  }
  gameId += (date.getMonth() + 1);

  if (date.getDate() < 10) {
    gameId += "0";
  }
  gameId += date.getDate();
  
  if (date.getHours() < 10) {
    gameId += "0";
  }
  gameId += date.getHours();
  
  if (date.getMinutes() < 10) {
    gameId += "0";
  }
  gameId += date.getMinutes();
  gameId += getRandomString(3);
  return gameId;
}
function deleteGame(gameId) {
  $.ajax({
    type: "POST",
    url: "admin",
    data: "gameId=" + encodeURIComponent(gameId) 
      + "&a=deleteGame",
    success: handleError
  });
}
function watchGame(gameId) {
  previewGame = true;
  previewId = gameId;
  $('#watch').show();
}
function stopWatchGame() {
  previewGame = false;
  previewId = "";
  $('#watch').hide();
}
function watchSlot(slotId) {
  previewSlotId = slotId;
  $('#watch').show();
  $('#slot').show();
}
function stopWatchSlot() {
  previewSlotId = "";
  $('#slot').hide();
}
$(document).ready(function(){
  $('#prepareServer').click(function(){
    if ($('#gameId').val().trim().length == 0) {
      showErrorMessage("Game ID must not be empty!");
      return;
    }
  
    var data = "a=createGame" 
      + "&gameId=" + $('#gameId').val() 
      + "&numberOfGameSlot=" + $('#numberOfGameSlot').val() 
      + "&maxBetrayPayoff=" + $('#maxBetrayPayoff').val() 
      + "&rewardPayoff=" + $('#rewardPayoff').val() 
      + "&betrayCaughtChance=" + $('#betrayCaughtChance').val() 
      + "&rewardCaughtAsBetrayalChance=" + $('#rewardCaughtAsBetrayalChance').val() 
      + "&tempteeSurvivalChance=" + $('#tempteeSurvivalChance').val() 
      + "&blackMarkUpperLimit=" + $('#blackMarkUpperLimit').val() 
      + "&initTempteeBonus=" + $('#initTempteeBonus').val()
      +"&initialTrusterBonus=" + $('#initialTrusterBonus').val()
      +"&exchangeRate=" + $('#exchangeRate').val()
      +"&betrayalCost=" + $('#betrayalCost').val()
    +"&maxRoundsNum=" + $('#maxRoundsNum').val();
    $.ajax({
        type: "POST",
        url: "admin",
        data: data,
        success: handleError
      });
  });
});
var errorMessageTimeout;
function showErrorMessage(message) {
  errorMessageTimeout && clearTimeout(errorMessageTimeout);
  $('#errorMessage').css('visibility', 'visible');
  $('#errorMessage').html(message);
  errorMessageTimeout = setTimeout(hideErrorMessage, 10000);
}
function getPostData(action) {
  var result = {
      "gameId" : previewId, 
      "slotId" : previewSlotId 
  };
  action && (result["a"] = action);
  return result;
}
function hideErrorMessage(message) {
  $('#errorMessage').css('visibility', 'hidden');
  errorMessageTimeout = null;
}
var intervalId = setInterval(function() {$.ajax({
  type: "POST",
  url: "admin",
  data: getPostData("getHksGames"),
  success: function(returnData){
    if (handleError()) { return; };
    // Clear rows beyond first two
    $("#gameTable").find("tr:gt(1)").remove();
    for (var i = 0; i < returnData.hksGames.length; i++) {
      var linkText = " <a href='javascript:deleteGame(\"" + returnData.hksGames[i].gameId + "\")'>Delete</a>";
      if (previewId == returnData.hksGames[i].gameId) {
        linkText += " <a href='javascript:stopWatchGame()'>Hide</a>";
      } else {
        linkText += " <a href='javascript:watchGame(\"" + returnData.hksGames[i].gameId + "\")'>Watch</a>";
      }
      var newRow = "<tr>" +
          "<td>" + returnData.hksGames[i].gameId + "</td>" +
          "<td>" + returnData.hksGames[i].numberOfGameSlot + "</td>\
          <td>" + returnData.hksGames[i].maxBetrayPayoff + "</td>\
          <td>" + returnData.hksGames[i].rewardPayoff + "</td>"+
          
          "<td>"+ returnData.hksGames[i].initialTrusterBonus+"</td>"+
          
          "<td>" + returnData.hksGames[i].betrayCaughtChance + "</td>\
          <td>" + returnData.hksGames[i].rewardCaughtAsBetrayalChance + "</td>\
          <td>" + returnData.hksGames[i].betrayalCost+"</td>"+
          "<td>" + returnData.hksGames[i].tempteeSurvivalChance + "</td>\
          <td>" + returnData.hksGames[i].blackMarkUpperLimit + "</td>\
          <td>" + returnData.hksGames[i].initTempteeBonus + "</td>"+
          
          "<td>" + returnData.hksGames[i].exchangeRate+"</td>"+ 
          "<td>" + returnData.hksGames[i].maxRoundsNum+"</td>"+ 
          "<td>" + linkText + "</td></tr>";
      $('#gameTable tr:last').after(newRow);
      if (returnData.currentSlot) {
        $('#report').html(returnData.currentSlot.log);
        $('#roundReport').html(returnData.currentSlot.report);
      }
    }
    $("#slotTable").find("tr:gt(0)").remove();
    if (returnData.slots && returnData.slots.length > 0) {
      for (var i = 0; i < returnData.slots.length; i++) {
        var linkText = "";
        if (previewSlotId == returnData.slots[i].slotId) {
          linkText += " <a href='javascript:stopWatchSlot()'>Hide</a>";
        } else {
          linkText += " <a href='javascript:watchSlot(\"" + returnData.slots[i].slotId + "\")'>Watch</a>";
        }
        if (returnData.slots[i].status == "INIT") {
          linkText += " <a target='_blank' href='game?gameId=" + previewId + "&workerId=admin7'>Test</a>";
        } else {
          linkText += " <a target='_blank' href='index.html?gameId=" + previewId + "&slotId=" + encodeURIComponent(returnData.slots[i].slotId) + "'>Test</a>";
        }
        var newRow = "<tr>" +
        "<td><a href='game?gameId=" + previewId + "'>" + returnData.slots[i].slotNumber + "</a></td>" +
        "<td>" + returnData.slots[i].status + "</td>\
        <td>" + returnData.slots[i].workerNumber + "</td>\
        <td>" + returnData.slots[i].workerId + "</td>\
        <td>" + returnData.slots[i].blackMarkCount + "/" + returnData.slots[i].blackMarkUpperLimit + "</td>\
        <td>" + round_to_2_points(returnData.slots[i].tempteeBonus) + "</td>\
        <td>" + round_to_2_points(returnData.slots[i].currentBetrayPayoff) + "</td>\
        <td>" + returnData.slots[i].lastAction + "</td>\
        <td>" + returnData.slots[i].rewardCaughtAsBetrayal + "</td>\
        <td>" + returnData.slots[i].betrayCaught + "</td>\
        <td>" + returnData.slots[i].survival + "</td>\
        <td>" + linkText + "</td></tr>";
        $('#slotTable tr:last').after(newRow);
      }
    } else {
      $('#watch').hide();
    }
    $('#gameTable tr:even').css("background-color", "#f5f8fa");
    $('#slotTable tr:even').css("background-color", "#f5f8fa");
  }
});}, 2000);
var previewGame = false;
var previewId = "";
var previewSlotId = "";
function handleError(returnData) {
  returnData && returnData["error"] && showErrorMessage(returnData["error"]);
  return returnData && returnData["error"];
}
