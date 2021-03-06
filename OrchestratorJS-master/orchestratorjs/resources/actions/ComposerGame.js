var p = console.log;
var misc = require('./misc.js'); 

var players;
var j, currentPlayer, numPlayers, currentPhase, currentStep;
var Game, gameController ;
var handlingDisconnection, playersInitialized, disconnectingEveryone;

function Player() { 
    this.device = null;
    this.state  = null;
    this.active = true;
};

function playersStatesArray(showInactive){
    var res = [];
    var i = 0;
    while(i < players.length){
        if(players[i].active && ! handlingDisconnection || showInactive && !handlingDisconnection)
            res.push(players[i].state);
        if(!handlingDisconnection)
            i++;
        else
            handlingDisconnection = false;
    }
    return res;
}   

function showCurrentState(){
    var playersState = playersStatesArray();
    j = 0;
    while(j < players.length){
        if(!handlingDisconnection)
            players[j].device.composerCapability.showCurrentState(playersState,gameController.commonData);
        if(!handlingDisconnection)
            j++;
        if(handlingDisconnection)
            handlingDisconnection = false;
    }
}

function countActivePlayers(){
    var res = 0;
    var i = 0;
    while(i < players.length){
        if(players[i].active && !handlingDisconnection)
            res++;
        if(!handlingDisconnection)
            i++;
        else
            handlingDisconnection = false;
    }

    return res;
}

