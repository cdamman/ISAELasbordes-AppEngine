var httpRequest = null;
var data = new Array(6);
var lonLatAlt;
var hdg;
var name;
var recordNode;
var lastWhen = 0;

function loadLastPosition() {
	if(!httpRequest) {
		httpRequest = CreateHTTPRequestObject();   // defined in ajax.js
	}
	if(httpRequest) {          
		// The requested file must be in the same domain that the page is served from.
		httpRequest.open("GET", URL, true);    // async
		httpRequest.onreadystatechange = OnStateChange;
		httpRequest.send(null);
	}
}

function OnStateChange() {
	if (httpRequest.readyState == 0 || httpRequest.readyState == 4) {
		if (IsRequestSuccessful(httpRequest)) { // defined in ajax.js
			modifyMarker();
		}
	}
}

function modifyMarker() {
	var xmlDoc = ParseHTTPResponse(httpRequest);   // defined in ajax.js
	if (!xmlDoc)
		return;

	name = xmlDoc.getElementsByTagName("name")[0].textContent;
	var itemTags = xmlDoc.getElementsByTagName("Track")[0];
	if(!itemTags || itemTags.length == 0) itemTags = xmlDoc.getElementsByTagName("gx:Track")[0];

	// when
	recordNode = itemTags.getElementsByTagName("when");
	data[0] = recordNode[recordNode.length-1].textContent;
	
	// coord
	recordNode = itemTags.getElementsByTagName("coord");
	if(!recordNode || recordNode.length == 0) recordNode = itemTags.getElementsByTagName("gx:coord");
	data[1] = recordNode[recordNode.length-1].textContent;
	 
	// values
	recordNode = itemTags.getElementsByTagName("value");
	if(!recordNode || recordNode.length == 0) recordNode = itemTags.getElementsByTagName("gx:value");
	if(!recordNode || recordNode.length == 0)  {
		var dateToString = new Date(Date.parse(data[0]));
		data[2] = zeroPad(dateToString.getDate(),10)+"/"+zeroPad(dateToString.getMonth()+1,10)+"/"+zeroPad(dateToString.getFullYear(),10)+" "+zeroPad(dateToString.getHours(),10)+":"+zeroPad(dateToString.getMinutes(),10)+":"+zeroPad(dateToString.getSeconds(),10);
		for(j = 1; j < 4; j++) {
			data[2+j] = "non disponible";
		}
	} else {
		for(j = 0; j < 4; j++) {
			data[2+j] = recordNode[(j+1)*recordNode.length/4-1].textContent;
		}
	}
	
	lonLatAlt = data[1].split(" ");
	if(data[5] == "non disponible") {
		hdg = "0.0";
	} else {
		hdg = data[5].substring(0, data[5].length-1).replace(',','.');
	}
	
	var thisWhen = Date.parse(data[0]);
	if(thisWhen - lastWhen >= 0) { // update only if is not a time machine
		lastWhen = thisWhen;
		
		kmlLayer.setUrl(URL);
		kmlLayer.setMap(map);
		
		document.getElementById("tme").innerHTML = data[2];
		document.getElementById("alt").innerHTML = data[3];
		document.getElementById("spd").innerHTML = data[4];
		document.getElementById("hdg").innerHTML = data[5];
		document.getElementById("name").innerHTML = name;
		document.title = name;
		
		planeMarker.setPosition({lat: Number(lonLatAlt[1]), lng: Number(lonLatAlt[0])});
		planeMarker.setIcon(new google.maps.MarkerImage(RotateIcon.makeIcon(plane).setRotation({deg: Number(hdg)}).getUrl(), new google.maps.Size(24,24), new google.maps.Point(0,0), new google.maps.Point(12,12)));
		planeMarker.setMap(map);
	}
}

function zeroPad(nr, base) {
	var  len = (String(base).length - String(nr).length)+1;
	return (len > 0) ? new Array(len).join('0')+nr : nr;
}