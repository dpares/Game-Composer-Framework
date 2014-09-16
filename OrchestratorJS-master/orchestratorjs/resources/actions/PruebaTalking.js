var pubsub      = require( '../tools.js' ).pubsub();

module.exports = {

  // the body
  body: function ( dev) {
    
    dev.talkingCapability.say("jiasjiasjias", "server", "1.0");
    pubsub.emit('arrancarPlayer');
  }

};