module.exports = {

    startGuessing: function() {
        var methodArguments = ['GuessingCapability', 'startGuessing', []];
        return this.device.invoke(methodArguments);
    },
    getGuess: function() {
        var methodArguments = ['GuessingCapability', 'getGuess', []];
        return this.device.invoke(methodArguments);
    },
    showResults: function(winner) {
        var methodArguments = ['GuessingCapability', 'showResults', [winner]];
        return this.device.invoke(methodArguments);
    },
};