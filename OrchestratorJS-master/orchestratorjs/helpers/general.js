ROOT = process.cwd();
var logger = require( ROOT + '/logs/log' );

var crypto = require( 'crypto' );
var uuid = require('node-uuid');

var fs = require( 'fs' );

this.log = function( m ) {
  console.log(m);
  //logger.info( m );
}

// very clos, in the same space, in vicinity
this.ssidToM = function( ssid ) {
  if( typeof( ssid ) == 'string' )
    ssid = parseInt( ssid );

  if( ssid > -70 )
    return 0.1;
  else if( ssid > -89 )
    return 1.0;
  else
    return 10;
};


function printHost() {
  var os = require( 'os' );
  var ifaces = os.networkInterfaces();
  for ( var dev in ifaces ) {
    var alias = 0;
    ifaces[ dev ].forEach( function( details ) {
      if ( details.family == 'IPv4' ) {
        if ( dev.slice( 0, 2 ) == 'en' ) {
          this.log( 'HOSTNAME: ' + details.address );
          this.log( 'PORT:     ' + config.server.port );
        }
        ++alias;
      }
    } );
  }
}

this.getUUID = function() {
  return uuid.v4();
}

this.md5 = function( s ) {
  return crypto.createHash('md5').update( s ).digest('hex');
}


this.getUniqueId = function() {
  return 'id' + ( new Date() ).getTime();
}



this.hexColor = function( str ) {
  function intToARGB( i ) {
    return ( ( i >> 24 ) & 0xFF ).toString( 16 ) +
      ( ( i >> 16 ) & 0xFF ).toString( 16 ) +
      ( ( i >> 8 ) & 0xFF ).toString( 16 ) +
      ( i & 0xFF ).toString( 16 );
  }

  var hash = 0;
  for ( var i = 0; i < str.length; i++ ) {
    hash = str.charCodeAt( i ) + ( ( hash << 5 ) - hash );
  }
  var r = intToARGB( hash ).slice( 2 );

  while ( r.length < 6 )
    r = r + '0';
  return r;
}


this.hashCode = function( str ) {
  function intToARGB( i ) {
    return ( ( i >> 24 ) & 0xFF ).toString( 16 ) +
      ( ( i >> 16 ) & 0xFF ).toString( 16 ) +
      ( ( i >> 8 ) & 0xFF ).toString( 16 ) +
      ( i & 0xFF ).toString( 16 );
  }

  var hash = 0;
  for ( var i = 0; i < str.length; i++ ) {
    hash = str.charCodeAt( i ) + ( ( hash << 5 ) - hash );
  }
  return intToARGB( hash ).slice( 2 );
}


this.deleteFile = function( filepath ) {
  fs.exists( filepath, function( exists ) {
    if ( exists ) {
      fs.unlinkSync( filepath );
    }
  } );
};

this.deleteFolderRecursive = function( path ) {
  var files = [];
  if ( fs.existsSync( path ) ) {
    files = fs.readdirSync( path );
    files.forEach( function( file, index ) {
      var curPath = path + "/" + file;
      if ( fs.statSync( curPath ).isDirectory() ) { // recurse
        deleteFolderRecursive( curPath );
      } else { // delete file
        fs.unlinkSync( curPath );
      }
    } );
    fs.rmdirSync( path );
  }
};


this.saveFileNoRequire = function( filepath, data, callback, callbackParam ) {
  fs.writeFile( filepath, data, function( err ) {
    if ( err ) {
      console.log( err );
    } else {
      console.log( "The file was saved!" );
    }

    // callback was given
    if ( callback && callbackParam ) {
      callback( callbackParam );
    } else if ( callback ) {
      callback( callbackParam );
    }
  } );
}


//this.saveFile = function(filepath, data) {
this.saveFile = function( filepath, data, callback, callbackParam ) {
  fs.writeFile( filepath, data, function( err ) {
    if ( err ) {
      console.log( err );
    } else {
      console.log( "The file was saved!" );
    }

    if ( require.cache[ require.resolve( filepath ) ] ) {
      delete require.cache[ require.resolve( filepath ) ];
    }
    require( filepath );

    // callback was given
    if ( callback && callbackParam ) {
      callback( callbackParam );
    } else if ( callback ) {
      callback( callbackParam );
    }
  } );
}


this.parseArgsFromString = function( str ) {
  var args = /\(([^)]+)/.exec( str );

  if ( args && args.length > 0 && args[ 1 ] ) {
    args = args[ 1 ].split( /\s*,\s*/ );
  } else {
    args = [];
  }
  return args;
};


this.reRequire = function( fullPath ) {
  if ( require.cache[ require.resolve( fullPath ) ] ) {
    delete require.cache[ require.resolve( fullPath ) ];
  }
  return require( fullPath );
};
