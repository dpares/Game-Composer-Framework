var Type = {
    HIGH_CARD:0, 
    ONE_PAIR:1, 
    TWO_PAIR:2, 
    THREE_OF_A_KIND:3, 
    STRAIGHT:4, 
    FLUSH:5, 
    FULL_HOUSE:6, 
    FOUR_OF_A_KIND:7, 
    STRAIGHT_FLUSH:8
}

var HIGHEST_VALUE = 14;

var type;
var highValues = [];

function remove(array,index){
    var res = [];
    for(i in array)
        if(i!=index)
            res.push(array[i]);

    return res;
}

function getRank(card){
    var res = card.rank + 1; // Aces are stored with a 0 rank
    if(res == 1)
        res = HIGHEST_VALUE; // Aces have the biggest value
    return res;
}

function compareCards(a,b){
    var res = a.rank - b.rank;
    if(res == 0)
        res = a.suit - b.suit;
    return res;
}

function compareBestHands(a,b) {
    var res = a.type - b.type; //comparing types
    var i = 0;
    while (res == 0 && i < a.highValues.lenght) { // Every BestHand's highValues has the same length.
        res = a.highValues[i] - b.highValues[i];
        i++;
    }
    return res;
}

function bestCombination(cards) {
    var res, best;
    if (cards.length > 5) {
        var aux, tempCards, cardsLength = cards.length;
        for (var i = 0; i < cardsLength; i++) {
            tempCards = remove(cards,i);
            res = bestCombination(tempCards);
            if (!best || compareBestHands(best,res) < 0)
                best = res;
        }
        return best;
    } else {
        var rankHistogram = [0,0,0,0,0,0,0,0,0,0,0,0,0,0]; // For the sake of simplicity, the initial slot won't be used
        var suitHistogram = [0,0,0,0];
        var isStraight = true;
        var previousCard = null;
        var highValues;
        var type;

        for (i in cards) {
            var c = cards[i];
            rankHistogram[getRank(c) - 1]++;
            suitHistogram[c.suit]++;
            if (isStraight) {
                if (previousCard == null || getRank(previousCard) + 1 == getRank(c) ||
                        getRank(previousCard) == 5 && getRank(c) == HIGHEST_VALUE) // A2345 Straight
                    previousCard = c;
                else
                    isStraight = false;
            }
        }

        var originalRankHistogram = rankHistogram;
        rankHistogram.sort();
        rankHistogram.reverse();
        suitHistogram.sort();
        suitHistogram.reverse();
 
        // Printing out what kind of hand it is
        highValues = [-1,-1,-1,-1,-1];
        if (isStraight) {
            if (suitHistogram[0] == 5)
                type = Type.STRAIGHT_FLUSH;
            else
                type = Type.STRAIGHT;
            highValues[0] = getRank(cards[cards.length-1]);
        } else if (suitHistogram[0] == 5) {
            type = Type.FLUSH;
            highValues[0] = getRank(cards[cards.length-1]);
        } else if (rankHistogram[0] == 4) {
            type = Type.FOUR_OF_A_KIND;
            for (i in originalRankHistogram) {
                if (originalRankHistogram[i] == 4)
                    highValues[0] = i;
                else if (originalRankHistogram[i] == 1)
                    highValues[1] = i;
            }
        } else if (rankHistogram[0] == 3 && rankHistogram[1] == 2) {
            type = Type.FULL_HOUSE;
            for (i in originalRankHistogram) {
                if (originalRankHistogram[i] == 3)
                    highValues[0] = i;
                else if (originalRankHistogram[i] == 2)
                    highValues[1] = i;
            }
        } else if (rankHistogram[0] == 3) {
            type = Type.THREE_OF_A_KIND;
            for (i in originalRankHistogram) {
                if (originalRankHistogram[i] == 3)
                    highValues[0] = i;
                else if (originalRankHistogram[i] == 1) {
                    if (highValues[1] == -1)
                        highValues[1] = i;
                    else if (i > highValues[1]) {
                        highValues[2] = highValues[1];
                        highValues[1] = i;
                    } else
                        highValues[2] = i;
                }
            }
        } else if (rankHistogram[0] == 2 && rankHistogram[1] == 2) {
            type = Type.TWO_PAIR;
            for (i in originalRankHistogram) {
                if (originalRankHistogram[i] == 1)
                    highValues[2] = i;
                else if (originalRankHistogram[i] == 2) {
                    if (highValues[0] == -1)
                        highValues[0] = i;
                    else if (i > highValues[0]) {
                        highValues[1] = highValues[0];
                        highValues[0] = i;
                    } else
                        highValues[1] = i;
                }
            }
        } else if (rankHistogram[0] == 2) {
            type = Type.ONE_PAIR;
            var i = 0;
            var foundPair = false;
            while (i < 5) {
                highValues[(foundPair ? i - 1 : i)] = getRank(cards[cards.length-i-1]);
                if (i > 0 && highValues[i] == highValues[i - 1]) {
                    for (var j = i; j > 0; j--)
                        highValues[j] = highValues[j - 1];
                    highValues[0] = getRank(cards[cards.length-i-1]);
                    foundPair = true;
                }
                i++;
            }
            highValues[highValues.length - 1] = -1;
        } else {
            type = Type.HIGH_CARD;
            for (var i = 0; i < 5; i++)
                highValues[i] = getRank(cards[cards.length-i-1]);
        }
        var res = {type: type, highValues: highValues};
        return res;
    }
}

function BestHand(holeCards, communityCards, owner) {
    var cards = holeCards.concat(communityCards);
    cards.sort(compareCards);
    
    var aux = bestCombination(cards);
    this.type = aux.type;
    this.highValues = aux.highValues;
    this.owner = owner;
}

module.exports = BestHand;