function showResults(){
    showCurrentState();
    var winners = gameController.declareWinners(players);
    var json = {data: winners};
    j = 0;
    while(j < players.length){
        var playerState;
        if(!handlingDisconnection)
            playerState = players[j].device.composerCapability.showResults(json,playersStatesArray(true),gameController.commonData);
        if(!handlingDisconnection){
            players[j].state = playerState;
            if(!gameController.isActive(players[j].state))
                players[j].active = false;
            j++;
        }
        if(handlingDisconnection)
            handlingDisconnection = false;
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

function handleDisconnection(action, device, event_value){
    if(currentPlayer != -1){
        if (players[currentPlayer].device.identity == device.identity){
            handlingDisconnection = true;
            currentStep = Game.config.phases[currentPhase];
        }
        players = gameController.exceptionHandler(players, device, event_value);
        numPlayers--;
    } else if(!playersInitialized){
        handlingDisconnection = true;
    }else {
        if(j < players.length && players[j].device.identity == device.identity)
            handlingDisconnection = true;
        players = gameController.exceptionHandler(players, device, event_value);
    }
    if(countActivePlayers() == 0 && !disconnectingEveryone){
        disconnectingEveryone = true;
        while(players.length > 0)
            players[0].device.composerCapability.exitGame("Not enough players");
        action.finishAction();   
    }
}

function updatePlayerStates(playersStates){
    for(i in players)
        players[i].state = playersStates[i];
}

module.exports = {

    exceptionHandler: function(action, device, exception_value) {
        console.log('error on client-side: '+ device.identity+', '+exception_value);
        if(playersInitialized)
            device.composerCapability.exitGame("An error ocurred");
        else
            handlingDisconnection = true
    },

    serverSideExceptionHandler: function(action, exception_value) {
        console.log('error on server-side: '+exception_value);
        handleDisconnection = true;
        if(j+1<players.length)
            j++;
    },

    eventHandler: function(action, device, event_value) {
        console.log('event from client: '+device.identity+', '+event_value);
        if(event_value == "disconnect")
            handleDisconnection(action, device, event_value);       
    },
    
    
    // the body
    body: function (devices, gameControllerPath) {
        var currentDevices = devices;
        Game = require(gameControllerPath);
        players = [];
        do{      
            /// GAME INITIALIZATION	
            handlingDisconnection = false;
            playersInitialized = false;
            currentPlayer = -1;
            if(players.length > 0){
                currentDevices = [];
                for(i in players)
                    currentDevices.push(players[i].device);
            }
            players = [];
            j = 0;
            while(j < currentDevices.length) {
                p(currentDevices[j].identity);
                var player = new Player();
                player.device = currentDevices[j];
                player.device.composerCapability.initGame(Game.config.initData);
                if(!handlingDisconnection){
                    var initState = player.device.composerCapability.getPlayerInitialState();
                    while(initState.null && !handlingDisconnection){
                        misc.sleep(1);
                        if(!handlingDisconnection)
                            initState = player.device.composerCapability.getPlayerInitialState();
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
            playersInitialized = true;
            gameController = new Game.Game(players);
            if(countActivePlayers() > 1)
                showCurrentState();
            else{
                j =0 ;
                while(j < players.length){
                    if(!handlingDisconnection)
                        players[j].device.composerCapability.exitGame("Not enough players");
                    if(!handlingDisconnection)
                         j++;
                    if(handlingDisconnection)
                        handlingDisconnection = false;
                }
            }

            /// MAIN GAME LOOP
            while(countActivePlayers() > 1){
                currentPlayer = gameController.nextPlayer(-1,players);
                currentPhase = 0;
                while(currentPhase < Game.config.phases){
                    gameController.phaseSetUp(currentPhase,players);
                    showCurrentState();
                    do{
                        numPlayers = gameController.countAvailablePlayers(players);
                        if(numPlayers > 1 || numPlayers == 1 && !gameController.phaseEnd(currentPhase,players)){
                            for(var i = 0; i < numPlayers; i++){
                                var player = players[currentPlayer];
                                currentStep = 0;
                                while(currentStep < Game.config.steps[currentPhase]){
                                    player.device.composerCapability.startStep(currentPhase,currentStep);
                                    var stepResult;
                                    if(!handlingDisconnection)
                                        stepResult = player.device.composerCapability.getStepResult(currentPhase,currentStep);
                                    while(!handlingDisconnection && stepResult.null){
                                        misc.sleep(1);
                                        if(!handlingDisconnection)
                                            stepResult = player.device.composerCapability.getStepResult(currentPhase,currentStep);
                                    }
                                    if(handlingDisconnection)
                                        handlingDisconnection = false;
                                     else{
                                        if(stepResult.hasOwnProperty("player_data"))
                                            player.state = stepResult.player_data;
                                        else if(stepResult.hasOwnProperty("all_players_data"))
                                            updatePlayerStates(stepResult.all_players_data);
                                        gameController.updateCommonData(stepResult.common_data);
                                        if(!stepResult.hasOwnProperty("additional_step"))
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
                if(countActivePlayers() > 1){
                    misc.sleep(4);
                    j = 0;
                    while(j < players.length){
                        if(!handlingDisconnection)
                            players[j].state = players[j].device.composerCapability.newRound();
                        if(!handlingDisconnection)
                            j++;
                        if(handlingDisconnection)
                            handlingDisconnection = false;
                    }
                    gameController.newRound(players);
                }
            }
            // ENDGAME
            var winner = gameController.nextPlayer(0,players); // Last active player
            j = 0;
            while (j < players.length){
                if(!handlingDisconnection)
                    players[j].device.composerCapability.announceWinner(playersStatesArray(true),winner);
                if(!handlingDisconnection)
                    j++;
                if(handlingDisconnection)
                    handlingDisconnection = false;
            }
            misc.sleep(4);
            j = 0;
            while (j < players.length){
                var wantsRematch;
                if(!handlingDisconnection){
                    wantsRematch = players[j].device.composerCapability.askForRematch();
                }while(!handlingDisconnection && wantsRematch.null){
                    misc.sleep(1);
                    if(!handlingDisconnection)
                        wantsRematch = players[j].device.composerCapability.askForRematch();
                }
                if(handlingDisconnection)
                    handlingDisconnection = false;
                else{
                    if(wantsRematch.value){
                        players[j].active = true;
                        j++;
                    }
                    else if(!handlingDisconnection)
                        players[j].device.composerCapability.exitGame("You have left the game");
                }
            }
            if(countActivePlayers <= 1){
                j = 0;
                while(j < players.length){
                    if(!handlingDisconnection)
                        players[j].device.composerCapability.exitGame("Not enough players");
                    if(!handleDisconnection)
                        j++;
                    if(handlingDisconnection)
                        handlingDisconnection = false;
                }
            }
        } while (players.length > 1); // Number of active players will be checked after initialization
    },

};
	