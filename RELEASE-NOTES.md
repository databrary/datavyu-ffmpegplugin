# Datavyu 1.2 Release Notes
## 06/30/2014

### Additions
* Choose your own favourites folder: will determine contents of bottom-left file panel, script dropdown menu

### Bug Fixes
* Undo works correctly for column deletions
* Application title bar now always reflects the name of the current project
* Fixes to scripting API: mutex, transfer columns

### Known Issues
* When renaming a file with "Save as" and trying to open the original file, loading sometimes fails with a wrongful "This file is already open" error.
* Undo for cell creation is not reliable

# Datavyu 1.1.8 Pre-Release Notes
## 06/09/2014

### Additions
* Undo is implemented to the value level
* Frame-by-frame export is now available in the drop down menu


### Bug Fixes
* Exporting from the drop down menu works with text columns and cells that contain commas
* The warning message for columns with special characters will either continue showing or not show again when you open a spreadsheet, depending on your preference
* Double clicking on the file directory is fixed
* Scripting and the code editor no longer break temporal alignment
* Script mutex works
* Scripting with single characters no longer causes an exception
* Codes left uncoded are evaluated as empty strings


### Known Issues
* Dragging the application to the desktop upon download causes the favorites folder to lose its contents. To use the scripts in this folder, go to your downloads.
* Deleting a column and then clicking "Undo" does not bring back all of the cells, their values, and onset/offset times.

# Datavyu 1.1.7 Pre-Release Notes
## 05/09/2014

### Additions
* You can now open multiple spreadsheets in datavyu! Each spreadsheet has its own corresponding controller. Tabs at the top of the spreadsheet are used to navigate between open spreadsheets. 
* We have added our user guide, release notes, and developer notes to the application folder. 
* You can manually set the jog rate of a video on the Controller. Please double click on “Steps per second”, type in your new frame rate and then click “Enter”. 

### Changes
* When you “Add Data” with the controller, it defaults to finding “Video Files”. If you cannot find your desired file, you can simply change the format to “All Files”. 


### Known Issues
* The scripting output on PCs does not give as detailed error messages as it does on Macs
* Double clicking on the file directory on the left does not always work. If it fails to do so, use the file and script menus to open spreadsheets and run scripts. 
* Opening the code editor breaks temporal alignment
* PC's with CPU's of less than 2.67 GHz have choppy video playback when playing at full speed

# Datavyu 1.1.3 Pre-Release Notes
## 04/11/2014

### Additions
* New hidden column count on the spreadsheet 
* Up and down arrow keys allow you to move up and down cells

### Bug Fixes
* Saving files works properly 
* Right and left arrow keys allow you to move through codes when in a cell
* Locking into a cell on a PC works
* Stable name restrictions in code editor and spreadsheet


### Known Issues
* You can drag a code name within a cell but it does not influence the code itself and it does not save. Please save your file and reopen it. 
* Scripting output sometimes does not give you the proper output or any output at all. You can cross check your scripts in OpenSHAPA.
* Occasionally, turning on temporal alignment makes the ordinal numbers disappear but it corrects itself when you turn temporal alignment off and it does not influence exporting.


# Datavyu 1.1 Release Notes
## 03/10/2014

### Additions
* Created more prompts to save to prevent accidental data loss.
* Minimizing the spreadsheet window will minimize all Datavyu windows.

### Changes
* The names of some software features were changed to adhere to a more intuitive naming scheme:
    * "Vocab Editor" --> "Code Editor"
    * "Observation" --> "Cell"
    * "Argument" --> "Code" 
    * "Temporal Ordering" --> "Temporal Alignment"
    * "Variable" --> "Column"
    * "Data Viewer" --> "Media Player" 
    * "Data Viewer Controller" --> "Controller" 
* Quicktime 7.7.5 no longer includes java libraries, but you can easily include them in your download. For new installs and for Quicktime updates, please follow the steps provided on the [installation page](http://www.datavyu.org/user-guide/_images/install-quicktime-special.png). 
* The [Controller](http://www.datavyu.org/user-guide/_images/keypad-add-data-button.png) has a new format that is no standard between PC and Mac keyboards.
* Users are now restricted to Matrix-style columns. Text and nominal columns can no longer be created but pre-existing instances from saved files are still supported. Since it is now the only type of column, matrices are now referred to simply as columns.
* Restrictions on column names have been enforced to prevent problems in the scripting interface. 
    * Column names must begin with a letter. 
    * Only letters, numbers and underscores are allowed in column names. 
    * Previously created columns that do not adhere to these restrictions will continue to work, but new column names (or name changes) will be restricted. 
    * We have provided a script in favorites folder that will turn periods into underscores in existing columns to aid the transition. 
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
* The arrow keys do not always allow you to move from code to code in the code editor and spreadsheet but the tab and shift tab keys have the same function. 
* Undo history is not always accurate.
* You can click, hold down, and drag a code name within a cell but it does not influence the code itself and it does not save. Please save your file and reopen it. 
* Occasionally, running a script will produce no output when in fact, the script did work.
* The changed file marker (*) doesn't always disappear on save.
* VLC cannot accurately determine frame rates of our videos and thus is unsupported in Datavyu.




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
* Fixed some cases where cells failed to display correctly when temporal alignment is active.

### Known Issues
  * In some cases, the asterisk in the title bar indicating that changes have been made to the file may not appear/disappear correctly
  * Hidden variables may be listed as shown in the variable list
  * Temporal alignment displays cells incorrectly in rare cases
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
  * Temporal alignment displays cells incorrectly in rare cases
