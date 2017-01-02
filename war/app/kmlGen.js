function generateKML(name, description, subname, subdescription, date, lon, lat, alt) {
	var kmlDoc;
	var kmlString = localStorage.getItem("kmlFile");
	if(!kmlString || kmlString.length == 0) {
		kmlDoc = document.implementation.createDocument(null, "kml");
		generateHeader(kmlDoc, name, subname, description, subdescription);
	} else {
		kmlDoc = new DOMParser().parseFromString(kmlString, "text/xml");
	}
	addDataToTree(kmlDoc, date, lat, lon, alt);
	kmlString = new XMLSerializer().serializeToString(kmlDoc);
	localStorage.setItem("kmlFile", kmlString);
	return kmlString;
}

function addDataToTree(doc, date, lat, lon, alt) {
	if (!doc)
		return;

	// Search for Elements to modify
	var Track = doc.getElementsByTagName("Track")[0];
	if(!Track || Track.length == 0) Track = doc.getElementsByTagName("gx:Track")[0];
	
	// Add data to Track
	var when = doc.createElement("when");
	when.appendChild(doc.createTextNode(date.toISOString()));
	Track.appendChild(when);
	var coord = doc.createElement("gx:coord");
	coord.appendChild(doc.createTextNode(lon+' '+lat+' '+alt));
	Track.appendChild(coord);
}

function generateHeader(doc, name, subname, description, subdescription) {
	if (!doc)
		return;
	
	doc.documentElement.setAttribute("xmlns", "http://www.opengis.net/kml/2.2");
	doc.documentElement.setAttribute("xmlns:gx", "http://www.google.com/kml/ext/2.2");
	
	var Document = doc.createElement("Document");
	doc.documentElement.appendChild(Document);

	var name1 = doc.createElement("name");
	name1.appendChild(doc.createTextNode(name));
	Document.appendChild(name1);

	var description1 = doc.createElement("description");
	description1.appendChild(doc.createTextNode(description));
	Document.appendChild(description1);

	var Style = doc.createElement("Style");
	Style.setAttribute("id", "greenLineYellowPoly");
	var LineStyle = doc.createElement("LineStyle");
	var color = doc.createElement("color");
	color.appendChild(doc.createTextNode("7f00ff00"));
	LineStyle.appendChild(color);
	var width = doc.createElement("width");
	width.appendChild(doc.createTextNode("4"));
	LineStyle.appendChild(width);
	Style.appendChild(LineStyle);
	var PolyStyle = doc.createElement("PolyStyle");
	var color2 = doc.createElement("color");
	color2.appendChild(doc.createTextNode("7f00ffff"));
	PolyStyle.appendChild(color2);
	Style.appendChild(PolyStyle);
	var Icon = doc.createElement("Icon");
	var href = doc.createElement("href");
	href.appendChild(doc.createTextNode("http://maps.google.com/mapfiles/kml/pal2/icon48.png"));
	Icon.appendChild(href);
	Style.appendChild(Icon);
	Document.appendChild(Style);

	var Placemark = doc.createElement("Placemark");
	var name2 = doc.createElement("name");
	name2.appendChild(doc.createTextNode(subname));
	Placemark.appendChild(name2);
	var description2 = doc.createElement("description");
	description2.appendChild(doc.createTextNode(subdescription));
	Placemark.appendChild(description2);
	var styleUrl = doc.createElement("styleUrl");
	styleUrl.appendChild(doc.createTextNode("greenLineYellowPoly"));
	Placemark.appendChild(styleUrl);
	var Track = doc.createElement("gx:Track");
	var extrude = doc.createElement("extrude");
	extrude.appendChild(doc.createTextNode("1"));
	Track.appendChild(extrude);
	var tessellate = doc.createElement("tessellate");
	tessellate.appendChild(doc.createTextNode("1"));
	Track.appendChild(tessellate);
	var altitudeMode = doc.createElement("altitudeMode");
	altitudeMode.appendChild(doc.createTextNode("absolute"));
	Track.appendChild(altitudeMode);
	
	Placemark.appendChild(Track);
	
	Document.appendChild(Placemark);
	
	return new XMLSerializer().serializeToString(doc);
}
