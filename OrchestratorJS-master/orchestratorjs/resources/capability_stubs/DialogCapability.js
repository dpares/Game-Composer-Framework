module.exports = {

	showDialog: function (title, options, validTime ) {
        var methodArguments = ['DialogCapability', 'showDialog', [title, options, validTime]];
        return this.device.invoke(methodArguments);
    },

    getDialogChoice: function () {
        var methodArguments = ['DialogCapability', 'getDialogChoice'];
        return this.device.invoke(methodArguments);
    },
    
};
