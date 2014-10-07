module.exports = {

    initGame: function(initData) {
        var methodArguments = ['FrameworkCapability', 'initGame', [initData]];
        return this.device.invoke(methodArguments);
    },
    setStatus: function(status) {
        var methodArguments = ['FrameworkCapability', 'setStatus', [status]];
        return this.device.invoke(methodArguments);
    },
    setPlayerState: function(state) {
        var methodArguments = ['FrameworkCapability', 'setPlayerState', [state]];
        return this.device.invoke(methodArguments);
    },
    getPlayerState: function() {
        var methodArguments = ['FrameworkCapability', 'getPlayerState', []];
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
};