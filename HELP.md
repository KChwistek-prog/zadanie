To use application fill user.authenticationToken= in application.properties with authentication token from github. 
It's recommended but not necessary:
Authenticated users have 2500 requests from API.
Unauthenticated only 60 per hour. 

Controller uses pathVariable so type repo owner in link eg. 
http://localhost:8080/getList/KChwistek-prog