# Cordova-Aviary-Plugin

Last edited May 29, 2013

## About

Hardly any of this code is mine.  I've merely pieced all these scraps together.  I hope I haven't violated any Licenses.  Please read the Cordova and Aviary licenses before using this code.

## License

- Aviary - http://www.aviary.com/legal/terms
- Cordova - http://www.apache.org/licenses/LICENSE-2.0

## Android Prerequisites

- Read and fully understand this: http://www.aviary.com/android/documentation
- Read and fully understand this: http://cordova.apache.org/docs/en/edge/guide_plugin-development_index.md.html#Plugin%20Development%20Guide

## Android Instructions

- Now, using the cordova android project, follow the instructions on the aviary site to add the aviary SDK to the project
- Next, take the supplied Aviary.java class and add to your cordova project as a new plugin.
- Make sure you've followed the instructions on the plugin development guide

## iOS Instructions
- Coming soon

## App Instructions

- Once you have the Plugin installed and compiled. You need to call it from JavaScript
- Add the aviary.js to your project and include it.
- follow the index.html in the examples folder to see how you can call and show the aviary plugin

## Sample Code

    // Called when a photo is successfully retrieved from camera
    //
    function onPhotoURISuccess(imageURI) {

	window.plugins.aviary.show(imageURI, {
		success: function(result){
			alert('Aviary Success');
			console.log('Your new File:' + result.src);
		},
		error: function(message){
			alert('Aviary Fail');
			alert(message);
		}
	});
    }

## Future

Send over any pull requests for adding more options to the show method.  You could pass over any params that you want to make aviary configurable based on it's API.

Also, if anyone wants to help supply a 'plugin.xml' please do so.