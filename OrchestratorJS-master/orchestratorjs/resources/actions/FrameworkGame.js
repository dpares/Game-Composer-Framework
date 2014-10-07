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

module.exports = {

    exceptionHandler: function(action, device, exception_value) {
        console.log('error on client-side: '+ device.identity+', '+exception_value);
        action.finishAction();
    },

    eventHandler: function(action, device, event_value) {
        console.log('event from client: '+device.identity+', '+event_value);
    },
    
    
    // the body
    body: function (devices) {
        var p = console.log;
        var misc = require('./misc.js'); 
      	var config = require('./FrameworkConfig.js');

        var Game = config.gameController;

        function Player() { 
            this.device = null;
            this.state  = null;
            this.active = true;
        };
      
        /// GAME INITIALIZATION	
        var players = [];
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
        var gameController = new Game(players);
        showCurrentState(players,gameController.commonData);

        /// MAIN LOOP
        while(countActivePlayers(players) > 1){
            var currentPlayer = 0;
            for(var currentPhase = 0; currentPhase < config.phases; currentPhase++){
                gameController.phaseSetUp(currentPhase,players);
                showCurrentState(players,gameController.commonData);
                do{
                    var numPlayers = gameController.countAvailablePlayers(players);
                    if(numPlayers > 1){
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
            // REPLAY GOES HERE
        }
    },

};
	