var p = console.log;
var misc = require('./misc.js'); 
var config = require('./FrameworkConfig.js');

var Game = config.gameController;

var players = [];
var gameController, currentPlayer, numPlayers, currentPhase, currentStep;
var handlingDisconnection = false;

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
    for(i in players)
        players[i].device.frameworkCapability.showCurrentState(playersState,gameController.commonData);
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
    for(i in players){
        var playerState = players[i].device.frameworkCapability.showResults(json,playersStatesArray(),gameController.commonData);
        players[i].state = playerState;
        if(!gameController.isActive(players[i].state))
            players[i].active = false;
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
    if(players[currentPlayer].device.identity == device.identity){
        handlingDisconnection = true;
        currentStep = config.phases[currentPhase];
    }
    players = gameController.exceptionHandler(players, device, event_value);
    numPlayers--;
}

module.exports = {

    exceptionHandler: function(action, device, exception_value) {
        console.log('error on client-side: '+ device.identity+', '+exception_value);
        for(i in players)
            players[i].device.frameworkCapability.exitGame("An error ocurred");
        action.finishAction();
    },

    eventHandler: function(action, device, event_value) {
        console.log('event from client: '+device.identity+', '+event_value);
        if(event_value == "disconnect")
            handleDisconnection(device, event_value);
    },
    
    
    // the body
    body: function (devices) {

        do{      
            /// GAME INITIALIZATION	
            players = [];
            for(i in devices) {
                p(devices[i].identity);
                var player = new Player();
                player.device = devices[i];
                player.device.frameworkCapability.initGame(config.initData);
                var initState = player.device.frameworkCapability.getPlayerInitialState();
                while(initState.null){
                    misc.sleep(1);
                    initState = player.device.frameworkCapability.getPlayerInitialState();
                }
                player.state = initState.state;
                player.active = initState.active;
                players.push(player);
            }
            console.log("PLAYERS:" + JSON.stringify(playersStatesArray()));
            gameController = new Game(players);
            if(countActivePlayers() > 1)
                showCurrentState();
            else
                for(i in players)
                    players[i].device.frameworkCapability.exitGame("Not enough players");

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
                gameController.computeResults(players);
                showResults();
                misc.sleep(4);
                if(countActivePlayers() > 1){
                    for(i in players)
                        players[i].state = players[i].device.frameworkCapability.newRound();
                    gameController.newRound(players);
                }
            }
            // ENDGAME
            var winner = gameController.nextPlayer(currentPlayer,players); // Last active player
            for(i in players)
                players[i].device.frameworkCapability.announceWinner(playersStatesArray(),winner);
            if(countActivePlayers() > 1){
                for(i in players){
                    var wantsRematch = players[i].device.frameworkCapability.askForRematch();
                    while(wantsRematch.null){
                        misc.sleep(1)
                        wantsRematch = players[i].device.frameworkCapability.askForRematch();
                    }
                    if(wantsRematch.value)
                        players[i].active = true;
                    else{
                        players[i].active = false;
                        players[i].device.frameworkCapability.exitGame("You have left the game");
                        players = remove(players,i);
                    }
                }
            } else {
                // Avisar de que se va a cerrar la sesión ya que es el único jugador
            } 

        } while (countActivePlayers() > 1);
    },

};
	