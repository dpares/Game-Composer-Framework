var playerStatus = {
    DEFAULT : 0,
    FINISHED : 1
};

var config = {};
config.initData = {initial_funds : 1000, activity_class : "framework.pokergame.ParchisActivity", 
    player_class : "framework.pokergame.ParchisPlayer"};
config.phases = 2;
config.steps = [1,3];


Game.prototype.newRound = function(players){
    this.winners = [];
    this.commonData = {last_roll: 0};
}

function Game(players){
    this.newRound();
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
            players[i].device.frameworkCapability.setPlayerState(state);
        }
    } else {
        //TODO
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
    //TODO
}

Game.prototype.computeResults = function(players){
    for(i in players)
        if (players[i].status.state == playerStatus.FINISHED)
            this.winners.push(players[i].state.name);
}

Game.prototype.declareWinners = function(players){
    return this.winners;
}

Game.prototype.isActive = function(player){
    return player.state.status == playerStatus.FINISHED;
}

function remove(array,index){
    var res = [];
    for(i in array)
        if(i!=index)
            res.push(array[i]);

    return res;
}

Game.prototype.exceptionHandler = function(players, device, exception_value){
    return remove(players,i);
}

module.exports.Game = Game;
module.exports.config = config;