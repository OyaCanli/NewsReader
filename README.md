# NewsReader
Udacity Android Basics Nanodegree Project 7 News App: 

Here is a video that demonstrates the app in action: https://www.youtube.com/watch?v=e50ZY0AXcxQ

###### The app contains:
- An animated splash screen. Fetching from internet begins at this activity.
- MainActivity with a ViewPager where each tab shows the articles of a section. Articles are shown with a recyclerview
- Each list item has a button for sharing article on social media, and another for adding it to bookmarks. When you click on the rest of the list item it opens DetailsActivity
- DetailsActivity where user can read the article within the app
- SettingsActivity where user can chose which sections to display in the MainActivity, in which order the sections should be displayed, how many articles per page should be shown and sort order. User can also choose whether to have regular notifications and regular backups for offline reading.
- A search wigdet in the toolbar which shows the results in another activity.
- A bookmarks activity where bookmarked articles are shown. Bookmarked articles can be deleted by swiping left or right. A snackbar makes it possible to undo delete.
###### Behind the scenes:
- Fetched articles are cached in the database. These are replaced at each synchronization. 
- Bookmarks are saved permanently, unless user deletes them by swiping. A snackbar makes it possible to undo delete.
- Database operations are done by the intermediance of a content provider. ListFragments in the main screen and bookmarks activity use cursorloaders for loading data from the content provider
- Each half-an-hour it looks whether there is a new article in the sections user is interested. If there is, it shows it in a notification. When clicked on the notification, app launches from detailsActivity and shows the article. If user clicks up button, MainActivity is launched. User can disable notifications from the settings.
- News are backed at each launch. But they are also refreshed regularly in the background according to the choices of user

![screenshot_2018-05-14-00-27-02](https://user-images.githubusercontent.com/33556367/39972367-00ae6240-570e-11e8-80d1-b2ea09d47802.png)

![screenshot_2018-05-11-08-02-33](https://user-images.githubusercontent.com/33556367/39972371-123ab8d8-570e-11e8-8a61-362e219ed28c.png)

![screenshot_2018-05-14-00-27-23](https://user-images.githubusercontent.com/33556367/39972373-178e0952-570e-11e8-8e4e-072ae7499546.png)

#### UPDATE:
I hided my api key from the repository, but I put the test key inside the repo. So, normally you can make this work with the test key. Though sometimes test key doesn't work. In that case you can replace the test key in the gradle.properties with your own key, or if you already have your key in your local gradle.properties, just make sure they have the same variable name and it will work with your key. 
