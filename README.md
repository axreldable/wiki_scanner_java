# wiki_scanner_java
Scans wikipedia and create directories which contains files with articles.

Catalogs tree on Wiki has next structure:
![alt text](https://github.com/axreldable/wiki_scanner_java/blob/master/wiki_structure.jpeg)

The scanner converts this structure into directories with files.

Files and directories has special names.

File name example: 07_020_001_008_011.txt
- 07 - category code
- 020 - zero level subcategory code
- 001 - first level subcategory code
- 008 - second level subcategory code
- 011 - category number

Directory name example: 07_020_001_008_Tenis.

Numbers has the same meaning and directory name ends with subcategory name.
The article 07_020_001_008_011.txt belongs to the directory 07_020_001_008_Tenis and has 11 number.

The scanner also makes report in csv format:
- csv format table here

In app configuration file you can change next parameters:
- Wiki's started categories
- amount of threads for crawling in each category
- path to store crawling results
- result csv file name

The scanner polls Wiki using wiki-api: https://ru.wikipedia.org/w/api.php.

What I used in this programm:
- http requests for wiki polling
- json parsing for parsing Wiki response
- java.util.concurrent for parallelizing app work


