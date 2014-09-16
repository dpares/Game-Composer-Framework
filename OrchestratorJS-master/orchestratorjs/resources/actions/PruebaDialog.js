
this.body = function (dev, dev2) {
  
    var misc = require('./misc.js');
    
    dev.talkingCapability.say("hola, elige una opción","server","0.8");
    dev.dialogCapability.showDialog("Opcion", ['SI','NO'], 60);
    while( !dev.dialogCapability.getDialogChoice() ) {
      misc.sleep(2);
      console.log("no choice");
    }

    var choice = dev.dialogCapability.getDialogChoice();
    console.log("CHOICE: "+choice);
    dev2.talkingCapability.say("has elegido "+choice,"server","0.8");
};