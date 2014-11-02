module.exports = {

    initGame: function(initData) {
        var methodArguments = ['FrameworkCapability', 'initGame', [initData]];
        return this.device.invoke(methodArguments);
    },
    setPlayerState: function(state) {
        var methodArguments = ['FrameworkCapability', 'setPlayerState', [state]];
        return this.device.invoke(methodArguments);
    },
    getPlayerInitialState: function() {
        var methodArguments = ['FrameworkCapability', 'getPlayerInitialState', []];
        return this.device.invoke(methodArguments);
    },
    showCurrentState: function(players,commonData) {
        var methodArguments = ['FrameworkCapability', 'showCurrentState', [players,commonData]];
        return this.device.invoke(methodArguments);
    },
    startStep: function(currentPhase,currentStep) {
        var methodArguments = ['FrameworkCapability', 'startStep', [currentPhase,currentStep]];
        return this.device.invoke(methodArguments);
    },
    getStepResult: function(currentPhase,currentStep) {
        var methodArguments = ['FrameworkCapability', 'getStepResult', [currentPhase,currentStep]];
        return this.device.invoke(methodArguments);
    },
    showResults: function(winners,players,commonData) {
        var methodArguments = ['FrameworkCapability', 'showResults', [winners,players,commonData]];
        return this.device.invoke(methodArguments);
    },
    newRound: function() {
        var methodArguments = ['FrameworkCapability', 'newRound', []];
        return this.device.invoke(methodArguments);
    },
    announceWinner: function(players,winner) {
        var methodArguments = ['FrameworkCapability', 'announceWinner', [players,winner]];
        return this.device.invoke(methodArguments);
    },
    askForRematch: function() {
        var methodArguments = ['FrameworkCapability', 'askForRematch', []];
        return this.device.invoke(methodArguments);
    },
    exitGame: function(reason) {
        var methodArguments = ['FrameworkCapability', 'exitGame', [reason]];
        return this.device.invoke(methodArguments);
    },
};