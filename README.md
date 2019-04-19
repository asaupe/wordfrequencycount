# wordfrequencycount - Coding exercise
This is a coding exercise I went through. It's does word frequency counting taking into account stop words and attempts to do some stemming. It's not 100% accurate nor does it make an attempt to be.

## Technology
* Dropwizard - For REST API development (Java)
* React - for building front-end website
* MongoDB Atlas - For storage

## The Approach
### Words counted
Punctuation is scrubbed from the file while processing words, except for apostrophes those are left in and considered letters. Any "words" that do not have any alpha characters are skipped during processing.

### Stop Words
https://www.ranks.nl/stopwords
A file of stop words is loaded into an ArrayList and each word is checked if it is a stop word. The word is then flagged as a stop word but still counted normally.

### Stemming
Stemming is achieved using simple stem rules. The stem rule contains an ending that indicates a possible plural or verb conjunction. A rule can also have letters to add after removing the ending to handle such cases as tries becomes try. The system makes no attempt to determine the correct rule to use but will apply all matching rules. This does leave the application open to the possibility of double counting words but that should be a small edge case.

A stem word is only persisted to MongoDB if it occurs in the document or if it is a stem word of multiple original words in the document.

### File uploading / processing
The user can upload a file for processing. The file is persisted to the web server file system and a document is saved to MongoDB indicating the file is ready for word count processing. The API will send back a success record when the persisting is complete. The separate thread will pick up the file for processing and persisting the word count results to MongoDB.