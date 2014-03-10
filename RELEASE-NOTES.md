# Datavyu 1.1 Release Notes
## 03/10/2014

### Additions
* Created more prompts to save to prevent accidental data loss.
* Minimizing the spreadsheet window will minimize all Datavyu windows.

### Changes
* The names of some software features were changed to adhere to a more intuitive naming scheme:
* "Vocab Editor" --> "Code Editor
* "Observation" --> "Cell"
* "Argument" --> "Code" 
* "Temporal Ordering" --> "Temporal Alignment"
* "Variable" --> "Column
* "Data Viewer" --> "Media Player" 
* "Data Viewer Controller" --> "Controller" 
* Quicktime 7.7.5 no longer includes java libraries when you install the program but you can very simply custom  include them in your download. Please follow the steps provided below. Those who update their quicktime version to this newest version and those who download the newest version and any new ones after it will have to custom download the program. See our:ref:`installation < >` page to guide you. 
* The :ref: 'Controller < >' has a new format that is no standard between PC and Mac keyboards.
* Users are now restricted to Matrix-style columns. Text and nominal columns can no longer be created but pre-existing instances from saved files are still supported. Since it is now the only type of column, matrices are now referred to simply as columns.
* Restrictions on column names have been enforced to prevent problems in the scripting interface. Column names must begin with a letter. Only letters, numbers and underscores are allowed in column names. Previously created columns that do not adhere to these restrictions will continue to work, but new column names (or name changes) will be restricted. We have provided a script in favorites folder that will turn periods into underscores in existing columns to aid the transition. 
* Datavyu is no longer uses Mongo DB. Eliminating this outside dependency means:
* Datavyu is less memory intensive.
* The Datavyu application is smaller.
* Startup and file loading are faster.
* Errors when closing Datavyu on Windows machines have been fixed.

### Bug Fixes
* Fixed issues causing cells to overlap when temporal alignment is turned on.
* When running scripts, more informative status messages are printed to the console window.
* You no longer have to scroll when you code a long spreadsheet.
* Column location in the spreadsheet is saved.
* Hide/Show columns list is saved. 

### Known Issues
* The arrow keys do not always allow you to move from code to code in the code editor and spreadsheet but the tab and shit tab keys have the same function. 
* Undo history is not always accurate.
* You can click, hold down, and drag a code name within a cell but it does not influence the code itself and it does not save. Please save your file and reopen it. 
* Occasionally, running a script will produce no output when in fact, the script did work.
* Saving a file rarely does not make the asterisk at the top of the file go away.
* VLC support still unreliable.




# Datavyu 1.04rev3 Release Notes
## 12/11/2013

### Additions
* Added file association support in Mac and Windows. Double-clicking on a .opf file will now open the file in Datavyu.

### Changes
* "Variables" have been renamed "columns" and "matrix arguments" have been renamed "codes" throughout the user interface.
* All API functions have been renamed to use the words "column" and "code", however, API functions using the old naming scheme will continue to work so that scripts do not need to be revised.
* Scripts now allow the user to overwrite an existing column. Previously, trying to set a column back to the spreadsheet caused an error if the columns shared the same name.

### Bug Fixes
* Fixed an issue with Datavyu failing to close completely (preventing the user from being able to reopen the program).
* Corrected a case where setting the offset of a cell changed the offset of the previous cell in a column
* Fixed the create\_mutually\_exclusive script to correct an error where the final cell was not written to the spreadsheet.
* Fixed a bug that was preventing output from being written to the scripting console.
* Improved video playback performance issues on Windows and Mac.
* Fixed some cases where cells failed to display correctly when temporal ordering is active.

### Known Issues
  * In some cases, the asterisk in the title bar indicating that changes have been made to the file may not appear/disappear correctly
  * Hidden variables may be listed as shown in the variable list
  * Temporal ordering displays cells incorrectly in rare cases
  * Windows will not correctly handle opening a file by double clicking it once Datavyu is already open.

# Datavyu 1.0.3 Release Notes
## 9/19/2013

### Additions
  * Datavyu automatically checks for updates when opened
  * On file open, a window pops up to display file open progress
  * Ruby scripting API is documented on the support wiki
  * Errors print to the scripting console window
  * A message prints to scripting console window to indicate that a script is running

### Changes
 * Scripts must not include the entire API at the top of the file. Instead, add the line _require ‘Datavyu\_API.rb'_ to the top of the file before the _begin_ command. Please see the support wiki for details.

### Bug Fixes
  * Fixed change_arg and make_rel functions in the scripting API
  * Prevented Datavyu from writing special characters that made files unreadable in OpenSHAPA
  * Fixed a bug where some offsets would be set to 00:00:000 when the file was saved
  * Fixed the delete cell command
  * Performance improvements when opening files
  * Fixed memory issues that caused a drop in video playback performance

### Known Issues
  * Video playback on Windows may be laggy
  * In some cases, the asterisk in the title bar indicating that changes have been made to the file may not appear/disappear correctly
  * Hidden variables may be listed as shown in the variable list
  * Temporal ordering displays cells incorrectly in rare cases
