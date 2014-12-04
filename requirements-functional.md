# Dictionary Application - Functional Requirements

1. User selects Show All Words
	* All Words and first n characters of Definitions are displayed in a table

2. User selects Word from Show All Words table
	* Word and Definition are loaded in appropriate UI areas

3. User enters in text for word and presses Search
	* If possible local matches exist, load Words in appropriate table
		* User selects existing option
			* Load Word and Definition in appropriate UI areas
		* User selects Create New Word button
			* Load Word in appropriate UI area
			* Set focus on Definition textbox
        * If exact word exists in local dictionary, 
            * Load Word and Definition in appropriate UI areas
		* If Word does not exist in local dictionary,
            * If internet connection exists, 
            	* Execute definition query in dictionary service
                    * If valid response occurs
                        * Display each definition to user in appropriate UI element
                		* User selects definition to save
                		* Definition is saved to local environment
                		* Load word and definition in appropriate UI areas
                    * If no valid response occurs, 
                		* Load word in appropriate UI areas
                		* Set focus on definition textbox
	        * If no internet connection exists,
                * Load word in appropriate UI area
                * Set focus on definition textbox

4. User modifies definition text and selects Save button
    * Message is displayed whether save execution failed or succeeded

5. User deletes word
    * Message is displayed whether delete operation failed or succeeded


