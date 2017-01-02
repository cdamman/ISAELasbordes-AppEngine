function startGPSListener() {
	watchLocation();
}

function GPSListener() {
	if(!newPosition) {
		if(errorBool == 0)
			geolocation.innerHTML = "En attente d'un fix GPS...";
	} else {
		lastPosition = newPosition;
		
		var date = new Date(lastPosition.timestamp);
		var altLasbordes = 465;
		geolocation.innerHTML = "Latitude = " + lastPosition.coords.latitude.toFixed(4) +
		"<br>Longitude = " + lastPosition.coords.longitude.toFixed(4) +
		"<br>Date = " + zeroPad(date.getDate(),10)+"/"+zeroPad(date.getMonth()+1,10)+"/"+zeroPad(date.getFullYear(),10)+" "+zeroPad(date.getHours(),10)+":"+zeroPad(date.getMinutes(),10)+":"+zeroPad(date.getSeconds(),10);
		
		var kmlFilename = localStorage.getItem("kmlFilename");
		if(!kmlFilename || kmlFilename.length == 0) {} else {
			var name = localStorage.getItem("kmlTitle");
			var description = localStorage.getItem("kmlDescription");
			generateKML(name, description, name, description, date, lastPosition.coords.longitude, lastPosition.coords.latitude, altLasbordes);
		}
	}
    setTimeout("GPSListener()", 5000);
}

function stopGPSListener() {
	unwatchLocation();
}

function startStopSendKML() {
	var kmlString = localStorage.getItem("kmlFile");
	if(!kmlString || kmlString.length == 0) {
		if(!lastPosition) {
			alert("En attente d'un fix GPS: \nVotre position GPS n'est pas encore connue. Merci de patienter le temps que votre récpeteur obtienne un fix GPS, avant de lancer un enregistrement du vol");
		} else if(!flightName.value || flightName.value.length == 0) {
			alert("Nom du vol vide :\nVeuillez entrer un nom pour ce vol, en incluant votre nom + prénom");
		} else {
			if(flightName.disabled == false) {
				flightName.disabled = true;
				startSendKML();
				buttonStartStopSendKML.innerHTML = "Arrêter l'enregistrement du vol";
			} else {
				stopSendKML();
				buttonStartStopSendKML.innerHTML = "Démarrer l'enregistrement du vol";
				flightName.disabled = false;
			}			
		}
	} else {
		stopSendKML();
		buttonStartStopSendKML.innerHTML = "Démarrer l'enregistrement du vol";
		flightName.disabled = false;
	}
}

function startSendKML() {
	var date = new Date(lastPosition.timestamp);
	localStorage.setItem("kmlTitle", flightName.value);
	localStorage.setItem("kmlDescription", "Vol enregistré le "+zeroPad(date.getDate(),10)+"/"+zeroPad(date.getMonth()+1,10)+"/"+zeroPad(date.getFullYear(),10)+" à "+zeroPad(date.getHours(),10)+":"+zeroPad(date.getMinutes(),10)+":"+zeroPad(date.getSeconds(),10));
	var dateString = zeroPad(date.getFullYear(),10)+""+zeroPad(date.getMonth()+1,10)+""+zeroPad(date.getDate(),10)+"-"+zeroPad(date.getHours(),10)+""+zeroPad(date.getMinutes(),10)+""+zeroPad(date.getSeconds(),10);
	localStorage.setItem("kmlFilename", "flight_"+"0000000000000000"+"_"+dateString+"_");
}

function SendKML() {
	var fileName = localStorage.getItem("kmlFilename");
	var date = new Date();
	var dateString = zeroPad(date.getFullYear(),10)+""+zeroPad(date.getMonth()+1,10)+""+zeroPad(date.getDate(),10)+"-"+zeroPad(date.getHours(),10)+""+zeroPad(date.getMinutes(),10)+""+zeroPad(date.getSeconds(),10);
	fileName = fileName+dateString+"_KML.kml"
	if(!fileName || fileName.length == 0) {} else {
		var kmlFile = localStorage.getItem("kmlFile");
		if(!kmlFile || kmlFile.length == 0) {
			sending.innerHTML = "Non envoyées, en attente de plus de données";
		} else {
			sendData(fileName, kmlFile);
		}
	}
    setTimeout("SendKML()", 10000);
}

function stopSendKML() {
	var fileName = localStorage.getItem("kmlFilename");
	if(!fileName || fileName.length == 0) {} else {
		var kmlFile = localStorage.getItem("kmlFile");
		if(!kmlFile || kmlFile.length == 0) {
			sending.innerHTML = "Non envoyées, en attente de plus de données";
		} else {
			sendData(fileName, kmlFile);
		}
	}
	sending.innerHTML = "En attente d'enregistrement";
	localStorage.setItem("kmlFile", "");
	localStorage.setItem("kmlFilename", "");
	localStorage.setItem("kmlDescription", "");
}