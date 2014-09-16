// the body
this.body = function (d1, d2) {
    var misc = require('./misc.js');
      
    d1.guessingCapability.startGuessing();
    while(!d1.guessingCapability.getGuess()) {
      misc.sleep(1);
    }
    var initialValue = d1.guessingCapability.getGuess();
  	console.log("JODETE" + initialValue);
  	
  	d2.guessingCapability.startGuessing();
    while( !d2.guessingCapability.getGuess() ) {
      misc.sleep(1);
    }
    var guessedValue = d2.guessingCapability.getGuess();
    	
  	if(initialValue == guessedValue)
    	d2.talkingCapability.say("A WINNER IS YOU","sever","1.0");
    else
      d2.talkingCapability.say("YOU DEFEATED","david","1.0");
    
};