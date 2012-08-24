/*
Temptation Game
*/
$.extend({
  getUrlVars: function(){
    var vars = [], hash;
    var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
    for(var i = 0; i < hashes.length; i++)
    {
      hash = hashes[i].split('=');
      vars.push(hash[0]);
      vars[hash[0]] = hash[1];
    }
    return vars;
  },
  getUrlVar: function(name){
    return $.getUrlVars()[name];
  }
});
(
function($){
	//creating a general class called Temptation Game
	function TemptationGame(element,options) {
		var elem = $(element);
		var obj = this;
		var settings = $.extend({
			units:'points',
			collectorBoxLabel:'Your Earnings',
			blackMarksLabel:'Remaining wishes',
			blackMarksLabelHighPayoff:'Uses up 1 wish:',
			blackMarksLabelLowPayoff:'  ',
			cardLabelLowPayoff:'Low Payoff',
			cardLabelHighPayoff:'High Payoff'
		}, options || {});
		this.blackMarkUpperLimit;
		this.collectorBoxLabel = settings.collectorBoxLabel;
		this.tempteeBonus;
		this.blackMarksLabel = settings.blackMarksLabel;
		this.blackMarksLabelHighPayoff = settings.blackMarksLabelHighPayoff;
		this.blackMarksLabelLowPayoff = settings.blackMarksLabelLowPayoff;
		this.units = settings.units;
		this.highPayoffPoint;
		this.lowPayoffPoint;
		this.maxBetrayPayoff = 20;
		this.cardLabelHighPayoff = settings.cardLabelHighPayoff;
		this.cardLabelLowPayoff = settings.cardLabelLowPayoff;
		this.blackMarkCount = 0;
		this.collectorBox;
		this.choicePanel;
		//from URL
		this.gameId = $.getUrlVar('gameId');
		this.slotId = $.getUrlVar('slotId');
		this.assignmentId = $.getUrlVar('assignmentId');
		this.turkSubmitTo = $.getUrlVar('turkSubmitTo');
		this.hitId = $.getUrlVar('hitId');
		this.workerId = $.getUrlVar('workerId');
		this.workerNum = $.getUrlVar('workerNum');
		this.currentStatus;
		this.returnData;
		this.lastAction = "";
		this.endChance;
		this.notContinueSampling;
		this.survival = true;
		this.screenNo = 0;
		this.mturkRate = 0.01;
		this.isNext = $.getUrlVar('next');
		this.tutorialScreen = function(){return [
		{
		'step' : 0,
		'content': ['<div class="label center">Welcome to the game!</div>','<div class="label center">This tutorial will quickly teach you how to play.</div>'],
		'figure' : '',
		nextButtonText : 'Begin'
		},
		{
		'step' : 1,
		'content': ['<div class="label">You will play multiple rounds of this game.</div>','<div class="label">In each round you will be shown two cards.</div>'],
		figure : {
				card1 : {
					visible : true,
					active	:false, 
					content :false, 
					title : true,
					cardType : 'low_payoff'
					},
				card2 : {
					visible : true,
					active : false,
					content :false,
					title : true,
					cardType : 'high_payoff'
					},
				collectorBox : {
					visible : false,
					title : true,
					content : false,
					downArrow : true,
					otherPlayer : false
					}
				},
		nextButtonText : 'What do I do with them? »',
		prevButtonText : ''
		},
		{
		'step' : 2,
		'content': ['<div class="label">You pick a card to deposit into your wallet below. </div>'],
		'figure' :{ 
				card1 : {
					visible : true,
					active	:false, 
					content :false, 
					title : true,
					cardType : 'low_payoff'
					},
				card2 : {
					visible : true,
					active : false,
					content :false,
					title : true,
					cardType : 'high_payoff'
					},
				collectorBox : {
					visible : true,
					title : true,
					content : false,
					downArrow : true,
					otherPlayer : false
					}
				},
		nextButtonText : 'What happens if I pick the first card? »',
		prevButtonText : '« Back'
		},
		{
		'step' : 3,
		'content': ['<div class="label">If you pick the first card, you get a Low Payoff of ' + ( obj.lowPayoffPoint || "Low Payoff" ) + ' points.</div>', '<div class="label hide-on-click">Click on the first card to try.</div>'],
		'figure' : { 
				card1 : {
					visible : true,
					active	:true, 
					content :true, 
					title : true,
					cardType : 'low_payoff'
					},
				card2 : {
					visible : true,
					active : false,
					content :false,
					title : true,
					cardType : 'high_payoff'
					},
				collectorBox : {
					visible : true,
					title : true,
					content : true,
					downArrow : true,
					otherPlayer : false
					}
				},
		nextButtonText : 'And what if I choose the second card? »',
		prevButtonText : '« Back'
		},
		{
		'step' : 4,
		'content': ['<div class="label">The second card gives you a High Payoff that changes randomly from round to round between ' + ( obj.lowPayoffPoint + 1) + ' and ' + ( ((obj.lowPayoffPoint + obj.maxBetrayPayoff)*1) || "High Payoff" ) + ' points. Every number in this range is equally likely. </div>'],
		'figure' : { 
				card1 : {
					visible : true,
					active	:false, 
					content :true, 
					title : true,
					cardType : 'low_payoff'
					},
				card2 : {
					visible : true,
					active : false,
					content :true,
					title : true,
					cardType : 'high_payoff'
					},
				collectorBox : {
					visible : true,
					title : true,
					content : true,
					downArrow : true,
					otherPlayer : false
					}
				},
		nextButtonText : 'What’s the catch? »',
		prevButtonText : '« Back'
		},
		{
		'step' : 5,
		'content': ['<div class="label">You begin the game with ' + (obj.blackMarkUpperLimit || "noOfWishes") + ' wishes that are displayed on your wallet below. Whenever you choose the High Payoff card, you use up one of your wishes. When you use up all ' + (obj.blackMarkUpperLimit || "noOfWishes") + ' of your wishes, the game ends.</div>', '<div class="label hide-on-click">Click on the second card to try.</div>'],
		'figure' : { 
				card1 : {
					visible : true,
					active	:false, 
					content :true, 
					title : true,
					cardType : 'low_payoff'
					},
				card2 : {
					visible : true,
					active : true,
					content :true,
					title : true,
					cardType : 'high_payoff'
					},
				collectorBox : {
					visible : true,
					title : true,
					content : true,
					downArrow : true,
					otherPlayer : false
					}
				},
		nextButtonText : 'What else do I need to know? »',
		prevButtonText : '« Back'
		},
		{
		'step' : 6,
		'content': ['<div class="label">There is one more thing you need to know.\
		The game may also end spontaneously after every round, even if you haven’t used up all your wishes.\
		You spin the wheel of fortune that determines whether the game continues to another round, or ends.\
		Try it below.</div>'
		,'<div class="label center">Another Round?</div>\
		<div id="roulette" style="width:100px;height:100px"></div>\
		<button id="spinner">Find out!</button>'],
		'figure' : { 
				card1 : {
					visible : false,
					active	:false, 
					content :true, 
					title : true,
					cardType : 'low_payoff'
					},
				card2 : {
					visible : false,
					active : false,
					content :true,
					title : true,
					cardType : 'high_payoff'
					},
				collectorBox : {
					visible : true,
					title : false,
					content : true,
					downArrow : false,
					otherPlayer : false
					},
				rouletteActive : true
				},
		nextButtonText : 'Got it!',
		prevButtonText : '« Back'
		},
		{
		'step' : 7,
		'content': ['<div class="label">The bigger the “Yes” segment of the wheel, the more likely the game will continue.</div>\
		<div class="roulette_left">\
		<div class="label center">Another Round?</div>\
		<div id="roulette1" style="width:100px;height:100px"></div>\
		<div class="label">If the arrow points to “Yes”, the game continues</div>\
		</div>\
		<div class="roulette_left">\
		<div class="label center">Another Round?</div>\
		<div id="roulette2" style="width:100px;height:100px"></div>\
		<div class="label">If the arrow points to “No”, the game ends</div>\
		</div>',
		'<div class="label">Here, there is a ' + Math.round(100 - obj.endChance * 100) + '% chance of the game continuing and a ' + Math.round(obj.endChance * 100) + '% chance of the game ending.</div>'],
		'figure' : '',
		nextButtonText : 'I see! »',
		prevButtonText : '« Back'
		},
		{
		'step' : 8,
		'content': ['<div class="label">To recap:<ol>'+
					'<li>You play multiple rounds.</li>' +
					'<li>In each round, you choose either the Low Payoff  card that gives you <b>' + ( obj.lowPayoffPoint || "Low Payoff" ) + '</b> points, or the High Payoff card that gives you between <b>' + ( obj.lowPayoffPoint + 1) + '</b> and <b>' + (( obj.maxBetrayPayoff + obj.lowPayoffPoint )*1) + '</b> points but uses up one wish.</li>' +
					'<li>If you use up all of your wishes, the game ends.</li>' +
					'<li>After every round, you spin the wheel of fortune, which can also end the game. There is a <b>' + Math.round(100 - obj.endChance * 100) + '%</b> chance that the game continues and a <b>' + Math.round(obj.endChance * 100) + '%</b> chance it ends.</li>' +
					'<li>Your goal is to earn as many points as you can before the game ends. You do not get any points for left-over wishes.</li>' +
					'<li>At the end of the game, we will convert your points into US dollars at the rate of <b>' + ((obj.mturkRate * 100) || "-mturkRate-") +'</b> cents per point, and add them to your MTurk account.</li>' +
					'</ol></div>'],
		'figure' : '',
		nextButtonText : 'Ok! »',
		prevButtonText : '« Back'
		},
		{
		'step' : 9,
		'content': ['<div class="label">Now get ready for a practice game. It will not affect your actual earnings. After the practice game, you will play a real game.</div>'],
		'figure' : '',
		nextButtonText : 'Begin',
		prevButtonText : '« Back'
		},
		]; };
		var obj = this;
		this.init = function(){
		  var obj=this;
			obj.updateClient();
		};

		this.initGame = function(){
      obj.collectorBox = obj.createCollectorBox();
      obj.choicePanel = obj.createChoicePanel();
      obj.survivalPanel = obj.createSurvivalPanel();
      elem.html('');
      elem.append(obj.collectorBox,obj.choicePanel,obj.survivalPanel);	
      obj.createWheelOfFortune('wheel_of_fortune','find_out');
      $('#collectorbox_table').css({
        'top' : $('#collectorbox_front').position().top + $('#collectorbox_front').outerHeight() -20
        });
      obj.updateBlackMarks();
		};

		this. getPostData = function(action) {
			obj.isNext = $.getUrlVar('next');  
			var result = {
				  "gameId" : decodeURIComponent(obj.gameId), 
				  "slotId" : decodeURIComponent(obj.slotId), 
				  "assignmentId" : obj.assignmentId ? decodeURIComponent(obj.assignmentId) : "", 
				  "turkSubmitTo" : obj.turkSubmitTo ? decodeURIComponent(obj.turkSubmitTo) : "", 
				  "hitId" : obj.hitId ? decodeURIComponent(obj.hitId) : "", 
				  "workerId" : obj.workerId ? decodeURIComponent(obj.workerId) : "",
				  "workerNum" : obj.workerNum ? decodeURIComponent(obj.workerNum) : "",
				  "next":obj.isNext
      };
      action && (result["a"] = action);
      obj.tutorialStep && (result["tutorialStep"] = encodeURIComponent(obj.tutorialStep));
      return result;
	  };

		this.updateClient = function(){
      $.ajax({
        type: "POST",
        url: "game",
        data: obj.getPostData("update"),
        success: function(returnData){
          obj.processReturnedData(returnData);
        },
        error: function(returnData, textStatus, errorThrown){
          alert('Error communicating to server');
        }
      });
    };
		this.processReturnedData = function(returnData) {
      obj.returnData = returnData;
      //hack, should find a proper way
      var reinitGame = obj.currentStatus == 'PRACTICE' ;
      if (obj.currentStatus != obj.returnData.status) {
        obj.syncWithServer();
        switch (obj.returnData.status) {
        case "TUTORIAL":
          obj.tutorial(0, 'Begin');
          break;
        case "PLAY":
          if (obj.returnData.blackMarkCount < obj.returnData.blackMarkUpperLimit) {
            if($('#collectorbox').size() == 0 || reinitGame) {
              obj.initGame();
            }
            obj.showChoicePanel();
          } else {
            if($('#collectorbox').size() == 0) obj.initGame();
            obj.gameOver();
          }
          break;
        case "PAYOFF":
          if($('#collectorbox').size() == 0)obj.initGame();
          obj.updateBlackMarks();
          obj.createWheelOfFortune('wheel_of_fortune','find_out');
          if (obj.returnData.blackMarkCount < obj.returnData.blackMarkUpperLimit) {
            obj.showSurvivalPanel();
            }
          else{
            obj.nextRound();
            }
          break;
        case "FINISHED":
          if($('#collectorbox').size() == 0) obj.initGame();
          obj.updateBlackMarks();
          obj.gameOver();
          break;
        case "THANKS":
          alert('thanks');
          break;
        case "DROPPED":
          alert('The game you are trying to play does not exist anymore');
          break;
        }
      } 
    };
		
		this.syncWithServer = function(){
			obj.version = obj.returnData.version;
			obj.currentStatus = obj.returnData.status;
			obj.lastAction = obj.returnData.lasAction;	
			obj.currentRound = obj.returnData.currentRound;	
			
			obj.tempteeBonus = obj.returnData.tempteeBonus;	
			obj.lowPayoffPoint = obj.returnData.rewardPayoff;
			obj.highPayoffPoint = obj.returnData.currentBetrayPayoff;

			obj.maxBetrayPayoff = obj.returnData.maxBetrayPayoff ;
			obj.mturkRate = obj.returnData.mturkRate ;
			obj.feedbackBonus = obj.returnData.feedbackBonus ;
			
			obj.blackMarkUpperLimit = obj.returnData.blackMarkUpperLimit;
			obj.blackMarkCount = obj.returnData.blackMarkCount;
			obj.betrayNotCaughtChance = obj.returnData.betrayNotCaughtChance;
			obj.betrayCaught = obj.returnData.betrayCaught;
			obj.rewardNotCaughtAsBetrayalSampling  = obj.returnData.rewardNotCaughtAsBetrayalSampling;
			obj.notContinueSampling = obj.returnData.notContinueSampling;
			obj.rewardCaughtAsBetrayal = obj.returnData.rewardCaughtAsBetrayal;
			obj.betrayNotCaughtSampling = obj.returnData.betrayNotCaughtSampling;
			obj.rewardNotCaughtAsBetrayalChance= obj.returnData.rewardNotCaughtAsBetrayalChance;
			obj.endChance = obj.returnData.endChance;
			
			obj.survival = obj.returnData.survival;
		};
		
		this.createCollectorBox = function(collectorBoxSettings) {
      var obj = this;
      var settings = $.extend({
        visible : true,
        title : false,
        content : true,
        downArrow : false,
        otherPlayer : false
        }, collectorBoxSettings || {});
        
      var $collectorBox = $('<div id="collectorbox"/>');
      var $collectorBoxFront = $('<div id="collectorbox_front"/>');
      var $collectorBoxBack = $('<div id="collectorbox_back"/>');
      var $collectorBoxTable = $('<div id="collectorbox_table"/>');
      
      var $label = $('<div class="label"/>');
      $label.text(obj.collectorBoxLabel);
      
      var $earnings = $('<div class="earnings"/>');
      $earnings.html('<span class="earning">'+ ( obj.tempteeBonus || "Earnings" ) + "</span> " + obj.units);
      
      var $blackMarks = $('<div class="blackmarks"/>');
        var $blackMarksLabel = $('<div class="label"/>');
        $blackMarksLabel.text(obj.blackMarksLabel);
        
        var $blackMarksCount = $('<div class="label" id="blackmark_count"/>');
        $blackMarksCount.text('('+ obj.blackMarkCount +'/'+ obj.blackMarkUpperLimit +')');
        
        var $blackMarkBars = obj.createBlackMarkGUI();
        
        $blackMarks.append($blackMarksLabel,$blackMarksCount,$blackMarkBars);
      //create down Arrow
      var $downArrow = $('<div id="down_arrow"><img src="images/down_arrow.png"></div>');
      
      if(settings.content)$collectorBoxFront.append($label,$earnings,$blackMarks);
      else $collectorBoxFront.append('<div class="blank"></div>');
      //append Down Arrow
      if(settings.downArrow)$collectorBox.append($downArrow);
      if(settings.title)$collectorBox.append('<div class="figure-label">Your Wallet</div>');
      $collectorBox.append($collectorBoxTable,$collectorBoxBack,$collectorBoxFront);
			return $collectorBox;
    };
			
		this.createBlackMarkGUI=function(){
		var obj = this;
			var $blackMarkBars = $('<div class="blackmark_bars"/>');
			for(var i=0; i<obj.blackMarkUpperLimit; i++){
				$blackMarkBars.append('<div class="blackmark_bar active"/>');
				}
			return $blackMarkBars;
			};
		
		this.createChoicePanel= function(){
		var obj = this;
			var $choicePanel = $('<div id="choice_panel"/>');
			$choicePanel.append('<div class="label center">Choose a Card</div>',obj.createCard({cardType:'low_payoff'}),obj.createCard({cardType:'high_payoff'}));
			return $choicePanel;
			};
			
			
		this.createSurvivalPanel= function(){
		  var obj = this;
			var $survivalPanel = $('<div id="survival_panel"/>');
		  var $anotherRoundMessage = $("<div class='label'>Another Round?</div>");
			var $wheelOfFortune = $("<div id='wheel_of_fortune'></div>");
			var $findOutButton = $('<button id="find_out">Find Out</button>');
				
			$survivalPanel.append($anotherRoundMessage,$wheelOfFortune,$findOutButton);
		  return $survivalPanel;
		};
		
		this.createWheelOfFortune = function(canvasSelector,buttonSelector,autoRender){
			  $('#'+canvasSelector).empty();
			  var width = $('#'+canvasSelector).width();
			  var height = $('#'+canvasSelector).width();
			  var cx = width / 2;
			  var cy = height / 2;
			  var dialLength = 10;
			  var rotTimes = 6;
			  var rad = (width / 2) -10;
			  obj.paper = Raphael(canvasSelector, width, height);
			  obj.paper.clear();
			  var endChance = 	obj.endChance;
			  var notContinueSampling = obj.notContinueSampling;
			  var dialStopAngle = 360* notContinueSampling / 999;
			  var startAngleNo = 90 + (360 * endChance / 2) - 2;
			  var endAngleNo = 90 - (360 * endChance / 2) + 2;
			  var startAngleYes = 270 - (360 * (1-endChance) / 2);
			  var endAngleYes = 270 + (360 * (1-endChance) / 2);
			  var zeroCorrection = endAngleNo - 360;
			  function arc(center, radius, startAngle, endAngle) {
				  (startAngle < endAngle)?angle = startAngle: angle = endAngle;
				  (startAngle > endAngle)?maxAngle = startAngle: maxAngle = endAngle;
				  coords = toCoords(center, radius, angle);
				  path = "M " + coords[0] + " " + coords[1];
				  while(angle<=maxAngle) {
					  coords = toCoords(center, radius, angle);
					  path += " L " + coords[0] + " " + coords[1];
					  angle += 1;
				  }
				  return path;
			  }

			  function toCoords(center, radius, angle) {
				  var radians = (angle/180) * Math.PI;
				  var x = center[0] + Math.cos(radians) * radius;
				  var y = center[1] + Math.sin(radians) * radius;
				  return [x, y];
			  }

			  var noArc = obj.paper.path(arc([width/2, height/2], rad, startAngleNo, endAngleNo)).attr({"stroke": "#ddd", "stroke-width": 8}); 
			  var yesText = obj.paper.text(width/2, height/4, "Yes").attr({ "fill": "#999", "font-size": 12, "font-family": "Arial, Helvetica, sans-serif" });
			  var yesArc = obj.paper.path(arc([width/2, height/2], rad, startAngleYes, endAngleYes)).attr({"stroke": "#ddd", "stroke-width": 8}); 
			  var noText = obj.paper.text(width/2, height*3/4, "No").attr({ "fill": "#999", "font-size": 12, "font-family": "Arial, Helvetica, sans-serif" });
			  var dialPath = "M"
							+ (cx - 5)
							+ " "
							+ (cy - 5)
							+ "L"
							+ (cx - 5)
							+ " "
							+ (cy + 5)
							+ "L"
							+ (cx + dialLength)
							+ " "
							+ cy
							+ "Z";
			  var dial = obj.paper.path(dialPath).attr({ "fill": "#999"});
			  var dialPin = obj.paper.circle(cx,cy,2).attr({"stroke": "#999","fill": "#fff", "stroke-width": 1});
			  dial.rotate(0,cx, cy,true);
			  var rotAngle = Math.floor(dialStopAngle+zeroCorrection);
			  (rotAngle < 0)? rotAngle=360+rotAngle: rotAngle=rotAngle;
			  rotAngle = (360 * (rotTimes - 1)) + rotAngle
			  var rotString = rotAngle +" "+ cx+" "+cy;
			  /* console.log(rotString); */
			  if(typeof buttonSelector != 'undefined' && buttonSelector != '') {
			  $('#'+ buttonSelector).unbind('click').click(function() {
				  $(this).attr('disabled','disabled');
				  obj.survival ? activeArc = yesArc : activeArc = noArc;
				  dial.animate({rotation:rotString},1000,'<',function(){
            var activeText = yesText ;
            if(activeArc == noArc) activeText = noText ;
            activeText.attr({ "fill": "#666", "font-weight" : "bold" });
					  activeArc.animate({scale:"1.05 1.05", stroke: "#666"},200,'bounce',function() {
						  activeArc.animate({scale:"1 1"},200,'bounce',function(){
							  if(obj.returnData.status == 'TUTORIAL'){
								  setTimeout(function() {
										$('#tutorial_next').css({'visibility' : 'visible'});
										$('.hide-on-click').css({'visibility' : 'hidden'});
									},2000); 
						    } else {
								  setTimeout(function(){
									  obj.hideSurvivalPanel();
									  obj.nextRound();
									},2000);
								}
							});
						});
					  });
				});
				}
			  
			  if(autoRender == true){
				//console.log(obj.survival);
				  obj.survival ? activeArc = yesArc: activeArc = noArc;
				  dial.animate({rotation:rotString},0,'<',function(){
					  activeArc.animate({scale:"1.05 1.05", stroke: "#666"},0,'bounce',function(){
						activeArc.animate({scale:"1 1"},0,'bounce');
						});
					  });
            var activeText = yesText ;
            if(activeArc == noArc) activeText = noText ;
            activeText.attr({ "fill": "#666", "font-weight" : "bold" });
				}
			};
			
		this.createCard = function(cardSettings){
			var settings = $.extend({
				active : true,
				content : true,
				title : false,
				cardType : ''
				}, cardSettings || {});
			var $card = $('<div class="card" id="'+settings.cardType+'"/>');
			
				var $label = $('<div class="label card_label"/>');
				switch(settings.cardType){
					case 'low_payoff': $label.text(obj.cardLabelLowPayoff);break;
					case 'high_payoff': $label.text(obj.cardLabelHighPayoff);break;
					}
				var $earnings = $('<div class="earnings"/>');
				switch(settings.cardType){
					case 'low_payoff': 
						if(obj.lowPayoffPoint > 0) $earnings.html('<span class="earning">+'+ ( obj.lowPayoffPoint || "Low Payoff" ) + "</span> " + ( obj.units || "units" ));
						else $earnings.html('<span class="earning">'+ ( obj.lowPayoffPoint || "Low Payoff" ) + "</span> " + ( obj.units || "units" ));
						break;
					case 'high_payoff': 
						if(obj.lowPayoffPoint > 0) $earnings.html('<span class="earning">+'+ ( obj.highPayoffPoint || "Low Payoff" ) + "</span> " + ( obj.units || "units" ));
						else $earnings.html('<span class="earning">'+ ( obj.highPayoffPoint || "Low Payoff" ) + "</span> " + ( obj.units || "units" ));
						break;
					}
				var $blackMarks = $('<div class="blackmarks"/>');
					var $blackMarksLabel = $('<div class="label"/>');
					switch(settings.cardType){
						case 'low_payoff': $blackMarksLabel.text(obj.blackMarksLabelLowPayoff);break;
						case 'high_payoff': $blackMarksLabel.text(obj.blackMarksLabelHighPayoff);break;
						}
						
					switch(settings.cardType){
						case 'high_payoff': var $blackMarkBars = $('<div class="blackmark_bars"/>');
											$blackMarkBars.append('<div class="blackmark_bar active"/>');
											$blackMarks.append($blackMarksLabel,$blackMarkBars)
											break;
						case 'low_payoff': $blackMarks.append($blackMarksLabel);break;
						}
				//Add title above card - used in tutorial
				if(settings.title){
					switch(settings.cardType){
							case 'high_payoff': $card.append('<div class="figure-label">Card 2</div>');break;
							case 'low_payoff': $card.append('<div class="figure-label">Card 1</div>');;break;
							}
					}
				//Add content or add question mark for blank card
				if(settings.content)$card.append($label,$earnings,$blackMarks);
				else $card.append('<div class="question">?</div>');
				//Bind click event if card needs to be active
				if(settings.active)$card.bind('click',obj.dropCard);
			return $card;
			};
		
		this.showChoicePanel = function(){
		var obj = this;
			obj.choicePanel.animate({
				top : [-1,'easeInQuad']
				},1000);
			};
			
		this.showSurvivalPanel = function(){
		var obj = this;
			obj.survivalPanel.find('button').removeAttr('disabled');
			obj.survivalPanel.animate({
				top : [-1,'easeInQuad']
				},1000);
			};
			
		this.hideSurvivalPanel = function(){
		var obj = this;
			if(obj.survivalPanel)
			obj.survivalPanel.animate({
				top : [ (obj.survivalPanel.height() + 7) * -1,'easeInQuad']
				},1000);
			};
		
		this.dropCard = function(){
		var trigger = $(this);
			//hide captions and arrow in tutorial mode
			if($('.figure-label,#down_arrow').size() > 0)$('.figure-label,#down_arrow').hide();
			//hiding over
			var cardType= trigger.attr('id');
			$('#'+cardType).css({'z-index':900});
			var middle = (obj.choicePanel.width()/2) - ($('.card').width()/2);
			var top = obj.collectorBox.position().top + 20;
			var points;
			switch(cardType){
				case 'low_payoff': points = obj.lowPayoffPoint;
									obj.lastAction = "reward";
									break;
				case 'high_payoff': points = obj.highPayoffPoint;
									obj.lastAction = "betray";
									if(obj.returnData.status == 'TUTORIAL'){obj.blackMarkCount++;}
									break;
				}
			if(obj.choicePanel.parent().attr('id') != 'figure')
			$('.card')
			.parent()
				.remove('.card')
				.end()
			.appendTo(elem);
			
			$('.card')
			.animate({
				'left' : [middle,'easeOutQuad']
				},1000,function(){
					$('.card:not(#'+cardType+')').hide();
					if(obj.choicePanel.parent().attr('id') != 'figure')
					obj.choicePanel.animate({
						'top': (obj.choicePanel.height() + 7) * -1
						},1000);
					})
			.animate({
				'top' : top
				},1000,'easeOutQuad',function(){
					if($(this).attr('id')==cardType) { 
            var timeout = 1000/points ;
            if(timeout > 50) timeout = 50 ;
						obj.updateCollectionBoxEarning(parseInt(obj.tempteeBonus, 10) + parseInt(points, 10) , cardType, timeout);
						//console.log(parseInt(obj.tempteeBonus, 10) + parseInt(points, 10));
						if(cardType=='high_payoff'){obj.lastAction='betray';}
						if(cardType=='low_payoff'){obj.lastAction='reward';}
						}
						//reset card positions
						if(obj.choicePanel.parent().attr('id') != 'figure')
						$('.card')
						.parent()
							.remove('.card')
							.end()
						.appendTo(obj.choicePanel)
						.attr('style','');
					});
			};
		
		this.updateBlackMarks = function(cardType){
			var n = obj.blackMarkCount;
			for( var i = 1 ; i <= n ; i++){
				if(obj.returnData.status == 'PAYOFF'){
					if(i <= n) obj.collectorBox.find('.blackmark_bars').find('.blackmark_bar:eq('+ (i-1) +')').removeClass('active').addClass('inactive');
					if(i == n && ( cardType=='highPayoff' )) obj.collectorBox.find('.blackmark_bars').find('.blackmark_bar:eq('+ (i-1) +')').removeClass('active').css({'width':0}).addClass('inactive').animate({'width':110},500);
					if(i == n && ( cardType!='highPayoff' )) obj.collectorBox.find('.blackmark_bars').find('.blackmark_bar:eq('+ (i-1) +')').removeClass('active').addClass('inactive');
			  } else {
					obj.collectorBox.find('.blackmark_bars').find('.blackmark_bar:eq('+ (i-1) +')').removeClass('active').addClass('inactive');
				}
			}
			obj.collectorBox.find('#blackmark_count').text('('+ (obj.blackMarkUpperLimit-obj.blackMarkCount) +'/'+ obj.blackMarkUpperLimit +')');;
		};
			
		this.updateCollectionBoxEarning = function(newEarning, cardType, timeout){
			if(obj.tempteeBonus < newEarning) obj.tempteeBonus++;
			if(obj.tempteeBonus > newEarning) obj.tempteeBonus--;
			obj.collectorBox.find('.earnings').html('<span class="earning">'+obj.tempteeBonus + "</span> " + obj.units);
      if(obj.tempteeBonus != newEarning){
        setTimeout(function(){
          obj.updateCollectionBoxEarning(newEarning,cardType, timeout);
        }, timeout);
      } else{
        if(obj.lastAction=='betray' && obj.returnData.status != 'TUTORIAL'){obj.betray();}
        if(obj.lastAction=='reward' && obj.returnData.status != 'TUTORIAL'){obj.reward();}
        if(obj.returnData.status == 'TUTORIAL'){
          obj.updateBlackMarks(cardType);
          //changes to be done if in tutorial mode
          if(obj.returnData.status == 'TUTORIAL'){
            $('#tutorial_next').css({'visibility' : 'visible'});
            $('.hide-on-click').css({'visibility' : 'hidden'});
          }
        }
      }
    };
		
		this.betray = function(){
      $.ajax({
      type: "POST",
      url: "game",
      data: obj.getPostData("betray"),
      success: function(returnData){
        //Game.currentStatus = "PAYOFF";
        obj.processReturnedData(returnData);
      }
      });
    };
			
		this.reward = function(){
			  $.ajax({
				type: "POST",
				url: "game",
				data: obj.getPostData("reward"),
				success: function(returnData){
				  //Game.currentStatus = "PAYOFF";
				  obj.processReturnedData(returnData);
				}
			  });
			};
		this.endChanceAck = function() {
			  $.ajax({
				type: "POST",
				url: "game",
				data: obj.getPostData("endChanceAck"),
				success: function(returnData){
				  obj.processReturnedData(returnData);
				}
			  });
	  };	
		this.nextRound = function(){
			  $.ajax({
				type: "POST",
				url: "game",
				data: obj.getPostData("payoffAck"),
				success: function(returnData){
				    obj.processReturnedData(returnData);
					obj.updateBlackMarks();
					obj.updateCards();
					/* obj.showChoicePanel(); */
				}
			  });
			};
		
		this.payoffAck = function(){
			var endChance = obj.endChance;
			var notContinueSampling = obj.notContinueSampling;
			var survival = obj.survival;
			//code to draw whel of fortune or some other alternative
			obj.showSurvivalPanel();
			};
			
		this.updateCards = function(){
			obj.choicePanel.find('.card').each(function(){
				var cardType = $(this).attr('id');
				switch(cardType){
					case 'low_payoff': 
						if(obj.lowPayoffPoint > 0) $(this).find('.earnings').html('<span class="earning">+'+ obj.lowPayoffPoint + "</span> " + obj.units);
						else $(this).find('.earnings').html('<span class="earning">'+ obj.lowPayoffPoint + "</span> " + obj.units);
						break;
					case 'high_payoff': 
						if(obj.highPayoffPoint > 0) $(this).find('.earnings').html('<span class="earning">+'+ obj.highPayoffPoint + "</span> " + obj.units);
						else $(this).find('.earnings').html('<span class="earning">'+ obj.highPayoffPoint + "</span> " + obj.units);
										break;
					}
				});
			};
			
		this.gameOver = function() {
      if(obj.returnData.practice) {
        var plurial = "s" ;
        if(obj.returnData.gameCanPlay + 1 <= 1) plurial = "" ;
			  var $gameOverMessage = $(
          '<div id="game_over">' +
            '<h4>Practice game is over</h4>'+
            '<div class="label">You\'ve earned <b>' + obj.returnData.tempteeBonus +'</b> points!</div>' +
            '<div class="label">Now you will play the real game.</div> '+
            '<div class="label">You will be able to play it up to <b>' + (obj.returnData.gameCanPlay + 1)+ '</b> time' + plurial +'.</div> '+
            '<div class="label"><br/><button id="play_now">Begin</button></div> ' + 
          '</div>');
			  obj.choicePanel.html($gameOverMessage) ;
			  obj.showChoicePanel();
			   //onclick for 'Play again' button
			   $('#play_now').click(function() {
           obj.currentStatus = 'PRACTICE' ;
           obj.finishPractice();
			   });
        return ;
      }
      var buttons = '<div class="label"><button id="play_again">Play Again</button><button id="quit">Quit</button></div>';
      if(obj.returnData.lastGameForPlayer) {
        buttons = '<div class="label"><button id="quit">Quit</button></div>';
      }
      
      var plurial = "s" ;
      if(obj.returnData.gameCanPlay <= 1) plurial = "" ;
      var earnings = obj.returnData.tempteeBonus * obj.returnData.mturkRate ;
      earnings = earnings.toFixed(2);
			var $gameOverMessage = $(
          '<div id="game_over"> ' +
            '<h4>Game Over</h4>' + 
            '<div class="label">Thank you for playing.</div>' +
            '<div class="label" style="text-align: center">You\'ve earned <b>'+ obj.returnData.tempteeBonus +'</b> points in this game!</div> ' + 
            //'<div class="label">Your earnings in the current game have been recorded and will be deposited to your MTurk account within 14 business days.<div> ' + 
            '<div class="label" style="text-align: left">As promised, the bonus of ' + 
              '<div style="text-align: center; padding: 10px 0px"><b>'+ obj.returnData.tempteeBonus + '</b> points x <b>' + (obj.returnData.mturkRate * 100) + '</b> cents per point = <b>$' + earnings + '</b></div> ' + 
              'will be deposited to your MTurk account within 14 business days.' +
            '</div> ' + 
            '<div class="label" style="text-align: left"> You can play this game <b>' + obj.returnData.gameCanPlay +'</b> more time' +plurial+ '.</div>' + 
            buttons + 
          '</div>');

			obj.choicePanel.html($gameOverMessage) ;

      $("#choice_panel").css("height", "265px");

			obj.showChoicePanel();

      //onclick for 'Play again' button
      if(!obj.returnData.lastGameForPlayer) {
        $('#play_again').click(function(){
          //var url = window.location.href+'&points='+obj.tempteeBonus+'&next=true';
          var url = 'game?gameId='+obj.gameId+'&workerId=' + obj.workerId;
          window.location.href = url;
          //obj.isNext = "true";
        });
      }
      $('#quit').click(function(){
        obj.leaveFeedback() ;
        //obj.endChanceAck();
        //if (confirm('Do you really want to exit?')) { 
          //window.close();
          //window.location.href = 'http://google.com';
        //}
      });
		};
			
		this.doneTutorial = function (returnData) {
      $.ajax({
      type: "POST",
      url: "game",
      data: obj.getPostData("doneTutorial"),
      success: function(){
        obj.updateClient();
        }
      });
    };

		this.finishPractice = function (returnData) {
			$.ajax({
			  type: "POST",
				url: "game",
				data: obj.getPostData("finishPractice"),
				success: function() {
					obj.updateClient();
			  }
			});
		};

		this.generateDummyApp = function(dummySettings)	{
			var obj = this;
			var settings = $.extend({
				card1 : {
					visible : true, //should this elemnt be drawn
					active	:true, //should this be working,clickable
					content :true, //should this be filled else ? is shown
					title : true, //should this show title like Card 1
					cardType : 'low_payoff'
					},
          card2 : {
            visible : true, active : true, content :true, title : true, cardType : 'high_payoff'
          },
          collectorBox : {
            visible : true, title : true, content : true, downArrow : true, otherPlayer : false
          }
				}, dummySettings || {});
				//console.log(dummySettings);
				//create figure container
				$figure = $('<div id="figure"/>');
				//create cards with values from obj.returnData/initial values
				$card1 = obj.createCard(settings.card1);
				$card2 = obj.createCard(settings.card2);
				//wrap cards choice panel
				obj.choicePanel = $('<div id="choice_panel"/>');
				if(settings.card1.visible)obj.choicePanel.append($card1);
				if(settings.card2.visible)obj.choicePanel.append($card2);
				//create collectorBox
				if(settings.collectorBox.visible){
					obj.collectorBox = obj.createCollectorBox(settings.collectorBox);
					if(obj.returnData.status == 'TUTORIAL'){obj.updateBlackMarks();}
					}
				else obj.collectorBox = '';
				$figure.append(obj.choicePanel,obj.collectorBox);
				
				return $figure;
			};
    this.tutorial = function(screenNo, tutorialAction) {
      //debugger
      var obj = this;
      if(screenNo < obj.tutorialScreen().length) {
        //alway show the begining tempteeBonus, and the remaining whishes
        if(obj.origTempteeBonus == null) {
          obj.origTempteeBonus = obj.tempteeBonus;
        } else {
          obj.tempteeBonus = obj.origTempteeBonus;
          obj.blackMarkCount = 0 ;
        }
        obj.screenNo = screenNo;
        var dataSet = obj.tutorialScreen()[screenNo];
        if(elem.find('#tutorial').size()==0){
          elem.html('');
          elem.append('<div id="tutorial"></div>');
        }
        var $tutorial_panel = $('<div id="tutorial_panel"/>');
        var $tutorial_head = $('<h3>Tutorial</h3>');
        //Add heading
        $tutorial_panel.append($tutorial_head);
        //Add content
        for(var i=0 ; i < dataSet.content.length ; i++){
          $tutorial_panel.append(dataSet.content[i]);
        }
        if(screenNo == 0) {
          //Add next step button
          var $next_button = $('<button id="tutorial_next" />');
          $next_button.text(dataSet.nextButtonText);
          $next_button.click(function(){
            $(this).attr('disabled', 'disabled');
            obj.tutorial(screenNo + 1, 'Next');
            return false;
          });
          $tutorial_panel.append($next_button);
        }
        if(screenNo == obj.tutorialScreen().length - 1){
          //Add next step button
          var $next_button = $('<button id="tutorial_next" />');
          $next_button.text("Begin");
          $next_button.click(function() {
            $(this).attr('disabled', 'disabled');
            obj.tutorial(screenNo + 1, 'Next');
            return false;
          });
          $tutorial_panel.append($next_button);
        }
        //Add prev step button
        if(screenNo > 0 && screenNo <  obj.tutorialScreen().length - 1){
          //Add next step link
          var $next_button = $('<a href="#" id="tutorial_next" />');
          $next_button.text(dataSet.nextButtonText);
          //check if we have clickable lements if yes show the next button only on clicking the lement logig done in dropCard()
          if( dataSet.figure &&  ( dataSet.figure.card1.active || dataSet.figure.card2.active || dataSet.figure.rouletteActive ) ){
            $next_button.css({'visibility' : 'hidden'});
          }
          $next_button.click(function(){
            //$(this).attr('disabled', 'disabled');
            obj.tutorial(screenNo + 1, 'Next');
            return false;
          });
          $tutorial_panel.append($next_button);
          //Add prev step link
          var $prev_button = $('<a href="#" id="tutorial_prev" />');
          $prev_button.text(dataSet.prevButtonText);
          $prev_button.click(function(){
            obj.tutorial(screenNo - 1, 'Back');
            //$(this).attr('disabled', 'disabled');
            return false;
          });
          if(dataSet.prevButtonText!='')$tutorial_panel.append($prev_button);
        }
        //create figure
        if(dataSet.figure != '') $figure = obj.generateDummyApp(dataSet.figure);
        else $figure = $('<div id="figure"/>');

        elem.find('#tutorial').remove();
        elem.append($tutorial_panel,$figure);
        $tutorial_panel.css({'top':$tutorial_panel.outerHeight()*-1});
        //collectorBox auto height only after adding to dom
        if($('#collectorbox_table').size() > 0)$('#collectorbox_table').css({
          'top' : $('#collectorbox_front').position().top + $('#collectorbox_front').outerHeight() -20
        });
        $figure.hide();
        //show up tutorial panel and figure by animating
        $tutorial_panel.animate({top:-2},400,function(){
          $figure.fadeIn(500,function(){

          });
        });
        //Add roulette
        if($('#roulette').size() > 0){
          //obj.endChance = .4;
          obj.notContinueSampling = 600;
          obj.survival = true;
          $('#roulette').wrap('<div class="roulette_wrap" style="width:100px;margin:auto;"/>');
          obj.createWheelOfFortune('roulette','spinner');
        }
        if($('#roulette1').size() > 0){
          //obj.endChance = .4;
          obj.notContinueSampling = (999 -(999 * obj.endChance)) / 2;
          obj.survival = true;
          $('#roulette1').wrap('<div class="roulette_wrap" style="width:100px;margin:auto;"/>');
          obj.createWheelOfFortune('roulette1','',true);
        }
        if($('#roulette2').size() > 0){
          //obj.endChance = .4;
          obj.notContinueSampling = 999 * obj.endChance /2;
          obj.survival = false;
          $('#roulette2').wrap('<div class="roulette_wrap" style="width:100px;margin:auto;"/>');
          setTimeout(function(){obj.createWheelOfFortune('roulette2','',true);},200);
        }
        obj.trackTutorial(screenNo, tutorialAction) ;
      } else {
        obj.doneTutorial();
      }
    };

    this.trackTutorial = function(screenNo, tutorialAction) {
      $.ajax({
        type: "POST",
          url: "game",
          data: { 
            "a": "trackTutorial", 
            "gameId" : decodeURIComponent(obj.gameId), 
            "slotId" : decodeURIComponent(obj.slotId), 
            "workerId" : obj.workerId ? decodeURIComponent(obj.workerId) : "",
            "screenNo": screenNo,
            "tutorialAction": tutorialAction,
        },
        success: function(returnData) {
        }
      });
    };

      this.leaveFeedback = function() {
        $("#collectorbox").hide() ;
        $("#survival_panel").hide() ;

        $("#choice_panel").html("");
        var feedbackHtml = 
          "<h3>Your Feedback</h3>" +
          "<div id='message'>" + 
          "  <p>Please let us know about your experiences during the game.</p>" +
          "  <p>All properly completed forms will be rewarded by an additional bonus of <b>$" + obj.feedbackBonus + "</b>.</p>" +
          "  <div>" + 
          "    <div class='fb_left'>What's your age? </div>" +
          "    <div class='fb_right'><input id='yourAge' style='width: 50px'></div>" +
          "  </div>" +

          "  <div>" + 
          "    <div class='fb_left'>What's your gender? </div>" +
          "    <div class='fb_right'>" +  
          "      <input type='radio' name='gender' value='1'> Male" + 
          "      <input type='radio' name='gender' value='0'> Female" + 
          "    </div>" +
          "  </div>" +

          "  <div>" + 
          "    <div class='fb_left'>Is English your native language?</div>" +
          "    <div class='fb_right'>" +  
          "    <input type='radio' name='nativeLanguage' value='1'> Yes" + 
          "    <input type='radio' name='nativeLanguage' value='0'> No" + 
          "    </div>" +
          "  </div>" +

          "  <div>" + 
          "    <div class='fb_left'>How clear were the instructions?</div>" + 
          "    <div class='fb_right'><div id='fb_instr'></div>" + 
          "    <span class='leftSliderValue'>Not at all</span>" + 
          "    <span class='rightSliderValue'>Very clear</span>" +
          "  </div>" +
          //"</div>" + 
          "  <div>" + 
          "    <div class='fb_left'>Did you find the game interesting?</div>" + 
          "    <div class='fb_right'><div id='fb_inter'></div>" +
          "    <span class='leftSliderValue'>Not at all</span>" + 
          "    <span class='rightSliderValue'>Very interesting</span>" +
          "  </div>" +
          //"</div>" +
          "  <div>" +
          "    <div class='fb_left'>How would you characterize the pace of the game? </div>" +
          "    <div class='fb_right'><div id='fb_speed'></div>"+
          "    <span class='leftSliderValue'>Too slow</span>" + 
          "    <span class='rightSliderValue'>Too fast</span>" + 
          "  </div>" +
          //"</div>" +
          "  <div>" + 
          "    <div class='fb_left'>Did you use a different strategy depending on how many wishes you had left?  If so, explain how it was different.</div>" +
          "    <div class='fb_right'><textarea type='text' id='fb_strat' style='width:400px;font-family: Verdana,Sans-Serif; font-size: 12px;' rows='3'></textarea></div>" +
          "  </div>" +
          "  <div>" + 
          "    <div class='fb_left'>What other thoughts do you have about the game?</div>" +
          "    <div class='fb_right'><input id='fb_think' style='width: 400px'></div>" +
          "  </div>" +
          //"  <p>What other thoughts do you have about the game? </p>" +
          //"  <div><textarea style='font-family: Verdana,Sans-Serif; font-size: 12px;' id='fb_think' cols='80' rows='5'></textarea></div>" +
          "</div>" +
          "<div style='clear:both'></div>" +

          "<div class='navi'>" +
          "  <div class='begin'><button id='begin'>Submit</button></div>" +
          "</div>" ;
        var fbPanel = $("#choice_panel"); 
        fbPanel.html(feedbackHtml);
        fbPanel.css("position", "relative");
        fbPanel.css("top", "0px");
        fbPanel.css("left", "0px");
        fbPanel.css("width",  "700px");
        fbPanel.css("height", "auto");
        fbPanel.css("padding", "10px");
        fbPanel.css("margin", "0px auto");
        fbPanel.parent().css("width", "700px");

        var sliderOption = { min: 1, max: 5, value: 1, step: 1 };
        $("#fb_instr").slider(sliderOption);
        $("#fb_inter").slider(sliderOption);
        $("#fb_speed").slider(sliderOption);

        $('#begin').click(function(){
          progress = 'SentFeedback';
          //updateClient();
          //track("Submit feedback", "", 5);
          $('#begin').attr("disabled", true);
          var age   = encodeURIComponent($("#yourAge").val());
          if(age == '') age = -1 ;
          var gender = $('input[name=gender]:checked', '#message').val() ;
          if(gender == undefined) gender = -1 ;
          var nativeLanguage = $('input[name=nativeLanguage]:checked', '#message').val() ;
          if(nativeLanguage == undefined) nativeLanguage = -1 ;

          var instr = encodeURIComponent($("#fb_instr").slider("option", "value"));
          var inter = encodeURIComponent($("#fb_inter").slider("option", "value"));
          var speed = encodeURIComponent($("#fb_speed").slider("option", "value"));
          var strat = encodeURIComponent($("#fb_strat").val());
          var think = encodeURIComponent($("#fb_think").val());

          $.ajax({
            type: "POST",
            url: "game",
            data: { 
              "a": "sendFeedback", 
				      "gameId" : decodeURIComponent(obj.gameId), 
				      "slotId" : decodeURIComponent(obj.slotId), 
				      "workerId" : obj.workerId ? decodeURIComponent(obj.workerId) : "",
              "age": age, "gender": gender, "nativeLanguage": nativeLanguage, 
              "instr": instr, "inter": inter, "speed": speed, "strat": strat, "think": think
            },
            success: function(returnData) {
              $("#title").html("");
              $("#message").html("<p style='font-size: 18px;font-weight: bold;height: 23px;text-align: center;'>Thank you for your feedback!</p>");
              $("#navi").html("<div class='navi'></div>");
            }
          });
        });
      }
	 };

   $.fn.temptationgame = function(options) {
     return this.each(function() {
       var element = $(this);
      
       // Return early if this element already has a plugin instance
       if (element.data('temptationgame')) return;

       // pass options to plugin constructor
       var temptationgame = new TemptationGame(this, options);
       temptationgame.init();
       // Store plugin object in this element's data
       element.data('temptationgame',temptationgame);
     });
   };
}

)(jQuery);

