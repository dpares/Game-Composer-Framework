module.exports = {

    initGame: function(initData) {
        var methodArguments = ['FrameworkCapability', 'initGame', [initData]];
        return this.device.invoke(methodArguments);
    },
    setStatus: function(state) {
        var methodArguments = ['FrameworkCapability', 'setStatus', [state]];
        return this.device.invoke(methodArguments);
    },
    showCurrentState: function(players,commonData) {
        var methodArguments = ['FrameworkCapability', 'showCurrentState', [players,commonData]];
        return this.device.invoke(methodArguments);
    },
    playStep: function(currentPhase,currentStep) {
        var methodArguments = ['FrameworkCapability', 'playStep', [currentPhase,currentStep]];
        return this.device.invoke(methodArguments);
    },
};