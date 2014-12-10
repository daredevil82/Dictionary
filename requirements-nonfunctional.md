# Dictionary Application - NonFunctional Requirements

1. MVC Application with Service and Utils layers
2. Data source is either a file or database.  Repository includes a database branch using SQLite, but update and delete functionality is broken.  Inserts and reads work as expected.
3. If dictData does not exist, it will be created in the same directory/folder as the executable jar file.
4. If a word does not have an exact match in the local data source, a query is made to an online definition service and the results are loaded in the Word Definition textarea.  Functionality is implemented in utils.HttpUtils
5. All Word objects have created and updated timestamps using Joda Time library
6. All read operations after application initialization are done via a HashMap data structure.  All create, update and delete operations are done in the HashMap prior to writing to file
7. Each time a create, update or delete operation is executed, it results in the rewrite of the entire data file, rather than having to parse the file text and update individual lines.
8. Service and DAO layers are instanciated via interface implementations.  Controller and HttpUtils are concrete implementations with no specific inheritance

