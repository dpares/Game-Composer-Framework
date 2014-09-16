module.exports = {

    initGame: function() {
        var methodArguments = ['Apocalymbics', 'initGame', []];
        return this.device.invoke(methodArguments);
    },
    launchApplication: function(playerNumber) {
        var methodArguments = ['Apocalymbics', 'launchApplication', [playerNumber]];
        return this.device.invoke(methodArguments);
    },
    makeAdminPlayer: function() {
        var methodArguments = ['Apocalymbics', 'makeAdminPlayer', []];
        return this.device.invoke(methodArguments);
    },
    makeNormalPlayer: function() {
        var methodArguments = ['Apocalymbics', 'makeNormalPlayer', []];
        return this.device.invoke(methodArguments);
    },
    getCountrySelection: function() {
        var methodArguments = ['Apocalymbics', 'getCountrySelection', []];
        return this.device.invoke(methodArguments);
    },
    updatePlayerInfo: function(countrySelections,playerNumbers) {
        var methodArguments = ['Apocalymbics', 'updatePlayerInfo', [countrySelections,playerNumbers]];
        return this.device.invoke(methodArguments);
    },
    moveToGameSelectionScreen: function() {
        var methodArguments = ['Apocalymbics', 'moveToGameSelectionScreen', []];
        return this.device.invoke(methodArguments);
    },
    getGameSelectionFromAdmin: function() {
        var methodArguments = ['Apocalymbics', 'getGameSelectionFromAdmin', []];
        return this.device.invoke(methodArguments);
    },
    updateGameSelection: function(selectedGame) {
        var methodArguments = ['Apocalymbics', 'updateGameSelection', [selectedGame]];
        return this.device.invoke(methodArguments);
    },
    moveToGameplayScreen: function() {
        var methodArguments = ['Apocalymbics', 'moveToGameplayScreen', []];
        return this.device.invoke(methodArguments);
    },
    moveToWinnerScreen: function() {
        var methodArguments = ['Apocalymbics', 'moveToWinnerScreen', []];
        return this.device.invoke(methodArguments);
    },
    getPlayerPlacement: function() {
        var methodArguments = ['Apocalymbics', 'getPlayerPlacement', []];
        return this.device.invoke(methodArguments);
    },
    moveToPlayAgainScreen: function() {
        var methodArguments = ['Apocalymbics', 'moveToPlayAgainScreen', []];
        return this.device.invoke(methodArguments);
    },
    hasDecided: function() {
        var methodArguments = ['Apocalymbics', 'hasDecided', []];
        return this.device.invoke(methodArguments);
    },
    wantsToContinue: function() {
        var methodArguments = ['Apocalymbics', 'wantsToContinue', []];
        return this.device.invoke(methodArguments);
    },
    moveToMainMenuScreen: function() {
        var methodArguments = ['Apocalymbics', 'moveToMainMenuScreen', []];
        return this.device.invoke(methodArguments);
    },
    startRound: function(roundNumber) {
        var methodArguments = ['Apocalymbics', 'startRound', [roundNumber]];
        return this.device.invoke(methodArguments);
    },
    updateGameplayTurn: function(playerNumber) {
        var methodArguments = ['Apocalymbics', 'updateGameplayTurn', [playerNumber]];
        return this.device.invoke(methodArguments);
    },
    getPlayerThrowAngle: function() {
        var methodArguments = ['Apocalymbics', 'getPlayerThrowAngle', []];
        return this.device.invoke(methodArguments);
    },
    showPlayerThrow: function(playerNumber,throwAngle) {
        var methodArguments = ['Apocalymbics', 'showPlayerThrow', [playerNumber,throwAngle]];
        return this.device.invoke(methodArguments);
    },
    isReady: function() {
        var methodArguments = ['Apocalymbics', 'isReady', []];
        return this.device.invoke(methodArguments);
    },
};