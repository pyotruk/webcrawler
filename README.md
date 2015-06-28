## Web Crawler
@author <a href="mailto:pyotruk@gmail.com">pyotruk</a>

#### Build
``gradle build fatJar``

#### Run 
``java -jar build/libs/webcrawler-all-1.0.jar startURL depth [poolSize=10]`` <br>
Example: ``java -jar build/libs/webcrawler-all-1.0.jar http://ya.ru/ 3 100`` <br>

#### TODOs
1. Add parent_id column to Page for hierarchy building.
2. Check global uniqueness of URL before JPA-transaction.
3. Kill URLs that is not global unique before they generated children.
