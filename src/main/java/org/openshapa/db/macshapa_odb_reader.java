/*
 * macshapa_odb_reader.java
 *
 * Created on November 29, 2008, 1:17 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.openshapa.db;

import java.util.Vector;
        
/**
 *
 */
public class macshapa_odb_reader
{
/******************************************************************************
 *                                                                             
 *                         macshapa_odb_reader                                 
 *                                                                             
 * Class to read MacSHAPA ODB files and load them (to the extent possible),
 * into an instance of the Database class.
 *                                                                             
 ******************************************************************************/

    /*************************************************************************/
    /****************************** Constants: *******************************/
    /*************************************************************************/
    
    final int FALSE = 0;
    final int TRUE  = 1;
    
    final int DEFAULT_HEXTENT = 200;
    
    final int MIN_COLUMN_WIDTH = 125;
    final int MAX_COLUMN_WIDTH = 1500;
    
    final int MIN_DEBUG_LEVEL = 0;
    final int MAX_DEBUG_LEVEL = 1;
    
    final int MAX_ERRORS_MIN = 1;
    final int MAX_ERRORS_MAX = 10;
    
    final int MAX_WARNINGS_MIN = 1;
    final int MAX_WARNINGS_MAX = 99;

    final long MACSHAPA_MAX_INT = 100000;
    final long MACSHAPA_MIN_INT = -100000;
    
    final long MACSHAPA_MIN_TIME = 0;
    final long MACSHAPA_MAX_TIME = 
            (59 * 60 * 60 * 60) + (59 * 60 * 60) + (59 * 60) + 59;
    
    final int MACSHAPA_TICKS_PER_SECOND = 60;
    
    final String QUERY_VAR_NAME = "###QUERY VAR###";
    
    
    /*************************************************************************
     * The following codes are used to differentiate between the various a-list
     * entry labels.  Note that the integer values assigned to theses constants
     * also index the string representations of the a-list labels in the a-list
     * label name table.  Since this table is searched via a binary search on a
     * sorted list, the a-list entry lable codes must be assigned in 
     * alphabetical order.  The a_list_tag_name_table is defined in lexer 
     * section below.
     *************************************************************************/

    final int ALIGNMENTS_LABEL                                      =  0;
    final int CELLS_LABEL                                           =  1;
    final int COLUMN_WIDTH_LABEL                                    =  2;
    final int DEBUG_LEVEL_LABEL                                     =  3;
    final int FORMAL_ARG_LIST_LABEL                                 =  4;
    final int GROUPS_LABEL                                          =  5;
    final int HARD_PREC_LABEL                                       =  6;
    final int HEADER_LABEL                                          =  7;
    final int IMPORT_FORMATS_LIST_LABEL                             =  8;
    final int IMPORT_PRODUCTION_LIST_LABEL                          =  9;
    final int INSERT_ACTIONS_LABEL                                  = 10;
    final int IS_SHOWN_LABEL                                        = 11;
    final int IS_TRACED_LABEL                                       = 12;
    final int MAX_ERRORS_LABEL                                      = 13;
    final int MAX_WARNINGS_LABEL                                    = 14;
    final int NAME_LABEL                                            = 15;
    final int OFFSET_LABEL                                          = 16;
    final int ONSET_LABEL                                           = 17;
    final int PATTERN_LABEL                                         = 18;
    final int PREDICATE_DEFINITIONS_LABEL                           = 19;
    final int PROGRAM_ACTIONS_LABEL                                 = 20;
    final int QUERY_LABEL                                           = 21;
    final int SHAPA_PANE_VARS_LABEL                                 = 22;
    final int SPREADSHEET_VARIABLE_DECLARATIONS_LABEL               = 23;
    final int SPREADSHEET_VARIABLE_DEFINITIONS_LABEL                = 24;
    final int SYSTEM_LABEL                                          = 25;
    final int TEXT_LABEL                                            = 26;
    final int TIME_LABEL                                            = 27;
    final int TYPE_LABEL                                            = 28;
    final int USER_LABEL                                            = 29;
    final int VAL_LABEL                                             = 30;
    final int VAR_LABEL                                             = 31;
    final int VARIABLE_LENGTH_LABEL                                 = 32;
    final int VERSION_LABEL                                         = 33;
    final int VOCAB_LABEL                                           = 34;
    
    final int NUMBER_OF_ALIST_LABELS                                = 35;
    
    final int UNKNOWN_ALIST_LABEL                                   = 35;


    /*************************************************************************
     * The following #defines contain the strings used to label a-list entries.
     * These are the labels refered to in the above a-list label #defines.  
     * They should also be listed in alphabetical order, although this is 
     * merely an aid updating the a_list_tag_name_table in the lexer section
     * below.
     *************************************************************************/

    final String ALIGNMENTS_STR                        = "ALIGNMENTS>";
    final String CELLS_STR                             = "CELLS>";
    final String COLUMN_WIDTH_STR                      = "COLUMN-WIDTH>";
    final String DEBUG_LEVEL_STR                       = "DEBUG-LEVEL>";
    final String FORMAL_ARG_LIST_STR                   = "FORMAL-ARG-LIST>";
    final String GROUPS_STR                            = "GROUPS>";
    final String HARD_PREC_STR                         = "HARD-PREC>";
    final String HEADER_STR                            = "HEADER>";
    final String IMPORT_FORMATS_LIST_STR               = "IMPORT-FORMATS-LIST>";
    final String IMPORT_PRODUCTION_LIST_STR            = "IMPORT-PRODUCTION-LIST>";
    final String INSERT_ACTIONS_STR                    = "INSERT-ACTIONS>";
    final String IS_SHOWN_STR                          = "IS-SHOWN>";
    final String IS_TRACED_STR                         = "IS-TRACED>";
    final String MAX_ERRORS_STR                        = "MAX-ERRORS>";
    final String MAX_WARNINGS_STR                      = "MAX-WARNINGS>";
    final String NAME_STR                              = "NAME>";
    final String OFFSET_STR                            = "OFFSET>";
    final String ONSET_STR                             = "ONSET>";
    final String PATTERN_STR                           = "PATTERN>";
    final String PREDICATE_DEFINITIONS_STR             = "PREDICATE-DEFINITIONS>";
    final String PROGRAM_ACTIONS_STR                   = "PROGRAM-ACTIONS>";
    final String QUERY_STR                             = "QUERY>";
    final String SHAPA_PANE_VARS_STR                   = "SHAPA-PANE-VARS>";
    final String SPREADSHEET_VARIABLE_DECLARATIONS_STR = "SPREADSHEET-VARIABLE-DECLARATIONS>";
    final String SPREADSHEET_VARIABLE_DEFINITIONS_STR  = "SPREADSHEET-VARIABLE-DEFINITIONS>";
    final String SYSTEM_STR                            = "SYSTEM>";
    final String TEXT_STR                              = "TEXT>";
    final String TIME_STR                              = "TIME>";
    final String TYPE_STR                              = "TYPE>";
    final String USER_STR                              = "USER>";
    final String VAL_STR                               = "VAL>";
    final String VAR_STR                               = "VAR>";
    final String VARIABLE_LENGTH_STR                   = "VARIABLE-LENGTH>";
    final String VERSION_STR                           = "VERSION>";
    final String VOCAB_STR                             = "VOCAB>";


    /*************************************************************************
     * The following code are used to differentiate between the various private
     * values that MacSHAPA uses to store the database.  Note that the integer
     * values associated with these constants also index the string 
     * representations of the private values in the private value name table.  
     * Since this table is searched via a binary search on a sorted list, the 
     * private value code must be assigned in alphabetical order. 
     *
     * If new private value codes are created, parse_s_var_type_attribute()
     * below will have to be updated, along with the tables in the lexer section
     * below.
     *************************************************************************/

    final int FLOAT_PVAL                                            = 0;
    final int INTEGER_PVAL                                          = 1;
    final int MATRIX_PVAL                                           = 2;
    final int NOMINAL_PVAL                                          = 3;
    final int PREDICATE_PVAL                                        = 4;
    final int TEXT_PVAL                                             = 5;

    final int NUMBER_OF_PRIVATE_VALUES                              = 6;
    final int UNKNOWN_PRIVATE_VALUE                                 = 6;


    /*************************************************************************
     * The following constants contain the strings used to represent the 
     * private values whose codes are given above.  They should also be 
     * listed in alphabetical order, although this is merely an aid updating 
     * the private_value_name_table in the lexer section below
     *************************************************************************/

    final String FLOAT_PVAL_STR		= "<<FLOAT>>";
    final String INTEGER_PVAL_STR       = "<<INTEGER>>";
    final String MATRIX_PVAL_STR        = "<<MATRIX>>";
    final String NOMINAL_PVAL_STR       = "<<NOMINAL>>";
    final String PREDICATE_PVAL_STR     = "<<PREDICATE>>";
    final String TEXT_PVAL_STR          = "<<TEXT>>";

    
    /* The token constants are used to indicate the general type of a token
     * recognized by the lexical analyzer.
     */
    final int ERROR_TOK       = 0;
    final int L_PAREN_TOK     = 1;
    final int R_PAREN_TOK     = 2;
    final int SYMBOL_TOK      = 3;
    final int INT_TOK         = 4;
    final int FLOAT_TOK       = 5;
    final int STRING_TOK      = 6;
    final int BOOL_TOK        = 7;
    final int ALIST_LABEL_TOK = 8;
    final int PRIVATE_VAL_TOK = 9;
    final int SETF_TOK        = 10;
    final int DB_VAR_TOK      = 11;
    final int QUOTE_TOK       = 12;
    final int EOF_TOK         = 13;

    final int MAX_TOKEN_CODE  = 13;
    

    /* When the lexical analyzer encounters a symbol (i.e. an identifier 
     * bracketed  with '|' characters), it doesn't know whether it should be 
     * looking for a  predicate name, a nominal or a formal argument.  Since 
     * there are different constraints on the characters that may appear in 
     * each of these types of symbols, the lexical analyzer keeps track of 
     * what the symbol could be as a function of the characters that appear 
     * in it, and indicates the possibilities via the following flag constants.  
     * There are similar ambiguities when dealing with quoted strings.  The 
     * second three flag constants are used in this case.
     */

    final int PRED_FLAG         = 0x01;
    final int COLUMN_FLAG       = 0x02;
    final int NOMINAL_FLAG      = 0x04;
    final int FORMAL_ARG_FLAG   = 0x08;

    final int TEXT_QSTRING_FLAG = 0x10;
    final int QSTRING_FLAG      = 0x20;
    final int NONBLANK_FLAG     = 0x40;
    
    /**
     * MacSHAPA Open Database Read Error Message code constants
     *
     * The associated table of error messages follow.  Both this table
     * and the following list of constants must be updated when a new
     * error or warning message is defined.
     */

    final int MAX_WARNINGS_EXCEEDED_ERR				=  0;
    final int NEW_LINE_IN_SYMBOL_ERR				=  1;
    final int UNTERMINATED_FORMAL_ARG_ERR			=  2;
    final int ZERO_LENGTH_SYMBOL_ERR				=  3;
    final int UNEXPECTED_END_OF_FILE_ERR                        =  4;
    final int ILL_FORMED_NUMERICAL_CONST_ERR			=  5;
    final int UNKNOWN_TOKEN_TYPE_ERR				=  6;
    final int ILL_FORMED_PRIVATE_VALUE_ERR			=  7;
    final int LEFT_PAREN_EXPECTED_ERR				=  8;
    final int RIGHT_PAREN_EXPECTED_ERR				=  9;
    final int SETF_EXPECTED_ERR					= 10;
    final int DB_VAR_EXPECTED_ERR				= 11;
    final int QUOTE_EXPECTED_ERR				= 12;
    final int MISSING_OR_OUT_OF_ORDER_ALIST_ENTRY_ERR           = 13;
    final int REQUIRED_ALIST_ENTRIES_MISSING_ERR                = 14;
    final int EMPTY_ALIST_ENTRY_ERR				= 15;
    final int DATA_ITEM_TOO_LARGE_ERR                           = 16;
    
    final int NUMBER_OF_ERROR_MESSAGES				= 17;
    
    
    /**
     * MacSHAPA Open Database Error Message table.
     */

    final String error_mssg_table[] =
    {
        /*  0 -- MAX_WARNINGS_EXCEEDED_ERR */
        "Warning limit exceeded - load aborted.\n",

        /*  1 -- NEW_LINE_IN_SYMBOL_ERR */
        "New line encountered in a symbol.  Symbol not terminated with a '|'?\n",

        /*  2 -- UNTERMINATED_FORMAL_ARG_ERR */
        "Encountered formal argument symbol that is not terminated with a '>'.\n",

        /*  3 -- ZERO_LENGTH_SYMBOL_ERR */
        "Symbols may not have zero length.  " +
                "Thus the symbol \"||\" is not permitted.\n",

        /*  4 -- UNEXPECTED_END_OF_FILE_ERR */
        "Encountered end of file unexpectedly.\n",

        /*  5 -- ILL_FORMED_NUMERICAL_CONST_ERR */
        "Ill formed numerical constant.\n",

        /*  6 -- UNKNOWN_TOKEN_TYPE_ERR */
        "Encountered unrecognizable token in the input stream.\n",

        /*  7 -- ILL_FORMED_PRIVATE_VALUE_ERR */
        "Encountered syntactically incorrect private value.\n",

        /*  8 -- LEFT_PAREN_EXPECTED_ERR */
        "Left parenthesis expected.  " +
                "Unable to recover from error by examining context.\n",

        /*  9 -- RIGHT_PAREN_EXPECTED_ERR */
        "Right parenthesis expected.  " +
                "Unable to recover from error by examining context.\n",

        /* 10 -- SETF_EXPECTED_ERR */
        "\"setf\" expected.  Unable to recover from error by examining context.\n",

        /* 11 -- DB_VAR_EXPECTED_ERR */
        "\"macshapa-db\" expected.  " +
                "Unable to recover from error by examining context.\n",

        /* 12 -- QUOTE_EXPECTED_ERR */
        "\"'\" expected.  Unable to recover from error by examining context.\n",

        /* 13 -- MISSING_OR_OUT_OF_ORDER_ALIST_ENTRY_ERR */
        "Encountered a-list entry prior to one or more of its " +
                "required predicessors in its a-list.\n",

        /* 14 -- REQUIRED_ALIST_ENTRIES_MISSING_ERR */
        "Encountered a-list with one or more required entries missing.\n",

        /* 15 -- EMPTY_ALIST_ENTRY_ERR */
        "Encountered a-list entry without a value.  " +
                "A-list entries must be of the form '(' <tag> <value> ')'\n",

        /* 16 -- DATA_ITEM_TOO_LARGE_ERR */
        "Encountered a data item whose text representation is too " +
                "long to be inserted into the database.\n"
    };


    final int ILLEGAL_ESCAPE_SEQ_IN_SYMBOL_WARN                 =   0;
    final int ILLEGAL_CHAR_IN_SYMBOL_WARN                       =   1;
    final int LEADING_WS_IN_SYMBOL_ERR				=   2;
    final int TRAILING_WS_IN_SYMBOL_ERR				=   3;
    final int ILLEGAL_ESC_SEQ_IN_QUOTE_STR_WARN                 =   4;
    final int ILLEGAL_CHAR_IN_QUOTE_STR_WARN			=   5;
    final int INTEGER_OUT_OF_RANGE_WARN				=   6;
    final int FLOAT_VAL_OUT_OF_RANGE_WARN			=   7;
    final int PART_OF_FRACTION_DISCARDED_WARN			=   8;
    final int LEFT_PAREN_EXPECTED_WARN				=   9;
    final int RIGHT_PAREN_EXPECTED_WARN				=  10;
    final int SETF_EXPECTED_WARN				=  11;
    final int DB_VAR_EXPECTED_WARN				=  12;
    final int QUOTE_EXPECTED_WARN				=  13;
    final int EMPTY_ALIST_ENTRY_WARN				=  14;
    final int ILLEGAL_UNKNOWN_ALIST_ENTRY_VAL_WARN              =  15;
    final int EXCESS_VALUES_IN_ALIST_ENTRY_WARN                 =  16;
    final int DUPLICATE_ALIST_ENTRY_WARN			=  17;
    final int UNKNOWN_OR_UNEXPECTED_ALIST_ENTRY_WARN            =  18;
    final int NON_ALIST_ENTRY_LIST_IN_ALIST_WARN		=  19;
    final int NON_ALIST_ENTRY_ATOM_IN_ALIST_WARN		=  20;
    final int NON_POSITIVE_DB_VERSION_NUM_WARN                  =  21;
    final int ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN                =  22;
    final int EMPTY_QUOTE_STRING_WARN                           =  23;
    final int NON_POSITIVE_ERROR_LIMIT_WARN			=  24;
    final int ERROR_LIMIT_TOO_LARGE_WARN			=  25;
    final int NON_POSITIVE_WARNING_LIMIT_WARN			=  26;
    final int WARNING_LIMIT_TOO_LARGE_WARN			=  27;
    final int NEGATIVE_DEBUG_LEVEL_WARN				=  28;
    final int MISSING_ALIST_ENTRY_WARN				=  29;
    final int ALIEN_LIST_IN_PRED_DEFS_LIST_WARN                 =  30;
    final int ALIEN_ATOM_IN_PRED_DEFS_LIST_WARN                 =  31;
    final int EMPTY_PRED_DEF_WARN                               =  32;
    final int TYPE_MISMATCH_IN_PRED_DEF_WARN			=  33;
    final int EXCESS_VALUES_IN_A_PREDICATE_DEF_WARN             =  34;
    final int NON_FARG_IN_FARG_LIST_WARN                        =  35;
    final int EMPTY_FORMAL_ARGUMENT_LIST_WARN			=  36;
    final int NAME_IN_PRED_DEF_NOT_A_PRED_NAME_WARN             =  37;
    final int PREDICATE_REDEFINITION_WARN                       =  38;
    final int ALIEN_LIST_IN_S_VAR_DEC_LIST_WARN                 =  39;
    final int ALIEN_ATOM_IN_S_VAR_DEC_LIST_WARN                 =  40;
    final int NAME_IN_S_VAR_DEC_NOT_A_S_VAR_NAME_WARN           =  41;
    final int EMPTY_S_VAR_DEC_WARN                              =  42;
    final int TYPE_MISMATCH_IN_S_VAR_DEC_WARN			=  43;
    final int EXCESS_VALUES_IN_A_S_VAR_DEC_WARN                 =  44;
    final int BAD_FARG_IN_SVAR_FARG_LIST_WARN			=  45;
    final int INSUF_FARGS_IN_SVAR_FARG_LIST_WARN		=  46;
    final int COL_WIDTH_OUT_OF_RANGE_WARN			=  47;
    final int S_VAR_TYPE_ARG_LIST_MISMATCH_WARN                 =  48;
    final int S_VAR_PRED_NAME_COLLISION_WARN			=  49;
    final int S_VAR_REDEFINITION_WARN				=  50;
    final int VAR_LEN_NON_MATRIX_S_VAR_DEC_WARN                 =  51;
    final int DUP_FARG_WARN					=  52;
    final int ALIEN_LIST_IN_S_VAR_DEF_LIST_WARN                 =  53;
    final int ALIEN_ATOM_IN_S_VAR_DEF_LIST_WARN                 =  54;
    final int NAME_IN_S_VAR_DEF_NOT_A_S_VAR_NAME_WARN           =  55;
    final int UNDECLARED_S_VAR_WARN				=  56;
    final int EMPTY_S_VAR_DEF_WARN                              =  57;
    final int TYPE_MISMATCH_IN_S_VAR_DEF_WARN			=  58;
    final int EXCESS_VALUES_IN_A_S_VAR_DEF_WARN                 =  59;
    final int INAPROPRIATE_VOCAB_ATTRIBUTE_WARN                 =  60;
    final int ATOM_IN_A_S_VAR_CELL_LIST_WARN			=  61;
    final int UNKNOWN_OR_OUT_OF_ORDER_CELL_VALUE_WARN           =  62;
    final int TIME_OUT_OF_RANGE_WARN				=  63;
    final int S_VAR_CELL_VALUE_TYPE_MISMATCH_WARN		=  64;
    final int S_VAR_CELL_VALUE_MISSING_WARN			=  65;
    final int FARG_NAME_MISMATCH_WARN				=  66;
    final int NOMINAL_COERCED_TO_CELL_NOMINAL_WARN              =  67;
    final int INVALID_PRED_NAME_IN_PRED_VALUE_WARN              =  68;
    final int UNDEFINED_PRED_WARN                               =  69;
    final int PRED_VALUE_EXPECTED_WARN                          =  70;
    final int ILLEGAL_CHARS_IN_QUOTE_STR_WARN			=  71;
    final int PRED_OR_COL_VAR_USED_AS_NOM_WARN                  =  72;
    final int ILLEGAL_ATOM_IN_PRED_ARG_LIST_WARN		=  73;
    final int EXCESS_ARGS_IN_PRED_VALUE_WARN			=  74;
    final int REQ_ARGS_MISSING_FROM_PRED_VAL_WARN		=  75;
    final int ILLEGAL_ATOM_IN_MATRIX_ARG_WARN			=  76;
    final int REQ_ARGS_MISSING_FROM_MATRIX_WARN                 =  77;
    final int CELL_WITH_UNDEFINED_VALUE_WARN			=  78;
    final int CELL_WITH_UNDEFINED_ONSET_WARN			=  79;
    final int CELL_WITH_UNDEFINED_OFFSET_WARN			=  80;
    final int NON_PRED_IN_VOCAB_WARN				=  81;
    final int UNDEF_PRED_IN_VOCAB_LIST_WARN			=  82;
    final int S_VAR_IN_VOCAB_LIST_WARN				=  83;
    final int SYSTEM_PRED_IN_VOCAB_LIST_WARN			=  84;
    final int ATOM_IN_QUERY_LIST_WARN				=  85;
    final int INVALID_S_VAR_NAME_IN_SP_VAR_LIST_WARN            =  86;
    final int REF_TO_UNDEF_S_VAR_IN_SP_VAR_LIST_WARN            =  87;
    final int REF_TO_SYSTEM_S_VAR_IN_SP_VAR_LIST_WARN           =  88;
    final int DUP_REF_TO_S_VAR_IN_SP_VAR_LIST_WARN              =  89;
    final int NON_S_VAR_IN_SHAPA_VARS_LIST_WARN                 =  90;
    final int ATOM_IN_GROUPS_LIST_WARN				=  91;
    final int ATOM_IN_GROUP_LIST_WARN				=  92;
    final int INVALID_S_VAR_NAME_IN_GROUP_MEMBER_WARN           =  93;
    final int REF_TO_UNDEF_S_VAR_IN_GROUP_MEMBER_WARN           =  94;
    final int REF_TO_SYS_S_VAR_IN_GROUP_MEMBER_WARN             =  95;
    final int ILL_FORMED_GROUP_MEMBER_WARN			=  96;
    final int REF_TO_UNDEF_S_VAR_CELL_IN_GRP_MEM_WARN           =  97;
    final int EXCESS_VALUES_IN_GROUP_MEMBER_WARN                =  98;
    final int ATOM_IN_ALIGNMENTS_LIST_WARN			=  99;
    final int ALIEN_LIST_IN_ALIGNMENTS_LIST_WARN		= 100;
    final int INVALID_ALIGNMENT_NAME_WARN                       = 101;
    final int ALIGNMENT_NAME_ALREADY_IN_USE_WARN		= 102;
    final int EMPTY_ALIGNMENT_WARN				= 103;
    final int TYPE_MISMATCH_IN_ALIGNMENT_WARN			= 104;
    final int EXCESS_VALUES_IN_AN_ALIGNMENT_WARN		= 105;
    final int ILL_FORMED_ALIGNMENT_WARN				= 106;
    final int ATOM_IN_IMPORT_FORMATS_LIST_WARN                  = 107;
    final int ILLEGAL_IMPORT_FORMAT_LIST_NAME_WARN              = 108;
    final int EMPTY_IMPORT_FORMAT_LIST_WARN			= 109;
    final int IMPORT_FORMAT_LIST_TYPE_MISMATCH_WARN             = 110;
    final int EXCESS_VALUES_IN_IMP_FORMAT_LIST_WARN             = 111;
    final int ATOM_IN_IMPORT_PRODUCTIONS_LIST_WARN              = 112;
    final int BOTH_PGM_AND_INSERT_ACTION_IN_PROD_WARN           = 113;
    final int IMPORT_PROD_NAME_TOO_LONG_WARN			= 114;
    final int IMPORT_PROD_PATTERN_TOO_LONG_WARN                 = 115;
    final int ATOM_IN_IMP_PROD_INS_ACTIONS_LIST_WARN            = 116;
    final int ATOM_IN_IMP_PROD_PMG_ACTIONS_LIST_WARN            = 117;
    final int MISSING_REQ_ATTR_IN_IMP_PROD_WARN                 = 118;
    final int ALIGNMENTS_AFTER_SHAPA_PANE_VARS_WARN             = 119;
    final int EMPTY_IMPORT_PROD_INS_ACTION_LIST_WARN            = 120;
    final int EMPTY_IMPORT_PROD_PGM_ACTION_LIST_WARN            = 121;
    final int MISSING_REQ_ATTR_IN_INS_ACTION_WARN		= 122;
    final int MISSING_REQ_ATTR_IN_PGM_ACTION_WARN		= 123;
    final int EXCESS_IMP_PROD_PGM_ACTIONS_WARN                  = 124;
    final int COL_PRED_CANT_REPLACE_PRED_FARG_WARN              = 125;
    final int FARG_ARG_TYPE_MISMATCH_WARN                       = 126;
    final int UNDEFINED_COL_PRED_WARN                           = 127;
    final int INVALID_COL_PRED_NAME_IN_COL_PRED_VALUE_WARN      = 128;
    final int COL_PRED_VALUE_EXPECTED_WARN                      = 129;
    final int ILLEGAL_ATOM_IN_COL_PRED_ARG_LIST_WARN		= 130;
    final int EXCESS_ARGS_IN_COL_PRED_VALUE_WARN		= 131;
    final int REQ_ARGS_MISSING_FROM_COL_PRED_VAL_WARN		= 132;
    
    final int NUMBER_OF_WARNING_MESSAGES			= 133;
    
    
    /**
    * WARNING MESSAGE TABLE
    */

    String warning_mssg_table[] =
    {
        /*   0 -- ILLEGAL_ESCAPE_SEQ_IN_SYMBOL_WARN */
        "Illegal escape sequence detected in a symbol.  " +
                "Sequence replaced with a '_'.\n",

        /*   1 -- ILLEGAL_CHAR_IN_SYMBOL_WARN */
        "Illegal character detected in a symbol.  " +
                "Character replaced with a '_'.\n",

        /*   2 -- LEADING_WS_IN_SYMBOL_ERR */
        "Leading white space detected in a symbol.  " +
                "Whitespace converted to a '_'.\n",

        /*   3 -- TRAILING_WS_IN_SYMBOL_ERR */
        "Trailing white space detected in a symbol.  " +
                "Whitespace converted to a '_'.\n",

        /*   4 -- ILLEGAL_ESC_SEQ_IN_QUOTE_STR_WARN */
        "Illegal escape sequence detected in a string.  " +
                "Sequence replaced with a '_'.\n",

        /*   5 -- ILLEGAL_CHAR_IN_QUOTE_STR_WARN */
        "Illegal character detected in a string.  " +
                "Character replaced with a '_'.\n",

        /*   6 -- INTEGER_OUT_OF_RANGE_WARN */
        "Integer value out of range.  Coerced value to nearest legal value.\n",

        /*   7 -- FLOAT_VAL_OUT_OF_RANGE_WARN */
        "Absolute value of floating point value too large.  " +
                "Coerced value to nearest legal value.\n",

        /*   8 -- PART_OF_FRACTION_DISCARDED_WARN */
        "Discarded one or more least significant digits from fractional " +
                "part of floating point value.\n",

        /*   9 -- LEFT_PAREN_EXPECTED_WARN */
        "Left parentheses expected.  Left parenthesis inserted to allow " +
                "continued parse.\n",

        /*  10 -- RIGHT_PAREN_EXPECTED_WARN */
        "Right parentheses expected.  Right parenthesis inserted to allow " +
                "continued parse.\n",

        /*  11 -- SETF_EXPECTED_WARN */
        "\"setf\" expected.  \"setf\" inserted to allow continued parse.\n",

        /*  12 -- DB_VAR_EXPECTED_WARN */
        "\"macshapa-db\" expected.  \"setf\" inserted to allow " +
                "continued parse.\n",

        /*  13 -- QUOTE_EXPECTED_WARN */
        "\"'\" expected.  \"'\" inserted to allow continued parse.\n",

        /*  14 -- EMPTY_ALIST_ENTRY_WARN */
        "Encountered a-list entry without a value.  " +
                "A-list entries must be of the form '(' <tag> <value> ')'.\n",

        /*  15 -- ILLEGAL_UNKNOWN_ALIST_ENTRY_VAL_WARN */
        "Illegal value in the value slot of an unknown or  " +
                "unexpected a-list entry.\n",

        /*  16 -- EXCESS_VALUES_IN_ALIST_ENTRY_WARN */
        "Encountered a-list entry with more than one value.  " +
                "Excess values were discarded.\n",

        /*  17 -- DUPLICATE_ALIST_ENTRY_WARN */
        "Duplicate a-list entry detected.  All data contained in the " +
                "duplicate entry will be discarded.\n",

        /*  18 -- UNKNOWN_OR_UNEXPECTED_ALIST_ENTRY_WARN */
        "Encountered unknown or unexpected alist entry.  " +
                "Entry will be read and discarded.\n",

        /*  19 -- NON_ALIST_ENTRY_LIST_IN_ALIST_WARN */
        "Encountered list that is not an a-list entry in an a-list.  " +
                "The list will be read and discarded.\n",

        /*  20 -- NON_ALIST_ENTRY_ATOM_IN_ALIST_WARN */
        "Encountered an atom in an a-list.  " +
                "The atom will be read and discarded.\n",

        /*  21 -- NON_POSITIVE_DB_VERSION_NUM_WARN */
        "The database format version number must be a positive integer.  " +
                "Version number forced to 1.\n",

        /*  22 -- ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN */
        "The type of the value supplied for the a-list entry is " +
                "inconsistant with that entry.\n",

        /*  23 -- EMPTY_QUOTE_STRING_WARN */
        "Encountered an empty quote string where a non-empty quote " +
                "string is required.\n",

        /*  24 -- NON_POSITIVE_ERROR_LIMIT_WARN */
        "The error limit in the MAX-ERRORS> attribute must be positive.  " +
                "Error limit forced to 1.\n",

        /*  25 -- ERROR_LIMIT_TOO_LARGE_WARN */
        "The error limit in the MAX-ERRORS> attribute is too large.  " +
                "Error limit forced to largest legal value.\n",

        /*  26 -- NON_POSITIVE_WARNING_LIMIT_WARN */
        "The warning limit in the MAX-WARNINGS> attribute must be positive.  " +
                "Warning limit forced to 1.\n",

        /*  27 -- WARNING_LIMIT_TOO_LARGE_WARN */
        "The warning limit in the MAX-WARNINGS> attribute is too large.  " +
                "Warning limit forcedto largest legal value.\n",

        /*  28 -- NEGATIVE_DEBUG_LEVEL_WARN */
        "The debug level in the DEBUG-LEVEL> attribute must be non-negative.  " +
                "Debug level forced to 0.\n",

        /*  29 -- MISSING_ALIST_ENTRY_WARN */
        "A required a-list entry appears to be missing.  Will attempt to " +
                "proceed with a default value.  Errors may result.\n",

        /*  30 -- ALIEN_LIST_IN_PRED_DEFS_LIST_WARN */
        "Encountered a list that is not a predicate definition in the " +
                "predicate definitions list.  List discarded.\n",

        /*  31 -- ALIEN_ATOM_IN_PRED_DEFS_LIST_WARN */
        "Encountered an atom in the predicate definitions list. " +
                "The atom was discarded.\n",

        /*  32 -- EMPTY_PRED_DEF_WARN */
        "Encountered a predicate definition that appears to be missing its " +
                "a-list.  The definition will be ignored.\n",

        /*  33 -- TYPE_MISMATCH_IN_PRED_DEF_WARN */
        "Type mis-match in the second item in a predicate definition.  This " +
                "item must be an attribute list describing the predicate.\n",

        /*  34 -- EXCESS_VALUES_IN_A_PREDICATE_DEF_WARN */
        "Encountered excess values in a predicate definition.  " +
                "The excess values were discarded.\n",

        /*  35 -- NON_FARG_IN_FARG_LIST_WARN */
        "Encountered an atom or list in a formal argument list that isn't a " +
                "formal argument.The atom or list will be discarded.\n",

        /*  36 -- EMPTY_FORMAL_ARGUMENT_LIST_WARN */
        "Encountered an empty formal argument list.\n",

        /*  37 -- NAME_IN_PRED_DEF_NOT_A_PRED_NAME_WARN */
        "Encountered a predicate definition in which the name of the " +
                "predicate is not a validpredicate name.\n",

        /*  38 -- PREDICATE_REDEFINITION_WARN */
        "Encountered a duplicate definition of a predicate.  The second " +
                "definition will be ignored.\n",

        /*  39 -- ALIEN_LIST_IN_S_VAR_DEC_LIST_WARN */
        "Encountered a list that is not a spreadsheet variable declarations " +
                "in the spreadsheet variables declarations list.  " +
                "List discarded.\n",

        /*  40 -- ALIEN_ATOM_IN_S_VAR_DEC_LIST_WARN */
        "Encountered an atom in the spreadsheet variable declarations list. " +
                "The atom was discarded.\n",

        /*  41 -- NAME_IN_S_VAR_DEC_NOT_A_S_VAR_NAME_WARN */
        "Encountered a spreadsheet variable declaration in which the name " +
                "of the spreadsheet variable is not a valid spreadsheet " +
                "variable name.\n",

        /*  42 -- EMPTY_S_VAR_DEC_WARN */
        "Encountered a spreadsheet variable declaration that appears to be " +
                "missing its a-list.  The declaration will be ignored.\n",

        /*  43 -- TYPE_MISMATCH_IN_S_VAR_DEC_WARN */
        "Type mis-match in the second item in a spreadsheet variable " +
                "declaration.  This item must be an attribute list " +
                "describing the spreadsheet variable.\n",

        /*  44 -- EXCESS_VALUES_IN_A_S_VAR_DEC_WARN */
        "Encountered excess values in a spreadsheet variable declaration.  " +
                "The excess values were discarded.\n",

        /*  45 -- BAD_FARG_IN_SVAR_FARG_LIST_WARN */
        "Illegal formal argument name in a spreadsheet variable formal " +
                "argument list.\n",

        /*  46 -- INSUF_FARGS_IN_SVAR_FARG_LIST_WARN */
        "The formal argument list of a spreadsheet variable must contain " +
                "at least four arguments.\n",

        /*  47 -- COL_WIDTH_OUT_OF_RANGE_WARN */
        "The integer value associated with a COLUMN-WIDTH> attribute is out " +
                "of range.  The value will be forced to the nearest legal value.\n",

        /*  48 -- S_VAR_TYPE_ARG_LIST_MISMATCH_WARN */
        "Attempt to declare a non matrix spreadsheet variable with a formal " +
                "argument list that can only appear in a matrix spreadsheet " +
                "variable.\n",

        /*  49 -- S_VAR_PRED_NAME_COLLISION_WARN */
        "Attempt to declare a spreadsheet variable with the same name as a " +
                "previously definedpredicate detected.\n",

        /*  50 -- S_VAR_REDEFINITION_WARN */
        "Encountered a duplicate declaration of a spreadsheet variable.  " +
                "The second declaration will be ignored.\n",

        /*  51 -- VAR_LEN_NON_MATRIX_S_VAR_DEC_WARN */
        "Attempt to declare a variable length spreadsheet variable that is " +
                "not of <<MATRIX>> type.  Spreadsheet variable forced to " +
                "fixed length.\n",

        /*  52 -- DUP_FARG_WARN */
        "Encountered duplicate mention of a single formal argument name in a " +
                "single formal argument list.  Duplicate formal argument " +
                "will be ignored.\n",

        /*  53 -- ALIEN_LIST_IN_S_VAR_DEF_LIST_WARN */
        "Encountered a list that is not a spreadsheet variable definition in " +
                "the spreadsheet variables definitions list.  List discarded.\n",

        /*  54 -- ALIEN_ATOM_IN_S_VAR_DEF_LIST_WARN */
        "Encountered an atom in the spreadsheet variable definitions list.  " +
                "The atom was discarded.\n",

        /*  55 -- NAME_IN_S_VAR_DEC_NOT_A_S_VAR_NAME_WARN */
        "Encountered a spreadsheet variable definition in which the name of " +
                "the spreadsheet variable is not a valid spreadsheet " +
                "variable name.\n",

        /*  56 -- UNDECLARED_S_VAR_WARN */
        "Encountered reference to an undeclared spreadsheet variable.\n",

        /*  57 -- EMPTY_S_VAR_DEF_WARN */
        "Encountered a spreadsheet variable definition that appears to be " +
                "missing its a-list. The definition will be ignored.\n",

        /*  58 -- TYPE_MISMATCH_IN_S_VAR_DEF_WARN */
        "Type mis-match in the second item in a spreadsheet variable " +
                "definition.  This item must be an attribute list defining " +
                "the spreadsheet variable.\n",

        /*  59 -- EXCESS_VALUES_IN_A_S_VAR_DEF_WARN */
        "Encountered excess values in a spreadsheet variable definition.  " +
                "The excess values were discarded.\n",

        /*  60 -- INAPROPRIATE_VOCAB_ATTRIBUTE_WARN */
        "Encountered VOCAB> attribute in the definition of a spreadsheet " +
                "variable that is notof either <<MATRIX>> or " +
                "<<PREDICATE>> type.\n",

        /*  61 -- ATOM_IN_A_S_VAR_CELL_LIST_WARN */
        "Encountered an atom in a spreadsheet variable cell list.  " +
                "The atom will be discarded.\n",

        /*  62 -- UNKNOWN_OR_OUT_OF_ORDER_CELL_VALUE_WARN */
        "Encountered an undefined or out of order spreadsheet variable cell " +
                "argument value.  The value will be discarded.\n",

        /*  63 -- TIME_OUT_OF_RANGE_WARN */
        "Encountered a time that falls outside the range of values " +
                "representable by MacSHAPA. The time will be forced to the " +
                "nearest legal value.\n",

        /*  64 -- S_VAR_CELL_VALUE_TYPE_MISMATCH_WARN */
        "Encountered a cell value that does not match the type of " +
                "spreadsheet variable in which the cell occurs.\n",

        /*  65 -- S_VAR_CELL_VALUE_MISSING_WARN */
        "Encountered a cell value whose value appears to be missing.\n",

        /*  66 -- FARG_NAME_MISMATCH_WARN */
        "Encountered a formal argument whose name does not match the " +
                "expected formal argumentname.\n",

        /*  67 -- NOMINAL_COERCED_TO_CELL_NOMINAL_WARN */
        "Nominals that appear as the value of cells in nominal spreadsheet " +
                "variables cannot contain the characters '|' and ':'.\n",

        /*  68 -- INVALID_PRED_NAME_IN_PRED_VALUE_WARN */
        "Encountered a predicate value with an invalid predicate name.\n",

        /*  69 -- UNDEFINED_PRED_WARN */
        "Encountered a predicate value that refers to an undefined predicate.  " +
                "The predicate value will be discarded.\n",

        /*  70 -- PRED_VALUE_EXPECTED_WARN */
        "Encountered a list that does not describe a predicate value where " +
                "a predicate value was expected.  The list will be discarded.\n",

        /*  71 -- ILLEGAL_CHARS_IN_QUOTE_STR_WARN */
        "Illegal characters encountered in a quote string appearing in a " +
                "matrix or predicate argument.  The illegal characters were " +
                "replaced with spaces.\n",

        /*  72 -- PRED_OR_COL_VAR_USED_AS_NOM_WARN */
        "Attempt to use a declared predicate or column variable name as a " +
                "nominal.  Will discard the name and use the appropriate " +
                "formal argument in its place.\n",

        /*  73 -- ILLEGAL_ATOM_IN_PRED_ARG_LIST_WARN */
        "Encountered an illegal argument in a predicate value argument list.  " +
                "Will replace the argument with the associated formal argument.\n",

        /*  74 -- EXCESS_ARGS_IN_PRED_VALUE_WARN */
        "Encountered excess arguments in a predicate value.  " +
                "The excess arguments were discarded.\n",

        /*  75 -- REQ_ARGS_MISSING_FROM_PRED_VAL_WARN */
        "Encountered predicate value which is missing one or more required " +
                "arguments.  These argument(s) will be filled in with formal " +
                "arguments.\n",

        /*  76 -- ILLEGAL_ATOM_IN_MATRIX_ARG_WARN */
        "Encountered an illegal argument in a matrix spreadsheet cell.  Will " +
                "replace the argument with the associated formal argument.\n",

        /*  77 -- REQ_ARGS_MISSING_FROM_MATRIX_WARN */
        "Encountered matrix spreadsheet cell which is missing one or more " +
                "required arguments. These argument(s) will be filled in with " +
                "formal arguments.\n",

        /*  78 -- CELL_WITH_UNDEFINED_VALUE_WARN */
        "Encountered a spreadsheet cell whose value is undefined.\n",

        /*  79 -- CELL_WITH_UNDEFINED_ONSET_WARN */
        "Encountered a spreadsheet cell whose onset is undefined.  Will force " +
                "onset to 00:00:00:00.\n",

        /*  80 -- CELL_WITH_UNDEFINED_OFFSET_WARN */
        "Encountered a spreadsheet cell whose offset is undefined.  Will force " +
                "offset to 00:00:00:00.\n",

        /*  81 -- NON_PRED_IN_VOCAB_WARN */
        "Encountered a item in a vocabulary list that is not a predicate " +
                "name.  The entry will be ignored.\n",

        /*  82 -- UNDEF_PRED_IN_VOCAB_LIST_WARN */
        "Encountered reference to an undefined predicate in a vocabulary " +
                "list.  The undefinedpredicate will be ignored.\n",

        /*  83 -- S_VAR_IN_VOCAB_LIST_WARN */
        "Encountered reference to a spreadsheet variable in a vocabulary " +
                "list.  The spreadsheet variable will be ignored.\n",

        /*  84 -- SYSTEM_PRED_IN_VOCAB_LIST_WARN */
        "Encountered reference to a system predicate in a vocabulary " +
                "list.  The system predicate will be ignored.\n",

        /*  85 -- ATOM_IN_QUERY_LIST_WARN */
        "Encountered an atom in the query list.  The atom will be discarded.\n",

        /*  86 -- INVALID_S_VAR_NAME_IN_SP_VAR_LIST_WARN */
        "Encountered an invalid spreadsheet variable name in the list " +
                "associated with the SYSTEM> SHAPA-PANE-VARS> attribute.\n",

        /*  87 -- REF_TO_UNDEF_S_VAR_IN_SP_VAR_LIST_WARN */
        "Encountered reference to an undefined spreadsheet variable in the " +
                "list associated with the SYSTEM> SHAPA-PANE-VARS> attribute.\n",

        /*  88 -- REF_TO_SYSTEM_S_VAR_IN_SP_VAR_LIST_WARN */
        "Encountered reference to a system spreadsheet variable in the list " +
                "associated with the SYSTEM> SHAPA-PANE-VARS> attribute.\n",

        /*  89 -- DUP_REF_TO_S_VAR_IN_SP_VAR_LIST_WARN */
        "Encountered duplicate reference to a spreadsheet variable in the list " +
                "associated with the SYSTEM> SHAPA-PANE-VARS> attribute.\n",

        /*  90 -- NON_S_VAR_IN_SHAPA_VARS_LIST_WARN */
        "Encountered a non spreadsheet variable name in the list associated " +
                "with the SYSTEM> SHAPA-PANE-VARS> attribute.  The item will be ignored.\n",

        /*  91 -- ATOM_IN_GROUPS_LIST_WARN */
        "Encountered an atom in the groups list associated with the SYSTEM> " +
                "GROUPS> attribute.  The atom will be ignored.\n",

        /*  92 -- ATOM_IN_GROUP_LIST_WARN */
        "Encountered an atom in one of the lists of group elements associated " +
                "with the SYSTEM> GROUPS> attribute.  The atom will be ignored.\n",

        /*  93 -- INVALID_S_VAR_NAME_IN_GROUP_MEMBER_WARN */
        "Encountered an invalid spreadsheet variable name in a group member " +
                "in the value of the SYSTEM> GROUPS> attribute.\n",

        /*  94 -- REF_TO_UNDEF_S_VAR_IN_GROUP_MEMBER_WARN */
        "Encountered reference to an undefined spreadsheet variable in a  " +
                "groupd member in thevalue of the SYSTEM> GROUPS> attribute.\n",

        /*  95 -- REF_TO_SYS_S_VAR_IN_GROUP_MEMBER_WARN */
        "Encountered reference to a system spreadsheet variable in a group " +
                "member in the value of the SYSTEM> GROUPS> attribute.\n",

        /*  96 -- ILL_FORMED_GROUP_MEMBER_WARN */
        "Encountered an ill formed group member in a group list in the value " +
                "of the SYSTEM> GROUPS> attribute.  Group members must be of " +
                "the form '(' <s_var_name> <cell_ord> ')'.\n",

        /*  97 -- REF_TO_UNDEF_S_VAR_CELL_IN_GRP_MEM_WARN */
        "Encountered a group member which refers to a non-existant " +
                "spreadsheet variable cell. The group member is in the " +
                "value of the SYSTEM> GROUPS> attribute.\n",

        /*  98 -- EXCESS_VALUES_IN_GROUP_MEMBER_WARN */
        "Encountered excess arguments in a group member in a group list in " +
                "the value of the SYSTEM> GROUPS> attribute.  The excess " +
                "arguments were discarded.\n",

        /*  99 -- ATOM_IN_ALIGNMENTS_LIST_WARN */
        "Encountered an atom in the aligments list in the SYSTEM> " +
                "ALIGNMENTS> attribute.  Theatom will be ignored.\n",

        /* 100 -- ALIEN_LIST_IN_ALIGNMENTS_LIST_WARN */
        "Encountered a list that is not an alignment in the alignments list " +
                "in the SYSTEM> ALIGNMENTS> attribute.  List discarded.\n",

        /* 101 -- INVALID_ALIGNMENT_NAME_WARN */
        "Encountered an alignment in the SYSTEM> ALIGNMENTS> attribute whose " +
                "name is invalid. The alignment will be discarded.\n",

        /* 102 -- ALIGNMENT_NAME_ALREADY_IN_USE_WARN */
        "Encountered an alignment whose name is already in use as a " +
                "predicate and/or spreadsheet variable name.  " +
                "The alignment will be discarded.\n",

        /* 103 -- EMPTY_ALIGNMENT_WARN */
        "Encountered an alignment that appears to be missing its a-list.  " +
                "The alignment will be ignored.\n",

        /* 104 -- TYPE_MISMATCH_IN_ALIGNMENT_WARN */
        "Type mis-match in the second item in an alignment.  " +
                "This item must be an attribute list describing the alignment.\n",

        /* 105 -- EXCESS_VALUES_IN_AN_ALIGNMENT_WARN */
        "Encountered excess values in an alignment.  " +
                "The excess values were discarded.\n",

        /* 106 -- ILL_FORMED_ALIGNMENT_WARN */
        "Encountered an alignment that appears to be ill formed.  " +
                "The alignment will be discarded.\n",

        /* 107 -- ATOM_IN_IMPORT_FORMATS_LIST_WARN */
        "Encountered an atom in the import formats list in the value of " +
                "the SYSTEM> IMPORT-FORMATS-LIST> attribute.  " +
                "The atom will be ignored.\n",

        /* 108 -- ILLEGAL_IMPORT_FORMAT_LIST_NAME_WARN */
        "Encountered an import format with an illegal name.  " +
                "The import format will be ignored.\n",

        /* 109 -- EMPTY_IMPORT_FORMAT_LIST_WARN */
        "Encountered an import format list whose value appears to be missing.  " +
                "The import format list will be ignored.\n",

        /* 110 -- IMPORT_FORMAT_LIST_TYPE_MISMATCH_WARN */
        "Encountered an import format list whose value is an atom instead " +
                "of the required a-list.  The import format list will " +
                "be ignored.\n",

        /* 111 -- EXCESS_VALUES_IN_IMP_FORMAT_LIST_WARN */
        "Encountered excess value(s) in an import format list in the value " +
                "of the SYSTEM> IMPORT-FORMATS-LIST> attribute.  " +
                "The excess values were discarded.\n",

        /* 112 -- ATOM_IN_IMPORT_PRODUCTIONS_LIST_WARN */
        "Encountered an atom in an import productions list in an import " +
                "format.  The atom will be ignored.\n",

        /* 113 -- BOTH_PGM_AND_INSERT_ACTION_IN_PROD_WARN */
        "Encountered both an INSERT-ACTIONS> attribute and a PROGRAM-ACTIONS> " +
                "attribute in animport production.\n",

        /* 114 -- IMPORT_PROD_NAME_TOO_LONG_WARN */
        "Encountered an import production whose name is too long.  " +
                "The import production willbe ignored.\n",

        /* 115 -- IMPORT_PROD_PATTERN_TOO_LONG_WARN */
        "Encountered an import production whose pattern string is too long.  " +
                "The import production will be ignored.\n",

        /* 116 -- ATOM_IN_IMP_PROD_INS_ACTIONS_LIST_WARN */
        "Encountered an atom in an import production insert actions list.  " +
                "The atom will be ignored.\n",

        /* 117 -- ATOM_IN_IMP_PROD_PMG_ACTIONS_LIST_WARN */
        "Encountered an atom in an import production program actions list.  " +
                "The atom will be ignored.\n",

        /* 118 -- MISSING_REQ_ATTR_IN_IMP_PROD_WARN */
        "Encountered an import production which is missing one or more " +
                "required arguments.  The import production will be discarded.\n",

        /* 119 -- ALIGNMENTS_AFTER_SHAPA_PANE_VARS_WARN */
        "Encountered ALIGNMENTS> attribute after the SHAPA-PANE-VARS> " +
                "attribute in the SYSTEM> section.  If both appear, the " +
                "ALIGNMENTS> attribute must appear first.\n",

        /* 120 -- EMPTY_IMPORT_PROD_INS_ACTION_LIST_WARN */
        "Encountered an empty insert action list in an import production.  " +
                "The import production will be discarded.\n",

        /* 121 -- EMPTY_IMPORT_PROD_PGM_ACTION_LIST_WARN */
        "Encountered an empty program action list in an import production.  " +
                "The import production will be discarded.\n",

        /* 122 -- MISSING_REQ_ATTR_IN_INS_ACTION_WARN */
        "Encountered an import production insert action a-list which is " +
                "missing one or more required attributes.  The insert " +
                "action will be discarded.\n",

        /* 123 -- MISSING_REQ_ATTR_IN_PGM_ACTION_WARN */
        "Encountered an import production program action a-list which is " +
                "missing one or more required attributes.  " +
                "The program action will be discarded.\n",

        /* 124 -- EXCESS_IMP_PROD_PGM_ACTIONS_WARN */
        "Encountered an import production program action list with more " +
                "than one program action.  This upsets the import utility,  " +
                "and thus the excess actions will be discarded.\n",
        
        /* 125 -- COL_PRED_CANT_REPLACE_PRED_FARG_WARN */
        "Encountered a column being used as the value of a predicate cell.  " +
                "This is currently not permitted in OpenSHAPA.  The column " +
                "predicate value will be discarded.\n",
    
        /* 126 -- FARG_ARG_TYPE_MISMATCH_WARN */
        "Encountered type mismatch between argument and formal argument.  " +
                "Argument replaced with an undefined value.",

        /*  127 -- UNDEFINED_PRED_WARN */
        "Encountered a column predicate value that refers to an undefined " +
                "column.  The column predicate value will be discarded.\n",

        /*  128 -- INVALID_COL_PRED_NAME_IN_COL_PRED_VALUE_WARN */
        "Encountered a column predicate value with an invalid column " +
                "predicate name.\n",

        /*  129 -- COL_PRED_VALUE_EXPECTED_WARN */
        "Encountered a list that does not describe a column predicate value " +
                "where a column predicate value was expected.  " +
                "The list will be discarded.\n",

        /*  130 -- ILLEGAL_ATOM_IN_COL_PRED_ARG_LIST_WARN */
        "Encountered an illegal argument in a column predicate value argument " +
                "list.  Will replace the argument with the associated formal " +
                "argument.\n",

        /*  131 -- EXCESS_ARGS_IN_COL_PRED_VALUE_WARN */
        "Encountered excess arguments in a column predicate value.  " +
                "The excess arguments were discarded.\n",

        /*  75 -- REQ_ARGS_MISSING_FROM_COL_PRED_VAL_WARN */
        "Encountered column predicate value which is missing one or more " +
                "required arguments.  These argument(s) will be filled in " +
                "with formal arguments.\n"
    };

    
    /*************************************************************************/
    /************************** Type Definitions: ****************************/
    /*************************************************************************/
    
    /*************************************************************************
     * class Token 
     *
     * The Token class is used to store tokens as they are read from the data 
     * file by the lexical analyzer, and to pass tokens to the parser.  While 
     * Token is technically a class, we treat it more like a C structure. 
     * While it does have a few associated methods, for the most part the code
     * that uses intstances of the Token class just go in an read and modify
     * fields as desired. 
     * 
     * The fields in the class are discussed individually below.  They are
     * all public.
     *
     * reader:  Reference to the instance of macshapa_odb_reader in which this
     *          token resides.
     *
     * code:	Integer code indicating the type of the token.  The set of 
     *		allowable values for this field is equal to the set of token 
     *		constant definitions earlier in this file.
     *
     * aux:	Unsigned integer whose use depends on the value of the code 
     *		field.
     *
     *		If code contains either SYMBOL_TOK or STRING_TOK, aux contains 
     *		flags indicating the type(s) of symbol or string the token may 
     *		contain as a function of the characters that appear in the token.
     *
     *		If code contains either ALIST_LABEL_TOK or PRIVATE_VAL_TOK, aux
     *		contains a code indicating the a-list label or private value.
     *
     *		If code contains BOOL_TOK, aux contains the actual boolean value 
     *		of the token.
     *
     * str:	Reference to an instance of StringBuilder which contains a 
     *		text representation of the token.  Note that in the case of 
     *		numerical values, str need not agree with val, as str may 
     *		contain a text representation of an out of range value.  In 
     *		this case, val contains the nearest legal value.
     *
     * val:	Double containg any numerical value associated with the instance 
     *		of class Token.
     *
     *************************************************************************/
    
    class Token
    {
        macshapa_odb_reader reader = null;
        
	int code = ERROR_TOK; /* an initial, invalid value */
	
	int aux = 0;
	
	StringBuilder str = null;
	
	double val = 0.0;


	/*********************************************************************
	 *
	 * Token()
	 *
	 * Constructor for an new instance of Token.  This method calls the 
         * super class constructor, and sets the reader field
	 *
	 *                                                 - 12/14/08
	 *
	 * Parameters:
	 *
	 *    - None.
	 *
	 * Returns:  Void
	 *
	 * Changes:
	 *
	 *    - None.
	 *
	 **********************************************************************/
        
        protected Token(macshapa_odb_reader reader)
            throws SystemErrorException
        {
            super();
         
            final String mName = "Token::Token(reader): ";
            
            if ( reader == null )
            {
                throw new SystemErrorException(mName + "reader null on entry");
            }
            
            this.reader = reader;
        
        } /* Token::Token(reader) */
        

	/*********************************************************************
	 *
	 * clip_numeric_token_string()
	 *
	 * If a numeric token is out of range, read_numeric_token() forces
	 * the val field of the token to the nearest legal value.  However, we
	 * must also force the string assocated with the token to a legal value.
	 * This method tends to this matter.
	 *
	 *                                                 - 6/3/08
	 *
	 * Parameters:
	 *
	 *    - None.
	 *
	 * Returns:  Void
	 *
	 * Changes:
	 *
	 *    - None.
	 *
	 **********************************************************************/

	public void clip_numeric_token_string()
	    throws SystemErrorException
	{
            final String mName = "Token::clip_numeric_token_string(): ";

	    if ( this.str == null )
	    {
		throw new SystemErrorException(mName + "str null on entry.");
	    }
	    
	    switch ( this.code )
	    {
		case INT_TOK:
		    this.str.delete(0, this.str.length() - 1);
		    this.str.append(((long)(this.val)));
		    break;
		    
		case FLOAT_TOK:
		    this.str.delete(0, this.str.length() - 1);
		    this.str.append(this.val);
		    break;
		    
		default:
		    throw new SystemErrorException(mName +
			                           "token isn't numeric.");
		    
	    }

	    return;

	} /* Token::clip_numeric_token_string() */
        

        /*******************************************************************
         *
         * coerce_float_token_to_integer()
         *
         * Coerce a floating point token to an integer token, coercing the 
         * value to the nearest legal integer value.
         *
         *						 - 7/29/08
         *
         * Parameters:
         *
         *    - None.
         *
         * Return Value:  void.
         *
         * Changes:
         *
         *    - None.
         *
         *******************************************************************/

        private long coerce_float_token_to_integer()
            throws SystemErrorException
        {
            final String mName = "Token::coerce_float_token_to_integer()";
            double sign;
            double value;
            double integer;
            double frac;

            if ( this.reader.abort_parse )
            {
                throw new SystemErrorException(mName + 
                        "read_vars->abort_parse TRUE on entry.");
            }
            
            if ( this.code != FLOAT_TOK )
            {
                throw new SystemErrorException(mName + 
                        "token_ptr->code isnt FLOAT_TOK on entry.");
            }
            
            /* do the coersion */
            
            if ( this.val < 0.0 )
            {
                    sign = -1.0;
            }
            else
            {
                    sign = 1.0;
            }

            value = java.lang.Math.abs(this.val);
            integer = java.lang.Math.floor(value);
            frac = value - integer;

            if ( frac > 0.5 )
            {
                integer += 1.0;
            }

            integer *= sign;

            if ( integer > (double)LONG_MAX )
            {
                integer = (double)LONG_MAX;
            }
            else if ( integer < (double)LONG_MIN )
            {
                integer = (double)LONG_MIN;
            }

            this.code = INT_TOK;
            this.val  = integer;

            clip_numeric_token_string();

            return (long)this.val;

        } /* Token::coerce_float_token_to_integer() */
        

        /*********************************************************************
         *
         * coerce_nominal_token_to_cell_nominal()
         *
         * Coerce the string associated with a symbol token to a spreadsheet 
         * cell nominal.  At present characters '|' and ':' are illegal in 
         * nominals that appear in nominal spreadsheet variables but not in 
         * nominals that appear in matrix and predicate arguments.   
         * 
         * This is an inconsistance in MacSHAPA that should be fixed, but for 
         * the nonce, we must support it.
         *
         * This is done by overwriting illegal characters in this.str with '_' 
         * characters.
         *
         *						 - 7/30/08
         *
         * Parameters:
         *
         *    - None.
         *
         * Return Value:  true if the nominal was altered, and false otherwise.
         *
         *  Changes:
         *
         *	  - None.
         *
         *********************************************************************/

        protected boolean coerce_nominal_token_to_cell_nominal()
            throws SystemErrorException
        {
                final String mName = 
                        "Token::coerce_nominal_token_to_cell_nominal()";
                boolean nominal_altered;
                int i;

                nominal_altered = false;

                if ( this.code != SYMBOL_TOK )
                {
                    throw new SystemErrorException(mName + 
                            "this.code isnt SYMBOL_TOK on entry.");
                }
                
                if ( (this.aux & NOMINAL_FLAG) == 0 )
                {
                    throw new SystemErrorException(mName + 
                            "((token_ptr->aux) & NOMINAL_FLAG) == 0.");
                }
                
                /* do the coersion */
                
                for ( i = 0; i < this.str.length(); i++ )
                {
                    switch ( this.str.charAt(i) )
                    {
                        case '|':
                        case ':':
                            this.str.setCharAt(i, '_');
                            nominal_altered = true;
                            break;

                        default:
                            /* do nothing */
                            break;
                    }
                }
                

                return(nominal_altered);

        } /* coerce_nominal_token_to_cell_nominal() */
        

        /*********************************************************************
         *
         * coerce_text_qstring_to_qstring()
         *
         * Coerce the text quote string associated with a string token to a
         * quote string.
         *
         * This is done by overwriting illegal characters in this.str with 
         * ' ' characters.
         *
         * Parameters:
         *
         *    - None.
         *
         * Returns:  Void.
         *
         * Changes:
         *
         *    - None.
         *
         *********************************************************************/

        private void coerce_text_qstring_to_qstring()
            throws SystemErrorException
        {
            final String mName = "Token::coerce_text_qstring_to_qstring()";
            char ch;
            int i;
	
            if ( this.code != STRING_TOK )
            {
                throw new SystemErrorException(mName + 
                        "token_ptr->code isnt STRING_TOK on entry.");
            }
            else if ( (this.aux & TEXT_QSTRING_FLAG) != 0 )
            {
                throw new SystemErrorException(mName + 
                        "token already contains a predicate name.");
            }
            else /* do the coersion */
            {
                for ( i = 0; i < this.str.length(); i++ )
                {
                    ch = this.str.charAt(i);
                    
                    if ( ( ch < 0x20 ) || ( ch > 0x7E ) || ( ch == '\"' ) )
                    {
                        this.str.setCharAt(i, ' ');
                    }
                }

                this.aux |= TEXT_QSTRING_FLAG;
            }

            return;

        } /* Token::coerce_text_qstring_to_qstring() */

        
        /*********************************************************************
         *
         * coerce_symbol_token_to_pred_name()
         *
         * Coerce the string associated with a symbol token to a predicate 
         * name.
         *
         * This is done by overwriting illegal characters in this.str with 
         * '_' characters.
         *
         * Parameters:
         *
         *    - None.
         *
         * Returns:  Void.
         *
         * Changes:
         *
         *    - None.
         *
         *********************************************************************/

        private void coerce_symbol_token_to_pred_name()
            throws SystemErrorException
        {
            final String mName = 
                    "Token::coerce_symbol_token_to_pred_name()";
            int i;
	
            if ( this.code != SYMBOL_TOK )
            {
                throw new SystemErrorException(mName + 
                        "token_ptr->code isnt SYMBOL_TOK on entry.");
            }
            else if ( (this.aux & PRED_FLAG) != 0 )
            {
                throw new SystemErrorException(mName + 
                        "token already contains a predicate name.");
            }
            else /* do the coersion */
            {
                for ( i = 0; i < this.str.length(); i++ )
                {
                    switch ( this.str.charAt(i) )
                    {
                        case '<':
                        case '>':
                        case ' ':
                            this.str.setCharAt(i, '_');
                            break;

                        default:
                            /* do nothing */
                            break;
                    }
                }

                this.aux = COLUMN_FLAG | NOMINAL_FLAG | PRED_FLAG;
            }

            return;

        } /* Token::coerce_symbol_token_to_pred_name() */

        
        /**********************************************************************
         *
         * coerce_symbol_token_to_spreadsheet_variable_name()
         *
         * Coerce the string associated with a symbol token to a spread sheet
         * variable name.
         *
         * This is done by overwriting illegal characters in this.str 
         * with '_' characters.
         *
         *						 - 7/26/08
         *
         * Parameters:
         *
         *    - None.
         *
         * Return Value:  Void.
         *
         * Changes:
         *
         *    - None.
         *
         **********************************************************************/

        private void coerce_symbol_token_to_spreadsheet_variable_name()
            throws SystemErrorException
        {
            final String mName = 
                    "Token::coerce_symbol_token_to_spreadsheet_variable_name()";
            Boolean could_be_pred;
            int i;

            if ( this.code != SYMBOL_TOK )
            {
                throw new SystemErrorException(mName + 
                        "token_ptr->code isnt SYMBOL_TOK on entry.");
            }
            else if ( (this.aux & COLUMN_FLAG) != 0 )
            {
                throw new SystemErrorException(mName + 
                        "token already contains a spreadsheet variable name.");
            }
            else /* do the coersion */
            {
                could_be_pred = true;

                for ( i = 0; i < this.str.length(); i++ )
                {
                    switch ( this.str.charAt(i) )
                    {
                        case '<':
                        case '>':
                            this.str.setCharAt(i, '_');
                            break;

                        case ' ':
                            could_be_pred = false;
                            break;

                        default:
                            /* do nothing */
                            break;
                    }
                }

                this.aux = COLUMN_FLAG | NOMINAL_FLAG;

                if ( could_be_pred )
                {
                    this.aux |= PRED_FLAG;
                }
            }

            return;

        } /* Token::coerce_symbol_token_to_spreadsheet_variable_name() */

	
	/*********************************************************************
	 * save_char_to_token()
	 *
	 * The save_char_to_token() method adds new_char to the instance of
	 * StringBuilder referenced by this.str.  If this.str is null, 
	 * initialize it with the enpty string and then append new_char to it.
	 *
	 *					 - 6/3/08
	 *
	 * Parameters:
	 *
	 *    - new_char:  Char containing the new character to be added to 
	 *	the token.
	 *
	 * Changes:
	 *
	 *    - None
	 *
	 ********************************************************************/

	public void save_char_to_token(char new_char)
	{
	    if ( this.str == null )
	    {
		this.str = new StringBuilder("");
	    }
	    
	    this.str.append(new_char);
	    
	    return;
	    
        } /* Token::save_char_to_token() */

	
	/*********************************************************************
	 *
	 * toString()
	 *
	 * Debugging routine that returns the contents of an instance of 
	 * Token rpresented in a string.
	 * 
	 *					     - 6/4/08
	 *
	 * Parameters:
	 *
	 *    - None.
	 *
	 * Returns:  
	 * 
	 *    - String containing text representation of the token.
	 *
	 *  Changes:
	 *
	 *        - None.
	 *
	 *******************************************************************************/

	public String toString()
	{
	    final String token_code_names[] =
	    {
		"ERROR_TOK",
		"L_PAREN_TOK",
		"R_PAREN_TOK",
		"SYMBOL_TOK",
		"INT_TOK",
		"FLOAT_TOK",
		"STRING_TOK",
		"BOOL_TOK",
		"ALIST_LABEL_TOK",
		"PRIVATE_VAL_TOK",
		"SETF_TOK",
		"DB_VAR_TOK",
		"QUOTE_TOK",
		"EOF_TOK"
	    };
	    final String a_list_labels[] =
	    {
		"ALIGNMENTS_LABEL",
		"CELLS_LABEL",
		"COLUMN_WIDTH_LABEL",
		"FORMAL_ARG_LIST_LABEL",
		"GROUPS_LABEL",
		"HARD_PREC_LABEL",
		"HEADER_LABEL",
		"IMPORT_FORMAT_LISTS_LABEL",
		"IMPORT_PRODUCTION_LIST_LABEL",
		"INSERT_ACTIONS_LABEL",
		"IS_SHOWN_LABEL",
		"IS_TRACED_LABEL",
		"NAME_LABEL",
		"OFFSET_LABEL",
		"ONSET_LABEL",
		"PATTERN_LABEL",
		"PREDICATE_DEFINITIONS_LABEL",
		"PROGRAM_ACTIONS_LABEL",
		"QUERY_LABEL",
		"SHAPA_PANE_VARS_LABEL",
		"SPREADSHEET_VARIABLE_DECLARATIONS_LABEL",
		"SPREADSHEET_VARIABLE_DEFINITIONS_LABEL",
		"SYSTEM_LABEL",
		"TEXT_LABEL",
		"TIME_LABEL",
		"TYPE_LABEL",
		"USER_LABEL",
		"VAL_LABEL",
		"VAR_LABEL",
		"VARIABLE_LENGTH_LABEL",
		"VERSION_LABEL",
		"VOCAB_LABEL",
		"UNKNOWN_ALIST_LABEL"
	    };
	    final String pval_names[] =
	    {
		"FLOAT_PVAL",
		"INTEGER_PVAL",
		"MATRIX_PVAL",
		"NOMINAL_PVAL",
		"PREDICATE_PVAL",
		"TEXT_PVAL",
		"UNKNOWN_PRIVATE_VALUE"
	    };
	    String retVal= null;

	    if ( ( this.code >= 0 ) && ( this.code <= MAX_TOKEN_CODE ) )
	    {
		retVal = new String("((code = " + this.code + "(" +
			            token_code_names[this.code] + "))");
	    }
	    else
	    {
		retVal = new String("((code = " + this.code + "(Undefined))");
	    }
		

	    /* dump the aux field */
	    switch (this.code)
	    {
		case ALIST_LABEL_TOK:
		    if ( ( this.aux >= 0 ) &&( this.aux <= NUMBER_OF_ALIST_LABELS ) )
		    { 
			retVal += " (aux = " + this.aux + "(" +
				  a_list_labels[this.aux] + "))";
		    }
		    else
		    {
			retVal += " (aux = " + this.aux + 
				  "(Unknown label code))";
		    }
		    break;

		case PRIVATE_VAL_TOK:
		    if ( ( this.aux >= 0 ) &&
			 ( this.aux <= NUMBER_OF_PRIVATE_VALUES ) )
		    {
			retVal += " (aux = " + this.aux + "(" + 
				  pval_names[this.aux] + "))";
		    }
		    else
		    {
			retVal += " (aux = " + this.aux + 
				  "(Unknown private value code))";
		    }
		    break;

		default:
		    retVal += " (aux = " + this.aux + ")"; 
		    break;
	    }
	    
	    /* dump the val fields */
	    retVal += " (val = " + this.val + ")";
	    
	    /* dump the string */
	    retVal += " (str = " + this.str + "))";

	    return retVal;

	} /* Token::toString() */
	
    }; /* class Token */

    
    /*************************************************************************/
    /******************************* Fields: *********************************/
    /*************************************************************************/
    
    /**
     * Disk input, file listing, and output related fields:
     *
     * input_stream: Reference to the input stream from which the open database 
     *              format MacSHAPA database is to be read.
     *
     * list_stream: Reference to the output stream to which the listing is to 
     *              be written, or null if no listing is desired.
     *
     * line:	    Reference to a String containing the last line read from 
     *		    from the input_file, or null if eof has been reached.
     *
     * line_number: long integer containing the number of the current line.  
     *		    This value is used to number the lines in the listing and 
     *		    in error and warning messages.
     *
     * line_len:    Integer containing the number of characters (including 
     *		    terminiating carriage return) in line, or zero if 
     *		    line is null, or has zero length.
     *
     * line_index:  Integer containing the index of the next character to be
     *		    read from line.  This field should always contain a value
     *		    in the range 0 to (line_len - 1) inclusive, or zero if the 
     *		    line is either empty or null.
     *
     * end_of_file: Boolean flag which is set to true after get_next_char() 
     *		    returns the last character in the input file.
     *
     * lookahead_char: Under certain circumstances, the lexical analyzer needs 
     *		    to be able to look at the next character to be read from 
     *		    the file without actually reading it.  The lookahead_char 
     *		    field always contains the next character that will be 
     *		    returned by the get next character function.  This field 
     *		    is initalized to ' ', which has the effect of inserting a 
     *		    blank at the beginning of every input file.  Due to the
     *		    syntax of the MacSHAPA open database file, this blank has 
     *		    no effect on the semantic content of the file.
     *
     * db:	    Instance of MacshapaDatabase into which the contents of the 
     *		    input file is to be loaded.
     *
     * odb_file_format_version: MacSHAPA ODB file format version number as read
     *		    from the input file, or -1 if the version hasn't been
     *		    read yet.
     */

    java.io.BufferedReader input_stream = null;

    java.io.PrintStream listing_stream = null;
    
    String line = null;
    
    long line_number = 0;
    
    int line_len = 0;
    
    int line_index = 0;
    
    boolean end_of_file = false;
    
    char lookahead_char = ' ';
    
    MacshapaDatabase db;
    
    int odb_file_format_version = -1;
    
    
    /**
     * Lexical Analysis and Parser related fields:
     *
     * While it would be possible to rework the grammar for the open database 
     * file format so that we could get away with a one token lookahead in all 
     * cases, the resulting grammar would be considerably more complex.  To 
     * avoid this complexity, we must pay the price of a two token lookahead.  
     * This is implemented by maintaining a list of the next three tokens in 
     * the input stream.  
     * 
     * Each time the parser requests a new token, the lookahead one token 
     * becomes the current token, the lookahead two token becomes the lookahead 
     * one token, and a new token is read into the lookahead two token.
     *
     * l0_tok:  Reference to an instance of class Token.  This instance contains 
     *		the "current" token.
     *
     * l1_tok:  Reference to an instance of class Token.  This instance contains 
     *		the lookahead one token.
     *
     * l2_tok:  Reference to an instance of class token.  This instance contains 
     *		the lookahead two token.
     *
     * in_query: Boolean flag that is set to true when we are parsing the 
     *          query variable.
     */
    
    Token l0_tok = null;
    
    Token l1_tok = null;
    
    Token l2_tok = null;
    
    boolean in_query = false;
    
    
    /**
     * Error and Warning related fields:
     *
     * Before listing the fields in this section, a brief discussion of 
     * warnings and errors is in order.
     * 
     * For the purposes of this application, a warning is issued when the parser
     * detects an error in the input file, but is able to recover from the error
     * without aborting the database load.  Note that the recovery may require
     * discarding data.
     * 
     * In contrast, an error is reported when an error is detected in the input
     * file, and the parser is unable to continue loading data from the file.  
     * In the initial implementation, this means that the parse will be aborted 
     * as well.  However in future implementations that need not be the case.  
     * Hence this section contains fields for counts of warnings and errors, 
     * maximum warnings or errrors before aborting, and an abort flag, even 
     * though initially, I plan to abort the parse upon detection of the initial 
     * error (as opposed to warning).
     *
     * warning_count:  Integer field used to maintain a count of the number of
     *		warnings that have been issued.  For the record, warnings are 
     *		issued when the parser detects an error that it is able to 
     *		recover from.
     * 
     * max_warnings:  Integer field containing the maximum number of warnings 
     *		that the parser will issue before aborting the database load.
     *
     * error_count:  Integer field used to maintain a count of the number of 
     *		error messages that have been issued.  By definition, the file 
     *		load is aborted as soon as an error is detected, however in the 
     *		future we may wish to continue the parse in the hopes of 
     *		detecting further errors.  For now, however, the error count 
     *		should always be either zero or one.
     * 
     * max_errors:  Integer field containing the maximum number of errors that 
     *		may be reported before the file parse is aborted.  I expect that 
     *		this value will be one for some time, if not forever.  However I 
     *		can concieve the case in which we may wish to contunue the parse 
     *		of the database file, even though previous errors have made it 
     *		impossible to continue loading data into OpenSHAPA.  This field 
     *		exists to serve this purpose whenever and if ever.
     *
     * debug_level:  Debug level for the parse, or -1 if it hasn't been 
     *		specified yet.
     *
     * abort_scan:  Boolean field that is set to TRUE iff we have encountered an
     *		error so bad that we can't even continue to scan the input file 
     *		for invalid tokens (even if we get so confused that we can't 
     *		continue to parse the input file in any meaningful way, there is 
     *		in general no reason why we can't tokenize the remainder of the 
     *		file and thereby check the remainder of the file for errors that 
     *		can be detected by the lexer.)  In general this field is set to 
     *		TRUE upon either a fatal error, or when error_count exceeds 
     *		max_errors.  However it can also be set as a result of an 
     *		operator interupt (cmd-period).  Note that 
     * 
     *			abort_scan ==> abort_parse ==> abort_load.
     *
     * abort_parse:  Boolean field that is set to TRUE iff we have encountered 
     *		some situation that requires us to abandon the parse of the 
     *		input file.  In general, this will occur when we exceed the 
     *		error or warning limit.  However, it can also be occasioned by 
     *		a user interrupt (command period).  Note that 
     * 
     *			abort_parse ==> abort_load.
     * 
     * abort_load:  Boolean field that is set to TRUE iff we have encountered 
     *		some situation that requires us to stop loading data from the 
     *		input file.  logically, this need not prevent us from continuing 
     *		to parse the input file, looking for further errors.  However, 
     *		in the first cut implementation, I expect abort_parse and 
     *		abort_load to always contain the same value.  For now, this 
     *		field is an attempt to build in hooks to allow more sophisticated 
     *		handling of input file errors in the future.
     */

    int warning_count = 0;
	
    int max_warnings = 1;
	
    int error_count = 0;
	
    int max_errors = 1;
    
    int debug_level = -1;
	
    boolean abort_scan = false;;
	
    boolean abort_parse = false;
	
    boolean abort_load = false;
       
    
    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/
    
    /*********************************************************************
     *
     * macshapa_odb_reader()
     *
     * Constructor for an new instance of macshapa_odb_reader.  
     *
     *                                                 - 12/14/08
     *
     * Parameters:
     *
     *    - None.
     *
     * Returns:  Void
     *
     * Changes:
     *
     *    - None.
     *
     **********************************************************************/

    protected macshapa_odb_reader()
    throws SystemErrorException {
        super();

        this.db = new MacshapaDatabase();
        this.l0_tok = new Token(this);
        this.l1_tok = new Token(this);
        this.l2_tok = new Token(this);
        
    } /* macshapa_odb_reader::macshapa_odb_reader() */
     
        
    /*************************************************************************/
    /*********************** Error Logging Methods: **************************/
    /*************************************************************************/
    
    /*************************************************************************
     *
     * post_error_message()
     *
     * Post an error message to the listing and increment this.error_count.
     * Set this.abort_load to true.
     *
     * If this.error_count exceeds this.max_errors, set both this.abort_parse 
     * and this.abort_scan to true.
     *
     * If the parse_fatal parameter is true, set thisabort_parse to true.
     * If in addition the scan_fatal parameter is true, also set this.abort_scan 
     * to TRUE.
     *
     *                                               - 5/26/08
     *
     * Parameters:
     *
     * error_num:  Integer code indicate the error message to be posted.
     *
     * comment:  In some cases, we may wish to add to the error message
     *      indicated by the indicated by the error_num with a message composed
     *      at run time.  If so, comment refers to this addition.  If comment
     *      is null, the error message is posted without runtime comment.
     *      Note that the last character in *comment_ptr should be '\n'.
     *
     * scan_fatal:  Boolean flag indicating whether or not the error will force
     *      an immediate halt of the scan of the file.
     *
     * parse_fatal:  Boolean flag indicating whether or not the error will
     *      force and immediate end to all attempts to parse the file.  Note
     *      that even if we can't parse the file, we can continue to tokenize
     *      it, thus allowing us to find more errors in a single attempt to
     *      load the database.  Observe that scan_fatal implies parse_fatal,
     *      but not vise-versa.
     *
     *
     * Return Value:  void.
     *
     * Changes:
     *
     *    - None.
     *
     *************************************************************************/

    private void post_error_message(int error_num,
                                    String comment,
                                    boolean scan_fatal,
                                    boolean parse_fatal)
        throws SystemErrorException
    {
        final String mName = "macshapa_odb_reader::post_error_message(): ";

        if ( ( error_num < 0 ) || ( error_num >= NUMBER_OF_ERROR_MESSAGES ) )
        {
            throw new SystemErrorException(mName + "error_num out of range.");
        }
        else
        {
            this.abort_load = true;

            (this.error_count)++;
            
            if ( this.listing_stream != null )
            {
                this.listing_stream.printf("\n      ERROR %2d: %s", 
                        error_num, this.error_mssg_table[error_num]);
                
                if ( comment != null )
                {
                    this.listing_stream.printf("        %s", comment);
                }
            }


            if ( parse_fatal )
            {
                this.abort_parse = true;

                if ( ( scan_fatal ) || ( this.error_count >= this.max_errors ) )
                {
                    this.abort_scan = true;
                    
                    if ( this.listing_stream != null )
                    {
                        this.listing_stream.print(
                                "\nUnable to continue parse -- Aborting...\n");
                    }
                }
                else
                {
                    if ( this.listing_stream != null )
                    {
                        this.listing_stream.print(
                                "\n\nUnable to continue parse -- " +
                                "Will continue lexer scan.\n\n");
                    }
                }
            }
            else if ( this.error_count >= this.max_errors )
            {
                this.abort_scan  = true;
                this.abort_parse = true;
                
                if ( this.listing_stream != null )
                {
                    this.listing_stream.print(
                            "\nMax error count exceeded -- Aborting...\n");
                }
            }
            else
            {
                if ( this.listing_stream != null )
                {
                    this.listing_stream.print("\n");
                }
            }
        }

        return;

    } /* macshapa_odb_reader::post_error_message() */


    /*************************************************************************
     *
     * post_warning_message()
     *
     * Post a warning message to the listing and increment this.warning_count.
     * If this.warning_count is greater than or equal to this.max_warnings, 
     * issue a MAX_WARNINGS_EXCEEDED_ERR.
     *
     *                                           - 5/26/08
     *
     * Parameters:
     *
     * warning_num:  Integer code indicate the warning message to be posted.
     *
     * comment:  In some cases, we may wish to supplement the warning message
     *      indicated by the warning_num with a message composed at run time.  
     *      If so, comment points to this addition.  Note that the last 
     *      character in the comment string should be '\n'.
     *
     * Return Value:  Void.
     *
     * Changes:
     *
     *    - None.
     *
     *************************************************************************/

    private void post_warning_message(int warning_num,
                                      String comment)
        throws SystemErrorException
    {
        final String mName = "macshapa_odb_reader::post_warning_message(): ";

        if ( ( warning_num < 0 ) ||
             ( warning_num >= NUMBER_OF_WARNING_MESSAGES ) )
        {
            throw new SystemErrorException(mName + "warning_num out of range.");
        }
        else
        {
            (this.warning_count)++;
            
            if ( this.listing_stream != null )
            {
                this.listing_stream.printf("\n      WARNING %3d: %s", 
                        warning_num, this.warning_mssg_table[warning_num]);
                
                if ( comment != null )
                {
                    this.listing_stream.printf("        %s", comment);
                }
                
                this.listing_stream.print("\n");
            }

            if ( this.warning_count >= this.max_warnings )
            {
                /* call this.post_error_message() with scan_fatal and 
                 * parse_fatal both equal to true so as to force an 
                 * immediate halt to all attempts to read the open 
                 * database file.   
                 */

                this.post_error_message(MAX_WARNINGS_EXCEEDED_ERR, null,
                                        true, true);
            }
        }

        return;

    } /* macshapa_odb_reader::post_warning_message() */
     
        
    /*************************************************************************/
    /************************* File Read Methods: ****************************/
    /*************************************************************************/
    
    /*************************************************************************
     *
     * get_next_char()
     *
     * Return the current value of this.lookahead_char, and load the next
     * character from the input file into this.lookahead_char.  Load the
     * next line if appropriate.  Maintain the this.line_buf_index field,
     * and set this.lookahead_char to ' ' when the end of file is reached.
     *
     *                                           - 5/27/08
     *
     * Parameters:
     *
     *    - None.
     *
     * Return Value:  The contents of this.lookahead_char upon entry.
     *
     * Changes:
     *
     *    - None.
     *
     **************************************************************************/

    private char get_next_char()
        throws SystemErrorException,
	       java.io.IOException
    {
        final String mName = "macshapa_odb_reader::get_next_char(): ";
        char next_char;

        next_char = ' ';  /* a good, safe value */

        if ( this.end_of_file )
        {
            throw new SystemErrorException(mName + 
                                           "this.end_of_file true on entry.");
        }

        next_char = this.lookahead_char;

	if ( this.line_index > this.line_len )
	{
	    /**
	     * must attempt to load the next line -- there may not be one,
	     * in which case we will simply set this.lookahead_char to ' '.
	     */
	    load_next_line();
	}
	
	if ( this.end_of_file )
	{
	    this.lookahead_char = ' ';
	}
	else if ( this.line_index < this.line_len )
        {
            this.lookahead_char = this.line.charAt(this.line_index);
            (this.line_index)++;
        }
	else if ( this.line_index == this.line_len )
	{
	    /* load_next_line() does not include the new line character
	     * at the end of each line it loads.  Thus we insert that new
	     * line character here ('\r' since we are dealing with pre-MacOS X
	     * files), and load the next line.
	     */
	    this.lookahead_char = '\r';
	    (this.line_index)++;
	}
	else
        {
            throw new SystemErrorException(mName + 
		    "this else clause should be unreachable.");
        }

        return(next_char);

    } /* macshapa_odb_reader::get_next_char() */


    /**************************************************************************
     *
     * load_next_line()
     *
     * Load the next line from the input file into rthis.line, and post the 
     * line to the listing file (if it is defined).  
     *
     *                                               - 5/27/08
     *
     * Parameters:
     *
     *    - None.
     *
     * Return Value:  void
     *
     *  Changes:
     *
     *        - None.
     *
     **************************************************************************/

    private void load_next_line()
	throws SystemErrorException, 
	       java.io.IOException
    {
        final String mName = "macshapa_odb_reader::load_next_line(): ";

	if ( this.end_of_file )
        {
            throw new SystemErrorException(mName + 
		                           "this.end_of_file true on entry");
        }

	this.line = input_stream.readLine();
	
	if ( this.line == null )
	{
	    this.end_of_file = true;
	    this.line_index = 0;
	    this.line_len = 0;
	}
	else
	{
	    this.line_number++;
	    this.line_index = 0;
	    this.line_len = this.line.length();
	}

        if ( this.listing_stream != null ) /* post the line to the listing */
        {
                post_line_to_listing();
        }

        return;

    } /* macshapa_odbr_reader::load_next_line() */


    /**************************************************************************
     *
     * post_line_to_listing()
     *
     * Write the current contents of this.line to the listing.
     *
     *                                                     - 6/1/08
     *
     * Parameters:
     *
     *    - None.
     *
     * Return Value:  Void.
     *
     *  Changes:
     *
     *        - None.
     *
     **************************************************************************/

    private void post_line_to_listing()
	throws SystemErrorException
    {
        final String mName = "macshapa_odb_reader::post_line_to_listing(): ";
 
	if ( this.listing_stream == null )
        {
            throw new SystemErrorException(mName + 
		    "this.listing_stream null on entry");
        }

	if ( this.line == null )
        {
            throw new SystemErrorException(mName + "this.line null on entry");
        }


        return;

    } /* post_line_to_listing() */
     
        
    /*************************************************************************/
    /****************************** Lexer: ***********************************/
    /*************************************************************************/
    /* Code and data definitions implementing the lexical analyzer that      */
    /* tokenizes the input file before it is fed to the parser proper.       */
    /*************************************************************************/

    /*** lexer constants ***/
    
    final double FLT_MAX = 10000000000000000000000000000000.0;
    final double FLT_MIN = 0.00000000000000000000000000000001;
    final long LONG_MAX =  1000000000;
    final long LONG_MIN = -1000000000;

    /*************************************************************************
     * a_list_tag_name_table 
     * 
     * The a-list tag name table contains all the known a-list tags.  This 
     * table must be alphabetically ordered as it is searched via a binary 
     * search on a sorted list.  In addition, the index of each a-list tag 
     * must match the value of the associated code defined at the top of this
     * file.  Thus both this table and list of codes must be updated whenever 
     * a new a-list label is defined.
     *************************************************************************/

    final String a_list_tag_name_table[] =
    {
	/* 00 ALIGNMENTS_LABEL                        = */ ALIGNMENTS_STR,
	/* 01 CELLS_LABEL                             = */ CELLS_STR,
	/* 02 COLUMN_WIDTH_LABEL                      = */ COLUMN_WIDTH_STR,
	/* 03 DEBUG_LEVEL_LABEL                       = */ DEBUG_LEVEL_STR,
	/* 04 FORMAL_ARG_LIST_LABEL                   = */ FORMAL_ARG_LIST_STR,
	/* 05 GROUPS_LABEL                            = */ GROUPS_STR,
	/* 06 HARD_PREC_LABEL                         = */ HARD_PREC_STR,
	/* 07 HEADER_LABEL                            = */ HEADER_STR,
	/* 08 IMPORT_FORMATS_LIST_LABEL               = */ IMPORT_FORMATS_LIST_STR,
	/* 09 IMPORT_PRODUCTION_LIST_LABEL            = */ IMPORT_PRODUCTION_LIST_STR,
	/* 10 INSERT_ACTIONS_LABEL                    = */ INSERT_ACTIONS_STR,
	/* 11 IS_SHOWN_LABEL                          = */ IS_SHOWN_STR,
	/* 12 IS_TRACED_LABEL                         = */ IS_TRACED_STR,
	/* 13 MAX_ERRORS_LABEL                        = */ MAX_ERRORS_STR,
	/* 14 MAX_WARNINGS_LABEL                      = */ MAX_WARNINGS_STR,
	/* 15 NAME_LABEL                              = */ NAME_STR,
	/* 16 OFFSET_LABEL                            = */ OFFSET_STR,
	/* 17 ONSET_LABEL                             = */ ONSET_STR,
	/* 18 PATTERN_LABEL                           = */ PATTERN_STR,
	/* 19 PREDICATE_DEFINITIONS_LABEL             = */ PREDICATE_DEFINITIONS_STR,
	/* 20 PROGRAM_ACTIONS_LABEL                   = */ PROGRAM_ACTIONS_STR,
	/* 21 QUERY_LABEL                             = */ QUERY_STR,
	/* 22 SHAPA_PANE_VARS_LABEL                   = */ SHAPA_PANE_VARS_STR,
	/* 23 SPREADSHEET_VARIABLE_DECLARATIONS_LABEL = */ SPREADSHEET_VARIABLE_DECLARATIONS_STR,
	/* 24 SPREADSHEET_VARIABLE_DEFINITIONS_LABEL  = */ SPREADSHEET_VARIABLE_DEFINITIONS_STR,
	/* 25 SYSTEM_LABEL                            = */ SYSTEM_STR,
	/* 26 TEXT_LABEL                              = */ TEXT_STR,
	/* 27 TIME_LABEL                              = */ TIME_STR,
	/* 28 TYPE_LABEL                              = */ TYPE_STR,
	/* 29 USER_LABEL                              = */ USER_STR,
	/* 30 VAL_LABEL                               = */ VAL_STR,
	/* 31 VAR_LABEL                               = */ VAR_STR,
	/* 32 VARIABLE_LENGTH_LABEL                   = */ VARIABLE_LENGTH_STR,
	/* 33 VERSION_LABEL                           = */ VERSION_STR,
	/* 34 VOCAB_LABEL                             = */ VOCAB_STR
    };


    /**************************************************************************
     * private_value_name_table
     *
     * The private value name table contains all the known private values.  
     * This table must be alphabetically ordered as it is searched via a binary 
     * search on a sorted list.  In addition, the index of each private value 
     * name must match the value of the associated code defined at the top of 
     * this file.  Thus both this table and the list of codes must be updated 
     * whenever a new private value name is defined.
     **************************************************************************/
	    
    final String private_value_name_table[] =
    {
	/* 00 FLOAT_PVAL          = */ FLOAT_PVAL_STR,
	/* 01 INTEGER_PVAL        = */ INTEGER_PVAL_STR,
	/* 02 MATRIX_PVAL         = */ MATRIX_PVAL_STR,
	/* 03 NOMINAL_PVAL        = */ NOMINAL_PVAL_STR,
	/* 04 PREDICATE_PVAL      = */ PREDICATE_PVAL_STR,
	/* 05 TEXT_PVAL           = */ TEXT_PVAL_STR
    };
    
    /**************************************************************************
     * isdigit(), isgraph(), islower(), isspace(), & isupper()
     *
     * Rewrites of the standard C library routines for use by the lexer.
     *
     *						     6/7/08
     *
     **************************************************************************/
    
    private boolean isdigit(char ch)
    {
	boolean retVal = false;
	
	if ( ( ch >= 0x30 ) && ( ch <= 0x39 ) )
	{
	    retVal = true;
	}
	
	return retVal;
    
    } /* macshapa_odb_reader::isdigit() */
    
    private boolean isgraph(char ch)
    {
	boolean retVal = false;
	
	
	if ( ( ch >= 0x21 ) && ( ch <= 0x7F ) )
	{
	    retVal = true;
	}
	
	return retVal;
    
    } /* macshapa_odb_reader::isgraph() */
    
    private boolean islower(char ch)
    {
	boolean retVal = false;
	
	if ( ( ch >= 0x61 ) && ( ch <= 0x7A ) )
	{
	    retVal = true;
	}
	
	return retVal;
    
    } /* macshapa_odb_reader::islower() */
    
    private boolean isspace(char ch)
    {
	boolean retVal = false;
	
	if ( ( ch == 0x20 ) || /* space */
	     ( ch == 0x0C ) || /* form feed */
	     ( ch == 0x0A ) || /* new line */
	     ( ch == 0x0D ) || /* carrige return */
	     ( ch == 0x09 ) || /* horizontal tab */
	     ( ch == 0x0B ) )  /* vertical tab */
	{
	    retVal = true;
	}
	
	return retVal;
    
    } /* macshapa_odb_reader::isspace() */
    
    private boolean isupper(char ch)
    {
	boolean retVal = false;
	
	if ( ( ch >= 0x41 ) && ( ch <= 0x5A ) )
	{
	    retVal = true;
	}
	
	return retVal;
    
    } /* macshapa_odb_reader::isupper() */


    /*************************************************************************
     *
     * get_next_token()
     *
     * Top level function for the lexical analyzer.
     *
     * The parser requires a two token lookahead on certain occasions, and thus
     * we must maintain a three token long queue of tokens that have been read
     * in from the input file, and are now waiting to be consumed by the parser.
     *
     * This function handles the mechanics of managing this queue.  The 
     * "current" token as seen by the parser is always in this.l0_tok, with the 
     * lookahead one and two tokens in this.l1_tok and this.l2_tok respectively.
     * In general, this is simply a matter of moving tokens along the queue.  
     * However at end of file, we must generate new end of file tokens as the 
     * queue empties.
     *
     *                                                  - 6/4/08
     *
     * Parameters:
     *
     *	  - None.
     *
     * Returns:  void.
     *
     * Changes:
     *
     *	  - None.
     *
     *******************************************************************************/

    public void get_next_token()
	throws SystemErrorException,
	       java.io.IOException
    {
        final String mName = "macshapa_odb_reader::get_next_token(): ";
	Token tempTok = null;

	if ( this.abort_scan )
	{
	    throw new SystemErrorException(mName + "abort_scan true on entry");
	}
	else if ( (this.l0_tok).code == EOF_TOK )
	{
	    post_error_message(UNEXPECTED_END_OF_FILE_ERR, null, true, true);
	}
	else
	{
	    tempTok = this.l0_tok;
	    this.l0_tok = this.l1_tok;
	    this.l1_tok = this.l2_tok;
	    
	    if ( this.l2_tok.code != EOF_TOK )
	    {
		this.l2_tok = tempTok;

		if ( this.l2_tok.str == null )
		{
		    this.l2_tok.str = new StringBuilder("");
		}
		else
		{
		    this.l2_tok.str.delete(0, this.l2_tok.str.length() - 1);
		}
		this.l2_tok.code             = -1; /* a convenient, invalid value */
		this.l2_tok.aux              = 0;
		this.l2_tok.val              = 0.0;

		read_token(this.l2_tok);
	    }
	    else /* keep the queue full with eof tokens */
	    {
		this.l2_tok = tempTok;
		if ( this.l2_tok.str == null )
		{
		    this.l2_tok.str = new StringBuilder("");
		}
		else
		{
		    this.l2_tok.str.delete(0, this.l2_tok.str.length() - 1);
		}
		this.l2_tok.code             = EOF_TOK;
		this.l2_tok.aux              = 0;
		this.l2_tok.val              = 0.0;
	    }
	}

	return;

    } /* macshapa_odb_reader::get_next_token() */

    
    /*******************************************************************************
    *
    * get_non_blank()
    *
    * Read characters from the input file until either a non white space
    * character or a the end of file is encountered.  Note that this routine
    * recognizes comments, and treats them as white space.
    *
    *                                                 - 6/4/08
    *
    * Parameters:
    *
    *	  - None.
    *
    * Returns:  The first non white space character read, or ' ' if the
    *	    end of file is encountered.
    *
    * Changes:
    *
    *	  - None.
    *
    *******************************************************************************/

    private char get_non_blank()
	throws SystemErrorException,
	       java.io.IOException
    {
        final String mName = "macshapa_odb_reader::get_non_blank(): ";
	char next_char;
	Boolean in_comment;

	next_char = ' ';
	in_comment = false;

	if ( ! this.end_of_file )
	{
	    next_char = get_next_char();

	    if ( next_char == ';' )
	    {
		in_comment = true;
	    }

	    while ( ( ! this.end_of_file ) &&
		    ( ( in_comment ) || ( isspace(next_char) ) ) )
	    {
		next_char = get_next_char();

		/**
		 * The end of a comment is indicated by the end of the line, 
		 * and the beginning of a comment is indicated by a ';'.  
		 * Note that we don't care if one of the following 
		 * assignments fails to alter the value of in_comment.
		 */

		if ( next_char == '\r' )
		{
		    in_comment = false;
		}
		else if ( next_char == ';' )
		{
		    in_comment = true;
		}
	    }

	    if ( ( this.end_of_file ) && ( in_comment ) )
	    {
		next_char = ' ';
	    }
	}

	return(next_char);

    } /* macshapa_odb_reader::get_non_blank() */


    /*************************************************************************
    *
    * read_boolean_or_alist_tag_token()
    *
    * Read either an a-list tag token or a boolean value.  The productions
    * describing these tokens are given below:
    *
    *	    <a_list_entry_label> --> <upper_case_letter>
    *                                (<upper_case_letter> | '-')*
    *                                '>'
    *
    *       <upper_case_letter> --> 'A' | 'B' | ... | 'Z'
    *
    *       <boolean> --> ( 'TRUE' | 'FALSE' )
    *
    * While we do have a production for a generic a-list tag name, in practice
    * we only expect to see know a-list tag names -- the generic case is there
    * simply to allow us to deal with later versions of the open database file
    * format gracefully.  Thus, once we determine that a token is an a-list
    * entry name, we look it up in the a_list_tag_name_table, and load its
    * associated code into token_ptr->aux.  If the a-list tag name is unknown,
    * we load UNKNOWN_ALIST_LABEL into token_ptr->aux so that the parser can
    * deal with the situation gracefully.
    *
    *                                                      - 6/5/08
    *
    * Parameters:
    *
    *	  - first_char:  First character in the symbol.  This parameter must
    *		contain an upper case letter upon entry.
    *
    *     - token:  Reference to the instance of Token in which the new
    *           token is to be stored.
    *
    *
    * Returns:  Void.
    *
    * Changes:
    *
    *	  - None.
    *
    **************************************************************************/

    void read_boolean_or_alist_tag_token(char first_char,
	                                 Token token)
        throws SystemErrorException,
	       java.io.IOException
    {
        final String mName =
		"macshapa_odb_reader::read_boolean_or_alist_tag_token(): ";
	boolean done;
	boolean is_alist_tag;
	char next_char;
	int bottom;
	int probe;
	int result;
	int top;

	done = false;
	is_alist_tag = false;

	if ( ( ! isupper(first_char) ) || ( token == null) )
	{
	    throw new SystemErrorException(mName + "bad param(s) on entry.");
	}
	
	/* try to parse the boolean value or alist tag */
	next_char = first_char;

	while ( ( ! done ) && ( ! this.end_of_file ) && ( this.abort_scan ) )
	{
	    if ( ( isupper(next_char) ) || ( next_char == '-' ) )
	    {
		token.save_char_to_token(next_char);
	    }
	    else if ( next_char == '>' )
	    {
	        done = true;
		is_alist_tag = true;
		token.save_char_to_token(next_char);
	    }
	    else
	    {
		throw new SystemErrorException(mName + 
			"This else clause should be unreachable.");
	    }

	    if ( ! ( ( isupper(this.lookahead_char) )
		     ||
		     ( this.lookahead_char == '-' )
		     ||
		     (  this.lookahead_char == '>')
		   )
	       )
	    {
		done = true;
	    }

	    if ( ! done )
	    {
		next_char = get_next_char();
	    }
	} /* while */

	if ( done )
	{
	    if ( is_alist_tag ) /* we have an a-list entry tag -- look it up */
	    {
		token.code = ALIST_LABEL_TOK;
		token.aux = UNKNOWN_ALIST_LABEL;

		/* do a binary search on the a_list_tag_name_table.  If we */
		/* find the tag in the table, we load the associated code  */
		/* into token_ptr->aux.                                    */

		bottom = 0;
		top = NUMBER_OF_ALIST_LABELS - 1;
		probe = (top + bottom) / 2;

		while ( top >= bottom )
		{
		    result = token.str.toString().
			    compareTo(a_list_tag_name_table[probe]);

		    if ( result < 0 )
		    {
			top = probe - 1;
		    }
		    else if ( result > 0 )
		    {
			bottom = probe + 1;
		    }
		    else /* we have a match */
		    {
			token.aux = probe;
			top = bottom - 1; /* exit the while */
		    }

		    probe = (top + bottom) / 2;
		}
	    }
	    else if ( token.str.toString().compareTo("TRUE") == 0 )
	    {
		token.code = BOOL_TOK;
		token.aux = TRUE;
	    }
	    else if  ( token.str.toString().compareTo("FALSE") == 0 )
	    {
		token.code = BOOL_TOK;
		token.aux = FALSE;
	    }
	    else /* error - fatal since I don't see how to recover from it. */
	    {
		token.code = ERROR_TOK;

		post_error_message(UNKNOWN_TOKEN_TYPE_ERR,
				   "Perhaps an unescaped symbol?\n", true, true);
	    }
	}
	else if ( this.end_of_file )
	{
	    token.code = EOF_TOK;

	    post_error_message(UNEXPECTED_END_OF_FILE_ERR,
		    "EOF was encountered in a boolean or a-list tag name.\n",
		    true, true);
	}
	else if ( ! this.abort_scan ) /* panic. */
	{
	    throw new SystemErrorException(mName + 
		    "Why did we exit the while?!?!");
	}

	return;

    } /* macshapa_odb_reader::read_boolean_or_alist_tag_token() */


    /*************************************************************************
     *
     * read_numeric_token()
     *
     * This method is called to scan a numeric token -- that is either an
     * integer or a floating point value.
     *
     * A numeric token is either an <integer> or a <float>, as defined in
     * the following productions:
     *
     *	    <integer> --> [<sign>] (<digit>)+
     *
     *      <float> --> ( [<sign>] (<digit>)+ '.' (<digit>)* ) |
     *                  ( [<sign>] (<digit>)* '.' (<digit>)+ )
     *
     *      <sign> --> '+' | '-'
     *
     *      <digit> --> '0' | '1' | ... | '9'
     *
     * The end of a numeric token is indicated by the first character
     * in the input stream that cannot be generated by the above productions.
     *
     *                                                  - 6/5/08
     *
     * Parameters:
     *
     *	  - first_char:  First character in the symbol.  This parameter must
     *		contain either '+', '-', '.' or a decimal digit upon entry.
     *
     *    - token:  reference to the instance of Token in which the new
     *          token is to be stored.
     *
     * Returns:  Void.
     *
     *  Changes:
     *
     *	  - None.
     *
     *************************************************************************/

    private void read_numeric_token(char first_char,
				    Token token)
        throws SystemErrorException,
	       java.io.IOException
    {
        final String mName = "macshapa_odb_reader::read_numeric_token(): ";
	char next_char;
	boolean is_integer;
	boolean done;
	boolean non_empty;
	boolean scanning_first_char;
	boolean integer_digits_discarded;
	boolean fraction_digits_discarded;
	boolean clip_string;
	double digit;
	double fraction;
	double sign;
	double value;

	is_integer = true;
	done = false;
	non_empty = false;

	integer_digits_discarded = false;
	fraction_digits_discarded = false;

	clip_string = false;

	fraction = 0.1;
	sign = 1.0;
	value = 0.0;

	if ( ( ( ! isdigit(first_char) ) && ( first_char != '+' ) &&
	       ( first_char != '-' ) && ( first_char != '.' )
	     ) 
	     ||
	     ( token == null ) )
	{
	    throw new SystemErrorException(mName + "bad param(s) on entry.");
	}

	/* try to parse the numeric value */
	next_char = first_char;

	do
	{
	    if ( isdigit(next_char) )
	    {
		non_empty = true;

		digit = (double)(next_char - '0');

		if ( is_integer )
		{
		    if ( value <= (double)FLT_MAX )
		    {
			value = (value * 10.0) + digit;
		    }
		    else
		    {
			integer_digits_discarded = true;
		    }
		}
		else
		{
		    if ( fraction >= (double)FLT_MIN )
		    {
			value = value + (digit * fraction);
			fraction /= 10.0;
		    }
		    else
		    {
			fraction_digits_discarded = true;
		    }
		}

		token.save_char_to_token(next_char);
	    }
	    else if ( next_char == '-' )
	    {
		sign = -1.0;

		token.save_char_to_token(next_char);
	    }
	    else if ( next_char == '+' )
	    {
		sign = +1.0;

		/* Don't save the '+' to the token, since it will confuse */
		/* MacSHAPA's parsing routines, and it is assumed anyway. */
	    }
	    else if ( next_char == '.' )
	    {
		is_integer = false;

		token.save_char_to_token(next_char);
	    }
	    else
	    {
		throw new SystemErrorException(mName + 
			"This else clause should be unreachable.");
	    }

	    /* a sign is only allowed as the first character of a numeric */
	    /* token, and thus we need only concern ourselves with digits */
	    /* and at most one decimal point.  As soon as we hit a        */
	    /* character that doesn't fit this description, we stop       */
	    /* reading new characters.  We check for errors after we exit */
	    /* the do loop.                                               */
	    if ( ( isdigit(this.lookahead_char) ) ||
		 ( ( is_integer ) && ( this.lookahead_char == '.' ) ) )
	    {
		next_char = get_next_char();
	    }
	    else
	    {
		done = true;
	    }
	}
	while ( ( ! done ) && ( ! this.end_of_file ) && ( ! this.abort_scan ) );

	/**
	 * while it should never happen in a valid database file, there
	 * is no reason on the lexcal analysis level why a numerical
	 * token shouldn't end with an EOF, and thus this function
	 * treats EOF and done the same way.
         */

	if ( ( done ) || ( this.end_of_file ) )
	{
	    if ( ! non_empty ) /* the numerical token contains no digits */
	    {
		/* this is a fatal error since I can't think of a way to recover
		 * that isn't likely to cascade into further & worse errors      
		 */

		token.code = ERROR_TOK;

		post_error_message(ILL_FORMED_NUMERICAL_CONST_ERR,
				   null, true, true);
	    }
	    else if ( is_integer )
	    {
		token.code = INT_TOK;

		value *= sign;

		if ( value > (double)LONG_MAX )
		{
		    clip_string = true;
		    value = (double)LONG_MAX;
		    post_warning_message(INTEGER_OUT_OF_RANGE_WARN, null);
		}
		else if ( value < (double)LONG_MIN )
		{
		    clip_string = true;
		    value = (double)LONG_MIN;
		    post_warning_message(INTEGER_OUT_OF_RANGE_WARN, null);
		}

		token.val = value;

		if ( clip_string )
		{
		    token.clip_numeric_token_string();
		}
	    }
	    else /* we have a floating point value */
	    {
		token.code = FLOAT_TOK;

		if ( integer_digits_discarded )
		{
		    clip_string = true;
		    value = (double)FLT_MAX;
		    post_warning_message(FLOAT_VAL_OUT_OF_RANGE_WARN, null);

		    /* TODO: it is possible that this warning may cause us 
		     *       to exceed the limit on warnings, and to abort 
		     *	 the parse.  Think on whether we need to check for
		     *       this, and if so, what action we should take.
		     */
		}

		if ( fraction_digits_discarded )
		{
		    clip_string = true;
		    post_warning_message(PART_OF_FRACTION_DISCARDED_WARN, null);
		}

		token.val = sign * value;

		if ( clip_string )
		{
		    token.clip_numeric_token_string();
		}
	    }
	}
	else if ( this.abort_scan ) /* panic */
	{
	    throw new SystemErrorException(mName + "Why did we exit while?!?");
	}

	return;

    } /* macshapa_odb_reader::read_numeric_token() */


    /*************************************************************************
     *
     * read_private_value_token()
     *
     * Read a private value token from the input stream.  The productions
     * describing a generic private value are given below:
     *
     *	    <private_value> --> '<<' (<upper_case_letter> | '-')+ '>>'
     *
     *       <upper_case_letter> --> 'A' | 'B' | ... | 'Z'
     *
     * While we have a production for a generic private value, in practice
     * we only expect to see a previously defined subset of same.  The
     * definition of a private value exists only so that we can recognize
     * that a token is an unknown private value and deal with the situation
     * gracefully.  This should case should only arise when we attemp to read
     * an open database file generated by a later version of MacSHAPA.
     *
     *                                          - 6/5/08
     *
     * Parameters:
     *
     *	  - first_char:  First character in the symbol.  This parameter must
     *		contain '<' upon entry.
     *
     *	  - token:  Reference to the instance of Token in which the new
     *           token is to be stored.
     *
     * Returns:  Void.
     *
     * Changes:
     *
     *	- None.
     *
     **************************************************************************/

    private void read_private_value_token(char first_char,
	                                  Token token)
	throws SystemErrorException,
	       java.io.IOException
    {
        final String mName = 
		"macshapa_odb_reader::read_private_value_token(): ";
	char next_char;
	Boolean done;
	Boolean in_postfix;
	Boolean in_prefix;
	int bottom;
	int probe;
	int result;
	int top;

	done = false;
	in_postfix = false;
	in_prefix = true;

	if ( ( first_char != '<' ) || ( token == null ) )
	{
	    throw new SystemErrorException(mName + "bad param(s) on entry.");
	}
	
	if ( this.lookahead_char != '<' )
	{
	    token.code = ERROR_TOK;
	    token.save_char_to_token('\0');
	    post_error_message(ILL_FORMED_PRIVATE_VALUE_ERR,
			       "Private values must begin with \"<<\".\r",
			       true, true);
	}
	else /* go ahead and try to parse the private value */
	{
	    token.save_char_to_token(first_char);

	    next_char = this.get_next_char();

	    while ( ( ! done ) && 
		    ( ! this.end_of_file ) && 
		    ( ! this.abort_scan ) )
	    {
		if ( ( isupper(next_char) ) || ( next_char == '-' ) )
		{
		    token.save_char_to_token(next_char);
		}
		else if ( next_char == '<' )
		{
		    if ( in_prefix )
		    {
			token.save_char_to_token(next_char);
		    }
		    else
		    {
			done = true;
			token.code = ERROR_TOK;
			post_error_message(ILL_FORMED_PRIVATE_VALUE_ERR,
			    "Only upper case letters & \"-\"s may appear in the body of a private value.\r",
			    true, true);
		    }
		}
		else if ( next_char == '>' )
		{
		    token.save_char_to_token(next_char);

		    if ( in_postfix )
		    {
			done = true;
			token.code = PRIVATE_VAL_TOK;
			token.aux = UNKNOWN_PRIVATE_VALUE; /* we will look it up shortly */
		    }
		    else if ( this.lookahead_char == '>' )
		    {
			    in_postfix = true;
		    }
		    else
		    {
			done = true;
			token.code = ERROR_TOK;
			post_error_message(ILL_FORMED_PRIVATE_VALUE_ERR,
				"Private values must end with \">>\".\r",
				true, true);
		    }
		}
		else
		{
		    done = true;
		    token.code = ERROR_TOK;

		    if ( next_char == '\r' )
		    {
			post_error_message(ILL_FORMED_PRIVATE_VALUE_ERR,
					   "Encountered new line in private value.\r",
					    true, true);
		    }
		    else
		    {
			post_error_message(ILL_FORMED_PRIVATE_VALUE_ERR,
				"Only upper case letters & '-'s may appear in the body of a private value.\r",
				true, true);
		    }
		}

		if ( ! done )
		{
		    next_char = this.get_next_char();
		}

		in_prefix = false;

	    } /* while */

	    if ( ( done ) && ( token.code == PRIVATE_VAL_TOK ) )
	    {
		/**
		 * We have successfully loaded a syntactically correct 
		 * private value token into token.  Must now see if we 
		 * recognize it.  
		 *
		 * To do this, do a binary search on the 
		 * private_value_name_table.  If we find the private value 
		 * in the table, we load the associated code into token.aux.
		 */

		bottom = 0;
		top = NUMBER_OF_PRIVATE_VALUES - 1;
		probe = (top + bottom) / 2;
		
		while ( top >= bottom )
		{
		    result = token.str.toString().
			    compareTo(private_value_name_table[probe]);

		    if ( result < 0 )
		    {
			top = probe - 1;
		    }
		    else if ( result > 0 )
		    {
			bottom = probe + 1;
		    }
		    else /* we have a match */
		    {
			token.aux = probe;
			top = bottom - 1; /* exit the while */
		    }

		    probe = (top + bottom) / 2;
		}
	    }
	    else if ( this.end_of_file )
	    {
		token.code = EOF_TOK;

		post_error_message(UNEXPECTED_END_OF_FILE_ERR,
			"EOF was encountered in a private value.\r",
			true, true);
	    }
	    else if ( ! this.abort_scan ) /* panic */
	    {
		throw new SystemErrorException(mName + 
			"Why did we exit the while?!?!");
	    }
	}

	return;

    } /* macshapa_odb_reader::read_private_value_token() */


    /*************************************************************************
     *
     * read_reserved_word_token()
     *
     * Read a reserved word token.  At present, there are only two reserved
     * words, "setf" and "macshapa-db".  Since no new reserved words are 
     * anticipated, this function simply reads an identifier consisting of 
     * lower case letters and '-'s, and tests to see if the identifier is 
     * one of the two reserved words.
     *
     *                                                   - 6/6/08
     *
     *  Parameters:
     *
     *	  - first_char:  First character in the symbol.  This parameter must
     *		contain an lower case letter upon entry.
     *
     *    - token:  Reference to the instance of Token in which the new
     *          token is to be stored.
     *
     * Returns:  Void.
     *
     * Changes:
     *
     *    - None.
     *
     *************************************************************************/

    private void read_reserved_word_token(char first_char,
		                          Token token)
	throws SystemErrorException,
	       java.io.IOException
    {
        final String mName = 
		"macshapa_odb_reader::read_reserved_work_token(): ";
	char next_char;
	Boolean done;
	Boolean is_alist_tag;
	int bottom;
	int probe;
	int result;
	int top;

	done = false;
	is_alist_tag = false;

	if ( ( ! islower(first_char) ) || ( token == null ) )
	{
	    throw new SystemErrorException(mName + "bad param(s) on entry.");
	}
	
	/* try to parse the reserved word */
	next_char = first_char;

	while ( ( ! done ) && ( ! this.end_of_file ) )
	{
	    if ( ( islower(next_char) ) || ( next_char == '-' ) )
	    {
	        token.save_char_to_token(next_char);
	    }
	    else
	    {
		throw new SystemErrorException(mName + 
			"This else clause should be unreachable.");
	    }

	    if ( ! ( ( islower(this.lookahead_char) )
		     ||
		     ( this.lookahead_char == '-' ) 
		   )
	       )
	    {
	        done = true;
	    }

	    if ( ! done )
	    {
		next_char = this.get_next_char();
	    }
	} /* while */

	if ( done )
	{
	    if ( token.str.toString().compareTo("setf") == 0 )
	    {
		token.code = SETF_TOK;
	    }
	    else if ( token.str.toString().compareTo("macshapa-db") == 0 )
	    {
		token.code = DB_VAR_TOK;
	    }
	    else /* error - fatal since I don't see how to recover from it. */
	    {
		token.code = ERROR_TOK;

		post_error_message(UNKNOWN_TOKEN_TYPE_ERR,
				   "Perhaps an unescaped symbol?\r",
				   true, true);
	    }
	}
	else if ( this.end_of_file )
	{
	    token.code = EOF_TOK;

	    post_error_message(UNEXPECTED_END_OF_FILE_ERR,
			       "EOF was encountered in a reserved word.\r",
		               true, true);
	}
	else /* panic. */
	{
	    throw new SystemErrorException(mName + 
		    "Why did we exit the while?!?!");
	}

	return;

    } /* macshapa_odb_reader::read_reserved_word_token() */


    /*************************************************************************
    *
    * read_string_token()
    *
    * This function is called to scan a string token -- that is a token that
    * consists of a string delimited with double quotes.
    *
    * The productions describing what is meant by a string in the context of
    * an open MacSHAPA database are given below.  Note the nonstandard use
    * of the '-' symbol.  While this notaion is discussed in full in the Open
    * Database Grammar document, thinking of it in terms of set theoretic
    * subtraction should give you the basic idea.
    *
    *	<text_quote_string_char> --> (<char> - ( '\' | '"' | <bs> ) ) | 
    *                                ( '\\' | '\"' )
    *
    *	<bs> --> backspace character (i.e. hexadecimal value 0x08)
    *
    *	<char> --> Any character in the Standard Roman character set,
    *              hexadecimal values 0x00 to 0xFF.
    *
    *   <text_quote_string> --> '"' (<text_quote_string_char>)* '"'
    *
    *   <non_ws_text_quote_string_char> --> <text_quote_string_char> - <ws_char>
    *
    *   <ws_char> --> <space> | <form_feed> | <new_line> | <cr> |
    *                 <horizontal_tab> | <vertical_tab>
    *
    *   <space> --> 0x20            (* the ASCII space character *)
    *
    *   <form_feed> --> 0x0C        (* the ASCII form feed character *)
    *
    *   <new_line> --> 0x0A         (* the ASCII line feed character *)
    *
    *   <cr> --> 0x0D               (* the ASCII carriage return character *)
    *
    *   <horizontal_tab> --> 0x09   (* the ASCII horizontal tab character *)
    *
    *   <vertical_tab> --> 0x0B     (* the ASCII vertical tab character *)
    *
    *   <non_blank_text_quote_string> --> '"'
    *                                     (<text_quote_string_char>)*
    *                                     <non_ws_text_quote_string_char>
    *                                     (<text_quote_string_char>)*
    *                                     '"'
    *
    *   <quote_string_char> --> (<graphic_char> - 
    *                             ( '"' | '\' ) ) | ( ' ' | '\\' )
    *
    *   <graphic_char> --> Any printing character, hexadecimal codes 0x21 - 0x7F
    *
    *   <quote_string> --> '"' (<quote_string_char>)* '"'
    *
    * As can be seen from these productions, we are interested in recognizing
    * three types of string tokens -- instances of <text_quote_string>,
    * <non_blank_text_quote_string>, and <quote_string>.  Note that these
    * are overlaping catagories.
    *
    * At the lexical analysys stage, we don't know which flavor of string
    * token we should be dealing with -- this is determined from context in
    * the parser proper.  Hence in this function we simply load the string
    * into the token and keep track of which kind of string it could be.  This
    * latter bit of information is encoded into the aux field of the token.
    *
    * In the process, we scan for characters that can't occur in any flavor of
    * string token, forcing them to a legal value ('_'), and issueing a warning
    * should we encounter them.
    *
    *                                                    -- 6/6/08
    *
    * Parameters:
    *
    *	  - first_char:  First character in the symbol.
    *
    *     - token:  Reference to the instance of Token in which the new
    *		token is to be stored.
    *
    * Returns:  Void.
    *
    * Changes:
    *
    *     - None.
    *
    *************************************************************************/

    private void read_string_token(char first_char,
				   Token token)
        throws SystemErrorException,
	       java.io.IOException
    {
        final String mName = 
		"macshapa_odb_reader::read_reserved_work_token(): ";
	char next_char;
	boolean could_be_quote_string;
	boolean done;
	boolean escape;
	boolean non_blank;

	done = false;
	escape = false;

	if ( ( first_char != '"' ) || ( token == null ) )
	{
	    throw new SystemErrorException(mName + "bad param(s) on entry.");
	}
	non_blank = false;

	could_be_quote_string = true;

	while ( ( ! done ) && ( ! this.end_of_file ) && ( ! this.abort_scan ) )
	{
	    next_char = this.get_next_char();

	    if ( escape )
	    {
		escape = false;

		switch ( next_char )
		{
		    case '\\':
			non_blank = true;
			break;

		    case '"':
			/* can't have embedded double quotes in quote strings */
			non_blank = true;
			could_be_quote_string = false;
			break;

		    default:  /* warning - illegal escape sequence */
			/* issue a warning ... */
			post_warning_message(ILLEGAL_ESC_SEQ_IN_QUOTE_STR_WARN,
				"The sequence was: \\" + next_char + "\r");
			/* ... and convert next_char to a '_' */
			next_char = '_';

			non_blank = true;
			break;
		}

		/* finally, save the character (possibly altered) to the token */

		token.save_char_to_token(next_char);
	    }
	    else if ( next_char == '\\' )
	    {
		escape = true;
	    }
	    else if ( next_char == '"' )
	    {
		done = true;

		token.code = STRING_TOK;
		token.aux = TEXT_QSTRING_FLAG;

		if ( could_be_quote_string )
		{
			(token.aux) |= QSTRING_FLAG;
		}

		if ( non_blank )
		{
			(token.aux) |= NONBLANK_FLAG;
		}
	    }
	    else if ( isgraph(next_char) || ( next_char == ' ' ) )
	    {
		token.save_char_to_token(next_char);

		if ( ! isspace(next_char) )
		{
			non_blank = true;
		}
	    }
	    else if ( next_char == '\010' ) /* i.e. next_char == 0x08 == back space */
	    {
		/* Convert it to a '_' and issue a warning. */

		token.save_char_to_token('_');

		post_warning_message(ILLEGAL_CHAR_IN_QUOTE_STR_WARN,
			"The illegal character was: " + next_char + 
			" (HEX 0x08)\r");

		non_blank = true;
	    }
	    else
	    {
		if ( ! isspace(next_char) )
		{
		    non_blank = true;
		}

		could_be_quote_string = false;

		token.save_char_to_token(next_char);
	    }
	} /* end while */
	
	if ( ! done )
	{
	    if ( this.end_of_file )
	    {
		token.code = EOF_TOK;

		post_error_message(UNEXPECTED_END_OF_FILE_ERR,
			"EOF was encountered in a string.  Unterminated string?\r",
		        true, true);
	    }
	    else if ( ! this.abort_scan )
	    {
		throw new SystemErrorException(mName + 
			"Why did we exit the while?!?!");
	    }
	}

	return;

    } /* macshapa_odb_reader::read_string_token() */


    /*************************************************************************
     *
     * read_symbol_token()
     *
     * This method is called to scan a symbol token -- that is a token that
     * contains the name of a formal argument, a nominal, a predicate, or a
     * column variable name.
     *
     * In the open database file format grammar, symbols are immediately
     * recognizable as they must be bracketed with '|' characters.  Note that
     * the '|' characters not part of the symbol name -- rather they are an
     * escape sequence that prevents Common Lisp from converting lower case
     * letters in the symbol name into upper case.
     *
     * This function scans the symbol name, and performs the following 
     * operations as it goes:
     *
     * 1) Discards the bracketing '|' characters, and converts the escape
     *    sequences "\|" and "\\" into the characters '|' and '\' respectively.
     *
     * 2) Loads the resulting string into the token.
     *
     * 3) Keeps track of the characters that appear in the symbol name as they
     *    reflect on the possible type of the symbol (i.e. nominal, predicate,
     *    etc), and then loads token_ptr->aux with flags indicating the possible
     *    type.  If the symbol contains character(s) that are illegal for all
     *    flavors of symbols, the function issues either a warning or an error
     *    depending on the severity of the lapse, and if appropriate, replaces
     *    the illegal character with a legal one.
     *
     *                                                   - 6/7/08
     *
     * Parameters:
     *
     *	  - first_char:  First character in the symbol.
     *
     *    - token:  Reference to the instance of Token in which the new token
     *		is to be stored.
     *
     * Returns:  Void.
     *
     * Changes:
     *
     *    - None.
     *
     *************************************************************************/

    private void read_symbol_token(char first_char,
				   Token token)
        throws SystemErrorException,
	       java.io.IOException
    {
        final String mName = "macshapa_odb_reader::read_symbol_token(): ";
	char last_char;
	char next_char;
	boolean could_be_column;
	boolean could_be_nominal;
	boolean could_be_pred;
	boolean done;
	boolean escape;
	boolean scanning_first_char;

	done = false;
	escape = false;

	if ( ( first_char != '|' ) || ( token == null ) )
	{
	    throw new SystemErrorException(mName + "bad param(s) on entry");
	}
	else
	{
	    if ( this.lookahead_char == '<' ) /* if anything, it is a formal */
	    {				      /* argument.	             */
		scanning_first_char = true;
		next_char = first_char;

		while ( ( ! done ) && 
			( ! this.end_of_file ) && 
			( ! this.abort_scan ) )
		{
		    last_char = next_char;
		    next_char = this.get_next_char();

		    if ( escape )
		    {
			escape = false;

			switch ( next_char )
			{
			    case '\\':
			    case '|':
				/**
				 * no action required - we save the 
				 * character below 
				 */
				break;

			    default:  /* warning - illegal escape sequence */
				 /* issue a warning ... */
				post_warning_message(
				    ILLEGAL_ESCAPE_SEQ_IN_SYMBOL_WARN,
				    "The sequence was: \\" + next_char + "\r");
			        /* ... and convert next_char to a '_' */
				next_char = '_';
				break;
			}

			/**
			 * finally, save the character (possibly altered) 
			 * to the token 
			 */

			token.save_char_to_token(next_char);
		    }
		    else if ( next_char == '\\' )
		    {
			escape = true;
		    }
		    else if ( next_char == '<' )
		    {
			if ( ! scanning_first_char ) /* '<' only allowed as the first character */
			{
			    /* Convert it to a '_' and issue a warning. */
			    token.save_char_to_token('_');
			    post_warning_message(ILLEGAL_CHAR_IN_SYMBOL_WARN,
				    "The illegal character was: '<'.\r");
			}
			else
			{
			    token.save_char_to_token(next_char);
			}
		    }
		    else if ( next_char == '>' )
		    {
			if ( this.lookahead_char != '|' ) /* '>' only allowed as final char */
			{
			    /* Convert it to a '_' and issue a warning. */
			    token.save_char_to_token('_');
			    post_warning_message(ILLEGAL_CHAR_IN_SYMBOL_WARN,
				    "The illegal character was: '>'.\r");
			}
			else
			{
			    token.save_char_to_token(next_char);
			}
		    }
		    else if ( next_char == '|' )
		    {
			done = true;

			if ( last_char == '>' )
			{
			    token.code = SYMBOL_TOK;
			    token.aux = FORMAL_ARG_FLAG;
			}
			else  /* the formal argument wasn't terminated with a '>' */
			{
			    /**
			     * In theory, we could patch this up by appending 
			     * a '>' to the formal argument name, and 
			     * continuing.  However, in most of the scenarios 
			     * that I can think of that lead to this error, 
			     * doing so will simply cause a cascade of further 
			     * errors.  Hence I issue a fatal error and quit.  
			     */

			    token.code = ERROR_TOK;

			    post_error_message(UNTERMINATED_FORMAL_ARG_ERR,
					       null, true, true);
			}
		    }
		    else if ( isgraph(next_char) )
		    {
			if ( ( next_char != '(' ) && ( next_char != ')' ) &&
			     ( next_char != '"' ) && ( next_char != ',' ) )
			{
			    token.save_char_to_token(next_char);
			}
			else
			{
			    /* Convert it to a '_' and issue a warning. */
			    token.save_char_to_token('_');
			    post_warning_message(ILLEGAL_CHAR_IN_SYMBOL_WARN,
				    "The illegal character was '" + next_char +
				    "'\r");
			}
		    }
		    else if ( next_char == '\r' )
		    {
			done = true;
			token.code = ERROR_TOK;

			/** 
			 * new line in a symbol is probably caused by a failure 
			 * to terminate a symbol with a '|' character.  In this 
			 * case we are probably hosed so issue a fatal error.
			 */

			post_error_message(NEW_LINE_IN_SYMBOL_ERR, 
				           null, true, true);
		    }
		    else /* illegal character */
		    {
			/* Convert it to a '_' and issue a warning. */
			token.save_char_to_token('_');
			post_warning_message(ILLEGAL_CHAR_IN_SYMBOL_WARN,
				"The illegal character was: '" + next_char +
				"' (HEX 0x" + 
				Integer.toHexString(((int)next_char)) + 
				")\n");
		    }

		    scanning_first_char = false;

		} /* end while */
	    }
	    else /* must be either a nominal, a predicate name, or a */
	    {    /* column variable name.                            */
		could_be_column = true;
		could_be_nominal = true;
		could_be_pred = true;

		scanning_first_char = true;

		while ( ( ! done ) && 
			( ! this.end_of_file ) && 
			( ! this.abort_scan ) )
		{
		    next_char = this.get_next_char();

		    if ( escape )
		    {
			escape = false;

			switch ( next_char )
			{
			    case '\\':
			    case '|':
				/**
				 * no action required - we save the 
				 * character below 
				 */
				break;

			    default:  /* warning - illegal escape sequence */
				/* issue a warning ... */
				post_warning_message(
					ILLEGAL_ESCAPE_SEQ_IN_SYMBOL_WARN,
					"The sequence was: '" + next_char +
					"'\n");
			        /* ... and convert next_char to a '_' */
				next_char = '_';
				break;
			}

			/**
			 * finally, save the character (possibly altered) 
			 * to the token 
			 */

			token.save_char_to_token(next_char);
		    }
		    else if ( next_char == '\\' )
		    {
			    escape = true;
		    }
		    else if ( next_char == ' ' )
		    {
			if ( scanning_first_char ) /* leading white space */
			{
			    /* issue warning and convert to '_' */

			    post_warning_message(LEADING_WS_IN_SYMBOL_ERR,
						 null);
			    next_char = '_';
			}
			else if ( this.lookahead_char == '|' ) /* trailing   */
			{                                      /* whitespace */
			    /* issue warning and convert to '_' */

			    post_warning_message(TRAILING_WS_IN_SYMBOL_ERR,
						 null);
			    next_char = '_';
			}
			else /* internal whitespace */
			{
			    could_be_pred = false;
			}

			token.save_char_to_token(next_char);
		    }
		    else if ( next_char == '|' )
		    {
			done = true;

			if ( scanning_first_char )
			{
			    /**
			     * we have encountered a zero length symbol.  
			     * I can't think of a good way of recovering, 
			     * so issue a fatal error.
			     */

			    token.code = ERROR_TOK;

			    post_error_message(ZERO_LENGTH_SYMBOL_ERR,
					       null, true, true);
			}
			else
			{
			    token.code = SYMBOL_TOK;
			    token.aux = 0;

			    if ( could_be_column )
			    {
				(token.aux) |= COLUMN_FLAG;
			    }

			    if ( could_be_nominal )
			    {
				(token.aux) |= NOMINAL_FLAG;
			    }

			    if ( could_be_pred )
			    {
				(token.aux) |= PRED_FLAG;
			    }
			}
		    }
		    else if ( isgraph(next_char) )
		    {
			if ( ( next_char != '(' ) && ( next_char != ')' ) &&
			     ( next_char != '"' ) && ( next_char != ',' ) &&
			     ( next_char != '<' ) && ( next_char != '>' ) )
			{
			    token.save_char_to_token(next_char);
			}
			else /* illegal char - issue warning & convert to '_' */
			{
			    post_warning_message(ILLEGAL_CHAR_IN_SYMBOL_WARN,
				    "The illegal character was: '" + next_char +
				    "'\n");
			    token.save_char_to_token('_');
			}
		    }
		    else if ( next_char == '\r' )
		    {
			done = true;
			token.code = ERROR_TOK;

			/**
			 * new line in a symbol is probably caused by a failure 
			 * to terminate a symbol with a '|' character.  In this 
			 * case we are probably hosed so issue a fatal error.
			 */

			post_error_message(NEW_LINE_IN_SYMBOL_ERR, 
				           null, true, true);
		    }
		    else /* illegal character */
		    {
			/* Convert it to a '_' and issue a warning. */
			token.save_char_to_token('_');
			post_warning_message(ILLEGAL_CHAR_IN_SYMBOL_WARN,
				"The illegal character was: '" + next_char +
				"' (HEX 0x" +  
				Integer.toHexString(((int)next_char)) + 
				")\n");
		    }

		    scanning_first_char = false;

		} /* end while */
	    }

	    if ( ! done )
	    {
		if ( this.end_of_file )
		{
		    token.code = EOF_TOK;

		    post_error_message(UNEXPECTED_END_OF_FILE_ERR,
				       "EOF was encountered in a symbol.\n",
				       true, true);
		}
		else if ( ! this.abort_parse ) /* panic */
		{
		    throw new SystemErrorException(mName + 
			    "Why did we exit the while?!?!");
		}
	    }
	}

	return;

    } /* macshapa_odb_reader::read_symbol_token() */


    /*************************************************************************
     *
     * read_token()
     *
     * Read the next token from the input file, and load it into the instance
     * of Token referenced by token.
     *
     *                                                   - 6/7/08
     *
     * Parameters:
     *
     *	  - token:  Reference to the instance of Token in which the new token
     *		is to be stored.
     *
     * Returns:  Void.
     *
     * Changes:
     *
     *    - None.
     *
     *************************************************************************/

    private void read_token(Token token)
        throws SystemErrorException,
	       java.io.IOException
    {
        final String mName = "macshapa_odb_reader::read_token(): ";
	char next_char;

	if ( token == null )
	{
	    throw new SystemErrorException(mName + "token null on entry.");
	}
	else if ( this.abort_scan ) 
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_scan true on entry.");
	}
	else
	{
	    if ( this.end_of_file )
	    {
		token.code = EOF_TOK;
	    }
	    else
	    {
		next_char = get_non_blank();

		token.str.delete(0, token.str.length() - 1);

		if ( ( this.end_of_file ) && ( isspace(next_char) ) )
		{
		    /**
		     * get_non_blank() encountered the EOF before it 
		     * found a non-blank character -- generate an EOF token.
		     */
		    token.code = EOF_TOK;
		}
		else if ( next_char == '(' ) /* left parenthesis */
		{
		    token.code = L_PAREN_TOK;
		    token.save_char_to_token('(');
		}
		else if ( next_char == ')' ) /* right parenthesis */
		{
		    token.code = R_PAREN_TOK;
		    token.save_char_to_token(')');
		}
		else if ( next_char == '|' ) /* symbol */
		{
		    read_symbol_token(next_char, token);
		}
		else if ( next_char == '"' ) /* string */
		{
		    read_string_token(next_char, token);
		}
		else if ( ( next_char == '+' ) ||
			  ( next_char == '-' ) ||
			  ( next_char == '.' ) ||
			  ( isdigit(next_char) ) ) /* integer or float */
		{
		    read_numeric_token(next_char, token);
		}
		else if ( isupper(next_char) ) /* boolean or a-list tag */
		{
		    read_boolean_or_alist_tag_token(next_char,token);
		}
		else if ( next_char == '<' ) /* private value */
		{
		    read_private_value_token(next_char, token);
		}
		else if ( islower(next_char) )
		{
		    read_reserved_word_token(next_char, token);
		}
		else if ( next_char == '\'' ) /* single quote */
		{
		    token.code = QUOTE_TOK;
		    token.save_char_to_token('\'');
		}
		else /* error - fatal since I don't see how to recover from it. */
		{
		    token.code = ERROR_TOK;
		    post_error_message(UNKNOWN_TOKEN_TYPE_ERR,
				       null, true, true);
		}
	    }
	}

	return;

    } /* macshapa_odb_reader::read_token() */
     
        
    /***************************************************************************************************/
    /******************************************* Parser: ***********************************************/
    /***************************************************************************************************/

    /***************************************************************************************************
     *                                                                                                
     *                               Organizational Overview
     *                                                                                                
     * Source code for the recursive descent parser used in the open database read function.  The 
     * code is devided into several sections .  The first (old odbr_parse.c) contains the top level 
     * routines of the recursive descent parser, and utility routines shared by various section of 
     * the parser.  The remaining functions are divided along functional lines between the following 
     * sections:                                    
     *                                                                                                
     *		2) Methods for parsing the header (old odbr_parse_header.c)
     *          3) Methods for parsing the user section (old odbr_parse_user.c)
     *          4) Methods for parsing the query section (old odbr_parse_query.c)
     *          5) Methods for parsing the system section (old odbr_parse_system.c)
     *          6) Methods for parsing predicates (old odbr_parse_pred.c)                                                     
     *                                                                                                
     * These sections contain methods specific to the HEADER>, USER>, QUERY>, SYSTEM> sections         
     * of the open database file, and predicate values respectively.
     *                                                                                                
     * Methods in each section are listed in alphabetical order.                                       
     *                                                                                                
     * The following chart shows an abreviated version of the calling hierarchy of the parser.        
     * In particular, calls to odbr_get_next_token(), error and warning message display               
     * routines, and other than top level data insertion routines are not shown.                      
     *                                                                                                
     * parse_db()                                                                               
     *    parse_db_body()                                                                       
     *     +-parse_header_section()                                                             
     *     |  +-parse_header_alist()                                                            
     *     |  |  +-parse_db_version_attribute()                                                 
     *     |  |  |    parse_arbitrary_list() R                                                  
     *     |  |  |    discard_excess_alist_entry_values()                                       
     *     |  |  |       parse_arbitrary_list() R                                               
     *     |  |  +-parse_db_name_attribute()                                                    
     *     |  |  |    parse_arbitrary_list() R                                                  
     *     |  |  |    discard_excess_alist_entry_values()                                       
     *     |  |  |       parse_arbitrary_list() R                                               
     *     |  |  +-parse_max_errors_attribute()                                                 
     *     |  |  |    parse_arbitrary_list() R                                                  
     *     |  |  |    discard_excess_alist_entry_values()                                       
     *     |  |  |       parse_arbitrary_list() R                                               
     *     |  |  +-parse_max_warnings_attribute()                                               
     *     |  |  |    parse_arbitrary_list() R                                                  
     *     |  |  |    discard_excess_alist_entry_values()                                       
     *     |  |  |       parse_arbitrary_list() R                                               
     *     |  |  +-parse_debug_level_attribute()                                                
     *     |  |  |    parse_arbitrary_list() R                                                  
     *     |  |  |    discard_excess_alist_entry_values()                                       
     *     |  |  |       parse_arbitrary_list() R                                               
     *     |  |  +-parse_unknown_alist_entry()                                                  
     *     |  |  |    parse_arbitrary_list() R                                                  
     *     |  |  +-parse_arbitrary_list() R                                                     
     *     |  +-discard_excess_alist_entry_values()                                             
     *     |       parse_arbitrary_list() R                                                     
     *     +-parse_user_section()                                                               
     *     |  +-parse_user_alist()                                                              
     *     |  |  +-discard_excess_alist_entry_values()                                          
     *     |  |  |    parse_arbitrary_list() R                                                  
     *     |  |  +-parse_preds_list_attribute()                                                 
     *     |  |  |    discard_excess_alist_entry_values()                                       
     *     |  |  |       parse_arbitrary_list() R                                               
     *     |  |  |    parse_pred_def_list()                                                     
     *     |  |  |     +-parse_pred_def()                                                       
     *     |  |  |     |    coerce_symbol_token_to_pred_name()                                  
     *     |  |  |     |    append_str_to_ibuf()                                                
     *     |  |  |     |    CSymbols::lookupPred()                                                    
     *     |  |  |     |    parse_pred_def_alist()                                              
     *     |  |  |     |     +-parse_variable_length_attribute()                                
     *     |  |  |     |     |    parse_arbitrary_list() R                                      
     *     |  |  |     |     |    discard_excess_alist_entry_values()                           
     *     |  |  |     |     |       parse_arbitrary_list() R                                   
     *     |  |  |     |     +-parse_pred_formal_arg_list_attribute()                           
     *     |  |  |     |     |    parse_pred_formal_arg_list()                                  
     *     |  |  |     |     |       parse_arbitrary_list() R                                   
     *     |  |  |     |     |       append_str_to_ibuf()                                       
     *     |  |  |     |     |    parse_arbitrary_list() R                                      
     *     |  |  |     |     |    discard_excess_alist_entry_values()                           
     *     |  |  |     |     |       parse_arbitrary_list() R                                   
     *     |  |  |     |     |    append_str_to_ibuf()                                          
     *     |  |  |     |     +-parse_unknown_alist_entry()                                      
     *     |  |  |     |     |    parse_arbitrary_list() R                                      
     *     |  |  |     |     +-parse_arbitrary_list() R                                         
     *     |  |  |     |     +-append_str_to_ibuf()                                             
     *     |  |  |     |     +-dump_predicate_definition_to_listing()                           
     *     |  |  |     |     +-CSymbols::DefPred()                                                    
     *     |  |  |     |    parse_arbitrary_list() R                                            
     *     |  |  |     +-parse_arbitrary_list() R                                               
     *     |  |  +-parse_s_var_decs_attribute()                                                 
     *     |  |  |    discard_excess_alist_entry_values()                                       
     *     |  |  |       parse_arbitrary_list() R                                               
     *     |  |  |    parse_s_var_dec_list()                                                    
     *     |  |  |     +-parse_s_var_dec()                                                      
     *     |  |  |     |    coerce_symbol_token_to_spreadsheet_variable_name()                  
     *     |  |  |     |    append_str_to_ibuf()                                                
     *     |  |  |     |    CSymbols::lookupPred()                                                    
     *     |  |  |     |    CSymbols::lookupVar()                                                     
     *     |  |  |     |    parse_s_var_dec_alist()                                             
     *     |  |  |     |     +-parse_variable_length_attribute()                                
     *     |  |  |     |     |    parse_arbitrary_list() R                                      
     *     |  |  |     |     |    discard_excess_alist_entry_values()                           
     *     |  |  |     |     |       parse_arbitrary_list() R                                   
     *     |  |  |     |     +-parse_s_var_formal_arg_list_attribute()                          
     *     |  |  |     |     |    parse_s_var_formal_arg_list()                                 
     *     |  |  |     |     |       append_str_to_ibuf()                                       
     *     |  |  |     |     |       parse_arbitrary_list() R                                   
     *     |  |  |     |     |    append_str_to_ibuf()                                          
     *     |  |  |     |     |    discard_excess_alist_entry_values()                           
     *     |  |  |     |     |       parse_arbitrary_list() R                                   
     *     |  |  |     |     +-parse_s_var_type_attribute()                                     
     *     |  |  |     |     |    parse_arbitrary_list() R                                      
     *     |  |  |     |     |    discard_excess_alist_entry_values()                           
     *     |  |  |     |     |       parse_arbitrary_list() R                                   
     *     |  |  |     |     +-parse_s_var_col_width_attribute()                                
     *     |  |  |     |     |    parse_arbitrary_list() R                                      
     *     |  |  |     |     |    discard_excess_alist_entry_values()                           
     *     |  |  |     |     |       parse_arbitrary_list() R                                   
     *     |  |  |     |     +-parse_unknown_alist_entry()                                      
     *     |  |  |     |     |    parse_arbitrary_list() R                                      
     *     |  |  |     |     +-parse_arbitrary_list() R                                         
     *     |  |  |     |     +-append_str_to_ibuf()                                             
     *     |  |  |     |     +-dump_s_var_definition_to_listing()                               
     *     |  |  |     |     +-CSymbols::DefVar()                                                     
     *     |  |  |     |    parse_arbitrary_list() R                                            
     *     |  |  |     +-parse_arbitrary_list() R                                               
     *     |  |  +-parse_s_var_defs_attribute()                                                 
     *     |  |  |    discard_excess_alist_entry_values()                                       
     *     |  |  |       parse_arbitrary_list() R                                               
     *     |  |  |    parse_s_var_def_list()                                                    
     *     |  |  |     +-parse_s_var_def()                                                      
     *     |  |  |     |    coerce_symbol_token_to_spreadsheet_variable_name()                  
     *     |  |  |     |    CSymbols::lookupVar()                                                     
     *     |  |  |     |    parse_s_var_def_alist()                                             
     *     |  |  |     |     +-parse_s_var_def_cells_attribute()                                
     *     |  |  |     |     |  +-parse_s_var_cell_list()                                       
     *     |  |  |     |     |  |    parse_s_var_cell()                                         
     *     |  |  |     |     |  |     +-parse_s_var_cell_onset_attribute()                      
     *     |  |  |     |     |  |     |    parse_arbitrary_list() R                             
     *     |  |  |     |     |  |     |    discard_excess_alist_entry_values()                  
     *     |  |  |     |     |  |     |       parse_arbitrary_list() R                          
     *     |  |  |     |     |  |     +-parse_s_var_cell_offset_attribute()                     
     *     |  |  |     |     |  |     |    parse_arbitrary_list() R                             
     *     |  |  |     |     |  |     |    discard_excess_alist_entry_values()                  
     *     |  |  |     |     |  |     |       parse_arbitrary_list() R                          
     *     |  |  |     |     |  |     +-parse_s_var_cell_value_attribute()                      
     *     |  |  |     |     |  |     |  +-parse_text_cell_value()                              
     *     |  |  |     |     |  |     |  |    append_str_to_ibuf()                              
     *     |  |  |     |     |  |     |  |    parse_arbitrary_list() R                          
     *     |  |  |     |     |  |     |  +-parse_nominal_cell_value()                           
     *     |  |  |     |     |  |     |  |    coerce_nominal_token_to_cell_nominal()            
     *     |  |  |     |     |  |     |  |    append_str_to_ibuf()                              
     *     |  |  |     |     |  |     |  |    parse_arbitrary_list() R                          
     *     |  |  |     |     |  |     |  +-parse_integer_cell_value()                           
     *     |  |  |     |     |  |     |  |    coerce_float_token_to_integer()                   
     *     |  |  |     |     |  |     |  |    append_str_to_ibuf()                              
     *     |  |  |     |     |  |     |  |    parse_arbitrary_list() R                          
     *     |  |  |     |     |  |     |  +-parse_float_cell_value()                             
     *     |  |  |     |     |  |     |  |    append_str_to_ibuf()                              
     *     |  |  |     |     |  |     |  |    parse_arbitrary_list() R                          
     *     |  |  |     |     |  |     |  +-parse_pred_cell_value()                              
     *     |  |  |     |     |  |     |  |    append_str_to_ibuf()                              
     *     |  |  |     |     |  |     |  |    parse_pred_value() R                              
     *     |  |  |     |     |  |     |  |       coerce_symbol_token_to_pred_name()             
     *     |  |  |     |     |  |     |  |       CSymbols::lookupPred()                               
     *     |  |  |     |     |  |     |  |       parse_arbitrary_list() R                       
     *     |  |  |     |     |  |     |  |       coerce_text_qstring_to_qstring()               
     *     |  |  |     |     |  |     |  |       append_str_to_ibuf()                           
     *     |  |  |     |     |  |     |  |       parse_time_stamp()                             
     *     |  |  |     |     |  |     |  |          parse_arbitrary_list() R                    
     *     |  |  |     |     |  |     |  |          discard_excess_alist_entry_values()         
     *     |  |  |     |     |  |     |  |             parse_arbitrary_list() R                 
     *     |  |  |     |     |  |     |  |       CSymbols::AddDefaultArgToPred()                      
     *     |  |  |     |     |  |     |  |       discard_excess_pred_value_arguments()          
     *     |  |  |     |     |  |     |  |          parse_arbitrary_list() R                    
     *     |  |  |     |     |  |     |  +-parse_matrix_cell_value()                            
     *     |  |  |     |     |  |     |  |    append_str_to_ibuf()                              
     *     |  |  |     |     |  |     |  |    parse_time_stamp()                                
     *     |  |  |     |     |  |     |  |       parse_arbitrary_list() R                       
     *     |  |  |     |     |  |     |  |       discard_excess_alist_entry_values()            
     *     |  |  |     |     |  |     |  |          parse_arbitrary_list() R                    
     *     |  |  |     |     |  |     |  |    coerce_text_qstring_to_qstring()                  
     *     |  |  |     |     |  |     |  |    parse_pred_value() R                              
     *     |  |  |     |     |  |     |  |       coerce_symbol_token_to_pred_name()             
     *     |  |  |     |     |  |     |  |       CSymbols::lookupPred()                               
     *     |  |  |     |     |  |     |  |       parse_arbitrary_list() R                       
     *     |  |  |     |     |  |     |  |       coerce_text_qstring_to_qstring()               
     *     |  |  |     |     |  |     |  |       append_str_to_ibuf()                           
     *     |  |  |     |     |  |     |  |       parse_time_stamp()                             
     *     |  |  |     |     |  |     |  |          parse_arbitrary_list() R                    
     *     |  |  |     |     |  |     |  |          discard_excess_alist_entry_values()         
     *     |  |  |     |     |  |     |  |             parse_arbitrary_list() R                 
     *     |  |  |     |     |  |     |  |       CSymbols::AddDefaultArgToPred()                      
     *     |  |  |     |     |  |     |  |       discard_excess_pred_value_arguments()          
     *     |  |  |     |     |  |     |  |          parse_arbitrary_list() R                    
     *     |  |  |     |     |  |     |  |    CSymbols::lookupPred()                                  
     *     |  |  |     |     |  |     |  +-append_str_to_ibuf()                                 
     *     |  |  |     |     |  |     |  +-discard_excess_alist_entry_values()                  
     *     |  |  |     |     |  |     |       parse_arbitrary_list() R                          
     *     |  |  |     |     |  |     +-dump_s_var_cell_definition_to_listing()                 
     *     |  |  |     |     |  |     +-CSheetPane::CreateEmptyCell()                                 
     *     |  |  |     |     |  |     +-CSymbols::InsertGivenVarCellCstr()                            
     *     |  |  |     |     |  |     +-parse_arbitrary_list() R                                
     *     |  |  |     |     |  |     +-parse_unknown_alist_entry()                             
     *     |  |  |     |     |  |          parse_arbitrary_list() R                             
     *     |  |  |     |     |  +-discard_excess_alist_entry_values()                           
     *     |  |  |     |     |       parse_arbitrary_list() R                                   
     *     |  |  |     |     +-parse_s_var_def_vocab_attribute()                                
     *     |  |  |     |     |    parse_vocab_list()                                            
     *     |  |  |     |     |       CSymbols::lookupPred()                                           
     *     |  |  |     |     |       CSymbols::PredDefined()                                          
     *     |  |  |     |     |       AddLocalVocab()                                                  
     *     |  |  |     |     |       parse_arbitrary_list() R                                   
     *     |  |  |     |     |    discard_excess_alist_entry_values()                           
     *     |  |  |     |     |       parse_arbitrary_list() R                                   
     *     |  |  |     |     +-parse_unknown_alist_entry()                                      
     *     |  |  |     |     |    parse_arbitrary_list() R                                      
     *     |  |  |     |     +-parse_arbitrary_list() R                                         
     *     |  |  |     |    parse_arbitrary_list() R                                            
     *     |  |  |     +-parse_arbitrary_list() R                                               
     *     |  |  +-parse_unknown_alist_entry()                                                  
     *     |  |  |    parse_arbitrary_list() R                                                  
     *     |  |  +-parse_arbitrary_list() R                                                     
     *     |  +-discard_excess_alist_entry_values()                                             
     *     |       parse_arbitrary_list() R                                                     
     *     +-parse_query_section()                                                              
     *     |  +-parse_query_list()                                                              
     *     |  |  +-append_str_to_ibuf()                                                         
     *     |  |  +-parse_pred_value() R                                                         
     *     |  |  |    coerce_symbol_token_to_pred_name()                                        
     *     |  |  |    CSymbols::lookupPred()                                                          
     *     |  |  |    parse_arbitrary_list() R                                                  
     *     |  |  |    coerce_text_qstring_to_qstring()                                          
     *     |  |  |    append_str_to_ibuf()                                                      
     *     |  |  |    parse_time_stamp()                                                        
     *     |  |  |       parse_arbitrary_list() R                                               
     *     |  |  |       discard_excess_alist_entry_values()                                    
     *     |  |  |          parse_arbitrary_list() R                                            
     *     |  |  |    CSymbols::AddDefaultArgToPred()                                                 
     *     |  |  |    discard_excess_pred_value_arguments()                                     
     *     |  |  |       parse_arbitrary_list() R                                               
     *     |  |  +-dump_s_var_cell_definition_to_listing()                                      
     *     |  |  +-CVocabCell::IVocabCell()                                                           
     *     |  |  +-CSymbols::InsertGivenVarCellCstr()                                                 
     *     |  +-discard_excess_alist_entry_values()                                             
     *     |       parse_arbitrary_list() R                                                     
     *     +-parse_system_section()                                                             
     *     |  +-parse_system_alist()                                                            
     *     |  |  +-parse_shapa_pane_vars_attribute()                                            
     *     |  |  |  +-parse_shapa_pane_var_list()                                               
     *     |  |  |  |    CSymbols::lookupVar()                                                        
     *     |  |  |  |    CShapaPane::FindColumnIndexOfVar()                                           
     *     |  |  |  |    CShapaPane::InsertVarIntoColumn()                                            
     *     |  |  |  |    parse_arbitrary_list() R                                               
     *     |  |  |  |    CShapaPane::UpdateShapaPaneTemporal()                                        
     *     |  |  |  +-add_all_user_svars_to_shapa_pane()                                        
     *     |  |  |  |    CVariablesDoc::FirstUserVariable()                                           
     *     |  |  |  |    CSymbols::lookupVar()                                                        
     *     |  |  |  |    CShapaPane::FindColumnIndexOfVar()                                           
     *     |  |  |  |    CShapaPane::InsertVarIntoColumn()                                            
     *     |  |  |  |    CVariablesDoc::NextUserVariable()                                            
     *     |  |  |  |    CShapaPane::UpdateShapaPaneTemporal()                                        
     *     |  |  |  +-discard_excess_alist_entry_values()                                       
     *     |  |  |       parse_arbitrary_list() R                                               
     *     |  |  +-parse_groups_attribute()                                                     
     *     |  |  |    parse_group_list()                                                        
     *     |  |  |       parse_group_member()                                                   
     *     |  |  |          CSymbols::lookupVar()                                                     
     *     |  |  |          GetVarCellByOrd()   (* in variables.c *)                                  
     *     |  |  |          parse_arbitrary_list() R                                            
     *     |  |  |       CSheetPane::GroupCell()                                                      
     *     |  |  |    discard_excess_alist_entry_values()                                       
     *     |  |  |       parse_arbitrary_list() R                                               
     *     |  |  +-parse_alignments_attribute()                                                 
     *     |  |  |  +-parse_alignments_list()                                                   
     *     |  |  |  |  +-parse_alignment_var()                                                  
     *     |  |  |  |  |    CSymbols::lookupPred()                                                    
     *     |  |  |  |  |    CSymbols::lookupVar()                                                     
     *     |  |  |  |  |    parse_arbitrary_list() R                                            
     *     |  |  |  |  |    parse_alignment_var_alist()                                         
     *     |  |  |  |  |     +-parse_variable_length_attribute()                                
     *     |  |  |  |  |     |    parse_arbitrary_list() R                                      
     *     |  |  |  |  |     |    discard_excess_alist_entry_values()                           
     *     |  |  |  |  |     |       parse_arbitrary_list() R                                   
     *     |  |  |  |  |     +-parse_s_var_formal_arg_list_attribute()                          
     *     |  |  |  |  |     |    parse_s_var_formal_arg_list()                                 
     *     |  |  |  |  |     |       append_str_to_ibuf()                                       
     *     |  |  |  |  |     |       parse_arbitrary_list() R                                   
     *     |  |  |  |  |     |    append_str_to_ibuf()                                          
     *     |  |  |  |  |     |    discard_excess_alist_entry_values()                           
     *     |  |  |  |  |     |       parse_arbitrary_list() R                                   
     *     |  |  |  |  |     +-parse_s_var_type_attribute()                                     
     *     |  |  |  |  |     |    parse_arbitrary_list() R                                      
     *     |  |  |  |  |     |    discard_excess_alist_entry_values()                           
     *     |  |  |  |  |     |       parse_arbitrary_list() R                                   
     *     |  |  |  |  |     +-parse_s_var_col_width_attribute()                                
     *     |  |  |  |  |     |    parse_arbitrary_list() R                                      
     *     |  |  |  |  |     |    discard_excess_alist_entry_values()                           
     *     |  |  |  |  |     |       parse_arbitrary_list() R                                   
     *     |  |  |  |  |     +-parse_unknown_alist_entry()                                      
     *     |  |  |  |  |     |    parse_arbitrary_list() R                                      
     *     |  |  |  |  |     +-parse_arbitrary_list() R                                         
     *     |  |  |  |  |     +-dump_s_var_definition_to_listing()                               
     *     |  |  |  |  |     +-CSymbols::DefVar()                                                     
     *     |  |  |  |  |     +-parse_s_var_def_cells_attribute()                                
     *     |  |  |  |  |        +-parse_s_var_cell_list()                                       
     *     |  |  |  |  |        |    parse_s_var_cell()                                         
     *     |  |  |  |  |        |     +-parse_s_var_cell_onset_attribute()                      
     *     |  |  |  |  |        |     |    parse_arbitrary_list() R                             
     *     |  |  |  |  |        |     |    discard_excess_alist_entry_values()                  
     *     |  |  |  |  |        |     |       parse_arbitrary_list() R                          
     *     |  |  |  |  |        |     +-parse_s_var_cell_offset_attribute()                     
     *     |  |  |  |  |        |     |    parse_arbitrary_list() R                             
     *     |  |  |  |  |        |     |    discard_excess_alist_entry_values()                  
     *     |  |  |  |  |        |     |       parse_arbitrary_list() R                          
     *     |  |  |  |  |        |     +-parse_s_var_cell_value_attribute()                      
     *     |  |  |  |  |        |     |  +-parse_text_cell_value()                              
     *     |  |  |  |  |        |     |  |    append_str_to_ibuf()                              
     *     |  |  |  |  |        |     |  |    parse_arbitrary_list() R                          
     *     |  |  |  |  |        |     |  +-parse_nominal_cell_value()                           
     *     |  |  |  |  |        |     |  |    coerce_nominal_token_to_cell_nominal()            
     *     |  |  |  |  |        |     |  |    append_str_to_ibuf()                              
     *     |  |  |  |  |        |     |  |    parse_arbitrary_list() R                          
     *     |  |  |  |  |        |     |  +-parse_integer_cell_value()                           
     *     |  |  |  |  |        |     |  |    coerce_float_token_to_integer()                   
     *     |  |  |  |  |        |     |  |    append_str_to_ibuf()                              
     *     |  |  |  |  |        |     |  |    parse_arbitrary_list() R                          
     *     |  |  |  |  |        |     |  +-parse_float_cell_value()                             
     *     |  |  |  |  |        |     |  |    append_str_to_ibuf()                              
     *     |  |  |  |  |        |     |  |    parse_arbitrary_list() R                          
     *     |  |  |  |  |        |     |  +-parse_pred_cell_value()                              
     *     |  |  |  |  |        |     |  |    append_str_to_ibuf()                              
     *     |  |  |  |  |        |     |  |    parse_pred_value() R                              
     *     |  |  |  |  |        |     |  |       coerce_symbol_token_to_pred_name()             
     *     |  |  |  |  |        |     |  |       CSymbols::lookupPred()                               
     *     |  |  |  |  |        |     |  |       parse_arbitrary_list() R                       
     *     |  |  |  |  |        |     |  |       coerce_text_qstring_to_qstring()               
     *     |  |  |  |  |        |     |  |       append_str_to_ibuf()                           
     *     |  |  |  |  |        |     |  |       parse_time_stamp()                             
     *     |  |  |  |  |        |     |  |          parse_arbitrary_list() R                    
     *     |  |  |  |  |        |     |  |          discard_excess_alist_entry_values()         
     *     |  |  |  |  |        |     |  |             parse_arbitrary_list() R                 
     *     |  |  |  |  |        |     |  |       CSymbols::AddDefaultArgToPred()                      
     *     |  |  |  |  |        |     |  |       discard_excess_pred_value_arguments()          
     *     |  |  |  |  |        |     |  |          parse_arbitrary_list() R                    
     *     |  |  |  |  |        |     |  +-parse_matrix_cell_value()                            
     *     |  |  |  |  |        |     |  |    append_str_to_ibuf()                              
     *     |  |  |  |  |        |     |  |    parse_time_stamp()                                
     *     |  |  |  |  |        |     |  |       parse_arbitrary_list() R                       
     *     |  |  |  |  |        |     |  |       discard_excess_alist_entry_values()            
     *     |  |  |  |  |        |     |  |          parse_arbitrary_list() R                    
     *     |  |  |  |  |        |     |  |    coerce_text_qstring_to_qstring()                  
     *     |  |  |  |  |        |     |  |    parse_pred_value() R                              
     *     |  |  |  |  |        |     |  |       coerce_symbol_token_to_pred_name()             
     *     |  |  |  |  |        |     |  |       CSymbols::lookupPred()                               
     *     |  |  |  |  |        |     |  |       parse_arbitrary_list() R                       
     *     |  |  |  |  |        |     |  |       coerce_text_qstring_to_qstring()               
     *     |  |  |  |  |        |     |  |       append_str_to_ibuf()                           
     *     |  |  |  |  |        |     |  |       parse_time_stamp()                             
     *     |  |  |  |  |        |     |  |          parse_arbitrary_list() R                    
     *     |  |  |  |  |        |     |  |          discard_excess_alist_entry_values()         
     *     |  |  |  |  |        |     |  |             parse_arbitrary_list() R                 
     *     |  |  |  |  |        |     |  |       CSymbols::AddDefaultArgToPred()                      
     *     |  |  |  |  |        |     |  |       discard_excess_pred_value_arguments()          
     *     |  |  |  |  |        |     |  |          parse_arbitrary_list() R                    
     *     |  |  |  |  |        |     |  |    CSymbols::lookupPred()                                  
     *     |  |  |  |  |        |     |  +-append_str_to_ibuf()                                 
     *     |  |  |  |  |        |     |  +-discard_excess_alist_entry_values()                  
     *     |  |  |  |  |        |     |       parse_arbitrary_list() R                          
     *     |  |  |  |  |        |     +-dump_s_var_cell_definition_to_listing()                 
     *     |  |  |  |  |        |     +-CSheetPane::CreateEmptyCell()                                 
     *     |  |  |  |  |        |     +-CSymbols::InsertGivenVarCellCstr()                            
     *     |  |  |  |  |        |     +-parse_arbitrary_list() R                                
     *     |  |  |  |  |        |     +-parse_unknown_alist_entry()                             
     *     |  |  |  |  |        |          parse_arbitrary_list() R                             
     *     |  |  |  |  |        +-discard_excess_alist_entry_values()                           
     *     |  |  |  |  |             parse_arbitrary_list() R                                   
     *     |  |  |  |  +-parse_arbitrary_list() R                                               
     *     |  |  |  +-discard_excess_alist_entry_values()                                       
     *     |  |  |       parse_arbitrary_list() R                                               
     *     |  |  +-parse_import_formats_attribute()                                             
     *     |  |  |  +-parse_imp_formats_list()                                                  
     *     |  |  |  |  +-parse_imp_format_list()                                                
     *     |  |  |  |  |  +-allocFormat()       (* in import_internal.c *)                            
     *     |  |  |  |  |  +-saveCstr()          (* in mem.c *)                                        
     *     |  |  |  |  |  +-strTrim()           (* in import_io.c *)                                  
     *     |  |  |  |  |  +-AddFormatListEnd()  (* in import_internal.c *)                            
     *     |  |  |  |  |  +-parse_imp_format_list_alist()                                       
     *     |  |  |  |  |  |  +-parse_imp_prods_attribute()                                      
     *     |  |  |  |  |  |  |  +-parse_imp_prod_list()                                         
     *     |  |  |  |  |  |  |  |    parse_imp_prod()                                           
     *     |  |  |  |  |  |  |  |     +-parse_imp_prod_name_attribute()                         
     *     |  |  |  |  |  |  |  |     |    saveCstr()          (* in mem.c *)                         
     *     |  |  |  |  |  |  |  |     |    strTrim()           (* in import_io.c *)                   
     *     |  |  |  |  |  |  |  |     |    parse_arbitrary_list() R                             
     *     |  |  |  |  |  |  |  |     |    discard_excess_alist_entry_values()                  
     *     |  |  |  |  |  |  |  |     |       parse_arbitrary_list() R                          
     *     |  |  |  |  |  |  |  |     +-parse_imp_prod_pattern_attribute()                      
     *     |  |  |  |  |  |  |  |     |    saveCstr()          (* in mem.c *)                         
     *     |  |  |  |  |  |  |  |     |    skipspace()         (* in import_io.c *)                   
     *     |  |  |  |  |  |  |  |     |    parse_arbitrary_list() R                             
     *     |  |  |  |  |  |  |  |     |    discard_excess_alist_entry_values()                  
     *     |  |  |  |  |  |  |  |     |       parse_arbitrary_list() R                          
     *     |  |  |  |  |  |  |  |     +-parse_imp_prod_hard_prec_attribute()                    
     *     |  |  |  |  |  |  |  |     |    parse_arbitrary_list() R                             
     *     |  |  |  |  |  |  |  |     |    discard_excess_alist_entry_values()                  
     *     |  |  |  |  |  |  |  |     |       parse_arbitrary_list() R                          
     *     |  |  |  |  |  |  |  |     +-parse_imp_prod_is_shown_attribute()                     
     *     |  |  |  |  |  |  |  |     |    parse_arbitrary_list() R                             
     *     |  |  |  |  |  |  |  |     |    discard_excess_alist_entry_values()                  
     *     |  |  |  |  |  |  |  |     |       parse_arbitrary_list() R                          
     *     |  |  |  |  |  |  |  |     +-parse_imp_prod_is_traced_attribute()                    
     *     |  |  |  |  |  |  |  |     |    parse_arbitrary_list() R                             
     *     |  |  |  |  |  |  |  |     |    discard_excess_alist_entry_values()                  
     *     |  |  |  |  |  |  |  |     |       parse_arbitrary_list() R                          
     *     |  |  |  |  |  |  |  |     +-parse_imp_prod_insert_actions_attribute()               
     *     |  |  |  |  |  |  |  |     |  +-parse_imp_prod_insert_actions_list()                 
     *     |  |  |  |  |  |  |  |     |  |  +-parse_imp_prod_insert_action_alist()              
     *     |  |  |  |  |  |  |  |     |  |  |  +-parse_imp_prod_insert_action_var_attribute()   
     *     |  |  |  |  |  |  |  |     |  |  |  |    saveCstr()     (* in mem.c *)                     
     *     |  |  |  |  |  |  |  |     |  |  |  |    parse_arbitrary_list() R                    
     *     |  |  |  |  |  |  |  |     |  |  |  |    discard_excess_alist_entry_values()         
     *     |  |  |  |  |  |  |  |     |  |  |  |       parse_arbitrary_list() R                 
     *     |  |  |  |  |  |  |  |     |  |  |  +-parse_imp_prod_insert_action_onset_attribute()  
     *     |  |  |  |  |  |  |  |     |  |  |  |    saveCstr()     (* in mem.c *)                     
     *     |  |  |  |  |  |  |  |     |  |  |  |    parse_arbitrary_list() R                    
     *     |  |  |  |  |  |  |  |     |  |  |  |    discard_excess_alist_entry_values()         
     *     |  |  |  |  |  |  |  |     |  |  |  |       parse_arbitrary_list() R                 
     *     |  |  |  |  |  |  |  |     |  |  |  +-parse_imp_prod_insert_action_offset_attribute() 
     *     |  |  |  |  |  |  |  |     |  |  |  |    saveCstr()     (* in mem.c *)                     
     *     |  |  |  |  |  |  |  |     |  |  |  |    parse_arbitrary_list() R                    
     *     |  |  |  |  |  |  |  |     |  |  |  |    discard_excess_alist_entry_values()         
     *     |  |  |  |  |  |  |  |     |  |  |  |       parse_arbitrary_list() R                 
     *     |  |  |  |  |  |  |  |     |  |  |  +-parse_imp_prod_insert_action_val_attribute()   
     *     |  |  |  |  |  |  |  |     |  |  |  |    saveCstr()     (* in mem.c *)                     
     *     |  |  |  |  |  |  |  |     |  |  |  |    parse_arbitrary_list() R                    
     *     |  |  |  |  |  |  |  |     |  |  |  |    discard_excess_alist_entry_values()         
     *     |  |  |  |  |  |  |  |     |  |  |  |       parse_arbitrary_list() R                 
     *     |  |  |  |  |  |  |  |     |  |  |  +-parse_unknown_alist_entry()                    
     *     |  |  |  |  |  |  |  |     |  |  |  |    parse_arbitrary_list() R                    
     *     |  |  |  |  |  |  |  |     |  |  |  +-parse_arbitrary_list()                         
     *     |  |  |  |  |  |  |  |     |  |  |  +-allocAction()       (* in import_internal.c *)       
     *     |  |  |  |  |  |  |  |     |  |  |  +-freeCstr()          (* in mem.c *)                   
     *     |  |  |  |  |  |  |  |     |  |  +-AddActionListEnd()     (* in import_internal.c *)       
     *     |  |  |  |  |  |  |  |     |  +-discard_excess_alist_entry_values()                  
     *     |  |  |  |  |  |  |  |     |       parse_arbitrary_list() R                          
     *     |  |  |  |  |  |  |  |     +-parse_imp_prod_program_actions_attribute()              
     *     |  |  |  |  |  |  |  |     |  +-parse_imp_prod_program_actions_list()                
     *     |  |  |  |  |  |  |  |     |  |  +-parse_imp_prod_program_action_alist()             
     *     |  |  |  |  |  |  |  |     |  |  |  +-parse_imp_prod_program_action_text_attribute() 
     *     |  |  |  |  |  |  |  |     |  |  |  |    saveCstr()       (* in mem.c *)                   
     *     |  |  |  |  |  |  |  |     |  |  |  |    parse_arbitrary_list() R                    
     *     |  |  |  |  |  |  |  |     |  |  |  |    discard_excess_alist_entry_values()         
     *     |  |  |  |  |  |  |  |     |  |  |  |       parse_arbitrary_list() R                 
     *     |  |  |  |  |  |  |  |     |  |  |  +-parse_unknown_alist_entry()                    
     *     |  |  |  |  |  |  |  |     |  |  |  |    parse_arbitrary_list() R                    
     *     |  |  |  |  |  |  |  |     |  |  |  +-parse_arbitrary_list() R                       
     *     |  |  |  |  |  |  |  |     |  |  |  +-allocAction()       (* in import_internal.c *)       
     *     |  |  |  |  |  |  |  |     |  |  |  +-freeCstr()          (* in mem.c *)                   
     *     |  |  |  |  |  |  |  |     |  |  +-AddActionListEnd()     (* in import_internal.c *)       
     *     |  |  |  |  |  |  |  |     |  +-discard_excess_alist_entry_values()                  
     *     |  |  |  |  |  |  |  |     |  |    parse_arbitrary_list() R                          
     *     |  |  |  |  |  |  |  |     +-parse_arbitrary_list() R                                
     *     |  |  |  |  |  |  |  |     +-parse_unknown_alist_entry()                             
     *     |  |  |  |  |  |  |  |     |    parse_arbitrary_list() R                             
     *     |  |  |  |  |  |  |  |     +-allocProd()                  (* in import_internal.c *)       
     *     |  |  |  |  |  |  |  |     +-AddProdListEnd()             (* in import_internal.c *)       
     *     |  |  |  |  |  |  |  |     +-freeCstr()                   (* in mem.c *)                   
     *     |  |  |  |  |  |  |  |     +-freeActionList()             (* in import_internal.c *)       
     *     |  |  |  |  |  |  |  +-discard_excess_alist_entry_values()                           
     *     |  |  |  |  |  |  |       parse_arbitrary_list() R                                   
     *     |  |  |  |  |  |  +-parse_unknown_alist_entry()                                      
     *     |  |  |  |  |  |  |    parse_arbitrary_list() R                                      
     *     |  |  |  |  |  |  +-parse_arbitrary_list() R                                         
     *     |  |  |  |  |  +-parse_arbitrary_list()                                              
     *     |  |  |  |  +-parse_arbitrary_list() R                                               
     *     |  |  |  +-discard_excess_alist_entry_values()                                       
     *     |  |  |       parse_arbitrary_list() R                                               
     *     |  |  +-parse_unknown_alist_entry()                                                  
     *     |  |  |    parse_arbitrary_list() R                                                  
     *     |  |  +-parse_arbitrary_list() R                                                     
     *     |  +-discard_excess_alist_entry_values()                                             
     *     |  |    parse_arbitrary_list() R                                                     
     *     |  +-add_all_user_svars_to_shapa_pane()                                              
     *     |       CVariablesDoc::FirstUserVariable()                                                 
     *     |       CSymbols::lookupVar()                                                              
     *     |       CShapaPane::FindColumnIndexOfVar()                                                 
     *     |       CShapaPane::InsertVarIntoColumn()                                                  
     *     |       CVariablesDoc::NextUserVariable()                                                  
     *     |       CShapaPane::UpdateShapaPaneTemporal()                                              
     *     +-parse_unknown_alist_entry()                                                        
     *     |    parse_arbitrary_list() R                                                        
     *     +-parse_arbitrary_list() R                                                           
     *                                                                                                
     * Functions whose names are followed by a 'R' are recursive.                                     
     *                                                                                                
     *                                                       - 6/8/08                         
     *                                                                                                
     ***************************************************************************************************/

    
    /*** Parser section 1 -- top level and utility methods ***/
    
    /*************************************************************************
     *
     * discard_excess_alist_entry_values()
     *
     * By the book, an a-list entry has the following format:
     *
     *		'(' <tag> <value> ')'
     *
     * where <tag> is an a-list entry name, and <value> is the value associated
     * with the a-list entry.
     *
     * However, it is always possible that the user may include more
     * than two items in the list that constitutes an a-list entry.  In such
     * cases, we simply discard all the excess items until we come to the
     * closing parenthesis.
     *
     * This issue arises in all a-list entries, and thus this method is
     * used to deal with this issue.  In essence, it reads and then discards
     * all items until it comes to the right parenthesis that ends the a-list
     * entry.
     *
     *                                                      - 6/8/08
     *
     * Parameters:
     *
     *	  - None.
     *
     * Returns:  Void.
     *
     * Changes:
     *
     *    - None.
     *
     *************************************************************************/

    private void discard_excess_alist_entry_values()
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = 
		"macshapa_odb_reader::discard_excess_alist_entry_values()";
	boolean done;

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse true on entry");
        }
	
	if ( this.l0_tok.code == R_PAREN_TOK )
	{
	    throw new SystemErrorException(mName + 
		    "a-list entry appears not to contain excess values.");
	}

	/* go ahead and read and then discard the excess values */

	done = false;

	while ( ( ! this.abort_parse ) && ( ! done ) )
	{
	    switch ( this.l0_tok.code )
	    {
		    case R_PAREN_TOK:
			done = true;
			break;

		    case L_PAREN_TOK:
			parse_arbitrary_list();
			break;

		    case ERROR_TOK:
		    case SYMBOL_TOK:
		    case INT_TOK:
		    case FLOAT_TOK:
		    case STRING_TOK:
		    case BOOL_TOK:
		    case ALIST_LABEL_TOK:
		    case PRIVATE_VAL_TOK:
		    case SETF_TOK:
		    case DB_VAR_TOK:
		    case QUOTE_TOK:
			get_next_token();
			break;

		    case EOF_TOK:
			done = true;
			post_error_message(UNEXPECTED_END_OF_FILE_ERR,
			    "EOF encountered in an alist entry with excess values.\n",
			    true, true);
			break;

		    default:
			throw new SystemErrorException(mName + 
				"Encountered unknown token type.");
			// break; /* commented out to keep the compiler happy */
	    }
	}

        return;

    } /* macshapa_odb_reader::discard_excess_alist_entry_values() */


    /*************************************************************************
     *
     * parse_arbitrary_list()
     *
     * This function exists to allow us ignore the contents of an arbitrary
     * list of tokens.  While it is quite useful in the development phase,
     * its primary purpose is in ignoring the contents of unknown and/or
     * unexpected a-list entries.  The production describing an arbitrary
     * list is given below:
     *
     *	    <arbitrary_val> --> <private_value>               |
     *                          <text_quote_string>           |
     *                          <non_empty_text_quote_string> |
     *                          <quote_string>                |
     *                          <nominal>                     |
     *                          <pred_name>                   |
     *                          <formal_arg>                  |
     *                          <boolean>                     |
     *                          <integer>                     |
     *                          <float>                       |
     *                          <arbitrary_list>              |
     *                          '''                           |
     *                          'setf'                        |
     *                          'macshapa-db'
     *
     *
     *                                                  - 6/8/08
     *
     * Parameters:
     *
     *	  - None.
     *
     * Returns:  Void.
     *
     * Changes:
     *
     *    - None.
     *
     *************************************************************************/

    private void parse_arbitrary_list()
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = "macshapa_odb_reader::parse_arbitrary_list()";
	boolean done = false;

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse true on entry");
	}
	
	/* parse the data base body */

	/* first parse the leading left parenthesis */

	if ( this.l0_tok.code == L_PAREN_TOK )
	{
	    get_next_token();
	}
	else /* panic - we shouldn't have been called unless the next token is a '(' */
	{
	    throw new SystemErrorException(mName + 
		    "this.l0_tok.code isnt L_PAREN_TOK.");
	}

	while ( ( ! this.abort_parse ) && ( ! done ) )
	{
	    switch ( this.l0_tok.code )
	    {
		case R_PAREN_TOK:
		    done = true;
		    get_next_token();
		    break;

		case L_PAREN_TOK:
		    parse_arbitrary_list();
		    break;

		case ERROR_TOK:
		case SYMBOL_TOK:
		case INT_TOK:
		case FLOAT_TOK:
		case STRING_TOK:
		case BOOL_TOK:
		case ALIST_LABEL_TOK:
		case PRIVATE_VAL_TOK:
		case SETF_TOK:
		case DB_VAR_TOK:
		case QUOTE_TOK:
		    get_next_token();
		    break;

		case EOF_TOK:
		    done = true;
		    post_error_message(UNEXPECTED_END_OF_FILE_ERR,
			    "EOF encountered in an arbitrary list that was " +
			    "being discarded.\n", true, true);
		    break;

		default:
		    throw new SystemErrorException(mName + 
			    "Encountered unknown token type.");
		    // break; /* commented out to keep the compiler happy */
	    }
	}

	return;

    } /* macshapa_odb_reader::parse_arbitrary_list() */


    /*************************************************************************
     *
     * parse_db()
     *
     * Top level function for the recursive descent parser.  According to the
     * open database grammar document, this function is responsible for dealing
     * with the productions:
     *
     *	    <db> --> <tag_string> '(' 'setf' 'macshapa-db' ''' <db_body> ')'
     *
     *      <tag_string> --> '; MacSHAPA Open Database' <cr>
     *
     *      <cr> --> 0x0D   (* the ASCII carriage return character *)
     *
     * However, the <tag_string> is used only to identify the file as an
     * open database file.  Since this has already been done by the time
     * we reach this point, we allow the lexer to treat the <tag_string>
     * like any other comment and filter it out.  Thus the actual production
     * parsed by this function is:
     *
     *      <db> --> '(' 'setf' 'macshapa-db' ''' <db_body> ')'
     *
     * None of the terminals contained in this production yield any information
     * that must be loaded into the MacSHAPA internal representation of the
     * database, so all we do here is verify that they appear, and issue error
     * messages if they do not.
     *
     *
     *                                             - 6/8/08
     *
     * Parameters:
     *
     *	  - None.
     *
     * Returns:  Void.
     *
     * Changes:
     *
     *    - None.
     *
     *************************************************************************/

    private void parse_db()
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = "macshapa_db_reader::parse_db()";
	final String db_start_mssg = 
	    "A database file must start with \"( setf macshapa-db '( ... \".\n";

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse true on entry");
	}
	
	/* read the opening left parenthesis */
	if ( this.l0_tok.code == L_PAREN_TOK )
	{
	    get_next_token();
	}
	else if ( this.l0_tok.code == SETF_TOK )
	{
	    post_warning_message(LEFT_PAREN_EXPECTED_WARN, db_start_mssg);
	}
	else /* flag an error - fatal alas */
	{
	    post_error_message(LEFT_PAREN_EXPECTED_ERR, 
		               db_start_mssg, false, true);
	}

	/* read the setf */
	if ( ! this.abort_parse )
	{
	    if ( this.l0_tok.code == SETF_TOK )
	    {
		get_next_token();
	    }
	    else if ( this.l0_tok.code == DB_VAR_TOK )
	    {
		post_warning_message(SETF_EXPECTED_WARN, db_start_mssg);
	    }
	    else /* flag an error - fatal alas */
	    {
		post_error_message(SETF_EXPECTED_ERR, db_start_mssg,
				   false, true);
	    }
	}

	/* read the database variable */
	if ( ! this.abort_parse )
	{
	    if ( this.l0_tok.code == DB_VAR_TOK )
	    {
		get_next_token();
	    }
	    else if ( this.l0_tok.code == QUOTE_TOK )
	    {
		post_warning_message(DB_VAR_EXPECTED_WARN, db_start_mssg);
	    }
	    else /* flag an error - fatal alas */
	    {
		post_error_message(DB_VAR_EXPECTED_ERR, db_start_mssg,
				   false, true);
	    }
	}

	/* read the quote */
	if ( ! this.abort_parse )
	{
	    if ( this.l0_tok.code == QUOTE_TOK )
	    {
		get_next_token();
	    }
	    else if ( this.l0_tok.code == L_PAREN_TOK )
	    {
		post_warning_message(QUOTE_EXPECTED_WARN, db_start_mssg);
	    }
	    else /* flag an error - fatal alas */
	    {
		post_error_message(QUOTE_EXPECTED_ERR, db_start_mssg,
				   false, true);
	    }
	}

	/* parse the body of the database */
	if ( ! this.abort_parse )
	{
	    parse_db_body();
	}

	/* read the terminating right parenthesis */
	if ( ! this.abort_parse )
	{
	    if ( this.l0_tok.code == R_PAREN_TOK )
	    {
		/* after this call, the l0_tok should be an EOF token, */
		/* but we won't check it.                              */
		get_next_token();
	    }
	    else
	    {
		post_warning_message(RIGHT_PAREN_EXPECTED_WARN, null);
	    }
	}

	return;

    } /* macshapa_odb_reader::parse_db() */


    /*************************************************************************
     *
     * parse_db_body()
     *
     * Parse the top level a-list in the open macshapa database.  This list
     * must contain four sections, header, user, query, and system, in that
     * order.  Undefined and unexpected a-list entries may also appear at
     * any point in the list, but they must be ignored.  If duplicate entries
     * are detected, the first entry is treated as the "real" entry, and a
     * warning must be issued.
     *
     * The production describing the database body follows.  Note that it
     * does not include provision for unknown/unexpected a-list entries or
     * for duplicate entries.
     *
     *	    <db_body> --> '(' <header_section> <user_section>
     *                        <query_section> <system_section> ')'
     *
     *                                                   - 6/8/08
     *
     * Parameters:
     *
     *	  - None
     *
     * Returns:  Void.
     *
     * Changes:
     *
     *    - None.
     *
     *************************************************************************/

    private void parse_db_body()
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = "macshapa_odb_reader::parse_db_body()";
	boolean done = false;
	int next_tag_index = 0; /* index into the expected_tag_codes array */
	final int expected_tag_codes[] = { HEADER_LABEL,
					   USER_LABEL,
					   QUERY_LABEL,
				           SYSTEM_LABEL};

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse TRUE on entry");
	}
	
	/* parse the data base body */

	/* first parse the leading left parenthesis */

	if ( this.l0_tok.code == L_PAREN_TOK )
	{
	    get_next_token();
	}
	else /* flag an error - fatal alas */
	{
	    post_error_message(LEFT_PAREN_EXPECTED_ERR, null, false, true);
	}

	while ( ( ! this.abort_parse ) &&
		( ! done ) )
	{
	    if ( this.l0_tok.code == L_PAREN_TOK )
	    {
		if ( this.l1_tok.code == ALIST_LABEL_TOK )
		{
		    if ( ( next_tag_index < 4 ) &&
			 ( this.l1_tok.aux == expected_tag_codes[next_tag_index] ) )
		    {
			/* parse the alist entry */
			switch ( next_tag_index )
			{
			    case 0: /* Header section */
				parse_header_section();
				break;

			    case 1: /* User section */
				parse_user_section();
				break;

			    case 2: /* Query section */
				parse_query_section();
				break;

			    case 3: /* System section */
				parse_system_section();
				break;

			    default:
				throw new SystemErrorException(mName + 
					"next_tag_index out of range.");
			        /* commented out to keep the compiler happy */
				// break;
			}
			next_tag_index++;
		    }
		    else /* Unknown, unexpected, out of order, or duplicate a-list entry */
		    {
			switch ( this.l1_tok.aux )
			{
			    case HEADER_LABEL:
				if ( next_tag_index > 0 )
				{
				    post_warning_message(
					    DUPLICATE_ALIST_ENTRY_WARN,
					    "Duplicate HEADER> entry\n");

				    if ( ! this.abort_parse )
				    {
					parse_unknown_alist_entry();
				    }
				}
				else
				{
				    throw new SystemErrorException(mName + 
					"This else clause should be unreachable.");
				}
				break;

			    case USER_LABEL:
				if ( next_tag_index > 1 )
				{
				    post_warning_message(
					    DUPLICATE_ALIST_ENTRY_WARN,
					    "Duplicate USER> entry.\"\n");

				    if ( ! this.abort_parse )
				    {
					parse_unknown_alist_entry();
				    }
				}
				else
				{
				    done = true;

				    post_error_message(
					MISSING_OR_OUT_OF_ORDER_ALIST_ENTRY_ERR,
					"HEADER> a-list entry missing from " +
					"the database body a-list?",
			                false, true);
				}
				break;

			    case QUERY_LABEL:
				if ( next_tag_index > 2 )
				{
				    post_warning_message(
					    DUPLICATE_ALIST_ENTRY_WARN,
					    "Duplicate QUERY> entry.\n");

				    if ( ! this.abort_parse )
				    {
					parse_unknown_alist_entry();
				    }
				}
				else
				{
				    done = true;

				    post_error_message(
					MISSING_OR_OUT_OF_ORDER_ALIST_ENTRY_ERR,
					"HEADER> or USER> a-list entry(s) " +
					"missing from the database body a-list?",
			                false,  true);
				}
				break;

			    case SYSTEM_LABEL:
				if ( next_tag_index > 3 )
				{
				    post_warning_message(
					    DUPLICATE_ALIST_ENTRY_WARN,
					    "Duplicate SYSTEM> entry.\n");

				    if ( ! this.abort_parse )
				    {
					parse_unknown_alist_entry();
				    }
				}
				else
				{
				    done = true;

				    post_error_message(
					MISSING_OR_OUT_OF_ORDER_ALIST_ENTRY_ERR,
					"HEADER>, USER>, or QUERY> a-list " +
					"entry(s) missing from the database " +
					"body a-list?", false, true);
				}
				break;

			    default:
				post_warning_message(
					UNKNOWN_OR_UNEXPECTED_ALIST_ENTRY_WARN,
					"The entry is located in the database " +
					"body a-list.\n");

				if ( ! this.abort_parse )
				{
				    parse_unknown_alist_entry();
				}
				break;
			}
		    }
		}
		else /* a-list contains a list that is not an a-list entry -- read */
		     /* it & discard it                                            */
		{
		    post_warning_message(NON_ALIST_ENTRY_LIST_IN_ALIST_WARN,
			"The list is located in the database body a-list.\r");

		    if ( ! this.abort_parse )
		    {
			parse_arbitrary_list();
		    }
		}
	    }
	    else if ( this.l0_tok.code == R_PAREN_TOK )
	    {
		done = true;
		get_next_token();

		if ( ( ! this.abort_parse ) &&
		     ( next_tag_index < 4 ) )
		{
			post_error_message(REQUIRED_ALIST_ENTRIES_MISSING_ERR,
			    "Required entry(s) missing from the database " +
			    "body a-list.", false, true);
		}
	    }
	    else if ( this.l0_tok.code == EOF_TOK )
	    {
		done = true;
		post_error_message(UNEXPECTED_END_OF_FILE_ERR,
			           "EOF occurred the database body a-list.\r",
				   true, true);
	    }
	    else /* this.l0_tok.code isnt '(', ')', or EOF */
	    {
		post_warning_message(NON_ALIST_ENTRY_ATOM_IN_ALIST_WARN,
			"The atom was detected in the database body a-list.\n");

		if ( ! this.abort_parse )
		{
		    get_next_token();
		}
	    }
	}

	return;

    } /* parse_db_body() */


    /*************************************************************************
     *
     * parse_unknown_alist_entry()
     *
     * This function exists to allow us ignore the contents of an unknown
     * a-list entry.  While the contents of the a-list entry is arbitrary,
     * it must must be syntactically correct -- that it is must consist of
     * an opening parenthesis, a alist entry name tag, a value which must
     * be either an atom or a list, followed by a closing parenthesis.  The
     * productions describing an unknown a-list entry follow:
     *
     *	    <unknown_alist_entry> --> '(' <unknown_a_list_entry_label>
     *                                    <unknown_a_list_entry_val> ')'
     *
     *      <unknown_a_list_entry_label> --> <a_list_entry_label>
     *
     *      <a_list_entry_val> --> <private_value>               |
     *                             <text_quote_string>           |
     *                             <non_empty_text_quote_string> |
     *                             <quote_string>                |
     *                             <nominal>                     |
     *                             <pred_name>                   |
     *                             <formal_arg>                  |
     *                             <boolean>                     |
     *                             <integer>                     |
     *                             <float>                       |
     *                             <arbitrary_list>
     *
     *
     *                                                 - 6/8/08
     *
     * Parameters:
     *
     *	  - None
     *
     * Returns:  Void.
     *
     * Changes:
     *
     *    - None.
     *
     *******************************************************************************/

    private void parse_unknown_alist_entry()
        throws SystemErrorException,
               java.io.IOException
    {
	final String mName = "macshapa_odb_reader::parse_unknown_alist_entry()";
	boolean done = false;
	boolean excess_values = false;

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse TRUE on entry");
	}
	
	/* parse the unknown a-list entry -- note that we discard its contents */

	/* first parse the leading left parenthesis */

	if ( this.l0_tok.code == L_PAREN_TOK )
	{
	    get_next_token();
	}
	else /* system error - we shouldn't have been called unless the next token is a '(' */
	{
	    throw new SystemErrorException(mName + 
		    "this.l0_tok.code isnt L_PAREN_TOK.");
	}

	/* read the a-list entry name */
	if ( ! this.abort_parse )
	{
	    if ( this.l0_tok.code == ALIST_LABEL_TOK )
	    {
		get_next_token();
	    }
	    else /* system error - we shouldn't have been called unless the next token is an a-list tag */
	    {
		throw new SystemErrorException(mName + 
			"this.l0_tok.code isnt ALIST_LABEL_TOK.");
	    }
	}

	/* read the a-list entry value */
	if ( ! this.abort_parse )
	{
	    switch ( this.l0_tok.code )
	    {
		case R_PAREN_TOK:
		    post_warning_message(EMPTY_ALIST_ENTRY_WARN,
			    "The empty a-list entry was unknown or " +
			    "unexpected, hence the point is probably moot.\n");
		    break;

		case L_PAREN_TOK:
		    parse_arbitrary_list();
		    break;

		case ERROR_TOK:
		case SYMBOL_TOK:
		case INT_TOK:
		case FLOAT_TOK:
		case STRING_TOK:
		case BOOL_TOK:
		case PRIVATE_VAL_TOK:
		    get_next_token();
		    break;

		case ALIST_LABEL_TOK:
		case SETF_TOK:
		case DB_VAR_TOK:
		case QUOTE_TOK:
		    post_warning_message(ILLEGAL_UNKNOWN_ALIST_ENTRY_VAL_WARN,
					 null);

		    if ( ! this.abort_parse )
		    {
			get_next_token();
		    }
		    break;

		case EOF_TOK:
		    done = true;
		    post_error_message(UNEXPECTED_END_OF_FILE_ERR,
			    "EOF encountered in an unknown or " +
			    "unexpected a-list entry.\n",
			    true, true);
		    break;

		default:
		    throw new SystemErrorException(mName +
			    "Encountered unknown token type.");
	            /* commented out to keep the compiler happy */
		    // break;
	    }
	}

	/**
	 * check to see if the a-list entry contains more than one value - if 
	 * it does, consume the excess value(s), and issue a warning.
	 */
	if ( ! this.abort_parse )
	{
	    done = false;
	    excess_values = false;

	    while ( ( ! this.abort_parse ) &&
		    ( ! done ) )
	    {
		switch ( this.l0_tok.code )
		{
		    case R_PAREN_TOK:
			done = true;
			break;

		    case L_PAREN_TOK:
			excess_values = true;
			parse_arbitrary_list();
			break;

		    case ERROR_TOK:
		    case SYMBOL_TOK:
		    case INT_TOK:
		    case FLOAT_TOK:
		    case STRING_TOK:
		    case BOOL_TOK:
		    case SETF_TOK:
		    case DB_VAR_TOK:
		    case QUOTE_TOK:
			excess_values = true;
			get_next_token();
			break;

		    case EOF_TOK:
			done = true;
			post_error_message(UNEXPECTED_END_OF_FILE_ERR,
				"EOF encountered in an unknown or unexpected " +
				"a-list entry.\n", true, true);
			break;

		    default:
			throw new SystemErrorException(mName + 
				"Encountered unknown token type.");
                        /* commented out to keep the compiler happy */
			// break;
		}
	    }

	    if ( ( ! this.abort_parse ) &&
		 ( done ) && ( excess_values ) )
	    {
		post_warning_message(EXCESS_VALUES_IN_ALIST_ENTRY_WARN,
			"Excess values encountered in an unexpected or " +
			"unknown a-list entry.\n");
	    }
	}

	/* finally, consume the closing parenthesis */
	if ( ! this.abort_parse )
	{
	    if ( this.l0_tok.code == R_PAREN_TOK )
	    {
		get_next_token();
	    }
	    else
	    {
		throw new SystemErrorException(mName +
			"This else clause should be unreachable.");
	    }
	}

	return;

    } /* macshapa_odb_reader::parse_unknown_alist_entry() */


    
    /*** Parser section 2 -- header parsing methods ***/

    /*************************************************************************
     *
     * dump_header_settings_to_string()
     *
     * Debugging routine that constructs a String containing the current 
     * settings of all fields that can be modified by attributes in the 
     * HEADER> section, and returns it.
     *
     *                                            - 6/8/08
     *
     * Parameters:
     *
     *	  - None.
     *
     * Returns:  String containing current settings of all fields that can 
     *	    be modified by the HEADER> section.
     *
     * Changes:
     *
     *    - None.
     *
     *************************************************************************/

    private String dump_header_settings_to_string()
	throws SystemErrorException
    {
	final String mName = 
		"macshapa_odb_reader::dump_header_settings_to_string()";
	String retVal = null;

	/* construct the string */
	retVal = "\n\nCurrent Header Settings:\n" +
	         "db.name = \"" + this.db.getName() + "\"\n" +
	         "odb file format version = " + 
		    this.odb_file_format_version + "\n" +
                 "warning limit = " + this.max_warnings + "\n" +
	         "error limit = " + this.max_errors + "\n" +
	         "debug level = " + this.debug_level + "\n\n";

	return retVal;

    } /* dump_header_settings_to_listing() */

    
    /*************************************************************************
     *
     * parse_db_name_attribute()
     *
     * This method parses the database name attribute, and if successful, sets
     * the name of this.db. This attribute only appears in the a-list 
     * associated with the header section of the open database body.  The 
     * production generating this attribute is given below:
     *
     *	    <db-name-attribute> --> '(' 'NAME>' <quote_string> ')'
     *
     *                                              - 6/8/08
     *
     * Parameters:
     *
     *	  - None.
     *
     * Returns:  Void.
     *
     * Changes:
     *
     *    - None.
     *
     **************************************************************************/

    private void parse_db_name_attribute()
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = "macshapa_odb_reader::parse_db_name_attribute()";
	final String missing_db_name_mssg =
	        "The HEADER> NAME> attribute appears ! to contain a value.  " +
		"The attribute will be ignored.\r";
	final String db_name_type_mismatch_mssg =
	        "The value of the HEADER> NAME> attribute must be a " +
		"quoted string.  The attribute will be ignored.\r";
	final String db_name_empty_mssg =
	        "The string provided in the HEADER> NAME> attribute == " +
		"empty.  The attribute will be ignored.\r";

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse TRUE on entry");
	}
	
	/* parse the version attribute */

	/* first parse the leading left parenthesis */

	if ( (this.l0_tok).code == L_PAREN_TOK )
	{
	    get_next_token();
	}
	else /* system error - we shouldn't have been called unless the next token == a '(' */
	{
	    throw new SystemErrorException(mName +
		    "this.l0_tok.code != L_PAREN_TOK.");
	}

	/* read the a-list entry name */
	if ( ! this.abort_parse )
	{
	    if ( ( (this.l0_tok).code == ALIST_LABEL_TOK ) &&
		 ( (this.l0_tok).aux == NAME_LABEL ) )
	    {
		get_next_token();
	    }
	    else /* we shouldn't have been called unless the next token == an a-list tag */
	    {
		throw new SystemErrorException(mName + "this.l0_tok != NAME>.");
	    }
	}

	/**
	 * read the value associated with the a-list entry & discard 
	 * any excess values 
	 */
	if ( ! this.abort_parse )
	{
	    switch ( (this.l0_tok).code )
	    {
		case STRING_TOK:
		    if ( (this.l0_tok.str.length()) > 0 )
		    {
			this.db.setName(this.l0_tok.str.toString());
		    }
		    else /* the string == empty - issue a warning & ignore the attribute */
		    {
			post_warning_message(EMPTY_QUOTE_STRING_WARN,
		                             db_name_empty_mssg);
		    }

		    if ( ! this.abort_parse )
		    {
			get_next_token();
		    }
		    break;

		case R_PAREN_TOK:
		    post_warning_message(EMPTY_ALIST_ENTRY_WARN,
				         missing_db_name_mssg);
		    break;

		case L_PAREN_TOK:
		    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN,
					 db_name_type_mismatch_mssg);

		    if ( ! this.abort_parse )
		    {
			parse_arbitrary_list();
		    }
		    break;

		case ERROR_TOK:
		case SYMBOL_TOK:
		case FLOAT_TOK:
		case INT_TOK:
		case BOOL_TOK:
		case ALIST_LABEL_TOK:
		case PRIVATE_VAL_TOK:
		case SETF_TOK:
		case DB_VAR_TOK:
		case QUOTE_TOK:
		    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN,
					 db_name_type_mismatch_mssg);

		    if ( ! this.abort_parse )
		    {
			get_next_token();
		    }
		    break;


		case EOF_TOK:
		    post_error_message(UNEXPECTED_END_OF_FILE_ERR,
				       "EOF in the HEADER> NAME> attribute.\n",
				       true, true);
		    break;
	    }
	}

	/* Check for EOF */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code == EOF_TOK )
	    {
		post_error_message(UNEXPECTED_END_OF_FILE_ERR,
			           "EOF in the HEADER> NAME> attribute.\n",
				   true, true);
	    }
	}

	/* discard any excess values that may appear in the NAME> a-list entry */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code != R_PAREN_TOK )
	    {
		discard_excess_alist_entry_values();

		if ( ! this.abort_parse )
		{
		    post_warning_message(EXCESS_VALUES_IN_ALIST_ENTRY_WARN,
					 "Excess values encountered in the " +
					 "HEADER> NAME> attribute.\n");
		}
	    }
	}

	/* read the terminating right parenthesis */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code == R_PAREN_TOK )
	    {
		get_next_token();
	    }
	    else
	    {
		post_warning_message(RIGHT_PAREN_EXPECTED_WARN,
			             "Closing parenthesis missing from the " +
			             "HEADER> NAME> attribute.\r");
	    }
	}

	return;

    } /* macshapa_odb_reader::parse_db_name_attribute() */


    /*************************************************************************
     *
     * parse_db_version_attribute()
     *
     * This method parses the database version attribute, and loads the
     * database version number into this.odb_file_format_version.  This 
     * attribute only appears in the a-list associated with the header 
     * section of the open database body.  The production generating this 
     * attribute is given below:
     *
     *	    <db-version-attribute> --> '(' 'VERSION>' <integer> ')'
     *
     *                                              - 6/9/08
     *
     * Parameters:
     *
     *	  - None
     *
     * Returns:  Void.
     *
     * Changes:
     *
     *    - None.
     *
     *******************************************************************************/

    private void parse_db_version_attribute()
        throws SystemErrorException,
               java.io.IOException
    {
	final String mName = "macshapa_odb_reader::parse_db_version_attribute()";
	final String missing_format_num_mssg =
	    "The HEADER> VERSION> attribute appears ! to contain a value.  " + 
	    "Version # forced to 1.\r";
	final String db_format_type_mismatch_mssg =
	    "The value of the HEADER> VERSION> attribute must be a positive " +
	    "integer.  Version # forced to 1.\r";

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse true on entry");
	}
	
	/* parse the version attribute */
	
	/* first parse the leading left parenthesis */

	if ( (this.l0_tok).code == L_PAREN_TOK )
	{
	    get_next_token();
	}
	else /* we shouldn't have been called unless the next token is a '(' */
	{
	    throw new SystemErrorException(mName + 
		    "(this.l0_tok).code != L_PAREN_TOK.");
	}

	/* read the a-list entry name */
	if ( ! this.abort_parse )
	{
	    if ( ( (this.l0_tok).code == ALIST_LABEL_TOK ) &&
		 ( (this.l0_tok).aux == VERSION_LABEL ) )
	    {
		get_next_token();
	    }
	    else /* we shouldn't have been called unless the next token is an a-list tag */
	    {
		throw new SystemErrorException(mName + 
			"this.l0_tok != VERSION>.");
	    }
	}

	/* read the value associated with the a-list entry & discard any excess values */
	if ( ! this.abort_parse )
	{
	    switch ( (this.l0_tok).code )
	    {
		case INT_TOK:
		    if ( (this.l0_tok).val >= 1 )
		    {
			this.odb_file_format_version = (int)((this.l0_tok).val);
		    }
		    else
		    {
			this.odb_file_format_version = 1;
			post_warning_message(NON_POSITIVE_DB_VERSION_NUM_WARN,
					     null);
		    }

		    if ( ! this.abort_parse )
		    {
			get_next_token();
		    }
		    break;

		case R_PAREN_TOK:
		    post_warning_message(EMPTY_ALIST_ENTRY_WARN,
					 missing_format_num_mssg);
		    break;

		case L_PAREN_TOK:
		    this.odb_file_format_version = 1;
		    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN,
					db_format_type_mismatch_mssg);

		    if ( ! this.abort_parse  )
		    {
			parse_arbitrary_list();
		    }
		    break;

		case ERROR_TOK:
		case SYMBOL_TOK:
		case FLOAT_TOK:
		case STRING_TOK:
		case BOOL_TOK:
		case ALIST_LABEL_TOK:
		case PRIVATE_VAL_TOK:
		case SETF_TOK:
		case DB_VAR_TOK:
		case QUOTE_TOK:
		    this.odb_file_format_version = 1;
		    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN,
					 db_format_type_mismatch_mssg);

		    if ( ! this.abort_parse )
		    {
			get_next_token();
		    }
		    break;


		case EOF_TOK:
		    post_error_message(UNEXPECTED_END_OF_FILE_ERR,
				       "EOF in HEADER> VERSION> attribute.\n",
				       true, true);
		    break;

		default:
		    throw new SystemErrorException(mName + 
			    "Encountered unknown token type.");
	            /* commented out to keep the compiler happy */
		    // break;
	    }
	}

	/* check for EOF */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code == EOF_TOK )
	    {
		post_error_message(UNEXPECTED_END_OF_FILE_ERR,
				   "EOF in HEADER> VERSION> attribute.\n",
				   true, true);
	    }
	}

	/* discard any excess values that may appear in the VERSION> a-list entry */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code != R_PAREN_TOK )
	    {
		discard_excess_alist_entry_values();

		if ( ! this.abort_parse )
		{
		    post_warning_message(EXCESS_VALUES_IN_ALIST_ENTRY_WARN,
			    "Excess values encountered in the HEADER> " +
			    "VERSION> attribute.\n");
		}
	    }
	}


	/* read the terminating right parenthesis */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code == R_PAREN_TOK )
	    {
		get_next_token();
	    }
	    else
	    {
		post_warning_message(RIGHT_PAREN_EXPECTED_WARN,
			"Closing parenthesis missing from the HEADER> " +
			"VERSION> attribute.\n");
	    }
	}

	return;

    } /* macshapa_odb_reader::parse_db_version_attribute() */


    /*************************************************************************
     *
     * parse_debug_level_attribute()
     *
     * This method parses the debug level attribute, and loads the debug 
     * level into this.debug_level.  This attribute only appears in the 
     * a-list associated with the header section of the open database body.  
     * The production generating this attribute is given below:
     *
     *	    <db-version-attribute> --> '(' 'DEBUG-LEVEL>' <integer> ')'
     *
     *                                                   - 6/9/08
     *
     * Parameters:
     *
     *	  - None.
     *
     * Returns:  Void.
     *
     * Changes:
     *
     *    - None.
     *
     *************************************************************************/

    private void parse_debug_level_attribute()
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = "macshapa_odb_reader::parse_debug_level_attribute()";
	final String missing_debug_level_mssg =
		"The HEADER> DEBUG-LEVEL> attribute appears ! to contain a " +
		"value.  Debug level forced to 0.\r";
	final String debug_level_type_mismatch_mssg =
		"The value of the HEADER> DEBUG-LEVEL> attribute must be a " +
		"non-negative integer.  Debug level forced to 0.\r";

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse true on entry");
	}
	
	/* parse the version attribute */

	/* first parse the leading left parenthesis */
	if ( (this.l0_tok).code == L_PAREN_TOK )
	{
	    get_next_token();
	}
	else /* we shouldn't have been called unless the next token == a '(' */
	{
	    throw new SystemErrorException(mName + 
		    "(this.l0_tok).code != L_PAREN_TOK.");
	}

	/* read the a-list entry name */
	if ( ! this.abort_parse )
	{
	    if ( ( (this.l0_tok).code == ALIST_LABEL_TOK ) &&
		 ( (this.l0_tok).aux == DEBUG_LEVEL_LABEL ) )
	    {
		get_next_token();
	    }
	    else /* we shouldn't have been called unless the next token == an a-list tag */
	    {
		throw new SystemErrorException(mName + 
			"this.l0_tok != DEBUG-LEVEL>.");
	    }
	}

	/** 
	 * read the value associated with the a-list entry & discard 
	 * any excess values 
	 */
	if ( ! this.abort_parse  )
	{
	    switch ( (this.l0_tok).code )
	    {
		case INT_TOK:
		    if ( (this.l0_tok).val >= MIN_DEBUG_LEVEL )
		    {
			if ( (this.l0_tok).val <= MAX_DEBUG_LEVEL )
			{
			    this.debug_level = (int)((this.l0_tok).val);
			}
			else
			{
			    this.debug_level = MAX_DEBUG_LEVEL;
			}
		    }
		    else
		    {
			this.debug_level = 0;
			post_warning_message(NEGATIVE_DEBUG_LEVEL_WARN, null);
		    }

		    if ( ! this.abort_parse )
		    {
			get_next_token();
		    }
		    break;

		case R_PAREN_TOK:
		    this.debug_level = MIN_DEBUG_LEVEL;
		    post_warning_message(EMPTY_ALIST_ENTRY_WARN,
					 missing_debug_level_mssg);
		    break;

		case L_PAREN_TOK:
		    this.debug_level = MIN_DEBUG_LEVEL;
		    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN,
					 debug_level_type_mismatch_mssg);

		    if ( ! this.abort_parse )
		    {
			parse_arbitrary_list();
		    }
		    break;

		case ERROR_TOK:
		case SYMBOL_TOK:
		case FLOAT_TOK:
		case STRING_TOK:
		case BOOL_TOK:
		case ALIST_LABEL_TOK:
		case PRIVATE_VAL_TOK:
		case SETF_TOK:
		case DB_VAR_TOK:
		case QUOTE_TOK:
		    this.debug_level = MIN_DEBUG_LEVEL;
		    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN,
					 debug_level_type_mismatch_mssg);

		    if ( ! this.abort_parse )
		    {
			get_next_token();
		    }
		    break;

		case EOF_TOK:
		    post_error_message(UNEXPECTED_END_OF_FILE_ERR,
			    "EOF in HEADER> DEBUG-LEVEL> attribute.\n",
			    true, true);
		    break;

		default:
		    throw new SystemErrorException(mName + 
			    "Encountered unknown token type.");
	            /* commented out to keep the compiler happy */
		    // break;
	    }
	}

	/* check for EOF */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code == EOF_TOK )
	    {
		post_error_message(UNEXPECTED_END_OF_FILE_ERR,
				   "EOF in HEADER> DEBUG-LEVEL> attribute.\n",
				   true, true);
	    }
	}

	/**
	 * discard any excess values that may appear in the 
	 * DEBUG-LEVEL> a-list entry 
	 */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code != R_PAREN_TOK )
	    {
		discard_excess_alist_entry_values();

		if ( ! this.abort_parse )
		{
		    post_warning_message(EXCESS_VALUES_IN_ALIST_ENTRY_WARN,
			    "Excess values encountered in the HEADER> " +
			    "DEBUG-LEVEL> attribute.\n");
		}
	    }
	}

	/* read the terminating right parenthesis */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code == R_PAREN_TOK )
	    {
		get_next_token();
	    }
	    else
	{
		post_warning_message(RIGHT_PAREN_EXPECTED_WARN,
			"Closing parenthesis missing from the HEADER> " +
			"DEBUG-LEVEL> attribute.\n");
	    }
	}

	return;

    } /* macshapa_odb_reader::parse_debug_level_attribute() */


    /*************************************************************************
     *
     * parse_header_alist()
     *
     * This mthod parses the a-list associated with the header section of
     * the open database body.  Structurally, this list is simply a list of
     * a-list entries, each of which is a two element list consisting an
     * a-list entry name and its assocated value.  The productions generating
     * the header section a-list are given below:
     *
     *	    <header-alist> --> '(' <header-attributes> ')'
     *
     *      <header_attributes> --> { <db-version-attribute>
     *                                [<db-name-attribute>]
     *                                [<max-errors-attribute>]
     *                                [<max-warnings-attribute>] }
     *
     *      <db-version-attribute> --> '(' 'VERSION>' <integer> ')'
     *
     *      <db-name-attribute> --> '(' 'NAME>' <db-name> ')'
     *
     *      <db_name> --> <quote_string>
     *
     *      <max-errors-attribute> --> '(' 'MAX-ERRORS>' <integer> ')'
     *
     *      <max-warnings-attribute> --> '(' 'MAX-WARNINGS>' <integer> ')'
     *
     *
     *                                             - 6/9/08
     *
     * Parameters:
     *
     *	  - None.
     *
     * Returns:  Void.
     *
     *  Changes:
     *
     *    - None.
     *
     *************************************************************************/

    private void parse_header_alist()
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = "macshapa_od_reader::parse_header_alist()";
	boolean done;
	boolean have_version;
	boolean have_name;
	boolean have_max_errors;
	boolean have_max_warnings;
	boolean have_debug_level;

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
                    "this.abort_parse TRUE on entry");
	}
	
	/* parse the header alist */

	/* first parse the leading left parenthesis */

	if ( (this.l0_tok).code == L_PAREN_TOK )
	{
	    if ( ( (this.l1_tok).code == L_PAREN_TOK ) ||
		 ( (this.l1_tok).code == R_PAREN_TOK ) )
	    {
	        get_next_token();
	    }
	    else if ( (this.l1_tok).code == ALIST_LABEL_TOK )
	    {
		post_warning_message(LEFT_PAREN_EXPECTED_WARN,
			"The opening left parenthesis of the HEADER> a-list " +
			"appears to be missing.\n");
	    }
	    else
	    {
		/**
		 * if a left paren is missing, the first item in the 
		 * a-list is not an a-list entry.  If we try to recover 
		 * from this error here, we will only confuse things further.  
		 * Thus we eat the left parenthesis & let the cards fall where 
		 * they may.                                                           */

		get_next_token();
	    }
	}
	else /* system error - we shouldn't have been called unless the next token == a '(' */
	{
	    throw new SystemErrorException(mName + 
		    "(this.l0_tok).code != L_PAREN_TOK.");
	}

	done              = false;
	have_version      = false;
	have_name         = false;
	have_max_errors   = false;
	have_max_warnings = false;
	have_debug_level  = false;

	/* now parse the a-list assocated with the HEADER> label */
	while ( ( ! this.abort_parse ) && ( ! done ) )
	{
	    if ( (this.l0_tok).code == L_PAREN_TOK )
	    {
		if ( (this.l1_tok).code == ALIST_LABEL_TOK )
		{
		    switch ( (this.l1_tok).aux )
		    {
			case VERSION_LABEL:
			    if ( ! have_version )
			    {
				have_version = true;
				parse_db_version_attribute();
			    }
			    else
			    {
				post_warning_message(DUPLICATE_ALIST_ENTRY_WARN,
					"Duplicate HEADER> VERSION> entry.\n");

				if ( ! this.abort_parse )
				{
				    parse_unknown_alist_entry();
				}
			    }
			    break;

			case NAME_LABEL:
			    if ( ! have_name )
			    {
				have_name = true;
				parse_db_name_attribute();
			    }
			    else
			    {
				post_warning_message(DUPLICATE_ALIST_ENTRY_WARN,
					"Duplicate HEADER> NAME> entry.\n");

				if ( ! this.abort_parse )
				{
				    parse_unknown_alist_entry();
				}
			    }
			    break;

			case MAX_ERRORS_LABEL:
			    if ( ! have_max_errors )
			    {
				have_max_errors = true;
				parse_max_errors_attribute();
			    }
			    else
			    {
				post_warning_message(DUPLICATE_ALIST_ENTRY_WARN,
					"Duplicate HEADER> MAX-ERRORS> entry.\n");

				if ( ! this.abort_parse )
				{
				    parse_unknown_alist_entry();
				}
			    }
			    break;

			case MAX_WARNINGS_LABEL:
			    if ( ! have_max_warnings )
			    {
				have_max_warnings = true;
				parse_max_warnings_attribute();
			    }
			    else
			    {
				post_warning_message(DUPLICATE_ALIST_ENTRY_WARN,
					"Duplicate HEADER> MAX-WARNINGS> entry.\n");

				if ( this.abort_parse )
				{
				    parse_unknown_alist_entry();
				}
			    }
			    break;

			case DEBUG_LEVEL_LABEL:
			    if ( ! have_debug_level )
			    {
				have_debug_level = true;
				parse_debug_level_attribute();
			    }
			    else
			    {
				post_warning_message(DUPLICATE_ALIST_ENTRY_WARN,
					"Duplicate HEADER> DEBUG-LEVEL> entry.\n");

				if ( ! this.abort_parse )
				{
				    parse_unknown_alist_entry();
				}
			    }
			    break;

			default:
			    post_warning_message(UNKNOWN_OR_UNEXPECTED_ALIST_ENTRY_WARN,
				    "The entry is located in the HEADER> a-list.\n");

			    if ( ! this.abort_parse )
			    {
				parse_unknown_alist_entry();
			    }
			    break;
		    }
		}
		else /* a-list contains a list that == ! an a-list entry.
		      * read it & discard it.
		      */
		{
		    post_warning_message(NON_ALIST_ENTRY_LIST_IN_ALIST_WARN,
			    "The list == located in the HEADER> a-list.\n");

		    if ( ! this.abort_parse )
		    {
			parse_arbitrary_list();
		    }
		}
	    }
	    else if ( (this.l0_tok).code == R_PAREN_TOK )
	    {
		done = true;
		get_next_token();
	    }
	    else if ( (this.l0_tok).code == EOF_TOK )
	    {
		done = true;
		post_error_message(UNEXPECTED_END_OF_FILE_ERR,
				   "EOF occurred the HEADER> a-list.\n",
				   true, true);
	    }
	    else /* (this.l0_tok).code != '(', ')', || EOF */
	    {
		post_warning_message(NON_ALIST_ENTRY_ATOM_IN_ALIST_WARN,
				     "The atom was detected in the HEADER> a-list.\n");

		if ( ! this.abort_parse )
		{
		    get_next_token();
		}
	    }
	}

	if ( ( ! this.abort_parse ) && ( ! have_version ) )
	{
	    this.odb_file_format_version = 1;

	    post_warning_message(MISSING_ALIST_ENTRY_WARN,
		    "The missing entry == the HEADER> VERSION> entry.  " +
		    "Format version forced to 1.\n");
	}

	return;

    } /* macshapa_odb_reader::parse_header_alist() */


    /*******************************************************************************
     *
     * parse_header_section()
     *
     * This method parses the header section of the open database body.
     * Structurally, the header section is an a-list entry with the label
     * "HEADER>"  and  and  a list as its value.  The production generating the
     * header section == shown below.
     *
     *	    <header_section> --> '(' 'HEADER>' <header-alist> ')'
     *
     *                                        - 6/10/08
     *
     *  Parameters:
     *
     *	  - None.
     *
     * Returns:  Void.
     *
     * Changes:
     *
     *    - None.
     *
     *************************************************************************/

    private void parse_header_section()
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = "macshapa_odb_reader::parse_header_section()";

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse true on entry");
	}
	
	/* parse the header section */
	
	/* first parse the leading left parenthesis */

	if ( (this.l0_tok).code == L_PAREN_TOK )
	{
	    get_next_token();
	}
	else /* we shouldn't have been called unless the next token == a '(' */
	{
	    throw new SystemErrorException(mName + 
		    "(this.l0_tok).code != L_PAREN_TOK.");
	}

	/* read the a-list entry name */
	if ( ! this.abort_parse )
	{
	    if ( ( (this.l0_tok).code == ALIST_LABEL_TOK ) &&
		 ( (this.l0_tok).aux == HEADER_LABEL ) )
	    {
		get_next_token();
	    }
	    else /* system error - we shouldn't have been called unless the */
	    {    /*                next token == the HEADER> a-list tag.    */
		throw new SystemErrorException(mName + 
			"this.l0_tok != \"HEADER>\".");
	    }
	}

	/* read the a-list associated with the header section */
	if ( ! this.abort_parse )
	{
	    switch ( (this.l0_tok).code )
	    {
		case L_PAREN_TOK:
		    if ( ( this.debug_level > 0 ) && 
			 ( this.listing_stream != null ) )
		    {
			this.listing_stream.print(
				this.dump_header_settings_to_string());
		    }

		    parse_header_alist();

		    if ( ( this.debug_level > 0 ) && 
			 ( this.listing_stream != null ) )
		    {
			this.listing_stream.print(
				this.dump_header_settings_to_string());
		    }
		    break;

		case R_PAREN_TOK:
		    post_warning_message(EMPTY_ALIST_ENTRY_WARN,
			    "The HEADER> section appears not to contain a value.\n");
		    break;

		case BOOL_TOK:
		case ERROR_TOK:
		case SYMBOL_TOK:
		case FLOAT_TOK:
		case STRING_TOK:
		case INT_TOK:
		case ALIST_LABEL_TOK:
		case PRIVATE_VAL_TOK:
		case SETF_TOK:
		case DB_VAR_TOK:
		case QUOTE_TOK:
		    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN,
			    "The value of the HEADER> section must be a list.\n");

		    if ( ! this.abort_parse )
		    {
			    get_next_token();
		    }
		    break;


		case EOF_TOK:
		    post_error_message(UNEXPECTED_END_OF_FILE_ERR,
				       "EOF in HEADER> section.\n",
				       true, true);
		    break;

		default:
		    throw new SystemErrorException(mName + 
			    "Encountered unknown token type.");
	            /* commented out to keep the compiler happy */
		    // break;
	    }
	}

	/* check for EOF */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code == EOF_TOK )
	    {
		post_error_message(UNEXPECTED_END_OF_FILE_ERR,
				   "EOF encountered in the HEADER> section.\n",
				   true, true);
	    }
	}

	/* discard any excess values that may appear in the HEADER> section */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code != R_PAREN_TOK )
	    {
		discard_excess_alist_entry_values();

		if ( ! this.abort_parse )
		{
		    post_warning_message(EXCESS_VALUES_IN_ALIST_ENTRY_WARN,
			"Excess values encountered in the HEADER> section.\n");
		}
	    }
	}

	/* read the terminating right parenthesis */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code == R_PAREN_TOK )
	    {
		get_next_token();
	    }
	    else
	    {
		/* since we are cleaning up any excess values in the HEADER> */
		/* section, this else clause is unreachable at present.      */
		/* Should we choose to drop this attempt at error recovery,  */
		/* this clause will again become reachable.    - 9/15/95  */

		post_warning_message(RIGHT_PAREN_EXPECTED_WARN,
			"The closing parenthesis was missing from " +
			"the HEADER> section.\n");
	    }
	}

	return;

    } /* macshapa_odb_reader::parse_header_section() */


    /*************************************************************************
     *
     * parse_max_errors_attribute()
     *
     * This method parses the max errors attribute, and loads
     * the error limit into this.max_errors.  This attribute
     * only appears in the a-list associated with the header section of
     * the open database body.  The production generating this attribute
     * is given below:
     *
     *	    <db-version-attribute> --> '(' 'MAX-ERRORS>' <integer> ')'
     *
     *                                                                                                       - 9/6/95
     *
     *  Parameters:
     *
     *	  - None.
     *
     * Returns:  Void.
     *
     * Changes:
     *
     *    - None.
     *
     *************************************************************************/

    private void parse_max_errors_attribute()
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = "macshapa_odb_reader::parse_max_errors_attribute()";
	final String missing_errors_limit_mssg =
		"The HEADER> MAX-ERRORS> attribute appears not to contain a " +
		"value.  Errors limit forced to 1.\n";
	final String errors_limit_type_mismatch_mssg =
		"The value of the HEADER> MAX-ERRORS> attribute must be a " +
		"positive integer.  Errors limit forced to 1.\n";

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse TRUE on entry");
	}
	
	/* parse the version attribute */
	
	/* first parse the leading left parenthesis */

	if ( (this.l0_tok).code == L_PAREN_TOK )
	{
	    get_next_token();
	}
	else /* we shouldn't have been called unless the next token == a '(' */
	{
	    throw new SystemErrorException(mName + 
		    "(this.l0_tok).code != L_PAREN_TOK.");
	}

	/* read the a-list entry name */
	if ( ! this.abort_parse )
	{
	    if ( ( (this.l0_tok).code == ALIST_LABEL_TOK ) &&
		 ( (this.l0_tok).aux == MAX_ERRORS_LABEL ) )
	    {
		get_next_token();
	    }
	    else /* we shouldn't have been called unless the next token == an a-list tag */
	    {
		throw new SystemErrorException(mName + 
			"this.l0_tok != MAX-ERRORS>.");
	    }
	}

	/* read the value associated with the a-list entry & discard any excess values */
	if ( ! this.abort_parse )
	{
	    switch ( (this.l0_tok).code )
	    {
		case INT_TOK:
		    if ( (this.l0_tok).val >= MAX_ERRORS_MIN )
		    {
			if ( (this.l0_tok).val <= MAX_ERRORS_MAX )
			{
			    this.max_errors = (int)((this.l0_tok).val);
			}
			else
			{
			    this.max_errors = MAX_ERRORS_MAX;
			    post_warning_message(ERROR_LIMIT_TOO_LARGE_WARN,
						 null);
			}
		    }
		    else
		    {
			this.max_errors = MAX_ERRORS_MIN;
			post_warning_message(NON_POSITIVE_ERROR_LIMIT_WARN,
					     null);
		    }

		    if ( ! this.abort_parse )
		    {
			get_next_token();
		    }
		    break;

		case R_PAREN_TOK:
		    this.max_errors = MAX_ERRORS_MIN;
		    post_warning_message(EMPTY_ALIST_ENTRY_WARN,
					 missing_errors_limit_mssg);
		    break;

		case L_PAREN_TOK:
		    this.max_errors = MAX_ERRORS_MIN;
		    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN,
					 errors_limit_type_mismatch_mssg);

		    if ( ! this.abort_parse )
		    {
			parse_arbitrary_list();
		    }
		    break;

		case ERROR_TOK:
		case SYMBOL_TOK:
		case FLOAT_TOK:
		case STRING_TOK:
		case BOOL_TOK:
		case ALIST_LABEL_TOK:
		case PRIVATE_VAL_TOK:
		case SETF_TOK:
		case DB_VAR_TOK:
		case QUOTE_TOK:
		    this.max_errors = MAX_ERRORS_MIN;
		    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN,
					 errors_limit_type_mismatch_mssg);

		    if ( ! this.abort_parse )
		    {
			get_next_token();
		    }
		    break;


		case EOF_TOK:
		    post_error_message(UNEXPECTED_END_OF_FILE_ERR,
				       "EOF in HEADER> MAX-ERRORS> attribute.\n",
				       true, true);
		    break;

		default:
		    throw new SystemErrorException(mName + 
			    "Encountered unknown token type.");
	            /* commented out to keep the compiler happy */
		    // break;
	    }
	}

	/* check for EOF */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code == EOF_TOK )
	    {
		post_error_message(UNEXPECTED_END_OF_FILE_ERR,
				   "EOF in HEADER> MAX-ERRORS> attribute.\n",
				    true, true);
	    }
	}

	/* discard any excess values that may appear in the MAX-ERRORS> a-list entry */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code != R_PAREN_TOK )
	    {
		discard_excess_alist_entry_values();

		if ( ! this.abort_parse )
		{
		    post_warning_message(EXCESS_VALUES_IN_ALIST_ENTRY_WARN,
			    "Excess values encountered in the HEADER> " +
			    "MAX-ERRORS> attribute.\n");
		}
	    }
	}


	/* read the terminating right parenthesis */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code == R_PAREN_TOK )
	    {
		get_next_token();
	    }
	    else
	    {
		post_warning_message(RIGHT_PAREN_EXPECTED_WARN,
			"Closing parenthesis missing from the " +
			"HEADER> MAX-ERRORS> attribute.\n");
	    }
	}

	return;

    } /* macshapa_odb_reader::parse_max_errors_attribute() */


    /*************************************************************************
     *
     * parse_max_warnings_attribute()
     *
     * This method parses the max warnings attribute,  and  and  loads the
     * warnings limit into this.max_warnings.  This attribute
     * only appears in the a-list associated with the header section of
     * the open database body.  The production generating this attribute
     * is given below:
     *
     *	    <db-version-attribute> --> '(' 'MAX-WARNINGS>' <integer> ')'
     *
     *                                                 - 6/10/08
     *
     *  Parameters:
     *
     *	  - None.
     *
     * Returns:  Void.
     *
     * Changes:
     *
     *    - None.
     *
     *************************************************************************/

    private void parse_max_warnings_attribute()
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = 
		"macshapa_odb_reader::parse_max_warnings_attribute()";
	final String missing_warnings_limit_mssg =
		"The HEADER> MAX-WARNINGS> attribute appears not to contain " +
		"a value.  Warnings limit forced to 1.\n";
	final String warnings_limit_type_mismatch_mssg =
		"The value of the HEADER> MAX-WARNINGS> attribute must be " +
		"a positive integer.  Warnings limit forced to 1.\n";

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse true on entry");
	}
	
	/* parse the version attribute */
	
	/* first parse the leading left parenthesis */

	if ( (this.l0_tok).code == L_PAREN_TOK )
	{
	    get_next_token();
	}
	else /* we shouldn't have been called unless the next token == a '(' */
	{
	    throw new SystemErrorException(mName + 
		    "(this.l0_tok).code != L_PAREN_TOK.");
	}

	/* read the a-list entry name */
	if ( ! this.abort_parse )
	{
	    if ( ( (this.l0_tok).code == ALIST_LABEL_TOK ) &&
		 ( (this.l0_tok).aux == MAX_WARNINGS_LABEL ) )
	    {
		get_next_token();
	    }
	    else /* we shouldn't have been called unless the next token == an a-list tag */
	    {
		throw new SystemErrorException(mName + 
			"this.l0_tok != MAX-ERRORS>.");
	    }
	}

	/**
	 * read the value associated with the a-list entry & 
	 * discard any excess values 
	 */
	if ( ! this.abort_parse )
	{
	    switch ( (this.l0_tok).code )
	    {
		case INT_TOK:
		    if ( (this.l0_tok).val >= MAX_WARNINGS_MIN )
		    {
			if ( (this.l0_tok).val <= MAX_WARNINGS_MAX )
			{
			    this.max_warnings = (int)((this.l0_tok).val);
			}
			else
			{
			    this.max_warnings = MAX_WARNINGS_MAX;
			    post_warning_message(WARNING_LIMIT_TOO_LARGE_WARN,
						 null);
			}
		    }
		    else
		    {
			this.max_warnings = MAX_WARNINGS_MIN;
			post_warning_message(NON_POSITIVE_WARNING_LIMIT_WARN,
					     null);
		    }

		    if ( ! this.abort_parse )
		    {
			get_next_token();
		    }
		    break;

		case R_PAREN_TOK:
		    this.max_warnings = MAX_WARNINGS_MIN;
		    post_warning_message(EMPTY_ALIST_ENTRY_WARN,
					 missing_warnings_limit_mssg);
		    break;

		case L_PAREN_TOK:
		    this.max_warnings = MAX_WARNINGS_MIN;
		    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN,
				         warnings_limit_type_mismatch_mssg);

		    if ( ! this.abort_parse )
		    {
			parse_arbitrary_list();
		    }
		    break;

		case ERROR_TOK:
		case SYMBOL_TOK:
		case FLOAT_TOK:
		case STRING_TOK:
		case BOOL_TOK:
		case ALIST_LABEL_TOK:
		case PRIVATE_VAL_TOK:
		case SETF_TOK:
		case DB_VAR_TOK:
		case QUOTE_TOK:
		    this.max_warnings = MAX_WARNINGS_MIN;
		    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN,
				         warnings_limit_type_mismatch_mssg);

		    if ( ! this.abort_parse )
		    {
			get_next_token();
		    }
		    break;

		case EOF_TOK:
		    post_error_message(UNEXPECTED_END_OF_FILE_ERR,
			"EOF in HEADER> MAX-WARNINGS> attribute.\n",
			 true, true);
		    break;

		default:
		    throw new SystemErrorException(mName + 
			    "Encountered unknown token type.");
	            /* commented out to keep the compiler happy */
		    // break;
	    }
	}

	/* check for EOF */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code == EOF_TOK )
	    {
		post_error_message(UNEXPECTED_END_OF_FILE_ERR,
				   "EOF in HEADER> MAX-WARNINGS> attribute.\n",
				   true, true);
	    }
	}

	/* discard any excess values that may appear in the MAX-WARNINGS> a-list entry */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code != R_PAREN_TOK )
	    {
		discard_excess_alist_entry_values();

		if ( ! this.abort_parse )
		{
		    post_warning_message(EXCESS_VALUES_IN_ALIST_ENTRY_WARN,
			    "Excess values encountered in the HEADER> " +
			    "MAX-WARNINGS> attribute.\n");
		}
	    }
	}

	/* read the terminating right parenthesis */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code == R_PAREN_TOK )
	    {
		get_next_token();
	    }
	    else
	    {
		post_warning_message(RIGHT_PAREN_EXPECTED_WARN,
			"Closing parenthesis missing from the HEADER> " +
			"MAX-WARNINGS> attribute.\n");
	    }
	}

	return;

    } /* macshapa_odb_reader::parse_max_warnings_attribute() */

        
    /*** Parser section 3 -- methods for parsing the user section ***/

    /*************************************************************************
     *
     * dump_s_var_cell_definition_to_listing()
     *
     * Dump the contents of the supplied DataCell to the listing file.
     *        
     *                                                  - 2/16/08
     *
     * Parameters:
     *
     *	  - dc: Reference to the DataCell to be dumped.
     *
     * Returns:  Void.
     *
     * Changes:
     *
     *  - None.
     *
     *************************************************************************/
    
    private void dump_s_var_cell_definition_to_listing(DataCell dc)
        throws SystemErrorException
    {
        final String mName = 
                "macshapa_odb_reader::dump_s_var_cell_definition_to_listing()";
        
        throw new SystemErrorException(mName + "method not implemented.");
        
	/* commented out to keep the compiler happy */
        // return;
        
    } /* macshapa_odb_reader::dump_s_var_cell_definition_to_listing() */

    

    /*************************************************************************
     *
     * dump_s_var_definition_to_listing()
     *
     * Dump the supplied spreadsheet variable definition data to the 
     * listing file.
     *        
     *                                                  - 2/16/08
     *
     * Parameters:
     *
     *	  - dc: Reference to the DataCell to be dumped.
     *
     * Returns:  Void.
     *
     * Changes:
     *
     *   - None.
     *
     *************************************************************************/
    
    private void dump_s_var_definition_to_listing(
                                            MatrixVocabElement.MatrixType type,
                                            boolean variable_length, 
                                            int col_width)
        throws SystemErrorException
    {
        final String mName = 
                "macshapa_odb_reader::dump_s_var_definition_to_listing()";
        
        throw new SystemErrorException(mName + "method not implemented.");
        
	/* commented out to keep the compiler happy */
        // return;
        
    } /* macshapa_odb_reader::dump_s_var_definition_to_listing() */

    
    /*************************************************************************
     *
     * parse_float_cell_value()
     *
     * This method parses the value of a spreadsheet variable cell in the 
     * context of a float spreadsheet variable.  The production generating 
     * such a cell value is given below:
     *        
     *	    <float_cell_value> --> <float>
     *
     * If there are no type conflicts, the function simply creates a 
     * FloatDataValue containing the value specified, and returns a 
     * reference to the newly created data value.
     *
     * Type conficts are handled by loading the value 0.0 into the 
     * FloatDataValue, issuing a warning message, and consuming the offending 
     * value.
     *        
     *                                                     - 6/12/08
     *
     * Parameters:
     *
     *	  - farg:  Reference to a copy of the formal argument we 
     *          are going to create a data value for.    As this function is
     *          only called to parse the value of a float cell, the 
     *          farg must be of type FLOAT. 
     *
     * Returns:  Void.
     *
     * Changes:
     *
     *  - None.
     *
     *************************************************************************/

    private DataValue parse_float_cell_value(FormalArgument farg)
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = "macshapa_odb_reader::parse_float_cell_value()";
	final String overflow_mssg = "Overflow occured in a float cell value.\n";
        double value = 0.0;
        FloatDataValue fdv = null;

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse true on entry");
	}
        
        if ( farg == null )
        {
            throw new SystemErrorException(mName + "farg null on entry");
        }
        
        if ( farg.fargType != FormalArgument.FArgType.FLOAT )
        {
            throw new SystemErrorException(mName + 
                    "Supplied farg cannot be replaced with a float.");
        }
        
        if ( farg.getID() == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + 
                                           "Supplied farg has invalid ID.");
        }
	
	/* try to parse the float cell value */
	
	switch ( (this.l0_tok).code )
	{
	    case FLOAT_TOK:
                value = this.l0_tok.val;

		if ( ! this.abort_parse )
		{
		    get_next_token();
		}
		break;

	    case INT_TOK:
                value = this.l0_tok.val;

		if ( ! this.abort_parse )
		{
		    get_next_token();
		}
		break;

	    case SYMBOL_TOK:
	    case STRING_TOK:
	    case BOOL_TOK:
	    case PRIVATE_VAL_TOK:
	    case ALIST_LABEL_TOK:
	    case SETF_TOK:
	    case DB_VAR_TOK:
	    case QUOTE_TOK:
		post_warning_message(S_VAR_CELL_VALUE_TYPE_MISMATCH_WARN, 
			"Will discard the value and set the value of the " +
			"float cell to zero.\n"); 

                value = 0.0;

		if ( ! this.abort_parse )
		{
		    get_next_token();
		}
		 break;

	    case L_PAREN_TOK:
		post_warning_message(S_VAR_CELL_VALUE_TYPE_MISMATCH_WARN, 
			"The value is a list, which will be discarded.  " +
			"The float cell will be set to zero.\n"); 

                value = 0.0;

		if ( ! this.abort_parse )
		{
		    parse_arbitrary_list();
		}
		break;

	     case R_PAREN_TOK:
		post_warning_message(S_VAR_CELL_VALUE_MISSING_WARN, 
		    "The float cell will be set to zero.\n"); 

                value = 0.0;
		break;

	    case ERROR_TOK:
		post_warning_message(S_VAR_CELL_VALUE_TYPE_MISMATCH_WARN, 
			"The value is an ill formed token.  The float cell " +
			"will be set to zero.\n"); 

                value = 0.0;

		if ( ! this.abort_parse )
		{
		    get_next_token();
		}
		break;

	    case EOF_TOK:
		post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
			           "EOF in a float cell value.\n", 
			           true, true);
		break;

	     default:
		 throw new SystemErrorException(mName + 
			 "Encountered unknown token type.");
	         /* commented out to keep the compiler happy */
		 // break;
	}
        
        // range checking done in the lexer, so if we get this far, just
        // create the data value.  
        //
        // Note that in the OpenSHAPA version of this code, we will have
        // to check to see if the formal argument is a subranged float,
        // and if so coerce the value to range if ncessary.
        
        fdv = new FloatDataValue(this.db, farg.getID(), value);
        
	return(fdv);

    } /* macshapa_odb_reader::parse_float_cell_value() */
    

    /*************************************************************************
     *
     * parse_float_value()
     *
     * This method parses a float value in the context of a matrix or 
     * predicate argument.
     *
     * If there are no type conflicts, the function simply creates a 
     * FloatDataValue containing the value specified, and returns a 
     * reference to the newly created data value.
     *
     * Type conficts are handled by  issuing a warning message, consuming the 
     * offending value, and returning an undefined data value.
     *        
     *                                                     - 6/12/08
     *
     * Parameters:
     *
     *	  - farg:  Reference to a copy of the formal argument we 
     *          are going to create a data value for.    This function is
     *          called to parse the value of a matrix or predicate argument,
     *          so the farg must be of type FLOAT or of type UNTYPED. 
     *
     * Returns:  Void.
     *
     * Changes:
     *
     *  - None.
     *
     *************************************************************************/

    private DataValue parse_float_value(FormalArgument farg)
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = "macshapa_odb_reader::parse_float_value()";
	final String overflow_mssg = "Overflow occured in a float value.\n";
        double value = 0.0;
        DataValue dv = null;
        

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse true on entry");
	}
        
        if ( farg == null )
        {
            throw new SystemErrorException(mName + "farg null on entry");
        }
        
        if ( farg.getID() == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + 
                                           "Supplied farg has invalid ID.");
        }
        
        /* we must only be called if the next token is a float -- scream
         * and die if it is not.
         */
        
        if ( (this.l0_tok).code != FLOAT_TOK )
        {
            throw new SystemErrorException(mName + 
                                           "(this.l0_tok).code != FLOAT_TOK");
        }
        
        value = this.l0_tok.val;
	
        if ( ( farg.fargType != FormalArgument.FArgType.FLOAT ) ||
             ( farg.fargType != FormalArgument.FArgType.UNTYPED ) )
        {
            // range checking done in the lexer, so if we get this far, just
            // create the data value.  
            //
            // Note that in the OpenSHAPA version of this code, we will have
            // to check to see if the formal argument is a subranged float,
            // and if so coerce the value to range if ncessary.

            dv = new FloatDataValue(this.db, farg.getID(), value);
        }
        else
        {
            /* type mismatch between formal argument and value.  Construct
             * an udefined data value, and flag a warning.
             */
            dv = new UndefinedDataValue(this.db, 
                                        farg.getID(), 
                                        farg.getFargName());
            
            post_warning_message(FARG_ARG_TYPE_MISMATCH_WARN, null);
        }
        
        if ( ! this.abort_parse ) /* consume the token */
        {
            get_next_token();
        }
        
	return(dv);

    } /* macshapa_odb_reader::parse_float_value() */
    

    /*************************************************************************
     *
     * parse_formal_arg_value()
     *
     * This method parses a foral argument value in the context of a matrix or 
     * predicate argument.
     *        
     *                                                     - 6/12/08
     *
     * Parameters:
     *
     *	  - farg:  Reference to a copy of the formal argument we 
     *          are going to create a data value for.
     *
     * Returns:  Void.
     *
     * Changes:
     *
     *  - None.
     *
     *************************************************************************/

    private DataValue parse_formal_arg_value(FormalArgument farg)
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = "macshapa_odb_reader::parse_formal_arg_value()";
        UndefinedDataValue udv = null;
        

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse true on entry");
	}
        
        if ( farg == null )
        {
            throw new SystemErrorException(mName + "farg null on entry");
        }
        
        if ( farg.getID() == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + 
                                           "Supplied farg has invalid ID.");
        }
        
        /* we must only be called if the next token is a symbol with the 
         * formal argument flag set -- scream and die if it is not.
         */
        
        if ( ( (this.l0_tok).code != SYMBOL_TOK ) ||
             ( ((this.l0_tok).aux & FORMAL_ARG_FLAG) == 0 ) )
        {
            throw new SystemErrorException(mName + "(this.l0_tok).code != " +
                    "SYMBOL_TOK or ((this.l0_tok).aux & FORMAL_ARG_FLAG) == 0");
        }
        
        if ( this.l0_tok.str.toString().compareTo(farg.getFargName()) != 0 )
        {
            post_warning_message(FARG_NAME_MISMATCH_WARN, 
                    "The formal argument appeared in a predicate value.  " +
                    "The expected formal argument will be used.\n");
        }

        udv = new UndefinedDataValue(this.db, farg.getID(), farg.getFargName());
        
        if ( ! this.abort_parse ) /* consume the token */
        {
            get_next_token();
        }
        
	return(udv);

    } /* macshapa_odb_reader::parse_formal_arg_value() */


    /*************************************************************************
     *
     * parse_integer_cell_value()
     *
     * This method parses the value of a spreadsheet variable cell in the 
     * context of an integer spreadsheet variable.  The production generating 
     * such a cell value is given below:
     *        
     *            <integer_cell_value> --> <integer>
     *
     * If there are no type conflicts, the function simply creates a Matrix
     * of length 1, places an IntDataValue containing the specified
     * value in the first (and only) entry in the matrix, and returns a
     * reference to the newly created instance of Matrix.
     *
     * In general, type conficts are handled by loading 0 into the 
     * IntDataValue, issuing a warning message, and consuming the 
     * offending value.  However, in the case of a float token, the
     * float value is silently coerced to an integer.  
     *        
     *                                                 - 6/12/08
     *
     *  Parameters:
     *
     *	  - farg:  Reference to a copy of the formal argument we 
     *          are going to create a data value for.  As this function is
     *          only called to parse the value of an integer cell, the 
     *          farg must be of type INTEGER. 
     *
     * Returns:  Void.
     *
     * Changes:
     *
     *	  - None.
     *
     *************************************************************************/

    private DataValue parse_integer_cell_value(FormalArgument farg)
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = 
                "macshapa_odb_reader::parse_integer_cell_value()";
	final String overflow_mssg = 
		"Overflow occured in an integer cell value.\n";
        long value = 0;
        IntDataValue idv = null;

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName +
		    "this.abort_parse TRUE on entry");
	}
        
        if ( farg == null )
        {
            throw new SystemErrorException(mName + "farg null on entry");
        }
        
        if ( farg.fargType != FormalArgument.FArgType.INTEGER )
        {
            throw new SystemErrorException(mName + 
                    "Supplied farg cannot be replaced with an integer.");
        }
        
        if ( farg.getID() == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + 
                                           "Supplied farg has invalid ID.");
        }
	
	/* try to parse the integer cell value */
	
	switch ( (this.l0_tok).code )
	{
	    case FLOAT_TOK:
		value = this.l0_tok.coerce_float_token_to_integer();

		if ( ! this.abort_parse )
		{
		    post_warning_message(S_VAR_CELL_VALUE_TYPE_MISMATCH_WARN, 
			    "Coerced the floating point value to the nearest " +
			    "legal integer cell value.\n");
		}

		if ( ! this.abort_parse )
		{
		    get_next_token();
		}
		break;

	    case INT_TOK:
                value = (long)this.l0_tok.val;

		if ( ! this.abort_parse )
		{
		    get_next_token();
		}
		break;

	    case SYMBOL_TOK:
	    case STRING_TOK:
	    case BOOL_TOK:
	    case PRIVATE_VAL_TOK:
	    case ALIST_LABEL_TOK:
	    case SETF_TOK:
	    case DB_VAR_TOK:
	    case QUOTE_TOK:
		post_warning_message(S_VAR_CELL_VALUE_TYPE_MISMATCH_WARN, 
			"Will discard the value && set the value of the " +
			"integer cell to zero.\n"); 

                value = 0;

		if ( ! this.abort_parse )
		{
		    get_next_token();
		}
		break;

	    case L_PAREN_TOK:
		post_warning_message(S_VAR_CELL_VALUE_TYPE_MISMATCH_WARN, 
			"The value is a list, which will be discarded.  The " +
			"integer cell will be set to zero.\n"); 

                value = 0;

		if ( ! this.abort_parse )
		{
		    parse_arbitrary_list();
		}
		break;

	     case R_PAREN_TOK:
		post_warning_message(S_VAR_CELL_VALUE_MISSING_WARN, 
		    "The integer cell will be set to zero.\n"); 

                value = 0;
		break;

	    case ERROR_TOK:
		post_warning_message(S_VAR_CELL_VALUE_TYPE_MISMATCH_WARN, 
			"The value is an ill formed token.  The integer cell " +
			"will be set to zero.\n"); 

                value = 0;

		if ( ! this.abort_parse )
		{
		    get_next_token();
		}
		break;

	    case EOF_TOK:
		post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
			           "EOF in an integer cell value.\n", 
			           true, true);
		break;

	     default:
		 throw new SystemErrorException(mName + 
			 "Encountered unknown token type.");
	         /* commented out to keep the compiler happy */
		 // break;
	}
        
        // range checking done in the lexer, so if we get this far, just
        // create the data value.  
        //
        // Note that in the OpenSHAPA version of this code, we will have
        // to check to see if the formal argument is a subranged integer,
        // and if so coerce the value to range if ncessary.
        
        idv = new IntDataValue(this.db, farg.getID(), value);
        
	return(idv);

    } /* machsapa_odb_reader::parse_integer_cell_value() */
    

    /*************************************************************************
     *
     * parse_integer_value()
     *
     * This method parses an integer value in the context of a matrix or 
     * predicate argument.
     *
     * If there are no type conflicts, the function simply creates an 
     * IntDataValue containing the value specified, and returns a 
     * reference to the newly created data value.
     *
     * Type conficts are handled by  issuing a warning message, consuming the 
     * offending value, and returning an undefined data value.
     *        
     *                                                     - 6/12/08
     *
     * Parameters:
     *
     *	  - farg:  Reference to a copy of the formal argument we 
     *          are going to create a data value for.    This function is
     *          called to parse the value of a matrix or predicate argument,
     *          so the farg must be of type INTEGER or of type UNTYPED. 
     *
     * Returns:  Void.
     *
     * Changes:
     *
     *  - None.
     *
     *************************************************************************/

    private DataValue parse_integer_value(FormalArgument farg)
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = "macshapa_odb_reader::parse_integer_value()";
	final String overflow_mssg = "Overflow occured in an integer value.\n";
        long value = 0;
        DataValue dv = null;
        

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse true on entry");
	}
        
        if ( farg == null )
        {
            throw new SystemErrorException(mName + "farg null on entry");
        }
        
        if ( farg.getID() == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + 
                                           "Supplied farg has invalid ID.");
        }
        
        /* we must only be called if the next token is a float -- scream
         * and die if it is not.
         */
        
        if ( (this.l0_tok).code != FLOAT_TOK )
        {
            throw new SystemErrorException(mName + 
                                           "(this.l0_tok).code != FLOAT_TOK");
        }
        
        value = (long)(this.l0_tok.val);
	
        if ( ( farg.fargType != FormalArgument.FArgType.INTEGER ) ||
             ( farg.fargType != FormalArgument.FArgType.UNTYPED ) )
        {
            // range checking done in the lexer, so if we get this far, just
            // create the data value.  
            //
            // Note that in the OpenSHAPA version of this code, we will have
            // to check to see if the formal argument is a subranged float,
            // and if so coerce the value to range if ncessary.

            dv = new IntDataValue(this.db, farg.getID(), value);
        }
        else
        {
            /* type mismatch between formal argument and value.  Construct
             * an udefined data value, and flag a warning.
             */
            dv = new UndefinedDataValue(this.db, 
                                        farg.getID(), 
                                        farg.getFargName());
            
            post_warning_message(FARG_ARG_TYPE_MISMATCH_WARN, null);
        }
        
        if ( ! this.abort_parse ) /* consume the token */
        {
            get_next_token();
        }
        
	return(dv);

    } /* macshapa_odb_reader::parse_int_value() */


    /*************************************************************************
     *
     * parse_matrix_cell_value()
     *
     * This method parses a matrix cell value.  If no major errors are detected,
     * the function construct a data value containing the matrix argument, and
     * returns a reference to the new data value,
     * 
     * If major errors are detected, the function constructs a undefined
     * data value for the supplied formal argument and returns that instead.
     *
     * The production generating a predicate value is given below:
     *        
     *        <matrix_cell_value> --> <pred_arg>
     *        
     *        <pred_arg> --> <quote_string> | <nominal> | <pred_value> | 
     *                       <integer> | <float> | <time_stamp> | <formal_arg>
     *        
     *        <time_stamp> --> '(' 'TIME>' <integer> ')' 
     *        
     *                                             - 6/13/08
     *
     * Parameters:
     *
     *	  - farg:  Reverence to a copy of the formal argument of the matrix 
     *          whose value is about to be parsed.  In the context of MacSHAPA 
     *          data bases, the farg must always be of type UNTYPED.
     *
     * Returns:  DataValue containing the value of the matrix argument.
     *
     * Changes:
     *
     *      - None.
     *
     **************************************************************************/

    private DataValue parse_matrix_cell_value(FormalArgument farg)
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = "macshapa_odb_reader::parse_matrix_cell_value()";
	final String overflow_mssg = 
		"Overflow occured in a matrix argument value.\n";
	boolean replace_with_farg;
        ColPred cp = null;
        Predicate pred = null;
        DataValue value = null;

        if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse TRUE on entry");
	}

        if ( farg == null )
        {
            throw new SystemErrorException(mName + "farg null on entry");
        }
        
        if ( farg.fargType != FormalArgument.FArgType.UNTYPED )
        {
            throw new SystemErrorException(mName + 
                    "matrix cell value farg is not UNTYPED?!?.");
        }
        
        if ( farg.getID() == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + 
                                           "Supplied farg has invalid ID.");
        }
	
	/* try to parse the matrix argument value */
	
	switch ( (this.l0_tok).code )
	{
	    case R_PAREN_TOK:
		post_warning_message(S_VAR_CELL_VALUE_MISSING_WARN, 
		    "The matrix cell argument will be left undefined.\n"); 
		break;

	    case L_PAREN_TOK:
                if ( ( (this.l1_tok).code == ALIST_LABEL_TOK ) &&
                     ( (this.l1_tok).aux == TIME_LABEL ) )
                {
                    value = parse_time_stamp(farg);
                }
                else if ( ( (this.l1_tok).code == SYMBOL_TOK ) &&
                          ( ((this.l1_tok).aux & COLUMN_FLAG) != 0 ) &&
                          ( this.db.matrixVEExists(
                                this.l1_tok.str.toString()) ) )
                {
                    value = parse_col_pred_value(farg);
                }
                else
                {
                    value = parse_pred_value(farg);
                }
		break;

            case FLOAT_TOK:
                value = parse_float_value(farg);
                break;

            case INT_TOK:
                value = parse_integer_value(farg);
                break;

            case STRING_TOK:
                value = parse_quote_string_value(farg);
                break;

            case SYMBOL_TOK:
                if ( ((this.l0_tok).aux & FORMAL_ARG_FLAG) != 0 )
                {
                    value = parse_formal_arg_value(farg);
                }
                else if ( ((this.l0_tok).aux & 
                           (PRED_FLAG | COLUMN_FLAG | NOMINAL_FLAG)) != 0 )
                {
                    value = parse_nominal_value(farg);
                }
                else /* in theory, this can't happen */
                {
                    throw new SystemErrorException(mName + 
                            "(this.l0_tok).aux corrupt?!?");
                }
                break;

	    case ALIST_LABEL_TOK:
	    case BOOL_TOK:
	    case PRIVATE_VAL_TOK:
	    case SETF_TOK:
	    case DB_VAR_TOK:
	    case QUOTE_TOK:
	    case ERROR_TOK:
		post_warning_message(ILLEGAL_ATOM_IN_MATRIX_ARG_WARN,  null);

		if ( ! this.abort_parse )
		{
		    get_next_token();
		}
		break;

	    case EOF_TOK:
		post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
			"EOF encountered in a matrix argument.\n", 
			true, true);
		break;

	     default:
		 throw new SystemErrorException(mName + 
			 "Encountered unknown token type.");
	         /* commented out to keep the compiler happy */
		 // break;

	}
        
        if ( ( value == null ) && ( ! this.abort_parse ) )
        {
            // construct an empty argument
            value = new UndefinedDataValue(this.db, 
                                           farg.getID(), 
                                           farg.getFargName());
        }

	return(value);

    } /* macshapa_odb_reader::parse_matrix_cell_value() */


    /************************************************************************
     *
     * parse_nominal_cell_value()
     *
     * This method parses the value of a spreadsheet variable cell in the 
     * context of a nominal spreadsheet variable.  The production generating 
     * such a cell value is given below:
     *        
     *    <nominal_cell_value> --> <nominal> 
     *                             |
     *                             '|<val>|'  (* if the nominal is undefined *)
     *
     *
     * If there are no type conflicts, the function simply creates a Matrix
     * of length 1, places a NominalDataValue containing the specified
     * value in the first (and only) entry in the matrix, and returns a
     * reference to the newly created instance of Matrix.
     *
     * Type conficts are handled by loading an empty string into the 
     * NominalDataValue, issuing a warning message, and consuming the 
     * offending value.
     *        
     *                                             - 6/14/08
     *
     * Parameters:
     *
     *	  - farg:  Reference to a copy of the formal argument we 
     *          are going to create a data value for.  As this function is
     *          only called to parse the value of an integer cell, the 
     *          farg must be of type NOMINAL. 
     *
     * Returns:  DataValue containing the nominal.
     *
     * Changes:
     *
     *   - None.
     *
     *************************************************************************/

    private DataValue parse_nominal_cell_value(FormalArgument farg)
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = "macshapa_odb_reader::parse_nominal_cell_value()";
	final String overflow_mssg = 
		"Overflow occured in a nominal cell value.\n";
	boolean altered;
        String value = "";
        NominalDataValue ndv = null;

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse TRUE on entry");
	}
        
        if ( farg == null )
        {
            throw new SystemErrorException(mName + "farg null on entry");
        }
        
        if ( farg.fargType != FormalArgument.FArgType.NOMINAL )
        {
            throw new SystemErrorException(mName + 
                    "Supplied farg cannot be replaced with an nominal.");
        }
        
        if ( farg.getID() == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + 
                                           "Supplied farg has invalid ID.");
        }
	
	/* try to parse the nominal cell value */

	switch ( (this.l0_tok).code )
	{
	    case SYMBOL_TOK:
		if ( (((this.l0_tok).aux) & NOMINAL_FLAG) != 0 )
		{
		    altered = this.l0_tok.coerce_nominal_token_to_cell_nominal();

		    if ( ( ! this.abort_parse ) && ( altered ) )
		    {
			post_warning_message(NOMINAL_COERCED_TO_CELL_NOMINAL_WARN, 
				"These characters were coerced to '_', and " +
				"the result used as the nominal cell value.");
		    }
                    
                    value = new String(this.l0_tok.str.toString());
		}
		else if ( (((this.l0_tok).aux) & FORMAL_ARG_FLAG) != 0 )
		{
                    if ( this.l0_tok.str.toString().compareTo("<val>") 
                         != 0 )
		    {
			post_warning_message(FARG_NAME_MISMATCH_WARN, 
			        "Mismatch occured in a nominal cell value.  " +
				"The cell will be left undefined.\n"); 
		    }
                    
                    value = new String("");
		}
		else /* not a nominal */
		{
		    /* This clause is unreachable at present.  However, if we 
		     * change our definitions of nominals, pred names, column 
		     * variable names, etc., this may change.
		     */

		    post_warning_message(S_VAR_CELL_VALUE_TYPE_MISMATCH_WARN, 
			    "Will discard the value && leave the nominal " +
			    "cell undefined.\n"); 
		}

		if ( ! this.abort_parse )
		{
		    get_next_token();
		}
		break;

	    case STRING_TOK:
	    case INT_TOK:
	    case FLOAT_TOK:
	    case BOOL_TOK:
	    case PRIVATE_VAL_TOK:
	    case ALIST_LABEL_TOK:
	    case SETF_TOK:
	    case DB_VAR_TOK:
	    case QUOTE_TOK:
		post_warning_message(S_VAR_CELL_VALUE_TYPE_MISMATCH_WARN, 
			"Will discard the value && leave the nominal " +
		 	"cell undefined.\n"); 

		if ( ! this.abort_parse )
		{
		    get_next_token();
		}
		break;

	    case L_PAREN_TOK:
		post_warning_message(S_VAR_CELL_VALUE_TYPE_MISMATCH_WARN, 
			"The value is a list, which will be discarded. " +
			"The nominal cell will be left undefined.\n"); 

		if ( ! this.abort_parse )
		{
		    parse_arbitrary_list();
		}
		break;

	     case R_PAREN_TOK:
		post_warning_message(S_VAR_CELL_VALUE_MISSING_WARN, 
			"The nominal cell will be left undefined.\n"); 
		break;

	    case ERROR_TOK:
		post_warning_message(S_VAR_CELL_VALUE_TYPE_MISMATCH_WARN, 
			"The value is an ill formed token.  " +
			"The nominal cell will be left undefined.\n"); 

		if ( ! this.abort_parse )
		{
		    get_next_token();
		}
		break;

	    case EOF_TOK:
		post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
				   "EOF in a nominal cell value.\n", 
				   true, true);
		break;

	     default:
		 throw new SystemErrorException(mName + 
			 "Encountered unknown token type.");
	         /* commented out to keep the compiler happy */
		 // break;
	}
        
        // range checking done in the lexer, so if we get this far, just
        // create the data value.  
        //
        // Note that in the OpenSHAPA version of this code, we will have
        // to check to see if the formal argument is a subranged nominal,
        // and if so coerce the value to range if ncessary.
        
        ndv = new NominalDataValue(this.db, farg.getID(), value);
        
        return(ndv);

    } /* macshapa_odb_reader::parse_nominal_cell_value() */
    

    /*************************************************************************
     *
     * parse_nominal_value()
     *
     * This method parses a nominal value in the context of a matrix or 
     * predicate argument.
     *
     * If there are no type conflicts, the function simply creates an 
     * NominalDataValue containing the value specified, and returns a 
     * reference to the newly created data value.
     *
     * Type conficts are handled by  issuing a warning message, consuming the 
     * offending value, and returning an undefined data value.
     *        
     *                                                     - 6/12/08
     *
     * Parameters:
     *
     *	  - farg:  Reference to a copy of the formal argument we 
     *          are going to create a data value for.    This function is
     *          called to parse the value of a matrix or predicate argument,
     *          so the farg must be of type NOMINAL or of type UNTYPED. 
     *
     * Returns:  Void.
     *
     * Changes:
     *
     *  - None.
     *
     *************************************************************************/

    private DataValue parse_nominal_value(FormalArgument farg)
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = "macshapa_odb_reader::parse_nominal_value()";
	final String overflow_mssg = "Overflow occured in a float value.\n";
        boolean replace_with_farg = false;
        String value = null;
        DataValue dv = null;
        

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse true on entry");
	}
        
        if ( farg == null )
        {
            throw new SystemErrorException(mName + "farg null on entry");
        }
        
        if ( farg.getID() == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + 
                                           "Supplied farg has invalid ID.");
        }
        
        /* we must only be called if the next token is a SYMBOL_TOK with the
         * formal arg flag not set -- scream and die if it is not.
         */
        if ( ( (this.l0_tok).code != SYMBOL_TOK ) ||
             ( ((this.l0_tok).aux & FORMAL_ARG_FLAG) != 0 ) )
        {
            throw new SystemErrorException(mName + "(this.l0_tok).code != " +
                    "SYMBOL_TOK or ((this.l0_tok).aux & FORMAL_ARG_FLAG) != 0");
        }

        
        /* we only do the following check when this.in_query is true as a  */
        /* result of a bug in MacSHAPA.  When and if this bug is fixed, we */
        /* will run this check all the time.                               */
        if ( ( this.in_query ) && 
             ( ((this.l0_tok).aux & (PRED_FLAG | COLUMN_FLAG)) != 0 ) )
        {
            /* Check to see if the symbol is the name of a defined predicate   */
            /* or column variable.  If it is, discard the argument and replace */
            /* it with the appropriate formal argument.                        */
            
            if ( ( this.db.vl.matrixInVocabList(this.l0_tok.str.toString()) ) ||
                 ( this.db.vl.predInVocabList(this.l0_tok.str.toString()) ) )
            {
                replace_with_farg = true;

                post_warning_message(PRED_OR_COL_VAR_USED_AS_NOM_WARN, 
                        "The predicate or column variable name appeared in " +
                        "a predicate value.\n");
            }
            else if ( ((this.l0_tok).aux & NOMINAL_FLAG) == 0 )
            {
                throw new SystemErrorException(mName + 
                        "This clause should be unreachable.");
            }
        }
        
        value = this.l0_tok.str.toString();
        
        if ( ( replace_with_farg ) ||
             ( ( farg.fargType != FormalArgument.FArgType.INTEGER ) &&
               ( farg.fargType != FormalArgument.FArgType.UNTYPED ) ) )
        {
            dv = new UndefinedDataValue(this.db, 
                                        farg.getID(), 
                                        farg.getFargName());
            
            if ( ! replace_with_farg )
            {
                /* type mismatch between formal argument and value.
                 * flag a warning.
                 */
                post_warning_message(FARG_ARG_TYPE_MISMATCH_WARN, null);
            }
        }
        else
        {
            // range checking done in the lexer, so if we get this far, just
            // create the data value.  
            //
            // Note that in the OpenSHAPA version of this code, we will have
            // to check to see if the formal argument is a subranged float,
            // and if so coerce the value to range if ncessary.

            dv = new NominalDataValue(this.db, farg.getID(), value);
        }
        
        if ( ! this.abort_parse ) /* consume the token */
        {
            get_next_token();
        }
        
	return(dv);

    } /* macshapa_odb_reader::parse_nominal_value() */


    /*******************************************************************************
     *
     * parse_pred_def()
     *
     * This method parses a predicate definition.  The matter of actually
     * loading the definition into the MacSHAPA internal database is handled
     * by parse_pred_def_alist().  The productions generating a 
     * predicate definition are given below:
     *        
     *	    <pred_def> --> '(' <pred_name> <pred_def_alist> ')'
     *    
     *      <pred_def_alist> --> '(' <pred_def_attributes> ')'
     *    
     *      <pred_def_attributes> --> { <pred_variable_length_attribute> 
     *                                  <pred_formal_arg_list_attribute> }
     *    
     *      <pred_variable_length_attribute> --> '(' 'VARIABLE-LENGTH>' 
     *                                               <boolean> ')'
     *    
     *      <pred_formal_arg_list_attribute> --> '(' 'FORMAL-ARG-LIST>' 
     *                                               <pred_formal_arg_list> ')'
     *    
     *      <pred_formal_arg_list> --> '(' (<formal_arg>)+ ')'
     *
     *                                                 - 6/14/08
     *
     * Parameters:
     *
     *	  - None.
     *
     *    Return Value:  Void.
     *
     * Changes:
     *
     *    - None.
     *
     *************************************************************************/

    private void parse_pred_def()
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = "macshapap_odb_reader::parse_pred_def()";
        String predName = null;
	boolean done;
	boolean excess_values;

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse true on entry");
	}
	
	/* parse the predicate definition */

	/* first parse the leading left parenthesis */
	if ( (this.l0_tok).code == L_PAREN_TOK )
	{
	    get_next_token();
	}
	else /* we shouldn't have been called unless the next token is a '(' */
	{
            throw new SystemErrorException(mName + 
                    "(this.l0_tok).code != L_PAREN_TOK.");
	}

	/* parse the predicate name */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code == SYMBOL_TOK )
	    {
		if ( ((this.l0_tok).aux & PRED_FLAG) == 0 )
		{
		    post_warning_message(NAME_IN_PRED_DEF_NOT_A_PRED_NAME_WARN, 
			"Will coerce the name to a valid predicate name.\n");

		    if ( ! this.abort_parse )
		    {
			this.l0_tok.coerce_symbol_token_to_pred_name();
		    }
		}

                predName = new String(this.l0_tok.str.toString());

		if ( ! this.abort_parse )
		{
		    get_next_token();
		}
	    }
	    else /* we shouldn't have been called unless the next token is a symbol */
	    {
                throw new SystemErrorException(mName + 
                        "this.l0_tok != SYMBOL_TOK.");
	    }
	}

	/* read the a-list associated with the predicate definition */
	if ( ! this.abort_parse )
	{
	    switch ( (this.l0_tok).code )
	    {
		case L_PAREN_TOK:
		    /* note that if we get this far, the predicate name is in
                     * the String referenced by predName.
                     */
		    parse_pred_def_alist(predName);
		    break;

                case R_PAREN_TOK:
		    post_warning_message(EMPTY_PRED_DEF_WARN, null);
                    break;

		case INT_TOK:
		case ERROR_TOK:
		case SYMBOL_TOK:
		case FLOAT_TOK:
		case STRING_TOK:
		case BOOL_TOK:
		case ALIST_LABEL_TOK:
		case PRIVATE_VAL_TOK:
		case SETF_TOK:
		case DB_VAR_TOK:
		case QUOTE_TOK:
		    post_warning_message(TYPE_MISMATCH_IN_PRED_DEF_WARN, 
			"The predicate definition will be discarded.\n"); 

		    if ( ! this.abort_parse )
		    {
			get_next_token();
		    }
		    break;

		case EOF_TOK:
		    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
                            "EOF in a predicate definition.\n", true, true); 
		    break;

                default:
                    throw new SystemErrorException(mName + 
                            "Encountered unknown token type.");
	            /* commented out to keep the compiler happy */
		    // break;
	     }
	 }

	/* discard any excess values & consume the closing parenthesis */
	 if ( ! this.abort_parse )
	 {
	    done = false;
	    excess_values = false;

	    while ( ( ! this.abort_parse ) && 
		    ( ! done ) )
	    {
		switch ( (this.l0_tok).code )
		{
		    case R_PAREN_TOK:
			done = true;
			break;

		    case L_PAREN_TOK:
			excess_values = true;
			parse_arbitrary_list();
			break;

		    case ERROR_TOK:
		    case SYMBOL_TOK:
		    case INT_TOK:
		    case FLOAT_TOK:
		    case STRING_TOK:
		    case BOOL_TOK:
		    case ALIST_LABEL_TOK:
		    case PRIVATE_VAL_TOK:
		    case SETF_TOK:
		    case DB_VAR_TOK:
		    case QUOTE_TOK:
			excess_values = true;
			get_next_token();
			break;

		    case EOF_TOK:
			done = true;
			post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
				"EOF encountered in a predicate definition.\n", 
				true, true);
			break;

		     default:
			 throw new SystemErrorException(mName + 
				 "Encountered unknown token type.");
	                 /* commented out to keep the compiler happy */
			 // break;
		 }
	    }

	    if ( ( ! this.abort_parse ) &&
		 ( done ) && ( excess_values ) )
	    {
		post_warning_message(EXCESS_VALUES_IN_A_PREDICATE_DEF_WARN,
				     null);
	    }
	}

	/* finally, consume the closing parenthesis */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code == R_PAREN_TOK )
	    {
		get_next_token();
	    }
	    else 
	    {
		throw new SystemErrorException(mName + 
			"This else clause should be unreachable.");
	    }
	}

	return;

    } /* macshapa_odb_reader::parse_pred_def() */


    /*************************************************************************
     *
     * parse_pred_def_alist()
     *
     * This method parses the a-list associated with a predicate definition, 
     * and uses the information obtained from it, along with the predicate name
     * passed to it to insert the definition of the predicate in this.db (the 
     * associated instance of MacshapaDatabase). 
     * 
     * The productions generating the predicate definition 
     * a-list are given below:
     *        
     *	    <pred_def_alist> --> '(' <pred_def_attributes> ')'
     *    
     *      <pred_def_attributes> --> { <pred_variable_length_attribute> 
     *                                 <pred_formal_arg_list_attribute> }
     *    
     *      <pred_variable_length_attribute> --> '(' 'VARIABLE-LENGTH>' 
     *                                               <boolean> ')'
     *    
     *      <pred_formal_arg_list_attribute> --> '(' 'FORMAL-ARG-LIST>' 
     *                                               <pred_formal_arg_list> ')'
     *    
     *      <pred_formal_arg_list> --> '(' (<formal_arg>)+ ')'
     *
     *                                              - 6/14/08
     *
     * Parameters:
     *
     *    - predName: Reference to a String containing the name of the 
     *          predicate vocab element whose definition is being parsed. 
     *
     * Returns:  Void.
     *
     * Changes:
     *
     *    - None.
     *
     *************************************************************************/

    private void parse_pred_def_alist(String predName)
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = "macshapa_odb_reader::parse_pred_def_alist()";
	final String overflow_mssg = 
		"Overflow occured in the formal argument list of a " +
		"predicate definition.\n";
	boolean done;
	boolean have_args_list;
	boolean have_variable_length;
        boolean ignore_pred_def = false;
	boolean variable_length;
        Vector<String> args = null;
        UnTypedFormalArg fa = null;
        PredicateVocabElement new_pve = null;
        PredicateVocabElement old_pve = null;

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
                    "this.abort_parse TRUE on entry");
	}
        
        if ( ( predName == null ) ||
             ( predName.length() <= 0 ) )
        {
            throw new SystemErrorException(mName + "predName is null or empty.");
        }

        /* parse the predicate definition alist */
	
	/* first parse the leading left parenthesis */

	if ( (this.l0_tok).code == L_PAREN_TOK )
	{
	    if ( ( (this.l1_tok).code == L_PAREN_TOK ) || 
		 ( (this.l1_tok).code == R_PAREN_TOK ) )
	    {
		get_next_token();
	    }
	    else if ( (this.l1_tok).code == ALIST_LABEL_TOK )
	    {
		post_warning_message(LEFT_PAREN_EXPECTED_WARN, 
                        "The opening left parenthesis of a predicate " +
                        "definition a-list appears to be missing.\n");
	    }
	    else 
	    {
		/* if a left paren is missing, the first item in the a-list is 
                 * not an a-list entry.  If we try to recover from this error 
                 * here, we will only confuse things further.  Thus we eat the 
                 * left parenthesis & let the cards fall where they may.
                 */

		get_next_token();
	    }
	}
	else /* we shouldn't have been called unless the next token is a '(' */
	{
            throw new SystemErrorException(mName + 
                    "(this.l0_tok).code != L_PAREN_TOK.");
	}

	done                  = false;
	have_args_list        = false;
	have_variable_length  = false;
	variable_length       = false;

	/* now parse the a-list assocated with the predicate declaration */
	 while ( ( ! this.abort_parse ) && 
		 ( ! done ) )
	{
	    if ( (this.l0_tok).code == L_PAREN_TOK )
	    {
		if ( (this.l1_tok).code == ALIST_LABEL_TOK )
		{
		    switch ( (this.l1_tok).aux )
		    {
			case VARIABLE_LENGTH_LABEL:
			    if ( ! have_variable_length )
			    {
				have_variable_length = true;
				variable_length = 
                                        parse_variable_length_attribute();
			    }
			    else
			    {
				post_warning_message(DUPLICATE_ALIST_ENTRY_WARN, 
					"Duplicate VARIABLE-LENGTH> entry in a " +
					"predicate definition.\n");

				if ( ! this.abort_parse )
				{
				     parse_unknown_alist_entry();
				}
			    }
			    break;

			case FORMAL_ARG_LIST_LABEL:
			    if ( ! have_args_list )
			    {
				have_args_list = true;
				args = parse_pred_formal_arg_list_attribute();

                                if ( ( this.abort_parse ) &&
                                     ( args == null ) )
                                {
                                    throw new SystemErrorException(mName + 
                                            "! abort_parse and " +
                                            "parse_pred_formal_arg_list() " +
                                            "returned null.");
                                }
			    }
			    else
			    {
				post_warning_message(DUPLICATE_ALIST_ENTRY_WARN, 
					"Duplicate FORMAL-ARG-LIST> entry in " +
					"a predicate definition.\n");

				if ( ! this.abort_parse )
				{
				    parse_unknown_alist_entry();
				}
			    }
			    break;

			default:
			    post_warning_message(
				    UNKNOWN_OR_UNEXPECTED_ALIST_ENTRY_WARN, 
				    "The entry is located in a predicate " +
				    "definition a-list.\n");

			    if ( ! this.abort_parse )
			    {
				 parse_unknown_alist_entry();
			    }
			    break;
		    }
		}
		else /* a-list contains a list that is not an a-list entry. */
		     /* read it & discard it.                               */
		{
		    post_warning_message(NON_ALIST_ENTRY_LIST_IN_ALIST_WARN, 
			    "The list is located in a predicate " +
			    "definition a-list.\n");

		    if ( ! this.abort_parse )
		    {
			 parse_arbitrary_list();
		    }
		}
	    }
	    else if ( (this.l0_tok).code == R_PAREN_TOK )
	    {
		done = true;
		get_next_token();
	    }
	    else if ( (this.l0_tok).code == EOF_TOK )
	    {
		done = true;
		post_error_message(UNEXPECTED_END_OF_FILE_ERR,
			"EOF occurred in a predicate definition a-list.\n",
			true, true);
	    }
	    else /* (this.l0_tok).code isn't '(', ')', or EOF */
	    {
		post_warning_message(NON_ALIST_ENTRY_ATOM_IN_ALIST_WARN, 
			"The atom was detected in a predicate " +
			"definition a-list.\n");

		if ( ! this.abort_parse )
		{
		    get_next_token();
		}
	    }
	}

	/* check for missing required attributes -- generate default 
	 * values if necessary. 
	 */

	if ( ( ! this.abort_parse ) && 
	     ( ! have_variable_length ) )
	{
	    /* force variable length to false here */

	    post_warning_message(MISSING_ALIST_ENTRY_WARN, 
		    "VARIABLE-LENGTH> entry missing from predicate " +
		    "definition?  VARIABLE-LENGTH> forced to false.\n");
	}

	if ( ( ! this.abort_parse ) && 
	     ( ! have_args_list ) )
	{
	    post_warning_message(MISSING_ALIST_ENTRY_WARN, 
		    "FORMAL-ARG-LIST> entry missing from predicate " +
		    "definition?  Will use the arg list \"(|<val>|)\".\n");

	    if ( ! this.abort_parse )
	    {
                args = new Vector<String>();
                args.add("<val>");
	    }
	}

	/* Check to see if the predicate definition conflicts or duplicates with 
         * an existing column variable or predicate definition -- set 
         * ignore_pred_def to true if it does.
         */
	if ( ! this.abort_parse )
	{
	    if ( this.debug_level >= 2 )
	    {
		dump_predicate_definition_to_listing(predName, args, 
                                                     variable_length);
	    }
            
            if ( this.db.vl.matrixInVocabList(predName) )
            {
                /* at this point in the parse, all mve's in the vocab list must 
                 * be system mve's defined during the creation of the macshapa 
                 * database.
                 */
                post_warning_message(PREDICATE_REDEFINITION_WARN,
                        "Predicate name matches that of a system column " +
                        "variable.  Predicate definition ignored.");
                
                ignore_pred_def = true;
            }
            else if ( this.db.vl.predInVocabList(predName) )
            {
                old_pve = this.db.getPredVE(predName);
                
                if ( ( old_pve.getSystem() ) ||
                     ( old_pve.getVarLen() != variable_length ) ||
                     ( old_pve.getNumFormalArgs() != args.size() ) )
                {
                    post_warning_message(PREDICATE_REDEFINITION_WARN,
                            "New definition doesn't match old definition.  " +
                            "New definition ignored.");
                
                    ignore_pred_def = true;
                }
                else
                {
                    boolean fargNameMismatch = false;
                    int i = 0;

                    while ( ( ! fargNameMismatch ) && 
                            ( i < old_pve.getNumFormalArgs() ) )
                    {
                        if ( old_pve.getFormalArg(i).
                                getFargName().compareTo(args.get(i)) != 0 )
                        {
                            fargNameMismatch = true;
                        }
                        
                        i++;
                    }
                    
                    if ( fargNameMismatch )
                    {
                        post_warning_message(PREDICATE_REDEFINITION_WARN,
                                "New definition doesn't match old definition -- " +
                                "formal argument name mismatch.  " +
                                "New definition ignored.");
                
                        ignore_pred_def = true;
                    }
                }
            }
        }
        
        if ( ( ! this.abort_parse ) && 
             ( ! ignore_pred_def ) )
        {
            int i;
            
            new_pve = new PredicateVocabElement(this.db, predName);
            
            for ( i = 0; i < args.size(); i++ )
            {
                fa = new UnTypedFormalArg(this.db, args.get(i));
                new_pve.appendFormalArg(fa);
            }
            
            new_pve.setVarLen(variable_length);

            this.db.addPredVE(new_pve);
	}

	return;

    } /* macshapa_odb_reader::parse_pred_def_alist() */


    /*************************************************************************
     *
     * parse_pred_def_list()
     *
     * This method parses a (posibly empty) list of predicate definitions.
     * Structurally, this list is simply a (possibly empty) list of lists.
     * The production generating it is given below:
     *
     *      <pred_def_list> --> '(' (<pred_def>)* ')'
     *
     * In reading this function, the following productions for <pred_def> are
     * also useful, as they are used in error detection & recovery.  Note
     * however that these productions are not parsed in this function, but 
     * rather in parse_pred_def() and its decendants.
     *        
     *      <pred_def> --> '(' <pred_name> <pred_def_alist> ')'
     *    
     *      <pred_def_alist> --> '(' <pred_def_attributes> ')'
     *    
     *      <pred_def_attributes> --> { <pred_variable_length_attribute> 
     *                                  <pred_formal_arg_list_attribute> }
     *    
     *      <pred_variable_length_attribute> --> '(' 'VARIABLE-LENGTH>' 
     *                                               <boolean> ')'
     *    
     *      <pred_formal_arg_list_attribute> --> '(' 'FORMAL-ARG-LIST>' 
     *                                               <pred_formal_arg_list> ')'
     *    
     *      <pred_formal_arg_list> --> '(' (<formal_arg>)+ ')'
     *
     *                                               - 6/14/08
     *
     * Parameters:
     *
     *	  - None.
     *
     * Returns:  Void.
     *
     * Changes:
     *
     *    - None.
     *
     *************************************************************************/

    private void parse_pred_def_list()
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = "macshapa_odb_reader::parse_pred_def_list()";
	boolean done;

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse true on entry");
	}
	
	/* parse the predicate definitions list */
	
	/* first parse the leading left parenthesis */

	if ( (this.l0_tok).code == L_PAREN_TOK )
	{
	    if ( ( (this.l1_tok).code == L_PAREN_TOK ) || 
	         ( (this.l1_tok).code == R_PAREN_TOK ) )
	    {
		get_next_token();
	    }
	    else if ( (this.l1_tok).code == SYMBOL_TOK )
	    {
		post_warning_message(LEFT_PAREN_EXPECTED_WARN, 
			"The opening left parenthesis of the predicate " +
			"definitions list appears to be missing.\n");
	    }
	    else 
	    {
		/* If in fact the leading parenthesis in the predicate 
		 * definitions list is missing, the first item in the list 
		 * is not a predicate definition.  Rather than confuse 
		 * things further by inserting a parenthesis, we eat
		 * the parenthesis here, and let the error come to light 
		 * below.
		 */

		get_next_token();
	    }
	}
	else /* we shouldn't have been called unless the next token is a '(' */
	{
	    throw new SystemErrorException(mName + 
		    "(this.l0_tok).code != L_PAREN_TOK.");
	}

	/* parse the predicate definitions list */
	done = false;

	while ( ( ! this.abort_parse ) && ( ! done ) )
	{
	    switch ( (this.l0_tok).code )
	    {
		case L_PAREN_TOK:
		    if ( (this.l1_tok).code == SYMBOL_TOK )
		    {
			parse_pred_def();
		    }
		    else /* ! a predicate definition - discard it */
		    {
			post_warning_message(ALIEN_LIST_IN_PRED_DEFS_LIST_WARN, 
					      null);

			if ( ! this.abort_parse )
			{
			    parse_arbitrary_list();
			}
		    }
		    break;

		case R_PAREN_TOK:
		     done = true;
		     get_next_token();
		     break;

		case EOF_TOK:
		    done = true;
		    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
			"EOF encountered in the predicate definitions list.\n", 
			true, true);
		    break;

		case INT_TOK:
		case ERROR_TOK:
		case SYMBOL_TOK:
		case FLOAT_TOK:
		case STRING_TOK:
		case BOOL_TOK:
		case ALIST_LABEL_TOK:
		case PRIVATE_VAL_TOK:
		case SETF_TOK:
		case DB_VAR_TOK:
		case QUOTE_TOK:
		    post_warning_message(ALIEN_ATOM_IN_PRED_DEFS_LIST_WARN, 
					 null);

		    if ( ! this.abort_parse )
		    {
			get_next_token();
		    }
		    break;


		 default:
		    throw new SystemErrorException(mName + 
			     "Encountered unknown token type.");
	            /* commented out to keep the compiler happy */
		    // break;
	     }
	}

	return;

    } /* macshapa_odb_reader::parse_pred_def_list() */


    /*************************************************************************
     *
     * parse_pred_formal_arg_list()
     *
     * This method parses a list of formal argument symbol tokens in the 
     * context of a predicate definition.  Such lists are generated by the 
     * following productions.
     *
     *        <pred_formal_arg_list_attribute> --> '(' 'FORMAL-ARG-LIST>' 
     *                                                 <pred_formal_arg_list> ')'
     *    
     *        <pred_formal_arg_list> --> '(' (<formal_arg>)+ ')'
     *
     * In addition to parsing the formal argument list, this function must
     * also copy the formal argument list into the insertion buffer in a 
     * format appropriate for a predicate definition.
     *
     * If the formal argument list is empty, the function writes a default
     * formal argument list to the insertion buffer.
     *
     *                                                  - 6/14/08
     *
     * Parameters:
     *
     *	  - None.
     *
     * Returns: Vector of Strings containing the names of the formal 
     *          arguments spedified in the formal arguments list.
     *
     * Changes:
     *
     *    - None.
     *
     *************************************************************************/

    private Vector<String> parse_pred_formal_arg_list()
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = "macshapa_odb_reader::parse_pred_formal_arg_list()";
	final String overflow_mssg = 
		"Overflow occured in a predicate formal argument list.\n";
        String newArg;
	boolean done;
        boolean duplicateArg;
	int arg_count;
        int i;
        Vector<String> args = new Vector<String>();

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse TRUE on entry");
	}
	
	/* parse the formal argument list */
	
	/* first parse the leading left parenthesis */

	if ( (this.l0_tok).code == L_PAREN_TOK )
	{
	    get_next_token();
	}
	else /* we shouldn't have been called unless the next token is a '(' */
	{
	    throw new SystemErrorException(mName + 
		    "(this.l0_tok).code != L_PAREN_TOK.");
	}

	arg_count = 0;
	done      = false;

	while ( ( ! this.abort_parse ) && 
		( ! done ) )
	{
	    switch ( (this.l0_tok).code )
	    {
		case SYMBOL_TOK:
		    if ( (((this.l0_tok).aux) & FORMAL_ARG_FLAG) != 0 )
		    {
                        newArg = this.l0_tok.str.toString();
                        duplicateArg = false;
                        i = 0;
                        while ( ( i < arg_count ) && ( ! duplicateArg ) )
                        {
                            if ( args.get(i).compareTo(newArg) == 0 )
                            {
                                duplicateArg = true;
                            }
                            i++;
                        }
                            
			if ( duplicateArg )
			{
			    post_warning_message(DUP_FARG_WARN, 
				    "Duplicate appeared in a predicate " +
				    "formal argument list.\n");
			}
			else
			{
                            args.add(newArg);

			    arg_count++;
			}
		    }
		    else
		    {
			post_warning_message(NON_FARG_IN_FARG_LIST_WARN, 
				"This warning was occasioned by a nominal " +
				"or predicate name.\n");
		    } 
		    get_next_token(); 
		    break;

		case R_PAREN_TOK:
		    done = true;
		    get_next_token();
		    break;

		case L_PAREN_TOK:
		    post_warning_message(NON_FARG_IN_FARG_LIST_WARN, 
			    "This warning was occasioned by a list.\n");

		    if ( ! this.abort_parse )
		    {
			parse_arbitrary_list();
		    }
		    break;

		case ERROR_TOK:
		case INT_TOK:
		case FLOAT_TOK:
		case STRING_TOK:
		case BOOL_TOK:
		case ALIST_LABEL_TOK:
		case PRIVATE_VAL_TOK:
		case SETF_TOK:
		case DB_VAR_TOK:
		case QUOTE_TOK:
		    post_warning_message(NON_FARG_IN_FARG_LIST_WARN, 
			    "This warning was occasioned by an atom.\n");

		    if ( ! this.abort_parse )
		    {
			get_next_token();
		    }
		    break;

		case EOF_TOK:
		    post_error_message(UNEXPECTED_END_OF_FILE_ERR,
			"EOF encountered in a predicate formal argument list.\n",
                        true, true);
		    break;

		 default:
		     throw new SystemErrorException(mName + 
			     "Encountered unknown token type.");
 	             /* commented out to keep the compiler happy */
		     // break;
	     }
	}

	if ( ! this.abort_parse )
	{
	    if ( arg_count <= 0 )
	    {
		post_warning_message(EMPTY_FORMAL_ARGUMENT_LIST_WARN, 
			"Will force the contents of the predicate formal " +
			"argument list to (|<val>|).\n");

		if ( ! this.abort_parse )
		{
                    args.add(new String("<val>"));
		}
	    }
	}

	return args;

    } /* macshapa_odb_reader::parse_pred_formal_arg_list() */


    /*************************************************************************
     *
     * parse_pred_formal_arg_list_attribute()
     *
     * This method parses a FORMAL-ARG-LIST> attribute in the context of a
     * predicate definition.  This attribute is generated by the following 
     * productions:
     *        
     *        <pred_formal_arg_list_attribute> --> '(' 'FORMAL-ARG-LIST>' 
     *                                                 <pred_formal_arg_list> ')'
     *    
     *        <pred_formal_arg_list> --> '(' (<formal_arg>)+ ')'
     *
     * If the value of the formal argument list attribute is either missing,
     * or of inapropriate type, this function writes a default formal argument
     * list to the insertion buffer.
     *
     *                                               - 6/14/08
     *
     * Parameters:
     *
     *	  - None.
     *
     * Returns: Vector of Strings containing the names of the formal 
     *          arguments spedified in the formal arguments list attribute.
     *
     * Changes:
     *
     *    - None.
     *
     *************************************************************************/

    private Vector<String> parse_pred_formal_arg_list_attribute()
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = 
		"macshapa_odb_reader::parse_pred_formal_arg_list_attribute()";
	final String missing_farg_list_mssg = 
		"A FORMAL-ARG-LIST> attribute appears not to contain a value.  " +
		"Value forced to (|<val>|).\n";
	final String farg_list_type_mismatch_mssg = 
		"The value of the FORMAL-ARG-LIST> attribute must be a list " +
		"of formal arguments.  Value forced to (|<val>|).\n";
	final String overflow_mssg = 
		"Overflow occured in a predicate formal argument list.\n";
        Vector<String> args = null;

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse TRUE on entry");
	}
	
	/* parse the formal argument list attribute */
	
	/* first parse the leading left parenthesis */

	if ( (this.l0_tok).code == L_PAREN_TOK )
	{
	    get_next_token();
	}
	else /* we shouldn't have been called unless the next token is a '(' */
	{
	    throw new SystemErrorException(mName + 
		    "(this.l0_tok).code != L_PAREN_TOK.");
	}

	/* read the a-list entry name */
	if ( ! this.abort_parse )
	{
	    if ( ( (this.l0_tok).code == ALIST_LABEL_TOK ) && 
		 ( (this.l0_tok).aux == FORMAL_ARG_LIST_LABEL ) )
	    {
		get_next_token();
	    }
	    else /* we shouldn't have been called unless the next token is an a-list tag */
	    {
		throw new SystemErrorException(mName + 
			"this.l0_tok != FORMAL-ARG-LIST>.");
	    }
	}

	/* read the value associated with the a-list entry & discard any excess values */
	if ( ! this.abort_parse )
	{
	    switch ( (this.l0_tok).code )
	    {
		case L_PAREN_TOK:
		    args = parse_pred_formal_arg_list();
		    break;

		case R_PAREN_TOK:
		    post_warning_message(EMPTY_ALIST_ENTRY_WARN, 
                            missing_farg_list_mssg);

		    if ( ! this.abort_parse )
		    {
                        args = new Vector<String>();
                        args.add(new String("<val>"));
		    }
		    break;

		case BOOL_TOK:
		case ERROR_TOK:
		case SYMBOL_TOK:
		case FLOAT_TOK:
		case STRING_TOK:
		case INT_TOK:
		case ALIST_LABEL_TOK:
		case PRIVATE_VAL_TOK:
		case SETF_TOK:
		case DB_VAR_TOK:
		case QUOTE_TOK:
		    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
					 farg_list_type_mismatch_mssg); 

		    if ( ! this.abort_parse )
		    {
                        args = new Vector<String>();
                        args.add(new String("<val>"));
		    }

		    if ( ! this.abort_parse )
		    {
			get_next_token();
		    }
		    break;


		case EOF_TOK:
		    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
				       "EOF in a FORMAL-ARG-LIST> attribute.\n", 
				       true, true);
		    break;

		default:
		    throw new SystemErrorException(mName + 
			    "Encountered unknown token type.");
	            /* commented out to keep the compiler happy */
		    // break;
	     }
	 }

	 /* check for EOF */
	 if ( ! this.abort_parse )
	 {
	     if ( (this.l0_tok).code == EOF_TOK )
	     {
		 post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
				    "EOF in a FORMAL-ARG-LIST> attribute.\n", 
				    true, true);
	     }
	 }

	/* discard any excess values that may appear in the FORMAL-ARG-LIST> 
         * a-list entry 
         */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code != R_PAREN_TOK )
	    {
		discard_excess_alist_entry_values();

		if ( ! this.abort_parse )
		{
		    post_warning_message(EXCESS_VALUES_IN_ALIST_ENTRY_WARN,
			    "Excess values encountered a FORMAL-ARG-LIST> " +
			    "attribute.\n");
		}
	    }
	}

	/* read the terminating right parenthesis */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code == R_PAREN_TOK )
	    {
		get_next_token();
	    }
	    else
	    {
		/* since we are cleaning up any excess values in the formal 
		 * arg list attribute, this else clause is unreachable at 
		 * present.  Should we choose to drop the above attempt at 
		 * error recovery, this clause will again become reachable.
		 */

		post_warning_message(RIGHT_PAREN_EXPECTED_WARN, 
			"Closing parenthesis missing from a FORMAL-ARG-LIST> " + 
			"attribute.\n");
	    }
	}

	return args;

    } /* macshapa_odb_reader::parse_pred_formal_arg_list_attribute() */


    /*************************************************************************
     *
     * parse_preds_list_attribute()
     *
     * This method parses the predicates list attribute in the USER>
     * a-list.  Structurally, this attribute is simply an a-list entry
     * with a list value.  The production generating it is given below.
     *
     *        <preds_list_attribute> --> '(' 'PREDICATE-DEFINITIONS>'
     *                                       <pred_def_list> ')'
     *        
     *
     *                                               - 6/14/08
     *
     * Parameters:
     *
     *	  - None.
     *
     * Returns:  Void.
     *
     * Changes:
     *
     *    - None.
     *
     *************************************************************************/

    private void parse_preds_list_attribute()
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = 
		"macshapa_odb_reader::parse_preds_list_attribute()";
	final String value_missing_mssg =
		"The USER> PREDICATE-DEFINITIONS> a-list entry doesn't seem " +
		"to contain a value.\n";
	final String type_mismatch_mssg =
		"The value of the USER> PREDICATE-DEFINITIONS> a-list entry " +
		"must be a (possibly empty) list of predicate definitions.\n"; 

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse true on entry");
	}
	
	/* parse the predicate definitions attribute */
	
	/* first parse the leading left parenthesis */

	if ( (this.l0_tok).code == L_PAREN_TOK )
	{
	    get_next_token();
	}
	else /* we shouldn't have been called unless the next token is a '(' */
	{
	    throw new SystemErrorException(mName + 
		    "(this.l0_tok).code != L_PAREN_TOK.");
	}

	/* read the a-list entry name */
	if ( ! this.abort_parse )
	{
	    if ( ( (this.l0_tok).code == ALIST_LABEL_TOK ) &&
		 ( (this.l0_tok).aux == PREDICATE_DEFINITIONS_LABEL ) )
	    {
		get_next_token();
	    }
	    else /* we shouldn't have been called unless the next   */
	    {    /* token == the PREDICATE-DEFINITIONS> a-list tag. */

		throw new SystemErrorException(mName + 
			"this.l0_tok != \"PREDICATE-DEFINITIONS>\".");
	    }
	}

	if ( ! this.abort_parse )
	{
	    switch ( (this.l0_tok).code )
	    {
		case L_PAREN_TOK:
		    parse_pred_def_list();
		    break;

		case R_PAREN_TOK:
		    post_warning_message(EMPTY_ALIST_ENTRY_WARN, 
					 value_missing_mssg); 
		    break;

		case INT_TOK:
		case ERROR_TOK:
		case SYMBOL_TOK:
		case FLOAT_TOK:
		case STRING_TOK:
		case BOOL_TOK:
		case ALIST_LABEL_TOK:
		case PRIVATE_VAL_TOK:
		case SETF_TOK:
		case DB_VAR_TOK:
		case QUOTE_TOK:
		    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
					 type_mismatch_mssg); 

		    if ( ! this.abort_parse )
		    {
			get_next_token();
		    }
		    break;


		case EOF_TOK:
		    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
			    "EOF in USER> PREDICATE-DEFINITIONS> attribute.\n", 
			    true, true);
		    break;

		default:
		    throw new SystemErrorException(mName + 
			    "Encountered unknown token type.");
	            /* commented out to keep the compiler happy */
		    // break;
	     }
	 }

	 /* check for EOF */
	 if ( ! this.abort_parse )
	 {
	     if ( (this.l0_tok).code == EOF_TOK )
	     {
		post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
			"EOF in USER> PREDICATE-DEFINITIONS> attribute.\n", 
			true, true);
	     }
	 }

	/* discard any excess values that may appear in the MAX-WARNINGS> 
	 * a-list entry 
	 */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code != R_PAREN_TOK )
	    {
		discard_excess_alist_entry_values();

		if ( ! this.abort_parse )
		{
		    post_warning_message(EXCESS_VALUES_IN_ALIST_ENTRY_WARN,
			"Excess values encountered in the USER> " +
			"PREDICATE-DEFINITIONS> attribute.\n");
		}
	     }
	 }

	 /* finally, consume the closing parenthesis */
	 if ( ! this.abort_parse )
	 {
	    if ( (this.l0_tok).code == R_PAREN_TOK )
	    {
		get_next_token();
	    }
	    else 
	    {
		/* Since we are cleaning up any excess values in the 
		 * preds list attribute, this else clause is unreachable at 
		 * present.  Should we choose to drop the above attempt at 
		 * error recovery, this clause will again become reachable.
		 */

		post_warning_message(RIGHT_PAREN_EXPECTED_WARN, 
			"The closing parenthesis was missing from the USER> " +
			"PREDICATE-DEFINITIONS> a-list entry.\n");
	     }
	}

	return;

    } /* macshapa_odb_reader::parse_preds_list_attribute() */


    /*************************************************************************
     *
     * parse_s_var_cell()
     *
     *  This method parses the a-list describing a cell in a spreadsheet
     *  variable, and inserts the cell into the spreadsheet if everything
     *  is in order.  The productions generating a spreadsheet variable 
     *  cell a-list are given below:
     *        
     *      <s_var_cell> --> '(' { <s_var_cell_onset_attribute>
     *                             <s_var_cell_offset_attribute>
     *                             (<s_var_cell_value_attribute>)+ } ')'
     *        
     *      <s_var_cell_onset_attribute> --> '(' 'ONSET>' <integer> ')'
     *        
     *      <s_var_cell_offset_attribute> --> '(' 'OFFSET>' <integer> ')'
     *        
     *      <s_var_cell_value_attribute> --> '(' <formal_arg> <cell_value> ')'
     *        
     *      <cell_value> --> 
     *                <text_cell_value>    (* if <s_var_type> == <<TEXT>> *)
     *                |
     *                <nominal_cell_value> (* if <s_var_type> == <<NOMINAL>> *)
     *                |
     *                <integer_cell_value> (* if <s_var_type> == <<INTEGER>> *)
     *                |
     *                <float_cell_value>   (* if <s_var_type> == <<FLOAT>> *)
     *                |
     *                <pred_cell_value>    (* if <s_var_type> == <<PREDICATE>> *)
     *                |
     *                <matrix_cell_value>  (* if <s_var_type> == <<MATRIX>> *)
     *        
     *      <text_cell_value> --> <text_quote_string>
     *        
     *      <nominal_cell_value> --> <nominal> 
     *                               |
     *                               '|<val>|'  (* if the nominal is undefined *)
     *        
     *      <integer_cell_value> --> <integer>
     *        
     *      <float_cell_value --> <float>
     *        
     *      <pred_cell_value> --> '(' ')' | <pred_value>
     *        
     *      <pred_value> --> '(' <pred_name> (<pred_arg>)+ ')'
     *        
     *      <pred_arg> --> <quote_string> | <nominal> | <pred_value> | 
     *                     <integer> | <float> | <time_stamp> | <formal_arg>
     *        
     *      <time_stamp> --> '(' 'TIME>' <integer> ')' 
     *        
     *      <matrix_cell_value> --> <pred_arg>
     *
     * It should be noted that this grammar is a bit looser that the parser.
     *
     * First, the parser will require that the type of the spreadsheet variable
     * match the type of the value provided for the cell.  
     *
     * Second, the formal argument used as a label in a 
     * <s_var_cell_value_attribute> must match the value in question.  In the 
     * case of spreadsheet variables that are not of matrix type, the argument 
     * must have been the fourth argument in the formal argument list in the 
     * spreadsheet variable declaration.  While it is not immediately apparent 
     * from the code, this means that the formal argument must be |<val>|.
     *
     * In the case of matrix spreadsheet variables, the argument must have 
     * appeared in the formal argument list in the fourth or later position.  
     * Futher, the attributes labeled with formal arguments must appear in 
     * the order in the formal arguments appeared in the spreadsheet variable 
     * declaration.
     *        
     *                                               - 6/14/08
     *
     * Parameters:
     *
     *	  - s_var_col_ID:  ID assigned by the database to the DataColumn into
     *          which we will be loading the spreadsheet variable definition.
     *          This ID is used when inserting cells into the DataColumn.
     *
     *    - s_var_type: Instance of matrixType indicating the type of the 
     *          spreadsheet variable.  This value is used primarily for 
     *          sanity checking.
     *
     *    - s_var_mve: Reference to a copy of the MatrixVocabElement 
     *          describing the structure of the DataColumn into which the 
     *          spreadsheet variable is being loaded.  
     *
     * Returns:  Void.
     *
     * Changes:
     *
     *    - None.
     *
     *************************************************************************/

    final void parse_s_var_cell(long s_var_col_ID,
                                MatrixVocabElement.MatrixType s_var_type,
                                MatrixVocabElement s_var_mve)
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = "macshapa_odb_reader::parse_s_var_cell()";
	final String overflow_mssg = 
		"Overflow occured in a spreadsheet variable cell.\n";
	boolean done;
	boolean have_onset;
	boolean have_offset;
	boolean success;
	int arg_number;
        int num_fargs;
        long dcID;
	TimeStamp onset;
	TimeStamp offset;
	FormalArgument next_farg = null;
        Vector<DataValue> argList = null;
        DataValue next_arg = null;
	Matrix cellValue = null;
        DataCell dc = null;

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse TRUE on entry");
	}
	else if ( s_var_col_ID == DBIndex.INVALID_ID )
	{
	    throw new SystemErrorException(mName + 
		    "s_var_col_ID is invalid on entry.");
	}
        else if ( ( s_var_mve == null ) ||
                  ( s_var_mve.getItsColID() != s_var_col_ID ) )
        {
            throw new SystemErrorException(mName + 
                    "s_var_mve null or col ID mismatch on entry.");
        }
	
	/* parse the spreadsheet variable definition a-list */
	{
	    /* start with a little more sanity checking */

	    if ( ( s_var_type != MatrixVocabElement.MatrixType.FLOAT ) &&
                 ( s_var_type != MatrixVocabElement.MatrixType.INTEGER ) &&
                 ( s_var_type != MatrixVocabElement.MatrixType.MATRIX ) &&
                 ( s_var_type != MatrixVocabElement.MatrixType.NOMINAL ) &&
                 ( s_var_type != MatrixVocabElement.MatrixType.PREDICATE ) &&
                 ( s_var_type != MatrixVocabElement.MatrixType.TEXT ) )
	    {
                throw new SystemErrorException(mName +
                        "s_var_type out of range.");
	    }

	    /* first parse the leading left parenthesis */
	    if ( (this.l0_tok).code == L_PAREN_TOK )
	    {
		if ( ( (this.l1_tok).code == L_PAREN_TOK ) || 
		     ( (this.l1_tok).code == R_PAREN_TOK ) )
		{
		    get_next_token();
		}
		else if ( (this.l1_tok).code == ALIST_LABEL_TOK )
		{
		    post_warning_message(LEFT_PAREN_EXPECTED_WARN, 
			    "The opening left parenthesis of a spreadsheet " +
			    "variable cell a-list appears to be missing.\n");
		}
		else 
		{
		    /* if a left paren is missing, the first item in the a-list 
		     * is not an a-list entry.  If we try to recover from this 
		     * error here, we will only confuse things further.  Thus 
		     * we eat the left parenthesis & let the cards fall where 
		     * they may.
		     */

		    get_next_token();
		}
	    }
	    else /* system error - we shouldn't have been called unless the next token is a '(' */
	    {
		throw new SystemErrorException(mName + 
			"(this.l0_tok).code != L_PAREN_TOK.");
	    }

	    done        = false;
	    have_onset  = false;
	    have_offset = false;
	    onset       = null;
	    offset      = null;
	    arg_number  = 0;
            num_fargs   = s_var_mve.getNumFormalArgs();
            
            if ( num_fargs < 1 )
            {
                throw new SystemErrorException(mName + "mve has no arguments?!?");
                
            }
        
            argList = new Vector<DataValue>();
            next_farg = s_var_mve.getFormalArg(0);

	    /* now parse the a-list assocated with the spreadsheet 
	     * variable definition 
	     */
	    while ( ( ! this.abort_parse ) && 
		    ( ! done ) )
	    {
		 if ( (this.l0_tok).code == L_PAREN_TOK )
		 {
		     if ( (this.l1_tok).code == ALIST_LABEL_TOK )
		     {
			 switch ( (this.l1_tok).aux )
			 {
			     case ONSET_LABEL:
				if ( ! have_onset )
				{
				    have_onset = true;
				    onset = parse_s_var_cell_onset_attribute();
				}
				else
				{
				    post_warning_message(
					DUPLICATE_ALIST_ENTRY_WARN, 
					"Duplicate ONSET> entry in a " +
					"spreadsheet variable cell a-list.\n");

				    if ( ! this.abort_parse )
				    {
					 parse_unknown_alist_entry();
				    }
				}
				break;

			    case OFFSET_LABEL:
				if ( ! have_offset )
				{
				    have_offset = true;
				    offset = parse_s_var_cell_offset_attribute();
				}
				else
				{
				    post_warning_message(
					DUPLICATE_ALIST_ENTRY_WARN, 
					"Duplicate OFFSET> entry in a " +
					"spreadsheet variable cell a-list.\n");

				    if ( ! this.abort_parse )
				    {
					 parse_unknown_alist_entry();
				    }
				}
				break;

			    default:
				post_warning_message(
				    UNKNOWN_OR_UNEXPECTED_ALIST_ENTRY_WARN, 
				    "The entry is located in a spreadsheet " +
				    "variable cell a-list.\n");

				if ( ! this.abort_parse )
				{
				    parse_unknown_alist_entry();
				}
                                break;
                        }
		    }
		    else if ( ( (this.l1_tok).code == SYMBOL_TOK ) && 
			       ( (((this.l1_tok).aux) & FORMAL_ARG_FLAG) != 0 ) )
                    {
                        if ( ( next_farg != null ) &&
                              ( next_farg.getFargName().
                                compareTo(this.l1_tok.str.toString()) == 0 ) )
                        {
			    next_arg = parse_s_var_cell_value_attribute(
                                            next_farg, arg_number, s_var_type);
                            
                            argList.add(next_arg);
                             
			    arg_number++;
                             
                            if ( arg_number < num_fargs )
                            {
                                next_farg = s_var_mve.getFormalArg(arg_number);
                            }
                            else
                            {
                                next_farg = null;
                            }
			 }
			 else
			 {
			    post_warning_message(
				    UNKNOWN_OR_OUT_OF_ORDER_CELL_VALUE_WARN, 
				    null);

			    if ( ! this.abort_parse )
			    {
				parse_arbitrary_list();
			    }
			 }
		     }
		     else /* a-list contains a list that is not an a-list entry. */
			  /* read it & discard it.                               */
		     {
			post_warning_message(NON_ALIST_ENTRY_LIST_IN_ALIST_WARN, 
				"The list is located in a spreadsheet " +
				"variable cell a-list.\n");

			if ( ! this.abort_parse )
			{
			    parse_arbitrary_list();
			}
		    }
		}
		else if ( (this.l0_tok).code == R_PAREN_TOK )
		{
		    done = true;
		    get_next_token();
		}
		else if ( (this.l0_tok).code == EOF_TOK )
		{
		    done = true;
		    post_error_message(UNEXPECTED_END_OF_FILE_ERR,
			"EOF occurred in a spreadsheet variable cell a-list.\n",
			true, true);
		}
		else /* (this.l0_tok).code isn't '(', ')', or EOF */
		{
		    post_warning_message(NON_ALIST_ENTRY_ATOM_IN_ALIST_WARN, 
			    "The atom was detected in a spreadsheet variable " +
			    "cell a-list.\n");

		    if ( ! this.abort_parse )
		    {
			get_next_token();
		    }
		}
	    }

	    /* Issue a warning if the onset is missing */
	    if ( ( ! this.abort_parse ) && ( ! have_onset ) )
	    {
		post_warning_message(CELL_WITH_UNDEFINED_ONSET_WARN, null);
                onset = new TimeStamp(MACSHAPA_TICKS_PER_SECOND, 
                                      MACSHAPA_MIN_TIME);
	    }

	    /* Issue a warning if the offset is missing */
	    if ( ( ! this.abort_parse ) && ( ! have_offset ) )
	    {
		post_warning_message(CELL_WITH_UNDEFINED_OFFSET_WARN, null);
                offset = new TimeStamp(MACSHAPA_TICKS_PER_SECOND, 
                                       MACSHAPA_MIN_TIME);
	    }

	    /* if we ran out of arguments before we ran out of formal 
	     * arguments, must issue a warning && insert formal arguments 
	     * or a null value depending on the type of the spreadsheet 
	     * variable.  
             *
             * In the old MacSHAPA odb reader code, there was an exception 
             * for variable length matrix spreadsheet variables, where it was
             * sufficient to sinply ensure that there was at least one argument,
             * inserting a formal argument if there were no arguments at all.
             *
             * This changes in OpenShapa, since we are building the cell, 
             * instead of a string that will be used to construct a cell.
             * However, exactly how we will handle variable length matricies
             * internally is TBD.  Thus for now thow a system error if the
             * cell is in a variable length matrix datacolumn.
	     */
	    if ( ! this.abort_parse )
	    {
                if ( ( s_var_type == MatrixVocabElement.MatrixType.MATRIX ) &&
                     ( s_var_mve.getVarLen() ) )
                {
                    throw new SystemErrorException(mName + 
                            "we don't handle var len matricies just yet.");
                }
                
                while ( next_farg != null )
                {
                    switch ( s_var_type )
                    {
                        case FLOAT:
                            next_arg = new FloatDataValue(this.db,
                                                          next_farg.getID());
                            break;
                            
                        case INTEGER:
                            next_arg = new IntDataValue(this.db,
                                                        next_farg.getID());
                            break;
                            
                        case MATRIX:
                            next_arg = new UndefinedDataValue(this.db,
                                                       next_farg.getID(),
                                                       next_farg.getFargName());
                            break;
                            
                        case NOMINAL:
                            next_arg = new NominalDataValue(this.db,
                                                            next_farg.getID());
                            break;
                            
                        case PREDICATE:
                            next_arg = new PredDataValue(this.db,
                                                         next_farg.getID());
                            break;
                            
                        case TEXT:
                            next_arg = new TextStringDataValue(this.db,
                                                             next_farg.getID());
                            break;

                        default:
                            throw new SystemErrorException(mName + 
                                 "s_var_type out of range.");
                            /* commented out to keep the compiler happy */
                            // break;
                    }
                    
                    argList.add(next_arg);

                    arg_number++;

                    if ( arg_number < num_fargs )
                    {
                        next_farg = s_var_mve.getFormalArg(arg_number);
                    }
                    else
                    {
                        next_farg = null;
                    }
                }
	    }
            
            if ( ( arg_number > 0 ) &&
                 ( s_var_type != MatrixVocabElement.MatrixType.MATRIX ) )
            {
                throw new SystemErrorException(mName + 
                        "non-matrix s_var with more than one argument?!?");
            }

	    /* if no errors, create the cell and insert it */
	    if ( ! this.abort_parse )
	    {
                long s_var_mve_ID = s_var_mve.getID();
                
                cellValue = new Matrix(this.db, s_var_mve_ID, argList);
                
                dc = new DataCell(this.db, null, s_var_col_ID, s_var_mve_ID, 
                                  onset, offset, cellValue);
                
                dcID = this.db.appendCell(dc);
                
                /* if debug level is high enough, dump the cell definition */
                if ( this.debug_level >= 2 )
                {
                    dc = (DataCell)(this.db.getCell(dcID));
                    dump_s_var_cell_definition_to_listing(dc);
                }
	    }
	 }

	 return;

    } /* macshapa_odb_reader::parse_s_var_cell() */


    /*******************************************************************************
     *
     * parse_s_var_cell_list()
     *
     * This method parses a (posibly empty) list of spreadsheet variable
     * cells.  Structurally, this list is simply a (possibly empty) list 
     * of lists.  The production generating it is given below:
     *
     *      <s_var_cell_list> --> '(' (<s_var_cell>)* ')'
     *
     * In reading this function, the following productions for <s_var_cell> 
     * are also useful, as they are used in error detection & recovery.  Note
     * however that these productions are not parsed in this function, but 
     * rather in parse_s_var_cell() and its decendants.
     *        
     *     <s_var_cell> --> '(' { <s_var_cell_onset_attribute>
     *                            <s_var_cell_offset_attribute>
     *                            (<s_var_cell_value_attribute>)+ } ')'
     *        
     *     <s_var_cell_onset_attribute> --> '(' 'ONSET>' <integer> ')'
     *        
     *     <s_var_cell_offset_attribute> --> '(' 'OFFSET>' <integer> ')'
     *        
     *     <s_var_cell_value_attribute> --> '(' <formal_arg> <cell_value> ')'
     *        
     *     <cell_value> --> 
     *                <text_cell_value>    (* if <s_var_type> == <<TEXT>> *)
     *                |
     *                <nominal_cell_value> (* if <s_var_type> == <<NOMINAL>> *)
     *                |
     *                <integer_cell_value> (* if <s_var_type> == <<INTEGER>> *)
     *                |
     *                <float_cell_value>   (* if <s_var_type> == <<FLOAT>> *)
     *                |
     *                <pred_cell_value>    (* if <s_var_type> == <<PREDICATE>> *)
     *                |
     *                <matrix_cell_value>  (* if <s_var_type> == <<MATRIX>> *)
     *        
     *     <text_cell_value> --> <text_quote_string>
     *        
     *     <nominal_cell_value> --> <nominal> 
     *                              |
     *                              '|<val>|'  (* if the nominal is undefined *)
     *        
     *     <integer_cell_value> --> <integer>
     *        
     *     <float_cell_value --> <float>
     *        
     *     <pred_cell_value> --> '(' ')' | <pred_value>
     *        
     *     <pred_value> --> '(' <pred_name> (<pred_arg>)+ ')'
     *        
     *     <pred_arg> --> <quote_string> | <nominal> | <pred_value> | 
     *                    <integer> | <float> | <time_stamp> | <formal_arg>
     *        
     *     <time_stamp> --> '(' 'TIME>' <integer> ')' 
     *        
     *     <matrix_cell_value> --> <pred_arg>
     *        
     *     <s_var_def_vocab_attribute> --> '(' VOCAB> <vocab_list> ')'
     *        
     *     <vocab_list> --> '(' (<vocab_entry>)* ')'
     *        
     *     <vocab_entry> --> <pred_name>
     *
     *                                              - 6/15/08
     *
     * Parameters:
     *
     *	  - s_var_col_ID:  ID assigned by the database to the DataColumn into
     *          which we will be loading the spreadsheet variable definition.
     *          This ID is used when inserting cells into the DataColumn.
     *
     *    - s_var_type: Instance of matrixType indicating the type of the 
     *          spreadsheet variable.  This value is used primarily for 
     *          sanity checking.
     *
     *    - s_var_mve_ID: ID assigned to the MatrixVocabElement describing
     *          the structure of the DataColumn into which the spreadsheet
     *          variable is being loaded.  
     *
     * Returns:  Void.
     *
     * Changes:
     *
     *    - None.
     *
     **************************************************************************/

    private void parse_s_var_cell_list(long s_var_col_ID,
                                       MatrixVocabElement.MatrixType s_var_type,
                                       long s_var_mve_ID)
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = "macshapa_odb_reader::parse_s_var_cell_list()";
	boolean done;
        MatrixVocabElement s_var_mve = null;

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse TRUE on entry");
	}
	
	if ( s_var_col_ID == DBIndex.INVALID_ID )
	{
	    throw new SystemErrorException(mName + 
                    "s_var_col_ID is invalid on entry.");
	}
	
	if ( s_var_mve_ID == DBIndex.INVALID_ID )
	{
	    throw new SystemErrorException(mName + 
                    "s_var_mve_ID is invalid on entry.");
	}
        
        /* get a copy of the matrix vocab element describing the data column
         * into which we are going to load the cells.  This call throws
         * a system error on failure, so no error check required.
         */
        s_var_mve = this.db.getMatrixVE(s_var_mve_ID);
	
	/* parse the spreadsheet variable cells list */
	
	/* first parse the leading left parenthesis */

	if ( (this.l0_tok).code == L_PAREN_TOK )
	{
	    if ( ( (this.l1_tok).code == L_PAREN_TOK ) || 
		 ( (this.l1_tok).code == R_PAREN_TOK ) )
	    {
		get_next_token();
	    }
	    else if ( (this.l1_tok).code == SYMBOL_TOK )
	    {
		post_warning_message(LEFT_PAREN_EXPECTED_WARN, 
			"The opening left parenthesis of a spreadsheet " +
			"variable cell list appears to be missing.\n");
	    }
	    else 
	    {
		/* If in fact the leading parenthesis in a spreadsheet variable 
		 * cell list is missing, the first item in the list is ! a cell.
		 * Rather than confuse things further by inserting a 
		 * parenthesis, we eat the parenthesis here, and let the 
		 * error come to light below.
		 */

		get_next_token();
	    }
	}
	else /* we shouldn't have been called unless the next token is a '(' */
	{
	    throw new SystemErrorException(mName + 
		    "(this.l0_tok).code != L_PAREN_TOK.");
	 }

	 /* parse the spreadsheet variables declarations list */
	 done = false;

	 while ( ( ! this.abort_parse ) && ( ! done ) )
	 {
	     switch ( (this.l0_tok).code )
	     {
		 case L_PAREN_TOK:
		     parse_s_var_cell(s_var_col_ID, s_var_type, s_var_mve);
		     break;

		 case R_PAREN_TOK:
		     done = true;
		     get_next_token();
		     break;

		case EOF_TOK:
		    done = true;
		    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
			    "EOF encountered in a spreadsheet variable " +
			    "cell list.\n", true, true);
		    break;

		case INT_TOK:
		case ERROR_TOK:
		case SYMBOL_TOK:
		case FLOAT_TOK:
		case STRING_TOK:
		case BOOL_TOK:
		case ALIST_LABEL_TOK:
		case PRIVATE_VAL_TOK:
		case SETF_TOK:
		case DB_VAR_TOK:
		case QUOTE_TOK:
		    post_warning_message(ATOM_IN_A_S_VAR_CELL_LIST_WARN, null);

		    if ( ! this.abort_parse )
		    {
			get_next_token();
		    }
		    break;


		default:
		    throw new SystemErrorException(mName + 
			    "Encountered unknown token type.");
	            /* commented out to keep the compiler happy */
		    // break;
	    }
	}

	return;

    } /* macshapa_odb_reader::parse_s_var_cell_list() */


    /*************************************************************************
     *
     * parse_s_var_cell_offset_attribute()
     *
     * This method parses an ONSET> attribute, which at present can only 
     * appear in the context of a spreadheet variable cell a-list.  
     * This attribute is generated by the following production:
     *        
     *     <s_var_cell_offset_attribute> --> '(' 'OFFSET>' <integer> ')'
     *
     * The function returns a reference to an instance of TimeStamp with
     * ticks per second taken from this.db, and ticks specified by the 
     * value of the integer associated with the OFFSET> attribute, or a 
     * default value if the integer is missing.  Out of range values are 
     * forced to the nearest legal value.
     *
     *                                              - 6/15/08
     *
     * Parameters:
     *
     *	  - None.
     *
     * Returns:  Reference to and instance of TimeStamp representing the
     *          time associated with the OFFSET> attribute, or a default 
     *          time if the integer is missing, or the nearest legal value
     *          if the integer is out of range.
     *
     * Changes:
     *
     *    - None.
     *
     *************************************************************************/

    private TimeStamp parse_s_var_cell_offset_attribute()
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = 
		"macshapa_odb_reader::parse_s_var_cell_offset_attribute()";
	final String missing_offset_mssg = 
		"An OFFSET> attribute appears  to contain a value.  " +
		"Default value used.\n";
	final String offset_type_mismatch_mssg = 
		"The value of an OFFSET> attribute must be an integer.  " +
		"Default value used.\n";
	boolean out_of_range;
        long ticks = MACSHAPA_MIN_TIME; /* default */
	TimeStamp offset = null;

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse TRUE on entry");
	}
	
	/* parse the integer associated with the attribute */
	
	/* first parse the leading left parenthesis */

	if ( (this.l0_tok).code == L_PAREN_TOK )
	{
	    get_next_token();
	}
	else /* we shouldn't have been called unless the next token is a '(' */
	{
            throw new SystemErrorException(mName + 
                    "(this.l0_tok).code != L_PAREN_TOK.");
	}

	/* read the a-list entry name */
	if ( ! this.abort_parse )
	{
	    if ( ( (this.l0_tok).code == ALIST_LABEL_TOK ) && 
		 ( (this.l0_tok).aux == OFFSET_LABEL ) )
	    {
		get_next_token();
	    }
	    else /* we shouldn't have been called unless the next token is OFFSET> */
	    {
                throw new SystemErrorException(mName + 
                        "this.l0_tok != OFFSET>.");
	    }
	}

	/* read the value associated with the a-list entry & discard 
         * any excess values 
         */
	if ( ! this.abort_parse )
        {
	    switch ( (this.l0_tok).code )
	    {
		case INT_TOK:
		    out_of_range = false;

		    if ( ((this.l0_tok).val) < (double)MACSHAPA_MIN_TIME )
		    {
			out_of_range = true;
			ticks = MACSHAPA_MIN_TIME;
		    }
		    else if ( ((this.l0_tok).val) > (double)MACSHAPA_MAX_TIME )
		    {
			out_of_range = true;
			ticks = MACSHAPA_MAX_TIME;
		    }
		    else
		    {
                        ticks = (long)((this.l0_tok).val);
                    }

		    if ( out_of_range )
		    {
			post_warning_message(TIME_OUT_OF_RANGE_WARN, 
				"The out of range value appeared in an " +
				"OFFSET> attribute\n"); 
		    }

		    if ( ! this.abort_parse )
		    {
			get_next_token();
		    }
		    break;

		case R_PAREN_TOK:
		    post_warning_message(EMPTY_ALIST_ENTRY_WARN, 
					 missing_offset_mssg); 
		    break;

		case L_PAREN_TOK:
		    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
					 offset_type_mismatch_mssg); 

		    if ( ! this.abort_parse )
		    {
			parse_arbitrary_list();
		    }
		    break;

		case PRIVATE_VAL_TOK:
		case BOOL_TOK:
		case ERROR_TOK:
		case SYMBOL_TOK:
		case FLOAT_TOK:
		case STRING_TOK:
		case ALIST_LABEL_TOK:
		case SETF_TOK:
		case DB_VAR_TOK:
		case QUOTE_TOK:
		    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
					 offset_type_mismatch_mssg); 

		    if ( ! this.abort_parse )
		    {
			get_next_token();
		    }
		    break;

		case EOF_TOK:
		    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
				       "EOF in an OFFSET> attribute.\n", 
				       true, true);
		    break;

		default:
		    throw new SystemErrorException(mName + 
			    "Encountered unknown token type.");
	            /* commented out to keep the compiler happy */
		    // break;
	    }
	}

	/* check for EOF */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code == EOF_TOK )
	    {
		post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
				   "EOF in an OFFSET> attribute.\n", 
				   true, true);
	    }
	}

	/* discard any excess values that may appear in the OFFSET> a-list entry */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code != R_PAREN_TOK )
	    {
		discard_excess_alist_entry_values();

		if ( ! this.abort_parse )
		{
		    post_warning_message(EXCESS_VALUES_IN_ALIST_ENTRY_WARN,
			    "Excess values encountered an OFFSET> attribute.\n");
		}
	    }
	}

	/* read the terminating right parenthesis */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code == R_PAREN_TOK )
	    {
		get_next_token();
	    }
	    else
	    {
		/* since we are cleaning up any excess values in the offset
		 * attribute, this else clause is unreachable at present.
		 * Should we choose to drop the above attempt at error recovery,
		 * this clause will again become reachable.
		 */

		post_warning_message(RIGHT_PAREN_EXPECTED_WARN, 
		    "Closing parenthesis missing from an OFFSET> attribute.\n");
	    }
	}
        
        offset = new TimeStamp(this.db.getTicks(), ticks);

	return(offset);

    } /* macshapa_odb_reader::parse_s_var_cell_offset_attribute() */


    /*************************************************************************
     *
     * parse_s_var_cell_onset_attribute()
     *
     * This method parses an ONSET> attribute, which at present can only 
     * appear in the context of a spreadheet variable cell a-list.  
     * This attribute is generated by the following production:
     *        
     *     <s_var_cell_onset_attribute> --> '(' 'ONSET>' <integer> ')'
     *
     * The function returns a reference to an instance of TimeStamp, whose
     * ticks pers second is takend from this.db, and whose ticks is simply
     * the of the integer value associated with the ONSET> attribute, or a 
     * default value if the integer is missing.  Out of range values are 
     * forced to the nearest legal value.
     *
     *                                              - 6/15/08
     *
     * Parameters:
     *
     *	  - None.
     *
     * Returns: Reference to an instance of TimeStamp representing the 
     *          the time specified in the attribute, or a default value 
     *          if the integer is missing, or the nearest legal value
     *          if the integer is out of range.
     *
     * Changes:
     *
     *    - None.
     *
     *************************************************************************/

    private TimeStamp parse_s_var_cell_onset_attribute()
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = 
		"macshapa_odb_reader::parse_s_var_cell_onset_attribute()";
	final String missing_onset_mssg = 
		"An ONSET> attribute appears not to contain a value.  " +
		"Default value used.\n";
	final String onset_type_mismatch_mssg = 
		"The value of an ONSET> attribute must be an integer.  " +
		"Default value used.\n";
	boolean out_of_range;
	TimeStamp onset = null;
        long ticks = MACSHAPA_MIN_TIME; /* default value */

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse TRUE on entry");
	}
	
	/* parse the integer associated with the attribute */
	
	/* first parse the leading left parenthesis */

	if ( (this.l0_tok).code == L_PAREN_TOK )
	{
	    get_next_token();
	}
	else /* we shouldn't have been called unless the next token is a '(' */
	{
	    throw new SystemErrorException(mName + 
		    "(this.l0_tok).code != L_PAREN_TOK.");
	}

	/* read the a-list entry name */
	if ( ! this.abort_parse )
	{
	    if ( ( (this.l0_tok).code == ALIST_LABEL_TOK ) && 
		 ( (this.l0_tok).aux == ONSET_LABEL ) )
	    {
		get_next_token();
	    }
	    else /* we shouldn't have been called unless the next token is ONSET> */
	    {
		throw new SystemErrorException(mName + "this.l0_tok != ONSET>.");
	    }
	}

	/* read the value associated with the a-list entry & discard 
	 * any excess values 
	 */
	if ( ! this.abort_parse )
	{
	     switch ( (this.l0_tok).code )
	     {
		case INT_TOK:
		    out_of_range = false;

		    if ( ((this.l0_tok).val) < (double)MACSHAPA_MIN_TIME )
		    {
			out_of_range = true;
			ticks = MACSHAPA_MIN_TIME;
		    }
		    else if ( ((this.l0_tok).val) > (double)MACSHAPA_MAX_TIME )
		    {
			out_of_range = true;
			ticks = MACSHAPA_MAX_TIME;
		    }
		    else
		    {
                        ticks = (long)((this.l0_tok).val);
                    }
                    
		    onset = new TimeStamp(MACSHAPA_TICKS_PER_SECOND, ticks);

		    if ( out_of_range )
		    {
			post_warning_message(TIME_OUT_OF_RANGE_WARN, 
				"The out of range value appeared in an " +
				"ONSET> attribute\n"); 
		    }

		    if ( ! this.abort_parse )
		    {
			get_next_token();
		    }
		    break;

		case R_PAREN_TOK:
		    post_warning_message(EMPTY_ALIST_ENTRY_WARN, 
					 missing_onset_mssg); 
		    break;

		case L_PAREN_TOK:
		    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
					 onset_type_mismatch_mssg); 

		    if ( ! this.abort_parse )
		    {
			parse_arbitrary_list();
		    }
		    break;

		case PRIVATE_VAL_TOK:
		case BOOL_TOK:
		case ERROR_TOK:
		case SYMBOL_TOK:
		case FLOAT_TOK:
		case STRING_TOK:
		case ALIST_LABEL_TOK:
		case SETF_TOK:
		case DB_VAR_TOK:
		case QUOTE_TOK:
		    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
					 onset_type_mismatch_mssg); 

		    if ( ! this.abort_parse )
		    {
			get_next_token();
		    }
		    break;

		case EOF_TOK:
		    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
				       "EOF in an ONSET> attribute.\n", 
				       true, true);
		    break;

		default:
		    throw new SystemErrorException(mName + 
			    "Encountered unknown token type.");
	            /* commented out to keep the compiler happy */
		    // break;
	     }
	 }

	/* check for EOF */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code == EOF_TOK )
	    {
		post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
			           "EOF in an ONSET> attribute.\n", 
				   true, true);
	    }
	}

	/* discard any excess values that may appear in the ONSET> a-list entry */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code != R_PAREN_TOK )
	    {
		discard_excess_alist_entry_values();

		if ( ! this.abort_parse )
		{
		    post_warning_message(EXCESS_VALUES_IN_ALIST_ENTRY_WARN,
			    "Excess values encountered an ONSET> attribute.\n");
		}
	    }
	}

	/* read the terminating right parenthesis */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code == R_PAREN_TOK )
	    {
		get_next_token();
	    }
	    else
	    {
		/* since we are cleaning up any excess values in the onset
		 * attribute, this else clause is unreachable at present.
		 * Should we choose to drop the above attempt at error recovery,
		 * this clause will again become reachable.
		 */

		post_warning_message(RIGHT_PAREN_EXPECTED_WARN, 
		    "Closing parenthesis missing from an ONSET> attribute.\n");
	    }
	}
        
        onset = new TimeStamp(this.db.getTicks(), ticks);

	return(onset);

    } /* macshapa_odb_reader::parse_s_var_cell_onset_attribute() */


    /*************************************************************************
     *
     * parse_s_var_cell_value_attribute()
     *
     * This method parses an a-list entry defining the value of one of the
     * formal agruments in a cell in a spreadsheet variable, and loads either
     * the associated value or the default/undefined value of the cell into the 
     * insertion buffer.  The productions generating a spreadsheet variable cell 
     * value attribute are given below:
     *        
     *     <s_var_cell_value_attribute> --> '(' <formal_arg> <cell_value> ')'
     *        
     *     <cell_value> --> 
     *                <text_cell_value>    (* if <s_var_type> == <<TEXT>> *)
     *                |
     *                <nominal_cell_value> (* if <s_var_type> == <<NOMINAL>> *)
     *                |
     *                <integer_cell_value> (* if <s_var_type> == <<INTEGER>> *)
     *                |
     *                <float_cell_value>   (* if <s_var_type> == <<FLOAT>> *)
     *                |
     *                <pred_cell_value>    (* if <s_var_type> == <<PREDICATE>> *)
     *                |
     *                <matrix_cell_value>  (* if <s_var_type> == <<MATRIX>> *)
     *        
     *     <text_cell_value> --> <text_quote_string>
     *        
     *     <nominal_cell_value> --> <nominal> 
     *                                     |
     *                              '|<val>|'  (* if the nominal is undefined *)
     *        
     *     <integer_cell_value> --> <integer>
     *        
     *     <float_cell_value> --> <float>
     *        
     *     <pred_cell_value> --> '(' ')' | <pred_value>
     *        
     *     <pred_value> --> '(' <pred_name> (<pred_arg>)+ ')'
     *        
     *     <pred_arg> --> <quote_string> | <nominal> | <pred_value> | 
     *                    <integer> | <float> | <time_stamp> | <formal_arg>
     *        
     *     <time_stamp> --> '(' 'TIME>' <integer> ')' 
     *        
     *     <matrix_cell_value> --> <pred_arg>
     *        
     *                                                     - 6/15/08
     *
     * Parameters:
     *
     *	  - farg:  Reference to a copy of the instance of FormalArgument 
     *          representing the formal argument whose value attribute is 
     *          being parsed.
     *
     *    - arg_num:  Number of the current argument in the target spreadsheet
     *          variable's formal argument list, less three.  (recall that the 
     *          first three arguments must be <ord>, <onset>, and <offset>.  
     *          Since these arguments are not thought of as containing the 
     *          "value" of the cell, they are not included in the argument 
     *          number count.) This means that in non-matrix cases, arg_num 
     *          must always be 0.
     *
     *    - s_var_type:  Type of the DataColumn in which the target cell
     *          will reside.
     *
     * Returns:  Void.
     *
     * Changes:
     *
     *    - None.
     *
     **************************************************************************/

    private DataValue parse_s_var_cell_value_attribute(
                                      FormalArgument farg,
                                      int arg_num,
                                      MatrixVocabElement.MatrixType s_var_type)
        throws SystemErrorException,
               java.io.IOException
    {
	final String mName = 
		"macshapa_odb_reader::parse_s_var_cell_value_attribute()";
	final String overflow_mssg = 
		"Overflow occured in a cell value attribute.\n";
	DataValue dv = null;
        
        if ( ( farg == null ) || ( arg_num < 0 ) )

	{
	    throw new SystemErrorException(mName + 
		    "invalid parameter(s) on entry.");
	}
	
	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse TRUE on entry");
	}

	/* try to parse the cell value attribute */
	
	/* start with a little more sanity checking */

	if ( ( s_var_type != MatrixVocabElement.MatrixType.FLOAT ) &&
             ( s_var_type != MatrixVocabElement.MatrixType.INTEGER ) &&
             ( s_var_type != MatrixVocabElement.MatrixType.MATRIX ) &&
             ( s_var_type != MatrixVocabElement.MatrixType.NOMINAL ) &&
             ( s_var_type != MatrixVocabElement.MatrixType.PREDICATE ) &&
             ( s_var_type != MatrixVocabElement.MatrixType.TEXT ) )
	{
	    throw new SystemErrorException(mName + 
		    "s_var_ptr->kind out of range.");
	}

	if ( ! this.abort_parse )
	{
	    if ( ( arg_num > 0 ) && 
                 ( s_var_type != MatrixVocabElement.MatrixType.MATRIX ) )
	    {
		throw new SystemErrorException(mName + 
			"((arg_num > 1) && (type != matrixID))");
	    }
	}

	/* if we haven't detected any errors so far, start the actual parse */

	/* first parse the leading left parenthesis */
	if ( (this.l0_tok).code == L_PAREN_TOK )
	{
	    get_next_token();
	}
	else /* system error - we shouldn't have been called unless the next token is a '(' */
	{
	    throw new SystemErrorException(mName + 
		    "(this.l0_tok).code != L_PAREN_TOK.");
	}


	/* read the name of the formal argument used to label the attribute */
	if ( ! this.abort_parse )
	{
	    if ( ( (this.l0_tok).code == SYMBOL_TOK ) && 
		 ( (((this.l0_tok).aux) & FORMAL_ARG_FLAG) != 0 ) &&
                 ( farg.getFargName().
                   compareTo(this.l0_tok.str.toString()) == 0 ) )
	    {
		get_next_token();
	    }
	    else /* we shouldn't have been called unless the next token */
	    {    /* is the formal argument referenced by farg.      */
		throw new SystemErrorException(mName + 
			"this.l0_tok.str != farg.getFargName()");
	    }
        }


        /* parse the value associated with the attribute */
        if ( ! this.abort_parse )
        {
            /* switch on the type of the spreadsheet variable, and then call  */
            /* the appropriate routine to parse the value and load it into    */
            /* an instance of DataValue.                                      */
            /*                                                                */
            /* Note that the functions called in the following case statement */
            /* consume the value argument of the a-list entry, if it exists.  */
            /* Excess values, if any, are discarded below.                    */
            switch ( s_var_type )
            {
                case FLOAT:
                    dv = parse_float_cell_value(farg);
                    break;

                case INTEGER:
                    dv = parse_integer_cell_value(farg);
                    break;

                case MATRIX:
                    dv = parse_matrix_cell_value(farg);
                    break;

                case NOMINAL:
                    dv = parse_nominal_cell_value(farg);
                    break;

                case PREDICATE:
                    dv = parse_pred_cell_value(farg);
                    break;

                case TEXT:
                    dv = parse_text_cell_value(farg);
                    break;

                default:
                    throw new SystemErrorException(mName + 
                         "s_var_type out of range.");
	            /* commented out to keep the compiler happy */
                    // break;
            }
        }

	/* check for EOF */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code == EOF_TOK )
	    {
		post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
				   "EOF in a cell value attribute.\n", 
				   true, true);
	    }
	}

	/* discard any excess values that may appear in the cell value a-list entry */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code != R_PAREN_TOK )
	    {
		discard_excess_alist_entry_values();

		if ( ! this.abort_parse )
		{
		    post_warning_message(EXCESS_VALUES_IN_ALIST_ENTRY_WARN,
			"Excess values encountered a cell value attribute.\n");
		}
	    }
	}

	/* read the terminating right parenthesis */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code == R_PAREN_TOK )
	    {
		get_next_token();
	    }
	    else
	    {
		/* Since we are cleaning up any excess values in the cell value
		 * attribute, this else clause is unreachable at present.
		 * Should we choose to drop the above attempt at error recovery,
		 * this clause will again become reachable.
		 */

		post_warning_message(RIGHT_PAREN_EXPECTED_WARN, 
		    "Closing parenthesis missing from a cell value attribute.\n");
	    }
	}

	return dv;

    } /* macshapa_odb_reader::parse_s_var_cell_value_attribute() */


    /*************************************************************************
     *
     * parse_s_var_col_width_attribute()
     *
     * This method parses a COLUMN-WIDTH> attribute, which at present can only 
     * appear in the context of a spreadheet variable declaration or definition.  
     * This attribute is generated by the following production:
     *        
     *     <s_var_col_width_attribute> --> '(' 'COLUMN-WIDTH>' <integer> ')'
     *
     * The function returns value of the integer associated with the 
     * COLUMN-WIDTH> attribute, or a default value if the integer is missing.  
     * Out of range values are forced to the nearest legal value.
     *
     *                                              - 6/15/08
     *
     * Parameters:
     *
     *	  - None.
     *
     * Return Value:  Integer associated with the COLUMN-WIDTH> attribute, or a 
     *	    default value if the integer is missing, or the nearest legal value
     *      if the integer is out of range.
     *
     * Changes:
     *
     *    - None.
     *
     *************************************************************************/

    private int parse_s_var_col_width_attribute()
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = 
		"macshapa_odb_reader::parse_s_var_col_width_attribute()";
	final String missing_col_width_mssg = 
		"A COLUMN-WIDTH> attribute appears not to contain a value.  " +
		"Default value used.\n";
	final String col_width_type_mismatch_mssg = 
		"The value of the COLUMN-WIDTH> attribute must be an integer.  " +
		"Default value used.\n";
	boolean out_of_range;
	int col_width;

	col_width = DEFAULT_HEXTENT; /* default value */

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse TRUE on entry");
	}
	
	/* parse the private value associated with the attribute */
	{
	    /* first parse the leading left parenthesis */

	    if ( (this.l0_tok).code == L_PAREN_TOK )
	    {
		get_next_token();
	    }
	    else /* we shouldn't have been called unless the next token is a '(' */
	    {
		throw new SystemErrorException(mName + 
			"(this.l0_tok).code != L_PAREN_TOK.");
	    }

	    /* read the a-list entry name */
	    if ( ! this.abort_parse )
	    {
		if ( ( (this.l0_tok).code == ALIST_LABEL_TOK ) && 
		     ( (this.l0_tok).aux == COLUMN_WIDTH_LABEL ) )
		{
		    get_next_token();
		}
		else /* we shouldn't have been called unless the next token is an a-list tag */
		{
		    throw new SystemErrorException(mName + 
			    "this.l0_tok != COLUMN-WIDTH>.");
		}
	    }

	    /* read the value associated with the a-list entry & discard any 
	     * excess values 
	     */
	    if ( ! this.abort_parse )
	    {
		switch ( (this.l0_tok).code )
		{
		    case INT_TOK:
			out_of_range = false;

			if ( (int)((this.l0_tok).val) < MIN_COLUMN_WIDTH )
			{
			    out_of_range = true;
			    col_width = MIN_COLUMN_WIDTH;
			}
			else if ( (int)((this.l0_tok).val) > MAX_COLUMN_WIDTH )
			{
			    out_of_range = true;
			    col_width = MAX_COLUMN_WIDTH;
			}
			else
			{
			    col_width = (int)((this.l0_tok).val);
			}

			if ( out_of_range )
			{
			    post_warning_message(COL_WIDTH_OUT_OF_RANGE_WARN, 
						 null); 
			}

			if ( ! this.abort_parse )
			{
			    get_next_token();
			}
			break;

		    case R_PAREN_TOK:
			post_warning_message(EMPTY_ALIST_ENTRY_WARN, 
					     missing_col_width_mssg); 
			break;

		    case L_PAREN_TOK:
			post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
					     col_width_type_mismatch_mssg); 

			if ( ! this.abort_parse )
			{
			    parse_arbitrary_list();
			}
			break;

		    case PRIVATE_VAL_TOK:
		    case BOOL_TOK:
		    case ERROR_TOK:
		    case SYMBOL_TOK:
		    case FLOAT_TOK:
		    case STRING_TOK:
		    case ALIST_LABEL_TOK:
		    case SETF_TOK:
		    case DB_VAR_TOK:
		    case QUOTE_TOK:
			post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
					     col_width_type_mismatch_mssg); 

			if ( ! this.abort_parse )
			{
			    get_next_token();
			}
			break;

		    case EOF_TOK:
			post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
					   "EOF in a COLUMN-WIDTH> attribute.\r", 
					   true, true);
			break;

		    default:
			throw new SystemErrorException(mName + 
				"Encountered unknown token type.");
                        /* commented out to keep the compiler happy */
			// break;
		}
	    }

	    /* check for EOF */
	    if ( ! this.abort_parse )
	    {
		if ( (this.l0_tok).code == EOF_TOK )
		{
		    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
				       "EOF in a COLUMN-WIDTH> attribute.\n", 
				       true, true);
		}
	    }

	    /* discard any excess values that may appear in the 
	     * COLUMN-WIDTH> a-list entry 
	     */
	    if ( ! this.abort_parse ) 
	    {
		if ( (this.l0_tok).code != R_PAREN_TOK )
		{
		    discard_excess_alist_entry_values();

		    if ( ! this.abort_parse )
		    {
			post_warning_message(EXCESS_VALUES_IN_ALIST_ENTRY_WARN,
				"Excess values encountered a " +
				"COLUMN-WIDTH> attribute.\n");
		    }
		}
	    }

	    /* read the terminating right parenthesis */
	    if ( ! this.abort_parse )
	    {
		if ( (this.l0_tok).code == R_PAREN_TOK )
		{
		    get_next_token();
		}
		else
		{
		    /* since we are cleaning up any excess values in the column
		     * width attribute, this else clause is unreachable at 
		     * present.  Should we choose to drop the above attempt at 
		     * error recovery, this clause will again become reachable.
		     */

		    post_warning_message(RIGHT_PAREN_EXPECTED_WARN, 
			    "Closing parenthesis missing from a " +
			    "COLUMN-WIDTH> attribute.\n");
		}
	    }
	}

	return(col_width);

    } /* macshapa_odb_reader::parse_s_var_col_width_attribute() */


    /*************************************************************************
     *
     * parse_s_var_dec()
     *
     * This method parses a spreadsheet variable declaration.  The productions 
     * generating a spreadsheet variable declaration are given below: 
     *        
     *    <s_var_dec> --> '(' <s_var_name> <s_var_dec_alist> ')'
     *
     *    <s_var_dec_alist> --> '(' <s_var_dec_attributes> ')'
     *
     *    <s_var_dec_attributes> --> { <s_var_type_attribute> 
     *                                 <s_var_variable_length_attribute> 
     *                                 <s_var_formal_arg_list_attribute> 
     *                                 [<s_var_col_width_attribute>] }
     *
     *    <s_var_type_attribute> --> '(' 'TYPE>' <s_var_type> ')'
     *
     *    <s_var_type> --> '<<TEXT>>' | '<<NOMINAL>>' | '<<INTEGER>>' | 
     *                     '<<FLOAT>>' | '<<PREDICATE>>' | '<<MATRIX>>'
     *
     *    <s_var_variable_length_attribute> --> 
     *                '(' 'VARIABLE-LENGTH>' <boolean> ')'
     *
     *    <s_var_formal_arg_list_attribute> --> 
     *                '(' 'FORMAL-ARG-LIST>' <s_var_formal_arg_list> ')'
     *
     *    <s_var_formal_arg_list> --> '(' '|<ord>|' '|<onset>|' 
     *                                    '|<offset>|' '|<val>|' ')' 
     *                                |
     *                                '(' '|<ord>|' '|<onset>|' '|<offset>|' 
     *                                    (<formal_arg>)+ ')'
     *
     *    <s_var_col_width_attribute> --> '(' 'COLUMN-WIDTH>' <integer> ')'
     *
     *                                             - 6/15/08
     *
     * Parameters:
     *
     *	  - None.
     *
     * Returns:  Void.
     *
     * Changes:
     *
     *      - None.
     *
     *******************************************************************************/

    private void parse_s_var_dec()
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = "macshapa_odb_reader::parse_s_var_dec()";
        String mveName = null;
	boolean done;
	boolean excess_values;

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse TRUE on entry");
	}
	
        /* parse the spreadsheet variable declaration */
	
        /* first parse the leading left parenthesis */
        if ( (this.l0_tok).code == L_PAREN_TOK )
        {
            get_next_token();
        }
        else /* we shouldn't have been called unless the next token is a '(' */
        {
            throw new SystemErrorException(mName + 
                    "(this.l0_tok).code != L_PAREN_TOK.");
        }

        /* parse the spreadsheet variable name */
        if ( ! this.abort_parse )
        {
            if ( (this.l0_tok).code == SYMBOL_TOK )
            {
                if ( ((this.l0_tok).aux & COLUMN_FLAG ) == 0 )
                {
                    post_warning_message(
                            NAME_IN_S_VAR_DEC_NOT_A_S_VAR_NAME_WARN, 
                            "Will coerce the name to a valid spreadsheet " +
                            "variable name.\n");

                    if ( ! this.abort_parse )
                    {
                        this.l0_tok.
                            coerce_symbol_token_to_spreadsheet_variable_name();
                    }
                }

                if ( ! this.abort_parse )
                {
                    get_next_token();
                }
            }
            else /* we shouldn't have been called unless the next token is a symbol */
            {
                throw new SystemErrorException(mName + 
                        "this.l0_tok != SYMBOL_TOK.");
            }
        }

        /* read the a-list associated with the spreadsheet 
         * variable declaration 
         */
        if ( ! this.abort_parse )
        {
            switch ( (this.l0_tok).code )
            {
                case L_PAREN_TOK:
                    /* note that if we get this far, the spreadsheet 
                     * variable name is already in the insertion buffer.
                     */
                    parse_s_var_dec_alist(mveName);
                    break;

                case R_PAREN_TOK:
                    post_warning_message(EMPTY_S_VAR_DEC_WARN, null);
                    break;

                case INT_TOK:
                case ERROR_TOK:
                case SYMBOL_TOK:
                case FLOAT_TOK:
                case STRING_TOK:
                case BOOL_TOK:
                case ALIST_LABEL_TOK:
                case PRIVATE_VAL_TOK:
                case SETF_TOK:
                case DB_VAR_TOK:
                case QUOTE_TOK:
                    post_warning_message(TYPE_MISMATCH_IN_S_VAR_DEC_WARN, 
                            "The spreadsheet variable declaration " +
                            "will be discarded.\n"); 

                    if ( ! this.abort_parse )
                    {
                        get_next_token();
                    }
                    break;

                case EOF_TOK:
                    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
                            "EOF in a spreadsheet variable declaration.\n", 
                            true, true);
                    break;

                default:
                    throw new SystemErrorException(mName + 
                            "Encountered unknown token type.");
	            /* commented out to keep the compiler happy */
                    // break;
            }
        }

        /* discard any excess values & consume the closing parenthesis */
        if ( ! this.abort_parse )
        {
            done = false;
            excess_values = false;

            while ( ( ! this.abort_parse ) && 
                    ( ! done ) )
            {
                switch ( (this.l0_tok).code )
                {
                    case R_PAREN_TOK:
                        done = true;
                        break;

                    case L_PAREN_TOK:
                        excess_values = true;
                        parse_arbitrary_list();
                        break;

                    case ERROR_TOK:
                    case SYMBOL_TOK:
                    case INT_TOK:
                    case FLOAT_TOK:
                    case STRING_TOK:
                    case BOOL_TOK:
                    case ALIST_LABEL_TOK:
                    case PRIVATE_VAL_TOK:
                    case SETF_TOK:
                    case DB_VAR_TOK:
                    case QUOTE_TOK:
                        excess_values = true;
                        get_next_token();
                        break;

                    case EOF_TOK:
                        done = true;
                        post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
                                "EOF encountered in a spreadsheet " +
                                "variable declaration.\n", true, true); 
                        break;

                    default:
                        throw new SystemErrorException(mName + 
                                "Encountered unknown token type.");
	                /* commented out to keep the compiler happy */
                        // break;
                }
            }

            if ( ( ! this.abort_parse ) &&
                 ( done ) && ( excess_values ) )
            {
                post_warning_message(EXCESS_VALUES_IN_A_S_VAR_DEC_WARN,
                                     null);
            }
        }

        /* finally, consume the closing parenthesis */
        if ( ! this.abort_parse )
        {
            if ( (this.l0_tok).code == R_PAREN_TOK )
            {
                get_next_token();
            }
            else 
            {
                throw new SystemErrorException(mName + 
                        "This else clause should be unreachable.");
            }
        }
	

	return;

    } /* macshapa_odb_reader::parse_s_var_dec() */


    /*************************************************************************
     *
     * parse_s_var_dec_alist()
     *
     * This method parses the a-list associated with a spreadsheet variable 
     * declaration, and uses the information obtained from it, along with the 
     * spreadsheet variable name passed to it, to insert the declaration of 
     * the spreadsheet variable in MacSHAPA's database.  The productions 
     * generating the spreadsheet variable declaration a-list are given below:
     *        
     *     <s_var_dec_alist> --> '(' <s_var_dec_attributes> ')'
     *
     *     <s_var_dec_attributes> --> { <s_var_type_attribute> 
     *                                  <s_var_variable_length_attribute> 
     *                                  <s_var_formal_arg_list_attribute> 
     *                                  [<s_var_col_width_attribute>] }
     *
     *     <s_var_type_attribute> --> '(' 'TYPE>' <s_var_type> ')'
     *
     *     <s_var_type> --> '<<TEXT>>' | '<<NOMINAL>>' | '<<INTEGER>>' | 
     *                      '<<FLOAT>>' | '<<PREDICATE>>' | '<<MATRIX>>'
     *
     *     <s_var_variable_length_attribute> --> 
     *                '(' 'VARIABLE-LENGTH>' <boolean> ')'
     *
     *     <s_var_formal_arg_list_attribute> --> 
     *                '(' 'FORMAL-ARG-LIST>' <s_var_formal_arg_list> ')'
     *
     *     <s_var_formal_arg_list> --> '(' '|<ord>|' '|<onset>|' 
     *                                     '|<offset>|' '|<val>|' ')' 
     *                                 |
     *                                 '(' '|<ord>|' '|<onset>|' '|<offset>|' 
     *                                     (<formal_arg>)+ ')'
     *
     *     <s_var_col_width_attribute> --> '(' 'COLUMN-WIDTH>' <integer> ')'
     *
     *                                              - 6/15/08
     *
     * Parameters:
     *
     *    - mveName: Name of the spreadsheet variable that is being declared.
     *
     * Returns:  Void.
     *
     * Changes:
     *
     *    - None.
     *
     *************************************************************************/

    private void parse_s_var_dec_alist(String mveName)
        throws SystemErrorException,
               java.io.IOException
    {
	final String mName = "macshapa_odb_reader::parse_s_var_dec_alist()";
	final String overflow_mssg = 
		"Overflow occured in the formal argument list of a " +
		"spreadsheet variable declaration.\n";
	boolean done;
        boolean fArgNameMisMatch;
	boolean have_args_list;
	boolean have_col_width;
	boolean have_type;
	boolean have_variable_length;
	boolean must_be_matrix;
        boolean set_system;
	int col_width;
        int i;
        long mve_id = DBIndex.INVALID_ID;
        long dc_id = DBIndex.INVALID_ID;
	boolean variable_length;
        FormalArgument fa;
	MatrixVocabElement mve;
        MatrixVocabElement.MatrixType mveType;
        Vector<String> args = null;
        DataColumn dc = null;

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse TRUE on entry");
	}

        /* parse the spreadsheet variable declaration a-list */
	
	/* first parse the leading left parenthesis */

	if ( (this.l0_tok).code == L_PAREN_TOK )
	{
	    if ( ( (this.l1_tok).code == L_PAREN_TOK ) || 
		 ( (this.l1_tok).code == R_PAREN_TOK ) )
	    {
		get_next_token();
	    }
	    else if ( (this.l1_tok).code == ALIST_LABEL_TOK )
	    {
		post_warning_message(LEFT_PAREN_EXPECTED_WARN, 
                        "The opening left parenthesis of a spreadsheet " +
                        "variable declaration a-list appears to be missing.\n");
	    }
	    else 
	    {
		/* if a left paren is missing, the first item in the a-list is 
                 * not an a-list entry.  If we try to recover from this error 
                 * here, we will only confuse things further.  Thus we eat the 
                 * left parenthesis & let the cards fall where they may.
                 */

		get_next_token();
	    }
	}
	else /* we shouldn't have been called unless the next token is a '(' */
	{
            throw new SystemErrorException(mName + 
                    "(this.l0_tok).code != L_PAREN_TOK.");
	}

	done                 = false;
	have_args_list       = false;
	have_col_width       = false;
	have_type            = false;
	have_variable_length = false;
	must_be_matrix       = false;
	variable_length      = false;
	mveType              = MatrixVocabElement.MatrixType.MATRIX;
	col_width            = DEFAULT_HEXTENT;

	/* now parse the a-list assocated with the spreadsheet variable declaration */
	while ( ( ! this.abort_parse ) && 
		( ! done ) )
	{
	    if ( (this.l0_tok).code == L_PAREN_TOK )
	    {
		if ( (this.l1_tok).code == ALIST_LABEL_TOK )
		{
		    switch ( (this.l1_tok).aux )
		    {
			case VARIABLE_LENGTH_LABEL:
			    if ( ! have_variable_length )
			    {
				have_variable_length = true;
				variable_length = 
					parse_variable_length_attribute();
			    }
			    else
			    {
				post_warning_message(DUPLICATE_ALIST_ENTRY_WARN, 
					"Duplicate VARIABLE-LENGTH> entry " +
					"in a spreadsheet variable " +
					"declaration.\n");

				if ( ! this.abort_parse )
				{
				     parse_unknown_alist_entry();
				}
			    }
			    break;

			case FORMAL_ARG_LIST_LABEL:
			    if ( ! have_args_list )
			    {
				have_args_list = true;
				args = parse_s_var_formal_arg_list_attribute();
                                
                                if ( ( args.size() > 4 ) ||
                                     ( args.get(3).compareTo("<val>") == 0 ) )
                                {
                                    must_be_matrix = true;
                                }
			    }
			    else
			    {
				post_warning_message(DUPLICATE_ALIST_ENTRY_WARN, 
					"Duplicate FORMAL-ARG-LIST> entry in " +
					"a spreadsheet variable declaration.\n");

				if ( ! this.abort_parse )
				{
				    parse_unknown_alist_entry();
				}
			    }
			    break;

			case TYPE_LABEL:
			    if ( ! have_type )
			    {
				have_type = true;
				mveType = parse_s_var_type_attribute();
			    }
			    else
			    {
				post_warning_message(DUPLICATE_ALIST_ENTRY_WARN, 
					"Duplicate TYPE> entry in a " + 
					"spreadsheet variable declaration.\n");

				if ( ! this.abort_parse )
				{
				     parse_unknown_alist_entry();
				}
			    }
			    break;

			case COLUMN_WIDTH_LABEL:
			    if ( ! have_col_width )
			    {
				have_col_width = true;
				col_width = parse_s_var_col_width_attribute();
			    }
			    else
			    {
				post_warning_message(DUPLICATE_ALIST_ENTRY_WARN, 
					"Duplicate COLUMN-WIDTH> entry in a " +
					"spreadsheet variable declaration.\n");

				if ( ! this.abort_parse )
				{
				     parse_unknown_alist_entry();
				}
			    }
			    break;

			default:
			    post_warning_message(
				    UNKNOWN_OR_UNEXPECTED_ALIST_ENTRY_WARN, 
				    "The entry is located in a spreadsheet " +
				    "variable declaration.\n");

			    if ( ! this.abort_parse )
			    {
				 parse_unknown_alist_entry();
			    }
			    break;
		    }
		}
		else /* a-list contains a list that is not an a-list entry. */
		     /* read it & discard it.                               */
		{
		    post_warning_message(NON_ALIST_ENTRY_LIST_IN_ALIST_WARN, 
			    "The list is located in a spreadsheet variable " +
			    "declaration a-list.\n");

		    if ( ! this.abort_parse )
		    {
			 parse_arbitrary_list();
		    }
		}
	    }
	    else if ( (this.l0_tok).code == R_PAREN_TOK )
	    {
		done = true;
		get_next_token();
	    }
	    else if ( (this.l0_tok).code == EOF_TOK )
	    {
		done = true;
		post_error_message(UNEXPECTED_END_OF_FILE_ERR,
			"EOF occurred in a spreadsheet variable " +
			"declaration a-list.\n", true, true);
	    }
	    else /* (this.l0_tok).code isn't '(', ')', or  EOF */
	    {
		post_warning_message(NON_ALIST_ENTRY_ATOM_IN_ALIST_WARN, 
			"The atom was detected in a spreadsheet variable " +
			"declaration a-list.\n");

		if ( ! this.abort_parse )
		{
		    get_next_token();
		}
	    }
	}

	/* check for missing required attributes -- generate default values 
	 * if necessary. 
	 */

	if ( ( ! this.abort_parse ) && 
	     ( ! have_variable_length ) )
	{
	    post_warning_message(MISSING_ALIST_ENTRY_WARN, 
		    "VARIABLE-LENGTH> entry missing from a spreadsheet " +
		    "variable declaration?  VARIABLE-LENGTH> forced to false.\n");
	}

	if ( ( ! this.abort_parse ) && 
	     ( ! have_args_list ) )
	{
	    post_warning_message(MISSING_ALIST_ENTRY_WARN, 
		    "FORMAL-ARG-LIST> entry missing from a spreadsheet " +
		    "variable declaration?  Will use the arg list \"(|<ord>| " +
		    "|<onset>| |<offset>| |<val>|)\".\n");

	    if ( ! this.abort_parse ) 
	    {
                args = new Vector<String>();
                args.add("<ord>");
                args.add("<onset>");
                args.add("<offset>");
                args.add("<val>");
	    }
	}

	if ( ( ! this.abort_parse ) && 
	     ( ! have_type ) )
	{
	    post_warning_message(MISSING_ALIST_ENTRY_WARN, 
		    "TYPE> entry missing from a spreadsheet variable " +
		    "declaration?  Will force type to <<MATRIX>>.\n");
	}

	/* check for disagreement between type and argument list */
	if ( ( ! this.abort_parse ) && 
	     ( must_be_matrix ) && 
             ( mveType != MatrixVocabElement.MatrixType.MATRIX ) )
	{
	    mveType = MatrixVocabElement.MatrixType.MATRIX;

	    post_warning_message(S_VAR_TYPE_ARG_LIST_MISMATCH_WARN, 
		    "Will force the type of the spreadsheet variable " +
		    "to <<MATRIX>>.\n");
	}

	/* check for disagreement between variable_length and type */
	if ( ( ! this.abort_parse ) && 
	     ( variable_length ) && 
             ( mveType != MatrixVocabElement.MatrixType.MATRIX ) )
	{
	    variable_length = false;

	    post_warning_message(VAR_LEN_NON_MATRIX_S_VAR_DEC_WARN, null);
	}

	/* attempt to declare the new spreadsheet variable in the database */
	if ( ! this.abort_parse )
	{
	    if ( this.debug_level >= 2 )
	    {
		dump_s_var_definition_to_listing(mveType, variable_length, 
                                                 col_width);
	    }
            
            if ( this.db.vl.predInVocabList(mveName) )
            {
                post_warning_message(S_VAR_PRED_NAME_COLLISION_WARN, 
                    "Spreadsheet variable declaration ignored.\n");
            }
            else if ( this.db.vl.matrixInVocabList(mveName) )
            {
                mve = this.db.getMatrixVE(mveName);
                
                if ( ( mve.getSystem() != false ) ||
                     ( mve.getVarLen() != variable_length ) ||
                     ( mve.getType() != mveType ) ||
                     ( mve.getNumFormalArgs() != args.size() - 3 ) )
                {
		    post_warning_message(S_VAR_REDEFINITION_WARN, 
			    "The duplicate declaration differed from the initial " +
			    "definition.\n");
                }
                else
                {
                    i = 0;
                    fArgNameMisMatch = false;
                    
                    while ( ( i + 3 < args.size() ) &&
                            ( ! fArgNameMisMatch ) )
                    {
                        if ( mve.getFormalArg(i).getFargName().
                                compareTo(args.get(i + 3)) != 0 )
                        {
                            fArgNameMisMatch = true;
                        }
                        i++;
                    }
                    
                    if ( fArgNameMisMatch )
                    {
                        post_warning_message(S_VAR_REDEFINITION_WARN, 
                                "The duplicate declaration differed from the " +
                                "initial definition in a formal argument " +
                                "name.\n");
                    }
                    else
                    {
                        post_warning_message(S_VAR_REDEFINITION_WARN, 
                                "The duplicate declaration was identical " +
                                "to the initial definition.\n");
                    }
                }
            }
            else if ( this.db.cl.dataColumnInColumnList(mveName) )
            {
                post_warning_message(S_VAR_REDEFINITION_WARN, 
                        "DataColumn name collision.\n");
            }
            else if ( this.db.cl.referenceColumnInColumnList(mveName) )
            {
                post_warning_message(S_VAR_REDEFINITION_WARN, 
                        "ReferenceColumn name collision.\n");
            }
            else
            {
                set_system = false;
                
                mve = new MatrixVocabElement(this.db, mveName);
                
                mve.setType(mveType);
                
                switch ( mveType )
                {
                    case FLOAT:
                        fa = new FloatFormalArg(this.db, "<val>");
                        mve.appendFormalArg(fa);
                        set_system = true;
                        break;
                        
                    case INTEGER:
                        fa = new IntFormalArg(this.db, "<val>");
                        mve.appendFormalArg(fa);
                        set_system = true;
                        break;
                        
                    case MATRIX:
                        for ( i = 3; i < args.size(); i++ )
                        {
                            fa = new UnTypedFormalArg(this.db, args.get(i));
                            mve.appendFormalArg(fa);
                        }
                        set_system = false;
                        break;
                        
                    case NOMINAL:
                        fa = new NominalFormalArg(this.db, "<val>");
                        mve.appendFormalArg(fa);
                        set_system = true;
                        break;
                        
                    case PREDICATE:
                        fa = new PredFormalArg(this.db, "<val>");
                        mve.appendFormalArg(fa);
                        set_system = true;
                        break;
                        
                    case TEXT:
                        // TextStringFormalArg doesn't allow a formal
                        // argument name at present -- formal argument
                        // name defaults to "<arg>".  This can be changed
                        // if it becomes a problem.
                        fa = new TextStringFormalArg(this.db);
                        mve.appendFormalArg(fa);
                        set_system = true;
                        break;
                        
                    case UNDEFINED:
                        throw new SystemErrorException(mName +
                                "matrix type undefined?!?");
                        /* commented out to keep the compiler happy */
                        // break;
                        
                    default:
                        throw new SystemErrorException(mName + 
                                "Unknown matrix type.");
                        /* commented out to keep the compiler happy */
                        // break;
                }
                
                mve.setVarLen(variable_length);
                
                if ( set_system ) 
                {
                    mve.setSystem();
                }
                
                /* Note that we are not making a copy of the mve
                 * before we insert it -- thus must be careful not
                 * to corrupt it.
                 */
                this.db.vl.addElement(mve);
        
                mve_id = mve.getID();
        
                if ( this.db.vl.getVocabElement(mve_id) != mve )
                {
                    throw new SystemErrorException(mName + 
                            "mve insertion in vl failed?");
                }
                
                dc = new DataColumn(this.db, mveName, false, false, mve_id);
        
                this.db.cl.addColumn(dc);
        
                dc_id = dc.getID();

                if ( ( this.db.cl.getColumn(dc_id) != dc ) ||
                     ( this.db.cl.getColumn(dc.getName()) != dc ) ||
                     ( dc.getItsCells() == null ) )
                {
                    throw new SystemErrorException(mName + 
                            "dc insertion in cl failed");
                }
        
                mve.setItsColID(dc_id);
            }
	}

	return;

    } /* macshapa_odb_reader::parse_s_var_dec_alist() */


    /*******************************************************************************
     *
     * parse_s_var_dec_list()
     *
     * This method parses a (posibly empty) list of spreadsheet variable
     * declarations.  Structurally, this list is simply a (possibly empty) list 
     * of lists.  The production generating it is given below:
     *
     *      <s_var_dec_list> --> '(' (<s_var_dec>)* ')'
     *
     * In reading this function, the following productions for <s_var_dec> are
     * also useful, as they are used in error detection & recovery.  Note
     * however that these productions are not parsed in this function, but 
     * rather in parse_s_var_dec() and its decendants.
     *        
     *     <s_var_dec> --> '(' <s_var_name> <s_var_dec_alist> ')'
     *
     *     <s_var_dec_alist> --> '(' <s_var_dec_attributes> ')'
     *
     *     <s_var_dec_attributes> --> { <s_var_type_attribute> 
     *                                  <s_var_variable_length_attribute> 
     *                                  <s_var_formal_arg_list_attribute> 
     *                                  [<s_var_col_width_attribute>] }
     *
     *     <s_var_type_attribute> --> '(' 'TYPE>' <s_var_type> ')'
     *
     *     <s_var_type> --> '<<TEXT>>' | '<<NOMINAL>>' | '<<INTEGER>>' | 
     *                      '<<FLOAT>>' | '<<PREDICATE>>' | '<<MATRIX>>'
     *
     *     <s_var_variable_length_attribute> --> 
     *                '(' 'VARIABLE-LENGTH>' <boolean> ')'
     *
     *     <s_var_formal_arg_list_attribute> --> 
     *                '(' 'FORMAL-ARG-LIST>' <s_var_formal_arg_list> ')'
     *
     *     <s_var_formal_arg_list> --> '(' '|<ord>|' '|<onset>|' 
     *                                     '|<offset>|' '|<val>|' ')' 
     *                                 |
     *                                 '(' '|<ord>|' '|<onset>|' '|<offset>|' 
     *                                     (<formal_arg>)+ ')'
     *
     *     <s_var_col_width_attribute> --> '(' 'COLUMN-WIDTH>' <integer> ')'
     *
     *                                              - 6/15/08
     *
     * Parameters:
     *
     *	  - None.
     *
     * Returns: Void.
     *
     * Changes:
     *
     *    - None.
     *
     *************************************************************************/

    private void parse_s_var_dec_list()
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = "macshapa_odb_reader::parse_s_var_dec_list()";
	boolean done;

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse TRUE on entry");
	}
	
	/* parse the spreadsheet variables declarations list */
	
	/* first parse the leading left parenthesis */

	if ( (this.l0_tok).code == L_PAREN_TOK )
	{
	    if ( ( (this.l1_tok).code == L_PAREN_TOK ) || 
		 ( (this.l1_tok).code == R_PAREN_TOK ) )
	    {
		get_next_token();
	    }
	    else if ( (this.l1_tok).code == SYMBOL_TOK )
	    {
		post_warning_message(LEFT_PAREN_EXPECTED_WARN, 
			"The opening left parenthesis of the spreadsheet " +
			"variables declaration list appears to be missing.\n");
	    }
	    else 
	    {
		/* If in fact the leading parenthesis in the spreadsheet 
		 * variables declarations list is missing, the first item 
		 * in the list is not a spreadsheet variable declaration.  
		 * Rather than confuse things further by inserting a 
		 * parenthesis, we eat the parenthesis here, and let the
		 * error come to light below.
		 */

		get_next_token();
	    }
	}
	else /* we shouldn't have been called unless the next token is a '(' */
	{
	    throw new SystemErrorException(mName + 
		    "(this.l0_tok).code != L_PAREN_TOK.");
	}

	/* parse the spreadsheet variables declarations list */
	done = false;

	while ( ( ! this.abort_parse ) && ( ! done ) )
	{
	    switch ( (this.l0_tok).code )
	    {
		case L_PAREN_TOK:
		    if ( (this.l1_tok).code == SYMBOL_TOK )
		    {
			parse_s_var_dec();
		    }
		    else /* not a predicate definition - discard it */
		    {
			post_warning_message(ALIEN_LIST_IN_S_VAR_DEC_LIST_WARN, 
					     null);

			if ( ! this.abort_parse )
			{
			    parse_arbitrary_list();
			}
		    }
		     break;

		case R_PAREN_TOK:
		    done = true;
		    get_next_token();
		    break;

		case EOF_TOK:
		    done = true;
		    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
			    "EOF encountered in the spreadsheet variables " +
			    "declarations list.\n", true, true);
		    break;

		case INT_TOK:
		case ERROR_TOK:
		case SYMBOL_TOK:
		case FLOAT_TOK:
		case STRING_TOK:
		case BOOL_TOK:
		case ALIST_LABEL_TOK:
		case PRIVATE_VAL_TOK:
		case SETF_TOK:
		case DB_VAR_TOK:
		case QUOTE_TOK:
		    post_warning_message(ALIEN_ATOM_IN_S_VAR_DEC_LIST_WARN, 
					 null);

		    if ( ! this.abort_parse )
		    {
			get_next_token();
		    }
		    break;

		 default:
		     throw new SystemErrorException(mName + 
			     "Encountered unknown token type.");
	             /* commented out to keep the compiler happy */
		     // break;
	     }
	}

	return;

    } /* macshapa_odb_reader::parse_s_var_dec_list() */


    /*************************************************************************
     *
     * parse_s_var_decs_attribute()
     *
     * This method parses the spread sheet variable declarations attribute 
     * in the USER> a-list.  Structurally, this attribute is simply an a-list 
     * entry with a list value.  The production generating it is given below.
     *
     *    <s_var_decs_attribute> --> '(' 'SPREADSHEET-VARIABLE-DECLARATIONS>'
     *                                      <s_var_dec_list> ')'
     *        
     *
     *                                                     - 6/16/08
     *
     * Parameters:
     *
     *	  - None.
     *
     * Returns:  Void.
     *
     * Changes:
     *
     *    - None.
     *
     *************************************************************************/

    private void parse_s_var_decs_attribute()
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = 
		"macshapa_odb_reader::parse_s_var_decs_attribute()";
	final String value_missing_mssg =
		"The USER> SPREADSHEET-VARIABLE-DECLARATIONS> a-list entry " +
		"doesn't seem to contain a value.\n";
	final String type_mismatch_mssg =
		"The value of the USER> SPREADSHEET-VARIABLE-DECLARATIONS> " +
		"a-list entry must be a (possibly empty) list of spreadsheet " +
		"variable declarations.\n"; 

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse TRUE on entry");
	}
	
	/* parse the spreadsheet variables declarations attribute */
	
	/* first parse the leading left parenthesis */

	if ( (this.l0_tok).code == L_PAREN_TOK )
	{
	    get_next_token();
	}
	else /* we shouldn't have been called unless the next token is a '(' */
	{
	    throw new SystemErrorException(mName + 
		    "(this.l0_tok).code != L_PAREN_TOK.");
	}

	/* read the a-list entry name */
	if ( ! this.abort_parse )
	{
	    if ( ( (this.l0_tok).code == ALIST_LABEL_TOK ) &&
		 ( (this.l0_tok).aux == SPREADSHEET_VARIABLE_DECLARATIONS_LABEL ) )
	    {
		get_next_token();
	    }
	    else /* system error - we shouldn't have been called unless the next */
	    {    /* token is the SPREADSHEET-VARIABLE-DECLARATIONS> a-list tag.  */

		throw new SystemErrorException(mName + 
			"this.l0_tok != \"SPREADSHEET-VARIABLE-DECLARATIONS>\".");
	    }
	}

	if ( ! this.abort_parse )
	{
	    switch ( (this.l0_tok).code )
	    {
		case L_PAREN_TOK:
		    parse_s_var_dec_list();
		    break;

		case R_PAREN_TOK:
		    post_warning_message(EMPTY_ALIST_ENTRY_WARN, 
					       value_missing_mssg); 
		    break;

		case INT_TOK:
		case ERROR_TOK:
		case SYMBOL_TOK:
		case FLOAT_TOK:
		case STRING_TOK:
		case BOOL_TOK:
		case ALIST_LABEL_TOK:
		case PRIVATE_VAL_TOK:
		case SETF_TOK:
		case DB_VAR_TOK:
		case QUOTE_TOK:
		    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
					 type_mismatch_mssg); 

		    if ( ! this.abort_parse )
		    {
			get_next_token();
		    }
		    break;

		case EOF_TOK:
		    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 	 
			    "EOF in USER> SPREADSHEET-VARIABLE-DECLARATIONS> " +
			    "attribute.\n", true, true); 
		    break;

		 default:
		     throw new SystemErrorException(mName + 
			     "Encountered unknown token type.");
	             /* commented out to keep the compiler happy */
		     // break;
	     }
	 }

	 /* check for EOF */
	 if ( ! this.abort_parse )
	 {
	     if ( (this.l0_tok).code == EOF_TOK )
	     {
		post_error_message(UNEXPECTED_END_OF_FILE_ERR, 		 
			"EOF in USER> SPREADSHEET-VARIABLE-DECLARATIONS> " +
			"attribute.\n", true, true); 
	     }
	 }

	/* discard any excess values that may appear in the USER>
	 * SPREADSHEET-VARIABLE-DECLARATIONS> a-list entry 
	 */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code != R_PAREN_TOK )
	    {
		discard_excess_alist_entry_values();

		if ( ! this.abort_parse )
		{
		    post_warning_message(EXCESS_VALUES_IN_ALIST_ENTRY_WARN,
			    "Excess values encountered in the USER> " +
			    "SPREADSHEET-VARIABLE-DECLARATIONS> attribute.\n");
		}
	    }
	}

	/* finally, consume the closing parenthesis */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code == R_PAREN_TOK )
	    {
		get_next_token();
	    }
	    else 
	    {
		/* Since we are cleaning up any excess values in the 
		 * spreadsheet variable declarations attribute, this else 
		 * clause is unreachable at present.  Should we choose to 
		 * drop the above attempt at error recovery, this clause 
		 * will again become reachable.
		 */

		post_warning_message(RIGHT_PAREN_EXPECTED_WARN, 
			"The closing parenthesis was missing from the USER> " +
			"SPREADSHEET-VARIABLE-DECLARATIONS> a-list entry.\n");
	    }
	}
	
	return;

    } /* macshapa_odb_reader::parse_s_var_decs_attribute() */


    /*************************************************************************
     *
     * parse_s_var_def()
     *
     * This method parses a spreadsheet variable definition.  The productions 
     * generating a spreadsheet variable definition are given below: 
     *        
     *     <s_var_def> --> '(' <s_var_name> <s_var_def_alist> ')'
     *        
     *     <s_var_def_alist> --> '(' <s_var_def_attributes> ')'
     *        
     *     <s_var_def_attributes> --> { <s_var_def_cells_attribute>
     *                                  [<s_var_def_vocab_attribute>] }
     *        
     *     <s_var_def_cells_attribute> --> '(' 'CELLS>' <s_var_cell_list> ')'
     *        
     *     <s_var_cell_list> --> '(' (<s_var_cell>)* ')'
     *        
     *     <s_var_cell> --> '(' { <s_var_cell_onset_attribute>
     *                            <s_var_cell_offset_attribute>
     *                            (<s_var_cell_value_attribute>)+ } ')'
     *        
     *     <s_var_cell_onset_attribute> --> '(' 'ONSET>' <integer> ')'
     *        
     *     <s_var_cell_offset_attribute> --> '(' 'OFFSET>' <integer> ')'
     *        
     *     <s_var_cell_value_attribute> --> '(' <formal_arg> <cell_value> ')'
     *        
     *     <cell_value> --> 
     *                <text_cell_value>    (* if <s_var_type> == <<TEXT>> *)
     *                |
     *                <nominal_cell_value> (* if <s_var_type> == <<NOMINAL>> *)
     *                |
     *                <integer_cell_value> (* if <s_var_type> == <<INTEGER>> *)
     *                |
     *                <float_cell_value>   (* if <s_var_type> == <<FLOAT>> *)
     *                |
     *                <pred_cell_value>    (* if <s_var_type> == <<PREDICATE>> *)
     *                |
     *                <matrix_cell_value>  (* if <s_var_type> == <<MATRIX>> *)
     *        
     *     <text_cell_value> --> <text_quote_string>
     *        
     *     <nominal_cell_value> --> <nominal> 
     *                              |
     *                              '|<val>|'  (* if the nominal is undefined *)
     *        
     *     <integer_cell_value> --> <integer>
     *        
     *     <float_cell_value --> <float>
     *        
     *     <pred_cell_value> --> '(' ')' | <pred_value>
     *        
     *     <pred_value> --> '(' <pred_name> (<pred_arg>)+ ')'
     *        
     *     <pred_arg> --> <quote_string> | <nominal> | <pred_value> | 
     *                    <integer> | <float> | <time_stamp> | <formal_arg>
     *        
     *     <time_stamp> --> '(' 'TIME>' <integer> ')' 
     *        
     *     <matrix_cell_value> --> <pred_arg>
     *        
     *     <s_var_def_vocab_attribute> --> '(' VOCAB> <vocab_list> ')'
     *        
     *     <vocab_list> --> '(' (<vocab_entry>)* ')'
     *        
     *     <vocab_entry> --> <pred_name>
     *
     *                                              - 6/15/08
     *
     * Parameters:
     *
     *	  - None.
     *
     * Returns:  Void.
     *
     * Changes:
     *
     *    - None.
     *
     *************************************************************************/

    private void parse_s_var_def()
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = "macshapa_odb_reader::parse_s_var_def()";
	boolean done;
	boolean excess_values;
        DataColumn s_var_col = null;
        long s_var_col_ID = DBIndex.INVALID_ID;
        long s_var_mve_ID = DBIndex.INVALID_ID;
        MatrixVocabElement.MatrixType s_var_type =
                MatrixVocabElement.MatrixType.UNDEFINED;

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse true on entry");
	}
	
	/* parse the spreadsheet variable definition */

	/* first parse the leading left parenthesis */
	if ( (this.l0_tok).code == L_PAREN_TOK )
	{
	    get_next_token();
	}
	else /* we shouldn't have been called unless the next token is a '(' */
	{
	    throw new SystemErrorException(mName + 
		    "(this.l0_tok).code != L_PAREN_TOK.");
	}

	/* parse the spreadsheet variable name */ 
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code == SYMBOL_TOK )
	    {
		if ( ((this.l0_tok).aux & COLUMN_FLAG ) == 0 )
		{
		    post_warning_message(
			    NAME_IN_S_VAR_DEF_NOT_A_S_VAR_NAME_WARN, 
			    "Will coerce the name to a valid spreadsheet " +
			    "variable name.\n");

		    if ( ! this.abort_parse )
		    {
			this.l0_tok.coerce_symbol_token_to_spreadsheet_variable_name();
		    }
		}

                /* Test to see if the target spreadsheet variable has been 
                 * declared, and load its ID into s_var_ID if it has.
                 */
                if ( this.db.colNameInUse(this.l0_tok.str.toString()) )
                {
                    /* the target spreadsheet variable exists -- get a copy of 
                     * it and read off the needed information.
                     */
                    s_var_col = this.db.getDataColumn(this.l0_tok.str.toString());
                    
                    /* the target data column exists -- look it up and get its
                     * ID
                     */
                    s_var_col_ID = s_var_col.getID();
                    
                    if ( s_var_col_ID == DBIndex.INVALID_ID )
                    {
                        throw new SystemErrorException(mName + 
                                "s_var_col_ID invalid?!?");
                    }

                    s_var_type = s_var_col.getItsMveType();
                    
                    if ( s_var_type == MatrixVocabElement.MatrixType.UNDEFINED )
                    {
                        throw new SystemErrorException(mName + 
                                "s_var_type undefined?!?");
                    }
                    
                    s_var_mve_ID = s_var_col.getItsMveID();
                    
                    if ( s_var_mve_ID == DBIndex.INVALID_ID )
                    {
                        throw new SystemErrorException(mName + 
                                "s_var_mve_ID invalid?!?");
                    }
                }

		if ( ! this.abort_parse )
		{
		    get_next_token();
		}
	    }
	    else /* we shouldn't have been called unless the next token is a symbol */
	    {
		throw new SystemErrorException(mName + 
			"this.l0_tok != SYMBOL_TOK.");
	    }
	}

	/* read the a-list associated with the spreadsheet variable declaration */
	if ( ! this.abort_parse )
	{
	     switch ( (this.l0_tok).code )
	     {
		case L_PAREN_TOK:
		    if ( s_var_col_ID != DBIndex.INVALID_ID )
		    {
			parse_s_var_def_alist(s_var_col_ID, 
                                s_var_type, s_var_mve_ID);
		    }
		    else /* undeclared spreadsheet variable error */
		    {
			post_warning_message(UNDECLARED_S_VAR_WARN, 
				"The definition of the undeclared " + 
				"spreadsheet variable will be discarded.\n");

			if ( ! this.abort_parse )
			{
			    parse_arbitrary_list();
			}
		    }
		    break;

		case R_PAREN_TOK:
		    post_warning_message(EMPTY_S_VAR_DEF_WARN, null);
		    break;

		case INT_TOK:
		case ERROR_TOK:
		case SYMBOL_TOK:
		case FLOAT_TOK:
		case STRING_TOK:
		case BOOL_TOK:
		case ALIST_LABEL_TOK:
		case PRIVATE_VAL_TOK:
		case SETF_TOK:
		case DB_VAR_TOK:
		case QUOTE_TOK:
		    post_warning_message(TYPE_MISMATCH_IN_S_VAR_DEF_WARN, 
			    "The spreadsheet variable definition will " +
			    "be discarded.\n"); 

		    if ( ! this.abort_parse )
		    {
			get_next_token();
		    }
		    break;


		case EOF_TOK:
		    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 		     
			    "EOF in a spreadsheet variable definition.\n",
			    true, true);
		    break;

		 default:
		     throw new SystemErrorException(mName + 
			     "Encountered unknown token type.");
	             /* commented out to keep the compiler happy */
		     // break;
	     }
	}

	/* discard any excess values & consume the closing parenthesis */
	if ( ! this.abort_parse )
	{
	    done = false;
	    excess_values = false;

	    while ( ( ! this.abort_parse ) && 
		    ( ! done ) )
	    {
		 switch ( (this.l0_tok).code )
		 {
		    case R_PAREN_TOK:
			done = true;
			break;

		    case L_PAREN_TOK:
			excess_values = true;
			parse_arbitrary_list();
			break;

		    case ERROR_TOK:
		    case SYMBOL_TOK:
		    case INT_TOK:
		    case FLOAT_TOK:
		    case STRING_TOK:
		    case BOOL_TOK:
		    case ALIST_LABEL_TOK:
		    case PRIVATE_VAL_TOK:
		    case SETF_TOK:
		    case DB_VAR_TOK:
		    case QUOTE_TOK:
			excess_values = true;
			get_next_token();
			break;

		    case EOF_TOK:
			done = true;
			post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
				"EOF encountered in a spreadsheet variable " +
				"definition.\n", true, true); 
			break;

		    default:
			throw new SystemErrorException(mName + 
				"Encountered unknown token type.");
                        /* commented out to keep the compiler happy */
			// break;
		 }
	    }

	    if ( ( ! this.abort_parse ) &&
		 ( done ) && ( excess_values ) )
	    {
		post_warning_message(EXCESS_VALUES_IN_A_S_VAR_DEC_WARN, null);
	    }
	}

	/* finally, consume the closing parenthesis */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code == R_PAREN_TOK )
	    {
		get_next_token();
	    }
	    else 
	    {
		throw new SystemErrorException(mName + 
			"This else clause should be unreachable.");
	    }
	}

	return;

    } /* macshapa_odb_reader::parse_s_var_def() */


    /*************************************************************************
     *
     * parse_s_var_def_alist()
     *
     * This method parses the a-list associated with a spreadsheet variable 
     * definition.  The productions generating the spreadsheet variable 
     * definition a-list are given below:
     *        
     *     <s_var_def_alist> --> '(' <s_var_def_attributes> ')'
     *        
     *     <s_var_def_attributes> --> { <s_var_def_cells_attribute>
     *                                  [<s_var_def_vocab_attribute>] }
     *        
     *     <s_var_def_cells_attribute> --> '(' 'CELLS>' <s_var_cell_list> ')'
     *        
     *     <s_var_cell_list> --> '(' (<s_var_cell>)* ')'
     *        
     *     <s_var_cell> --> '(' { <s_var_cell_onset_attribute>
     *                            <s_var_cell_offset_attribute>
     *                            (<s_var_cell_value_attribute>)+ } ')'
     *        
     *     <s_var_cell_onset_attribute> --> '(' 'ONSET>' <integer> ')'
     *        
     *     <s_var_cell_offset_attribute> --> '(' 'OFFSET>' <integer> ')'
     *        
     *     <s_var_cell_value_attribute> --> '(' <formal_arg> <cell_value> ')'
     *        
     *     <cell_value> --> 
     *                <text_cell_value>    (* if <s_var_type> == <<TEXT>> *)
     *                |
     *                <nominal_cell_value> (* if <s_var_type> == <<NOMINAL>> *)
     *                |
     *                <integer_cell_value> (* if <s_var_type> == <<INTEGER>> *)
     *                |
     *                <float_cell_value>   (* if <s_var_type> == <<FLOAT>> *)
     *                |
     *                <pred_cell_value>    (* if <s_var_type> == <<PREDICATE>> *)
     *                |
     *                <matrix_cell_value>  (* if <s_var_type> == <<MATRIX>> *)
     *        
     *     <text_cell_value> --> <text_quote_string>
     *        
     *     <nominal_cell_value> --> <nominal> 
     *                              |
     *                              '|<val>|'  (* if the nominal is undefined *)
     *        
     *     <integer_cell_value> --> <integer>
     *        
     *     <float_cell_value --> <float>
     *        
     *     <pred_cell_value> --> '(' ')' | <pred_value>
     *        
     *     <pred_value> --> '(' <pred_name> (<pred_arg>)+ ')'
     *        
     *     <pred_arg> --> <quote_string> | <nominal> | <pred_value> | 
     *                    <integer> | <float> | <time_stamp> | <formal_arg>
     *        
     *     <time_stamp> --> '(' 'TIME>' <integer> ')' 
     *        
     *     <matrix_cell_value> --> <pred_arg>
     *        
     *     <s_var_def_vocab_attribute> --> '(' VOCAB> <vocab_list> ')'
     *        
     *     <vocab_list> --> '(' (<vocab_entry>)* ')'
     *        
     *     <vocab_entry> --> <pred_name>
     *
     * Note that when parse_s_var_def_alist() is called, we are 
     * guaranteed that the spreadsheet variable has been declared, and that
     * s_var_ptr parameter pointes to the MacSHAPA Variable data structure
     * associated with the spreadsheet variable.
     *
     *                                              - 6/18/08
     *
     * Parameters:
     *
     *	  - s_var_col_ID:  ID assigned by the database to the DataColumn into
     *          which we will be loading the spreadsheet variable definition.
     *          This ID is used when inserting cells into the DataColumn.
     *
     *    - s_var_type: Instance of matrixType indicating the type of the 
     *          spreadsheet variable.  This value is used primarily for 
     *          sanity checking.
     *
     *    - s_var_mve_ID: ID assigned to the MatrixVocabElement describing
     *          the structure of the DataColumn into which the spreadsheet
     *          variable is being loaded.  
     *
     * Returns:  Void.
     *
     * Changes:
     *
     *    - None.
     *
     **************************************************************************/

    private void parse_s_var_def_alist(long s_var_col_ID,
                                       MatrixVocabElement.MatrixType s_var_type,
                                       long s_var_mve_ID)
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = "macshapa_odb_reader::parse_s_var_def_alist()";
	boolean done;
	boolean have_cells;
	boolean have_vocab;

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse TRUE on entry");
	}
	else if ( s_var_col_ID == DBIndex.INVALID_ID )
	{
	    throw new SystemErrorException(mName + 
		    "s_var_col_ID is invalid on entry.");
	}
	else if ( s_var_mve_ID == DBIndex.INVALID_ID )
	{
	    throw new SystemErrorException(mName + 
		    "s_var_mve_ID is invalid on entry.");
	}
	
	/* parse the spreadsheet variable definition a-list */
	
	/* first parse the leading left parenthesis */

	if ( (this.l0_tok).code == L_PAREN_TOK )
	{
	    if ( ( (this.l1_tok).code == L_PAREN_TOK ) || 
		 ( (this.l1_tok).code == R_PAREN_TOK ) )
	    {
		get_next_token();
	    }
	    else if ( (this.l1_tok).code == ALIST_LABEL_TOK )
	    {
		post_warning_message(LEFT_PAREN_EXPECTED_WARN, 
			"The opening left parenthesis of a spreadsheet " +
			"variable definition a-list appears to be missing.\n");
	    }
	    else 
	    {
		/* if a left paren is missing, the first item in the a-list 
		 * is not an a-list entry.  If we try to recover from this 
		 * error here, we will only confuse things further.  Thus we 
		 * eat the left parenthesis & let the cards fall where they 
		 * may.
		 */

		get_next_token();
	    }
	}
	else /* we shouldn't have been called unless the next token is a '(' */
	{
	    throw new SystemErrorException(mName + 
		    "(this.l0_tok).code != L_PAREN_TOK.");
	}

	done       = false;
	have_cells = false;
	have_vocab = false;

	/* now parse the a-list assocated with the spreadsheet variable 
	 * definition 
	 */
	 while ( ( ! this.abort_parse ) && 
		 ( ! done ) )
	{
	     if ( (this.l0_tok).code == L_PAREN_TOK )
	     {
		 if ( (this.l1_tok).code == ALIST_LABEL_TOK )
		 {
		     switch ( (this.l1_tok).aux )
		     {
			case CELLS_LABEL:
			    if ( ! have_cells )
			    {
				have_cells = true;
				parse_s_var_def_cells_attribute(s_var_col_ID, 
                                                                s_var_type,
                                                                s_var_mve_ID);
			    }
			    else
			    {
				post_warning_message(DUPLICATE_ALIST_ENTRY_WARN, 
					"Duplicate CELLS> entry in a " +
					"spreadsheet variable definition.\n");

				if ( ! this.abort_parse )
				{
				     parse_unknown_alist_entry();
				}
			    }
			    break;

			case VOCAB_LABEL:
			    if ( ! have_vocab )
			    {
				have_vocab = true;

				if ( ( s_var_type ==
                                       MatrixVocabElement.MatrixType.MATRIX ) ||
                                     ( s_var_type ==
                                       MatrixVocabElement.MatrixType.PREDICATE ) )
				{
				    parse_s_var_def_vocab_attribute(s_var_col_ID);
				}
				else
				{
				    post_warning_message(
					    INAPROPRIATE_VOCAB_ATTRIBUTE_WARN, 
					    "The VOCAB> attribute will be " +
					    "discarded.\n");

				    if ( ! this.abort_parse )
				    {
					parse_unknown_alist_entry();
				    }
				}
			    }
			    else
			    {
				post_warning_message(DUPLICATE_ALIST_ENTRY_WARN, 
					"Duplicate VOCAB> entry in a " +
					"spreadsheet variable definition.\n");

				if ( ! this.abort_parse )
				{
				     parse_unknown_alist_entry();
				}
			    }
			    break;

			default:
			    post_warning_message(
				    UNKNOWN_OR_UNEXPECTED_ALIST_ENTRY_WARN, 
				    "The entry is located in a spreadsheet " +
				    "variable definition.\n");

			    if ( ! this.abort_parse )
			    {
				 parse_unknown_alist_entry();
			    }
			    break;
		    }
		}
		else /* a-list contains a list that is not an a-list entry. */
		     /* read it & discard it.                               */
		{
		    post_warning_message(NON_ALIST_ENTRY_LIST_IN_ALIST_WARN, 
			    "The list is located in a spreadsheet variable " +
			    "definition a-list.\n");

		    if ( ! this.abort_parse )
		    {
			 parse_arbitrary_list();
		    }
		}
	    }
	    else if ( (this.l0_tok).code == R_PAREN_TOK )
	    {
		done = true;
		get_next_token();
	    }
	    else if ( (this.l0_tok).code == EOF_TOK )
	    {
		done = true;
		post_error_message(UNEXPECTED_END_OF_FILE_ERR,
			"EOF occurred in a spreadsheet variable definition " +
			"a-list.\n", true, true);
	    }
	    else /* (this.l0_tok).code isn't '(', ')', or EOF */
	    {
		post_warning_message(NON_ALIST_ENTRY_ATOM_IN_ALIST_WARN, 
			"The atom was detected in a spreadsheet variable " +
			"definition a-list.\n");

		if ( ! this.abort_parse )
		{
		    get_next_token();
		}
	    }
	}

	/* check for missing required attributes. */

	if ( ( ! this.abort_parse ) && 
	     ( ! have_cells ) )
	{
	    post_warning_message(MISSING_ALIST_ENTRY_WARN, "CELLS> entry " +
		    "missing from a spreadsheet variable definition?\n");
	}

	if ( ( ! this.abort_parse ) && ( ! have_vocab ) && 
             ( ( s_var_type == MatrixVocabElement.MatrixType.MATRIX ) ||
               ( s_var_type == MatrixVocabElement.MatrixType.PREDICATE ) ) )
	{
	    post_warning_message(MISSING_ALIST_ENTRY_WARN, 
		    "VOCAB> entry missing from a spreadsheet predicate or " +
		    "matrix variable definition?\n");
	}

	return;

    } /* macshapa_odb_reader::parse_s_var_def_alist() */


    /**************************************************************************
     *
     * parse_s_var_def_cells_attribute()
     *
     * This method parses a CELLS> attribute in the context of a
     * spreadsheet variable definition.  This attribute is generated by the 
     * following productions:
     *        
     *     <s_var_def_cells_attribute> --> '(' 'CELLS>' <s_var_cell_list> ')'
     *        
     *     <s_var_cell_list> --> '(' (<s_var_cell>)* ')'
     *        
     *     <s_var_cell> --> '(' { <s_var_cell_onset_attribute>
     *                            <s_var_cell_offset_attribute>
     *                            (<s_var_cell_value_attribute>)+ } ')'
     *        
     *     <s_var_cell_onset_attribute> --> '(' 'ONSET>' <integer> ')'
     *        
     *     <s_var_cell_offset_attribute> --> '(' 'OFFSET>' <integer> ')'
     *        
     *     <s_var_cell_value_attribute> --> '(' <formal_arg> <cell_value> ')'
     *        
     *     <cell_value> --> 
     *                <text_cell_value>    (* if <s_var_type> == <<TEXT>> *)
     *                |
     *                <nominal_cell_value> (* if <s_var_type> == <<NOMINAL>> *)
     *                |
     *                <integer_cell_value> (* if <s_var_type> == <<INTEGER>> *)
     *                |
     *                <float_cell_value>   (* if <s_var_type> == <<FLOAT>> *)
     *                |
     *                <pred_cell_value>    (* if <s_var_type> == <<PREDICATE>> *)
     *                |
     *                <matrix_cell_value>  (* if <s_var_type> == <<MATRIX>> *)
     *        
     *     <text_cell_value> --> <text_quote_string>
     *        
     *     <nominal_cell_value> --> <nominal> 
     *                              |
     *                              '|<val>|'  (* if the nominal is undefined *)
     *        
     *     <integer_cell_value> --> <integer>
     *        
     *     <float_cell_value --> <float>
     *        
     *     <pred_cell_value> --> '(' ')' | <pred_value>
     *        
     *     <pred_value> --> '(' <pred_name> (<pred_arg>)+ ')'
     *        
     *     <pred_arg> --> <quote_string> | <nominal> | <pred_value> | 
     *                    <integer> | <float> | <time_stamp> | <formal_arg>
     *        
     *     <time_stamp> --> '(' 'TIME>' <integer> ')' 
     *        
     *     <matrix_cell_value> --> <pred_arg>
     *
     *                                              - 6/18/08
     *
     * Parameters:
     *
     *	  - s_var_col_ID:  ID assigned by the database to the DataColumn into
     *          which we will be loading the spreadsheet variable definition.
     *          This ID is used when inserting cells into the DataColumn.
     *
     *    - s_var_type: Instance of matrixType indicating the type of the 
     *          spreadsheet variable.  This value is used primarily for 
     *          sanity checking.
     *
     *    - s_var_mve_ID: ID assigned to the MatrixVocabElement describing
     *          the structure of the DataColumn into which the spreadsheet
     *          variable is being loaded.  
     *
     * Returns:  void.
     *
     * Changes:
     *
     *    - None.
     *
     **************************************************************************/

    private void parse_s_var_def_cells_attribute(long s_var_col_ID,
                                       MatrixVocabElement.MatrixType s_var_type,
                                       long s_var_mve_ID)
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = 
		"macshapa_odb_reader::parse_s_var_def_cells_attribute()";
	final String missing_cells_list_mssg = 
		"A CELLS> attribute appears not to contain a value.  " +
		"Attribute ignored.\n";
	final String cells_list_type_mismatch_mssg = 
		"The value of a CELLS> attribute must be a list of " +
		"spreadsheet variable cells.  Attribute ignored.\n";

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse TRUE on entry");
	}
	
	if ( s_var_col_ID == DBIndex.INVALID_ID )
	{
	    throw new SystemErrorException(mName + 
		    "s_var_col_ID == invalid on entry.");
	}
	
	if ( s_var_mve_ID == DBIndex.INVALID_ID )
	{
	    throw new SystemErrorException(mName + 
		    "s_var_mve_ID == invalid on entry.");
	}
	
	/* parse the formal argument list attribute */
	
	/* first parse the leading left parenthesis */

	if ( (this.l0_tok).code == L_PAREN_TOK )
	{
	    get_next_token();
	}
	else /* we shouldn't have been called unless the next token is a '(' */
	{
	    throw new SystemErrorException(mName + 
		    "(this.l0_tok).code != L_PAREN_TOK.");
	}

	/* read the a-list entry name */
	if ( ! this.abort_parse )
	{
	    if ( ( (this.l0_tok).code == ALIST_LABEL_TOK ) && 
		 ( (this.l0_tok).aux == CELLS_LABEL ) )
	    {
		get_next_token();
	    }
	    else /* we shouldn't have been called unless the next token CELLS> */
	    {
                throw new SystemErrorException(mName + "this.l0_tok != CELLS>.");
	    }
	}

	/* read the value associated with the a-list entry & discard any 
	 * excess values 
	 */
	if ( ! this.abort_parse )
	{
	     switch ( (this.l0_tok).code )
	     {
		case L_PAREN_TOK:
		    parse_s_var_cell_list(s_var_col_ID, s_var_type, s_var_mve_ID);
		    break;

		case R_PAREN_TOK:
		    post_warning_message(EMPTY_ALIST_ENTRY_WARN, 
					 missing_cells_list_mssg);
		    break;

		case BOOL_TOK:
		case ERROR_TOK:
		case SYMBOL_TOK:
		case FLOAT_TOK:
		case STRING_TOK:
		case INT_TOK:
		case ALIST_LABEL_TOK:
		case PRIVATE_VAL_TOK:
		case SETF_TOK:
		case DB_VAR_TOK:
		case QUOTE_TOK:
		    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
					 cells_list_type_mismatch_mssg); 

		    if ( ! this.abort_parse )
		    {
			get_next_token();
		    }
		    break;


		case EOF_TOK:
		    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
				       "EOF in a CELLS> attribute.\n", 
				       true, true);
		    break;

		default:
		    throw new SystemErrorException(mName + 
			    "Encountered unknown token type.");
	            /* commented out to keep the compiler happy */
		    // break;
	    }
	}

	/* check for EOF */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code == EOF_TOK )
	    {
		post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
				   "EOF in a CELLS> attribute.\n", 
				   true, true);
	    }
	}

	/* discard any excess values that may appear in the CELLS> a-list entry */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code != R_PAREN_TOK )
	    {
		discard_excess_alist_entry_values();

		if ( ! this.abort_parse )
		{
		    post_warning_message(EXCESS_VALUES_IN_ALIST_ENTRY_WARN,
			    "Excess values encountered a CELLS> attribute.\n");
		}
	    }
	}

	/* read the terminating right parenthesis */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code == R_PAREN_TOK )
	    {
		get_next_token();
	    }
	    else
	    {
		/* since we are cleaning up any excess values in the cells list
		 * attribute, this else clause is unreachable at present.
		 * Should we choose to drop the above attempt at error recovery,
		 * this clause will again become reachable.
		 */

		post_warning_message(RIGHT_PAREN_EXPECTED_WARN, "Closing " +
			"parenthesis missing from a CELLS> attribute.\n");
	    }
	}

	return;

    } /* macshapa_odb_reader::parse_s_var_def_cells_attribute() */


    /*************************************************************************
     *
     * parse_s_var_def_list()
     *
     * This method parses a (possibly empty) list of spreadsheet variable
     * definitions.  Structurally, this list is simply a (possibly empty) list 
     * of lists.  The production generating it is given below:
     *
     *     <s_var_def_list> --> '(' (<s_var_def)* ')'
     *
     * In reading this function, the following productions for <s_var_def> are
     * also useful, as they are used in error detection & recovery.  Note
     * however that these productions are not parsed in this function, but 
     * rather in parse_s_var_def() and its decendants.
     *        
     *     <s_var_def> --> '(' <s_var_name> <s_var_def_alist> ')'
     *        
     *     <s_var_def_alist> --> '(' <s_var_def_attributes> ')'
     *        
     *     <s_var_def_attributes> --> { <s_var_def_cells_attribute>
     *                                 [<s_var_def_vocab_attribute>] }
     *        
     *     <s_var_def_cells_attribute> --> '(' 'CELLS>' <s_var_cell_list> ')'
     *        
     *     <s_var_cell_list> --> '(' (<s_var_cell>)* ')'
     *        
     *     <s_var_cell> --> '(' { <s_var_cell_onset_attribute>
     *                            <s_var_cell_offset_attribute>
     *                            (<s_var_cell_value_attribute>)+ } ')'
     *        
     *     <s_var_cell_onset_attribute> --> '(' 'ONSET>' <integer> ')'
     *        
     *     <s_var_cell_offset_attribute> --> '(' 'OFFSET>' <integer> ')'
     *        
     *     <s_var_cell_value_attribute> --> '(' <formal_arg> <cell_value> ')'
     *        
     *     <cell_value> --> 
     *                <text_cell_value>    (* if <s_var_type> == <<TEXT>> *)
     *                |
     *                <nominal_cell_value> (* if <s_var_type> == <<NOMINAL>> *)
     *                |
     *                <integer_cell_value> (* if <s_var_type> == <<INTEGER>> *)
     *                |
     *                <float_cell_value>   (* if <s_var_type> == <<FLOAT>> *)
     *                |
     *                <pred_cell_value>    (* if <s_var_type> == <<PREDICATE>> *)
     *                |
     *                <matrix_cell_value>  (* if <s_var_type> == <<MATRIX>> *)
     *        
     *     <text_cell_value> --> <text_quote_string>
     *        
     *     <nominal_cell_value> --> <nominal> 
     *                              |
     *                              '|<val>|'  (* if the nominal is undefined *)
     *        
     *     <integer_cell_value> --> <integer>
     *        
     *     <float_cell_value --> <float>
     *        
     *     <pred_cell_value> --> '(' ')' | <pred_value>
     *        
     *     <pred_value> --> '(' <pred_name> (<pred_arg>)+ ')'
     *        
     *     <pred_arg> --> <quote_string> | <nominal> | <pred_value> | 
     *                    <integer> | <float> | <time_stamp> | <formal_arg>
     *        
     *     <time_stamp> --> '(' 'TIME>' <integer> ')' 
     *        
     *     <matrix_cell_value> --> <pred_arg>
     *        
     *     <s_var_def_vocab_attribute> --> '(' VOCAB> <vocab_list> ')'
     *        
     *     <vocab_list> --> '(' (<vocab_entry>)* ')'
     *        
     *     <vocab_entry> --> <pred_name>
     *
     *                                              - 6/18/08
     *
     * Parameters:
     *
     *	  - None
     *
     * Returns:  Void.
     *
     * Changes:
     *
     *    - None.
     *
     *************************************************************************/

    private void parse_s_var_def_list()
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = "macshapa_odb_reader::parse_s_var_def_list()";
	boolean done;

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse TRUE on entry");
	}
	
	/* parse the spreadsheet variables declarations list */
	
	/* first parse the leading left parenthesis */

	if ( (this.l0_tok).code == L_PAREN_TOK )
	{
	    if ( ( (this.l1_tok).code == L_PAREN_TOK ) || 
		 ( (this.l1_tok).code == R_PAREN_TOK ) )
	    {
		get_next_token();
	    }
	    else if ( (this.l1_tok).code == SYMBOL_TOK )
	    {
		post_warning_message(LEFT_PAREN_EXPECTED_WARN, 
			"The opening left parenthesis of the spreadsheet " +
			"variable definitions list appears to be missing.\n");
	    }
	    else 
	    {
		/* If in fact the leading parenthesis in the spreadsheet 
		 * variables definitions list is missing, the first item in 
		 * the list is not a spreadsheet variable definition.  Rather 
		 * than confuse things further by inserting a parenthesis, 
		 * we eat the parenthesis here, and let the error come to 
		 * light below.
		 */

		get_next_token();
	    }
	}
	else /* we shouldn't have been called unless the next token is a '(' */
	{
	    throw new SystemErrorException(mName + 
		    "(this.l0_tok).code != L_PAREN_TOK.");
	}

	/* parse the spreadsheet variables declarations list */
	done = false;

	while ( ( ! this.abort_parse ) && ( ! done ) )
	{
	    switch ( (this.l0_tok).code )
	    {
		case L_PAREN_TOK:
		    if ( (this.l1_tok).code == SYMBOL_TOK )
		    {
			parse_s_var_def();
		    }
		    else /* not a predicate definition - discard it */
		    {
			 post_warning_message(ALIEN_LIST_IN_S_VAR_DEF_LIST_WARN, 
					      null);

			if ( ! this.abort_parse )
			{
			    parse_arbitrary_list();
			}
		    }
		    break;

		case R_PAREN_TOK:
		    done = true;
		    get_next_token();
		    break;

		case EOF_TOK:
		    done = true;
		    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
			    "EOF encountered in the spreadsheet variable " +
			    "definitions list.\n", true, true);
		    break;

		case INT_TOK:
		case ERROR_TOK:
		case SYMBOL_TOK:
		case FLOAT_TOK:
		case STRING_TOK:
		case BOOL_TOK:
		case ALIST_LABEL_TOK:
		case PRIVATE_VAL_TOK:
		case SETF_TOK:
		case DB_VAR_TOK:
		case QUOTE_TOK:
		    post_warning_message(ALIEN_ATOM_IN_S_VAR_DEF_LIST_WARN, 
					 null);

		    if ( ! this.abort_parse )
		    {
			get_next_token();
		    }
		    break;


		 default:
		     throw new SystemErrorException(mName + 
			     "Encountered unknown token type.");
	             /* commented out to keep the compiler happy */
		     // break;
	    }
	}

	return;

    } /* macshapa_odb_reader::parse_s_var_def_list() */


    /*************************************************************************
     *
     * parse_s_var_defs_attribute()
     *
     * This method parses the spread sheet variable definitions attribute 
     * in the USER> a-list.  Structurally, this attribute is simply an a-list 
     * entry with a list value.  The production generating it is given below.
     *
     *     <s_var_defs_attribute> --> '(' 'SPREADSHEET-VARIABLE-DEFINITIONS>' 
     *                                    <s_var_def_list> ')'
     *
     *                                              - 6/18/08
     *
     * Parameters:
     *
     *	  - None.
     *
     * Returns:  Void.
     *
     * Changes:
     *
     *    - None.
     *
     *************************************************************************/

    private void parse_s_var_defs_attribute()
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = 
		"macshapa_odb_reader::parse_s_var_defs_attribute()";
	final String value_missing_mssg =
		"The USER> SPREADSHEET-VARIABLE-DEFINITIONS> a-list entry " +
		"doesn't seem to contain a value.\n";
	final String type_mismatch_mssg =
		"The value of the USER> SPREADSHEET-VARIABLE-DEFINITIONS> " +
		"a-list entry must be a (possibly empty) list of spreadsheet " +
		"variable definitions.\n"; 

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse true on entry");
	}
	
	/* parse the spreadsheet variables definitions attribute */
	
	/* first parse the leading left parenthesis */

	if ( (this.l0_tok).code == L_PAREN_TOK )
	{
	    get_next_token();
	}
	else /* we shouldn't have been called unless the next token is a '(' */
	{
	    throw new SystemErrorException(mName + 
		    "(this.l0_tok).code != L_PAREN_TOK.");
	}

	/* read the a-list entry name */
	if ( ! this.abort_parse )
	{
	    if ( ( (this.l0_tok).code == ALIST_LABEL_TOK ) &&
		 ( (this.l0_tok).aux == SPREADSHEET_VARIABLE_DEFINITIONS_LABEL ) )
	    {
		get_next_token();
	    }
	    else /* system error - we shouldn't have been called unless the next */
	    {    /* token is the SPREADSHEET_VARIABLE_DEFINITIONS> a-list tag.   */

		throw new SystemErrorException(mName + 
			"this.l0_tok != \"SPREADSHEET-VARIABLE-DEFINITIONS>\".");
	    }
	}

	if ( ! this.abort_parse )
	{
	    switch ( (this.l0_tok).code )
	    {
		case L_PAREN_TOK:
		    parse_s_var_def_list();
		     break;

		case R_PAREN_TOK:
		    post_warning_message(EMPTY_ALIST_ENTRY_WARN, 
			                 value_missing_mssg); 
		    break;

		case INT_TOK:
		case ERROR_TOK:
		case SYMBOL_TOK:
		case FLOAT_TOK:
		case STRING_TOK:
		case BOOL_TOK:
		case ALIST_LABEL_TOK:
		case PRIVATE_VAL_TOK:
		case SETF_TOK:
		case DB_VAR_TOK:
		case QUOTE_TOK:
		    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
					       type_mismatch_mssg); 

		    if ( ! this.abort_parse )
		    {
			get_next_token();
		    }
		    break;

		case EOF_TOK:
		    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
			    "EOF in USER> SPREADSHEET-VARIABLE-DEFINITIONS> " +
			    "attribute.\n", true, true); 
		    break;

		default:
		    throw new SystemErrorException(mName + 
			    "Encountered unknown token type.");
	            /* commented out to keep the compiler happy */
		    // break;
	    }
	}

	/* check for EOF */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code == EOF_TOK )
	    {
		post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
			"EOF in USER> SPREADSHEET-VARIABLE-DEFINITIONS> " +
			"attribute.\n", true, true); 
	    }
	}

	/* discard any excess values that may appear in the MAX-WARNINGS> a-list entry */
	if ( ! this.abort_parse )
	{
	     if ( (this.l0_tok).code != R_PAREN_TOK )
	     {
		discard_excess_alist_entry_values();

		if ( ! this.abort_parse )
		{
		    post_warning_message(EXCESS_VALUES_IN_ALIST_ENTRY_WARN,
			    "Excess value(s) encountered in the USER> " +
			    "SPREADSHEET-VARIABLE-DEFINITIONS> attribute.\n");
		}
	    }
	}

	/* finally, consume the closing parenthesis */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code == R_PAREN_TOK )
	    {
		get_next_token();
	    }
	    else 
	    {
		/* Since we are cleaning up any excess values in the spreadsheet
		 * variable definitions attribute, this else clause is 
		 * unreachable at present.  Should we choose to drop the above 
		 * attempt at error recovery, this clause will again become 
		 * reachable.
		 */

		post_warning_message(RIGHT_PAREN_EXPECTED_WARN, 
			"The closing parenthesis was missing from the USER> " +
			"SPREADSHEET-VARIABLE-DECLARATIONS> a-list entry.\n");
	    }
	}

	return;

    } /* macshapa_odb_reader::parse_s_var_defs_attribute() */


    /*************************************************************************
     *
     * parse_s_var_def_vocab_attribute()
     *
     * This method parses a VOCAB> attribute, and adds the predicates in the
     * vocab list to the local vocabulary of the spreadsheet variable pointed
     * to by s_var_ptr.  This attribute is generated by the following 
     * productions:
     *        
     *     <s_var_def_vocab_attribute> --> '(' VOCAB> <vocab_list> ')'
     *        
     *     <vocab_list> --> '(' (<vocab_entry>)* ')'
     *        
     *     <vocab_entry> --> <pred_name>
     *
     *                                                     - 6/18/08
     *
     * Parameters:
     *
     *	  - s_var_ID:  ID of the spreadsheet variable whose vocab  
     *          attribute we are reading.
     *
     * Returns:  Void.
     *
     * Changes:
     *
     *    - None.
     *
     **************************************************************************/

    private void parse_s_var_def_vocab_attribute(long s_var_col_ID)
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = 
		"macshapa_odb_reader::parse_s_var_def_vocab_attribute()";
	final String missing_vocab_list_mssg = 
		"A VOCAB> attribute appears not to contain a value.  The " +
		"attribute will be ignored.\n";
	final String vocab_list_type_mismatch_mssg = 
		"The value of a VOCAB> attribute must be a list of predicate " +
		"names.  The attribute will be ignored.\n";

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse TRUE on entry");
	}
        
        if ( s_var_col_ID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + 
                    "s_var_col_ID invalid on entry.");
        }
	
	/* parse the vocab attribute */
	
	/* first parse the leading left parenthesis */

	if ( (this.l0_tok).code == L_PAREN_TOK )
	{
	    get_next_token();
	}
	else /* we shouldn't have been called unless the next token is a '(' */
	{
	    throw new SystemErrorException(mName + 
		    "(this.l0_tok).code != L_PAREN_TOK.");
	}

	/* read the a-list entry name */
	if ( ! this.abort_parse )
	{
	    if ( ( (this.l0_tok).code == ALIST_LABEL_TOK ) && 
		 ( (this.l0_tok).aux == VOCAB_LABEL ) )
	    {
		get_next_token();
	    }
	    else /* we shouldn't have been called unless the next token is VOCAB> */
	    {
		throw new SystemErrorException(mName + 
			"this.l0_tok != VOCAB>.");
	    }
	}

	/* read the value associated with the a-list entry & discard any excess values */
	if ( ! this.abort_parse )
	{
	    switch ( (this.l0_tok).code )
	    {
		case R_PAREN_TOK:
		    post_warning_message(EMPTY_ALIST_ENTRY_WARN, 
					 missing_vocab_list_mssg); 
		    break;

		case L_PAREN_TOK:
		    parse_vocab_list(s_var_col_ID);
		    break;

		case BOOL_TOK:
		case ERROR_TOK:
		case SYMBOL_TOK:
		case FLOAT_TOK:
		case STRING_TOK:
		case INT_TOK:
		case ALIST_LABEL_TOK:
		case PRIVATE_VAL_TOK:
		case SETF_TOK:
		case DB_VAR_TOK:
		case QUOTE_TOK:
		    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
					 vocab_list_type_mismatch_mssg); 

		    if ( ! this.abort_parse )
		    {
			get_next_token();
		    }
		    break;

		case EOF_TOK:
		    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
				       "EOF in a VOCAB> attribute.\n", 
				       true, true);
		    break;

		default:
		    throw new SystemErrorException(mName + 
			    "Encountered unknown token type.");
	            /* commented out to keep the compiler happy */
		    // break;
	    }
	}

	/* check for EOF */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code == EOF_TOK )
	    {
		post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
				   "EOF in a VOCAB> attribute.\n", true, true); 
	    }
	}

	/* discard any excess values that may appear in the VARIABLE-LENGTH> 
         * a-list entry 
         */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code != R_PAREN_TOK )
	    {
		discard_excess_alist_entry_values();

		if ( ! this.abort_parse )
		{
		    post_warning_message(EXCESS_VALUES_IN_ALIST_ENTRY_WARN,
			    "Excess values encountered a VOCAB> attribute.\n");
		}
	    }
	}

	/* read the terminating right parenthesis */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code == R_PAREN_TOK )
	    {
		get_next_token();
	    }
	    else
	    {
		/* since we are cleaning up any excess values in the vocab
		 * attribute, this else clause is unreachable at present. 
		 * Should we choose to drop the above attempt at error 
		 * recovery, this clause will again become reachable.
		 */

		post_warning_message(RIGHT_PAREN_EXPECTED_WARN, 
			"Closing parenthesis missing from a VOCAB> attribute.\n");
	    }
	}

	return;

    } /* macshapa_odb_reader::parse_s_var_def_vocab_attribute() */


    /**************************************************************************
     *
     * parse_s_var_formal_arg_list()
     *
     * This method parses a list of formal argument symbol tokens in the 
     * context of spreadsheet variable formal argument list.  Such
     * list can be generated by the following productions.
     *
     *     <s_var_formal_arg_list_attribute> --> 
     *                '(' 'FORMAL-ARG-LIST>' <s_var_formal_arg_list> ')'
     *
     *     <s_var_formal_arg_list> --> 
     *                '(' '|<ord>|' '|<onset>|' '|<offset>|' '|<val>|' ')' 
     *                |
     *                '(' '|<ord>|' '|<onset>|' '|<offset>|' (<formal_arg>)+ ')'
     *
     * Note that the second alternative in the production for 
     * <s_var_formal_arg_list> can only occur in matrix spreadsheet variables.
     *
     * In addition to parsing the formal argument list, this function must
     * also copy the formal argument list into a Vector of String, and return
     * a reference to the Vector.
     *
     * If the formal argument list is empty, the function writes a default
     * formal argument list to the insertion buffer.
     *
     *                                                  - 6/18/08
     *
     * Parameters:
     *
     *	  - None.
     *
     * Returns: A Vector of String containing the names of the formal arguments
     *      of the spreadsheet variable.
     *
     *  Changes:
     *
     *      - None.
     *
     *******************************************************************************/

    private Vector<String> parse_s_var_formal_arg_list()
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = 
		"macshapa_odb_reader::parse_s_var_formal_arg_list()";
	final String overflow_mssg = 
		"Overflow occured in a spreadsheet variable formal " +
		"argument list.\n";
	final String bad_first_farg_mssg =
		"The first argument in a spreadsheet variable formal " +
		"argument list must be \"|<ord>|\".  First argument forced " +
		"to \"|<ord>|\".\n";
	final String bad_second_farg_mssg =
		"The second argument in a spreadsheet variable formal " +
		"argument list must be \"|<onset>|\".  Second argument " +
		"forced to \"|<onset>|\".\n";
	final String bad_third_farg_mssg =
		"The third argument in a spreadsheet variable formal argument " +
		"list must be \"|<offset>|\".  Third argument forced to " +
		"\"|<offset>|\".\n";
        String fargName;
	boolean done;
        boolean duplicateArgName;
	int arg_count;
        int i;
        Vector<String> args = new Vector<String>();

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse TRUE on entry");
	}
	
	/* parse the data base body */
	
	/* first parse the leading left parenthesis */

	if ( (this.l0_tok).code == L_PAREN_TOK )
	{
	    get_next_token();
	}
	else /* we shouldn't have been called unless the next token is a '(' */
	{
            throw new SystemErrorException(mName + 
                    "(this.l0_tok).code != L_PAREN_TOK.");
	}

	arg_count      = 0;
	done           = false;

        while ( ( ! this.abort_parse ) && 
		 ( ! done ) )
	{
            switch ( (this.l0_tok).code )
	    {
		case SYMBOL_TOK:
		    if ( (((this.l0_tok).aux) & FORMAL_ARG_FLAG) != 0 )
		    {
			if ( arg_count == 0 ) /* the argument must be <ord> */
			{
			    if ( this.l0_tok.str.toString().compareTo("<ond>") 
			         != 0 )
			    {
				post_warning_message(
					BAD_FARG_IN_SVAR_FARG_LIST_WARN, 
					bad_first_farg_mssg);
			    }

			    if ( ! this.abort_parse )
			    {
                                args.add("<ord>");
			    }

			    arg_count++;
			}
			else if ( arg_count == 1 ) /* the argument must be <onset> */
			{
			    if ( this.l0_tok.str.toString().compareTo("<onset>") 
			         != 0 )
			    {
				post_warning_message(
					BAD_FARG_IN_SVAR_FARG_LIST_WARN, 
					bad_second_farg_mssg);
			    }

			    if ( ! this.abort_parse )
			    {
                                args.add("<onset>");
			    }

			    arg_count++;
			}
			else if ( arg_count == 2 ) /* the argument must be <offset> */
			{
			    if ( this.l0_tok.str.toString().compareTo("<offset>") 
			         != 0 )
			    {
				post_warning_message(
					BAD_FARG_IN_SVAR_FARG_LIST_WARN, 
					bad_third_farg_mssg);
			    }

			    if ( ! this.abort_parse )
			    {
                                args.add("<offset>");
			    }

			    arg_count++;
			}
			else if ( arg_count == 3 ) /* if the argument != <val>, */
			{                          /* must be matrix.           */
                            fargName = this.l0_tok.str.toString();
                            if ( ( fargName.compareTo("<ord>") == 0 ) ||
                                 ( fargName.compareTo("<onset>") == 0 ) || 
                                 ( fargName.compareTo("<offset>") == 0 ) )
			    {
				post_warning_message(DUP_FARG_WARN, 
					"Duplicate appeared in a spreadsheet " +
					"variable formal argument list.\n");
			    }
			    else
			    {
                                args.add(fargName);

                                arg_count++;
			    }
			}
			else if ( arg_count >= 4 ) /* must be matrix */
			{
                            fargName = this.l0_tok.str.toString();
                            i = 0;
                            duplicateArgName = false;
                            
                            while ( ( i < arg_count ) &&
                                    ( ! duplicateArgName ) )
                            {
                                if ( fargName.compareTo(args.get(i)) == 0 )
                                {
                                    duplicateArgName = true;
                                }
                                i++;
                            }
                            
			    if ( duplicateArgName )
			    {
				post_warning_message(DUP_FARG_WARN, 
                                        "Duplicate appeared in a spreadsheet " +
                                        "variable formal argument list.\n");
			    }
			    else
			    {
                                args.add(fargName);

                                arg_count++;
			    }
			}
			else /* error */
			{
			    throw new SystemErrorException(mName + 
				    "arg_count out of range.");
			}
		    }
		    else
		    {
			post_warning_message(NON_FARG_IN_FARG_LIST_WARN, 
				"This warning was occasioned by a nominal or " +
				"predicate name.\n");
		    } 
		    get_next_token(); 
		    break;

		case R_PAREN_TOK:
		    done = true;
		    get_next_token();
		    break;

		case L_PAREN_TOK:
		    post_warning_message(NON_FARG_IN_FARG_LIST_WARN, 
			    "This warning was occasioned by a list.\n");

		    if ( ! this.abort_parse ) 
		    {
			parse_arbitrary_list();
		    }
		    break;

		case ERROR_TOK:
		case INT_TOK:
		case FLOAT_TOK:
		case STRING_TOK:
		case BOOL_TOK:
		case ALIST_LABEL_TOK:
		case PRIVATE_VAL_TOK:
		case SETF_TOK:
		case DB_VAR_TOK:
		case QUOTE_TOK:
		    post_warning_message(NON_FARG_IN_FARG_LIST_WARN, 
			    "This warning was occasioned by an atom.\n");

		    if ( ! this.abort_parse )
		    {
			get_next_token();
		    }
		    break;

		case EOF_TOK:
		    done = true;
		    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
			    "EOF encountered in a spreadsheet variable " +
			    "formal argument list.\n", true, true);
		    break;

		 default:
		     throw new SystemErrorException(mName + 
			     "Encountered unknown token type.");
	             /* commented out to keep the compiler happy */
		     // break;
	     }
	}

	if ( ! this.abort_parse )
	{
	    if ( arg_count <= 0 )
	    {
		post_warning_message(EMPTY_FORMAL_ARGUMENT_LIST_WARN, 
			"Formal argument list forced to \"(|<ord>| |<onset>| " +
			"|<offset>| |<val>|)\".\n");

		if ( ! this.abort_parse )
		{
                    args = new Vector<String>();
                    args.add("<ord>");
                    args.add("<onset>");
                    args.add("<offset>");
                    args.add("<val>");
		}
	    }
	    else if ( arg_count < 4 ) 
	    {
		post_warning_message(INSUF_FARGS_IN_SVAR_FARG_LIST_WARN, 
			"Formal argument list forced to \"(|<ord>| |<onset>| " +
			"|<offset>| |<val>|)\".\n");

		if ( ! this.abort_parse )
		{
                        args = new Vector<String>();
                        args.add("<ord>");
                        args.add("<onset>");
                        args.add("<offset>");
                        args.add("<val>");
		}
	    }
	}

	return(args);

    } /* macshapa_odb_reader::parse_s_var_formal_arg_list() */


    /*************************************************************************
     *
     * parse_s_var_formal_arg_list_attribute()
     *
     * This method parses a FORMAL-ARG-LIST> attribute in the context of a
     * spreadsheet variable declarations.  This attribute is generated by the 
     * following productions:
     *        
     *            <s_var_formal_arg_list_attribute> --> 
     *                '(' 'FORMAL-ARG-LIST>' <s_var_formal_arg_list> ')'
     *
     *            <s_var_formal_arg_list> --> 
     *                '(' '|<ord>|' '|<onset>|' '|<offset>|' '|<val>|' ')' 
     *                |
     *                '(' '|<ord>|' '|<onset>|' '|<offset>|' (<formal_arg>)+ ')'
     *
     * Note that the second alternative in the production for 
     * <s_var_formal_arg_list> can only occur in matrix spreadsheet variables.
     * If this alternative occurs, the function returns true.  Otherwise the
     * function returns false.
     *
     * If the value of the formal argument list attribute is either missing,
     * or of inapropriate type, this function loads a default formal argument
     * list in to a vector of String and returns it.
     *
     *                                                     - 9/21/95
     *
     * Parameters:
     *
     *	  - None.
     *
     * Returns: a Vector of String containing the argument list of the 
     *          spread sheet variable.
     *
     * Changes:
     *
     *    - None.
     *
     *************************************************************************/

    private Vector<String> parse_s_var_formal_arg_list_attribute()
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = 
		"macshapa_odb_reader::parse_s_var_formal_arg_list_attribute()";
	final String missing_farg_list_mssg = 
		"A FORMAL-ARG-LIST> attribute appears not to contain a value.  " +
		"Value forced to (|<ord>| |<onset>| |<offset>| |<val>|).\n";
	final String farg_list_type_mismatch_mssg = 
		"The value of the FORMAL-ARG-LIST> attribute must be a list " +
		"of formal arguments.  Value forced to (|<ord>| |<onset>| " +
		"|<offset>| |<val>|).\n";
	final String overflow_mssg = 
		"Overflow occured in a spreadsheet variable declaration " +
		"formal argument list.\n";
        Vector<String> args = null;

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse TRUE on entry");
	}
	
	/* parse the formal argument list attribute */
	
	/* first parse the leading left parenthesis */

	if ( (this.l0_tok).code == L_PAREN_TOK )
	{
	    get_next_token();
	}
	else /* we shouldn't have been called unless the next token is a '(' */
	{
	    throw new SystemErrorException(mName + 
		    "(this.l0_tok).code != L_PAREN_TOK.");
	}

	/* read the a-list entry name */
	if ( ! this.abort_parse ) 
	{
	    if ( ( (this.l0_tok).code == ALIST_LABEL_TOK ) && 
		 ( (this.l0_tok).aux == FORMAL_ARG_LIST_LABEL ) )
	    {
		get_next_token();
	    }
	    else /* we shouldn't have been called unless the next token is an a-list tag */
	    {
		throw new SystemErrorException(mName + 
			"this.l0_tok != FORMAL-ARG-LIST>.");
	    }
	}

	/* read the value associated with the a-list entry & discard any excess values */
	if ( ! this.abort_parse )
	{
	    switch ( (this.l0_tok).code )
	    {
		case L_PAREN_TOK:
		    args = parse_s_var_formal_arg_list();
		    break;

		case R_PAREN_TOK:
		    post_warning_message(EMPTY_ALIST_ENTRY_WARN, 
					 missing_farg_list_mssg);

		    if ( ! this.abort_parse )
		    {
                        args = new Vector<String>();
                        args.add("<ord>");
                        args.add("<onset>");
                        args.add("<offset>");
                        args.add("<val>");
		    }
		    break;

		case BOOL_TOK:
		case ERROR_TOK:
		case SYMBOL_TOK:
		case FLOAT_TOK:
		case STRING_TOK:
		case INT_TOK:
		case ALIST_LABEL_TOK:
		case PRIVATE_VAL_TOK:
		case SETF_TOK:
		case DB_VAR_TOK:
		case QUOTE_TOK:
		    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
					 farg_list_type_mismatch_mssg); 

		    if ( ! this.abort_parse )
		    {
                        args = new Vector<String>();
                        args.add("<ord>");
                        args.add("<onset>");
                        args.add("<offset>");
                        args.add("<val>");
		    }

		    if ( ! this.abort_parse )
		    {
			get_next_token();
		    }
		    break;


		case EOF_TOK:
		    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
				       "EOF in a FORMAL-ARG-LIST> attribute.\n", 
				       true, true);
		    break;

		default:
		    throw new SystemErrorException(mName + 
			    "Encountered unknown token type.");
	            /* commented out to keep the compiler happy */
		    // break;
	    }
	}

	/* check for EOF */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code == EOF_TOK )
	    {
		post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
				   "EOF in a FORMAL-ARG-LIST> attribute.\n", 
				   true, true);
	    }
	}

	/* discard any excess values that may appear in the FORMAL-ARG-LIST> 
	 * a-list entry 
	 */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code != R_PAREN_TOK )
	    {
		discard_excess_alist_entry_values();

		if ( ! this.abort_parse )
		{
		    post_warning_message(EXCESS_VALUES_IN_ALIST_ENTRY_WARN,
			    "Excess values encountered a FORMAL-ARG-LIST> " +
			    "attribute.\n");
		}
	    }
	}

	/* read the terminating right parenthesis */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code == R_PAREN_TOK )
	    {
		get_next_token();
	    }
	    else
	    {
		/* since we are cleaning up any excess values in the formal arg
		 * list attribute, this else clause is unreachable at present.
		 * Should we choose to drop the above attempt at error recovery,
		 * this clause will again become reachable.
		 */

		post_warning_message(RIGHT_PAREN_EXPECTED_WARN, 
			"Closing parenthesis missing from a FORMAL-ARG-LIST> " +
			"attribute.\n");
	    }
	}

	return(args);

    } /* macshapa_odb_reader::parse_s_var_formal_arg_list_attribute() */


    /*************************************************************************
     *
     * parse_s_var_type_attribute()
     *
     * This method parses a TYPE> attribute, which at present can only appear
     * in the context of a spreadheet variable declaration or definition.  This 
     * attribute is generated by the following productions:
     *        
     *     <s_var_type_attribute> --> '(' 'TYPE>' <s_var_type> ')'
     *
     *     <s_var_type> --> '<<TEXT>>' | '<<NOMINAL>>' | '<<INTEGER>>' | 
     *                      '<<FLOAT>>' | '<<PREDICATE>>' | '<<MATRIX>>'
     *
     * The function returns a MatrixVocabElement.MatrixType indicating the value
     * of the TYPE> a-list entry, or the code for <<MATRIX>> if the value is 
     * either missing or of an inappropriate type.
     *
     *                                              - 6/18/08
     *
     * Parameters:
     *
     *	  - None.
     *
     * Returns: MatrixVocabElement.MatrixType correlated with the value of the
     *          TYPE> attribute.
     *
     * Changes:
     *
     *    - None.
     *
     *************************************************************************/

    private MatrixVocabElement.MatrixType parse_s_var_type_attribute()
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = "macshap_odb_reader::parse_s_var_type_attribute()";
	final String missing_type_val_mssg = 
		"A TYPE> attribute appears not to contain a value.  " +
		"Value forced to <<MATRIX>>.\n";
	final String type_val_type_mismatch_mssg = 
		"The value of the TYPE> attribute must be a private value.  " +
		"Value forced to <<MATRIX>>.\n";
	MatrixVocabElement.MatrixType type;

	type = MatrixVocabElement.MatrixType.MATRIX;  /* default value --
                                                       * overwritten if we read 
                                                       * a value from the db 
                                                       * file
                                                       */

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse TRUE on entry");
	}
	
	/* parse the private value associated with the attribute */
	
	/* first parse the leading left parenthesis */

	if ( (this.l0_tok).code == L_PAREN_TOK )
	{
	    get_next_token();
	}
	else /* we shouldn't have been called unless the next token is a '(' */
	{
	    throw new SystemErrorException(mName + 
		    "(this.l0_tok).code != L_PAREN_TOK.");
	}

	/* read the a-list entry name */
	if ( ! this.abort_parse )
	{
	    if ( ( (this.l0_tok).code == ALIST_LABEL_TOK ) && 
		 ( (this.l0_tok).aux == TYPE_LABEL ) )
	    {
		get_next_token();
	    }
	    else /* we shouldn't have been called unless the next token is an a-list tag */
	    {
		throw new SystemErrorException(mName + "this.l0_tok != TYPE>.");
	    }
        }

	/* read the value associated with the a-list entry & discard any excess values */
	if ( ! this.abort_parse )
	{
	    switch ( (this.l0_tok).code )
	    {
		case PRIVATE_VAL_TOK:
		    switch ( (this.l0_tok).aux )
		    {
			case FLOAT_PVAL:
			    type = MatrixVocabElement.MatrixType.FLOAT;
			    break;

			case INTEGER_PVAL:
			    type = MatrixVocabElement.MatrixType.INTEGER;
			    break;

			case MATRIX_PVAL:
			    type = MatrixVocabElement.MatrixType.MATRIX;
			    break;

			case NOMINAL_PVAL:
			    type = MatrixVocabElement.MatrixType.NOMINAL;
			    break;

			case PREDICATE_PVAL:
			    type = MatrixVocabElement.MatrixType.PREDICATE;
			    break;

			case TEXT_PVAL:
			    type = MatrixVocabElement.MatrixType.TEXT;
			    break;

			default:
			    /* if we add new private values, this will cease to 
			     * be an automatic system error.
			     */

			    throw new SystemErrorException(mName + 
				    "(this.l0_tok).aux out of range.");
		    }
		    get_next_token();
		    break;

		case R_PAREN_TOK:
		    post_warning_message(EMPTY_ALIST_ENTRY_WARN, 
					 missing_type_val_mssg); 
		     break;

		case L_PAREN_TOK:
		    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
					 type_val_type_mismatch_mssg); 

		     if ( ! this.abort_parse )
		     {
			parse_arbitrary_list();
		     }
		     break;

		case BOOL_TOK:
		case ERROR_TOK:
		case SYMBOL_TOK:
		case FLOAT_TOK:
		case STRING_TOK:
		case INT_TOK:
		case ALIST_LABEL_TOK:
		case SETF_TOK:
		case DB_VAR_TOK:
		case QUOTE_TOK:
		    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
					 type_val_type_mismatch_mssg); 

		    if ( ! this.abort_parse )
		    {
			get_next_token();
		    }
		    break;

		case EOF_TOK:
		    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
			    "EOF in a TYPE> attribute.\n", true, true); 
		    break;

		default:
		    throw new SystemErrorException(mName + 
			     "Encountered unknown token type.");
	            /* commented out to keep the compiler happy */
		    // break;
	     }
	 }

	 /* check for EOF */
	 if ( ! this.abort_parse )
	 {
	     if ( (this.l0_tok).code == EOF_TOK )
	     {
		post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
			"EOF in a TYPE> attribute.\n", true, true); 
	     }
	 }

	/* discard any excess values that may appear in the TYPE> a-list entry */
	 if ( ! this.abort_parse )
	 {
	     if ( (this.l0_tok).code != R_PAREN_TOK )
	     {
		discard_excess_alist_entry_values();

		if ( ! this.abort_parse )
		{
		    post_warning_message(EXCESS_VALUES_IN_ALIST_ENTRY_WARN,
			    "Excess values encountered a TYPE> attribute.\n");
		}
	     }
	 }

	/* read the terminating right parenthesis */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code == R_PAREN_TOK )
	    {
		get_next_token();
	    }
	    else
	    {
		/* Since we are cleaning up any excess values in the type
		 * attribute, this else clause is unreachable at present.
		 * Should we choose to drop the above attempt at error recovery,
		 * this clause will again become reachable.
		 */

		post_warning_message(RIGHT_PAREN_EXPECTED_WARN, 
			"Closing parenthesis missing from a TYPE> attribute.\n");
	    }
	}

	return(type);

    } /* macshapa_odb_reader::parse_s_var_type_attribute() */


    /*************************************************************************
     *
     * parse_text_cell_value()
     *
     * This method parses the value of a spreadsheet variable cell in the 
     * context of a text spreadsheet variable.  The production generating such
     * a cell value is given below:
     *        
     *            <text_cell_value> --> <text_quote_string>
     *
     * If there are no type conflicts, the function simply creates a Matrix
     * of length 1, places a TextStringDataValue containing the specified
     * text string in the first (and only) entry in the matrix, and returns a
     * reference to the newly created instance of Matrix.
     *
     * Type conficts are handled by coercing coercing the value to a string
     * where convenient, and loading the text representation into the 
     * TextStringDataValue.  Where this is inconvenient, the value is discarded, 
     * and the TextStringDataValue is set to contain an empty string.        
     *        
     *                                                     - 6/18/08
     *
     * Parameters:
     *
     *	  - farg:  Reference to a copy of the formal argument we 
     *          are going to create a data value for.  As this function is
     *          only called to parse the value of an integer cell, the 
     *          farg must be of type TEXT. 
     *
     * Returns:  Void.
     *
     * Changes:
     *
     *    - None.
     *
     *************************************************************************/

    private DataValue parse_text_cell_value(FormalArgument farg)
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = "macshapa_odb_reader::parse_text_cell_value()";
	final String overflow_mssg = "Overflow occured in a text cell value.\r";
        String value = "";
        TextStringDataValue tsdv = null;
        Vector<DataValue> argList = null;
        Matrix m = null;

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse TRUE on entry");
	}
        
        if ( farg == null )
        {
            throw new SystemErrorException(mName + "farg null on entry");
        }
        
        if ( farg.fargType != FormalArgument.FArgType.TEXT )
        {
            throw new SystemErrorException(mName + 
                    "Supplied farg cannot be replaced with a text string.");
        }
        
        if ( farg.getID() == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + 
                                           "Supplied farg has invalid ID.");
        }
	
	/* try to parse the text cell value */
	
	switch ( (this.l0_tok).code )
	{
	    case STRING_TOK:
                value = new String(this.l0_tok.str.toString());

		if ( ! this.abort_parse )
		{
		    get_next_token();
		}
		break;

	    case INT_TOK:
	    case FLOAT_TOK:
	    case SYMBOL_TOK:
	    case BOOL_TOK:
	    case PRIVATE_VAL_TOK:
	    case ALIST_LABEL_TOK:
	    case SETF_TOK:
	    case DB_VAR_TOK:
	    case QUOTE_TOK:
		post_warning_message(S_VAR_CELL_VALUE_TYPE_MISMATCH_WARN, 
			"Will force the text cell value to a text " +
			"representation of the value.\n"); 

		if ( ! this.abort_parse )
		{
                    value = new String(this.l0_tok.str.toString());
		}

		if ( ! this.abort_parse )
		{
		    get_next_token();
		}
		break;

	    case L_PAREN_TOK:
		post_warning_message(S_VAR_CELL_VALUE_TYPE_MISMATCH_WARN, 
			"The value is a list, which will be discarded. " +
			"Text cell value will be set to the empty string.\n"); 

		if ( ! this.abort_parse )
		{
		    parse_arbitrary_list();
		}
		break;

	     case R_PAREN_TOK:
		post_warning_message(S_VAR_CELL_VALUE_MISSING_WARN, 
			"Text cell value set to the empty string.\n"); 
		break;

	    case ERROR_TOK:
		post_warning_message(S_VAR_CELL_VALUE_TYPE_MISMATCH_WARN, 
			"The value is an ill formed token.  Text cell value " +
			"set to the empty string.\n"); 

		if ( ! this.abort_parse )
		{
		    get_next_token();
		}
		break;

	    case EOF_TOK:
		post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
				   "EOF in a text cell value.\n", true, true); 
		break;

	    default:
		throw new SystemErrorException(mName + 
			 "Encountered unknown token type.");
	        /* commented out to keep the compiler happy */
		// break;
	}

        tsdv = new TextStringDataValue(this.db, farg.getID(), value);

	return(tsdv);

    } /* macshapa_odb_reader::parse_text_cell_value() */


    /*************************************************************************
     *
     * parse_user_alist()
     *
     * This method parses the a-list associated with the user section of 
     * the open database body.  Structurally, this list is simply a list of
     * a-list entries, each of which is a two element list consisting a an
     * a-list entry name and its assocated value.  The productions generating
     * the user section a-list are given below:
     *
     *     <user_alist> --> '(' <preds_list_attribute> 
     *                          <s_var_decs_attribute>
     *                          <s_var_defs_attribute> ')'
     *
     *
     *     <preds_list_attribute> --> '(' 'PREDICATE-DEFINITIONS>'
     *                                    <pred_def_list> ')'
     *
     *     <s_var_decs_attribute> --> '(' 'SPREADSHEET-VARIABLE-DECLARATIONS>'
     *                                    <s_var_dec_list> ')'
     *
     *     <s_var_defs_attribute> --> '(' 'SPREADSHEET-VARIABLE-DEFINITIONS>' 
     *                                    <s_var_def_list> ')'
     *
     * Note that the attributes in the user alist must appear in the order
     * given, as each may employ definitions introduced in its predicessor.
     *
     *                                                     - 6/18/08
     *
     * Parameters:
     *
     *	  - None.
     *
     * Returns:  Void.
     *
     * Changes:
     *
     *    - None.
     *
     *******************************************************************************/

    private void parse_user_alist()
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = "macshapa_odb_reader::parse_user_alist()";
	final String missing_entry_mssg_1 =
		"PREDICATE-DEFINITIONS> a-list entry missing from the " +
		"USER> a-list?";
	final String missing_entry_mssg_2 =
		"PREDICATE-DEFINITIONS> and/or " +
		"SPREADSHEET-VARIABLE-DEFINITIONS> a-list entry(s) missing " +
		"from the USER> a-list?";
	boolean done;
	int next_tag_index; /* index into the expected_tag_codes array */
	int expected_tag_codes[] = { PREDICATE_DEFINITIONS_LABEL, 
			             SPREADSHEET_VARIABLE_DECLARATIONS_LABEL, 
				     SPREADSHEET_VARIABLE_DEFINITIONS_LABEL};

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse TRUE on entry");
	}
	
	/* parse the user alist */
	
	/* first parse the leading left parenthesis */

	if ( (this.l0_tok).code == L_PAREN_TOK )
	{
	    if ( ( (this.l1_tok).code == L_PAREN_TOK ) || 
		 ( (this.l1_tok).code == R_PAREN_TOK ) )
	    {
		get_next_token();
	    }
	    else if ( (this.l1_tok).code == ALIST_LABEL_TOK )
	    {
		post_warning_message(LEFT_PAREN_EXPECTED_WARN, 
			"The opening left parenthesis of the USER> a-list " +
			"appears to be missing.\n");
	    }
	    else
	    {
		/* if a left paren is missing, the first item in the a-list 
		 * is not an a-list entry.  If we try to recover from this 
		 * error here, we will only confuse things further.  Thus we 
		 * eat the left parenthesis & let the cards fall where they 
		 * may.
		 */

		get_next_token();
	    }
	}
	else /* we shouldn't have been called unless the next token is a '(' */
	{
	    throw new SystemErrorException(mName + 
		    "(this.l0_tok).code != L_PAREN_TOK.");
	}

	done           = false;
	next_tag_index = 0;

	while ( ( ! this.abort_parse ) && 
		( ! done ) )
	{
	    if ( (this.l0_tok).code == L_PAREN_TOK )
	    {
		if ( (this.l1_tok).code == ALIST_LABEL_TOK )
		{
		    if ( ( next_tag_index < 3 ) && 
			 ( (this.l1_tok).aux == expected_tag_codes[next_tag_index] ) )
		    {
			/* parse the alist entry */
			switch ( next_tag_index )
			{
			    case 0: /* predicate definitions */
				parse_preds_list_attribute();
				break;

			    case 1: /* spreadsheet variable declarations */
				parse_s_var_decs_attribute();
				break;

			    case 2: /* spreadsheet variable definitions */
				parse_s_var_defs_attribute();
				break;

			    default:
				throw new SystemErrorException(mName + 
					"next_tag_index out of range.");
                                /* commented out to keep the compiler happy */
				// break;
			}
			next_tag_index++;
		    }
		    else /* Unknown, unexpected, out of order, or duplicate a-list entry */
		    {
			switch ( (this.l1_tok).aux )
			{
			    case PREDICATE_DEFINITIONS_LABEL:
				if ( next_tag_index > 0 )
				{
				    post_warning_message(
					    DUPLICATE_ALIST_ENTRY_WARN, 
					    "Duplicate USER> " +
					    "PREDICATE-DEFINITIONS> entry\n");

				    if ( ! this.abort_parse )
				    {
					 parse_unknown_alist_entry();
				    }
				}
				else
				{
				    done = true;
				    throw new SystemErrorException(mName + 
					    "This else clause should be " + 
					    "unreachable.");
				}
				break;

			    case SPREADSHEET_VARIABLE_DECLARATIONS_LABEL:
				if ( next_tag_index > 1 )
				{
				    post_warning_message(
					    DUPLICATE_ALIST_ENTRY_WARN, 
					    "Duplicate USER> " +
					    "SPREADSHEET-VARIABLE-DECLARATIONS> " +
					    "entry.\"\n");

				    if ( ! this.abort_parse )
				    {
					parse_unknown_alist_entry();
				    }
				}
				else
				{
				    done = true;

				    post_error_message(
					MISSING_OR_OUT_OF_ORDER_ALIST_ENTRY_ERR,
					missing_entry_mssg_1, false, true);
				}
				break;

			    case SPREADSHEET_VARIABLE_DEFINITIONS_LABEL:
				if ( next_tag_index > 2 )
				{
				    post_warning_message(
					    DUPLICATE_ALIST_ENTRY_WARN, 
					    "Duplicate USER> " +
					    "SPREADSHEET-VARIABLE-DEFINITIONS> " +
					    "entry.\n");

				    if ( ! this.abort_parse )
				    {
					 parse_unknown_alist_entry();
				    }
				}
				else
				{
				    done = true;

				    post_error_message(
					MISSING_OR_OUT_OF_ORDER_ALIST_ENTRY_ERR,
					missing_entry_mssg_2, false, true);
				}
				break;

			    default:
				post_warning_message(
					UNKNOWN_OR_UNEXPECTED_ALIST_ENTRY_WARN, 
					"The entry is located in the USER> " +
					"a-list.\n");

				if ( ! this.abort_parse )
				{
				    parse_unknown_alist_entry();
				}
				break;
			}
		    }
		}
		else /* a-list contains a list that is not an a-list entry -- read */
		     /* it & discard it                                            */
		{
		    post_warning_message(NON_ALIST_ENTRY_LIST_IN_ALIST_WARN, 
			    "The list is located in the USER> a-list.\n");

		    if ( ! this.abort_parse )
		    {
			parse_arbitrary_list();
		    }
		}
	    }
	    else if ( (this.l0_tok).code == R_PAREN_TOK )
	    {
		done = true;
		get_next_token();

		if ( ( ! this.abort_parse ) && 
		     ( next_tag_index < 3 ) )
		{
		    post_error_message(REQUIRED_ALIST_ENTRIES_MISSING_ERR,
			    "Required entry(s) missing from the USER a-list.",
			    false, true);
		}
	    }
	    else if ( (this.l0_tok).code == EOF_TOK )
	    {
		done = true;
		post_error_message(UNEXPECTED_END_OF_FILE_ERR,
				   "EOF occurred in the USER> a-list.\n",
				   true, true);
	    }
	    else /* (this.l0_tok).code isn't '(', ')', or EOF */
	    {
		post_warning_message(NON_ALIST_ENTRY_ATOM_IN_ALIST_WARN, 
			"The atom was detected in the USER> a-list.\n");

		if ( ! this.abort_parse )
		{
		    get_next_token();
		}
	    }
	}

	return;

    } /* macshapa_odb_reader::parse_user_alist() */


    /*************************************************************************
     *
     * parse_user_section()
     *
     * This method parses the user section of the open database body.
     * Structurally, the user section is an a-list entry with the label
     * "USER>" and a list as its value.  The production generating the 
     * user section is shown below.
     *
     *         <user_section> --> '(' 'USER>' <user-alist> ')'
     *        
     *
     *                                              - 9/11/95
     *
     * Parameters:
     *
     *	  - None.
     *
     * Returns:  Void.
     *
     * Changes:
     *
     *    - None.
     *
     **************************************************************************/

    final void parse_user_section()
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = "macshapa_odb_reader::parse_user_section()";

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse TRUE on entry");
	}

	/* parse the user section */
	
	/* first parse the leading left parenthesis */

	if ( (this.l0_tok).code == L_PAREN_TOK )
	{
	    get_next_token();
	}
	else /* we shouldn't have been called unless the next token is a '(' */
	{
	    throw new SystemErrorException(mName + 
		    "(this.l0_tok).code != L_PAREN_TOK.");
	}

	/* read the a-list entry name */
	if ( ! this.abort_parse )
	{
	    if ( ( (this.l0_tok).code == ALIST_LABEL_TOK ) &&
		 ( (this.l0_tok).aux == USER_LABEL ) )
	    {
		get_next_token();
	    }
	    else /* we shouldn't have been called unless the */
	    {    /* next token is the USER> a-list tag.    */

		throw new SystemErrorException(mName + 
			"this.l0_tok != \"USER>\".");
	    }
	}

	/* read the a-list associated with the user section */
	if ( ! this.abort_parse )
	{
	    switch ( (this.l0_tok).code )
	    {
		case L_PAREN_TOK:
		    parse_user_alist();
		    break;

		 case R_PAREN_TOK:
		    post_warning_message(EMPTY_ALIST_ENTRY_WARN, 
			    "The USER> section appears not to have a value.\n"); 
		    break;

		case INT_TOK:
		case ERROR_TOK:
		case SYMBOL_TOK:
		case FLOAT_TOK:
		case STRING_TOK:
		case BOOL_TOK:
		case ALIST_LABEL_TOK:
		case PRIVATE_VAL_TOK:
		case SETF_TOK:
		case DB_VAR_TOK:
		case QUOTE_TOK:
		    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
			    "The value of the USER> section must be a list.\n"); 

		    if ( ! this.abort_parse )
		    {
			get_next_token();
		    }
		    break;

		case EOF_TOK:
		    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
				       "EOF in the USER> section.\n", 
				       true, true);
		    break;

		default:
		    throw new SystemErrorException(mName + 
			     "Encountered unknown token type.");
	            /* commented out to keep the compiler happy */
		    // break;
	    }
	}

	/* check for EOF */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code == EOF_TOK )
	    {
		post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
			"EOF in USER> section.\n", true, true);
	    }
	}

	/* discard any excess values that may appear in the USER> section a-list */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code != R_PAREN_TOK )
	    {
		discard_excess_alist_entry_values();

		if ( ! this.abort_parse )
		{
		    post_warning_message(EXCESS_VALUES_IN_ALIST_ENTRY_WARN,
			"Excess values encountered in the USER> section.\n");
		}
	    }
	}

	/* finally, consume the closing parenthesis */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code == R_PAREN_TOK )
	    {
		get_next_token();
	    }
	    else 
	    {
		/* since we are cleaning up any excess values in the user 
		 * section this else clause is unreachable at present.  
		 * Should we choose to drop the above attempt at error recovery, 
		 * this clause will again become reachable.
		 */

		post_warning_message(RIGHT_PAREN_EXPECTED_WARN, 
			"The closing parenthesis was missing from the USER> " +
			"section.\n");
	    }
	}

	return;

    } /* macshapa_odb_reader::parse_user_section() */


    /*************************************************************************
     *
     * parse_variable_length_attribute()
     *
     * This method parses a VARIABLE-LENGTH> attribute, and returns
     * the boolean value associated with the attribute.  This attribute
     * can appear in a number of contexts, and is generated by the 
     * following productions:
     *        
     * <pred_variable_length_attribute> --> '(' 'VARIABLE-LENGTH>' <boolean> ')'
     *
     *                                                     - 6/18/08
     *
     * Parameters:
     *
     *	  - None.
     *
     * Returns:  Boolean value associated with the VARIABLE-LENGTH> attribute,
     *           or FALSE if the value is missing.
     *
     * Changes:
     *
     *    - None.
     *
     *************************************************************************/

    private boolean parse_variable_length_attribute()
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = 
		"macshapa_odb_reader::parse_variable_length_attribute()";
	final String missing_variable_length_mssg = 
		"A VARIABLE-LENGTH> attribute appears not to contain a " +
		"value.  Value forced to FALSE.\n";
	final String variable_length_type_mismatch_mssg = 
		"The value of a VARIABLE-LENGTH> attribute must be TRUE " +
		"or FALSE.  Value forced to FALSE.\n";
	boolean variable_length;

	variable_length = false;

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse TRUE on entry");
	}
	
	/* parse the version attribute */
	
	/* first parse the leading left parenthesis */

	if ( (this.l0_tok).code == L_PAREN_TOK )
	{
	    get_next_token();
	}
	else /* we shouldn't have been called unless the next token is a '(' */
	{
	    throw new SystemErrorException(mName + 
		    "(this.l0_tok).code != L_PAREN_TOK.");
	}

	/* read the a-list entry name */
	if ( ! this.abort_parse )
	{
	    if ( ( (this.l0_tok).code == ALIST_LABEL_TOK ) && 
		 ( (this.l0_tok).aux == VARIABLE_LENGTH_LABEL ) )
	    {
		get_next_token();
	    }
	    else /* we shouldn't have been called unless the next token is an a-list tag */
	    {
		throw new SystemErrorException(mName + 
			"this.l0_tok != VARIABLE-LENGTH>.");
	    }
	}

	/* read the value associated with the a-list entry & discard any excess values */
	if ( ! this.abort_parse )
	{
	    switch ( (this.l0_tok).code )
	    {
		case BOOL_TOK:
                    if ( (this.l0_tok).aux == TRUE )
                    {
                        variable_length = true;
                    }
                    else if ( (this.l0_tok).aux == FALSE )
                    {
                        variable_length = false;
                    }
                    else
                    {
                        throw new SystemErrorException(mName + 
                                "boolean token neither true nor false?!?");
                    }
		    get_next_token();
		    break;

		 case R_PAREN_TOK:
		    post_warning_message(EMPTY_ALIST_ENTRY_WARN, 
					 missing_variable_length_mssg); 
		    break;

		case L_PAREN_TOK:
		    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
					 variable_length_type_mismatch_mssg); 

		    if ( ! this.abort_parse )
		    {
			parse_arbitrary_list();
		    }
		    break;

		case ERROR_TOK:
		case SYMBOL_TOK:
		case FLOAT_TOK:
		case STRING_TOK:
		case INT_TOK:
		case ALIST_LABEL_TOK:
		case PRIVATE_VAL_TOK:
		case SETF_TOK:
		case DB_VAR_TOK:
		case QUOTE_TOK:
		    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
					 variable_length_type_mismatch_mssg); 

		    if ( ! this.abort_parse )
		    {
			get_next_token();
		    }
		    break;

		case EOF_TOK:
		    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
				       "EOF in a VARIABLE-LENGTH> attribute.\n", 
				       true, true);
		    break;

		default:
		    throw new SystemErrorException(mName + 
			    "Encountered unknown token type.");
	            /* commented out to keep the compiler happy */
		    // break;
	    }
	}

	/* check for EOF */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code == EOF_TOK )
	    {
		post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
				   "EOF in a VARIABLE-LENGTH> attribute.\n", 
				   true, true);
	    }
	}

	/* discard any excess values that may appear in the VARIABLE-LENGTH> 
	 * a-list entry
	 */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code != R_PAREN_TOK )
	    {
		discard_excess_alist_entry_values();

		if ( ! this.abort_parse )
		{
		    post_warning_message(EXCESS_VALUES_IN_ALIST_ENTRY_WARN,
			    "Excess values encountered a VARIABLE-LENGTH> " +
			    "attribute.\n");
		}
	    }
	}

	/* read the terminating right parenthesis */
	if ( ! this.abort_parse )
	{
	    if ( (this.l0_tok).code == R_PAREN_TOK )
	    {
		get_next_token();
	    }
	    else
	    {
		/* since we are cleaning up any excess values in the variable
		 * length attribute, this else clause is unreachable at present.
		 * Should we choose to drop the above attempt at error recovery,
		 * this clause will again become reachable.
		 */

		post_warning_message(RIGHT_PAREN_EXPECTED_WARN, 
			"Closing parenthesis missing from a VARIABLE-LENGTH> " +
			"attribute.\n");
	    }
	}

	return(variable_length);

    } /* macshapa_odb_reader::parse_variable_length_attribute() */


    /**************************************************************************
     *
     * parse_vocab_list()
     *
     * This method parses a vocabulary list.
     * 
     * This attribute is generated by the following productions:
     *        
     *            <vocab_list> --> '(' (<vocab_entry>)* ')'
     *        
     *            <vocab_entry> --> <pred_name>
     *
     * OpenSHAPA doesn't maintain a local vocab list, so we just discard the 
     * data in the vocab list.
     *
     *                                                     - 6/18/08
     *
     * Parameters:
     *
     *	  - s_var_ID:  ID of the spreadsheet variable whose vocab list
     *          we are reading.
     *
     * Returns:  Void.
     *
     * Changes:
     *
     *    - None.
     *
     **************************************************************************/

    private void parse_vocab_list(long s_var_col_ID)
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = "macshapa_odb_reader::parse_vocab_list()";
	boolean done;

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse TRUE on entry");
	}
	
	if ( s_var_col_ID == DBIndex.INVALID_ID )
	{
	    throw new SystemErrorException(mName + 
		    "s_var_col_ID is invalid on entry.");
	}
	
	/* parse the vocab list */
	
	/* first parse the leading left parenthesis */

	if ( (this.l0_tok).code == L_PAREN_TOK )
	{
	    get_next_token();
	}
	else /* we shouldn't have been called unless the next token is a '(' */
	{
	    throw new SystemErrorException(mName + 
		    "(this.l0_tok).code != L_PAREN_TOK.");
	}

	/* now read the vocabulary list */
	if ( ! this.abort_parse )
	{
	    done = false;

	    while ( ( ! this.abort_parse ) && ( ! done ) )
	    {
		switch ( (this.l0_tok).code )
		{
		    case SYMBOL_TOK:
			if ( ((this.l0_tok).aux & FORMAL_ARG_FLAG) != 0 )
			{
			    post_warning_message(NON_PRED_IN_VOCAB_WARN, 
				    "The item was a formal argument.\n"); 
			}
			else if ( ((this.l0_tok).aux & PRED_FLAG) != 0 )
			{
			    if ( this.db.matrixVEExists(
                                        this.l0_tok.str.toString()) )
			    {
				post_warning_message(S_VAR_IN_VOCAB_LIST_WARN, 
					null); 
			    }
                            else if ( ! this.db.predVEExists(
                                        this.l0_tok.str.toString()) )
			    {
				post_warning_message(
					UNDEF_PRED_IN_VOCAB_LIST_WARN, 
					null); 
			    }
			}
			else if ( ((this.l0_tok).aux & (COLUMN_FLAG | NOMINAL_FLAG)) != 0 )
			{
			    post_warning_message(NON_PRED_IN_VOCAB_WARN, 
				    "The item was a nominal or column " +
				    "variable name.\n"); 
			}
			else /* this clause should be unreachable */
			{
			    throw new SystemErrorException(mName + 
				    "(this.l0_tok).aux appears to be corrupt.");
			}

			if ( ! this.abort_parse )
			{
			    get_next_token();
			}
			break;

		    case R_PAREN_TOK:
			done = true;
			get_next_token();
			break;

		    case L_PAREN_TOK:
			post_warning_message(NON_PRED_IN_VOCAB_WARN, 
					     "The item was a list.\n"); 

			if ( ! this.abort_parse )
			{
			    parse_arbitrary_list();
			}
			break;

		    case BOOL_TOK:
		    case ERROR_TOK:
		    case FLOAT_TOK:
		    case STRING_TOK:
		    case INT_TOK:
		    case ALIST_LABEL_TOK:
		    case PRIVATE_VAL_TOK:
		    case SETF_TOK:
		    case DB_VAR_TOK:
		    case QUOTE_TOK:
			post_warning_message(NON_PRED_IN_VOCAB_WARN, 
				"The item was an atom.\n"); 

			if ( ! this.abort_parse )
			{
			    get_next_token();
			}
			break;

		    case EOF_TOK:
			post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
					   "EOF in a vocabulary list.\r", 
					   true, true);
			break;

		    default:
			throw new SystemErrorException(mName + 
				"Encountered unknown token type.");
                        /* commented out to keep the compiler happy */
			// break;
		 }
	     }
	}

	return;

    } /* macshapa_odb_reader::parse_vocab_list() */

        
    /*** Parser section 4 -- methods for parsing the query section ***/

    /**************************************************************************
     *
     * parse_query_list()
     *
     * This function parses the list of queries that constitutes the core of 
     * the query section of the open database body.  Structurally, the query 
     * list is a list of predicate cell values, each of which is nominally a
     * query.  The production generating the query list is shown below.
     *
     *	<query_list> --> '(' (<pred_cell_value>)* ')'
     *
     * In addition to parsing the query list, this function also inserts each
     * query in the list into the query variable.
     *
     *						 - 12/14/08
     *
     * Parameters:
     *
     *    - query_col_ID: ID of the query data column.
     *
     *    - query_mve_ID: ID of the mve defining the structure of the query 
     *          column.   
     *
     * Return Value:  Void.
     *
     * Changes:
     *
     *    - None.
     *
     **************************************************************************/

    private void parse_query_list(long query_col_ID,
                                  long query_mve_ID,
                                  FormalArgument farg)
        throws SystemErrorException,
               java.io.IOException
    {
	final String mName = "macshapa_odb_reader::parse_query_list()";
	final String overflow_mssg = "Overflow occured in a query.\n";
	Boolean done;
	Boolean have_query;
	Boolean success;
        long cellID = DBIndex.INVALID_ID;
        Vector<DataValue> argList = null;
        DataValue query_dv = null;
        Matrix query_matrix = null;
	DataCell query_cell = null;
	
	if ( this.abort_parse )
	{
            throw new SystemErrorException(mName + 
                    "this.abort_parse TRUE on entry");
	}
        else if ( ( query_col_ID == DBIndex.INVALID_ID ) ||
                  ( query_mve_ID == DBIndex.INVALID_ID ) )
        {
            throw new SystemErrorException(mName + 
                    "query col and/or mve ID invalid on entry.");
        }
        else if ( farg == null )
        {
            throw new SystemErrorException(mName + "farg null on entry");
        }
        else if ( farg.getFargType() != FormalArgument.FArgType.PREDICATE )
        {
            throw new SystemErrorException(mName + 
                    "farg not of type predicate?!?");
        }
        else if ( farg.getID() == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "farg has invalid ID");
        }
	else /* parse the header section */
	{
            /* first parse the leading left parenthesis */
		
            if ( (this.l0_tok).code == L_PAREN_TOK )
            {
                this.get_next_token();
            }
	    else /* we shouldn't have been called unless the next token is a '(' */
            {
                throw new SystemErrorException(mName + 
                        "(this.l0_tok).code isnt L_PAREN_TOK.");
            }
 		
            /* set the in_query flag to true, so that parse_pred_value() will
             * flag as an error any attempt to use a defined predicate or column 
             * variable name as a nominal.
             */
            this.in_query = true;
		
            done = false;
		
            while ( ( ! this.abort_parse ) && ( ! done ) )
            {
                have_query = false; /* will set this to true if we have a query */
                query_dv = null;
                argList = null;
                query_matrix = null;
                query_cell = null;
                
			
                switch ( (this.l0_tok).code )
                {
                    case L_PAREN_TOK:
                        have_query = true; /* at worst, we will insert an empty query */
					
                        if ( (this.l1_tok).code == R_PAREN_TOK ) /* empty cell */
			{
                            /* construct empty query cell value */
                            query_dv = new PredDataValue(this.db, 
                                                         farg.getID(), 
                                                         new Predicate(this.db));
	
                            if ( ! this.abort_parse )
                            {
                                this.get_next_token(); // eat left paren
                            }
	
                            if ( ! this.abort_parse )
                            {
                                this.get_next_token();
                            }
                        }
                        else /* should be a predicate value */
                        {
                            query_dv = parse_pred_value(farg);
			}
                        break;
	 				
                    case R_PAREN_TOK:
                            done = true;
                            this.get_next_token(); /* eat the closing parenthesis */
                            break;

                    case FLOAT_TOK:
                    case INT_TOK:
                    case SYMBOL_TOK:
                    case STRING_TOK:
                    case BOOL_TOK:
                    case PRIVATE_VAL_TOK:
                    case ALIST_LABEL_TOK:
                    case SETF_TOK:
                    case DB_VAR_TOK:
                    case QUOTE_TOK:
                    case ERROR_TOK:
                        post_warning_message(ATOM_IN_QUERY_LIST_WARN, null); 

                        if ( ! this.abort_parse )
                        {
                            this.get_next_token();
                        }
                        break;

                    case EOF_TOK:
                        post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
                                "EOF encountered in the query list.\n", 
                                true, true); 
                        break;

                    default:
                        throw new SystemErrorException(mName + 
                                "Encountered unknown token type.");
                        /* commented out to keep the compiler happy */
                        // break;
	 				
                } /* end switch */
 		 	
                if ( ( have_query ) && ( ! this.abort_parse ) )
                {
                    argList = new Vector<DataValue>();
                    argList.add(query_dv);
                    query_matrix = new Matrix(this.db, query_mve_ID, argList);
                    query_cell = 
                        new DataCell(this.db, 
                            null, 
                            query_col_ID,
                            query_mve_ID,
                            new TimeStamp(MACSHAPA_TICKS_PER_SECOND, 0),
                            new TimeStamp(MACSHAPA_TICKS_PER_SECOND, 0),
                            query_matrix);
                    cellID = this.db.appendCell(query_cell);
                    
                    /* if debug level is high enough, dump the query cell 
                     * definition 
                     */
                    if ( ( ! this.abort_parse ) && 
                         ( this.debug_level >= 2 ) )
                    {
                        query_cell = (DataCell)this.db.getCell(cellID);
                        dump_s_var_cell_definition_to_listing(query_cell);
                    }
                }
                
            } /* end while */
 		
            this.in_query = false; /* reset the in_query flag to FALSE */
	}

	return;

    } /* macshapa_odb_reader::parse_query_list() */


    /**************************************************************************
     *
     * parse_query_section()
     *
     * This function parses the query section of the open database body.
     * Structurally, the query section is an a-list entry with the label
     * "QUERY>" and a list as its value.  The production generating the 
     * query section is shown below.
     *
     *  	<query_section> --> '(' 'QUERY>' <query_list> ')'
     *
     *          <query_list> --> '(' (<pred_cell_value>)* ')'
     *
     *
     *                                               - 12/14/08
     *
     * Parameters:
     *
     *    - None.
     *
     * Return Value:  Void.
     *
     * Changes:
     *
     *    - None.
     *
     **************************************************************************/

    private void parse_query_section()
        throws SystemErrorException,
               java.io.IOException
    {
        final String mName = "macshapa_odb_reader::parse_query_section()";
        long query_mve_ID = DBIndex.INVALID_ID;
        long query_col_ID = DBIndex.INVALID_ID;
        FormalArgument query_farg = null;
        MatrixVocabElement query_mve = null;
        DataColumn query_col = null;

        if ( this.abort_parse )
        {
            throw new SystemErrorException(mName + 
                    "this.abort_parse TRUE on entry");
        }
        else if ( this.db.colNameInUse(QUERY_VAR_NAME) )
        {
            throw new SystemErrorException(mName + 
                    "Query variable alread defined?!?");
        }
        else /* parse the header section */
        {
            /* Set up the query variable */
            
            query_mve = new MatrixVocabElement(this.db, QUERY_VAR_NAME);
            
            query_mve.setType(MatrixVocabElement.MatrixType.PREDICATE);
                
            query_farg = new PredFormalArg(this.db, "<val>");
            
            query_mve.appendFormalArg(query_farg);
                
            query_mve.setSystem();
                
            /* Note that we are not making a copy of the mve
             * before we insert it -- thus must be careful not
             * to corrupt it.
             */
            this.db.vl.addElement(query_mve);

            query_mve_ID = query_mve.getID();
        
            if ( this.db.vl.getVocabElement(query_mve_ID) != query_mve )
            {
                throw new SystemErrorException(mName + 
                        "query_mve insertion in vl failed?");
            }
                
            query_col = new DataColumn(this.db, QUERY_VAR_NAME, true, false, query_mve_ID);
        
            this.db.cl.addColumn(query_col);
        
            query_col_ID = query_col.getID();

            if ( ( this.db.cl.getColumn(query_col_ID) != query_col ) ||
                 ( this.db.cl.getColumn(query_col.getName()) != query_col ) ||
                 ( query_col.getItsCells() == null ) )
            {
                throw new SystemErrorException(mName + 
                        "query col insertion in cl failed");
            }
        
            query_mve.setItsColID(query_col_ID);
            
            /* now get copies of the query col, mve, & farg so we don't have
             * to worry about corrupting them.
             */
            query_mve = this.db.getMatrixVE(query_mve_ID);
            query_farg = query_mve.getFormalArg(0);
            query_col = this.db.getDataColumn(query_col_ID);
        
            
            /* Parse the leading left parenthesis */

            if ( (this.l0_tok).code == L_PAREN_TOK )
            {
                this.get_next_token();
            }
            else /* we shouldn't have been called unless the next token is a '(' */
            {
                throw new SystemErrorException(mName + 
                        "(this.l0_tok).code isnt L_PAREN_TOK.");
            }

            /* read the a-list entry name */
            if ( ! this.abort_parse )
            {
                if ( ( (this.l0_tok).code == ALIST_LABEL_TOK ) &&
                     ( (this.l0_tok).aux == QUERY_LABEL ) )
                {
                    this.get_next_token();
                }
                else /* system error - we shouldn't have been called unless the */
                {    /*                next token is the QUERY> a-list tag.     */
                    throw new SystemErrorException(mName + 
                            "this.l0_tok isnt \"QUERY>\".");
                }
            }

            /* read the query list associated with the query section */
            if ( ! this.abort_parse )
            {
                switch ( (this.l0_tok).code )
                {
                    case L_PAREN_TOK:
                            parse_query_list(query_col_ID, 
                                             query_mve_ID, 
                                             query_farg);
                            break;

                    case R_PAREN_TOK:
                            post_warning_message(EMPTY_ALIST_ENTRY_WARN, 
                                "The QUERY> section appears not to have a value.\n"); 
                            break;

                    case INT_TOK:
                    case ERROR_TOK:
                    case SYMBOL_TOK:
                    case FLOAT_TOK:
                    case STRING_TOK:
                    case BOOL_TOK:
                    case ALIST_LABEL_TOK:
                    case PRIVATE_VAL_TOK:
                    case SETF_TOK:
                    case DB_VAR_TOK:
                    case QUOTE_TOK:
                        post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
                                "The value of the QUERY> section must be a list.\n"); 

                        if ( ! this.abort_parse )
                        {
                            this.get_next_token();
                        }
                        break;


                    case EOF_TOK:
                        post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
                                           "EOF in QUERY> section.\r", 
                                           true, true);
                        break;

                    default:
                        throw new SystemErrorException(mName + 
                                "Encountered unknown token type.");
                        /* commented out to keep the compiler happy */
                        // break;
                }
            }

            /* check for EOF */
            if ( ! this.abort_parse )
            {
                if ( (this.l0_tok).code == EOF_TOK )
                {
                    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
                            "EOF in QUERY> section.\n", true, true);
                }
            }

            /* discard any excess values that may appear in the QUERY> section */
            if ( ! this.abort_parse )
            {
                if ( (this.l0_tok).code != R_PAREN_TOK )
                {
                    discard_excess_alist_entry_values();

                    if ( ! this.abort_parse )
                    {
                        post_warning_message(EXCESS_VALUES_IN_ALIST_ENTRY_WARN,
                            "Excess values encountered in the QUERY> section.\n");
                    }
                }
            }

            /* finally, consume the closing parenthesis */
            if ( ! this.abort_parse )
            {
                if ( (this.l0_tok).code == R_PAREN_TOK )
                {
                    this.get_next_token();
                }
                else 
                {
                    /* since we are cleaning up any excess values in the query 
                     * section this else clause is unreachable at present.  
                     * Should we choose to drop the above attempt at error 
                     * recovery, this clause will again become reachable.
                     *                                   -10/24/95
                     */

                    post_warning_message(RIGHT_PAREN_EXPECTED_WARN, 
                            "The closing parenthesis was missing from " +
                            "the QUERY> section.\n");
                }
             }
        }

        return;

    } /* macshapa_odb_reader::parse_query_section() */

        
    /*** Parser section 5 -- methods for parsing the system section ***/

//    /*************************************************************************
//     *
//     * parse_alignments_attribute()
//     *
//     * This method parses a ALIGNMENTS> attribute, which is generated by 
//     * the following productions:
//     * 
//     *     <alignments_attribute> --> '(' 'ALIGNMENTS>' <alignments_list> ')'
//     *    
//     *     <alignments_list> --> '(' (<alignment_var>)* ')'
//     *
//     *     <alignment_var> --> '(' <s_var_name> <alignment_var_alist> ')'
//     *
//     *     <alignment_var_alist> --> '(' <alignment_var_attributes> ')'
//     *
//     *     <alignment_var_attributes> --> <s_var_type_attribute> 
//     *                                    <s_var_variable_length_attribute> 
//     *                                    <s_var_formal_arg_list_attribute> 
//     *                                    <s_var_col_width_attribute>
//     *                                    <s_var_def_cells_attribute>
//     *
//     * The purpose of this attribute is to support the saving and restoring of
//     * alignments by the comparisons report.  Note that the set of valid 
//     * saved alignments is much smaller than the set described by the above
//     * productions.  Thus, I presume that it is possible to include a 
//     * stored alignment in the value of this attribute that will crash the
//     * comparisons report.  On the other hand, I don't understand the 
//     * comparisons report well enough to write a parser that would prevent
//     * this.  On the third hand, this should all be moot as this section of
//     * the SYSTEM> section is optional, and had been declared off limits to
//     * the user.  
//     *
//     * At present, the parser for the ALIGNEMNTS> attribute piggybacks on the
//     * code for parsing spreadsheet variable declarations and definitions in
//     * the user section.  Should we ever narrow the above production so that
//     * it will be impossible for the user to crash MacSHAPA by entering 
//     * junk in the value of this attribute, we should write new code for the
//     * purpose, instead of modifying the user spreadsheet variable parsing
//     * code.
//     *
//     *                                              - 6/22/08
//     *
//     * Parameters:
//     *
//     *    - None.
//     *
//     * Returns:  Void.
//     *
//     * Changes:
//     *
//     *    - None.
//     *
//     *************************************************************************/
//
//    private void parse_alignments_attribute()
//        throws SystemErrorException
//    {
//        final String mName = 
//                "macshapa_odb_reader::parse_alignments_attribute()";
//        final String missing_alignments_list_mssg = 
//                "The ALIGNMENTS> attribute appears not to contain a value.  " +
//                "The attribute will be ignored.\n";
//        final String alignments_list_type_mismatch_mssg = 
//                "The value of a ALIGNMENTS> attribute must be a list.  " +
//                "The attribute will be ignored.\n";
//
//        if ( this.abort_parse )
//        {
//            throw new SystemErrorException(mName + 
//                    "this.abort_parse TRUE on entry");
//        }
//        
//        /* parse the alignments attribute */
//
//        /* first parse the leading left parenthesis */
//
//        if ( (this.l0_tok).code == L_PAREN_TOK )
//        {
//            get_next_token();
//        }
//        else /* we shouldn't have been called unless the next token is a '(' */
//        {
//            throw new SystemErrorException(mName + 
//                    "(this.l0_tok).code isnt L_PAREN_TOK.");
//        }
//
//        /* read the a-list entry name */
//        if ( ! this.abort_parse )
//        {
//            if ( ( (this.l0_tok).code == ALIST_LABEL_TOK ) && 
//                 ( (this.l0_tok).aux == ALIGNMENTS_LABEL ) )
//            {
//                get_next_token();
//            }
//            else /* we shouldn't have been called unless the next token is ALIGNMENTS> */
//            {
//                throw new SystemErrorException(mName + 
//                        "this.l0_tok isnt ALIGNMENTS>.");
//            }
//        }
//
//        /* read the value associated with the a-list entry & discard 
//         * any excess values 
//         */
//        if ( ! this.abort_parse )
//        {
//            switch ( (this.l0_tok).code )
//            {
//                case R_PAREN_TOK:
//                    post_warning_message(EMPTY_ALIST_ENTRY_WARN, 
//                                               missing_alignments_list_mssg); 
//                    break;
//
//                case L_PAREN_TOK:
//                    parse_alignments_list();
//                    break;
//
//                case BOOL_TOK:
//                case ERROR_TOK:
//                case SYMBOL_TOK:
//                case FLOAT_TOK:
//                case STRING_TOK:
//                case INT_TOK:
//                case ALIST_LABEL_TOK:
//                case PRIVATE_VAL_TOK:
//                case SETF_TOK:
//                case DB_VAR_TOK:
//                case QUOTE_TOK:
//                    post_warning_message(
//                            ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
//                            alignments_list_type_mismatch_mssg); 
//
//                     if ( ! this.abort_parse )
//                     {
//                        get_next_token();
//                    }
//                     break;
//
//                case EOF_TOK:
//                    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                            "EOF in the SYSTEM> ALIGNMENTS> attribute.\n", 
//                            true, true);
//                    break;
//
//                 default:
//                     throw new SysetmErrorException(mName + 
//                             "Encountered unknown token type.");
//                     break;
//            }
//        }
//
//        /* check for EOF */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code == EOF_TOK )
//            {
//                post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                        "EOF in the SYSTEM> ALIGNMENTS> attribute.\n",  
//                        true, true);
//            }
//        }
//
//        /* discard any excess values that may appear in the ALIGNMENTS> a-list entry */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code != R_PAREN_TOK )
//            {
//                discard_excess_alist_entry_values();
//
//                if ( ! this.abort_parse )
//                {
//                    post_warning_message(
//                            EXCESS_VALUES_IN_ALIST_ENTRY_WARN,
//                            "Excess values encountered the SYSTEM> " +
//                            "ALIGNMENTS> attribute.\n");
//                }
//            }
//        }
//
//        /* read the terminating right parenthesis */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code == R_PAREN_TOK )
//            {
//                get_next_token();
//            }
//            else
//            {
//                /* since we are cleaning up any excess values in the alignments
//                 * attribute, this else clause is unreachable at present. 
//                 * Should we choose to drop the above attempt at error 
//                 * recovery, this clause will again become reachable.
//                 */
//
//                post_warning_message(RIGHT_PAREN_EXPECTED_WARN, 
//                        "Closing parenthesis missing from the SYSTEM> " +
//                        "ALIGNMENTS> attribute.\n");
//            }
//        }
//
//        return;
//
//    } /* macshapa_odb_reader::parse_alignments_attribute() */
//
//
//    /*************************************************************************
//     *
//     * parse_alignments_list()
//     *
//     * This method parses a list of alignments, each of which is a spreadsheet
//     * variable of system type.  While there are rather strict restrictions on 
//     * the type and contents of alignments, at present the parser will accept
//     * almost anything generated by the following productions:
//     * 
//     *     <alignments_list> --> '(' (<alignment_var>)* ')'
//     *
//     *     <alignment_var> --> '(' <s_var_name> <alignment_var_alist> ')'
//     *
//     *     <alignment_var_alist> --> '(' <alignment_var_attributes> ')'
//     *
//     *     <alignment_var_attributes> --> <s_var_type_attribute> 
//     *                                    <s_var_variable_length_attribute> 
//     *                                    <s_var_formal_arg_list_attribute> 
//     *                                    <s_var_col_width_attribute>
//     *                                    <s_var_def_cells_attribute>
//     *
//     * The one exception to this rule is the insistance that the name of the
//     * alignment variable include the required prefix.
//     *
//     * As a result of the laxity of the parser it is possible to load an
//     * invalid alignment into MacSHAPA and presumably crash it.  However, 
//     * if the users follow instructions, and never attempt to generate or
//     * alter an alignment, this should never happen.  Frankly, this makes
//     * me nervous as hell, as the user should never be able to crash a program
//     * with input.  However we don't have the resources to do the job 
//     * correctly at this time, and this seems to be one of the safest places
//     * to cut corners.
//     *
//     *                                              - 6/22/08
//     *
//     * Parameters:
//     *
//     *    - None.
//     *
//     * Returns:  Void.
//     *
//     * Changes:
//     *
//     *    - None.
//     *
//     *************************************************************************/
//       
//    private void parse_alignments_list()
//        throws SystemErrorException
//    {
//        final String mName = "macshapa_odb_reader::parse_alignments_list()";
//        boolean done;
//
//        if ( this.abort_parse )
//        {
//            throw new SystemErrorException(mName + 
//                    "this.abort_parse TRUE on entry");
//        }
//        
//        /* parse the alignments list */
//        
//        /* first parse the leading left parenthesis */
//
//        if ( (this.l0_tok).code == L_PAREN_TOK )
//        {
//            get_next_token();
//        }
//        else /* we shouldn't have been called unless the next token is a '(' */
//        {
//            throw new SystemErrorException(mName + 
//                    "(this.l0_tok).code isnt L_PAREN_TOK.");
//        }
//
//        /* now read the alignments list */
//
//        done = FALSE;
//
//        while ( ( ! this.abort_parse ) && ( ! done ) )
//        {
//            switch ( (this.l0_tok).code )
//            {
//                case R_PAREN_TOK:
//                    done = true;
//                    get_next_token();
//                    break;
//
//                case L_PAREN_TOK:
//                    if ( (this.l1_tok).code == SYMBOL_TOK )
//                    {
//                        parse_alignment_var();
//                    }
//                    else /* ! an alignment variable - discard it */
//                    {
//                        post_warning_message(
//                                ALIEN_LIST_IN_ALIGNMENTS_LIST_WARN, null); 
//
//                        if ( ! this.abort_parse )
//                        {
//                            parse_arbitrary_list();
//                        }
//                    }
//                    break;
//
//                case BOOL_TOK:
//                case ERROR_TOK:
//                case SYMBOL_TOK:
//                case FLOAT_TOK:
//                case STRING_TOK:
//                case INT_TOK:
//                case ALIST_LABEL_TOK:
//                case PRIVATE_VAL_TOK:
//                case SETF_TOK:
//                case DB_VAR_TOK:
//                case QUOTE_TOK:
//                    post_warning_message(ATOM_IN_ALIGNMENTS_LIST_WARN, 
//                                               null); 
//
//                    if ( ! this.abort_parse )
//                    {
//                        get_next_token();
//                    }
//                    break;
//
//                case EOF_TOK:
//                    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                            "EOF in the alignments list in the SYSTEM> " +
//                            "ALIGNMENTS> attribute.\n", 
//                            true, true);
//                    break;
//
//                default:
//                    throw new SystemErrorException(mName + 
//                            "Encountered unknown token type.");
//                    break;
//            }
//        }
//
//        return;
//
//    } /* parse_alignments_list() */
//
//
//    /*************************************************************************
//     *
//     * parse_alignment_var()
//     *
//     * This method parses an alignment spreadsheet variable.  The productions 
//     * generating a alignment spreadsheet variable are given below: 
//     * 
//     *     <alignment_var> --> '(' <s_var_name> <alignment_var_alist> ')'
//     *
//     *     <alignment_var_alist> --> '(' <alignment_var_attributes> ')'
//     *
//     *     <alignment_var_attributes> --> <s_var_type_attribute> 
//     *                                    <s_var_variable_length_attribute> 
//     *                                    <s_var_formal_arg_list_attribute> 
//     *                                    <s_var_col_width_attribute>
//     *                                    <s_var_def_cells_attribute>
//     *
//     * Note the heavy use of non-terminals from the user section of the 
//     * grammar.  In effect, the parser will currently accept almost any 
//     * spreadsheet variable as an alignment.  This causes potential problems
//     * as the actual code for storing and reloading alignments is much more
//     * finicky, but in theory the user will never attempt to generate or 
//     * modify an alignment.  Wishful thinking I know, but given the limited
//     * resources available, I had to cut corners somewhere, and this seemed to
//     * be one of the least dangerous places.
//     *
//     * Note however that we do a bit of error checking here and there where
//     * it is convenient and not too costly in terms of development time.  For
//     * instance, in this function we test to make sure that we are dealing with
//     * a valid alignment variable name.
//     *
//     *                                              - 6/22/08
//     *
//     * Parameters:
//     *
//     *    - None
//     *
//     * Returns:  Void.
//     *
//     * Changes:
//     *
//     *    - None.
//     *
//     **************************************************************************/
//
//    private void parse_alignment_var()
//        throws SystemErrorException
//    {
//        final String mName = "macshapa_odb_reader::parse_alignment_var()";
//        boolean discard;
//        boolean done;
//        boolean excess_values;
//
//        if ( this.abort_parse )
//        {
//            throw new SystemErrorException(mName + 
//                    "this.abort_parse TRUE on entry");
//        }
//        
//        /* parse the spreadsheet variable declaration */
//        
//        discard       = false;
//
//        /* reset ibuf_len to zero since we are probably starting a new buffer */
//        this.ibuf_len = 0;
//
//        /* first parse the leading left parenthesis */
//        if ( (this.l0_tok).code == L_PAREN_TOK )
//        {
//            get_next_token();
//        }
//        else /* we shouldn't have been called unless the next token == a '(' */
//        {
//            throw new SystemErrorException(mName + 
//                    "(this.l0_tok).code isnt L_PAREN_TOK.");
//        }
//
//        /* parse the spreadsheet variable name */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code == SYMBOL_TOK )
//            {
//                if ( ( ((this.l0_tok).aux & COLUMN_FLAG ) == 0 ) ||
//                     ( this.l0_tok.toString().compareTo(STORED_ALIGNMENT_PREFIX) 
//                       != 0 ) )
//                {
//                    discard = true;
//
//                    post_warning_message(INVALID_ALIGNMENT_NAME_WARN, 
//                                         null);
//                }
//                else if ( ( this.spread_doc_ptr->SymTable->
//                            lookupPred((this.l0_tok).str_ptr) != NULL 
//                          ) 
//                          or
//                          ( this.spread_doc_ptr->SymTable->
//                            lookupVar((this.l0_tok).str_ptr) != NULL 
//                          ) 
//                        )
//                {
//                    /* a predicate and/or spreadsheet variable of the same name already exists */
//
//                    discard = TRUE;
//
//                    post_warning_message(ALIGNMENT_NAME_ALREADY_IN_USE_WARN, 
//                                         null);
//                }
//                else /* load the name of the alignment into the buffer */
//                {
//                    append_str_to_ibuf((this.l0_tok).str_ptr,
//                        "Overflow occured in an alignment.\n");
//                }
//
//                if ( ! this.abort_parse )
//                {
//                    get_next_token();
//                }
//            }
//            else /* we shouldn't have been called unless the next token is a symbol */
//            {
//                throw new SystemErrorException(mName + 
//                        "this.l0_tok isnt SYMBOL_TOK.");
//            }
//        }
//
//        /* read the a-list associated with the alignment */
//        if ( ! this.abort_parse )
//        {
//            switch ( (this.l0_tok).code )
//            {
//                case L_PAREN_TOK:
//                    /* note that if we get this far, and discard isnt TRUE, the
//                     * spreadsheet variable name is already in the insertion 
//                     * buffer. 
//                     */
//
//                    if ( discard )
//                    {
//                        parse_arbitrary_list();
//                    }
//                    else
//                    {
//                        parse_alignment_var_alist();
//                    }
//                    break;
//
//                case R_PAREN_TOK:
//                    post_warning_message(EMPTY_ALIGNMENT_WARN, null);
//                    break;
//
//                case INT_TOK:
//                case ERROR_TOK:
//                case SYMBOL_TOK:
//                case FLOAT_TOK:
//                case STRING_TOK:
//                case BOOL_TOK:
//                case ALIST_LABEL_TOK:
//                case PRIVATE_VAL_TOK:
//                case SETF_TOK:
//                case DB_VAR_TOK:
//                case QUOTE_TOK:
//                    post_warning_message(TYPE_MISMATCH_IN_ALIGNMENT_WARN, 
//                        "The alignment will be discarded.\n"); 
//
//                    if ( ! this.abort_parse )
//                    {
//                        get_next_token();
//                    }
//                    break;
//
//
//                case EOF_TOK:
//                    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                            "EOF in an alignment in the SYSTEM> ALIGNMENTS> " +
//                            "attribute.\n", true, true); 
//                    break;
//
//                default:
//                    throw new SystemErrorException(mName + 
//                            "Encountered unknown token type.");
//                    break;
//            }
//        }
//
//        /* discard any excess values & consume the closing parenthesis */
//        if ( ! this.abort_parse )
//        {
//            done = false;
//            excess_values = false;
//
//            while ( ( ! this.abort_parse ) && 
//                    ( ! done ) )
//            {
//                switch ( (this.l0_tok).code )
//                {
//                    case R_PAREN_TOK:
//                        done = true;
//                        break;
//
//                    case L_PAREN_TOK:
//                        excess_values = true;
//                        parse_arbitrary_list();
//                        break;
//
//                    case ERROR_TOK:
//                    case SYMBOL_TOK:
//                    case INT_TOK:
//                    case FLOAT_TOK:
//                    case STRING_TOK:
//                    case BOOL_TOK:
//                    case ALIST_LABEL_TOK:
//                    case PRIVATE_VAL_TOK:
//                    case SETF_TOK:
//                    case DB_VAR_TOK:
//                    case QUOTE_TOK:
//                        excess_values = true;
//                        get_next_token();
//                        break;
//
//                    case EOF_TOK:
//                        done = true;
//                        post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                                "EOF in an alignment in the SYSTEM> " +
//                                "ALIGNMENTS> attribute.\n", true, true); 
//                        break;
//
//                    default:
//                        throw new SystemErrorException(mName + 
//                                "Encountered unknown token type.");
//                        break;
//                }
//            }
//
//            if ( ( ! this.abort_parse ) &&
//                 ( done ) && ( excess_values ) )
//            {
//                post_warning_message(EXCESS_VALUES_IN_AN_ALIGNMENT_WARN, null);
//            }
//         }
//
//         /* finally, consume the closing parenthesis */
//         if ( ! this.abort_parse )
//         {
//            if ( (this.l0_tok).code == R_PAREN_TOK )
//            {
//                get_next_token();
//            }
//            else 
//            {
//                throw new SystemErrorException(mName + 
//                        "This else clause should be unreachable.");
//            }
//        }
//
//        return;
//
//    } /* macshapa_odb_reader::parse_alignment_var() */
//
//
//    /*************************************************************************
//     *
//     * parse_alignment_var_alist()
//     *
//     * This method parses the alist assocated with an alignment spreadsheet
//     * variable.  The productions generating a alignment spreadsheet variable 
//     * are given below: 
//     *
//     *     <alignment_var_alist> --> '(' <alignment_var_attributes> ')'
//     *
//     *     <alignment_var_attributes> --> <s_var_type_attribute> 
//     *                                    <s_var_variable_length_attribute> 
//     *                                    <s_var_formal_arg_list_attribute> 
//     *                                    <s_var_col_width_attribute>
//     *                                    <s_var_def_cells_attribute>
//     *
//     * Note the heavy use of non-terminals from the user section of the 
//     * grammar.  In effect, the parser will currently accept almost any 
//     * spreadsheet variable as an alignment.  This causes potential problems
//     * as the actual code for storing and reloading alignments is much more
//     * finicky, but in theory the user will never attempt to generate or 
//     * modify an alignment.  Wishful thinking I know, but given the limited
//     * resources available, I had to cut corners somewhere, and this seemed to
//     * be one of the least dangerous places.
//     *
//     * However that we do a bit of error checking here and there where
//     * it is convenient and not too costly in terms of development time.  For
//     * instance, in this function we test to make sure that we are dealing with
//     * a fixed length matrix spreadsheet variable before we actually create the
//     * variable.
//     *
//     * When this function is called, we are guaranteed that the name of the 
//     * alignment has already been written to the input buffer.  
//     *
//     *                                              - 6/22/08
//     *
//     * Parameters:
//     *
//     *    - None.
//     *
//     * Returns:  Void.
//     *
//     * Changes:
//     *
//     *    - None.
//     *
//     *************************************************************************/
//
//    private void parse_alignment_var_alist()
//        throws SystemErrorException
//    {
//        final String mName = "macshapa_odb_reader::parse_alignment_var_alist()";
//        boolean discard;
//        boolean done;
//        boolean must_be_matrix;
//        boolean variable_length;
//        Pixels col_width;
//        Variable alignment_var_ptr;
//        DataID type;
//        int next_tag_index; /* index into the expected_tag_codes array */
//        int expected_tag_codes[] = { TYPE_LABEL, 
//                                     VARIABLE_LENGTH_LABEL, 
//                                     FORMAL_ARG_LIST_LABEL, 
//                                     COLUMN_WIDTH_LABEL,
//                                     CELLS_LABEL };
//
//        if ( this.abort_parse )
//        {
//            throw new SystemErrorException(mName + 
//                    "this.abort_parse TRUE on entry");
//        }
//        
//        /* parse the a-list */
//        
//        /* first parse the leading left parenthesis */
//        if ( (this.l0_tok).code == L_PAREN_TOK )
//        {
//            get_next_token();
//        }
//        else /* system error - we shouldn't have been called unless the next token is a '(' */
//        {
//            throw new SystemErrorException(mName + 
//                    "(this.l0_tok).code isnt L_PAREN_TOK.");
//        }
//
//        discard        = false;
//        done           = false;
//        next_tag_index = 0;
//
//        while ( ( ! this.abort_parse ) && 
//                ( ! done ) )
//        {
//            if ( (this.l0_tok).code == L_PAREN_TOK )
//            {
//                if ( (this.l1_tok).code == ALIST_LABEL_TOK )
//                {
//                    if ( ( next_tag_index < 5 ) && 
//                         ( (this.l1_tok).aux == 
//                            expected_tag_codes[next_tag_index] ) )
//                    {
//                        /* parse the alist entry */
//                        switch ( next_tag_index )
//                        {
//                            case 0: /* get the type */
//                                type = parse_s_var_type_attribute();
//
//                                if ( ( ! this.abort_parse ) &&
//                                     ( type != matrixID ) )
//                                {
//                                    discard = true;
//
//                                    post_warning_message(
//                                        ILL_FORMED_ALIGNMENT_WARN, 
//                                        "Alignments must be of matrix type.\n");
//                                }
//                                break;
//
//                            case 1: /* variable length */
//                                variable_length = 
//                                        parse_variable_length_attribute();
//
//                                if ( ( ! this.abort_parse ) &&
//                                     ( variable_length ) )
//                                {
//                                    discard = true;
//
//                                    post_warning_message(
//                                            ILL_FORMED_ALIGNMENT_WARN, 
//                                            "Alignments may ! have variable " +
//                                            "length argument lists.\n");
//                                }
//                                break;
//
//                            case 2: /* formal argument list */
//                                must_be_matrix = 
//                                        parse_s_var_formal_arg_list_attribute();
//
//                                if ( ( ! this.abort_parse ) &&
//                                     ( ! must_be_matrix ) )
//                                {
//                                    discard = true;
//
//                                    post_warning_message(
//                                            ILL_FORMED_ALIGNMENT_WARN, 
//                                            "Alignments must have three " +
//                                            "arguments.\n");
//                                }
//                                break;
//
//                            case 3: /* column width */
//                                col_width = parse_s_var_col_width_attribute();
//                                break;
//
//                            case 4: /* cells */
//                                if ( ! discard )  /* attempt to create the */
//                                                  /* alignment variable    */
//                                {
//                                    if ( this.debug_level >= 2 )
//                                    {
//                                        dump_s_var_definition_to_listing(type, 
//                                                variable_length, col_width);
//                                    }
//
//                                    alignment_var_ptr = this.spread_doc_ptr->SymTable->
//                                        DefVar(this.ibuf, type, variable_length, TRUE);
//
//                                    if ( alignment_var_ptr == NULL )
//                                    {
//                                        this.proceed = FALSE;
//                                        error3("SYSTEM ERROR", 
//                                               "Definition of an alignment variable failed.",
//                                               fcnNamePtr);
//                                    }
//                                    else
//                                    {
//                                        alignment_var_ptr->hExtent = col_width;
//                                    }
//                                }
//
//                                if ( ! this.abort_parse )
//                                {
//                                    if ( discard )
//                                    {
//                                        parse_unknown_alist_entry();
//                                    }
//                                    else
//                                    {
//                                        parse_s_var_def_cells_attribute(alignment_var_ptr);
//                                    }
//                                }
//                                break;
//
//                            default:
//                                throw new SystemErrorException(mName + 
//                                        "next_tag_index out of range.");
//                                break;
//                         }
//                         next_tag_index++;
//                     }
//                     else /* Unknown, unexpected, out of order, or duplicate a-list entry */
//                     {
//                         switch ( (this.l1_tok).aux )
//                         {
//                             case TYPE_LABEL:
//                                 if ( next_tag_index > 0 )
//                                 {
//                                    post_warning_message(
//                                            DUPLICATE_ALIST_ENTRY_WARN, 
//                                            "Duplicate TYPE> entry in an " +
//                                            "alignment a-list\n");
//
//                                    if ( ! this.abort_parse )
//                                    {
//                                         parse_unknown_alist_entry();
//                                    }
//                                }
//                                else
//                                {
//                                     throw new SystemErrorException(mName + 
//                                             "This else clause should be " +
//                                             "unreachable.");
//                                }
//                                break;
//
//                            case VARIABLE_LENGTH_LABEL:
//                                if ( next_tag_index > 1 )
//                                {
//                                    post_warning_message(
//                                            DUPLICATE_ALIST_ENTRY_WARN, 
//                                            "Duplicate VARIABLE-LENGTH> " +
//                                            "entry in an alignment a-list.\n");
//
//                                    if ( ! this.abort_parse ) 
//                                    {
//                                         parse_unknown_alist_entry();
//                                    }
//                                }
//                                else
//                                {
//                                    done = true;
//
//                                    post_error_message(
//                                        MISSING_OR_OUT_OF_ORDER_ALIST_ENTRY_ERR,
//                                        "TYPE> a-list entry missing from an " +
//                                        "alignment a-list?", false, true);
//                                }
//                                break;
//
//                            case FORMAL_ARG_LIST_LABEL:
//                                if ( next_tag_index > 2 )
//                                {
//                                    post_warning_message(
//                                            DUPLICATE_ALIST_ENTRY_WARN, 
//                                            "Duplicate FORMAL-ARG-LIST> " +
//                                            "entry in an alignment a-list.\n");
//
//                                    if ( ! this.abort_parse )
//                                    {
//                                         parse_unknown_alist_entry();
//                                    }
//                                }
//                                else
//                                {
//                                    done = true;
//
//                                    post_error_message(
//                                        MISSING_OR_OUT_OF_ORDER_ALIST_ENTRY_ERR,
//                                        "TYPE> or VARIABLE-LENGTH> a-list " +
//                                        "entry(s) missing from an alignment " +
//                                        "a-list?", false, true);
//                                }
//                                break;
//
//                            case COLUMN_WIDTH_LABEL:
//                                if ( next_tag_index > 3 )
//                                {
//                                    post_warning_message(
//                                            DUPLICATE_ALIST_ENTRY_WARN, 
//                                            "Duplicate COLUMN-WIDTH> entry " +
//                                            "in an alignment a-list.\n");
//
//                                    if ( ! this.abort_parse )
//                                    {
//                                        parse_unknown_alist_entry();
//                                    }
//                                }
//                                else
//                                {
//                                    done = true;
//
//                                    post_error_message(
//                                        MISSING_OR_OUT_OF_ORDER_ALIST_ENTRY_ERR,
//                                        "TYPE>, VARIABLE-LENGTH> or " +
//                                        "FORMAL-ARG-LIST> a-list entry(s) " +
//                                        "missing from an alignment a-list?",
//                                        false, true);
//                                }
//                                break;
//
//                            case CELLS_LABEL:
//                                if ( next_tag_index > 4 )
//                                {
//                                    post_warning_message(
//                                            DUPLICATE_ALIST_ENTRY_WARN, 
//                                            "Duplicate CELLS> entry in an " +
//                                            "alignment a-list.\n");
//
//                                    if ( ! this.abort_parse )
//                                    {
//                                        parse_unknown_alist_entry();
//                                    }
//                                }
//                                else
//                                {
//                                    done = true;
//
//                                    post_error_message(
//                                        MISSING_OR_OUT_OF_ORDER_ALIST_ENTRY_ERR,
//                                        "TYPE>, VARIABLE-LENGTH>, " +
//                                        "FORMAL-ARG-LIST> or COLUMN-WIDTH> " +
//                                        "a-list entry(s) missing from an " +
//                                        "alignment a-list?", false, true);
//                                }
//                                break;
//
//                            default:
//                                post_warning_message(
//                                        UNKNOWN_OR_UNEXPECTED_ALIST_ENTRY_WARN, 
//                                        "The entry is located in an " +
//                                        "alignment a-list.\n");
//
//                                if ( ! this.abort_parse )
//                                {
//                                     parse_unknown_alist_entry();
//                                }
//                                break;
//                        }
//                    }
//                }
//                else /* a-list contains a list that is not an a-list entry */
//                     /* read it & discard it                               */
//                {
//                    post_warning_message(
//                            NON_ALIST_ENTRY_LIST_IN_ALIST_WARN, 
//                            "The list is located in an alignment a-list.\n");
//
//                    if ( ! this.abort_parse )
//                    {
//                        parse_arbitrary_list();
//                    }
//                }
//            }
//            else if ( (this.l0_tok).code == R_PAREN_TOK )
//            {
//                done = true;
//                get_next_token();
//
//                if ( ( ! this.abort_parse ) && 
//                     ( next_tag_index < 5 ) )
//                {
//                    post_error_message(REQUIRED_ALIST_ENTRIES_MISSING_ERR,
//                        "Required entry(s) missing from an alignment a-list.",
//                        false, true);
//                }
//            }
//            else if ( (this.l0_tok).code == EOF_TOK )
//            {
//                done = true;
//                post_error_message(UNEXPECTED_END_OF_FILE_ERR,
//                                   "EOF occurred in an alignment a-list.\n",
//                                   true, true);
//            }
//            else /* (this.l0_tok).code isnt '(', ')', or EOF */
//            {
//                post_warning_message(NON_ALIST_ENTRY_ATOM_IN_ALIST_WARN, 
//                        "The atom was detected in an alignment a-list.\n");
//
//                if ( ! this.abort_parse )
//                {
//                    get_next_token();
//                }
//            }
//        }
//
//        return;
//
//    } /* macshapa_odb_reader::parse_alignment_var_alist() */
//
//
//    /*************************************************************************
//     *
//     * parse_group_list()
//     *
//     * This method parses a list of spreadsheet variable name, cell ord pairs.  
//     * This list is generated by the following productions:
//     *
//     *     <group_list --> '(' <group_member> (<group_member>)+ ')'
//     *
//     *     <group_member> --> '(' <s_var_name> <cell_ord> ')'
//     *
//     *     <cell_ord> --> <integer>
//     *
//     * For each spreadsheet variable name/cell ord pair, parse_group_member()
//     * attempts to look up the specified cell, and returns a pointer to it if it
//     * exists.  parse_group_list() uses these pointers to link all the cells
//     * mentioned in the group list into a single group.
//     *
//     *                                              - 6/28/08
//     *
//     * Parameters:
//     *
//     *    - None.
//     *
//     * Returns:  Void.
//     *
//     * Changes:
//     *
//     *    - None.
//     *
//     *************************************************************************/
//
//    private void parse_group_list()
//        throws SystemErrorException
//    {
//        final String mName = "macshapa_odb_reader::parse_group_list()";
//        boolean done;
//        VarCellPtr group_ptr;
//        VarCellPtr cell_ptr;
//
//        if ( this.abort_parse )
//        {
//            throw new SystemErrorException(mName + 
//                    "this.abort_parse TRUE on entry");
//        }
//        
//        /* parse the groups list */
//        {
//            /* first parse the leading left parenthesis */
//
//            if ( (this.l0_tok).code == L_PAREN_TOK )
//            {
//                get_next_token();
//            }
//            else /* we shouldn't have been called unless the next token is a '(' */
//            {
//                throw new SystemErrorException(mName + 
//                        "(this.l0_tok).code isnt L_PAREN_TOK.");
//            }
//
//            /* now parse the list of members of the group */
//
//            done      = false;
//            group_ptr = NULL; /* essential */
//            cell_ptr  = NULL; /* just on general principles */
//
//            while ( ( ! this.abort_parse ) && ( ! done ) )
//            {
//                switch ( (this.l0_tok).code )
//                {
//                    case R_PAREN_TOK:
//                        done = true;
//                        get_next_token();
//                        break;
//
//                    case L_PAREN_TOK:
//                        cell_ptr = parse_group_member();
//
//                        if ( ( cell_ptr != NULL ) && 
//                             ( ! this.abort_parse ) )
//                        {
//                            if ( group_ptr == NULL )
//                            {
//                                group_ptr = cell_ptr;
//                            }
//                            else
//                            {
//                                ((CSheetPane *)(this.spread_doc_ptr->itsMainPane))->
//                                    GroupCell(group_ptr, cell_ptr);
//                            }
//                        }
//                        break;
//
//                    case BOOL_TOK:
//                    case ERROR_TOK:
//                    case SYMBOL_TOK:
//                    case FLOAT_TOK:
//                    case STRING_TOK:
//                    case INT_TOK:
//                    case ALIST_LABEL_TOK:
//                    case PRIVATE_VAL_TOK:
//                    case SETF_TOK:
//                    case DB_VAR_TOK:
//                    case QUOTE_TOK:
//                        post_warning_message(ATOM_IN_GROUP_LIST_WARN, null); 
//
//                        if ( ! this.abort_parse )
//                        {
//                            get_next_token();
//                        }
//                        break;
//
//                    case EOF_TOK:
//                        post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                                "EOF in a group list in the SYSTEM> " +
//                                "GROUPS> attribute.\n", true, true); 
//                        break;
//
//                    default:
//                        throw new SystemErrorException(mName + 
//                                "Encountered unknown token type.");
//                        break;
//                 }
//             }
//        }
//
//        return;
//
//    } /* macshapa_odb_reader::parse_group_list() */
//
//
//    /*************************************************************************
//     *
//     * parse_group_member()
//     *
//     * This method parses a spreadsheet variable name, cell ord pair.  
//     * These pairs are generated by the following productions:
//     *
//     *     <group_member> --> '(' <s_var_name> <cell_ord> ')'
//     *
//     *     <cell_ord> --> <integer>
//     *
//     * The function attempts to look up the spreadsheet variable cell referenced
//     * by the spreadsheet variable / ord pair.  If successful, the function 
//     * returns a pointer to the varcell.  Otherwise the function returns NULL.
//     *
//     *                                              - 6/22/08
//     *
//     * Parameters:
//     *
//     *    - None.
//     *
//     * Returns:  Void.
//     *
//     * Changes:
//     *
//     *    - None.
//     *
//     *************************************************************************/
//
//    private VarCellPtr parse_group_member()
//
//    {
//        final String mName = "macshapa_odb_reader::parse_group_member()";
//        boolean done;
//        boolean excess_values;
//        boolean group_element_empty;
//        boolean have_ord;
//        VarOrd ord;
//        VarCellPtr cell_ptr;
//        Variable s_var_ptr;
//
//        cell_ptr  = NULL; /* essential */
//        s_var_ptr = NULL; /* just on general principles */
//
//        if ( this.abort_parse )
//        {
//            throw new SystemErrorException(mName + 
//                    "this.abort_parse TRUE on entry");
//        }
//        else /* parse the group member */
//        {
//            /* first parse the leading left parenthesis */
//
//            if ( (this.l0_tok).code == L_PAREN_TOK )
//            {
//                get_next_token();
//            }
//            else /* we shouldn't have been called unless the next token is a '(' */
//            {
//                throw new SystemErrorException(mName + 
//                        "(this.l0_tok).code isnt L_PAREN_TOK.");
//            }
//
//
//            /* next, parse the spreadsheet variable name */
//
//            group_element_empty = false;
//
//            if ( ! this.abort_parse )
//            {
//                switch ( (this.l0_tok).code )
//                {
//                    case SYMBOL_TOK:
//                        if ( ((this.l0_tok).aux & COLUMN_FLAG ) == 0 )
//                        {
//                            post_warning_message(
//                                    INVALID_S_VAR_NAME_IN_GROUP_MEMBER_WARN, 
//                                    "Will coerce the name to a valid " +
//                                    "spreadsheet variable name.\n");
//
//                            if ( ! this.abort_parse )
//                            {
//                                coerce_symbol_token_to_spreadsheet_variable_name
//                                    (&(this.l0_tok));
//                            }
//                        }
//
//                        /* get pointer to the spreadsheet variable.  The 
//                         * spreadsheet variable should already have been 
//                         * defined.  If not, lookupVar() will return  NULL.
//                         */
//
//                        s_var_ptr = this.spread_doc_ptr->SymTable->
//                            lookupVar((this.l0_tok).str_ptr);
//
//                        if ( s_var_ptr == NULL )
//                        {
//                            post_warning_message(
//                                    REF_TO_UNDEF_S_VAR_IN_GROUP_MEMBER_WARN, 
//                                    "The group member will be ignored.\n");
//                        }
//                        else if ( s_var_ptr->system )
//                        {
//                            s_var_ptr = NULL;
//
//                            post_warning_message(
//                                    REF_TO_SYS_S_VAR_IN_GROUP_MEMBER_WARN, 
//                                    "Since cells in system spreadsheet " +
//                                    "variables cannot be members of groups, " +
//                                    "the entry will be ignored.\n");
//                        }
//
//                        if ( ! this.abort_parse )
//                        {
//                            get_next_token();
//                        }
//                        break;
//
//                    case R_PAREN_TOK:
//                        group_element_empty = TRUE; /* to keep us from issuing confusing warning messages */
//
//                        post_warning_message(ILL_FORMED_GROUP_MEMBER_WARN, 
//                            "The group member is an empty list.\n"); 
//
//                        /* don't eat the right parenthesis now -- save it for 
//                         * later so we don't get confused. 
//                         */
//
//                        break;
//
//                    case L_PAREN_TOK:
//                        post_warning_message(
//                                ILL_FORMED_GROUP_MEMBER_WARN, 
//                                "The <s_var_name> element in the group " +
//                                "member is a list, instead of the required " +
//                                "spreadsheet variable name.\n"); 
//
//                        if ( ! this.abort_parse )
//                        {
//                            parse_arbitrary_list();
//                        }
//                        break;
//
//                    case BOOL_TOK:
//                    case ERROR_TOK:
//                    case FLOAT_TOK:
//                    case STRING_TOK:
//                    case INT_TOK:
//                    case ALIST_LABEL_TOK:
//                    case PRIVATE_VAL_TOK:
//                    case SETF_TOK:
//                    case DB_VAR_TOK:
//                    case QUOTE_TOK:
//                        post_warning_message(ILL_FORMED_GROUP_MEMBER_WARN, 
//                                "The <s_var_name> element in the group " +
//                                "member must be an atom of symbol type.\n"); 
//
//                        if ( ! this.abort_parse )
//                        {
//                            get_next_token();
//                        }
//                        break;
//
//                    case EOF_TOK:
//                        post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                                "EOF in a group member in the value of the " +
//                                "SYSTEM> GROUP> attribute.\n", true, true); 
//                        break;
//
//                    default:
//                        throw new SystemErrorException(mName + 
//                                "Encountered unknown token type.");
//                        break;
//                }
//            }
//
//
//            /* now, parse the ord */
//
//            have_ord = false;
//
//            if ( ! this.abort_parse )
//            {
//                switch ( (this.l0_tok).code )
//                {
//                    case INT_TOK:
//                        ord = (this.l0_tok).val;
//
//                        if ( ord > 0 )
//                        {
//                            have_ord = TRUE;
//                        }
//                        else
//                        {
//                            post_warning_message(ILL_FORMED_GROUP_MEMBER_WARN, 
//                                "The <cell_ord> must be a positive integer.\n"); 
//                        }
//
//                        if ( ! this.abort_parse )
//                        {
//                            get_next_token();
//                        }
//                        break;
//
//                    case R_PAREN_TOK:
//                        if ( ! group_element_empty )
//                        {
//                            post_warning_message(ILL_FORMED_GROUP_MEMBER_WARN, 
//                                    "The <cell_ord> element of the group " +
//                                    "member is missing.\n");
//                        } 
//                        break;
//
//                    case L_PAREN_TOK:
//                        post_warning_message(ILL_FORMED_GROUP_MEMBER_WARN, 
//                                "The <cell_ord> element must be a positive " +
//                                "integer, not a list.\n"); 
//
//                        if ( ! this.abort_parse )
//                        {
//                            parse_arbitrary_list();
//                        }
//                        break;
//
//                    case SYMBOL_TOK:
//                    case BOOL_TOK:
//                    case ERROR_TOK:
//                    case FLOAT_TOK:
//                    case STRING_TOK:
//                    case ALIST_LABEL_TOK:
//                    case PRIVATE_VAL_TOK:
//                    case SETF_TOK:
//                    case DB_VAR_TOK:
//                    case QUOTE_TOK:
//                        post_warning_message(ILL_FORMED_GROUP_MEMBER_WARN, 
//                                "The <cell_ord> element in the group member " +
//                                "must be a positive integer, not an atom of " +
//                                "non-integer type.\n"); 
//
//                        if ( ! this.abort_parse )
//                        {
//                            get_next_token();
//                        }
//                        break;
//
//                    case EOF_TOK:
//                        post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                                "EOF in a group member in the value of the " +
//                                "SYSTEM> GROUP> attribute.\n", true, true); 
//                        break;
//
//                     default:
//                         throw new SystemErrorException(mName + 
//                                 "Encountered unknown token type.");
//                         break;
//                }
//            }
//
//
//            /* assuming we have both a spreadsheet variable pointer and a varcell ord,  */
//            /* look up the varcell now, so that if we fail, we can generate the warning */
//            /* message closer to the actual point of failure.                           */
//
//            if ( ( ! this.abort_parse ) && 
//                 ( s_var_ptr != NULL ) && ( have_ord ) )
//            {
//                cell_ptr = s_var_ptr->GetVarCellByOrd(ord);
//
//                if ( cell_ptr == NULL ) /* reference to non-existant cell */
//                {
//                    post_warning_message(REF_TO_UNDEF_S_VAR_CELL_IN_GRP_MEM_WARN, 
//                            "The group member will be ignored.\n"); 
//                }
//                else if ( s_var_ptr != cell_ptr->itsVar )
//                {
//                    cell_ptr = NULL;
//                    throw new SystemErrorException(mName + 
//                            "s_var_ptr appears to be corrupt.");
//                }
//            }
//
//
//            /* discard any excess values & consume the closing right parenthesis */
//
//            if ( ! this.abort_parse )
//            {
//                done = false;
//                excess_values = false;
//
//                while ( ( ! this.abort_parse ) && 
//                        ( ! done ) )
//                 {
//                     switch ( (this.l0_tok).code )
//                     {
//                        case R_PAREN_TOK:
//                            done = true;
//                            get_next_token();
//                            break;
//
//                        case L_PAREN_TOK:
//                            excess_values = true;
//                            parse_arbitrary_list();
//                            break;
//
//                        case ERROR_TOK:
//                        case SYMBOL_TOK:
//                        case INT_TOK:
//                        case FLOAT_TOK:
//                        case STRING_TOK:
//                        case BOOL_TOK:
//                        case ALIST_LABEL_TOK:
//                        case PRIVATE_VAL_TOK:
//                        case SETF_TOK:
//                        case DB_VAR_TOK:
//                        case QUOTE_TOK:
//                            excess_values = true;
//                            get_next_token();
//                            break;
//
//                        case EOF_TOK:
//                            done = true;
//                            post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                                    "EOF in a group member in the value of " +
//                                    "the SYSTEM> GROUP> attribute.\n",
//                                    true, true);
//                            break;
//
//                        default:
//                            throw new SystemErrorException(mName + 
//                                    "Encountered unknown token type.");
//                            break;
//                     }
//                }
//
//                if ( ( ! this.abort_parse ) &&
//                     ( done ) && ( excess_values ) )
//                {
//                    post_warning_message(EXCESS_VALUES_IN_GROUP_MEMBER_WARN,
//                                         null);
//                }
//            }
//        }
//
//        return(cell_ptr);
//
//    } /* macshapa_odb_reader::parse_group_member() */
//
//
//    /*******************************************************************************
//     *
//     * parse_groups_attribute()
//     *
//     * This method parses a GROUPS> attribute, which is generated by 
//     * the following productions:
//     * 
//     *     <groups_attribute> --> '(' 'GROUPS>' <groups_list> ')'
//     *
//     *     <groups_list> --> '(' (<group_list>)* ')'
//     *
//     *     <group_list --> '(' <group_member> (<group_member>)+ ')'
//     *
//     *     <group_member> --> '(' <s_var_name> <cell_ord> ')'
//     *
//     *     <cell_ord> --> <integer>
//     *
//     * The purpose of this attribute is to support the grouping of spreadsheet
//     * variable cells.  However, the actual construction of the groups is done
//     * in parse_group_list().
//     *
//     *                                              - 6/20/08
//     *
//     * Parameters:
//     *
//     *    - None
//     *
//     * Returns:  Void.
//     *
//     * Changes:
//     *
//     *    - None.
//     *
//     *******************************************************************************/
//
//    private void parse_groups_attribute()
//        throws SystemErrorException
//    {
//        final String mName = "macshapa_odb_reader::parse_groups_attribute()";
//        final String missing_groups_list_mssg = 
//                "The GROUPS> attribute appears not to contain a value.  " +
//                "The attribute will be ignored.\n";
//        final String groups_list_type_mismatch_mssg = 
//                "The value of a GROUPS> attribute must be a list.  " +
//                "The attribute will be ignored.\n";
//
//        if ( this.abort_parse )
//        {
//            throw new SystemErrorException(mName + 
//                    "this.abort_parse TRUE on entry");
//        }
//        
//        
//        /* parse the groups attribute */
//        
//        /* first parse the leading left parenthesis */
//
//        if ( (this.l0_tok).code == L_PAREN_TOK )
//        {
//            get_next_token();
//        }
//        else /* we shouldn't have been called unless the next token is a '(' */
//        {
//            throw new SystemErrorException(mName + 
//                    "(this.l0_tok).code != L_PAREN_TOK.");
//        }
//
//        /* read the a-list entry name */
//        if ( ! this.abort_parse )
//        {
//            if ( ( (this.l0_tok).code == ALIST_LABEL_TOK ) && 
//                 ( (this.l0_tok).aux == GROUPS_LABEL ) )
//            {
//                get_next_token();
//            }
//            else /* we shouldn't have been called unless the next token is GROUPS> */
//            {
//                throw new SystemErrorException(mName + 
//                        "this.l0_tok isnt GROUPS>.");
//            }
//        }
//
//        /* read the value associated with the a-list entry & discard any 
//         * excess values 
//         */
//        if ( ! this.abort_parse )
//        {
//            switch ( (this.l0_tok).code )
//            {
//                case R_PAREN_TOK:
//                    post_warning_message(EMPTY_ALIST_ENTRY_WARN, 
//                                         missing_groups_list_mssg); 
//                    break;
//
//                case L_PAREN_TOK:
//                    parse_groups_list();
//                    break;
//
//                case BOOL_TOK:
//                case ERROR_TOK:
//                case SYMBOL_TOK:
//                case FLOAT_TOK:
//                case STRING_TOK:
//                case INT_TOK:
//                case ALIST_LABEL_TOK:
//                case PRIVATE_VAL_TOK:
//                case SETF_TOK:
//                case DB_VAR_TOK:
//                case QUOTE_TOK:
//                    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
//                                         groups_list_type_mismatch_mssg); 
//
//                    if ( ! this.abort_parse )
//                    {
//                        get_next_token();
//                    }
//                    break;
//
//                case EOF_TOK:
//                    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                            "EOF in the SYSTEM> GROUPS> attribute.\n", 
//                            true, true);
//                    break;
//
//                default:
//                    throw new SystemErrorException(mName + 
//                            "Encountered unknown token type.");
//                    break;
//            }
//        }
//
//        /* check for EOF */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code == EOF_TOK )
//            {
//                post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                                   "EOF in the SYSTEM> GROUPS> attribute.\n",  
//                                   true, true);
//            }
//        }
//
//        /* discard any excess values that may appear in the GROUPS> 
//         * a-list entry 
//         */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code != R_PAREN_TOK )
//            {
//                discard_excess_alist_entry_values();
//
//                if ( ! this.abort_parse )
//                {
//                    post_warning_message(EXCESS_VALUES_IN_ALIST_ENTRY_WARN,
//                            "Excess values encountered the SYSTEM> GROUPS> " +
//                            "attribute.\n");
//                }
//            }
//        }
//
//        /* read the terminating right parenthesis */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code == R_PAREN_TOK )
//            {
//                get_next_token();
//            }
//            else
//            {
//                /* since we are cleaning up any excess values in the groups 
//                 * attripute, this else clause is unreachable at present. 
//                 * Should we choose to drop the above attempt at error recovery, 
//                 * this clause will again become reachable.
//                 */
//
//                post_warning_message(RIGHT_PAREN_EXPECTED_WARN, 
//                        "Closing parenthesis missing from the SYSTEM> " +
//                        "GROUPS> attribute.\n");
//            }
//        }
//
//        return;
//
//    } /* macshapa_odb_reader::parse_groups_attribute() */
//
//
//    /*************************************************************************
//     *
//     * parse_groups_list()
//     *
//     * This function parses a list of lists of spreadsheet variable name, cell
//     * ord pairs.  This list is generated by the following productions:
//     * 
//     *     <groups_list> --> '(' (<group_list>)* ')'
//     *
//     *     <group_list --> '(' <group_member> (<group_member>)+ ')'
//     *
//     *     <group_member> --> '(' <s_var_name> <cell_ord> ')'
//     *
//     *     <cell_ord> --> <integer>
//     *
//     * The purpose of this list is to support the groups feature in MacSHAPA.
//     * However the actual construction of groups is done in 
//     * parse_group_list().
//     *
//     *                                              - 6/29/08
//     *
//     * Parameters:
//     *
//     *    - None
//     *
//     * Returns:  Void.
//     *
//     * Changes:
//     *
//     *    - None.
//     *
//     *************************************************************************/
//
//    private void parse_groups_list()
//        throws SystemErrorException
//    {
//        final String mName = "macshapa_odb_reader::parse_groups_list()";
//        boolean done;
//
//        if ( this.abort_parse )
//        {
//            throw new SystemErrorException(mName + 
//                    "this.abort_parse TRUE on entry");
//        }
//        
//        /* parse the groups list */
//        
//        /* first parse the leading left parenthesis */
//
//        if ( (this.l0_tok).code == L_PAREN_TOK )
//        {
//            get_next_token();
//        }
//        else /* we shouldn't have been called unless the next token is a '(' */
//        {
//            throw new SystemErrorException(mName + 
//                    "(this.l0_tok).code isnt L_PAREN_TOK.");
//        }
//
//        /* now read the groups list */
//
//        done = FALSE;
//
//        while ( ( ! this.abort_parse ) && ( ! done ) )
//        {
//            switch ( (this.l0_tok).code )
//            {
//                case R_PAREN_TOK:
//                    done = TRUE;
//                    get_next_token();
//                    break;
//
//                case L_PAREN_TOK:
//                    parse_group_list();
//                    break;
//
//                case BOOL_TOK:
//                case ERROR_TOK:
//                case SYMBOL_TOK:
//                case FLOAT_TOK:
//                case STRING_TOK:
//                case INT_TOK:
//                case ALIST_LABEL_TOK:
//                case PRIVATE_VAL_TOK:
//                case SETF_TOK:
//                case DB_VAR_TOK:
//                case QUOTE_TOK:
//                    post_warning_message(ATOM_IN_GROUPS_LIST_WARN, 
//                                               NULL); 
//
//                    if ( ! this.abort_parse )
//                    {
//                        get_next_token();
//                    }
//                    break;
//
//                case EOF_TOK:
//                    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                            "EOF in the groups list of the SYSTEM> " +
//                            "GROUPS> attribute.\n", true, true);
//                    break;
//
//                 default:
//                     throw new SystemErrorException(mName + 
//                             "Encountered unknown token type.");
//                     break;
//             }
//        }
//
//        return;
//
//    } /* macshapa_odb_reader::parse_groups_list() */
//
//
//    /*************************************************************************
//     *
//     * parse_imp_format_list()
//     *
//     * This method parses an import format list.  This list is generated
//     * by the following productions:
//     *
//     *     <imp_format_list> --> 
//     *             '(' <imp_format_list_name> <imp_format_list_alist> ')'
//     *
//     *     <imp_format_list_name> --> <non_blank_text_quote_string>
//     *
//     *     <imp_format_list_alist> --> '(' (<imp_format_attributes>) ')'
//     *
//     *     <imp_format_attributes> --> { <imp_prods_attribute> }
//     *
//     *     <imp_prods_attribute> --> 
//     *             '(' 'IMPORT-PRODUCTION-LIST>' <imp_prod_list> ')'
//     *
//     *     <imp_prod_list> --> '(' (<imp_prod>)* ')'
//     *
//     *     <imp_prod> --> '(' <imp_prod_alist> ')'
//     *
//     *     <imp_prod_alist> --> { <imp_prod_name_attribute>
//     *                            <imp_prod_pattern_attribute>
//     *                            <imp_prod_hard_prec_attribute>
//     *                            <imp_prod_is_shown_attribute>
//     *                            <imp_prod_is_traced_attribute>
//     *                            ( <imp_prod_insert_actions_attribute> |
//     *                              <imp_prod_program_actions_attribute> ) }
//     *
//     *     <imp_prod_name_attribute> --> '(' 'NAME>' <text_quote_string> ')'
//     *
//     *     <imp_prod_pattern_attribute> --> '(' 'PATTERN>' <text_quote_string> ')'
//     *
//     *     <imp_prod_hard_prec_attribute> --> '(' 'HARD-PREC>' <integer> ')'
//     *
//     *     <imp_prod_is_shown_attribute> --> '(' 'IS-SHOWN>' <boolean> ')'
//     *
//     *     <imp_prod_is_traced_attribute> --> '(' 'IS-TRACED>' <boolean> ')'
//     *
//     *     <imp_prod_insert_actions_attribute> --> 
//     *             '(' 'INSERT-ACTIONS>' <imp_prod_insert_actions_list> ')'
//     *
//     *     <imp_prod_insert_actions_list> --> 
//     *             '(' (<imp_prod_insert_action_alist>)* ')'
//     *
//     *     <imp_prod_insert_action_alist> --> 
//     *             '(' <imp_prod_insert_action_attributes> ')'
//     *
//     *     <imp_prod_insert_action_attributes> --> 
//     *             ( <imp_prod_insert_action_var_attribute> 
//     *               <imp_prod_insert_action_onset_attribute> 
//     *               <imp_prod_insert_action_offset_attribute> 
//     *               <imp_prod_insert_action_val_attribute> )
//     *
//     *     <imp_prod_insert_action_var_attribute> --> 
//     *             '(' 'VAR>' <text_quote_string> ')'
//     *
//     *     <imp_prod_insert_action_onset_attribute> --> 
//     *             '(' 'ONSET>' <text_quote_string> ')'
//     *
//     *     <imp_prod_insert_action_offset_attribute> --> 
//     *             '(' 'OFFSET>' <text_quote_string> ')'
//     *
//     *     <imp_prod_insert_action_val_attribute> --> 
//     *             '(' 'VAL>' <text_quote_string> ')'
//     *
//     *     <imp_prod_program_actions_attribute> --> 
//     *             '(' 'PROGRAM-ACTIONS>' <imp_prod_program_actions_list> ')'
//     *
//     *     <imp_prod_program_actions_list> --> 
//     *             '(' (<imp_prod_program_action_alist>)* ')'
//     *
//     *     <imp_prod_program_action_alist> --> 
//     *             '(' <imp_prod_program_action_attributes> ')'
//     *
//     *     <imp_prod_program_action_attributes> --> 
//     *             ( <imp_prod_program_action_text_attribute> )
//     *
//     *     <imp_prod_program_action_text_attribute> -->
//     *             '(' 'TEXT>' <text_quote_string> ')' 
//     *
//     * Note that this method adds formats to the list pointed to by 
//     * this.import_format_list_root instead of adding them directly to 
//     * this.spread_doc_ptr->itsFormatList.  We do this so that we can 
//     * read import formats with this code even when this.spread_doc_ptr
//     * is undefined (i.e. NULL).  This allows us to load the formats from a 
//     * database file without loading the rest of the database as well.
//     *
//     *                                              - 10/31/95
//     *
//     * Parameters:
//     *
//     *    - None.
//     *
//     * Returns:  Void.
//     *
//     * Changes:
//     *
//     *    - None.
//     *
//     *************************************************************************/
//
//    private void parse_imp_format_list()
//        throws SystemErrorException
//    {
//        final String mName = "macshapa_odb_reader::parse_imp_format_list()";
//        boolean discard;
//        boolean done;
//        boolean excess_values;
//        FormatPtr format_ptr;
//        ProdPtr prod_ptr;
//        ActionPtr action_ptr;
//
//        format_ptr = null; /* on general principles */
//        prod_ptr   = null; /* on general principles */
//        action_ptr = null; /* on general principles */
//
//        if ( this.abort_parse )
//        {
//            throw new SystemErrorException(mName + 
//                    "this.abort_parse TRUE on entry");
//        }
//        
//        /* parse the import format list */
//        
//        /* first parse the leading left parenthesis */
//
//        if ( (this.l0_tok).code == L_PAREN_TOK )
//        {
//            get_next_token();
//        }
//        else /* we shouldn't have been called unless the next token is a '(' */
//        {
//            throw new SystemErrorException(mName + 
//                    "(this.l0_tok).code isnt L_PAREN_TOK.");
//        }
//
//        /* now read the import format name & allocate the new format data structure */
//        if ( ! this.abort_parse )
//        {
//            if ( ( (this.l0_tok).code == STRING_TOK ) && 
//                 ( (((this.l0_tok).aux) & TEXT_QSTRING_FLAG) != 0 ) &&
//                 ( (((this.l0_tok).aux) & NONBLANK_FLAG) != 0 ) )
//            {
//                format_ptr = allocFormat();
//
//                if ( format_ptr == NULL )
//                {
//                    this.proceed = FALSE;
//                    error3("SYSTEM ERROR", "attempt to allocate format failed.", fcnNamePtr);
//                }
//                else
//                {
//                    format_ptr->name = saveCstr(strTrim((this.l0_tok).str_ptr));
//
//                    if ( ( format_ptr->name == NULL ) || ( strlen(format_ptr->name) == 0 ) )
//                    {
//                        this.proceed = FALSE;
//                        error3("SYSTEM ERROR", "Error saving format name.", fcnNamePtr);
//                    }
//                }
//
//                if ( ! this.abort_parse )
//                {
//                    get_next_token();
//                }
//            }
//            else /* we shouldn't have been called unless the import format name was valid */
//            {
//                throw new SystemErrorException(mName + 
//                        "this.l0_tok isnt a valid format name.");
//            }
//        }
//
//        /* read the a-list associated with the import format list */
//
//        discard = FALSE;
//
//        if ( ! this.abort_parse )
//        {
//            switch ( (this.l0_tok).code )
//            {
//                case R_PAREN_TOK:
//                    discard = TRUE;
//                    post_warning_message(EMPTY_IMPORT_FORMAT_LIST_WARN, null); 
//                    break;
//
//                case L_PAREN_TOK:
//                    parse_imp_format_list_alist(format_ptr);
//                    break;
//
//                case BOOL_TOK:
//                case ERROR_TOK:
//                case SYMBOL_TOK:
//                case FLOAT_TOK:
//                case STRING_TOK:
//                case INT_TOK:
//                case ALIST_LABEL_TOK:
//                case PRIVATE_VAL_TOK:
//                case SETF_TOK:
//                case DB_VAR_TOK:
//                case QUOTE_TOK:
//                    discard = true;
//                    post_warning_message(IMPORT_FORMAT_LIST_TYPE_MISMATCH_WARN, 
//                                         null); 
//
//                    if ( ! this.abort_parse )
//                    {
//                        get_next_token();
//                    }
//                    break;
//
//                case EOF_TOK:
//                    discard = true;
//                    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                            "EOF in an import format list in the SYSTEM> " +
//                            "IMPORT-FORMAT-LISTS> attribute.\n", true, true); 
//                    break;
//
//                 default:
//                     throw new SystemErrorException(mName + 
//                             "Encountered unknown token type.");
//                     break;
//            }
//        }
//
//
//        if ( ( ! discard ) && 
//             ( format_ptr != null ) && ( format_ptr->prods == null ) )
//        {
//            /* Encountered an import format that doesn't contain any productions. */
//            /* This gives the import code a slight belly ache, so we add an empty */
//            /* insert production here.                                            */
//
//            prod_ptr = allocProd();
//
//            if ( prod_ptr == NULL )
//            {
//                this.proceed = FALSE;
//                error3("SYSTEM ERROR", "allocProd() returned NULL.", fcnNamePtr);
//            }
//            else
//            {
//                prod_ptr->name     = saveCstr("");
//                prod_ptr->pattern  = saveCstr("");
//                prod_ptr->hardPrec = 0;
//                prod_ptr->isTraced = FALSE;
//                prod_ptr->isShown  = TRUE;
//
//                prod_ptr->actionKind = INSERT_ACTION;
//
//                action_ptr = allocAction();
//
//                if ( action_ptr == NULL )
//                {
//                    this.proceed = FALSE;
//                    error3("SYSTEM ERROR", "Action allocation failed.", fcnNamePtr);
//                }
//                else
//                {
//                    action_ptr->kind            = INSERT_ACTION;
//                    action_ptr->u.insert.var    = saveCstr("");
//                    action_ptr->u.insert.onset  = saveCstr("");
//                    action_ptr->u.insert.offset = saveCstr("");
//                    action_ptr->u.insert.val    = saveCstr("");
//
//                    prod_ptr->actions = AddActionListEnd(prod_ptr->actions, action_ptr);
//
//                    format_ptr->prods = AddProdListEnd(format_ptr->prods, prod_ptr);
//                }
//            }
//        }
//
//        /* add the format to the list of formats or discard it as appropriate */
//        if ( format_ptr != NULL )
//        {
//             if ( discard )
//             {
//                 freeFormat(format_ptr);
//             }
//             else /* add the new format to the formats list */
//             {
//                this.import_format_list_root = 
//                    AddFormatListEnd(this.import_format_list_root, format_ptr);
//             }
//        }
//
//        /* discard any excess values & consume the closing right parenthesis */
//
//        if ( ! this.abort_parse )
//        {
//            done = false;
//            excess_values = false;
//
//            while ( ( ! this.abort_parse ) && 
//                    ( ! done ) )
//            {
//                switch ( (this.l0_tok).code )
//                {
//                    case R_PAREN_TOK:
//                        done = true;
//                        get_next_token();
//                        break;
//
//                    case L_PAREN_TOK:
//                        excess_values = true;
//                        parse_arbitrary_list();
//                        break;
//
//                    case ERROR_TOK:
//                    case SYMBOL_TOK:
//                    case INT_TOK:
//                    case FLOAT_TOK:
//                    case STRING_TOK:
//                    case BOOL_TOK:
//                    case ALIST_LABEL_TOK:
//                    case PRIVATE_VAL_TOK:
//                    case SETF_TOK:
//                    case DB_VAR_TOK:
//                    case QUOTE_TOK:
//                        excess_values = true;
//                        get_next_token();
//                        break;
//
//                    case EOF_TOK:
//                        done = TRUE;
//                        post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                                "EOF in an import format list in the SYSTEM> " +
//                                "IMPORT-FORMAT-LISTS> attribute.\n", 
//                                true, true);
//                        break;
//
//                    default:
//                        throw new SystemErrorException(mName + 
//                                "Encountered unknown token type.");
//                        break;
//                }
//            }
//
//            if ( ( ! this.abort_parse ) &&
//                 ( done ) && ( excess_values ) )
//            {
//                post_warning_message(EXCESS_VALUES_IN_IMP_FORMAT_LIST_WARN,
//                                     null);
//            }
//        }
//
//        return;
//
//    } /* macshapa_odb_reader::parse_imp_format_list() */
//
//
//    /*************************************************************************
//     *
//     * parse_imp_format_list_alist()
//     *
//     * This function parses an import format list attribute list.  This list is 
//     * generated by the following productions:
//     *
//     *     <imp_format_list_alist> --> '(' (<imp_format_attributes>) ')'
//     *
//     *     <imp_format_attributes> --> { <imp_prods_attribute> }
//     *
//     *     <imp_prods_attribute> --> 
//     *             '(' 'IMPORT-PRODUCTION-LIST>' <imp_prod_list> ')'
//     *
//     *     <imp_prod_list> --> '(' (<imp_prod>)* ')'
//     *
//     *     <imp_prod> --> '(' <imp_prod_alist> ')'
//     *
//     *     <imp_prod_alist> --> { <imp_prod_name_attribute>
//     *                            <imp_prod_pattern_attribute>
//     *                            <imp_prod_hard_prec_attribute>
//     *                            <imp_prod_is_shown_attribute>
//     *                            <imp_prod_is_traced_attribute>
//     *                            ( <imp_prod_insert_actions_attribute> |
//     *                              <imp_prod_program_actions_attribute> ) }
//     *
//     *     <imp_prod_name_attribute> --> '(' 'NAME>' <text_quote_string> ')'
//     *
//     *     <imp_prod_pattern_attribute> --> '(' 'PATTERN>' <text_quote_string> ')'
//     *
//     *     <imp_prod_hard_prec_attribute> --> '(' 'HARD-PREC>' <integer> ')'
//     *
//     *     <imp_prod_is_shown_attribute> --> '(' 'IS-SHOWN>' <boolean> ')'
//     *
//     *     <imp_prod_is_traced_attribute> --> '(' 'IS-TRACED>' <boolean> ')'
//     *
//     *     <imp_prod_insert_actions_attribute> --> 
//     *             '(' 'INSERT-ACTIONS>' <imp_prod_insert_actions_list> ')'
//     *
//     *     <imp_prod_insert_actions_list> --> 
//     *             '(' (<imp_prod_insert_action_alist>)* ')'
//     *
//     *     <imp_prod_insert_action_alist> --> 
//     *             '(' <imp_prod_insert_action_attributes> ')'
//     *
//     *     <imp_prod_insert_action_attributes> --> 
//     *             ( <imp_prod_insert_action_var_attribute> 
//     *               <imp_prod_insert_action_onset_attribute> 
//     *               <imp_prod_insert_action_offset_attribute> 
//     *               <imp_prod_insert_action_val_attribute> )
//     *
//     *     <imp_prod_insert_action_var_attribute> --> 
//     *             '(' 'VAR>' <text_quote_string> ')'
//     *
//     *     <imp_prod_insert_action_onset_attribute> --> 
//     *             '(' 'ONSET>' <text_quote_string> ')'
//     *
//     *     <imp_prod_insert_action_offset_attribute> --> 
//     *             '(' 'OFFSET>' <text_quote_string> ')'
//     *
//     *     <imp_prod_insert_action_val_attribute> --> 
//     *             '(' 'VAL>' <text_quote_string> ')'
//     *
//     *     <imp_prod_program_actions_attribute> --> 
//     *             '(' 'PROGRAM-ACTIONS>' <imp_prod_program_actions_list> ')'
//     *
//     *     <imp_prod_program_actions_list> --> 
//     *             '(' (<imp_prod_program_action_alist>)* ')'
//     *
//     *     <imp_prod_program_action_alist> --> 
//     *             '(' <imp_prod_program_action_attributes> ')'
//     *
//     *     <imp_prod_program_action_attributes> --> 
//     *             ( <imp_prod_program_action_text_attribute> )
//     *
//     *     <imp_prod_program_action_text_attribute> -->
//     *             '(' 'TEXT>' <text_quote_string> ')' 
//     *
//     *                                              - 6/29/08
//     *
//     * Parameters:
//     *
//     * format_ptr:  Pointer to an instance of the Format structure, whose
//     *     type definition may be found in import.h.  format_ptr points
//     *     to the instance of format into which we will load the import 
//     *     productions as we read them. 
//     *
//     * Returns:  Void.
//     *
//     * Changes:
//     *
//     *    - None.
//     *
//     *************************************************************************/
//
//    private void parse_imp_format_list_alist(FormatPtr format_ptr)
//        throws SystemErrorException
//    {
//        final String mName = 
//                "macshapa_odb_reader::parse_imp_format_list_alist()";
//        boolean done;
//        boolean have_import_production_list;
//
//        if ( this.abort_parse )
//        {
//            throw new SystemErrorException(mName + 
//                    "this.abort_parse TRUE on entry");
//        }
//        
//        if ( ( format_ptr == null ) || ( format_ptr->name == null ) || 
//             ( format_ptr->prods != null ) || ( format_ptr->next != null ) )
//        {
//            throw new SystemErrorException(mName + 
//                    "format_ptr seems to be corrupt on entry");
//        }
//        
//        /* parse the a-list */
//        
//        /* first parse the leading left parenthesis */
//
//        if ( (this.l0_tok).code == L_PAREN_TOK )
//        {
//            if ( ( (this.l1_tok).code == L_PAREN_TOK ) || 
//                 ( (this.l1_tok).code == R_PAREN_TOK ) )
//            {
//                get_next_token();
//            }
//            else if ( (this.l1_tok).code == ALIST_LABEL_TOK )
//            {
//                post_warning_message(LEFT_PAREN_EXPECTED_WARN, 
//                        "The opening left parenthesis of an import " +
//                        "format list a-list appears to be missing.\n");
//            }
//            else 
//            {
//                /* if a left paren is missing, the first item in the a-list is 
//                 * not an a-list entry.  If we try to recover from this error 
//                 * here, we will only confuse things further.  Thus we eat the 
//                 * left parenthesis & let the cards fall where they may.
//                 */
//
//                get_next_token();
//            }
//        }
//        else /* we shouldn't have been called unless the next token is a '(' */
//        {
//            throw new SystemErrorException(mName + 
//                    "(this.l0_tok).code isnt L_PAREN_TOK.");
//        }
//
//        done                        = false;
//        have_import_production_list = false;
//
//        /* now parse the a-list assocated with the import format list */
//        while ( ( ! this.abort_parse ) && 
//                ( ! done ) )
//        {
//            if ( (this.l0_tok).code == L_PAREN_TOK )
//            {
//                if ( (this.l1_tok).code == ALIST_LABEL_TOK )
//                {
//                    switch ( (this.l1_tok).aux )
//                    {
//                        case IMPORT_PRODUCTION_LIST_LABEL:
//                            if ( ! have_import_production_list )
//                            {
//                                have_import_production_list = true;
//                                parse_imp_prods_attribute(format_ptr);
//                            }
//                            else
//                            {
//                                post_warning_message(DUPLICATE_ALIST_ENTRY_WARN, 
//                                        "Duplicate IMPORT-PRODUCTION-LIST> entry " +
//                                        "in an import format list a-list.\n");
//
//                                if ( ! this.abort_parse )
//                                {
//                                     parse_unknown_alist_entry();
//                                }
//                            }
//                            break;
//
//                        default:
//                            post_warning_message(
//                                    UNKNOWN_OR_UNEXPECTED_ALIST_ENTRY_WARN, 
//                                    "The entry is located in an import format " +
//                                    "list a-list.\n");
//
//                            if ( ! this.abort_parse )
//                            {
//                                parse_unknown_alist_entry();
//                            }
//                            break;
//                    }
//                }
//                else /* a-list contains a list that is ! an a-list entry. */
//                     /* read it & discard it.                               */
//                {
//                    post_warning_message(NON_ALIST_ENTRY_LIST_IN_ALIST_WARN, 
//                            "The list is located in an import format list a-list.\n");
//
//                    if ( ! this.abort_parse )
//                    {
//                         parse_arbitrary_list();
//                    }
//                }
//            }
//            else if ( (this.l0_tok).code == R_PAREN_TOK )
//            {
//                done = true;
//                get_next_token();
//            }
//            else if ( (this.l0_tok).code == EOF_TOK )
//            {
//                done = true;
//                post_error_message(UNEXPECTED_END_OF_FILE_ERR,
//                       "EOF occurred in an import format list a-list.\n",
//                       true, true);
//            }
//            else /* (this.l0_tok).code isnt '(', ')', or EOF */
//            {
//                post_warning_message(NON_ALIST_ENTRY_ATOM_IN_ALIST_WARN, 
//                        "The atom was detected in an import format list a-list.\n");
//
//                if ( ! this.abort_parse )
//                {
//                    get_next_token();
//                }
//            }
//        } /* while */
//
//        return;
//
//    } /* macshapa_od_reader::parse_imp_format_list_alist() */
//
//
//    /*************************************************************************
//     *
//     * parse_imp_formats_list()
//     *
//     * This method parses a list of import format lists.  This list is generated
//     * by the following productions:
//     *
//     *     <imp_formats_list> --> '(' (<imp_format_list>)* ')'
//     *
//     *     <imp_format_list> --> 
//     *             '(' <imp_format_list_name> <imp_format_list_alist> ')'
//     *
//     *     <imp_format_list_name> --> <non_blank_text_quote_string>
//     *
//     *     <imp_format_list_alist> --> '(' (<imp_format_attributes>) ')'
//     *
//     *     <imp_format_attributes> --> { <imp_prods_attribute> }
//     *
//     *     <imp_prods_attribute> --> 
//     *             '(' 'IMPORT-PRODUCTION-LIST>' <imp_prod_list> ')'
//     *
//     *     <imp_prod_list> --> '(' (<imp_prod>)* ')'
//     *
//     *     <imp_prod> --> '(' <imp_prod_alist> ')'
//     *
//     *     <imp_prod_alist> --> { <imp_prod_name_attribute>
//     *                            <imp_prod_pattern_attribute>
//     *                            <imp_prod_hard_prec_attribute>
//     *                            <imp_prod_is_shown_attribute>
//     *                            <imp_prod_is_traced_attribute>
//     *                            ( <imp_prod_insert_actions_attribute> |
//     *                              <imp_prod_program_actions_attribute> ) }
//     *
//     *     <imp_prod_name_attribute> --> '(' 'NAME>' <text_quote_string> ')'
//     *
//     *     <imp_prod_pattern_attribute> --> '(' 'PATTERN>' <text_quote_string> ')'
//     *
//     *     <imp_prod_hard_prec_attribute> --> '(' 'HARD-PREC>' <integer> ')'
//     *
//     *     <imp_prod_is_shown_attribute> --> '(' 'IS-SHOWN>' <boolean> ')'
//     *
//     *     <imp_prod_is_traced_attribute> --> '(' 'IS-TRACED>' <boolean> ')'
//     *
//     *     <imp_prod_insert_actions_attribute> --> 
//     *             '(' 'INSERT-ACTIONS>' <imp_prod_insert_actions_list> ')'
//     *
//     *     <imp_prod_insert_actions_list> --> 
//     *             '(' (<imp_prod_insert_action_alist>)* ')'
//     *
//     *     <imp_prod_insert_action_alist> --> 
//     *             '(' <imp_prod_insert_action_attributes> ')'
//     *
//     *     <imp_prod_insert_action_attributes> --> 
//     *             ( <imp_prod_insert_action_var_attribute> 
//     *               <imp_prod_insert_action_onset_attribute> 
//     *               <imp_prod_insert_action_offset_attribute> 
//     *               <imp_prod_insert_action_val_attribute> )
//     *
//     *     <imp_prod_insert_action_var_attribute> --> 
//     *             '(' 'VAR>' <text_quote_string> ')'
//     *
//     *     <imp_prod_insert_action_onset_attribute> --> 
//     *             '(' 'ONSET>' <text_quote_string> ')'
//     *
//     *     <imp_prod_insert_action_offset_attribute> --> 
//     *             '(' 'OFFSET>' <text_quote_string> ')'
//     *
//     *     <imp_prod_insert_action_val_attribute> --> 
//     *             '(' 'VAL>' <text_quote_string> ')'
//     *
//     *     <imp_prod_program_actions_attribute> --> 
//     *             '(' 'PROGRAM-ACTIONS>' <imp_prod_program_actions_list> ')'
//     *
//     *     <imp_prod_program_actions_list> --> 
//     *             '(' (<imp_prod_program_action_alist>)* ')'
//     *
//     *     <imp_prod_program_action_alist> --> 
//     *             '(' <imp_prod_program_action_attributes> ')'
//     *
//     *     <imp_prod_program_action_attributes> --> 
//     *             ( <imp_prod_program_action_text_attribute> )
//     *
//     *     <imp_prod_program_action_text_attribute> -->
//     *             '(' 'TEXT>' <text_quote_string> ')' 
//     *
//     *                                              - 6/29/08
//     *
//     * Parameters:
//     *
//     *    - None.
//     *
//     * Returns:  Void.
//     *
//     * Changes:
//     *
//     *    - None.
//     *
//     *************************************************************************/
//
//    private void parse_imp_formats_list()
//        throws SystemErrorException
//    {
//        final String mName = "macshapa_odb_reader::parse_imp_formats_list()";
//        boolean done;
//
//        if ( this.abort_parse )
//        {
//            throw new SystemErrorException(mName + 
//                    "this.abort_parse TRUE on entry");
//        }
//        
//        /* parse the import formats list */
//
//        /* first parse the leading left parenthesis */
//
//        if ( (this.l0_tok).code == L_PAREN_TOK )
//        {
//            get_next_token();
//        }
//        else /* we shouldn't have been called unless the next token is a '(' */
//        {
//            throw new SystemErrorException(mName + 
//                    "(this.l0_tok).code isnt L_PAREN_TOK.");
//        }
//
//        /* now read the import format lists */
//
//        done = false;
//
//        while ( ( ! this.abort_parse ) && ( ! done ) )
//        {
//            switch ( (this.l0_tok).code )
//            {
//                case R_PAREN_TOK:
//                    done = TRUE;
//                    get_next_token();
//                    break;
//
//                case L_PAREN_TOK:
//                    if ( ( (this.l1_tok).code == STRING_TOK ) && 
//                         ( (((this.l1_tok).aux) & TEXT_QSTRING_FLAG) != 0 ) &&
//                         ( (((this.l1_tok).aux) & NONBLANK_FLAG) != 0 ) )
//                    {
//                        parse_imp_format_list();
//                    }
//                    else
//                    {
//                        post_warning_message(ILLEGAL_IMPORT_FORMAT_LIST_NAME_WARN, 
//                                             null);
//
//                        parse_arbitrary_list();
//                    }
//                    break;
//
//                case BOOL_TOK:
//                case ERROR_TOK:
//                case SYMBOL_TOK:
//                case FLOAT_TOK:
//                case STRING_TOK:
//                case INT_TOK:
//                case ALIST_LABEL_TOK:
//                case PRIVATE_VAL_TOK:
//                case SETF_TOK:
//                case DB_VAR_TOK:
//                case QUOTE_TOK:
//                    post_warning_message(ATOM_IN_IMPORT_FORMATS_LIST_WARN, 
//                                         null); 
//
//                    if ( ! this.abort_parse )
//                    {
//                        get_next_token();
//                    }
//                    break;
//
//                case EOF_TOK:
//                    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                            "EOF in the import formats list in the SYSTEM> " +
//                            "IMPORT-FORMATS-LIST> attribute.\n", true, true); 
//                    break;
//
//                 default:
//                     throw new SystemErrorException(mName + 
//                             "Encountered unknown token type.");
//                     break;
//             }
//        }
//
//        return;
//
//    } /* macshapa_odb_reader::parse_imp_formats_list() */
//
//
//    /*************************************************************************
//     *
//     * parse_import_formats_attribute()
//     *
//     * This method is the top level routine in a collection of functions that
//     * parses a 'IMPORT-FORMAT-LISTS> attribute>  The grammar generating the
//     * import formats attribute is given below:
//     * 
//     *     <import_formats_attribute> --> 
//     *             '(' 'IMPORT-FORMAT-LISTS>' <imp_formats_list> ')'
//     *
//     *     <imp_formats_list> --> '(' (<imp_format_list>)* ')'
//     *
//     *     <imp_format_list> --> 
//     *             '(' <imp_format_list_name> <imp_format_list_alist> ')'
//     *
//     *     <imp_format_list_name> --> <non_blank_text_quote_string>
//     *
//     *     <imp_format_list_alist> --> '(' (<imp_format_attributes>) ')'
//     *
//     *     <imp_format_attributes> --> { <imp_prods_attribute> }
//     *
//     *     <imp_prods_attribute> --> 
//     *             '(' 'IMPORT-PRODUCTION-LIST>' <imp_prod_list> ')'
//     *
//     *     <imp_prod_list> --> '(' (<imp_prod>)* ')'
//     *
//     *     <imp_prod> --> '(' <imp_prod_alist> ')'
//     *
//     *     <imp_prod_alist> --> { <imp_prod_name_attribute>
//     *                            <imp_prod_pattern_attribute>
//     *                            <imp_prod_hard_prec_attribute>
//     *                            <imp_prod_is_shown_attribute>
//     *                            <imp_prod_is_traced_attribute>
//     *                            ( <imp_prod_insert_actions_attribute> |
//     *                              <imp_prod_program_actions_attribute> ) }
//     *
//     *     <imp_prod_name_attribute> --> '(' 'NAME>' <text_quote_string> ')'
//     *
//     *     <imp_prod_pattern_attribute> --> '(' 'PATTERN>' <text_quote_string> ')'
//     *
//     *     <imp_prod_hard_prec_attribute> --> '(' 'HARD-PREC>' <integer> ')'
//     *
//     *     <imp_prod_is_shown_attribute> --> '(' 'IS-SHOWN>' <boolean> ')'
//     *
//     *     <imp_prod_is_traced_attribute> --> '(' 'IS-TRACED>' <boolean> ')'
//     *
//     *     <imp_prod_insert_actions_attribute> --> 
//     *             '(' 'INSERT-ACTIONS>' <imp_prod_insert_actions_list> ')'
//     *
//     *     <imp_prod_insert_actions_list> --> 
//     *             '(' (<imp_prod_insert_action_alist>)* ')'
//     *
//     *     <imp_prod_insert_action_alist> --> 
//     *             '(' <imp_prod_insert_action_attributes> ')'
//     *
//     *     <imp_prod_insert_action_attributes> --> 
//     *             ( <imp_prod_insert_action_var_attribute> 
//     *               <imp_prod_insert_action_onset_attribute> 
//     *               <imp_prod_insert_action_offset_attribute> 
//     *               <imp_prod_insert_action_val_attribute> )
//     *
//     *     <imp_prod_insert_action_var_attribute> --> 
//     *             '(' 'VAR>' <text_quote_string> ')'
//     *
//     *     <imp_prod_insert_action_onset_attribute> --> 
//     *             '(' 'ONSET>' <text_quote_string> ')'
//     *
//     *     <imp_prod_insert_action_offset_attribute> --> 
//     *             '(' 'OFFSET>' <text_quote_string> ')'
//     *
//     *     <imp_prod_insert_action_val_attribute> --> 
//     *             '(' 'VAL>' <text_quote_string> ')'
//     *
//     *     <imp_prod_program_actions_attribute> --> 
//     *             '(' 'PROGRAM-ACTIONS>' <imp_prod_program_actions_list> ')'
//     *
//     *     <imp_prod_program_actions_list> --> 
//     *             '(' (<imp_prod_program_action_alist>)* ')'
//     *
//     *     <imp_prod_program_action_alist> --> 
//     *             '(' <imp_prod_program_action_attributes> ')'
//     *
//     *     <imp_prod_program_action_attributes> --> 
//     *             ( <imp_prod_program_action_text_attribute> )
//     *
//     *     <imp_prod_program_action_text_attribute> -->
//     *             '(' 'TEXT>' <text_quote_string> ')' 
//     *
//     *
//     *                                              - 6/29/08
//     *
//     * Parameters:
//     *
//     *    - None
//     *
//     * Returns:  Void.
//     *
//     * Changes:
//     *
//     *    - None.
//     *
//     *************************************************************************/
//
//    private void parse_import_formats_attribute()
//        throws SystemErrorException
//    {
//        final String mName = "macshapa_odb_reader::parse_import_formats_attribute()";
//        final String missing_import_formats_list_mssg = 
//                "The IMPORT-FORMAT-LISTS> attribute appears not to contain a " +
//                "value.  The attribute will be ignored.\n";
//        final String import_formats_list_type_mismatch_mssg = 
//                "The value of a IMPORT-FORMAT-LISTS> attribute must be a list.  " +
//                "The attribute will be ignored.\n";
//
//        if ( this.abort_parse )
//        {
//            throw new SystemErrorException(mName + 
//                    "this.abort_parse TRUE on entry");
//        }
//        
//        /* parse the alignments attribute */
//        
//        /* first parse the leading left parenthesis */
//
//        if ( (this.l0_tok).code == L_PAREN_TOK )
//        {
//            get_next_token();
//        }
//        else /* we shouldn't have been called unless the next token is a '(' */
//        {
//            throw new SystemErrorException(mName + 
//                    "(this.l0_tok).code isnt L_PAREN_TOK.");
//        }
//
//        /* read the a-list entry name */
//        if ( ! this.abort_parse )
//        {
//            if ( ( (this.l0_tok).code == ALIST_LABEL_TOK ) && 
//                 ( (this.l0_tok).aux == IMPORT_FORMATS_LIST_LABEL ) )
//            {
//                get_next_token();
//            }
//            else /* we shouldn't have been called unless the next token is IMPORT-FORMAT-LISTS> */
//            {
//                throw new SystemErrorException(mName + 
//                        "this.l0_tok isnt IMPORT-FORMAT-LISTS>.");
//            }
//        }
//
//        /* read the value associated with the a-list entry */
//        if ( ! this.abort_parse )
//        {
//            switch ( (this.l0_tok).code )
//            {
//                case R_PAREN_TOK:
//                    post_warning_message(EMPTY_ALIST_ENTRY_WARN, 
//                                         missing_import_formats_list_mssg); 
//                    break;
//
//                case L_PAREN_TOK:
//                    parse_imp_formats_list();
//                    break;
//
//                case BOOL_TOK:
//                case ERROR_TOK:
//                case SYMBOL_TOK:
//                case FLOAT_TOK:
//                case STRING_TOK:
//                case INT_TOK:
//                case ALIST_LABEL_TOK:
//                case PRIVATE_VAL_TOK:
//                case SETF_TOK:
//                case DB_VAR_TOK:
//                case QUOTE_TOK:
//                    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
//                                         import_formats_list_type_mismatch_mssg); 
//
//                    if ( ! this.abort_parse )
//                    {
//                        get_next_token();
//                    }
//                    break;
//
//                case EOF_TOK:
//                    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                            "EOF in the SYSTEM> IMPORT-FORMAT-LISTS> attribute.\n", 
//                            true, true);
//                    break;
//
//                 default:
//                     throw new SystemErrorException(mName + 
//                             "Encountered unknown token type.");
//                     break;
//             }
//         }
//
//         /* check for EOF */
//         if ( ! this.abort_parse )
//         {
//             if ( (this.l0_tok).code == EOF_TOK )
//             {
//                post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                        "EOF in the SYSTEM> IMPORT-FORMAT-LISTS> attribute.\n",  
//                        true, true);
//             }
//         }
//
//        /* discard any excess values that may appear in the IMPORT-FORMAT-LISTS> a-list entry */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code != R_PAREN_TOK )
//            {
//                discard_excess_alist_entry_values();
//
//                if ( ! this.abort_parse )
//                {
//                    post_warning_message(EXCESS_VALUES_IN_ALIST_ENTRY_WARN,
//                            "Excess values encountered the SYSTEM> " +
//                            "IMPORT-FORMAT-LISTS> attribute.\n");
//                }
//            }
//        }
//
//        /* read the terminating right parenthesis */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code == R_PAREN_TOK )
//            {
//                get_next_token();
//            }
//            else
//            {
//                /* since we are cleaning up any excess values in the import formats 
//                 * list attribute, this else clause is unreachable at present.
//                 * Should we choose to drop the above attempt at error recovery,
//                 * this clause will again become reachable.
//                 */
//
//                post_warning_message(RIGHT_PAREN_EXPECTED_WARN, 
//                        "Closing parenthesis missing from the SYSTEM> " +
//                        "IMPORT-FORMAT-LISTS> attribute.\n");
//            }
//        }
//
//        return;
//
//    } /* macshapa_odb_reader::parse_import_formats_attribute() */
//
//
//    /*************************************************************************
//     *
//     * parse_imp_prod()
//     *
//     * This method parses an import production, which is simply an attribute
//     * list generated by the following productions:
//     *
//     *     <imp_prod> --> '(' <imp_prod_alist> ')'
//     *
//     *     <imp_prod_alist> --> { <imp_prod_name_attribute>
//     *                            <imp_prod_pattern_attribute>
//     *                            <imp_prod_hard_prec_attribute>
//     *                            <imp_prod_is_shown_attribute>
//     *                            <imp_prod_is_traced_attribute>
//     *                            ( <imp_prod_insert_actions_attribute> |
//     *                              <imp_prod_program_actions_attribute> ) }
//     *
//     *     <imp_prod_name_attribute> --> '(' 'NAME>' <text_quote_string> ')'
//     *
//     *     <imp_prod_pattern_attribute> --> '(' 'PATTERN>' <text_quote_string> ')'
//     *
//     *     <imp_prod_hard_prec_attribute> --> '(' 'HARD-PREC>' <integer> ')'
//     *
//     *     <imp_prod_is_shown_attribute> --> '(' 'IS-SHOWN>' <boolean> ')'
//     *
//     *     <imp_prod_is_traced_attribute> --> '(' 'IS-TRACED>' <boolean> ')'
//     *
//     *     <imp_prod_insert_actions_attribute> --> 
//     *             '(' 'INSERT-ACTIONS>' <imp_prod_insert_actions_list> ')'
//     *
//     *     <imp_prod_insert_actions_list> --> 
//     *             '(' (<imp_prod_insert_action_alist>)* ')'
//     *
//     *     <imp_prod_insert_action_alist> --> 
//     *             '(' <imp_prod_insert_action_attributes> ')'
//     *
//     *     <imp_prod_insert_action_attributes> --> 
//     *             ( <imp_prod_insert_action_var_attribute> 
//     *               <imp_prod_insert_action_onset_attribute> 
//     *               <imp_prod_insert_action_offset_attribute> 
//     *               <imp_prod_insert_action_val_attribute> )
//     *
//     *     <imp_prod_insert_action_var_attribute> --> 
//     *             '(' 'VAR>' <text_quote_string> ')'
//     *
//     *     <imp_prod_insert_action_onset_attribute> --> 
//     *             '(' 'ONSET>' <text_quote_string> ')'
//     *
//     *     <imp_prod_insert_action_offset_attribute> --> 
//     *             '(' 'OFFSET>' <text_quote_string> ')'
//     *
//     *     <imp_prod_insert_action_val_attribute> --> 
//     *             '(' 'VAL>' <text_quote_string> ')'
//     *
//     *     <imp_prod_program_actions_attribute> --> 
//     *             '(' 'PROGRAM-ACTIONS>' <imp_prod_program_actions_list> ')'
//     *
//     *     <imp_prod_program_actions_list> --> 
//     *             '(' (<imp_prod_program_action_alist>)* ')'
//     *
//     *     <imp_prod_program_action_alist> --> 
//     *             '(' <imp_prod_program_action_attributes> ')'
//     *
//     *     <imp_prod_program_action_attributes> --> 
//     *             ( <imp_prod_program_action_text_attribute> )
//     *
//     *     <imp_prod_program_action_text_attribute> -->
//     *             '(' 'TEXT>' <text_quote_string> ')' 
//     *
//     * In addition to parsing the import production, this function also 
//     * allocates an instance of the Prod structure, loads the data from the 
//     * production into it, and appends it to the linked list of instances of 
//     * Prod associated with the instance of the Format structure.  
//     *
//     *                                              - 7/1/08
//     *
//     * Parameters:
//     *
//     *    - format_ptr:  Pointer to an instance of the Format structure, whose
//     *          type definition may be found in import.h.  format_ptr points
//     *          to the instance of format which contains the root of the linked
//     *          list of instances of Prod structure to which we will append the
//     *          new i nstance of Prod.
//     *
//     * Returns:  Void.
//     *
//     * Changes:
//     *
//     *    - None.
//     *
//     *************************************************************************/
//
//    void parse_imp_prod(FormatPtr format_ptr)
//        throws SystemErrorException
//    {
//        final String mName = "macshapa_odb_reader::parse_imp_prod()";
//        String name_ptr;
//        String pattern_str_ptr;
//        boolean done;
//        boolean have_name;
//        boolean have_pattern;
//        boolean have_hard_prec;
//        boolean have_is_shown;
//        boolean have_is_traced;
//        boolean have_insert_actions;
//        boolean have_program_actions;
//        boolean is_shown;
//        boolean is_traced;
//        int hardPrec;
//        ProdPtr prod_ptr;
//        ActionPtr action_list_ptr;
//
//        prod_ptr        = null;  /* on general principles */
//        action_list_ptr = null;  /* on general principles */
//
//        if ( this.abort_parse )
//        {
//            throw new SystemErrorException(mName + 
//                    "this.abort_parse TRUE on entry");
//        }
//        
//        if ( ( format_ptr == null ) || 
//             ( format_ptr->name == null ) || 
//             ( format_ptr->next != null ) )
//        {
//            this.proceed = FALSE;
//            error3("SYSTEM ERROR", "format_ptr seems to be corrupt on entry", fcnNamePtr);
//        }
//        
//        /* parse the attribute list */
//        
//        /* first parse the leading left parenthesis */
//
//        if ( (this.l0_tok).code == L_PAREN_TOK )
//        {
//            if ( ( (this.l1_tok).code == L_PAREN_TOK ) || 
//                 ( (this.l1_tok).code == R_PAREN_TOK ) )
//            {
//                get_next_token();
//            }
//            else if ( (this.l1_tok).code == ALIST_LABEL_TOK )
//            {
//                post_warning_message(LEFT_PAREN_EXPECTED_WARN, 
//                        "The opening left parenthesis of an import " +
//                        "production appears to be missing.\n");
//            }
//            else 
//            {
//                /* if a left paren is missing, the first item in the a-list 
//                 * is not an a-list  entry.  If we try to recover from this 
//                 * error here, we will only confuse things further.  Thus we
//                 * eat the left parenthesis & let the cards fall where they may.
//                 */
//
//                get_next_token();
//            }
//        }
//        else /* we shouldn't have been called unless the next token is a '(' */
//        {
//            throw new SystemErrorException(mName + 
//                    "(this.l0_tok).code isnt L_PAREN_TOK.");
//        }
//
//        done                 = false;
//        have_name            = false;
//        have_pattern         = false;
//        have_hard_prec       = false;
//        have_is_shown        = false;
//        have_is_traced       = false;
//        have_insert_actions  = false;
//        have_program_actions = false;
//
//        name_ptr             = null;
//        pattern_str_ptr      = null;
//        hardPrec             = 0;
//        is_shown             = false;
//        is_traced            = false;
//        action_list_ptr      = null;
//
//        /* now parse the a-list assocated with the import production */
//        while ( ( ! this.abort_parse ) && 
//                 ( ! done ) )
//        {
//            if ( (this.l0_tok).code == L_PAREN_TOK )
//            {
//                if ( (this.l1_tok).code == ALIST_LABEL_TOK )
//                {
//                    switch ( (this.l1_tok).aux )
//                    {
//                        case NAME_LABEL:
//                            if ( ! have_name )
//                            {
//                                have_name = true;
//                                name_ptr = parse_imp_prod_name_attribute();
//                            }
//                            else
//                            {
//                                 post_warning_message(DUPLICATE_ALIST_ENTRY_WARN, 
//                                        "Duplicate NAME> entry in an import " +
//                                        "production.\n");
//
//                                 if ( ! this.abort_parse )
//                                 {
//                                     parse_unknown_alist_entry();
//                                 }
//                            }
//                            break;
//
//                       case PATTERN_LABEL:
//                           if ( ! have_pattern )
//                           {
//                               have_pattern = true;
//                               pattern_str_ptr =
//                                        parse_imp_prod_pattern_attribute();
//                           }
//                           else
//                           {
//                                post_warning_message(DUPLICATE_ALIST_ENTRY_WARN, 
//                                        "Duplicate PATTERN> entry in an " +
//                                        "import production.\n");
//
//                                if ( ! this.abort_parse )
//                                {
//                                     parse_unknown_alist_entry();
//                                }
//                            }
//                            break;
//
//                        case HARD_PREC_LABEL:
//                            if ( ! have_hard_prec )
//                            {
//                                have_hard_prec = true;
//                                hardPrec = parse_imp_prod_hard_prec_attribute();
//                            }
//                            else
//                            {
//                                post_warning_message(DUPLICATE_ALIST_ENTRY_WARN, 
//                                        "Duplicate HARD-PREC> entry in an " +
//                                        "import production.\n");
//
//                                if ( ! this.abort_parse )
//                                {
//                                     parse_unknown_alist_entry();
//                                }
//                            }
//                            break;
//
//                        case IS_SHOWN_LABEL:
//                            if ( ! have_is_shown )
//                            {
//                                have_is_shown = true;
//                                is_shown = parse_imp_prod_is_shown_attribute();
//                            }
//                            else
//                            {
//                                post_warning_message(DUPLICATE_ALIST_ENTRY_WARN, 
//                                        "Duplicate IS-SHOWN> entry in an " +
//                                        "import production.\n");
//
//                                if ( ! this.abort_parse )
//                                {
//                                     parse_unknown_alist_entry();
//                                }
//                            }
//                            break;
//
//                        case IS_TRACED_LABEL:
//                            if ( ! have_is_traced )
//                            {
//                                 have_is_traced = true;
//                                 is_traced = 
//                                         parse_imp_prod_is_traced_attribute();
//                            }
//                            else
//                            {
//                                post_warning_message(DUPLICATE_ALIST_ENTRY_WARN, 
//                                        "Duplicate IS-TRACED> entry in an " +
//                                        "import production.\n");
//
//                                if ( ! this.abort_parse )
//                                {
//                                     parse_unknown_alist_entry();
//                                }
//                            }
//                            break;
//
//                        case INSERT_ACTIONS_LABEL:
//                            if ( ! have_insert_actions )
//                            {
//                                have_insert_actions = true;
//
//                                if ( ! have_program_actions )
//                                {
//                                     action_list_ptr = 
//                                         parse_imp_prod_insert_actions_attribute();
//                                }
//                                else
//                                {
//                                    post_warning_message(
//                                        BOTH_PGM_AND_INSERT_ACTION_IN_PROD_WARN, 
//                                        "Since the PROGRAM-ACTIONS> attribute " +
//                                        "appeared first, the INSERT-ACTIONS> " +
//                                        "attribute will be discarded.\n");
//
//                                    if ( ! this.abort_parse )
//                                    {
//                                        parse_unknown_alist_entry();
//                                    }
//                                }
//                            }
//                            else
//                            {
//                                post_warning_message(DUPLICATE_ALIST_ENTRY_WARN, 
//                                        "Duplicate INSERT-ACTIONS> entry " +
//                                        "in an import production.\n");
//
//                                if ( ! this.abort_parse )
//                                {
//                                     parse_unknown_alist_entry();
//                                }
//                            }
//                            break;
//
//                        case PROGRAM_ACTIONS_LABEL:
//                            if ( ! have_program_actions )
//                            {
//                                have_program_actions = true;
//
//                                if ( ! have_insert_actions )
//                                {
//                                    action_list_ptr = 
//                                        parse_imp_prod_program_actions_attribute();
//                                }
//                                else
//                                {
//                                    post_warning_message(
//                                        BOTH_PGM_AND_INSERT_ACTION_IN_PROD_WARN, 
//                                        "Since the INSERT-ACTIONS> attribute " +
//                                        "appeared first, the PROGRAM-ACTIONS> " +
//                                        "attribute will be discarded.\n");
//
//                                    if ( ! this.abort_parse )
//                                    {
//                                         parse_unknown_alist_entry();
//                                    }
//                                }
//                            }
//                            else
//                            {
//                                post_warning_message(DUPLICATE_ALIST_ENTRY_WARN, 
//                                        "Duplicate PROGRAM-ACTIONS> entry " +
//                                        "in an import production.\n");
//
//                                if ( ! this.abort_parse )
//                                {
//                                     parse_unknown_alist_entry();
//                                }
//                            }
//                            break;
//
//                         default:
//                            post_warning_message(
//                                    UNKNOWN_OR_UNEXPECTED_ALIST_ENTRY_WARN, 
//                                    "The entry is located in an import " +
//                                    "production.\n");
//
//                            if ( ! this.abort_parse )
//                            {
//                                parse_unknown_alist_entry();
//                            }
//                            break;
//                    }
//                }
//                else /* a-list contains a list that is not an a-list entry. */
//                     /* read it & discard it.                               */
//                {
//                    post_warning_message(NON_ALIST_ENTRY_LIST_IN_ALIST_WARN, 
//                        "The list is located in an import production.\n");
//
//                    if ( ! this.abort_parse )
//                    {
//                        parse_arbitrary_list();
//                    }
//                }
//            }
//            else if ( (this.l0_tok).code == R_PAREN_TOK )
//            {
//                done = true;
//                get_next_token();
//            }
//            else if ( (this.l0_tok).code == EOF_TOK )
//            {
//                done = true;
//                post_error_message(UNEXPECTED_END_OF_FILE_ERR,
//                        "EOF occurred in an import production.\n", true, true);
//            }
//            else /* (this.l0_tok).code isnt '(', ')', or EOF */
//            {
//                post_warning_message(NON_ALIST_ENTRY_ATOM_IN_ALIST_WARN, 
//                        "The atom was detected in an import production.\n");
//
//                if ( ! this.abort_parse )
//                {
//                    get_next_token();
//                }
//            }
//        } /* while */
//
//         /* create the production & add it to the production list */
//         if ( ! this.abort_parse )
//         {
//             if ( ( have_name ) && 
//                  ( have_pattern ) && 
//                  ( have_hard_prec ) && 
//                  ( have_is_shown ) && 
//                  ( have_is_traced ) && 
//                  ( ( have_insert_actions ) || ( have_program_actions ) ) &&
//                  ( name_ptr != NULL ) && 
//                  ( pattern_str_ptr != null ) &&
//                  ( action_list_ptr != null ) )
//             {
//                 /* we have all the required information to create the production */
//
//                 prod_ptr = allocProd();
//
//                 if ( prod_ptr == NULL )
//                 {
//                     this.proceed = FALSE;
//                     error3("SYSTEM ERROR", "allocProd() returned NULL.", fcnNamePtr);
//                 }
//                 else
//                 {
//                     prod_ptr->name     = name_ptr;
//                     prod_ptr->pattern  = pattern_str_ptr;
//                     prod_ptr->hardPrec = hardPrec;
//                     prod_ptr->isTraced = is_traced;
//                     prod_ptr->isShown  = is_shown;
//
//                     if ( have_insert_actions ) 
//                     {
//                         prod_ptr->actionKind = INSERT_ACTION;
//                         prod_ptr->actions    = action_list_ptr;
//
//                         format_ptr->prods = AddProdListEnd(format_ptr->prods, prod_ptr);
//                     }
//                     else if ( have_program_actions )
//                     {
//                         prod_ptr->actionKind = PROGRAM_ACTION;
//                         prod_ptr->actions    = action_list_ptr;
//
//                         format_ptr->prods = AddProdListEnd(format_ptr->prods, prod_ptr);
//                     }
//                     else
//                     {
//                         throw SystemErrorException(mName + 
//                                 "This clause should be unreachable.");
//                     }
//                 }
//             }
//             else /* discard dynamically allocated data as required */
//             {
//                 if ( name_ptr        != NULL ) freeCstr(name_ptr);
//                 if ( pattern_str_ptr != NULL ) freeCstr(pattern_str_ptr);
//                 if ( action_list_ptr != NULL ) freeActionList(action_list_ptr);
//
//                 if ( ( ! have_name ) || 
//                      ( ! have_pattern ) || 
//                      ( ! have_hard_prec ) || 
//                      ( ! have_is_shown ) || 
//                      ( ! have_is_traced ) || 
//                      ( ( ! have_insert_actions ) && ( ! have_program_actions ) ) )
//                {
//                    post_warning_message(MISSING_REQ_ATTR_IN_IMP_PROD_WARN, 
//                                               NULL);
//                }
//            }
//        }
//
//        return;
//
//    } /* macshapa_odb_reader::parse_imp_prod() */
//
//
//    /*************************************************************************
//     *
//     * parse_imp_prod_hard_prec_attribute()
//     *
//     * This function parses an import production hard precidence attribute, and 
//     * returns the integer value associated with the attribute, or zero if no
//     * integer value is found.  The hard precidence attribute is generated by
//     * the following production:
//     *
//     *     <imp_prod_hard_prec_attribute> --> '(' 'HARD-PREC>' <integer> ')'
//     *
//     * As best I can tell, the hardPrec field of the Prod structure is not used
//     * for anything.  Thus I am not particularly concerned about using a more or 
//     * less randomly selected default value.  (Actually, zero is not quite 
//     * random.  It is the value to which the hardPrec field is initialized in 
//     * the allocProd() function in import_internals.c.)
//     *
//     *                                              - 7/2/08
//     *
//     * Parameters:
//     *
//     *    - None.
//     *
//     * Returns: Integer value associated with the attribute, or zero if an
//     *          error is detected.
//     *
//     * Changes:
//     *
//     *    - None.
//     *
//     *************************************************************************/
//
//    private int parse_imp_prod_hard_prec_attribute()
//        throws SystemErrorException
//    {
//        final String mName = 
//                "macshapa_odb_reader::parse_imp_prod_hard_prec_attribute()";
//        final String missing_hard_prec_mssg = 
//                "A HARD-PREC> attribute appears not to contain a value.  " + 
//                "Value forced to 0.\n";
//        final String hard_prec_type_mismatch_mssg = 
//                "The value of a HARD-PREC> attribute must be an integer.  " +
//                "Value forced to 0.\n";
//        int hardPrec;
//
//        hardPrec = 0; /* default value */
//
//        if ( this.abort_parse )
//        {
//            throw new SystemErrorException(mName + 
//                    "this.abort_parse TRUE on entry");
//        }
//        
//        /* parse the attribute */
//        
//        /* first parse the leading left parenthesis */
//
//        if ( (this.l0_tok).code == L_PAREN_TOK )
//        {
//            get_next_token();
//        }
//        else /* we shouldn't have been called unless the next token is a '(' */
//        {
//            throw new SystemErrorException(mName + 
//                    "(this.l0_tok).code isnt L_PAREN_TOK.");
//        }
//
//        /* read the a-list entry name */
//        if ( ! this.abort_parse )
//        {
//            if ( ( (this.l0_tok).code == ALIST_LABEL_TOK ) && 
//                 ( (this.l0_tok).aux == HARD_PREC_LABEL ) )
//            {
//                get_next_token();
//            }
//            else /* system error - we shouldn't have been called unless the next token is an a-list tag */
//            {
//                throw new SystemErrorException(mName + 
//                        "this.l0_tok inst HARD-PREC>.");
//            }
//        }
//
//        /* read the value associated with the a-list entry & discard any excess values */
//        if ( ! this.abort_parse )
//        {
//            switch ( (this.l0_tok).code )
//            {
//                case INT_TOK:
//                    hardPrec = (int)((this.l0_tok).val);
//
//                    if ( ! this.abort_parse )
//                    {
//                        get_next_token();
//                    }
//                    break;
//
//                case R_PAREN_TOK:
//                    post_warning_message(EMPTY_ALIST_ENTRY_WARN, 
//                                         missing_hard_prec_mssg); 
//                    break;
//
//                case L_PAREN_TOK:
//                    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
//                                         hard_prec_type_mismatch_mssg); 
//
//                    if ( ! this.abort_parse )
//                    {
//                        parse_arbitrary_list();
//                    }
//                    break;
//
//                case ERROR_TOK:
//                case SYMBOL_TOK:
//                case FLOAT_TOK:
//                case STRING_TOK:
//                case BOOL_TOK:
//                case ALIST_LABEL_TOK:
//                case PRIVATE_VAL_TOK:
//                case SETF_TOK:
//                case DB_VAR_TOK:
//                case QUOTE_TOK:
//                    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
//                                         hard_prec_type_mismatch_mssg); 
//
//                    if ( ! this.abort_parse )
//                    {
//                        get_next_token();
//                    }
//                    break;
//
//
//                case EOF_TOK:
//                    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                                       "EOF in a HARD-PREC> attribute.\n", 
//                                       true, true);
//                    break;
//
//                 default:
//                     throw new SystemErrorException(mName + 
//                             "Encountered unknown token type.");
//                     break;
//            }
//        }
//
//        /* check for EOF */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code == EOF_TOK )
//            {
//                post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                                   "EOF in a HARD-PREC> attribute.\n", 
//                                   true, true);
//            }
//        }
//
//        /* discard any excess values that may appear in the a-list entry */
//        if ( ! this.abort_parse )
//        {
//             if ( (this.l0_tok).code != R_PAREN_TOK )
//             {
//                discard_excess_alist_entry_values();
//
//                if ( ! this.abort_parse )
//                {
//                    post_warning_message(EXCESS_VALUES_IN_ALIST_ENTRY_WARN,
//                        "Excess values encountered in a HARD-PREC> attribute.\n");
//                }
//            }
//        }
//
//        /* read the terminating right parenthesis */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code == R_PAREN_TOK )
//            {
//                get_next_token();
//            }
//            else
//            {
//                /* since we are cleaning up any excess values in the hard 
//                 * precidence, this else clause is unreachable at present.
//                 */
//
//                post_warning_message(RIGHT_PAREN_EXPECTED_WARN, 
//                    "Closing parenthesis missing from a HARD-PREC> attribute.\n");
//            }
//        }
//
//        return(hardPrec);
//
//    } /* parse_imp_prod_hard_prec_attribute() */
//
//
//    /*******************************************************************************
//     *
//     * parse_imp_prod_insert_action_alist()
//     *
//     * This method parses an import production insert action a-lists, and 
//     * returns a pointer to an instance of Action which has been loaded with 
//     * the data obtained from the a-list.  Import production insert action 
//     * a-lists may be generated by the following productions:
//     *
//     *     <imp_prod_insert_action_alist> --> 
//     *             '(' <imp_prod_insert_action_attributes> ')'
//     *
//     *     <imp_prod_insert_action_attributes> --> 
//     *             ( <imp_prod_insert_action_var_attribute> 
//     *               <imp_prod_insert_action_onset_attribute> 
//     *               <imp_prod_insert_action_offset_attribute> 
//     *               <imp_prod_insert_action_val_attribute> )
//     *
//     *     <imp_prod_insert_action_var_attribute> --> 
//     *             '(' 'VAR>' <text_quote_string> ')'
//     *
//     *     <imp_prod_insert_action_onset_attribute> --> 
//     *             '(' 'ONSET>' <text_quote_string> ')'
//     *
//     *     <imp_prod_insert_action_offset_attribute> --> 
//     *             '(' 'OFFSET>' <text_quote_string> ')'
//     *
//     *     <imp_prod_insert_action_val_attribute> --> 
//     *             '(' 'VAL>' <text_quote_string> ')'
//     *
//     * The function returns NULL if an error is detected, or if a required
//     * attribute is missing.
//     *
//     *                                              - 7/2/08
//     *
//     * Parameters:
//     *
//     *    - None.
//     *
//     * Returns:  Pointer an instance of the Action structure, or NULL if 
//     *     any errors are detected.
//     *
//     * Changes:
//     *
//     *    - None.
//     *
//     *******************************************************************************/
//
//    private ActionPtr parse_imp_prod_insert_action_alist()
//        throws SystemErrorException
//    {
//        final String mName = 
//                "macshapa_odb_reader::parse_imp_prod_insert_action_alist()";
//        String var_str_ptr;
//        String onset_str_ptr;
//        String offset_str_ptr;
//        String val_str_ptr;
//        boolean have_var_str;
//        boolean have_onset_str;
//        boolean have_offset_str;
//        boolean have_val_str;
//        boolean done;
//        ActionPtr action_ptr;
//
//        action_ptr = NULL; /* essential */
//
//        if ( this.abort_parse )
//        {
//            throw new SystemErrorException(mName + 
//                    "this.abort_parse TRUE on entry");
//        }
//        
//        /* parse the attribute list */
//        
//        /* first parse the leading left parenthesis */
//
//        if ( (this.l0_tok).code == L_PAREN_TOK )
//        {
//            if ( ( (this.l1_tok).code == L_PAREN_TOK ) || 
//                 ( (this.l1_tok).code == R_PAREN_TOK ) )
//            {
//                get_next_token();
//            }
//            else if ( (this.l1_tok).code == ALIST_LABEL_TOK )
//            {
//                post_warning_message(LEFT_PAREN_EXPECTED_WARN, 
//                        "The opening left parenthesis of an import production " +
//                        "insert action a-list appears to be missing.\n");
//            }
//            else 
//            {
//                /* if a left paren is missing, the first item in the a-list is 
//                 * not an a-list entry.  If we try to recover from this error 
//                 * here, we will only confuse things further.  Thus we eat the 
//                 * left parenthesis & let the cards fall where they may.
//                 */
//
//                get_next_token();
//            }
//        }
//        else /* we shouldn't have been called unless the next token is a '(' */
//        {
//            throw new SystemErrorException(mName + 
//                    "(this.l0_tok).code != L_PAREN_TOK.");
//        }
//
//        done            = false;
//        have_var_str    = false;
//        have_onset_str  = false;
//        have_offset_str = false;
//        have_val_str    = false;
//
//        var_str_ptr     = null;
//        onset_str_ptr   = null;
//        offset_str_ptr  = null;
//        val_str_ptr     = null;
//
//        /* now parse the a-list */
//
//        while ( ( ! this.abort_parse ) && 
//                ( ! done ) )
//        {
//            if ( (this.l0_tok).code == L_PAREN_TOK )
//            {
//                if ( (this.l1_tok).code == ALIST_LABEL_TOK )
//                {
//                    switch ( (this.l1_tok).aux )
//                    {
//                        case VAR_LABEL:
//                            if ( ! have_var_str )
//                            {
//                                 have_var_str = true;
//                                 var_str_ptr = 
//                                     parse_imp_prod_insert_action_var_attribute();
//                            }
//                            else
//                            {
//                                post_warning_message(DUPLICATE_ALIST_ENTRY_WARN, 
//                                        "Duplicate VAR> entry in an import " +
//                                        "production insert action.\n");
//
//                                if ( ! this.abort_parse )
//                                {
//                                     parse_unknown_alist_entry();
//                                }
//                            }
//                            break;
//
//                        case ONSET_LABEL:
//                            if ( ! have_onset_str )
//                            {
//                                have_onset_str = true;
//                                onset_str_ptr = 
//                                    parse_imp_prod_insert_action_onset_attribute();
//                            }
//                            else
//                            {
//                                post_warning_message(DUPLICATE_ALIST_ENTRY_WARN, 
//                                        "Duplicate ONSET> entry in an import " +
//                                        "production insert action.\n");
//
//                                if ( ! this.abort_parse )
//                                {
//                                     parse_unknown_alist_entry();
//                                }
//                            }
//                            break;
//
//                        case OFFSET_LABEL:
//                            if ( ! have_offset_str )
//                            {
//                                have_offset_str = true;
//                                offset_str_ptr = 
//                                    parse_imp_prod_insert_action_offset_attribute();
//                            }
//                            else
//                            {
//                                post_warning_message(DUPLICATE_ALIST_ENTRY_WARN, 
//                                        "Duplicate OFFSET> entry in an import " +
//                                        "production insert action.\n");
//
//                                if ( ! this.abort_parse )
//                                {
//                                     parse_unknown_alist_entry();
//                                }
//                            }
//                            break;
//
//                        case VAL_LABEL:
//                            if ( ! have_val_str )
//                            {
//                                 have_val_str = true;
//                                 val_str_ptr = 
//                                     parse_imp_prod_insert_action_val_attribute();
//                            }
//                            else
//                            {
//                                post_warning_message(DUPLICATE_ALIST_ENTRY_WARN, 
//                                        "Duplicate VAL> entry in an import " +
//                                        "production insert action.\n");
//
//                                if ( ! this.abort_parse )
//                                {
//                                     parse_unknown_alist_entry();
//                                }
//                            }
//                            break;
//
//                        default:
//                            post_warning_message(
//                                    UNKNOWN_OR_UNEXPECTED_ALIST_ENTRY_WARN, 
//                                    "The entry is located in an import " +
//                                    "production insert action.\n");
//
//                            if ( ! this.abort_parse )
//                            {
//                                 parse_unknown_alist_entry();
//                            }
//                            break;
//                    }
//                }
//                else /* a-list contains a list that is not an a-list entry. */
//                     /* read it & discard it.                               */
//                {
//                    post_warning_message(NON_ALIST_ENTRY_LIST_IN_ALIST_WARN, 
//                            "The list is located in an import production " +
//                            "insert action.\n");
//
//                    if ( ! this.abort_parse )
//                    {
//                         parse_arbitrary_list();
//                    }
//                }
//            }
//            else if ( (this.l0_tok).code == R_PAREN_TOK )
//            {
//                done = true;
//                get_next_token();
//            }
//            else if ( (this.l0_tok).code == EOF_TOK )
//            {
//                done = true;
//                post_error_message(UNEXPECTED_END_OF_FILE_ERR,
//                        "EOF occurred in an import production insert action.\n",
//                        true, true);
//            }
//            else /* (this.l0_tok).code isnt '(', ')', or EOF */
//            {
//                post_warning_message(NON_ALIST_ENTRY_ATOM_IN_ALIST_WARN, 
//                        "The atom was detected in an import production " +
//                        "insert action.\n");
//
//                if ( ! this.abort_parse )
//                {
//                    get_next_token();
//                }
//            }
//        } /* while */
//
//        /* if we have all the data, allocate the instance of Action and load 
//         * pointers to strings into it.  Otherwise, we discard the CStrings 
//         * that we would otherwise load into the action.
//         */
//
//        if ( ! this.abort_parse )
//        {
//            if ( ( have_var_str ) && ( have_onset_str ) && 
//                 ( have_offset_str ) && ( have_val_str ) )
//            {
//                action_ptr = allocAction();
//
//                if ( action_ptr == NULL )
//                {
//                    this.proceed = FALSE;
//                    error3("SYSTEM ERROR", "Action allocation failed.", fcnNamePtr);
//                }
//                else
//                {
//                    action_ptr->kind            = INSERT_ACTION;
//                    action_ptr->u.insert.var    = var_str_ptr;
//                    action_ptr->u.insert.onset  = onset_str_ptr;
//                    action_ptr->u.insert.offset = offset_str_ptr;
//                    action_ptr->u.insert.val    = val_str_ptr;
//                }
//            }
//            else /* free the strings & issue warning message */
//            {
//                if ( var_str_ptr    != NULL ) freeCstr(var_str_ptr);
//                if ( onset_str_ptr  != NULL ) freeCstr(onset_str_ptr);
//                if ( offset_str_ptr != NULL ) freeCstr(offset_str_ptr);
//                if ( val_str_ptr    != NULL ) freeCstr(val_str_ptr);
//
//                post_warning_message(MISSING_REQ_ATTR_IN_INS_ACTION_WARN, 
//                                     null);
//            }
//        }
//
//        return(action_ptr);
//
//    } /* macshapa_odb_reader::parse_imp_prod_insert_action_alist() */
//
//
//    /*************************************************************************
//     *
//     * parse_imp_prod_insert_action_offset_attribute()
//     *
//     * This function parses an import production insert action offset attribute, 
//     * and returns a pointer to a CString containing the contents of the 
//     * <text_quote_string>.  This attribute is generated by the following 
//     * production:
//     *
//     * <imp_prod_insert_action_offset_attribute> --> 
//     *                                  '(' 'OFFSET>' <text_quote_string> ')'
//     *
//     *                                              - 7/10/08
//     *
//     * Parameters:
//     *
//     *    - None.
//     *
//     * Returns:  Reference to a String containing the string associated
//     *      with the ONSET> attribute, or NULL if no string is found.
//     *
//     * Changes:
//     *
//     *    - None.
//     *
//     *************************************************************************/
//
//    private String parse_imp_prod_insert_action_offset_attribute()
//
//    {
//        final String mName = 
//                "macshapa_odb_reader::parse_imp_prod_insert_action_offset_attribute()";
//        final String missing_offset_mssg = 
//                "An OFFSET> attribute in an import production insert action " +
//                "appears not to contain a value.  Value forced to the empty string.\n";
//        final String offset_type_mismatch_mssg = 
//                "The value associated with an OFFSET> attribute in an import " +
//                "production insert action must be a quoted string.  Value forced " +
//                "to the empty string.\n";
//        String str = NULL;
//
//        if ( this.abort_parse )
//        {
//            throw new SystemErrorException(mName + 
//                    "this.abort_parse TRUE on entry");
//        }
//        
//        /* parse the offset attribute */
//        
//        /* first parse the leading left parenthesis */
//
//        if ( (this.l0_tok).code == L_PAREN_TOK )
//        {
//            get_next_token();
//        }
//        else /* we shouldn't have been called unless the next token is a '(' */
//        {
//            throw new SystemErrorException(mName + 
//                    "(this.l0_tok).code != L_PAREN_TOK.");
//        }
//
//        /* read the a-list entry name */
//        if ( ! this.abort_parse )
//        {
//            if ( ( (this.l0_tok).code == ALIST_LABEL_TOK ) && 
//                 ( (this.l0_tok).aux == OFFSET_LABEL ) )
//            {
//                get_next_token();
//            }
//            else /* we shouldn't have been called unless the next token is an a-list tag */
//            {
//                throw new SystemErrorException(mName + "this.l0_tok != OFFSET>.");
//            }
//        }
//
//        /* read the value associated with the a-list entry & discard any excess values */
//        if ( ! this.abort_parse )
//        {
//            switch ( (this.l0_tok).code )
//            {
//                case STRING_TOK:
//                     str = new String((this.l0_tok).str.toString());
//
//                     if ( ! this.abort_parse )
//                     {
//                         get_next_token();
//                     }
//                     break;
//
//                 case R_PAREN_TOK:
//                     post_warning_message(EMPTY_ALIST_ENTRY_WARN, 
//                                          missing_offset_mssg); 
//                     break;
//
//                case L_PAREN_TOK:
//                     post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
//                                          offset_type_mismatch_mssg); 
//
//                     if ( ! this.abort_parse )
//                     {
//                        parse_arbitrary_list();
//                     }
//                     break;
//
//                case ERROR_TOK:
//                case SYMBOL_TOK:
//                case FLOAT_TOK:
//                case INT_TOK:
//                case BOOL_TOK:
//                case ALIST_LABEL_TOK:
//                case PRIVATE_VAL_TOK:
//                case SETF_TOK:
//                case DB_VAR_TOK:
//                case QUOTE_TOK:
//                    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
//                                         offset_type_mismatch_mssg); 
//
//                     if ( ! this.abort_parse )
//                     {
//                        get_next_token();
//                     }
//                     break;
//
//                case EOF_TOK:
//                    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                                       "EOF in an OFFSET> attribute.\n", 
//                                       true, true);
//                    break;
//
//                default:
//                    throw new SystemErrorException(mName + 
//                            "Encountered unknown token type.");
//                    break;
//            }
//        }
//
//        /* Check for EOF */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code == EOF_TOK )
//            {
//                post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                                   "EOF in an OFFSET> attribute.\n",
//                                   true, true);
//            }
//        }
//
//        /* discard any excess values that may appear in the OFFSET> a-list entry */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code != R_PAREN_TOK )
//            {
//                discard_excess_alist_entry_values();
//
//                if ( ! this.abort_parse )
//                {
//                    post_warning_message(EXCESS_VALUES_IN_ALIST_ENTRY_WARN,
//                        "Excess values encountered in an OFFSET> attribute.\n");
//                }
//            }
//        }
//
//        /* read the terminating right parenthesis */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code == R_PAREN_TOK )
//            {
//                get_next_token();
//            }
//            else
//            {
//                /* since we are cleaning up any excess values in the offset */
//                /* attribute, this else clause should be unreachable.       */
//
//                post_warning_message(RIGHT_PAREN_EXPECTED_WARN, 
//                    "Closing parenthesis missing from an OFFSET> attribute.\n");
//            }
//        }
//
//        if ( ( ! this.abort_parse ) && ( str == null ) )
//        {
//            /* never return null under normal circumstances -- return */
//            /* a pointer to an empty String instead                   */
//
//            str = new String("");
//        }
//
//        return(str);
//
//    } /* macshapa_odb_reader::parse_imp_prod_insert_action_offset_attribute() */
//
//
//    /*************************************************************************
//     *
//     * parse_imp_prod_insert_action_onset_attribute()
//     *
//     * This function parses an import production insert action onset attribute, 
//     * and returns a reference to a String containing the contents of the 
//     * <text_quote_string>.  This attribute is generated by the following 
//     * production:
//     *
//     * <imp_prod_insert_action_onset_attribute> -->
//     *                      '(' 'ONSET>' <text_quote_string> ')'
//     *
//     *                                              - 7/11/08
//     *
//     * Parameters:
//     *
//     *    - None.
//     *
//     * Returns: Reference to a String containing the string associated
//     *          with the ONSET> attribute, or the empty string if no string 
//     *          is found.
//     *
//     * Changes:
//     *
//     *    - None.
//     *
//     *************************************************************************/
//
//    private String parse_imp_prod_insert_action_onset_attribute()
//        throws SystemErrorException
//    {
//        final String mName = 
//                "macshapa_odb_reader::parse_imp_prod_insert_action_onset_attribute()";
//        String missing_onset_mssg = 
//                "An ONSET> attribute in an import production insert action " +
//                "appears not to contain a value.  " +
//                "Value forced to the empty string.\n";
//        String onset_type_mismatch_mssg = 
//                "The value associated with an ONSET> attribute in an import " +
//                "production insert action must be a quoted string.  " +
//                "Value forced to the empty string.\n";
//        String str = null;
//
//        if ( this.abort_parse )
//        {
//            throw new SystemErrorException(mName + 
//                    "this.abort_parse TRUE on entry");
//        }
//        
//        /* parse the onset attribute */
//
//        /* first parse the leading left parenthesis */
//
//        if ( (this.l0_tok).code == L_PAREN_TOK )
//        {
//            get_next_token();
//        }
//        else /* we shouldn't have been called unless the next token is a '(' */
//        {
//            throw new SystemErrorException(mName + 
//                    "(this.l0_tok).code != L_PAREN_TOK.");
//        }
//
//        /* read the a-list entry name */
//        if ( ! this.abort_parse )
//        {
//            if ( ( (this.l0_tok).code == ALIST_LABEL_TOK ) && 
//                 ( (this.l0_tok).aux == ONSET_LABEL ) )
//            {
//                get_next_token();
//            }
//            else /* we shouldn't have been called unless the next token is an a-list tag */
//            {
//                throw new SystemErrorException(mName + "this.l0_tok != ONSET>.");
//            }
//        }
//
//        /* read the value associated with the a-list entry & discard any excess values */
//        if ( ! this.abort_parse )
//        {
//             switch ( (this.l0_tok).code )
//             {
//                case STRING_TOK:
//                    str = new String(this.l0_tok.toString());
//                    
//                    if ( ! this.abort_parse )
//                    {
//                        get_next_token();
//                    }
//                    break;
//
//                case R_PAREN_TOK:
//                    post_warning_message(EMPTY_ALIST_ENTRY_WARN, 
//                                         missing_onset_mssg); 
//                    break;
//
//                case L_PAREN_TOK:
//                    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
//                                               onset_type_mismatch_mssg); 
//
//                    if ( ! this.abort_parse )
//                    {
//                        parse_arbitrary_list();
//                    }
//                    break;
//
//                case ERROR_TOK:
//                case SYMBOL_TOK:
//                case FLOAT_TOK:
//                case INT_TOK:
//                case BOOL_TOK:
//                case ALIST_LABEL_TOK:
//                case PRIVATE_VAL_TOK:
//                case SETF_TOK:
//                case DB_VAR_TOK:
//                case QUOTE_TOK:
//                    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
//                                         onset_type_mismatch_mssg); 
//
//                     if ( ! this.abort_parse )
//                     {
//                        get_next_token();
//                     }
//                     break;
//
//                case EOF_TOK:
//                    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                                       "EOF in an ONSET> attribute.\n", 
//                                       true, true);
//                    break;
//
//                 default:
//                     throw new SystemErrorException(mName + 
//                             "Encountered unknown token type.");
//                     break;
//             }
//        }
//
//        /* Check for EOF */
//        if ( ! this.abort_parse )
//        {
//             if ( (this.l0_tok).code == EOF_TOK )
//             {
//                post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                                   "EOF in an ONSET> attribute.\n",
//                                   true, true);
//             }
//         }
//
//        /* discard any excess values that may appear in the ONSET> a-list entry */
//        if ( ! this.abort_parse )
//        {
//             if ( (this.l0_tok).code != R_PAREN_TOK )
//             {
//                discard_excess_alist_entry_values();
//
//                if ( ! this.abort_parse )
//                {
//                    post_warning_message(EXCESS_VALUES_IN_ALIST_ENTRY_WARN,
//                        "Excess values encountered in an ONSET> attribute.\n");
//                }
//            }
//        }
//
//        /* read the terminating right parenthesis */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code == R_PAREN_TOK )
//            {
//                get_next_token();
//            }
//            else
//            {
//                /* since we are cleaning up any excess values in the onset */
//                /* attribute, this else clause should be unreachable.      */
//
//                post_warning_message(RIGHT_PAREN_EXPECTED_WARN, 
//                    "Closing parenthesis missing from an ONSET> attribute.\n");
//            }
//        }
//
//        if ( ( ! this.abort_parse ) && ( str_ptr == null ))
//        {
//            /* never return null under normal circumstances -- return */
//            /* a pointer to an empty string instead                   */
//
//            str = new String("");
//        }
//
//        return(str);
//
//    } /* macshapa_odb_reader::parse_imp_prod_insert_action_onset_attribute() */
//
//
//    /*************************************************************************
//     *
//     * parse_imp_prod_insert_actions_attribute()
//     *
//     * This mthod parses an import production insert actions attribute,
//     * and returns a pointer to a linked list of instance of Action.  Import
//     * production insert actions attributes may be generated by the following
//     * productions:
//     *
//     *     <imp_prod_insert_actions_attribute> --> 
//     *             '(' 'INSERT-ACTIONS>' <imp_prod_insert_actions_list> ')'
//     *
//     *     <imp_prod_insert_actions_list> --> 
//     *             '(' (<imp_prod_insert_action_alist>)* ')'
//     *
//     *     <imp_prod_insert_action_alist> --> 
//     *             '(' <imp_prod_insert_action_attributes> ')'
//     *
//     *     <imp_prod_insert_action_attributes> --> 
//     *             ( <imp_prod_insert_action_var_attribute> 
//     *               <imp_prod_insert_action_onset_attribute> 
//     *               <imp_prod_insert_action_offset_attribute> 
//     *               <imp_prod_insert_action_val_attribute> )
//     *
//     *     <imp_prod_insert_action_var_attribute> --> 
//     *             '(' 'VAR>' <text_quote_string> ')'
//     *
//     *     <imp_prod_insert_action_onset_attribute> --> 
//     *             '(' 'ONSET>' <text_quote_string> ')'
//     *
//     *     <imp_prod_insert_action_offset_attribute> --> 
//     *             '(' 'OFFSET>' <text_quote_string> ')'
//     *
//     *     <imp_prod_insert_action_val_attribute> --> 
//     *             '(' 'VAL>' <text_quote_string> ')'
//     *
//     * The function returns a pointer to the first element in a
//     * linked list of instances of the Action structure, whose type
//     * definition may be found in import.h.  All instances of Action
//     * in this structure will be of INSERT_ACTION kind.  
//     *
//     * Note that a NULL pointer will be returned if the list is empty.
//     *
//     *                                              - 7/13/08
//     *
//     * Parameters:
//     *
//     *    - None
//     *
//     * Returns:  Pointer to the first element in a linked list of 
//     *     instances of the Action structure, or NULL if the list
//     *     happens to be empty.
//     *
//     * Changes:
//     *
//     *    - None.
//     *
//     *************************************************************************/
//
//    private ActionPtr parse_imp_prod_insert_actions_attribute()
//        throws SystemErrorException
//    {
//        final String mName = 
//                "macshapa_odb_reader::parse_imp_prod_insert_actions_attribute()";
//        final String missing_insert_actions_mssg = 
//                "An INSERT-ACTIONS> attribute appears not to contain a value.  " +
//                "The associated import production will be discarded.\n";
//        final String insert_actions_type_mismatch_mssg = 
//                "The value of an INSERT-ACTIONS> attribute must be a list.  " +
//                "The associated import production will be discarded.\n";
//        ActionPtr action_list_ptr;
//
//        action_list_ptr = NULL;
//
//        if ( this.abort_parse )
//        {
//            throw new SystemErrorException(mName + 
//                    "this.abort_parse TRUE on entry");
//        }
//        
//        /* parse the attribute */
//
//        /* first parse the leading left parenthesis */
//
//        if ( (this.l0_tok).code == L_PAREN_TOK )
//        {
//            get_next_token();
//        }
//        else /* we shouldn't have been called unless the next token is a '(' */
//        {
//            throw new SystemErrorException(mName + 
//                    "(this.l0_tok).code != L_PAREN_TOK.");
//        }
//
//        /* read the a-list entry name */
//        if ( ! this.abort_parse )
//        {
//            if ( ( (this.l0_tok).code == ALIST_LABEL_TOK ) && 
//                 ( (this.l0_tok).aux == INSERT_ACTIONS_LABEL ) )
//            {
//                get_next_token();
//            }
//            else /* we shouldn't have been called unless the next token is an a-list tag */
//            {
//                throw new SystemErrorException(mName + 
//                        "this.l0_tok != INSERT-ACTIONS>.");
//            }
//        }
//
//        /* read the value associated with the a-list entry & discard any excess values */
//        if ( ! this.abort_parse )
//        {
//            switch ( (this.l0_tok).code )
//            {
//                case R_PAREN_TOK:
//                    post_warning_message(EMPTY_ALIST_ENTRY_WARN, 
//                                         missing_insert_actions_mssg); 
//                    break;
//
//                case L_PAREN_TOK:
//                    action_list_ptr = parse_imp_prod_insert_actions_list();
//                    break;
//
//                case INT_TOK:
//                case ERROR_TOK:
//                case SYMBOL_TOK:
//                case FLOAT_TOK:
//                case STRING_TOK:
//                case BOOL_TOK:
//                case ALIST_LABEL_TOK:
//                case PRIVATE_VAL_TOK:
//                case SETF_TOK:
//                case DB_VAR_TOK:
//                case QUOTE_TOK:
//                    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
//                                         insert_actions_type_mismatch_mssg); 
//
//                    if ( ! this.abort_parse )
//                    {
//                        get_next_token();
//                    }
//                    break;
//
//                case EOF_TOK:
//                    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                                       "EOF in an INSERT-ACTIONS> attribute.\n", 
//                                       true, true);
//                    break;
//
//                default:
//                    throw new SystemErrorException(mName + 
//                            "Encountered unknown token type.");
//                    break;
//            }
//        }
//
//        /* check for EOF */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code == EOF_TOK )
//            {
//                post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                                   "EOF in an INSERT-ACTIONS> attribute.\n", 
//                                   true, true);
//             }
//        }
//
//        /* discard any excess values that may appear in the a-list entry */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code != R_PAREN_TOK )
//            {
//                discard_excess_alist_entry_values();
//
//                if ( ! this.abort_parse )
//                {
//                    post_warning_message(EXCESS_VALUES_IN_ALIST_ENTRY_WARN,
//                            "Excess values encountered in an " +
//                            "INSERT-ACTIONS> attribute.\n");
//                }
//            }
//        }
//
//        /* read the terminating right parenthesis */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code == R_PAREN_TOK )
//            {
//                get_next_token();
//            }
//            else
//            {
//                /* since we are cleaning up any excess values in the insert 
//                 * actions attribute, this else clause is unreachable at 
//                 * present.
//                 */
//
//                post_warning_message(RIGHT_PAREN_EXPECTED_WARN, 
//                        "Closing parenthesis missing from an " +
//                        "INSERT-ACTIONS> attribute.\n");
//            }
//        }
//
//        return(action_list_ptr);
//
//    } /* macshapa_odb_reader::parse_imp_prod_insert_actions_attribute() */
//
//
//    /*************************************************************************
//     *
//     * parse_imp_prod_insert_actions_list()
//     *
//     * This method parses a list of import production insert actions, and 
//     * returns a pointer to a linked list of instance of Action.  Lists of 
//     * import production insert actions may be generated by the following 
//     * productions:
//     *
//     *     <imp_prod_insert_actions_list> --> 
//     *             '(' (<imp_prod_insert_action_alist>)* ')'
//     *
//     *     <imp_prod_insert_action_alist> --> 
//     *             '(' <imp_prod_insert_action_attributes> ')'
//     *
//     *     <imp_prod_insert_action_attributes> --> 
//     *             ( <imp_prod_insert_action_var_attribute> 
//     *               <imp_prod_insert_action_onset_attribute> 
//     *               <imp_prod_insert_action_offset_attribute> 
//     *               <imp_prod_insert_action_val_attribute> )
//     *
//     *     <imp_prod_insert_action_var_attribute> --> 
//     *             '(' 'VAR>' <text_quote_string> ')'
//     *
//     *     <imp_prod_insert_action_onset_attribute> --> 
//     *             '(' 'ONSET>' <text_quote_string> ')'
//     *
//     *     <imp_prod_insert_action_offset_attribute> --> 
//     *             '(' 'OFFSET>' <text_quote_string> ')'
//     *
//     *     <imp_prod_insert_action_val_attribute> --> 
//     *             '(' 'VAL>' <text_quote_string> ')'
//     *
//     * The function returns a pointer to the first element in a
//     * linked list of instances of the Action structure, whose type
//     * definition may be found in import.h.  All instances of Action
//     * in this structure will be of INSERT_ACTION kind.  
//     *
//     * Note that a NULL pointer will be returned if the list is empty.
//     *
//     *                                              - 7/13/08
//     *
//     * Parameters:
//     *
//     *    - None.
//     *
//     * Returns:  Pointer to the first element in a linked list of 
//     *     instances of the Action structure, or NULL if the list
//     *     happens to be empty.
//     *
//     * Changes:
//     *
//     *    - None.
//     *
//     *************************************************************************/
//
//    private ActionPtr parse_imp_prod_insert_actions_list()
//        throws SystemErrorException
//    {
//        final String mName = 
//                "macshapa_odb_reader::parse_imp_prod_insert_actions_list()";
//        boolean done;
//        ActionPtr action_ptr;
//        ActionPtr action_list_ptr;
//
//        action_ptr      = NULL; /* on general principles */
//        action_list_ptr = NULL; /* essential */
//
//        if ( this.abort_parse )
//        {
//            throw new SystemErrorException(mName + 
//                    "this.abort_parse TRUE on entry");
//        }
//
//        /* parse the import production insert actions list */
//
//        /* first parse the leading left parenthesis */
//
//        if ( (this.l0_tok).code == L_PAREN_TOK )
//        {
//            get_next_token();
//        }
//        else /* we shouldn't have been called unless the next token is a '(' */
//        {
//            throw new SystemErrorException(mName + 
//                    "(this.l0_tok).code != L_PAREN_TOK.");
//        }
//
//        /* now read the import production insert action a-lists */
//
//        done = FALSE;
//
//        while ( ( ! this.abort_parse ) && ( ! done ) )
//        {
//            switch ( (this.l0_tok).code )
//            {
//                case R_PAREN_TOK:
//                    done = true;
//                    get_next_token();
//                    break;
//
//                case L_PAREN_TOK:
//                    action_ptr = parse_imp_prod_insert_action_alist();
//
//                    if ( ! this.abort_parse )
//                    {
//                        if ( action_ptr != NULL )
//                        {
//                            action_list_ptr = AddActionListEnd(action_list_ptr, 
//                                                               action_ptr);
//                        }
//                    } 
//                    break;
//
//                case BOOL_TOK:
//                case ERROR_TOK:
//                case SYMBOL_TOK:
//                case FLOAT_TOK:
//                case STRING_TOK:
//                case INT_TOK:
//                case ALIST_LABEL_TOK:
//                case PRIVATE_VAL_TOK:
//                case SETF_TOK:
//                case DB_VAR_TOK:
//                case QUOTE_TOK:
//                    post_warning_message(ATOM_IN_IMP_PROD_INS_ACTIONS_LIST_WARN, 
//                                         null); 
//
//                    if ( ! this.abort_parse )
//                    {
//                        get_next_token();
//                    }
//                    break;
//
//                case EOF_TOK:
//                    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                            "EOF in an import production insert actions list.\n", 
//                            true, true);
//                    break;
//
//                 default:
//                     throw new SystemErrorException(mName + 
//                             "Encountered unknown token type.");
//                     break;
//             }
//        }
//
//        if ( ( ! this.abort_parse ) && ( action_list_ptr == NULL ) )
//        {
//            post_warning_message(EMPTY_IMPORT_PROD_INS_ACTION_LIST_WARN, 
//                                 null); 
//        }
//
//        return(action_list_ptr);
//
//    } /* macshapa_odb_reader::parse_imp_prod_insert_actions_list() */
//
//
//    /*************************************************************************
//     *
//     * parse_imp_prod_insert_action_val_attribute()
//     *
//     * This method parses an import production insert action val attribute, 
//     * and returns a pointer to a CString containing the contents of the 
//     * <text_quote_string>.  This attribute is generated by the following 
//     * production:
//     *
//     * <imp_prod_insert_action_val_attribute> --> 
//     *                                  '(' 'VAL>' <text_quote_string> ')'
//     *
//     *                                              - 7/13/08
//     *
//     * Parameters:
//     *
//     *    - None.
//     *
//     * Returns: Refernce to a String containing the string associated
//     *          with the VAL> attribute, or the empty string if no string is 
//     *          found.
//     *
//     * Changes:
//     *
//     *    - None.
//     *
//     *************************************************************************/
//
//    String parse_imp_prod_insert_action_val_attribute()
//
//    {
//        final String mName = 
//                "macshapa_odb_reader::parse_imp_prod_insert_action_val_attribute()";
//        final String missing_val_mssg = 
//                "An VAL> attribute in an import production insert action " +
//                "appears not to contain a value.  Value forced to the empty " +
//                "string.\n";
//        final String val_type_mismatch_mssg = 
//                "The value associated with a VAL> attribute in an import " +
//                "production insert action must be a quoted string.  Value " +
//                "forced to the empty string.\n";
//        String str;
//
//        str = NULL;
//
//        if ( this.abort_parse )
//        {
//            throw new SystemErrorException(mName + 
//                    "this.abort_parse TRUE on entry");
//        }
//        
//        /* parse the val attribute */
//
//        /* first parse the leading left parenthesis */
//
//        if ( (this.l0_tok).code == L_PAREN_TOK )
//        {
//            get_next_token();
//        }
//        else /* we shouldn't have been called unless the next token is a '(' */
//        {
//            throw new SystemErrorException(mName + 
//                    "(this.l0_tok).code != L_PAREN_TOK.");
//        }
//
//        /* read the a-list entry name */
//        if ( ! this.abort_parse )
//        {
//            if ( ( (this.l0_tok).code == ALIST_LABEL_TOK ) && 
//                 ( (this.l0_tok).aux == VAL_LABEL ) )
//            {
//                get_next_token();
//            }
//            else /* we shouldn't have been called unless the next token is an a-list tag */
//            {
//                throw new SystemErrorException(mName + "this.l0_tok != VAL>.");
//            }
//        }
//
//        /* read the value associated with the a-list entry & discard any excess values */
//        if ( ! this.abort_parse )
//        {
//            switch ( (this.l0_tok).code )
//            {
//                case STRING_TOK:
//                    str = new String((this.l0_tok).str.toString());
//
//                    if ( ! this.abort_parse )
//                    {
//                        get_next_token();
//                    }
//                    break;
//
//                case R_PAREN_TOK:
//                    post_warning_message(EMPTY_ALIST_ENTRY_WARN, 
//                                         missing_val_mssg); 
//                    break;
//
//                case L_PAREN_TOK:
//                    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
//                                         val_type_mismatch_mssg); 
//
//                    if ( ! this.abort_parse )
//                    {
//                        parse_arbitrary_list();
//                    }
//                    break;
//
//                case ERROR_TOK:
//                case SYMBOL_TOK:
//                case FLOAT_TOK:
//                case INT_TOK:
//                case BOOL_TOK:
//                case ALIST_LABEL_TOK:
//                case PRIVATE_VAL_TOK:
//                case SETF_TOK:
//                case DB_VAR_TOK:
//                case QUOTE_TOK:
//                    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
//                                         val_type_mismatch_mssg); 
//
//                    if ( ! this.abort_parse )
//                    {
//                        get_next_token();
//                    }
//                    break;
//
//                case EOF_TOK:
//                    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                                       "EOF in a VAL> attribute.\n", 
//                                       true, true);
//                    break;
//
//                 default:
//                     throw new SystemErrorException(mName + 
//                             "Encountered unknown token type.");
//                     break;
//             }
//        }
//
//        /* Check for EOF */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code == EOF_TOK )
//            {
//                post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                                   "EOF in a VAL> attribute.\n",
//                                   true, true);
//            }
//        }
//
//        /* discard any excess values that may appear in the VAL> a-list entry */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code != R_PAREN_TOK )
//            {
//                discard_excess_alist_entry_values();
//
//                if ( ! this.abort_parse )
//                {
//                    post_warning_message(EXCESS_VALUES_IN_ALIST_ENTRY_WARN,
//                            "Excess values encountered in a VAL> attribute.\n");
//                }
//            }
//        }
//
//        /* read the terminating right parenthesis */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code == R_PAREN_TOK )
//            {
//                get_next_token();
//            }
//            else
//            {
//                /* since we are cleaning up any excess values in the val */
//                /* attribute, this else clause should be unreachable.    */
//
//                post_warning_message(RIGHT_PAREN_EXPECTED_WARN,
//                        "Closing parenthesis missing from a VAL> attribute.\n");
//            }
//        }
//
//        if ( ( ! this.abort_parse ) && ( str == null ) )
//        {
//            /* never return NULL under normal circumstances -- return */
//            /* a pointer to an empty string instead                   */
//
//            str = new String("");
//
//        }
//
//        return(str_ptr);
//
//    } /* macshapa_odb_reader::parse_imp_prod_insert_action_val_attribute() */
//
//
//    /*************************************************************************
//     *
//     * parse_imp_prod_insert_action_var_attribute()
//     *
//     * This method parses an import production insert action var attribute, 
//     * and returns a referende to a String containing the contents of the 
//     * <text_quote_string>.  This attribute is generated by the following 
//     * production:
//     *
//     * <imp_prod_insert_action_var_attribute> --> 
//     *                          '(' 'VAR>' <text_quote_string> ')'
//     *
//     *                                              - 7/13/08
//     *
//     * Parameters:
//     *
//     *   - None.
//     *
//     * Returns: Refernce to a String containing the string associated
//     *          with the VAR> attribute, or the empty string if no string is 
//     *          found.
//     *
//     * Changes:
//     *
//     *    - None.
//     *
//     *************************************************************************/
//
//    final String parse_imp_prod_insert_action_var_attribute()
//        throws SystemErrorException
//    {
//        final String mName = 
//                "macshapa_odb_reader::parse_imp_prod_insert_action_var_attribute()";
//        final String missing_var_mssg = 
//                "A VAR> attribute in an import production insert action " +
//                "appears not to contain a value.  " +
//                "Value forced to the empty string.\n";
//        final String var_type_mismatch_mssg = 
//                "The value associated with a VAR> attribute in an import " +
//                "production insert action must be a quoted string.  " +
//                "Value forced to the empty string.\n";
//        String str;
//
//        str = null;
//
//        if ( this.abort_parse )
//        {
//            throw new SystemErrorException(mName + 
//                    "this.abort_parse TRUE on entry");
//        }
//        
//        /* parse the var attribute */
//
//        /* first parse the leading left parenthesis */
//
//        if ( (this.l0_tok).code == L_PAREN_TOK )
//        {
//            get_next_token();
//        }
//        else /* we shouldn't have been called unless the next token is a '(' */
//        {
//            throw new SystemErrorException(mName + 
//                    "(this.l0_tok).code != L_PAREN_TOK.");
//        }
//
//        /* read the a-list entry name */
//        if ( ! this.abort_parse )
//        {
//            if ( ( (this.l0_tok).code == ALIST_LABEL_TOK ) && 
//                 ( (this.l0_tok).aux == VAR_LABEL ) )
//            {
//                get_next_token();
//            }
//            else /* we shouldn't have been called unless the next token is an a-list tag */
//            {
//                throw new SystemErrorException(mName + "this.l0_tok != VAR>.");
//            }
//        }
//
//        /* read the value associated with the a-list entry & discard 
//         * any excess values 
//         */
//        if ( ! this.abort_parse )
//        {
//            switch ( (this.l0_tok).code )
//            {
//                case STRING_TOK:
//                    str_ptr = new String((this.l0_tok).str.toString());
//
//                    if ( ! this.abort_parse )
//                    {
//                        get_next_token();
//                    }
//                    break;
//
//                case R_PAREN_TOK:
//                    post_warning_message(EMPTY_ALIST_ENTRY_WARN,
//                                         missing_var_mssg); 
//                    break;
//
//                case L_PAREN_TOK:
//                    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
//                                         var_type_mismatch_mssg); 
//
//                    if ( ! this.abort_parse )
//                    {
//                        parse_arbitrary_list();
//                    }
//                    break;
//
//                case ERROR_TOK:
//                case SYMBOL_TOK:
//                case FLOAT_TOK:
//                case INT_TOK:
//                case BOOL_TOK:
//                case ALIST_LABEL_TOK:
//                case PRIVATE_VAL_TOK:
//                case SETF_TOK:
//                case DB_VAR_TOK:
//                case QUOTE_TOK:
//                    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
//                                         var_type_mismatch_mssg); 
//
//                    if ( ! this.abort_parse )
//                    {
//                        get_next_token();
//                    }
//                    break;
//
//                case EOF_TOK:
//                    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                                       "EOF in a VAR> attribute.\n", 
//                                       true, true);
//                    break;
//
//                default:
//                    throw new SystemErrorException(mName + 
//                            "Encountered unknown token type.");
//                    break;
//            }
//        }
//
//        /* Check for EOF */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code == EOF_TOK )
//            {
//                post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                                   "EOF in a VAR> attribute.\n",
//                                   true, true);
//            }
//        }
//
//        /* discard any excess values that may appear in the NAME> a-list entry */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code != R_PAREN_TOK )
//            {
//                discard_excess_alist_entry_values();
//
//                if ( ! this.abort_parse )
//                {
//                    post_warning_message(EXCESS_VALUES_IN_ALIST_ENTRY_WARN,
//                            "Excess values encountered in a VAR> attribute.\n");
//                }
//            }
//        }
//
//        /* read the terminating right parenthesis */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code == R_PAREN_TOK )
//            {
//                get_next_token();
//            }
//            else
//            {
//                /* since we are cleaning up any excess values in the var */
//                /* attribute, this else clause should be unreachable.    */
//
//                post_warning_message(RIGHT_PAREN_EXPECTED_WARN, 
//                        "Closing parenthesis missing from a VAR> attribute.\n");
//            }
//        }
//
//        if ( ( ! this.abort_parse ) && ( str_ptr == null ) )
//        {
//            /* never return NULL under normal circumstances -- return */
//            /* a pointer to an empty string instead                   */
//
//            str = new String("");
//        }
//
//        return(str);
//
//    } /* macshapa_odb_reader::parse_imp_prod_insert_action_var_attribute() */
//
//
//    /**************************************************************************
//     *
//     * parse_imp_prod_is_shown_attribute()
//     *
//     * This method parses an import production is shown attribute, and returns
//     * a Boolean containing value associated with the attribute, or false if no
//     * value is found.  The is shown attribute is generated by the following
//     * production:
//     *
//     *     <imp_prod_is_shown_attribute> --> '(' 'IS-SHOWN>' <boolean> ')'
//     *
//     *                                              - 7/13/08
//     *
//     * Parameters:
//     *
//     *    - None.
//     *
//     * Returns: Boolean value associated with the attribute, or false if
//     *          no value is found or an error occurs.
//     *
//     * Changes:
//     *
//     *    - None.
//     *
//     *************************************************************************/
//
//    private boolean parse_imp_prod_is_shown_attribute()
//        throws SystemErrorException
//    {
//        final String mName = 
//                "macshapa_odb_reader::parse_imp_prod_is_shown_attribute()";
//        final String missing_is_shown_mssg = 
//                "An IS-SHOWN> attribute appears not to contain a value.  " +
//                "Value forced to FALSE.\n";
//        final String is_shown_type_mismatch_mssg = 
//                "The value of an IS-SHOWN> attribute must be either TRUE " +
//                "or FALSE.  Value forced to FALSE.\n";
//        boolean is_shown;
//
//        is_shown = false;
//
//        if ( this.abort_parse )
//        {
//            throw new SystemErrorException(mName + 
//                    "this.abort_parse TRUE on entry");
//        }
//        
//        /* parse the version attribute */
//
//        /* first parse the leading left parenthesis */
//
//        if ( (this.l0_tok).code == L_PAREN_TOK )
//        {
//            get_next_token();
//        }
//        else /* we shouldn't have been called unless the next token == a '(' */
//        {
//            throw new SystemErrorException(mName + 
//                    "(this.l0_tok).code != L_PAREN_TOK.");
//        }
//
//        /* read the a-list entry name */
//        if ( ! this.abort_parse )
//        {
//            if ( ( (this.l0_tok).code == ALIST_LABEL_TOK ) && 
//                 ( (this.l0_tok).aux == IS_SHOWN_LABEL ) )
//            {
//                get_next_token();
//            }
//            else /* we shouldn't have been called unless the next token is an a-list tag */
//            {
//                throw new SystemErrorException(mName + 
//                        "this.l0_tok != IS-SHOWN>.");
//            }
//        }
//
//        /* read the value associated with the a-list entry & discard any excess values */
//        if ( ! this.abort_parse )
//        {
//            switch ( (this.l0_tok).code )
//            {
//                case BOOL_TOK:
//                    is_shown = (Boolean)((this.l0_tok).aux);
//                    get_next_token();
//                    break;
//
//                case R_PAREN_TOK:
//                    post_warning_message(EMPTY_ALIST_ENTRY_WARN, 
//                                         missing_is_shown_mssg); 
//                    break;
//
//                case L_PAREN_TOK:
//                    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
//                                         is_shown_type_mismatch_mssg); 
//
//                    if ( ! this.abort_parse )
//                    {
//                        parse_arbitrary_list();
//                    }
//                    break;
//
//                case ERROR_TOK:
//                case SYMBOL_TOK:
//                case FLOAT_TOK:
//                case STRING_TOK:
//                case INT_TOK:
//                case ALIST_LABEL_TOK:
//                case PRIVATE_VAL_TOK:
//                case SETF_TOK:
//                case DB_VAR_TOK:
//                case QUOTE_TOK:
//                    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
//                                         is_shown_type_mismatch_mssg); 
//
//                    if ( ! this.abort_parse )
//                    {
//                        get_next_token();
//                    }
//                    break;
//
//                case EOF_TOK:
//                    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                                       "EOF in an IS-SHOWN> attribute.\n", 
//                                       true, true);
//                    break;
//
//                default:
//                    throw new SystemErrorException(mName + 
//                            "Encountered unknown token type.");
//                    break;
//             }
//        }
//
//        /* check for EOF */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code == EOF_TOK )
//            {
//                post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                                   "EOF in an IS-SHOWN> attribute.\n", 
//                                   true, true);
//            }
//        }
//
//        /* discard any excess values that may appear in the IS-SHOWN> a-list entry */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code != R_PAREN_TOK )
//            {
//                discard_excess_alist_entry_values();
//
//                if ( ! this.abort_parse )
//                {
//                    post_warning_message(EXCESS_VALUES_IN_ALIST_ENTRY_WARN,
//                        "Excess values encountered an IS-SHOWN> attribute.\n");
//                }
//            }
//        }
//
//        /* read the terminating right parenthesis */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code == R_PAREN_TOK )
//            {
//                get_next_token();
//            }
//            else
//            {
//                /* since we are cleaning up any excess values in the is shown
//                 * attribute, this else clause is unreachable at present.  
//                 * Should we choose to drop the above attempt at error recovery,
//                 * this clause will again become reachable.
//                 */
//
//                post_warning_message(RIGHT_PAREN_EXPECTED_WARN, 
//                    "Closing parenthesis missing from an IS-SHOWN> attribute.\n");
//            }
//        }
//
//        return(is_shown);
//
//    } /* macshapa_odb_reader::parse_imp_prod_is_shown_attribute() */
//
//
//    /*************************************************************************
//     *
//     * parse_imp_prod_is_traced_attribute()
//     *
//     * This method parses an import production is traced attribute, and returns
//     * a Boolean containing value associated with the attribute, or FALSE if no
//     * value is found.  The is traced attribute is generated by the following
//     * production:
//     *
//     *     <imp_prod_is_traced_attribute> --> '(' 'IS-TRACED>' <boolean> ')'
//     *
//     *                                              - 7/13/08
//     *
//     * Parameters:
//     *
//     *    - None.
//     *
//     * Returns: Boolean value associated with the attribute, or false if
//     *          no value is found or an error occurs.
//     *
//     * Changes:
//     *
//     *    - None.
//     *
//     *************************************************************************/
//
//    private boolean parse_imp_prod_is_traced_attribute()
//
//    {
//        final String mName = 
//                "macshapa_odb_reader::parse_imp_prod_is_traced_attribute()";
//        final String missing_is_traced_mssg = 
//                "An IS-TRACED> attribute appears not to contain a value.  " +
//                "Value forced to FALSE.\n";
//        final String is_traced_type_mismatch_mssg = 
//                "The value of an IS-TRACED> attribute must be either " +
//                "TRUE or FALSE.  Value forced to FALSE.\n";
//        boolean is_traced;
//
//        is_traced = false;
//
//        if ( this.abort_parse )
//        {
//            throw new SystemErrorException(mName + 
//                    "this.abort_parse TRUE on entry");
//        }
//        
//        /* parse the version attribute */
//
//        /* first parse the leading left parenthesis */
//
//        if ( (this.l0_tok).code == L_PAREN_TOK )
//        {
//            get_next_token();
//        }
//        else /* we shouldn't have been called unless the next token is a '(' */
//        {
//            throw new SystemErrorException(mName + 
//                    "(this.l0_tok).code != L_PAREN_TOK.");
//        }
//
//        /* read the a-list entry name */
//        if ( ! this.abort_parse )
//        {
//            if ( ( (this.l0_tok).code == ALIST_LABEL_TOK ) && 
//                 ( (this.l0_tok).aux == IS_TRACED_LABEL ) )
//            {
//                get_next_token();
//            }
//            else /* we shouldn't have been called unless the next token is an a-list tag */
//            {
//                throw new SystemErrorException(mName + 
//                        "this.l0_tok != IS-TRACED>.");
//            }
//        }
//
//        /* read the value associated with the a-list entry & discard any excess values */
//        if ( ! this.abort_parse )
//        {
//            switch ( (this.l0_tok).code )
//            {
//                case BOOL_TOK:
//                    is_traced = (boolean)((this.l0_tok).aux);
//                    get_next_token();
//                    break;
//
//                case R_PAREN_TOK:
//                    post_warning_message(EMPTY_ALIST_ENTRY_WARN, 
//                                         missing_is_traced_mssg); 
//                    break;
//
//                case L_PAREN_TOK:
//                    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
//                                         is_traced_type_mismatch_mssg); 
//
//                    if ( ! this.abort_parse )
//                    {
//                        parse_arbitrary_list();
//                    }
//                    break;
//
//                case ERROR_TOK:
//                case SYMBOL_TOK:
//                case FLOAT_TOK:
//                case STRING_TOK:
//                case INT_TOK:
//                case ALIST_LABEL_TOK:
//                case PRIVATE_VAL_TOK:
//                case SETF_TOK:
//                case DB_VAR_TOK:
//                case QUOTE_TOK:
//                    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
//                                         is_traced_type_mismatch_mssg); 
//
//                    if ( ! this.abort_parse )
//                    {
//                        get_next_token();
//                    }
//                    break;
//
//                case EOF_TOK:
//                    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                                       "EOF in an IS-TRACED> attribute.\n", 
//                                       true, true);
//                    break;
//
//                 default:
//                     throw new SystemErrorException(mName + 
//                             "Encountered unknown token type.");
//                     break;
//            }
//        }
//
//        /* check for EOF */
//        if ( ! this.abort_parse )
//        {
//             if ( (this.l0_tok).code == EOF_TOK )
//             {
//                post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                                   "EOF in an IS-TRACED> attribute.\n", 
//                                   true, true);
//             }
//        }
//
//        /* discard any excess values that may appear in the IS-TRACED> a-list entry */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code != R_PAREN_TOK )
//            {
//                discard_excess_alist_entry_values();
//
//                if ( ! this.abort_parse )
//                {
//                    post_warning_message(EXCESS_VALUES_IN_ALIST_ENTRY_WARN,
//                        "Excess values encountered an IS-TRACED> attribute.\n");
//                }
//            }
//        }
//
//        /* read the terminating right parenthesis */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code == R_PAREN_TOK )
//            {
//                get_next_token();
//            }
//            else
//            {
//                /* since we are cleaning up any excess values in the is traced
//                 * attribute, this else clause is unreachable at present.  
//                 * Should we choose to drop the above attempt at error recovery,
//                 * this clause will again become reachable.
//                 */
//
//                post_warning_message(RIGHT_PAREN_EXPECTED_WARN, 
//                        "Closing parenthesis missing from an IS-TRACED> " +
//                        "attribute.\n");
//            }
//        }
//
//        return(is_traced);
//
//    } /* macshapa_odb_reader::parse_imp_prod_is_traced_attribute() */
//
//
//    /*************************************************************************
//     *
//     * parse_imp_prod_list()
//     *
//     * This method parses a list of import productions.  This list is 
//     * generated by the following productions:
//     *
//     *     <imp_prod_list> --> '(' (<imp_prod>)* ')'
//     *
//     *     <imp_prod> --> '(' <imp_prod_alist> ')'
//     *
//     *     <imp_prod_alist> --> { <imp_prod_name_attribute>
//     *                            <imp_prod_pattern_attribute>
//     *                            <imp_prod_hard_prec_attribute>
//     *                            <imp_prod_is_shown_attribute>
//     *                            <imp_prod_is_traced_attribute>
//     *                            ( <imp_prod_insert_actions_attribute> |
//     *                              <imp_prod_program_actions_attribute> ) }
//     *
//     *     <imp_prod_name_attribute> --> '(' 'NAME>' <text_quote_string> ')'
//     *
//     *     <imp_prod_pattern_attribute> --> '(' 'PATTERN>' <text_quote_string> ')'
//     *
//     *     <imp_prod_hard_prec_attribute> --> '(' 'HARD-PREC>' <integer> ')'
//     *
//     *     <imp_prod_is_shown_attribute> --> '(' 'IS-SHOWN>' <boolean> ')'
//     *
//     *     <imp_prod_is_traced_attribute> --> '(' 'IS-TRACED>' <boolean> ')'
//     *
//     *     <imp_prod_insert_actions_attribute> --> 
//     *             '(' 'INSERT-ACTIONS>' <imp_prod_insert_actions_list> ')'
//     *
//     *     <imp_prod_insert_actions_list> --> 
//     *             '(' (<imp_prod_insert_action_alist>)* ')'
//     *
//     *     <imp_prod_insert_action_alist> --> 
//     *             '(' <imp_prod_insert_action_attributes> ')'
//     *
//     *     <imp_prod_insert_action_attributes> --> 
//     *             ( <imp_prod_insert_action_var_attribute> 
//     *               <imp_prod_insert_action_onset_attribute> 
//     *               <imp_prod_insert_action_offset_attribute> 
//     *               <imp_prod_insert_action_val_attribute> )
//     *
//     *     <imp_prod_insert_action_var_attribute> --> 
//     *             '(' 'VAR>' <text_quote_string> ')'
//     *
//     *     <imp_prod_insert_action_onset_attribute> --> 
//     *             '(' 'ONSET>' <text_quote_string> ')'
//     *
//     *     <imp_prod_insert_action_offset_attribute> --> 
//     *             '(' 'OFFSET>' <text_quote_string> ')'
//     *
//     *     <imp_prod_insert_action_val_attribute> --> 
//     *             '(' 'VAL>' <text_quote_string> ')'
//     *
//     *     <imp_prod_program_actions_attribute> --> 
//     *             '(' 'PROGRAM-ACTIONS>' <imp_prod_program_actions_list> ')'
//     *
//     *     <imp_prod_program_actions_list> --> 
//     *             '(' (<imp_prod_program_action_alist>)* ')'
//     *
//     *     <imp_prod_program_action_alist> --> 
//     *             '(' <imp_prod_program_action_attributes> ')'
//     *
//     *     <imp_prod_program_action_attributes> --> 
//     *             ( <imp_prod_program_action_text_attribute> )
//     *
//     *     <imp_prod_program_action_text_attribute> -->
//     *             '(' 'TEXT>' <text_quote_string> ')' 
//     *
//     *                                              - 7/13/08
//     *
//     * Parameters:
//     *
//     *    - format_ptr:  Pointer to an instance of the Format structure, whose
//     *          type definition may be found in import.h.  format_ptr points
//     *          to the instance of format into which we will load the import 
//     *          productions as we read them. 
//     *
//     * Returns:  Void.
//     *
//     * Changes:
//     *
//     *    - None.
//     *
//     *************************************************************************/
//
//    private void parse_imp_prod_list(FormatPtr format_ptr)
//        throws SystemErrorException
//    {
//        final String mName = "macshapa_odb_reader::parse_imp_prod_list()";
//        boolean done;
//
//        if ( this.abort_parse )
//        {
//            throws SystemErrorException(mName + 
//                    "this.abort_parse TRUE on entry");
//        }
//        
//        if ( ( format_ptr == NULL ) || ( format_ptr->name == NULL ) || 
//             ( format_ptr->prods != NULL ) || ( format_ptr->next != NULL ) )
//        {
//            throw new SystemErrorException(mName + ]
//                    "format_ptr seems to be corrupt on entry");
//        }
//
//        /* parse the list of import productions */
//        
//        /* first parse the leading left parenthesis */
//
//        if ( (this.l0_tok).code == L_PAREN_TOK )
//        {
//            get_next_token();
//        }
//        else /* we shouldn't have been called unless the next token == a '(' */
//        {
//            throw new SystemErrorException(mName + 
//                    "(this.l0_tok).code != L_PAREN_TOK.");
//        }
//
//        /* now read the list of import productions */
//
//        done = FALSE;
//
//        while ( ( ! this.abort_parse ) && ( ! done ) )
//        {
//            switch ( (this.l0_tok).code )
//            {
//                case R_PAREN_TOK:
//                    done = true;
//                    get_next_token();
//                    break;
//
//                case L_PAREN_TOK:
//                    parse_imp_prod(format_ptr);
//                    break;
//
//                case BOOL_TOK:
//                case ERROR_TOK:
//                case SYMBOL_TOK:
//                case FLOAT_TOK:
//                case STRING_TOK:
//                case INT_TOK:
//                case ALIST_LABEL_TOK:
//                case PRIVATE_VAL_TOK:
//                case SETF_TOK:
//                case DB_VAR_TOK:
//                case QUOTE_TOK:
//                    post_warning_message(ATOM_IN_IMPORT_PRODUCTIONS_LIST_WARN, 
//                                         null); 
//
//                    if ( ! this.abort_parse )
//                    {
//                        get_next_token();
//                    }
//                    break;
//
//                case EOF_TOK:
//                    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                                       "EOF in an import productions list.\n", 
//                                       true, true);
//                    break;
//
//                 default:
//                     throw new SystemErrorException(mName + 
//                             "Encountered unknown token type.");
//                     break;
//             }
//        }
//
//        return;
//
//    } /* macshapa_odb_reader::parse_imp_prod_list() */
//
//
//    /*************************************************************************
//     *
//     * parse_imp_prod_name_attribute()
//     *
//     * This method parses an import production name attribute, and returns
//     * a refereence to a String containing the contents of the 
//     * <text_quote_string>.  Import production name attributes are generated 
//     * by the following production:
//     *
//     *     <imp_prod_name_attribute> --> '(' 'NAME>' <text_quote_string> ')'
//     *
//     *                                              - 7/13/08
//     *
//     * Parameters:
//     *
//     *    - None.
//     *
//     * Returns: Reference to a String containing the name associated
//     *          with the NAME> attribute, or the empty string if no name is 
//     *          found.
//     *
//     * Changes:
//     *
//     *    - None.
//     *
//     *******************************************************************************/
//
//    private String parse_imp_prod_name_attribute()
//        throws SystemErrorException
//    {
//        final String mName = 
//                "macshapa_odb_reader::parse_imp_prod_name_attribute()";
//        final String missing_prod_name_mssg = 
//                "A NAME> attribute in an import production appears not to " +
//                "contain a value.  The attribute will be ignored.\n";
//        final String prod_name_type_mismatch_mssg = 
//                "The value associated with the NAME> attribute in an import " +
//                "production must be a quoted string.  " +
//                "The attribute will be ignored.\n";
//        String name = null;
//
//        if ( this.abort_parse )
//        {
//            throw new SystemErrorException(mName + 
//                    "this.abort_parse TRUE on entry");
//        }
//        
//        /* parse the version attribute */
//
//        /* first parse the leading left parenthesis */
//
//        if ( (this.l0_tok).code == L_PAREN_TOK )
//        {
//            get_next_token();
//        }
//        else /* system error - we shouldn't have been called unless the next token is a '(' */
//        {
//            throw new SystemErrorException(mName + 
//                    "(this.l0_tok).code != L_PAREN_TOK.");
//        }
//
//        /* read the a-list entry name */
//        if ( ! this.abort_parse )
//        {
//            if ( ( (this.l0_tok).code == ALIST_LABEL_TOK ) && 
//                 ( (this.l0_tok).aux == NAME_LABEL ) )
//            {
//                get_next_token();
//            }
//            else /* we shouldn't have been called unless the next token is an a-list tag */
//            {
//                throw new SystemErrorException(mName + "this.l0_tok != NAME>.");
//            }
//        }
//
//        /* read the value associated with the a-list entry & discard any excess values */
//        if ( ! this.abort_parse )
//        {
//            switch ( (this.l0_tok).code )
//            {
//                case STRING_TOK:
//                    /* the following 255 character limit on the length of a 
//                     * pattern name is a legacy of the old import save and 
//                     * reload routines.  Don't change it without changing 
//                     * those routines as well.
//                     */
//
//                    if ( (this.l0_tok).str_len > 255 )
//                    {
//                         name_ptr = NULL;
//
//                         post_warning_message(IMPORT_PROD_NAME_TOO_LONG_WARN, 
//                                              null); 
//                    }
//                    else
//                    {
//                         name = new String(strTrim((this.l0_tok).str.toString()));
//
//                    }
//
//                    if ( ! this.abort_parse )
//                    {
//                        get_next_token();
//                    }
//                    break;
//
//                case R_PAREN_TOK:
//                    post_warning_message(EMPTY_ALIST_ENTRY_WARN, 
//                                         missing_prod_name_mssg); 
//                    break;
//
//                case L_PAREN_TOK:
//                    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
//                                         prod_name_type_mismatch_mssg); 
//
//                    if ( ! this.abort_parse )
//                    {
//                        parse_arbitrary_list();
//                    }
//                    break;
//
//                case ERROR_TOK:
//                case SYMBOL_TOK:
//                case FLOAT_TOK:
//                case INT_TOK:
//                case BOOL_TOK:
//                case ALIST_LABEL_TOK:
//                case PRIVATE_VAL_TOK:
//                case SETF_TOK:
//                case DB_VAR_TOK:
//                case QUOTE_TOK:
//                    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
//                                         prod_name_type_mismatch_mssg); 
//
//                    if ( ! this.abort_parse )
//                    {
//                        get_next_token();
//                    }
//                    break;
//
//
//                case EOF_TOK:
//                    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                            "EOF in an import format NAME> attribute.\n", 
//                            true, true);
//                    break;
//
//                default:
//                    throw new SystemErrorException(mName + 
//                            "Encountered unknown token type.");
//                    break;
//             }
//        }
//
//        /* Check for EOF */
//        if ( ! this.abort_parse )
//        {
//             if ( (this.l0_tok).code == EOF_TOK )
//             {
//                post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                        "EOF in an import format NAME> attribute.\n",
//                        true, true);
//             }
//        }
//
//        /* discard any excess values that may appear in the NAME> a-list entry */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code != R_PAREN_TOK )
//            {
//                discard_excess_alist_entry_values();
//
//                if ( ! this.abort_parse )
//                {
//                    post_warning_message(EXCESS_VALUES_IN_ALIST_ENTRY_WARN,
//                            "Excess values encountered in an import " +
//                            "format NAME> attribute.\n");
//                }
//            }
//        }
//
//
//        /* read the terminating right parenthesis */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code == R_PAREN_TOK )
//            {
//                get_next_token();
//            }
//            else
//            {
//                /* since we are cleaning up any excess values in the import 
//                 * format name attribute, this else clause should be 
//                 * unreachable. 
//                 */
//
//                post_warning_message(RIGHT_PAREN_EXPECTED_WARN, 
//                        "Closing parenthesis missing from an import " +
//                        "format NAME> attribute.\n");
//            }
//        }
//
//        return(name_ptr);
//
//    } /* macshapa_odb_reader::parse_imp_prod_name_attribute() */
//
//
//    /*************************************************************************
//     *
//     * parse_imp_prod_pattern_attribute()
//     *
//     * This method parses an import production pattern attribute, and returns
//     * a reference to a String containing the contents of the
//     * <text_quote_string>.  Import production pattern attributes are 
//     * generated by the following production:
//     *
//     *     <imp_prod_pattern_attribute> --> '(' 'PATTERN>' <text_quote_string> ')'
//     *
//     *                                              - 7/13/08
//     *
//     * Parameters:
//     *
//     *    - None.
//     *
//     * Returns: Reference to a String containing the pattern string associated
//     *          with the PATTERN> attribute, or the empty string if no pattern 
//     *          string is found.
//     *
//     * Changes:
//     *
//     *    - None.
//     *
//     *************************************************************************/
//
//    private String parse_imp_prod_pattern_attribute()
//
//    {
//        final String mName = 
//                "macshapa_odb_reader::parse_imp_prod_pattern_attribute()";
//        final String missing_pattern_mssg = 
//                "A PATTERN> attribute in an import production appears not to " +
//                "contain a value.  The attribute will be ignored.\n";
//        final String pattern_type_mismatch_mssg = 
//                "The value associated with the PATTERN> attribute in an " +
//                "import production must be a quoted string.  " +
//                "The attribute will be ignored.\n";
//        String pattern_str = null;
//
//        if ( this.abort_parse )
//        {
//            throw new SystemErrorException(mName + 
//                    "this.abort_parse TRUE on entry");
//        }
//        
//        /* parse the version attribute */
//        
//        /* first parse the leading left parenthesis */
//
//        if ( (this.l0_tok).code == L_PAREN_TOK )
//        {
//            get_next_token();
//        }
//        else /* we shouldn't have been called unless the next token is a '(' */
//        {
//            throw new SystemErrorException(mName + 
//                    "(this.l0_tok).code != L_PAREN_TOK.");
//        }
//
//        /* read the a-list entry name */
//        if ( ! this.abort_parse )
//        {
//            if ( ( (this.l0_tok).code == ALIST_LABEL_TOK ) && 
//                 ( (this.l0_tok).aux == PATTERN_LABEL ) )
//            {
//                get_next_token();
//            }
//            else /* we shouldn't have been called unless the next token == an a-list tag */
//            {
//                throw new SystemErrorException(mName +
//                        "this.l0_tok != PATTERN>.");
//            }
//        }
//
//        /* read the value associated with the a-list entry & discard 
//         * any excess values 
//         */
//        if ( ! this.abort_parse )
//        {
//            switch ( (this.l0_tok).code )
//            {
//                case STRING_TOK:
//                    /* the following 255 character limit on the length of a 
//                     * pattern string is a legacy of the old import save and 
//                     * reload routines.  Don't change it without changing 
//                     * those routines as well.
//                     */
//
//                    if ( (this.l0_tok).str_len > 255 )
//                    {
//                        pattern_str_ptr = NULL;
//
//                        post_warning_message(IMPORT_PROD_PATTERN_TOO_LONG_WARN, 
//                                             null); 
//                    }
//                    else
//                    {
//                        pattern_str = 
//                            new String((skipspace((this.l0_tok).str_ptr.toString()));
//                    }
//
//                    if ( ! this.abort_parse )
//                    {
//                        get_next_token();
//                    }
//                    break;
//
//                case R_PAREN_TOK:
//                    post_warning_message(EMPTY_ALIST_ENTRY_WARN, 
//                                         missing_pattern_mssg); 
//                    break;
//
//                case L_PAREN_TOK:
//                    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
//                                          pattern_type_mismatch_mssg); 
//
//                    if ( ! this.abort_parse )
//                    {
//                        parse_arbitrary_list();
//                    }
//                    break;
//
//                case ERROR_TOK:
//                case SYMBOL_TOK:
//                case FLOAT_TOK:
//                case INT_TOK:
//                case BOOL_TOK:
//                case ALIST_LABEL_TOK:
//                case PRIVATE_VAL_TOK:
//                case SETF_TOK:
//                case DB_VAR_TOK:
//                case QUOTE_TOK:
//                    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
//                                         pattern_type_mismatch_mssg); 
//
//                    if ( ! this.abort_parse )
//                    {
//                        get_next_token();
//                    }
//                    break;
//
//
//                case EOF_TOK:
//                    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                            "EOF in an import format PATTERN> attribute.\n", 
//                            true, true);
//                    break;
//
//                 default:
//                     throw new SystmeErrorException(mName +
//                             "Encountered unknown token type.");
//                     break;
//             }
//        }
//
//        /* Check for EOF */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code == EOF_TOK )
//            {
//                post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                        "EOF in an import format PATTERN> attribute.\n",
//                        true, true);
//            }
//        }
//
//        /* discard any excess values that may appear in the NAME> a-list entry */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code != R_PAREN_TOK )
//            {
//                discard_excess_alist_entry_values();
//
//                if ( ! this.abort_parse )
//                {
//                    post_warning_message(EXCESS_VALUES_IN_ALIST_ENTRY_WARN,
//                        "Excess values encountered in an import format PATTERN> attribute.\n", 
//                        read_vars);
//                }
//            }
//        }
//
//
//        /* read the terminating right parenthesis */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code == R_PAREN_TOK )
//            {
//                get_next_token();
//            }
//            else
//            {
//                /* since we are cleaning up any excess values in the import
//                 * format name attribute, this else clause should be 
//                 * unreachable.
//                 */
//
//                post_warning_message(RIGHT_PAREN_EXPECTED_WARN, 
//                        "Closing parenthesis missing from an import format " +
//                        "PATTERN> attribute.\n");
//            }
//        }
//
//        if ( ( ! this.abort_parse ) && ( pattern_str == null ) )
//        {
//            /* never return NULL under normal circumstances -- return */
//            /* a pointer to an empty string instead                   */
//
//            pattern_str = new String("");
//        }
//
//        return(pattern_str);
//
//    } /* parse_imp_prod_pattern_attribute() */
//
//
//    /*************************************************************************
//     *
//     * parse_imp_prod_program_action_alist()
//     *
//     * This method parses an import production program action a-list, and 
//     * returns a pointer to an instance of Action which has been loaded with 
//     * the data obtained from the a-list.  Import production program action 
//     * a-lists may be generated by the following productions:
//     *
//     *     <imp_prod_program_action_alist> --> 
//     *             '(' <imp_prod_program_action_attributes> ')'
//     *
//     *     <imp_prod_program_action_attributes> --> 
//     *             ( <imp_prod_program_action_text_attribute> )
//     *
//     *     <imp_prod_program_action_text_attribute> -->
//     *             '(' 'TEXT>' <text_quote_string> ')' 
//     *
//     * The function returns NULL if an error is detected, or if a required
//     * attribute is missing.
//     *
//     *                                              - 7/13/08
//     *
//     * Parameters:
//     *
//     *    - None.
//     *
//     * Returns:  Pointer an instance of the Action structure, or NULL if 
//     *     any errors are detected.
//     *
//     * Changes:
//     *
//     *    - None.
//     *
//     *************************************************************************/
//
//    private ActionPtr parse_imp_prod_program_action_alist()
//
//    {
//        final String mName = 
//                "macshapa_odb_reader::parse_imp_prod_program_action_alist()";
//        String text_str = null;
//        boolean have_text_str;
//        boolean done;
//        ActionPtr action_ptr;
//
//        action_ptr = NULL;
//
//        if ( this.abort_parse )
//        {
//            throw new SystemErrorException(mName + 
//                    "this.abort_parse TRUE on entry");
//        }
//        
//        /* parse the attribute list */
//        
//        /* first parse the leading left parenthesis */
//
//        if ( (this.l0_tok).code == L_PAREN_TOK )
//        {
//            if ( ( (this.l1_tok).code == L_PAREN_TOK ) || 
//                 ( (this.l1_tok).code == R_PAREN_TOK ) )
//            {
//                get_next_token();
//            }
//            else if ( (this.l1_tok).code == ALIST_LABEL_TOK )
//            {
//                post_warning_message(LEFT_PAREN_EXPECTED_WARN, 
//                        "The opening left parenthesis of an import " +
//                        "production program action a-list appears to " +
//                        "be missing.\n");
//            }
//            else 
//            {
//                /* if a left paren is missing, the first item in the a-list is 
//                 * not an a-list entry.  If we try to recover from this error 
//                 * here, we will only confuse things further.  Thus we eat the 
//                 * left parenthesis & let the cards fall where they may.
//                 */
//
//                get_next_token();
//            }
//        }
//        else /* we shouldn't have been called unless the next token is a '(' */
//        {
//            throw new SystemErrorException(mName + 
//                    "(this.l0_tok).code != L_PAREN_TOK.");
//        }
//
//        done          = false;
//        have_text_str = false;
//        text_str_ptr  = null;
//
//        /* now parse the a-list */
//
//        while ( ( ! this.abort_parse ) && 
//                ( ! done ) )
//        {
//            if ( (this.l0_tok).code == L_PAREN_TOK )
//            {
//                if ( (this.l1_tok).code == ALIST_LABEL_TOK )
//                {
//                    switch ( (this.l1_tok).aux )
//                    {
//                        case TEXT_LABEL:
//                            if ( ! have_text_str )
//                            {
//                                have_text_str = true;
//                                text_str = 
//                                    parse_imp_prod_program_action_text_attribute();
//                            }
//                            else
//                            {
//                                post_warning_message(DUPLICATE_ALIST_ENTRY_WARN, 
//                                        "Duplicate TEXT> entry in an import " +
//                                        "production program action.\n");
//
//                                if ( ! this.abort_parse )
//                                {
//                                     parse_unknown_alist_entry();
//                                }
//                            }
//                            break;
//
//                        default:
//                            post_warning_message(
//                                    UNKNOWN_OR_UNEXPECTED_ALIST_ENTRY_WARN, 
//                                    "The entry == located in an import " +
//                                    "production program action.\n");
//
//                            if ( ! this.abort_parse )
//                            {
//                                 parse_unknown_alist_entry();
//                            }
//                            break;
//                    }
//                }
//                else /* a-list contains a list that is not an a-list entry. */
//                     /* read it & discard it.                               */
//                {
//                    post_warning_message(NON_ALIST_ENTRY_LIST_IN_ALIST_WARN, 
//                            "The list is located in an import production " +
//                            "program action.\n");
//
//                    if ( ! this.abort_parse )
//                    {
//                         parse_arbitrary_list();
//                    }
//                }
//            }
//            else if ( (this.l0_tok).code == R_PAREN_TOK )
//            {
//                done = true;
//                get_next_token();
//            }
//            else if ( (this.l0_tok).code == EOF_TOK )
//            {
//                done = true;
//                post_error_message(UNEXPECTED_END_OF_FILE_ERR,
//                        "EOF occurred in an import production program action.\n",
//                        true, true);
//            }
//            else /* (this.l0_tok).code isnt '(', ')', or EOF */
//            {
//                post_warning_message(NON_ALIST_ENTRY_ATOM_IN_ALIST_WARN, 
//                        "The atom was detected in an import production " +
//                        "program action.\n");
//
//                if ( ! this.abort_parse )
//                {
//                    get_next_token();
//                }
//            }
//        } /* while */
//
//        /* if we have all the data, allocate the instance of Action && load 
//         * references to strings into it.  Otherwise, we discard the Strings 
//         * that we would otherwise load into the action.
//         */
//
//        if ( ! this.abort_parse )
//        {
//            if ( have_text_str )
//            {
//                action_ptr = allocAction();
//
//                if ( action_ptr == NULL )
//                {
//                    this.proceed = FALSE;
//                    error3("SYSTEM ERROR", "Action allocation failed.", fcnNamePtr);
//                }
//                else
//                {
//                    action_ptr->kind           = PROGRAM_ACTION;
//                    action_ptr->u.program.text = text_str_ptr;
//                }
//            }
//            else /* free the strings & issue warning */
//            {
//                if ( text_str_ptr != NULL ) freeCstr(text_str_ptr);
//
//                post_warning_message(MISSING_REQ_ATTR_IN_PGM_ACTION_WARN, 
//                                           NULL);
//            }
//        }
//
//        return(action_ptr);
//
//    } /* macshapa_odb_reader::parse_imp_prod_program_action_alist() */
//
//
//    /*************************************************************************
//     *
//     * parse_imp_prod_program_actions_attribute()
//     *
//     * This method parses an import production program actions attribute,
//     * and returns a reference to a linked list of instances of Action.  Import
//     * production program actions attributes may be generated by the following
//     * productions:
//     *
//     *     <imp_prod_program_actions_attribute> --> 
//     *             '(' 'PROGRAM-ACTIONS>' <imp_prod_program_actions_list> ')'
//     *
//     *     <imp_prod_program_actions_list> --> 
//     *             '(' (<imp_prod_program_action_alist>)* ')'
//     *
//     *     <imp_prod_program_action_alist> --> 
//     *             '(' <imp_prod_program_action_attributes> ')'
//     *
//     *     <imp_prod_program_action_attributes> --> 
//     *             ( <imp_prod_program_action_text_attribute> )
//     *
//     *     <imp_prod_program_action_text_attribute> -->
//     *             '(' 'TEXT>' <text_quote_string> ')' 
//     *
//     * The function returns a pointer to the first element in a
//     * linked list of instances of the Action structure, whose type
//     * definition may be found in import.h.  All instances of Action
//     * in this structure will be of PROGRAM_ACTION kind.  
//     *
//     * Note that null will be returned if the list is empty.
//     *
//     *                                              - 11/2/95
//     *
//     * Parameters:
//     *
//     *    - None.
//     *
//     * None.
//     *
//     * Returns: Reference to the first element in a linked list of 
//     *          instances of the Action structure, or NULL if the list
//     *          happens to be empty.
//     *
//     * Changes:
//     *
//     *    - None.
//     *
//     *************************************************************************/
//
//    private ActionPtr parse_imp_prod_program_actions_attribute()
//
//    {
//        final String mName = 
//                "macshapa_odb_reader::parse_imp_prod_program_actions_attribute()";
//        final String missing_program_actions_mssg = 
//                "An PROGRAM-ACTIONS> attribute appears not to contain a value.  " +
//                "The associated import production will be discarded.\n";
//        final String program_actions_type_mismatch_mssg = 
//                "The value of an PROGRAM-ACTIONS> attribute must be a list.  " +
//                "The associated import production will be discarded.\n";
//        ActionPtr action_list_ptr;
//
//        action_list_ptr = NULL;
//
//        if ( this.abort_parse )
//        {
//            throw new SystemErrorException(mName + 
//                    "this.abort_parse TRUE on entry");
//        }
//        
//        /* parse the attribute */
//        
//        /* first parse the leading left parenthesis */
//
//        if ( (this.l0_tok).code == L_PAREN_TOK )
//        {
//            get_next_token();
//        }
//        else /* we shouldn't have been called unless the next token is a '(' */
//        {
//            throw new SystemErrorException(mName + 
//                    "(this.l0_tok).code != L_PAREN_TOK.");
//        }
//
//        /* read the a-list entry name */
//        if ( ! this.abort_parse )
//        {
//            if ( ( (this.l0_tok).code == ALIST_LABEL_TOK ) && 
//                 ( (this.l0_tok).aux == PROGRAM_ACTIONS_LABEL ) )
//            {
//                get_next_token();
//            }
//            else /*we shouldn't have been called unless the next token is an a-list tag */
//            {
//                throw new SystemErrorException(mName + 
//                        "this.l0_tok != PROGRAM-ACTIONS>.");
//            }
//        }
//
//        /* read the value associated with the a-list entry & discard any excess values */
//        if ( ! this.abort_parse )
//        {
//            switch ( (this.l0_tok).code )
//            {
//                case R_PAREN_TOK:
//                    post_warning_message(EMPTY_ALIST_ENTRY_WARN, 
//                                         missing_program_actions_mssg); 
//                    break;
//
//                case L_PAREN_TOK:
//                    action_list_ptr = parse_imp_prod_program_actions_list();
//                    break;
//
//                case INT_TOK:
//                case ERROR_TOK:
//                case SYMBOL_TOK:
//                case FLOAT_TOK:
//                case STRING_TOK:
//                case BOOL_TOK:
//                case ALIST_LABEL_TOK:
//                case PRIVATE_VAL_TOK:
//                case SETF_TOK:
//                case DB_VAR_TOK:
//                case QUOTE_TOK:
//                    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
//                                         program_actions_type_mismatch_mssg); 
//
//                    if ( ! this.abort_parse )
//                    {
//                        get_next_token();
//                    }
//                    break;
//
//
//                case EOF_TOK:
//                    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                                       "EOF in a PROGRAM-ACTIONS> attribute.\n", 
//                                       true, true);
//                    break;
//
//                 default:
//                     throw new SystemErrorException(mName + 
//                             "Encountered unknown token type.");
//                     break;
//            }
//        }
//
//        /* check for EOF */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code == EOF_TOK )
//            {
//                post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                                   "EOF in a PROGRAM-ACTIONS> attribute.\n", 
//                                   true, true);
//            }
//        }
//
//        /* discard any excess values that may appear in the a-list entry */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code != R_PAREN_TOK )
//            {
//                discard_excess_alist_entry_values();
//
//                if ( ! this.abort_parse )
//                {
//                    post_warning_message(EXCESS_VALUES_IN_ALIST_ENTRY_WARN,
//                            "Excess values encountered in an " +
//                            "PROGRAM-ACTIONS> attribute.\n");
//                }
//            }
//        }
//
//        /* read the terminating right parenthesis */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code == R_PAREN_TOK )
//            {
//                get_next_token();
//            }
//            else
//            {
//                /* since we are cleaning up any excess values in the program 
//                 * actions attribute, this else clause is unreachable at present.
//                 */
//
//                post_warning_message(RIGHT_PAREN_EXPECTED_WARN, 
//                        "Closing parenthesis missing from an " +
//                        "PROGRAM-ACTIONS> attribute.\n");
//            }
//        }
//
//        return(action_list_ptr);
//
//    } /* macshapa_odb_reader::parse_imp_prod_program_actions_attribute() */
//
//
//    /*************************************************************************
//     *
//     * parse_imp_prod_program_actions_list()
//     *
//     * This method parses a list of import production program actions, and 
//     * returns a pointer to a linked list of instance of Action.  Lists of 
//     * import production program actions may be generated by the following 
//     * productions:
//     *
//     *     <imp_prod_program_actions_list> --> 
//     *             '(' (<imp_prod_program_action_alist>)* ')'
//     *
//     *     <imp_prod_program_action_alist> --> 
//     *             '(' <imp_prod_program_action_attributes> ')'
//     *
//     *     <imp_prod_program_action_attributes> --> 
//     *             ( <imp_prod_program_action_text_attribute> )
//     *
//     *     <imp_prod_program_action_text_attribute> -->
//     *             '(' 'TEXT>' <text_quote_string> ')' 
//     *
//     * The method returns a reference to the first element in a
//     * linked list of instances of the Action structure, whose type
//     * definition may be found in import.h.  All instances of Action
//     * in this structure will be of PROGRAM_ACTION kind.  
//     *
//     * Note that a null will be returned if the list is empty.
//     *
//     *                                              - 7/13/08
//     *
//     * Parameters:
//     *
//     *    - None.
//     *
//     * Returns: Reference to the first element in a linked list of 
//     *          instances of the Action structure, or null if the list
//     *          happens to be empty.
//     *
//     * Changes:
//     *
//     *    - None.
//     *
//     *************************************************************************/
//
//    private ActionPtr parse_imp_prod_program_actions_list()
//
//    {
//        final String mName = 
//                "macshapa_odb_reader::parse_imp_prod_program_actions_list()";
//        boolean done;
//        boolean excess_actions_warning_delivered;
//        ActionPtr action_ptr;
//        ActionPtr action_list_ptr;
//
//        action_ptr      = NULL; /* on general principles */
//        action_list_ptr = NULL; /* essential */
//
//        if ( this.abort_parse )
//        {
//            throw new SystemErrorException(mName + 
//                    "this.abort_parse TRUE on entry");
//        }
//        
//        /* parse the import production program actions list */
//        
//        /* first parse the leading left parenthesis */
//
//        if ( (this.l0_tok).code == L_PAREN_TOK )
//        {
//            get_next_token();
//        }
//        else /* we shouldn't have been called unless the next token == a '(' */
//        {
//            throw new SystemErrorException(mName + 
//                    "(this.l0_tok).code != L_PAREN_TOK.");
//        }
//
//        /* now read the import production program action a-lists */
//
//        done = false;
//        excess_actions_warning_delivered = false;
//
//        while ( ( ! this.abort_parse ) && ( ! done ) )
//        {
//            switch ( (this.l0_tok).code )
//            {
//                case R_PAREN_TOK:
//                     done = true;
//                     get_next_token();
//                     break;
//
//                case L_PAREN_TOK:
//                    if ( action_list_ptr == NULL )
//                    {
//                        action_ptr = parse_imp_prod_program_action_alist();
//
//                        if ( ! this.abort_parse )
//                        {
//                            if ( action_ptr != NULL )
//                            {
//                                action_list_ptr = 
//                                        AddActionListEnd(action_list_ptr, action_ptr);
//                            }
//                        }
//                    }
//                    else
//                    {
//                        if ( ! excess_actions_warning_delivered )
//                        { 
//                            excess_actions_warning_delivered = TRUE;
//
//                            post_warning_message(EXCESS_IMP_PROD_PGM_ACTIONS_WARN, 
//                                                 null); 
//                        }
//
//                        if ( ! this.abort_parse )
//                        {
//                            parse_arbitrary_list();
//                        }
//                    } 
//                    break;
//
//                case BOOL_TOK:
//                case ERROR_TOK:
//                case SYMBOL_TOK:
//                case FLOAT_TOK:
//                case STRING_TOK:
//                case INT_TOK:
//                case ALIST_LABEL_TOK:
//                case PRIVATE_VAL_TOK:
//                case SETF_TOK:
//                case DB_VAR_TOK:
//                case QUOTE_TOK:
//                    post_warning_message(ATOM_IN_IMP_PROD_PMG_ACTIONS_LIST_WARN, 
//                                         null); 
//
//                    if ( ! this.abort_parse )
//                    {
//                        get_next_token();
//                    }
//                    break;
//
//                case EOF_TOK:
//                    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                            "EOF in an import production program actions list.\n", 
//                            true, true);
//                    break;
//
//                 default:
//                     throw new SystemErrorException(mName + 
//                             "Encountered unknown token type.");
//                     break;
//             }
//        }
//
//        if ( ( ! this.abort_parse ) && ( action_list_ptr == NULL ) )
//        {
//            post_warning_message(EMPTY_IMPORT_PROD_PGM_ACTION_LIST_WARN, null); 
//        }
//
//        return(action_list_ptr);
//
//    } /* macshapa_odb_reader::parse_imp_prod_program_actions_list() */
//
//
//    /*************************************************************************
//     *
//     * parse_imp_prod_program_action_text_attribute()
//     *
//     * This method parses an import production program action text attribute, 
//     * and returns a pointer to a CString containing the contents of the 
//     * <text_quote_string>.  This attribute is generated by the following 
//     * production:
//     *
//     * <imp_prod_program_action_text_attribute> --> 
//     *                                  '(' 'TEXT>' <text_quote_string> ')' 
//     *
//     *                                              - 7/13/08
//     *
//     * Parameters:
//     *
//     *    - None.
//     *
//     * Returns: Reference to a String containing the string associated
//     *          with the ONSET> attribute, or NULL if no string is found.
//     *
//     * Changes:
//     *
//     *    - None.
//     *
//     *************************************************************************/
//
//    private String parse_imp_prod_program_action_text_attribute()
//        throws SystemErrorException
//    {
//        final String mName = 
//            "macshapa_odb_reader::parse_imp_prod_program_action_text_attribute()";
//        final String missing_text_mssg = 
//                "An TEXT> attribute in an import production program action " +
//                "appears not to contain a value.  Value forced to the empty " +
//                "string.\n";
//        final String text_type_mismatch_mssg = 
//                "The value associated with a TEXT> attribute in an import " +
//                "production program action must be a quoted string.  " +
//                "Value forced to the empty string.\n";
//        String str = null;
//
//        if ( this.abort_parse )
//        {
//            throw new SystemErrorException(mName + 
//                    "this.abort_parse TRUE on entry");
//        }
//        
//        /* parse the text attribute */
//    
//        /* first parse the leading left parenthesis */
//
//        if ( (this.l0_tok).code == L_PAREN_TOK )
//        {
//            get_next_token();
//        }
//        else /* we shouldn't have been called unless the next token is a '(' */
//        {
//            throw new SystemErrorException(mName + 
//                    "(this.l0_tok).code != L_PAREN_TOK.");
//        }
//
//        /* read the a-list entry name */
//        if ( ! this.abort_parse )
//        {
//            if ( ( (this.l0_tok).code == ALIST_LABEL_TOK ) && 
//                 ( (this.l0_tok).aux == TEXT_LABEL ) )
//            {
//                get_next_token();
//            }
//            else /* we shouldn't have been called unless the next token is an a-list tag */
//            {
//                throw new SystemErrorException(mName + 
//                        "this.l0_tok != TEXT>.");
//            }
//        }
//
//        /* read the value associated with the a-list entry & discard any excess values */
//        if ( ! this.abort_parse )
//        {
//            switch ( (this.l0_tok).code )
//            {
//                case STRING_TOK:
//                    str = new String((this.l0_tok).str.toString());
//
//                    if ( ! this.abort_parse )
//                    {
//                        get_next_token();
//                    }
//                    break;
//
//                case R_PAREN_TOK:
//                    post_warning_message(EMPTY_ALIST_ENTRY_WARN, 
//                                         missing_text_mssg); 
//                    break;
//
//                case L_PAREN_TOK:
//                    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
//                                         text_type_mismatch_mssg); 
//
//                    if ( ! this.abort_parse )
//                    {
//                        parse_arbitrary_list();
//                    }
//                    break;
//
//                case ERROR_TOK:
//                case SYMBOL_TOK:
//                case FLOAT_TOK:
//                case INT_TOK:
//                case BOOL_TOK:
//                case ALIST_LABEL_TOK:
//                case PRIVATE_VAL_TOK:
//                case SETF_TOK:
//                case DB_VAR_TOK:
//                case QUOTE_TOK:
//                    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
//                                         text_type_mismatch_mssg); 
//
//                    if ( ! this.abort_parse )
//                    {
//                        get_next_token();
//                    }
//                    break;
//
//                case EOF_TOK:
//                    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                                       "EOF in a TEXT> attribute.\n", 
//                                       true, true);
//                    break;
//
//                 default:
//                     throw new SystemErrorException(mName + 
//                             "Encountered unknown token type.");
//                     break;
//             }
//        }
//
//        /* Check for EOF */
//        if ( ! this.abort_parse )
//        {
//             if ( (this.l0_tok).code == EOF_TOK )
//             {
//                post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                                   "EOF in a TEXT> attribute.\n",
//                                   true, true);
//             }
//        }
//
//        /* discard any excess values that may appear in the TEXT> a-list entry */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code != R_PAREN_TOK )
//            {
//                discard_excess_alist_entry_values();
//
//                if ( ! this.abort_parse )
//                {
//                    post_warning_message(EXCESS_VALUES_IN_ALIST_ENTRY_WARN,
//                        "Excess values encountered in a TEXT> attribute.\n");
//                }
//            }
//        }
//
//        /* read the terminating right parenthesis */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code == R_PAREN_TOK )
//            {
//                get_next_token();
//            }
//            else
//            {
//                /* since we are cleaning up any excess values in the text */
//                /* attribute, this else clause should be unreachable.     */
//
//                post_warning_message(RIGHT_PAREN_EXPECTED_WARN, 
//                    "Closing parenthesis missing from a TEXT> attribute.\n");
//            }
//        }
//
//        if ( ( ! this.abort_parse ) && ( str_ptr == NULL ) )
//        {
//            /* never return NULL under normal circumstances -- return */
//            /* a pointer to an empty string instead                   */
//
//            str = new String("");
//
//        }
//
//        return(str);
//
//    } /* macshapa_odb_reader::parse_imp_prod_program_action_text_attribute() */
//
//
//    /*************************************************************************
//     *
//     * parse_imp_prods_attribute()
//     *
//     * This method parses an import productions attribute list.  This list is 
//     * generated by the following productions:
//     *
//     *     <imp_prods_attribute> --> 
//     *             '(' 'IMPORT-PRODUCTION-LIST>' <imp_prod_list> ')'
//     *
//     *     <imp_prod_list> --> '(' (<imp_prod>)* ')'
//     *
//     *     <imp_prod> --> '(' <imp_prod_alist> ')'
//     *
//     *     <imp_prod_alist> --> { <imp_prod_name_attribute>
//     *                            <imp_prod_pattern_attribute>
//     *                            <imp_prod_hard_prec_attribute>
//     *                            <imp_prod_is_shown_attribute>
//     *                            <imp_prod_is_traced_attribute>
//     *                            ( <imp_prod_insert_actions_attribute> |
//     *                              <imp_prod_program_actions_attribute> ) }
//     *
//     *     <imp_prod_name_attribute> --> '(' 'NAME>' <text_quote_string> ')'
//     *
//     *     <imp_prod_pattern_attribute> --> '(' 'PATTERN>' <text_quote_string> ')'
//     *
//     *     <imp_prod_hard_prec_attribute> --> '(' 'HARD-PREC>' <integer> ')'
//     *
//     *     <imp_prod_is_shown_attribute> --> '(' 'IS-SHOWN>' <boolean> ')'
//     *
//     *     <imp_prod_is_traced_attribute> --> '(' 'IS-TRACED>' <boolean> ')'
//     *
//     *     <imp_prod_insert_actions_attribute> --> 
//     *             '(' 'INSERT-ACTIONS>' <imp_prod_insert_actions_list> ')'
//     *
//     *     <imp_prod_insert_actions_list> --> 
//     *             '(' (<imp_prod_insert_action_alist>)* ')'
//     *
//     *     <imp_prod_insert_action_alist> --> 
//     *             '(' <imp_prod_insert_action_attributes> ')'
//     *
//     *     <imp_prod_insert_action_attributes> --> 
//     *             ( <imp_prod_insert_action_var_attribute> 
//     *               <imp_prod_insert_action_onset_attribute> 
//     *               <imp_prod_insert_action_offset_attribute> 
//     *               <imp_prod_insert_action_val_attribute> )
//     *
//     *     <imp_prod_insert_action_var_attribute> --> 
//     *             '(' 'VAR>' <text_quote_string> ')'
//     *
//     *     <imp_prod_insert_action_onset_attribute> --> 
//     *             '(' 'ONSET>' <text_quote_string> ')'
//     *
//     *     <imp_prod_insert_action_offset_attribute> --> 
//     *             '(' 'OFFSET>' <text_quote_string> ')'
//     *
//     *     <imp_prod_insert_action_val_attribute> --> 
//     *             '(' 'VAL>' <text_quote_string> ')'
//     *
//     *     <imp_prod_program_actions_attribute> --> 
//     *             '(' 'PROGRAM-ACTIONS>' <imp_prod_program_actions_list> ')'
//     *
//     *     <imp_prod_program_actions_list> --> 
//     *             '(' (<imp_prod_program_action_alist>)* ')'
//     *
//     *     <imp_prod_program_action_alist> --> 
//     *             '(' <imp_prod_program_action_attributes> ')'
//     *
//     *     <imp_prod_program_action_attributes> --> 
//     *             ( <imp_prod_program_action_text_attribute> )
//     *
//     *     <imp_prod_program_action_text_attribute> -->
//     *             '(' 'TEXT>' <text_quote_string> ')' 
//     *
//     *                                              - 7/13/08
//     *
//     * Parameters:
//     *
//     *    - format_ptr:  Pointer to an instance of the Format structure, whose
//     *          type definition may be found in import.h.  format_ptr points
//     *          to the instance of format into which we will load the import 
//     *          productions as we read them. 
//     *
//     * Returns:  Void.
//     *
//     * Changes:
//     *
//     *    - None.
//     *
//     *******************************************************************************/
//
//    private void parse_imp_prods_attribute(FormatPtr format_ptr)
//        throws SystemErrorException
//    {
//        final String mName = "macshapa_odb_reader::parse_imp_prods_attribute()";
//        final String missing_import_prods_list_mssg = 
//                "An IMPORT-PRODUCTION-LIST> attribute appears not to contain " +
//                "a value.  The attribute will be ignored.\n";
//        final String import_prods_list_type_mismatch_mssg = 
//                "The value of a IMPORT-PRODUCTION-LIST> attribute must be a " +
//                "list.  The attribute will be ignored.\n";
//
//        if ( this.abort_parse )
//        {
//            throw new SystemErrorException(mName + 
//                    "this.abort_parse TRUE on entry");
//        }
//        
//        if ( ( format_ptr == NULL ) || ( format_ptr->name == NULL ) || 
//             ( format_ptr->prods != NULL ) || ( format_ptr->next != NULL ) )
//        {
//            throw new SystemErrorException(mName + 
//                    "format_ptr seems to be corrupt on entry");
//        }
//        
//        /* parse the import productions attribute */
//        
//        /* first parse the leading left parenthesis */
//
//        if ( (this.l0_tok).code == L_PAREN_TOK )
//        {
//            get_next_token();
//        }
//        else /* we shouldn't have been called unless the next token is a '(' */
//        {
//            throw new SystemErrorException(mName + 
//                    "(this.l0_tok).code != L_PAREN_TOK.");
//        }
//
//        /* read the a-list entry name */
//
//        if ( ! this.abort_parse )
//        {
//            if ( ( (this.l0_tok).code == ALIST_LABEL_TOK ) && 
//                 ( (this.l0_tok).aux == IMPORT_PRODUCTION_LIST_LABEL ) )
//            {
//                get_next_token();
//            }
//            else /* we shouldn't have been called unless the next token 
//                  * is IMPORT-FORMAT-LISTS> 
//                  */
//            {
//                throw new SystemErrorException(mName + 
//                        "this.l0_tok != IMPORT-PRODUCTION-LIST>.");
//            }
//        }
//
//
//        /* read the value associated with the attribute */
//
//        if ( ! this.abort_parse )
//        {
//            switch ( (this.l0_tok).code )
//            {
//                 case R_PAREN_TOK:
//                    post_warning_message(EMPTY_ALIST_ENTRY_WARN, 
//                                         missing_import_prods_list_mssg); 
//                    break;
//
//                case L_PAREN_TOK:
//                    parse_imp_prod_list(format_ptr);
//                    break;
//
//                case BOOL_TOK:
//                case ERROR_TOK:
//                case SYMBOL_TOK:
//                case FLOAT_TOK:
//                case STRING_TOK:
//                case INT_TOK:
//                case ALIST_LABEL_TOK:
//                case PRIVATE_VAL_TOK:
//                case SETF_TOK:
//                case DB_VAR_TOK:
//                case QUOTE_TOK:
//                    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
//                                         import_prods_list_type_mismatch_mssg); 
//
//                    if ( ! this.abort_parse )
//                    {
//                        get_next_token();
//                    }
//                    break;
//
//                case EOF_TOK:
//                    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                            "EOF in an IMPORT-PRODUCTION-LIST> attribute.\n", 
//                            true, true);
//                    break;
//
//                default:
//                    throw new SystemErrorException(mName + 
//                            "Encountered unknown token type.");
//                    break;
//            }
//        }
//
//        /* check for EOF */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code == EOF_TOK )
//            {
//                post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                        "EOF in an IMPORT-PRODUCTION-LIST> attribute.\n",  
//                        true, true);
//            }
//        }
//
//        /* discard any excess values that may appear in the IMPORT-FORMAT-LISTS> a-list entry */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code != R_PAREN_TOK )
//            {
//                discard_excess_alist_entry_values();
//
//                if ( ! this.abort_parse )
//                {
//                    post_warning_message(EXCESS_VALUES_IN_ALIST_ENTRY_WARN,
//                            "Excess values encountered in an " +
//                            "IMPORT-PRODUCTION-LIST> attribute.\n");
//                }
//            }
//        }
//
//        /* read the terminating right parenthesis */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code == R_PAREN_TOK )
//            {
//                get_next_token();
//            }
//            else
//            {
//                /* since we are cleaning up any excess values in the import 
//                 * productions list attribute, this else clause is unreachable 
//                 * at present.  Should we choose to drop the above attempt at 
//                 * error recovery, this clause will again become reachable.
//                 */
//
//                post_warning_message(RIGHT_PAREN_EXPECTED_WARN, 
//                        "Closing parenthesis missing from an " +
//                        "IMPORT-PRODUCTION-LIST> attribute.\n");
//            }
//        }
//
//        return;
//
//    } /* macshapa_odb_reader::parse_imp_prods_attribute() */
//
//
//    /*************************************************************************
//     *
//     * parse_shapa_pane_var_list()
//     *
//     * This method parses a list of spreadsheet variable names that are to
//     * be added to the shapaPane of the current document, thereby making them
//     * visible.  This list is generated by the following productions:
//     * 
//     *     <shapa_pane_var_list> --> '(' (<shapa_pane_var_name>)* ')'
//     * 
//     *     <shapa_pane_var_name> --> <s_var_name>
//     *
//     *                                              - 7/13/08
//     *
//     * Parameters:
//     *
//     *    - None.
//     *
//     * Returns:  Void.
//     *
//     * Changes:
//     *
//     *    - None.
//     *
//     *************************************************************************/
//
//    private void parse_shapa_pane_var_list()
//        throws SystemErrorException
//    {
//        final String mName = "macshapa_odb_reader::parse_shapa_pane_var_list()";
//        boolean done;
//        ColumnIndex index;
//        Variable s_var_ptr;
//        CShapaPane *shapa_pane_ptr;
//
//        if ( this.abort_parse )
//        {
//            throw new SystemErrorException(mName + 
//                    "this.abort_parse TRUE on entry");
//        }
//        
//        /* parse the shapa pane vars list */
//        
//        /* first parse the leading left parenthesis */
//
//        if ( (this.l0_tok).code == L_PAREN_TOK )
//        {
//            get_next_token();
//        }
//        else /* we shouldn't have been called unless the next token is a '(' */
//        {
//            throw3 new SystemErrorException(mName + 
//                    "(this.l0_tok).code != L_PAREN_TOK.");
//        }
//
//        /* now read the shapa pane variables list */
//        if ( ! this.abort_parse )
//        {
//            done = false;
//            index = 0;
//
//            shapa_pane_ptr = (CShapaPane *)(this.spread_doc_ptr->itsMainPane);
//
//            while ( ( ! this.abort_parse ) && ( ! done ) )
//            {
//                switch ( (this.l0_tok).code )
//                {
//                    case SYMBOL_TOK:
//                        if ( ((this.l0_tok).aux & COLUMN_FLAG ) == 0 )
//                        {
//                            post_warning_message(INVALID_S_VAR_NAME_IN_SP_VAR_LIST_WARN, 
//                                "Will coerce the name to a valid spreadsheet variable name.\n", 
//                                read_vars);
//
//                            if ( ! this.abort_parse )
//                            {
//                                coerce_symbol_token_to_spreadsheet_variable_name
//                                    (&(this.l0_tok));
//                            }
//                        }
//
//                        /* get pointer to the spreadsheet variable.  The 
//                         * spreadsheet variable should already have been 
//                         * defined.  If not, lookupVar() will return NULL.
//                         */
//
//                        s_var_ptr = this.spread_doc_ptr->SymTable->
//                            lookupVar((this.l0_tok).str_ptr);
//
//                        if ( s_var_ptr == NULL )
//                        {
//                            post_warning_message(
//                                    REF_TO_UNDEF_S_VAR_IN_SP_VAR_LIST_WARN, 
//                                    "The entry in the shapa pane variables " +
//                                    "list will be ignored.\n");
//                        }
//                        else if ( s_var_ptr->system )
//                        {
//                            post_warning_message(
//                                    REF_TO_SYSTEM_S_VAR_IN_SP_VAR_LIST_WARN, 
//                                    "Since system spreadsheet variables never " +
//                                    "appear on the spreadsheet, the entry " +
//                                    "will be ignored.\n");
//                        }
//                        else if ( shapa_pane_ptr->FindColumnIndexOfVar(s_var_ptr) != FAIL )
//                        {
//                            post_warning_message(
//                                    DUP_REF_TO_S_VAR_IN_SP_VAR_LIST_WARN, 
//                                    "The duplicate reference will be ignored.\n");
//                        }
//                        else /* insert the spreadsheet variable into the shapaPane */
//                        {
//                            shapa_pane_ptr->InsertVarIntoColumn(/* var            */ s_var_ptr,
//                                                                /* colIndex       */ index,
//                                                                /* updateTemporal */ FALSE,
//                                                                /* updateVExtents */ TRUE,
//                                                                /* redraw         */ FALSE);
//                            index++;
//                        }
//
//                        if ( ! this.abort_parse )
//                        {
//                            get_next_token();
//                        }
//                        break;
//
//                     case R_PAREN_TOK:
//                         done = TRUE;
//                         get_next_token();
//                         break;
//
//                    case L_PAREN_TOK:
//                        post_warning_message(NON_S_VAR_IN_SHAPA_VARS_LIST_WARN, 
//                                             "The item was a list.\n"); 
//
//                        if ( ! this.abort_parse )
//                        {
//                            parse_arbitrary_list();
//                        }
//                        break;
//
//                    case BOOL_TOK:
//                    case ERROR_TOK:
//                    case FLOAT_TOK:
//                    case STRING_TOK:
//                    case INT_TOK:
//                    case ALIST_LABEL_TOK:
//                    case PRIVATE_VAL_TOK:
//                    case SETF_TOK:
//                    case DB_VAR_TOK:
//                    case QUOTE_TOK:
//                        post_warning_message(NON_S_VAR_IN_SHAPA_VARS_LIST_WARN, 
//                                             "The item was an atom.\n"); 
//
//                        if ( ! this.abort_parse )
//                        {
//                            get_next_token();
//                        }
//                        break;
//                         
//                    case EOF_TOK:
//                        post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                                "EOF in the list associated with the SYSTEM> " +
//                                "SHAPA-PANE-VARS> attribute.\n", 
//                                true, true);
//                        break;
//
//                     default:
//                         throw new SystemErrorException(mName + 
//                                 "Encountered unknown token type.");
//                         break;
//                }
//
//            } /* end while */
//
//            shapa_pane_ptr->UpdateShapaPaneTemporal(TRUE, FALSE);
//        }
//
//        return;
//
//    } /* macshapa_odb_reader::parse_shapa_pane_var_list() */
//
//
//    /*************************************************************************
//     *
//     * parse_shapa_pane_vars_attribute()
//     *
//     * This method parses a SHAPA-PANE-VARS> attribute, which is generated by 
//     * the following productions:
//     * 
//     *     <shapa_pane_vars_attribute> --> 
//     *             '(' 'SHAPA-PANE-VARS>' <shapa_pane_var_list> ')'
//     * 
//     *     <shapa_pane_var_list> --> '(' (<shapa_pane_var_name>)* ')'
//     * 
//     *     <shapa_pane_var_name> --> <s_var_name>
//     *
//     * The purpose of this attribute is to support the insertion of user
//     * spreadsheet variables into the shapaPane associated with the current
//     * document, thereby making them visible on the spreadsheet.  However,
//     * the actual insertions are made in shapa_pane_var_list().
//     *
//     *                                              - 10/28/95
//     *
//     * Parameters:
//     *
//     *    - None.
//     *
//     * Returns:  Void.
//     *
//     * Changes:
//     *
//     *    - None.
//     *
//     *************************************************************************/
//
//    private void parse_shapa_pane_vars_attribute()
//        throws SystemErrorException
//    {
//        final String mName = 
//                "macshapa_odb_reader::parse_shapa_pane_vars_attribute()";
//        final String missing_shapa_pane_vars_list_mssg = 
//                "The SHAPA-PANE-VARS> attribute appears not to contain a " +
//                "value.  All spreadsheet variables will be made visible.\n";
//        final String shapa_pane_vars_list_type_mismatch_mssg = 
//                "The value of a SHAPA-PANE-VARS> attribute must be a list " +
//                "of spreadsheet names.  " +
//                "All spreadsheet variables will be made visible.\n";
//
//        if ( this.abort_parse )
//        {
//            throw new SystemErrorException(mName + 
//                    "this.abort_parse TRUE on entry");
//        }
//        
//        /* parse the shapa pane vars attribute */
//        
//        /* first parse the leading left parenthesis */
//
//        if ( (this.l0_tok).code == L_PAREN_TOK )
//        {
//            get_next_token();
//        }
//        else /* we shouldn't have been called unless the next token is a '(' */
//        {
//            throw new SystemErrorException(mName + 
//                    "(this.l0_tok).code != L_PAREN_TOK.");
//        }
//
//        /* read the a-list entry name */
//        if ( ! this.abort_parse )
//        {
//            if ( ( (this.l0_tok).code == ALIST_LABEL_TOK ) && 
//                 ( (this.l0_tok).aux == SHAPA_PANE_VARS_LABEL ) )
//            {
//                get_next_token();
//            }
//            else /* we shouldn't have been called unless the next token 
//                  * is SHAPA-PANE-VARS> 
//                  */
//            {
//                throw new SystemErrorException(mName + 
//                        "this.l0_tok != SHAPA-PANE-VARS>.");
//            }
//        }
//
//        /* read the value associated with the a-list entry & discard 
//         * any excess values 
//         */
//        if ( ! this.abort_parse )
//        {
//             switch ( (this.l0_tok).code )
//             {
//                 case R_PAREN_TOK:
//                    post_warning_message(EMPTY_ALIST_ENTRY_WARN, 
//                                         missing_shapa_pane_vars_list_mssg); 
//
//                    if ( ! this.abort_parse )
//                    {
//                        add_all_user_svars_to_shapa_pane();
//                    }
//                    break;
//
//                case L_PAREN_TOK:
//                    parse_shapa_pane_var_list();
//                    break;
//
//                case BOOL_TOK:
//                case ERROR_TOK:
//                case SYMBOL_TOK:
//                case FLOAT_TOK:
//                case STRING_TOK:
//                case INT_TOK:
//                case ALIST_LABEL_TOK:
//                case PRIVATE_VAL_TOK:
//                case SETF_TOK:
//                case DB_VAR_TOK:
//                case QUOTE_TOK:
//                    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
//                            shapa_pane_vars_list_type_mismatch_mssg); 
//
//                    if ( ! this.abort_parse )
//                    {
//                        add_all_user_svars_to_shapa_pane();
//                    }
//
//                    if ( ! this.abort_parse )
//                    {
//                        get_next_token();
//                    }
//                    break;
//
//                case EOF_TOK:
//                    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                            "EOF in the SYSTEM> SHAPA-PANE-VARS> attribute.\n", 
//                            true, true);
//                    break;
//
//                default:
//                    throw new SystemErrorException(mName + 
//                            "Encountered unknown token type.");
//                    break;
//            }
//        }
//
//        /* check for EOF */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code == EOF_TOK )
//            {
//                post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                                   "EOF in a SHAPA-PANE-VARS> attribute.\n", 
//                                   true, true);
//            }
//        }
//
//        /* discard any excess values that may appear in the 
//         * SHAPA-PANE-VARS> a-list entry 
//         */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code != R_PAREN_TOK )
//            {
//                discard_excess_alist_entry_values();
//
//                if ( ! this.abort_parse )
//                {
//                    post_warning_message(EXCESS_VALUES_IN_ALIST_ENTRY_WARN,
//                            "Excess values encountered the SYSTEM> " +
//                            "SHAPA-PANE-VARS> attribute.\n");
//                }
//            }
//        }
//
//        /* read the terminating right parenthesis */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code == R_PAREN_TOK )
//            {
//                get_next_token();
//            }
//            else
//            {
//                /* since we are cleaning up any excess values in the shapa 
//                 * pane vars list attribute, this else clause is unreachable 
//                 * at present. Should we choose to drop the above attempt 
//                 * at error recovery, this clause will again become reachable.
//                 */
//
//                post_warning_message(RIGHT_PAREN_EXPECTED_WARN, 
//                        "Closing parenthesis missing from the SYSTEM> " +
//                        "SHAPA-PANE-VARS> attribute.\n");
//            }
//        }
//
//        return;
//
//    } /* macshapa_odb_reader::parse_shapa_pane_vars_attribute() */
//
//
//    /*************************************************************************
//     *
//     * parse_system_alist()
//     *
//     * This method parses the a-list associated with the system section of 
//     * the open database body.  Structurally, this list is simply a list of
//     * a-list entries, each of which is a two element list consisting a an
//     * a-list entry name and its assocated value.  The productions generating
//     * the user section a-list are given below:
//     *
//     *     <system_alist> --> '(' <system_attributes> ')'
//     *
//     *     <system_attributes> --> { [<shapa_pane_vars_attribute>]
//     *                               [<groups_attribute>]
//     *                               [<alignments-attribute>]
//     *                               [<import_formats_attribute>] }
//     *
//     *     <shapa_pane_vars_attribute> --> 
//     *             '(' 'SHAPA-PANE-VARS>' <shapa_pane_var_list> ')'
//     *
//     *     <groups_attribute> --> '(' 'GROUPS>' <groups_list> ')'
//     *
//     *     <alignments_attribute> --> '(' 'ALIGNMENTS>' <alignments_list> ')'
//     *
//     *     <import_formats_attribute> --> 
//     *             '(' 'IMPORT-FORMAT-LISTS>' <imp_format_lists> ')'
//     *
//     * Note that the attributes in the system alist may appear in any order,
//     * or they may be ommited entirely.
//     *
//     *                                              - 7/13/08
//     *
//     * Parameters:
//     *
//     *    - None.
//     *
//     * Returns:  Void.
//     *
//     * Changes:
//     *
//     *    - None.
//     *
//     *************************************************************************/
//
//    private void parse_system_alist()
//        throws SystemErrorException
//    {
//        final String mName = "macshapa_odb_reader::parse_system_alist()";
//        boolean done;
//        boolean have_shapa_pane_vars;
//        boolean have_groups;
//        boolean have_alignments;
//        boolean have_import_formats;
//
//        if ( this.abort_parse )
//        {
//            throw new SystemErrorException(mName + 
//                    "this.abort_parse TRUE on entry");
//        }
//        
//        /* parse the user alist */
//        
//        /* first parse the leading left parenthesis */
//
//        if ( (this.l0_tok).code == L_PAREN_TOK )
//        {
//            if ( ( (this.l1_tok).code == L_PAREN_TOK ) || 
//                 ( (this.l1_tok).code == R_PAREN_TOK ) )
//            {
//                get_next_token();
//            }
//            else if ( (this.l1_tok).code == ALIST_LABEL_TOK )
//            {
//                post_warning_message(LEFT_PAREN_EXPECTED_WARN, 
//                        "The opening left parenthesis of the SYSTEM> " +
//                        "a-list appears to be missing.\n");
//            }
//            else 
//            {
//                /* if a left paren is missing, the first item in the a-list is 
//                 * not an a-list entry.  If we try to recover from this error 
//                 * here, we will only confuse things further.  Thus we eat the 
//                 * left parenthesis & let the cards fall where they may.
//                 */
//
//                get_next_token();
//            }
//        }
//        else /* we shouldn't have been called unless the next token is a '(' */
//        {
//            throw new SystemErrorException(mName + 
//                    "(this.l0_tok).code != L_PAREN_TOK.");
//        }
//
//        done                 = false;
//        have_shapa_pane_vars = false;
//        have_groups          = false;
//        have_alignments      = false;
//        have_import_formats  = false;
//
//        /* now parse the a-list assocated with the predicate declaration */
//        while ( ( ! this.abort_parse ) && 
//                ( ! done ) )
//        {
//             if ( (this.l0_tok).code == L_PAREN_TOK )
//             {
//                 if ( (this.l1_tok).code == ALIST_LABEL_TOK )
//                 {
//                     switch ( (this.l1_tok).aux )
//                     {
//                         case SHAPA_PANE_VARS_LABEL:
//                             if ( ! have_shapa_pane_vars )
//                             {
//                                 have_shapa_pane_vars = true;
//                                 parse_shapa_pane_vars_attribute();
//                             }
//                             else
//                             {
//                                post_warning_message(DUPLICATE_ALIST_ENTRY_WARN, 
//                                        "Duplicate SHAPA-PANE-VARS> entry in " +
//                                        "the SYSTEM> alist.\n");
//
//                                if ( ! this.abort_parse )
//                                {
//                                     parse_unknown_alist_entry();
//                                }
//                            }
//                            break;
//
//                        case GROUPS_LABEL:
//                            if ( ! have_groups )
//                            {
//                                have_groups = true;
//                                parse_groups_attribute();
//                            }
//                            else
//                            {
//                                post_warning_message(DUPLICATE_ALIST_ENTRY_WARN, 
//                                        "Duplicate GROUPS> entry in the " +
//                                        "SYSTEM> alist.\n");
//
//                                if ( ! this.abort_parse )
//                                {
//                                     parse_unknown_alist_entry();
//                                }
//                            }
//                            break;
//
//                        case ALIGNMENTS_LABEL:
//                            if ( ! have_alignments )
//                            {
//                                have_alignments = true;
//
//                                if ( ! have_shapa_pane_vars )
//                                {
//                                    parse_alignments_attribute();
//                                }
//                                else /* can't read alignments after we have 
//                                      * read the shapa pane variables 
//                                      */
//                                {
//                                    post_warning_message(
//                                            ALIGNMENTS_AFTER_SHAPA_PANE_VARS_WARN, 
//                                            "The ALIGNMENTS> attribute will " +
//                                            "be discarded.\n");
//
//                                    parse_unknown_alist_entry();
//                                }
//                            }
//                            else
//                            {
//                                post_warning_message(DUPLICATE_ALIST_ENTRY_WARN, 
//                                        "Duplicate ALIGNMENTS> entry in the " +
//                                        "SYSTEM> alist.\n");
//
//                                if ( ! this.abort_parse )
//                                {
//                                     parse_unknown_alist_entry();
//                                }
//                            }
//                            break;
//
//                        case IMPORT_FORMATS_LIST_LABEL:
//                            if ( ! have_import_formats )
//                            {
//                                have_import_formats = true;
//                                parse_import_formats_attribute();
//
//                                if ( this.proceed ) /* save the format list */
//                                {
//                                    this.spread_doc_ptr->itsFormatList = 
//                                         this.import_format_list_root;
//                                }
//                            }
//                            else
//                            {
//                                post_warning_message(DUPLICATE_ALIST_ENTRY_WARN, 
//                                        "Duplicate IMPORT-FORMAT-LISTS> entry " +
//                                        "in the SYSTEM> alist.\n");
//
//                                if ( ! this.abort_parse )
//                                {
//                                     parse_unknown_alist_entry();
//                                }
//                            }
//                            break;
//
//                        default:
//                            post_warning_message(
//                                UNKNOWN_OR_UNEXPECTED_ALIST_ENTRY_WARN, 
//                                "The entry is located in the SYSTEM> a-list.\n");
//
//                            if ( ! this.abort_parse )
//                            {
//                                 parse_unknown_alist_entry();
//                            }
//                            break;
//                    }
//                }
//                else /* a-list contains a list that is not an a-list entry. */
//                     /* read it & discard it.                               */
//                {
//                    post_warning_message(NON_ALIST_ENTRY_LIST_IN_ALIST_WARN, 
//                            "The list is located in the SYSTEM> a-list.\n");
//
//                    if ( ! this.abort_parse )
//                    {
//                         parse_arbitrary_list();
//                    }
//                }
//            }
//            else if ( (this.l0_tok).code == R_PAREN_TOK )
//            {
//                done = true;
//                get_next_token();
//            }
//            else if ( (this.l0_tok).code == EOF_TOK )
//            {
//                done = true;
//                post_error_message(UNEXPECTED_END_OF_FILE_ERR,
//                                   "EOF occurred in the SYSTEM> a-list.\n",
//                                   true, true);
//            }
//            else /* (this.l0_tok).code isnt '(', ')', or EOF */
//            {
//                post_warning_message(NON_ALIST_ENTRY_ATOM_IN_ALIST_WARN, 
//                        "The atom was detected in the SYSTEM> a-list.\n");
//
//                if ( ! this.abort_parse )
//                {
//                    get_next_token();
//                }
//            }
//        }
//
//        /* check for missing <shapa_pane_vars_attribute>. */
//
//        if ( ( ! this.abort_parse ) && 
//             ( ! have_shapa_pane_vars ) )
//        {
//            add_all_user_svars_to_shapa_pane();
//        }
//
//        return;
//
//    } /* macshapa_odb_reader::parse_system_alist() */
//
//
//    /*************************************************************************
//     *
//     * parse_system_section()
//     *
//     * This method parses the system section of the open database body.
//     * Structurally, the system section is an a-list entry with the label
//     * "SYSTEM>" and a list as its value.  The production generating the 
//     * user section is shown below.
//     *
//     *     <system_section> --> '(' 'SYSTEM>' <system_alist> ')'
//     * 
//     *
//     *                                              - 7/13/08
//     *
//     * Parameters:
//     *
//     *    - None.
//     *
//     * Returns:  Void.
//     *
//     * Changes:
//     *
//     *    - None.
//     *
//     *************************************************************************/

    // stub -- discard eventually
    private void parse_system_section()
        throws SystemErrorException,
               java.io.IOException
    {
        parse_arbitrary_list();
    }
    
//    private void parse_system_section()
//        throws SystemErrorException
//    {
//        final String mName = "macshapa_odb_reader::parse_system_section()";
//
//        if ( this.abort_parse )
//        {
//            throw new SystemErrorException(mName + 
//                    "this.abort_parse TRUE on entry");
//        }
//        
//        /* parse the system section */
//        
//        /* first parse the leading left parenthesis */
//
//        if ( (this.l0_tok).code == L_PAREN_TOK )
//        {
//            get_next_token();
//        }
//        else /* we shouldn't have been called unless the next token is a '(' */
//        {
//            throw new SystemErrorException(mName + 
//                    "(this.l0_tok).code != L_PAREN_TOK.");
//        }
//
//        /* read the a-list entry name */
//        if ( ! this.abort_parse )
//        {
//            if ( ( (this.l0_tok).code == ALIST_LABEL_TOK ) &&
//                 ( (this.l0_tok).aux == SYSTEM_LABEL ) )
//            {
//                get_next_token();
//            }
//            else /* system error - we shouldn't have been called unless the */
//            {    /*         next token is the SYSTEM> a-list tag.    */
//
//                throw new SystemErrorException(mName + 
//                        "this.l0_tok != \"SYSTEM>\".");
//            }
//        }
//
//        /* read the a-list associated with the user section */
//        if ( ! this.abort_parse )
//        {
//            switch ( (this.l0_tok).code )
//            {
//                case L_PAREN_TOK:
//                    parse_system_alist();
//                    break;
//
//                case R_PAREN_TOK:
//                    post_warning_message(EMPTY_ALIST_ENTRY_WARN, 
//                            "The SYSTEM> section appears ! to have a value.\n"); 
//                    break;
//
//                case INT_TOK:
//                case ERROR_TOK:
//                case SYMBOL_TOK:
//                case FLOAT_TOK:
//                case STRING_TOK:
//                case BOOL_TOK:
//                case ALIST_LABEL_TOK:
//                case PRIVATE_VAL_TOK:
//                case SETF_TOK:
//                case DB_VAR_TOK:
//                case QUOTE_TOK:
//                    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
//                        "The value of the SYSTEM> section must be a list.\n"); 
//
//                    if ( ! this.abort_parse )
//                    {
//                        get_next_token();
//                    }
//                    break;
//
//
//                case EOF_TOK:
//                    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                                       "EOF in the SYSTEM> section.\n", 
//                                       true, true);
//                    break;
//
//                 default:
//                     throw new SystemErrorException(mName + 
//                             "Encountered unknown token type.");
//                     break;
//            }
//        }
//
//        /* check for EOF */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code == EOF_TOK )
//            {
//                post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
//                                   "EOF in SYSTEM> section.\n", true, true);
//            }
//        }
//
//        /* discard any excess values that may appear in the SYSTEM> section a-list */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code != R_PAREN_TOK )
//            {
//                discard_excess_alist_entry_values();
//
//                if ( ! this.abort_parse )
//                {
//                    post_warning_message(EXCESS_VALUES_IN_ALIST_ENTRY_WARN,
//                        "Excess values encountered in the SYSTEM> section.\n");
//                }
//            }
//        }
//
//        /* finally, consume the closing parenthesis */
//        if ( ! this.abort_parse )
//        {
//            if ( (this.l0_tok).code == R_PAREN_TOK )
//            {
//                get_next_token();
//            }
//            else 
//            {
//                /* since we are cleaning up any excess values in the system 
//                 * section this else clause is unreachable at present.  Should 
//                 * we choose to drop the above attempt at error recovery, this 
//                 * clause will again become reachable.
//                 */
//
//                post_warning_message(RIGHT_PAREN_EXPECTED_WARN, 
//                        "The closing parenthesis was missing from the " +
//                        "SYSTEM> section.\n");
//            }
//        }
//
//        return;
//
//    } /* macshapa_odb_reader::parse_system_section() */


        
    /*** Parser section 6 -- methods for parsing predicates ***/

    /*************************************************************************
     *
     * discard_excess_pred_value_arguments()
     *
     * A predicate value is generated by the following productions:
     *
     *     <pred_value> --> '(' <pred_name> (<pred_arg>)+ ')'
     *
     * with the maximum number of arguments specified by the predicate
     * definition.
     *
     * However, it is always possible that the user may include more
     * than the maximum number of arguments.  In such cases, we simply 
     * discard all the excess items until we come to the closing parenthesis.
     *
     *                                              - 10/3/95
     *
     * Parameters:
     *
     *    - None.
     *
     * Returns:  Void.
     *
     * Changes:
     *
     *    - None.
     *
     *************************************************************************/

    private void discard_excess_pred_value_arguments()
        throws SystemErrorException,
               java.io.IOException
    {
        final String mName = 
                "macshapa_odb_reader::discard_excess_pred_value_arguments()";
        boolean done;

        if ( this.abort_parse )
        {
            throw new SystemErrorException(mName + 
                    "this.abort_parse TRUE on entry");
        }
        
        if ( (this.l0_tok).code == R_PAREN_TOK )
        {
            throw new SystemErrorException(mName + 
                    "pred value appears not to contain excess arguments.");
        }
        
        /* go ahead and read and then discard the excess arguments */
        
        done = false;

        while ( ( ! this.abort_parse ) && 
                ( ! done ) )
        {
            switch ( (this.l0_tok).code )
            {
                case R_PAREN_TOK:
                     done = true;
                     break;

                case L_PAREN_TOK:
                    parse_arbitrary_list();
                    break;

                case ERROR_TOK:
                case SYMBOL_TOK:
                case INT_TOK:
                case FLOAT_TOK:
                case STRING_TOK:
                case BOOL_TOK:
                case ALIST_LABEL_TOK:
                case PRIVATE_VAL_TOK:
                case SETF_TOK:
                case DB_VAR_TOK:
                case QUOTE_TOK:
                    get_next_token();
                    break;

                case EOF_TOK:
                    done = true;
                    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
                            "EOF encountered in a predicate value with " +
                            "excess arguments.\n", true, true); 
                    break;

                default:
                    throw new SystemErrorException(mName +
                            "Encountered unknown token type.");
	            /* commented out to keep the compiler happy */
                    // break;
            }
        }

        return;

    } /* macshapa_odb_reader::discard_excess_pred_value_arguments() */


    /*************************************************************************
     *
     * dump_predicate_definition_to_listing()
     *
     * Dump the supplied predicate to the listing file
     *
     *                                            - 6/8/08
     *
     * Parameters:
     *
     *	  - None.
     *
     * Returns:  String containing current settings of all fields that can 
     *	    be modified by the HEADER> section.
     *
     * Changes:
     *
     *    - None.
     *
     *************************************************************************/
    
    private void dump_predicate_definition_to_listing(String name, 
                                                      Vector<String> args, 
                                                      boolean variable_length)
        throws SystemErrorException
    {
        final String mName = 
                "macshapa_odb_reader::dump_predicate_definition_to_listing()";
        
        throw new SystemErrorException(mName + "method not implemented");
        
	/* commented out to keep the compiler happy */
        // return;
        
    } /* macshapa_odb_reader::dump_predicate_definition_to_listing() */


    /*************************************************************************
     *
     * parse_col_pred_value()
     *
     * This method parses column predicate value in the context of a 
     * predicate argument, or a matrix argument.  
     * 
     * If no errors are detected, the function constructs an instance of 
     * ColPred and loads it with a representation of the column predicate 
     * and its arguments.  
     *
     * If errors are detected, the function simply returns an empty column
     * predicate.
     *
     * The production generating column predicate is the same as that generating
     * a predicate, and is given below:
     * 
     *     <pred_value> --> '(' <pred_name> (<pred_arg>)+ ')'
     * 
     *     <pred_arg> --> <quote_string> | <nominal> | <pred_value> | 
     *                    <integer> | <float> | <time_stamp> | <formal_arg>
     * 
     *     <time_stamp> --> '(' 'TIME>' <integer> ')' 
     *
     * Note that MacSHAPA creates a predicate for every column variable, and
     * treats these predicates the same as all other predicates.  In contrast,
     * OpenSHAPA with its option for strong typing keeps predicates
     * and predicates implied by columns as separate types.
     *
     * This causes some incompatibilities with MacSHAPA databases.  However,
     * it is hoped that these incompatibilities will be minor in practice.
     *
     * Note that we are guaranteed that upon entry, the next token is a left 
     * parenthesis. 
     * 
     *                                              - 12/11/08
     *
     * Parameters:
     *
     *	  - farg:  Reference to a copy of the formal argument we 
     *          are going to create a data value for.  It must be of type
     *          COL_PREDICATE or type UNTYPED.
     *
     * Returns:  Instance of ColPredDataValue containing a representation of 
     *          the specified column predicate value.
     *
     * Changes:
     *
     *    - None.
     *
     **************************************************************************/

    private DataValue parse_col_pred_value(FormalArgument farg)
        throws SystemErrorException,
               java.io.IOException
    {
        final String mName = "macshapa_odb_reader::parse_col_pred_value()";
        boolean done;
        boolean varLen = false;
        int arg_num = 0;
        int num_fargs = 0;
        MatrixVocabElement mve = null;
        FormalArgument next_farg = null;
        ColPred value = null; 
        Vector<DataValue> argList = null;
        DataValue next_arg = null;
        DataValue arg;

        if ( this.abort_parse )
        {
            throw new SystemErrorException(mName + 
                    "this.abort_parse TRUE on entry");
        }
        
        if ( (this.l0_tok).code != L_PAREN_TOK )
        {
            throw new SystemErrorException(mName + 
                    "(this.l0_tok).code != L_PAREN_TOK on entry");
        }
        
        if ( farg == null )
        {
            throw new SystemErrorException(mName + "farg null on entry");
        }
        
        if ( ( farg.fargType != FormalArgument.FArgType.COL_PREDICATE ) ||
             ( farg.fargType != FormalArgument.FArgType.UNTYPED ) )
        {
            throw new SystemErrorException(mName + 
                    "Supplied farg cannot be replaced with a column predicate.");
        }
        
        if ( farg.getID() == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + 
                                           "Supplied farg has invalid ID.");
        }
        
        /* try to parse the predicate value */
        
        /* first check to make sure that we are dealing with a defined 
         * column predicate.
         */
        if ( (this.l1_tok).code == SYMBOL_TOK )
        {
            if ( ((this.l1_tok).aux & COLUMN_FLAG) != 0 )
            {
                if ( this.db.vl.matrixInVocabList(this.l1_tok.str.toString()) )
                {
                    mve = this.db.getMatrixVE(this.l1_tok.str.toString());
                }
            }
            else /* the symbol is not a column variable name */
            {
                /* the name of the column in the column predicate value is not a 
                 * valid column variable name -- force it to a valid column
                 * variable name, issue a warning message, and look it up
                 * again.
                 */

                post_warning_message(INVALID_COL_PRED_NAME_IN_COL_PRED_VALUE_WARN, 
                        "Will coerce the name to a valid column predicate or " +
                        "spreadsheet variable name.\n");

                if ( ! this.abort_parse )
                {
                    this.l1_tok.coerce_symbol_token_to_spreadsheet_variable_name();
                }

                if ( ! this.abort_parse )
                {
                    if ( this.db.vl.matrixInVocabList(this.l1_tok.str.toString()) )
                    {
                        mve = this.db.getMatrixVE(this.l1_tok.str.toString());
                    }
                }
            } 

            if ( ! this.abort_parse )
            {
                if ( mve == null )
                {
                    post_warning_message(UNDEFINED_COL_PRED_WARN, null);
                }
            }
        }
        else /* it ain't a predicate -- discard it, issue a warning & construct 
              * a empty predicate place holder 
              */
        {
            post_warning_message(PRED_VALUE_EXPECTED_WARN, null);
        }

        if ( mve == null )
        {
            /* we are done -- discard the list & construct an empty instance
             * of ColPred to return.
             */

            if ( ! this.abort_parse )
            {
                parse_arbitrary_list();
            }
            
            value = new ColPred(this.db);

        }
        else /* eat the left paren & col pred name tokens, and set up to parse 
              * the argument list 
              */
        {
	    arg_num = 0;

            num_fargs = mve.getNumCPFormalArgs();
            varLen = mve.getVarLen();
            
            if ( num_fargs < 4 )
            {
                throw new SystemErrorException(mName + 
                        "col pred has less than 4 arguments?!?");
            }
        
            argList = new Vector<DataValue>();
            
            next_farg = mve.getCPFormalArg(0);
            
            if ( next_farg.getID() == DBIndex.INVALID_ID )
            {
                throw new SystemErrorException(mName + 
                        "next_farg has invalid ID?!?");
            }

            if ( ! this.abort_parse )
            {
                get_next_token();  /* eat the left parenthesis */
            }

            if ( ! this.abort_parse )
            {
                get_next_token();  /* eat the predicate name */
            }
        }

        /* parse the arguments of the column predicate or predicate value */
        if ( mve != null ) 
        {
            done = false;
            next_arg = null;

            while ( ( next_farg != null ) && 
                    ( ! done ) && 
                    ( ! this.abort_parse ) )
            {
                switch ( (this.l0_tok).code )
                {
                    case R_PAREN_TOK:
                         done = true;
                         break;

                    case L_PAREN_TOK:
                        if ( ( (this.l1_tok).code == ALIST_LABEL_TOK ) &&
                             ( (this.l1_tok).aux == TIME_LABEL ) )
                        {
                            next_arg = parse_time_stamp(next_farg);
                        }
                        else if ( next_farg.getFargType() ==
                                  FormalArgument.FArgType.COL_PREDICATE )
                        {
                            next_arg = parse_col_pred_value(next_farg);
                        }
                        else if ( next_farg.getFargType() ==
                                  FormalArgument.FArgType.PREDICATE )
                        {
                            next_arg = parse_pred_value(next_farg);
                        }
                        else if ( ( (this.l1_tok).code == SYMBOL_TOK ) &&
                                  ( ((this.l1_tok).aux & COLUMN_FLAG) != 0 ) &&
                                  ( this.db.matrixVEExists(
                                        this.l1_tok.str.toString()) ) )
                        {
                            next_arg = parse_col_pred_value(next_farg);
                        }
                        else
                        {
                            next_arg = parse_pred_value(next_farg);
                        }
                        break;

                    case FLOAT_TOK:
                        next_arg = parse_float_value(next_farg);
                        break;

                        
                    case INT_TOK:
                        next_arg = parse_integer_value(next_farg);
                        break;

                    case STRING_TOK:
                        next_arg = parse_quote_string_value(next_farg);
                        break;

                    case SYMBOL_TOK:
                        if ( ((this.l0_tok).aux & FORMAL_ARG_FLAG) != 0 )
                        {
                            next_arg = parse_formal_arg_value(next_farg);
                        }
                        else if ( ((this.l0_tok).aux & 
                                   (PRED_FLAG | COLUMN_FLAG | NOMINAL_FLAG)) != 0 )
                        {
                            next_arg = parse_nominal_value(next_farg);
                        }
                        else /* in theory, this can't happen */
                        {
                            throw new SystemErrorException(mName + 
                                    "(this.l0_tok).aux corrupt?!?");
                        }
                        break;

                    case ALIST_LABEL_TOK:
                    case BOOL_TOK:
                    case PRIVATE_VAL_TOK:
                    case SETF_TOK:
                    case DB_VAR_TOK:
                    case QUOTE_TOK:
                    case ERROR_TOK:
                        post_warning_message(
                                ILLEGAL_ATOM_IN_COL_PRED_ARG_LIST_WARN, 
                                null);

                        if ( ! this.abort_parse )
                        {
                            get_next_token();
                        }
                        break;

                    case EOF_TOK:
                        done = true;
                        post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
                                "EOF encountered in a predicate value " +
                                "argument list.\n", true, true);
                        break;

                     default:
                         throw new SystemErrorException(mName + 
                                 "Encountered unknown token type.");
                         /* commented out to keep the compiler happy */
                         // break;

                } /* end switch */
                
                if ( ( next_arg == null ) && ( ! this.abort_parse ) )
                {
                    // construct an empty argument
                    next_arg = new UndefinedDataValue(this.db, 
                                                      next_farg.getID(), 
                                                      next_farg.getFargName());
                }
                
                if ( ! this.abort_parse )
                {
                    argList.add(next_arg);
                    next_arg = null;
                }

                if ( ( ! this.abort_parse ) && ( ! done ) )
                {
                    arg_num++;
                    
                    if ( num_fargs > arg_num )
                    {
                        next_farg = mve.getCPFormalArg(arg_num);
                    }
                    else
                    {
                        next_farg = null;
                    }
                }
            } /* end while */

            /* check for EOF */
            if ( ! this.abort_parse )
            {
                if ( (this.l0_tok).code == EOF_TOK )
                {
                    post_error_message(UNEXPECTED_END_OF_FILE_ERR,                  
                            "EOF encountered in a column predicate value " +
                            "argument list.\n", true, true); 
                }
            }

            /* discard any excess values that may appear in the column 
             * predicate value argument list 
             */
            if ( ! this.abort_parse )
            {
                if ( ! done )
                {
                    if ( next_farg != null )
                    {
                        throw new SystemErrorException(mName + 
                                "( ! done ) && ( next_farg != null )");    
                    }
                    else if ( (this.l0_tok).code != R_PAREN_TOK )
                    {
                        discard_excess_pred_value_arguments();

                        if ( ! this.abort_parse )
                        {
                            post_warning_message(
                                    EXCESS_ARGS_IN_COL_PRED_VALUE_WARN,
                                    null);
                        }
                    }
                }
            }

            /* consume the closing parenthesis in the predicate value 
             * argument list. 
             */
            if ( ! this.abort_parse )
            {
                if ( (this.l0_tok).code == R_PAREN_TOK )
                {
                    get_next_token();
                }
                else /* system error */
                {
                    throw new SystemErrorException(mName + 
                            "(this.l0_tok).code != R_PAREN_TOK.");
                }
            }


            /* if we ran out of arguments before we ran out of formal arguments, 
             * must issue a warning and insert formal arguments.  Note however 
             * that there is an exception in the case of variable length 
             * predicates.  In this case, we simply make sure that there is at 
             * least one argument, inserting a formal argument if there is not.
             */
            // TODO: Figure out how we will support variable length preds and 
            //       and update this code accordingly.
            if ( ! this.abort_parse )
            {
                if ( next_farg != null )
                {
                    if ( varLen )
                    {
                        /* make sure we have at least one argument */
                        if ( arg_num == 0 )
                        {
                            post_warning_message(
                                    REQ_ARGS_MISSING_FROM_PRED_VAL_WARN,
                                    null);
                        }
                        
                        // TODO:  Here we make sure that the argument list 
                        //        contains at least one argument in the 
                        //        case of a variable length predicate or 
                        //        column predicate.
                        //
                        //        This may or may not be the right thing to 
                        //        do.  Return to this when we have variable
                        //        length arguments hammered out.
                        
                        next_arg = new UndefinedDataValue(this.db, 
                                                          next_farg.getID(), 
                                                          next_farg.getFargName());
                        argList.add(next_arg);
                        next_arg = null;
                    }
                    else /* fill in the missing arguments with formal arguments */
                    {
                        post_warning_message(
                                REQ_ARGS_MISSING_FROM_COL_PRED_VAL_WARN,
                                null);

                        while ( ( ! this.abort_parse ) && 
                                ( next_farg != null ) )
                        {
                            next_arg = new UndefinedDataValue(this.db, 
                                                          next_farg.getID(), 
                                                          next_farg.getFargName());
                            argList.add(next_arg);
                            next_arg = null;

                            arg_num++;

                            if ( num_fargs > arg_num )
                            {
                                next_farg = mve.getCPFormalArg(arg_num);
                            }
                            else
                            {
                                next_farg = null;
                            }
                        }
                    }
                }
            }

            /* Finally, construct the instance of ColPred, 
             * load it into an instance of ColPredDataValue, and return 
             * the data value.
             */
            value = new ColPred(this.db, mve.getID(), argList);

        } /* end if */

        arg = new ColPredDataValue(this.db, farg.getID(), value);

        return(arg);

    } /* macshapa_odb_reader::parse_col_pred_value() */


    /*************************************************************************
     *
     * parse_pred_cell_value()
     *
     * This method parses the value of a spreadsheet variable cell in the 
     * context of a predicate spreadsheet variable.  The production generating 
     * such a cell value is given below:
     * 
     *     <pred_cell_value> --> '(' ')' | <pred_value>
     * 
     *     <pred_value> --> '(' <pred_name> (<pred_arg>)+ ')'
     * 
     *     <pred_arg> --> <quote_string> | <nominal> | <pred_value> | 
     *                    <integer> | <float> | <time_stamp> | <formal_arg>
     * 
     *     <time_stamp> --> '(' 'TIME>' <integer> ')' 
     *
     * If there are no type conflicts, the function returns a PredDataValue 
     * containing the specified value.
     *
     * Type conficts are handled by creating an empty PredDataValue, issuing 
     * a warning message, and consuming the offending value.
     * 
     *                                              - 7/13/08
     *
     * Parameters:
     *
     *	  - farg:  Reference to a copy of the formal argument we 
     *          are going to create a data value for.  As this function is
     *          only called to parse the value of an integer cell, the 
     *          farg must be of type PREDICATE. 
     *
     * Returns:  Void.
     *
     * Changes:
     *
     *    - None.
     *
     *************************************************************************/

    private DataValue parse_pred_cell_value(FormalArgument farg)
        throws SystemErrorException,
               java.io.IOException
    {
        final String mName = "macshapa_odb_reader::parse_pred_cell_value()";
        final String overflow_mssg = 
                "Overflow occured in a predicate cell value.\n";
        DataValue dv = null;

        if ( this.abort_parse )
        {
            throw new SystemErrorException(mName + 
                    "this.abort_parse TRUE on entry");
        }
        
        if ( farg == null )
        {
            throw new SystemErrorException(mName + "farg null on entry");
        }
        
        if ( farg.fargType != FormalArgument.FArgType.PREDICATE )
        {
            throw new SystemErrorException(mName + 
                    "Supplied farg cannot be replaced with an predicate.");
        }
        
        if ( farg.getID() == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + 
                                           "Supplied farg has invalid ID.");
        }
        
        /* try to parse the predicate cell value */
        
        switch ( (this.l0_tok).code )
        {
            case FLOAT_TOK:
            case INT_TOK:
            case SYMBOL_TOK:
            case STRING_TOK:
            case BOOL_TOK:
            case PRIVATE_VAL_TOK:
            case ALIST_LABEL_TOK:
            case SETF_TOK:
            case DB_VAR_TOK:
            case QUOTE_TOK:
                post_warning_message(S_VAR_CELL_VALUE_TYPE_MISMATCH_WARN, 
                        "Will discard the value and leave the predicate " +
                        "cell empty.\n"); 

                if ( ! this.abort_parse )
                {
                    get_next_token();
                }
                break;

            case L_PAREN_TOK:
                if ( (this.l1_tok).code == R_PAREN_TOK ) /* empty cell */
                {
                    if ( ! this.abort_parse ) // eat left paren
                    {
                        get_next_token();
                    }

                    if ( ! this.abort_parse ) // eat right paren
                    {
                        get_next_token();
                    }
                }
                else /* should be a predicate value */
                {
                    dv = parse_pred_value(farg);
                }
                break;

            case R_PAREN_TOK:
                post_warning_message(S_VAR_CELL_VALUE_MISSING_WARN, 
                        "The predicate cell will be left empty.\n"); 
                break;

            case ERROR_TOK:
                post_warning_message(S_VAR_CELL_VALUE_TYPE_MISMATCH_WARN, 
                        "The value is an ill formed token.  The predicate " +
                        "cell will be left empty.\n"); 

                if ( ! this.abort_parse )
                {
                    get_next_token();
                }
                break;

            case EOF_TOK:
                post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
                                   "EOF in a predicate cell value.\n", 
                                   true, true);
                break;

            default:
                throw new SystemErrorException(mName + 
                        "Encountered unknown token type.");
                /* commented out to keep the compiler happy */
                // break;
        }

        if ( dv == null )
        {
            dv = new PredDataValue(this.db, 
                                   farg.getID(), 
                                   new Predicate(this.db));
        }

        return(dv);

    } /* parse_pred_cell_value() */


    /*************************************************************************
     *
     * parse_pred_value()
     *
     * This method parses a predicate or column predicate value in the context 
     * of a predicate cell, a predicate argument, or a matrix argument.  
     * 
     * If no errors are detected, the function constructs an instance of 
     * Predicate and loads it with a representation of the predicate and its
     * arguments.  
     *
     * If errors are detected, the function simply returns an empty predicate.
     *
     * The production generating a predicate value is given below:
     * 
     *     <pred_value> --> '(' <pred_name> (<pred_arg>)+ ')'
     * 
     *     <pred_arg> --> <quote_string> | <nominal> | <pred_value> | 
     *                    <integer> | <float> | <time_stamp> | <formal_arg>
     * 
     *     <time_stamp> --> '(' 'TIME>' <integer> ')' 
     *
     * Note that we are guaranteed that upon entry, the next token is a left 
     * parenthesis. 
     * 
     *                                              - 12/11/08
     *
     * Parameters:
     *
     *	  - farg:  Reference to a copy of the formal argument we 
     *          are going to create a data value for.  It must be of type
     *          PREDICATE or type UNTYPED.
     *
     * Returns:  Instance of PredDataValue containing a representation of the 
     *          specified predicate.
     *
     * Changes:
     *
     *    - None.
     *
     **************************************************************************/

    private DataValue parse_pred_value(FormalArgument farg)
        throws SystemErrorException,
               java.io.IOException
    {
        final String mName = "macshapa_odb_reader::parse_pred_value()";
        final String overflow_mssg = "Overflow occured in a predicate value.\n";
        boolean done;
        boolean varLen = false;
        int arg_num = 0;
        int num_fargs = 0;
        PredicateVocabElement pve = null;
        Predicate value = null; 
        Vector<DataValue> argList = null;
        FormalArgument next_farg = null;
        DataValue next_arg = null;
        DataValue arg = null;

        if ( this.abort_parse )
        {
            throw new SystemErrorException(mName + 
                    "this.abort_parse TRUE on entry");
        }
        
        if ( (this.l0_tok).code != L_PAREN_TOK )
        {
            throw new SystemErrorException(mName + 
                    "(this.l0_tok).code != L_PAREN_TOK on entry");
        }
        
        if ( farg == null )
        {
            throw new SystemErrorException(mName + "farg null on entry");
        }
        
        if ( ( farg.fargType != FormalArgument.FArgType.PREDICATE ) ||
             ( farg.fargType != FormalArgument.FArgType.UNTYPED ) )
        {
            throw new SystemErrorException(mName + 
                    "Supplied farg cannot be replaced with an predicate.");
        }
        
        if ( farg.getID() == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + 
                                           "Supplied farg has invalid ID.");
        }
        
        /* try to parse the predicate value */
        
        /* first check to make sure that we are dealing with a defined 
         * predicate or column predicate.
         */
        if ( (this.l1_tok).code == SYMBOL_TOK )
        {
            if ( ((this.l1_tok).aux & PRED_FLAG) != 0 )
            {
                if ( this.db.vl.predInVocabList(this.l1_tok.str.toString()) )
                {
                    pve = this.db.getPredVE(this.l1_tok.str.toString());
                }
            }
            else /* the symbol is not a predicate name */
            {
                /* the name of the predicate in the predicate value is not a 
                 * valid predicate name -- force it to a valid predicate name, 
                 * issue a warning message, and look it up.
                 */

                post_warning_message(INVALID_PRED_NAME_IN_PRED_VALUE_WARN, 
                        "Will coerce the name to a valid predicate name.\n");

                if ( ! this.abort_parse )
                {
                    this.l1_tok.coerce_symbol_token_to_pred_name();
                }

                if ( ! this.abort_parse )
                {
                    if ( this.db.vl.predInVocabList(this.l1_tok.str.toString()) )
                    {
                        pve = this.db.getPredVE(this.l1_tok.str.toString());
                    }
                }
            } 

            if ( ! this.abort_parse )
            {
                if ( pve == null )
                {
                    post_warning_message(UNDEFINED_PRED_WARN, null);
                }
            }
        }
        else /* it ain't a predicate -- discard it, issue a warning & construct 
              * a empty predicate place holder 
              */
        {
            post_warning_message(PRED_VALUE_EXPECTED_WARN, null);
        }

        if ( pve == null )
        {
            /* we are done -- discard the list & construct an empty instance
             * of Predicate to return.
             */

            if ( ! this.abort_parse )
            {
                parse_arbitrary_list();
            }
            
            value = new Predicate(this.db);

        }
        else /* eat the left paren & pred name tokens, and set up to parse the 
              * argument list 
              */
        {
	    arg_num = 0;
            
            num_fargs = pve.getNumFormalArgs();
            varLen = pve.getVarLen();
            
            if ( num_fargs < 1 )
            {
                throw new SystemErrorException(mName + 
                        "pve or mve has no arguments?!?");
            }
        
            argList = new Vector<DataValue>();
            
            next_farg = pve.getFormalArg(0);
            
            if ( farg.getID() == DBIndex.INVALID_ID )
            {
                throw new SystemErrorException(mName + 
                        "next_farg has invalid ID?!?");
            }

            if ( ! this.abort_parse )
            {
                get_next_token();  /* eat the left parenthesis */
            }

            if ( ! this.abort_parse )
            {
                get_next_token();  /* eat the predicate name */
            }

        }

        /* parse the arguments of the predicate value */
        if ( pve != null ) 
        {
            done = false;
            next_arg = null;

            while ( ( next_farg != null ) && 
                    ( ! done ) && 
                    ( ! this.abort_parse ) )
            {
                switch ( (this.l0_tok).code )
                {
                    case R_PAREN_TOK:
                         done = true;
                         break;

                    case L_PAREN_TOK:
                        if ( ( (this.l1_tok).code == ALIST_LABEL_TOK ) &&
                             ( (this.l1_tok).aux == TIME_LABEL ) )
                        {
                            next_arg = parse_time_stamp(next_farg);
                        }
                        else if ( next_farg.getFargType() ==
                                  FormalArgument.FArgType.COL_PREDICATE )
                        {
                            next_arg = parse_col_pred_value(next_farg);
                        }
                        else if ( next_farg.getFargType() ==
                                  FormalArgument.FArgType.PREDICATE )
                        {
                            next_arg = parse_pred_value(next_farg);
                        }
                        else if ( ( (this.l1_tok).code == SYMBOL_TOK ) &&
                                  ( ((this.l1_tok).aux & COLUMN_FLAG) != 0 ) &&
                                  ( this.db.matrixVEExists(
                                        this.l1_tok.str.toString()) ) )
                        {
                            next_arg = parse_col_pred_value(next_farg);
                        }
                        else
                        {
                            next_arg = parse_pred_value(next_farg);
                        }
                        break;

                    case FLOAT_TOK:
                        next_arg = parse_float_value(next_farg);
                        break;
                        
                    case INT_TOK:
                        next_arg = parse_integer_value(next_farg);
                        break;

                    case STRING_TOK:
                        next_arg = parse_quote_string_value(next_farg);
                        break;

                    case SYMBOL_TOK:
                        if ( ((this.l0_tok).aux & FORMAL_ARG_FLAG) != 0 )
                        {
                            next_arg = parse_formal_arg_value(next_farg);
                        }
                        else if ( ((this.l0_tok).aux & 
                                   (PRED_FLAG | COLUMN_FLAG | NOMINAL_FLAG)) != 0 )
                        {
                            next_arg = parse_nominal_value(next_farg);
                        }
                        else /* in theory, this can't happen */
                        {
                            throw new SystemErrorException(mName + 
                                    "(this.l0_tok).aux corrupt?!?");
                        }
                        break;

                    case ALIST_LABEL_TOK:
                    case BOOL_TOK:
                    case PRIVATE_VAL_TOK:
                    case SETF_TOK:
                    case DB_VAR_TOK:
                    case QUOTE_TOK:
                    case ERROR_TOK:
                        post_warning_message(ILLEGAL_ATOM_IN_PRED_ARG_LIST_WARN, 
                                             null);

                        if ( ! this.abort_parse )
                        {
                            get_next_token();
                        }
                        break;

                    case EOF_TOK:
                        done = true;
                        post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
                                "EOF encountered in a predicate value " +
                                "argument list.\n", true, true);
                        break;

                     default:
                         throw new SystemErrorException(mName + 
                                 "Encountered unknown token type.");
                         /* commented out to keep the compiler happy */
                         // break;

                } /* end switch */
                
                if ( ( next_arg == null ) && ( ! this.abort_parse ) )
                {
                    // construct an empty argument
                    next_arg = new UndefinedDataValue(this.db, 
                                                      next_farg.getID(), 
                                                      next_farg.getFargName());
                }
                
                if ( ! this.abort_parse )
                {
                    argList.add(next_arg);
                    next_arg = null;
                }

                if ( ( ! this.abort_parse ) && ( ! done ) )
                {
                    /* in the case of a system variable length predicate that 
                     * is not a predicate associated with a spreadsheet variable 
                     * (i.e. and(), or(), etc.) we need to add new formal 
                     * arguments as necessary.  Do this as follows:
                     */

                    if ( ( num_fargs <= arg_num ) &&
                         ( pve.getVarLen() ) &&
                         ( pve.getSystem() ) )
                    {
                        // TODO: Write code to support this -- for now 
                        //       throw a system error.
                        throw new SystemErrorException(mName +
                                "Adding args to var len predicates isn't " +
                                "supported yet.");
                    }
                    
                    arg_num++;
                    
                    if ( num_fargs > arg_num )
                    {
                        next_farg = pve.getFormalArg(arg_num);
                    }
                    else
                    {
                        next_farg = null;
                    }
                }
            } /* end while */

            /* check for EOF */
            if ( ! this.abort_parse )
            {
                if ( (this.l0_tok).code == EOF_TOK )
                {
                    post_error_message(UNEXPECTED_END_OF_FILE_ERR,                  
                            "EOF encountered in a predicate value " +
                            "argument list.\n", true, true); 
                }
            }

            /* discard any excess values that may appear in the predicate 
             * value argument list 
             */
            if ( ! this.abort_parse )
            {
                if ( ! done )
                {
                    if ( next_farg != null )
                    {
                        throw new SystemErrorException(mName + 
                                "( ! done ) && ( next_farg != null )");    
                    }
                    else if ( (this.l0_tok).code != R_PAREN_TOK )
                    {
                        discard_excess_pred_value_arguments();

                        if ( ! this.abort_parse )
                        {
                            post_warning_message(EXCESS_ARGS_IN_PRED_VALUE_WARN,
                                                 null);
                        }
                    }
                }
            }

            /* consume the closing parenthesis in the predicate value 
             * argument list. 
             */
            if ( ! this.abort_parse )
            {
                if ( (this.l0_tok).code == R_PAREN_TOK )
                {
                    get_next_token();
                }
                else /* system error */
                {
                    throw new SystemErrorException(mName + 
                            "(this.l0_tok).code != R_PAREN_TOK.");
                }
            }


            /* if we ran out of arguments before we ran out of formal arguments, 
             * must issue a warning and insert formal arguments.  Note however 
             * that there is an exception in the case of variable length 
             * predicates.  In this case, we simply make sure that there is at 
             * least one argument, inserting a formal argument if there is not.
             */
            // TODO: Figure out how we will support variable length preds and 
            //       and update this code accordingly.
            if ( ! this.abort_parse )
            {
                if ( next_farg != null )
                {
                    if ( varLen )
                    {
                        /* make sure we have at least one argument */
                        if ( arg_num == 0 )
                        {
                            post_warning_message(
                                    REQ_ARGS_MISSING_FROM_PRED_VAL_WARN,
                                    null);
                        }
                        
                        // TODO:  Here we make sure that the argument list 
                        //        contains at least one argument in the 
                        //        case of a variable length predicate or 
                        //        column predicate.
                        //
                        //        This may or may not be the right thing to 
                        //        do.  Return to this when we have variable
                        //        length arguments hammered out.
                        
                        next_arg = new UndefinedDataValue(this.db, 
                                                          next_farg.getID(), 
                                                          next_farg.getFargName());
                        argList.add(next_arg);
                        next_arg = null;
                    }
                    else /* fill in the missing arguments with formal arguments */
                    {
                        post_warning_message(REQ_ARGS_MISSING_FROM_PRED_VAL_WARN,
                                             null);

                        while ( ( ! this.abort_parse ) && 
                                ( next_farg != null ) )
                        {
                            next_arg = new UndefinedDataValue(this.db, 
                                                          next_farg.getID(), 
                                                          next_farg.getFargName());
                            argList.add(next_arg);
                            next_arg = null;

                            arg_num++;

                            if ( num_fargs > arg_num )
                            {
                                next_farg = pve.getFormalArg(arg_num);
                            }
                            else
                            {
                                next_farg = null;
                            }
                        }
                    }
                }
            }

            /* Finally, construct the instance of predicate, load it into
             * a predicate data value, and return it. */
            value = new Predicate(this.db, pve.getID(), argList);
            arg = new PredDataValue(this.db, farg.getID(), value);

        } /* end if */

        return(arg);

    } /* macshapa_odb_reader::parse_pred_value() */
    

    /*************************************************************************
     *
     * parse_quote_string_value()
     *
     * This method parses an quote string value in the context of a matrix or 
     * predicate argument.
     *
     * If there are no type conflicts, the function simply creates an 
     * QuoteStringDataValue containing the value specified, and returns a 
     * reference to the newly created data value.
     *
     * Type conficts are handled by  issuing a warning message, consuming the 
     * offending value, and returning an undefined data value.
     *        
     *                                                     - 6/12/08
     *
     * Parameters:
     *
     *	  - farg:  Reference to a copy of the formal argument we 
     *          are going to create a data value for.    This function is
     *          called to parse the value of a matrix or predicate argument,
     *          so the farg must be of type QUOTE_STRING or of type UNTYPED. 
     *
     * Returns:  Void.
     *
     * Changes:
     *
     *  - None.
     *
     *************************************************************************/

    private DataValue parse_quote_string_value(FormalArgument farg)
	throws SystemErrorException,
               java.io.IOException
    {
	final String mName = "macshapa_odb_reader::parse_integer_value()";
	final String overflow_mssg = "Overflow occured in a float value.\n";
        String value = null;
        DataValue dv = null;
        

	if ( this.abort_parse )
	{
	    throw new SystemErrorException(mName + 
		    "this.abort_parse true on entry");
	}
        
        if ( farg == null )
        {
            throw new SystemErrorException(mName + "farg null on entry");
        }
        
        if ( farg.getID() == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + 
                                           "Supplied farg has invalid ID.");
        }
        
        /* we must only be called if the next token is a float -- scream
         * and die if it is not.
         */
        
        if ( (this.l0_tok).code != STRING_TOK )
        {
            throw new SystemErrorException(mName + 
                                           "(this.l0_tok).code != STRING_TOK");
        }
        
        value = this.l0_tok.str.toString();
	
        if ( ( farg.fargType != FormalArgument.FArgType.QUOTE_STRING ) ||
             ( farg.fargType != FormalArgument.FArgType.UNTYPED ) )
        {
            if ( ((this.l0_tok).aux & QSTRING_FLAG) == 0 )
            {
                this.l0_tok.coerce_text_qstring_to_qstring();

                if ( ! this.abort_parse )
                {
                    post_warning_message(
                            ILLEGAL_CHARS_IN_QUOTE_STR_WARN, 
                            "The quote string appeared in a " +
                            "matrix or predicate value.\n");
                }
            }

            dv = new QuoteStringDataValue(this.db, farg.getID(), value);
        }
        else
        {
            /* type mismatch between formal argument and value.  Construct
             * an udefined data value, and flag a warning.
             */
            dv = new UndefinedDataValue(this.db, 
                                        farg.getID(), 
                                        farg.getFargName());
            
            post_warning_message(FARG_ARG_TYPE_MISMATCH_WARN, null);
        }
        
        if ( ! this.abort_parse ) /* consume the token */
        {
            get_next_token();
        }
        
	return(dv);

    } /* macshapa_odb_reader::parse_quote_string_value() */


    /*************************************************************************
     *
     * parse_time_stamp()
     *
     * This method parses a TIME> attribute, which at present can only 
     * appear in the context of either a predicate value or a matrix cell
     * value.  This attribute is generated by the following production:
     * 
     *     <time_stamp> --> '(' 'TIME>' <integer> ')' 
     *
     * If there are no type conflicts, the function simply creates a
     * TimeStampDataValue containing the value specified, and returns a 
     * reference to the newly created data value.
     *
     * Type conficts are handled by  issuing a warning message, consuming the 
     * offending value, and returning an undefined data value.
     *
     *                                              - 7/13/08
     *
     * Parameters:
     *
     *	  - farg:  Reference to a copy of the formal argument we 
     *          are going to create a data value for.  It must be of type
     *          TIME_STAMP or type UNTYPED.
     *
     * Returns:  None.
     *
     * Changes:
     *
     *    - None.
     *
     **************************************************************************/

    private DataValue parse_time_stamp(FormalArgument farg)
        throws SystemErrorException,
               java.io.IOException
    {
        final String mName = "macshapa_odb_reader::parse_time_stamp()";
        final String overflow_mssg = 
                "Overflow occured in a TIME> a-list entry.\n";
        final String missing_time_mssg = 
                "A TIME> attribute appears not to contain a value.  " +
                "Default value used.\n";
        final String time_type_mismatch_mssg = 
                "The value of a TIME> attribute must be an integer.  " +
                "Default value used.\n";
        boolean out_of_range;
        long ticks;
        TimeStamp ts = null;
        DataValue dv = null;
        
        ticks = MACSHAPA_MIN_TIME; /* default value */

        if ( this.abort_parse )
        {
            throw new SystemErrorException(mName + 
                    "this.abort_parse TRUE on entry");
        }
        
        if ( farg == null )
        {
            throw new SystemErrorException(mName + "farg null on entry");
        }
        
        if ( farg.getID() == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + 
                                           "Supplied farg has invalid ID.");
        }
        
                
        /* parse the time stamp */
        
        /* first parse the leading left parenthesis */

        if ( (this.l0_tok).code == L_PAREN_TOK )
        {
            get_next_token();
        }
        else /* we shouldn't have been called unless the next token is a '(' */
        {
            throw new SystemErrorException(mName + 
                    "(this.l0_tok).code != L_PAREN_TOK.");
        }

        /* read the a-list entry name */
        if ( ! this.abort_parse )
        {
            if ( ( (this.l0_tok).code == ALIST_LABEL_TOK ) && 
                 ( (this.l0_tok).aux == TIME_LABEL ) )
            {
                get_next_token();
            }
            else /* we shouldn't have been called unless the next token is TIME> */
            {
                throw new SystemErrorException(mName + 
                        "this.l0_tok != TIME>.");
            }
        }

        /* read the value associated with the a-list entry & discard 
         * any excess values 
         */
        if ( ! this.abort_parse )
        {
            switch ( (this.l0_tok).code )
            {
                case INT_TOK:
		    out_of_range = false;

		    if ( ((this.l0_tok).val) < (double)MACSHAPA_MIN_TIME )
		    {
			out_of_range = true;
			ticks = MACSHAPA_MIN_TIME;
		    }
		    else if ( ((this.l0_tok).val) > (double)MACSHAPA_MAX_TIME )
		    {
			out_of_range = true;
			ticks = MACSHAPA_MAX_TIME;
		    }
		    else
		    {
                        ticks = (long)((this.l0_tok).val);
                    }

		    if ( out_of_range )
		    {
                        post_warning_message(TIME_OUT_OF_RANGE_WARN, 
                                "The out of range value appeared in a " +
                                "TIME> attribute\n"); 
		    }

		    if ( ! this.abort_parse )
		    {
			get_next_token();
		    }
		    break;

                case R_PAREN_TOK:
                    post_warning_message(EMPTY_ALIST_ENTRY_WARN, 
                                         missing_time_mssg); 
                    break;

                case L_PAREN_TOK:
                    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
                                         time_type_mismatch_mssg); 

                    if ( ! this.abort_parse )
                    {
                        parse_arbitrary_list();
                    }
                    break;

                case PRIVATE_VAL_TOK:
                case BOOL_TOK:
                case ERROR_TOK:
                case SYMBOL_TOK:
                case FLOAT_TOK:
                case STRING_TOK:
                case ALIST_LABEL_TOK:
                case SETF_TOK:
                case DB_VAR_TOK:
                case QUOTE_TOK:
                    post_warning_message(ATTRIBUTE_VALUE_TYPE_MISMATCH_WARN, 
                                         time_type_mismatch_mssg); 

                    if ( ! this.abort_parse )
                    {
                        get_next_token();
                    }
                    break;

                case EOF_TOK:
                    post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
                                       "EOF in a TIME> attribute.\n", 
                                       true, true);
                    break;

                default:
                    throw new SystemErrorException(mName + 
                            "Encountered unknown token type.");
                    /* commented out to keep the compiler happy */
                    // break;
             }
         }

         /* check for EOF */
         if ( ! this.abort_parse )
         {
             if ( (this.l0_tok).code == EOF_TOK )
             {
                post_error_message(UNEXPECTED_END_OF_FILE_ERR, 
                                   "EOF in a TIME> attribute.\n", 
                                   true, true);
             }
         }

        /* discard any excess values that may appear in the TIME> a-list entry */
        if ( ! this.abort_parse )
        {
            if ( (this.l0_tok).code != R_PAREN_TOK )
            {
                discard_excess_alist_entry_values();

                if ( ! this.abort_parse )
                {
                    post_warning_message(EXCESS_VALUES_IN_ALIST_ENTRY_WARN,
                        "Excess values encountered in a TIME> attribute.\n");
                }
            }
        }

        /* read the terminating right parenthesis */
        if ( ! this.abort_parse )
        {
            if ( (this.l0_tok).code == R_PAREN_TOK )
            {
                get_next_token();
            }
            else
            {
                /* since we are cleaning up any excess values in the offset
                 * attribute, this else clause is unreachable at present.
                 * Should we choose to drop the above attempt at error recovery,
                 * this clause will again become reachable.
                 */

                post_warning_message(RIGHT_PAREN_EXPECTED_WARN, 
                    "Closing parenthesis missing from an OFFSET> attribute.\n");
            }
        }

        if ( ( farg.fargType != FormalArgument.FArgType.TIME_STAMP ) ||
             ( farg.fargType != FormalArgument.FArgType.UNTYPED ) )
        {
            ts = new TimeStamp(MACSHAPA_TICKS_PER_SECOND, ticks);
            dv = new TimeStampDataValue(this.db, farg.getID(), ts);
        }
        else
        {
            /* type mismatch between formal argument and value.  Construct
             * an udefined data value, and flag a warning.
             */
            dv = new UndefinedDataValue(this.db, 
                                        farg.getID(), 
                                        farg.getFargName());
            
            post_warning_message(FARG_ARG_TYPE_MISMATCH_WARN, null);
        }
                    
        return(dv);

    } /* macshapa_odb_reader::parse_time_stamp() */    
    
} /* class macshapa_odb_reader */
