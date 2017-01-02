var survId;
var lastPosition;
var newPosition;
var errorBool = 0;

function watchLocation() {
    survId = navigator.geolocation.watchPosition(updatePosition, showError, {enableHighAccuracy:true});
}

function unwatchLocation() {
	navigator.geolocation.clearWatch(survId);
	geolocation.innerHTML = "Géolocalisation désactivée !";
}
    
function updatePosition(position) {
	newPosition = position;
}

function showError(error) {
	errorBool = 1;
    switch(error.code) {
        case error.PERMISSION_DENIED:
            geolocation.innerHTML = "Géolocalisation refusée par l'utilisateur, ou SSL (https) non activé...<br><a href=\"https://isaelasbordes.appspot.com/app/\">Cliquez ici</a> pour réessayer"
            break;
        case error.POSITION_UNAVAILABLE:
            geolocation.innerHTML = "En attente d'un fix GPS...<br>Position non disponible"
            break;
        case error.TIMEOUT:
            geolocation.innerHTML = "En attente d'un fix GPS...<br>Position expirée"
            break;
        case error.UNKNOWN_ERROR:
            geolocation.innerHTML = "Une erreur inconnue est survenue...<br>Pas de chance ! :-("
            break;
    }
}