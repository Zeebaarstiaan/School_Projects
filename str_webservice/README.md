STR6 Webservice demo

Eigen RESTful webservice
De url voor de webservice:  http://str6-webservice.herokuapp.com/
De root URI is leeg en linkt door naar de documentatie of de html-views.

Algemene informatie
De daadwerkelijke webservice begint bij de resource “/movies”
	http://str6-webservice.herokuapp.com/movies

Alle algemene resources kunnen worden weergegeven via json of xml door er simpelweg “.json” of “.xml” achter te plaatsten. Alleen .xml en .json worden ondersteund, alle andere extensies verwijzen door daar de html representatie.
 	Zoals: http://str6-webservice.herokuapp.com/movies.json
Ondersteunde resources:
-	/movies
-	/movies/{id}
-	/movies/{id}/reviews
-	/movies/{id}/reviews/{id}	

Foutafhandeling gaat via de HTTP statuscodes, feitelijk worden alleen 404 gebruikt (indien de pagina niet gevonden kan worden) of 405 als de method niet allowed is. 500 errors zijn als het goed is allemaal weggewerkt ;) 
Get Methods
•	/movies
	Haal een lijst met alle toegevoegde films op.

•	/movies/{id}
	Haal detailinformatie over de geselecteerde film op.

•	/movies/{id}/edit
	Haal een formulier weer om een film aan te passen op.

•	/movies/{id}/delete
	Haal waarschuwing voor het verwijderen van een film op.

•	/movies/{id}/reviews
	Haal een lijst met alle reviews die bij een bepaalde film horen op.

•	/movies/{id_user}/reviews/{id_review}
	Haal detailinformatie over de selecteerde review op.

•	/movies/{id_user}/revies/{id_review}/edit
	Haal een formulier weer om een review aan te passen op.

•	/movies/{id_user}/revies/{id_review}/edit
	Haal een waarschuwing voor het verwijderen van een review op.


Post Methods
•	/movies
	Maak een nieuwe film aan.

•	/movies/{id}/reviews
	Maak een nieuwe review aan

 
PUT Methods
•	/movies/{id}
	Bewerk de geselecteerde film

•	/movies/{id_user}/reviews/{id_review}
	Bewerk de geselecteerde review

Delete Methods
•	/movies/{id}
	verwijder de geselecteerde film

•	/movies/{id_user}/reviews/{id_review}
	verwijder de geselecteerde review

Queries
•	/movies&{query}
	Zoekt films op titels die de waarde {query} bevatten

•	/movies/{id}/reviews&{query}
	Zoekt de reviews op basis van rating, {query} kan “good” of “bad” zijn.

HTTP Error codes
•	200 indien het goed gaat
•	404 indien een pagina niet gevonden kan worden
•	405 indien een methode niet gebruikt mag worden
•	500 indien er iets behoorlijk mis gaat
