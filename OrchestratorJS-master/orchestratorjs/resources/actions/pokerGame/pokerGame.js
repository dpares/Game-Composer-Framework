var Deck = require('./Deck.js');
var BestHand = require('./BestHand.js');

var playerStatus = {
    DEFAULT : 0,
    DEALER : 1,
    SMALL_BLIND : 2,
    BIG_BLIND : 3,
    ALL_IN : 4,
    FOLDED : 5
};

var config = {};
config.initData = {initial_funds : 1000, activity_class : "composer.pokergame.PokerActivity", 
    player_class : "composer.pokergame.PokerPlayer"};
config.phases = 5;
config.steps = [1,1,1,1,1];

function compareBestHands(a,b) {
    var res = a.type - b.type; //comparing types
    var i = 0;
    while (res == 0 && i < a.highValues.length) {
        res = a.highValues[i] - b.highValues[i];
        i++;
    }
    return res;
}

Game.prototype.newRound = function(players){
    this.deck = new Deck();
    this.commonData = {community_cards: [], biggest_bet: 200, current_pot: 0};
    this.bestHands = [];
    this.dealer = this.nextPlayer(this.dealer,players);

    players[this.dealer].state.status = playerStatus.DEALER;
    players[this.dealer].device.composerCapability.setPlayerState(players[this.dealer].state);
}

function Game(players){
    this.dealer = -1;

    this.newRound(players);

}

Game.prototype.countAvailablePlayers = function(players){
    var res = 0;
    for(i in players){
        if(players[i].active && players[i].state.status != playerStatus.ALL_IN && players[i].state.status != playerStatus.FOLDED)
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
        } while (!players[res].active || player.status == playerStatus.ALL_IN || player.status == playerStatus.FOLDED);
    }
    return res;
}

Game.prototype.phaseSetUp = function(currentPhase,players){
    if(currentPhase == 0){
        var smallBlindPlayer = -1, bigBlindPlayer;   
        if(players.length > 2){
            smallBlindPlayer = this.nextPlayer(this.dealer,players);
            bigBlindPlayer = this.nextPlayer(smallBlindPlayer,players);
        } else{
            bigBlindPlayer = this.nextPlayer(this.dealer,players);
        }
        for(i in players){
            if(players[i].active){
                var player = players[i].state;
                if(i == smallBlindPlayer)
                    player.status = playerStatus.SMALL_BLIND;
                else if(i == bigBlindPlayer)
                    player.status = playerStatus.BIG_BLIND;
                for(var j=0;j<2;j++)
                    player.hole_cards.push(this.deck.draw());
                players[i].device.composerCapability.setPlayerState(player);
            }
        }    
    } else if(currentPhase == 2){
        this.deck.discard();
        for(var i=0;i<3;i++)
            this.commonData.community_cards.push(this.deck.draw());
    } else if(currentPhase > 2 && currentPhase < 5){
        this.deck.discard();
        this.commonData.community_cards.push(this.deck.draw());
    }
}

Game.prototype.phaseEnd = function(currentPhase,players){
    var res = true;
    if(currentPhase > 0 && currentPhase < 5){
        var i = 0;
        while (i < players.length && res) {
            var p = players[i].state;
            if (!players[i].active || p.status == playerStatus.FOLDED || p.status == playerStatus.ALL_IN || 
                    p.current_bet == this.commonData.biggest_bet)
                i++;
            else
                res = false;
        }
    }
    return res;
}

Game.prototype.updateCommonData = function(commonData){
    this.commonData.biggest_bet = commonData.biggest_bet;
    this.commonData.current_pot = commonData.current_pot;
}

Game.prototype.computeResults = function(players){
    for(p in players){
        var holeCards = JSON.parse(JSON.stringify(players[p].state.hole_cards));
        if(players[p].active && players[p].state.status != playerStatus.FOLDED){
            var hand = new BestHand(holeCards,this.commonData.community_cards,players[p].state.name);
            players[p].state.best_hand = hand;
            this.bestHands.push(hand);
        }
    }
}

Game.prototype.declareWinners = function(players){
    var res = [];
    if(this.bestHands.length > 0){
        this.bestHands.sort(compareBestHands);
        this.bestHands.reverse();
        var i = 0;
        res.push(this.bestHands[i].owner);
        while(i+1 < this.bestHands.length && compareBestHands(this.bestHands[i],this.bestHands[i+1])==0){
            i++;
            res.push(this.bestHands[i].owner);
        }
    }

    return res;
}

Game.prototype.isActive = function(player){
    return player.funds > 0;
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
    this.commonData.current_pot += players[i].state.funds;
    return remove(players,i);
}

module.exports.Game = Game;
module.exports.config = config;