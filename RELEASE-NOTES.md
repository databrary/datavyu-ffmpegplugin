## Datavyu 1.0.4rev3 Release Notes
### 12/11/2013

#### Additions
* Added file association support in Mac and Windows. Double-clicking on a .opf file will now open the file in Datavyu.

#### Changes
* "Variables" have been renamed "columns" and "matrix arguments" have been renamed "codes" throughout the user interface.
* All API functions have been renamed to use the words "column" and "code", however, API functions using the old naming scheme will continue to work so that scripts do not need to be revised. 
* Scripts now allow the user to overwrite an existing column. Previously, trying to set a column back to the spreadsheet caused an error if the columns shared the same name. 

#### Bug Fixes
* Fixed an issue with Datavyu failing to close completely (preventing the user from being able to reopen the program). 
* Corrected a case where setting the offset of a cell changed the offset of the previous cell in a column
* Fixed the create\_mutually\_exclusive script to correct an error where the final cell was not written to the spreadsheet.
* Fixed a bug that was preventing output from being written to the scripting console.
* Improved video playback performance issues on Windows and Mac.
* Fixed some cases where cells failed to display correctly when temporal ordering is active.

#### Known Issues
  * In some cases, the asterisk in the title bar indicating that changes have been made to the file may not appear/disappear correctly
  * Hidden variables may be listed as shown in the variable list
  * Temporal ordering displays cells incorrectly in rare cases
  * Windows will not correctly handle opening a file by double clicking it once Datavyu is already open.

_To download the Datavyu 1.0.4rev3 pre-release, please visit 
[http://datavyu.org/download_pre/][1]_

## Datavyu 1.0.3 Release Notes
### 9/19/2013

#### Additions
  * Datavyu automatically checks for updates when opened
  * On file open, a window pops up to display file open progress 
  * Ruby scripting API is documented on the support wiki
  * Errors print to the scripting console window
  * A message prints to scripting console window to indicate that a script is running

#### Changes
 * Scripts must not include the entire API at the top of the file. Instead, add the line _require ‘Datavyu\_API.rb'_ to the top of the file before the _begin_ command. Please see the support wiki for details.

#### Bug Fixes
  * Fixed change_arg and make_rel functions in the scripting API
  * Prevented Datavyu from writing special characters that made files unreadable in OpenSHAPA
  * Fixed a bug where some offsets would be set to 00:00:000 when the file was saved
  * Fixed the delete cell command
  * Performance improvements when opening files
  * Fixed memory issues that caused a drop in video playback performance

#### Known Issues
  * Video playback on Windows may be laggy
  * In some cases, the asterisk in the title bar indicating that changes have been made to the file may not appear/disappear correctly
  * Hidden variables may be listed as shown in the variable list
  * Temporal ordering displays cells incorrectly in rare cases

_To download the Datavyu 1.0.3 full release, please visit 
[http://datavyu.org/download/][1]_
 [1]: http://datavyu.org/download_pre/
