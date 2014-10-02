var Deck = require('./Deck.js');

var playerStatus = {
    DEFAULT : 0,
    DEALER : 1,
    SMALL_BLIND : 2,
    BIG_BLIND : 3,
    ALL_IN : 4,
    FOLDED : 5
};

function Game(players){
    var deck = new Deck();
    this.commonData = {communityCards: [], biggestBet: 0, currentPot: 0};
    
    this.commonData.communityCards.push(deck.draw());
    this.commonData.communityCards.push(deck.draw());
    this.commonData.communityCards.push(deck.draw());
    this.commonData.communityCards.push(deck.draw());
    this.commonData.communityCards.push(deck.draw());
    players[0].device.frameworkCapability.setStatus(playerStatus.DEALER);

}

module.exports = Game;