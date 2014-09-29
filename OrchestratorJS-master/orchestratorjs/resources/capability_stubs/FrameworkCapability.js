module.exports = {

    initGame: function() {
        var methodArguments = ['FrameworkCapability', 'initGame', []];
        return this.device.invoke(methodArguments);
    },
};