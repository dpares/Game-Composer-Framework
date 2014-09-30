module.exports = {

    initGame: function(initData) {
        var methodArguments = ['FrameworkCapability', 'initGame', [initData]];
        return this.device.invoke(methodArguments);
    },
    setStatus: function(state) {
        var methodArguments = ['FrameworkCapability', 'setStatus', [state]];
        return this.device.invoke(methodArguments);
    },
};