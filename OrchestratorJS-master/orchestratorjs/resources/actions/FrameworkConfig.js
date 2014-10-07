var config = {};

config.gameController = require('./PokerGame/PokerGame.js');
config.initData = {initial_funds : 1000};
config.phases = 6;
config.steps = [1,1,1,1,1,1];

module.exports = config;