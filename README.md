<img alt="Kajoo Logo" src="http://kajoo.com.br/imagens/facebook.png"> 
This project was created to demonstrate one of the ways to connect your Android app to a Facebook account in order to display all friends in a list sorted alphabetically including their picture.

######FACEBOOK API LIMITATION
Before proceeding it is very important to mention that any application created after _April_ _30th_ _2014_ uses version _2.0_ of the API, which means that full friends_list download is no longer supported. The entire discussion around this can be found [here.](https://developers.facebook.com/bugs/1502515636638396)

To bypass this limitation and see the application running, you need to add yourself as a tester for this application.
* Facebook APP_ID: 499735953492138

######KNOWLEDGE BASE
Here is a list of specific knowledge base demonstrated in this repository.
- ListView and ListAdapter concepts
- Lazy load of images from web
- Lazy load of list items (fake in RAM since FACEBOOK API no longer supports partial list download)
- Handling fragments and activity lifecycles
- Sorting objects from custom classes alphabetically based on specific attributes
- Async operations performed in file system and Facebook servers.
- Theme customization and bitmap transformation
- Views and layout manipulation
- Regular strings and JSON format parsing
- General Android SDK

######TIME FOR IMPLEMENTATION
This project was executed in TWO DAYS.

Most of the time was spent trying to make the download of friends_list from Facebook until I realized the API was no longer supported and FACEBOOK documentation was not updated. Since time was limited, I wasn't able to handle user list in an appropriate SQL database and leave one service responsible to maintain it updated. Instead, the list is downloaded when user log into Facebook and will only refresh if the user logout or in case the application is closed (not only the activity).
I think the code documentation is also poor and for sure I would do several things differently after playing a little bit more with Facebook APIs, but this was a good "first shot" based on time restrictions.
