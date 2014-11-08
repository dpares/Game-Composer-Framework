var playerStatus = {
    DEFAULT : 0,
    FINISHED : 1
};

var UNDEFINED_COLOUR = 4;

var config = {};
config.initData = {activity_class : "framework.parchisgame.ParchisActivity", player_class : "framework.parchisgame.ParchisPlayer"};
config.phases = 1;
config.steps = [3];


Game.prototype.newRound = function(players){
    this.winners = [];
    this.commonData = {last_roll: 0};
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
            if(res == players.length)
                res = 0;
            player = players[res].state;
        } while (!players[res].active || player.status == playerStatus.FINISHED);
    }
    return res;
}

Game.prototype.phaseSetUp = function(currentPhase,players){
    if(currentPhase == 0){
        for(i in players){
            players[i].state.colour = i;
            players[i].device.frameworkCapability.setPlayerState(players[i].state);
        }
    }
}

Game.prototype.phaseEnd = function(currentPhase,players){
    var res = true;
    if(currentPhase == 1){
        var i = 0;
        while (i < players.length && res) {
            if (players[i].state.status == playerStatus.DEFAULT)
                i++;
            else
                res = false;
        }
    }
    return res;
}

Game.prototype.updateCommonData = function(commonData){
    this.commonData.last_roll = commonData.last_roll
}

Game.prototype.computeResults = function(players){
    for(i in players)
        if (players[i].state.status == playerStatus.FINISHED)
            this.winners.push(players[i].state.name);
}

Game.prototype.declareWinners = function(players){
    return this.winners;
}

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