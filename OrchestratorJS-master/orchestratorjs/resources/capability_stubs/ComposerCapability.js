module.exports = {

    initGame: function(initData) {
        var methodArguments = ['ComposerCapability', 'initGame', [initData]];
        return this.device.invoke(methodArguments);
    },
    setPlayerState: function(state) {
        var methodArguments = ['ComposerCapability', 'setPlayerState', [state]];
        return this.device.invoke(methodArguments);
    },
    getPlayerInitialState: function() {
        var methodArguments = ['ComposerCapability', 'getPlayerInitialState', []];
        return this.device.invoke(methodArguments);
    },
    showCurrentState: function(players,commonData) {
        var methodArguments = ['ComposerCapability', 'showCurrentState', [players,commonData]];
        return this.device.invoke(methodArguments);
    },
    startStep: function(currentPhase,currentStep) {
        var methodArguments = ['ComposerCapability', 'startStep', [currentPhase,currentStep]];
        return this.device.invoke(methodArguments);
    },
    getStepResult: function(currentPhase,currentStep) {
        var methodArguments = ['ComposerCapability', 'getStepResult', [currentPhase,currentStep]];
        return this.device.invoke(methodArguments);
    },
    showResults: function(winners,players,commonData) {
        var methodArguments = ['ComposerCapability', 'showResults', [winners,players,commonData]];
        return this.device.invoke(methodArguments);
    },
    newRound: function() {
        var methodArguments = ['ComposerCapability', 'newRound', []];
        return this.device.invoke(methodArguments);
    },
    announceWinner: function(players,winner) {
        var methodArguments = ['ComposerCapability', 'announceWinner', [players,winner]];
        return this.device.invoke(methodArguments);
    },
    askForRematch: function() {
        var methodArguments = ['ComposerCapability', 'askForRematch', []];
        return this.device.invoke(methodArguments);
    },
    exitGame: function(reason) {
        var methodArguments = ['ComposerCapability', 'exitGame', [reason]];
        return this.device.invoke(methodArguments);
    },
};