module.exports = {

	showUrlPhoto: function(photo_url) {
        var methodArguments = ['PlayerCapability', 'showUrlPhoto', [photo_url]];
        return this.device.invoke(methodArguments);
    },
      
};
