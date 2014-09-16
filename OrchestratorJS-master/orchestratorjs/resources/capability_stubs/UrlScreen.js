module.exports = {

    showUrl: function(url_to_show) {
        var methodArguments = ['UrlScreen', 'showUrl', [url_to_show]];
        return this.device.invoke(methodArguments);
    },
};