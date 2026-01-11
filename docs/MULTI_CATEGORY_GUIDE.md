# How to Scrape Multiple Categories

You can scrape any Cdiscount category by passing its URL as an argument to the `run.bat` script.

## Method 1: Using the Command Line (Terminal)
Open a terminal in the `scrap_no_maven` folder and run:
```bash
run.bat "https://www.cdiscount.com/your-category-url-here"
```

You can even pass **multiple** URLs at once, separated by a space:
```bash
run.bat "URL1" "URL2" "URL3"
```

## Method 2: Editing the Code (Permanent)
If you want to change the "default" categories, open `src/com/example/scrapper/Main.java` and modify the list in the `main` method (lines 10-17). Remember to run `compile.bat` after making any code changes.

> [!TIP]
> Make sure to put the URLs in double quotes `""` to avoid issues with special characters in the links.
