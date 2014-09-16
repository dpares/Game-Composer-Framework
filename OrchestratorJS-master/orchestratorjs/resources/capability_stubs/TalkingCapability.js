module.exports = {

    say: function (line, voice, pitch) {
        var methodArguments = ['TalkingCapability', 'say', [line, voice, pitch]];
        return this.device.invoke(methodArguments);
    },


    shout: function (line, voice, pitch) {
        var methodArguments = ['TalkingCapability', 'shout', [line, voice, pitch]];
        return this.device.invoke(methodArguments);
    },
    
};
