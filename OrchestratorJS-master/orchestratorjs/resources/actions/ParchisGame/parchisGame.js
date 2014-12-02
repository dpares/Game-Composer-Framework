var playerStatus = {
    DEFAULT : 0,
    FINISHED : 1
};

var UNDEFINED_COLOUR = 4;

var config = {};
config.initData = {activity_class : "composer.parchisgame.ParchisActivity", player_class : "composer.parchisgame.ParchisPlayer"};
config.phases = 1;
config.steps = [1];


Game.prototype.newRound = function(players){
    this.winners = [];
    this.commonData = {last_roll: 0, last_player: -1, skip_turn: false};
}

function Game(players){
    this.newRound();

    for(i in players)
        players[i].state.colour = UNDEFINED_COLOUR;
}

Game.prototype.countAvailablePlayers = function(players){
    var res = 0;
    for(i in players){
        if(players[i].active && players[i].state.status != playerStatus.FINISHED)
            res++;
    }
    return res;
}

Game.prototype.nextPlayer = function(index,players){
    var res = index;
    var player;
    if(this.countAvailablePlayers(players) > 0){
        do{
            res++;
            if(res >= players.length)
                res = 0;
            player = players[res].state;
        } while (!players[res].active || player.status == playerStatus.FINISHED);
    }
    return res;
}

Game.prototype.phaseSetUp = function(currentPhase,players){
    for(i in players){
        players[i].state.colour = i;
        for(j in players[i].state.pawns)
            players[i].state.pawns[j].colour = i;
        players[i].device.composerCapability.setPlayerState(players[i].state);
    }
}

Game.prototype.phaseEnd = function(currentPhase,players){
    var res = false;
    if(this.countAvailablePlayers(players) <= 1)
        res = true;
    else {
        var i = 0;
        while (i < players.length && !res) {
            if (players[i].state.status == playerStatus.DEFAULT)
                i++;
            else
                res = true;
        }
    }
    return res;
}

Game.prototype.updateCommonData = function(commonData){
    this.commonData = commonData;
}

Game.prototype.computeResults = function(players){
    for(i in players)
        if (players[i].state.status == playerStatus.FINISHED)
            this.winners.push(players[i].state.name);
}

Game.prototype.declareWinners = function(players){
    return this.winners;
}

/* It's only checked in the end, so if a player hasn't finished yet he becomes inactive */
Game.prototype.isActive = function(player){ 
    return player.status == playerStatus.FINISHED;
}

function remove(array,index){
    var res = [];
    for(i in array)
        if(i!=index)
            res.push(array[i]);

    return res;
}

Game.prototype.exceptionHandler = function(players, device, exception_value){
    var playerFound = false;
    var i = 0;
    while(!playerFound && i<players.length){
        if(players[i].device.identity == device.identity)
            playerFound = true;
        else
            i++;
    }
    return remove(players,i);
}

module.exports.Game = Game;
module.exports.config = config;