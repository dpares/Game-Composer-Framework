var deck = [];

function randomInt (limit) {
    return Math.floor(Math.random() * (limit + 1));
}

function Deck(){
    this.newHand();
}

Deck.prototype.newHand = function(){
    var deck = [];
    for (var i = 0; i < 4; i++) // Filling the deck with cards
        for (var j = 0; j < 13; j++){
            var card = {suit: i, rank: j};
            deck.push(card);
        }

    // Shuffling the deck
    var cont = deck.length;
    var aux, i;
    while (cont > 0) {
        i = Math.floor(Math.random() * cont);
        cont--;
        aux = deck[cont];
        deck[cont] = deck[i];
        deck[i] = aux;
    }
    this.deck = deck;
}

Deck.prototype.draw = function(){
    return this.deck.shift();
}

Deck.prototype.discard = function(){
    this.deck.shift();
}

module.exports = Deck;