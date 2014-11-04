var p = console.log;
var misc = require('./misc.js'); 
var config = require('./FrameworkConfig.js');

var Game = config.gameController;

var players = [];
var j;
var gameController, currentPlayer, numPlayers, currentPhase, currentStep;
var handlingDisconnection;

function Player() { 
    this.device = null;
    this.state  = null;
    this.active = true;
};

function playersStatesArray(){
    var res = [];
    for(i in players)
        if(players[i].active)
            res.push(players[i].state);
    return res;
}   

function showCurrentState(){
    var playersState = playersStatesArray()
    j = 0;
    while(j < players.length){
        if(!handlingDisconnection){
            players[j].device.frameworkCapability.showCurrentState(playersState,gameController.commonData);
            j++;
        } else
            handlingDisconnection = true;
    }
}

function countActivePlayers(){
    var res = 0;
    for(var i = 0; i < players.length; i++){
        if(players[i].active)
            res++;
    }

    return res;
}

function showResults(){
    showCurrentState();
    var winners = gameController.declareWinners(players);
    var json = {data: winners};
    j = 0;
    while(j < players.length){
        if(!handlingDisconnection){
            var playerState = players[j].device.frameworkCapability.showResults(json,playersStatesArray(),gameController.commonData);
            players[j].state = playerState;
            if(!gameController.isActive(players[j].state))
                players[j].active = false;
            j++;
        } else
            handlingDisconnection = true;
    }
    showCurrentState();
}

function remove(array,index){
    var res = [];
    for(i in array)
        if(i!=index)
            res.push(array[i]);

    return res;
}

function handleDisconnection(device, event_value){
    if(currentPlayer != -1){
        if (players[currentPlayer].device.identity == device.identity){
            handlingDisconnection = true;
            currentStep = config.phases[currentPhase];
        }
        players = gameController.exceptionHandler(players, device, event_value);
        numPlayers--;
    } else if(typeof players[j] === 'undefined'){
        handlingDisconnection = true;
    } else {
        if(players[j].device.identity = device.identity)
            handlingDisconnection = true;
        players = gameController.exceptionHandler(players, device, event_value);
    }
}

module.exports = {

    exceptionHandler: function(action, device, exception_value) {
        console.log('error on client-side: '+ device.identity+', '+exception_value);
    },

    serverSideExceptionHandler: function(action, exception_value) {
        console.log('error on server-side: '+exception_value);
        handlingDisconnection = true;
        j++;
    },

    eventHandler: function(action, device, event_value) {
        console.log('event from client: '+device.identity+', '+event_value);
        if(event_value == "disconnect")
            handleDisconnection(device, event_value);
        if(players.length == 0)
            action.finishAction();
    },
    
    
    // the body
    body: function (devices) {
        var currentDevices = devices;
        do{      
            /// GAME INITIALIZATION	
            j = 0;
            handlingDisconnection = false;
            currentPlayer = -1;
            if(players.length > 0){
                console.log("PLAYERS: " + players.length);
                currentDevices = [];
                for(i in players)
                    currentDevices.push(players[i].device);
            }
            players = [];
            while(j < currentDevices.length) {
                p(devices[j].identity);
                var player = new Player();
                player.device = currentDevices[j];
                player.device.frameworkCapability.initGame(config.initData);
                if(!handlingDisconnection){
                    var initState = player.device.frameworkCapability.getPlayerInitialState();
                    while(initState.null && !handlingDisconnection){
                        misc.sleep(1);
                        initState = player.device.frameworkCapability.getPlayerInitialState();
                    }
                    if(!handlingDisconnection){
                        player.state = initState.state;
                        player.active = initState.active;
                        players.push(player);
                    } else
                        handlingDisconnection = false;
                } else
                    handlingDisconnection = false;
                j++;
            }
            gameController = new Game(players);
            if(countActivePlayers() > 1)
                showCurrentState();
            else{
                j = 0;
                while(j < players.length){
                    if(!handlingDisconnection){
                        players[j].device.frameworkCapability.exitGame("Not enough players");
                        j++;
                    } else
                        handlingDisconnection = false;
                }
            }

            /// MAIN GAME LOOP
            while(countActivePlayers() > 1){
                currentPlayer = gameController.nextPlayer(-1,players);
                currentPhase = 0;
                while(currentPhase < config.phases){
                    gameController.phaseSetUp(currentPhase,players);
                    showCurrentState();
                    do{
                        numPlayers = gameController.countAvailablePlayers(players);
                        if(numPlayers == 1 && !gameController.phaseEnd(currentPhase,players) || numPlayers > 1){
                            for(var i = 0; i < numPlayers; i++){
                                var player = players[currentPlayer];
                                currentStep = 0;
                                while(currentStep < config.steps[currentPhase]){
                                    player.device.frameworkCapability.startStep(currentPhase,currentStep);
                                    if(!handlingDisconnection)
                                        var stepResult = player.device.frameworkCapability.getStepResult(currentPhase,currentStep);
                                    while(stepResult.null && !handlingDisconnection){
                                        misc.sleep(1);
                                        if(!handlingDisconnection)
                                            stepResult = player.device.frameworkCapability.getStepResult(currentPhase,currentStep);
                                    }
                                    if(handlingDisconnection)
                                        handlingDisconnection = false;
                                     else{
                                        player.state = stepResult.player_data;
                                        gameController.updateCommonData(stepResult.common_data);
                                        currentStep++;
                                    }
                                    showCurrentState();
                                }
                                currentPlayer = gameController.nextPlayer(currentPlayer,players);
                            }
                        }
                    }while (!gameController.phaseEnd(currentPhase,players));
                    currentPhase++;
                }
                currentPlayer = -1;
                gameController.computeResults(players);
                showResults();
                misc.sleep(4);
                if(countActivePlayers() > 1){
                    j = 0;
                    while(j < players.length){
                        if(!handlingDisconnection){
                            players[j].state = players[j].device.frameworkCapability.newRound();
                            j++;
                        } else
                            handlingDisconnection = true;
                    }
                    gameController.newRound(players);
                }
            }
            // ENDGAME
            var winner = gameController.nextPlayer(0,players); // Last active player
            j = 0;
            while (j < players.length){
                if(!handlingDisconnection){
                    players[j].device.frameworkCapability.announceWinner(playersStatesArray(),winner);
                    j++;
                } else 
                    handlingDisconnection = true;
            }
            j = 0;
            while (j < players.length){
                if(!handlingDisconnection)
                    var wantsRematch = players[j].device.frameworkCapability.askForRematch();
                while(wantsRematch.null && !handlingDisconnection){
                    misc.sleep(1)
                    if(!handlingDisconnection)
                        wantsRematch = players[j].device.frameworkCapability.askForRematch();
                }
                if(handlingDisconnection)
                    handlingDisconnection = false;
                else{
                    if(wantsRematch.value){
                        players[j].active = true;
                        j++;
                    }
                    else
                        players[j].device.frameworkCapability.exitGame("You have left the game");
                }
            }
            if(countActivePlayers() <= 1){
                j = 0;
                while(j < players.length){
                    if(!handlingDisconnection){
                        players[j].device.frameworkCapability.exitGame("Not enough players");
                        j++;
                    } else
                        handlingDisconnection = false;
                }
            }
        } while (countActivePlayers() > 1);
    },

};
	