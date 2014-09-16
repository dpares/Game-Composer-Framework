
 
this.getParticipants = function () {
    return [d1, d2];
};


// the body
this.body = function (d1,d2) {
    d1.talkingCapability.say("hi","david","0.7");
    d2.talkingCapability.say("hellou","server","0.3");
};


