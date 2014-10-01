var config = {};

config.gameController = require('./PokerGame/PokerGame.js');
config.initData = {initial_funds : 1000};
config.phases = 1;
config.steps = [1];

module.exports = config;