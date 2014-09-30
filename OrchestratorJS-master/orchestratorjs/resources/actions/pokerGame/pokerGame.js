var Deck = require('./deck.js');

function Game(){
    var deck = new Deck();
    this.commonData = {deck: deck, communityCards: []};

}

module.exports = Game;