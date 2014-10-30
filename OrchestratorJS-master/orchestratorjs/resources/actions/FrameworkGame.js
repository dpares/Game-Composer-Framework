var p = console.log;
var misc = require('./misc.js'); 
var config = require('./FrameworkConfig.js');

var Game = config.gameController;

var players = [];
var gameController;

function Player() { 
    this.device = null;
    this.state  = null;
    this.active = true;
};

function showCurrentState(players,commonData){
    var playersState = playersStatesArray(players)
    for(i in players){
        players[i].device.frameworkCapability.showCurrentState(playersState,commonData);
    }
}

function playersStatesArray(players){
    var res = [];
    for(i in players)
        res.push(players[i].state);
    return res;
}	

function countActivePlayers(players){
    var res = 0;
    for(var i = 0; i < players.length; i++){
        if(players[i].active)
            res++;
    }

    return res;
}

function showResults(players,gameController,misc){
    showCurrentState(players,gameController.commonData);
    var winners = gameController.declareWinners(players);
    var json = {data: winners};
    for(i in players){
        var playerState = players[i].device.frameworkCapability.showResults(
            json,playersStatesArray(players),gameController.commonData);
        players[i].state = playerState;
        if(!gameController.isActive(players[i].state))
            players[i].active = false;
    }
    showCurrentState(players,gameController.commonData);
}

module.exports = {

    exceptionHandler: function(action, device, exception_value) {
        console.log('error on client-side: '+ device.identity+', '+exception_value);
        /*if(gameController == undefined)
            action.finishAction();
        else{
            console.log("HUEHUEHUE");
            players = gameController.exceptionHandler(players, device, exception_value);
            showCurrentState(players, device, gameController.commonData);
        }*/
    },

    serverSideExceptionHandler : function(exception_value){
        console.log('error on server-side: '+ device.identity+', '+exception_value);
        // Cerrar el juego
    },

    eventHandler: function(action, device, event_value) {
        console.log('event from client: '+device.identity+', '+event_value);
        players = gameController.exceptionHandler(players, device, event_value);
        showCurrentState(players, gameController.commonData);
    },
    
    
    // the body
    body: function (devices) {
      
        /// GAME INITIALIZATION	
        for(i in devices) {
            p(devices[i].identity);
            var player = new Player();
            player.device = devices[i];
            player.number = parseInt(i);
            player.device.frameworkCapability.initGame(config.initData);
            player.state = player.device.frameworkCapability.getPlayerState();
            while(player.state.null){
                player.state = player.device.frameworkCapability.getPlayerState();
                misc.sleep(1);
            }
            players.push(player);
        }
        gameController = new Game(players);
        showCurrentState(players,gameController.commonData);

        /// MAIN LOOP
        while(countActivePlayers(players) > 1){
            var currentPlayer = gameController.nextPlayer(-1,players);
            for(var currentPhase = 0; currentPhase < config.phases; currentPhase++){
                gameController.phaseSetUp(currentPhase,players);
                showCurrentState(players,gameController.commonData);
                do{
                    var numPlayers = gameController.countAvailablePlayers(players);
                    if(numPlayers == 1 && !gameController.phaseEnd(currentPhase,players) || numPlayers > 1){
                        for(var i = 0; i < numPlayers; i++){
                            var player = players[currentPlayer];
                            for( var currentStep = 0; currentStep < config.steps[currentPhase]; currentStep++){
                                player.device.frameworkCapability.startStep(currentPhase,currentStep);
                                var stepResult = player.device.frameworkCapability.getStepResult(currentPhase,currentStep);
                                while(stepResult.null){
                                    stepResult = player.device.frameworkCapability.getStepResult(currentPhase,currentStep);
                                    misc.sleep(1);
                                }
                                player.state = stepResult.player_data;
                                gameController.updateCommonData(stepResult.common_data);
                                showCurrentState(players,gameController.commonData);
                            }
                            currentPlayer = gameController.nextPlayer(currentPlayer,players);
                        }
                    }
                }while (!gameController.phaseEnd(currentPhase,players));
            }
            gameController.computeResults(players);
            showResults(players,gameController,misc);
            misc.sleep(4);
            console.log(players);
            if(countActivePlayers(players) > 1){
                for(i in players)
                    players[i].state = players[i].device.frameworkCapability.newRound();
                gameController.newRound(players);
            }
            // Insert proper closure
        }
    },

};
	