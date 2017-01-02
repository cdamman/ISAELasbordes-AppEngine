// The sendData function is our main function
// from: https://developer.mozilla.org/en-US/docs/Web/Guide/HTML/Forms/Sending_forms_through_JavaScript#Dealing_with_binary_data
function sendData(fileName, kmlString) {
    // At first, if there is a file selected, we have to wait it is read
    // If it is not, we delay the execution of the function
    if(!kmlString || kmlString.length == 0)
		return;

    // To construct our multipart form data request,
    // We need an XMLHttpRequest instance
    var XHR      = new XMLHttpRequest();

    // We need a sperator to define each part of the request
    var boundary = "*****";

    // And we'll store our body request as a string.
    var data     = "";

	// We start a new part in our body's request
	data += "--" + boundary + "\r\n";

	// We said it's form data (it could be something else)
	data += 'Content-Disposition: form-data; '
	// We define the name of the form data
		+ 'name="uploaded_file"; '
	// We provide the real name of the file
		+ 'filename="'     + fileName + '"\r\n';
	// We provide the MIME type of the file
	data += 'Content-Type: application/vnd.google-earth.kml+xml;charset=utf-8\r\n';

	// There is always a blank line between the meta-data and the data
	data += '\r\n';

	// We happen the data to our body's request
	data += kmlString + '\r\n';
	
    // Once we are done, we "close" the body's request
    data += "--" + boundary + "--\r\n";

    // We define what will happen if the data are successfully sent
    XHR.addEventListener('load', function(event) {
		var kmlFile = localStorage.getItem("kmlFile");
		if(!kmlFile || kmlFile.length == 0) {} else {
			var date = new Date();
			sending.innerHTML = "Données envoyées !<br>Date = " + zeroPad(date.getDate(),10)+"/"+zeroPad(date.getMonth()+1,10)+"/"+zeroPad(date.getFullYear(),10)+" "+zeroPad(date.getHours(),10)+":"+zeroPad(date.getMinutes(),10)+":"+zeroPad(date.getSeconds(),10);
		}
    });

    // We define what will happen in case of error
    XHR.addEventListener('error', function(event) {
		sending.innerHTML = "Données non envoyées, une erreur est survenue.<br>Veuillez vérifier votre accès internet...";
    });

    // We setup our request
    XHR.open('POST', '../send');

    // We add the required HTTP header to handle a multipart form data POST request
    XHR.setRequestHeader('Content-Type','multipart/form-data; boundary=' + boundary);
    //XHR.setRequestHeader('Content-Length', data.length);
	//XHR.setRequestHeader("Connection", "Keep-Alive");
	XHR.setRequestHeader("ENCTYPE", "multipart/form-data");
	XHR.setRequestHeader("uploaded_file", fileName); 
	
    // And finally, We send our data.
    XHR.send(data);
}